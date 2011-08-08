/*
 * File:                DefaultPairTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    DefaultPair
 *
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2006-07-18",
    changesNeeded=false,
    comments="I'm not crazy about the monolithic tests... but it works."
)
public class DefaultPairTest 
    extends TestCase
{
    
    public DefaultPairTest(
        String testName)
    {
        super(testName);
    }

    public void testCreation()
    {
        Object o1 = new Object();
        Object o2 = new Object();
        DefaultPair<Object, Object> pair = null;
        
        pair = new DefaultPair<Object, Object>();
        pair = new DefaultPair<Object, Object>(o1, o1);
        pair = new DefaultPair<Object, Object>(o1, o2);
        pair = new DefaultPair<Object, Object>(null, null);

        DefaultPair<Object,Object> p2 = new DefaultPair<Object, Object>( pair );
        assertNotSame( pair, p2 );
        assertSame( pair.getFirst(), p2.getFirst() );
        assertSame( pair.getSecond(), p2.getSecond() );
    }
    
    public void testPair()
    {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        Object o4 = new Object();
        DefaultPair<Object, Object> pair = new DefaultPair<Object, Object>(o1, o2);
        
        Assert.assertSame(o1, pair.getFirst());
        Assert.assertSame(o2, pair.getSecond());
        
        pair.setFirst(o3);
        Assert.assertSame(o3, pair.getFirst());
        
        pair.setSecond(o4);
        Assert.assertSame(o4, pair.getSecond());
        
        pair.setFirst(null);
        Assert.assertNull(pair.getFirst());
        pair.setSecond(null);
        Assert.assertNull(pair.getSecond());
        
        pair = new DefaultPair<Object, Object>();
        Assert.assertNull(pair.getFirst());
        Assert.assertNull(pair.getSecond());
        
        pair = new DefaultPair<Object, Object>(null, null);
        Assert.assertNull(pair.getFirst());
        Assert.assertNull(pair.getSecond());
    }

    public void testEquals()
    {
        DefaultPair<String, Character> empty = DefaultPair.create();
            //new DefaultPair<String, Character>();
        DefaultPair<String, Character> ab = DefaultPair.create("a", 'b');
        DefaultPair<String, Character> ba = DefaultPair.create("b", 'a');

        assertTrue(empty.equals(empty));
        assertFalse(empty.equals(ab));
        assertFalse(empty.equals(ba));
        assertFalse(empty.equals(null));
        assertFalse(empty.equals(new Object()));

        assertTrue(ab.equals(ab));
        assertTrue(ab.equals(DefaultPair.create("a", 'b')));
        assertFalse(ab.equals(empty));
        assertFalse(ab.equals(ba));
        assertFalse(ab.equals(null));
        assertFalse(ab.equals(new Object()));

        assertTrue(ba.equals(ba));
        assertTrue(ba.equals(DefaultPair.create("b", 'a')));
        assertFalse(ba.equals(empty));
        assertFalse(ba.equals(ab));
        assertFalse(ba.equals(null));
        assertFalse(ba.equals(new Object()));
    }


    public void testHashCode()
    {
        DefaultPair<Object, Object> pair = new DefaultPair<Object, Object>();
        assertEquals(pair.hashCode(), new DefaultPair<Object, Object>().hashCode());

        pair.setFirst("a");
        pair.setSecond("b");
        assertEquals(pair.hashCode(), DefaultPair.create("a", "b").hashCode());
    }

    public void testMergeCollections()
    {
        System.out.println( "mergeCollections" );

        List<Integer> firsts = Arrays.asList( 1, 2, 3 );
        List<Double> seconds = Arrays.asList( 1.0, 2.0, 3.0 );

        ArrayList<DefaultPair<Integer,Double>> combined =
            DefaultPair.mergeCollections(firsts, seconds);

        assertEquals( 3, combined.size() );
        for( int i = 0; i < combined.size(); i++ )
        {
            assertSame( firsts.get(i), combined.get(i).getFirst() );
            assertSame( seconds.get(i), combined.get(i).getSecond() );
        }

        List<Boolean> barf = Arrays.asList( false );
        try
        {
            DefaultPair.mergeCollections(firsts, barf);
            fail( "Lists must be same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }


    public void testCreate()
    {
        System.out.println( "Create" );

        Double first = 1.0;
        Integer second = 2;

        DefaultPair<Double,Integer> pair = DefaultPair.create(first, second);
        assertSame( first, pair.getFirst() );
        assertSame( second, pair.getSecond() );

        pair = DefaultPair.create();
        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
        
    }

}
