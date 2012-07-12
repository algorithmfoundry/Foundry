/*
 * File:                DefaultTripleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 29, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests of DefaultTriple
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultTripleTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultTripleTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Random
     */
    public final Random RANDOM = new Random(1);

    /**
     * Constructors
     */
    public void testConstructors()
    {
        Object first = null;
        Object second = null;
        Object third = null;
        
        DefaultTriple<Object, Object, Object> instance = new DefaultTriple<Object, Object, Object>();
        assertSame(first, instance.getFirst());
        assertSame(second, instance.getSecond());
        assertSame(third, instance.getThird());
        
        first = new Object();
        second = new Object();
        third = new Object();
        
        instance = new DefaultTriple<Object, Object, Object>(first, second, third);
        assertSame(first, instance.getFirst());
        assertSame(second, instance.getSecond());
        assertSame(third, instance.getThird());
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        DefaultTriple<?,?,?> instance = new DefaultTriple<Vector,Vector,Vector>(
            Vector3.createRandom(RANDOM),
            Vector3.createRandom(RANDOM),
            Vector3.createRandom(RANDOM) );
        DefaultTriple<?,?,?> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getFirst() );
        assertNotSame( instance.getFirst(), clone.getFirst() );
        assertEquals( instance.getFirst(), clone.getFirst() );
        assertNotNull( clone.getSecond() );
        assertNotSame( instance.getSecond(), clone.getSecond() );
        assertEquals( instance.getSecond(), clone.getSecond() );
        assertNotNull( clone.getThird() );
        assertNotSame( instance.getThird(), clone.getThird() );
        assertEquals( instance.getThird(), clone.getThird() );
        
    }

    /**
     * Test of getFirst method, of class DefaultTriple.
     */
    public void testGetFirst()
    {
        this.testSetFirst();
    }

    /**
     * Test of setFirst method, of class DefaultTriple.
     */
    public void testSetFirst()
    {
        Object first = null;
        DefaultTriple<Object, Object, Object> instance = new DefaultTriple<Object, Object, Object>();
        assertSame(first, instance.getFirst());
        
        first = new Object();
        instance.setFirst(first);
        assertSame(first, instance.getFirst());
        
        first = new Object();
        instance.setFirst(first);
        assertSame(first, instance.getFirst());
        
        
        first = null;
        instance.setFirst(first);
        assertSame(first, instance.getFirst());
    }

    /**
     * Test of getSecond method, of class DefaultTriple.
     */
    public void testGetSecond()
    {
        this.testSetSecond();
    }

    /**
     * Test of setSecond method, of class DefaultTriple.
     */
    public void testSetSecond()
    {
        Object second = null;
        DefaultTriple<Object, Object, Object> instance = new DefaultTriple<Object, Object, Object>();
        assertSame(second, instance.getSecond());
        
        second = new Object();
        instance.setSecond(second);
        assertSame(second, instance.getSecond());
        
        second = new Object();
        instance.setSecond(second);
        assertSame(second, instance.getSecond());
        
        second = null;
        instance.setSecond(second);
        assertSame(second, instance.getSecond());
    }

    /**
     * Test of getThird method, of class DefaultTriple.
     */
    public void testGetThird()
    {
        this.testSetThird();
    }

    /**
     * Test of setThird method, of class DefaultTriple.
     */
    public void testSetThird()
    {
        Object third = null;
        DefaultTriple<Object, Object, Object> instance = new DefaultTriple<Object, Object, Object>();
        assertSame(third, instance.getThird());
        
        third = new Object();
        instance.setThird(third);
        assertSame(third, instance.getThird());
        
        third = new Object();
        instance.setThird(third);
        assertSame(third, instance.getThird());
        
        third = null;
        instance.setThird(third);
        assertSame(third, instance.getThird());
    }

    /**
     * Test of mergeCollections method, of class DefaultTriple.
     */
    public void testMergeCollections()
    {
        Integer[] firsts = { 1, 2, 3 };
        Double[] seconds = { 2.1, 2.2, 2.3 };
        String[] thirds = { "one", "two", "three" };

        ArrayList<DefaultTriple<Integer, Double, String>> result =
            DefaultTriple.mergeCollections(
            Arrays.asList(firsts), Arrays.asList(seconds), Arrays.asList(thirds));
        
        for (int i = 0; i < firsts.length; i++)
        {
            DefaultTriple<Integer, Double, String> triple = result.get(i);
            
            assertSame(firsts[i], triple.getFirst());
            assertSame(seconds[i], triple.getSecond());
            assertSame(thirds[i], triple.getThird());
        }

        try
        {
            DefaultTriple.mergeCollections(
                Arrays.asList(firsts), Arrays.asList(seconds), new LinkedList<Object>() );
            fail( "Collections are different sizes!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
