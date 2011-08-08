/*
 * File:                NeighborhoodGaussianClusterInitializer.java
 * Authors:             Kevin R. Dixon
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
package gov.sandia.cognition.learning.algorithm.clustering.initializer;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
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
public class NeighborhoodGaussianClusterInitializerTest
    extends TestCase
{
    
    public NeighborhoodGaussianClusterInitializerTest(
        String testName)
    {
        super(testName);
    }

    Random random = new Random( 1 );

    /**
     * Test of getRandomRange method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testGetRandomRange()
    {
        System.out.println("getRandomRange");

        double defaultCovariance = random.nextDouble();
        double randomRange = random.nextDouble();
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer(
                defaultCovariance, randomRange, random );
        
        assertEquals( randomRange, instance.getRandomRange() );
        
    }

    /**
     * Test of setRandomRange method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testSetRandomRange()
    {
        System.out.println("setRandomRange");
        
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer( random );
        
        double change = random.nextDouble();
        instance.setRandomRange( change );
        assertEquals( change, instance.getRandomRange() );
    }

    /**
     * Test of getDefaultCovariance method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testGetDefaultCovariance()
    {
        System.out.println("getDefaultCovariance");
        
        double defaultCovariance = random.nextDouble();
        double randomRange = random.nextDouble();
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer(
                defaultCovariance, randomRange, random );
        
        assertEquals( defaultCovariance, instance.getDefaultCovariance() );
    }

    /**
     * Test of setDefaultCovariance method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testSetDefaultCovariance()
    {
        System.out.println("setDefaultCovariance");
        
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer( random );
        double change = random.nextDouble();
        instance.setDefaultCovariance( change );
        assertEquals( change, instance.getDefaultCovariance() );
    }

    /**
     * Test of getRandomGenerator method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testGetRandomGenerator()
    {
        System.out.println("getRandomGenerator");
        
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer( random );
        assertSame( random, instance.getRandom() );
    }

    /**
     * Test of setRandomGenerator method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testSetRandomGenerator()
    {
        System.out.println("setRandomGenerator");
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer( random );
        
        assertSame( random, instance.getRandom() );
        
        Random change = new Random();
        instance.setRandom( change );
        
        assertNotSame( random, instance.getRandom() );
        assertEquals( change, instance.getRandom() );
    }

    /**
     * Test of initializeClusters method, of class gov.sandia.isrc.learning.vector.NeighborhoodGaussianClusterInitializer.
     */
    public void testInitializeClusters()
    {
        System.out.println("initializeClusters");
        
        NeighborhoodGaussianClusterInitializer instance = 
            new NeighborhoodGaussianClusterInitializer( random );
        
        int numClusters = 10;
        Collection<Vector> elements = new ArrayList<Vector>();
        int numData = 1000;
        double range = 10;
        for( int i = 0; i < numData; i++ )
        {
            elements.add( VectorFactory.getDefault().createUniformRandom(5, -range, range, random ) );
        }
        
        ArrayList<GaussianCluster> clusters = 
            instance.initializeClusters( numClusters, elements );
        
        assertEquals( numClusters, clusters.size() );
        
    }
    
}
