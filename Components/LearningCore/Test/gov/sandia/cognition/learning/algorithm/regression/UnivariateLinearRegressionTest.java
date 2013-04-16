/*
 * File:            UnivariateLinearRegressionTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class UnivariateLinearRegression.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class UnivariateLinearRegressionTest
{
    /** Random number generator. */
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public UnivariateLinearRegressionTest()
    {
        super();
    }

    /**
     * Test of constructors of class UnivariateLinearRegression.
     */
    @Test
    public void testConstructors()
    {
        UnivariateLinearRegression instance = new UnivariateLinearRegression();
        assertNotNull(instance);
    }

    /**
     * Test of learn method, of class UnivariateLinearRegression.
     */
    @Test
    public void testLearn()
    {
        UnivariateLinearRegression instance = new UnivariateLinearRegression();

        Collection<InputOutputPair<Double, Double>> data = new LinkedList<InputOutputPair<Double, Double>>();

        // Empty should be 0x+0
        LinearFunction result = instance.learn(data);
        assertEquals(0.0, result.getOffset(), 0.0);
        assertEquals(0.0, result.getSlope(), 0.0);

        // One data point should have b=x
        data.add(DefaultInputOutputPair.create(4.0, 7.0));
        result = instance.learn(data);
        assertEquals(7.0, result.getOffset(), 0.0);
        assertEquals(0.0, result.getSlope(), 0.0);

        // Duplicate first data point.
        data.add(DefaultInputOutputPair.create(4.0, 7.0));
        result = instance.learn(data);
        assertEquals(7.0, result.getOffset(), 0.0);
        assertEquals(0.0, result.getSlope(), 0.0);

        // Now here is a real function.
        data.add(DefaultInputOutputPair.create(5.0, 6.0));
        result = instance.learn(data);
        assertEquals(11.0, result.getOffset(), 0.0);
        assertEquals(-1.0, result.getSlope(), 0.0);

        data.add(DefaultInputOutputPair.create(0.5, 10.5));
        result = instance.learn(data);
        assertEquals(11.0, result.getOffset(), 0.0);
        assertEquals(-1.0, result.getSlope(), 0.0);

        // Generate some random functions and put random data through them.
        double epsilon = 1e-10;
        for (int i = 0; i < 10; i++)
        {
            LinearFunction f = new LinearFunction(random.nextDouble(),
                random.nextDouble());

            data.clear();
            int pointCount = random.nextInt(100) + 1;
            for (int j = 0; j < pointCount; j++)
            {
                double x = 1000.0 * random.nextDouble();
                double y = f.evaluate(x);
                data.add(DefaultInputOutputPair.create(x, y));
            }

            result = instance.learn(data);

            assertEquals(f.getOffset(), result.getOffset(), epsilon);
            assertEquals(f.getSlope(), result.getSlope(), epsilon);
        }

        // Now do random weights too.
        for (int i = 0; i < 10; i++)
        {
            LinearFunction f = new LinearFunction(random.nextDouble(),
                random.nextDouble());

            data.clear();
            int pointCount = random.nextInt(100) + 1;
            for (int j = 0; j < pointCount; j++)
            {
                double weight = 10.0 * random.nextDouble();
                double x = 1000.0 * random.nextDouble();
                double y = f.evaluate(x);
                data.add(DefaultWeightedInputOutputPair.create(x, y, weight));
            }

            result = instance.learn(data);

            assertEquals(f.getOffset(), result.getOffset(), epsilon);
            assertEquals(f.getSlope(), result.getSlope(), epsilon);
        }

        for (int i = 0; i < 10; i++)
        {
            double noisyness = random.nextDouble();
            LinearFunction f = new LinearFunction(random.nextDouble(), random.nextDouble());

            data.clear();
            int pointCount = random.nextInt(100) + 1;
            for (int j = 0; j < pointCount; j++)
            {
                double noise = random.nextGaussian();
                double x = 1000.0 * noisyness * random.nextDouble();
                double y = f.evaluate(x) + noise;
                data.add(DefaultInputOutputPair.create(x, y));
            }

            result = instance.learn(data);

            assertEquals(f.getOffset(), result.getOffset(), noisyness);
            assertEquals(f.getSlope(), result.getSlope(), noisyness);
        }
    }

}