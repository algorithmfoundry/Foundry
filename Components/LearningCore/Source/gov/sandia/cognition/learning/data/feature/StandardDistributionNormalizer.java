/*
 * File:                StandardDistributionNormalizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.collection.NumberComparator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The {@code StandardDistributionNormalizer} class implements a normalization
 * method where a real value is converted onto a standard distribution. This
 * means that the value is subtracted by the mean and divided by the standard
 * deviation.
 * <BR><BR>
 *    f(x) = (x - mean) / (standardDeviation)
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-06",
    changesNeeded=false,
    comments={
        "Made the learning methods take <? extends Number>",
        "Now extends AbstractUnivariateScalarFunction",
        "Cleaned up javadoc"
    }
)
public class StandardDistributionNormalizer
    extends AbstractUnivariateScalarFunction
{

    /** The default mean is {@value}. */
    public static final double DEFAULT_MEAN = 0.0;

    /** The default variance is {@value}. This means that by default there is no
     *  normalization by variance. */
    public static final double DEFAULT_VARIANCE = 1.0;

    /** The mean of normalization. */
    protected double mean;

    /** The variance for normalization. */
    protected double variance;

    /** The cached value of the standard deviation for normalization. */
    protected double standardDeviation;

    /**
     * Creates a new instance of StandardNormalization with a mean of 0.0 and
     * a variance of 1.0.
     */
    public StandardDistributionNormalizer()
    {
        this(DEFAULT_MEAN, DEFAULT_VARIANCE);
    }

    /**
     * Creates a new instance of StandardDistributionNormalizer with the given
     * mean and variance.
     *
     * @param  mean The mean.
     * @param  variance The variance.
     */
    public StandardDistributionNormalizer(
        final double mean,
        final double variance)
    {
        super();

        this.setMean(mean);
        this.setVariance(variance);
    }

    /**
     * Creates a new instance of StandardDistributionNormalizer from the given
     * Gaussian.
     *
     * @param  gaussian The Gaussian to initialize the normalizer with.
     */
    public StandardDistributionNormalizer(
        final UnivariateGaussian gaussian)
    {
        this(gaussian.getMean(), gaussian.getVariance());
    }

    /**
     * Creates a new copy of a StandardDistributionNormalizer.
     *
     * @param  other The StandardDistributionNormalizer to copy.
     */
    public StandardDistributionNormalizer(
        final StandardDistributionNormalizer other)
    {
        this(other.getMean(), other.getVariance());
    }

    /**
     * Creates a new copy of this StandardDistributionNormalizer.
     *
     * @return A new copy of this StandardDistributionNormalizer.
     */
    @Override
    public StandardDistributionNormalizer clone()
    {
        return (StandardDistributionNormalizer) super.clone();
    }

    /**
     * Normalizes the given double value by subtracting the mean and dividing
     * by the standard deviation (the square root of the variance).
     *
     * @param  value The value to normalize.
     * @return The normalized value.
     */
    public double evaluate(
        final double value)
    {
        return (value - this.mean) / this.standardDeviation;
    }

    /**
     * Gets the mean.
     *
     * @return The mean.
     */
    public double getMean()
    {
        return this.mean;
    }

    /**
     * Sets the mean.
     *
     * @param  mean The mean.
     */
    public void setMean(
        final double mean)
    {
        this.mean = mean;
    }

    /**
     * Gets the variance.
     *
     * @return The variance.
     */
    public double getVariance()
    {
        return variance;
    }

    /**
     * Sets the variance. It must be greater than 0.0.
     *
     * @param  variance The variance.
     */
    public void setVariance(
        double variance)
    {
        if (variance <= 0.0)
        {
            throw new IllegalArgumentException("variance must be positive");
        }

        this.variance = variance;
        this.standardDeviation = Math.sqrt(variance);
    }

    /**
     * Builds a StandardDistributionNormalizer by computing the mean and
     * variance of the given collection of values.
     *
     * @param  values The values to use to build the normalizer.
     * @return The StandardDistributionNormalizer created from the mean and
     *         variance of the given values.
     */
    public static StandardDistributionNormalizer learn(
        final Collection<? extends Number> values)
    {
        return learn(values, 0.0);
    }

    /** 
     * Builds a StandardDistributionNormalizer by computing the mean and
     * variance of the given collection of values. It will exclude the given
     * percentage of outliers from the value.
     *
     * @param  values The values to use to build the normalizer.
     * @param  outlierPercent The percentage of outliers to exclude.
     * @return The StandardDistributionNormalizer created from the mean and
     *         variance of the given values.
     */
    public static StandardDistributionNormalizer learn(
        final Collection<? extends Number> values,
        double outlierPercent)
    {
        if (values == null)
        {
            // Error: Bad values.
            throw new NullPointerException("values cannot be null.");
        }
        else if (outlierPercent < 0.0 || outlierPercent >= 1.0)
        {
            // Error: Bad outlier percent.
            throw new IllegalArgumentException(
                "outlierPercent must be [0.0, 1.0)");
        }

        int count = values.size();
        if (count <= 0)
        {
            // Error: Not enough samples.
            throw new IllegalArgumentException("values cannot be empty.");
        }

        // Figure out the collection to compute the mean and variance on.
        Collection<? extends Number> included = values;
        if (outlierPercent > 0.0)
        {
            // Discard the given percentage of outliers by removing half that
            // percentage from each side.
            final ArrayList<Number> sorted = new ArrayList<Number>(values);
            Collections.sort(sorted, NumberComparator.INSTANCE );
            int numToDiscard = (int) (count * outlierPercent / 2.0);
            if (numToDiscard > 0 && (2 * numToDiscard) < count)
            {
                included = sorted.subList(numToDiscard, count - numToDiscard);
            }
        }

        // Get the new count of values to compute.
        count = included.size();

        // Compute the mean.
        Pair<Double,Double> result =
            UnivariateStatisticsUtil.computeMeanAndVariance(included);
        double mean = result.getFirst();
        double variance = ((count-1.0)/(count))*result.getSecond();
        if( variance <= 0.0 )
        {
            variance = 1.0;
        }
        return new StandardDistributionNormalizer(mean, variance);
    }

    /**
     * The {@code Learner} class implements a {@code BatchLearner} object for
     * a {@code StandardDistributionNormalizer}.
     */
    public static class Learner
        extends AbstractCloneableSerializable
        implements BatchLearner<Collection<Double>, StandardDistributionNormalizer>
    {

        /** The default percentage of outliers is {@value}. */
        public static final double DEFAULT_OUTLIER_PERCENT = 0.0;

        /** The percentage of outliers to exclude from learning. */
        protected double outlierPercent;

        /**
         * Creates a new StandardDistributionNormalizer.Learner.
         */
        public Learner()
        {
            this(DEFAULT_OUTLIER_PERCENT);
        }

        /**
         * Creates a new StandardDistributionNormalizer.Learner.
         *
         * @param  outlierPercent The percentage of outliers to exclude.
         */
        public Learner(
            final double outlierPercent)
        {
            super();

            this.setOutlierPercent(outlierPercent);
        }

        /**
         * Learns a StandardDistributionNormalizer from the given values by
         * computing the mean and standard deviation of the values.
         *
         * @param  values The values to use.
         * @return The StandardDistributionNormalizer computed from the given
         *         values.
         */
        public StandardDistributionNormalizer learn(
            final Collection<Double> values)
        {
            return StandardDistributionNormalizer.learn(
                values, this.outlierPercent);
        }

        /** 
         * Sets the percentage of outliers to exclude from learning. 
         *
         * @return The percentage of outliers.
         */
        public double getOutlierPercent()
        {
            return outlierPercent;
        }

        /**
         * Sets the percentage of outliers to exclude from learning. Must be
         * between 0.0 and 1.0.
         *
         * @param  outlierPercent The percentage of outliers.
         */
        public void setOutlierPercent(
            final double outlierPercent)
        {
            if (outlierPercent < 0.0 || outlierPercent >= 1.0)
            {
                throw new IllegalArgumentException(
                    "outlierPercent must be [0.0, 1.0)");
            }

            this.outlierPercent = outlierPercent;
        }

    }

}
