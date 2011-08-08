/*
 * File:                StringToDoubleConverterTest.java
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

package gov.sandia.cognition.data.convert.number;

import gov.sandia.cognition.data.convert.ObjectToStringConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class StringToDoubleConverter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class StringToDoubleConverterTest
{
    /**
     * Creates a new test.
     */
    public StringToDoubleConverterTest() 
    {
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        StringToDoubleConverter instance = new StringToDoubleConverter();
        assertNotNull(instance);
    }

    /**
     * Test of evaluate method, of class StringToDoubleConverter.
     */
    @Test
    public void testEvaluate()
    {
        StringToDoubleConverter instance = new StringToDoubleConverter();
        assertEquals(4.7, instance.evaluate("4.7"), 0.0);
        assertEquals(-7.4, instance.evaluate("-7.4"), 0.0);
        assertEquals(0.0, instance.evaluate("0"), 0.0);
        
        boolean exceptionThrown = false;
        try
        {
            instance.evaluate("a");
        }
        catch (NumberFormatException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of reverse method, of class StringToDoubleConverter.
     */
    @Test
    public void testReverse()
    {
        StringToDoubleConverter instance = new StringToDoubleConverter();
        ObjectToStringConverter reverse = instance.reverse();
        assertNotNull(reverse);
        
        String input = "4.7";
        assertEquals(input, reverse.evaluate(instance.evaluate(input)));
    }
}