/*
 * File:                KMeansFactoryTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for KMeansFactoryTest.
 *
 * @author krdixon
 */
public class KMeansFactoryTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Tests for class KMeansFactoryTest.
     * @param testName Name of the test.
     */
    public KMeansFactoryTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class KMeansFactoryTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        KMeansFactory kmf = new KMeansFactory();
        assertEquals( KMeansFactory.DEFAULT_NUM_CLUSTERS, kmf.getNumClusters() );
        assertNotNull( kmf.getRandom() );

        int k = random.nextInt( 100 ) + 1;
        kmf = new KMeansFactory( k, random );
        assertEquals( k, kmf.getNumClusters() );
        assertSame( random, kmf.getRandom() );

    }

    /**
     * Test of create method, of class KMeansFactory.
     */
    public void testCreate_int_Random()
    {
        System.out.println("create");
        int numClusters = random.nextInt( 10 ) + 1;
        ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> result =
            KMeansFactory.create(numClusters, random);
        assertEquals( numClusters, result.getNumRequestedClusters() );
        assertNotNull( result.getInitializer() );
        assertNotNull( result.getDivergenceFunction() );
        assertNotNull( result.getThreadPool() );

    }

    /**
     * Test of create method, of class KMeansFactory.
     */
    public void testCreate_int_Semimetric_Random()
    {
        System.out.println("create");
        int numClusters = random.nextInt(10) + 1;
        CosineDistanceMetric metric = CosineDistanceMetric.INSTANCE;
        ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> result =
            KMeansFactory.create(numClusters, metric, random);
        assertEquals(numClusters, result.getNumRequestedClusters());
        assertNotNull(result.getInitializer());
        assertNotNull( result.getDivergenceFunction() );
        assertNotNull(result.getThreadPool());
    }

    /**
     * Test of create method, of class KMeansFactory.
     */
    public void testCreate_0args()
    {
        System.out.println("create");
        KMeansFactory instance = new KMeansFactory();
        ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> result =
            instance.create();
        assertEquals( KMeansFactory.DEFAULT_NUM_CLUSTERS, result.getNumRequestedClusters() );
        assertNotNull( result.getInitializer() );
        assertNotNull( result.getDivergenceFunction() );
        assertNotNull( result.getThreadPool() );

    }

    /**
     * Test of getNumClusters method, of class KMeansFactory.
     */
    public void testGetNumClusters()
    {
        System.out.println("getNumClusters");
        KMeansFactory instance = new KMeansFactory();
        assertEquals( KMeansFactory.DEFAULT_NUM_CLUSTERS, instance.getNumClusters() );
    }

    /**
     * Test of setNumClusters method, of class KMeansFactory.
     */
    public void testSetNumClusters()
    {
        System.out.println("setNumClusters");
        KMeansFactory instance = new KMeansFactory();
        int k2 = instance.getNumClusters() + 1;
        instance.setNumClusters(k2);
        assertEquals( k2, instance.getNumClusters() );
    }

}
