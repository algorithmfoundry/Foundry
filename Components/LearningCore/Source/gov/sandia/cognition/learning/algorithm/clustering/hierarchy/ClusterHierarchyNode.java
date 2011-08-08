/*
 * File:                ClusterHierarchyNode.java
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

import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import java.io.Serializable;
import java.util.List;

/**
 * Defines a node in a hierarchy of clusters.
 * 
 * @param   <DataType>
 *      The type of the data in the cluster.
 * @param   <ClusterType>
 *      The type of cluster contained in the hierarchy node.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface ClusterHierarchyNode<DataType, ClusterType extends Cluster<DataType>>
    extends Cluster<DataType>, Serializable
{
    /**
     * Gets the cluster associated with the node.
     * 
     * @return  The cluster associated with the node.
     */
    public ClusterType getCluster();

    /**
     * True if this node has any children.
     * 
     * @return  True if this node has any children.
     */
    public boolean hasChildren();
    
    /**
     * Gets the children of this node. May be null if there are no children.
     * 
     * @return  The children of this node.
     */
    public List<ClusterHierarchyNode<DataType, ClusterType>> getChildren();
    
}
