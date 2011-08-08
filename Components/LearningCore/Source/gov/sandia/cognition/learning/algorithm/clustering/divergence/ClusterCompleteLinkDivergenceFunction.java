/*
 * File:                ClusterCompleteLinkDivergenceFunction.java
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
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.math.DivergenceFunction;
import java.util.Collection;

/**
 * The ClusterCompleteLinkDivergenceFunction class implements the complete 
 * linkage distance metric between two clusters. That is, the value returned 
 * by the metric is the maximum distance between any point in the first 
 * cluster and any point in the second cluster.
 *
 *
 * @param <ClusterType> type of {@code Cluster<DataType>} used in the
 * {@code learn()} method
 * @param <DataType> The algorithm operates on a {@code Collection<DataType>},
 * so {@code DataType} will be something like Vector or String
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
public class ClusterCompleteLinkDivergenceFunction<ClusterType extends Cluster<DataType>, DataType>
    extends AbstractClusterToClusterDivergenceFunction<ClusterType, DataType>
{
    /**
     * Creates a new instance of ClusterCompleteLinkDivergenceFunction.
     */
    public ClusterCompleteLinkDivergenceFunction()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of ClusterCompleteLinkDivergenceFunction using
     * the given divergence function for elements.
     *
     * @param  divergenceFunction The divergence function for elements.
     */
    public ClusterCompleteLinkDivergenceFunction(
        DivergenceFunction<? super DataType, ? super DataType> 
            divergenceFunction)
    {
        super(divergenceFunction);
    }
    
    /**
     * This method computes the complete link distance between the two given 
     * Clusters. The distance returned is the maximum distance between a 
     * member of the first cluster and a member of the second cluster.
     *
     * @param  from The first Cluster.
     * @param  to The second Cluster.
     * @return The complete link distance between the two given Clusters.
     */
    public double evaluate(
        ClusterType from,
        ClusterType to)
    {
        // Get the members of each cluster.
        Collection<DataType> fromMembers = from.getMembers();
        Collection<DataType> toMembers   = to.getMembers();
        
        // We are going to compute the maximum distance so start with the
        // minimum distance value.
        double maxDistance = Double.MIN_VALUE;
        
        // Do a double loop through the members of the two clusters.
        for ( DataType first : fromMembers )
        {
            for ( DataType second : toMembers )
            {
                // Compute the distance between the two memebers.
                double distance = this.divergenceFunction.evaluate(first, second);

                if ( distance > maxDistance )
                {
                    // This is the maximum distance seen so far so
                    // store it.
                    maxDistance = distance;
                }
            }
        }

        // Return the maximum distance that was found.
        return maxDistance;
    }
}

