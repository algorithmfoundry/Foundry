/*
 * File:            HardSigmoidFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * A hard sigmoid function, which is an approximation of a logistic sigmoid
 * whose output is between 0 and 1. The function maps values between -2.5 
 * and 2.5 linearly between 0 and 1 and for values outside that range it is 
 * capped. Unlike the soft sigmoid it actually ends up producing values of 0
 * and 1 for inputs outside of that range, where it is saturated.
 * 
 * The functional form is: f(x) = min(1.0, max(0.0, 0.2 * x + 0.5))
 * 
 * @author  Justin Basilico
 * @since   4.0.0
 * @see     SigmoidFunction
 */
public class HardSigmoidFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new {@link HardSigmoidFunction}.
     */
    public HardSigmoidFunction()
    {
        super();
    }
    
    @Override
    public HardSigmoidFunction clone()
    {
        return (HardSigmoidFunction) super.clone();
    }

    @Override
    public double evaluate(
        final double input)
    {
        return hardSigmoid(input);
    }
    
    @Override
    public double differentiate(
        final double input)
    {
        final double y = this.evaluate(input);
        if (y == 0.0 || y == 1.0)
        {
            return 0.0;
        }
        else
        {
            return 0.2;
        }
    }
    
    /**
     * Computes the hard sigmoid function. The functional form is:
     * f(x) = min(1.0, max(0.0, 0.2 * x + 0.5))
     * 
     * @param   input
     *      The input value to apply the hard sigmoid to.
     * @return 
     *      The output, which is between 0.0 and 1.0. If NaN is given, NaN will
     *      be returned.
     */
    public static double hardSigmoid(
        final double input)
    {
        double x = 0.2 * input + 0.5;
        if (x <= 0.0)
        {
            x = 0.0;
        }
        else if (x >= 1.0)
        {
            x = 1.0;
        }

        return x;
    }
    
}
