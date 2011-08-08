/*
 * File:                ObjectToStringTextualConverterTest.java
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class ObjectToStringTextualConverter.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class ObjectToStringTextualConverterTest
{
    /**
     * Creates a new test.
     */
    public ObjectToStringTextualConverterTest()
    {
    }

    /**
     * Test of constructors of class ObjectToStringTextualConverter.
     */
    public void testConstructors()
    {
        ObjectToStringTextualConverter instance = new ObjectToStringTextualConverter();
    }
    
    /**
     * Test of evaluate method, of class ObjectToStringTextualConverter.
     */
    @Test
    public void testEvaluate()
    {
        ObjectToStringTextualConverter instance = new ObjectToStringTextualConverter();

        Object[] inputs = { new Object(), "a", 1, 2.3, null };
        for (Object input : inputs)
        {
            assertEquals("" + input, instance.evaluate(input).getText());
        }
    }

}