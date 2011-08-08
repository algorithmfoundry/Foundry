/*
 * File:                CentroidClusterDivergenceFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CentroidClusterDivergenceFunction
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CentroidClusterDivergenceFunctionTest
    extends TestCase
{
    /**
     * Creates a new instance of CentroidClusterDivergenceFunctionTest.
     *
     * @param  testName The test name.
     */
    public CentroidClusterDivergenceFunctionTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        CentroidClusterDivergenceFunction<Vector> f =
            new CentroidClusterDivergenceFunction<Vector>(metric);

        CentroidClusterDivergenceFunction<Vector> clone = f.clone();
        assertNotNull( clone );
        assertNotSame( f, clone );
        assertNotNull( clone.getDivergenceFunction() );
        assertNotSame( f.getDivergenceFunction(), clone.getDivergenceFunction() );

    }

    /**
     * Tests the CentroidClusterDivergenceFunction class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCentroidClusterDivergenceFunction()
    {
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        CentroidClusterDivergenceFunction<Vector> f = 
            new CentroidClusterDivergenceFunction<Vector>(metric);

        // Evaluate the cluster divergence between these two points.
        Vector2 v00 = new Vector2(0.0, 0.0);
        Vector2 v01 = new Vector2(0.0, 1.0);
        
        // Create the clusters.
        CentroidCluster<Vector> c00 = new CentroidCluster<Vector>(v00);
        CentroidCluster<Vector> c01 = new CentroidCluster<Vector>(v01);
        
        // Make sure the distances are valid
        assertEquals(0.0, f.evaluate(c00, v00));
        assertEquals(0.0, f.evaluate(c01, v01));
        assertEquals(1.0, f.evaluate(c00, v01));
        assertEquals(1.0, f.evaluate(c01, v00));

        metric = null;
        f = new CentroidClusterDivergenceFunction<Vector>(metric);
        assertNull( f.getDivergenceFunction() );
        
    }
}
