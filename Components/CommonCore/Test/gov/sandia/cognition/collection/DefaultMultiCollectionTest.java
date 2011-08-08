/*
 * File:                DefaultMultiCollectionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DefaultMultiCollection
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2006-07-18",
    changesNeeded=false,
    comments="Looks fine"
)
public class DefaultMultiCollectionTest
    extends TestCase
{
    public DefaultMultiCollectionTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        String a = "a";
        String b = "b";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        DefaultMultiCollection<String> instance = null;
        
        instance = new DefaultMultiCollection<String>(list1, list2);
        
        assertEquals(2, instance.size());
        assertTrue(instance.contains(a));
        assertTrue(instance.contains(b));
        
        ArrayList<ArrayList<String>> listsList = 
            new ArrayList<ArrayList<String>>();
        listsList.add(list1);
        listsList.add(list2);
        
        instance = new DefaultMultiCollection<String>(listsList);
        
        assertEquals(2, instance.size());
        assertTrue(instance.contains(a));
        assertTrue(instance.contains(b));
    }

    /**
     * Test of contains method, of class gov.sandia.isrc.util.DefaultMultiCollection.
     */
    public void testContains()
    {
        String a = "a";
        String b = "b";
        String c = "c";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        DefaultMultiCollection<String> instance =
            new DefaultMultiCollection<String>(list1, list2);
        
        assertTrue(instance.contains(a));
        assertTrue(instance.contains(b));
        assertFalse(instance.contains(c));
        assertTrue(instance.contains("a"));
        
        list1.add(c);
        list2.add(null);
        assertTrue(instance.contains(c));
        assertTrue(instance.contains(null));
    }

    /**
     * Test of size method, of class gov.sandia.isrc.util.DefaultMultiCollection.
     */
    public void testSize()
    {
        String a = "a";
        String b = "b";
        String c = "c";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        DefaultMultiCollection<String> instance =
            new DefaultMultiCollection<String>(list1, list2);

        assertEquals(2, instance.size());
        
        assertEquals( 2, instance.getSubCollectionsCount() );
        list1.add(c);
        assertEquals( 2, instance.getSubCollectionsCount() );
        
        assertEquals(3, instance.size());


    }

    /**
     * Test of iterator method, of class gov.sandia.isrc.util.DefaultMultiCollection.
     */
    public void testIterator()
    {
        String a = "a";
        String b = "b";
        String c = "c";
        
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        
        list1.add(a);
        list2.add(b);
        
        DefaultMultiCollection<String> instance =
            new DefaultMultiCollection<String>(list1, list2);
        
        Iterator<String> it = instance.iterator();
        
        assertNotNull(it);
        assertTrue(it.hasNext());
        assertEquals(a, it.next());
        assertTrue(it.hasNext());
        assertEquals(b, it.next());
        assertFalse(it.hasNext());
    }

}
