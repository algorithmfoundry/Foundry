/*
 * File:            TanHFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * The hyperbolic tangent (tanh) function. It is often used as an activation
 * function in neural networks since it is a sigmoid shaped function ranging
 * between -1 and +1.
 * 
 * @author  Justin Basilico
 * @since   3.4.3
 */
public class TanHFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new instance of TanHFunction
     */
    public TanHFunction()
    {
        super();
    }

    @Override
    public TanHFunction clone()
    {
        return (TanHFunction) super.clone();
    }

    /**
     * Evaluates the squashing function on the given input value.
     *
     * @param  input The input value to squash.
     * @return The output of the function, which is between -1 and +1.
     */
    @Override
    public double evaluate(
        final double input)
    {
        // This should be equivalent to but faster than Math.tanh(input).
        return 2.0 / (1.0 + Math.exp(-2.0 * input)) - 1.0;
    }

    @Override
    public double differentiate(
        final double input)
    {
        final double y = this.evaluate(input);
        return 1.0 - y * y;
    }
    
}
