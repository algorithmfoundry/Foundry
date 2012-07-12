/*
 * File:                DelayFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 25, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import junit.framework.TestCase;

/**
 * Tests for DelayFunction
 * @author Kevin R. Dixon
 */
public class DelayFunctionTest
    extends TestCase
{
    
    /**
     * Constructor
     * @param testName Name of the test.
     */
    public DelayFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getDelaySamples method, of class gov.sandia.cognition.learning.util.function.DelayFunction.
     */
    public void testGetDelaySamples()
    {
        System.out.println("getDelaySamples");
        
        int N = (int) (Math.random() * 10) + 1;
        DelayFunction<Double> instance = new DelayFunction<Double>( N );
        
        assertEquals( N, instance.getDelaySamples() );
    }
    
    /**
     * Test of setDelaySamples method, of class gov.sandia.cognition.learning.util.function.DelayFunction.
     */
    public void testSetDelaySamples()
    {
        System.out.println("setDelaySamples");
        
        int N = (int) (Math.random() * 10) + 1;
        DelayFunction<Double> instance = new DelayFunction<Double>( N );
        assertEquals( N, instance.getDelaySamples() );
        instance.evaluate( Math.random() );
        
        instance = new DelayFunction<Double>( 0 );
        instance.evaluate( Math.random() );
        
        try
        {
            instance = new DelayFunction<Double>( -1 );
            fail( "Can't have negative delaySamples" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }    

    /**
     * Test of createDefaultState method, of class gov.sandia.cognition.learning.util.function.DelayFunction.
     */
    public void testCreateDefaultState()
    {
        System.out.println("createDefaultState");
        
        int N = (int) (Math.random() * 10) + 1;
        DelayFunction<Double> instance = new DelayFunction<Double>( N );

        FiniteCapacityBuffer<Double> b = instance.createDefaultState();
        assertEquals( N+1, b.getCapacity() );
        assertEquals( 0, b.size() );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.DelayFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        int N = (int) (Math.random() * 10) + 1;
        DelayFunction<Double> instance = new DelayFunction<Double>( N );

        FiniteCapacityBuffer<Double> b = new FiniteCapacityBuffer<Double>( N + 1 );
        for( int i = 0; i < (10*N); i++ )
        {
            double v = Math.random();
            b.addLast( v );
            double e = b.iterator().next();
            
            assertEquals( e, instance.evaluate( v ) );
        }
        
        instance = new DelayFunction<Double>( 0 );
        for( int i = 0; i < 10; i++ )
        {
            double v = Math.random();
            assertEquals( v, instance.evaluate( v ) );
        }

    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.DelayFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        int N = (int) (Math.random() * 10) + 1;
        DelayFunction<?> instance = new DelayFunction<Double>( N );
        DelayFunction<?> clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getDelaySamples(), clone.getDelaySamples() );
        assertNotSame( instance.getState(), clone.getState() );
        assertEquals( ((FiniteCapacityBuffer) instance.getState()).getCapacity(), ((FiniteCapacityBuffer) clone.getState()).getCapacity() );

    }
    
}
