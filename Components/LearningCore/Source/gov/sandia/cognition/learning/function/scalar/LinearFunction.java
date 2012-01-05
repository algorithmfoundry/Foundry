/*
 * File:                LinearFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * This function acts as a simple linear function of the form f(x) = m*x + b.
 * Here m is known as the slope and b as the offset. Other terms for m and b
 * are scale/bias, beta_1/beta_0. 
 *
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.1
 */
public class LinearFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /** The default slope is {@value}. */
    public static final double DEFAULT_SLOPE = 1.0;

    /** The default offset is {@value}. */
    public static final double DEFAULT_OFFSET = 0.0;

    /** The slope (m). */
    protected double slope;

    /** The offset (b). */
    protected double offset;

    /** 
     * Creates a new {@code LinearFunction} with a slope of 1 and offset of 0.
     * This makes f(x) = x.
     */
    public LinearFunction()
    {
        this(DEFAULT_SLOPE, DEFAULT_OFFSET);
    }

    /**
     * Creates a new {@code LinearFunction} with the given slope and offset.
     *
     * @param   slope
     *      The slope.
     * @param   offset
     *      The offset.
     */
    public LinearFunction(
        final double slope,
        final double offset)
    {
        super();

        this.setSlope(slope);
        this.setOffset(offset);
    }

    /**
     * Creates a copy of a given {@code LinearFunction}.
     *
     * @param   other
     *      The LinearFunction to copy.
     */
    public LinearFunction(
        final LinearFunction other)
    {
        this(other.getSlope(), other.getOffset());
    }

    @Override
    public LinearFunction clone()
    {
        return (LinearFunction) super.clone();
    }

    @Override
    public double evaluate(
        final double input)
    {
        return (this.slope * input) + this.offset;
    }

    @Override
    public double differentiate(
        final double input)
    {
        return this.slope;
    }

    /**
     * Gets the slope of the function, which is the m term in: f(x) = m*x + b.
     *
     * @return
     *      The slope.
     */
    public double getSlope()
    {
        return this.slope;
    }

    /**
     * Sets the slope of the function, which is the m term in: f(x) = m*x + b.
     *
     * @param   slope
     *      The slope.
     */
    public void setSlope(
        final double slope)
    {
        this.slope = slope;
    }

    /**
     * Gets the offset of the function, which is the b term in: f(x) = m*x + b.
     *
     * @return
     *      The offset.
     */
    public double getOffset()
    {
        return this.offset;
    }

    /**
     * Sets the offset of the function, which is the b term in: f(x) = m*x + b.
     *
     * @param   offset
     *      The offset.
     */
    public void setOffset(
        final double offset)
    {
        this.offset = offset;
    }

}
