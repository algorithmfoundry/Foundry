/*
 * File:                UnivariateDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 2, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.method.ChiSquareConfidence;
import gov.sandia.cognition.statistics.method.ConfidenceStatistic;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for UnivariateDistribution.
 *
 * @param <NumberType> Number type
 * @author krdixon
 */
public abstract class UnivariateDistributionTestHarness<NumberType extends Number>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Confidence for sampling
     */
    public double CONFIDENCE = 0.95;

    /**
     * Number of samples to draw
     */
    public int NUM_SAMPLES = 1000;

    /**
     * Monte Carlo error fudge factor.
     */
    public double MONTE_CARLO_FACTOR = 2.0;

    /**
     * Tests for class ScalarDistributionTestHarness2.
     * @param testName Name of the test.
     */
    public UnivariateDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a new Distribution
     * @return
     * New Distribution
     */
    public abstract UnivariateDistribution<NumberType> createInstance();

    /**
     * createInstance
     */
    public void testCreateInstance()
    {
        System.out.println( "createInstance" );

        UnivariateDistribution<? extends Number> instance = this.createInstance();
        assertNotNull( instance );
        assertFalse( instance instanceof CumulativeDistributionFunction );
        assertFalse( instance instanceof ProbabilityDensityFunction );
    }

    /**
     * Tests the constructors of class ScalarDistributionTestHarness2.
     */
    public abstract void testDistributionConstructors();

    /**
     * Tests the CDF constructors
     */
    public abstract void testCDFConstructors();

    /**
     * CDF known values
     */
    public abstract void testCDFKnownValues();

    /**
     * Samples
     * @param s1
     * @param s2
     * @return
     */
    public boolean distributionSamplesEqual(
        ArrayList<? extends Number> s1,
        ArrayList<? extends Number> s2 )
    {

        if( s1.size() != s2.size() )
        {
            return false;
        }
        for( int n = 0; n < s1.size(); n++ )
        {
            if( s1.get(n) == null )
            {
                return false;
            }
            if( s2.get(n) == null )
            {
                return false;
            }
            if( s1 == s2 )
            {
                return false;
            }
            if( Math.abs(s1.get(n).doubleValue() - s2.get(n).doubleValue()) > TOLERANCE )
            {
                return false;
            }
        }

        return true;

    }

    /**
     * Clone
     */
    public void testDistributionClone()
    {
        System.out.println( "Distribution.clone" );

        Distribution<? extends Number> instance = this.createInstance();
        @SuppressWarnings("unchecked")
        Distribution<? extends Number> clone =
            (Distribution<? extends Number>) instance.clone();

        assertNotNull( clone );
        assertNotSame( instance, clone );

        Random r11 = new Random(1);
        Random r12 = new Random(1);
        ArrayList<? extends Number> s1 = instance.sample(r11, NUM_SAMPLES);
        ArrayList<? extends Number> s2 = clone.sample(r12, NUM_SAMPLES);
        assertTrue( this.distributionSamplesEqual(s1, s2) );

    }

    /**
     * CDF.clone
     */
    public void testCDFClone()
    {

        UnivariateDistribution<? extends Number> distribution = this.createInstance();
        CumulativeDistributionFunction<? extends Number> instance = distribution.getCDF();

        @SuppressWarnings("unchecked")
        CumulativeDistributionFunction<? extends Number> clone =
            (CumulativeDistributionFunction<? extends Number>) instance.clone();

        assertNotNull( clone );
        assertNotSame( instance, clone );

        Random r11 = new Random(1);
        Random r12 = new Random(1);
        ArrayList<? extends Number> s1 = instance.sample(r11, NUM_SAMPLES);
        ArrayList<? extends Number> s2 = clone.sample(r12, NUM_SAMPLES);
        assertTrue( this.distributionSamplesEqual(s1, s2) );

    }

    /**
     * Test of getMean method, of class Distribution.
     */
    public void testDistributionGetMean()
    {
        System.out.println( "Distribution.getMean" );
        UnivariateDistribution<NumberType> instance = this.createInstance();

        // Ask the distribution for its mean
        Number mean = instance.getMean();

        // Sample from the distribution
        ArrayList<? extends Number> samples =
            instance.sample( RANDOM, NUM_SAMPLES  );
        ArrayList<Double> doubleSamples = new ArrayList<Double>( samples.size() );
        for( Number sample : samples )
        {
            doubleSamples.add( sample.doubleValue() );
        }

        // Here is the confidence that the sample mean could have been
        // sampled from the hypothesis distribution.  This should be 1.0.
        // If it's less than 0.05, then we've got a problem.
        ConfidenceStatistic confidence =
            GaussianConfidence.evaluateNullHypothesis( doubleSamples, mean.doubleValue() );
        System.out.println( "Distribution: " + instance );
        System.out.println( "Mean Confidence: " + confidence );
        System.out.println( "Sample mean: " + UnivariateStatisticsUtil.computeMean( samples ) + ", Mean: " + mean );
        assertEquals( 1.0, confidence.getNullHypothesisProbability(), CONFIDENCE );
    }

    /**
     * CDF.getMean
     */
    public void testCDFGetMean()
    {
        System.out.println( "CDF.getMean" );

        UnivariateDistribution<? extends Number> instance = this.createInstance();
        CumulativeDistributionFunction<? extends Number> cdf = instance.getCDF();

        Number m1 = instance.getMean();
        Number m2 = cdf.getMean();

        assertEquals( m1.doubleValue(), m2.doubleValue() );

    }

    /**
     * Test of sample method, of class Distribution.
     */
    public void testDistributionSample_Random()
    {
        System.out.println( "Distribution.sample(random)" );

        // Make sure that when we re-feed an identical RANDOM seed, then
        // we get an equal sample from the distribution.  But different seeds
        // should give us different results... maybe.
        Random random1a = new Random( 1 );
        Distribution<? extends Number> instance = this.createInstance();
        Number r11 = instance.sample( random1a );
        Number rx2 = instance.sample( random1a );
        assertNotNull( r11 );
        assertNotNull( rx2 );
//        assertNotSame( r11, rx2 );

        Random random1b = new Random( 1 );
        Number r13 = instance.sample( random1b );
        assertNotNull( r13 );
//        assertNotSame( r11, r13 );
        assertEquals( r11.doubleValue(), r13.doubleValue(), TOLERANCE );

        Random random2 = new Random( 2 );
        Number r21 = instance.sample( random2 );
        assertNotNull( r21 );
//        assertNotSame( r13, r21 );
//        assertNotSame( r11, r21 );

        Random random1c = new Random( 1 );
        Number r14 = instance.sample( random1c );
        assertNotNull( r14 );
//        assertNotSame( r11, r14 );
        assertEquals( r11.doubleValue(), r14.doubleValue(), TOLERANCE );
//        assertNotSame( r13, r14 );
        assertEquals( r13.doubleValue(), r14.doubleValue(), TOLERANCE );
    }

    /**
     * Test of sample method, of class Distribution.
     */
    public void testDistributionSample_Random_int()
    {
        System.out.println( "Distribution.sample(random,int)" );
        Distribution<? extends Number> instance = this.createInstance();

        // Identical RANDOM seeds should produce equal squences.
        // (Can't say anything about different seeds because deterministic
        // distributions always return the same result, regardless of seed.)
        Random r1a = new Random( 1 );
        ArrayList<? extends Number> s1a = instance.sample( r1a, NUM_SAMPLES );
        assertEquals( NUM_SAMPLES, s1a.size() );

        Random r1b = new Random( 1 );
        ArrayList<? extends Number> s1b = instance.sample( r1b, NUM_SAMPLES );
        assertEquals( NUM_SAMPLES, s1b.size() );

        assertEquals( s1a.size(), s1b.size() );
        assertTrue( this.distributionSamplesEqual(s1a, s1b) );

    }

    /**
     * Test of sample method, of class Distribution.
     */
    public void testCDFSample_Random_int()
    {
        System.out.println( "CDF.sample(random,int)" );
        UnivariateDistribution<NumberType> instance = this.createInstance();
        CumulativeDistributionFunction<NumberType> cdf = instance.getCDF();

        Random r11 = new Random(1);
        Random r12 = new Random(1);
        ArrayList<? extends NumberType> s1 = instance.sample(r11, NUM_SAMPLES);
        ArrayList<? extends NumberType> s2 = cdf.sample(r12, NUM_SAMPLES);        
        assertTrue( this.distributionSamplesEqual(s1, s2) );

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(s1,cdf);
        System.out.println( "K-S test: " + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );

    }


    /**
     * Tests getEstimator
     */
    public void testEstimableDistributionGetEstimator()
    {
        System.out.println( "EstimableDistribution.getEstimator" );

        UnivariateDistribution<NumberType> instance = this.createInstance();
        if( instance instanceof EstimableDistribution )
        {
            EstimableDistribution<NumberType,? extends EstimableDistribution<NumberType,? extends UnivariateDistribution<NumberType>>> estimable =
                (EstimableDistribution<NumberType, ? extends EstimableDistribution<NumberType, ? extends UnivariateDistribution<NumberType>>>) instance;
            @SuppressWarnings("unchecked")
            DistributionEstimator<NumberType, ? extends UnivariateDistribution<NumberType>> estimator =
                (DistributionEstimator<NumberType, ? extends UnivariateDistribution<NumberType>>) estimable.getEstimator();

            this.distributionEstimatorTest(estimator);

        }

    }

    /**
     * Tests the ability of the learner to estimate a distribution from its
     * samples.
     * @param learner
     */
    @SuppressWarnings("unchecked")
    public void distributionEstimatorTest(
        BatchLearner<Collection<? extends NumberType>, ? extends UnivariateDistribution<NumberType>> learner )
    {
        System.out.println( "Test learner" );
        UnivariateDistribution<? extends NumberType> distribution = this.createInstance();

        if( distribution instanceof ClosedFormDistribution )
        {
            Vector parameters = ((ClosedFormDistribution) distribution).convertToVector();
            System.out.println( "Target: " + distribution.getClass().getCanonicalName() + ", Parameters: " + parameters );
        }
        else
        {
            System.out.println( "Target distribution:\n" + distribution.toString() );
        }

        Random r1 = new Random(1);
        ArrayList<? extends NumberType> samples =
            distribution.sample(r1, NUM_SAMPLES);

        UnivariateDistribution<? extends NumberType> estimate = learner.learn(samples);

        if( distribution instanceof ClosedFormDistribution )
        {
            Vector parameters = ((ClosedFormDistribution) estimate).convertToVector();
            System.out.println( "Estimate: " + distribution.getClass().getCanonicalName() + ", Parameters: " + parameters );
        }
        else
        {
            System.out.println( "Estimated distribution:\n" + estimate.toString() );
        }

        if( estimate instanceof DiscreteDistribution )
        {
            ProbabilityMassFunction<NumberType> pmf =
                ((DiscreteDistribution<NumberType>) estimate).getProbabilityFunction();
            ChiSquareConfidence.Statistic chisquare =
                ChiSquareConfidence.evaluateNullHypothesis( samples, pmf );
            System.out.println( "Chi-Square Test Results:\n" + chisquare );
            assertEquals( 1.0, chisquare.getNullHypothesisProbability(), CONFIDENCE );
        }
        else
        {
            CumulativeDistributionFunction<NumberType> cdf =
                (CumulativeDistributionFunction<NumberType>) estimate.getCDF();
            KolmogorovSmirnovConfidence.Statistic kstest =
                KolmogorovSmirnovConfidence.evaluateNullHypothesis(samples, cdf);
            System.out.println( "K-S Test Results:\n" + kstest );
            assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );
        }

    }

    /**
     * Test of getCDF method, of class UnivariateDistribution.
     */
    public void testDistributionGetCDF()
    {
        System.out.println("Distribution.getCDF");
        UnivariateDistribution<? extends Number> instance = this.createInstance();
        CumulativeDistributionFunction<? extends Number> cdf = instance.getCDF();
        assertNotNull( cdf );
        assertNotSame( instance, cdf );

        assertEquals( instance.getMean().doubleValue(), cdf.getMean().doubleValue() );

        Random r11 = new Random( 1 );
        Random r12 = new Random( 1 );
        ArrayList<? extends Number> s1 = instance.sample(r11, NUM_SAMPLES);
        ArrayList<? extends Number> s2 = cdf.sample(r12, NUM_SAMPLES);
        assertTrue( this.distributionSamplesEqual(s1, s2) );
    }

    /**
     * CDF.getCDF
     */
    public void testCDFGetCDF()
    {
        System.out.println( "CDF.getCDF" );
        UnivariateDistribution<? extends Number> instance = this.createInstance();
        CumulativeDistributionFunction<? extends Number> cdf = instance.getCDF();

        CumulativeDistributionFunction<? extends Number> cdf2 = cdf.getCDF();
        assertNotNull( cdf2 );
        assertNotSame( instance, cdf );
        assertSame( cdf, cdf2 );
    }

    /**
     * Test of getVariance method, of class UnivariateDistribution.
     */
    public void testDistributionGetVariance()
    {
        System.out.println("getVariance");
        UnivariateDistribution<? extends Number> instance = this.createInstance();

        ArrayList<? extends Number> s1 = instance.sample(RANDOM, NUM_SAMPLES);
        double sampleVariance = UnivariateStatisticsUtil.computeVariance(s1);
        double estimatedVariance = instance.getVariance();
        System.out.println( "Sample Variance: " + sampleVariance );
        System.out.println( "Stated Variance: " + estimatedVariance );
        double max = Math.max(sampleVariance,estimatedVariance);
        assertEquals( sampleVariance, instance.getVariance(),
            MONTE_CARLO_FACTOR*max/Math.sqrt(NUM_SAMPLES) );
    }

    /**
     * CDF.getVariance
     */
    public void testCDFGetVariance()
    {
        System.out.println( "CDF.getVariance" );
        UnivariateDistribution<? extends Number> instance = this.createInstance();
        CumulativeDistributionFunction<? extends Number> cdf = instance.getCDF();
        assertEquals( instance.getVariance(), cdf.getVariance(), TOLERANCE );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.statistics.CumulativeDistributionFunction.
     */
    public void testCDFBounded()
    {
        System.out.println("CDF.bounded");

        UnivariateDistribution<NumberType> instance = this.createInstance();
        CumulativeDistributionFunction<NumberType> cdf = instance.getCDF();
        ArrayList<? extends NumberType> samples = cdf.sample( RANDOM, NUM_SAMPLES );
        for( NumberType sample : samples )
        {
            double p = cdf.evaluate( sample );
            assertTrue( 0.0 <= p );
            assertTrue( p <= 1.0 );
        }

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.statistics.CumulativeDistributionFunction.
     */
    @SuppressWarnings("unchecked")
    public void testCDFBoundaryConditions()
    {
        System.out.println("testCDFBoundaryConditions");

        UnivariateDistribution<NumberType> instance = this.createInstance();
        CumulativeDistributionFunction<NumberType> cdf = instance.getCDF();
        NumberType min = cdf.getMinSupport();
        NumberType max = cdf.getMaxSupport();

        // If it's discrete, then the CDF won't be zero at the support bounds,
        // but the value of the PMF at the support... ugh.
        // So, let's just go to infinity (or max int) and check there.
        if( cdf instanceof DiscreteDistribution )
        {
            if( min instanceof Integer )
            {
               min = (NumberType) new Integer( Integer.MIN_VALUE+1 );
               max = (NumberType) new Integer( Integer.MAX_VALUE-1 );
            }
            else
            {
                min = (NumberType) new Double( Double.NEGATIVE_INFINITY );
                max = (NumberType) new Double( Double.POSITIVE_INFINITY );
            }
        }

        assertEquals( 0.0, cdf.evaluate( min ), TOLERANCE );
        assertEquals( 1.0, cdf.evaluate( max ), TOLERANCE );

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.statistics.CumulativeDistributionFunction.
     */
    public void testCDFNonDecreasing()
    {
        System.out.println("CDF.nondecreasing");

        UnivariateDistribution<NumberType> instance = this.createInstance();
        CumulativeDistributionFunction<NumberType> cdf = instance.getCDF();

        ArrayList<? extends NumberType> s1 = cdf.sample(RANDOM, NUM_SAMPLES);
        ArrayList<? extends NumberType> s2 = cdf.sample(RANDOM, NUM_SAMPLES);
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            NumberType x1 = s1.get(n);
            NumberType x2 = s2.get(n);
            double v1 = cdf.evaluate(x1);
            double v2 = cdf.evaluate(x2);
            if( x1.doubleValue() < x2.doubleValue() )
            {
                assertTrue( v1 <= v2 );
            }
            else
            {
                assertTrue( v1 >= v2 );
            }
        }

    }

    /**
     * Tests the support bound
     */
    public void testDistributionSupport()
    {
        System.out.println( "Distribution.Support" );
        UnivariateDistribution<NumberType> instance = this.createInstance();

        ArrayList<? extends NumberType> samples = instance.sample(RANDOM, NUM_SAMPLES);
        for( NumberType sample : samples )
        {
            assertTrue( instance.getMinSupport().doubleValue() <= sample.doubleValue() );
            assertTrue( sample.doubleValue() <= instance.getMaxSupport().doubleValue() );
        }
    }

    /**
     * Tests the support bound
     */
    public void testCDFSupport()
    {
        System.out.println( "CDF.Support" );
        UnivariateDistribution<NumberType> instance = this.createInstance();
        CumulativeDistributionFunction<NumberType> cdf = instance.getCDF();
        assertEquals( instance.getMinSupport(), cdf.getMinSupport() );
        assertEquals( instance.getMaxSupport(), cdf.getMaxSupport() );
    }

}
