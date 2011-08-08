/*
 * File:                MapBasedSortedDataHistogramTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 17, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for MapBasedSortedDataHistogramTest.
 *
 * @author krdixon
 */
public class MapBasedSortedDataHistogramTest
    extends MapBasedDataHistogramTest
{

    /**
     * Tests for class MapBasedSortedDataHistogramTest.
     * @param testName Name of the test.
     */
    public MapBasedSortedDataHistogramTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public MapBasedSortedDataHistogram<String> createInstanceEmpty()
    {
        return new MapBasedSortedDataHistogram.PMF<String>();
    }

    /**
     * Tests the constructors of class MapBasedSortedDataHistogramTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MapBasedSortedDataHistogram<String> instance =
            new MapBasedSortedDataHistogram<String>();
        assertEquals( 0, instance.getCountMap().size() );
        assertEquals( 0, instance.getTotalCount() );

        List<String> s = Arrays.asList( "a", "c", "b", "c" );
        instance = new MapBasedSortedDataHistogram<String>(s);
        assertEquals( s.size(), instance.getTotalCount() );
        assertEquals( 3, instance.getDomain().size() );

        MapBasedSortedDataHistogram<String> i2 =
            new MapBasedSortedDataHistogram<String>( instance );
        assertNotSame( i2.getCountMap(), instance.getCountMap() );
        assertEquals( i2.getDomain().size(), instance.getDomain().size() );
        assertEquals( i2.getTotalCount(), instance.getTotalCount() );

    }

    /**
     * Test of clone method, of class MapBasedSortedDataHistogram.
     */
    @Override
    public void testClone()
    {
        System.out.println("clone");
        MapBasedSortedDataHistogram<String> instance =
            new MapBasedSortedDataHistogram<String>();
        MapBasedSortedDataHistogram<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.countMap, clone.countMap);
        assertEquals(0, clone.getTotalCount());
        assertTrue(clone.getDomain().isEmpty());
        assertEquals(0, instance.getTotalCount());
        assertTrue(instance.getDomain().isEmpty());

        instance.add("a", 4);
        instance.add("b", 7);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.countMap, clone.countMap);
        assertEquals(11, clone.getTotalCount());
        assertEquals(4, clone.getCount("a"));
        assertEquals(7, clone.getCount("b"));
        assertEquals(11, instance.getTotalCount());
        assertEquals(4, instance.getCount("a"));
        assertEquals(7, instance.getCount("b"));
    }

    public void testSortedGetDomain()
    {
        System.out.println( "getDomain sorted" );

        MapBasedSortedDataHistogram<String> instance =
            (MapBasedSortedDataHistogram<String>) this.createInstancePopulated();
        instance.add("0");
        instance.add(" ");
        instance.add("z");
        instance.add("b");
        instance.add("g");
        instance.add("e");
        String previous = null;
        for( String current : instance.getDomain() )
        {
            if( previous != null )
            {
                assertTrue( current.compareTo( previous ) > 0 );
            }
            previous = current;
        }

    }

}
