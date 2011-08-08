/*
 * File:                ParameterCostMinimizerTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.cost.MeanSquaredErrorCostFunction;
import gov.sandia.cognition.learning.function.vector.ThreeLayerFeedforwardNeuralNetwork;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public abstract class SupervisedLearnerTestHarness
    extends TestCase
{

    public SupervisedLearnerTestHarness(String testName)
    {
        super(testName);
    }

    public static int FUNCTION_EVALUATIONS = 0;
    public static int FUNCTION_GRADIENTS = 0;

    private static Random RNG = new Random(0);

    public final MeanSquaredErrorCostFunction COST_FUNCTION =
        new MeanSquaredErrorCostFunction();

    public static class FunctionDiffyANN
        extends ThreeLayerFeedforwardNeuralNetwork
    {
        public FunctionDiffyANN(
            int numInputs,
            int numHidden,
            int numOutputs )
        {
            super( numInputs, numHidden, numOutputs );
            this.setRandom(RNG);
            this.setInitializationRange(1.0);
            this.initializeWeights(numInputs, numHidden, numOutputs);
        }

        public Vector evaluate(
            Vector input)
        {
            SupervisedLearnerTestHarness.FUNCTION_EVALUATIONS++;
            return super.evaluate(input);
        }

        public Matrix computeParameterGradient(
            Vector input)
        {
            SupervisedLearnerTestHarness.FUNCTION_GRADIENTS++;
            return super.computeParameterGradient(input);
        }



    }


    /**
     * Creates the dataset
     * @param numInputs
     * @param numData
     * @param f
     * @return
     */
    public static ArrayList<? extends InputOutputPair<Vector, Vector>> createDataset(
        int numInputs,
        int numData,
        GradientDescendable f)
    {
        ArrayList<InputOutputPair<Vector, Vector>> dataset =
            new ArrayList<InputOutputPair<Vector, Vector>>(numData);
        double r = 2.0;
        for (int n = 0; n < numData; n++)
        {
            Vector x = VectorFactory.getDefault().createUniformRandom(numInputs, -r, r, RNG);
            Vector y = f.evaluate(x);
            dataset.add(new DefaultInputOutputPair<Vector, Vector>(x, y));
        }
        return dataset;
    }

    /**
     * 
     * @param objectToOptimize
     * @return
     */
    abstract public SupervisedBatchLearner<Vector,Vector, ?> createInstance(
        GradientDescendable objectToOptimize);

    /**
     * Tests the diffyAnn!
     */
    public void diffyAnnTest(
        int numInputs,
        int numHidden,
        int numOutputs,
        int numANNs )
    {
        int numParameters = numInputs*numHidden + numHidden*numOutputs;
        int numData = (int) (1.1*numParameters);
        FunctionDiffyANN target = new FunctionDiffyANN(numInputs,numHidden,numOutputs);
        ArrayList<? extends InputOutputPair<Vector, Vector>> dataset =
            SupervisedLearnerTestHarness.createDataset(numInputs, numData, target);

        LinkedList<FunctionDiffyANN> estimates = new LinkedList<FunctionDiffyANN>();
        for (int i = 0; i < numANNs; i++)
        {
            estimates.add(new FunctionDiffyANN(
                numInputs,RNG.nextInt(numHidden) + 2,numOutputs));
        }
        
        StudentTConfidence ttest = new StudentTConfidence();
        double confidence = 0.95;

        // Let's get a sense of the ratio of time between gradients and function evaluations
        boolean computeGradEvalRatio = false;
        if( computeGradEvalRatio )
        {
            LinkedList<Double> evalTime = new LinkedList<Double>();
            LinkedList<Double> gradTime = new LinkedList<Double>();
            double sum = 0.0;
            RingAccumulator<Vector> vsum = new RingAccumulator<Vector>();
            COST_FUNCTION.setCostParameters(dataset);
            for( int n = 0; n < 100; n++ )
            {
                long start = System.currentTimeMillis();
                Double value = COST_FUNCTION.evaluate( target );
                long stop = System.currentTimeMillis();
                evalTime.add( (double) (stop-start) );
                value += RNG.nextDouble();
                sum += value;
                start = System.currentTimeMillis();
                Vector grad = COST_FUNCTION.computeParameterGradient( target );
                stop = System.currentTimeMillis();
                gradTime.add( (double) (stop-start) );
                vsum.accumulate( grad );
            }

            double averageEval = UnivariateStatisticsUtil.computeMean( evalTime );
            double averageGrad = UnivariateStatisticsUtil.computeMean( gradTime );
            System.out.println( "Evaluation time: " + ttest.computeConfidenceInterval( evalTime, confidence) );
            System.out.println( "Gradient   time: " + ttest.computeConfidenceInterval( gradTime, confidence) );
            System.out.println( "Grad/Eval ratio: " + averageGrad/averageEval );
        }
        
        LinkedList<Double> e1 = new LinkedList<Double>();
        LinkedList<Double> g1 = new LinkedList<Double>();
        LinkedList<Double> t1 = new LinkedList<Double>();
        LinkedList<Double> a1 = new LinkedList<Double>();
        LinkedList<Double> i1 = new LinkedList<Double>();

        int num = 0;
        for (FunctionDiffyANN estimate : estimates)
        {
            Vector startParam = estimate.convertToVector().clone();
            COST_FUNCTION.setCostParameters(dataset);
            double scf = COST_FUNCTION.evaluate(estimate);

            FUNCTION_EVALUATIONS = 0;
            FUNCTION_GRADIENTS = 0;

            long start = System.currentTimeMillis();
            SupervisedBatchLearner<Vector,Vector, ?> instance = this.createInstance(estimate);
            Evaluator<? super Vector,? extends Vector> result = instance.learn(dataset);
            long stop = System.currentTimeMillis();
            
            // The result and the estimate must not be the same!!
            assertNotSame( result, estimate );
            
            // The learning algorithm also should have not modified the
            // parameters of the original estimate by side-effect
            Vector stopParam = estimate.convertToVector();
            assertEquals( startParam, stopParam );            
            
            t1.add((double) (stop - start));
            int evals = FUNCTION_EVALUATIONS;
            int grads = FUNCTION_GRADIENTS;
            e1.add( new Double( evals  ) );
            g1.add( new Double( grads ) );
            double accuracy = COST_FUNCTION.evaluate(result);
            a1.add(accuracy);
            double pn = startParam.minus(((Vectorizable) result).convertToVector()).norm1();
            System.out.print(num + ": ");
            if (instance instanceof IterativeAlgorithm)
            {
                int iterations = ((IterativeAlgorithm) instance).getIteration();
                i1.add((double) iterations);
                System.out.print(" Iterations: " + iterations);
            }

            System.out.println(" Cost: " + accuracy + ", StartCost: " + scf + ", Diff: " + pn + " Evals: " + evals + " Grads: " + grads );

            num++;
        }

        System.out.println("============== Instance: " + this.createInstance(null).getClass() + "==============");
        System.out.println( "Num Parameters: " + numParameters + ": " + numInputs + " -> " + numHidden + " -> " + numOutputs );
        System.out.println( "Num ANNs: " + numANNs + ", num data: " + numData );
        if (i1.size() > 1)
        {
            System.out.println("Iterations:  " + ttest.computeConfidenceInterval(i1, confidence));
        }
        System.out.println(
              "Evaluations: " + ttest.computeConfidenceInterval(e1, confidence) +
            "\nGradients:   " + ttest.computeConfidenceInterval(g1, confidence) +
            "\nTime:        " + ttest.computeConfidenceInterval(t1, confidence) +
            "\nAccuracy:    " + ttest.computeConfidenceInterval(a1, confidence));

    }
    
    public void testLowDimension()
    {
        System.out.println( "Test Low Dimension" );
        this.diffyAnnTest( 3, 2, 3, 10 );
    }

    public void testHighDimension()
    {
        System.out.println( "Test High Dimension" );
//        this.diffyAnnTest( 10, 10, 10, 20 );
    }

    public void testSupervisedClone()
    {
        System.out.println( "SupervisedLearner.clone" );


        SupervisedBatchLearner<Vector,Vector, ?> instance = this.createInstance(
            new FunctionDiffyANN(3,2,1) );
        CloneableSerializable clone = instance.clone();
        System.out.println( "Clone: " + clone );
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertTrue( clone instanceof SupervisedBatchLearner );
    }


}
