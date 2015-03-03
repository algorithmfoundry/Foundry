/*
 * File:                RegressionTree.java
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
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.function.regression.Regressor;

/**
 * The {@code RegressionTree} class extends the {@code DecisionTree} class
 * to implement a decision tree that does regression.
 *
 * @param  <InputType> The input type the regression tree evaluates.
 * @author Justin Basilico
 * @since  2.0
 */
public class RegressionTree<InputType>
    extends DecisionTree<InputType, Double>
    implements Regressor<InputType>
{
    /**
     * Creates a new instance of RegressionTree.
     */
    public RegressionTree()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of RegressionTree.
     *
     * @param  rootNode The root node of the tree.
     */
    public RegressionTree(
        final DecisionTreeNode<InputType, Double> rootNode)
    {
        super(rootNode);
    }

    @Override
    public RegressionTree<InputType> clone()
    {
        return (RegressionTree<InputType>) super.clone();
    }
    
    @Override
    public double evaluateAsDouble(
        final InputType input)
    {
        return this.evaluate(input);
    }

}
