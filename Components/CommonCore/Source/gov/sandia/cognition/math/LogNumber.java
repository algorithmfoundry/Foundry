/*
 * File:            LogNumber.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

/**
 * Represents a number in log-space, storing the log of the absolute value
 * log(|value|) and the sign of the value sign(value). It is used to operate
 * on a number as if it were not being represented in log space.
 * Thus if you have two log numbers, a and b such that
 * a = log(x) and b = log(y), doing c = a * b will be result in c = log(x * y),
 * not c = log(x) * log(y). This means that when you do a.getValue() you will
 * get x and if you do a.getLogValue() you will get log(x).
 *
 * <br/><br/>
 *
 * This class is useful if you need to do a lot of operations on data with very
 * large or very small exponents to avoid numerical overflow and underflow.
 * This can be useful, for example, for probabilities that involve many
 * products.
 *
 * <br/><br/>
 *
 * All of the logarithms done by the class are done using the natural base (e),
 * which is often denoted as ln(x) instead of log(x).
 *
 * <br/><br/>
 *
 * If you know that all of your numbers are positive, such as if they are all
 * probabilities, then you may want to use an {@link UnsignedLogNumber} instead,
 * since it does not have to maintain the sign information, so it will consume
 * less memory and will be faster.
 *
 * @author  Justin Basilico
 * @version 3.3.0
 * @see     UnsignedLogNumber
 * @see     LogMath
 */
public class LogNumber
    extends Number
    implements Field<LogNumber>, Comparable<LogNumber>
{

    /** The sign of the value, sign(value). True for negative, and false for
     *  positive. */
    protected boolean negative;

    /** The log of the absolute value represented by this object, log(|value|).
     */
    protected double logValue;

    /**
     * Creates the {@code LogNumber} representing zero.
     */
    public LogNumber()
    {
        this(false, LogMath.LOG_0);
    }

    /**
     * Creates a new {@code LogNumber} from the given value in log-space.
     * This method is protected to avoid people calling it with being unsure if
     * value or log(value) should be given. Thus, two separate static utility
     * functions createFromValue and createFromLogValue are to be used instead.
     *
     * @param   negative
     *      The flag indicating if the value is positive (false) or negative
     *      (true).
     * @param   logValue
     *      The log(value) for the value to be represented by this
     * LogNumber.
     */
    protected LogNumber(
        final boolean negative,
        final double logValue)
    {
        super();

        this.negative = negative;
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

        this.negative = other.negative;
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
        if (value >= 0.0)
        {
            return new LogNumber(false, Math.log(value));
        }
        else
        {
            return new LogNumber(true, Math.log(-value));
        }
    }

    /**
     * Creates a new {@code LogNumber} from the given value that is
     * already in log-space. Equivalent to calling createFromValue(
     * exp(logValue)) or createFromLogValue(false, logValue).
     *
     * @param   logValue
     *      The value that is already in log-space.
     * @return
     *      The LogNumber representation of value = exp(logValue).
     */
    public static LogNumber createFromLogValue(
        final double logValue)
    {
        return new LogNumber(false, logValue);
    }
    
    /**
     * Creates a new {@code LogNumber} from the given value that is
     * already in log-space. Equivalent to calling createFromValue(
     * (negative ? +1.0 : -1.0) * exp(logValue)).
     *
     * @param   negative
     *      True if the value is negative.
     * @param   logValue
     *      The value that is already in log-space.
     * @return
     *      The LogNumber representation of value = sign * exp(logValue).
     */
    public static LogNumber createFromLogValue(
        final boolean negative,
        final double logValue)
    {
        return new LogNumber(negative, logValue);
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
        if (this.negative == other.negative)
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
        else
        {
            return LogMath.add(this.logValue, other.logValue)
                <= Math.log(effectiveZero);
        }
    }

    @Override
    public int compareTo(
        final LogNumber other)
    {
        if (this.negative)
        {
            if (other.negative)
            {
                return -Double.compare(this.logValue, other.logValue);
            }
            else
            {
                return -1;
            }
        }
        else
        {
            if (other.negative)
            {
                return 1;
            }
            else
            {
                return Double.compare(this.logValue, other.logValue);
            }
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + (this.negative ? 1 : 0);
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
        return (this.negative ? "-" : "+") + "exp(" + this.logValue + ")";
    }
    
    @Override
    public LogNumber plus(
        final LogNumber other)
    {
        final LogNumber result = this.clone();
        result.plusEquals(other);
        return result;
    }

    @Override
    public void plusEquals(
        final LogNumber other)
    {
        if (this.negative == other.negative)
        {
            this.logValue = LogMath.add(this.logValue, other.logValue);
        }
        else if (this.logValue > other.logValue)
        {
            this.logValue = LogMath.subtract(this.logValue, other.logValue);
        }
        else
        {
            this.negative = other.negative;
            this.logValue = LogMath.subtract(other.logValue, this.logValue);
        }
    }

    @Override
    public LogNumber minus(
        final LogNumber other)
    {
        final LogNumber result = this.clone();
        result.minusEquals(other);
        return result;
    }

    @Override
    public void minusEquals(
        final LogNumber other)
    {
        if (this.negative != other.negative)
        {
            this.logValue = LogMath.add(this.logValue, other.logValue);
        }
        else if (this.logValue < other.logValue)
        {
            this.negative = !other.negative;
            this.logValue = LogMath.subtract(other.logValue, this.logValue);
        }
        else
        {
            this.logValue = LogMath.subtract(this.logValue, other.logValue);
        }
    }

    /**
     * Multiples this value times another value and returns the result.
     *
     * @param   other
     *      The other value.
     * @return
     *      The result of this * other.
     */
    @Override
    public LogNumber times(
        final LogNumber other)
    {
        // Multiplication inside the log becomes addition of the log values.
        return new LogNumber(this.negative ^ other.negative,
            this.logValue + other.logValue);
    }

    /**
     * Multiplies this value times another value and stores the result in
     * this value.
     *
     * @param   other
     *      The other value.
     */
    @Override
    public void timesEquals(
        final LogNumber other)
    {
        // Multiplication inside the log becomes addition of the log values.
        this.negative ^= other.negative;
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
    @Override
    public LogNumber divide(
        final LogNumber other)
    {
        // Division inside the log becomes subtraction of the log values.
        return new LogNumber(this.negative ^ other.negative,
            this.logValue - other.logValue);
    }

    /**
     * Divides this value by another value and stores the result in
     * this value.
     *
     * @param   other
     *      The other value.
     */
    @Override
    public void divideEquals(
        final LogNumber other)
    {
        // Division inside the log becomes subtraction of the log values.
        this.negative ^= other.negative;
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
        final LogNumber result = this.clone();
        result.scaleEquals(scaleFactor);
        return result;
    }

    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        if (scaleFactor == 0.0)
        {
            this.negative = false;

            if (    this.logValue == Double.POSITIVE_INFINITY
                 || Double.isNaN(this.logValue))
            {
                this.logValue = Double.NaN;
            }
            else
            {
                this.logValue = LogMath.LOG_0;
            }
        }
        else if (scaleFactor < 0.0)
        {
            this.negative = !this.negative;
            this.logValue += Math.log(-scaleFactor);
        }
        else // (scaleFactor > 0.0)
        {
            this.logValue += Math.log(scaleFactor);
        }
    }

    @Override
    public LogNumber scaledPlus(
        final double scaleFactor,
        final LogNumber other)
    {
        final LogNumber result = this.clone();
        result.scaledPlusEquals(scaleFactor, other);
        return result;
    }

    @Override
    public void scaledPlusEquals(
        final double scaleFactor,
        final LogNumber other)
    {
        this.plusEquals(other.scale(scaleFactor));
    }

    @Override
    public LogNumber scaledMinus(
        final double scaleFactor,
        final LogNumber other)
    {
        final LogNumber result = this.clone();
        result.scaledMinusEquals(scaleFactor, other);
        return result;
    }

    @Override
    public void scaledMinusEquals(
        final double scaleFactor,
        final LogNumber other)
    {
        this.minusEquals(other.scale(scaleFactor));
    }

    @Override
    public LogNumber negative()
    {
        return new LogNumber(!this.negative, this.logValue);
    }

    @Override
    public void negativeEquals()
    {
        this.negative = !this.negative;
    }

    @Override
    public void inverseEquals()
    {
        this.logValue = -this.logValue;
    }

    @Override
    public LogNumber inverse()
    {
        return new LogNumber(this.negative, -this.logValue);
    }
    
    @Override
    public void zero()
    {
        this.negative = false;
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
     * Returns a new {@code LogNumber} that represents the absolute value
     * of this {@code LogNumber}.
     *
     * @return
     *      The absolute value of this log number.
     */
    public LogNumber absoluteValue()
    {
        return new LogNumber(false, this.logValue);
    }

    /**
     * Transforms this value to be its absolute value.
     */
    public void absoluteValueEquals()
    {
        this.negative = false;
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
        final LogNumber result = this.clone();
        result.powerEquals(power);
        return result;
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
            this.negative = false;
        }
        else if (!this.negative)
        {
            this.logValue *= power;
        }
        else if (this.negative)
        {
            final double powerInt = Math.floor(power);
            if (powerInt == power)
            {
                this.negative = ((powerInt % 2) == 1) || powerInt == -1;
                this.logValue *= power;
            }
            else if (this.logValue == Double.POSITIVE_INFINITY)
            {
                this.negative = false;
                this.logValue = Double.NEGATIVE_INFINITY;
            }
            else if (power == Double.POSITIVE_INFINITY
                && this.logValue != 0.0)
            {
                this.negative = false;
                this.logValue = Double.NEGATIVE_INFINITY;
            }
            else
            {
                // Can only take non-integer powers of positive numbers.
                this.logValue = Double.NaN;
            }
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
        final LogNumber result = this.clone();
        result.minEquals(other);
        return result;
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
        if (this.compareTo(other) > 0)
        {
            this.negative = other.negative;
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
        final LogNumber result = this.clone();
        result.maxEquals(other);
        return result;
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
        if (this.compareTo(other) < 0)
        {
            this.negative = other.negative;
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
        if (this.negative)
        {
            return -Math.exp(this.logValue);
        }
        else
        {
            return +Math.exp(this.logValue);
        }
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
        if (value >= 0.0)
        {
            this.negative = false;
            this.logValue = Math.log(value);
        }
        else
        {
            this.negative = true;
            this.logValue = Math.log(-value);
        }
    }

    /**
     * Gets whether or not this value has a negative sign.
     *
     * @return
     *      True if this value has a negative sign.
     */
    public boolean isNegative()
    {
        return this.negative;
    }

    /**
     * Sets whether or not this value has a negative sign.
     *
     * @param   negative
     *      True if this value has a negative sign.
     */
    public void setNegative(
        final boolean negative)
    {
        this.negative = negative;
    }

    /**
     * Gets the log of the value represented by this object, which is what is
     * stored in the object. That is, log(|value|).
     *
     * @return
     *      The log of the value represented by the object (log(|value|)).
     */
    public double getLogValue()
    {
        return this.logValue;
    }

    /**
     * Sets the log of the value represented by this object, which is what is
     * stored in the object. That is, log(|value|).
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
