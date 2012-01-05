/*
 * File:            IdentityScalarFunctionTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2012 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

/**
 * Unit tests for class IdentityScalarFunction.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class IdentityScalarFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The name of the test.
     */
    public IdentityScalarFunctionTest(
        final String testName)
    {
        super(testName);
    }

    @Override
    public IdentityScalarFunction createInstance()
    {
        return new IdentityScalarFunction();
    }

    /**
     * Test of constructors of class IdentityScalarFunction.
     */
    public void testConstructors()
    {
        IdentityScalarFunction instance = new IdentityScalarFunction();
        assertNotNull(instance);
    }

    /**
     * Test of clone method, of class IdentityScalarFunction.
     */
    public void testClone()
    {
        IdentityScalarFunction instance = new IdentityScalarFunction();
        IdentityScalarFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(clone, clone.clone());
    }

    /**
     * Test of evaluate method, of class IdentityScalarFunction.
     */
    public void testEvaluate_Double()
    {

        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            IdentityScalarFunction instance = new IdentityScalarFunction();
            Double input = RANDOM.nextDouble();
            assertSame(input, instance.evaluate(input));
        }
    }

    /**
     * Test of evaluate method, of class IdentityScalarFunction.
     */
    public void testEvaluate_double()
    {

        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            IdentityScalarFunction instance = new IdentityScalarFunction();
            double input = RANDOM.nextDouble();
            assertEquals(input, instance.evaluate(input));
        }
    }

    /**
     * Test of evaluateAsDouble method, of class IdentityScalarFunction.
     */
    public void testEvaluateAsDouble()
    {
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            IdentityScalarFunction instance = new IdentityScalarFunction();
            double input = RANDOM.nextDouble();
            assertEquals(input, instance.evaluateAsDouble(input));
        }
    }

    /**
     * Test of differentiate method, of class IdentityScalarFunction.
     */
    public void testDifferentiate()
    {
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            IdentityScalarFunction instance = new IdentityScalarFunction();
            double input = RANDOM.nextDouble();
            assertEquals(1.0, instance.differentiate(input));
        }
    }

}
