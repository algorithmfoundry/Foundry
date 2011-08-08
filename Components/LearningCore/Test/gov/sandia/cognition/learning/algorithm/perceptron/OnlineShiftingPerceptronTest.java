/*
 * File:                OnlineShiftingPerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.perceptron.KernelizableBinaryCategorizerOnlineLearner;
import gov.sandia.cognition.learning.algorithm.perceptron.OnlineShiftingPerceptron;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlineShiftingPerceptron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlineShiftingPerceptronTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{
    /**
     * Creates a new test.
     */
    public OnlineShiftingPerceptronTest()
    {
    }

    @Override
    protected KernelizableBinaryCategorizerOnlineLearner createLinearInstance()
    {
        return new OnlineShiftingPerceptron();
    }

    /**
     * Test of constructors of class OnlineShiftingPerceptron.
     */
    @Test
    public void testConstructors()
    {
        double lambda = OnlineShiftingPerceptron.DEFAULT_LAMBDA;
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        OnlineShiftingPerceptron instance = new OnlineShiftingPerceptron();
        assertEquals(lambda, instance.getLambda(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());

        lambda = random.nextDouble();
        instance = new OnlineShiftingPerceptron(lambda);
        assertEquals(lambda, instance.getLambda(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = new SparseVectorFactoryMTJ();
        instance = new OnlineShiftingPerceptron(lambda, vectorFactory);
        assertEquals(lambda, instance.getLambda(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of getLambda method, of class OnlineShiftingPerceptron.
     */
    @Test
    public void testGetLambda()
    {
        this.testSetLambda();
    }

    /**
     * Test of setLambda method, of class OnlineShiftingPerceptron.
     */
    @Test
    public void testSetLambda()
    {
        double lambda = OnlineShiftingPerceptron.DEFAULT_LAMBDA;
        OnlineShiftingPerceptron instance = new OnlineShiftingPerceptron();
        assertEquals(lambda, instance.getLambda(), 0.0);

        double[] goodValues = { 0.01, 0.1, 1.0, 2.3, 10.0, random.nextDouble() };
        for (double goodValue : goodValues)
        {
            lambda = goodValue;
            instance.setLambda(lambda);
            assertEquals(lambda, instance.getLambda(), 0.0);
        }

        double[] badValues = {0.0, -0.01, -1.0, -2.3, -10.0, -random.nextDouble() };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setLambda(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(lambda, instance.getLambda(), 0.0);
        }
    }

}