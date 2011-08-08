/*
 * File:                MinDistanceSamplingClusterInitializerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.clustering.initializer;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class DistanceSamplingClusterInitializer.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DistanceSamplingClusterInitializerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DistanceSamplingClusterInitializerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class MinDistanceSamplingClusterInitializer.
     */
    public void testClone()
    {

        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        VectorMeanCentroidClusterCreator creator =
            VectorMeanCentroidClusterCreator.INSTANCE;
        Random random = new Random();
        DistanceSamplingClusterInitializer<CentroidCluster<Vector>, Vector> instance =
            new DistanceSamplingClusterInitializer<CentroidCluster<Vector>, Vector>(
                metric, creator, random);

        DistanceSamplingClusterInitializer<CentroidCluster<Vector>, Vector> clone =
            instance.clone();

        assertNotSame(clone, instance);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of clone method, of class MinDistanceSamplingClusterInitializer.
     */
    public void testInitializeClusters()
    {
        // Create the initializer.
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        VectorMeanCentroidClusterCreator creator =
            VectorMeanCentroidClusterCreator.INSTANCE;
        Random random = new Random();
        DistanceSamplingClusterInitializer<CentroidCluster<Vector>, Vector> instance =
            new DistanceSamplingClusterInitializer<CentroidCluster<Vector>, Vector>(
                metric, creator, random);

        ArrayList<CentroidCluster<Vector>> clusters = null;
        ArrayList<Vector> elements = new ArrayList<Vector>();

        // We will be looking at two clusters.
        CentroidCluster<Vector> cluster1 = null;
        CentroidCluster<Vector> cluster2 = null;

        // These are the three datapoints we will use.
        Vector2 v1 = new Vector2(-1.0,  1.0);
        Vector2 v2 = new Vector2( 1.0, -1.0);
        Vector2 v3 = new Vector2( 5.0,  5.0);

        // Make sure that the constructor worked properly.
        assertSame(metric,  instance.getDivergenceFunction());
        assertSame(creator, instance.getCreator());
        assertSame(random,  instance.getRandom());

        // Try calling it with no clusters to initialize.
        clusters = instance.initializeClusters(0, elements);
        assertNotNull(clusters);
        assertEquals(0, clusters.size());

        // Request a cluster but provide no elements.
        clusters = instance.initializeClusters(1, elements);
        assertNotNull(clusters);
        assertEquals(0, clusters.size());

        // Add a vector to the list of elements.
        elements.add(v1);

        // Try giving no clusters to create with a non-empty list of elements.
        clusters = instance.initializeClusters(0, elements);
        assertNotNull(clusters);
        assertEquals(0, clusters.size());

        // Try creating one cluster from one element.
        clusters = instance.initializeClusters(1, elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());

        // The centroid should be equal to the given element.
        cluster1 = clusters.get(0);
        assertNotNull(cluster1);
        assertNotNull(cluster1.getCentroid());
        assertEquals(cluster1.getCentroid(), v1);
        assertNotNull(cluster1.getMembers());
        assertEquals(1, cluster1.getMembers().size());
        assertTrue(cluster1.getMembers().contains(v1));

        // Try creating two clusters from one element. Only one should be
        // returned.
        clusters = instance.initializeClusters(2, elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());


        // Add a second element to the list.
        elements.add(v2);

        // Create 1 cluster.
        clusters = instance.initializeClusters(1, elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());

        // Get the first cluster.
        cluster1 = clusters.get(0);

        // Make sure that the centroid is one of the two elements.
        assertNotNull(cluster1);
        assertNotNull(cluster1.getCentroid());
        assertTrue(cluster1.getCentroid().equals(v1)
                || cluster1.getCentroid().equals(v2));
        assertEquals(1, cluster1.getMembers().size());

        // Create two clusters.
        clusters = instance.initializeClusters(2, elements);
        assertNotNull(clusters);
        assertEquals(2, clusters.size());
        cluster1 = clusters.get(0);
        cluster2 = clusters.get(1);

        // Make sure that the two clusters have different centroids.
        assertNotNull(cluster1);
        assertNotNull(cluster2);
        assertNotNull(cluster1.getCentroid());
        assertNotNull(cluster2.getCentroid());
        assertFalse(cluster1.getCentroid().equals(cluster2.getCentroid()));
        assertEquals(1, cluster1.getMembers().size());
        assertEquals(1, cluster2.getMembers().size());


        // Add a third element.
        elements.add(v3);

        // Create two clusters from three elements.
        clusters = instance.initializeClusters(2, elements);
        assertNotNull(clusters);
        assertEquals(2, clusters.size());

        // Test to make sure exceptions are thrown properly.
        boolean exceptionThrown = false;
        try
        {
            clusters = instance.initializeClusters(-1, elements);
        }
        catch ( IllegalArgumentException iae )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}
