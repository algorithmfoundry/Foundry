/*
 * File:                FiniteCapacityBufferTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class FiniteCapacityBufferTest
    extends TestCase
{
    
    /**
     * Random number generator
     */
    private Random random = new Random(1);
    
    public FiniteCapacityBufferTest(
        String testName)
    {
        super(testName);
    }

    public FiniteCapacityBuffer<Double> createInstance()
    {
        
        int capacity = random.nextInt(10) + 10;
        
        FiniteCapacityBuffer<Double> buffer = 
            new FiniteCapacityBuffer<Double>( capacity );
        
        int N = random.nextInt( 2 * capacity );
        for( int n = 0; n < N; n++ )
        {
            buffer.add( random.nextDouble() );
        }
        
        return buffer;
        
    }


    public void testConstructors()
    {
        System.out.println( "constructors" );

        FiniteCapacityBuffer<Double> buffer = new FiniteCapacityBuffer<Double>();
        assertNotNull( buffer );
        assertTrue( buffer.isEmpty() );
        assertFalse( buffer.isFull() );
        assertEquals( 0, buffer.size() );
        assertEquals( 1, buffer.getCapacity() );
        buffer.add( random.nextGaussian() );
        assertEquals( 1, buffer.size() );

        FiniteCapacityBuffer<Double> copy = new FiniteCapacityBuffer<Double>( buffer );
        assertNotSame( buffer, copy );
        assertEquals( buffer.size(), copy.size() );
        assertEquals( buffer.getCapacity(), copy.getCapacity() );
        
    }

    public void testListStuff()
    {
        System.out.println( "List Stuff" );

        FiniteCapacityBuffer<Double> buffer = new FiniteCapacityBuffer<Double>(1000);

        boolean exceptionThrown = false;
        try
        {
            buffer.getFirst();
        }
        catch (NoSuchElementException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            buffer.getLast();
        }
        catch (NoSuchElementException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        FiniteCapacityBuffer<Double> clone = buffer.clone();
        assertNotNull( clone );
        assertNotSame( buffer, clone );

    }

    /**
     * Test of clone method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        FiniteCapacityBuffer<Double> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getCapacity(), clone.getCapacity() );
        assertEquals( instance.size(), clone.size() );
        for( int n = 0; n < instance.size(); n++ )
        {
            assertEquals( instance.get(n), clone.get(n) );
        }
        
    }

    /**
     * Test of size method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testSize()
    {
        System.out.println("size");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        instance.clear();

        for( int i = 0; i < 10; i++ )
        {
            assertEquals( i, instance.size() );
            instance.add( random.nextDouble() );
        }
        
    }

    /**
     * Test of isEmpty method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testIsEmpty()
    {
        System.out.println("isEmpty");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();

        assertFalse( instance.isEmpty() );
        instance.clear();
        assertTrue( instance.isEmpty() );
        instance.add( random.nextDouble() );
        assertFalse( instance.isEmpty() );
        instance.clear();
        assertTrue( instance.isEmpty() );
        for( int i = 0; i < (2*instance.getCapacity()); i++ )
        {
            instance.add( random.nextDouble() );
            assertFalse( instance.isEmpty() );
        }
            
        instance.clear();
        assertTrue( instance.isEmpty() );
        
    }

    /**
     * Test of contains method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testContains()
    {
        System.out.println("contains");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        assertFalse( instance.contains( null ) );
        instance.clear();
        assertFalse( instance.contains( null ) );
        for( int i = 0; i < 100; i++ )
        {
            Double v = random.nextDouble();
            assertFalse( instance.contains( v ) );
            instance.add( v );
            assertTrue( instance.contains( v ) );
        }
        assertTrue( instance.contains( instance.iterator().next() ) );
        
        
    }

    /**
     * Test of iterator method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testIterator()
    {
        System.out.println("iterator");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        Iterator<Double> iter = instance.iterator();
        assertNotNull( iter );
        for( int i = 0; i < instance.size(); i++ )
        {
            assertTrue( iter.hasNext() );
            assertTrue( instance.contains( iter.next() ) );
        }
        assertFalse( iter.hasNext() );
        
    }

    /**
     * Test of toArray method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testToArray()
    {
        System.out.println("toArray");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        
        Object[] oa = instance.toArray();
        assertEquals( oa.length, instance.size() );

        Double[] da = new Double[ instance.size() ];
        instance.toArray( da );
        assertEquals( da.length, instance.size() );
        for( int i = 0; i < oa.length; i++ )
        {
            assertSame( oa[i], instance.get(i) );
            assertSame( da[i], instance.get(i) );
        }
         

    }

    /**
     * Test of add method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testAdd()
    {
        System.out.println("add");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        for( int i = 0; i < (instance.getCapacity() * 2); i++ )
        {
            double v = random.nextDouble();
            instance.add( v );
            assertTrue( instance.size() <= instance.getCapacity() );
            assertEquals( v, instance.get(instance.size()-1) );
        }
    }

    /**
     * Test of remove method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testRemove()
    {
        System.out.println("remove");

        FiniteCapacityBuffer<Double> instance = this.createInstance();
        assertFalse( instance.remove( Math.PI ) );
        while( !instance.isEmpty() )
        {
            assertTrue( instance.remove( instance.get( instance.size() / 2 ) ) );
        }
        
    }

    /**
     * Test of addFirst method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testPrepend()
    {
        System.out.println("prepend");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        for( int i = 0; i < (instance.getCapacity() * 2); i++ )
        {
            double v = random.nextDouble();
            instance.addFirst( v );
            assertTrue( instance.size() <= instance.getCapacity() );
            assertEquals( v, instance.getFirst() );
        }
    }

    /**
     * Test of addLast method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testAppend()
    {
        System.out.println("append");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        for( int i = 0; i < (instance.getCapacity() * 2); i++ )
        {
            double v = random.nextDouble();
            instance.addLast( v );
            assertTrue( instance.size() <= instance.getCapacity() );
            assertEquals( v, instance.getLast() );
        }
    }

    /**
     * Test of containsAll method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    @SuppressWarnings("unchecked")
    public void testContainsAll()
    {
        System.out.println("containsAll");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        LinkedList<Double> c = new LinkedList<Double>( instance );
        assertTrue( instance.containsAll( c ) );

        c.remove( c.iterator().next() );
        assertTrue( instance.containsAll( c ) );
        
        c.add( random.nextDouble() );
        assertFalse( instance.containsAll( c ) );
    }

    /**
     * Test of addAll method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testAddAll()
    {
        System.out.println("addAll");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        LinkedList<Double> c = new LinkedList<Double>();
        for( int i = 0; i < (instance.getCapacity()/2); i++ )
        {
            c.add( random.nextDouble() );
        }
        assertFalse( instance.containsAll( c ) );
        instance.addAll( c );
        assertTrue( instance.containsAll( c ) );
        
    }

    /**
     * Test of removeAll method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    @SuppressWarnings("unchecked")
    public void testRemoveAll()
    {
        System.out.println("removeAll");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        LinkedList<Double> c = new LinkedList<Double>( instance );
        assertTrue( instance.containsAll( c ) );
        int N = instance.size();
        for( int i = 0; i < (N/2); i++ )
        {
            c.remove( c.size()/2 );
        }
        c.add( random.nextDouble() );
        
        assertEquals( N, instance.size() );
        
        instance.removeAll( c );
        
        for( Double d : c )
        {
            assertFalse( instance.contains( d ) );
        }
        
    }

    /**
     * Test of retainAll method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    @SuppressWarnings("unchecked")
    public void testRetainAll()
    {
        System.out.println("retainAll");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        LinkedList<Double> c = new LinkedList<Double>( instance );
        assertTrue( instance.containsAll( c ) );
        int N = instance.size();
        for( int i = 0; i < (N/2); i++ )
        {
            c.remove( c.size()/2 );
        }
        
        assertEquals( N, instance.size() );
        
        instance.retainAll( c );
        assertEquals( c.size(), instance.size() );
        
        for( Double d : c )
        {
            assertTrue( instance.contains( d ) );
        }
    }

    /**
     * Test of clear method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testClear()
    {
        System.out.println("clear");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        assertTrue( instance.size() > 0 );
        instance.clear();
        assertEquals( 0, instance.size() );
        for( Double d : instance )
        {
            fail( "There should be no elements in the list!" );
        }
    }

    /**
     * Test of getCapacity method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testGetCapacity()
    {
        System.out.println("getCapacity");
        
        FiniteCapacityBuffer<Double> instance = this.createInstance();
        assertTrue( instance.getCapacity() > 0 );
        assertTrue( instance.getCapacity() >= instance.size() );
        
        int N = instance.getCapacity() * 2;
        instance = new FiniteCapacityBuffer<Double>( N );
        assertEquals( N, instance.getCapacity() );
    }

    /**
     * Test of setCapacity method, of class gov.sandia.cognition.util.FiniteCapacityBuffer.
     */
    public void testSetCapacity()
    {
        System.out.println("setCapacity");

        int N = random.nextInt(10) + 2;
        FiniteCapacityBuffer<Double> instance = new FiniteCapacityBuffer<Double>( N );
        assertEquals( N, instance.getCapacity() );

        instance.setCapacity( N+1 );
        assertEquals( N+1, instance.getCapacity() );
        assertTrue( instance.isEmpty() );
        
        try
        {
            instance.setCapacity(0);
            fail( "Cannot have capacity <= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }
    
}
