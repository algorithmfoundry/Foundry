/*
 * File:                IntegerSpanTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 23, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *  
 * 
 */

package gov.sandia.cognition.collection;

import java.util.Iterator;
import junit.framework.TestCase;

/**
 * JUnit tests for class IntegerSpanTest
 * @author Kevin R. Dixon
 */
public class IntegerSpanTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class IntegerSpanTest
     * @param testName name of this test
     */
    public IntegerSpanTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test constructors
     */
    public void testConstuctors()
    {
        System.out.println( "constructors" );

        IntegerSpan i;
        i = new IntegerSpan( 0, 1 );
        i = new IntegerSpan( 1, 1 );

        try
        {
            i = new IntegerSpan( 1, 0 );
            fail( "minValue must be <= maxValue" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getMinValue method, of class IntegerSpan.
     */
    public void testGetMinValue()
    {
        System.out.println( "getMinValue" );

        int minValue = 1;
        int maxValue = 2;
        IntegerSpan i = new IntegerSpan( minValue, maxValue );
        assertEquals( minValue, i.getMinValue() );

    }

    /**
     * Test of setMinValue method, of class IntegerSpan.
     */
    public void testSetMinValue()
    {
        System.out.println( "setMinValue" );
        int minValue = 1;
        int maxValue = 2;
        IntegerSpan i = new IntegerSpan( minValue, maxValue );
        assertEquals( minValue, i.getMinValue() );

        int m2 = minValue - 1;
        i.setMinValue( m2 );
        assertEquals( m2, i.getMinValue() );
    }

    /**
     * Test of getMaxValue method, of class IntegerSpan.
     */
    public void testGetMaxValue()
    {
        System.out.println( "getMaxValue" );
        int minValue = 1;
        int maxValue = 2;
        IntegerSpan i = new IntegerSpan( minValue, maxValue );
        assertEquals( maxValue, i.getMaxValue() );
    }

    /**
     * Test of setMaxValue method, of class IntegerSpan.
     */
    public void testSetMaxValue()
    {
        System.out.println( "setMaxValue" );
        int minValue = 1;
        int maxValue = 2;
        IntegerSpan i = new IntegerSpan( minValue, maxValue );
        assertEquals( maxValue, i.getMaxValue() );

        int m2 = maxValue + 1;
        i.setMaxValue( m2 );
        assertEquals( m2, i.getMaxValue() );
    }

    /**
     * Test of iterator method, of class IntegerSpan.
     */
    public void testIterator()
    {
        System.out.println( "iterator" );
        int minValue = 1;
        int maxValue = 2;
        IntegerSpan i = new IntegerSpan( minValue, maxValue );
        Iterator<Integer> iter = i.iterator();

        assertNotNull( iter );
        assertTrue( iter.hasNext() );

        int index = i.getMinValue();
        while (iter.hasNext())
        {
            assertEquals( index, (int) iter.next() );
            index++;
        }

        assertEquals( index, i.getMaxValue() + 1 );

        try
        {
            iter.remove();
            fail( "Cannot remove" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clone method, of class IntegerSpan.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        IntegerSpan instance = new IntegerSpan( 2, 3 );
        IntegerSpan clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getMinValue(), clone.getMinValue() );
        assertEquals( instance.getMaxValue(), clone.getMaxValue() );
    }

    /**
     * Test of size method, of class IntegerSpan.
     */
    public void testSize()
    {
        System.out.println( "size" );
        IntegerSpan instance = new IntegerSpan( 10, 20 );
        int expected = instance.size();
        int num = 0;
        for (Integer v : instance)
        {
            num++;
        }

        assertEquals( num, expected );

        instance = new IntegerSpan( 5, 5 );
        assertEquals( 1, instance.size() );
    }

    /**
     * Test of contains method, of class IntegerSpan.
     */
    public void testContains()
    {
        System.out.println( "contains" );
        int minValue = 10;
        int maxValue = 20;
        IntegerSpan instance = new IntegerSpan(minValue, maxValue);
        assertTrue( instance.contains(minValue) );
        assertTrue( instance.contains(maxValue) );
        assertTrue( instance.contains(minValue+1) );
        assertTrue( instance.contains(maxValue-1) );
        
        assertFalse(instance.contains(minValue-1) );
        assertFalse(instance.contains(maxValue+1) );
        
        assertTrue( instance.contains( new Integer( minValue + 1 ) ) );
        assertFalse( instance.contains( new Integer( minValue-1 ) ) );
        assertFalse( instance.contains( instance ) );
        
    }

}
