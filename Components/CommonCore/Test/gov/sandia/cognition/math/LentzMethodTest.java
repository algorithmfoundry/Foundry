/*
 * File:                LentzMethodTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.math;

import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class LentzMethodTest
 * @author Kevin R. Dixon
 */
public class LentzMethodTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class LentzMethodTest
     * @param testName name of this test
     */
    public LentzMethodTest(
        String testName)
    {
        super(testName);
    }

    Random random = new Random(1);
    public void testTan()
    {
        System.out.println( "Testing tan" );
        
        for( int i = 0; i < 100; i++ )
        {
            double x = random.nextDouble();

            LentzMethod lentz = new LentzMethod();
            lentz.initializeAlgorithm( 0.0 );
            while( lentz.getKeepGoing() )
            {
                int j = lentz.getIteration();
                double b = (j*2)+1;
                double a = (j<1) ? x : (-x*x);
                lentz.iterate(a, b);
            }

            assertEquals( Math.tan(x), lentz.getResult(), lentz.getTolerance() );
        }
    }
    
    /**
     * Test of initializeAlgorithm method, of class LentzMethod.
     */
    public void testInitializeAlgorithm()
    {
        System.out.println( "initializeAlgorithm" );
        double b0 = random.nextDouble();
        LentzMethod instance = new LentzMethod();
        
        boolean result = instance.initializeAlgorithm( b0 );
        
        assertTrue( result );
        assertNull( instance.getResult() );
        assertEquals( 0, instance.getIteration() );
        assertEquals( b0, instance.getFractionValue() );
        assertTrue( instance.getKeepGoing() );
    }

    /**
     * Test of iterate method, of class LentzMethod.
     */
    public void testIterate()
    {
        System.out.println( "iterate" );
        double a = 1.0;
        double b = 1.0;
        LentzMethod instance = new LentzMethod();
        assertNull( instance.getResult() );
        instance.initializeAlgorithm(b);
        while( instance.getKeepGoing() )
        {
            instance.iterate( a, b );
        }
        
        assertTrue( instance.isResultValid() );
        assertNotNull( instance.getResult() );
        
        try
        {
            instance.iterate(a, b);
            fail( "Should throw exception after keepGoing == false" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of getMaxIterations method, of class LentzMethod.
     */
    public void testGetMaxIterations()
    {
        System.out.println( "getMaxIterations" );
        LentzMethod instance = new LentzMethod();
        assertEquals( LentzMethod.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
    }

    /**
     * Test of setMaxIterations method, of class LentzMethod.
     */
    public void testSetMaxIterations()
    {
        System.out.println( "setMaxIterations" );
        int maxIterations = 1;
        LentzMethod instance = new LentzMethod();
        assertEquals( LentzMethod.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
        
        instance.setMaxIterations( maxIterations );
        assertEquals( maxIterations, instance.getMaxIterations() );
    }

    /**
     * Test of getTolerance method, of class LentzMethod.
     */
    public void testGetTolerance()
    {
        System.out.println( "getTolerance" );
        LentzMethod instance = new LentzMethod();
        assertEquals( LentzMethod.DEFAULT_TOLERANCE, instance.getTolerance() );
    }

    /**
     * Test of setTolerance method, of class LentzMethod.
     */
    public void testSetTolerance()
    {
        System.out.println( "setTolerance" );
        double tolerance = 0.0;
        LentzMethod instance = new LentzMethod();
        assertEquals( LentzMethod.DEFAULT_TOLERANCE, instance.getTolerance() );
        instance.setTolerance(tolerance);
        assertEquals( tolerance, instance.getTolerance() );
    }

    /**
     * Test of getMinDenominator method, of class LentzMethod.
     */
    public void testGetMinDenominator()
    {
        System.out.println( "getMinDenominator" );
        LentzMethod instance = new LentzMethod();
        assertEquals( LentzMethod.DEFAULT_MIN_DENOMINATOR, instance.getMinDenominator() );
    }

    /**
     * Test of setMinDenominator method, of class LentzMethod.
     */
    public void testSetMinDenominator()
    {
        System.out.println( "setMinDenominator" );
        double minDenominator = 0.0;
        LentzMethod instance = new LentzMethod();
        assertEquals( LentzMethod.DEFAULT_MIN_DENOMINATOR, instance.getMinDenominator() );
        instance.setMinDenominator(minDenominator);
        assertEquals( minDenominator, instance.getMinDenominator() );
    }

    /**
     * Test of getResult method, of class LentzMethod.
     */
    public void testGetResult()
    {
        System.out.println( "getResult" );
        LentzMethod instance = new LentzMethod();
        assertNull( instance.getResult() );
    }

    /**
     * Test of stop method, of class LentzMethod.
     */
    public void testStop()
    {
        System.out.println( "stop" );
        LentzMethod instance = new LentzMethod();
        instance.initializeAlgorithm(0.0);
        assertTrue( instance.getKeepGoing() );
        instance.stop();
        assertFalse( instance.getKeepGoing() );
    }

    /**
     * Test of isResultValid method, of class LentzMethod.
     */
    public void testIsResultValid()
    {
        System.out.println( "isResultValid" );
        LentzMethod instance = new LentzMethod();
        assertFalse( instance.isResultValid() );
        instance.initializeAlgorithm(0.0);
        assertFalse( instance.isResultValid() );
    }

    /**
     * Test of getKeepGoing method, of class LentzMethod.
     */
    public void testGetKeepGoing()
    {
        System.out.println( "getKeepGoing" );
        LentzMethod instance = new LentzMethod();
        assertFalse( instance.getKeepGoing() );
        instance.initializeAlgorithm(0.0);
        assertTrue( instance.getKeepGoing() );
    }

    /**
     * Test of getFractionValue method, of class LentzMethod.
     */
    public void testGetFractionValue()
    {
        System.out.println( "getFractionValue" );
        LentzMethod instance = new LentzMethod();
        assertEquals( 0.0, instance.getFractionValue() );
    }

    /**
     * Test of setFractionValue method, of class LentzMethod.
     */
    public void testSetFractionValue()
    {
        System.out.println( "setFractionValue" );
        double fractionValue = random.nextDouble();
        LentzMethod instance = new LentzMethod();
        assertEquals( 0.0, instance.getFractionValue() );
        
        instance.setFractionValue(fractionValue);
        assertEquals( fractionValue, instance.getFractionValue() );
    }

}
