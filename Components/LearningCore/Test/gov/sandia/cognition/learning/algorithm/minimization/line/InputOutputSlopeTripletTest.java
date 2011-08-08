/*
 * File:                InputOutputSlopeTripletTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class InputOutputSlopeTripletTest
 * @author Kevin R. Dixon
 */
public class InputOutputSlopeTripletTest
    extends TestCase
{
    
    /**
     * random-number generator to use
     */
    Random r = new Random(1);

    /**
     * Entry point for JUnit tests for class InputOutputSlopeTripletTest
     * @param testName name of this test
     */
    public InputOutputSlopeTripletTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class InputOutputSlopeTriplet.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        InputOutputSlopeTriplet instance =
            new InputOutputSlopeTriplet( r.nextDouble(), r.nextDouble(), r.nextDouble() );
        InputOutputSlopeTriplet result = instance.clone();
        assertNotNull( result );
        assertNotSame( instance, result );
        assertEquals( instance.getInput(), result.getInput() );
        assertEquals( instance.getOutput(), result.getOutput() );
        assertEquals( instance.getSlope(), result.getSlope() );
    }

    /**
     * Test of getSlope method, of class InputOutputSlopeTriplet.
     */
    public void testGetSlope()
    {
        System.out.println( "getSlope" );
        Double x = new Double( r.nextDouble() );
        Double y = new Double( r.nextDouble() );
        Double slope = new Double( r.nextDouble() );
        InputOutputSlopeTriplet instance = new InputOutputSlopeTriplet( x, y, slope );
        
        assertSame( slope, instance.getSlope() );

        InputOutputSlopeTriplet instance2 = new InputOutputSlopeTriplet( x, y, null );
        assertNull( instance2.getSlope() );
        
    }

    /**
     * Test of setSlope method, of class InputOutputSlopeTriplet.
     */
    public void testSetSlope()
    {
        System.out.println( "setSlope" );
        Double x = new Double( r.nextDouble() );
        Double y = new Double( r.nextDouble() );
        Double slope = new Double( r.nextDouble() );
        InputOutputSlopeTriplet instance = new InputOutputSlopeTriplet( x, y, slope );
        
        assertSame( slope, instance.getSlope() );
        
        slope += 1.0;
        assertNotSame( slope, instance.getSlope() );
        
        instance.setSlope( slope );
        assertSame( slope, instance.getSlope() );
        
        instance.setSlope( null );
        assertNull( instance.getSlope() );
        
    }

    /**
     * Test of toString method, of class InputOutputSlopeTriplet.
     */
    public void testToString()
    {
        System.out.println( "toString" );
        
        Double x = new Double( r.nextDouble() );
        Double y = new Double( r.nextDouble() );
        Double slope = new Double( r.nextDouble() );
        InputOutputSlopeTriplet instance = new InputOutputSlopeTriplet( x, y, slope );
        
        String r1 = instance.toString();
        String r2 = instance.toString();
        System.out.println( "R1: " + r1 );
        
        InputOutputPair<Double,Double> i2 = new DefaultInputOutputPair<Double, Double>(
            instance.getInput(), instance.getOutput() );
        String p1 = i2.toString();
        System.out.println( "P1: " + p1 );
        
        assertEquals( r1, r2 );
        assertFalse( r1.equals( p1 ) );
        
        System.out.println( "Null test: " + new InputOutputSlopeTriplet() );
        
    }

}
