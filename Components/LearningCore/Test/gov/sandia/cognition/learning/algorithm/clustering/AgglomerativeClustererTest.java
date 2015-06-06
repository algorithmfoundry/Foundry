/*
 * File:                AgglomerativeClustererTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BinaryClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.ClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterMeanLinkDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterToClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: AgglomerativeClusterer
 *
 * @author Justin Basilico
 * @since  2.0
 */

public class AgglomerativeClustererTest
    extends TestCase
{
    /** The distance metric used in tests. */
    protected EuclideanDistanceMetric metric;
    
    /** The cluster creator used in tests. */
    protected VectorMeanCentroidClusterCreator creator;
    
    /** The cluster divergence function used in tests. */
    protected ClusterToClusterDivergenceFunction<CentroidCluster<Vector>, Vector> clusterMetric;
    
    public AgglomerativeClustererTest(
        String testName)
    {
        super(testName);
        
        this.metric = EuclideanDistanceMetric.INSTANCE;
        this.creator = VectorMeanCentroidClusterCreator.INSTANCE;
        this.clusterMetric = new ClusterMeanLinkDivergenceFunction<CentroidCluster<Vector>, Vector>(
            this.metric);
    }
    
    @SuppressWarnings("deprecation")
    public void testConstants()
    {
        assertEquals(1, AgglomerativeClusterer.DEFAULT_MIN_NUM_CLUSTERS);
        assertEquals(Double.MAX_VALUE, AgglomerativeClusterer.DEFAULT_MAX_DISTANCE);
        assertEquals(Double.MAX_VALUE, AgglomerativeClusterer.DEFAULT_MAX_MIN_DISTANCE);
    }
    
    public void testConstructors()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance
            = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>();
        assertNull(instance.getDivergenceFunction());
        assertNull(instance.getCreator());
        assertEquals(AgglomerativeClusterer.DEFAULT_MIN_NUM_CLUSTERS, instance.getMinNumClusters());
        assertEquals(AgglomerativeClusterer.DEFAULT_MAX_DISTANCE, instance.getMaxDistance());
        
        instance = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>(
                this.clusterMetric, this.creator);
        assertSame(this.clusterMetric, instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(AgglomerativeClusterer.DEFAULT_MIN_NUM_CLUSTERS, instance.getMinNumClusters());
        assertEquals(AgglomerativeClusterer.DEFAULT_MAX_DISTANCE, instance.getMaxDistance());
        
        int minNumClusters = 4;
        instance = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>(
                this.clusterMetric, this.creator, minNumClusters);
        assertSame(this.clusterMetric, instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(minNumClusters, instance.getMinNumClusters());
        assertEquals(AgglomerativeClusterer.DEFAULT_MAX_DISTANCE, instance.getMaxDistance());
        
        double maxDistance = 0.47;
        instance = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>(
                this.clusterMetric, this.creator, maxDistance);
        assertSame(this.clusterMetric, instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(AgglomerativeClusterer.DEFAULT_MIN_NUM_CLUSTERS, instance.getMinNumClusters());
        assertEquals(maxDistance, instance.getMaxDistance());
        
        instance = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>(
                this.clusterMetric, this.creator, minNumClusters, maxDistance);
        assertSame(this.clusterMetric, instance.getDivergenceFunction());
        assertSame(this.creator, instance.getCreator());
        assertEquals(minNumClusters, instance.getMinNumClusters());
        assertEquals(maxDistance, instance.getMaxDistance());
        
    }

    public void testClone()
    {

        System.out.println( "Clone" );
        
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance =
            new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>(
                this.clusterMetric, this.creator);

        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> clone = instance.clone();

        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getDivergenceFunction() );
        assertNotSame( instance.getDivergenceFunction(), clone.getDivergenceFunction() );
        assertNotNull( clone.getCreator() );
        assertNotSame( instance.getCreator(), clone.getCreator() );

    }

    /**
     * Test of cluster method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testCluster()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance = 
            new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>(
                this.clusterMetric, this.creator);
        
        Vector[] data1 = new Vector[]
        {
            new Vector2(-2.341500, 3.696800),
            new Vector2(-1.109200, 3.111700),
            new Vector2(-1.566900, 1.835100),
            new Vector2(-2.658500, 0.664900),
            new Vector2(-4.031700, 2.845700),
            new Vector2(-3.081000, 2.101100),
            new Vector2(-1.144400, 0.505300),
        };
        
        Vector[] data2 = new Vector[]
        {
            new Vector2(-0.123200, -1.516000),
            new Vector2(-1.355600, -3.058500),
            new Vector2(0.017600, -4.016000),
            new Vector2(1.003500, -3.590400),
            new Vector2(0.017600, -2.420200),
            new Vector2(0.616200, -1.516000),
            new Vector2(1.707700, -2.207400)
            // new Vector2(-1.531700, -0.930900),
        };
        
        Vector[] data3 = new Vector[]
        {
            new Vector2(2.588000, 1.781900),
            new Vector2(3.292300, 3.058500),
            new Vector2(4.031700, 1.622300),
            new Vector2(1.320400, 2.207400),
            new Vector2(1.637300, 1.409600),
            new Vector2(2.095100, 3.430900)
            // new Vector2(0.264100, 0.398900),
            // new Vector2(0.193700, 3.643600),
            // new Vector2(1.954200, -0.505300),
            // new Vector2(3.081000, -0.611700),
        };
        
        ArrayList<Vector> data = new ArrayList<Vector>();
        data.addAll(Arrays.asList(data1));
        data.addAll(Arrays.asList(data2));
        data.addAll(Arrays.asList(data3));
        
        instance.setMinNumClusters(3);
        Collection<CentroidCluster<Vector>> clusters = instance.learn(data);
        assertEquals(3, instance.getNumClusters());

        ClusterHierarchyNode<?,?> root = instance.clusterHierarchically(data);
        assertNotNull(root);
        assertNotNull(root.getCluster());
        assertEquals(2, root.getChildren().size());
        assertTrue(root instanceof BinaryClusterHierarchyNode);

        ArrayList<CentroidCluster<Vector>> clustersList = 
            new ArrayList<CentroidCluster<Vector>>(clusters);

        assertEquals(data1.length, clustersList.get(0).getMembers().size());
        assertEquals(data2.length, clustersList.get(1).getMembers().size());
        assertEquals(data3.length, clustersList.get(2).getMembers().size());
        
        for ( Vector example : data1 )
        {
            assertTrue(clustersList.get(0).getMembers().contains(example));
        }
        
        for ( Vector example : data2 )
        {
            assertTrue(clustersList.get(1).getMembers().contains(example));
        }
        
        for ( Vector example : data3 )
        {
            assertTrue(clustersList.get(2).getMembers().contains(example));
        }
        
    }

    /**
     * Test of getNumClusters method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testGetNumClusters()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance
            = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals(0, instance.getNumClusters());
    }

    /**
     * Test of getDivergenceFunction method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testGetDivergenceFunction()
    {
        this.testSetDivergenceFunction();
    }

    /**
     * Test of setDivergenceFunction method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testSetDivergenceFunction()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance
            = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>();
        assertNull(instance.getDivergenceFunction());
        
        instance.setDivergenceFunction(this.clusterMetric);
        assertSame(this.clusterMetric, instance.getDivergenceFunction());
        
        instance.setDivergenceFunction(null);
        assertNull(instance.getDivergenceFunction());
    }

    /**
     * Test of getCreator method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testGetCreator()
    {
        this.testSetCreator();
    }

    /**
     * Test of setCreator method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testSetCreator()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance
            = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>();
        assertNull(instance.getCreator());
        
        instance.setCreator(this.creator);
        assertSame(this.creator, instance.getCreator());
        
        instance.setCreator(null);
        assertNull(instance.getCreator());
    }

    /**
     * Test of getMinNumClusters method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testGetMinNumClusters()
    {
        this.testSetMinNumClusters();
    }

    /**
     * Test of setMinNumClusters method, of class gov.sandia.cognition.learning.clustering.AgglomerativeClusterer.
     */
    public void testSetMinNumClusters()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance
            = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals(AgglomerativeClusterer.DEFAULT_MIN_NUM_CLUSTERS, instance.getMinNumClusters());
        
        int minNumClusters = 4;
        instance.setMinNumClusters(minNumClusters);
        assertEquals(minNumClusters, instance.getMinNumClusters());
        
        minNumClusters = 7;
        instance.setMinNumClusters(minNumClusters);
        assertEquals(minNumClusters, instance.getMinNumClusters());
        
        minNumClusters = -3;
        instance.setMinNumClusters(minNumClusters);
        assertEquals(1, instance.getMinNumClusters());
    }

    /**
     * Test of getMaxDistance method, of class AgglomerativeClusterer.
     */
    public void testGetMaxDistance()
    {
        this.testSetMaxDistance();
    }

    /**
     * Test of setMaxDistance method, of class AgglomerativeClusterer.
     */
    public void testSetMaxDistance()
    {
        AgglomerativeClusterer<Vector, CentroidCluster<Vector>> instance
            = new AgglomerativeClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals(AgglomerativeClusterer.DEFAULT_MAX_DISTANCE, instance.getMaxDistance());
        
        double maxDistance = Math.random();
        instance.setMaxDistance(maxDistance);
        assertEquals(maxDistance, instance.getMaxDistance());
        
        maxDistance = -Math.random();
        instance.setMaxDistance(maxDistance);
        assertEquals(maxDistance, instance.getMaxDistance());
    }

}
