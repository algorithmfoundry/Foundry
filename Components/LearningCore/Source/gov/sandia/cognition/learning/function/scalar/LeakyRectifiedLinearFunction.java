/*
 * File:            LeakyRectifiedLinearFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * A leaky rectified linear unit. For a value greater than 0, the function 
 * just returns the value. For a value less than or equal to zero, it returns
 * the leakage (usually a small value, like 0.01) times the input. Its 
 * derivative is 1 for values greater than 0 and the leakage value for those
 * less than 0.  Although the derivative is ill-defined at 0 itself, the 
 * implementation treats the derivative at 0 as the amount of leakage. It is 
 * typically useful for handling the vanishing gradient problem. If no leakage
 * is used, then the standard {@link RectifiedLinearFunction} can be used 
 * instead. Also, if the leakage is 1, then a {@link IdentityScalarFunction} 
 * could be used.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 * @see     RectifiedLinearFunction
 */
@PublicationReference(
    author="Wikipedia",
    title="Rectifier",
    type=PublicationType.WebPage,
    year=2014,
    url="http://en.wikipedia.org/wiki/Rectifier_(neural_networks)"
)
public class LeakyRectifiedLinearFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{
    /** The default leakage is {@value}. */
    public static final double DEFAULT_LEAKAGE = 0.01;
    
    /** The amount of leakage for when the value is less than zero. */
    protected double leakage;

    /**
     * Creates a new {@link LeakyRectifiedLinearFunction} with the default
     * amount of leakage.
     */
    public LeakyRectifiedLinearFunction()
    {
        this(DEFAULT_LEAKAGE);
    }

    /**
     * Creates a new {@link LeakyRectifiedLinearFunction} with the given
     * leakage.
     * 
     * @param   leakage 
     *      The leakage amount. Must be between 0 and 1.
     */
    public LeakyRectifiedLinearFunction(
        final double leakage)
    {
        super();
        
        this.setLeakage(leakage);
    }

    @Override
    public LeakyRectifiedLinearFunction clone()
    {
        return (LeakyRectifiedLinearFunction) super.clone();
    }
    
    @Override
    public double evaluate(
        final double input)
    {
        if (input > 0)
        {
            return input;
        }
        else
        {
            return this.leakage * input;
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
            return this.leakage;
        }
    }

    /**
     * Sets the leakage, which is the multiplier for the value when it is
     * less than zero. It is usually a small value.
     * 
     * @return 
     *      The leakage amount. Must be between 0 and 1.
     */
    public double getLeakage()
    {
        return this.leakage;
    }

    /**
     * Sets the leakage, which is the multiplier for the value when it is
     * less than zero. It is usually a small value.
     * 
     * @param   leakage 
     *      The leakage amount. Must be between 0 and 1.
     */
    public void setLeakage(
        final double leakage)
    {
        ArgumentChecker.assertIsInRangeInclusive("leakage", leakage, 0.0, 1.0);
        this.leakage = leakage;
    }
    
}
