/*
 * File:                OnlineKernelPerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;

/**
 * An implementation of the online version of the Perceptron algorithm.
 *
 * @param   <InputType>
 *      The input type to perform learning on, which is passed to the kernel
 *      function.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlineKernelPerceptron<InputType>
    extends AbstractOnlineKernelBinaryCategorizerLearner<InputType>
{

    /**
     * Creates a new {@code OnlineKernelPerceptron} with no kernel.
     */
    public OnlineKernelPerceptron()
    {
        this(null);
    }

    /**
     * Creates a new {@code OnlineKernelPerceptron} with the given kernel.
     *
     * @param   kernel
     *      The kernel function to use.
     */
    public OnlineKernelPerceptron(
        final Kernel<? super InputType> kernel)
    {
        this.setKernel(kernel);
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
        // Perform the update.
        update(target, input, label, true);
    }

    /**
     * Performs a Perceptron update step on the given target. If an error is
     * made by target on the input, then the input is added as a support
     * value to the target.
     *
     * @param <InputType>
     *      The input type to the kernel.
     * @param target
     *      The kernel binary categorizer to update using the Perceptron update
     *      step.
     * @param input
     *      The input value.
     * @param label
     *      The label associated with the input.
     * @param updateBias
     *      True to update the bias term. False to leave it the same.
     * @return
     *      True if a change was made, otherwise false.
     */
    public static <InputType> boolean update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label,
        final boolean updateBias)
    {
        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;

        if (prediction * actual <= 0.0)
        {
            // Update the target by adding the input.
            target.add(input, actual);

            if (updateBias)
            {
                target.setBias(target.getBias() + actual);
            }
            return true;
        }
        else
        {
            // Not an error.
            return false;
        }
    }

}
