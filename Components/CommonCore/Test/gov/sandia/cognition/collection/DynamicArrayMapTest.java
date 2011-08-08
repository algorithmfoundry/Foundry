/*
 * File:                DynamicArrayMapTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 2, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DynamicArrayMap
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2006-07-19",
    changesNeeded=false,
    comments="Looks fine."
)
public class DynamicArrayMapTest
    extends TestCase
{
    public DynamicArrayMapTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        Object o1 = new Object();
        Object o2 = new Object();
        instance.put(4, o1);
        instance.put(7, o2);

        DynamicArrayMap<Object> copy = new DynamicArrayMap<Object>( instance );
        assertNotNull( copy );
        assertNotSame( instance, copy );
        assertEquals( instance.size(), copy.size() );
        assertSame( instance.get(4), copy.get(4) );
        assertSame( instance.get(7), copy.get(7) );

        try
        {
            instance = new DynamicArrayMap<Object>(0);
            fail( "Initial capacity must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clear method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testClear()
    {
        System.out.println("clear");
        
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        Object o1 = new Object();
        Object o2 = new Object();
        instance.put(4, o1);
        instance.put(7, o2);
        
        assertEquals(2, instance.size());
        instance.clear();
        assertEquals(0, instance.size());
        assertNull(instance.get(4));
        assertNull(instance.get(7));
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testClone()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        Object o1 = new Object();
        Object o2 = new Object();
        instance.put(4, o1);
        instance.put(7, o2);
        
        DynamicArrayMap<Object> clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertEquals(2, clone.size());
        assertSame(o1, clone.get(4));
        assertSame(o2, clone.get(7));
        
        instance.clear();
        assertEquals(2, clone.size());
    }

    /**
     * Test of containsKey method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testContainsKey()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        
        assertFalse(instance.containsKey(null));
        assertFalse(instance.containsKey(4));
        assertFalse(instance.containsKey(7));
        
        Object o1 = new Object();
        Object o2 = new Object();
        instance.put(4, o1);
        instance.put(7, o2);
        
        assertTrue(instance.containsKey(4));
        assertTrue(instance.containsKey(7));
        
        instance.remove(4);
        instance.put(7, null);
        
        assertFalse(instance.containsKey(4));
        assertFalse(instance.containsKey(7));

        assertFalse( instance.containsKey(4.0) );
    }

    /**
     * Test of containsValue method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testContainsValue()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        
        assertFalse(instance.containsValue(null));
        
        Object o1 = new Object();
        Object o2 = new Object();
        
        assertFalse(instance.containsValue(o1));
        assertFalse(instance.containsValue(o2));
        
        instance.put(4, o1);
        instance.put(7, o2);
        
        assertTrue(instance.containsValue(o1));
        assertTrue(instance.containsValue(o2));
        
        instance.remove(4);
        instance.put(7, null);
        
        assertFalse(instance.containsValue(o1));
        assertFalse(instance.containsValue(o2));
    }

    /**
     * Test of entrySet method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testEntrySet()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        instance.put(47, "a");
        instance.put(4, "b");
        instance.put(7, "c");
        
        Set<Map.Entry<Integer, Object>> keys = instance.entrySet();
        assertEquals(3, keys.size());
        
        Iterator<Map.Entry<Integer, Object>> it = keys.iterator();
        Map.Entry<Integer, Object> entry = null;
        
        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals(4, (int) entry.getKey());
        assertEquals("b", entry.getValue());
        
        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals(7, (int) entry.getKey());
        assertEquals("c", entry.getValue());
        
        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals(47, (int) entry.getKey());
        assertEquals("a", entry.getValue());
        assertFalse(it.hasNext());
    }

    /**
     * Test of get method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testGet()
    {
        DynamicArrayMap<String> instance = new DynamicArrayMap<String>();
        assertNull(instance.get(4));
        
        instance.put(4, "a");
        assertEquals("a", instance.get(4));
        assertEquals("a", instance.get( new Integer(4)) );
        assertEquals( null, instance.get( new Double(4.0) ) );

        instance.put(7, "b");
        assertEquals("b", instance.get(7));
        
        assertNull(instance.get(47));
        
        instance.put(4, null);
        assertNull(instance.get(4));
        
        boolean exceptionThrown = false;
        try
        {
            instance.get(null);
        }
        catch ( NullPointerException npe )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of isEmpty method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testIsEmpty()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        assertTrue(instance.isEmpty());
        
        instance.put(3, null);
        assertTrue(instance.isEmpty());
        
        instance.put(4, new Object());
        assertFalse(instance.isEmpty());
        
        instance.remove(4);
        assertTrue(instance.isEmpty());
        
        instance.put(7, new Object());
        assertFalse(instance.isEmpty());
        
        instance.put(7, null);
        assertTrue(instance.isEmpty());
        
        instance.put(47, new Object());
        assertFalse(instance.isEmpty());
        
        instance.clear();
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of keySet method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testKeySet()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        instance.put(47, "a");
        instance.put(4, "b");
        instance.put(7, "c");
        
        Set<Integer> keys = instance.keySet();
        assertEquals(3, keys.size());
        
        Iterator<Integer> it = keys.iterator();
        assertTrue(it.hasNext());
        assertEquals(4, (int) it.next());
        assertTrue(it.hasNext());
        assertEquals(7, (int) it.next());
        assertTrue(it.hasNext());
        assertEquals(47, (int) it.next());
        assertFalse(it.hasNext());
    }

    /**
     * Test of put method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testPut()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();

        instance.put(4, "a");
        instance.put(7, "7");
        assertEquals(2, instance.size());
        assertEquals("a", instance.get(4));
        assertEquals("7", instance.get(7));
        
        instance.put(4, "b");
        assertEquals(2, instance.size());
        assertEquals("b", instance.get(4));
        
        instance.put(4, null);
        assertEquals(1, instance.size());
        assertNull(instance.get(4));

        assertNull( instance.put( Integer.MAX_VALUE, null ) );

        boolean exceptionThrown = false;
        try
        {
            instance.put(null, "bad");
        }
        catch ( NullPointerException npe )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of remove method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testRemove()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        
        Object o1 = new Object();
        Object o2 = new Object();
        
        assertNull( instance.remove(4) );
        assertNull( instance.remove(new Integer(4)) );
        assertNull( instance.remove(new Double(4.0)) );

        instance.put(4, o1);
        
        assertEquals(1, instance.size());
        assertEquals(o1, instance.get(4));
        assertTrue(instance.containsKey(4));
        assertTrue(instance.containsValue(o1));
        
        instance.remove(4);
        
        assertEquals(0, instance.size());
        assertNull(instance.get(4));
        assertFalse(instance.containsKey(4));
        assertFalse(instance.containsValue(o1));
        
        boolean exceptionThrown = false;
        try
        {
            instance.remove(null);
        }
        catch ( NullPointerException npe )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of size method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testSize()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        assertEquals(0, instance.size());
        
        instance.put(4, null);
        assertEquals(0, instance.size());
        
        instance.put(4, new Object());
        assertEquals(1, instance.size());
        
        instance.put(4, new Object());
        assertEquals(1, instance.size());
        
        instance.put(7, new Object());
        assertEquals(2, instance.size());
        
        instance.put(4, null);
        assertEquals(1, instance.size());
        
        instance.put(47, new Object());
        assertEquals(2, instance.size());
        
        instance.remove(7);
        assertEquals(1, instance.size());
        
        instance.clear();
        assertEquals(0, instance.size());
    }

    /**
     * Test of values method, of class gov.sandia.isrc.util.DynamicArrayMap.
     */
    public void testValues()
    {
        DynamicArrayMap<Object> instance = new DynamicArrayMap<Object>();
        instance.put(47, "47");
        instance.put(4, "4");
        instance.put(7, "7");
        
        Collection<Object> values = instance.values();
        assertEquals(3, values.size());
        
        Iterator<Object> it = values.iterator();
        assertTrue(it.hasNext());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertEquals("7", it.next());
        assertTrue(it.hasNext());
        assertEquals("47", it.next());
        assertFalse(it.hasNext());

        assertEquals( values, instance.values() );
        assertNotSame( values, instance.values() );

        instance.put( 10, "10" );
        assertEquals( values, instance.values() );
        assertEquals( 4, values.size() );

        DynamicArrayMap<Object> d = new DynamicArrayMap<Object>( instance );
        d.put( 15, "15" );
        assertFalse( values.equals(d.values()) );

        assertFalse( values.equals(null) );
        assertFalse( values.equals(d) );
        assertFalse( values.equals(instance) );


    }

}
