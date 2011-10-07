/*
 * File:                OnlineKernelRandomizedBudgetPerceptron.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Randomized;
import java.util.Random;

/**
 * An implementation of a fixed-memory kernel Perceptron algorithm. It randomly
 * removes an item when it needs to make an update and its budget is exceeded.
 *
 * @param   <InputType>
 *      The type of the input to the algorithm, which is passed to the kernel
 *      function for use in learning.
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Nicolo Cesa-Bianchi", "Claudio Gentile"},
    title="Tracking the Best Hyperplane with a Simple Budget Perceptron",
    year=2006,
    type=PublicationType.Conference,
    publication="Conference on Learning Theory",
    pages={483, 498},
    url="http://www.springerlink.com/index/d65th427143p0532.pdf")
public class OnlineKernelRandomizedBudgetPerceptron<InputType>
    extends AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
    implements Randomized
{
    
    /** The random number generator. */
    protected Random random;

    /**
     * Creates a new {@code OnlineKernelRandomizedBudgetPerceptron} with default
     * parameters and a null kernel.
     */
    public OnlineKernelRandomizedBudgetPerceptron()
    {
        this(null, DEFAULT_BUDGET, new Random());
    }

    /**
     * Creates a new {@code OnlineKernelRandomizedBudgetPerceptron} with the
     * given parameters.
     *
     * @param   kernel
     *      The kernel function to use.
     * @param   budget
     *      The budget for the algorithm. Must be positive.
     * @param   random
     *      The random number generator.
     */
    public OnlineKernelRandomizedBudgetPerceptron(
        final Kernel<? super InputType> kernel,
        final int budget,
        final Random random)
    {
        super(kernel, budget);

        this.setRandom(random);
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean output)
    {
        OnlineKernelPerceptron.update(target, input, output, true);

        // Remove instances to recover the budget.
        int size = target.getExampleCount();
        while (size > this.getBudget())
        {
            final int randomIndex = this.getRandom().nextInt(size);
            final DefaultWeightedValue<InputType> entry =
                target.remove(randomIndex);
            target.setBias(target.getBias() - entry.getWeight());
            size--;
        }
    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

}
