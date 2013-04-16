/*
 * File:            ChebyshevDistanceMetricTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Unit tests for class: ChebyshevDistanceMetric
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class ChebyshevDistanceMetricTest
    extends MetricTestHarness<Vectorizable>
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public ChebyshevDistanceMetricTest(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors method, of class ChebyshevDistanceMetric.
     */
    public void testConstructors()
    {
        ChebyshevDistanceMetric instance = new ChebyshevDistanceMetric();
        assertNotNull(instance);
    }

    /**
     * Test of evaluate method, of class ChebyshevDistanceMetric.
     */
    public void testEvaluate()
    {
        double epsilon = 1e-10;
        int d = 10;
        Vector x = VectorFactory.getDefault().createUniformRandom(d, -100, +100, RANDOM);
        Vector y = VectorFactory.getDefault().createUniformRandom(d, -100, +100, RANDOM);

        ChebyshevDistanceMetric instance = new ChebyshevDistanceMetric();
        assertEquals(x.minus(y).normInfinity(), instance.evaluate(x, y), epsilon);
        assertEquals(x.minus(y).normInfinity(), instance.evaluate(y, x), epsilon);
        assertEquals(0.0, instance.evaluate(x, x), epsilon);
        assertEquals(0.0, instance.evaluate(y, y), epsilon);
    }

    @Override
    public ChebyshevDistanceMetric createInstance()
    {
        return new ChebyshevDistanceMetric();
    }

    @Override
    public Vector generateRandomFirstType()
    {
        return VectorFactory.getDefault().createUniformRandom(5,-10.0,5.0, RANDOM);
    }

    @Override
    public void testKnownValues()
    {
        ChebyshevDistanceMetric instance = new ChebyshevDistanceMetric();

        // Create four points to compute the distances between.
        Vector2 v00 = new Vector2(0.0, 0.0);
        Vector2 v01 = new Vector2(0.0, 1.0);
        Vector2 v10 = new Vector2(1.0, 0.0);
        Vector2 v11 = new Vector2(1.0, 1.0);

        // Make sure the distance to self is zero.
        assertEquals(0.0, instance.evaluate(v00, v00));
        assertEquals(0.0, instance.evaluate(v01, v01));
        assertEquals(0.0, instance.evaluate(v10, v10));
        assertEquals(0.0, instance.evaluate(v11, v11));

        // Make sure the distances between points are correct.
        assertEquals(1.0, instance.evaluate(v00, v01));
        assertEquals(1.0, instance.evaluate(v00, v10));
        assertEquals(1.0, instance.evaluate(v01, v00));
        assertEquals(1.0, instance.evaluate(v01, v11));
        assertEquals(1.0, instance.evaluate(v00, v11));

        // Make sure that it works with negative points.
        assertEquals(1.0, instance.evaluate(v00, v01.scale(-1.0)));
        assertEquals(1.0, instance.evaluate(v00, v10.scale(-1.0)));
        assertEquals(1.0, instance.evaluate(v00, v11.scale(-1.0)));

        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            Vector x = this.generateRandomFirstType();
            Vector y = this.generateRandomFirstType();

            double expected = x.minus(y).normInfinity();
            double result = instance.evaluate(x, y);
            assertEquals(expected, result);
            assertTrue(result >= 0.0);
            assertEquals(0.0, instance.evaluate(x, x), TOLERANCE);
            assertEquals(0.0, instance.evaluate(y, y), TOLERANCE);
        }
    }


}