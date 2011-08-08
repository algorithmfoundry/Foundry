/*
 * File:                ValueMapperTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 03, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.TestCase;

/**
 * Unit tests for class ValueMapper.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class ValueMapperTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ValueMapperTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class ValueMapper.
     */
    public void testConstructors()
    {
        ValueMapper<Integer, String> instance = new ValueMapper<Integer, String>();
        assertNotNull(instance.getValueMap());
        assertTrue(instance.getValueMap().isEmpty());
        assertTrue(instance.getValueMap() instanceof HashMap);
        
        Map<Integer, String> valueMap = new TreeMap<Integer, String>();
        instance = new ValueMapper<Integer, String>(valueMap);
        assertSame(valueMap, instance.getValueMap());
    }

    /**
     * Test of evaluate method, of class ValueMapper.
     */
    public void testEvaluate()
    {
        
        ValueMapper<Integer, String> instance = new ValueMapper<Integer, String>();
        assertNull(instance.evaluate(1));
        assertNull(instance.evaluate(2));
        assertNull(instance.evaluate(3));
        assertNull(instance.evaluate(4));
        
        instance.getValueMap().put(1, "one");
        instance.getValueMap().put(2, "two");
        instance.getValueMap().put(3, "three");
        
        assertEquals("one", instance.evaluate(1));
        assertEquals("two", instance.evaluate(2));
        assertEquals("three", instance.evaluate(3));
        assertEquals(null, instance.evaluate(-1));
    }

    /**
     * Test of getValueMap method, of class ValueMapper.
     */
    public void testGetValueMap()
    {
        this.testSetValueMap();
    }

    /**
     * Test of setValueMap method, of class ValueMapper.
     */
    public void testSetValueMap()
    {
        ValueMapper<Integer, String> instance = new ValueMapper<Integer, String>();
        assertNotNull(instance.getValueMap());
        assertTrue(instance.getValueMap().isEmpty());
        assertTrue(instance.getValueMap() instanceof HashMap);
        
        
        Map<Integer, String> valueMap = new TreeMap<Integer, String>();
        instance.setValueMap(valueMap);
        assertSame(valueMap, instance.getValueMap());
        
        valueMap = null;
        instance.setValueMap(valueMap);
        assertSame(valueMap, instance.getValueMap());
    }

}
