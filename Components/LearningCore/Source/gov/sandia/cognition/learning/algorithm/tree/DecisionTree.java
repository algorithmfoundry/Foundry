/*
 * File:                DecisionTree.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The {@code DecisionTree} class implements a standard decision tree that is
 * made up of {@code DecisionTreeNode} objects. It contains the root node of
 * the tree plus the code that descends the decision tree to come up with a
 * result value.
 *
 * @param  <InputType> The input type of the decision tree to evaluate
 * @param  <OutputType> The output type that the decision tree returns.
 * @author Justin Basilico
 * @since  2.0
 */
public class DecisionTree<InputType, OutputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType, OutputType>
{
    /** The root node of the decision tree. */
    protected DecisionTreeNode<InputType, OutputType> rootNode;
    
    /**
     * Creates a new instance of DecisionTree.
     */
    public DecisionTree()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of DecisionTree.
     *
     * @param   rootNode The root node of the tree.
     */
    public DecisionTree(
        final DecisionTreeNode<InputType, OutputType> rootNode)
    {
        super();
        
        this.setRootNode(rootNode);
    }

    /**
     * Evaluates the decision tree against the given input.
     *
     * @param  input The input to evaluate.
     * @return The output of the decision tree for the given input.
     */
    public OutputType evaluate(
        final InputType input)
    {
        return this.evaluateNode(input, this.getRootNode());
    }
    
    /**
     * Evaluates an input against the given node of a decision tree, using
     * recursion to come up with the answer.
     *
     * @param  input The input to evaluate.
     * @param  node The node at which to evaluate.
     * @return The output of the decision tree for the given input.
     */
    public OutputType evaluateNode(
        final InputType input,
        final DecisionTreeNode<InputType, OutputType> node)
    {
        if ( node == null )
        {
            // The node is null so there is no output.
            return null;
        }
        else if ( node.isLeaf() )
        {
            // This is a leaf node so get the output of the node.
            return node.getOutput(input);
        }
        
        // Get the proper child node for the input.
        final DecisionTreeNode<InputType, OutputType> child = 
            node.chooseChild(input);
        
        if ( child == null )
        {
            // There was no child node so use this node as a leaf.
            return node.getOutput(input);
        }
        else
        {
            // Evaluate the child node to get the result.
            return this.evaluateNode(input, child);
        }
    }

    /**
     * Finds the terminal node for the given input. The terminal node is the
     * node in the tree that the input will use to produce an output. It is
     * usually the case that it is a leaf node, but it is not required, since
     * it may happen that the decision function finds a node that has no
     * child for that input. In this case, this is the node returned.
     *
     * @param   input
     *      The input to find the terminal node for.
     * @return
     *      The terminal node for the input, starting from the root node.
     */
    public DecisionTreeNode<InputType, OutputType> findTerminalNode(
        final InputType input)
    {
        return this.findTerminalNode(input, this.getRootNode());
    }

    /**
     * Finds the terminal node for the given input, starting from the given
     * node. The terminal node is the node in the tree that the input will use
     * to produce an output. It is usually the case that it is a leaf node, but
     * it is not required, since it may happen that the decision function finds
     * a node that has no child for that input. In this case, this is the node
     * returned.
     *
     * @param   input
     *      The input to find the terminal node for.
     * @param   node
     *      The node to search from.
     * @return
     *      The terminal node for the input, starting from the root node.
     */
    public DecisionTreeNode<InputType, OutputType> findTerminalNode(
        final InputType input,
        final DecisionTreeNode<InputType, OutputType> node)
    {
        if (node == null)
        {
            // Can't traverse a null node.
            return null;
        }
        else if (node.isLeaf())
        {
            // This is a leaf node so get the output of the node.
            return node;
        }

        // Get the proper child node for the input.
        final DecisionTreeNode<InputType, OutputType> child =
            node.chooseChild(input);

        if (child == null)
        {
            // There was no child node so use this node as a leaf.
            return node;
        }
        else
        {
            // Search from the child node to find the result.
            return this.findTerminalNode(input, child);
        }
    }

    /**
     * Gets the root node of the decision tree.
     * 
     * @return The root node of the decision tree.
     */
    public DecisionTreeNode<InputType, OutputType> getRootNode()
    {
        return this.rootNode;
    }

    /**
     * Sets the root node of the decision tree.
     * 
     * @param  rootNode The root node of the decision tree.
     */
    public void setRootNode(
        final DecisionTreeNode<InputType, OutputType> rootNode)
    {
        this.rootNode = rootNode;
    }
}
