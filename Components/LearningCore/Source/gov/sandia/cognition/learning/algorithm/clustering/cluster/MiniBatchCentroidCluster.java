/*
 * File:                MiniBatchCentroidCluster.java
 * Authors:             Jeff Piersol
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright October 20, 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */
package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Jeff Piersol
 */
public class MiniBatchCentroidCluster
    extends CentroidCluster<Vector>
{

    /**
     * The number of data points that have been used to calculate this centroid.
     */
    private int numUpdates;

    /**
     *
     * @param initialPoints
     */
    public MiniBatchCentroidCluster(
        final Collection<? extends Vector> initialPoints)
    {
        if (initialPoints.size() <= 0)
        {
            throw new IllegalArgumentException(
                "You must provide at least one data point in order to create a cluster.");
        }

        // Create a centroid of the same type as input vectors
        this.centroid = initialPoints.stream().findAny().get().clone();
        this.centroid.zero();

        this.updateCluster(initialPoints);
    }

    public void updateCluster(Vector dataPoint)
    {
        updateCluster(Collections.singletonList(dataPoint));
    }

    public void updateCluster(Collection<? extends Vector> dataPoints)
    {
        int initNumUpdates = numUpdates;
        this.numUpdates += dataPoints.size();
        double finalEta = 1 / (double) numUpdates;

        Vector shiftVector = DenseVectorFactoryMTJ.INSTANCE.createVector(
            centroid.getDimensionality());

        for (Vector sample : dataPoints)
        {
            shiftVector.plusEquals(sample);
        }

        // Move centroid towards data point
        centroid.scaleEquals(initNumUpdates * finalEta);
        centroid.scaledPlusEquals(finalEta, shiftVector);
    }

}
