/*
 * File:                AbstractDecisionTreeLearner.java
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

import gov.sandia.cognition.algorithm.AbstractIterativeAlgorithm;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The {@code AbstractDecisionTreeLearner} implements common functionality for
 * learning algorithms that learn a decision tree.
 * 
 * @param  <InputType> The input type for the decision tree.
 * @param  <OutputType> The output type for the decision tree.
 * @author Justin Basilico
 * @since  2.0
 */
public abstract class AbstractDecisionTreeLearner<InputType, OutputType>
    extends AbstractIterativeAlgorithm
    implements Serializable
{
    /** The learning algorithm for the decision function. */
    protected DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner;
    
    /**
     * Creates a new instance of AbstractDecisionTreeLearner
     */
    public AbstractDecisionTreeLearner()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of AbstractDecisionTreeLearner.
     *
     * @param  deciderLearner The learner for the decision function
     */
    public AbstractDecisionTreeLearner(
        final DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner)
    {
        super();
        
        this.setDeciderLearner(deciderLearner);
    }
    
    /**
     * Recursively learns the decision tree using the given collection
     * of data, returning the created node.
     *
     * @param  data The set of data to learn a node from.
     * @param  parent The parent node.
     * @return The decision tree node learned from the given data.
     */
    protected abstract AbstractDecisionTreeNode<InputType, OutputType, ?> 
    learnNode(
        final Collection
            <? extends InputOutputPair<? extends InputType, OutputType>>
            data,
        final AbstractDecisionTreeNode<InputType, OutputType, ?> parent);
    
    /**
     * Learns the child nodes for a node using the given data at the node
     * plus the decision function for the node. It recursively calls the
     * learnNode method on each child and then adds the child to the given
     * node.
     *
     * @param  <DecisionType> The type of decision function.
     * @param  node
     *          The node to learn the children for. The child nodes are added 
     *          by this method.
     * @param  data The data at the node to learn the children for.
     * @param  decider The decision function to use.
     */
    protected <DecisionType> void learnChildNodes(
        final AbstractDecisionTreeNode<InputType, OutputType, DecisionType> 
            node,
        final Collection
            <? extends InputOutputPair<? extends InputType, OutputType>> 
            data,
        final Categorizer<? super InputType, ? extends DecisionType> decider)
    {
        // We split the data up by the decider that we just created.
        final Map<DecisionType, LinkedList<InputOutputPair<? extends InputType, OutputType>>>
            splitsMap = this.splitData(data, decider);

        if (splitsMap.size() < 2)
        {
            // Don't make child nodes if there are less than 2 children.
            return;
        }
        
        // Loop through the split up data and learn a child node for each value.
        for (Map.Entry<DecisionType, LinkedList<InputOutputPair<? extends InputType, OutputType>>> entry
            : splitsMap.entrySet())
        {
            // Get the decision value the child node is for.
            final DecisionType value = entry.getKey();

            // Learn the child node.
            final AbstractDecisionTreeNode<InputType, OutputType, ?> child =
                this.learnNode(entry.getValue(), node);

            if (child != null)
            {
                // Add the child.
                child.setIncomingValue(value);
                node.addChild(value, child);
            }
            // else - This should not happen unless something went wrong in
            //        the creation of the child.
        }
    }
    
    /**
     * Splits the data into new lists based on the given decision function.
     *
     * @param  <DecisionType> The type of decision function.
     * @param  data The data to split.
     * @param  decider The decision function.
     * @return
     *      A mapping of category decided by the decision function to a list
     *      of examples that have that value as indicated by the decision 
     *      function.
     */
    public <DecisionType> Map
        <DecisionType, LinkedList
            <InputOutputPair<? extends InputType, OutputType>>>
    splitData(
        final Collection
            <? extends InputOutputPair<? extends InputType, OutputType>> 
            data,
        final Categorizer<? super InputType, ? extends DecisionType> decider)
    {
        // Create the map to store the output.
        final Map<DecisionType, LinkedList
                <InputOutputPair<? extends InputType, OutputType>>>
            splitsMap = new HashMap
                <DecisionType, 
                 LinkedList
                    <InputOutputPair<? extends InputType, OutputType>>>(2);
        
        // Go through all the examples and apply the decider to them.
        for ( InputOutputPair<? extends InputType, OutputType> example : data )
        {
            // Make a decision on the value.
            final DecisionType value = decider.evaluate(example.getInput());
            
            // See if a split exists for the value.
            LinkedList<InputOutputPair<? extends InputType, OutputType>>
                split = splitsMap.get(value);
            
            if ( split == null )
            {
                // No split exists, so add it to the map.
                split = new LinkedList
                    <InputOutputPair<? extends InputType, OutputType>>();
                splitsMap.put(value, split);
            }
            
            // Add the example to the split.
            split.add(example);
        }
        
        // Return the map of splits.
        return splitsMap;
    }
    
    /**
     * Determines if all of the output values in the collection are equal. It
     * does this in a fast way by getting the first value and then checking
     * it against the subsequent values and failing if one does not match.
     *
     * @param  data The data to check for equality on the outputs.
     * @return True if all the output values are equal; otherwise, false.
     */
    public boolean areAllOutputsEqual(
        final Collection
            <? extends InputOutputPair<? extends InputType, OutputType>> 
            data)
    {
        // Store the output value to see if they are all equal.
        OutputType allOutput = null;
        for ( InputOutputPair<? extends InputType, OutputType> example : data )
        {
            // Get the output.
            final OutputType output = example.getOutput();
            
            if ( allOutput == null )
            {
                // First output.
                allOutput = output;
            }
            else if ( output != null && !allOutput.equals(output) )
            {
                // These output values are not equal.
                return false;
            }
        }
        
        // All the output values are equal.
        return true;
    }
    
    /**
     * Gets the learner for the decision function.
     *
     * @return deciderLearner The learner for the decision function
     */
    public DeciderLearner<? super InputType, OutputType, ?, ?> getDeciderLearner()
    {
        return this.deciderLearner;
    }

    /**
     * Sets the learner for the decision function.
     *
     * @param  deciderLearner The learner for the decision function
     */
    public void setDeciderLearner(
        final DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner)
    {
        this.deciderLearner = deciderLearner;
    }
}
