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

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * The {@code VectorThresholdInformationGainLearner} computes the best 
 * threshold over a dataset of vectors using information gain to determine the 
 * optimal index and threshold. This is an implementation of what is used in
 * the C4.5 decision tree algorithm.
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
