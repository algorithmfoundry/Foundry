/*
 * File:                StringToIntegerConverterTest.java
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
 * Unit tests for class StringToIntegerConverter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class StringToIntegerConverterTest
{
    /**
     * Creates a new test.
     */
    public StringToIntegerConverterTest() 
    {
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        StringToIntegerConverter instance = new StringToIntegerConverter();
        assertNotNull(instance);
    }

    /**
     * Test of evaluate method, of class StringToIntegerConverter.
     */
    @Test
    public void testEvaluate()
    {
        StringToIntegerConverter instance = new StringToIntegerConverter();
        assertEquals(47, (int) instance.evaluate("47"));
        assertEquals(-74, (int) instance.evaluate("-74"));
        assertEquals(0, (int) instance.evaluate("0"));
        
        boolean exceptionThrown = false;
        try
        {
            instance.evaluate("4.7");
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
     * Test of createReverse method, of class StringToIntegerConverter.
     */
    @Test
    public void testReverse()
    {
        StringToIntegerConverter instance = new StringToIntegerConverter();
        ObjectToStringConverter reverse = instance.reverse();
        assertNotNull(reverse);
        
        String input = "47";
        assertEquals(input, reverse.evaluate(instance.evaluate(input)));
    }
}