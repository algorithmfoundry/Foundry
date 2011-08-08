/*
 * File:                ClusterDistortionMeasureTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 20, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class ClusterDistortionMeasureTest
 * @author Kevin R. Dixon
 */
public class ClusterDistortionMeasureTest
    extends TestCase
{
    
    private static Random random = new Random( 1 );

    /**
     * Entry point for JUnit tests for class ClusterDistortionMeasureTest
     * @param testName name of this test
     */
    public ClusterDistortionMeasureTest(
        String testName)
    {
        super(testName);
    }

    public ClusterDistortionMeasure<Vector,CentroidCluster<Vector>> createInstance()
    {
        return new ClusterDistortionMeasure<Vector,CentroidCluster<Vector>>(
            new CentroidClusterDivergenceFunction<Vector>( new EuclideanDistanceMetric() ) );
    }
    
    public CentroidCluster<Vector> createRandomCluster()
    {
        
        int N = 3;
        double r = 2.0;
        Vector centroid = VectorFactory.getDefault().createUniformRandom( N, -r, r, random);
        
        int num = random.nextInt( 100 );
        LinkedList<Vector> members = new LinkedList<Vector>();
        for( int i = 0; i < num; i++ )
        {
            members.add( VectorFactory.getDefault().createUniformRandom( N, -r, r, random) );
        }
        
        return new CentroidCluster<Vector>( centroid, members );
        
        
    }
    
    
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ClusterDistortionMeasure instance = this.createInstance();
        
        assertNotNull( instance );
        assertNotNull( instance.getCostParameters() );
       
        instance = new ClusterDistortionMeasure();
        assertNull( instance.getCostParameters() );
        
    }
    
    /**
     * Test of clone method, of class ClusterDistortionMeasure.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        ClusterDistortionMeasure instance = this.createInstance();
        ClusterDistortionMeasure clone = instance.clone();
        assertNotSame( instance, clone );
        
        assertSame( instance.getCostParameters(), clone.getCostParameters() );

    }

    /**
     * Test of evaluate method, of class ClusterDistortionMeasure.
     */
    @SuppressWarnings("unchecked")
    public void testEvaluate_Collection()
    {
        System.out.println( "evaluate" );
        ClusterDistortionMeasure instance = this.createInstance();
        
        LinkedList<CentroidCluster<Vector>> clusters = new LinkedList<CentroidCluster<Vector>>();
        int num = random.nextInt( 10 );
        double expected = 0.0;
        for( int i = 0; i < num; i++ )
        {
            CentroidCluster<Vector> cluster = this.createRandomCluster();
            expected += instance.evaluate( cluster );
            clusters.add( cluster );
        }
        
        assertEquals( expected, instance.evaluate( clusters ) );
        

    }

    /**
     * Test of evaluate method, of class ClusterDistortionMeasure.
     */
    public void testEvaluate_Cluster()
    {
        System.out.println( "evaluate" );
        
        Vector centroid = VectorFactory.getDefault().copyValues( 1.0 );
        
        LinkedList<Vector> members = new LinkedList<Vector>();
        members.add( VectorFactory.getDefault().copyValues( 0.0 ) );
        members.add( VectorFactory.getDefault().copyValues( 2.0 ) );
        members.add( VectorFactory.getDefault().copyValues( -1.0 ) );
        
        
        CentroidCluster<Vector> cluster = new CentroidCluster<Vector>( centroid, members );
        ClusterDistortionMeasure instance = this.createInstance();
        @SuppressWarnings("unchecked")
        Double y = instance.evaluate( cluster );
        
        double expected = 1.0 + 1.0 + 2.0;
        assertEquals( expected, y );
        
    }

    /**
     * Test of getCostParameters method, of class ClusterDistortionMeasure.
     */
    public void testGetCostParameters()
    {
        System.out.println( "getCostParameters" );
        ClusterDistortionMeasure instance = this.createInstance();
        assertNotNull( instance.getCostParameters() );

    }

    /**
     * Test of setCostParameters method, of class ClusterDistortionMeasure.
     */
    @SuppressWarnings("unchecked")
    public void testSetCostParameters()
    {
        System.out.println( "setCostParameters" );
        ClusterDistortionMeasure instance = this.createInstance();

        ClusterDivergenceFunction div = instance.getCostParameters();
        assertNotNull( div );
        instance.setCostParameters( null );
        assertNull( instance.getCostParameters() );
        instance.setCostParameters( div );
        assertSame( div, instance.getCostParameters() );
        
    }

}
