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
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /** Prior probabilities for the different categories.  If null,
     *  the priors default to the category frequencies of the training
     *  data.
     */
    protected Map<OutputType,Double> priors;

    /**
     * How often each category appears in training data.
     */
    protected transient Map<OutputType,Integer> trainCounts;


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
        this(deciderLearner, 
             DEFAULT_LEAF_COUNT_THRESHOLD, 
             DEFAULT_MAX_DEPTH,
             null);
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
        this(deciderLearner, leafCountThreshold, maxDepth, null);
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
     * @param   priors
     *      Prior probabilities for categories.  (See setCategoryPriors().)
     */
    public CategorizationTreeLearner(
        final DeciderLearner<? super InputType, OutputType, ?, ?> deciderLearner,
        final int leafCountThreshold,
        final int maxDepth,
        final Map<OutputType,Double> priors)
    {
        super(deciderLearner);

        this.setLeafCountThreshold(leafCountThreshold);
        this.setMaxDepth(maxDepth);
        this.setCategoryPriors(priors);
    }

    @Override
    public CategorizationTreeLearner<InputType, OutputType> clone()
    {
        final CategorizationTreeLearner<InputType, OutputType> result = (CategorizationTreeLearner<InputType, OutputType>) 
            super.clone();
        
        result.priors = this.priors == null ? null : new LinkedHashMap<>(this.priors);
        result.trainCounts = this.trainCounts == null ? null : new LinkedHashMap<>(this.trainCounts);
        
        return result;
    }
    
    @Override
    public CategorizationTree<InputType, OutputType> learn(
        Collection<? extends InputOutputPair<? extends InputType, OutputType>>
            data)
    {
        if ( data == null )
        {
            // Bad data.
            return null;
        }

        final DefaultDataDistribution<OutputType> rootCounts =
            getOutputCounts(data);

        trainCounts = new HashMap<OutputType,Integer>();
        for (OutputType category : rootCounts.getDomain()) 
        {
            int freq = (int)(rootCounts.get(category));
            trainCounts.put(category, new Integer(freq));
        }

        // Configure prior weights if supported by split criterion.
        if (deciderLearner instanceof PriorWeightedNodeLearner)
        {
            // The compiler is unable to figure out that this cast is
            // safe.  Suppress the incorrect warning.
            @SuppressWarnings("unchecked")
            PriorWeightedNodeLearner<OutputType> criterion =
                (PriorWeightedNodeLearner<OutputType>)(deciderLearner);
            criterion.configure(priors, trainCounts);
        }

        // Recursively learn the node.
	CategorizationTree<InputType, OutputType> tree = 
            new CategorizationTree<InputType, OutputType>(
                this.learnNode(data, null),
                new HashSet<OutputType>(rootCounts.getDomain()));

        trainCounts = null;
        return tree;
    }

    /**
     * Recursively learns the categorization tree using the given collection
     * of data, returning the created node.
     *
     * @param  data The set of data to learn a node from.
     * @param  parent The parent node.
     * @return The categorization tree node learned from the given data.
     */
    @Override
    protected CategorizationTreeNode<InputType, OutputType, ?> learnNode(
        final Collection<? extends InputOutputPair<? extends InputType, OutputType>> data,
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

        // We put the most probable output category on every node in
        // the tree, in case we get a bad decision function or leaf
        // node. This ensures That we can always make a
        // categorization.
        OutputType mostProbOutput = computeMaxProbPrediction(data);

        // Give the node we are creating the most probable output.
        final CategorizationTreeNode<InputType, OutputType, Object> node =
            new CategorizationTreeNode<InputType, OutputType, Object>(
                parent, mostProbOutput);

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
    public static <OutputType> DefaultDataDistribution<OutputType> getOutputCounts(
        final Collection<? extends InputOutputPair<?, OutputType>> data)
    {
        // Create the histogram.
        DefaultDataDistribution<OutputType> counts =
            new DefaultDataDistribution<OutputType>();
        
        if ( data == null )
        {
            // Bad data.
            return counts;
        }
        
        for ( InputOutputPair<?, OutputType> example : data )
        {
            // Add the output.
            final OutputType output = example.getOutput();
            counts.increment(output);
        }
        
        // Return the histogram.
        return counts;
    }

    /**
     * Return the most probable output value, taking into
     * consideration both the frequency counts in the data sample (at
     * the current node) and the prior proabalities for possible
     * output values.
     *
     * @param <OutputType> The type of outputs.
     * @param data 
     *    The data sample, with output labels for each data point.
     *    The sample must contain at least 1 data point.
     * @return The output value with highest conditional probability.
     */
    private OutputType computeMaxProbPrediction(
        final Collection<? extends InputOutputPair<?, OutputType>> data)
    {
        DefaultDataDistribution<OutputType> nodeCounts = getOutputCounts(data);
        if (priors == null) {
            // With no explicit prior, the most probable prediction is
            // the most common category.
            return nodeCounts.getMaxValueKey();
        }

        // Iterate over possible predictions, and keep track of the
        // prediction with highest probability.  Note that these
        // probabilities are not normalized.  (It would be easy, just
        // divide by sum of the unnormalized probs . . . but since
        // that would be a constant scaling, the maximum probability
        // prediction would be the same.)
        double bestProb = -1.0;
        OutputType bestVal = null;
        for (OutputType category : nodeCounts.getDomain()) {
            double likelihood = 
                nodeCounts.get(category) / trainCounts.get(category);
            double prior = priors.get(category);
            double prob = prior * likelihood;
            if (prob > bestProb) {
                bestProb = prob;
                bestVal = category;
            }
        }

        return bestVal;
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

    /**
     * <P>Set prior category probabilities.  A higher prior
     * probability for a category will cause the tree learner to
     * weight examples from that category more highly.</P>
     *
     * <P>If the priors are not manually specified (through this
     * method or passing priors into the constructor), prior
     * probabilities default to the frequencies of the different
     * categories in the training data.</P>
     *
     * @param priors 
     *    If null, use default prior probabilities.  Otherwise, priors
     *    becomes the new prior weights.  In the latter case,
     *    priors.keySet() contain the same values as the possible
     *    categories in data passed to the learn() method.
     */
    public void setCategoryPriors(Map<OutputType,Double> priors) 
    {
        if (priors == null) {
            this.priors = null;
        }
        else {
            this.priors = new LinkedHashMap<OutputType,Double>(priors);
        }
    }
}
