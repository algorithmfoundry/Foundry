/*
 * File:                BinaryClusterHierarchyNode.java
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
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a binary cluster hierarchy node. It can support two children.
 *
 * @param   <DataType>
 *      The type of the data in the cluster.
 * @param   <ClusterType>
 *      The type of cluster contained in the hierarchy node.
 * @author  Justin Basilico
 * @since   2.1
 */
public class BinaryClusterHierarchyNode<DataType, ClusterType extends Cluster<DataType>>
    extends AbstractClusterHierarchyNode<DataType, ClusterType>
{
    /** The first child node. */
    protected ClusterHierarchyNode<DataType, ClusterType> firstChild;
    
    /** The second child node. */
    protected ClusterHierarchyNode<DataType, ClusterType> secondChild;
    
    /**
     * Creates a new {@code BinaryClusterHierarchyNode}.
     */
    public BinaryClusterHierarchyNode()
    {
        this(null);
    }

    /**
     * Creates a new {@code BinaryClusterHierarchyNode}.
     * 
     * @param   cluster
     *      The cluster associated with this node.
     */
    public BinaryClusterHierarchyNode(
        final ClusterType cluster)
    {
        this(cluster, null, null);
    }
    
    /**
     * Creates a new {@code BinaryClusterHierarchyNode}.
     * 
     * @param   cluster
     *      The cluster associated with this node.
     * @param   firstChild
     *      The first child node.
     * @param   secondChild
     *      The second child node.
     */
    public BinaryClusterHierarchyNode(
        final ClusterType cluster,
        final ClusterHierarchyNode<DataType, ClusterType> firstChild,
        final ClusterHierarchyNode<DataType, ClusterType> secondChild)
    {
        super(cluster);
        
        this.setFirstChild(firstChild);
        this.setSecondChild(secondChild);
    }
    
    @Override
    public boolean hasChildren()
    {
        return this.firstChild != null || this.secondChild != null;
    }
    
    public List<ClusterHierarchyNode<DataType, ClusterType>> getChildren()
    {
        if (!this.hasChildren())
        {
            // No children.
            return null;
        }
        
        // Create the result.
        final ArrayList<ClusterHierarchyNode<DataType, ClusterType>> result =
            new ArrayList<ClusterHierarchyNode<DataType, ClusterType>>();
        
        if (this.firstChild != null)
        {
            result.add(this.firstChild);
        }
        
        if (this.secondChild != null)
        {
            result.add(this.secondChild);
        }
        
        return result;
    }

    /**
     * Gets the first child node, if there is one. Otherwise, null is returned.
     * 
     * @return  The first child node.
     */
    public ClusterHierarchyNode<DataType, ClusterType> getFirstChild()
    {
        return this.firstChild;
    }

    /**
     * Sets the first child node.
     * 
     * @param   firstChild
     *      The first child node.
     */
    public void setFirstChild(
        final ClusterHierarchyNode<DataType, ClusterType> firstChild)
    {
        this.firstChild = firstChild;
    }

    /**
     * Gets the second child node, if there is one. Otherwise, null is returned.
     * 
     * @return  The second child node.
     */
    public ClusterHierarchyNode<DataType, ClusterType> getSecondChild()
    {
        return this.secondChild;
    }

    /**
     * Sets the second child node.
     * 
     * @param   secondChild
     *      The second child node.
     */
    public void setSecondChild(
        final ClusterHierarchyNode<DataType, ClusterType> secondChild)
    {
        this.secondChild = secondChild;
    }
}
