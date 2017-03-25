/*
 * File:            DefaultSummaryStatistics.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * A default implementation of the {@link SummaryStatistics} class. Just stores
 * the various values for the summary statistics.
 * 
 * @author  Justin Basilico
 * @since   4.0.0
 */
public class DefaultSummaryStatistics
    extends AbstractCloneableSerializable
    implements SummaryStatistics
{
    
    /** The mean of the values. This is the first statistical moment. */
    protected double mean;

    /** The variance of the values. This is the unbiased version of the second
     * statistical moment, and also the square of the standard deviation. */
    protected double variance;

    /** The standard deviation of the values. It is the square root of the
     *  variance. */
    protected double standardDeviation;

    /** The minimum of the values. */
    protected double min;

    /** The maximum of the values. */
    protected double max;

    /**
     * Creates a new, empty {@link SummaryStatistics}.
     */
    public DefaultSummaryStatistics()
    {
        this(0.0, 0.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    /**
     * Creates a new {@link SummaryStatistics} from the given values.
     *
     * @param   mean
     *      The mean of the values.
     * @param   variance
     *      The unbiased variance of the values.
     * @param   min
     *      The minimum value.
     * @param   max
     *      The maximum value.
     */
    public DefaultSummaryStatistics(
        final double mean,
        final double variance,
        final double min,
        final double max)
    {
        super();

        this.setMean(mean);
        this.setVariance(variance);
        this.setMin(min);
        this.setMax(max);
    }

    @Override
    public DefaultSummaryStatistics clone()
    {
        return (DefaultSummaryStatistics) super.clone();
    }

    @Override
    public double getMean()
    {
        return this.mean;
    }

    /**
     * Sets the mean of the values.
     *
     * @param   mean
     *      The mean.
     */
    public void setMean(
        final double mean)
    {
        this.mean = mean;
    }

    @Override
    public double getVariance()
    {
        return this.variance;
    }

    /**
     * Sets the unbiased variance of the values seen so far. It is the
     * unbiased version of the second moment and the square of the
     * standard deviation. Also adjusts the standard deviation,
     * which is the square root of the variance.
     *
     * @param   variance
     *      The variance. Cannot be negative.
     */
    public void setVariance(
        final double variance)
    {
        ArgumentChecker.assertIsNonNegative("variance", variance);
        this.variance = variance;
        this.standardDeviation = Math.sqrt(variance);
    }

    @Override
    public double getStandardDeviation()
    {
        return this.standardDeviation;
    }

    /**
     * Sets the standard deviation. Also adjusts the variance based on it,
     * which is the standard deviation squared.
     *
     * @param   standardDeviation
     *      The standard deviation. Cannot be negative.
     */
    public void setStandardDeviation(
        final double standardDeviation)
    {
        ArgumentChecker.assertIsNonNegative("standardDeviation", standardDeviation);
        this.standardDeviation = standardDeviation;
        this.variance = standardDeviation * standardDeviation;
    }

    @Override
    public double getMin()
    {
        return this.min;
    }

    /**
     * Sets the minimum value.
     *
     * @param   min
     *      The minimum value.
     */
    public void setMin(
        final double min)
    {
        this.min = min;
    }

    @Override
    public double getMax()
    {
        return this.max;
    }

    /**
     * Sets the maximum value.
     *
     * @param   max
     *      The maximum value.
     */
    public void setMax(
        final double max)
    {
        this.max = max;
    }
}
