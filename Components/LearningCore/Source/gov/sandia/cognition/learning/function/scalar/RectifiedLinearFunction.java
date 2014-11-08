/*
 * File:            RectifiedLinearFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * A rectified linear unit, which is the maximum of its input or 0. It is 
 * typically used as an activation function in a neural network. Generally, its
 * derivative is 1 for values larger than 0 and 0 for those smaller than 0.
 * Although the derivative is ill-defined at 0 itself, the implementation treats
 * the derivative at 0 as 0. It is typically useful for handling the vanishing
 * gradient problem.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Rectifier",
    type=PublicationType.WebPage,
    year=2014,
    url="http://en.wikipedia.org/wiki/Rectifier_(neural_networks)"
)
public class RectifiedLinearFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new {@link RectifiedLinearFunction}.
     */
    public RectifiedLinearFunction()
    {
        super();
    }

    @Override
    public RectifiedLinearFunction clone()
    {
        return (RectifiedLinearFunction) super.clone();
    }

    @Override
    public double evaluate(
        final double input)
    {
        // f(x) = max(0, x)
        if (input > 0.0)
        {
            return input;
        }
        else
        {
            return 0.0;
        }
    }

    @Override
    public double differentiate(
        final double input)
    {
        if (input > 0.0)
        {
            return 1.0;
        }
        else
        {
            return 0.0;
        }
    }
    
}
