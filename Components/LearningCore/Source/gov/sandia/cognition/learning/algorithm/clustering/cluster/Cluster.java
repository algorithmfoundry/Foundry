/*
 * File:                Cluster.java
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
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Collection;

/**
 * The Cluster interface defines the general functionality of a cluster, which 
 * is just the ability to get the members of the cluster.
 *
 * @param  <ClusterType> The type of data stored in the cluster.
 *
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
public interface Cluster<ClusterType>
    extends CloneableSerializable
{
    /**
     * Gets the member objects of the cluster.
     *
     * @return The members of the cluster.
     */
    Collection<ClusterType> getMembers();
}
