/*
 * File:                ObjectToStringTextualConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Text Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.text.convert;

import gov.sandia.cognition.text.DefaultTextual;

/**
 * A text converter that can take in any type of object and then returns a
 * new {@code DefaultTextual} that wraps that object's {@code toString()}.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class ObjectToStringTextualConverter
    extends AbstractSingleTextualConverter<Object, DefaultTextual>
{

    /**
     * Creates a new {@code ObjectToStringTextualConverter}.
     */
    public ObjectToStringTextualConverter()
    {
        super();
    }

    @Override
    public DefaultTextual evaluate(
        final Object input)
    {
        return new DefaultTextual("" + input);
    }

}
