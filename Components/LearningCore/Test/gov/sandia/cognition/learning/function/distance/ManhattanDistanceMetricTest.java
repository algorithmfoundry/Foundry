/*
 * File:                ManhattanDistanceMetricTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 13, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 *
 * @author Justin Basilico
 */
public class ManhattanDistanceMetricTest
    extends MetricTestHarness<Vectorizable>
{

    public ManhattanDistanceMetricTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public ManhattanDistanceMetric createInstance()
    {
        return ManhattanDistanceMetric.INSTANCE;
    }

    @Override
    public Vector generateRandomFirstType()
    {
        return VectorFactory.getDefault().createUniformRandom(4,-1.0,10.0,RANDOM);
    }

    @Override
    public void testKnownValues()
    {
        ManhattanDistanceMetric instance = this.createInstance();
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
