/*
 * File:                VectorDeciderLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;

/**
 * The {@code VectorThresholdInformationGainLearner} computes the best 
 * threshold over a dataset of vectors using information gain to determine the 
 * optimal index and threshold. This is an implementation of what is used in
 * the C4.5 decision tree algorithm.
 *
 * Information gain for a given split (sets X and Y) for two categories (a and b):
 *     ig(X, Y) = entropy(X + Y)
 *              – (|X| / (|X| + |Y|)) entropy(X)
 *              – (|Y| / (|X| + |Y|)) entropy(Y)
 * with
 *
 *     entropy(Z) = - (Za / |Z|) log2(Za / |Z|) – (Zb / |Z|) log2(Zb / |Z|)
 *
 * where
 *     Za = number of a's in Z, and
 *     Zb = number of b's in Z.
 * In the multi-class case, the entropy is defined as the sum over all of the
 * categories (c) of -Zc / |Z| log2(Zc / |Z|).
 *
 * @param  <OutputType> The output type of the data.
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorThresholdInformationGainLearner<OutputType>
    extends AbstractVectorThresholdMaximumGainLearner<OutputType>
{

    /**
     * Creates a new instance of VectorDeciderLearner.
     */
    public VectorThresholdInformationGainLearner()
    {
        super();
    }
    
    public double computeSplitGain(
        final MapBasedDataHistogram<OutputType> baseCounts,
        final MapBasedDataHistogram<OutputType> positiveCounts,
        final MapBasedDataHistogram<OutputType> negativeCounts)
    {
        final int totalCount = baseCounts.getTotalCount();
        final double entropyBase = baseCounts.getEntropy();
        final double entropyPositive = positiveCounts.getEntropy();
        final double entropyNegative = negativeCounts.getEntropy();
        final double proportionPositive =
            (double) positiveCounts.getTotalCount() / totalCount;
        final double proportionNegative =
            (double) negativeCounts.getTotalCount() / totalCount;

        final double gain = entropyBase
            - proportionPositive * entropyPositive
            - proportionNegative * entropyNegative;

        return gain;
    }
}
