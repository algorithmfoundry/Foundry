/*
 * File:                IncrementalClusterCreator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 09, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

/**
 * An interface for a {@code ClusterCreator} that can incrementally add and
 * remove members from a cluster.
 *
 * @param   <ClusterType>
 *      The type of Cluster<DataType> created by this object.
 * @param   <DataType>
 *      The data type from which to create a new cluster.
 * @author  Justin Basilico
 * @since   3.1.1
 */
public interface IncrementalClusterCreator<ClusterType extends Cluster<DataType>, DataType>
    extends ClusterCreator<ClusterType, DataType>
{
    
    /**
     * Creates a new, empty cluster.
     *
     * @return
     *      A new, empty cluster.
     */
    public ClusterType createCluster();

    /**
     * Adds a member to the given cluster.
     *
     * @param   cluster
     *      The cluster to add a member to.
     * @param   member
     *      The member to add to the cluster.
     */
    public void addClusterMember(
        final ClusterType cluster,
        final DataType member);

    /**
     * Removes a member from the given cluster.
     *
     * @param   cluster
     *      The cluster to remove the member from.
     * @param   member
     *      The member to remove.
     * @return
     *      True if the member was successfully removed. False if there was
     *      an error, such as the member not actually being in the cluster to
     *      start with.
     */
    public boolean removeClusterMember(
        final ClusterType cluster,
        final DataType member);
    
}
