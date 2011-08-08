/*
 * File:                DefaultTemporalValue.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 28, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Date;

/**
 * The {@code DefaultTemporalValue} class is a default implementation of the
 * {@code TemporalValue} interface. It has fields for the time and the value.
 * 
 * @param   <ValueType> The value type.
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultTemporalValue<ValueType>
    extends AbstractTemporal
    implements TemporalValue<ValueType>, Pair<Date, ValueType>
{
    
    /** The value associated with the time. */
    protected ValueType value;

    /**
     * Creates a new, empty {@code DefaultTemporalValue}.
     */
    public DefaultTemporalValue()
    {
        this(null, null);
    }
    
    /**
     * Creates a new {@code DefaultTemporalValue}.
     * 
     * @param   time The time associated with the value.
     * @param   value The value associated with the time.
     */
    public DefaultTemporalValue(
        final Date time,
        final ValueType value)
    {
        super( time );
        this.setValue(value);
    }
    
    public ValueType getValue()
    {
        return this.value;
    }

    /**
     * Sets the value.
     * 
     * @param   value The new value.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    /**
     * Gets the first value of the pair, which is the time.
     * 
     * @return  The time.
     */
    public Date getFirst()
    {
        return this.getTime();
    }

    /**
     * Gets the second value fo the pair, which is the value.
     * 
     * @return  The value.
     */
    public ValueType getSecond()
    {
        return this.getValue();
    }

}
