/*
 * File:                SingleToMultiTextualConverterAdapterTest.java
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

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.text.Textual;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class SingleToMultiTextualConverterAdapter.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class SingleToMultiTextualConverterAdapterTest
{
    /**
     * Creates a new test.
     */
    public SingleToMultiTextualConverterAdapterTest()
    {
    }

    /**
     * Test of constructors of class SingleToMultiTextualConverterAdapter.
     */
    @Test
    public void testConstructors()
    {
        SingleTextualConverter<Object, ?> converter = null;
        SingleToMultiTextualConverterAdapter<Object, Textual> instance =
            new SingleToMultiTextualConverterAdapter<Object, Textual>();
        assertEquals(converter, instance.getConverter());

        converter = new ObjectToStringTextualConverter();
        instance = new SingleToMultiTextualConverterAdapter<Object, Textual>(converter);
        assertEquals(converter, instance.getConverter());
    }

    /**
     * Test of evaluate method, of class SingleToMultiTextualConverterAdapter.
     */
    @Test
    public void testEvaluate()
    {
        SingleToMultiTextualConverterAdapter<Object, Textual> instance =
            new SingleToMultiTextualConverterAdapter<Object, Textual>(new ObjectToStringTextualConverter());


        Object[] inputs = { new Object(), "a", 1, 2.3, null };
        for (Object input : inputs)
        {
            Iterable<Textual> result = instance.evaluate(input);
            assertEquals(1, CollectionUtil.size(result));
            assertEquals("" + input, CollectionUtil.getFirst(result).getText());
        }
    }

    /**
     * Test of getConverter method, of class SingleToMultiTextualConverterAdapter.
     */
    @Test
    public void testGetConverter()
    {
        this.testSetConverter();
    }

    /**
     * Test of setConverter method, of class SingleToMultiTextualConverterAdapter.
     */
    @Test
    public void testSetConverter()
    {
        SingleTextualConverter<Object, ?> converter = null;
        SingleToMultiTextualConverterAdapter<Object, Textual> instance =
            new SingleToMultiTextualConverterAdapter<Object, Textual>();
        assertEquals(converter, instance.getConverter());

        converter = new ObjectToStringTextualConverter();
        instance.setConverter(converter);
        assertEquals(converter, instance.getConverter());

        converter = new ObjectToStringTextualConverter();
        instance.setConverter(converter);
        assertEquals(converter, instance.getConverter());

        converter = null;
        instance.setConverter(converter);
        assertEquals(converter, instance.getConverter());

        converter = new ObjectToStringTextualConverter();
        instance.setConverter(converter);
        assertEquals(converter, instance.getConverter());
    }

}