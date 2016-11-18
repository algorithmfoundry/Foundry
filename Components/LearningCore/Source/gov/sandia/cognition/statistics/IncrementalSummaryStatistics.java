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
 * Implements a class that stores basic summary statistics about a set of
 * data. It includes the mean, variance, standard deviation, minimum, and
 * maximum. These are all statistics that can be computed over a stream of
 * data in a single pass with a fixed amount of memory.
 *
 * @author  Justin Basilico
 * @since   3.4.4
 */
public class IncrementalSummaryStatistics 
    extends AbstractCloneableSerializable
    implements SummaryStatistics
// TODO: Should this implement SufficientStatistic?
{

    /** The default variance assumed is {@value}. */
    public static final double DEFAULT_VARIANCE = 0.0;

    /** The number of values seen. */
    protected long count;

    /** The minimum of the values seen. */
    protected double min;

    /** The maximum of the values seen. */
    protected double max;

// TODO: Lots of overlap with the univariate gaussian incremental estimator.
    /** The mean of the values seen. */
    protected double mean;

    /** The sum of squared differences from the mean of the values seen.
     *  An intermediate value from which the variance can be computed. */
    protected double sumSquaredDifferences;

    /**
     * Creates a new, empty {@link IncrementalSummaryStatistics}. A
     * default variance of 0.0 is used.
     */
    public IncrementalSummaryStatistics()
    {
        this(DEFAULT_VARIANCE);
    }

    /**
     * Creates a new {@link IncrementalSummaryStatistics}.
     *
     * @param   defaultVariance
     *      The default variance. Cannot be negative.
     */
    public IncrementalSummaryStatistics(
        final double defaultVariance)
    {
        super();

        ArgumentChecker.assertIsNonNegative("defaultVariance", defaultVariance);

        this.count = 0;
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        this.mean = 0.0;
        this.sumSquaredDifferences = defaultVariance;
    }

    @Override
    public IncrementalSummaryStatistics clone()
    {
        return (IncrementalSummaryStatistics) super.clone();
    }
    
    /**
     * Adds the given value to this summary statistics.
     * 
     * @param   value
     *      The value to add.
     */
    public void update(
        final double value)
    {
        this.count++;

        // Update the minimum.
        if (value < this.min)
        {
            this.min = value;
        }

        // Update the maximum.
        if (value > this.max)
        {
            this.max = value;
        }

        // Update the mean and variance.

        // Compute the difference between the value and the current mean.
        final double delta = value - this.mean;

        // Update the mean based on the difference between the value
        // and the mean along with the new count.
        this.mean += delta / this.count;

        // Update the squared differences from the mean, using the new
        // mean in the process.
        this.sumSquaredDifferences += delta * (value - this.mean);
    }
    
    /**
     * Updates the summary statistics with all the given values.
     * 
     * @param   values 
     *      The values to add.
     */
    public void updateAll(
        final double... values)
    {
        for (final Number value : values)
        {
            this.update(value.doubleValue());
        }
    }
    
    /**
     * Updates the summary statistics with all the given values.
     * 
     * @param   values 
     *      The values to add.
     */
    public void updateAll(
        final Iterable<? extends Number> values)
    {
        for (final Number value : values)
        {
            this.update(value.doubleValue());
        }
    }
    
    /**
     * Merges the given other summary statistics into this one. The result is
     * that this is modified while other is unchanged.
     * 
     * @param   other 
     *      The other summary statistics to merge into this one.
     */
    public void merge(
        final IncrementalSummaryStatistics other)
    {
        if (other.count <= 0)
        {
            // Other is empty.
            return;
        }

        this.min = Math.min(this.min, other.min);
        this.max = Math.max(this.max, other.max);

        // Merge the sum of squared differences.
        final double delta = other.mean - this.mean;
        final long totalCount = this.count + other.count;
        this.sumSquaredDifferences =
            this.sumSquaredDifferences
                + other.sumSquaredDifferences
                + delta * delta * this.count * other.count / totalCount;

        // We update these after the sum of squared distances since we
        // use the previous mean and count values to come up with it.
        this.mean = (this.count * this.mean + other.count * other.mean) / totalCount;
        this.count = totalCount;
    }
    
    /**
     * Returns a copy of the these summary statistic values.
     * 
     * @return 
     *      The values of this summary statistics.
     */
    public DefaultSummaryStatistics summarize()
    {
        return new DefaultSummaryStatistics(this.getMean(), this.getVariance(),
            this.getMin(), this.getMax());
    }

    /**
     * Gets the number of values seen.
     *
     * @return
     *      The number of values seen.
     */
    public long getCount()
    {
        return this.count;
    }

    @Override
    public double getMean()
    {
        return this.mean;
    }

    @Override
    public double getMin()
    {
        return this.min;
    }

    @Override
    public double getMax()
    {
        return this.max;
    }

    @Override
    public double getVariance()
    {
        if (this.count <= 1)
        {
            // This allows the default variance to be used.
            return this.sumSquaredDifferences;
        }

        return this.sumSquaredDifferences / (this.count - 1);
    }

    @Override
    public double getStandardDeviation()
    {
        return Math.sqrt(this.getVariance());
    }
    
}
