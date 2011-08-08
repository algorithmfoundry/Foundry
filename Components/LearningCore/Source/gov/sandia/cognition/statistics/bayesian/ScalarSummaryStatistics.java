/*
 * File:                ScalarSummaryStatistics.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.collection.NumberComparator;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A Bayesian-style synopsis of a Collection of scalar data.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class ScalarSummaryStatistics
    extends AbstractCloneableSerializable
{

    /**
     * Region of the confidence interval, {@value}.
     */
    public static final double CONFIDENCE_REGION = 0.95;

    /**
     * Min
     */
    private double min;

    /**
     * Max
     */
    private double max;

    /**
     * Quintile boundaries
     */
    private double[] quintiles;

    /**
     * Lower 95% confidence region (alpha=0.025)
     */
    private double confidenceLower;

    /**
     * Upper 95% confidence region (alpha=0.975)
     */
    private double confidenceUpper;

    /**
     * Median
     */
    private double median;

    /**
     * Number of samples
     */
    private int numSamples;

    /**
     * Arithmetic mean
     */
    private double mean;

    /**
     * Variance
     */
    private double variance;

    /**
     * Skew
     */
    private double skewness;

    /**
     * Excess kurtosis
     */
    private double kurtosis;

    /**
     * Creates a new set of scalar summary statistics.
     *
     * @param min
     *      The minimum.
     * @param max
     *      The maximum.
     * @param quintiles
     *      The quintiles.
     * @param confidenceLower
     *      The lower bound of the confidence.
     * @param confidenceUpper
     *      The upper bound of the confidence.
     * @param median
     *      The median.
     * @param numSamples
     *      The number of samples.
     * @param mean
     *      The mean.
     * @param variance
     *      The variance.
     * @param skewness
     *      The skewness.
     * @param kurtosis
     *      The kurtosis.
     */
    protected ScalarSummaryStatistics(
        double min,
        double max,
        double[] quintiles,
        double confidenceLower,
        double confidenceUpper,
        double median,
        int numSamples,
        double mean,
        double variance,
        double skewness,
        double kurtosis)
    {
        this.min = min;
        this.max = max;
        this.quintiles = quintiles;
        this.confidenceLower = confidenceLower;
        this.confidenceUpper = confidenceUpper;
        this.median = median;
        this.numSamples = numSamples;
        this.mean = mean;
        this.variance = variance;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
    }

    @Override
    public ScalarSummaryStatistics clone()
    {
        ScalarSummaryStatistics clone =
            (ScalarSummaryStatistics) super.clone();
        return clone;
    }

    /**
     * Creates a new instance of ScalarSummaryStatistics from a Collection
     * of scalar values.
     * @param data
     * Data from which to cull the results
     * @return
     * ScalarSummaryStatistics describing the data
     */
    public static ScalarSummaryStatistics create( 
        Collection<? extends Number> data )
    {

        ArrayList<? extends Number> sortedData =
            CollectionUtil.asArrayList(data);
        Collections.sort(sortedData,NumberComparator.INSTANCE);
        Pair<Double,Double> result = UnivariateStatisticsUtil.computeMinAndMax(sortedData);
        double min = result.getFirst();
        double max = result.getSecond();
        double median = UnivariateStatisticsUtil.computeMedian(sortedData);
        double[] quintiles = new double[ 4 ];
        quintiles[0] = UnivariateStatisticsUtil.computePercentile(sortedData, 0.2);
        quintiles[1] = UnivariateStatisticsUtil.computePercentile(sortedData, 0.4);
        quintiles[2] = UnivariateStatisticsUtil.computePercentile(sortedData, 0.6);
        quintiles[3] = UnivariateStatisticsUtil.computePercentile(sortedData, 0.8);

        double a2 = (1.0-CONFIDENCE_REGION)/2.0;
        double confidenceLower = UnivariateStatisticsUtil.computePercentile(sortedData,a2);
        double confidenceUpper = UnivariateStatisticsUtil.computePercentile(sortedData,1.0-a2);

        int numSamples = data.size();

        result = UnivariateStatisticsUtil.computeMeanAndVariance(sortedData);
        double mean = result.getFirst();
        double variance = result.getSecond();
        double skewness = UnivariateStatisticsUtil.computeSkewness(sortedData);
        double kurtosis = UnivariateStatisticsUtil.computeKurtosis(sortedData);
        return new ScalarSummaryStatistics(min, max, quintiles, confidenceLower,
            confidenceUpper, median, numSamples, mean, variance, skewness,
            kurtosis);
    }

    @Override
    public String toString()
    {
        StringBuilder retval = new StringBuilder( 1000 );
        retval.append( "Sample Count = " ).append( this.getNumSamples() );
        retval.append( "\n" );
        retval.append( "20/80 Region = [" ).append( this.getQuintiles()[0] ).append( ", " ).append( this.getQuintiles()[3] ).append( "]" );
        retval.append( "\n" );
        retval.append( "40/60 Region = [" ).append( this.getQuintiles()[1] ).append( ", " ).append( this.getQuintiles()[2] ).append( "]" );
        retval.append( "\n" );
        retval.append( "95% Region   = [" ).append( this.getConfidenceLower() ).append( ", " ).append( this.getConfidenceUpper() ).append( "]" );
        retval.append( "\n" );
        retval.append( "Min and Max  = [" ).append( this.getMin() ).append( ", " ).append( this.getMax() ).append( "]" );
        retval.append( "\n" );
        retval.append( "Median   = " ).append( this.getMedian() );
        retval.append( "\n" );
        retval.append( "Mean     = " ).append( this.getMean() ).append( ", StdDev = " ).append( Math.sqrt(this.getVariance()) );
        retval.append( "\n" );
        retval.append( "Skewness = " ).append( this.getSkewness() ).append( ", Kurtosis = " ).append( this.getKurtosis() );
        retval.append( "\n" );

        return retval.toString();
    }

    /**
     * Getter for mean
     * @return
     * Mean
     */
    public double getMean()
    {
        return this.mean;
    }

    /**
     * Getter for variance
     * @return
     * Variance
     */
    public double getVariance()
    {
        return this.variance;
    }

    /**
     * Getter for skewness
     * @return
     * Skewness
     */
    public double getSkewness()
    {
        return this.skewness;
    }

    /**
     * Getter for Kurtosis
     * @return
     * Excess kurtosis
     */
    public double getKurtosis()
    {
        return this.kurtosis;
    }

    /**
     * Getter for numSamples
     * @return
     * Number of samples
     */
    public int getNumSamples()
    {
        return this.numSamples;
    }

    /**
     * Getter for median
     * @return
     * Median
     */
    public double getMedian()
    {
        return this.median;
    }

    /**
     * Getter for min
     * @return Min
     */
    public double getMin()
    {
        return this.min;
    }

    /**
     * Getter for max
     * @return Max
     */
    public double getMax()
    {
        return this.max;
    }

    /**
     * Getter for quintiles
     * @return
     * Quintile boundaries
     */
    public double[] getQuintiles()
    {
        return this.quintiles;
    }

    /**
     * Getter for confidenceLower
     * @return
     * Lower 95% confidence region (alpha=0.025)
     */
    public double getConfidenceLower()
    {
        return this.confidenceLower;
    }

    /**
     * Getter for confidenceUpper
     * @return
     * Upper 95% confidence region (alpha=0.975)
     */
    public double getConfidenceUpper()
    {
        return this.confidenceUpper;
    }

}
