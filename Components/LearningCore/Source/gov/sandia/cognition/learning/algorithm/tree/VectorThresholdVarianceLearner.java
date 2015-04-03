/*
 * File:                VectorThresholdVarianceLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.DefaultWeightedValue.WeightComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The {@code VectorThresholdVarianceLearner} computes the best threshold over
 * a dataset of vectors using the reduction in variance to determine the
 * optimal index and threshold. This is an implementation of what is used in
 * the CART regression tree algorithm.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorThresholdVarianceLearner
    extends AbstractCloneableSerializable
    implements VectorThresholdLearner<Double>
{
    
    /** The default value for the minimum split size is {@value}. */
    public static final int DEFAULT_MIN_SPLIT_SIZE = 1;
    
    /** The threshold for allowing a split to be made, determined by how many
     *  instances fall in each left or right sides of the split. Both sides
     *  must have at least this number of instances. Must be positive. */
    protected int minSplitSize;

    /** The array of 0-based dimensions to consider in the input. Null means
     *  all dimensions are considered. */
    protected int[] dimensionsToConsider;

    /**
     * Creates a new {@code VectorThresholdVarianceLearner}.
     */
    public VectorThresholdVarianceLearner()
    {
    	this(DEFAULT_MIN_SPLIT_SIZE, null);
    }

    /**
     * Creates a new {@code VectorThresholdVarianceLearner}
     * 
     * @param   minSplitSize
     *      The minimum split size. Must be positive.
     */
    public VectorThresholdVarianceLearner(
        final int minSplitSize)
    {
        this(minSplitSize, null);
    }

    /**
     * Creates a new {@code VectorThresholdVarianceLearner}.
     *
     * @param   minLeafSize
     *      The minimum split size. Must be positive.
     * @param   dimensionsToConsider
     *      The array of vector dimensions to consider. Null means all of them
     *      are considered.
     */
    public VectorThresholdVarianceLearner(
        final int minLeafSize, 
        final int... dimensionsToConsider)
    {
        super();

        this.setMinSplitSize(minLeafSize);
        this.setDimensionsToConsider(dimensionsToConsider);
    }

    /**
     * Learns a VectorElementThresholdCategorizer from the given data by
     * picking the vector element and threshold that best maximizes information
     * gain.
     *
     * @param  data 
     *      The data to learn from.
     * @return
     *      The learned threshold categorizer, or none if there is no good
     *      categorizer.
     */
    @Override
    public VectorElementThresholdCategorizer learn(
        final Collection<? extends InputOutputPair<? extends Vectorizable, Double>> data)
    {
        // Each split needs to have at least the minimum on each side.
        if (data == null || data.size() < 2 * this.minSplitSize)
        {
            // Nothing to learn.
            return null;
        }

        // Compute the base variance.
        final double baseVariance = DatasetUtil.computeOutputVariance(data);

        // Figure out the dimensionality of the data.
        final int dimensionality = DatasetUtil.getInputDimensionality(data);

        // Go through all the dimensions to find the one with the best gain and
        // the best threshold.
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
                this.computeBestGainThreshold(data, index, baseVariance);

            if ( gainThresholdPair == null )
            {
                // There was no gain-threshold pair that created a threshold.
                continue;
            }

            // Get the gain from the pair.
            final double gain = gainThresholdPair.getFirst();

            // Determine if this is the best gain seen.
            if ( bestIndex == -1 || gain > bestGain )
            {
                // This is the best gain, so store the gain, threshold, and
                // index.
                final double threshold = gainThresholdPair.getSecond();
                bestGain = gain;
                bestIndex = index;
                bestThreshold = threshold;
            }
        }

        if ( bestIndex < 0 )
        {
            // There was no dimension that provided any information gain for
            // the data, so no decision function can be made.
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
     * Computes the best information gain-threshold pair for the given
     * dimension on the given data. It does this by sorting the data according
     * to the dimension and then walking the sorted values to find the one that
     * has the best threshold.
     *
     *
     * @param data The data to use.
     * @param dimension The dimension to compute the best threshold over.
     * @param baseVariance The variance of the data.
     * @return
     *      The pair containing the best information gain found along this
     *      dimension and the corresponding threshold.
     */
    public DefaultPair<Double, Double> computeBestGainThreshold(
        final Collection
            <? extends InputOutputPair<? extends Vectorizable, Double>>
            data,
        final int dimension,
        final double baseVariance)
    {
        // To compute the gain we will sort all of the values along the given
        // dimension and then walk along the values to determine the best
        // threshold.

        // The first step is to create a list of (value, output) pairs. Note
        // that the value is stored as the weight in the pair and the output
        // is called the value. Unfortuate terminology but that is the easiest
        // existing data structure to use.
        final int total = data.size();
        
        // Need enough data for there to have the minimum split size on each
        // side.
        if (total < 2 * this.minSplitSize)
        {
            return null;
        }
        
        final ArrayList<DefaultWeightedValue<Double>> values = 
            new ArrayList<>(total);
        for (InputOutputPair<? extends Vectorizable, Double> example : data)
        {
            // Add this example to the list.
            final Vector input = example.getInput().convertToVector();
            final Double value = Double.valueOf(input.getElement(dimension));
            final Double output = example.getOutput();

            values.add(new DefaultWeightedValue<>(output, value));
        }

        // Sort the list in ascending order by value.
        Collections.sort(values, WeightComparator.getInstance());

        // If all the values on this dimension are the same then there is
        // nothing to split on. We've made sure that indxing is fine by 
        // checking above the minimum split size (which must be positive).
        if (values.get(0).getWeight() == values.get(total - 1).getWeight())
        {
            // All of the values are the same.
            return null;
        }

        // In order to find the best split we are going to keep track of the
        // distributions of each label on each side of the threshold. This means
        // that we maintain two univariate gaussian distribution objects.
        // To start with all of the examples are on the positive side of
        // the split, so we initialize the base counts (all the data points)
        // and the negative counts with nothing.
        final UnivariateGaussian.SufficientStatistic positiveGaussian =
            new UnivariateGaussian.SufficientStatistic();
        final UnivariateGaussian.SufficientStatistic negativeGaussian =
            new UnivariateGaussian.SufficientStatistic();
        for (DefaultWeightedValue<Double> valueLabel : values)
        {
            final double label = valueLabel.getValue();
            positiveGaussian.update(label);
        }

        // We are going to loop over all the values to compute the best gain
        // and the best threshold.
        double bestGain = 0.0;
        double bestTieBreaker = 0.0;
        double bestThreshold = 0.0;

        // We need to keep track of the previous value for two reasons:
        //    1) To determine if we've already tested the value, since we loop
        //       over a >= threshold.
        //    2) So that the threshold can be computed to be half way between
        //       two values.
        double previousValue = 0.0;
        final int maxIndex = total - this.minSplitSize;
        boolean splitFound = false;
        for (int i = 0; i <= maxIndex; i++)
        {
            final DefaultWeightedValue<Double> valueLabel = values.get(i);
            final double value = valueLabel.getWeight();
            final double label = valueLabel.getValue();

            if (i < this.minSplitSize)
            {
                // We are going to loop over a threshold value that is >=
                // to handle equivalent values properly. However, we also need
                // to ignore the first minSplitSize values.
                bestThreshold = value;
            }
            else if (value != previousValue)
            {
                // Evaluate this threshold.

                // Compute the total positive and negative at this point.
                final int numNegative = i;
                final int numPositive = total - i;

                // Compute variance of the negatives.
                final double varianceNegative =
                    negativeGaussian.getSampleVariance();

                // Compute the mean and variance of the positives.
                final double variancePositive =
                    positiveGaussian.getSampleVariance();

                // Compute the proportion of positives and negatives.
                final double proportionPositive = (double) numPositive / total;
                final double proportionNegative = (double) numNegative / total;

                // Compute the gain.
                final double gain = baseVariance
                    - proportionPositive * variancePositive
                    - proportionNegative * varianceNegative;

                if (gain >= bestGain)
                {
                    // This is our tiebreaker criteria for the case where the
                    // gains are equal. It means that we prefer ties that are
                    // more balanced in how they split (50%/50% being optimal).
                    final double tieBreaker = 1.0
                        - Math.abs(proportionPositive - proportionNegative);

                    if (gain > bestGain || tieBreaker > bestTieBreaker)
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
                        splitFound = true;
                    }
                }
            }
            // else - This threshold was equal to the previous one. Since we
            //        use a >= cutting criteria,


            // For the next loop we remove the label from the positive side
            // and add it to the negative side of the threshold.
            positiveGaussian.remove(label);
            negativeGaussian.update(label);

            // Store this value as the previous value.
            previousValue = value;
        }
        
        // No proper threshold found.
        if (!splitFound)
        {
        	return null;
        }
        
        // Return the pair containing the best gain and best threshold found.
        return new DefaultPair<>(bestGain, bestThreshold);
    }

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