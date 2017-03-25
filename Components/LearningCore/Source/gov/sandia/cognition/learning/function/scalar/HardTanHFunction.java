/*
 * File:            HardTanHFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * A hard sigmoid function, which is an approximation of a tanh sigmoid
 * whose output is between -1 and 1. The function also maps values between 
 * -1 and 1 linearly between -1 and 1 and for values outside that range it is 
 * capped. Unlike the soft tanh it actually ends up producing values of -1
 * and 1 for inputs outside of that range, where it is saturated.
 * 
 * The functional form is: f(x) = min(1.0, max(-1.0, x))
 * 
 * @author  Justin Basilico
 * @since   4.0.0
 * @see     TanHFunction
 */
public class HardTanHFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new {@link HardTanHFunction}.
     */
    public HardTanHFunction()
    {
        super();
    }
    
    @Override
    public HardTanHFunction clone()
    {
        return (HardTanHFunction) super.clone();
    }

    @Override
    public double evaluate(
        final double input)
    {
        return hardTanH(input);
    }
    
    @Override
    public double differentiate(
        final double input)
    {
        if (input >= 1.0 || input <= -1.0)
        {
            return 0.0;
        }
        else
        {
            return 1.0;
        }
    }
    
    /**
     * Computes the hard sigmoid function. The functional form is:
     * f(x) = min(1.0, max(-1.0, x))
     * 
     * @param   input
     *      The input value to apply the hard sigmoid to.
     * @return 
     *      The output, which is between -1.0 and 1.0. If NaN is given, NaN will
     *      be returned.
     */
    public static double hardTanH(
        final double input)
    {
        if (input <= -1.0)
        {
            return -1.0;
        }
        else if (input >= 1.0)
        {
            return 1.0;
        }
        else
        {
            return input;
        }
    }
}
