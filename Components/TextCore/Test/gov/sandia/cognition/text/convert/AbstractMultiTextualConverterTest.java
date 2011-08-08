/*
 * File:                AbstractMultiTextualConverterTest.java
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

import java.util.Collections;
import gov.sandia.cognition.collection.CollectionUtil;
import java.util.Arrays;
import gov.sandia.cognition.text.DefaultTextual;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractMultiTextualConverter.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class AbstractMultiTextualConverterTest
{
    /**
     * Creates a new test.
     */
    public AbstractMultiTextualConverterTest()
    {
    }

    /**
     * Test of constructors of class AbstractMultiTextualConverter.
     */
    @Test
    public void testConstructors()
    {
         AbstractMultiTextualConverter<?, ?> instance = new DummyMultiTextualConverter();
    }

    /**
     * Test of convert method, of class AbstractMultiTextualConverter.
     */
    @Test
    public void testConvert()
    {
        AbstractMultiTextualConverter<Object, ?> instance =
            new DummyMultiTextualConverter();
        assertEquals(3, CollectionUtil.size(instance.convert(null)));
        assertEquals(3, CollectionUtil.size(instance.convert(new Object())));
    }

    /**
     * Test of convertAll method, of class AbstractMultiTextualConverter.
     */
    @Test
    public void testConvertAll()
    {
        AbstractMultiTextualConverter<Object, ?> instance =
            new DummyMultiTextualConverter();
        assertEquals(0, CollectionUtil.size(instance.convertAll(Collections.emptyList())));
        assertEquals(3, CollectionUtil.size(instance.convertAll(Collections.singletonList("a"))));
        assertEquals(6, CollectionUtil.size(instance.convertAll(Arrays.asList("a", "b"))));
    }

    private class DummyMultiTextualConverter
        extends AbstractMultiTextualConverter<Object, DefaultTextual>
    {

        public DummyMultiTextualConverter()
        {
            super();
        }

        @Override
        public Iterable<DefaultTextual> evaluate(
            final Object input)
        {
            return Arrays.asList(new DefaultTextual("1"), new DefaultTextual("2"), new DefaultTextual("3"));
        }

    }

}