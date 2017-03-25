/*
 * File:                SummaryStatistics.java
 * Authors:             Jeremy D. Wendt and Justin Basilico
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
 * An interface for a collection of basic summary statistics for a series of
 * numbers (mean, min, max, standard deviation, variance, and count).
 *
 * @author  jdwendt
 * @author  Justin Basilico
 * @since   4.0.0
 */
public interface SummaryStatistics
{
    /**
     * Gets the mean of the values.
     *
     * @return
     *      The mean.
     */
    public double getMean();

    /**
     * Gets the unbiased variance of the values seen so far. It is the
     * unbiased version of the second moment and the square of the
     * standard deviation.
     *
     * @return
     *      The unbiased variance.
     */
    public double getVariance();

    /**
     * Gets the standard deviation.
     *
     * @return
     *      The standard deviation. Cannot be negative.
     */
    public double getStandardDeviation();
    
    /**
     * Gets the minimum value.
     *
     * @return
     *      The minimum value.
     */
    public double getMin();
    /**
     * Gets the maximum value.
     *
     * @return
     *      The maximum value.
     */
    public double getMax();

}
