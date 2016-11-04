/*
 * File:                SummaryStatistics.java
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
public interface SummaryStatistics
{
    /**
     * Add the input value to the summary statistics
     *
     * @param x the value to add to the summary statistics
     */
    public void addValue(double x);

    /**
     * Merges the summary statistics stored in the input into this
     *
     * @param stats The other statistics to merge into this
     */
    public void merge(StreamingSummaryStatistics stats);

    /**
     * Returns the minimum value seen thus far
     *
     * @return the minimum value seen thus far
     */
    public double min();

    /**
     * Returns the maximum value seen thus far
     *
     * @return the maximum value seen thus far
     */
    public double max();

    /**
     * Returns the arithmetic mean of all values seen thus far
     *
     * @return the arithmetic mean of all values seen thus far
     */
    public double mean();

    /**
     * Returns the number of entries seen thus far
     *
     * @return the number of entries seen thus far
     */
    public double numEntries();

    /**
     * Returns the variance of the values seen thus far
     *
     * @return the variance of the values seen thus far
     */
    public double variance();

    /**
     * Returns the standard deviation of the values seen thus far
     *
     * @return the standard deviation of the values seen thus far
     */
    public double standardDeviation();

}
