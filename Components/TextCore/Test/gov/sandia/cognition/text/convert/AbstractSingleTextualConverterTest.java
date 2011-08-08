/*
 * File:                AbstractSingleTextualConverterTest.java
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

import java.util.Arrays;
import gov.sandia.cognition.collection.CollectionUtil;
import java.util.Collections;
import gov.sandia.cognition.text.DefaultTextual;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractSingleTextualConverter.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class AbstractSingleTextualConverterTest
{
    /**
     * Creates a new test.
     */
    public AbstractSingleTextualConverterTest()
    {
    }

    /**
     * Test of constructors of class AbstractSingleTextualConverter.
     */
    @Test
    public void testConstructors()
    {
         AbstractSingleTextualConverter<?, ?> instance = new DummySingleTextualConverter();
    }

    /**
     * Test of convert method, of class AbstractSingleTextualConverter.
     */
    @Test
    public void testConvert()
    {
        AbstractSingleTextualConverter<Object, ?> instance =
            new DummySingleTextualConverter();
        assertEquals(new DefaultTextual("dummy"), instance.convert(null));
        assertEquals(new DefaultTextual("dummy"), instance.convert(new Object()));
    }

    /**
     * Test of convertAll method, of class AbstractSingleTextualConverter.
     */
    @Test
    public void testConvertAll()
    {
        AbstractSingleTextualConverter<Object, ?> instance =
            new DummySingleTextualConverter();
        List<?> result = instance.convertAll(Collections.emptyList());
        assertTrue(result.isEmpty());

        result = instance.convertAll(Collections.singletonList(new Object()));
        assertEquals(1, result.size());
        assertEquals(new DefaultTextual("dummy"), CollectionUtil.getFirst(result));


        result = instance.convertAll(Arrays.asList(new Object(), new Object()));
        assertEquals(2, result.size());
        assertEquals(new DefaultTextual("dummy"), CollectionUtil.getElement(result, 0));
        assertEquals(new DefaultTextual("dummy"), CollectionUtil.getElement(result, 1));
    }

    private class DummySingleTextualConverter
        extends AbstractSingleTextualConverter<Object, DefaultTextual>
    {

        public DummySingleTextualConverter()
        {
            super();
        }

        @Override
        public DefaultTextual evaluate(
            final Object input)
        {
            return new DefaultTextual("dummy");
        }

    }

}