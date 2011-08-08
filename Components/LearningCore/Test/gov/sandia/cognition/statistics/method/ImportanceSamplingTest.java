/*
 * File:                ImportanceSamplingTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for ImportanceSamplingTest.
 *
 * @author krdixon
 */
public class ImportanceSamplingTest
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
     * Tests for class ImportanceSamplingTest.
     * @param testName Name of the test.
     */
    public ImportanceSamplingTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class ImportanceSamplingTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ImportanceSampling instance = new ImportanceSampling();
        assertNotNull( instance );

    }

    /**
     * Test of sample method, of class ImportanceSampling.
     */
    public void testSample()
    {
        System.out.println("sample");
        ProbabilityDensityFunction<Double> importanceDistribution =
            new UnivariateGaussian.PDF( 1.0, 2.0 );
        ScalarProbabilityDensityFunction targetDistribution =
            new BetaDistribution.PDF( 1.0, 2.0 );
        int numSamples = 100000;
        ArrayList<DefaultWeightedValue<Double>> result =
            ImportanceSampling.sample(importanceDistribution, targetDistribution, RANDOM, numSamples);

        UnivariateGaussian estimate =
            UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(result,0.0);
        System.out.println( "Estimate: " + estimate );
        System.out.println( "Target: Mean: " + targetDistribution.getMean() + " Variance: " + targetDistribution.getVariance() );

        assertEquals( targetDistribution.getMean(), estimate.getMean(), 10.0/Math.sqrt(numSamples) );
        assertEquals( targetDistribution.getVariance(), estimate.getVariance(), 10.0/Math.sqrt(numSamples) );

    }

}
