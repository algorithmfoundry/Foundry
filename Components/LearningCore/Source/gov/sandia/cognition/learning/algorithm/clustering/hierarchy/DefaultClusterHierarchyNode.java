/*
 * File:                DefaultClusterHierarchyNode.java
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
import java.util.Arrays;
import java.util.List;

/**
 * A default implementation of the cluster hierarchy node. It keeps a list of
 * its children.
 * 
 * @param   <DataType>
 *      The type of the data in the cluster.
 * @param   <ClusterType>
 *      The type of cluster contained in the hierarchy node.
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultClusterHierarchyNode
        <DataType, ClusterType extends Cluster<DataType>>
    extends AbstractClusterHierarchyNode<DataType, ClusterType>
{
    /** The list of children. */
    protected List<ClusterHierarchyNode<DataType, ClusterType>> children;

    /**
     * Creates a new {@code DefaultClusterHierarchyNode}.
     */
    public DefaultClusterHierarchyNode()
    {
        this(null);
    }

    /**
     * Creates a new {@code DefaultClusterHierarchyNode}.
     * 
     * @param   cluster
     *      The cluster associated with this node.
     */
    public DefaultClusterHierarchyNode(
        final ClusterType cluster)
    {
        this(cluster, (List<ClusterHierarchyNode<DataType, ClusterType>>) null);
    }
    
    /**
     * Creates a new {@code DefaultClusterHierarchyNode}.
     * 
     * @param   cluster
     *      The cluster associated with this node.
     * @param   children
     *      The children of this node.
     */
    public DefaultClusterHierarchyNode(
        final ClusterType cluster,
        final List<ClusterHierarchyNode<DataType, ClusterType>> children)
    {
        super(cluster);

        this.setChildren(children);
    }
    
    /**
     * Creates a new {@code DefaultClusterHierarchyNode}.
     * 
     * @param   cluster
     *      The cluster associated with this node.
     * @param   firstChild
     *      The first child.
     * @param   secondChild
     *      The second child.
     */
    public DefaultClusterHierarchyNode(
        final ClusterType cluster,
        final ClusterHierarchyNode<DataType, ClusterType> firstChild,
        final ClusterHierarchyNode<DataType, ClusterType> secondChild)
    {
        this(cluster, CollectionUtil.createArrayList(firstChild, secondChild));
    }

    public List<ClusterHierarchyNode<DataType, ClusterType>> getChildren()
    {
        return this.children;
    }

    /**
     * Sets the children of this node.
     * 
     * @param   children
     *      The children of this node.
     */
    public void setChildren(
        final List<ClusterHierarchyNode<DataType, ClusterType>> children)
    {
        this.children = children;
    }

    /**
     * Sets the children of this node.
     * 
     * @param   firstChild
     *      The first child.
     * @param   secondChild
     *      The second child.
     */
    public void setChildren(
        final ClusterHierarchyNode<DataType, ClusterType> firstChild,
        final ClusterHierarchyNode<DataType, ClusterType> secondChild)
    {
        this.setChildren(CollectionUtil.createArrayList(
            firstChild, secondChild));
    }

}
