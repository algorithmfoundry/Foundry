/*
 * File:                DirichletProcessClusteringTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DirichletProcessClusteringTest.
 *
 * @author krdixon
 */
public class DirichletProcessClusteringTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Number of samples, {@value}.
     */
    public static final int NUM_SAMPLES = 5;

    /**
     * Tests for class DirichletProcessClusteringTest.
     * @param testName Name of the test.
     */
    public DirichletProcessClusteringTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class DirichletProcessClusteringTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DirichletProcessClustering instance = new DirichletProcessClustering();
        assertNotNull( instance.getRandom() );
        assertNotNull( instance.getAlgorithm() );
    }

    /**
     * Test of clone method, of class DirichletProcessClustering.
     */
    public void testClone()
    {
        System.out.println("clone");
        DirichletProcessClustering instance = new DirichletProcessClustering( 3 );
        DirichletProcessClustering clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getAlgorithm(), clone.getAlgorithm() );
        assertNotNull( clone.getAlgorithm() );
        assertSame( instance.getRandom(), clone.getRandom() );
    }

    /**
     * Test of getResult method, of class DirichletProcessClustering.
     */
    public void testGetResult()
    {
        System.out.println("getResult");
        DirichletProcessClustering instance = new DirichletProcessClustering();
        assertNull( instance.getResult() );
    }

    /**
     * Test of learn method, of class DirichletProcessClustering.
     */
    public void testLearn()
    {
        System.out.println("learn");

        double m = 1.0;
        double s = 0.1;
        int N = 4;
        int DIM = 2;
        ArrayList<Vector> data = new ArrayList<Vector>( NUM_SAMPLES*N );
        for( int n = 0; n < N; n++ )
        {
            Vector mean = VectorFactory.getDefault().createVector(DIM, m*n);
            Matrix C = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale(s*s);
            MultivariateGaussian g = new MultivariateGaussian(mean, C);
            data.addAll( g.sample(RANDOM, NUM_SAMPLES ) );
        }

        DirichletProcessClustering instance = new DirichletProcessClustering( DIM );
        instance.setRandom(RANDOM);

        ArrayList<GaussianCluster> clusters = instance.learn(data);
        assertSame( clusters, instance.getResult() );

        for( GaussianCluster cluster : clusters )
        {
            System.out.println( "Cluster: " + cluster.getGaussian() );
        }
    }

}
