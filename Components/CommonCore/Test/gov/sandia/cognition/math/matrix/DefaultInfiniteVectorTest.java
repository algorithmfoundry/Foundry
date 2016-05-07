/*
 * File:                DefaultInfiniteVectorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 26, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.collection.ScalarMap;
import java.util.Map;
import java.util.Set;

/**
 * Tests for class DefaultInfiniteVectorTest.
 * @author krdixon
 */
public class DefaultInfiniteVectorTest
    extends InfiniteVectorTestHarness<String>
{
    /**
     * Default Constructor
     */
    public DefaultInfiniteVectorTest(
        String name )
    {
        super( name );
    }

    @Override
    protected DefaultInfiniteVector<String> createZero()
    {
        return new DefaultInfiniteVector<>();
    }
    
    @Override
    protected DefaultInfiniteVector<String> createRandom()
    {
        String[] s2 = { "f", "e", "d", "c", "b" };
        DefaultInfiniteVector<String> v2 =
            new DefaultInfiniteVector<String>();
        for( String s : s2 )
        {
            v2.set( s, RANDOM.nextDouble() );
        }
        return v2;
    }

    protected DefaultInfiniteVector<String> createOther()
    {
        String[] s1 = { "a", "b", "c", "d" };
        DefaultInfiniteVector<String> v1 =
            new DefaultInfiniteVector<String>();
        for( String s : s1 )
        {
            v1.set( s, RANDOM.nextDouble() );
        }
        return v1;
    }

    @Override
    protected DefaultInfiniteVector<String> createRandom2()
    {
        return this.createOther();
    }

    /**
     * Constructor
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DefaultInfiniteVector<String> instance = new DefaultInfiniteVector<String>();
    }

    @Override
    protected InfiniteVector<String> createVector(
        int numDim)
    {
        DefaultInfiniteVector<String> instance =
            new DefaultInfiniteVector<String>();
        for( int i = 0; i < numDim; i++ )
        {
            instance.set( Integer.toString(i), RANDOM.nextGaussian() );
        }
        return instance;
    }

    @Override
    protected InfiniteVector<String> createCopy(
        Vector vector)
    {
        DefaultInfiniteVector<String> instance =
            new DefaultInfiniteVector<String>();
        for( VectorEntry entry : vector )
        {
            instance.set( Integer.toString(entry.getIndex()), entry.getValue() );
        }
        return instance;
    }

    public InfiniteVector<Integer> createInstance()
    {
        int min = RANDOM.nextInt(100);
        int max = RANDOM.nextInt(100) + min;
        DefaultInfiniteVector<Integer> result = new DefaultInfiniteVector<Integer>();
        for( int i = min; i <= max; i++ )
        {
            result.set( i, RANDOM.nextGaussian() );
        }
        return result;
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
        IntegerSpan keys = new IntegerSpan(0,10);
        ScalarMap<Integer> instance = this.createInstance();
        instance.clear();
        double base = RANDOM.nextDouble();
        for( Integer key : keys )
        {
            instance.set(key, key+base);
        }
        instance.decrementAll(keys);
        for( Integer key : keys )
        {
            assertEquals( key+base-1.0, instance.get(key) );
        }
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
        instance.set( RANDOM.nextInt(), max-1.0);
        assertEquals( instance.keySet().size()-1, instance.getMaxValueKeys().size() );
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
        instance.set( RANDOM.nextInt(), min+1.0 );
        assertEquals( instance.keySet().size()-1, instance.getMinValueKeys().size() );
        for( Integer key : instance.getMinValueKeys() )
        {
            assertEquals( min, instance.get(key) );
        }

    }

    /**
     * Test of compact method, of class NumericMap.
     */
    public void testCompact()
    {
        System.out.println("compact");

        DefaultInfiniteVector<String> instance = this.createRandom();
        String i1 = "a";
        instance.set(i1, RANDOM.nextDouble());
        int s1 = instance.size();
        instance.set(i1,0.0);
        assertEquals( s1, instance.size() );
        instance.compact();
        assertTrue( instance.size() < s1 );
    }
    
}
