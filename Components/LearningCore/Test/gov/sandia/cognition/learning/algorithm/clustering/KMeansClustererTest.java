/*
 * File:                KMeansClustererTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.GreedyClusterInitializer;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
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
 *     KMeansClusterer
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class KMeansClustererTest
    extends TestCase
{

    /** The distance metric used in tests. */
    protected EuclideanDistanceMetric metric = null;

    /** The cluster creator used in tests. */
    protected VectorMeanCentroidClusterCreator creator = null;

    /** The random number generator used in tests. */
    protected Random random = null;

    /** The cluster initializer used in tests. */
    protected GreedyClusterInitializer<CentroidCluster<Vector>, Vector> initializer = null;

    /** The cluster divergence functio used in tests. */
    protected CentroidClusterDivergenceFunction<Vector> clusterMetric = null;

    /**
     * Creates a new instance of KMeansClustererTest.
     *
     * @param  testName The test name.
     */
    public KMeansClustererTest(
        String testName)
    {
        super(testName);

        this.metric = EuclideanDistanceMetric.INSTANCE;
        this.creator = VectorMeanCentroidClusterCreator.INSTANCE;
        this.random = new Random();
        this.initializer =
            new GreedyClusterInitializer<CentroidCluster<Vector>, Vector>(
            metric, creator, random);
        this.clusterMetric =
            new CentroidClusterDivergenceFunction<Vector>(metric);
    }

    /**
     * Creates a new clusterer to test.
     *
     * @return A new cluster to test.
     */
    public KMeansClusterer<Vector, CentroidCluster<Vector>> createClusterer()
    {
        return new KMeansClusterer<Vector, CentroidCluster<Vector>>(
            0, 100, this.initializer, this.clusterMetric, this.creator);
    }

    /**
     * Tests the creation of a KMeansClusterer.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCreation()
    {
        KMeansClusterer<Vector, CentroidCluster<Vector>> kmeans =
            this.createClusterer();

        assertEquals(0, kmeans.getNumClusters());
        assertSame(this.initializer, kmeans.getInitializer());
        assertEquals(this.clusterMetric, kmeans.getDivergenceFunction());
        assertSame(this.creator, kmeans.getCreator());

        kmeans.setNumRequestedClusters(1);
        assertEquals(1, kmeans.getNumRequestedClusters());
    }

    /**
     * Tests the clustering of a KMeansClusterer.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testClustering()
    {
        KMeansClusterer<Vector, CentroidCluster<Vector>> kmeans =
            this.createClusterer();
        assertEquals( 0, kmeans.getNumElements() );

        ArrayList<Vector> elements = new ArrayList<Vector>();
        Collection<CentroidCluster<Vector>> clusters = null;
        ArrayList<CentroidCluster<Vector>> clustersList = null;

        Vector2 v1 = new Vector2(1.0, 1.0);
        Vector2 v2 = new Vector2(1.0, 1.2);
        Vector2 v3 = new Vector2(4.0, 4.0);
        Vector2 v4 = new Vector2(4.0, 4.2);

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
        
        // Try creating two clusters from one element. Only one should be
        // returned.
        kmeans.setNumRequestedClusters(2);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());

        // Add some more elements.
        elements.add(v2);
        elements.add(v3);
        elements.add(v4);

        kmeans.setNumRequestedClusters(2);
        clusters = kmeans.learn(elements);
        assertNotNull(clusters);
        assertEquals(2, clusters.size());

//        clustersList = new ArrayList<CentroidCluster<Vector>>(clusters);
        CentroidCluster<Vector> cluster1 = kmeans.getCluster(0);
        CentroidCluster<Vector> cluster2 = kmeans.getCluster(1);

        assertNotNull(cluster1);
        assertNotNull(cluster2);
        assertEquals(2, cluster1.getMembers().size());
        assertEquals(2, cluster2.getMembers().size());

        Vector centroid1 = cluster1.getCentroid();
        Vector centroid2 = cluster2.getCentroid();

        assertNotNull(centroid1);
        assertNotNull(centroid1);
        assertFalse(centroid1.equals(centroid2));

        if (metric.evaluate(v1, centroid1) < metric.evaluate(v1, centroid2))
        {
            assertEquals(new Vector2(1.0, 1.1), centroid1);
            assertEquals(new Vector2(4.0, 4.1), centroid2);
            assertTrue(cluster1.getMembers().contains(v1));
            assertTrue(cluster1.getMembers().contains(v2));
            assertTrue(cluster2.getMembers().contains(v3));
            assertTrue(cluster2.getMembers().contains(v4));
        }
        else
        {
            assertEquals(new Vector2(1.0, 1.1), centroid2);
            assertEquals(new Vector2(4.0, 4.1), centroid1);
            assertTrue(cluster2.getMembers().contains(v1));
            assertTrue(cluster2.getMembers().contains(v2));
            assertTrue(cluster1.getMembers().contains(v3));
            assertTrue(cluster1.getMembers().contains(v4));
        }

        NamedValue<? extends Number> value = kmeans.getPerformance();
        assertNotNull( value );
        System.out.println( "Value: " + value.getName() + " = " + value.getValue() );


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
