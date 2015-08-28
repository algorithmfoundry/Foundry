/*
 * File:                KernelWeightedRobustRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *  
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import java.util.ArrayList;

/**
 * KernelWeightedRobustRegression takes a supervised learning algorithm that 
 * operates on a weighted collection of InputOutputPairs and modifies the 
 * weight of a sample based on the dataset output and its corresponding 
 * estimate from the Evaluator from the supervised learning algorithm at each 
 * iteration.  This weight is added to the dataset sample and the supervised 
 * learning algorithm is run again.  This process repeats until the weights 
 * converge.  This  algorithm is a direct generalization of the LOESS-based 
 * (LOWESS-based) Robust Regression using a general learner and kernel.
 * 
 * A typical use case is using a regression algorithm (LinearRegression or 
 * DecoupledVectorLinearRegression) and a RadialBasisKernel.  This results in
 * a regression algorithm that learns to "ignore" outliers and fit the 
 * remaining data.  (Think of fitting a height-versus-age curve and an 8-foot 
 * tall Yao Ming made it into your training set, skewing your results with that 
 * massive outlier.)
 * 
 * KernelWeightedRobustRegression is different from LocallyWeightedLearning in 
 * that KWRR creates a global function approximator and holds for all inputs.
 * Thus, learning time for KWRR is relatively high up front, but evaluation time
 * is relatively low.  On the other hand, LWL creates a local function 
 * approximator in response to each evaluation, and LWL does not create a global
 * function approximator.  As such, LWL has (almost) no up-front learning time,
 * but each evaluation requires a relatively high evaluation.
 * 
 * KWRR is more appropriate when you know the general structure of your data,
 * but it is riddled with outliers. LWL is more appropriate when you don't
 * know/understand the general trend of your data AND you can afford evaluation
 * time to be somewhat costly.
 * 
 * @param <InputType> Input class for the Evaluator and inputs on the 
 * InputOutputPairs dataset
 * @param <OutputType> Output class for the Evaluator, outputs on the
 * InputOutputPairs dataset. Furthermore, the Kernel must be able to
 * evaluate OutputTypes.
 * @author Kevin R. Dixon
 * @since 2.0
 */
public class KernelWeightedRobustRegression<InputType, OutputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, OutputType, Evaluator<? super InputType, ? extends OutputType>>
{

    /**
     * DecoupledVectorFunction that is being optimized
     */
    private Evaluator<? super InputType, ? extends OutputType> result;

    /**
     * Internal learning algorithm that computes optimal solutions
     * given the current weightedData. The iterationLearner should operate on
     * WeightedInputOutputPairs (we have a hard time enforcing this, as many
     * learning algorithms operate both on InputOutputPairs and
     * WeightedInputOutputPairs)
     */
    private SupervisedBatchLearner<InputType, OutputType, ?> iterationLearner;

    /**
     * Kernel function that provides the weighting for the estimate error,
     * generally the Kernel should weight accurate estimates higher than
     * inaccurate estimates.
     */
    private Kernel<? super OutputType> kernelWeightingFunction;

    /**
     * Tolerance before stopping the algorithm
     */
    private double tolerance;

    /**
     * Default maximum number of iterations before stopping
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /**
     * Default tolerance stopping criterion
     */
    public static final double DEFAULT_TOLERANCE = 1e-5;

    /** 
     * Creates a new instance of RobustRegression 
     * @param iterationLearner 
     * Internal learning algorithm that computes optimal solutions
     * given the current weightedData. The iterationLearner should operate on
     * WeightedInputOutputPairs (we have a hard time enforcing this, as many
     * learning algorithms operate both on InputOutputPairs and
     * WeightedInputOutputPairs and their prototype is "? extends InputOutputPair")
     * @param kernelWeightingFunction
     * Kernel function that provides the weighting for the estimate error,
     * generally the Kernel should weight accurate estimates higher than
     * inaccurate estimates.
     */
    public KernelWeightedRobustRegression(
        SupervisedBatchLearner<InputType, OutputType, ?> iterationLearner,
        Kernel<? super OutputType> kernelWeightingFunction )
    {
        this( iterationLearner, kernelWeightingFunction, DEFAULT_MAX_ITERATIONS, DEFAULT_TOLERANCE );
    }

    /** 
     * Creates a new instance of RobustRegression 
     * @param iterationLearner 
     * Internal learning algorithm that computes optimal solutions
     * given the current weightedData. The iterationLearner should operate on
     * WeightedInputOutputPairs (we have a hard time enforcing this, as many
     * learning algorithms operate both on InputOutputPairs and
     * WeightedInputOutputPairs and their prototype is "? extends InputOutputPair")
     * @param kernelWeightingFunction
     * Kernel function that provides the weighting for the estimate error,
     * generally the Kernel should weight accurate estimates higher than
     * inaccurate estimates.
     * @param maxIterations The maximum number of iterations
     * @param tolerance The maximum tolerance
     * Tolerance before stopping the algorithm
     */
    public KernelWeightedRobustRegression(
        SupervisedBatchLearner<InputType, OutputType, ?> iterationLearner,
        Kernel<? super OutputType> kernelWeightingFunction,
        int maxIterations,
        double tolerance )
    {
        super( maxIterations );
        this.setLearned( null );
        this.setTolerance( tolerance );
        this.setKernelWeightingFunction( kernelWeightingFunction );
        this.setIterationLearner( iterationLearner );
    }

    /**
     * Weighted copy of the data
     */
    private ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>> weightedData;

    protected boolean initializeAlgorithm()
    {

        this.weightedData =
            new ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>>(
            this.data.size() );
        for (InputOutputPair<? extends InputType, ? extends OutputType> pair : this.data)
        {
            // Create an initial weighted dataset, using the weights from
            // the original dataset, if available, otherwise just use
            // uniform weights
            double weight = DatasetUtil.getWeight(pair);

            this.weightedData.add( new DefaultWeightedInputOutputPair<InputType, OutputType>(
                pair.getInput(), pair.getOutput(), weight ) );
        }

        return true;
    }

    protected boolean step()
    {

        // Compute the learner based on the current weighting of the samples
        this.result = this.iterationLearner.learn( this.weightedData );

        // Update the weight set using the result function and the Kernel
        // and track the how much the weights have changed
        double change = this.updateWeights( this.result );

        // If the weights have stabilized, then we're done.
        return (change > this.tolerance);

    }

    protected void cleanupAlgorithm()
    {
    }

    /**
     * Updates the weightedData from the given prediction function and
     * the internal Kernel
     * @param f
     * Prediction function to use to update the weights of the weightedData
     * using the Kernel
     * @return Mean L1 norm of the weight change
     */
    private double updateWeights(
        Evaluator<? super InputType, ? extends OutputType> f )
    {

        double change = 0.0;
        for (DefaultWeightedInputOutputPair<InputType, OutputType> pair
            : this.weightedData)
        {
            // Use the kernel to determine the new weight of the sample
            // Generally, the kernel should weight accurate samples more
            // than inaccurate samples
            OutputType yhat = f.evaluate( pair.getInput() );
            double weightNew = this.kernelWeightingFunction.evaluate(
                pair.getOutput(), yhat );
            double weightOld = pair.getWeight();
            change += Math.abs( weightNew - weightOld );
            pair.setWeight( weightNew );
        }

        change /= this.weightedData.size();

        return change;

    }

    /**
     * Getter for kernelWeightingFunction
     * @return
     * Kernel function that provides the weighting for the estimate error,
     * generally the Kernel should weight accurate estimates higher than
     * inaccurate estimates.
     */
    public Kernel<? super OutputType> getKernelWeightingFunction()
    {
        return this.kernelWeightingFunction;
    }

    /**
     * Getter for kernelWeightingFunction
     * @param kernelWeightingFunction
     * Kernel function that provides the weighting for the estimate error,
     * generally the Kernel should weight accurate estimates higher than
     * innaccurate estimates.
     */
    public void setKernelWeightingFunction(
        Kernel<? super OutputType> kernelWeightingFunction )
    {
        this.kernelWeightingFunction = kernelWeightingFunction;
    }

    /**
     * Getter for tolerance
     * @return
     * Tolerance before stopping the algorithm
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Tolerance before stopping the algorithm
     */
    public void setTolerance(
        double tolerance )
    {
        if (tolerance <= 0.0)
        {
            throw new IllegalArgumentException(
                "Tolerance must be > 0.0" );
        }
        this.tolerance = tolerance;
    }

    /**
     * Getter for result
     * @param result
     * DecoupledVectorFunction that is being optimized
     */
    public void setLearned(
        Evaluator<InputType, OutputType> result )
    {
        this.result = result;
    }

    public Evaluator<? super InputType, ? extends OutputType> getResult()
    {
        return this.result;
    }

    /**
     * Getter for iterationLearner
     * @return
     * Internal learning algorithm that computes optimal solutions
     * given the current weightedData. The iterationLearner should operate on
     * WeightedInputOutputPairs (we have a hard time enforcing this, as many
     * learning algorithms operate both on InputOutputPairs and
     * WeightedInputOutputPairs)
     */
    public SupervisedBatchLearner<InputType, OutputType, ?> getIterationLearner()
    {
        return this.iterationLearner;
    }

    /**
     * 
     * @param iterationLearner
     * Internal learning algorithm that computes optimal solutions
     * given the current weightedData. The iterationLearner should operate on
     * WeightedInputOutputPairs (we have a hard time enforcing this, as many
     * learning algorithms operate both on InputOutputPairs and
     * WeightedInputOutputPairs)
     */
    public void setIterationLearner(
        SupervisedBatchLearner<InputType, OutputType, ?> iterationLearner )
    {
        this.iterationLearner = iterationLearner;
    }

}
