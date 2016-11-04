/*
 * File:            DefaultIndexerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.collection;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link DefaultIndexer}.
 *
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class DefaultIndexerTest
    extends Object
{

    /**
     * Creates a new test.
     */
    public DefaultIndexerTest()
    {
        super();
    }

    @Test
    public void testConstructors()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertTrue(instance.isEmpty());
        assertTrue(instance.valueSet().isEmpty());
        assertTrue(instance.valueList().isEmpty());
        assertTrue(instance.asMap().isEmpty());


        instance = new DefaultIndexer<String>(123);
        assertTrue(instance.isEmpty());
        assertTrue(instance.valueSet().isEmpty());
        assertTrue(instance.valueList().isEmpty());
        assertTrue(instance.asMap().isEmpty());


        List<String> values = Arrays.asList(
            "a", "c", new String("a"), null, "b");
        instance = new DefaultIndexer<String>(values);
        assertEquals(4, instance.size());
        assertEquals(4, instance.valueSet().size());
        assertEquals(4, instance.valueList().size());
        assertEquals(4, instance.asMap().size());
        assertTrue(instance.valueSet().containsAll(values));
        assertTrue(instance.valueList().containsAll(values));
        assertTrue(instance.asMap().keySet().containsAll(values));
        assertEquals(0, (int) instance.getIndex("a"));
        assertEquals(3, (int) instance.getIndex("b"));
        assertEquals(1, (int) instance.getIndex("c"));
        assertEquals(2, (int) instance.getIndex(null));
    }

    /**
     * Test of clone method, of class DefaultIndexer.
     */
    @Test
    public void testClone()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        DefaultIndexer<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());

        instance.add("a");
        assertEquals(0, (int) instance.getIndex("a"));
        assertNull(clone.getIndex("a"));
        assertTrue(clone.isEmpty());
        assertTrue(clone.valueSet().isEmpty());
        assertTrue(clone.valueList().isEmpty());

        instance.add("b");
        clone = instance.clone();
        assertEquals(2, clone.size());
        assertEquals(instance.valueList(), clone.valueList());
        assertEquals(instance.asMap(), clone.asMap());
        assertEquals(0, (int) clone.getIndex("a"));
        assertEquals(1, (int) clone.getIndex("b"));
    }

    /**
     * Test of add method, of class DefaultIndexer.
     */
    @Test
    public void testAdd()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertTrue(instance.isEmpty());

        assertEquals(0, (int) instance.add("a"));
        assertEquals(1, (int) instance.add("b"));
        assertEquals(2, (int) instance.add("another"));
        assertEquals(1, (int) instance.add(new String("b")));
        assertEquals(3, (int) instance.add(null));
        assertEquals(3, (int) instance.add(null));
        assertEquals(0, (int) instance.add("a"));
        assertEquals(4, (int) instance.add("something else"));
    }

    /**
     * Test of addAll method, of class DefaultIndexer.
     */
    @Test
    public void testAddAll()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertTrue(instance.isEmpty());

        instance.add("something");

        List<String> values = Arrays.asList(
            "a", "c", new String("a"), null, "b");
        instance.addAll(values);

        assertEquals(5, instance.size());
        assertEquals(0, (int) instance.getIndex("something"));
        assertEquals(1, (int) instance.getIndex("a"));
        assertEquals(4, (int) instance.getIndex("b"));
        assertEquals(2, (int) instance.getIndex("c"));
        assertEquals(3, (int) instance.getIndex(null));

        instance.addAll(values);
        assertEquals(0, (int) instance.getIndex("something"));
        assertEquals(1, (int) instance.getIndex("a"));
        assertEquals(4, (int) instance.getIndex("b"));
        assertEquals(2, (int) instance.getIndex("c"));
        assertEquals(3, (int) instance.getIndex(null));
    }

    /**
     * Test of getIndex method, of class DefaultIndexer.
     */
    @Test
    public void testGetIndex()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertNull(instance.getIndex("a"));
        assertNull(instance.getIndex("b"));
        assertNull(instance.getIndex("another"));
        assertNull(instance.getIndex(null));

        instance.add("a");
        assertEquals(0, (int) instance.getIndex("a"));
        assertNull(instance.getIndex("b"));
        assertNull(instance.getIndex("another"));
        assertNull(instance.getIndex(null));

		instance.add("b");
		assertEquals(0, (int) instance.getIndex("a"));
		assertEquals(1, (int) instance.getIndex("b"));
		assertNull(instance.getIndex("another"));
		assertNull(instance.getIndex(null));

		instance.add("another");
		assertEquals(0, (int) instance.getIndex("a"));
		assertEquals(1, (int) instance.getIndex("b"));
		assertEquals(2, (int) instance.getIndex("another"));
		assertNull(instance.getIndex(null));

		instance.add(null);
		assertEquals(0, (int) instance.getIndex("a"));
		assertEquals(1, (int) instance.getIndex("b"));
		assertEquals(2, (int) instance.getIndex("another"));
		assertEquals(3, (int) instance.getIndex(null));

		instance.add("another");
		assertEquals(0, (int) instance.getIndex("a"));
		assertEquals(1, (int) instance.getIndex("b"));
		assertEquals(2, (int) instance.getIndex("another"));
		assertEquals(3, (int) instance.getIndex(null));

        assertEquals(0, (int) instance.getIndex(new String("a")));
        assertEquals(1, (int) instance.getIndex(new String("b")));
        assertEquals(2, (int) instance.getIndex(new String("another")));
        assertNull(instance.getIndex("something else"));
    }

    /**
     * Test of getValue method, of class DefaultIndexer.
     */
    @Test
    public void testGetValue()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();

        instance.add("a");
        assertSame("a", instance.getValue(0));

        instance.add("b");
        assertSame("a", instance.getValue(0));
        assertSame("b", instance.getValue(1));

        instance.add("another");
        assertSame("a", instance.getValue(0));
        assertSame("b", instance.getValue(1));
        assertSame("another", instance.getValue(2));

        instance.add(new String("b"));
        assertSame("a", instance.getValue(0));
        assertSame("b", instance.getValue(1));
        assertSame("another", instance.getValue(2));

        instance.add(null);
        assertSame("a", instance.getValue(0));
        assertSame("b", instance.getValue(1));
        assertSame("another", instance.getValue(2));
        assertSame(null, instance.getValue(3));

        instance.add(null);
        assertSame("a", instance.getValue(0));
        assertSame("b", instance.getValue(1));
        assertSame("another", instance.getValue(2));
        assertSame(null, instance.getValue(3));

        instance.add("something else");
        assertSame("a", instance.getValue(0));
        assertSame("b", instance.getValue(1));
        assertSame("another", instance.getValue(2));
        assertSame(null, instance.getValue(3));
        assertSame("something else", instance.getValue(4));

		Integer[] badValues = { -1, -2, 5, instance.size(), instance.size() + 1, null };
		for (Integer badIndex : badValues)
		{
			boolean exceptionThrown = false;
			try
			{
				instance.getValue(badIndex);
			}
			catch (Exception e)
			{
				exceptionThrown = true;
			}
			finally
			{
				assertTrue(exceptionThrown);
			}
		}
    }
    
    /**
     * Test of hasValue method, of class DefaultIndexer.
     */
    @Test
    public void testHasValue()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertFalse(instance.hasValue("a"));
        assertFalse(instance.hasValue("b"));
        assertFalse(instance.hasValue("another"));
        assertFalse(instance.hasValue(null));

        instance.add("a");
        assertTrue(instance.hasValue("a"));
        assertFalse(instance.hasValue("b"));
        assertFalse(instance.hasValue("another"));
        assertFalse(instance.hasValue(null));

		instance.add("b");
		assertTrue(instance.hasValue("a"));
		assertTrue(instance.hasValue("b"));
		assertFalse(instance.hasValue("another"));
		assertFalse(instance.hasValue(null));

		instance.add("another");
		assertTrue(instance.hasValue("a"));
		assertTrue(instance.hasValue("b"));
		assertTrue(instance.hasValue("another"));
		assertFalse(instance.hasValue(null));

		instance.add(null);
		assertTrue(instance.hasValue("a"));
		assertTrue(instance.hasValue("b"));
		assertTrue(instance.hasValue("another"));
		assertTrue(instance.hasValue(null));

		instance.add("another");
		assertTrue(instance.hasValue("a"));
		assertTrue(instance.hasValue("b"));
		assertTrue(instance.hasValue("another"));
		assertTrue(instance.hasValue(null));

        assertTrue(instance.hasValue(new String("a")));
        assertTrue(instance.hasValue(new String("b")));
        assertTrue(instance.hasValue(new String("another")));
        assertFalse(instance.hasValue("something else"));
    }

    /**
     * Test of hasIndex method, of class DefaultIndexer.
     */
    @Test
    public void testHasIndex()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertFalse(instance.hasIndex(0));
        assertFalse(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));
        assertFalse(instance.hasIndex(3));
        assertFalse(instance.hasIndex(4));
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(-2));
        assertFalse(instance.hasIndex(null));

        instance.add("a");
        assertTrue(instance.hasIndex(0));
        assertFalse(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));
        assertFalse(instance.hasIndex(3));
        assertFalse(instance.hasIndex(4));
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(-2));
        assertFalse(instance.hasIndex(null));

		instance.add("b");
        assertTrue(instance.hasIndex(0));
        assertTrue(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));
        assertFalse(instance.hasIndex(3));
        assertFalse(instance.hasIndex(4));
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(-2));
        assertFalse(instance.hasIndex(null));

		instance.add("another");
        assertTrue(instance.hasIndex(0));
        assertTrue(instance.hasIndex(1));
        assertTrue(instance.hasIndex(2));
        assertFalse(instance.hasIndex(3));
        assertFalse(instance.hasIndex(4));
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(-2));
        assertFalse(instance.hasIndex(null));

		instance.add(null);
        assertTrue(instance.hasIndex(0));
        assertTrue(instance.hasIndex(1));
        assertTrue(instance.hasIndex(2));
        assertTrue(instance.hasIndex(3));
        assertFalse(instance.hasIndex(4));
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(-2));
        assertFalse(instance.hasIndex(null));

		instance.add("another");
        assertTrue(instance.hasIndex(0));
        assertTrue(instance.hasIndex(1));
        assertTrue(instance.hasIndex(2));
        assertTrue(instance.hasIndex(3));
        assertFalse(instance.hasIndex(4));
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(-2));
        assertFalse(instance.hasIndex(null));
    }


    /**
     * Test of size method, of class DefaultIndexer.
     */
    @Test
    public void testSize()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertEquals(0, instance.size());

        instance.add("a");
        assertEquals(1, instance.size());

        instance.add("b");
        assertEquals(2, instance.size());

        instance.add("another");
        assertEquals(3, instance.size());

        instance.add(new String("b"));
        assertEquals(3, instance.size());

        instance.add(null);
        assertEquals(4, instance.size());

        instance.add(null);
        assertEquals(4, instance.size());

        instance.add("something else");
        assertEquals(5, instance.size());
    }

    /**
     * Test of valueSet method, of class DefaultIndexer.
     */
    @Test
    public void testValueSet()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertTrue(instance.valueSet().isEmpty());

        instance.add("something");
        assertEquals(1, instance.valueSet().size());
        assertTrue(instance.valueSet().contains("something"));

        List<String> values = Arrays.asList(
            "a", "c", new String("a"), null, "b");
        instance.addAll(values);
        assertEquals(5, instance.valueSet().size());
        assertTrue(instance.valueSet().contains("something"));
        assertTrue(instance.valueSet().containsAll(values));
    }

    /**
     * Test of valueList method, of class DefaultIndexer.
     */
    @Test
    public void testValueList()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertTrue(instance.valueList().isEmpty());

        instance.add("something");
        assertEquals(1, instance.valueSet().size());
        assertTrue(instance.valueList().contains("something"));

        List<String> values = Arrays.asList(
            "a", "c", new String("a"), null, "b");
        instance.addAll(values);
        assertEquals(5, instance.valueList().size());
        assertTrue(instance.valueList().contains("something"));
        assertTrue(instance.valueList().containsAll(values));
    }

    /**
     * Test of asMap method, of class DefaultIndexer.
     */
    @Test
    public void testAsMap()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<String>();
        assertTrue(instance.asMap().isEmpty());

        instance.add("something");
        assertEquals(1, instance.asMap().size());
        assertTrue(instance.asMap().containsKey("something"));
        assertEquals(0, (int) instance.asMap().get("something"));

        List<String> values = Arrays.asList(
            "a", "c", new String("a"), null, "b");
        instance.addAll(values);
        assertEquals(5, instance.asMap().size());
        assertTrue(instance.asMap().containsKey("something"));
        assertTrue(instance.asMap().keySet().containsAll(values));
        assertEquals(0, (int) instance.asMap().get("something"));
        assertEquals(1, (int) instance.asMap().get("a"));
        assertEquals(2, (int) instance.asMap().get("c"));
        assertEquals(3, (int) instance.asMap().get(null));
        assertEquals(4, (int) instance.asMap().get("b"));
    }

    /**
     * Test of the clear method
     */
    @Test
    public void testClear()
    {
        DefaultIndexer<String> instance = new DefaultIndexer<>(5);
        instance.add("bob");
        instance.add("frank");
        assertEquals(2, instance.size());
        assertTrue(instance.hasValue("bob"));
        assertTrue(instance.hasValue("frank"));
        instance.clear();
        assertEquals(0, instance.size());
        assertFalse(instance.hasValue("bob"));
        assertFalse(instance.hasValue("frank"));
        instance.add("billy");
        instance.add("bob");
        assertEquals(2, instance.size());
        assertTrue(instance.hasValue("bob"));
        assertTrue(instance.hasValue("billy"));
        assertFalse(instance.hasValue("frank"));
    }
}
