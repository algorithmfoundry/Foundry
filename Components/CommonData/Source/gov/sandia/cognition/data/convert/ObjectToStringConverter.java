/*
 * File:                ObjectToStringConverter.java
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

package gov.sandia.cognition.data.convert;

/**
 * Converts an {@code Object} to a {@code String} using the {@code toString}
 * method.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class ObjectToStringConverter
    extends AbstractDataConverter<Object, String>
{

    /**
     * Creates a new {@code ObjectToStringConverter}.
     */
    public ObjectToStringConverter()
    {
        super();
    }

    /**
     * Converts the given {@code Object} to an {@code String} by calling the
     * {@code toString} method.
     * 
     * @param   input
     *      The input to convert.
     * @return
     *      The String representation of that input. If null is passed in,
     *      null is returned.
     */
    public String evaluate(
        final Object input)
    {
        return input == null ? null : input.toString();
    }

}
