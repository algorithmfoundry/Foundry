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

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import junit.framework.TestCase;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BinaryClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.NormalizedCentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.ClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.WithinClusterDivergence;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.WithinNormalizedCentroidClusterCosineDivergence;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.NormalizedCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import java.util.ArrayList;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * Unit tests for class PartitionalClusterer.
 *
 * @author Natasha Singh-Miller
 * @author Andrew Fisher
 * @since 3.1.1
 */
public class PartitionalClustererTest
    extends TestCase
{

    protected Random random = new Random(211);

    protected CosineDistanceMetric cosineDistanceMetric;

    protected VectorMeanCentroidClusterCreator creator;

    protected NormalizedCentroidClusterCreator norm_creator;

    protected CentroidClusterDivergenceFunction<Vector> clusterDivergenceFunction;

    protected WithinClusterDivergence<NormalizedCentroidCluster<Vectorizable>, Vectorizable> withinClusterDivergenceFunction;

    protected ArrayList<Vector> data;

    /**
     * Creates a new test.
     *
     * @param testName The name.
     */
    public PartitionalClustererTest(
        String testName)
    {
        super(testName);
        this.cosineDistanceMetric = CosineDistanceMetric.INSTANCE;
        this.creator = VectorMeanCentroidClusterCreator.INSTANCE;
        this.norm_creator = new NormalizedCentroidClusterCreator();
        this.clusterDivergenceFunction
            = new CentroidClusterDivergenceFunction<Vector>(
                this.cosineDistanceMetric);
        this.withinClusterDivergenceFunction
            = new WithinNormalizedCentroidClusterCosineDivergence<>();

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

        ArrayList<Vector> data = new ArrayList<>();
        data.addAll(Arrays.asList(data1));
        data.addAll(Arrays.asList(data2));
        data.addAll(Arrays.asList(data3));

        this.data = data;
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
    public void testWithinDivergenceConstructors()
    {
        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> instance
            = new PartitionalClusterer<>(99,
                this.withinClusterDivergenceFunction,
                this.norm_creator);
        assertSame(this.withinClusterDivergenceFunction,
            instance.getWithinClusterDivergenceFunction());
        assertSame(this.norm_creator, instance.getCreator());
        assertEquals(instance.getMaxCriterionDecrease(),
            PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE);
        assertEquals(instance.getMinClusterSize(),
            PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE);

        int minClusterSize = 4;
        double maxCriterionDecrease = 0.95;
        instance.setMinClusterSize(minClusterSize);
        instance.setMaxCriterionDecrease(maxCriterionDecrease);
        instance.setRandom(random);
        assertEquals(minClusterSize, instance.getMinClusterSize());
        assertEquals(instance.getMaxCriterionDecrease(), maxCriterionDecrease);
        assertSame(random, instance.getRandom());

        int numRequestedClusters = 99;
        assertEquals(numRequestedClusters, instance.getNumRequestedClusters());
    }

    /**
     * Test of constructors of class PartitionClusterer.
     */
    public void testDivergenceConstructors()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(99, this.clusterDivergenceFunction,
                this.creator);
        assertSame(this.clusterDivergenceFunction,
            instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(instance.getMaxCriterionDecrease(),
            PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE);
        assertEquals(instance.getMinClusterSize(),
            PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE);

        int minClusterSize = 4;
        double maxCriterionDecrease = 0.95;
        instance.setMinClusterSize(minClusterSize);
        instance.setMaxCriterionDecrease(maxCriterionDecrease);
        instance.setRandom(random);
        assertEquals(minClusterSize, instance.getMinClusterSize());
        assertEquals(instance.getMaxCriterionDecrease(), maxCriterionDecrease);
        assertSame(random, instance.getRandom());

        int numRequestedClusters = 99;
        assertEquals(numRequestedClusters, instance.getNumRequestedClusters());
    }

    /**
     * Test of clone method, of class PartitionClusterer.
     */
    public void testClone()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(99, this.clusterDivergenceFunction,
                this.creator);

        instance.setMinClusterSize(4);
        instance.setMaxCriterionDecrease(0.95);
        instance.setRandom(random);

        PartitionalClusterer<Vector, CentroidCluster<Vector>> clone
            = instance.clone();

        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotNull(clone.getDivergenceFunction());
        assertNotSame(instance.getDivergenceFunction(),
            clone.getDivergenceFunction());
        assertNotNull(clone.getCreator());
        assertNotSame(instance.getCreator(), clone.getCreator());
        assertNotNull(clone.getRandom());

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = new PartitionalClusterer<>(99,
                this.withinClusterDivergenceFunction, this.norm_creator);
        norm_instance.setMinClusterSize(4);
        norm_instance.setMaxCriterionDecrease(0.95);
        norm_instance.setRandom(random);

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_clone
            = norm_instance.clone();

        assertNotNull(norm_clone);
        assertNotSame(norm_instance, norm_clone);
        assertNotNull(clone.getWithinClusterDivergenceFunction());
        assertNotSame(instance.getWithinClusterDivergenceFunction(),
            clone.getWithinClusterDivergenceFunction());
        assertNotNull(clone.getCreator());
        assertNotSame(instance.getCreator(), clone.getCreator());
        assertNotNull(clone.getRandom());
    }

    /**
     * Test of cluster method, of class PartitionClusterer.
     */
    public void testCluster()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = PartitionalClusterer.create(Integer.MAX_VALUE);

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = PartitionalClusterer.createSpherical(Integer.MAX_VALUE);

        instance.setNumRequestedClusters(2);
        Collection<CentroidCluster<Vector>> clusters = instance.learn(this.data);
        assertEquals(2, clusters.size());
        assertEquals(3, instance.getClusterCount());

        instance.setNumRequestedClusters(3);
        clusters = instance.learn(this.data);
        assertEquals(3, clusters.size());
        assertEquals(5, instance.getClusterCount());

        ClusterHierarchyNode<Vector, CentroidCluster<Vector>> root
            = instance.clusterHierarchically(this.data);
        assertNotNull(root);
        assertNotNull(root.getCluster());
        assertEquals(2, root.getChildren().size());
        assertTrue(root instanceof BinaryClusterHierarchyNode<?, ?>);
        assertEquals(21, root.getMembers().size());

        norm_instance.setNumRequestedClusters(2);
        Collection<NormalizedCentroidCluster<Vectorizable>> norm_clusters
            = norm_instance.learn(this.data);
        assertEquals(2, norm_clusters.size());
        assertEquals(3, norm_instance.getClusterCount());

        norm_instance.setNumRequestedClusters(3);
        norm_clusters = norm_instance.learn(this.data);
        assertEquals(3, norm_clusters.size());
        assertEquals(5, norm_instance.getClusterCount());

        ClusterHierarchyNode<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_root
            = norm_instance.clusterHierarchically(this.data);
        assertNotNull(norm_root);
        assertNotNull(norm_root.getCluster());
        assertEquals(2, norm_root.getChildren().size());
        assertTrue(norm_root instanceof BinaryClusterHierarchyNode<?, ?>);
        assertEquals(21, norm_root.getMembers().size());
    }

    public void testLearnOnDemand()
    {
        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = PartitionalClusterer.createSpherical(Integer.MAX_VALUE);

        norm_instance.setNumRequestedClusters(2);
        Collection<NormalizedCentroidCluster<Vectorizable>> norm_clusters
            = norm_instance.learnUsingCachedClusters(this.data);
        assertEquals(2, norm_clusters.size());
        assertEquals(3, norm_instance.getClusterCount());

        norm_clusters = norm_instance.learnUsingCachedClusters(this.data);
        assertEquals(2, norm_clusters.size());
        assertEquals(3, norm_instance.getClusterCount());

        norm_instance.setNumRequestedClusters(3);
        norm_clusters = norm_instance.learnUsingCachedClusters(this.data);
        assertEquals(3, norm_clusters.size());
        assertEquals(5, norm_instance.getClusterCount());

        norm_instance.setNumRequestedClusters(2);
        norm_clusters = norm_instance.learnUsingCachedClusters(this.data);
        assertEquals(2, norm_clusters.size());
        assertEquals(5, norm_instance.getClusterCount());

        norm_instance.setNumRequestedClusters(1);
        norm_clusters = norm_instance.learnUsingCachedClusters(this.data);
        assertEquals(1, norm_clusters.size());
        assertEquals(5, norm_instance.getClusterCount());
    }

    /**
     * Test of getClusterCount method, of class PartitionClusterer.
     */
    public void testGetClusterCount()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(0, clusterDivergenceFunction, creator);
        assertEquals(0, instance.getClusterCount());

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = new PartitionalClusterer<>(0, withinClusterDivergenceFunction,
                norm_creator);
        assertEquals(0, norm_instance.getClusterCount());
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
        CentroidClusterDivergenceFunction<Vector> newDivFct
            = new CentroidClusterDivergenceFunction<>(
                EuclideanDistanceMetric.INSTANCE);
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(0, newDivFct, creator);

        instance.setDivergenceFunction(this.clusterDivergenceFunction);
        assertNotSame(newDivFct,
            instance.getDivergenceFunction());
        assertSame(this.clusterDivergenceFunction,
            instance.getDivergenceFunction());

        boolean exceptionThrown = false;
        try
        {
            instance.setDivergenceFunction(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of setDivergenceFunction method, of class PartitionClusterer.
     */
    public void testSetWithinDivergenceFunction()
    {
        WithinClusterDivergence<NormalizedCentroidCluster<Vectorizable>, Vectorizable> newDivFct
            = new WithinNormalizedCentroidClusterCosineDivergence<>();

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> instance
            = new PartitionalClusterer<>(0, newDivFct, norm_creator);

        instance.setWithinClusterDivergenceFunction(
            this.withinClusterDivergenceFunction);
        assertNotSame(newDivFct, instance.getDivergenceFunction());
        assertSame(this.withinClusterDivergenceFunction,
            instance.getWithinClusterDivergenceFunction());

        boolean exceptionThrown = false;
        try
        {
            instance.setWithinClusterDivergenceFunction(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of setCreator method, of class PartitionClusterer.
     */
    public void testSetCreator()
    {
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(5, this.clusterDivergenceFunction,
                this.creator);

        VectorMeanCentroidClusterCreator creator2
            = new VectorMeanCentroidClusterCreator();

        assertNotNull(instance.getCreator());
        instance.setCreator(creator2);
        assertNotSame(this.creator, instance.getCreator());
        assertSame(creator2, instance.getCreator());

        boolean exceptionThrown = false;
        try
        {
            instance.setCreator(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = new PartitionalClusterer<>(5, this.withinClusterDivergenceFunction,
                this.norm_creator);

        NormalizedCentroidClusterCreator norm_creator2
            = new NormalizedCentroidClusterCreator();

        norm_instance.setCreator(norm_creator2);
        assertNotSame(this.norm_creator, norm_instance.getCreator());
        assertSame(norm_creator2, norm_instance.getCreator());

        exceptionThrown = false;
        try
        {
            instance.setCreator(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
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
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(5, this.clusterDivergenceFunction,
                this.creator);
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

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = new PartitionalClusterer<>(5, this.withinClusterDivergenceFunction,
                this.norm_creator);
        assertEquals(PartitionalClusterer.DEFAULT_MIN_CLUSTER_SIZE,
            norm_instance.getMinClusterSize());

        norm_instance.setMinClusterSize(minClusterSize);
        assertEquals(minClusterSize, norm_instance.getMinClusterSize());

        try
        {
            norm_instance.setMinClusterSize(-3);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(minClusterSize, norm_instance.getMinClusterSize());
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
        PartitionalClusterer<Vector, CentroidCluster<Vector>> instance
            = new PartitionalClusterer<>(5, this.clusterDivergenceFunction,
                this.creator);
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

        PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> norm_instance
            = new PartitionalClusterer<>(5, this.withinClusterDivergenceFunction,
                this.norm_creator);
        assertEquals(PartitionalClusterer.DEFAULT_MAX_CRITERION_DECREASE,
            norm_instance.getMaxCriterionDecrease());

        norm_instance.setMaxCriterionDecrease(maxCriterionDecrease);
        assertEquals(maxCriterionDecrease,
            norm_instance.getMaxCriterionDecrease());

        try
        {
            norm_instance.setMaxCriterionDecrease(-this.random.nextDouble());
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(maxCriterionDecrease,
            norm_instance.getMaxCriterionDecrease());
    }

}
