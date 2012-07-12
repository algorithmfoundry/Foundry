/*
 * File:                ParallelizedKMeansClustererTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 7, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * JUnit tests for class ParallelizedKMeansClustererTest
 * @author Kevin R. Dixon
 */
public class ParallelizedKMeansClustererTest
    extends KMeansClustererTest
{

    /**
     * Entry point for JUnit tests for class ParallelizedKMeansClustererTest
     * @param testName name of this test
     */
    public ParallelizedKMeansClustererTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> createClusterer()
    {
        return new ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>>(
            0, 100, null, this.initializer, this.clusterMetric, this.creator);        
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ParallelizedKMeansClusterer<?,?> instance = new ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>>();
        assertEquals( ParallelizedKMeansClusterer.DEFAULT_NUM_REQUESTED_CLUSTERS, instance.getNumRequestedClusters() );
        assertEquals( ParallelizedKMeansClusterer.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
    }

    /**
     * Test of clone method, of class ParallelizedKMeansClusterer.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        ParallelizedKMeansClusterer<?,?> instance = this.createClusterer();
        ParallelizedKMeansClusterer<?,?> clone = instance.clone();
        
        assertNotNull( clone );
        assertNotSame( instance, clone );
        
        assertSame( instance.getClusters(), clone.getClusters() );
        assertEquals( instance.getNumClusters(), clone.getNumClusters() );
        for( int i = 0; i < clone.getNumClusters(); i++ )
        {
            assertSame( instance.getCluster( i ), clone.getCluster( i ) );
        }
        
    }

    /**
     * Test of getThreadPool method, of class ParallelizedKMeansClusterer.
     */
    @SuppressWarnings("unchecked")
    public void testGetThreadPool()
    {
        System.out.println( "getThreadPool" );
        ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> instance = this.createClusterer();
        assertNotNull( instance.getThreadPool() );
        
        instance.setData( new LinkedList<Vector>() );
        instance.initializeAlgorithm();
        
        assertNotNull( instance.getThreadPool() );
    }

    /**
     * Test of setThreadPool method, of class ParallelizedKMeansClusterer.
     */
    @SuppressWarnings("unchecked")
    public void testSetThreadPool()
    {
        System.out.println( "setThreadPool" );
        ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> instance = this.createClusterer();
        assertNotNull( instance.getThreadPool() );
        
        instance.setData( new LinkedList<Vector>() );
        instance.initializeAlgorithm();
        
        ThreadPoolExecutor pool = instance.getThreadPool();
        assertNotNull( pool );
        
        instance.setThreadPool( null );
        assertNotNull( instance.getThreadPool() );
        instance.setThreadPool( pool );
        assertSame( pool, instance.getThreadPool() );
        
    }
    
}
