/*
 * File:                CategorizationTreeLearner.java
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

import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Collection;
import java.util.HashSet;

/**
 * The {@code CategorizationTreeLearner} class implements a supervised learning 
 * algorithm for learning a categorization tree.
 *
 * @param  <InputType> The input type for the tree.
 * @param  <OutputType> The output type for the tree.
 * @author Justin Basilico
 * @since  2.0
 */
public class CategorizationTreeLearner<InputType, OutputType>
    extends AbstractDecisionTreeLearner<InputType, OutputType>
    implements SupervisedBatchLearner<InputType, OutputType, CategorizationTree<InputType, OutputType>>
{

    /** The default threshold for making a leaf node based on count. */
    public static final int DEFAULT_LEAF_COUNT_THRESHOLD = 1;

    /** The default maximum depth to grow the tree to. */
    public static final int DEFAULT_MAX_DEPTH = -1;

    /** The threshold for making a node a leaf, determined by how many
     *  instances fall in the threshold. */
    protected int leafCountThreshold;

    /** The maximum depth for the tree. Ignored if less than 1. */
    protected int maxDepth;

    /**
     * Creates a new instance of CategorizationTreeLearner.
     */
    public CategorizationTreeLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of CategorizationTreeLearner.
     *
     * @param  deciderLearner The learner for the decision function
     */
    public CategorizationTreeLearner(
        final DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner)
    {
        this(deciderLearner, DEFAULT_LEAF_COUNT_THRESHOLD, DEFAULT_MAX_DEPTH);
    }

    /**
     * Creates a new instance of CategorizationTreeLearner.
     *
     * @param   deciderLearner
     *      The learner for the decision function.
     * @param   leafCountThreshold
     *      The leaf count threshold. Must be non-negative.
     * @param   maxDepth
     *      The maximum depth for the tree.
     */
    public CategorizationTreeLearner(
        final DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner,
        final int leafCountThreshold,
        final int maxDepth)
    {
        super(deciderLearner);

        this.setLeafCountThreshold(leafCountThreshold);
        this.setMaxDepth(maxDepth);
    }

    public CategorizationTree<InputType, OutputType> learn(
        Collection<? extends InputOutputPair<? extends InputType, OutputType>>
            data)
    {
        if ( data == null )
        {
            // Bad data.
            return null;
        }
        else
        {
            // Recursively learn the node.
            final MapBasedDataHistogram<OutputType> rootCounts = 
                getOutputCounts(data);
	    
            return new CategorizationTree<InputType, OutputType>(
                this.learnNode(data, null),
                new HashSet<OutputType>(rootCounts.getDomain()));
        }
    }

    /**
     * Recursively learns the categorization tree using the given collection
     * of data, returning the created node.
     *
     * @param  data The set of data to learn a node from.
     * @param  parent The parent node.
     * @return The categorization tree node learned from the given data.
     */
    protected CategorizationTreeNode<InputType, OutputType, ?> learnNode(
        final Collection
            <? extends InputOutputPair<? extends InputType, OutputType>>
            data,
        final AbstractDecisionTreeNode<InputType, OutputType, ?> parent)
    {
        if (data == null || data.size() <= 0)
        {
            // Invalid data, nothing to learn. This case should never happen.
            return null;
        }


// TODO: Revisit the tree data structures later to determine if
// really necessary to include category on internal nodes.  An
// implementation should be possible that does not spend the
// storage on internal nodes and only searches for the most
// common output when it is known that the node is a leaf.
//
// Also, would it be cleaner to push areAllOutputsEqual() into
// the search for a decision function?  Both of these likely
// need to scan through all the data.
// -- mamunso (2010/11/18)

        // We put the most common output category on every node in the
        // tree, in case we get a bad decision function or leaf
        // node. This ensures That we can always make a
        // categorization.

        // Prediction at the leaf is the most common output category.
        // (We know that mostCommonOutput is not null because we
        // already checked that the data's sizes is greater than
        // zero.)
        final OutputType mostCommonOutput =
            getOutputCounts(data).getMaximumValue();

        // We give the node we are creating the most common output value.
        final CategorizationTreeNode<InputType, OutputType, Object> node =
            new CategorizationTreeNode<InputType, OutputType, Object>(
                parent, mostCommonOutput);

        // Check for termination conditions that produce a leaf node.
        boolean isLeaf = this.areAllOutputsEqual(data)
            || data.size() <= this.leafCountThreshold
            || (this.maxDepth > 0 && node.getDepth() >= this.maxDepth);

        if (!isLeaf)
        {
            // Search for a decision function to split the data.
            final Categorizer<? super InputType, ? extends Object> decider =
                this.getDeciderLearner().learn(data);

            if (decider != null)
            {
                // We learned a good decider.
                node.setDecider(decider);

                // Learn the child nodes.
                super.learnChildNodes(node, data, decider);
            }
            else
            {
                // Failed to find a decision function ==> there is no
                // attribute that separates the values of different
                // output categories.  This node necessarily becomes a
                // leaf.
                isLeaf = true;
            }
        }

        // Return the new node we've created.
        return node;
    }
    
    /**
     * Creates a histogram of values based on the output values in the given
     * collection of pairs.
     *
     * @param   <OutputType> The type of the outputs to count over.
     * @param  data The data to create the output count histogram for.
     * @return The output count histogram.
     */
    public static <OutputType> MapBasedDataHistogram<OutputType> getOutputCounts(
        final Collection
            <? extends InputOutputPair<?, OutputType>> 
            data)
    {
        // Create the histogram.
        MapBasedDataHistogram<OutputType> counts =
            new MapBasedDataHistogram<OutputType>();
        
        if ( data == null )
        {
            // Bad data.
            return counts;
        }
        
        for ( InputOutputPair<?, OutputType> example : data )
        {
            // Add the output.
            final OutputType output = example.getOutput();
            counts.add(output);
        }
        
        // Return the histogram.
        return counts;
    }


    /**
     * Gets the leaf count threshold, which determines the number of elements
     * at which to make an element into a leaf.
     *
     * @return The leaf count threshold.
     */
    public int getLeafCountThreshold()
    {
        return this.leafCountThreshold;
    }

    /**
     * Sets the leaf count threshold, which determines the number of elements
     * at which to make an element into a leaf.
     *
     * @param   leafCountThreshold
     *          The leaf count threshold. Must be non-negative.
     */
    public void setLeafCountThreshold(
        final int leafCountThreshold)
    {
        ArgumentChecker.assertIsNonNegative("leafCountThreshold", leafCountThreshold);
        this.leafCountThreshold = leafCountThreshold;
    }

    /**
     * Gets the maximum depth to grow the tree.
     *
     * @return
     *      The maximum depth to grow the tree. Zero or less means no
     *      maximum depth.
     */
    public int getMaxDepth()
    {
        return this.maxDepth;
    }

    /**
     * Sets the maximum depth to grow the tree.
     *
     * @param   maxDepth
     *      The maximum depth to grow the tree. Zero or less means no
     *      maximum depth.
     */
    public void setMaxDepth(
        final int maxDepth)
    {
        this.maxDepth = maxDepth;
    }
}
