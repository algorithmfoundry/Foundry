/*
 * File:                AbstractVectorThresholdMaximumGainLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
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
 * An abstract class for decider learners that produce a threshold function
 * on a vector element based on maximizing some gain value. It handles the
 * looping over the elements of the vector and then for each element looping
 * over the possible split points. Subclasses only need to define a method to
 * compute the gain of a given split.
 * 
 * @param   <OutputType>
 *      The output category type for the training data.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractVectorThresholdMaximumGainLearner<OutputType>
    extends AbstractCloneableSerializable
    implements DeciderLearner<Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer>
{

    /**
     * Creates a new {@code AbstractVectorThresholdMaximumGainLearner}.
     */
    public AbstractVectorThresholdMaximumGainLearner()
    {
        super();
    }

    public VectorElementThresholdCategorizer learn(
        final Collection<? extends InputOutputPair<? extends Vectorizable, OutputType>> data)
    {
        if (CollectionUtil.isEmpty(data))
        {
            // Nothing to learn.
            return null;
        }

        // Compute the base count values for the node.
        final MapBasedDataHistogram<OutputType> baseCounts =
            CategorizationTreeLearner.getOutputCounts(data);

        // Figure out the dimensionality of the data.
        final int dimensionality = getDimensionality(data);

        // Go through all the dimensions to find the one with the best gain
        // and the best threshold.
        double bestGain = -1.0;
        int bestIndex = -1;
        double bestThreshold = 0.0;
        for (int i = 0; i < dimensionality; i++)
        {
            // Compute the best gain-threshold pair for the given dimension of
            // the data.
            final DefaultPair<Double, Double> gainThresholdPair =
                this.computeBestGainAndThreshold(data, i, baseCounts);

            if (gainThresholdPair == null)
            {
                // There was no gain-threshold pair that created a
                // threshold.
                continue;
            }

            // Get the gain from the pair.
            final double gain = gainThresholdPair.getFirst();

            // Determine if this is the best gain seen.
            if (bestIndex == -1 || gain > bestGain)
            {
                // This is the best gain, so store the gain, threshold,
                // and index.
                final double threshold = gainThresholdPair.getSecond();
                bestGain = gain;
                bestIndex = i;
                bestThreshold = threshold;
            }
        }

        if (bestIndex < 0)
        {
            // There was no dimension that provided any gain for the data,
            // so no decision function can be made.
            return null;
        }
        else
        {
            // Create the decision function for the best gain.
            return new VectorElementThresholdCategorizer(
                bestIndex, bestThreshold);
        }
    }

    /**
     * Computes the best gain and threshold for a given dimension using the
     * computeSplitGain method for each potential split point of values for the
     * given dimension.
     *
     * @param   data
     *      The data to use to compute the threshold.
     * @param   dimension
     *      The dimension to compute the threshold for.
     * @param   baseCounts
     *      Information about the base category counts.
     * @return
     *      A pair containing the best gain computed and its associated
     *      threshold. If there is no good split point, null is returned. This
     *      can happen if there is no data or every value is the same.
     */
    public DefaultPair<Double, Double> computeBestGainAndThreshold(
        final Collection<? extends InputOutputPair<? extends Vectorizable, OutputType>>
            data,
        final int dimension,
        final MapBasedDataHistogram<OutputType> baseCounts)
{
        // To compute the gain we will sort all of the values along the given
        // dimension and then walk along the values to determine the best
        // threshold.

        // The first step is to create a list of (value, output) pairs for the
        // given dimension.
        final int totalCount = data.size();
        final ArrayList<DefaultPair<Double, OutputType>> values =
            new ArrayList<DefaultPair<Double, OutputType>>(totalCount);
        for (InputOutputPair<? extends Vectorizable, OutputType> example
            : data)
        {
            // Add this example to the list.
            final Vector input = example.getInput().convertToVector();
            final OutputType output = example.getOutput();
            final double value = input.getElement(dimension);

            values.add(new DefaultPair<Double, OutputType>(value, output));
        }

        // Sort the list in ascending order by value.
        Collections.sort(values, new Comparator<DefaultPair<Double, OutputType>>()
        {
            public int compare(
                DefaultPair<Double, OutputType> o1,
                DefaultPair<Double, OutputType> o2)
            {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });

        // If all the values on this dimension are the same then there is
        // nothing to split on.
        if (    totalCount <= 1
             || values.get(0).getFirst().equals(values.get(totalCount - 1).getFirst()))
        {
            // All of the values are the same.
            return null;
        }

        // In order to find the best split we are going to keep track of the
        // counts of each label on each side of the threshold. This means
        // that we maintain two counting objects.
        // To start with all of the examples are on the positive side of
        // the split, so we initialize the base counts (all the data points)
        // and the negative counts with nothing.
        final MapBasedDataHistogram<OutputType> positiveCounts =
            baseCounts.clone();
        final MapBasedDataHistogram<OutputType> negativeCounts =
            new MapBasedDataHistogram<OutputType>();

        // We are going to loop over all the values to compute the best
        // gain and the best threshold.
        double bestGain = 0.0;
        double bestTieBreaker = 0.0;
        double bestThreshold = 0.0;

        // We need to keep track of the previous value for two reasons:
        //    1) To determine if we've already tested the value, since we loop
        //       over a >= threshold.
        //    2) So that the threshold can be computed to be half way between
        //       two values.
        double previousValue = 0.0;
        for (int i = 0; i < totalCount; i++)
        {
            final DefaultPair<Double, OutputType> valueLabel = values.get(i);
            final double value = valueLabel.getFirst();
            final OutputType label = valueLabel.getSecond();

            if (i == 0)
            {
                // We are going to loop over a threshold value that is >=.
                // Since there is no point on splitting on the first value,
                // since nothing will be less than it, we skip it. However, we
                // do need to add it to the counts.
                bestGain = 0.0;
                bestTieBreaker = 0.0;
                bestThreshold = value;
            }
            else if (value != previousValue)
            {
                // Compute the gain.
                final double gain = computeSplitGain(
                    baseCounts, positiveCounts, negativeCounts);

                if (gain >= bestGain)
                {
                    final double proportionPositive =
                        (double) positiveCounts.getTotalCount() / totalCount;
                    final double proportionNegative =
                        (double) negativeCounts.getTotalCount() / totalCount;

                    // This is our tiebreaker criteria for the case where the
                    // gains are equal. It means that we prefer ties that are
                    // more balanced in how they split (50%/50% being optimal).
                    final double tieBreaker = 1.0
                        - Math.abs(proportionPositive - proportionNegative);

                    if (    gain > bestGain
                         || tieBreaker > bestTieBreaker)
                    {
                        // For the decision threshold we actually want to pick
                        // the point that is half way between the current value
                        // and the previous value. Hopefully this will be more
                        // robust than using just the value itself.
                        final double threshold =
                            (value + previousValue) / 2.0;

                        bestGain = gain;
                        bestTieBreaker = tieBreaker;
                        bestThreshold = threshold;
                    }
                }
            }
            // else - This threshold was equal to the previous one. Since we
            //        use a >= cutting criteria,


            // For the next loop we remove the label from the positive side
            // and add it to the negative side of the threshold.
            positiveCounts.remove(label);
            negativeCounts.add(label);

            // Store this value as the previous value.s
            previousValue = value;
        }

        // Return the pair containing the best gain and best threshold
        // found.
        return new DefaultPair<Double, Double>(bestGain, bestThreshold);
    }

    /**
     * Computes the gain of a given split. The base counts contains the
     * category information before the split.
     *
     * @param   baseCounts
     *      The base category information before splitting. Contains the sum of
     *      the positive and negative counts.
     * @param positiveCounts
     *      The category information on the positive side of the split.
     * @param negativeCounts
     *      The category information on the negative side of the split.
     * @return
     *      The gain of the given split computed by comparing the positive and
     *      negative counts to the base counts.
     */
    public abstract double computeSplitGain(
        final MapBasedDataHistogram<OutputType> baseCounts,
        final MapBasedDataHistogram<OutputType> positiveCounts,
        final MapBasedDataHistogram<OutputType> negativeCounts);

    /**
     * Figures out the dimensionality of the Vector data.
     *
     * @param  data The data.
     * @return The dimensionality of the data in the vector.
     */
    protected static int getDimensionality(
        final Collection
            <? extends InputOutputPair<? extends Vectorizable, ?>>
            data)
    {
        if (CollectionUtil.isEmpty(data))
        {
            // Bad data.
            return 0;
        }
        else
        {
            // Get the dimensionality of the first data element.
            return CollectionUtil.getFirst(data).getInput().convertToVector()
                .getDimensionality();
        }
    }
}
