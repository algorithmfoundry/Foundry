/*
 * File:                EuclideanDistanceSquaredMetricTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;

/**
 * This class implements JUnit tests for the following classes: EuclideanDistanceSquaredMetric
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class EuclideanDistanceSquaredMetricTest
    extends SemimetricTestHarness<Vectorizable>
{
    public EuclideanDistanceSquaredMetricTest(
        String testName )
    {
        super(testName);
    }


    @Override
    public EuclideanDistanceSquaredMetric createInstance()
    {
        return EuclideanDistanceSquaredMetric.INSTANCE;
    }

    @Override
    public Vector generateRandomFirstType()
    {
        return VectorFactory.getDefault().createUniformRandom(2,-5.0,10.0,RANDOM);
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
        EuclideanDistanceSquaredMetric metric = this.createInstance();
        
        // Create four points to compute the distances between.
        Vector2 v00 = new Vector2(0.0, 0.0);
        Vector2 v01 = new Vector2(0.0, 1.0);
        Vector2 v10 = new Vector2(1.0, 0.0);
        Vector2 v11 = new Vector2(1.0, 1.0);
        
        // Make sure the distance to self is zero.
        assertEquals(0.0, metric.evaluate(v00, v00));
        assertEquals(0.0, metric.evaluate(v01, v01));
        assertEquals(0.0, metric.evaluate(v10, v10));
        assertEquals(0.0, metric.evaluate(v11, v11));
        
        // Make sure the distances between points are correct.
        assertEquals(1.0, metric.evaluate(v00, v01));
        assertEquals(1.0, metric.evaluate(v00, v10));
        assertEquals(1.0, metric.evaluate(v01, v00));
        assertEquals(1.0, metric.evaluate(v01, v11));
        assertEquals(2.0, metric.evaluate(v00, v11), TOLERANCE);
        
        // Make sure that it works with negative points.
        assertEquals(1.0, metric.evaluate(v00, v01.scale(-1.0)));
        assertEquals(1.0, metric.evaluate(v00, v10.scale(-1.0)));
        assertEquals(2.0, metric.evaluate(v00, v11.scale(-1.0)), TOLERANCE);
    }

}
