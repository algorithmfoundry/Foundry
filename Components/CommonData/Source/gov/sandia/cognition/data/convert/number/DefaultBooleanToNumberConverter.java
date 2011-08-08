/*
 * File:                DefaultBooleanToNumberConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 02, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert.number;

import gov.sandia.cognition.data.convert.AbstractReverseCachedDataConverter;
import gov.sandia.cognition.data.convert.AbstractReversibleDataConverter;


/**
 * Converts a {@code Boolean} to a {@code Number} by using predefined values
 * for true, false, and (optionally) null.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultBooleanToNumberConverter
    extends AbstractReverseCachedDataConverter<Boolean, Number, DefaultBooleanToNumberConverter.Reverse>
{

    /** The default value for true is {@value}. */
    public static final double DEFAULT_TRUE_VALUE = +1.0;

    /** The default value for false is {@value}. */
    public static final double DEFAULT_FALSE_VALUE = -1.0;

    /** The default value for null is {@value}. */
    public static final double DEFAULT_NULL_VALUE = 0.0;

    /** The number to use to represent a true value. */
    protected Number trueValue;

    /** The number to use to represent a false value. */
    protected Number falseValue;

    /** The number to use to represent a null value. */
    protected Number nullValue;

    /**
     * Creates a new {@code DefaultBooleanToNumberConverter} with default values.
     */
    public DefaultBooleanToNumberConverter()
    {
        this(DEFAULT_TRUE_VALUE, DEFAULT_FALSE_VALUE, DEFAULT_NULL_VALUE);
    }

    /**
     * Creates a new {@code DefaultBooleanToNumberConverter}.
     * 
     * @param   trueValue The number to use for true.
     * @param   falseValue The number to use for false.
     * @param   nullValue The number to use for null.
     */
    public DefaultBooleanToNumberConverter(
        final Number trueValue,
        final Number falseValue,
        final Number nullValue)
    {
        super();

        this.trueValue = trueValue;
        this.falseValue = falseValue;
        this.nullValue = nullValue;
    }

    /**
     * Converts an input boolean to a number.
     * 
     * @param   input
     *      The input value to convert.
     * @return
     *      The boolean converted to a number.
     */
    public Number evaluate(
        final Boolean input)
    {
        return this.convertToNumber(input);
    }

    /**
     * Converts the given boolean to a number using the 
     * 
     * @param   input
     *      The input boolean to convert to a number.
     * @return
     *      The number value for the boolean.
     */
    public Number convertToNumber(
        final Boolean input)
    {
        if (input == null)
        {
            return this.getNullValue();
        }
        else if (input.booleanValue())
        {
            return this.getTrueValue();
        }
        else
        {
            return this.getFalseValue();
        }
    }

    /**
     * Converts the given number to a boolean value by determining if it is
     * closer to the number representing true or the number representing false.
     * 
     * @param   input
     *      The input number to convert to a boolean.
     * @return
     *      The boolean value of the number.
     */
    public Boolean convertToBoolean(
        final Number input)
    {
        if (input == null)
        {
            return null;
        }

        final double value = input.doubleValue();
        if (this.nullValue != null && this.nullValue.equals(value))
        {
// TODO: Handle the case where someone is treating null as true or false 
// automatically.
            return null;
        }
// TODO: Cache the double value.
        else if (Math.abs(value - this.trueValue.doubleValue()) <=
            Math.abs(value - this.falseValue.doubleValue()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected DefaultBooleanToNumberConverter.Reverse createReverse()
    {
        return new Reverse();
    }

    /**
     * Gets the number that represents a true value.
     * 
     * @return  The number that represents a true value.
     */
    public Number getTrueValue()
    {
        return this.trueValue;
    }

    /**
     * Sets the number that represents a true value.
     * 
     * @param   trueValue
     *      The number that represents a true value.
     */
    public void setTrueValue(
        final Number trueValue)
    {
        this.trueValue = trueValue;
    }

    /**
     * Gets the number that represents a falue value.
     * 
     * @return  The number that represents a false value.
     */
    public Number getFalseValue()
    {
        return this.falseValue;
    }

    /**
     * Sets the number that represents a falue value.
     * 
     * @param   falseValue The number that represents a false value.
     */
    public void setFalseValue(
        final Number falseValue)
    {
        this.falseValue = falseValue;
    }

    /**
     * Gets the number that represents a null value.
     * 
     * @return  The number that represents a null value.
     */
    public Number getNullValue()
    {
        return this.nullValue;
    }

    /**
     * Sets the number that represents a null value.
     * 
     * @param   nullValue The number that represents a null value.
     */
    public void setNullValue(
        final Number nullValue)
    {
        this.nullValue = nullValue;
    }

    /**
     * The reverse converter for the {@code DefaultBooleanToNumberConverter}.
     */
    public class Reverse
        extends AbstractReversibleDataConverter<Number, Boolean>
    {

        /**
         * Creates a new reverse converter for the 
         * {@code DefaultBooleanToNumberConverter}.
         */
        public Reverse()
        {
            super();
        }

        /**
         * Converts the given number to a boolean.
         * 
         * @param   input 
         *      The input number to convert.
         * @return
         *      The boolean whose number representation is closest: 
         *      true or false.
         */
        public Boolean evaluate(
            final Number input)
        {
            return convertToBoolean(input);
        }

        /**
         * Reverses the converter, which is the original converter.
         * 
         * @return The original {@code DefaultBooleanToNumberConverter}.
         */
        public DefaultBooleanToNumberConverter reverse()
        {
            return DefaultBooleanToNumberConverter.this;
        }

    }

}
