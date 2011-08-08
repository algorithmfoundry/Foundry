/*
 * File:                OptimizedKMeansClustererTest.java
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
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.math.matrix.Vector;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     OptimizedKMeansClusterer
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class OptimizedKMeansClustererTest
    extends KMeansClustererTest
{

    /**
     * Creates a new instance of OptimizedKMeansClustererTest.
     *
     * @param  testName The test name.
     */
    public OptimizedKMeansClustererTest(
        String testName)
    {
        super(testName);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public 
    @Override
    OptimizedKMeansClusterer<Vector> createClusterer()
    {
        return new OptimizedKMeansClusterer<Vector>(
            0, 1000, this.initializer, this.metric, this.creator);
    }

    /**
     * Tests the creation of an OptimizedKMeansClusterer.
     */
    public 
    @Override
    void testCreation()
    {
        OptimizedKMeansClusterer<Vector> kmeans = this.createClusterer();

        assertEquals(0, kmeans.getNumClusters());
        assertSame(this.initializer, kmeans.getInitializer());
        assertSame(this.metric, kmeans.getMetric());
        assertSame(this.creator, kmeans.getCreator());

        kmeans.setNumRequestedClusters(1);
        assertEquals(1, kmeans.getNumRequestedClusters());
    }

}
