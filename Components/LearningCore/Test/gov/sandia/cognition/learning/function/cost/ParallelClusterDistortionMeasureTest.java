/*
 * File:                ParallelClusterDistortionMeasureTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 23, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class ParallelClusterDistortionMeasureTest
 * @author Kevin R. Dixon
 */
public class ParallelClusterDistortionMeasureTest
    extends ClusterDistortionMeasureTest
{

    /**
     * Entry point for JUnit tests for class ParallelClusterDistortionMeasureTest
     * @param testName name of this test
     */
    public ParallelClusterDistortionMeasureTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public ParallelClusterDistortionMeasure<Vector, CentroidCluster<Vector>> createInstance()
    {
        ParallelClusterDistortionMeasure<Vector,CentroidCluster<Vector>> instance =
            new ParallelClusterDistortionMeasure<Vector,CentroidCluster<Vector>>(
                new CentroidClusterDivergenceFunction<Vector>( new EuclideanDistanceMetric() ) );
        instance.setThreadPool( ParallelUtil.createThreadPool( 1 ));
        assertEquals( 1, instance.getNumThreads() );
        return instance;
    }

    
    
}
