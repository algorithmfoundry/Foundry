/*
 * File:                ClusterCompleteLinkDivergenceFunctionTest.java
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

import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultCluster;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     ClusterCompleteLinkDivergenceFunction
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class ClusterCompleteLinkDivergenceFunctionTest
    extends TestCase
{
    public ClusterCompleteLinkDivergenceFunctionTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        ClusterCompleteLinkDivergenceFunction<DefaultCluster<Vector>, Vector>
            instance = new ClusterCompleteLinkDivergenceFunction<DefaultCluster<Vector>, Vector>();
        assertNull(instance.getDivergenceFunction());
        
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        instance = new ClusterCompleteLinkDivergenceFunction<DefaultCluster<Vector>, Vector>(
                metric);
        assertSame(metric, instance.getDivergenceFunction());
    }
        
    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.clustering.divergence.ClusterCompleteLinkDivergenceFunction.
     */
    public void testEvaluate()
    {
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        
        ClusterCompleteLinkDivergenceFunction<DefaultCluster<Vector>, Vector>
            instance = new ClusterCompleteLinkDivergenceFunction<DefaultCluster<Vector>, Vector>(
                metric);
        
        DefaultCluster<Vector> c1 = new DefaultCluster<Vector>();
        c1.getMembers().add(new Vector2(-2.341500, 3.696800));
        c1.getMembers().add(new Vector2(-1.109200, 3.111700));
        c1.getMembers().add(new Vector2(-1.566900, 1.835100));
        c1.getMembers().add(new Vector2(-2.658500, 0.664900));
        c1.getMembers().add(new Vector2(-4.031700, 2.845700)); // Furthest.
        c1.getMembers().add(new Vector2(-3.081000, 2.101100));
        c1.getMembers().add(new Vector2(-1.144400, 0.505300));
        
        DefaultCluster<Vector> c2 = new DefaultCluster<Vector>();
        c2.getMembers().add(new Vector2(2.588000, 1.781900));
        c2.getMembers().add(new Vector2(3.292300, 3.058500));
        c2.getMembers().add(new Vector2(4.031700, 1.622300)); // Furthest.
        c2.getMembers().add(new Vector2(3.081000, -0.611700));
        c2.getMembers().add(new Vector2(0.264100, 0.398900));
        c2.getMembers().add(new Vector2(1.320400, 2.207400));
        c2.getMembers().add(new Vector2(0.193700, 3.643600));
        c2.getMembers().add(new Vector2(1.954200, -0.505300));
        c2.getMembers().add(new Vector2(1.637300, 1.409600));
        c2.getMembers().add(new Vector2(2.095100, 3.430900));

        double expected = c1.getMembers().get(4).euclideanDistance(c2.getMembers().get(2));
        assertEquals(expected, instance.evaluate(c1, c2));
    }
}
