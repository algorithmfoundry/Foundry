/*
 * File:                ClusterCentroidDivergenceFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.math.DivergenceFunction;

/**
 * The ClusterCentroidDivergenceFunction class implements the distance 
 * between two clusters by computing the distance between the cluster's 
 * centroid. When used in conjunction with a ClusterCreator that computes 
 * the mean of the member elements, this distance metric can compute the 
 * average group distance between the two clusters. It is important to link 
 * up the DivergenceFunction passed to this class with the ClusterCreator used
 * to create the element for the clusters.
 *
 * @param <DataType> The algorithm operates on a {@code Collection<DataType>},
 * so {@code DataType} will be something like Vector or String
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments="Looks fine."
)
public class ClusterCentroidDivergenceFunction<DataType>
    extends AbstractClusterToClusterDivergenceFunction
        <CentroidCluster<DataType>, DataType>
{
    /**
     * Creates a new instance of ClusterCompleteLinkDivergenceFunction.
     */
    public ClusterCentroidDivergenceFunction()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of ClusterCompleteLinkDivergenceFunction using
     * the given divergence function for elements.
     *
     * @param  divergenceFunction The divergence function for elements.
     */
    public ClusterCentroidDivergenceFunction(
        final DivergenceFunction<? super DataType, ? super DataType> 
            divergenceFunction)
    {
        super(divergenceFunction);
    }

    /**
     * This method computes the complete link distance between the two given 
     * Clusters. The distance returned is the distance between the element 
     * representing the center of the two clusters.
     *
     * @param  from The first Cluster.
     * @param  to The second Cluster.
     * @return The distance between the two given Clusters elements.
     */
    public double evaluate(
        final CentroidCluster<DataType> from,
        final CentroidCluster<DataType> to)
    {
        return this.divergenceFunction.evaluate(
            from.getCentroid(), to.getCentroid());
    }
}
