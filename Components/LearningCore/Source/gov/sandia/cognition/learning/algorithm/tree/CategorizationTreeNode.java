/*
 * File:                CategorizationTreeNode.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.function.categorization.Categorizer;

/**
 * The {@code CategorizationTreeNode} implements a {@code DecisionTreeNode} for
 * a tree that does categorization.
 *
 * @param  <InputType> The input type for the tree to make a decision about.
 * @param  <OutputType> The output type of the tree.
 * @param  <InteriorType> The type that is used to make a decision at this node.
 * @author Justin Basilico
 * @since  2.0
 */
public class CategorizationTreeNode<InputType, OutputType, InteriorType>
    extends AbstractDecisionTreeNode<InputType, OutputType, InteriorType>
{

    /** The output category of the node. All nodes should have this, but it
     *  is absolutely required for a leaf node. */
    protected OutputType outputCategory;
    
    /**
     * Creates a new instance of CategorizationTreeNode.
     */
    public CategorizationTreeNode()
    {
        this(null, null);
    }
    
    /**
     * Creates a new instance of CategorizationTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   outputCategory The output category.
     */
    public CategorizationTreeNode(
        final DecisionTreeNode<InputType, OutputType> parent,
        final OutputType outputCategory)
    {
        this(parent, outputCategory, null);
    }
    
    /**
     * Creates a new instance of CategorizationTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   outputCategory The output category.
     * @param   incomingValue The incoming value.
     */
    public CategorizationTreeNode(
        final DecisionTreeNode<InputType, OutputType> parent,
        final OutputType outputCategory,
        final Object incomingValue)
    {
        this(parent, null, outputCategory, incomingValue);
    }
    
    /**
     * Creates a new instance of CategorizationTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   decider The decision function.
     * @param   outputCategory The output category.
     * @param   incomingValue The incoming value.
     */
    public CategorizationTreeNode(
        final DecisionTreeNode<InputType, OutputType> parent,
        final Categorizer<? super InputType, ? extends InteriorType> decider,
        final OutputType outputCategory,
        final Object incomingValue)
    {
        super(parent, decider, incomingValue);
        
        this.setOutputCategory(outputCategory);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public CategorizationTreeNode<InputType, OutputType, InteriorType> 
        clone()
    {
        return (CategorizationTreeNode<InputType, OutputType, InteriorType>)
            super.clone();
    }

    public OutputType getOutput(
        final InputType input)
    {
        return this.outputCategory;
    }
    
    /**
     * Gets the output category for the node.
     *
     * @return The output category.
     */
    public OutputType getOutputCategory()
    {
        return this.outputCategory;
    }

    /**
     * Sets the output category for the node.
     *
     * @param  outputCategory The output category.
     */
    public void setOutputCategory(
        final OutputType outputCategory)
    {
        this.outputCategory = outputCategory;
    }

}
