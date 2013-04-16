/*
 * File:            ScalarIdentityFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * A univariate scalar identity function: f(x) = x. This function simply acts
 * as a pass-through, where evaluate(input) == input for any input. The
 * derivative is always equal to 1.0. This is for those classes that expect an
 * evaluator, but you don't want to alter the value of the function, like a
 * FeedforwardNeuralNetwork.
 *
 * Note: prior to 3.3.0, this class was named "LinearFunction". Using
 * LinearFunction with default values will produce the same result. However,
 * it how has additional parameters.
 *
 * @author  Kevin R. Dixon
 * @since   3.3.3
 */
public class IdentityScalarFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new {@code LinearFunction}.
     */
    public IdentityScalarFunction()
    {
        super();

        // Nothing to set
    }

    @Override
    public IdentityScalarFunction clone()
    {
        return (IdentityScalarFunction) super.clone();
    }

    @Override
    public Double evaluate(
        final Double input)
    {
        return input;
    }
    
    @Override
    public double evaluate(
        final double input)
    {
        return input;
    }

    @Override
    public double evaluateAsDouble(
        final Double input)
    {
        return input;
    }
    
    @Override
    public double differentiate(
        final double input)
    {
        return 1.0;
    }

}
