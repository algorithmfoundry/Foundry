/*
 * File:                AbstractOnlineBudgetedKernelBinaryCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An abstract implementation of the {@code BudgetedKernelBinaryCategorizerLearner}
 * for online learners.
 *
 * @param   <InputType>
 *      The type of input passed to the kernel to do learning over.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public abstract class AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
    extends AbstractOnlineKernelBinaryCategorizerLearner<InputType>
{

    /** The default budget is {@value}. */
    public static final int DEFAULT_BUDGET = 100;

    /** The budget of the number of examples to keep. Must be positive. */
    protected int budget;

    /**
     * Creates a new {@code AbstractOnlineBudgetedKernelBinaryCategorizerLearner}
     * with a null kernel and default budget.
     */
    public AbstractOnlineBudgetedKernelBinaryCategorizerLearner()
    {
        this(null, DEFAULT_BUDGET);
    }

    /**
     * Creates a new {@code AbstractOnlineBudgetedKernelBinaryCategorizerLearner}
     * with the given parameters.
     *
     * @param   kernel
     *      The kernel function to use.
     * @param   budget
     *      The budget of examples. Must be positive.
     */
    public AbstractOnlineBudgetedKernelBinaryCategorizerLearner(
        final Kernel<? super InputType> kernel,
        final int budget)
    {
        super(kernel);

        this.setBudget(budget);
    }

    /**
     * Gets the budget. No more than this number of examples will be used in
     * the resulting kernel categorizer.
     *
     * @return
     *      The budget. Must be positive.
     */
    public int getBudget()
    {
        return this.budget;
    }

    /**
     * Sets the budget. No more than this number of examples will be used in
     * the resulting kernel categorizer.
     *
     * @param   budget
     *      The budget. Must be positive.
     */
    public void setBudget(
        final int budget)
    {
        ArgumentChecker.assertIsPositive("budget", budget);
        this.budget = budget;
    }
    
}
