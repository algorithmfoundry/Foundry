/*
 * File:                MutableDouble.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 31, 2011, Sandia Corporation.
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
 * A mutable object containing a double. It is meant to be used for cases where
 * you may want to use a java.lang.Double but its value will be updated
 * frequently, such as in a map or other data structure. Many of the
 * implementations of the methods in this class are based on the behavior
 * described in java.lang.Double.
 * 
 * @author  Justin Basilico
 * @since   3.2.0
 */
public class MutableDouble
    extends Number
    implements CloneableSerializable, Comparable<MutableDouble>,
        Field<MutableDouble>, Vectorizable
{
    
    /** Serial version ID for the class. */
    private static final long serialVersionUID = 20110131L;

    /** The value. Note: This is public just for performance reasons when
     *  people don't want to do the getter/setter for overhead reasons. */
    public double value;

    /**
     * Creates an {@code MutableDouble} with an initial value of zero.
     */
    public MutableDouble()
    {
        this(0.0);
    }

    /**
     * Creates an {@code MutableDouble} with the given value.
     *
     * @param   value
     *      The value to store in the object.
     */
    public MutableDouble(
        final double value)
    {
        super();

        this.value = value;
    }

    /**
     * Creates a copy of a {@code MutableDouble}.
     *
     * @param   other
     *      The other value.
     */
    public MutableDouble(
        final MutableDouble other)
    {
        this(other.value);
    }

    @Override
    public MutableDouble clone()
    {
        try
        {
            return (MutableDouble) super.clone();
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
            || (other instanceof MutableDouble
                && this.equals((MutableDouble) other));
    }

    /**
     * Determines if this MutableDouble is equal to another MutableDouble.
     * Provides a convenience for not casting.
     *
     * @param   other
     *      The other value.
     * @return
     *      True if the two values are equal and false otherwise.
     */
    public boolean equals(
        final MutableDouble other)
    {
        return this.equals(other.value);
    }

    /**
     * Determines if this MutableDouble's value is equal to a given double.
     * This is done using the Double equals method, which converts the values
     * to a long and then does equality on those. Thus, for strange values,
     * this may not be exactly the same as ==. For example, -0.0 == +0.0  is true
     * but -0.0.equals(+0.0) is false. Likewise, NaN == NaN is false, but
     * NaN.equals(NaN) is true. Thus, this method conforms to the Double
     * equality method.
     *
     *
     * @param   other
     *      A double.
     * @return
     *      True if the two values are equal (according to Double.equals) and
     *      false otherwise.
     */
    public boolean equals(
        final double other)
    {
        return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other);
    }

    @Override
    public int compareTo(
        final MutableDouble other)
    {
        return Double.compare(this.value, other.value);
    }

    @Override
    public int hashCode()
    {
        final long bits = Double.doubleToLongBits(this.value);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public int intValue()
    {
        return (int) this.value;
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
        return this.value;
    }

    /**
     * Gets the value stored in the object.
     *
     * @return
     *      The value.
     */
    public double getValue()
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
        final double value)
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
        final MutableDouble other,
        final double effectiveZero)
    {
        return Math.abs(this.value - other.value) <= effectiveZero;
    }

    @Override
    public MutableDouble plus(
        final MutableDouble other)
    {
        return new MutableDouble(this.value + other.value);
    }

    @Override
    public void plusEquals(
        final MutableDouble other)
    {
        this.value += other.value;
    }

    @Override
    public MutableDouble minus(
        final MutableDouble other)
    {
        return new MutableDouble(this.value - other.value);
    }

    @Override
    public void minusEquals(
        final MutableDouble other)
    {
        this.value -= other.value;
    }

    @Override
    public MutableDouble times(
        final MutableDouble other)
    {
        return new MutableDouble(this.value * other.value);
    }

    @Override
    public void timesEquals(
        final MutableDouble other)
    {
        this.value *= other.value;
    }

    @Override
    public MutableDouble divide(
        final MutableDouble other)
    {
        return new MutableDouble(this.value / other.value);
    }

    @Override
    public void divideEquals(
        final MutableDouble other)
    {
        this.value /= other.value;
    }
    
    @Override
    public MutableDouble dotTimes(
        final MutableDouble other)
    {
        return new MutableDouble(this.value * other.value);
    }

    @Override
    public void dotTimesEquals(
        final MutableDouble other)
    {
        this.value *= other.value;
    }

    @Override
    public MutableDouble scale(
        final double scaleFactor)
    {
        return new MutableDouble(this.value * scaleFactor);
    }

    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        this.value *= scaleFactor;
    }

    @Override
    public MutableDouble scaledPlus(
        final double scaleFactor,
        final MutableDouble other)
    {
        return new MutableDouble(this.value + scaleFactor * other.value);
    }

    @Override
    public void scaledPlusEquals(
        final double scaleFactor,
        final MutableDouble other)
    {
        this.value += scaleFactor * other.value;
    }

    @Override
    public MutableDouble scaledMinus(
        final double scaleFactor,
        final MutableDouble other)
    {
        return new MutableDouble(this.value - scaleFactor * other.value);
    }

    @Override
    public void scaledMinusEquals(
        final double scaleFactor,
        final MutableDouble other)
    {
        this.value -= scaleFactor * other.value;
    }

    @Override
    public MutableDouble negative()
    {
        return new MutableDouble(-this.value);
    }

    @Override
    public void negativeEquals()
    {
        this.value = -this.value;
    }

    @Override
    public MutableDouble inverse()
    {
        return new MutableDouble(1.0 / this.value);
    }
    
    @Override
    public void inverseEquals()
    {
        this.value = 1.0 / this.value;
    }

    @Override
    public void zero()
    {
        this.value = 0.0;
    }

    @Override
    public boolean isZero()
    {
        return this.value == 0.0 || this.value == -0.0;
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
        this.value = parameters.getElement(0);
    }

}
