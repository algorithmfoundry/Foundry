/*
 * File:            RectifiedLinearFunctionTest.java
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
 * Unit tests for class {@link RectifiedLinearFunction}.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class RectifiedLinearFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{
    
    /**
     * Creates a new test.
     * 
     * @param   testName
     *      The name of this test.
     */
    public RectifiedLinearFunctionTest(
        final String testName)
    {
        super(testName);
    }

    @Override
    public RectifiedLinearFunction createInstance()
    {
        return new RectifiedLinearFunction();
    }
    
    /**
     * Test of constructors, of class RectifiedLinearFunction.
     */
    @Test
    public void testConstructors()
    {
        RectifiedLinearFunction instance = new RectifiedLinearFunction();
        assertNotNull(instance);
    }
    
    /**
     * Test of clone method, of class RectifiedLinearFunction.
     */
    @Test
    public void testClone()
    {
        RectifiedLinearFunction instance = new RectifiedLinearFunction();
        RectifiedLinearFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotNull(clone);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of evaluate method, of class RectifiedLinearFunction.
     */
    @Test
    public void testEvaluate()
    {
        RectifiedLinearFunction instance = new RectifiedLinearFunction();
        assertEquals(0.0, instance.evaluate(0.0), 0.0);
        assertEquals(0.1, instance.evaluate(0.1), 0.0);
        assertEquals(2.0, instance.evaluate(2.0), 0.0);
        assertEquals(123.0, instance.evaluate(123.0), 0.0);
        assertEquals(0.0, instance.evaluate(-0.1), 0.0);
        assertEquals(0.0, instance.evaluate(-2.0), 0.0);
        assertEquals(0.0, instance.evaluate(-123.0), 0.0);
    }

    /**
     * Test of differentiate method, of class RectifiedLinearFunction.
     */
    @Test
    public void testDifferentiate()
    {
        RectifiedLinearFunction instance = new RectifiedLinearFunction();
        assertEquals(0.0, instance.differentiate(0.0), 0.0);
        assertEquals(1.0, instance.differentiate(0.1), 0.0);
        assertEquals(1.0, instance.differentiate(2.0), 0.0);
        assertEquals(1.0, instance.differentiate(123.0), 0.0);
        assertEquals(0.0, instance.differentiate(-0.1), 0.0);
        assertEquals(0.0, instance.differentiate(-2.0), 0.0);
        assertEquals(0.0, instance.differentiate(-123.0), 0.0);
    }
    
}
