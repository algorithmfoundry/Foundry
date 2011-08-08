/*
 * File:                AbstractClusterHierarchyNode.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 02, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering.hierarchy;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * An abstract implementation of the {@code ClusterHierarchyNode} class. It
 * keeps track of the cluster.
 *
 * @param   <DataType>
 *      The type of the data in the cluster.
 * @param   <ClusterType>
 *      The type of cluster contained in the hierarchy node.
 * @author  Justin Basilico
 * @since   2.1
 */
public abstract class AbstractClusterHierarchyNode<DataType, ClusterType extends Cluster<DataType>>
    extends AbstractCloneableSerializable
    implements ClusterHierarchyNode<DataType, ClusterType>
{
    /** The cluster associated with the node. */
    protected ClusterType cluster;
    
    /**
     * Creates a new {@code AbstractClusterHierarchyNode}.
     */
    public AbstractClusterHierarchyNode()
    {
        this(null);
    }
    
    /**
     * Creates a new {@code AbstractClusterHierarchyNode}.
     * 
     * @param   cluster
     *      The cluster associated with the node.
     */
    public AbstractClusterHierarchyNode(
        final ClusterType cluster)
    {
        super();
        
        this.setCluster(cluster);
    }
    
    public boolean hasChildren()
    {
        return !CollectionUtil.isEmpty(this.getChildren());
    }
    
    public Collection<DataType> getMembers()
    {
        return this.cluster.getMembers();
    }

    public ClusterType getCluster()
    {
        return cluster;
    }

    /**
     * Sets the cluster associated with the node.
     * 
     * @param   cluster
     *      The cluster associated with the node.
     */
    public void setCluster(
        final ClusterType cluster)
    {
        this.cluster = cluster;
    }
    
}
