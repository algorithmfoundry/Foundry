/*
 * File:            SoftPlusFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.MathUtil;

/**
 * A smoothed approximation for rectified linear unit. It is typically used as 
 * an activation function in a neural network. Its value is computed as
 * f(x) = log(1 + e^x). Thus, its derivative is the logistic sigmoid,
 * f'(x) = 1 / (1 + e^-x). As such, it can help avoid the vanishing
 * gradient problem. Its output is always positive, it is roughly linear
 * for positive values and it approaches zero for negative values.
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
public class SoftPlusFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new {@link SoftPlusFunction}.
     */
    public SoftPlusFunction()
    {
        super();
    }

    @Override
    public SoftPlusFunction clone()
    {
        return (SoftPlusFunction) super.clone();
    }
    
    @Override
    public double evaluate(
        final double input)
    {
        return MathUtil.log1PlusExp(input);
    }

    @Override
    public double differentiate(
        final double input)
    {
        return 1.0 / (1.0 + Math.exp(-input));
    }
    
}
