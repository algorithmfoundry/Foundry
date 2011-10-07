/*
 * File:                AbstractOnlineKernelBinaryCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.algorithm.AbstractSupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.KernelContainer;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;

/**
 * An abstract class for an online kernel binary categorizer learner.
 * 
 * @param   <InputType>
 *      The input value type passed to the kernel function to perform learning
 *      over.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public abstract class AbstractOnlineKernelBinaryCategorizerLearner<InputType>
    extends AbstractSupervisedBatchAndIncrementalLearner<InputType, Boolean, DefaultKernelBinaryCategorizer<InputType>>
    implements KernelContainer<InputType>
{

    /** The kernel to use. */
    protected Kernel<? super InputType> kernel;

    /**
     * Creates a new {@code AbstractOnlineKernelBinaryCategorizerLearner} with
     * a null kernel.
     */
    public AbstractOnlineKernelBinaryCategorizerLearner()
    {
        this(null);
    }

    /**
     * Creates a new {@code AbstractOnlineKernelBinaryCategorizerLearner} with
     * the given kernel.
     * 
     * @param   kernel
     *      The kernel to use.
     */
    public AbstractOnlineKernelBinaryCategorizerLearner(
        final Kernel<? super InputType> kernel)
    {
        this.setKernel(kernel);
    }

    @Override
    public DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject()
    {
        return new DefaultKernelBinaryCategorizer<InputType>(
            this.getKernel(), new ArrayList<DefaultWeightedValue<InputType>>(),
            0.0);
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final Boolean output)
    {
        this.update(target, input, (boolean) output);
    }

    /**
     * Updates the target categorizer based on the given input and its
     * associated output.
     *
     * @param   target
     *      The target categorizer to update.
     * @param   input
     *      The input value to learn from.
     * @param   output
     *      The output value associated with the input.
     */
    public abstract void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean output);

    @Override
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the kernel used by this learner.
     *
     * @param   kernel
     *      The kernel to use.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }
    
}
