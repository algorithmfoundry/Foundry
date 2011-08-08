/*
 * File:                DefaultClusterCreator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * The {@code DefaultClusterCreator} class implements a default 
 * {@code ClusterCreator} that just creates a {@code DefaultCluster} from the
 * given list of members.
 * 
 * @param   <DataType> The type of the data to put in the cluster.
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultClusterCreator<DataType>
    extends AbstractCloneableSerializable
    implements ClusterCreator<DefaultCluster<DataType>, DataType>
{
    /**
     * Creates a new {@code DefaultClusterCreator}.
     */
    public DefaultClusterCreator()
    {
        super();
    }
    
    /**
     * Creates a new {@code DefaultCluster} from the given list of members.
     * 
     * @param   members The cluster members.
     * @return  A new cluster from the given members.
     */
    public DefaultCluster<DataType> createCluster(
        final Collection<DataType> members)
    {
        if (members == null)
        {
            // Error: Members is null.
            throw new NullPointerException("The members cannot be null.");
        }
        else if (members.size() <= 0)
        {
            // No members to create the cluster from.
            return new DefaultCluster<DataType>();
        }
        else
        {
            // Create a cluster from the given members.
            return new DefaultCluster<DataType>(members);
        }
    }
    
}
