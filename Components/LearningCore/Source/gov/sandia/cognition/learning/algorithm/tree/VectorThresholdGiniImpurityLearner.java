/*
 * File:                VectorThresholdGiniImpurityLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 14, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;

/**
 * Learns vector thresholds based on the Gini impurity measure. It attempts to
 * minimize the Gini impurity in splits. If f_i is the fraction of examples
 * belonging to category i in split f, then the Gini impurity measure is defined
 * as:
 * <BR>     sum_i f_i * (1 - f_i)
 * <BR> Notice that sum_i f_i = 1, so the value will range between 0 and 1.
 * <BR><BR>
 * This measure is the one used in the Classification and Regression Tree
 * (CART) algorithm.
 *
 * @param   <OutputType>
 *      The type of the output categories to learn over.
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Decision tree learning",
    year=2010,
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Decision_tree_learning#Gini_impurity")
public class VectorThresholdGiniImpurityLearner<OutputType>
    extends AbstractVectorThresholdMaximumGainLearner<OutputType>
{

    /**
     * Creates a new instance of VectorThresholdGiniImpurityLearner.
     */
    public VectorThresholdGiniImpurityLearner()
    {
        super();
    }
    
    /**
     * Creates a new {@code VectorThresholdGiniImpurityLearner}.
     * 
     * @param   minSplitSize
     *      The minimum split size. Must be positive.
     */
    public VectorThresholdGiniImpurityLearner(
        final int minSplitSize)
    {
        super(minSplitSize, null);
    }

    @Override
    public VectorThresholdGiniImpurityLearner<OutputType> clone()
    {
        return (VectorThresholdGiniImpurityLearner<OutputType>) super.clone();
    }
    
    /**
     * Computes the split gain by computing the Gini impurity for the
     * given split.
     *
     * @param   baseCounts
     *      The histogram of counts before the split.
     * @param   positiveCounts
     *      The counts on the positive side of the threshold.
     * @param   negativeCounts
     *      The counts on the negative side of the threshold.
     * @return
     *      The split gain by computing the gain in Gini impurity for
     *      the given split. Will be between 0.0 and 1.0.
     */
    @Override
    public double computeSplitGain(
        final DefaultDataDistribution<OutputType> baseCounts,
        final DefaultDataDistribution<OutputType> positiveCounts,
        final DefaultDataDistribution<OutputType> negativeCounts)
    {
// TODO: This is almost the same as the code in the
// InformationGainLearner. They should be merged.
// -- jdbasil (2010-01-14)

        // Compute the initial impurity.
        final double impurityBase = giniImpurity(baseCounts);
        final double impurityPositive = giniImpurity(positiveCounts);
        final double impurityNegative = giniImpurity(negativeCounts);

        // Compute the proportion positive and negative.
        final double totalCount = baseCounts.getTotal();
        final double proportionPositive =
            positiveCounts.getTotal() / totalCount;
        final double proportionNegative =
            negativeCounts.getTotal() / totalCount;

        // Compute the gain in impurity.
        final double gain = impurityBase
            - proportionPositive * impurityPositive
            - proportionNegative * impurityNegative;

        // Return the gain.
        return gain;
    }

    /**
     * Computes the Gini impurity of a histogram. For each item in the
     * histogram, it is the probability that it is randomly assigned to the
     * wrong category, given the frequency of the different categories. This
     * is computed by looping over all the categories and multiplying the
     * fraction of elements in that category (f_i) times the probability of
     * choosing a different category (1 - f_i). That is:
     *
     *     sum_i f_i * (1 - f_i)
     *
     * @param <DataType>
     *      The type of data the counts are over.
     * @param   counts
     *      The distribution to compute the impurity over.
     * @return
     *      The Gini impurity of the given distribution.
     */
    public static <DataType> double giniImpurity(
        final DefaultDataDistribution<DataType> counts)
    {

        double sum = 0.0;
        for (DataType value : counts.getDomain())
        {
            final double fraction = counts.getFraction(value);
            sum += fraction * (1.0 - fraction);
        }

        return sum;
    }
}

