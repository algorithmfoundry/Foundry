/*
 * File:                Stoptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;

/**
 * An online, budgeted, kernel version of the Perceptron algorithm that stops
 * learning once it has reached its budget.
 *
 * @param   <InputType>
 *      The input type to learn over, which is passed to the kernel function.
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Francesco Orabona", "Joseph Keshet", "Barbara Caputo"},
    title="Bounded Kernel-Based Online Learning",
    year=2009,
    type=PublicationType.Journal,
    publication="Journal of Machine Learning Research",
    pages={2643, 2666},
    url="http://portal.acm.org/citation.cfm?id=1755875")
public class Stoptron<InputType>
    extends AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
{
    
    /**
     * Creates a new {@code Stoptron} with default parameters and a null kernel.
     */
    public Stoptron()
    {
        this(null, DEFAULT_BUDGET);
    }

    /**
     * Creates a new {@code Stoptron} with the given parameters.
     *
     * @param   kernel
     *      The kernel function to use.
     * @param   budget
     *      The budget for learning. Must be positive.
     */
    public Stoptron(
        final Kernel<? super InputType> kernel,
        final int budget)
    {
        super(kernel, budget);
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
        if (target.getExamples().size() < this.getBudget())
        {
            // We haven't exceeded the budget, so make an update.
            OnlineKernelPerceptron.update(target, input, label, true);
        }
        // else - We've exceeded the budget, don't update.
    }

}
