/*
 * File:                VectorizableIndexComparatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for VectorizableIndexComparatorTest.
 *
 * @author krdixon
 */
public class VectorizableIndexComparatorTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class VectorizableIndexComparatorTest.
     * @param testName Name of the test.
     */
    public VectorizableIndexComparatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class VectorizableIndexComparatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        VectorizableIndexComparator vic = new VectorizableIndexComparator();
        assertEquals( 0, vic.getIndex() );

        int index = RANDOM.nextInt(10) + 1;
        vic = new VectorizableIndexComparator( index );
        assertEquals( index, vic.getIndex() );
    }

    /**
     * Test of clone method, of class VectorizableIndexComparator.
     */
    public void testClone()
    {
        System.out.println("clone");
        VectorizableIndexComparator instance = new VectorizableIndexComparator(1);
        VectorizableIndexComparator clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getIndex(), clone.getIndex() );
    }

    /**
     * Test of compare method, of class VectorizableIndexComparator.
     */
    public void testCompare()
    {
        System.out.println("compare");
        Vectorizable o1 = new Vector3(1.0, -2.0, 3.0 );
        Vectorizable o2 = new Vector3(1.0, 2.0, -3.0 );
        VectorizableIndexComparator instance = new VectorizableIndexComparator();
        instance.setIndex(0);
        assertEquals( 0, instance.compare(o1, o2) );
        assertEquals( 0, instance.compare(o2, o1) );

        instance.setIndex(1);
        assertEquals( -1, instance.compare(o1, o2) );
        assertEquals(  1, instance.compare(o2, o1) );

        instance.setIndex(2);
        assertEquals(  1, instance.compare(o1, o2) );
        assertEquals( -1, instance.compare(o2, o1) );
    }

    /**
     * Test of getIndex method, of class VectorizableIndexComparator.
     */
    public void testGetIndex()
    {
        System.out.println("getIndex");
        int index = RANDOM.nextInt(10) + 1;
        VectorizableIndexComparator instance = new VectorizableIndexComparator(index);
        assertEquals( index, instance.getIndex() );
    }

    /**
     * Test of setIndex method, of class VectorizableIndexComparator.
     */
    public void testSetIndex()
    {
        System.out.println("setIndex");
        int index = RANDOM.nextInt(10) + 1;
        VectorizableIndexComparator instance = new VectorizableIndexComparator();
        instance.setIndex(index);
        assertEquals( index, instance.getIndex() );
    }



}
