/*
 * File:            MinkowskiDistanceMetricTest.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;

/**
 * Unit tests for class MinkowskiDistanceMetric.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class MinkowskiDistanceMetricTest
    extends MetricTestHarness<Vectorizable>
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public MinkowskiDistanceMetricTest(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors method, of class MinkowskiDistanceMetric.
     */
    public void testConstructors()
    {

        double power = MinkowskiDistanceMetric.DEFAULT_POWER;
        MinkowskiDistanceMetric instance = new MinkowskiDistanceMetric();
        assertEquals(power, instance.getPower(), 0.0);

        power *= RANDOM.nextDouble();
        instance = new MinkowskiDistanceMetric(power);
        assertEquals(power, instance.getPower(), 0.0);
    }

    /**
     * Test of evaluate method, of class MinkowskiDistanceMetric.
     */
    public void testEvaluate()
    {
        double epsilon = 1e-10;
        int d = 10;
        Vector x = VectorFactory.getDefault().createUniformRandom(d, -100, +100, RANDOM);
        Vector y = VectorFactory.getDefault().createUniformRandom(d, -100, +100, RANDOM);

        MinkowskiDistanceMetric instance = new MinkowskiDistanceMetric();
        instance.setPower(1.0);
        assertEquals(x.minus(y).norm1(), instance.evaluate(x, y), epsilon);
        assertEquals(x.minus(y).norm1(), instance.evaluate(y, x), epsilon);
        assertEquals(0.0, instance.evaluate(x, x), epsilon);
        assertEquals(0.0, instance.evaluate(y, y), epsilon);
        
        instance.setPower(2.0);
        assertEquals(x.minus(y).norm2(), instance.evaluate(x, y), epsilon);
        assertEquals(x.minus(y).norm2(), instance.evaluate(y, x), epsilon);
        assertEquals(0.0, instance.evaluate(x, x), epsilon);
        assertEquals(0.0, instance.evaluate(y, y), epsilon);
    }

    /**
     * Test of getPower method, of class MinkowskiDistanceMetric.
     */
    public void testGetPower()
    {
        this.testSetPower();
    }

    /**
     * Test of setPower method, of class MinkowskiDistanceMetric.
     */
    public void testSetPower()
    {
        double power = MinkowskiDistanceMetric.DEFAULT_POWER;
        MinkowskiDistanceMetric instance = new MinkowskiDistanceMetric();
        assertEquals(power, instance.getPower(), 0.0);

        double[] goodValues = {0.001, 0.2, 1.0, 10.0, 123.0, RANDOM.nextDouble()};
        for (double goodValue : goodValues)
        {
            power = goodValue;
            instance.setPower(power);
            assertEquals(power, instance.getPower(), 0.0);
        }

        double[] badValues = {0.0, -0.2, -1.0, -RANDOM.nextDouble()};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setPower(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(power, instance.getPower(), 0.0);
        }
    }

    @Override
    public MinkowskiDistanceMetric createInstance()
    {
        return new MinkowskiDistanceMetric(RANDOM.nextDouble() * 10 + 1.0);
    }

    @Override
    public Vector generateRandomFirstType()
    {
        return VectorFactory.getDefault().createUniformRandom(5,-10.0,5.0, RANDOM);
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
        MinkowskiDistanceMetric instance = new MinkowskiDistanceMetric(2.0);

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
        assertEquals(Math.sqrt(2), instance.evaluate(v00, v11));

        // Make sure that it works with negative points.
        assertEquals(1.0, instance.evaluate(v00, v01.scale(-1.0)));
        assertEquals(1.0, instance.evaluate(v00, v10.scale(-1.0)));
        assertEquals(Math.sqrt(2), instance.evaluate(v00, v11.scale(-1.0)));

        // Test examples of manhattan distance by setting power to 2.0.
        instance.setPower(1.0);


        assertEquals(0.0, instance.evaluate(v00, v00));
        assertEquals(0.0, instance.evaluate(v01, v01));
        assertEquals(0.0, instance.evaluate(v10, v10));
        assertEquals(0.0, instance.evaluate(v11, v11));

        // Make sure the distances between points are correct.
        assertEquals(1.0, instance.evaluate(v00, v01));
        assertEquals(1.0, instance.evaluate(v00, v10));
        assertEquals(1.0, instance.evaluate(v01, v00));
        assertEquals(1.0, instance.evaluate(v01, v11));
        assertEquals(2.0, instance.evaluate(v00, v11));

        // Make sure that it works with negative points.
        assertEquals(1.0, instance.evaluate(v00, v01.scale(-1.0)));
        assertEquals(1.0, instance.evaluate(v00, v10.scale(-1.0)));
        assertEquals(2.0, instance.evaluate(v00, v11.scale(-1.0)));

        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            Vector x = this.generateRandomFirstType();
            Vector y = this.generateRandomFirstType();

            double expected = x.minus(y).norm1();
            double result = instance.evaluate(x, y);
            assertEquals(expected, result);
            assertTrue(result >= 0.0);
            assertEquals(0.0, instance.evaluate(x, x), TOLERANCE);
            assertEquals(0.0, instance.evaluate(y, y), TOLERANCE);
        }
    }

}