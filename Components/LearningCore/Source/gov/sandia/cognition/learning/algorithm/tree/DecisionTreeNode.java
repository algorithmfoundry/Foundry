/*
 * File:                DecisionTreeNode.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Collection;

/**
 * The {@code DecisionTreeNode} interface defines the functionality of a node
 * in a decision tree. In particular, it defines how to get the child nodes
 * plus how to get the output for a leaf node.
 *
 * @param  <InputType> The input type of the decision tree to evaluate
 * @param  <OutputType> The output type that the decision tree returns.
 * @author Justin Basilico
 * @since  2.0
 */
public interface DecisionTreeNode<InputType, OutputType>
    extends CloneableSerializable
{
    /**
     * Gets the parent node for this node. Null if it is the root node.
     *
     * @return
     *      The parent node for this node.
     */
    DecisionTreeNode<InputType, OutputType> getParent();

    /**
     * Gets the collection of children of the node.
     *
     * @return The collection of children of the node.
     */
    Collection<? extends DecisionTreeNode<InputType, OutputType>> getChildren();
    
    /**
     * Returns true if this node is a leaf node (has no children) and false
     * otherwise.
     *
     * @return True if this is a leaf node; false otherwise.
     */
    boolean isLeaf();

    /**
     * Chooses the child node corresponding to the given input. If there is no
     * corresponding child node, then null is returned.
     *
     * @param   input The input.
     * @return 
     *      The corresponding child node for the given input, if one exists; 
     *      otherwise, null.
     */
    DecisionTreeNode<InputType, OutputType> chooseChild(
        InputType input);
    
    /**
     * Gets the local output of this node for the given input. This is done to
     * determine the output value for a leaf node or the output value in the
     * case that there is no corresponding child node for an input.
     *
     * @param   input The input.
     * @return 
     *      The local output value for the given input.
     */
    OutputType getOutput(
        InputType input);
    
    /**
     * Gets the label of the incoming value to the node.
     *
     * @return The incoming value to the node, if any.
     */
    Object getIncomingValue();

    /**
     * Gets the depth of the node in the tree.
     *
     * @return
     *      The depth of the node. The root is depth 1.
     */
    int getDepth();

    /**
     * Gets the size of the sub-tree from this node.
     *
     * @return
     *      The size of the sub-tree rooted at this node. Must be positive.
     */
    int getTreeSize();
}
