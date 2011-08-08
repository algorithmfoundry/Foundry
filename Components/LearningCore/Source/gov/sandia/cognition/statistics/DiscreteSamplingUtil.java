/*
 * File:                DiscreteSamplingUtil.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.Permutation;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A utility class for sampling.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class DiscreteSamplingUtil
{

    /**
     * Samples an random index according to the given array of probabilities.
     *
     * @param   random
     *      The random number generator to use.
     * @param   probabilities
     *      The array of probabilities. Must sum to 1.0.
     * @return
     *      A random index sampled according to the given probabilities.
     */
    public static int sampleIndexFromProbabilities(
        final Random random,
        final double[] probabilities)
    {
        // Randomly pick a number between 0.0 and 1.0.
        double value = random.nextDouble();

        final int lastIndex = probabilities.length - 1;
        for (int i = 0; i < lastIndex; i++)
        {
            value -= probabilities[i];
            if (value <= 0.0)
            {
                // This index was selected.
                return i;
            }
        }

        return lastIndex;
    }

    /**
     * Samples an random index according to the given vector of probabilities.
     *
     * @param   random
     *      The random number generator to use.
     * @param   probabilities
     *      The vector of probabilities. Must sum to 1.0.
     * @return
     *      A random index sampled according to the given probabilities.
     */
    public static int sampleIndexFromProbabilities(
        final Random random,
        final Vector probabilities)
    {
        // Randomly pick a number between 0.0 and 1.0.
        double value = random.nextDouble();

        final int lastIndex = probabilities.getDimensionality() - 1;
        for (int i = 0; i < lastIndex; i++)
        {
            value -= probabilities.getElement(i);
            if (value <= 0.0)
            {
                // This index was selected.
                return i;
            }
        }

        return lastIndex;
    }

    /**
     * Samples a random index according to the given proportions. Note that
     * calling this requires calculating the sum of the proportions first, so
     * if it is known in advance, it is more efficient to call the version of
     * this method that takes the sum of the proportions as a parameter.
     *
     * @param   random
     *      The random number generator to use.
     * @param   proportions
     *      The array of proportions. All entries must be greater than or
     *      equal to zero.
     * @return
     *      A random index sampled according to the given proportions.
     */
    public static int sampleIndexFromProportions(
        final Random random,
        final double[] proportions)
    {
        // To sample from the proportions we will need the sum.
        double proportionSum = 0.0;
        for (double value : proportions)
        {
            proportionSum += value;
        }

        return sampleIndexFromProportions(random, proportions, proportionSum);
    }

    /**
     * Samples an array of indices from a given set of proportions.
     *
     * @param   random
     *      The random number generator to use.
     * @param   proportions
     *      The array of proportions. All entries must be greater than or
     *      equal to zero.
     * @param sampleSize
     *      The number of samples to make.
     * @return
     *      A an array of random indices sampled according to the given
     *      proportions.
     */
    public static int[] sampleIndicesFromProportions(
        final Random random,
        final double[] proportions,
        final int sampleSize)
    {
        // To sample from the proportions we will need the sum.
        final int length = proportions.length;
        double proportionSum = 0.0;
        if (sampleSize == 1)
        {
            // In the case of a single sample just do one sample using the
            // sum.
            for (double value : proportions)
            {
                proportionSum += value;

            }

            // Sample one index.
            final int randomIndex = sampleIndexFromProportions(
                random, proportions, proportionSum);
            return new int[] { randomIndex };
        }
        else
        {
            // With multiple samples, create the array of cumulative proportions
            // to sample quickly from it.
            final double[] cumulativeProportions = new double[length];

            for (int i = 0; i < length; i++)
            {
                proportionSum += proportions[i];
                cumulativeProportions[i] = proportionSum;
            }

            return sampleIndicesFromCumulativeProportions(
                random, cumulativeProportions, sampleSize);
        }
    }


    /**
     * Samples a random index according to the given proportions. Note that
     * sampling according cumulative proportions may be slightly faster than
     * this method.
     *
     * @param   random
     *      The random number generator to use.
     * @param   proportions
     *      An array of proportions. None of the entries can be negative. It
     *      must sum to proportionSum.
     * @param   proportionSum
     *      The sum of the given proportions array.
     * @return
     *      An index sampled at random from the given proportions array,
     *      according to those proportions.
     */
    public static int sampleIndexFromProportions(
        final Random random,
        final double[] proportions,
        final double proportionSum)
    {
        // Generate a random number between 0.0 and the proportion sum.
        double value = random.nextDouble() * proportionSum;
        final int lastIndex = proportions.length - 1;
        for (int i = 0; i < lastIndex; i++)
        {
            value -= proportions[i];
            if (value <= 0.0)
            {
                return i;
            }
        }

        return lastIndex;
    }

    /**
     * Samples a random index from an array of cumulative proportions.
     *
     * @param   random
     *      The random number generator to use.
     * @param   cumulativeProportions
     *      The array of cumulative proportions. The entries must be
     *      non-negative and monotonically increasing.
     * @return
     *      An index of the given array samples at random according to the
     *      given cumulative proportions.
     */
    public static int sampleIndexFromCumulativeProportions(
        final Random random,
        final double[] cumulativeProportions)
    {
        // The sum of the proportions is the last value in the array.
        final int lastIndex = cumulativeProportions.length - 1;
        final double sum = cumulativeProportions[lastIndex];

        // Sample a value between 0.0 and the sum of the proportions.
        final double value = random.nextDouble() * sum;

        // Do a binary search to find the index for the value.
        int index = Arrays.binarySearch(cumulativeProportions, value);
        if (index < 0)
        {
            int insertionPoint = -index - 1;
            index = insertionPoint;
        }

        return index;
    }
    /**
     * Samples a multiple indices with replacement from an array of cumulative
     * proportions.
     *
     * @param   random
     *      The random number generator to use.
     * @param   cumulativeProportions
     *      The array of cumulative proportions. The entries must be
     *      non-negative and monotonically increasing.
     * @param   sampleSize
     *      The number of samples to draw from the cumulative proportions.
     * @return
     *      An array of indices of sampled with replacement according to
     *      the given cumulative proportions.
     */
    public static int[] sampleIndicesFromCumulativeProportions(
        final Random random,
        final double[] cumulativeProportions,
        final int sampleSize)
    {
        final int[] result = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++)
        {
            result[i] = sampleIndexFromCumulativeProportions(
                random, cumulativeProportions);
        }
        return result;
    }

    /**
     * Samples a a given number of items from a list with replacement.
     *
     * @param   <DataType>
     *      The type of data in the list.
     * @param   random
     *      The random number generator.
     * @param   data
     *      The list to sample from.
     * @param   sampleSize
     *      The sample size. Must be positive.
     * @return
     *      An array list of the given size sampled with replacement from the
     *      given data.
     */
    public static <DataType> ArrayList<DataType> sampleWithReplacement(
        final Random random,
        final List<? extends DataType> data,
        final int sampleSize)
    {
        final int dataSize = data.size();
        final ArrayList<DataType> result = new ArrayList<DataType>(sampleSize);
        for (int i = 0; i < sampleSize; i++)
        {
            final int randomIndex = random.nextInt(dataSize);
            result.add(data.get(randomIndex));
        }
        return result;
    }

    /**
     * Samples a a given number of items from a list without replacement.
     *
     * @param   <DataType>
     *      The type of data in the list.
     * @param   random
     *      The random number generator.
     * @param   data
     *      The list to sample from.
     * @param   sampleSize
     *      The sample size. Must be positive and less than or equal to
     *      the data size.
     * @return
     *      A list sampled without replacement from the given data. It will
     *      be of size sampleSize.
     */
    public static <DataType> List<DataType> sampleWithoutReplacement(
        final Random random,
        final List<DataType> data,
        final int sampleSize)
    {
        final int dataSize = data.size();
        if (sampleSize <= 0)
        {
            throw new IllegalArgumentException("sampleSized must be positive");
        }
        else if (sampleSize == 1)
        {
            // Only a single sample was requested.
            final int randomIndex = random.nextInt(dataSize);
            return Collections.singletonList(data.get(randomIndex));
        }
        else if (sampleSize < dataSize)
        {
            // Create a sample using a permutation.
            return Permutation.createReordering(data, random)
                .subList(0, sampleSize);
        }
        else if (sampleSize == dataSize)
        {
            // More samples than data were requested, so just return
            // the entire list.
            return Collections.unmodifiableList(data);
        }
        else
        {
            throw new IllegalArgumentException(
                "sampleSize (" + sampleSize + ") cannot be larger than "
                + "data size (" + dataSize + ")");
        }
    }
}
