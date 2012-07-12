/*
 * File:                KMeansClustererRemovalTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.GaussianClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.NeighborhoodGaussianClusterInitializer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class KMeansClustererWithRemovalTest
    extends TestCase
{

    /** The random number generator for the tests. */
    private Random random = new Random(2);
    
    public KMeansClustererWithRemovalTest(
        String testName)
    {
        super(testName);
    }

    public KMeansClustererWithRemoval<Vector, GaussianCluster> createClusterer()
    {
        int numClusters = 10;
        
        double defaultCovariance = random.nextDouble();
        double randomRange = random.nextDouble();

        NeighborhoodGaussianClusterInitializer initializer =
            new NeighborhoodGaussianClusterInitializer(
            defaultCovariance, randomRange, random);

        GaussianClusterDivergenceFunction divergence =
            GaussianClusterDivergenceFunction.INSTANCE;

        GaussianClusterCreator creator =
            new GaussianClusterCreator(defaultCovariance);

        double removalThreshold = 0.8;

        KMeansClustererWithRemoval<Vector, GaussianCluster> instance =
            new KMeansClustererWithRemoval<Vector, GaussianCluster>(
            numClusters, 1000, initializer, divergence, creator, removalThreshold);

        assertEquals(numClusters, instance.getNumRequestedClusters());
        assertEquals(initializer, instance.getInitializer());
        assertEquals(divergence, instance.getDivergenceFunction());
        assertEquals(creator, instance.getCreator());
        assertEquals(removalThreshold, instance.getRemovalThreshold());

        return instance;
    }

    /**
     * Test of getRemovalThreshold method, of class gov.sandia.isrc.learning.unsupervised.KMeansClustererRemoval.
     */
    public void testGetRemovalThreshold()
    {
        System.out.println("getRemovalThreshold");

        KMeansClustererWithRemoval<?,?> instance = this.createClusterer();
        double removalThreshold = random.nextDouble();
        instance.setRemovalThreshold(removalThreshold);

        assertEquals(removalThreshold, instance.getRemovalThreshold());

    }

    /**
     * Test of setRemovalThreshold method, of class gov.sandia.isrc.learning.unsupervised.KMeansClustererRemoval.
     */
    public void testSetRemovalThreshold()
    {
        System.out.println("setRemovalThreshold");

        KMeansClustererWithRemoval<?,?> instance = this.createClusterer();
        double removalThreshold = random.nextDouble();
        instance.setRemovalThreshold(removalThreshold);

        assertEquals(removalThreshold, instance.getRemovalThreshold());

        double change = random.nextDouble();
        instance.setRemovalThreshold(change);
        assertFalse(removalThreshold == instance.getRemovalThreshold());
        assertEquals(change, instance.getRemovalThreshold());
        
        try
        {
            instance.setRemovalThreshold( 1.0 );
            fail( "removalThreshold must be < 1.0" );

        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clusterStep method, of class gov.sandia.isrc.learning.unsupervised.KMeansClustererRemoval.
     */
    public void testClusterStep()
    {
        System.out.println("clusterStep");

        KMeansClustererWithRemoval<Vector, GaussianCluster> instance =
            this.createClusterer();

        Collection<Vector> elements = new ArrayList<Vector>();
        int numData = 1000;
        double range = 0.1;
        for (int i = 0; i < numData; i++)
        {
            elements.add(VectorFactory.getDefault().createUniformRandom(5, -range, range, random));
        }

        Collection<GaussianCluster> clusters = instance.learn(elements);
        assertTrue(clusters.size() > 0);

    }

}
