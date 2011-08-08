/*
 * File:                ClusterCentroidDivergenceFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright August 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import junit.framework.*;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     ClusterCentroidDivergenceFunction
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class ClusterCentroidDivergenceFunctionTest
    extends TestCase
{
    public ClusterCentroidDivergenceFunctionTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        ClusterCentroidDivergenceFunction<Vector> instance = 
            new ClusterCentroidDivergenceFunction<Vector>();
        assertNull(instance.getDivergenceFunction());
        
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        instance = new ClusterCentroidDivergenceFunction<Vector>(metric);
        assertSame(metric, instance.getDivergenceFunction());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.clustering.divergence.ClusterCentroidDivergenceFunction.
     */
    public void testEvaluate()
    {
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        Vector v1 = new Vector3(Math.random(), Math.random(), Math.random());
        Vector v2 = new Vector3(Math.random(), Math.random(), Math.random());
        CentroidCluster<Vector> c1 = new CentroidCluster<Vector>(v1);
        c1.getMembers().add(v1);
        c1.getMembers().add(new Vector3(Math.random(), Math.random(), Math.random()));
        CentroidCluster<Vector> c2 = new CentroidCluster<Vector>(v2);
        c2.getMembers().add(v2);
        c2.getMembers().add(new Vector3(Math.random(), Math.random(), Math.random()));
        c2.getMembers().add(new Vector3(Math.random(), Math.random(), Math.random()));
        
        ClusterCentroidDivergenceFunction<Vector> instance = 
            new ClusterCentroidDivergenceFunction<Vector>(metric);
        assertEquals(v1.euclideanDistance(v2), instance.evaluate(c1, c2));
        
    }
}
