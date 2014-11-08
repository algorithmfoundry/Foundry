/*
 * File:            SoftPlusFunctionTest.java
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
 * Unit tests for class {@link SoftPlusFunction}.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class SoftPlusFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{
    
    /**
     * Creates a new test.
     * 
     * @param   testName
     *      The name of this test.
     */
    public SoftPlusFunctionTest(
        final String testName)
    {
        super(testName);
    }

    @Override
    public DifferentiableUnivariateScalarFunction createInstance()
    {
        return new SoftPlusFunction();
    }
    
    /**
     * Test of constructors, of class SoftPlusFunction.
     */
    @Test
    @Override
    public void testConstructors()
    {
        SoftPlusFunction instance = new SoftPlusFunction();
        assertNotNull(instance);
    }
    /**
     * Test of clone method, of class SoftPlusFunction.
     */
    @Test
    public void testClone()
    {
        SoftPlusFunction instance = new SoftPlusFunction();
        SoftPlusFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotNull(clone);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of evaluate method, of class SoftPlusFunction.
     */
    @Test
    public void testEvaluate()
    {
        SoftPlusFunction instance = new SoftPlusFunction();
        double epsilon = 1e-10;
        
        double[] values = { 0, 1, -1, 0.2, -0.2, 12.3, -12.3 };
        for (double value : values)
        {
            assertEquals(Math.log(1 + Math.exp(value)), instance.evaluate(value), epsilon);
        }
    }

    /**
     * Test of differentiate method, of class SoftPlusFunction.
     */
    @Test
    public void testDifferentiate()
    {
        SoftPlusFunction instance = new SoftPlusFunction();
        SigmoidFunction sigmoid = new SigmoidFunction();
        double epsilon = 1e-10;
        
        double[] values = { 0, 1, -1, 0.2, -0.2, 12.3, -12.3 };
        for (double value : values)
        {
            assertEquals(sigmoid.evaluate(value), instance.differentiate(value), epsilon);
        }
    }
    
}
