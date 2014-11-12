/*
 * File:                ClusterCreator.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Collection;

/**
 * The ClusterCreator defines the functionality of a class that can create a
 * new cluster from a given collection of members of that cluster.
 *
 * @param   <ClusterType>
 *      The type of {@code Cluster<DataType>} created by this object.
 * @param   <DataType>
 *      The data type from which to create a new cluster.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Interface looks fine."
)
public interface ClusterCreator<ClusterType extends Cluster<DataType>, DataType>
{
    /**
     * Create a new cluster from the given members of that cluster.
     *
     * @param members The members of the cluster.
     * @return A new cluster created from the given members.
     * @throws NullPointerException If members is null.
     */
    public ClusterType createCluster(
        Collection<DataType> members);
}
