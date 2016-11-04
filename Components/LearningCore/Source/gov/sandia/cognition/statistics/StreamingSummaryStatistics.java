/*
 * File:                StreamingSummaryStatistics.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2016, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * A straightforward class for computing summary statistics for a series of
 * numbers (mean, min, max, standard deviation, variance). This class stores
 * only the summary statistics, but is written such that it can compute them in
 * the best known numerically stable manner, computes variance in a single pass,
 * maintains values (so you can query then add new data), and will allow you to
 * merge results from other instances keeping correct values for all summary
 * statistics.
 *
 * @author jdwendt
 */
public class StreamingSummaryStatistics implements SummaryStatistics
{

    /**
     * The number of entries entered into this engine
     */
    private int numEntries;

    /**
     * The minimum value seen thus far
     */
    private double min;

    /**
     * The maximum value seen thus far
     */
    private double max;

    /**
     * The arithmetic mean of the values seen thus far
     */
    private double mean;

    /**
     * The intermediate value which is optimized for adding new values or
     * returning actual variance
     */
    private double intermediateForVariance;

    /**
     * Initializes an empty summary. Values returned from this before any data
     * is entered will not be good (max values, NaNs, etc.)
     */
    public StreamingSummaryStatistics()
    {
        numEntries = 0;
        min = Double.MAX_VALUE;
        max = -Double.MIN_VALUE;
        mean = Double.NaN;
        intermediateForVariance = Double.NaN;
    }

    /**
     * Add the input value to the summary statistics
     *
     * @param x the value to add to the summary statistics
     */
    @PublicationReference(type = PublicationType.WebPage, title
        = "Algorithms for calculating variance - Online algorithm", year = 2016,
        url
        = "https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm",
        author = "Wikipedia")
    @Override
    public void addValue(double x)
    {
        if (Double.isNaN(mean))
        {
            mean = 0;
            intermediateForVariance = 0;
        }
        min = Math.min(x, min);
        max = Math.max(x, max);
        ++numEntries;
        double delta = x - mean;
        mean += delta / numEntries;
        intermediateForVariance += delta * (x - mean);
    }

    /**
     * Merges the summary statistics stored in the input into this
     *
     * @param stats The other statistics to merge into this
     */
    @PublicationReference(type = PublicationType.WebPage, title
        = "An average of standard deviations", year = 2010, author
        = "See response by Dragon on 11/06/2010", url
        = "http://www.talkstats.com/showthread.php/14523-An-average-of-standard-deviations")
    @Override
    public void merge(StreamingSummaryStatistics stats)
    {
        // No need to merge anything in if nothing is there
        if (stats.numEntries == 0)
        {
            return;
        }
        // If I have nothing, I just copy him
        if (numEntries == 0)
        {
            numEntries = stats.numEntries;
            min = stats.min;
            max = stats.max;
            mean = stats.mean;
            intermediateForVariance = stats.intermediateForVariance;
            return;
        }
        // Now for the default case
        // Updating the variance is a pain, and the equation I found for it requires
        // the actual variance.  We'll back out to the intermediate in a moment
        double sx2 = variance();
        double sy2 = stats.variance();
        double nx = numEntries();
        double ny = stats.numEntries();
        double xbar = mean();
        double ybar = stats.mean();
        double newVariance = ((sqr(nx) * sx2) + (sqr(ny) * sy2) - (ny * sx2)
            - (ny * sy2) - (nx * sx2) - (nx * sy2) + (ny * nx * sx2) + (ny * nx
            * sy2) + (nx * ny * sqr(xbar - ybar))) / ((nx + ny - 1) * (nx + ny));
        mean = ((mean * numEntries) + (stats.mean * stats.numEntries))
            / (numEntries + stats.numEntries);
        numEntries += stats.numEntries;
        intermediateForVariance = newVariance * (numEntries - 1);
        min = Math.min(stats.min, min);
        max = Math.max(max, stats.max);
    }

    /**
     * Internal helper that makes the variance-merging equation easier to read
     *
     * @param x The value to square
     * @return x * x
     */
    private static double sqr(double x)
    {
        return x * x;
    }

    /**
     * Returns the minimum value seen thus far
     *
     * @return the minimum value seen thus far
     */
    @Override
    public double min()
    {
        return min;
    }

    /**
     * Returns the maximum value seen thus far
     *
     * @return the maximum value seen thus far
     */
    @Override
    public double max()
    {
        return max;
    }

    /**
     * Returns the arithmetic mean of all values seen thus far
     *
     * @return the arithmetic mean of all values seen thus far
     */
    @Override
    public double mean()
    {
        return mean;
    }

    /**
     * Returns the number of entries seen thus far
     *
     * @return the number of entries seen thus far
     */
    @Override
    public double numEntries()
    {
        return numEntries;
    }

    /**
     * Returns the variance of the values seen thus far
     *
     * @return the variance of the values seen thus far
     */
    @Override
    public double variance()
    {
        return intermediateForVariance / (numEntries - 1);
    }

    /**
     * Returns the standard deviation of the values seen thus far
     *
     * @return the standard deviation of the values seen thus far
     */
    @Override
    public double standardDeviation()
    {
        return Math.sqrt(variance());
    }

}
