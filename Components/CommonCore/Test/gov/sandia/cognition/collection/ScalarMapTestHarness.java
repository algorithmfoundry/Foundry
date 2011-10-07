/*
 * File:                ScalarMap.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 *
 * Copyright April 27, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 *
 */
package gov.sandia.cognition.collection;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import junit.framework.TestCase;

/**
 * Tests for class ScalarMapTestHarness.
 * @author krdixon
 */
public class ScalarMapTestHarness
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
     * Default number of samples to test against, {@value}.
     */
    public final int NUM_SAMPLES = 1000;


    /**
     * Default Constructor
     */
    public ScalarMapTestHarness(
        String name )
    {
        super( name );
    }

    public ScalarMap<Integer> createInstance()
    {
        return null;
    }

    /**
     * Tests the constructors of class ScalarMapTestHarness.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Tests the clone method of class ScalarMapTestHarness.
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asMap method, of class ScalarMap.
     */
    public void testAsMap()
    {
        System.out.println("asMap");
        ScalarMap<Integer> instance = this.createInstance();
        Map<Integer, ? extends Number> result = instance.asMap();
        assertEquals( instance.size(), result.size() );
        for( Map.Entry<Integer,? extends Number> entry : result.entrySet() )
        {
            assertEquals( instance.get(entry.getKey()), entry.getValue().doubleValue(), TOLERANCE );
        }
    }

    /**
     * Test of get method, of class ScalarMap.
     */    
    public void testGet()
    {
        System.out.println("get");
        ScalarMap<Integer> instance = this.createInstance();
        Integer index = 0;
        double value = RANDOM.nextDouble();
        instance.set( index, value );
        assertEquals( value, instance.get(index) );

        instance.clear();
        assertEquals( 0.0, instance.get(index) );
    }

    /**
     * Test of set method, of class ScalarMap.
     */
    public void testSet()
    {
        System.out.println("set");
        ScalarMap<Integer> instance = this.createInstance();
        Integer index = 0;
        double value = RANDOM.nextDouble();
        instance.set( index, value );
        assertEquals( value, instance.get(index) );
        instance.clear();
        assertEquals( 0.0, instance.get(index) );
        instance.set(index, value);
        assertEquals( value, instance.get(index) );
    }

    /**
     * Test of setAll method, of class ScalarMap.
     */
    public void testSetAll()
    {
        System.out.println("setAll");
        IntegerSpan keys = new IntegerSpan(0,10);
        double value = RANDOM.nextDouble();
        ScalarMap<Integer> instance = this.createInstance();
        instance.clear();
        instance.setAll(keys, value);
        assertEquals( keys.size(), instance.size() );
        for( ScalarMap.Entry<Integer> entry : instance.entrySet() )
        {
            assertTrue( keys.contains( entry.getKey() ) );
            assertEquals( value, entry.getValue() );
        }
    }

    /**
     * Test of increment method, of class ScalarMap.
     */
    public void testIncrement_GenericType()
    {
        System.out.println("increment");
        ScalarMap<Integer> instance = this.createInstance();
        for( Integer key : instance.keySet() )
        {
            double previous = instance.get(key);
            double expected = previous + 1.0;
            assertEquals( expected, instance.increment(key) );
            assertEquals( expected, instance.get(key) );
        }

        instance.clear();
        Integer index = RANDOM.nextInt();
        double expected = 1.0;
        assertEquals( expected, instance.increment(index) );
        assertEquals( expected, instance.get(index) );

    }

    /**
     * Test of increment method, of class ScalarMap.
     */
    public void testIncrement_GenericType_double()
    {
        System.out.println("increment");
        ScalarMap<Integer> instance = this.createInstance();
        for( Integer key : instance.keySet() )
        {
            double delta = RANDOM.nextDouble();
            double previous = instance.get(key);
            double expected = previous + delta;
            assertEquals( expected, instance.increment(key,delta) );
            assertEquals( expected, instance.get(key) );
        }

        instance.clear();
        Integer index = RANDOM.nextInt();
        double delta = RANDOM.nextDouble();
        double expected = delta;
        assertEquals( expected, instance.increment(index,delta) );
        assertEquals( expected, instance.get(index) );

    }

    /**
     * Test of incrementAll method, of class ScalarMap.
     */
    public void testIncrementAll_Iterable()
    {
        System.out.println("incrementAll");
        IntegerSpan keys = new IntegerSpan(0,10);
        ScalarMap<Integer> instance = this.createInstance();
        instance.clear();
        double base = RANDOM.nextDouble();
        for( Integer key : keys )
        {
            instance.set(key, key+base);
        }
        instance.incrementAll(keys);
        for( Integer key : keys )
        {
            assertEquals( key+base+1.0, instance.get(key) );
        }
    }

    /**
     * Test of decrement method, of class ScalarMap.
     */    
    public void testDecrement_GenericType()
    {
        System.out.println("decrement");
        ScalarMap<Integer> instance = this.createInstance();
        for( Integer key : instance.keySet() )
        {
            double previous = instance.get(key);
            double expected = previous - 1.0;
            assertEquals( expected, instance.decrement(key) );
            assertEquals( expected, instance.get(key) );
        }

        instance.clear();
        Integer index = RANDOM.nextInt();
        double expected = 1.0;
        assertEquals( expected, instance.increment(index) );
        assertEquals( expected, instance.get(index) );
    }

    /**
     * Test of decrement method, of class ScalarMap.
     */
    
    public void testDecrement_GenericType_double()
    {
        System.out.println("decrement");
        ScalarMap<Integer> instance = this.createInstance();
        for( Integer key : instance.keySet() )
        {
            double delta = RANDOM.nextDouble();
            double previous = instance.get(key);
            double expected = previous - delta;
            assertEquals( expected, instance.decrement(key,delta) );
            assertEquals( expected, instance.get(key) );
        }

        instance.clear();
        Integer index = RANDOM.nextInt();
        double delta = RANDOM.nextDouble();
        double expected = -delta;
        assertEquals( expected, instance.decrement(index,delta) );
        assertEquals( expected, instance.get(index) );
    }

    /**
     * Test of decrementAll method, of class ScalarMap.
     */
    
    public void testDecrementAll_Iterable()
    {
        System.out.println("decrementAll");
        Iterable<? extends Integer> keys = null;
        ScalarMap<Integer> instance = this.createInstance();
        instance.decrementAll(keys);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decrementAll method, of class ScalarMap.
     */
    
    public void testDecrementAll_ScalarMap()
    {
        System.out.println("decrementAll");
        ScalarMap<? extends Integer> other = null;
        ScalarMap<Integer> instance = this.createInstance();
        instance.decrementAll(other);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxValue method, of class ScalarMap.
     */
    
    public void testGetMaxValue()
    {
        System.out.println("getMaxValue");
        ScalarMap<Integer> instance = this.createInstance();
        double expResult = 0.0;
        double result = instance.getMaxValue();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMinValue method, of class ScalarMap.
     */
    
    public void testGetMinValue()
    {
        System.out.println("getMinValue");
        ScalarMap<Integer> instance = this.createInstance();
        double expResult = 0.0;
        double result = instance.getMinValue();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of entrySet method, of class ScalarMap.
     */
    
    public void testEntrySet()
    {
        System.out.println("entrySet");
        ScalarMap<Integer> instance = this.createInstance();
        Set<? extends ScalarMap.Entry<Integer>> entrySet = instance.entrySet();
        assertEquals( instance.size(), entrySet.size() );
        for( ScalarMap.Entry<Integer> entry : entrySet )
        {
            assertEquals( instance.get(entry.getKey()), entry.getValue() );
        }
    }

    /**
     * Test of isEmpty method, of class NumericMap.
     */
    
    public void testIsEmpty()
    {
        System.out.println("isEmpty");
        ScalarMap<Integer> instance = this.createInstance();
        assertFalse( instance.isEmpty() );
        instance.clear();
        assertTrue( instance.isEmpty() );

        instance.set(1, RANDOM.nextDouble());
        assertFalse( instance.isEmpty() );
        instance.set(1, 0.0);
        assertFalse( instance.isEmpty() );
    }

    /**
     * Test of keySet method, of class NumericMap.
     */
    
    public void testKeySet()
    {
        System.out.println("keySet");
        ScalarMap<Integer> instance = this.createInstance();
        Set<Integer> keySet = instance.keySet();
        assertEquals( instance.size(), keySet.size() );
        for( Integer key : keySet )
        {
            assertTrue( instance.containsKey(key) );
        }
    }

    /**
     * Test of containsKey method, of class NumericMap.
     */
    
    public void testContainsKey()
    {
        System.out.println("containsKey");
        ScalarMap<Integer> instance = this.createInstance();
        instance.clear();
        assertFalse( instance.containsKey(0) );
        instance.set(0, RANDOM.nextDouble() );
        assertTrue( instance.containsKey(0) );
    }

    /**
     * Test of size method, of class NumericMap.
     */
    
    public void testSize()
    {
        System.out.println("size");
        ScalarMap<Integer> instance = this.createInstance();
        instance.clear();
        assertEquals( 0, instance.size() );
        for( int i = 0; i < 10; i++ )
        {
            assertEquals( i, instance.size() );
            instance.set(i, RANDOM.nextDouble() );
            assertEquals( i+1, instance.size() );
            instance.set( new Integer(i), RANDOM.nextDouble() );
            assertEquals( i+1, instance.size() );
            instance.set( i, 0.0 );
            assertEquals( i+1, instance.size() );
        }
    }

    /**
     * Test of clear method, of class NumericMap.
     */
    
    public void testClear()
    {
        System.out.println("clear");
        ScalarMap<Integer> instance = this.createInstance();
        instance.clear();
        assertEquals( 0, instance.size() );
        assertEquals( 0, instance.asMap().size() );
    }

    /**
     * Test of getMaxValueKey method, of class NumericMap.
     */
    
    public void testGetMaxValueKey()
    {
        System.out.println("getMaxValueKey");
        ScalarMap<Integer> instance = this.createInstance();
        Integer result = instance.getMaxValueKey();

        double max = Double.NEGATIVE_INFINITY;
        for( ScalarMap.Entry<Integer> key : instance.entrySet() )
        {
            if( max < key.getValue() )
            {
                max = key.getValue();
            }
        }
        assertEquals( max, instance.get(result) );
    }

    /**
     * Test of getMaxValueKeys method, of class NumericMap.
     */
    
    public void testGetMaxValueKeys()
    {
        System.out.println("getMaxValueKeys");
        ScalarMap<Integer> instance = this.createInstance();

        double max = Double.NEGATIVE_INFINITY;
        for( ScalarMap.Entry<Integer> key : instance.entrySet() )
        {
            if( max < key.getValue() )
            {
                max = key.getValue();
            }
        }

        Set<Integer> maxValueKeys = instance.getMaxValueKeys();
        for( Integer key : instance.keySet() )
        {
            if( maxValueKeys.contains(key) )
            {
                assertEquals( max, instance.get(key) );
            }
            else
            {
                assertTrue( instance.get(key) < max );
            }
        }

        instance.setAll(instance.keySet(), max);
        instance.set( RANDOM.nextInt(), -max );
        assertEquals( instance.keySet().size(), instance.getMaxValueKeys().size() );
        for( Integer key : instance.getMaxValueKeys() )
        {
            assertEquals( max, instance.get(key) );
        }

    }

    /**
     * Test of getMinValueKey method, of class NumericMap.
     */
    
    public void testGetMinValueKey()
    {
        System.out.println("getMinValueKey");
        ScalarMap<Integer> instance = this.createInstance();
        Integer result = instance.getMinValueKey();

        double min = Double.POSITIVE_INFINITY;
        for( ScalarMap.Entry<Integer> key : instance.entrySet() )
        {
            if( min > key.getValue() )
            {
                min = key.getValue();
            }
        }
        assertEquals( min, instance.get(result) );
    }

    /**
     * Test of getMinValueKeys method, of class NumericMap.
     */
    
    public void testGetMinValueKeys()
    {
        System.out.println("getMinValueKeys");
        ScalarMap<Integer> instance = this.createInstance();
        Integer result = instance.getMinValueKey();

        double min = Double.POSITIVE_INFINITY;
        for( ScalarMap.Entry<Integer> key : instance.entrySet() )
        {
            if( min > key.getValue() )
            {
                min = key.getValue();
            }
        }
        assertEquals( min, instance.get(result) );

        Set<Integer> minValueKeys = instance.getMinValueKeys();
        for( Integer key : instance.keySet() )
        {
            if( minValueKeys.contains(key) )
            {
                assertEquals( min, instance.get(key) );
            }
            else
            {
                assertTrue( instance.get(key) > min );
            }
        }

        instance.setAll(instance.keySet(), min);
        instance.set( RANDOM.nextInt(), -min );
        assertEquals( instance.keySet().size(), instance.getMaxValueKeys().size() );
        for( Integer key : instance.getMaxValueKeys() )
        {
            assertEquals( min, instance.get(key) );
        }

    }

}
