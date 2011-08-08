/*
 * File:                StringToIntegerConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert.number;

import gov.sandia.cognition.data.convert.AbstractReverseCachedDataConverter;
import gov.sandia.cognition.data.convert.ObjectToStringConverter;

/**
 * Converts a {@code String} to a {@code Integer} using the 
 * {@code Integer.valueOf} method.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class StringToIntegerConverter
    extends AbstractReverseCachedDataConverter<String, Integer, ObjectToStringConverter>
{

    /**
     * Creates a new {@code StringToIntegerConverter}.
     */
    public StringToIntegerConverter()
    {
        super();
    }

    /**
     * Converts the given {@code String} to a {@code Integer}.
     * 
     * @param   input 
     *      The String to convert.
     * @return  
     *      The integer value of the String.
     */
    public Integer evaluate(
        final String input)
    {
// TODO: Figure out how to handle exception from this parsing.
        return Integer.valueOf(input);
    }

    protected ObjectToStringConverter createReverse()
    {
        return new ObjectToStringConverter();
    }

}
