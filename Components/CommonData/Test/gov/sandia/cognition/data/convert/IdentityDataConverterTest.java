/*
 * File:                IdentityDataConverterTest.java
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Justin Basilico
 */
public class IdentityDataConverterTest
{

    public IdentityDataConverterTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }
    
    @Test
    public void testConstructors()
    {
        IdentityDataConverter<Object> instance = 
            new IdentityDataConverter<Object>();
        assertNotNull(instance);
    }

    /**
     * Test of evaluate method, of class IdentityDataConverter.
     */
    @Test
    public void testEvaluate()
    {
        IdentityDataConverter<Object> instance = 
            new IdentityDataConverter<Object>();
        
        Object input = new Object();
        assertSame(input, instance.evaluate(input));
        
        assertEquals("a", instance.evaluate("a"));
        assertEquals("b", instance.evaluate("b"));
        assertEquals(null, instance.evaluate(null));
    }

    /**
     * Test of reverse method, of class IdentityDataConverter.
     */
    @Test
    public void testReverse()
    {
        IdentityDataConverter<Object> instance = 
            new IdentityDataConverter<Object>();
        assertSame(instance, instance.reverse());
    }

}