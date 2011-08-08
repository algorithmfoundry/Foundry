/*
 * File:                KernelBinaryCategorizerOnlineLearnerAdapter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 10, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.algorithm.perceptron.KernelizableBinaryCategorizerOnlineLearner;

/**
 * A wrapper class for a {@code KernelizableBinaryCategorizerOnlineLearner}
 * that allows it to be used as a batch or incremental learner over the
 * input type directly, rather than using utility methods.
 *
 * @param   <InputType>
 *      The input type to perform learning over. It is passed to the kernel
 *      function.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class KernelBinaryCategorizerOnlineLearnerAdapter<InputType>
    extends AbstractOnlineKernelBinaryCategorizerLearner<InputType>
{

    /** The wrapped kernelizable learner. */
    protected KernelizableBinaryCategorizerOnlineLearner learner;

    /**
     * Creates a new {@code KernelBinaryCategorizerOnlineLearnerAdapter} with
     * a null learner.
     */
    public KernelBinaryCategorizerOnlineLearnerAdapter()
    {
        super();
    }

    /**
     * Creates a new {@code KernelBinaryCategorizerOnlineLearnerAdapter} with
     * the given kernel and learner.
     *
     * @param   kernel
     *      The kernel to use.
     * @param   learner
     *      The kernelizable learner to use.
     */
    public KernelBinaryCategorizerOnlineLearnerAdapter(
        final Kernel<? super InputType> kernel,
        final KernelizableBinaryCategorizerOnlineLearner learner)
    {
        super(kernel);

        this.setLearner(learner);
    }

    @Override
    public DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject()
    {
        return this.getLearner().createInitialLearnedObject(this.getKernel());
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputOutputPair<? extends InputType, Boolean> data)
    {
        this.getLearner().update(target, data);
    }

    @Override
    public void update(DefaultKernelBinaryCategorizer<InputType> target,
        InputType input,
        Boolean output)
    {
        this.getLearner().update(target, input, output);
    }
    
    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean output)
    {
        this.getLearner().update(target, input, output);
    }

    /**
     * Gets the kernelizable learner that this adapter is wrapping.
     *
     * @return
     *      The kernelizable learner that this adapter is wrapping.
     */
    public KernelizableBinaryCategorizerOnlineLearner getLearner()
    {
        return this.learner;
    }

    /**
     * Sets the kernelizable learner that this adapter is wrapping.
     *
     * @param   learner
     *      The kernelizable learner that this adapter is wrapping.
     */
    public void setLearner(
        final KernelizableBinaryCategorizerOnlineLearner learner)
    {
        this.learner = learner;
    }

}
