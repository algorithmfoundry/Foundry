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

    /** The threshold for making a node a leaf, determined by how many
     *  instances fall in the threshold. */
    protected int leafCountThreshold;

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
        this(deciderLearner, DEFAULT_LEAF_COUNT_THRESHOLD);
    }

    /**
     * Creates a new instance of CategorizationTreeLearner.
     *
     * @param   deciderLearner
     *      The learner for the decision function.
     * @param   leafCountThreshold
     *          The leaf count threshold. Must be non-negative.
     */
    public CategorizationTreeLearner(
        final DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner,
        final int leafCountThreshold)
    {
        super(deciderLearner);

        this.setLeafCountThreshold(leafCountThreshold);
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
                new HashSet<OutputType>(rootCounts.getValues()));
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
        if ( data == null || data.size() <= 0 )
        {
            // Invalid data, nothing to learn.
            return null;
        }
        
        // Determine if this is a leaf node by determining if all the outputs
        // are equal.
        if ( this.areAllOutputsEqual(data) )
        {
            // This is a leaf node.
            // Get the output label of the first value, since we know they
            // are all equal.
            final OutputType allOutput = data.iterator().next().getOutput();
            
            // Create the leaf node.
            return new CategorizationTreeNode
                <InputType, OutputType, Object>(allOutput);
        }
        
        // We put the most common output category on every node in the tree,
        // in case we get a bad decision function or leaf node. This ensures
        // That we can always make a categorization. 
        final MapBasedDataHistogram<OutputType> counts = 
            getOutputCounts(data);
        final OutputType mostCommonOutput = counts.getMaximumValue();
        // We know that mostCommonOutput is not null because we already checked
        // that the data's sizes is greater than zero.

        // We give the node we are creating the most common output value.
        final CategorizationTreeNode<InputType, OutputType, Object>
            node =
            new CategorizationTreeNode<InputType, OutputType, Object>(
                mostCommonOutput);

        if (data.size() <= this.leafCountThreshold)
        {
            // This is a leaf node.
            return node;
        }
        
        // Learn the decision function for this node.
        final Categorizer<? super InputType, ? extends Object> decider = 
            this.getDeciderLearner().learn(data);
        
        
        if ( decider == null )
        {
            // This is the case where no decision could be made at the node,
            // in which case we are forced to make a leaf node. This can happen
            // if there is no attribute that separates the values of the
            // different output categories.
            return node;
        }
        
        node.setDecider(decider);
        
        // Learn the child nodes.
        super.learnChildNodes(node, data, decider);
        
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
        if (leafCountThreshold < 0)
        {
            throw new IllegalArgumentException(
                "leafCountThreshold cannot be negative");
        }

        this.leafCountThreshold = leafCountThreshold;
    }
}
