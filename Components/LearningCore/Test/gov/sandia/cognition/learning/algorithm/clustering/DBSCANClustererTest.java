/*
 * File:                DBSCANClustererTest.java
 * Authors:             Quinn McNamara
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2016, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultClusterCreator;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;

/**
 * This class implements JUnit tests for the following classes:
 *
 * DBSCANClusterer
 *
 * @author Quinn McNamara
 * @since 1.0
 */
public class DBSCANClustererTest
    extends TestCase
{

    /**
     * The distance metric used in tests.
     */
    protected EuclideanDistanceMetric metric = null;

    /**
     * The cluster creator used in tests.
     */
    protected DefaultClusterCreator<Vector> creator = null;

    /**
     * Creates a new instance of KMeansClustererTest.
     *
     * @param testName The test name.
     */
    public DBSCANClustererTest(
        String testName)
    {
        super(testName);

        this.metric = EuclideanDistanceMetric.INSTANCE;
        this.creator = new DefaultClusterCreator<Vector>();
    }

    /**
     * Creates a new clusterer to test.
     *
     * @return A new cluster to test.
     */
    public DBSCANClusterer<Vector, DefaultCluster<Vector>> createClusterer()
    {
        return new DBSCANClusterer<Vector, DefaultCluster<Vector>>(
            1.0, 2, this.metric, this.creator);
    }

    /**
     * Tests the creation of a spatial index
     *
     * If this test fails, contact Quinn McNamara.
     */
    public void testSpatialIndex()
    {
        DBSCANClusterer<Vector, DefaultCluster<Vector>> dbscan
            = this.createClusterer();
        ArrayList<Vector> elements = new ArrayList<Vector>();
        elements.add(new Vector2(1.0, 1.0));
        Collection<DefaultCluster<Vector>> clusters = dbscan.learn(elements);

        // We are using Euclidean distance and Vectors,
        // so the spatial index should be used.
        assertNotNull(dbscan.getSpatialIndex());
    }

    /**
     * Tests the creation of a KMeansClusterer.
     *
     * If this test fails, contact Quinn McNamara.
     */
    public void testCreation()
    {
        DBSCANClusterer<Vector, DefaultCluster<Vector>> dbscan
            = this.createClusterer();

        assertEquals(0, dbscan.getClusterCount());
        assertSame(this.creator, dbscan.getCreator());
    }

    /**
     * Tests the clustering of a KMeansClusterer.
     *
     * If this test fails, contact Quinn McNamara.
     */
    public void testClustering()
    {
        DBSCANClusterer<Vector, DefaultCluster<Vector>> dbscan
            = this.createClusterer();

        ArrayList<Vector> elements = new ArrayList<Vector>();
        Collection<DefaultCluster<Vector>> clusters = null;
        ArrayList<DefaultCluster<Vector>> clustersList = null;

        Vector2 v1 = new Vector2(1.0, 1.0);
        Vector2 v2 = new Vector2(1.0, 1.2);
        Vector2 v3 = new Vector2(-1.0, 4.0);
        Vector2 v4 = new Vector2(-1.0, 4.2);
        Vector2 v5 = new Vector2(100.0, 100.0);

        clusters = dbscan.learn(elements);
        assertNull(clusters);

        // Add a vector to the list of elements.
        elements.add(v1);

        // Should give a noise cluster only.
        dbscan = this.createClusterer();
        clusters = dbscan.learn(elements);
        assertNotNull(clusters);
        assertEquals(1, clusters.size());

        // Add some more elements.
        elements.add(v2);
        elements.add(v3);
        elements.add(v4);

        // Should give two content clusters and one noise cluster
        dbscan = this.createClusterer();
        clusters = dbscan.learn(elements);
        assertNotNull(clusters);
        assertEquals(3, clusters.size());

        DefaultCluster<Vector> cluster1 = dbscan.getCluster(0);
        DefaultCluster<Vector> cluster2 = dbscan.getCluster(1);
        DefaultCluster<Vector> cluster3 = dbscan.getCluster(2);

        // v1 and v2 should be clustered, v3 and v4 should be clustered
        assertNotNull(cluster1);
        assertNotNull(cluster2);
        assertNotNull(cluster3);
        assertEquals(0, cluster1.getMembers().size());
        assertEquals(2, cluster2.getMembers().size());
        assertEquals(2, cluster3.getMembers().size());

        // test DBSCAN with a noise point
        dbscan = this.createClusterer();
        elements.add(v5);

        // Should give two content clusters and one noise cluster
        clusters = dbscan.learn(elements);
        assertNotNull(clusters);
        assertEquals(3, clusters.size());

        cluster1 = dbscan.getCluster(0);
        cluster2 = dbscan.getCluster(1);
        cluster3 = dbscan.getCluster(2);

        // v1 and v2 should be clustered, v3 and v4 should be clustered
        assertNotNull(cluster1);
        assertNotNull(cluster2);
        assertNotNull(cluster3);
        assertEquals(1, cluster1.getMembers().size());
        assertEquals(2, cluster2.getMembers().size());
        assertEquals(2, cluster3.getMembers().size());
    }

    /**
     * Tests for NullPointerExceptions
     *
     * If this test fails, contact Quinn McNamara
     */
    public void testClusteringAvoidNulls()
    {
        DBSCANClusterer<Vector, DefaultCluster<Vector>> instance
            = this.createClusterer();

        ArrayList<Vector> data = new ArrayList<Vector>();

        Vector2 v1 = new Vector2(1.0, 1.0);
        Vector2 v2 = new Vector2(2.0, 2.0);
        data.add(v1.clone());
        data.add(v1.clone());
        data.add(v2.clone());
        data.add(v1.clone());
        data.add(v2.clone());
        data.add(v2.clone());

        Collection<DefaultCluster<Vector>> result = instance.learn(data);
    }

}
