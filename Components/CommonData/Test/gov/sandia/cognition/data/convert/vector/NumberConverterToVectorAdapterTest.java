/*
 * File:                NumberConverterToVectorAdapterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert.vector;

import gov.sandia.cognition.data.convert.DataConverter;
import gov.sandia.cognition.data.convert.number.DefaultBooleanToNumberConverter;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class NumberConverterToVectorAdapter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class NumberConverterToVectorAdapterTest
{

    /**
     * Creates a new test.
     */
    public NumberConverterToVectorAdapterTest()
    {
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        DataConverter<? super Boolean, ? extends Number> converter = null;
        NumberConverterToVectorAdapter<Boolean> instance =
            new NumberConverterToVectorAdapter<Boolean>();
        assertSame(converter, instance.getConverter());

        converter = new DefaultBooleanToNumberConverter();
        instance = new NumberConverterToVectorAdapter<Boolean>(converter);
        assertSame(converter, instance.getConverter());
    }

    /**
     * Test of encode method, of class NumberConverterToVectorAdapter.
     */
    @Test
    public void testEncode()
    {
        NumberConverterToVectorAdapter<Boolean> instance =
            new NumberConverterToVectorAdapter<Boolean>(
                new DefaultBooleanToNumberConverter());

        Vector2 vector = new Vector2();
        instance.encode(true, vector, 1);
        assertEquals(new Vector2(0.0, 1.0), vector);
        
        instance.encode(false, vector, 0);
        assertEquals(new Vector2(-1.0, 1.0), vector);
    }

    /**
     * Test of getOutputDimensionality method, of class NumberConverterToVectorAdapter.
     */
    @Test
    public void testGetOutputDimensionality()
    {
        NumberConverterToVectorAdapter<Boolean> instance =
            new NumberConverterToVectorAdapter<Boolean>();
        assertEquals(1, instance.getOutputDimensionality());
    }

    /**
     * Test of getConverter method, of class NumberConverterToVectorAdapter.
     */
    @Test
    public void testGetConverter()
    {
        this.testSetConverter();
    }

    /**
     * Test of setConverter method, of class NumberConverterToVectorAdapter.
     */
    @Test
    public void testSetConverter()
    {
        DataConverter<? super Boolean, ? extends Number> converter = null;
        NumberConverterToVectorAdapter<Boolean> instance =
            new NumberConverterToVectorAdapter<Boolean>();
        assertSame(converter, instance.getConverter());

        converter = new DefaultBooleanToNumberConverter();
        instance.setConverter(converter);
        assertSame(converter, instance.getConverter());

        converter = null;
        instance.setConverter(converter);
        assertSame(converter, instance.getConverter());
    }

}