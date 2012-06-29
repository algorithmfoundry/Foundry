/*
 * File:                PriorWeightedNodeLearner.java
 * Authors:             Art Munson
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 6, 2011, Sandia Corporation.  Under the terms
 * of Contract DE-AC04-94AL85000, there is a non-exclusive license for
 * use of this work by or on behalf of the U.S. Government. Export of
 * this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import java.util.Map;

/**
 * The {@code PriorWeightedNodeLearner} interface specifies the
 * ability to configure prior weights on the learning algorithm that
 * searches for a decision function inside a decision tree.  The
 * {@code CategorizationTreeLearner} checks if the split criterion
 * supports this interface, and if it does, configures the split
 * criterion with prior weights and counts.
 *
 * Classes implementing {@code DeciderLearner} or {@code
 * VectorThresholdMaximumGainLearner} should consider whether it makes
 * sense to also implement this class.
 *
 * @param <OutputType>  The (output) type for the decision tree.  E.g., Integer.
 * @author   Art Munson
 * @since    3.4
 */
public interface PriorWeightedNodeLearner<OutputType>
{
    /**
     * Configure the node learner with prior weights and training counts.
     * <BR>
     * <BR>
     * If the prior weights are not specified, this method will
     * configure default priors that match the relative frequencies of
     * the different categories in the training data.  The frequencies
     * are based on the given category counts from the training data.
     *
     * @param priors
     *    Prior weights for each of the possible output values (i.e.,
     *    the categories for the prediction task).  If null, the
     *    method will estimate default priors from the training
     *    counts.
     * @param trainCounts
     *    Frequency counts of the possible output values (i.e.,
     *    categories) in the training data.  This parameter should
     *    always be specified.
     */
    public void configure(
        final Map<OutputType,Double> priors,
        final Map<OutputType,Integer> trainCounts);
}
