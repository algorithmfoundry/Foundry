/*
 * File:                Forgetron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 10, 2011, Sandia Corporation.
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
import gov.sandia.cognition.learning.function.kernel.KernelUtil;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.LinkedList;

/**
 * An implementation of the "self-tuned" Forgetron algorithm, which is an online
 * budgeted kernel binary categorizer learner.
 *
 * Note that this class requires its own extension of the
 * {@code DefaultKernelBinaryCategorizer} to keep some extra information about
 * learning.
 *
 * @param   <InputType>
 *      The type of input to learn over. Passed to the kernel function.
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Ofer Dekel", "Shai Shalev-Shwartz", "Yoram Singer"},
    title="The Forgetron: A Kernel-based Perceptron on a Budget",
    year=2008,
    type=PublicationType.Journal,
    publication="SIAM Journal on Computing",
    pages={1342, 1372},
    url="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.134.7604&rep=rep1&type=pdf",
    notes="This is the self-tuned version.")
public class Forgetron<InputType>
    extends AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
{
    
    /**
     * Creates a new {@code Forgetron} with a null kernel and the default
     * budget.
     */
    public Forgetron()
    {
        this(null, DEFAULT_BUDGET);
    }

    /**
     * Creates a new {@code Forgetron} with the given kernel and budget.
     *
     * @param   kernel
     *      The kernel to use.
     * @param   budget
     *      The budget for the maximum number of supports.
     */
    public Forgetron(
        final Kernel<? super InputType> kernel,
        final int budget)
    {
        super(kernel, budget);
    }

    @Override
    public DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject()
    {
        // We need to use a special result type to keep track of the error
        // count and Q.
        return new Result<InputType>(this.getKernel());
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;
        final double margin = prediction * actual;

        if (margin > 0.0)
        {
            // Did not make an error.
            return;
        }

        // Cast the result type.
        Result<InputType> result = (Result<InputType>) target;

        // We've made an error.
        result.errorCount++;

        // Add the new example with a sigma of 1. This value will be adjsuted
        // later, if needed.
        result.add(input, actual);
        
        while (target.getExampleCount() > this.getBudget())
        {
            // We've violated the budget. Normally this loop is only executed 
            // once, though if the budget is changed, we do it multiple times
            // to bring the number of supports back down to the budget.
            shrink(result);
        }

    }

    /**
     * Apply the shrinking step of the algorithm.
     *
     * @param   result
     *      The shrinking step.
     */
    protected void shrink(
        final Result<InputType> result)
    {
        // Get the oldest support. We will later remove it.
        final WeightedValue<InputType> oldest = result.get(0);
        // Get the weight of the oldest, its sigma value (remove the
        // sign, which encodes the label), and its label.
        final double oldestWeight = oldest.getWeight();
        double sigmaOldest = Math.abs(oldestWeight);
        final double yOldest = oldestWeight >= 0.0 ? +1.0 : -1.0;
        // Evaluate the function on the oldest.
        double fOldest = result.evaluateAsDouble(oldest.getValue());
        // Compute the a, b, c, and d values to figure out how to make the
        // update.
        final double a =
            sigmaOldest * sigmaOldest - 2.0 * sigmaOldest * yOldest * fOldest;
        final double b = 2.0 * sigmaOldest;
        final double c = result.q - 15.0 / 32.0 * result.errorCount;
        final double d = b * b - 4.0 * a * c;
        // Now compute the update value (phi) that all of the weights will
        // be scaled by.
        final double update;
        if (a > 0.0 ||
            (a < 0.0 && d > 0.0 &&
            (-b - Math.sqrt(d)) / (2.0 * a) > 1.0))
        {
            update =
                Math.min(1.0, (-b + Math.sqrt(d)) / (2.0 * a));
        }
        else if (a == 0.0)
        {
            update = Math.min(1.0, -c / b);
        }
        else
        {
            // Remove-oldest Perceptron update.
            update = 1.0;
        }
        // Perform the scaling as long as the update is not 1.0.
        if (update != 1.0)
        {
            KernelUtil.scaleEquals(result, update);
        } // the oldest instead of recomputing fOldest.
        sigmaOldest *= update;
        fOldest *= update;
        result.q +=
            sigmaOldest * sigmaOldest + 2.0 * sigmaOldest -
            2.0 * sigmaOldest * yOldest * fOldest;
        // Remove the oldest.
        result.remove(0);
    }

    /**
     * The result object learned by the {@code Forgetron}, which extends
     * the {@code DefaultKernelBinaryCategorizer} with some additional state
     * information needed in the update step.
     *
     * @param   <InputType>
     *      The input type to categorize, which is passed to the kernel
     *      function.
     */
    public static class Result<InputType>
        extends DefaultKernelBinaryCategorizer<InputType>
    {

        /** The number of errors that the categorizer has made in the learning
         *  step. */
        protected long errorCount;

        /** The value of Q for the algorithm. */
        protected double q;

        /**
         * Creates a new {@code Result} with a null kernel.
         */
        public Result()
        {
            this(null);
        }

        /**
         * Creates a new {@code Result} with the given kernel.
         *
         * @param   kernel
         *      The kernel to use.
         */
        public Result(
            final Kernel<? super InputType> kernel)
        {
            // Use a linked list to deal with the fact that we're going to
            // enforce a budget and thus will continuously remove the first
            // element when we need to add new supports.
            super(kernel, new LinkedList<DefaultWeightedValue<InputType>>(),
                0.0);

            this.setErrorCount(errorCount);
            this.setQ(q);
        }

        /**
         * Gets the error count.
         *
         * @return
         *      The error count.
         */
        public long getErrorCount()
        {
            return this.errorCount;
        }

        /**
         * Sets the error count.
         *
         * @param   errorCount
         *      The error count.
         */
        protected void setErrorCount(
            final long errorCount)
        {
            this.errorCount = errorCount;
        }

        /**
         * Gets the value Q updated by the algorithm.
         *
         * @return
         *      The Q value.
         */
        protected double getQ()
        {
            return this.q;
        }

        /**
         * Gets the value Q updated by the algorithm.
         *
         * @param   q
         *      The Q value.
         */
        protected void setQ(
            final double q)
        {
            this.q = q;
        }

    }

    /**
     * An implementation of the "basic" Forgetron algorithm, which is an online
     * budgeted kernel binary categorizer learner. Note that this appears to
     * be somewhat for illustrative purposes only since it uses the worst-case
     * definition of the shrinking coefficients and thus tends to perform
     * poorly.
     *
     * @param   <InputType>
     *      The type of input to learn over. Passed to the kernel function.
     * @author  Justin Basilico
     * @since   3.3.0
     */
    @PublicationReference(
        author={"Ofer Dekel", "Shai Shalev-Shwartz", "Yoram Singer"},
        title="The Forgetron: A Kernel-based Perceptron on a Budget",
        year=2008,
        type=PublicationType.Journal,
        publication="SIAM Journal on Computing",
        pages={1342, 1372},
        url="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.134.7604&rep=rep1&type=pdf",
        notes="This is the basic version.")
    public static class Basic<InputType>
        extends AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
    {

        /**
         * Creates a new {@code Forgetron.Basic} with a null kernel and default
         * budget.
         */
        public Basic()
        {
            this(null, DEFAULT_BUDGET);
        }

        /**
         * Creates a new {@code Forgetron.Basic} with the given kernel and
         * budget.
         * 
         * @param   kernel
         *      The kernel to use.
         * @param   budget
         *      The budget for the maximum number of supports.
         */
        public Basic(
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
            return new DefaultKernelBinaryCategorizer<InputType>(
                this.getKernel(),
                new LinkedList<DefaultWeightedValue<InputType>>(), 0.0);
        }

        @Override
        public void update(
            final DefaultKernelBinaryCategorizer<InputType> target,
            final InputType input,
            final boolean label)
        {
            // Predict the output as a double (negative values are false, positive
            // are true).
            final double prediction = target.evaluateAsDouble(input);
            final double actual = label ? +1.0 : -1.0;
            final double margin = prediction * actual;

            if (margin > 0.0)
            {
                // Did not make an error.
                return;
            }

            // Add the new example.
            target.add(input, actual);

// TODO: It may be possible to cache the norm to avoid the O(b^2) computation,
// with b=budget, that occurs from this call to create an O(b) algorithm.
// However, this algorithm doesn't perform very well, so it is not currently
// worth the time to do the optimization.
// -- jdbasil (2011-05-11)
            // Compute the norm.
            final double norm = KernelUtil.norm2(target);

            // We use the B + 1 in the following equations, so cache it.
            final double b = this.budget + 1;

            // Compute minimum of (b + 1)^(-1 / (2 * (b + 1)) and
            // U / ||f't|| to get the update, phi.
            final double r = Math.pow(b, -1.0 / (2.0 * b));
            final double u = 0.25 * Math.sqrt(b / Math.log(b));
            final double update = Math.min(r, u / norm);

            // Scale the sigmas by phi.
            KernelUtil.scaleEquals(target, update);

            // If we've violated the budget, remove the oldest. Normally this
            // loop is only executed once, though if the budget is changed, we
            // do it multiple times to bring the number of supports back down
            // to the budget.
            while (target.getExampleCount() > this.budget)
            {
                target.remove(0);
            }

        }

    }

    /**
     * An implementation of the "greedy" Forgetron algorithm, which is an online
     * budgeted kernel binary categorizer learner. It is an extension of the
     * "self-tuned" algorithm that
     *
     * @param   <InputType>
     *      The type of input to learn over. Passed to the kernel function.
     * @author  Justin Basilico
     * @since   3.3.0
     */
    @PublicationReference(
        author={"Ofer Dekel", "Shai Shalev-Shwartz", "Yoram Singer"},
        title="The Forgetron: A Kernel-based Perceptron on a Budget",
        year=2008,
        type=PublicationType.Journal,
        publication="SIAM Journal on Computing",
        pages={1342, 1372},
        url="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.134.7604&rep=rep1&type=pdf",
        notes="This is the greedy version.")
    public static class Greedy<InputType>
        extends Forgetron<InputType>
    {

        /**
         * Creates a new {@code Forgetron.Greedy} with a null kernel and default
         * budget.
         */
        public Greedy()
        {
            this(null, DEFAULT_BUDGET);
        }

        /**
         * Creates a new {@code Forgetron.Greedy} with the given kernel and
         * budget.
         *
         * @param   kernel
         *      The kernel to use.
         * @param   budget
         *      The budget for the maximum number of supports.
         */
        public Greedy(
            final Kernel<? super InputType> kernel,
            final int budget)
        {
            super(kernel, budget);
        }
        
        @Override
        protected void shrink(
            final Result<InputType> result)
        {
            // If we find an entry whose psi is below this threshold, starting
            // from the beginning, then we will remove that one rather than
            // doing a full shrinking using the oldest value.
            final double threshold = 15.0 / 32.0;

// TODO: This is an O(b^2) implementation, where b is the budget of the
// algorithm. It should be possible to do an O(b) implementation if the
// f values are cached and updated whenever a support is added/removed or the
// weights are scaled.
// -- jdbasil (2011-05-11)
            int i = 0;
            for (DefaultWeightedValue<InputType> example : result.getExamples())
            {
                final double weight = example.getWeight();
                final double sigma = Math.abs(weight);
                final double y = weight >= 0.0 ? +1.0 : -1.0;
                final double f = result.evaluateAsDouble(example.getValue());
                final double psi =
                    sigma * sigma + 2.0 * sigma - 2.0 * sigma * y * f;

                if (psi <= threshold)
                {
                    // Less then the threshold, so remove it and avoid doing
                    // the shrinking step.
                    result.remove(i);
                    return;
                }
                i++;
            }

            // Do the normal shrinking step.
            super.shrink(result);


/*

// TODO: This is an O(b^2) implementation, where b is the budget of the
// algorithm. It should be possible to do an O(b) implementation if the
// f values are cached and updated whenever a support is added/removed or the
// weights are scaled.
// -- jdbasil (2011-05-11)
            int i = 0;
            int bestI = -1;
            double bestPsi = Double.POSITIVE_INFINITY;

            // Find the minimum value of psi for all of the supports.
            for (DefaultWeightedValue<InputType> example : supports)
            {
                final double weight = example.getWeight();
                final double sigma = Math.abs(weight);
                final double y = weight >= 0.0 ? +1.0 : -1.0;
                final double f = result.evaluateAsDouble(example.getValue());
                final double psi =
                    sigma * sigma + 2.0 * sigma - 2.0 * sigma * y * f;

                if (psi < bestPsi)
                {
                    bestI = i;
                    bestPsi = psi;

                    if (psi <= 15.0 / 32.0)
                    {
                        supports.remove(bestI);
                        return;
                    }
                }

                i++;
            }

            // If there is a psi who is less than 15/32, then we remove the
            // support for the smallest such entry.
            if (bestPsi <= 15.0 / 32.0)
            {
                // Found something less then the threshold, so remove it and
                // avoid doing the shrinking step.
                supports.remove(bestI);
            }
            else
            {
                // Nothing less than the threshold, so do the normal shrinking
                // step.
                super.shrink(result);
            }
        }
 */
        }

    }

}