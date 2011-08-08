/*
 * File:                MultiIteratorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.collection;

import java.util.NoSuchElementException;
import junit.framework.*;
import java.util.ArrayList;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MultiIterator
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class MultiIteratorTest
    extends TestCase
{
    public MultiIteratorTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MultiIteratorTest.class);
        
        return suite;
    }

    /**
     * Test of hasNext method, of class gov.sandia.isrc.util.MultiIterator.
     */
    public void testHasNext()
    {
        String a = "a";
        String b = "b";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        ArrayList<ArrayList<String>> listsList = 
            new ArrayList<ArrayList<String>>();
        listsList.add(list1);
        listsList.add(list2);
        
        MultiIterator<String> it = new MultiIterator<String>(listsList);
        
        assertTrue(it.hasNext());
        it.next();
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
        
        // Try it with an empty list.
        it = new MultiIterator<String>(new ArrayList<ArrayList<String>>());
        assertFalse(it.hasNext());
    }

    /**
     * Test of next method, of class gov.sandia.isrc.util.MultiIterator.
     */
    public void testNext()
    {
        String a = "a";
        String b = "b";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        ArrayList<ArrayList<String>> listsList = 
            new ArrayList<ArrayList<String>>();
        listsList.add(list1);
        listsList.add(list2);
        
        MultiIterator<String> it = new MultiIterator<String>(listsList);
        
        assertEquals(a, it.next());
        assertEquals(b, it.next());
        
        boolean exceptionThrown = false;
        try
        {
            String bad = it.next();
        }
        catch ( NoSuchElementException nsee )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // Try it with an empty list.
        it = new MultiIterator<String>(new ArrayList<ArrayList<String>>());
        exceptionThrown = false;
        try
        {
            String bad = it.next();
        }
        catch ( NoSuchElementException nsee )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of remove method, of class gov.sandia.isrc.util.MultiIterator.
     */
    public void testRemove()
    {
        String a = "a";
        String b = "b";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        ArrayList<ArrayList<String>> listslist = 
            new ArrayList<ArrayList<String>>();
        listslist.add(list1);
        listslist.add(list2);
        
        MultiIterator<String> it = new MultiIterator<String>(listslist);
        
        
        it.next();
        
        boolean exceptionThrown = false;
        try
        {
            it.remove();
        }
        catch ( UnsupportedOperationException uoe )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}
