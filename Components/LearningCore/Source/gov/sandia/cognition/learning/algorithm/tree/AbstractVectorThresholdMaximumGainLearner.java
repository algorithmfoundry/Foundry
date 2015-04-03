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

import gov.sandia.cognition.collection.ArrayUtil;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
    implements VectorThresholdLearner<OutputType>
{

    /** The default value for the minimum split size is {@value}. */
    public static final int DEFAULT_MIN_SPLIT_SIZE = 1;
    
    /** The threshold for allowing a split to be made, determined by how many
     *  instances fall in each left or right sides of the split. Both sides
     *  must have at least this number of instances. Must be positive. */
    protected int minSplitSize;

    /** The array of dimensions for the learner to consider. If this is null,
     *  then all dimensions are considered. */
    protected int[] dimensionsToConsider;

    /**
     * Creates a new {@code AbstractVectorThresholdMaximumGainLearner}.
     */
    public AbstractVectorThresholdMaximumGainLearner()
    {
        this(DEFAULT_MIN_SPLIT_SIZE, null);
    }

    /**
     * 
     * Creates a new {@code AbstractVectorThresholdMaximumGainLearner}.
     * 
     * @param   minSplitSize
     *      The minimum split size. Must be positive.
     * @param   dimensionsToConsider
     *      The array of vector dimensions to consider. Null means all of them
     *      are considered.
     */
    public AbstractVectorThresholdMaximumGainLearner(
        final int minSplitSize,
        final int[] dimensionsToConsider)
    {
        super();
        
        this.setMinSplitSize(minSplitSize);
        this.setDimensionsToConsider(dimensionsToConsider);
    }
    
    @Override
    public AbstractVectorThresholdMaximumGainLearner<OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        final AbstractVectorThresholdMaximumGainLearner<OutputType> result = (AbstractVectorThresholdMaximumGainLearner<OutputType>)
            super.clone();
        result.dimensionsToConsider = ArrayUtil.copy(this.dimensionsToConsider);
        return result;
    }

    @Override
    public VectorElementThresholdCategorizer learn(
        final Collection<? extends InputOutputPair<? extends Vectorizable, OutputType>> data)
    {
        final int totalCount = CollectionUtil.size(data);
        if (totalCount <= 1)
        {
            // Nothing to learn.
            return null;
        }

        // Compute the base count values for the node.
        final DefaultDataDistribution<OutputType> baseCounts =
            CategorizationTreeLearner.getOutputCounts(data);

        // Pre-allocate a workspace of data for computing the gain.
        final ArrayList<DefaultWeightedValue<OutputType>> workspace =
            new ArrayList<DefaultWeightedValue<OutputType>>(totalCount);
        for (int i = 0; i < totalCount; i++)
        {
            workspace.add(new DefaultWeightedValue<OutputType>());
        }

        // Figure out the dimensionality of the data.
        final int dimensionality = DatasetUtil.getInputDimensionality(data);

        // Go through all the dimensions to find the one with the best gain
        // and the best threshold.
        double bestGain = -1.0;
        int bestIndex = -1;
        double bestThreshold = 0.0;

        final int dimensionsCount = this.dimensionsToConsider == null ?
            dimensionality : this.dimensionsToConsider.length;
        for (int i = 0; i < dimensionsCount; i++)
        {
            final int index = this.dimensionsToConsider == null ?
                i : this.dimensionsToConsider[i];

            // Compute the best gain-threshold pair for the given dimension of
            // the data.
            final DefaultPair<Double, Double> gainThresholdPair =
                this.computeBestGainAndThreshold(data, index, baseCounts);

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
                bestIndex = index;
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
        final Collection<? extends InputOutputPair<? extends Vectorizable, OutputType>> data,
        final int dimension,
        final DefaultDataDistribution<OutputType> baseCounts)
    {
        final int totalCount = data.size();
     
        final ArrayList<DefaultWeightedValue<OutputType>> workspace =
            new ArrayList<DefaultWeightedValue<OutputType>>(totalCount);
        for (int i = 0; i < totalCount; i++)
        {
            workspace.add(new DefaultWeightedValue<OutputType>());
        }
        return this.computeBestGainAndThreshold(data, dimension, baseCounts,
            workspace);
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
     * @param   values
     *      A workspace to store the values of the data in. Recycled to avoid
     *      recreating a large array each time.
     * @return
     *      A pair containing the best gain computed and its associated
     *      threshold. If there is no good split point, null is returned. This
     *      can happen if there is no data or every value is the same.
     */
    protected DefaultPair<Double, Double> computeBestGainAndThreshold(
        final Collection<? extends InputOutputPair<? extends Vectorizable, OutputType>> data,
        final int dimension,
        final DefaultDataDistribution<OutputType> baseCounts,
        final ArrayList<DefaultWeightedValue<OutputType>> values)
    {
        // We can only compute thresholds if we can put the minimum split
        // size on each side.
        final int totalCount = data.size();
        if (totalCount < 2 * this.minSplitSize)
        {
            return null;
        }

        // To compute the gain we will sort all of the values along the given
        // dimension and then walk along the values to determine the best
        // threshold.

        // The first step is to create a list of (value, output) pairs for the
        // given dimension. We do this by using the given workspace.
        int index = 0;
        for (InputOutputPair<? extends Vectorizable, OutputType> example
            : data)
        {
            // Add this example to the list.
            final Vector input = example.getInput().convertToVector();
            final OutputType output = example.getOutput();
            final double value = input.getElement(dimension);
            DefaultWeightedValue<OutputType> entry = values.get(index);
            entry.setWeight(value);
            entry.setValue(output);
            index++;
        }

        // Sort the list in ascending order by value.
        Collections.sort(values,
            DefaultWeightedValue.WeightComparator.getInstance());

        // Get the smallest and largest values.
        final double smallestValue = values.get(0).getWeight();
        final double largestValue = values.get(totalCount - 1).getWeight();

        // If all the values on this dimension are the same then there is
        // nothing to split on.
        if (smallestValue >= largestValue)
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
        final DefaultDataDistribution<OutputType> positiveCounts =
            baseCounts.clone();
        final DefaultDataDistribution<OutputType> negativeCounts =
            new DefaultDataDistribution<OutputType>(baseCounts.getDomain().size());

        // We are going to loop over all the values to compute the best
        // gain and the best threshold.
        double bestGain = Double.NEGATIVE_INFINITY;
        double bestTieBreaker = Double.NEGATIVE_INFINITY;
        double bestThreshold = Double.NEGATIVE_INFINITY;
        boolean validSplit = false;

        // We need to keep track of the previous value for two reasons:
        //    1) To determine if we've already tested the value, since we loop
        //       over a >= threshold.
        //    2) So that the threshold can be computed to be half way between
        //       two values.
	    //
        // We advance i through values and stop whenever value[i] != value[i-1].
        // These are all the points where it is meaningful to evaluate a split.
        // All values to the left of i go into the negative count bucket.
        final int maxIndex = totalCount - this.minSplitSize;
        double previousValue = 0.0;
        for (int i = 0; i <= maxIndex; i++)
        {
            final DefaultWeightedValue<OutputType> valueLabel = values.get(i);
            final OutputType label = valueLabel.getValue();
            final double value = valueLabel.getWeight();

            // Check if it is worth evaluating a threshold between
            // previous and next value.
            if (i < this.minSplitSize)
            {
                // We are going to loop over a threshold value that is >=
                // to handle equivalent values properly. However, we also need
                // to ignore the first minSplitSize values.
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
                        positiveCounts.getTotal() / totalCount;
                    final double proportionNegative =
                        negativeCounts.getTotal() / totalCount;

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
                        double threshold = (value + previousValue) / 2.0;

                        // If a round-off error drops the threshold back to
                        // the previous value, set it equal to the current
                        // value since we use a >= threshold.
                        if (threshold <= previousValue)
                        {
                            threshold = value;
                        }

                        bestGain = gain;
                        bestTieBreaker = tieBreaker;
                        bestThreshold = threshold;
                        validSplit = true;
                    }
                }
            }
            
            // Store this value as the previous value.
            previousValue = value;
            
            // Update the sides of the split.
            positiveCounts.decrement(label);
            negativeCounts.increment(label);
        }

        // Sanity check to make sure we found a threshold that
        // partitions the values.
        if (   bestThreshold <= smallestValue
            || bestThreshold >  largestValue)
        {
            throw new RuntimeException(
                "bestThreshold (" + bestThreshold + ") lies outside range of "
                + "values (" + smallestValue + ", " + largestValue + "]");
        }

        if (!validSplit)
        {
            return null;
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
        final DefaultDataDistribution<OutputType> baseCounts,
        final DefaultDataDistribution<OutputType> positiveCounts,
        final DefaultDataDistribution<OutputType> negativeCounts);

    @Override
    public int[] getDimensionsToConsider()
    {
        return this.dimensionsToConsider;
    }

    @Override
    public void setDimensionsToConsider(
        final int... dimensionsToConsider)
    {
        this.dimensionsToConsider = dimensionsToConsider;
    }

    /**
     * Gets the minimum split size. This is the minimum number of examples
     * that can fall on either side of the split for it to be valid. If there
     * is not at least twice this number of examples in the input data, then
     * no split is returned.
     * 
     * @return 
     *      The minimum split size. Must be positive.
     */
    public int getMinSplitSize()
    {
        return this.minSplitSize;
    }
    
    /**
     * Sets the minimum split size. This is the minimum number of examples
     * that can fall on either side of the split for it to be valid. If there
     * is not at least twice this number of examples in the input data, then
     * no split is returned.
     * 
     * @param   minSplitSize
     *      The minimum split size. Must be positive.
     */
    public void setMinSplitSize(
        final int minSplitSize)
    {
        ArgumentChecker.assertIsPositive("minSplitSize", minSplitSize);
        this.minSplitSize = minSplitSize;
    }

}
