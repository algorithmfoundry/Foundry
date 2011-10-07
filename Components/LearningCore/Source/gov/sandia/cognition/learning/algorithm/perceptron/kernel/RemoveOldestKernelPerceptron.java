/*
 * File:                RemoveOldestKernelPerceptron.java
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

import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.LinkedList;

/**
 * A budget kernel Perceptron that always removes the oldest item.
 *
 * @param   <InputType>
 *      The input type to learn over, which is passed to the kernel function.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class RemoveOldestKernelPerceptron<InputType>
    extends AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
{

    /**
     * Creates a new {@code RemoveOldestKernelPerceptron} with a null kernel
     * and default budget.
     */
    public RemoveOldestKernelPerceptron()
    {
        this(null, DEFAULT_BUDGET);
    }

    /**
     * Creates a new {@code RemoveOldestKernelPerceptron} with the given
     * parameters.
     *
     * @param   kernel
     *      The kernel function to use.
     * @param   budget
     *      The budget of examples. Must be positive.
     */
    public RemoveOldestKernelPerceptron(
        final Kernel<? super InputType> kernel,
        final int budget)
    {
        super(kernel, budget);
    }

    @Override
    public DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject()
    {
        // Use a linked list underneath to make sure removing the oldest element
        // is fast.
        return new DefaultKernelBinaryCategorizer<InputType>(this.getKernel(),
            new LinkedList<DefaultWeightedValue<InputType>>(), 0.0);
    }
    
    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
        OnlineKernelPerceptron.update(target, input, label, true);

        // Remove old instances to recover the budget.
        while (target.getExampleCount() > this.getBudget())
        {
            final DefaultWeightedValue<InputType> entry = target.remove(0);
            target.setBias(target.getBias() - entry.getWeight());
        }
    }
    
}
