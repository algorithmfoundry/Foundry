/*
 * File:            IncrementalSummaryStatisticsTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link IncrementalSummaryStatistics}.
 * 
 * @author Justin Basilico
 * @since   3.4.4
 */
public class IncrementalSummaryStatisticsTest
    extends Object
{
    
    /** Floating point precision error tolerance. */
    protected static final double TOLERANCE = 1e-5;

    /** The random number generator to use. */
    protected Random random = new Random();

    /**
     * Creates a new test.
     */
    public IncrementalSummaryStatisticsTest()
    {
        super();
    }

    @Test
    public void testConstants()
    {
        assertEquals(0.0, IncrementalSummaryStatistics.DEFAULT_VARIANCE, 0.0);
    }

    @Test
    public void testConstructors()
    {
        double defaultVariance = IncrementalSummaryStatistics.DEFAULT_VARIANCE;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(0, instance.getCount());
        assertEquals(0.0, instance.getMean(), 0.0);
        assertEquals(defaultVariance, instance.getVariance(), 0.0);
        assertEquals(Math.sqrt(defaultVariance), instance.getStandardDeviation(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, instance.getMin(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, instance.getMax(), 0.0);

        defaultVariance = 0.001;
        instance = new IncrementalSummaryStatistics(defaultVariance);
        assertEquals(0, instance.getCount());
        assertEquals(0.0, instance.getMean(), 0.0);
        assertEquals(defaultVariance, instance.getVariance(), 0.0);
        assertEquals(Math.sqrt(defaultVariance), instance.getStandardDeviation(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, instance.getMin(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, instance.getMax(), 0.0);
    }

    @Test
    public void testClone()
    {
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        IncrementalSummaryStatistics clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(clone, instance);
        assertNotSame(clone, instance.clone());

        assertEquals(instance.getCount(), clone.getCount());
        assertEquals(instance.getMean(), clone.getMean(), 0.0);
        assertEquals(instance.getVariance(), clone.getVariance(), 0.0);
        assertEquals(instance.getStandardDeviation(), clone.getStandardDeviation(), 0.0);
        assertEquals(instance.getMin(), clone.getMin(), 0.0);
        assertEquals(instance.getMax(), clone.getMax(), 0.0);

        instance.update(1.0);
        assertEquals(1, instance.getCount());
        assertEquals(1.0, instance.getMean(), TOLERANCE);
        assertEquals(0.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(0.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, instance.getMin(), TOLERANCE);
        assertEquals(1.0, instance.getMax(), TOLERANCE);

        clone.update(2.0);
        clone.update(2.0);
        assertEquals(2, clone.getCount());
        assertEquals(2.0, clone.getMean(), TOLERANCE);
        assertEquals(0.0, clone.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(0.0), clone.getStandardDeviation(), TOLERANCE);
        assertEquals(2.0, clone.getMin(), TOLERANCE);
        assertEquals(2.0, clone.getMax(), TOLERANCE);
    }

    @Test
    public void testAccumulate()
    {
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(0, instance.getCount());
        assertEquals(0.0, instance.getMean(), 0.0);
        assertEquals(0.0, instance.getVariance(), 0.0);
        assertEquals(0.0, instance.getStandardDeviation(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, instance.getMin(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, instance.getMax(), 0.0);

        instance.update(1.0);
        assertEquals(1, instance.getCount());
        assertEquals(1.0, instance.getMean(), TOLERANCE);
        assertEquals(0.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(0.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, instance.getMin(), TOLERANCE);
        assertEquals(1.0, instance.getMax(), TOLERANCE);

        instance.update(2.0);
        instance.update(3.0);
        instance.update(4.0);
        instance.update(5.0);
        assertEquals(5, instance.getCount());
        assertEquals(3.0, instance.getMean(), TOLERANCE);
        assertEquals(2.5, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(2.5), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, instance.getMin(), TOLERANCE);
        assertEquals(5.0, instance.getMax(), TOLERANCE);

        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        instance = new IncrementalSummaryStatistics();
        instance.updateAll(4.0, 7.0, 13.0, 16.0);
        assertEquals(4, instance.getCount());
        assertEquals(10.0, instance.getMean(), TOLERANCE);
        assertEquals(30.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(30.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(4.0, instance.getMin(), TOLERANCE);
        assertEquals(16.0, instance.getMax(), TOLERANCE);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        instance = new IncrementalSummaryStatistics();
        instance.updateAll(1.0e9 + 4.0, 1e9 + 7, 1e9 + 13, 1e9 + 16);
        assertEquals(4, instance.getCount());
        assertEquals(1e9 + 10.0, instance.getMean(), TOLERANCE);
        assertEquals(30.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(30.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1e9 + 4.0, instance.getMin(), TOLERANCE);
        assertEquals(1e9 + 16.0, instance.getMax(), TOLERANCE);
    }

    @Test
    public void testMerge()
    {
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        instance.merge(new IncrementalSummaryStatistics());
        assertEquals(0, instance.getCount());
        assertEquals(0.0, instance.getMean(), 0.0);
        assertEquals(0.0, instance.getVariance(), 0.0);
        assertEquals(0.0, instance.getStandardDeviation(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, instance.getMin(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, instance.getMax(), 0.0);

        instance.update(1.0);
        instance.merge(new IncrementalSummaryStatistics());
        assertEquals(1, instance.getCount());
        assertEquals(1.0, instance.getMean(), TOLERANCE);
        assertEquals(0.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(0.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, instance.getMin(), TOLERANCE);
        assertEquals(1.0, instance.getMax(), TOLERANCE);

        IncrementalSummaryStatistics other = new IncrementalSummaryStatistics();
        instance.update(2.0);
        other.update(3.0);
        other.update(4.0);
        instance.update(5.0);
        instance.merge(other);
        assertEquals(5, instance.getCount());
        assertEquals(3.0, instance.getMean(), TOLERANCE);
        assertEquals(2.5, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(2.5), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, instance.getMin(), TOLERANCE);
        assertEquals(5.0, instance.getMax(), TOLERANCE);

        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        instance = new IncrementalSummaryStatistics();
        instance.updateAll(4.0, 7.0);
        other = new IncrementalSummaryStatistics();
        other.updateAll(13.0, 16.0);
        instance.merge(other);
        assertEquals(4, instance.getCount());
        assertEquals(10.0, instance.getMean(), TOLERANCE);
        assertEquals(30.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(30.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(4.0, instance.getMin(), TOLERANCE);
        assertEquals(16.0, instance.getMax(), TOLERANCE);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        instance = new IncrementalSummaryStatistics();
        instance.updateAll(1.0e9 + 4.0, 1e9 + 7);
        other = new IncrementalSummaryStatistics();
        other.updateAll(1e9 + 13, 1e9 + 16);
        instance.merge(other);
        assertEquals(4, instance.getCount());
        assertEquals(1e9 + 10.0, instance.getMean(), TOLERANCE);
        assertEquals(30.0, instance.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(30.0), instance.getStandardDeviation(), TOLERANCE);
        assertEquals(1e9 + 4.0, instance.getMin(), TOLERANCE);
        assertEquals(1e9 + 16.0, instance.getMax(), TOLERANCE);
    }
    @Test
    public void testSummarize()
    {
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        SummaryStatistics result = instance.summarize();
        assertEquals(0.0, result.getMean(), 0.0);
        assertEquals(0.0, result.getVariance(), 0.0);
        assertEquals(0.0, result.getStandardDeviation(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, result.getMin(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, result.getMax(), 0.0);

        instance.update(1.0);
        result = instance.summarize();
        assertEquals(1.0, result.getMean(), TOLERANCE);
        assertEquals(0.0, result.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(0.0), result.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, result.getMin(), TOLERANCE);
        assertEquals(1.0, result.getMax(), TOLERANCE);

        instance.update(2.0);
        instance.update(3.0);
        instance.update(4.0);
        instance.update(5.0);
        result = instance.summarize();
        assertEquals(3.0, result.getMean(), TOLERANCE);
        assertEquals(2.5, result.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(2.5), result.getStandardDeviation(), TOLERANCE);
        assertEquals(1.0, result.getMin(), TOLERANCE);
        assertEquals(5.0, result.getMax(), TOLERANCE);

        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        instance = new IncrementalSummaryStatistics();
        instance.updateAll(4.0, 7.0, 13.0, 16.0);
        result = instance.summarize();
        assertEquals(10.0, result.getMean(), TOLERANCE);
        assertEquals(30.0, result.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(30.0), result.getStandardDeviation(), TOLERANCE);
        assertEquals(4.0, result.getMin(), TOLERANCE);
        assertEquals(16.0, result.getMax(), TOLERANCE);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        instance = new IncrementalSummaryStatistics();
        instance.updateAll(1.0e9 + 4.0, 1e9 + 7, 1e9 + 13, 1e9 + 16);
        result = instance.summarize();
        assertEquals(1e9 + 10.0, result.getMean(), TOLERANCE);
        assertEquals(30.0, result.getVariance(), TOLERANCE);
        assertEquals(Math.sqrt(30.0), result.getStandardDeviation(), TOLERANCE);
        assertEquals(1e9 + 4.0, result.getMin(), TOLERANCE);
        assertEquals(1e9 + 16.0, result.getMax(), TOLERANCE);
    }

    @Test
    public void testGetCount()
    {
        long count = 0;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(count, instance.getCount());

        int sampleSize = 10 + this.random.nextInt(10);
        List<Double> values = sampleGaussians(sampleSize);
        count += sampleSize;
        instance.updateAll(values);
        assertEquals(count, instance.getCount());

        sampleSize = 1 + this.random.nextInt(10);
        values = sampleGaussians(sampleSize);
        count += sampleSize;
        instance.updateAll(values);
        assertEquals(count, instance.getCount());
    }

    @Test
    public void testGetMean()
    {
        double mean = 0.0;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(mean, instance.getMean(), 0.0);

        List<Double> values = sampleGaussians(10 + this.random.nextInt(10));
        mean = UnivariateStatisticsUtil.computeMean(values);
        instance.updateAll(values);
        assertEquals(mean, instance.getMean(), TOLERANCE);
    }

    @Test
    public void testGetMin()
    {
        double min = Double.POSITIVE_INFINITY;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(min, instance.getMin(), 0.0);

        List<Double> values = sampleGaussians(10 + this.random.nextInt(10));
        min = UnivariateStatisticsUtil.computeMinimum(values);
        instance.updateAll(values);
        assertEquals(min, instance.getMin(), 0.0);
    }

    @Test
    public void testGetMax()
    {
        double max = Double.NEGATIVE_INFINITY;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(max, instance.getMax(), 0.0);

        List<Double> values = sampleGaussians(10 + this.random.nextInt(10));
        max = UnivariateStatisticsUtil.computeMaximum(values);
        instance.updateAll(values);
        assertEquals(max, instance.getMax(), 0.0);
    }

    @Test
    public void testGetVariance()
    {
        double variance = 0.0;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(variance, instance.getVariance(), 0.0);

        List<Double> values = sampleGaussians(10 + this.random.nextInt(10));
        variance = UnivariateStatisticsUtil.computeVariance(values);
        instance.updateAll(values);
        assertEquals(variance, instance.getVariance(), 0.0);
    }

    @Test
    public void testGetStandardDeviation()
    {
        double standardDeviation = 0.0;
        IncrementalSummaryStatistics instance = new IncrementalSummaryStatistics();
        assertEquals(standardDeviation, instance.getStandardDeviation(), 0.0);

        List<Double> values = sampleGaussians(10 + this.random.nextInt(10));
        standardDeviation = Math.sqrt(UnivariateStatisticsUtil.computeVariance(values));
        instance.updateAll(values);
        assertEquals(standardDeviation, instance.getStandardDeviation(), 0.0);
    }

    /**
     * Samples a list of random Gaussians with mean 0 and variance 1.
     *
     * @param   count
     *      The number of samples.
     * @return
     *      A list of Gaussians sampled from the N(0, 1) distribution.
     */
    protected List<Double> sampleGaussians(
        final int count)
    {
        final ArrayList<Double> result = new ArrayList<Double>(count);
        for (int i = 0; i < count; i++)
        {
            result.add(this.random.nextGaussian());
        }
        return result;
    }
}
