/*
 * File:                LineBracketTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class LineBracketTest
 * @author Kevin R. Dixon
 */
public class LineBracketTest
    extends TestCase
{

    Random random = new Random( 0 );
    
    /**
     * Entry point for JUnit tests for class LineBracketTest
     * @param testName name of this test
     */
    public LineBracketTest(
        String testName)
    {
        super(testName);
    }
    
    public InputOutputSlopeTriplet createTriplet()
    {
        final double range = 10.0;
        return new InputOutputSlopeTriplet(
            range*(random.nextDouble() * 2.0 - 1.0),
            range*(random.nextDouble() * 2.0 - 1.0),
            range*(random.nextDouble() * 2.0 - 1.0) );
    }
    
    
    public LineBracket createInstance()
    {
        return new LineBracket( 
            this.createTriplet(), this.createTriplet(), this.createTriplet() );
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );
        LineBracket instance = this.createInstance();
        assertNotNull( instance.getLowerBound() );
        assertNotNull( instance.getUpperBound() );
        assertNotNull( instance.getOtherPoint() );
        
        LineBracket b2 = new LineBracket( instance );
        assertNotNull( b2.getLowerBound() );
        assertNotNull( b2.getUpperBound() );
        assertNotNull( b2.getOtherPoint() );
        assertNotSame( instance.getLowerBound(), b2.getLowerBound() );
        assertNotSame( instance.getUpperBound(), b2.getUpperBound() );
        assertNotSame( instance.getOtherPoint(), b2.getOtherPoint() );

    }
    
    /**
     * Test of clone method, of class LineBracket.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        LineBracket instance = this.createInstance();
        LineBracket b2 = instance.clone();
        assertNotNull( b2 );
        assertNotSame( instance, b2 );
        assertNotNull( b2.getLowerBound() );
        assertNotNull( b2.getUpperBound() );
        assertNotNull( b2.getOtherPoint() );
        assertNotSame( instance.getLowerBound(), b2.getLowerBound() );
        assertNotSame( instance.getUpperBound(), b2.getUpperBound() );
        assertNotSame( instance.getOtherPoint(), b2.getOtherPoint() );
    }

    /**
     * Test of toString method, of class LineBracket.
     */
    public void testToString()
    {
        System.out.println( "toString" );
        LineBracket instance = this.createInstance();
        System.out.println( "instance: " + instance );
        
    }

    /**
     * Test of getLowerBound method, of class LineBracket.
     */
    public void testGetLowerBound()
    {
        System.out.println( "getLowerBound" );
        InputOutputSlopeTriplet a = this.createTriplet();
        InputOutputSlopeTriplet b = this.createTriplet();
        InputOutputSlopeTriplet c = this.createTriplet();
        LineBracket instance = new LineBracket( a, b, c );
        assertSame( a, instance.getLowerBound() );
    }

    /**
     * Test of setLowerBound method, of class LineBracket.
     */
    public void testSetLowerBound()
    {
        System.out.println( "setLowerBound" );
        InputOutputSlopeTriplet a = this.createTriplet();
        InputOutputSlopeTriplet b = this.createTriplet();
        InputOutputSlopeTriplet c = this.createTriplet();
        LineBracket instance = new LineBracket( a, b, c );
        assertSame( a, instance.getLowerBound() );
        
        instance.setLowerBound( null );
        assertNull( instance.getLowerBound() );
        instance.setLowerBound( b );
        assertSame( b, instance.getLowerBound() );

    }

    /**
     * Test of getUpperBound method, of class LineBracket.
     */
    public void testGetUpperBound()
    {
        System.out.println( "getUpperBound" );
        InputOutputSlopeTriplet a = this.createTriplet();
        InputOutputSlopeTriplet b = this.createTriplet();
        InputOutputSlopeTriplet c = this.createTriplet();
        LineBracket instance = new LineBracket( a, b, c );
        assertSame( b, instance.getUpperBound() );

        
    }

    /**
     * Test of setUpperBound method, of class LineBracket.
     */
    public void testSetUpperBound()
    {
        System.out.println( "setUpperBound" );
        InputOutputSlopeTriplet a = this.createTriplet();
        InputOutputSlopeTriplet b = this.createTriplet();
        InputOutputSlopeTriplet c = this.createTriplet();
        LineBracket instance = new LineBracket( a, b, c );
        assertSame( b, instance.getUpperBound() );
        
        instance.setUpperBound( null );
        assertNull( instance.getUpperBound() );
        instance.setUpperBound( c );
        assertSame( c, instance.getUpperBound() );
    }

    /**
     * Test of getOtherPoint method, of class LineBracket.
     */
    public void testGetOtherPoint()
    {
        System.out.println( "getOtherPoint" );
        InputOutputSlopeTriplet a = this.createTriplet();
        InputOutputSlopeTriplet b = this.createTriplet();
        InputOutputSlopeTriplet c = this.createTriplet();
        LineBracket instance = new LineBracket( a, b, c );
        assertSame( c, instance.getOtherPoint() );
    }

    /**
     * Test of setOtherPoint method, of class LineBracket.
     */
    public void testSetOtherPoint()
    {
        System.out.println( "setOtherPoint" );
        InputOutputSlopeTriplet a = this.createTriplet();
        InputOutputSlopeTriplet b = this.createTriplet();
        InputOutputSlopeTriplet c = this.createTriplet();
        LineBracket instance = new LineBracket( a, b, c );
        assertSame( c, instance.getOtherPoint() );
        
        instance.setOtherPoint( null );
        assertNull( instance.getOtherPoint() );
        instance.setOtherPoint( a );
        assertSame( a, instance.getOtherPoint() );
    }

}
