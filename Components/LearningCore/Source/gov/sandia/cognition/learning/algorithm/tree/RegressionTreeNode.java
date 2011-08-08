/*
 * File:                RegressionTreeNode.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 29, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.categorization.Categorizer;

/**
 * The {@code RegressionTreeNode} implements a {@code DecisionTreeNode} for
 * a tree that does regression.
 *
 * @param  <InputType> The input type for the tree.
 * @param  <InteriorType> The type that is the output of the decision made at 
 *         this tree node.
 * @author Justin Basilico
 * @since  2.0
 */
public class RegressionTreeNode<InputType, InteriorType>
    extends AbstractDecisionTreeNode<InputType, Double, InteriorType>
{
    /** The default value for the node is 0.0. */
    public static final double DEFAULT_VALUE = 0.0;

    
    /** The function to apply for leaf nodes. */
    protected Evaluator<? super InputType, Double> scalarFunction;
    
    /** The value stored at the tree node. It is used as a backup value if
     *  no scalar function exists for the node but the output is requested. */
    protected double value;

    
    /**
     * Creates a new instance of RegressionTreeNode.
     */
    public RegressionTreeNode()
    {
        this(null, DEFAULT_VALUE);
    }
    
    /**
     * Creates a new instance of RegressionTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   value The value stored at the node.
     */
    public RegressionTreeNode(
        final DecisionTreeNode<InputType, Double> parent,
        final double value)
    {
        this(parent, null, null, value, null);
    }

    /**
     * Creates a new instance of RegressionTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   decider The decision function for interior nodes.
     * @param   value The value stored at the node.
     */
    public RegressionTreeNode(
        final DecisionTreeNode<InputType, Double> parent,
        final Categorizer<? super InputType, ? extends InteriorType> decider,
        final double value)
    {
        this(parent, decider, null, value, null);
    }
    
    /**
     * Creates a new instance of RegressionTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   scalarFunction The scalar function to apply at leaf nodes.
     * @param   value The value stored at the node.
     */
    public RegressionTreeNode(
        final DecisionTreeNode<InputType, Double> parent,
        final Evaluator<? super InputType, Double> scalarFunction,
        final double value)
    {
        this(parent, scalarFunction, value, null);
    }
    
    /**
     * Creates a new instance of RegressionTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   scalarFunction The scalar function to apply at leaf nodes.
     * @param   value The value stored at the node.
     * @param   incomingValue The incoming value.
     */
    public RegressionTreeNode(
        final DecisionTreeNode<InputType, Double> parent,
        final Evaluator<? super InputType, Double> scalarFunction,
        final double value,
        final Object incomingValue)
    {
        this(parent, null, scalarFunction, value, incomingValue);
    }
    
    /**
     * Creates a new instance of RegressionTreeNode.
     *
     * @param   parent The parent node of this node. Null if this is a root.
     * @param   decider The decision function for interior nodes.
     * @param   scalarFunction The scalar function to apply at leaf nodes.
     * @param   value The value stored at the node.
     * @param   incomingValue The incoming value.
     */
    public RegressionTreeNode(
        final DecisionTreeNode<InputType, Double> parent,
        final Categorizer<? super InputType, ? extends InteriorType> decider,
        final Evaluator<? super InputType, Double> scalarFunction,
        final double value,
        final Object incomingValue)
    {
        super(parent, decider, incomingValue);
        
        this.setScalarFunction(scalarFunction);
        this.setValue(value);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public RegressionTreeNode<InputType, InteriorType> 
        clone()
    {
        return (RegressionTreeNode<InputType, InteriorType>)
            super.clone();
    }

    public Double getOutput(
        final InputType input)
    {
        if ( this.scalarFunction == null )
        {
            // No function, so output the value at the node.
            return value;
        }
        else
        {
            // Evaluate the function on the output.
            return this.scalarFunction.evaluate(input);
        }
    }

    /**
     * Gets the scalar function applied to the input when the node is a leaf 
     * node.
     *
     * @return  The scalar function applied to the input for leaves.
     */
    public Evaluator<? super InputType, Double> getScalarFunction()
    {
        return this.scalarFunction;
    }
    
    /**
     * Sets the scalar function applied to the input when the node is a leaf 
     * node.
     *
     * @param   scalarFunction 
     *          The scalar function applied to the input for leaves.
     */
    public void setScalarFunction(
        final Evaluator<? super InputType, Double> scalarFunction)
    {
        this.scalarFunction = scalarFunction;
    }
    
    /**
     * Gets the value stored at the node, which is usually the mean value.
     * 
     * @return The value stored at the node.
     */
    public double getValue()
    {
        return this.value;
    }
    
    /**
     * Sets the value stored at the node, which is usually the mean value.
     * 
     * @param   value 
     *          The value stored at the node.
     */
    public void setValue(
        final double value)
    {
        this.value = value;
    }
}
