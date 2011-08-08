/*
 * File:                AbstractTextualConverterTest.java
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
import org.junit.Test;

/**
 * Unit tests for class AbstractTextualConverter.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class AbstractTextualConverterTest
{
    /**
     * Creates a new test.
     */
    public AbstractTextualConverterTest()
    {
    }


    /**
     * Test of constructors of class AbstractTextualConverter.
     */
    @Test
    public void testConstructors()
    {
         AbstractTextualConverter<?, ?> instance = new DummyTextualConverter();
    }

    /**
     * Dummy implementation of AbstractTextualConverter.
     */
    private class DummyTextualConverter
        extends AbstractTextualConverter<Object, DefaultTextual>
    {

        public DummyTextualConverter()
        {
            super();
        }

        @Override
        public Iterable<DefaultTextual> convertAll(
            final Iterable<? extends Object> inputs)
        {
            throw new UnsupportedOperationException();
        }

    }
}