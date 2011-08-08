/*
 * File:                LocallyWeightedFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * LocallyWeightedFunction is a generalization of the k-nearest neighbor
 * concept, also known as "Instance-Based Learning", "Memory-Based Learning",
 * "Nonparametric Regression", "Case-Based Regression", or 
 * "Kernel-Based Regression".  This approach essentially has no up-front 
 * learning time, but creates a local function approximation in response to
 * a evaluate() call.  The local function approximation is created by weighting
 * the original dataset by a value given by a Kernel against each input sample
 * in the dataset.
 * <BR><BR> 
 * KernelWeightedRobustRegression is different from LocallyWeightedFunction in 
 * that KWRR creates a global function approximator and holds for all inputs.
 * Thus, up-front learning time for KWRR is relatively high, but evaluation time
 * is relatively low.  On the other hand, LWL creates a local function 
 * approximator in response to each evaluation, and LWL does not create a global
 * function approximator.  As such, LWL has (almost) no up-front learning time,
 * but each evaluation requires relatively high computation.  The cost of LWL
 * function evaluation depends strongly on the type of learner given to the 
 * algorithm.  If you use fast or closed-form learners, then you may not notice
 * the evaluation time.  But if you use some brain-dead iterative technique,
 * like Gradient Descent, then use LWL at your own risk.
 * <BR><BR>
 * KWRR is more appropriate when you know the general structure of your data,
 * but it is riddled with outliers. LWL is more appropriate when you don't
 * know/understand the general trend of your data AND you can afford evaluation
 * time to be somewhat costly.
 * 
 * @see KernelWeightedRobustRegression
 *
 * @param <InputType>
 * Input class to map onto the Output class
 * @param <OutputType> 
 * Output of the Evaluator
 * @author Kevin R. Dixon
 */
@PublicationReference(
    author="Andrew W. Moore",
    title="Instance-based learning (aka Case-based or Memory-based or non-parametric)",
    type=PublicationType.WebPage,
    year=2006,
    url="http://www.autonlab.org/tutorials/mbl.html"
)
public class LocallyWeightedFunction<InputType, OutputType>
    implements Evaluator<InputType, OutputType>
{

    /**
     * Kernel that provides the weights between an input and each sample
     * in the input dataset
     */
    private Kernel<? super InputType> kernel;

    /**
     * Learner that takes the Collection of WeightedInputOutputPairs from
     * the Kernel reweighting and creates a local function approximation at
     * the given input.  I would strongly recommend using fast or closed-form
     * learners for this.
     */
    private SupervisedBatchLearner<InputType,OutputType,?> learner;

    /**
     * Original (weighted) dataset
     */
    private ArrayList<WeightedInputOutputPair<InputType, OutputType>> rawData;

    /**
     * Dataset containing the weights in response to an evaluate() call.  The
     * weights in this dataset will be a product of the original dataset 
     * weights times the weights from the Kernel response to the given input
     * to the evaluate() method call.
     */
    private ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>> locallyWeightedData;

    /**
     * Local function approximator created from the learner and the 
     * locallyWeightedData, may be null if you haven't called evaluate() yet
     */
    private Evaluator<? super InputType, ? extends OutputType> localApproximator;

    /**
     * Evaluator that implements the concept of LocallyWeightedLearning.  That
     * is, given an input point, this function re-weights the dataset according
     * to how "close" the dataset inputs are to the given input. An inner-loop
     * learner then uses the re-weighted to compute a local function 
     * approximator for this input.  The output of this class is the output
     * of the local function approximator, which is recomputed each time
     * evaluate() is called.  Thus, evaluate() on this method is relatively
     * expensive (because it calls learn() on the given BatchLearner)
     * 
     * @param kernel
     * Kernel that provides the weights between an input and each sample
     * in the input dataset
     * @param rawData
     * Original (weighted) dataset
     * @param learner
     * Learner that takes the Collection of WeightedInputOutputPairs from
     * the Kernel reweighting and creates a local function approximation at
     * the given input.  I would strongly recommend using fast or closed-form
     * learners for this.
     */
    public LocallyWeightedFunction(
        Kernel<? super InputType> kernel,
        Collection<? extends InputOutputPair<? extends InputType, OutputType>> rawData,
        SupervisedBatchLearner<InputType,OutputType,?> learner )
    {
        this.setKernel( kernel );

        ArrayList<WeightedInputOutputPair<InputType,OutputType>> weightedRawData =
            new ArrayList<WeightedInputOutputPair<InputType,OutputType>>( rawData.size() );
        this.locallyWeightedData = new ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>>( rawData.size() );
        for (InputOutputPair<? extends InputType, ? extends OutputType> pair : rawData)
        {
            double weight = DatasetUtil.getWeight(pair);

            // Note that these have to be different instances of 
            // WeightedInputOutputPair because we'll be overwriting the weight 
            // in locallyWeightedData and we don't want to blow away the weight
            // in the original data
            weightedRawData.add(
                new DefaultWeightedInputOutputPair<InputType, OutputType>( pair, weight ) );
            this.locallyWeightedData.add(
                new DefaultWeightedInputOutputPair<InputType, OutputType>( pair, weight ) );
        }

        this.rawData = weightedRawData;
        this.setLearner( learner );

        this.setLocalApproximator( null );
    }

    /**
     * Getter for kernel
     * @return
     * Kernel that provides the weights between an input and each sample
     * in the input dataset
     */
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Setter for kernel
     * @param kernel
     * Kernel that provides the weights between an input and each sample
     * in the input dataset
     */
    public void setKernel(
        Kernel<? super InputType> kernel )
    {
        this.kernel = kernel;
    }

    /**
     * Getter for learner
     * @return
     * Learner that takes the Collection of WeightedInputOutputPairs from
     * the Kernel reweighting and creates a local function approximation at
     * the given input.  I would strongly recommend using fast or closed-form
     * learners for this.
     */
    public SupervisedBatchLearner<InputType,OutputType,?> getLearner()
    {
        return this.learner;
    }

    /**
     * Setter for learner
     * @param learner
     * Learner that takes the Collection of WeightedInputOutputPairs from
     * the Kernel reweighting and creates a local function approximation at
     * the given input.  I would strongly recommend using fast or closed-form
     * learners for this.
     */
    public void setLearner(
        SupervisedBatchLearner<InputType,OutputType,?> learner )
    {
        this.learner = learner;
    }

    /**
     * This function re-weights the dataset according to the Kernel value
     * between the input and each input in the dataset.  This re-weighted
     * dataset is then given to the learner to create a local approximator
     * that then evaluate the input to produce the prediction
     * @param input
     * Input to create a local approximator for, using the Kernel to weight
     * the original dataset
     * @return
     * Approximation at the given input using the Kernel weights, the original
     * (weighted) dataset, and the BatchLearner
     */
    public OutputType evaluate(
        InputType input )
    {
        // Re-weight the samples according to the kernel weight times the
        // original sample weight, then run the learner on this locally
        // weighted dataset
        for (int i = 0; i < this.rawData.size(); i++)
        {
            WeightedInputOutputPair<? extends InputType, ? extends OutputType> originalPair =
                this.rawData.get( i );
            DefaultWeightedInputOutputPair<InputType, OutputType> locallyWeightedPair =
                this.locallyWeightedData.get( i );
            double kernelWeight = this.kernel.evaluate(
                input, originalPair.getInput() );
            double originalWeight = originalPair.getWeight();
            double localWeight = kernelWeight * originalWeight;

            locallyWeightedPair.setWeight( localWeight );
        }

        this.localApproximator = this.learner.learn( this.locallyWeightedData );
        return this.localApproximator.evaluate( input );
    }

    /**
     * Getter for localApproximator
     * @return
     * Dataset containing the weights in response to an evaluate() call.  The
     * weights in this dataset will be a product of the original dataset 
     * weights times the weights from the Kernel response to the given input
     * to the evaluate() method call.
     */
    public Evaluator<? super InputType, ? extends OutputType> getLocalApproximator()
    {
        return this.localApproximator;
    }

    /**
     * Setter for localApproximator
     * @param localApproximator
     * Dataset containing the weights in response to an evaluate() call.  The
     * weights in this dataset will be a product of the original dataset 
     * weights times the weights from the Kernel response to the given input
     * to the evaluate() method call.
     */
    public void setLocalApproximator(
        Evaluator<? super InputType, ? extends OutputType> localApproximator )
    {
        this.localApproximator = localApproximator;
    }

    /**
     * Learning algorithm for creating LocallyWeightedFunctions. This is 
     * essentially just a pass through, as no learning takes place, but a 
     * model is fitted to the data about each point on an evaluate() call
     * @param <InputType>
     * Input class to map onto the Output class
     * @param <OutputType> 
     * Output of the Evaluator
     */
    public static class Learner<InputType, OutputType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<InputType,OutputType,LocallyWeightedFunction<? super InputType,OutputType>>
    {

        /**
         * Kernel that provides the weights between an input and each sample
         * in the input dataset
         */
        private Kernel<? super InputType> kernel;

        /**
         * Learner that takes the Collection of WeightedInputOutputPairs from
         * the Kernel reweighting and creates a local function approximation at
         * the given input.  I would strongly recommend using fast or closed-form
         * learners for this.
         */
        private SupervisedBatchLearner<InputType,OutputType,?> learner;

        /**
         * Creates a new instance of LocallyWeightedFunction
         * @param kernel
         * Kernel that provides the weights between an input and each sample
         * in the input dataset
         * @param learner
         * Learner that takes the Collection of WeightedInputOutputPairs from
         * the Kernel reweighting and creates a local function approximation at
         * the given input.  I would strongly recommend using fast or closed-form
         * learners for this.
         */
        public Learner(
            Kernel<? super InputType> kernel,
            SupervisedBatchLearner<InputType,OutputType,?> learner )
        {
            this.setKernel( kernel );
            this.setLearner( learner );
        }

        public LocallyWeightedFunction<InputType, OutputType> learn(
            Collection<? extends InputOutputPair<? extends InputType, OutputType>> data )
        {
            return new LocallyWeightedFunction<InputType, OutputType>(
                this.getKernel(), data, this.getLearner() );
        }

        /**
         * Getter for kernel
         * @return
         * Kernel that provides the weights between an input and each sample
         * in the input dataset
         */
        public Kernel<? super InputType> getKernel()
        {
            return this.kernel;
        }

        /**
         * Setter for kernel
         * @param kernel
         * Kernel that provides the weights between an input and each sample
         * in the input dataset
         */
        public void setKernel(
            Kernel<? super InputType> kernel )
        {
            this.kernel = kernel;
        }

        /**
         * Getter for learner
         * @return
         * Learner that takes the Collection of WeightedInputOutputPairs from
         * the Kernel reweighting and creates a local function approximation at
         * the given input.  I would strongly recommend using fast or closed-form
         * learners for this.
         */
        public SupervisedBatchLearner<InputType,OutputType, ?> getLearner()
        {
            return this.learner;
        }

        /**
         * Setter for learner
         * @param learner
         */
        public void setLearner(
            SupervisedBatchLearner<InputType,OutputType,?> learner )
        {
            this.learner = learner;
        }

    }

}
