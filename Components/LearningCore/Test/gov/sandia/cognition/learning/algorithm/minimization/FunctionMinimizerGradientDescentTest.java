/*
 * File:                FunctionMinimizerGradientDescentTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class FunctionMinimizerGradientDescentTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerGradientDescentTest
    extends FunctionMinimizerTestHarness<DifferentiableEvaluator<? super Vector,Double,Vector>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerGradientDescentTest
     * @param testName name of this test
     */
    public FunctionMinimizerGradientDescentTest(
        String testName)
    {
        super(testName);
    }

    double MOMENTUM = 0.5;

    double LEARNING_RATE = 0.1;

    /**
     * Test of getLearningRate method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerGradientDescent.
     */
    public void testGetLearningRate()
    {
        System.out.println("getLearningRate");

        FunctionMinimizerGradientDescent instance = this.createInstance();
        assertEquals(LEARNING_RATE, instance.getLearningRate());
    }

    /**
     * Test of setLearningRate method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerGradientDescent.
     */
    public void testSetLearningRate()
    {
        System.out.println("setLearningRate");

        FunctionMinimizerGradientDescent instance = this.createInstance();
        double v = instance.getLearningRate() / 2.0;
        instance.setLearningRate(v);
        assertEquals(v, instance.getLearningRate());

        instance.setLearningRate(1.0);

        try
        {
            instance.setLearningRate(0.0);
            fail("Learning Rate must be (0,1]");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance.setLearningRate(2.0);
            fail("Learning Rate must be (0,1]");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
    }

    /**
     * Test of getMomentum method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerGradientDescent.
     */
    public void testGetMomentum()
    {
        System.out.println("getMomentum");

        FunctionMinimizerGradientDescent instance = this.createInstance();

        assertEquals(MOMENTUM, instance.getMomentum());

    }

    /**
     * Test of setMomentum method, of class gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerGradientDescent.
     */
    public void testSetMomentum()
    {
        System.out.println("setMomentum");

        FunctionMinimizerGradientDescent instance = this.createInstance();
        double v = instance.getMomentum() / 2.0;
        instance.setMomentum(v);
        assertEquals(v, instance.getMomentum());

        instance.setMomentum(0.0);

        try
        {
            instance.setMomentum(1.0);
            fail("Learning Rate must be [0,1)");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance.setMomentum(-1.0);
            fail("Learning Rate must be (0,1]");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
    }

    @Override
    public FunctionMinimizerGradientDescent createInstance()
    {
        FunctionMinimizerGradientDescent mini = 
            new FunctionMinimizerGradientDescent( LEARNING_RATE, MOMENTUM );
        return mini;
    }

    @Override
    public void testMinimizeRosenbrock()
    {
        System.out.println( "CANNOT TEST ROSENBROCK WITH GRADIENT DESCENT, AS the parameters needed to solve it suck." );
    }

}
