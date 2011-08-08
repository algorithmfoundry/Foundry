/*
 * File:                WolfeConditionsTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 27, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization.line;

import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class WolfeConditionsTest
 * @author Kevin R. Dixon
 */
public class WolfeConditionsTest
    extends TestCase
{
    
    private Random random = new Random( 0 );

    /**
     * Entry point for JUnit tests for class WolfeConditionsTest
     * @param testName name of this test
     */
    public WolfeConditionsTest(
        String testName)
    {
        super(testName);
    }

    public InputOutputSlopeTriplet createTriplet()
    {
        return new InputOutputSlopeTriplet( 
            random.nextDouble(), random.nextDouble(), -random.nextDouble() );
    }
    
    public WolfeConditions createWolfe()
    {
        double curvatureCondition = random.nextDouble();
        double slopeCondition = curvatureCondition / 2.0;
        return new WolfeConditions( 
            this.createTriplet(), slopeCondition, curvatureCondition );
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        WolfeConditions wolfe = this.createWolfe();
        assertNotNull( wolfe.getOriginalPoint() );

        WolfeConditions w2 = new WolfeConditions( wolfe );
        assertNotNull( w2 );
        assertNotSame( wolfe, w2 );
        assertNotNull( w2.getOriginalPoint() );
        assertNotSame( wolfe.getOriginalPoint(), w2.getOriginalPoint() );
        assertEquals( wolfe.getCurvatureCondition(), w2.getCurvatureCondition() );
        assertEquals( wolfe.getSlopeCondition(), w2.getSlopeCondition() );
    }
    
    /**
     * Test of clone method, of class WolfeConditions.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        
        WolfeConditions wolfe = this.createWolfe();
        WolfeConditions w2 = wolfe.clone();
        assertNotNull( w2 );
        assertNotSame( wolfe, w2 );
        assertNotNull( w2.getOriginalPoint() );
        assertNotSame( wolfe.getOriginalPoint(), w2.getOriginalPoint() );
        assertEquals( wolfe.getCurvatureCondition(), w2.getCurvatureCondition() );
        assertEquals( wolfe.getSlopeCondition(), w2.getSlopeCondition() );
        
    }

    /**
     * Test of evaluate method, of class WolfeConditions.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        WolfeConditions instance = this.createWolfe();
        
        assertFalse( instance.evaluate( instance.getOriginalPoint() ) );
        InputOutputSlopeTriplet trialPoint = new InputOutputSlopeTriplet(
            instance.getOriginalPoint().getInput(),
            instance.getOriginalPoint().getOutput(),
            -instance.getSlopeCondition() );
        assertTrue( instance.evaluate( trialPoint ) );
        
        instance.getOriginalPoint().setSlope( 0.0 );
        try
        {
            instance.evaluate( trialPoint );
            fail( "Original slope must be < 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }


    /**
     * Test of evaluateGoldsteinCondition method, of class WolfeConditions.
     */
    public void testEvaluateGoldsteinCondition()
    {
        System.out.println( "evaluateGoldsteinCondition" );
        WolfeConditions instance = this.createWolfe();
        assertTrue( instance.evaluateGoldsteinCondition( instance.getOriginalPoint() ) );

        InputOutputSlopeTriplet trialPoint = new InputOutputSlopeTriplet(
            instance.getOriginalPoint().getInput()+1.0,
            instance.getOriginalPoint().getOutput()+1.0,
            -instance.getSlopeCondition() );
        assertFalse( instance.evaluateGoldsteinCondition( trialPoint ) );

    }

    /**
     * Test of evaluateStrictCurvatureCondition method, of class WolfeConditions.
     */
    public void testEvaluateStrictCurvatureCondition_double()
    {
        System.out.println( "evaluateStrictCurvatureCondition" );
        WolfeConditions instance = this.createWolfe();
        assertFalse( instance.evaluateStrictCurvatureCondition( instance.getOriginalPoint().getSlope() ) );
        
        InputOutputSlopeTriplet trialPoint = new InputOutputSlopeTriplet(
            instance.getOriginalPoint().getInput()+1.0,
            instance.getOriginalPoint().getOutput()+1.0,
            -instance.getSlopeCondition() );
        assertTrue( instance.evaluateStrictCurvatureCondition( trialPoint.getSlope() ) );

    }

    /**
     * Test of getOriginalPoint method, of class WolfeConditions.
     */
    public void testGetOriginalPoint()
    {
        System.out.println( "getOriginalPoint" );
        InputOutputSlopeTriplet o = this.createTriplet();
        double c = random.nextDouble();
        double s = c/2.0;
        WolfeConditions wolfe = new WolfeConditions( o, s, c );
        assertSame( o, wolfe.getOriginalPoint() );

    }

    /**
     * Test of setOriginalPoint method, of class WolfeConditions.
     */
    public void testSetOriginalPoint()
    {
        System.out.println( "setOriginalPoint" );
        InputOutputSlopeTriplet o = this.createTriplet();
        double c = random.nextDouble();
        double s = c/2.0;
        WolfeConditions wolfe = new WolfeConditions( o, s, c );
        assertSame( o, wolfe.getOriginalPoint() );
        wolfe.setOriginalPoint( null );
        assertNull( wolfe.getOriginalPoint() );
        
        wolfe.setOriginalPoint( o );
        assertSame( o, wolfe.getOriginalPoint() );
        
    }

    /**
     * Test of getSlopeCondition method, of class WolfeConditions.
     */
    public void testGetSlopeCondition()
    {
        System.out.println( "getSlopeCondition" );
        InputOutputSlopeTriplet o = this.createTriplet();
        double c = random.nextDouble();
        double s = c/2.0;
        WolfeConditions wolfe = new WolfeConditions( o, s, c );
        assertEquals( s, wolfe.getSlopeCondition() );
        
        try
        {
            wolfe = new WolfeConditions( o, s, s );
            fail( "Slope condition must be < curvature condition" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of setSlopeCondition method, of class WolfeConditions.
     */
    public void testSetSlopeCondition()
    {
        System.out.println( "setSlopeCondition" );
        InputOutputSlopeTriplet o = this.createTriplet();
        double c = random.nextDouble();
        double s = c/2.0;
        WolfeConditions wolfe = new WolfeConditions( o, s, c );
        assertEquals( s, wolfe.getSlopeCondition() );
        
        double s2 = s/2.0;
        wolfe.setSlopeCondition( s2 );
        assertEquals( s2, wolfe.getSlopeCondition() );
        
        try
        {
            wolfe.setSlopeCondition( 0.0 );
            fail( "slopeCondition must be (0,1)" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            wolfe.setSlopeCondition( 1.0 );
            fail( "slopeCondition must be (0,1)" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }        

    }

    /**
     * Test of getCurvatureCondition method, of class WolfeConditions.
     */
    public void testGetCurvatureCondition()
    {
        System.out.println( "getCurvatureCondition" );
        InputOutputSlopeTriplet o = this.createTriplet();
        double c = random.nextDouble();
        double s = c/2.0;
        WolfeConditions wolfe = new WolfeConditions( o, s, c );
        assertEquals( c, wolfe.getCurvatureCondition() );
        
        try
        {
            wolfe = new WolfeConditions( o, s, s );
            fail( "Slope condition must be < curvature condition" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of setCurvatureCondition method, of class WolfeConditions.
     */
    public void testSetCurvatureCondition()
    {
        System.out.println( "setCurvatureCondition" );
        InputOutputSlopeTriplet o = this.createTriplet();
        double c = random.nextDouble();
        double s = c/2.0;
        WolfeConditions wolfe = new WolfeConditions( o, s, c );
        assertEquals( s, wolfe.getSlopeCondition() );
        
        double c2 = 1.0 - 0.5*(1.0-c);
        wolfe.setCurvatureCondition( c2 );
        assertEquals( c2, wolfe.getCurvatureCondition() );
        
        try
        {
            wolfe.setCurvatureCondition( 0.0 );
            fail( "curvatureCondition must be (0,1)" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            wolfe.setCurvatureCondition( 1.0 );
            fail( "curvatureCondition must be (0,1)" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }  
    }

}
