/*
 * File:                CombinationsTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 11, 2012, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 *
 * @author krdixon
 */
public class CombinationsTest
    extends TestCase
{
    
    public CombinationsTest(
        String testName )
    {
        super( testName );
    }
    
    /**
     * Test of clone method, of class Combinations.
     */
    public void testClone()
    {
        System.out.println("clone");
        int N = 10;
        int k = 3;
        Combinations instance = new Combinations( N, k );
        Combinations clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( clone, instance );
        assertEquals( instance.getN(), clone.getN() );
        assertEquals( instance.getK(), clone.getK() );
        assertEquals( instance.size(), clone.size() );
    }

    /**
     * Test of iterator method, of class Combinations.
     */
    public void testIterator()
    {
        System.out.println("iterator");
        Combinations instance = new Combinations(7, 3);
        Iterator<Vector> iterator = instance.iterator();
        int n = 0;
        while( iterator.hasNext() )
        {
            Vector v = iterator.next();
            System.out.println( n + ": " + v );
            assertEquals( instance.getN(), v.getDimensionality() );
            assertEquals( instance.getK(), (int) v.norm1() );
            n++;
        }

        assertEquals( instance.size(), n );
    }

    /**
     * Test of size method, of class Combinations.
     */
    public void testSize()
    {
        System.out.println("size");
        int N = 10;
        int k = 3;
        Combinations instance = new Combinations( N, k );
        assertEquals( MathUtil.binomialCoefficient( N, k ), instance.size() );
    }

    /**
     * Test of getN method, of class Combinations.
     */
    public void testGetN()
    {
        System.out.println("getN");
        int N = 10;
        int k = 3;
        Combinations instance = new Combinations( N, k );
        assertEquals( N, instance.getN() );
    }

    /**
     * Test of getK method, of class Combinations.
     */
    public void testGetK()
    {
        System.out.println("getK");
        int N = 10;
        int k = 3;
        Combinations instance = new Combinations( N, k );
        assertEquals( k, instance.getK() );
    }

    /**
     * Test of iterator method, of class Combinations.
     */
    public void testIterator2()
    {
        System.out.println("iterator");
        Combinations instance = new Combinations(7, 3);
        
        ArrayList<Integer> set = new ArrayList<Integer>( 7 );
        for( int i = 0; i < 7; i++ )
        {
            set.add( i );
        }

        Combinations.SubsetIterator<Integer> iterator =
            new Combinations.SubsetIterator<Integer>(7, 3, set);
        int n = 0;
        while( iterator.hasNext() )
        {
            LinkedList<Integer> s = iterator.next();
            System.out.println( n + ": " + s );
            assertEquals( instance.getK(), s.size() );
            n++;
        }

        assertEquals( instance.size(), n );
    }    
    
}
