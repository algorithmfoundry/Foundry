/*
 * File:                PartitionalClustererTest.java
 * Authors:             Natasha Singh-Miller
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 25, 2011, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import junit.framework.TestCase;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BinaryClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.ClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import java.util.ArrayList;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.Arrays;
import java.util.Random;

/**
 * Unit tests for class PartitionalClusterer.
 * 
 * @author  Natasha Singh-Miller
 * @since   3.1.1
 */
public class PartitionalClustererTest
    extends TestCase
{

    protected Random random = new Random(211);

    protected CosineDistanceMetric cosineDistanceMetric;

    protected VectorMeanCentroidClusterCreator creator;

    protected CentroidClusterDivergenceFunction<Vector> clusterDivergenceFunction;

    /**
     * Creates a new test.
     *
     * @param testName  The name.
     */
    public PartitionalClustererTest(
        String testName)
    {
        super(testName);
        this.cosineDistanceMetric = CosineDistanceMetric.INSTANCE;
        this.creator = VectorMeanCentroidClusterCreator.INSTANCE;
        this.clusterDivergenceFunction = new CentroidClusterDivergenceFunction<Vector>(
            this.cosineDistanceMetric);
    }

    /**
     * Test of constants of class PartitionClusterer.
     */
    public void testConstants()
    {
        assertEquals(1.0, PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE);
        assertEquals(1, PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE);
        assertEquals(Integer.MAX_VALUE,
            PartitionalClusterer.DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Test of constructors of class PartitionClusterer.
     */
    public void testConstructors()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertNull(instance.getDivergenceFunction());
        assertNull(instance.getCreator());
        assertEquals(instance.getMaxCriterionDecrease(),
            PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE);
        assertEquals(instance.getMinClusterSize(),
            PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE);

        Random random = new Random();
        instance = new PartitionalClusterer<Vector, CentroidCluster<Vector>>(
            this.clusterDivergenceFunction, this.creator, random);
        assertSame(this.clusterDivergenceFunction,
            instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(instance.getMaxCriterionDecrease(),
            PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE);
        assertEquals(instance.getMinClusterSize(),
            PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE);
        assertSame(random, instance.getRandom());

        int minClusterSize = 4;
        double maxCriterionDecrease = 0.95;
        instance = new PartitionalClusterer<Vector, CentroidCluster<Vector>>(
            this.clusterDivergenceFunction, this.creator, minClusterSize,
            maxCriterionDecrease, random);
        assertSame(this.clusterDivergenceFunction,
            instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(minClusterSize, instance.getMinClusterSize());
        assertEquals(minClusterSize, instance.getMinClusterSize());
        assertEquals(instance.getMaxCriterionDecrease(),
            maxCriterionDecrease);
        assertSame(random, instance.getRandom());

        int maxIterations = 99;
        instance = new PartitionalClusterer<Vector, CentroidCluster<Vector>>(
            this.clusterDivergenceFunction, this.creator, maxIterations,
            minClusterSize, maxCriterionDecrease, random);
        assertSame(this.clusterDivergenceFunction,
            instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minClusterSize,
            instance.getMinClusterSize());
        assertEquals(instance.getMaxCriterionDecrease(),
            maxCriterionDecrease);
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of clone method, of class PartitionClusterer.
     */
    public void testClone()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>(
            this.clusterDivergenceFunction, this.creator, 4,
            0.95, new Random());

        PartitionalClusterer<Vector, CentroidCluster<Vector>> clone =
            instance.clone();

        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotNull(clone.getDivergenceFunction());
        assertNotSame(instance.getDivergenceFunction(),
            clone.getDivergenceFunction());
        assertNotNull(clone.getCreator());
        assertNotSame(instance.getCreator(), clone.getCreator());
        assertNotNull(clone.getRandom());
    }

    /**
     * Test of cluster method, of class PartitionClusterer.
     */
    public void testCluster()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>(
            this.clusterDivergenceFunction, this.creator, 7,
            1.0, this.random);

        Vector[] data1 = new Vector[]
        {
            new Vector2(-2.0, -2.0),
            new Vector2(-1.0, -1.01),
            new Vector2(-2.01, -2.02),
            new Vector2(-1.99, -1.98),
            new Vector2(-1.5, -1.54),
            new Vector2(-0.99, -0.98),
            new Vector2(-1.1, -1.0),
        };

        Vector[] data2 = new Vector[]
        {
            new Vector2(2.0, 2.0),
            new Vector2(2.1, 2.2),
            new Vector2(1.0, 1.01),
            new Vector2(1.0, 0.99),
            new Vector2(0.3, 0.32),
            new Vector2(0.6, 0.59),
            new Vector2(1.7, 1.71)
        };

        Vector[] data3 = new Vector[]
        {
            new Vector2(2.5, -2.51),
            new Vector2(3.2, -3.1),
            new Vector2(4.0, -4.04),
            new Vector2(1.3, -1.33),
            new Vector2(1.6, -1.59),
            new Vector2(2.0, -2.01),
            new Vector2(4.5, -4.4)
        };

        ArrayList<Vector> data = new ArrayList<Vector>();
        data.addAll(Arrays.asList(data1));
        data.addAll(Arrays.asList(data2));
        data.addAll(Arrays.asList(data3));

        ArrayList<CentroidCluster<Vector>> clusters =
            new ArrayList<CentroidCluster<Vector>>(instance.learn(data));
        assertEquals(clusters.size(), 5);

        ClusterHierarchyNode<Vector, CentroidCluster<Vector>> root =
            instance.clusterHierarchically(data);
        assertNotNull(root);
        assertNotNull(root.getCluster());
        assertEquals(2, root.getChildren().size());
        assertTrue(root instanceof BinaryClusterHierarchyNode<?, ?>);
        assertEquals(21, root.getMembers().size());
    }

    /**
     * Test of getCusterCount method, of class PartitionClusterer.
     */
    public void testGetClusterCount()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals(0, instance.getClusterCount());
    }

    /**
     * Test of getRandom method, of class PartitionClusterer.
     */
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class PartitionClusterer.
     */
    public void testSetRandom()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of getDivergenceFunction method, of class PartitionClusterer.
     */
    public void testGetDivergenceFunction()
    {
        this.testSetDivergenceFunction();
    }

    /**
     * Test of setDivergenceFunction method, of class PartitionClusterer.
     */
    public void testSetDivergenceFunction()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertNull(instance.getDivergenceFunction());

        instance.setDivergenceFunction(this.clusterDivergenceFunction);
        assertSame(this.clusterDivergenceFunction,
            instance.getDivergenceFunction());

        instance.setDivergenceFunction(null);
        assertNull(instance.getDivergenceFunction());
    }

    /**
     * Test of getCreator method, of class PartitionClusterer.
     */
    public void testGetCreator()
    {
        this.testSetCreator();
    }

    /**
     * Test of setCreator method, of class PartitionClusterer.
     */
    public void testSetCreator()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertNull(instance.getCreator());

        instance.setCreator(this.creator);
        assertSame(this.creator, instance.getCreator());

        instance.setCreator(null);
        assertNull(instance.getCreator());
    }

    /**
     * Test of getMinClusterSize method, of class PartitionClusterer.
     */
    public void testGetMinClusterSizes()
    {
        this.testSetMinClusterSize();
    }

    /**
     * Test of setMinNumClusters method, of class PartitionClusterer.
     */
    public void testSetMinClusterSize()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals(PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE,
            instance.getMinClusterSize());

        int minClusterSize = 4;
        instance.setMinClusterSize(minClusterSize);
        assertEquals(minClusterSize, instance.getMinClusterSize());

        boolean exceptionThrown = false;
        try
        {
            instance.setMinClusterSize(-3);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(minClusterSize, instance.getMinClusterSize());
    }

    /**
     * Test of getMaxCriterionDecrease method, of class PartitionClusterer.
     */
    public void testGetMaxCriterionDecrease()
    {
        this.testSetMaxCriterionDecrease();
    }

    /**
     * Test of setMaxCriterionDecrease method, of class PartitionClusterer.
     */
    public void testSetMaxCriterionDecrease()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance =
            new PartitionalClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals(PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE,
            instance.getMaxCriterionDecrease());

        double maxCriterionDecrease = this.random.nextDouble();
        instance.setMaxCriterionDecrease(maxCriterionDecrease);
        assertEquals(maxCriterionDecrease, instance.getMaxCriterionDecrease());

        boolean exceptionThrown = false;
        try
        {
        instance.setMaxCriterionDecrease(-this.random.nextDouble());
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(maxCriterionDecrease, instance.getMaxCriterionDecrease());
    }

}
