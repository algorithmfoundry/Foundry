/*
 * File:                BayesianCredibleIntervalTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 8, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.bayesian.conjugate.BernoulliBayesianEstimator;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DiscreteDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for BayesianCredibleIntervalTest.
 *
 * @author krdixon
 */
public class BayesianCredibleIntervalTest
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
     * Tests for class BayesianCredibleIntervalTest.
     * @param testName Name of the test.
     */
    public BayesianCredibleIntervalTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class BayesianCredibleIntervalTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        double xmedian = 0.5;
        double xmin = 0.1;
        double xmax = 0.9;
        double confidence = 0.01;
        BayesianCredibleInterval instance = new BayesianCredibleInterval(
            xmedian, xmin, xmax, confidence);
        assertEquals( xmedian, instance.getCentralValue() );
        assertEquals( xmin, instance.getLowerBound() );
        assertEquals( xmax, instance.getUpperBound() );
        assertEquals( confidence, instance.getConfidence() );
        assertEquals( 1, instance.getNumSamples() );
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        double xmedian = 0.5;
        double xmin = 0.1;
        double xmax = 0.9;
        double confidence = 0.01;
        BayesianCredibleInterval instance = new BayesianCredibleInterval(
            xmedian, xmin, xmax, confidence);
        BayesianCredibleInterval clone = (BayesianCredibleInterval) instance.clone();
        assertEquals( instance.getCentralValue(), clone.getCentralValue() );
        assertEquals( instance.getLowerBound(), clone.getLowerBound() );
        assertEquals( instance.getUpperBound(), clone.getUpperBound() );
        assertEquals( instance.getConfidence(), clone.getConfidence() );
        assertEquals( instance.getNumSamples(), clone.getNumSamples() );
    }

    /**
     * Test of compute method, of class BayesianCredibleInterval.
     */
    public void testComputeGaussian()
    {
        System.out.println("compute Gaussian");

        for( int n = 0; n < 10; n++ )
        {
            UnivariateGaussian.CDF cdf = new UnivariateGaussian.CDF(
                RANDOM.nextGaussian(), RANDOM.nextDouble()*5.0 );
            double confidence = RANDOM.nextDouble();
            BayesianCredibleInterval result =
                BayesianCredibleInterval.compute(cdf, confidence);

            assertTrue( result.getLowerBound() <= result.getCentralValue() );
            assertTrue( result.getCentralValue() <= result.getUpperBound() );
            assertEquals( confidence, result.getConfidence() );
            assertEquals( 1, result.getNumSamples() );
            assertEquals( 0.5, cdf.evaluate(result.getCentralValue()), TOLERANCE );
            double delta = cdf.evaluate(result.getUpperBound()) -
                cdf.evaluate(result.getLowerBound());
            assertEquals( confidence, delta, TOLERANCE );
            this.validateInterval(cdf, confidence, result);
        }

    }

    /**
     * Compute Binomial
     */
    public void testComputeBinomial()
    {
        System.out.println( "compute Binomial" );

        for( int n = 0; n < 10; n++ )
        {
            final int N = RANDOM.nextInt(10) + 5;
            final double p = RANDOM.nextDouble();
            BinomialDistribution.CDF cdf = new BinomialDistribution.CDF( N, p );
            double confidence = RANDOM.nextDouble();
            BayesianCredibleInterval result =
                BayesianCredibleInterval.compute(cdf, confidence);
            assertTrue( result.getLowerBound() <= result.getCentralValue() );
            assertTrue( result.getCentralValue() <= result.getUpperBound() );
            assertEquals( confidence, result.getConfidence() );
            assertEquals( 1, result.getNumSamples() );
            double delta = cdf.evaluate(result.getUpperBound()) -
                cdf.evaluate(result.getLowerBound()-1);
            assertTrue( delta >= confidence );
            this.validateInterval(cdf, confidence, result);
        }

    }

    public <NumberType extends Number> void validateInterval(
        CumulativeDistributionFunction<NumberType> cdf,
        double confidence,
        BayesianCredibleInterval interval )
    {

        ArrayList<? extends NumberType> samples = cdf.sample(RANDOM, 1000);
        ArrayList<Integer> results = new ArrayList<Integer>( samples.size() );
        for( int n = 0; n < samples.size(); n++ )
        {
            results.add( interval.withinInterval(samples.get(n).doubleValue()) ? 1 : 0 );
        }
        BernoulliBayesianEstimator estimator = new BernoulliBayesianEstimator();
        BetaDistribution.CDF bayes = estimator.learn(results).getCDF();
        BayesianCredibleInterval bernoulli = BayesianCredibleInterval.compute(bayes, 0.95);
        System.out.println( "Confidence = " + confidence + ", Bernoulli: " + bernoulli );
        if( cdf instanceof DiscreteDistribution )
        {
            assertTrue( confidence <= bernoulli.getUpperBound() );
        }
        else
        {
            assertTrue( bernoulli.withinInterval(confidence) );
        }

    }

}
