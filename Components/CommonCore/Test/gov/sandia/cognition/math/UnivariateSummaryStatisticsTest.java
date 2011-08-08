/*
 * File:                UnivariateSummaryStatisticsTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for UnivariateSummaryStatisticsTest.
 *
 * @author krdixon
 */
public class UnivariateSummaryStatisticsTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class UnivariateSummaryStatisticsTest.
     * @param testName Name of the test.
     */
    public UnivariateSummaryStatisticsTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Samples
     * @param random
     * RNG
     * @param numSamples
     * num samples
     * @return
     * samples from Gaussian
     */
    public static ArrayList<Double> sample(
        Random random,
        int numSamples )
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            samples.add( random.nextGaussian() );
        }
        return samples;
    }

    /**
     * Test of clone method, of class UnivariateSummaryStatistics.
     */
    public void testClone()
    {
        System.out.println("clone");
        int numSamples = 1000;
        Collection<? extends Number> data = sample(RANDOM, numSamples);
        UnivariateSummaryStatistics instance = UnivariateSummaryStatistics.create(data);
        UnivariateSummaryStatistics clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.toString(), clone.toString() );

    }

    /**
     * Test of create method, of class UnivariateSummaryStatistics.
     */
    public void testCreate()
    {
        System.out.println("create");
        int numSamples = 1000;
        Collection<? extends Number> data = sample(RANDOM, numSamples);
        UnivariateSummaryStatistics result = UnivariateSummaryStatistics.create(data);
        System.out.println( "Result: " + result );
        assertEquals( numSamples, result.getNumSamples() );
        assertEquals( UnivariateStatisticsUtil.computeMean(data), result.getMean(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeVariance(data), result.getVariance(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeSkewness(data), result.getSkewness(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeKurtosis(data), result.getKurtosis(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeMinimum(data), result.getMin(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeMaximum(data), result.getMax(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeMedian(data), result.getMedian(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data,0.20), result.getQuintiles()[0], TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data,0.40), result.getQuintiles()[1], TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data,0.60), result.getQuintiles()[2], TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data,0.80), result.getQuintiles()[3], TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data,0.025), result.getConfidenceLower(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data,0.975), result.getConfidenceUpper(), TOLERANCE );

    }

    /**
     * Test of toString method, of class UnivariateSummaryStatistics.
     */
    public void testToString()
    {
        System.out.println("toString");
        int numSamples = 1000;
        Collection<? extends Number> data = sample(RANDOM, numSamples);
        UnivariateSummaryStatistics instance = UnivariateSummaryStatistics.create(data);
        String result = instance.toString();
        System.out.println( "Result: " +  result );
        assertTrue( result.length() > 0 );
    }


}
