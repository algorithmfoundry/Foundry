/*
 * File:                BudgetedKernelBinaryCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

/**
 * Interface for a budgeted kernel binary categorizer learner. Exposes the
 * parameter to get and set the budget.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public interface BudgetedKernelBinaryCategorizerLearner
{

    /**
     * Gets the budget. No more than this number of examples will be used in
     * the resulting kernel categorizer.
     *
     * @return
     *      The budget. Must be positive.
     */
    public int getBudget();

    /**
     * Sets the budget. No more than this number of examples will be used in
     * the resulting kernel categorizer.
     *
     * @param   budget
     *      The budget. Must be positive.
     */
    public void setBudget(
        final int budget);
    
}
