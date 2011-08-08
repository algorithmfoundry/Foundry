/*
 * File:                MutableLong.java
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
 * A mutable object containing a long. It is meant to be used for cases where
 * you may want to use a java.lang.Long but its value will be updated
 * frequently, such as in a map or other data structure. Many of the
 * implementations of the methods in this class are based on the behavior
 * described in java.lang.Long.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class MutableLong
    extends Number
    implements CloneableSerializable, Comparable<MutableLong>,
        Ring<MutableLong>, Vectorizable
{
    /** Serial version ID for the class. */
    private static final long serialVersionUID = 20110602L;

    /** The value. Note: This is public just for performance reasons when
     *  people don't want to do the getter/setter for overhead reasons. */
    public long value;

    /**
     * Creates an {@code MutableLong} with an initial value of zero.
     */
    public MutableLong()
    {
        this(0);
    }

    /**
     * Creates an {@code MutableLong} with the given value.
     *
     * @param   value
     *      The value to store in the object.
     */
    public MutableLong(
        final long value)
    {
        super();

        this.value = value;
    }

    /**
     * Creates a copy of a {@code MutableLong}.
     *
     * @param   other
     *      The other value.
     */
    public MutableLong(
        final MutableLong other)
    {
        this(other.value);
    }

    @Override
    public MutableLong clone()
    {
        try
        {
            return (MutableLong) super.clone();
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
            || (other instanceof MutableLong
                && this.equals((MutableLong) other));
    }

    /**
     * Determines if this MutableLong is equal to another MutableLong.
     * Provides a convenience for not casting.
     *
     * @param   other
     *      The other value.
     * @return
     *      True if the two values are equal and false otherwise.
     */
    public boolean equals(
        final MutableLong other)
    {
        return this.equals(other.value);
    }

    /**
     * Determines if this MutableLong's value is equal to a given long.
     *
     * @param   other
     *      A long.
     * @return
     *      True if the two values are equal and false otherwise.
     */
    public boolean equals(
        final long other)
    {
        return this.value == other;
    }

    @Override
    public int compareTo(
        final MutableLong other)
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
        // This is the recommended way to generate a HashCode for a Long.
        return (int) (this.value ^ (this.value >>> 32));
    }

    @Override
    public int intValue()
    {
        return (int) this.value;
    }

    @Override
    public long longValue()
    {
        return this.value;
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
    public long getValue()
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
        final long value)
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
        final MutableLong other,
        final double effectiveZero)
    {
        return Math.abs(this.value - other.value) <= effectiveZero;
    }

    @Override
    public MutableLong plus(
        final MutableLong other)
    {
        return new MutableLong(this.value + other.value);
    }

    @Override
    public void plusEquals(
        final MutableLong other)
    {
        this.value += other.value;
    }

    @Override
    public MutableLong minus(
        final MutableLong other)
    {
        return new MutableLong(this.value - other.value);
    }

    @Override
    public void minusEquals(
        final MutableLong other)
    {
        this.value -= other.value;
    }

    @Override
    public MutableLong dotTimes(
        final MutableLong other)
    {
        return new MutableLong(this.value * other.value);
    }

    @Override
    public void dotTimesEquals(
        final MutableLong other)
    {
        this.value *= other.value;
    }

    @Override
    public MutableLong scale(
        final double scaleFactor)
    {
        return new MutableLong((long) (this.value * scaleFactor));
    }

    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        this.value *= scaleFactor;
    }

    @Override
    public MutableLong negative()
    {
        return new MutableLong(-this.value);
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
        this.value = (long) parameters.getElement(0);
    }
}
