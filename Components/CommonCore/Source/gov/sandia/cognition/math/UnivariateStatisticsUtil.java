/*
 * File:                UnivariateStatisticsUtil.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.NumberComparator;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Some static methods for computing generally useful univariate statistics.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class UnivariateStatisticsUtil
{

    /**
     * Computes the arithmetic mean (average, expectation, first central moment)
     * of a dataset
     * @param data
     * Collection of Doubles to consider
     * @return
     * Arithmetic mean of the given dataset
     */
    @PublicationReference(
        title="Algorithms for calculating variance",
        type=PublicationType.WebPage,
        year=2010,
        author="Wikipedia",
        url="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance")
    public static double computeMean(
        Iterable<? extends Number> data)
    {
        // Note: This is more compilcated than a straight-forward algorithm
        // that just computes the sum and sum-of-squares to get around
        // numerical precision issues.
        int n = 0;
        double mean = 0.0;
        for (Number v : data)
        {
            final double x = v.doubleValue();
            final double delta = x - mean;
            n += 1;
            mean += delta / n;
        }
        return mean;
    }

    /**
     * Computes the arithmetic mean (average, expectation, first central moment)
     * of a dataset. The absolute value of the weight is used to handle negative
     * weights.
     * 
     * @param   data
     *      Collection of Doubles to consider.
     * @return
     *      Arithmetic mean of the given dataset.
     */
    @PublicationReference(
        title="Algorithms for calculating variance",
        type=PublicationType.WebPage,
        year=2010,
        author="Wikipedia",
        url="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance")
    public static double computeWeightedMean(
        Iterable<? extends WeightedValue<? extends Number>> data )
    {

        // Note: This is more compilcated than a straight-forward algorithm
        // that just computes the sum and sum-of-squares to get around
        // numerical precision issues.
        double mean = 0.0;
        double weightSum = 0.0;
        for ( WeightedValue<? extends Number> v : data)
        {
            final double x = v.getValue().doubleValue();
            double weight = v.getWeight();

            if (weight != 0.0)
            {
                if (weight < 0.0)
                {
                    // Use the absolute value of weights.
                    weight = -weight;
                }

                final double newWeightSum = weightSum + weight;
                final double delta = x - mean;

                final double update = delta * weight / newWeightSum;
                mean += update;
                weightSum = newWeightSum;
            }
        }

        return mean;
    }

    /**
     * Computes the unbiased variance (second central moment,
     * squared standard deviation) of a dataset.  Computes the mean first,
     * then computes the variance.  If you already have the mean, then use the
     * two-argument computeVariance(data,mean) method to save duplication of
     * effort.
     * @param data
     * Data to consider
     * @return
     * Unbiased variance of the given dataset
     */
    static public double computeVariance(
        Collection<? extends Number> data )
    {
        return computeMeanAndVariance(data).getSecond();
    }

    /**
     * Computes the unbiased variance (second central moment,
     * squared standard deviation) of a dataset
     * @param data
     * Data to consider
     * @param mean
     * Pre-computed mean (or central value) of the dataset
     * @return
     * Unbiased variance of the given dataset
     */
    static public double computeVariance(
        Collection<? extends Number> data,
        double mean )
    {

        int num = data.size();
        if( num < 2 )
        {
            return 0.0;
        }

        double biasedVariance = computeCentralMoment(data, mean, 2);
        double unbiasedVariance = (num/(num-1.0))*biasedVariance;
        return unbiasedVariance;
        
    }
    
    /**
     * Computes the standard deviation of a dataset, which is the square root
     * of the unbiased variance. It computes the mean first and then computes
     * the standard deviation. If you already have the mean, then use the
     * two-argument computeStandardDeviation(data, mean) to save duplication of
     * effort.
     *
     * @param   data
     *      The data to consider.
     * @return
     *      The standard deviation of the given data. Will not be negative.
     */
    public static double computeStandardDeviation(
        final Collection<? extends Number> data)
    {
        return Math.sqrt(computeVariance(data));
    }

    /**
     * Computes the standard deviation of a dataset, which is the square root
     * of the unbiased variance.
     *
     * @param   data
     *      The data to consider.
     * @param   mean
     *      The pre-computed mean of the given data.
     * @return
     *      The standard deviation of the given data. Will not be negative.
     */
    public static double computeStandardDeviation(
        final Collection<? extends Number> data,
        final double mean)
    {
        return Math.sqrt(computeVariance(data, mean));
    }

    /**
     * Computes the Root mean-squared (RMS) error between the data and its mean.
     * Computes the mean first, then computes the RMS error.  If
     * you already have the mean, then use the two-argument
     * computeRootMeanSquaredError(data,mean) method to save computation
     *
     * @return RMS error of the dataset about the mean
     * @param data Dataset to consider
     */
    static public double computeRootMeanSquaredError(
        Collection<? extends Number> data )
    {
        double mean = UnivariateStatisticsUtil.computeMean( data );
        return UnivariateStatisticsUtil.computeRootMeanSquaredError( data, mean );
    }

    /**
     * Computes the Root mean-squared (RMS) error between the data and its mean
     * @param data
     * Dataset to consider
     * @param mean
     * Mean value about which to compute the sum-squared error
     * @return
     * RMS error of the dataset about the mean
     */
    static public double computeRootMeanSquaredError(
        Collection<? extends Number> data,
        double mean )
    {

        double sse = UnivariateStatisticsUtil.computeSumSquaredDifference( data, mean );
        double rms;
        if (data.size() > 0)
        {
            rms = Math.sqrt( sse / data.size() );
        }
        else
        {
            rms = 0.0;
        }

        return rms;

    }

    /**
     * Computes the arithmetic sum of the dataset
     * @param data
     * Dataset to consider
     * @return
     * Arithmetic sum of the given dataset
     */
    static public double computeSum(
        Iterable<? extends Number> data )
    {

        double sum = 0.0;
        for (Number value : data)
        {
            sum += value.doubleValue();
        }

        return sum;

    }

    /**
     * Computes the sum-squared difference between the data and a target
     * @param data
     * Dataset to consider
     * @param target
     * Target about which to compute the difference
     * @return
     * Sum-squared difference between the dataset and the target
     */
    static public double computeSumSquaredDifference(
        Iterable<? extends Number> data,
        double target )
    {

        double sum = 0.0;
        double delta;
        for (Number value : data)
        {
            delta = value.doubleValue() - target;
            sum += delta * delta;
        }

        return sum;

    }

    /**
     * Computes the correlation coefficient in a single pass.  However, this
     * algorithm can become numerically unstable but is about twice as fast
     * as the non-single-pass method.
     * @param data1
     * First dataset to consider, must have same size as data2
     * @param data2
     * Second dataset to consider
     * @return
     * Normalized correlation coefficient, [-1,+1]
     */
    @PublicationReference(
        author="Wikipedia",
        title="Pearson product-moment correlation coefficient",
        type=PublicationType.WebPage,
        year=2011,
        url="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient"
    )
    static public double computeCorrelation(
        Collection<? extends Number> data1,
        Collection<? extends Number> data2 )
    {

        if (data1.size() != data2.size())
        {
            throw new IllegalArgumentException(
                "Datasets must be the same size!" );
        }

        int num = data1.size();
        if( num <= 0 )
        {
            return 1.0;
        }
        Iterator<? extends Number> i1 = data1.iterator();
        Iterator<? extends Number> i2 = data2.iterator();
        double sum1 = 0.0;
        double sum2 = 0.0;
        double sum11 = 0.0;
        double sum22 = 0.0;
        double sum12 = 0.0;

        for( int n = 0; n < num; n++ )
        {
            final double value1 = i1.next().doubleValue();
            final double value2 = i2.next().doubleValue();

            sum1 += value1;
            sum2 += value2;

            sum11 += value1*value1;
            sum22 += value2*value2;
            sum12 += value1*value2;
        }

        double top = num * (sum12 - ((sum1/num)*sum2));
        double b1 = Math.sqrt( num * (sum11 - (sum1/num)*sum1) );
        double b2 = Math.sqrt( num * (sum22 - (sum2/num)*sum2) );
        double bottom = b1*b2;

        double retval;
        if( bottom <= 0.0 )
        {
            retval = 1.0;
        }
        else
        {
            retval = top/bottom;
        }

        return retval;

    }

    /**
     * Computes the median of the given data.
     * @param data
     * Data from which to compute the median.
     * @return
     * Median of the sample.
     */
    public static double computeMedian(
        Collection<? extends Number> data )
    {
        return computePercentile(data, 0.5);
    }

    /**
     * Computes the percentile value of the given data.  For example, if
     * data has 101 values and "percentile" is 0.27, then the return value
     * would be the ascending-sorted value in the 26th zero-based index.
     * @param data
     * Data from which to compute the percentile.
     * @param percentile
     * Percentile to choose, must be on the closed interval 0.0 to 1.0.
     * @return
     * Requested percentile from the data.
     */
    public static double computePercentile(
        final Collection<? extends Number> data,
        final double percentile)
    {
        final ArrayList<Number> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData, NumberComparator.INSTANCE);
        return getPercentileFromSorted(sortedData, percentile);
    }
    
    /**
     * Computes the percentile value of the given pre-sorted data in increasing
     * order.  For example, if data has 101 values and "percentile" is 0.27, 
     * then the return value would be the ascending-sorted value in the 26th 
     * zero-based index.
     * 
     * @param sortedData 
     *      The data sorted in increasing order from which to get the 
     *      percentile. Must have at least one element.
     * @param percentile 
     *      The percentile to choose. Must be between 0 and 1, inclusive.
     * @return 
     *      The requested percentile from the data.
     */
    public static double getPercentileFromSorted(
        final List<? extends Number> sortedData,
        final double percentile)
    {
        ProbabilityUtil.assertIsProbability(percentile);
        
        int num = sortedData.size();
        double numPct = (num-1) * percentile;
        double remainder = numPct % 1.0;

        int lowerIndex = (int) Math.floor(numPct);
        double lower = sortedData.get(lowerIndex).doubleValue();
        if( remainder == 0.0 )
        {
            return lower;
        }

        int upperIndex = lowerIndex + 1;
        double upper = sortedData.get(upperIndex).doubleValue();

        return lower * (1.0-remainder) + upper*remainder;
    }
    
    /**
     * Computes the given percentiles of the given data. For example, if data 
     * has 101 values and "percentile" is 0.27, then the return value would be 
     * the ascending-sorted value in the 26th zero-based index.
     *
     * @param data 
     *      The data sorted from which to compute the percentiles. 
     *      Must have at least one element.
     * @param percentiles
     *      The percentiles to compute. All must be between 0 and 1, inclusive.
     * @return 
     *      The an array of equal length to the given percentiles that contains
     *      the requested percentiles from the data.
     */
    public static double[] computePercentiles(
        final Collection<? extends Number> data,
        final double... percentiles)
    {
        ArgumentChecker.assertIsNotNull("percentiles", percentiles);
        
        // Sort the data once.
        final ArrayList<Number> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData, NumberComparator.INSTANCE);
        
        // Now get all the percentiles.
        final int percentileCount = percentiles.length;
        final double[] result = new double[percentileCount];
        for (int i = 0; i < percentileCount; i++)
        {
            result[i] = getPercentileFromSorted(sortedData, percentiles[i]);
        }
        
        return result;
    }

    /**
     * Finds the minimum value of a data set.
     * @param data
     * Data set to consider
     * @return
     * Minimum value of the data set.
     */
    public static double computeMinimum(
        Iterable<? extends Number> data )
    {
        double minimum = Double.POSITIVE_INFINITY;
        for( Number value : data )
        {
            double x = value.doubleValue();
            if( minimum > x )
            {
                minimum = x;
            }
        }

        return minimum;
    }

    /**
     * Finds the maximum value of a data set.
     * @param data
     * Data set to consider
     * @return
     * Maximum value of the data set.
     */
    public static double computeMaximum(
        Iterable<? extends Number> data )
    {
        double maximum = Double.NEGATIVE_INFINITY;
        for( Number value : data )
        {
            double x = value.doubleValue();
            if( maximum < x )
            {
                maximum = x;
            }
        }

        return maximum;

    }

    /**
     * Computes the minimum and maximum of a set of data in a single pass.
     * @param data
     * Data to consider
     * @return
     * Minimum and Maximum
     */
    public static Pair<Double,Double> computeMinAndMax(
        Iterable<? extends Number> data )
    {

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for( Number value : data )
        {
            double x = value.doubleValue();
            if( min > x )
            {
                min = x;
            }
            if( max < x )
            {
                max = x;
            }
        }
        return DefaultPair.create( min, max );
    }

    /**
     * Computes the unbiased skewness of the dataset.
     * @param data
     * Data from which to compute the unbiased skewness.
     * @return
     * Unbiased skewness.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Skewness",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Skewness"
    )
    public static double computeSkewness(
        Collection<? extends Number> data )
    {

        int num = data.size();
        if( num < 3 )
        {
            return 0.0;
        }

        Pair<Double,Double> pair = computeMeanAndVariance(data);
        double mean = pair.getFirst();
        double stddev = Math.sqrt(pair.getSecond());
        double moment3 = computeCentralMoment(data, mean, 3);
        double biasedSkewness = moment3 / Math.pow(stddev, 3.0);
        return biasedSkewness;
        
    }

    /**
     * Computes the desired biased estimate central moment of the given dataset.
     * @param data
     * Data to compute the moment of.
     * @param mean
     * Mean of the data (to prevent redundant computation).
     * @param moment
     * Desired moment of the data, must be greater than or equal to 1.
     * @return
     * Biased estimate of the desired central moment.
     */
    public static double computeCentralMoment(
        Iterable<? extends Number> data,
        double mean,
        int moment )
    {

        if( moment < 1 )
        {
            throw new IllegalArgumentException( "Moment must be >= 1" );
        }

        int num = 0;
        double sum = 0.0;
        for( Number value : data )
        {
            double delta = value.doubleValue() - mean;
            sum += Math.pow(delta, moment);
            num++;
        }

        if( num < 1 )
        {
            return 0.0;
        }
        else
        {
            return sum / num;
        }

    }

    /**
     * Computes the desired biased estimate central moment of the given dataset.
     * The absolute value of the weight is used to handle negative weights.
     *
     * @param data
     * Data to compute the moment of.
     * @param mean
     * Mean of the data (to prevent redundant computation).
     * @param moment
     * Desired moment of the data, must be greater than or equal to 1.
     * @return
     * Biased estimate of the desired central moment.
     */
    public static double computeWeightedCentralMoment(
        Iterable<? extends WeightedValue<? extends Number>> data,
        double mean,
        int moment )
    {

        if( moment < 1 )
        {
            throw new IllegalArgumentException( "Moment must be >= 1" );
        }

        int num = 0;
        double sum = 0.0;
        double weightSum = 0.0;
        for( WeightedValue<? extends Number> value : data )
        {
            final double weight = Math.abs(value.getWeight());
            if( weight != 0.0 )
            {
                double delta = value.getValue().doubleValue() - mean;
                sum += weight*Math.pow(delta, moment);
                weightSum += weight;
            }
            num++;
        }

        if( num < 1 )
        {
            return 0.0;
        }
        else
        {
            return sum / weightSum;
        }

    }

    /**
     * Computes the biased excess kurtosis of the given dataset.  Intuitively,
     * kurtosis quantifies the pointiness of the data by normalizing the fourth
     * central moment.
     * @param data
     * Dataset to compute its kurtosis.
     * @return
     * Biased excess kurtosis of the given dataset.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Kurtosis",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Kurtosis"
    )
    public static double computeKurtosis(
        Collection<? extends Number> data )
    {

        if( data.size() < 2 )
        {
            return 0.0;
        }

        Pair<Double,Double> pair = computeMeanAndVariance(data);
        double mean = pair.getFirst();
        double variance = pair.getSecond();
        double moment4 = computeCentralMoment(data, mean, 4);
        double biasedKurtosis = moment4 / (variance*variance) - 3.0;
        return biasedKurtosis;

    }

    /**
     * Computes the biased excess kurtosis of the given dataset.  Intuitively,
     * kurtosis quantifies the pointiness of the data by normalizing the fourth
     * central moment. The absolute value of the weight is used to handle
     * negative weights.
     *
     * @param data
     * Dataset to compute its kurtosis.
     * @return
     * Biased excess kurtosis of the given dataset.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Kurtosis",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Kurtosis"
    )
    public static double computeWeightedKurtosis(
        Collection<? extends WeightedValue<? extends Number>> data )
    {

        if( data.size() < 2 )
        {
            return 0.0;
        }

        Pair<Double,Double> pair = computeWeightedMeanAndVariance(data);
        double mean = pair.getFirst();
        double variance = pair.getSecond();
        double moment4 = computeWeightedCentralMoment(data, mean, 4);
        double biasedKurtosis = moment4 / (variance*variance) - 3.0;
        return biasedKurtosis;

    }


    /**
     * Computes the information-theoretic entropy of the PMF in bits (base 2).
     * @param data
     * Data to compute the entropy.
     * @return
     * Entropy in bits of the given PMF.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Entropy (information theory)",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Entropy_(Information_theory)"
    )
    public static double computeEntropy(
        Iterable<? extends Number> data )
    {

        // Compute the entropy by looping over the values in the map.s
        double entropy = 0.0;
        for( Number value : data )
        {
            double p = value.doubleValue();
            if( p != 0.0 )
            {
                entropy -= p * MathUtil.log2( p );
            }
        }

        // Return the computed entropy.
        return entropy;

    }

    /**
     * Computes the mean and unbiased variance of a Collection of data using
     * the one-pass approach.
     * @param data
     * Data to consider
     * @return
     * Mean and unbiased Variance Pair.
     */
    @PublicationReference(
        title="Algorithms for calculating variance",
        type=PublicationType.WebPage,
        year=2010,
        author="Wikipedia",
        url="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance")
    public static Pair<Double,Double> computeMeanAndVariance(
        Iterable<? extends Number> data)
    {
        // Note: This is more compilcated than a straight-forward algorithm
        // that just computes the sum and sum-of-squares to get around
        // numerical precision issues.
        int n = 0;
        double mean = 0.0;
        double m2 = 0.0;
        for (Number v : data)
        {
            final double x = v.doubleValue();

            final double delta = x - mean;
            n += 1;
            mean += delta / n;
            m2 += delta * (x - mean);
        }

        double variance;
        if (n >= 2)
        {
            variance = m2 / (n - 1);
        }
        else
        {
            variance = 0.0;
        }
 
        return DefaultPair.create(mean, variance);
    }

    /**
     * Computes the mean and unbiased variance of a Collection of data using
     * the one-pass approach. The absolute value is used to handle negative
     * weights.
     *
     * @param   data
     *      Data to consider.
     * @return
     *      Mean and unbiased Variance Pair.
     */
    @PublicationReference(
        title="Algorithms for calculating variance",
        type=PublicationType.WebPage,
        year=2010,
        author="Wikipedia",
        url="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance")
    public static Pair<Double,Double> computeWeightedMeanAndVariance(
        Iterable<? extends WeightedValue<? extends Number>> data )
    {
        
        // Note: This is more compilcated than a straight-forward algorithm
        // that just computes the sum and sum-of-squares to get around
        // numerical precision issues.
        double mean = 0.0;
        double weightSum = 0.0;
        double m2 = 0.0;

        for ( WeightedValue<? extends Number> v : data)
        {
            final double x = v.getValue().doubleValue();
            double weight = v.getWeight();

            if (weight != 0.0)
            {
                if (weight < 0.0)
                {
                    // Use the absolute value of weights.
                    weight = -weight;
                }

                final double newWeightSum = weightSum + weight;
                final double delta = x - mean;

                final double update = delta * weight / newWeightSum;
                m2 += weightSum * delta * update;
                mean += update;
                weightSum = newWeightSum;
            }
        }

        double variance;
        if (weightSum > 0.0)
        {
            variance = m2 / weightSum;
        }
        else
        {
            variance = 0.0;
        }
        
        return DefaultPair.create(mean, variance);
    }

}
