/*
 * File:                DefaultIncrementalClusterCreator.java
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
 * A default implementation of the {@code IncrementalClusterCreator} interface
 * that just creates a cluster as having a collection of members.
 *
 * @param   <DataType>
 *      The data type to create the clusters over.
 * @author  Justin Basilico
 * @since   3.1.1
 */
public class DefaultIncrementalClusterCreator<DataType>
    extends DefaultClusterCreator<DataType>
    implements IncrementalClusterCreator<DefaultCluster<DataType>, DataType>
{

    /**
     * Creates a new {@code DefaultIncrementalClusterCreator}.
     */
    public DefaultIncrementalClusterCreator()
    {
        super();
    }

    @Override
    public DefaultCluster<DataType> createCluster()
    {
        // An empty cluster.
        return new DefaultCluster<DataType>();
    }

    @Override
    public void addClusterMember(
        final DefaultCluster<DataType> cluster,
        final DataType member)
    {
        // Add the member to the cluster.
        cluster.getMembers().add(member);
    }

    @Override
    public boolean removeClusterMember(
        final DefaultCluster<DataType> cluster,
        final DataType member)
    {
        // Remove the member from the cluter.
        return cluster.getMembers().remove(member);
    }
    
}
