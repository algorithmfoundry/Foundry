/*
 * File:                DefaultKeyValuePairTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 28, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Date;
import junit.framework.TestCase;

/**
 * Unit tests for class DefaultKeyValuePair.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultKeyValuePairTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultKeyValuePairTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class DefaultKeyValuePair.
     */
    public void testConstructors()
    {
        String key = null;
        Date value = null;
        DefaultKeyValuePair<String, Date> instance =
            new DefaultKeyValuePair<String, Date>();
        assertSame(key, instance.getKey());
        assertSame(value, instance.getValue());

        key = "abc";
        value = new Date();
        instance = new DefaultKeyValuePair<String, Date>(key, value);
        assertSame(key, instance.getKey());
        assertSame(value, instance.getValue());

        instance = new DefaultKeyValuePair<String, Date>(instance);
        assertSame(key, instance.getKey());
        assertSame(value, instance.getValue());
    }

    /**
     * Test of toString method, of class DefaultKeyValuePair.
     */
    public void testToString()
    {
        DefaultKeyValuePair<String, Integer> instance =
            new DefaultKeyValuePair<String, Integer>();
        assertNotNull(instance.toString());

        instance.setKey("something");
        instance.setValue(3);

        assertEquals("(key: something, value: 3)", instance.toString());
    }

    /**
     * Test of getFirst method, of class DefaultKeyValuePair.
     */
    public void testGetFirst()
    {
        String key = null;
        DefaultKeyValuePair<String, Date> instance =
            new DefaultKeyValuePair<String, Date>();
        assertSame(key, instance.getFirst());

        key = "abc";
        instance.setKey(key);
        assertSame(key, instance.getFirst());

        key = null;
        instance.setKey(key);
        assertSame(key, instance.getFirst());

        key = "other";
        instance.setKey(key);
        assertSame(key, instance.getFirst());
    }

    /**
     * Test of getSecond method, of class DefaultKeyValuePair.
     */
    public void testGetSecond()
    {
        Date value = null;
        DefaultKeyValuePair<String, Date> instance =
            new DefaultKeyValuePair<String, Date>();
        assertSame(value, instance.getSecond());

        value = new Date();
        instance.setValue(value);
        assertSame(value, instance.getSecond());

        value = null;
        instance.setValue(value);
        assertSame(value, instance.getSecond());

        value = new Date(54321);
        instance.setValue(value);
        assertSame(value, instance.getSecond());
    }

    /**
     * Test of getKey method, of class DefaultKeyValuePair.
     */
    public void testGetKey()
    {
        this.testSetKey();
    }

    /**
     * Test of setKey method, of class DefaultKeyValuePair.
     */
    public void testSetKey()
    {
        String key = null;
        DefaultKeyValuePair<String, Date> instance =
            new DefaultKeyValuePair<String, Date>();
        assertSame(key, instance.getKey());

        key = "abc";
        instance.setKey(key);
        assertSame(key, instance.getKey());

        key = null;
        instance.setKey(key);
        assertSame(key, instance.getKey());

        key = "other";
        instance.setKey(key);
        assertSame(key, instance.getKey());
    }

    /**
     * Test of getValue method, of class DefaultKeyValuePair.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class DefaultKeyValuePair.
     */
    public void testSetValue()
    {
        Date value = null;
        DefaultKeyValuePair<String, Date> instance =
            new DefaultKeyValuePair<String, Date>();
        assertSame(value, instance.getValue());

        value = new Date();
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = null;
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = new Date(54321);
        instance.setValue(value);
        assertSame(value, instance.getValue());
    }

    /**
     * Test of create method, of class DefaultKeyValuePair.
     */
    public void testCreate()
    {
        String key = null;
        Date value = null;

        DefaultKeyValuePair<String, Date> instance = DefaultKeyValuePair.create();
        assertSame(key, instance.getKey());
        assertSame(value, instance.getValue());

        key = "abc";
        value = new Date();
        instance = DefaultKeyValuePair.create(key, value);
        assertSame(key, instance.getKey());
        assertSame(value, instance.getValue());
    }

}
