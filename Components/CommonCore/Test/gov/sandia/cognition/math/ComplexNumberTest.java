/*
 * File:                ComplexNumberTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Implements tests for ComplexNumber.
 *
 * @author Kevin R. Dixon
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-19",
            changesNeeded=false,
            comments={
                "Added test for clone().",
                "Otherwise, looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments={
                "Added proper file header.",
                "Added documentation for a few functions."
            }
        )
    }
)
public class ComplexNumberTest
    extends RingTestHarness<ComplexNumber>
{
    /**
     * Creates a new instance of ComplexNumberTest.
     *
     * @param testName The name of the test.
     */
    public ComplexNumberTest(String testName)
    {
        super(testName);
    }

    /**
     * Returns the test.
     * @return Stuff
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(ComplexNumberTest.class);
        
        return suite;
    }

    
    protected ComplexNumber createRandom()
    {
        double real = RANDOM.nextGaussian();
        double imag = RANDOM.nextGaussian();
        return new ComplexNumber( real, imag );
    }

    /**
     * Test of clone method
     */
    @Override
    public void testClone()
    {
        System.out.println( "Clone" );

        super.testClone();

        ComplexNumber c1 = createRandom();
        ComplexNumber clone = c1.clone();
        assertNotNull( clone );
        assertNotSame( c1, clone );
        assertEquals( c1, clone );

        c1.scaleEquals( RANDOM.nextGaussian() );
        assertFalse( c1.equals( clone ) );

        ComplexNumber c2 = new ComplexNumber( c1 );
        assertNotSame( c1, c2 );
        assertEquals( c1, c2 );
        c2.scaleEquals( RANDOM.nextGaussian() );
        assertFalse( c1.equals( c2 ) );
    }

    /**
     * Test of plusEquals method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testPlusEquals()
    {     
        System.out.println("plusEquals");
        
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber object1 = new ComplexNumber( 10.0, -3.0 );
        
        ComplexNumber object2 = new ComplexNumber( -2.0, 20 );
        
        ComplexNumber expected = new ComplexNumber( 8, 17 );
        
        ComplexNumber result = object1.clone();
        
        result.plusEquals( object2 );
        
        assertEquals( expected, result );        
    }

    /**
     * Test of getRealPart method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testGetRealPart()
    {
        System.out.println("getRealPart");
        
        double realPart = RANDOM.nextGaussian();
        ComplexNumber instance = new ComplexNumber( realPart, -1.0 );
        
        assertEquals( realPart, instance.getRealPart() );
    }

    /**
     * Test of setRealPart method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testSetRealPart()
    {
        
        System.out.println("setRealPart");
        
        double realPart1 = RANDOM.nextGaussian();
        double realPart2 = RANDOM.nextGaussian();
        ComplexNumber instance = new ComplexNumber( realPart1, -1.0 );
        assertEquals( realPart1, instance.getRealPart() );
        
        instance.setRealPart( realPart2 );
        assertEquals( realPart2, instance.getRealPart() );
        
    }

    /**
     * Test of getImaginaryPart method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testGetImaginaryPart()
    {
        System.out.println("getImaginaryPart");
        
        double i = RANDOM.nextGaussian();
        ComplexNumber instance = new ComplexNumber( 1.0, i );
        assertEquals( i, instance.getImaginaryPart() );
    }

    /**
     * Test of setImaginaryPart method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testSetImaginaryPart()
    {
        System.out.println("setImaginaryPart");
        
        double i1 = RANDOM.nextGaussian();
        double i2 = RANDOM.nextGaussian();
        ComplexNumber instance = new ComplexNumber( 12, i1 );
        assertEquals( i1, instance.getImaginaryPart() );
        
        instance.setImaginaryPart( i2 );
        assertEquals( i2, instance.getImaginaryPart() );
    }

    /**
     * Test of getMagnitude method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testGetMagnitude()
    {
        System.out.println("getMagnitude");
        
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( 3.0, -4.0 );
        double m1 = 5.0;
        assertEquals( m1, c1.getMagnitude() );
        
        ComplexNumber c2 = new ComplexNumber( -6, 8 );
        double m2 = 10.0;
        assertEquals( m2, c2.getMagnitude() );
        
    }

    /**
     * Test of getPhase method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testGetPhase()
    {
        System.out.println("getPhase");
        
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( 2.0, 2.0 );
        double a1 = Math.PI / 4.0;
        assertEquals( a1, c1.getPhase() );
        
        ComplexNumber c2 = new ComplexNumber( 0.0, -10.0 );
        double a2 = -Math.PI / 2.0;
        assertEquals( a2, c2.getPhase() );
    }

    /**
     * Test of computeExponent method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testComputeExponent()
    {
        System.out.println("computeExponent");
        
        // check that the function returns what MATLAB told us the answer is
        double r = 2.0;
        ComplexNumber i1 = new ComplexNumber( r, -r );
        ComplexNumber copy = i1.clone();
        ComplexNumber result = i1.computeExponent();

        // Make sure we don't change "this" when calling the function
        assertEquals( copy, i1 );
        
        ComplexNumber expected = new ComplexNumber( -3.0749, -6.7188 );
        assertTrue( expected.equals( result, 0.0001 ) );
              
    }

    /**
     * Test of computeNaturalLogarithm method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testComputeNaturalLogarithm()
    {
        System.out.println("computeNaturalLogarithm");
        
        // check that the function returns what MATLAB told us the answer is
        double r = 1.5;
        ComplexNumber i1 = new ComplexNumber( -r, r );
        ComplexNumber copy = i1.clone();
        ComplexNumber result = i1.computeNaturalLogarithm();

        // Make sure we don't change "this" when calling the function
        assertEquals( copy, i1 );
        
        ComplexNumber expected = new ComplexNumber( 0.75204, 2.35619 );
        System.out.println( "Expected: " + expected );
        System.out.println( "Result: " + result );
        assertTrue( expected.equals( result, 0.0001 ) );
    }

    /**
     * Test of dotTimesEquals method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testDotTimesEquals()
    {
        System.out.println("dotTimesEquals");
        
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( 4.0, 5.0 );
        ComplexNumber c2 = new ComplexNumber( -2.0, 1.2 );
        ComplexNumber expected = new ComplexNumber( -8.0, 6.0 );
        
        c1.dotTimesEquals( c2 );
        assertEquals( expected, c1 );
        
    }

    /**
     * Test of scaleEquals method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testScaleEquals()
    {
        System.out.println("scaleEquals");
        
        // check that the function returns what MATLAB told us the answer is
        double scaleFactor = -1.5;
        ComplexNumber c1 = new ComplexNumber( 3.0, -6.0 );
        ComplexNumber expected = new ComplexNumber( -4.5, 9.0 );
        c1.scaleEquals( scaleFactor );
        assertEquals( expected, c1 );
    }

    /**
     * Test of times method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testTimes()
    {

        System.out.println("times");
        
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( 2.0, -2.0 );
        ComplexNumber c2 = new ComplexNumber( 3.0, 4.0 );
        ComplexNumber expected = new ComplexNumber( 14, 2 );
        ComplexNumber copy = c1.clone();
        
        ComplexNumber result = c1.times( c2 );
        
        assertEquals( copy, c1 );
        
        assertTrue( expected.equals( result, 0.00001 ) );
    }

    /**
     * Test of timesEquals method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testTimesEquals()
    {
        System.out.println("timesEquals");
        
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( -3.0, 3.0 );
        ComplexNumber c2 = new ComplexNumber( 4.0, -5.0 );
        ComplexNumber expected = new ComplexNumber( 3, 27 );
        
        c1.timesEquals( c2 );
        assertTrue( expected.equals( c1, 0.00001 ) );
    }

    /**
     * Test of dividedBy method, of class gov.sandia.isrc.math.ComplexNumber.
     */
    public void testDividedBy()
    {
        System.out.println("dividedBy");
     
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( 10.0, -5.0 );
        ComplexNumber c2 = new ComplexNumber( -2.0, -3.0 );
        ComplexNumber expected = new ComplexNumber( -0.38462, 3.07692 );
        ComplexNumber copy = c1.clone();
        
        ComplexNumber result = c1.dividedBy( c2 );
        
        assertEquals( copy, c1 );
        
        assertTrue( expected.equals( result, 0.00001 ) );
    }

    
    /**
     * Test of dividedByEquals method, of class 
     * gov.sandia.isrc.math.ComplexNumber.
     */
    public void testDividedByEquals()
    { 
        System.out.println("dividedByEquals");
     
        // check that the function returns what MATLAB told us the answer is
        ComplexNumber c1 = new ComplexNumber( -5.0, 3.1 );
        ComplexNumber c2 = new ComplexNumber( 2.7, 1.4 );
        ComplexNumber expected = new ComplexNumber( -0.99027, 1.66162 );
        c1.dividedByEquals( c2 );
        
        assertTrue( expected.equals( c1, 0.00001 ) );
    }

    public void testConjugateEquals()
    {
        System.out.println( "conjugateEquals" );

        double r = RANDOM.nextGaussian();
        double i = RANDOM.nextGaussian();
        ComplexNumber c1 = new ComplexNumber( r, i );
        c1.conjugateEquals();
        assertEquals( r, c1.getRealPart() );
        assertEquals( -i, c1.getImaginaryPart() );

        c1.conjugateEquals();
        assertEquals( r, c1.getRealPart() );
        assertEquals( i, c1.getImaginaryPart() );

    }

    public void testConjugate()
    {
        System.out.println( "conjugate" );

        double r = RANDOM.nextGaussian();
        double i = RANDOM.nextGaussian();
        ComplexNumber c1 = new ComplexNumber( r, i );
        ComplexNumber c2 = c1.conjugate();
        assertEquals( r, c1.getRealPart() );
        assertEquals( i, c1.getImaginaryPart() );
        assertEquals( r, c2.getRealPart() );
        assertEquals(-i, c2.getImaginaryPart() );

        ComplexNumber c3 = c2.conjugate();
        assertEquals( c1, c3 );

    }
    
}
