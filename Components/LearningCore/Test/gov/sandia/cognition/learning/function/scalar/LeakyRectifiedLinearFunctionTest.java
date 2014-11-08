/*
 * File:            LeakyRectifiedLinearFunctionTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2014 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link LeakyRectifiedLinearFunctionTest}.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class LeakyRectifiedLinearFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{

    /**
     * Creates a new test.
     * 
     * @param   testName
     *      The name of this test.
     */
    public LeakyRectifiedLinearFunctionTest(
        final String testName)
    {
        super(testName);
    }

    @Override
    public DifferentiableUnivariateScalarFunction createInstance()
    {
        return new LeakyRectifiedLinearFunction(this.RANDOM.nextDouble());
    }

    /**
     * Test of constructors, of class LeakyRectifiedLinearFunction.
     */
    @Test
    @Override
    public void testConstructors()
    {
        double leakage = LeakyRectifiedLinearFunction.DEFAULT_LEAKAGE;
        LeakyRectifiedLinearFunction instance = new LeakyRectifiedLinearFunction();
        assertEquals(leakage, instance.getLeakage(), 0.0);
        
        leakage = this.RANDOM.nextDouble();
        instance = new LeakyRectifiedLinearFunction(leakage);
        assertEquals(leakage, instance.getLeakage(), 0.0);
    }
    
    /**
     * Test of clone method, of class LeakyRectifiedLinearFunction.
     */
    @Test
    public void testClone()
    {
        LeakyRectifiedLinearFunction instance = new LeakyRectifiedLinearFunction();
        LeakyRectifiedLinearFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotNull(clone);
        assertNotSame(clone, instance.clone());
    }
    
    /**
     * Test of evaluate method, of class LeakyRectifiedLinearFunction.
     */
    @Test
    public void testEvaluate()
    {
        LeakyRectifiedLinearFunction instance = new LeakyRectifiedLinearFunction();
        assertEquals(0.0, instance.evaluate(0.0), 0.0);
        assertEquals(0.1, instance.evaluate(0.1), 0.0);
        assertEquals(2.0, instance.evaluate(2.0), 0.0);
        assertEquals(123.0, instance.evaluate(123.0), 0.0);
        assertEquals(-0.001, instance.evaluate(-0.1), 0.0);
        assertEquals(-0.02, instance.evaluate(-2.0), 0.0);
        assertEquals(-1.23, instance.evaluate(-123.0), 0.0);
        
        instance.setLeakage(0.001);
        assertEquals(0.0, instance.evaluate(0.0), 0.0);
        assertEquals(123.0, instance.evaluate(123.0), 0.0);
        assertEquals(-0.123, instance.evaluate(-123.0), 0.0);
    }

    /**
     * Test of differentiate method, of class LeakyRectifiedLinearFunction.
     */
    @Test
    public void testDifferentiate()
    {
        LeakyRectifiedLinearFunction instance = new LeakyRectifiedLinearFunction();
        assertEquals(0.01, instance.differentiate(0.0), 0.0);
        assertEquals(1.0, instance.differentiate(0.1), 0.0);
        assertEquals(1.0, instance.differentiate(2.0), 0.0);
        assertEquals(1.0, instance.differentiate(123.0), 0.0);
        assertEquals(0.01, instance.differentiate(-0.1), 0.0);
        assertEquals(0.01, instance.differentiate(-2.0), 0.0);
        assertEquals(0.01, instance.differentiate(-123.0), 0.0);
        
        double leakage = this.RANDOM.nextDouble();
        instance.setLeakage(leakage);
        assertEquals(leakage, instance.differentiate(0.0), 0.0);
        assertEquals(1.0, instance.differentiate(123.0), 0.0);
        assertEquals(leakage, instance.differentiate(-123.0), 0.0);
    }

    /**
     * Test of getLeakage method, of class LeakyRectifiedLinearFunction.
     */
    @Test
    public void testGetLeakage()
    {
        this.testSetLeakage();
    }

    /**
     * Test of setLeakage method, of class LeakyRectifiedLinearFunction.
     */
    @Test
    public void testSetLeakage()
    {
        double leakage = LeakyRectifiedLinearFunction.DEFAULT_LEAKAGE;
        LeakyRectifiedLinearFunction instance = new LeakyRectifiedLinearFunction();
        assertEquals(leakage, instance.getLeakage(), 0.0);
        
        double[] goodValues = { 0.0, 0.001, 0.5, 1.0, this.RANDOM.nextDouble() };
        for (double value : goodValues)
        {
            leakage = value;
            instance.setLeakage(leakage);
            assertEquals(leakage, instance.getLeakage(), 0.0);
        }

        double[] badValues = { -0.1, 1.1, -this.RANDOM.nextDouble(), 
            Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setLeakage(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue( "" + badValue, exceptionThrown);
            }
            assertEquals(leakage, instance.getLeakage(), 0.0);
        }
    }

    
}
