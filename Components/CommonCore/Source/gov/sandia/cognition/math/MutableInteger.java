/*
 * File:                MutableInteger.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 14, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector1;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * A mutable object containing an integer. It is meant to be used for cases where
 * you may want to use a java.lang.Integer but its value will be updated
 * frequently, such as in a map or other data structure. Many of the
 * implementations of the methods in this class are based on the behavior
 * described in java.lang.Integer.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class MutableInteger
    extends Number
    implements CloneableSerializable, Comparable<MutableInteger>,
        Ring<MutableInteger>, Vectorizable
{
    /** Serial version ID for the class. */
    private static final long serialVersionUID = 20110601L;


    /** The value. Note: This is public just for performance reasons when
     *  people don't want to do the getter/setter for overhead reasons. */
    public int value;


    /**
     * Creates an {@code MutableInteger} with an initial value of zero.
     */
    public MutableInteger()
    {
        this(0);
    }

    /**
     * Creates an {@code MutableInteger} with the given value.
     *
     * @param   value
     *      The value to store in the object.
     */
    public MutableInteger(
        final int value)
    {
        super();

        this.value = value;
    }

    /**
     * Creates a copy of a {@code MutableInteger}.
     *
     * @param   other
     *      The other value.
     */
    public MutableInteger(
        final MutableInteger other)
    {
        this(other.value);
    }

    @Override
    public MutableInteger clone()
    {
        try
        {
            return (MutableInteger) super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other == this
            || (other instanceof MutableInteger
                && this.equals((MutableInteger) other));
    }

    /**
     * Determines if this MutableInteger is equal to another MutableInteger.
     * Provides a convenience for not casting.
     *
     * @param   other
     *      The other value.
     * @return
     *      True if the two values are equal and false otherwise.
     */
    public boolean equals(
        final MutableInteger other)
    {
        return this.equals(other.value);
    }

    /**
     * Determines if this MutableInteger's value is equal to a given integer.
     *
     * @param   other
     *      An integer.
     * @return
     *      True if the two values are equal and false otherwise.
     */
    public boolean equals(
        final int other)
    {
        return this.value == other;
    }

    @Override
    public int compareTo(
        final MutableInteger other)
    {
        if (this.value < other.value)
        {
            return -1;
        }
        else if (this.value == other.value)
        {
            return 0;
        }
        else
        {
            return +1;
        }
    }

    @Override
    public int hashCode()
    {
        return this.value;
    }

    @Override
    public int intValue()
    {
        return this.value;
    }

    @Override
    public long longValue()
    {
        return (long) this.value;
    }

    @Override
    public float floatValue()
    {
        return (float) this.value;
    }

    @Override
    public double doubleValue()
    {
        return (double) this.value;
    }

    /**
     * Gets the value stored in the object.
     *
     * @return
     *      The value.
     */
    public int getValue()
    {
        return this.value;
    }

    /**
     * Sets the value stored in the object.
     *
     * @param   value
     *      The value.
     */
    public void setValue(
        final int value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "" + this.value;
    }

    @Override
    public boolean equals(
        final MutableInteger other,
        final double effectiveZero)
    {
        return Math.abs(this.value - other.value) <= effectiveZero;
    }

    @Override
    public MutableInteger plus(
        final MutableInteger other)
    {
        return new MutableInteger(this.value + other.value);
    }

    @Override
    public void plusEquals(
        final MutableInteger other)
    {
        this.value += other.value;
    }

    @Override
    public MutableInteger minus(
        final MutableInteger other)
    {
        return new MutableInteger(this.value - other.value);
    }

    @Override
    public void minusEquals(
        final MutableInteger other)
    {
        this.value -= other.value;
    }

    @Override
    public MutableInteger dotTimes(
        final MutableInteger other)
    {
        return new MutableInteger(this.value * other.value);
    }

    @Override
    public void dotTimesEquals(
        final MutableInteger other)
    {
        this.value *= other.value;
    }

    @Override
    public MutableInteger scale(
        final double scaleFactor)
    {
        return new MutableInteger((int) (this.value * scaleFactor));
    }

    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        this.value *= scaleFactor;
    }

    @Override
    public MutableInteger negative()
    {
        return new MutableInteger(-this.value);
    }

    @Override
    public void negativeEquals()
    {
        this.value = -this.value;
    }

    @Override
    public void zero()
    {
        this.value = 0;
    }

    @Override
    public boolean isZero()
    {
        return this.value == 0;
    }

    @Override
    public boolean isZero(
        final double effectiveZero)
    {
        return Math.abs(this.value) <= effectiveZero;
    }

    @Override
    public Vector1 convertToVector()
    {
        return new Vector1(this.value);
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(1);
        this.value = (int) parameters.getElement(0);
    }
}
