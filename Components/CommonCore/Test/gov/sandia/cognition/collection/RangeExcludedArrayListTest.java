/*
 * File:                RangeExcludedArrayListTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: RangeExcludedArrayList
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class RangeExcludedArrayListTest
    extends TestCase
{

    public RangeExcludedArrayListTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");


        RangeExcludedArrayList<String> instance = null;
        int count = list.size();
        for (int i = 0; i < count; i++)
        {
            for (int j = i; j < i; j++)
            {
                instance = new RangeExcludedArrayList<String>(list, i, j);
            }
        }

        boolean exceptionThrown = false;
        try
        {
            instance = new RangeExcludedArrayList<String>(list, -1, 1);
        }
        catch (IndexOutOfBoundsException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            instance = new RangeExcludedArrayList<String>(list, 1, count);
        }
        catch (IndexOutOfBoundsException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            instance = new RangeExcludedArrayList<String>(list, 2, 1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of get method, of class gov.sandia.cognition.collections.RangeExcludedArrayList.
     */
    public void testGet()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");

        RangeExcludedArrayList<String> instance =
            new RangeExcludedArrayList<String>(list, 4, 6);

        assertEquals(5, instance.size());
        assertEquals("0", instance.get(0));
        assertEquals("1", instance.get(1));
        assertEquals("2", instance.get(2));
        assertEquals("3", instance.get(3));
        assertEquals("7", instance.get(4));
    }

    /**
     * Test of size method, of class gov.sandia.cognition.collections.RangeExcludedArrayList.
     */
    public void testSize()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");

        assertEquals(7, new RangeExcludedArrayList<String>(list, 0, 0).size());
        assertEquals(7, new RangeExcludedArrayList<String>(list, 1, 1).size());
        assertEquals(7, new RangeExcludedArrayList<String>(list, 4, 4).size());
        assertEquals(7, new RangeExcludedArrayList<String>(list, 7, 7).size());
        assertEquals(5, new RangeExcludedArrayList<String>(list, 4, 6).size());
        assertEquals(1, new RangeExcludedArrayList<String>(list, 1, 7).size());
        assertEquals(1, new RangeExcludedArrayList<String>(list, 0, 6).size());
        assertEquals(0, new RangeExcludedArrayList<String>(list, 0, 7).size());
    }

    public void testForAll()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");

        RangeExcludedArrayList<String> instance =
            new RangeExcludedArrayList<String>(list, 4, 6);

        for (String s : instance)
        {
            if (s.equals("4") || s.equals("5") || s.equals("6"))
            {
                fail("Iterator is accessing excluded values!");
            }
        }

    }
    
    public void testSubCollections()
    {

        ArrayList<String> list = new ArrayList<String>();

        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");

        RangeExcludedArrayList<String> instance =
            new RangeExcludedArrayList<String>(list, 4, 6);
        List<? extends Collection<String>> result = instance.subCollections();
        assertEquals(2, result.size());
        assertEquals(list.subList(0, 4), result.get(0));
        assertEquals(list.subList(7, 8), result.get(1));

        instance = new RangeExcludedArrayList<String>(list, 0, 3);
        result = instance.subCollections();
        assertEquals(1, result.size());
        assertEquals(list.subList(4, 8), result.get(0));

        instance = new RangeExcludedArrayList<String>(list, 3, 7);
        result = instance.subCollections();
        assertEquals(1, result.size());
        assertEquals(list.subList(0, 3), result.get(0));

        instance = new RangeExcludedArrayList<String>(list, 3, 3);
        result = instance.subCollections();
        assertEquals(2, result.size());
        assertEquals(list.subList(0, 3), result.get(0));
        assertEquals(list.subList(4, 8), result.get(1));
    }
}
