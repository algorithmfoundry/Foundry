/*
 * File:                LogNumber.java
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

/**
 * Represents a number in log space, storing log(value) and operating directly
 * on it. It is used to operate on the number as if it were not being
 * represented in log space. Thus if you have two log numbers, a and b such that
 * a = log(x) and b = log(y), doing c = a * b will be result in c = log(x * y),
 * not c = log(x) * log(y). This means that when you do a.getValue() you will
 * get x and if you do a.getLogValue() you will get log(x).
 *
 * This class is useful if you need to do a lot of operations on data with very
 * large or very small exponents to avoid numerical overflow and underflow.
 * This can be useful, for example, for probabilities that involve many
 * products.
 *
 * All of the logarithms done by the class are done using the natural base,
 * which is often represented as ln(x) instead of log(x).
 *
 * Note that this can only represent non-negative numbers (x >= 0). Any
 * operations that will create negative numbers result in a NaN being stored
 * as the value.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class LogNumber
    extends Number
    implements Ring<LogNumber>, Comparable<LogNumber>
{
    
    /** The log of the value represented by this object, log(value). */
    protected double logValue;

    /**
     * Creates the {@code LogNumber} representing zero.
     */
    public LogNumber()
    {
        this(LogMath.LOG_0);
    }

    /**
     * Creates a new {@code LogNumber} from the given value in log-space. This
     * method is protected to avoid people calling it with being unsure if
     * value or log(value) should be given. Thus, two separate static utility
     * functions createFromValue and createFromLogValue are to be used instead.
     *
     * @param   logValue
     *      The log(value) for the value to be represented by this LogNumber.
     */
    protected LogNumber(
        final double logValue)
    {
        super();

        this.logValue = logValue;
    }

    /**
     * Copies a given LogNumber.
     *
     * @param   other
     *      The LogNumber to copy.
     */
    public LogNumber(
        final LogNumber other)
    {
        super();

        this.logValue = other.logValue;
    }

    /**
     * Creates a new {@code LogNumber} from the given value.
     *
     * @param   value
     *      A normal value.
     * @return
     *      The LogNumber representation of that value.
     */
    public static LogNumber createFromValue(
        final double value)
    {
        return new LogNumber(Math.log(value));
    }

    /**
     * Creates a new {@code LogNumber} from the given value that is already in
     * log-space. Equivalent to calling createFromValue(exp(logValue)).
     *
     * @param   logValue
     *      The value that is already in log-space.
     * @return
     *      The LogNumber representation of value = exp(logValue).
     */
    public static LogNumber createFromLogValue(
        final double logValue)
    {
        return new LogNumber(logValue);
    }

    @Override
    public LogNumber clone()
    {
        try
        {
            return (LogNumber) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof LogNumber
            && this.equals((LogNumber) other, 0.0);
    }

    @Override
    public boolean equals(
        final LogNumber other,
        final double effectiveZero)
    {
        // This if statement is to avoid creating negative numbers.
        if (this.logValue == other.logValue)
        {
            // This check for exact equality helps deal with positive
            // infinity.
            return true;
        }
        else if (this.logValue > other.logValue)
        {
            return LogMath.subtract(this.logValue, other.logValue)
                <= Math.log(effectiveZero);
        }
        else if (this.logValue < other.logValue)
        {
            return LogMath.subtract(other.logValue, this.logValue)
                <= Math.log(effectiveZero);
        }
        else
        {
            // This is to deal with NaNs.
            return false;
        }
    }

    @Override
    public int compareTo(
        final LogNumber other)
    {
        return Double.compare(this.logValue, other.logValue);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash =
            17 * hash +
            (int) (Double.doubleToLongBits(this.logValue)
            ^ (Double.doubleToLongBits(this.logValue) >>> 32));
        return hash;
    }

    @Override
    public String toString()
    {
        // Since the value is represented as a log, its true value is the
        // exponentiation of the stored log value.
        return "exp(" + this.logValue + ")";
    }


    @Override
    public LogNumber plus(
        final LogNumber other)
    {
        return new LogNumber(LogMath.add(this.logValue, other.logValue));
    }

    @Override
    public void plusEquals(
        final LogNumber other)
    {
        this.logValue = LogMath.add(this.logValue, other.logValue);
    }

    @Override
    public LogNumber minus(
        final LogNumber other)
    {
        return new LogNumber(LogMath.subtract(this.logValue, other.logValue));
    }

    @Override
    public void minusEquals(
        final LogNumber other)
    {
        this.logValue = LogMath.subtract(this.logValue, other.logValue);
    }

    /**
     * Multiples this value times another value and returns the result.
     *
     * @param   other
     *      The other value.
     * @return
     *      The result of this * other.
     */
    public LogNumber times(
        final LogNumber other)
    {
        // Multiplication inside the log becomes addition of the log values.
        return new LogNumber(this.logValue + other.logValue);
    }

    /**
     * Multiplies this value times another value and stores the result in
     * this value.
     *
     * @param   other
     *      The other value.
     */
    public void timesEquals(
        final LogNumber other)
    {
        // Multiplication inside the log becomes addition of the log values.
        this.logValue += other.logValue;
    }

    /**
     * Divides this value by another value and returns the result.
     *
     * @param   other
     *      The other value.
     * @return
     *      The result of this / other.
     */
    public LogNumber divide(
        final LogNumber other)
    {
        // Division inside the log becomes division of the log values.
        return new LogNumber(this.logValue - other.logValue);
    }

    /**
     * Divides this value by another value and stores the result in
     * this value.
     *
     * @param   other
     *      The other value.
     */
    public void divideEquals(
        final LogNumber other)
    {
        // Division inside the log becomes division of the log values.
        this.logValue -= other.logValue;
    }

    @Override
    public LogNumber dotTimes(
        final LogNumber other)
    {
        return this.times(other);
    }

    @Override
    public void dotTimesEquals(
        final LogNumber other)
    {
        this.timesEquals(other);
    }

    @Override
    public LogNumber scale(
        final double scaleFactor)
    {
        return new LogNumber(this.logValue + Math.log(scaleFactor));
    }

    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        this.logValue += Math.log(scaleFactor);
    }

    @Override
    public LogNumber negative()
    {
        // Can't negate log numbers.
        return new LogNumber(Double.NaN);
    }

    @Override
    public void negativeEquals()
    {
        // Can't negate log numbers.
        this.logValue = Double.NaN;
    }

    @Override
    public void zero()
    {
        this.logValue = LogMath.LOG_0;
    }

    @Override
    public boolean isZero()
    {
        return this.logValue == LogMath.LOG_0;
    }

    @Override
    public boolean isZero(
        final double effectiveZero)
    {
        return this.logValue <= Math.log(effectiveZero);
    }

    /**
     * Returns a new {@code LogNumber} representing this log number taken
     * to the given power.
     *
     * @param   power
     *      The power.
     * @return
     *      The log number to the given power.
     */
    public LogNumber power(
        final double power)
    {
        if (power == 0.0)
        {
            return new LogNumber(0.0);
        }
        else
        {
            return new LogNumber(this.logValue * power);
        }
    }


    /**
     * Transforms this log number by taking it to the given power.
     *
     * @param   power
     *      The power.
     */
    public void powerEquals(
        final double power)
    {
        if (power == 0.0)
        {
            this.logValue = 0.0;
        }
        else
        {
            this.logValue *= power;
        }
    }

    /**
     * A new {@code LogNumber} that is the minimum of this and another.
     *
     * @param   other
     *      Another value.
     * @return
     *      A new object containing the minimum of this value or the given
     *      value.
     */
    public LogNumber min(
        final LogNumber other)
    {
        if (this.logValue <= other.logValue || this.logValue != this.logValue)
        {
            return this.clone();
        }
        else
        {
            return other.clone();
        }
    }
    
    /**
     * Changes this value to be the minimum of this value or the given value.
     *
     * @param   other
     *      Another value.
     */
    public void minEquals(
        final LogNumber other)
    {
        if (this.logValue > other.logValue)
        {
            this.logValue = other.logValue;
        }
    }

    /**
     * A new {@code LogNumber} that is the maximum of this and another.
     *
     * @param   other
     *      Another value.
     * @return
     *      A new object containing the maximum of this value or the given
     *      value.
     */
    public LogNumber max(
        final LogNumber other)
    {
        if (this.logValue >= other.logValue || this.logValue != this.logValue)
        {
            return this.clone();
        }
        else
        {
            return other.clone();
        }
    }
    
    /**
     * Changes this value to be the maximum of this value or the given value.
     *
     * @param   other
     *      Another value.
     */
    public void maxEquals(
        final LogNumber other)
    {
        if (this.logValue < other.logValue)
        {
            this.logValue = other.logValue;
        }
    }

    /**
     * Gets the value represented by the log number. Since the value is
     * stored as log(value), this returns exp(log(value)) == value. Thus, it
     * is equivalent to Math.exp(getLogValue()).
     *
     * @return
     *      The value represented by this object.
     */
    public double getValue()
    {
        return Math.exp(this.logValue);
    }

    /**
     * Sets the value represented by the log number. Since the value is
     * stored as log(value), this sets the logValue to log(value). Thus, it
     * is equivalent to doing setLogValue(Math.log(value)).
     *
     * @param   value
     *      The value for this object to represent.
     */
    public void setValue(
        final double value)
    {
        this.logValue = Math.log(value);
    }

    /**
     * Gets the log of the value represented by this object, which is what is
     * stored in the object. That is, log(value).
     *
     * @return
     *      The log of the value represented by the object (log(value)).
     */
    public double getLogValue()
    {
        return this.logValue;
    }

    /**
     * Sets the log of the value represented by this object, which is what is
     * stored in the object. That is, log(value).
     *
     * @param   logValue
     *      The log of the value for this object to represent.
     */
    public void setLogValue(
        final double logValue)
    {
        this.logValue = logValue;
    }

    @Override
    public int intValue()
    {
        return (int) this.getValue();
    }

    @Override
    public long longValue()
    {
        return (long) this.getValue();
    }

    @Override
    public float floatValue()
    {
        return (float) this.getValue();
    }

    @Override
    public double doubleValue()
    {
        return this.getValue();
    }

}
