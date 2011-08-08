/*
 * File:                KernelUtil.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 07, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.util.DefaultWeightedValue;

/**
 * A utility class for dealing with kernels.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class KernelUtil
{

    /**
     * Computes the 2-norm of the given value according to the given kernel.
     *
     * @param   <ValueType>
     *      The type of the value.
     * @param   value
     *      A value.
     * @param   kernel
     *      A kernel function that can operate on the value.
     * @return
     *      The 2-norm of the value according to the kernel.
     */
    public static <ValueType> double norm2(
        final ValueType value,
        final Kernel<? super ValueType> kernel)
    {
        return Math.sqrt(norm2Squared(value, kernel));
    }

    /**
     * Computes the 2-norm of the weight vector implied by the given kernel
     * binary categorizer.
     *
     * @param   <InputType>
     *      The type of input to the categorizer value.
     * @param   target
     *      A kernel binary categorizer.
     * @return
     *      The 2-norm of the categorizer according to the kernel.
     */
    public static <InputType> double norm2(
        final DefaultKernelBinaryCategorizer<InputType> target)
    {
        return Math.sqrt(norm2Squared(target));
    }
    /**
     * Computes the squared 2-norm of the given value according to the given
     * kernel. Useful for bypassing the square-root computation in the 2-norm
     * computation.
     *
     * @param   <ValueType>
     *      The type of the value.
     * @param   value
     *      A value.
     * @param   kernel
     *      A kernel function that can operate on the value.
     * @return
     *      The squared 2-norm of the value according to the kernel.
     */
    public static <ValueType> double norm2Squared(
        final ValueType value,
        final Kernel<? super ValueType> kernel)
    {
        return kernel.evaluate(value, value);
    }

    /**
     * Computes the squared 2-norm of the weight vector implied by the given
     * kernel binary categorizer. Useful for bypassing the square-root
     * computation in the 2-norm computation.
     *
     * @param   <InputType>
     *      The type of input to the categorizer value.
     * @param   target
     *      A kernel binary categorizer.
     * @return
     *      The 2-norm of the categorizer according to the kernel.
     */
    public static <InputType> double norm2Squared(
        final DefaultKernelBinaryCategorizer<InputType> target)
    {
        double result = 0.0;
        final double bias = target.getBias();
        for (DefaultWeightedValue<InputType> example : target.getExamples())
        {
            double sum = target.evaluateAsDouble(example.getValue()) - bias;

            result += sum * example.getWeight();
        }
        return result;
    }

    /**
     * Scales all of the weights in the given kernel binary categorizer by
     * the given value.
     *
     * @param   target
     *      The kernel binary categorizer to update the weights on.
     * @param   scale
     *      The scale to apply to all the weights.
     */
    public static void scaleEquals(
        final DefaultKernelBinaryCategorizer<?> target,
        final double scale)
    {
        for (DefaultWeightedValue<?> example : target.getExamples())
        {
            final double oldWeight = example.getWeight();
            final double newWeight = scale * oldWeight;
            example.setWeight(newWeight);
        }
    }
}
