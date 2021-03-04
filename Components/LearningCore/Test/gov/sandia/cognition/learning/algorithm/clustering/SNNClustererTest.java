
/**
 * File:            SNNClustererTest.java
 * Authors:         Melissa Bain
 * Company:         Sandia National Laboratories
 * Project:         Cognitive Foundry
 */
package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultClusterCreator;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.Collection;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * This Class implements JUnit tests for the following classes:
 *
 * SNNClusterer
 *
 * @author mbain
 */
public class SNNClustererTest
{

    /**
     * The distance metric used in tests.
     */
    protected EuclideanDistanceMetric metric = null;

    /**
     * Additional distance semimetric to test non metric functionality.
     */
    protected CosineDistanceMetric semimetric = null;

    /**
     * The cluster creator used in tests.
     */
    protected DefaultClusterCreator<Vector> creator = null;

    /**
     * Creates a new instance of KMeansClustererTest.
     */
    public SNNClustererTest()
    {
        this.semimetric = CosineDistanceMetric.INSTANCE;
        this.metric = EuclideanDistanceMetric.INSTANCE;
        this.creator = new DefaultClusterCreator<>();
    }

    /**
     * Creates a new clusterer to test. Uses the Euclidean distance metric and
     * sets k equal to 2 neighbors.
     *
     * @return A SNN clusterer
     */
    public SNNClusterer<Vector, DefaultCluster<Vector>> createClusterer()
    {
        int k = 2;
        int esp = 2;
        int minPts = 1;
        return new SNNClusterer<Vector, DefaultCluster<Vector>>(
            k, esp, minPts, this.metric, this.creator);
    }

    /**
     * Creates a new clusterer to test. Uses the Cosine distance semimetric,
     * with k equal to 3 neighbors.
     *
     * @return A SNN clusterer
     */
    public SNNClusterer<Vector, DefaultCluster<Vector>> createSemimetricClusterer()
    {
        int k = 3;
        int esp = 2;
        int minPts = 1;
        return new SNNClusterer<Vector, DefaultCluster<Vector>>(
            k, esp, minPts, this.semimetric, this.creator);
    }

    /**
     * Tests the creation of a SNNClusterer. Checks that 0 clusters are present
     * to start with and that the cluster creator of the SNN matches the
     * provided creator.
     */
    @Test
    public void testCreation()
    {
        SNNClusterer<Vector, DefaultCluster<Vector>> snn
            = this.createClusterer();

        assertEquals(0, snn.getClusterCount());
        assertSame(this.creator, snn.getCreator());
    }

    /**
     * Tests the clustering of a SNNClusterer.
     */
    @Test
    public void testClustering()
    {
        //Create instance to test
        SNNClusterer<Vector, DefaultCluster<Vector>> snn
            = this.createClusterer();

        //Create storage for points to cluster
        ArrayList<Vector> elements = new ArrayList<>();

        //Initialize empty cluster collection to use with output
        Collection<DefaultCluster<Vector>> clusters = null;

        //Check clustering with no points
        clusters = snn.learn(elements);

        //Add points to cluster
        Vector2 v1 = new Vector2(1.0, 1.0);
        Vector2 v2 = new Vector2(1.0, 1.2);
        Vector2 v3 = new Vector2(-1.0, 4.0);
        Vector2 v4 = new Vector2(-1.0, 4.2);
        Vector2 v5 = new Vector2(100.0, 100.0);

        //Check functioning with 1 point. Should be clustered as noise
        snn = this.createClusterer();
        elements.add(v1);
        clusters = snn.learn(elements);
        assertEquals(1, clusters.size());
        assertSame(v1, snn.getCluster(0).getMembers().get(0));

        //Add second point and check that they are now clustered as non-noise
        snn = this.createClusterer();
        elements.add(v2);
        clusters = snn.learn(elements);
        assertEquals(2, clusters.size());
        assertEquals(0, snn.getCluster(0).getMembers().size());
        assertEquals(2, snn.getCluster(1).getMembers().size());

        //Add remaining points. Check that there are three clusters with v5 as noise
        snn = this.createClusterer();
        elements.add(v3);
        elements.add(v4);
        elements.add(v5);
        clusters = snn.learn(elements);
        assertEquals(3, clusters.size());
        assertSame(v5, snn.getCluster(0).getMembers().get(0));
        assertEquals(1, snn.getCluster(0).getMembers().size());
        assertEquals(2, snn.getCluster(1).getMembers().size());
        assertEquals(2, snn.getCluster(2).getMembers().size());
    }

    /**
     * Tests SNN capabilities using a Semimetric that is not a Metric.
     */
    @Test
    public void testNonMetric()
    {
        SNNClusterer<Vector, DefaultCluster<Vector>> snn
            = createSemimetricClusterer();

        // Check initialization creates zero clusters
        assertEquals(0, snn.getClusterCount());

        // Create storage for points to cluster
        ArrayList<Vector> elements = new ArrayList<>();

        // Initialize empty cluster collection to use with output
        Collection<DefaultCluster<Vector>> clusters = null;

        // Check clustering with no points
        clusters = snn.learn(elements);

        //Add points to cluster
        Vector2 v1 = new Vector2(1.0, 1.0);
        Vector2 v2 = new Vector2(2.0, 2.0);
        Vector2 v3 = new Vector2(1.0, 2.0);
        Vector2 v4 = new Vector2(1.0, -1.0);
        Vector2 v5 = new Vector2(2.0, -2.0);
        Vector2 v6 = new Vector2(1.0, -2.0);
        Vector2 v7 = new Vector2(-1.0, 0.0);

        elements.add(v1);
        elements.add(v2);
        elements.add(v3);
        elements.add(v4);
        elements.add(v5);
        elements.add(v6);
        elements.add(v7);

        snn = createSemimetricClusterer();
        clusters = snn.learn(elements);

        assertEquals(3, clusters.size());

        // Check noise cluster has 1 point in it (v7) 
        assertEquals(1, snn.getCluster(0).getMembers().size());

        // Check members are as expected         
        ArrayList<Vector> cluster1 = new ArrayList<>();
        cluster1.add(v1);
        cluster1.add(v2);
        cluster1.add(v3);

        Boolean cluster1Same = cluster1.equals(snn.getCluster(1).getMembers());
        assertTrue(cluster1Same);

        ArrayList<Vector> cluster2 = new ArrayList<>();
        cluster2.add(v4);
        cluster2.add(v5);
        cluster2.add(v6);

        Boolean cluster2Same = cluster2.equals(snn.getCluster(2).getMembers());
        assertTrue(cluster2Same);
    }

}
