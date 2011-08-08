/*
 * File:                LearningExperimentExample.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package examples;

import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerLiuStorey;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.algorithm.regression.ParameterDifferentiableCostMinimizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.experiment.CrossFoldCreator;
import gov.sandia.cognition.learning.experiment.SupervisedLearnerValidationExperiment;
import gov.sandia.cognition.learning.algorithm.nearest.KNearestNeighborExhaustive;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.experiment.SupervisedLearnerComparisonExperiment;
import gov.sandia.cognition.learning.function.cost.MeanSquaredErrorCostFunction;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionToScalarFunction;
import gov.sandia.cognition.learning.function.vector.ThreeLayerFeedforwardNeuralNetwork;
import gov.sandia.cognition.learning.function.vector.VectorizableVectorConverter;
import gov.sandia.cognition.learning.performance.RootMeanSquaredErrorEvaluator;
import gov.sandia.cognition.math.NumberAverager;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * This tutorial demonstrates how to construct some sophisticated supervised 
 * learning algorithms and compare their performance using 
 * statistical-validation techniques.
 * 
 * @author Kevin R. Dixon
 * @since 2.0
 */
public class LearningExperimentExample
{

    /**
     * Random-number generator, created with the same seed, used to generate
     * random, but repeatable, numbers for this example
     */
    private Random randomNumberGenerator = new Random( 1 );

    /**
     * Main method
     * @param argv
     * We don't take any command-line arguments
     */
    public static void main( String[] argv )
    {

        new LearningExperimentExample();

    }

    /**
     * This is where the tutorial takes place
     */
    public LearningExperimentExample()
    {

        final int num = 100;
        final int dimensionality = 3;

        // Step 1.0: create the dataset

        // Step 1.1: create the (random) inputs. We're not interested in any
        // particular data, so just create some random stuff.
        Collection<Vector> inputs = this.createInputData( num, dimensionality );

        // Step 1.2: create the corresponding targets using a random neural 
        // network.
        Collection<Double> targets =
            this.createTargetData( dimensionality, inputs );

        // Step 1.3: merge the inputs and targets to create a supervised 
        // (labeled) training set
        ArrayList<DefaultInputOutputPair<Vector, Double>> labeledDataset =
            DefaultInputOutputPair.mergeCollections( inputs, targets );

        // Step 2.0: set up the learning experiment parameters

        // Step 2.1: We need to choose an objective manner to evaluate the 
        // performance of a learning algorithm.  A very common measure is
        // Root Mean Squared (RMS) Error
        RootMeanSquaredErrorEvaluator<Vector> rms =
            new RootMeanSquaredErrorEvaluator<Vector>();

        // Step 2.2: We need to capture a notion about the confidence interval
        // about the performance of the algorithm on cross-validation data.
        // This will tell us, for example, that with 95% confidence the
        // RMS Error is between 0.1 and 0.2.
        // A very common measure for computing confidence intervals is
        // a Student-t distribution and a 95% confidence interval is the
        // standard amongst social scientists.
        final double confidence = 0.95;
        StudentTConfidence.Summary tdistribution =
            new StudentTConfidence.Summary( confidence );

        // Step 2.3: We need to chop up our data to create training sets,
        // that the learning algorithm uses to tune its parameters, and 
        // cross-validation sets that are withheld during training, but used to
        // evaluate its performance.  This cross-validation performance is
        // supposed to shed light on how well the learning algorithm will
        // generalize to a larger universe of data.
        // A typical strategy for creating cross-validation sets from a large
        // dataset is to chop the data into n "folds", and train on n-1 folds
        // and use the remaining fold to test the data.  This process is
        // repeated for each of the n folds.  We'll create 10 folds in this
        // example.
        final int numFolds = 10;
        CrossFoldCreator<InputOutputPair<Vector, Double>> foldCreator =
            new CrossFoldCreator<InputOutputPair<Vector, Double>>(
            numFolds, this.randomNumberGenerator );

        // Step 3.0: Create the experiment framework.  Since we have labeled
        // input-output pairs, this is a Supervised Learner experiment.
        // The generic parameters mean
        //      - The inputs are Vectors
        //      - The ouputs (labels, targets, etc.) are Doubles
        //      - The performance measure is a Double (RMS)
        //      - The experiment statistic is a ConfidenceInterval 
        // (output of the t-test)
        SupervisedLearnerValidationExperiment<Vector, Double, Double, ConfidenceInterval> experiment =
            new SupervisedLearnerValidationExperiment<Vector, Double, Double, ConfidenceInterval>(
                foldCreator, rms, tdistribution  );

        // Let's try the simplest learning algorithm first: k-nearest neighbor.
        // This learning algorithm just returns the average of the outputs
        // to the nearest "k" inputs we've seen.
        // We're going to use k=3 neighbors, Euclidean distance being used
        // to determine "closeness", and a simple arithmetic mean (average)
        // to average the outputs together
        final int numNeighbors = 3;
        KNearestNeighborExhaustive.Learner<Vector, Double> knn =
            new KNearestNeighborExhaustive.Learner<Vector, Double>(
            numNeighbors, new EuclideanDistanceMetric(), new NumberAverager() );
        ConfidenceInterval knnResult = experiment.evaluatePerformance( knn, labeledDataset );

        // This print statement will read:
        // "Pr{0.3908356360206158<=x(0.46258811033058195)<=0.5343405846405481} >= 0.95, Based on 10 samples"
        // This means that, with 95% confidence, the RMS error on the interval
        // [0.391, 0.534] and we used 10 samples (folds) to determine this result
        // Not bad, eh?
        System.out.println( knn.getClass() + " Result: " + knnResult );

        // Let's use a (slightly) more sophisticated learning algorithm and 
        // compare.
        // Linear regression determines the best (linear) fit to the data.
        // In this case, we're just fitting a single vector of weights to
        // the input set using the regression algorithm.
        LinearRegression<Vector> regression =
            new LinearRegression<Vector>( new VectorizableVectorConverter() );
        ConfidenceInterval regressionResult = 
            experiment.evaluatePerformance( regression, labeledDataset );

        // The print statement will read:
        // "Pr{0.7094902733851925<=x(0.7692147911561242)<=0.8289393089270559} >= 0.95, Based on 10 samples"
        // We can see that, with 95% confidence, the RMS error for regression is
        // [0.709, 0.829].  This is statistically significantly worse than
        // just using 3-nearest neighbor.
        System.out.println( regression.getClass() + " Result: " + regressionResult );
        
        // Let's create a neural network and see how well that performs
        // This is a neural network with a differentiable with "dimensionality"
        // inputs, "dimensionality*2" hidden units, and one output
        ThreeLayerFeedforwardNeuralNetwork ann =
            new ThreeLayerFeedforwardNeuralNetwork( dimensionality, 2*dimensionality, 1 );

        // Let's use the conjugate gradient learning algorithm to optimize the
        // weights of the neural net.  Use a mean-squared error cost function
        // to evaluate the performance of the neural network.
        // We have to use "ParameterDifferentiableCostMinimizer" to use
        // conjugate gradient because CG is a minimization algorithm, not
        // a parameter-optimization algorithm.  The
        // "ParameterDifferentiableCostMinimizer" acts as a bridge between
        // minmization algorithms like CG and BFGS for the purpose of finding
        // minimum-cost parameters for functions like neural nets.
        ParameterDifferentiableCostMinimizer conjugateGradient =
            new ParameterDifferentiableCostMinimizer(
                new FunctionMinimizerLiuStorey() );
        conjugateGradient.setObjectToOptimize( ann );
        conjugateGradient.setCostFunction( new MeanSquaredErrorCostFunction() );
        
        // However, a neural network maps Vectors to Vectors, whereas our
        // training data are Vectors to Doubles.  So, let's create an adapter
        // that maps a Vector->Vector function to a Vector->Double function
        VectorFunctionToScalarFunction.Learner<Vector> adapterLeaner =  
            new VectorFunctionToScalarFunction.Learner<Vector>( conjugateGradient ); 
        
        ConfidenceInterval annResult = experiment.evaluatePerformance(adapterLeaner, labeledDataset );

        // This print statement will read:
        // "Pr{0.04863023231715411<=x(0.1053300627328921)<=0.1620298931486301} >= 0.95, Based on 10 sampels"
        // We can see that, with 95% confidence, the RMS error for ANN with CG
        // is [0.049, 0.162].  This is statistically significantly better than
        // any other learner we've tried (not surprising, since this is the
        // functional form used to generate the targets!).
        System.out.println( ann.getClass() + " Result: " + annResult );
        
        // However, this comparison of learners is somewhat ad hoc.  Wouldn't
        // it be great to run an experiment to determine if one of two
        // learners was significantly better than another?
        // As luck would have it, we've created this class for you!!
        StudentTConfidence ttest = new StudentTConfidence();

        SupervisedLearnerComparisonExperiment<Vector,Double, Double,ConfidenceInterval> comparison =
            new SupervisedLearnerComparisonExperiment<Vector, Double, Double, ConfidenceInterval>(
                foldCreator, rms, ttest, tdistribution );

        comparison.evaluate(adapterLeaner, knn, labeledDataset);

        // We can see from this experiment that the chance that the neural net
        // performs (statistically) identically to the k-nearest neighbor
        // learner on this data is is given by the statement
        // "nullHypothesisProbability = 1.798046487768712E-4"
        // In other words, the p-value is p<1.8e-4 (0.018%), which is tiny.
        // Therefore, we can confidently say that using the neural net for this
        // problem is significantly better than k-nearest neighbor (again, not
        // surprising since a neural net was used to generate the dataset!).
        // If you're interested, the ObjectUtil print statement also contains
        // all the associated information for reporting the results of a 
        // t-test, such as "t = 6.097428929046153" and "degreesOfFreedom = 9.0"
        // All the confidence statistics from our statistical package will 
        // contain all their necessary information as well.
        System.out.println( "Confidence Statistic:\n" + 
            ObjectUtil.toString(comparison.getConfidence()) );
        
    }

    /**
     * Creates "num" Vectors of equal dimension "dimensionality"
     * @param num
     * Number of Vectors to create
     * @param dimensionality
     * Dimensionality of each Vector
     * @return
     * Collection of "num" Vectors each of dimension "dimensionality"
     */
    public Collection<Vector> createInputData(
        final int num,
        final int dimensionality )
    {
        // All we're doing here is creating "num" random Vectors of
        // equal dimenension "dimensionality"
        final double randomRange = 10.0;
        ArrayList<Vector> inputs = new ArrayList<Vector>( num );
        for (int n = 0; n < num; n++)
        {
            inputs.add( VectorFactory.getDefault().createUniformRandom(
                dimensionality, -randomRange, randomRange, this.randomNumberGenerator ) );
        }
        return inputs;
    }

    /**
     * Creates "num" random Double targets as the response to a random
     * neural network
     * 
     * @param dimensionality
     * Dimensionality of the input Vectors
     * @param inputs
     * Input data
     * @return
     * Collection of Doubles that are the response of a random neural net to
     * the inputs
     */
    public Collection<Double> createTargetData(
        int dimensionality,
        Collection<Vector> inputs )
    {

        // Let's just create a neural network to create targets.
        // This neural net has "dimensionality" inputs, "dimensionality*2" 
        // hidden units, and one output node.  It uses an atan (Arctangent)
        // function on each unit
        // Let's set all the parameters to random numbers on [-1,+1]
        ThreeLayerFeedforwardNeuralNetwork ann =
            new ThreeLayerFeedforwardNeuralNetwork(
                dimensionality, dimensionality*2, 1, new AtanFunction(), 2,1.0);

        ArrayList<Double> targets = new ArrayList<Double>( inputs.size() );
        for (Vector input : inputs)
        {
            // Let the output be the neural net's response to each input
            Vector output = ann.evaluate( input );

            // But we're looking for a Double, not a Vector, so just snarf
            // the zeroth element from the Vector and add it to the targets
            targets.add( output.getElement( 0 ) );
        }
        return targets;
    }

}
