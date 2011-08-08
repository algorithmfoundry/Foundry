/*
 * File:                PIDControllerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;
import gov.sandia.cognition.math.signals.PIDController.State;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for {@name}.
 *
 * @author krdixon
 */
public class PIDControllerTest
extends TestCase
{

    Random random = new Random( 1 );

    public PIDControllerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getTargetInput method, of class PIDController.
     */
    public void testGetTargetInput()
    {
        System.out.println("getTargetInput");
        double targetInput = random.nextGaussian();
        PIDController instance = new PIDController(
            random.nextDouble(), random.nextDouble(), random.nextDouble(), targetInput );
        assertEquals( targetInput, instance.getTargetInput() );
    }

    /**
     * Test of setTargetInput method, of class PIDController.
     */
    public void testSetTargetInput()
    {
        System.out.println("setTargetInput");
        double targetInput = random.nextGaussian();
        PIDController instance = new PIDController();
        assertEquals( 0.0, instance.getTargetInput() );
        instance.setTargetInput(targetInput);
        assertEquals( targetInput, instance.getTargetInput() );
    }

    /**
     * Test of getProportionalGain method, of class PIDController.
     */
    public void testGetProportionalGain()
    {
        System.out.println("getProportionalGain");
        PIDController instance = new PIDController();
        assertEquals( PIDController.DEFAULT_PROPORTIONAL_GAIN, instance.getProportionalGain() );
    }

    /**
     * Test of setProportionalGain method, of class PIDController.
     */
    public void testSetProportionalGain()
    {
        System.out.println("setProportionalGain");
        double proportionalGain = random.nextGaussian();
        PIDController instance = new PIDController();
        instance.setProportionalGain(proportionalGain);
        assertEquals( proportionalGain, instance.getProportionalGain() );
    }

    /**
     * Test of getIntegralGain method, of class PIDController.
     */
    public void testGetIntegralGain()
    {
        System.out.println("getIntegralGain");
        PIDController instance = new PIDController();
        assertEquals( PIDController.DEFAULT_INTEGRAL_GAIN, instance.getIntegralGain() );
    }

    /**
     * Test of setIntegralGain method, of class PIDController.
     */
    public void testSetIntegralGain()
    {
        System.out.println("setIntegralGain");
        double integralGain = random.nextGaussian();
        PIDController instance = new PIDController();
        instance.setIntegralGain(integralGain);
        assertEquals( integralGain, instance.getIntegralGain() );
    }

    /**
     * Test of getDerivativeGain method, of class PIDController.
     */
    public void testGetDerivativeGain()
    {
        System.out.println("getDerivativeGain");
        PIDController instance = new PIDController();
        assertEquals( PIDController.DEFAULT_DERIVATIVE_GAIN, instance.getDerivativeGain() );
    }

    /**
     * Test of setDerivativeGain method, of class PIDController.
     */
    public void testSetDerivativeGain()
    {
        System.out.println("setDerivativeGain");
        double derivativeGain = random.nextGaussian();
        PIDController instance = new PIDController();
        instance.setDerivativeGain(derivativeGain);
        assertEquals( derivativeGain, instance.getDerivativeGain() );
    }

    public static class AtanFunction
        extends AbstractUnivariateScalarFunction
    {

        public double evaluate(
            double input)
        {
            return Math.atan(input);
        }

    }

    /**
     * Test of evaluate method, of class PIDController.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        Evaluator<Double,Double> f = new AtanFunction();
        PIDController instance = new PIDController( 1.0, 0.5, 0.1, 1.0 );


        double EPS = 1e-5;
        double x = -10.0;
        double y = 0.0;
        for( int i = 0; i < 100; i++ )
        {
            y = f.evaluate(x);
            x = instance.evaluate(y);
//            System.out.println( "y: " + y + ", State: " + ObjectUtil.toString(instance.getState()) );
        }

        assertEquals( y, instance.getTargetInput(), EPS );

    }

    /**
     * Test of createDefaultState method, of class PIDController.
     */
    public void testCreateDefaultState()
    {
        System.out.println("createDefaultState");
        PIDController instance = new PIDController();
        State result = instance.createDefaultState();
        assertEquals( 0.0, result.getErrSum() );
        assertEquals( 0.0, result.getLastErr() );
    }

}
