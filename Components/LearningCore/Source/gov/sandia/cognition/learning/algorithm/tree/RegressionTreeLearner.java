/*
 * File:                RegressionTreeLearner.java
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
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import java.util.Collection;

/**
 * The {@code RegressionTreeLearner} class implements a learning algorithm for
 * a regression tree that makes use of a decider learner and a regresion 
 * learner. The tree grows as a decision tree until it gets to a leaf node
 * (determined by a minimum number of nodes), and then learns a regression
 * function at the leaf node.
 *
 * @param  <InputType> The type of the input to the tree.
 * @author Justin Basilico
 * @since  2.0
 */
public class RegressionTreeLearner<InputType>
    extends AbstractDecisionTreeLearner<InputType, Double>
    implements SupervisedBatchLearner<InputType, Double, RegressionTree<InputType>>
{

    /** The default threshold for making a leaf node based on count. */
    public static final int DEFAULT_LEAF_COUNT_THRESHOLD = 4;

    /** The learning algorithm for the regression function. */
    protected BatchLearner
        <Collection<? extends InputOutputPair<? extends InputType, Double>>, 
         ? extends Evaluator<? super InputType, Double>> 
         regressionLearner;

    /** The threshold for making a node a leaf, determined by how many 
     *  instances fall in the threshold. */
    protected int leafCountThreshold;

    /**
     * Creates a new instance of RegressionTreeLearner
     */
    public RegressionTreeLearner()
    {
        this(null, null);
    }

    /**
     * Creates a new instance of CategorizationTreeLearner.
     *
     * @param  deciderLearner The learner for the decision function.
     * @param  regressionLearner The learner for the regression function.
     */
    public RegressionTreeLearner(
        final DeciderLearner<? super InputType, Double, ?, ?> deciderLearner,
        final BatchLearner
            <Collection<? extends InputOutputPair<? extends InputType, Double>>, 
             ? extends Evaluator<? super InputType, Double>>  
             regressionLearner)
    {
        this(deciderLearner, regressionLearner, DEFAULT_LEAF_COUNT_THRESHOLD);
    }

    /**
     * Creates a new instance of CategorizationTreeLearner.
     *
     * @param   deciderLearner The learner for the decision function.
     * @param   regressionLearner The learner for the regression function.
     * @param   leafCountThreshold 
     *          The leaf count threshold, which determines the number of 
     *          elements at which to learn a regression function.
     */
    public RegressionTreeLearner(
        final DeciderLearner<? super InputType, Double, ?, ?> deciderLearner,
        final BatchLearner
            <Collection<? extends InputOutputPair<? extends InputType, Double>>, 
             ? extends Evaluator<? super InputType, Double>>  
             regressionLearner,
        final int leafCountThreshold)
    {
        super(deciderLearner);

        this.setRegressionLearner(regressionLearner);
        this.setLeafCountThreshold(leafCountThreshold);
    }

    /**
     * {@inheritDoc}
     *
     * @param  data {@inheritDoc}
     * @return {@inheritDoc}
     */
    public RegressionTree<InputType> learn(
        Collection<? extends InputOutputPair<? extends InputType, Double>> data)
    {
        if (data == null)
        {
            // Bad data.
            return null;
        }
        else
        {
            // Recursively learn the node.
            return new RegressionTree<InputType>(
                this.learnNode(data, null));
        }
    }

    /**
     * Recursively learns the regression tree using the given collection
     * of data, returning the created node.
     *
     * @param  data The set of data to learn a node from.
     * @param  parent The parent node.
     * @return The regression tree node learned from the given data.
     */
    protected RegressionTreeNode<InputType, ?> learnNode(
        final Collection<? extends InputOutputPair<? extends InputType, Double>> data,
        final AbstractDecisionTreeNode<InputType, Double, ?> parent)
    {

        if (data == null || data.size() <= 0)
        {
            // Invalid data, nothing to learn.
            return null;
        }

        // Determine if this is a leaf node by checking the cound threshold and
        // determining if all the outputs are equal.
        final boolean isLeaf = data.size() <= this.leafCountThreshold || this.areAllOutputsEqual(data);

        // We use the mean value as part of the node.
        final double mean = DatasetUtil.computeOutputMean(data);

        // Learn the decision function for this node.
        Categorizer<? super InputType, ? extends Object> decider = null;
        if (!isLeaf)
        {
            // Only learn for a leaf node.
            decider = this.getDeciderLearner().learn(data);
        }

        // If we couldn't learn a decider, then this is also aleaf node.
        if (isLeaf || decider == null)
        {
            // This is a leaf node.
            // Build a regression function for the node.
            Evaluator<? super InputType, Double> scalarFunction = null;

            if (this.regressionLearner != null)
            {
                scalarFunction = this.regressionLearner.learn(data);
            }
            // else - Without a regression learner the output value for the
            //        tree will be the mean.

            // Create the leaf node.
            return new RegressionTreeNode<InputType, Object>(scalarFunction,
                mean);
        }

        // We give the node we are creating the most common output value.
        final RegressionTreeNode<InputType, Object> node =
            new RegressionTreeNode<InputType, Object>(decider, mean);

        // Learn the child nodes.
        this.learnChildNodes(node, data, decider);

        // Return the new node we've created.
        return node;
    }

    /**
     * Gets the regression learner that is to be used to fit a function
     * approximator to the values in the tree.
     *
     * @return  The regression learner.
     */
    public BatchLearner
        <Collection<? extends InputOutputPair<? extends InputType, Double>>, 
         ? extends Evaluator<? super InputType, Double>>
        getRegressionLearner()
    {
        return this.regressionLearner;
    }

    /**
     * Sets the regression learner that is to be used to fit a function
     * approximator to the values in the tree.
     *
     * @param   regressionLearner The regression learner.
     */
    public void setRegressionLearner(
        final BatchLearner
            <Collection<? extends InputOutputPair<? extends InputType, Double>>, 
             ? extends Evaluator<? super InputType, Double>>  
             regressionLearner)
    {
        this.regressionLearner = regressionLearner;
    }

    /**
     * Gets the leaf count threshold, which determines the number of elements
     * at which to learn a regression function.
     *
     * @return The leaf count threshold.
     */
    public int getLeafCountThreshold()
    {
        return this.leafCountThreshold;
    }

    /**
     * Sets the leaf count threshold, which determines the number of elements
     * at which to learn a regression function.
     *
     * @param   leafCountThreshold 
     *          The leaf count threshold. Must be non-negative.
     */
    public void setLeafCountThreshold(
        final int leafCountThreshold)
    {
        if (leafCountThreshold < 0)
        {
            throw new IllegalArgumentException(
                "leafCountThreshold cannot be negative");
        }

        this.leafCountThreshold = leafCountThreshold;
    }

}
