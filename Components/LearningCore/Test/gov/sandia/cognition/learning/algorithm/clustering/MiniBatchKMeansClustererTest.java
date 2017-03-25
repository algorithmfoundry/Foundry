/*
 * File:                KMeansClustererTest.java
 * Authors:             Justin Basilico
 * Authors:             Jeff Piersol
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 4, 2016, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.MiniBatchCentroidCluster;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanMiniBatchCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.GreedyClusterInitializer;
import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * MiniBatchKMeansClusterer
 *
 * @author Justin Basilico
 * @author Jeff Piersol
 * @since 4.0.0
 */
public class MiniBatchKMeansClustererTest
    extends TestCase
{

    /**
     * The distance metric used in tests.
     */
    protected EuclideanDistanceMetric metric = null;

    /**
     * The cluster creator used in tests.
     */
    protected ClusterCreator<MiniBatchCentroidCluster, Vector> creator = null;

    /**
     * The random number generator used in tests.
     */
    protected Random random = null;

    /**
     * The cluster initializer used in tests.
     */
    protected FixedClusterInitializer<MiniBatchCentroidCluster, Vector> initializer
        = null;

    /**
     * Creates a new instance of MiniBatchKMeansClustererTest.
     *
     * @param testName The test name.
     */
    public MiniBatchKMeansClustererTest(
        String testName)
    {
        super(testName);

        this.metric = EuclideanDistanceMetric.INSTANCE;
        this.creator = VectorMeanMiniBatchCentroidClusterCreator.INSTANCE;
        this.random = new Random();
        this.initializer = new GreedyClusterInitializer<>(
            CosineDistanceMetric.INSTANCE, creator, random);
    }

    /**
     * Creates a new clusterer to test.
     *
     * @return A new cluster to test.
     */
    public MiniBatchKMeansClusterer<Vector> createClusterer()
    {
        return new MiniBatchKMeansClusterer<>(
            0, 100, this.initializer, this.metric, this.creator,
            this.random);
    }

    /**
     * Tests the creation of a MiniBatchKMeansClusterer.
     */
    public void testCreation()
    {
        MiniBatchKMeansClusterer<Vector> kmeans
            = this.createClusterer();

        assertEquals(0, kmeans.getNumClusters());
        assertSame(this.initializer, kmeans.getInitializer());
        assertEquals(this.metric,
            ((CentroidClusterDivergenceFunction) kmeans.getDivergenceFunction()).getDivergenceFunction());
        assertSame(this.creator, kmeans.getCreator());

        kmeans.setNumRequestedClusters(1);
        assertEquals(1, kmeans.getNumRequestedClusters());
    }

    /**
     * Tests the clustering of a MiniBatchKMeansClusterer.
     */
    public void testClustering()
    {
        MiniBatchKMeansClusterer<Vector> kmeans
            = this.createClusterer();
        assertEquals(0, kmeans.getNumElements());

        ArrayList<Vector> elements = new ArrayList<>();
        Collection<MiniBatchCentroidCluster> clusters = null;
        ArrayList<CentroidCluster<Vector>> clustersList = null;

        Vector2 v1 = new Vector2(-2.0, 0.0);
        Vector2 v2 = new Vector2(-1.0, 2.0);
        Vector2 v3 = new Vector2(0.0, 3.0);
        Vector2 v4 = new Vector2(3.0, 1.0);
        Vector2 v5 = new Vector2(3.0, -1.0);

        kmeans.setNumRequestedClusters(0);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(0, clusters.size());

        kmeans.setNumRequestedClusters(1);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(0, clusters.size());

        // Add a vector to the list of elements.
        elements.add(v1);

        // Try giving no clusters to create with a non-empty list of elements.
        kmeans.setNumRequestedClusters(0);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(0, clusters.size());

        // Try creating one cluster from one element.
        kmeans.setNumRequestedClusters(1);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());

        // Try creating two clusters from one element. Should return non-null b/c of greedy initializer.
        kmeans.setNumRequestedClusters(2);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());

        // Add some more elements.
        elements.add(v2);
        elements.add(v3);
        elements.add(v4);
        elements.add(v5);

        // Use spherical k-means
        kmeans.setNumRequestedClusters(2);
        kmeans.setDivergenceFunction(new CentroidClusterDivergenceFunction<>(
            CosineDistanceMetric.INSTANCE));
        kmeans.setMinibatchSize(20);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(2, clusters.size());

//        clustersList = new ArrayList<CentroidCluster<Vector>>(clusters);
        int biggerClusterIdx = kmeans.getCluster(0).getMembers().size()
            >= kmeans.getCluster(1).getMembers().size() ? 0 : 1;
        int smallerClusterIdx = biggerClusterIdx == 1 ? 0 : 1;
        CentroidCluster<Vector> cluster1 = kmeans.getCluster(biggerClusterIdx);
        CentroidCluster<Vector> cluster2 = kmeans.getCluster(smallerClusterIdx);

        assertNotNull(cluster1);
        assertNotNull(cluster2);
        assertEquals(3, cluster1.getMembers().size());
        assertEquals(2, cluster2.getMembers().size());

        Vector centroid1 = cluster1.getCentroid();
        Vector centroid2 = cluster2.getCentroid();
        centroid1.unitVectorEquals(); // part of spherical k-means
        centroid2.unitVectorEquals();

        assertNotNull(centroid1);
        assertNotNull(centroid2);
        assertFalse(centroid1.equals(centroid2));

        assertEquals(-0.514, centroid1.get(0), 0.5);
        assertEquals(0.857, centroid1.get(1), 0.5);
        assertEquals(1, centroid2.get(0), 0.5);
        assertEquals(0, centroid2.get(1), 0.5);
        assertTrue(cluster1.getMembers().contains(v1));
        assertTrue(cluster1.getMembers().contains(v2));
        assertTrue(cluster1.getMembers().contains(v3));
        assertTrue(cluster2.getMembers().contains(v4));
        assertTrue(cluster2.getMembers().contains(v5));

        NamedValue<? extends Number> value = kmeans.getPerformance();
        assertNotNull(value);
        System.out.println("Value: " + value.getName() + " = "
            + value.getValue());

        boolean exceptionThrown = false;
        try
        {
            kmeans.setNumRequestedClusters(-1);
        }
        catch (IllegalArgumentException iae)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}
