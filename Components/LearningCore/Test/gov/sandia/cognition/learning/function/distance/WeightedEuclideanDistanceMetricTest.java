/*
 * File:                WeightedEuclideanDistanceMetricTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class WeightedEuclideanDistanceMetricTest
    extends SemimetricTestHarness<Vectorizable>
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public WeightedEuclideanDistanceMetricTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class WeightedEuclideanDistanceMetric.
     */
    public void testConstructors()
    {
        Vector weights = null;
        WeightedEuclideanDistanceMetric instance =
            new WeightedEuclideanDistanceMetric();
        assertSame(weights, instance.getWeights());

        weights = new Vector2(9.0, -3.2);
        instance = new WeightedEuclideanDistanceMetric(weights);
        assertSame(weights, instance.getWeights());
    }

    /**
     * Test of clone method, of class WeightedEuclideanDistanceMetric.
     */
    @Override
    public void testClone()
    {
        WeightedEuclideanDistanceMetric instance =
            new WeightedEuclideanDistanceMetric(new Vector2(1.0, -6.0));
        WeightedEuclideanDistanceMetric result = instance.clone();
        assertNotSame(instance, result);
        assertNotSame(result, instance.clone());
        assertEquals(instance.getWeights(), result.getWeights());
        assertNotSame(instance.getWeights(), result.getWeights());
    }

    /**
     * Test of getInputDimensionality method, of class WeightedEuclideanDistanceMetric.
     */
    public void testGetInputDimensionality()
    {
        WeightedEuclideanDistanceMetric instance =
            new WeightedEuclideanDistanceMetric();
        assertEquals(-1, instance.getInputDimensionality());
        instance.setWeights(new Vector2(1.0, -6.0));
        assertEquals(2, instance.getInputDimensionality());
        instance.setWeights(new Vector3(1.0, -6.0, 3.5));
        assertEquals(3, instance.getInputDimensionality());
    }

    /**
     * Test of getWeights method, of class WeightedEuclideanDistanceMetric.
     */
    public void testGetWeights()
    {
        this.testSetWeights();
    }

    /**
     * Test of setWeights method, of class WeightedEuclideanDistanceMetric.
     */
    public void testSetWeights()
    {
        Vector weights = null;
        WeightedEuclideanDistanceMetric instance =
            new WeightedEuclideanDistanceMetric();
        assertSame(weights, instance.getWeights());

        weights = new Vector2(9.0, -3.2);
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());

        weights = null;
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());

        weights = new Vector3(9.0, 0.0, -3.2);
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
    }

    @Override
    public Semimetric<Vectorizable> createInstance()
    {
        return new WeightedEuclideanDistanceMetric(
            VectorFactory.getDefault().createUniformRandom(
                10, 0.0, 1.0, RANDOM));
    }

    @Override
    public Vectorizable generateRandomFirstType()
    {
        return VectorFactory.getDefault().createUniformRandom(
                10, -10.0, 10.0, RANDOM);
    }

    @Override
    public void testKnownValues()
    {
        double epsilon = 0.0000001;
        WeightedEuclideanDistanceMetric metric =
            new WeightedEuclideanDistanceMetric(new Vector2(2.0, 0.5));
        
        // Create four points to compute the distances between.
        Vector2 v00 = new Vector2(0.0, 0.0);
        Vector2 v01 = new Vector2(0.0, 1.0);
        Vector2 v10 = new Vector2(1.0, 0.0);
        Vector2 v11 = new Vector2(1.0, 1.0);

        // Make sure the distance to self is zero.
        assertEquals(0.0, metric.evaluate(v00, v00), 0.0);
        assertEquals(0.0, metric.evaluate(v01, v01), 0.0);
        assertEquals(0.0, metric.evaluate(v10, v10), 0.0);
        assertEquals(0.0, metric.evaluate(v11, v11), 0.0);

        // Make sure the distances between points are correct.
        assertEquals(Math.sqrt(0.5), metric.evaluate(v00, v01), epsilon);
        assertEquals(Math.sqrt(2.0), metric.evaluate(v00, v10), epsilon);
        assertEquals(Math.sqrt(0.5), metric.evaluate(v01, v00), epsilon);
        assertEquals(Math.sqrt(2.0), metric.evaluate(v01, v11), epsilon);
        assertEquals(Math.sqrt(2.5), metric.evaluate(v00, v11), epsilon);

        // Make sure that it works with negative points.
        assertEquals(Math.sqrt(0.5), metric.evaluate(v00, v01.scale(-1.0)));
        assertEquals(Math.sqrt(2.0), metric.evaluate(v00, v10.scale(-1.0)));
        assertEquals(Math.sqrt(2.5), metric.evaluate(v00, v11.scale(-1.0)));
    }

}
