/*
 * File:                FourierTransformTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 8, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for FourierTransformTest.
 *
 * @author krdixon
 */
public class FourierTransformTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Tests for class FourierTransformTest.
     * @param testName Name of the test.
     */
    public FourierTransformTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class FourierTransformTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        FourierTransform fft = new FourierTransform();
        assertNotNull( fft );
    }

    public void testKnown1()
    {
        System.out.println( "DFT" );

        ArrayList<Double> x = new ArrayList<Double>( Arrays.asList(
            1.0, 2.0, -1.0 ) );

        ComplexNumber[] y = FourierTransform.discreteFourierTransform(x);
        System.out.println( "DFT: " + ObjectUtil.toString(y) );

        // I calculated these by hand, using the definition of the DFT
        assertTrue( y[0].equals( new ComplexNumber( 2.0, 0.0 ), EPS ) );
        assertTrue( y[1].equals( new ComplexNumber( 0.5, -2.598076 ), EPS ) );
        assertTrue( y[2].equals( new ComplexNumber( 0.5,  2.598076 ), EPS ) );

    }

    /**
     * Test of evaluate method, of class FourierTransform.
     */
    public void testEvaluate1()
    {
        System.out.println("evaluate1");
        FourierTransform fft = new FourierTransform();

        ArrayList<Double> x1 = new ArrayList<Double>( Arrays.asList(
            1.0, 0.0, 1.0, 0.0 ) );
        List<ComplexNumber> y1 = fft.evaluate(x1);
        assertEquals( x1.size(), y1.size() );
        assertEquals( new ComplexNumber( 2.0, 0.0 ), y1.get(0) );
        assertEquals( new ComplexNumber( 0.0, 0.0 ), y1.get(1) );
        assertEquals( new ComplexNumber( 2.0, 0.0 ), y1.get(2) );
        assertEquals( new ComplexNumber( 0.0, 0.0 ), y1.get(3) );

        ComplexNumber[] ydft = FourierTransform.discreteFourierTransform(x1);
        assertEquals( ydft.length, y1.size() );
        for( int i = 0; i < ydft.length; i++ )
        {
            assertTrue( ydft[i].equals( y1.get(i), EPS ) );
        }
    }

    /**
     * Test of evaluate method, of class FourierTransform.
     */
    public void testEvaluate2()
    {
        System.out.println("evaluate2");
        FourierTransform fft = new FourierTransform();

        ArrayList<Double> x = new ArrayList<Double>( Arrays.asList(
            1.0, 0.0, 0.0, 1.0 ) );
        List<ComplexNumber> y = fft.evaluate(x);
        assertEquals( x.size(), y.size() );
        assertTrue( y.get(0).equals( new ComplexNumber( 2.0, 0.0 ), EPS ) );
        assertTrue( y.get(1).equals( new ComplexNumber( 1.0, 1.0 ), EPS ) );
        assertTrue( y.get(2).equals( new ComplexNumber( 0.0, 0.0 ), EPS ) );
        assertTrue( y.get(3).equals( new ComplexNumber( 1.0,-1.0 ), EPS ) );

        ComplexNumber[] ydft = FourierTransform.discreteFourierTransform(x);
        assertEquals( ydft.length, y.size() );
        for( int i = 0; i < ydft.length; i++ )
        {
            assertTrue( ydft[i].equals( y.get(i), EPS ) );
        }

    }

    /**
     * Test of evaluate method, of class FourierTransform.
     */
    public void testEvaluate3()
    {
        System.out.println("evaluate3");
        FourierTransform fft = new FourierTransform();

        ArrayList<Double> x = new ArrayList<Double>( Arrays.asList(
            1.0, 0.0, 0.0, -2.0, 0.0 ) );
        List<ComplexNumber> y = fft.evaluate(x);

        ComplexNumber[] ydft = FourierTransform.discreteFourierTransform(x);
        assertEquals( ydft.length, y.size() );
        for( int i = 0; i < ydft.length; i++ )
        {
            assertTrue( ydft[i].equals( y.get(i), EPS ) );
        }

        assertEquals( x.size(), y.size() );
        assertTrue( y.get(0).equals( new ComplexNumber( -1.0, 0.0 ), EPS ) );
        assertTrue( y.get(1).equals( new ComplexNumber( 2.618033988749895, -1.175570504584946 ), EPS ) );
        assertTrue( y.get(2).equals( new ComplexNumber( 0.381966011250104,  1.902113032590307 ), EPS ) );
        assertTrue( y.get(3).equals( new ComplexNumber( 0.381966011250104, -1.902113032590307 ), EPS ) );
        assertTrue( y.get(4).equals( new ComplexNumber( 2.618033988749895,  1.175570504584946 ), EPS ) );

    }

    /**
     * Test of evaluate method, of class FourierTransform.
     */
    public void testEvaluate4()
    {
        System.out.println("evaluate3");
        FourierTransform fft = new FourierTransform();

        ArrayList<Double> x = new ArrayList<Double>( Arrays.asList(
            1.0, 2.0, 3.0, -1.0, -2.0, -3.0, 0.0, 1.0 ) );
        List<ComplexNumber> y = fft.evaluate(x);

        ComplexNumber[] ydft = FourierTransform.discreteFourierTransform(x);
        assertEquals( ydft.length, y.size() );
        for( int i = 0; i < ydft.length; i++ )
        {
            assertTrue( ydft[i].equals( y.get(i), EPS ) );
        }

        assertEquals( x.size(), y.size() );
        assertTrue( y.get(0).equals( new ComplexNumber( 1.0, 0.0 ), EPS ) );
        assertTrue( y.get(1).equals( new ComplexNumber( 7.949747468305832, -5.121320343559642), EPS ) );
        assertTrue( y.get(2).equals( new ComplexNumber(-4.0, 1.0 ), EPS ) );
        assertTrue( y.get(3).equals( new ComplexNumber(-1.949747468305830,  0.878679656440355), EPS ) );
        assertTrue( y.get(4).equals( new ComplexNumber( 3.0, 0.0 ), EPS ) );
        assertTrue( y.get(5).equals( new ComplexNumber(-1.949747468305830, -0.878679656440355), EPS ) );
        assertTrue( y.get(6).equals( new ComplexNumber(-4.0, -1.0 ), EPS ) );
        assertTrue( y.get(7).equals( new ComplexNumber( 7.949747468305832,  5.121320343559642), EPS ) );
    }


    /**
     * Test of inverse method
     */
    public void testInverse1()
    {
        System.out.println( "inverse1" );

        ArrayList<Double> x = new ArrayList<Double>( Arrays.asList(
            1.0, 2.0, 3.0, -1.0, -2.0, -3.0, 0.0, 1.0 ) );

        FourierTransform fft = new FourierTransform();
        List<ComplexNumber> xfft = fft.evaluate(x);
        ArrayList<Double> xhat = FourierTransform.inverse(xfft);
        assertEquals( xfft.size(), xhat.size() );
        assertEquals( x.size(), xhat.size() );
        assertEquals( x.size(), xhat.size() );
        for( int i = 0; i < xhat.size(); i++ )
        {
            assertEquals( x.get(i), xhat.get(i), EPS );
        }

    }

    /**
     * Test of evaluate method, of class FourierTransform.
     */
    public void testInverse2()
    {
        System.out.println("inverse2");
        FourierTransform fft = new FourierTransform();

        ArrayList<Double> x = new ArrayList<Double>( Arrays.asList(
            1.0, 0.0, 0.0, -2.0, 0.0 ) );
        List<ComplexNumber> xfft = fft.evaluate(x);
        FourierTransform.Inverse ifft = new FourierTransform.Inverse();
        ArrayList<Double> xhat = ifft.evaluate(xfft);
        assertEquals( xfft.size(), xhat.size() );
        assertEquals( x.size(), xhat.size() );
        for( int i = 0; i < xhat.size(); i++ )
        {
            assertEquals( x.get(i), xhat.get(i), EPS );
        }

    }

    public void testInverseRandom()
    {

        System.out.println( "Inverse Random" );

        FourierTransform fft = new FourierTransform();
        FourierTransform.Inverse ifft = new FourierTransform.Inverse();

        int num = random.nextInt(100) + 100;
        ArrayList<Double> x = new ArrayList<Double>( num );
        for( int i = 0; i < num; i++ )
        {
            x.add( random.nextGaussian() );
        }

        List<ComplexNumber> xfft = fft.evaluate(x);
        ArrayList<Double> xhat = ifft.evaluate(xfft);
        assertEquals( xfft.size(), xhat.size() );
        assertEquals( x.size(), xhat.size() );

        for( int i = 0; i < num; i++ )
        {
            assertEquals( x.get(i), xhat.get(i), EPS );
        }

    }

}
