/*
 * File:                ObjectToStringConverterTest.java
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

package gov.sandia.cognition.data.convert;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for ObjectToStringConverter
 * @author  Justin Basilico
 * @since   2.1
 */
public class ObjectToStringConverterTest
{
    /**
     * Creates a new test.
     */
    public ObjectToStringConverterTest() 
    {
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        ObjectToStringConverter instance = new ObjectToStringConverter();
        assertNotNull(instance);
    }

    /**
     * Test of evaluate method, of class ObjectToStringConverter.
     */
    @Test
    public void testEvaluate()
    {
        ObjectToStringConverter instance = new ObjectToStringConverter();
        Object input = new Object();
        assertEquals(input.toString(), instance.evaluate(input));
        assertEquals("a", instance.evaluate("a"));
        assertEquals("1", instance.evaluate(1));
        
        assertEquals(null, instance.evaluate(null));
    }
}