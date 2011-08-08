/*
 * File:                MovingAverageFilterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for {@name}.
 *
 * @author krdixon
 */
public class MovingAverageFilterTest
extends TestCase
{

    Random random = new Random( 1 );
    
    public MovingAverageFilterTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        final int num = 4;
        MovingAverageFilter f = new MovingAverageFilter( num );
        assertEquals( num, f.getNumMovingAverageCoefficients() );
        for( int i = 0; i < num; i++ )
        {
            assertEquals( 1.0/num, f.getMovingAverageCoefficients().getElement( i ) );
        }

        final double[] c2 = { random.nextGaussian(), random.nextGaussian(), random.nextGaussian() };
        MovingAverageFilter f2 = new MovingAverageFilter( c2 );
        assertEquals( c2.length, f2.getNumMovingAverageCoefficients() );
        for( int i = 0; i < c2.length; i++ )
        {
            assertEquals( c2[i], f2.getMovingAverageCoefficients().getElement( i ) );
        }

        final Vector v3 = null;
        MovingAverageFilter f3 = new MovingAverageFilter( v3 );
        assertNull( f3.getMovingAverageCoefficients() );
        assertEquals( 0, f3.getNumMovingAverageCoefficients() );

    }

    /**
     * Test of createDefaultState method, of class MovingAverageFilter.
     */
    public void testCreateDefaultState()
    {
        System.out.println( "createDefaultState" );
        final int num = 2;
        MovingAverageFilter instance = new MovingAverageFilter( num );
        FiniteCapacityBuffer<Double> result = instance.createDefaultState();
        assertEquals( num, result.getCapacity() );
        assertEquals( 0, result.size() );
    }

    /**
     * Test of evaluate method, of class MovingAverageFilter.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        final double[] c = { -1.0, 2.0, 3.0, -4.0 };
        MovingAverageFilter f = new MovingAverageFilter( c );
        assertEquals( -2.0, f.evaluate( 2.0 ) );
        assertEquals( -1.0+ 4.0, f.evaluate( 1.0 ) );
        assertEquals( -5.0+ 2.0+ 6.0, f.evaluate( 5.0 ) );
        assertEquals(  1.0+10.0+ 3.0- 8.0, f.evaluate( -1.0 ) );
        assertEquals(  2.0- 2.0+15.0- 4.0, f.evaluate( -2.0 ) );
        assertEquals( -3.0- 4.0- 3.0-20.0, f.evaluate(  3.0 ) );

    }

    /**
     * Test of clone method, of class MovingAverageFilter.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        MovingAverageFilter instance =
            new MovingAverageFilter( random.nextInt( 10 ) + 1 );
        MovingAverageFilter clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getState(), clone.getState() );
        assertNotSame( instance.getMovingAverageCoefficients(), clone.getMovingAverageCoefficients() );
        assertEquals( instance.getMovingAverageCoefficients(), clone.getMovingAverageCoefficients() );
    }

    /**
     * Test of convertToVector method, of class MovingAverageFilter.
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );

        final double[] c = { random.nextGaussian(), random.nextGaussian(), random.nextGaussian() };
        MovingAverageFilter instance = new MovingAverageFilter( c );
        assertEquals( c.length, instance.convertToVector().getDimensionality() );
        assertEquals( VectorFactory.getDefault().copyArray( c ), instance.convertToVector() );
        assertSame( instance.getMovingAverageCoefficients(), instance.convertToVector() );
    }

    /**
     * Test of convertFromVector method, of class MovingAverageFilter.
     */
    public void testConvertFromVector()
    {
        System.out.println( "convertFromVector" );
        final double[] c = { random.nextGaussian(), random.nextGaussian(), random.nextGaussian() };
        Vector v = VectorFactory.getDefault().copyArray( c );
        MovingAverageFilter instance = new MovingAverageFilter( c.length );
        assertFalse( v.equals( instance.convertToVector() ) );
        instance.convertFromVector( v );
        assertSame( v, instance.convertToVector() );

        try
        {
            instance.convertFromVector( VectorFactory.getDefault().createVector( 10 ) );
            fail( "Cannot convert from different sized Vector" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getNumMovingAverageCoefficients method, of class MovingAverageFilter.
     */
    public void testGetNumMovingAverageCoefficients()
    {
        System.out.println( "getNumMovingAverageCoefficients" );
        final int num = 10;
        MovingAverageFilter instance = new MovingAverageFilter( num );
        assertEquals( num, instance.getNumMovingAverageCoefficients() );
        assertEquals( instance.getNumMovingAverageCoefficients(), instance.getMovingAverageCoefficients().getDimensionality() );
        assertEquals( instance.getNumMovingAverageCoefficients(), instance.getState().getCapacity() );
    }

    /**
     * Test of getMovingAverageCoefficients method, of class MovingAverageFilter.
     */
    public void testGetMovingAverageCoefficients()
    {
        System.out.println( "getMovingAverageCoefficients" );
        final int num = 10;
        MovingAverageFilter instance = new MovingAverageFilter( num );
        assertEquals( num, instance.getMovingAverageCoefficients().getDimensionality() );
    }

    /**
     * Test of setMovingAverageCoefficients method, of class MovingAverageFilter.
     */
    public void testSetMovingAverageCoefficients()
    {
        System.out.println( "setMovingAverageCoefficients" );
        final int num = 10;
        MovingAverageFilter instance = new MovingAverageFilter( num );
        Vector v = instance.getMovingAverageCoefficients();
        assertNotNull( v );
        instance.setMovingAverageCoefficients( null );
        assertNull( instance.getMovingAverageCoefficients() );
        instance.setMovingAverageCoefficients( v );
        assertSame( v, instance.getMovingAverageCoefficients() );
    }

}
