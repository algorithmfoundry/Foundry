/*
 * File:                AutoRegressiveMovingAverageFilterTest.java
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
import gov.sandia.cognition.util.Pair;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for AutoRegressiveMovingAverageFilter.
 *
 * @author krdixon
 */
public class AutoRegressiveMovingAverageFilterTest
extends TestCase
{

    Random random = new Random( 1 );

    /**
     * Tests
     * @param testName Name of test.
     */
    public AutoRegressiveMovingAverageFilterTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );
        final int na = 2;
        final int nb = 3;
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( na, nb );
        assertEquals( na, instance.getNumAutoRegressiveCoefficients() );
        assertEquals( nb, instance.getNumMovingAverageCoefficients() );
        for( int n = 0; n < na; n++ )
        {
            assertEquals( 1.0/na, instance.getAutoRegressiveCoefficients().getElement( n ) );
        }
        for( int n = 0; n < nb; n++ )
        {
            assertEquals( 1.0/nb, instance.getMovingAverageCoefficients().getElement( n ) );
        }

        double[] a = { random.nextGaussian(), random.nextGaussian(), random.nextGaussian() };
        double[] b = { random.nextGaussian(), random.nextGaussian() };
        instance = new AutoRegressiveMovingAverageFilter( a, b );
        for( int n = 0; n < a.length; n++ )
        {
            assertEquals( a[n], instance.getAutoRegressiveCoefficients().getElement( n ) );
        }
        for( int n = 0; n < b.length; n++ )
        {
            assertEquals( b[n], instance.getMovingAverageCoefficients().getElement( n ) );
        }

    }

    /**
     * Test of createDefaultState method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testCreateDefaultState()
    {
        System.out.println( "createDefaultState" );
        final int na = 2;
        final int nb = 3;
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( na, nb );
        Pair<FiniteCapacityBuffer<Double>, FiniteCapacityBuffer<Double>> result = instance.createDefaultState();
        assertNotNull( result );
        assertEquals( nb, result.getFirst().getCapacity() );
        assertEquals( na, result.getSecond().getCapacity() );
    }

    /**
     * Test of evaluate method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        double[] a = { -1.0, 2.0 };
        double[] b = { 3.0, -2.0, 1.0 };
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        double x0 = 2.0;
        double y0 = b[0]*x0;
        assertEquals( y0, instance.evaluate( x0 ) );

        double x1 = -1.0;
        double y1 = b[0]*x1 + b[1]*x0 - a[0]*y0;
        assertEquals( y1, instance.evaluate( x1 ) );

        double x2 = 3.0;
        double y2 = b[0]*x2 + b[1]*x1 + b[2]*x0 - a[0]*y1 - a[1]*y0;
        assertEquals( y2, instance.evaluate( x2 ) );

        double x3 = -2.0;
        double y3 = b[0]*x3 + b[1]*x2 + b[2]*x1 - a[0]*y2 - a[1]*y1;
        assertEquals( y3, instance.evaluate( x3 ) );

    }

    /**
     * Test of clone method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        double[] a = { -1.0, 2.0 };
        double[] b = { 3.0, -2.0, 1.0 };
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        AutoRegressiveMovingAverageFilter clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getState(), clone.getState() );
        assertNotSame( instance.getAutoRegressiveCoefficients(), clone.getAutoRegressiveCoefficients() );
        assertEquals( instance.getAutoRegressiveCoefficients(), clone.getAutoRegressiveCoefficients() );
        assertNotSame( instance.getMovingAverageCoefficients(), clone.getMovingAverageCoefficients() );
        assertEquals( instance.getMovingAverageCoefficients(), clone.getMovingAverageCoefficients() );
    }

    /**
     * Test of convertToVector method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        Vector result = instance.convertToVector();

        int M = a.getDimensionality();
        int N = b.getDimensionality();
        assertEquals( M+N, result.getDimensionality() );

        for( int i = 0; i < M; i++ )
        {
            assertEquals( a.getElement( i ), result.getElement( i ) );
        }
        for( int i = 0; i < N; i++ )
        {
            assertEquals( b.getElement( i ), result.getElement( i + M ) );
        }

    }

    /**
     * Test of convertFromVector method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testConvertFromVector()
    {
        System.out.println( "convertFromVector" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        Vector result = instance.convertToVector();

        Vector r2 = result.scale( random.nextGaussian() );
        instance.convertFromVector( r2 );
        Vector r2hat = instance.convertToVector();
        assertNotSame( r2, r2hat );
        assertEquals( r2, r2hat );

        try
        {
            instance.convertFromVector( a.stack( a ) );
            fail( "Wrong dimension!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getNumMovingAverageCoefficients method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testGetNumMovingAverageCoefficients()
    {
        System.out.println( "getNumMovingAverageCoefficients" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        assertSame( b.getDimensionality(), instance.getNumMovingAverageCoefficients() );

        instance = new AutoRegressiveMovingAverageFilter( (Vector) null, (Vector) null );
        assertEquals( 0, instance.getNumMovingAverageCoefficients() );

    }

    /**
     * Test of getMovingAverageCoefficients method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testGetMovingAverageCoefficients()
    {
        System.out.println( "getMovingAverageCoefficients" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        assertSame( b, instance.getMovingAverageCoefficients() );
    }

    /**
     * Test of setMovingAverageCoefficients method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testSetMovingAverageCoefficients()
    {
        System.out.println( "setMovingAverageCoefficients" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        instance.setMovingAverageCoefficients( null );
        assertNull( instance.getMovingAverageCoefficients() );
        instance.setMovingAverageCoefficients( b );
        assertSame( b, instance.getMovingAverageCoefficients() );
    }

    /**
     * Test of getNumAutoRegressiveCoefficients method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testGetNumAutoRegressiveCoefficients()
    {
        System.out.println( "getNumAutoRegressiveCoefficients" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        assertEquals( a.getDimensionality(), instance.getNumAutoRegressiveCoefficients() );

        instance = new AutoRegressiveMovingAverageFilter( (Vector) null, (Vector) null );
        assertEquals( 0, instance.getNumAutoRegressiveCoefficients() );

    }

    /**
     * Test of getAutoRegressiveCoefficients method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testGetAutoRegressiveCoefficients()
    {
        System.out.println( "getAutoRegressiveCoefficients" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        assertSame( a, instance.getAutoRegressiveCoefficients() );
    }

    /**
     * Test of setAutoregressiveCoefficients method, of class AutoRegressiveMovingAverageFilter.
     */
    public void testSetAutoregressiveCoefficients()
    {
        System.out.println( "setAutoregressiveCoefficients" );
        Vector a = VectorFactory.getDefault().copyValues( -1.0, 2.0 );
        Vector b = VectorFactory.getDefault().copyValues( 3.0, -2.0, 1.0 );
        AutoRegressiveMovingAverageFilter instance =
            new AutoRegressiveMovingAverageFilter( a, b );
        instance.setAutoregressiveCoefficients( null );
        assertNull( instance.getAutoRegressiveCoefficients() );
        instance.setAutoregressiveCoefficients( a );
        assertSame( a, instance.getAutoRegressiveCoefficients() );
    }

}
