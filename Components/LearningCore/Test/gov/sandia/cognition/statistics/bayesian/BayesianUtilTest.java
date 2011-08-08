/*
 * File:                BayesianUtilTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 7, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.bayesian.conjugate.UnivariateGaussianMeanBayesianEstimator;
import gov.sandia.cognition.statistics.bayesian.conjugate.UnivariateGaussianMeanVarianceBayesianEstimator;
import gov.sandia.cognition.statistics.distribution.NormalInverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for BayesianUtilTest.
 *
 * @author krdixon
 */
public class BayesianUtilTest
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
     * Number of samples, {@value}.
     */
    public final int NUM_SAMPLES = 1000;

    /**
     * Default confidence, {@value}.
     */
    public final double CONFIDENCE = 0.95;

    /**
     * Tests for class BayesianUtilTest.
     * @param testName Name of the test.
     */
    public BayesianUtilTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class BayesianUtilTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        BayesianUtil instance = new BayesianUtil();
        assertNotNull( instance );
    }

    /**
     * Test of logLikelihood method, of class BayesianUtil.
     */
    public void testLogLikelihood()
    {
        System.out.println("logLikelihood");

        UnivariateGaussian.PDF f = new UnivariateGaussian.PDF();
        ArrayList<Double> observations = f.sample(RANDOM, NUM_SAMPLES);
        double result = BayesianUtil.logLikelihood(f, observations);
        double logSum = 0.0;
        for( Double observation : observations )
        {
            logSum += f.logEvaluate(observation);
        }
        assertEquals(logSum, result, TOLERANCE);
    }

    /**
     * Sample
     */
    public void testSampleParameter()
    {
        System.out.println( "sample" );
        double mean = RANDOM.nextGaussian();
        double variance = 1.0/RANDOM.nextDouble();
        UnivariateGaussian conditional = new UnivariateGaussian( mean, variance );
        UnivariateGaussianMeanVarianceBayesianEstimator estimator =
            new UnivariateGaussianMeanVarianceBayesianEstimator();

        ArrayList<Double> observations = conditional.sample(RANDOM, NUM_SAMPLES);
        NormalInverseGammaDistribution posterior = estimator.learn(observations);
        StudentTDistribution.CDF predictive =
            estimator.createPredictiveDistribution(posterior).getCDF();

        UnivariateGaussianMeanVarianceBayesianEstimator.Parameter parameter =
            new UnivariateGaussianMeanVarianceBayesianEstimator.Parameter(
                conditional, posterior);

        ArrayList<? extends Double> samples = BayesianUtil.sample(
            parameter, RANDOM, NUM_SAMPLES );
        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(samples, predictive);
        System.out.println( "K-S test:\n" + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );

    }

    /**
     * Sample
     */
    public void testSample3()
    {
        System.out.println( "Sample3" );
        double mean = RANDOM.nextGaussian();
        double variance = 1.0/RANDOM.nextDouble();
        UnivariateGaussian conditional = new UnivariateGaussian( mean, variance );
        ArrayList<Double> observations = conditional.sample(RANDOM, NUM_SAMPLES);
        UnivariateGaussianMeanBayesianEstimator instance =
            new UnivariateGaussianMeanBayesianEstimator(variance);

        UnivariateGaussian posterior = instance.learn(observations);
        UnivariateGaussian.CDF predictive =
            instance.createPredictiveDistribution(posterior).getCDF();
        ArrayList<? extends Double> samples = BayesianUtil.sample(
            conditional, "mean", posterior, RANDOM, NUM_SAMPLES );
        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(samples, predictive);
        System.out.println( "K-S test:\n" + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );
    }

    /**
     * Test of deviance method, of class BayesianUtil.
     */
    public void testDeviance()
    {
        System.out.println("deviance");
        double mean = RANDOM.nextGaussian();
        double variance = 1.0/RANDOM.nextDouble();
        UnivariateGaussian conditional = new UnivariateGaussian( mean, variance );
        UnivariateGaussianMeanVarianceBayesianEstimator estimator =
            new UnivariateGaussianMeanVarianceBayesianEstimator();

        ArrayList<Double> observations = conditional.sample(RANDOM, NUM_SAMPLES);
        NormalInverseGammaDistribution posterior = estimator.learn(observations);
        StudentTDistribution.CDF predictive =
            estimator.createPredictiveDistribution(posterior).getCDF();

        UnivariateGaussianMeanVarianceBayesianEstimator.Parameter parameter =
            new UnivariateGaussianMeanVarianceBayesianEstimator.Parameter(
                conditional, posterior);
    }

    /**
     * Test of expectedDeviance method, of class BayesianUtil.
     */
    public void testExpectedDeviance()
    {
        System.out.println("expectedDeviance");

        double mean = RANDOM.nextGaussian();
        double variance = 1.0/RANDOM.nextDouble();
        UnivariateGaussian conditional = new UnivariateGaussian( mean, variance );
        UnivariateGaussianMeanVarianceBayesianEstimator estimator =
            new UnivariateGaussianMeanVarianceBayesianEstimator();

        ArrayList<Double> observations = conditional.sample(RANDOM, NUM_SAMPLES);
        NormalInverseGammaDistribution posterior = estimator.learn(observations);
        StudentTDistribution predictive =
            estimator.createPredictiveDistribution(posterior);

        UnivariateGaussianMeanVarianceBayesianEstimator.Parameter parameter =
            new UnivariateGaussianMeanVarianceBayesianEstimator.Parameter(
                conditional, posterior);

        UnivariateGaussian expected = BayesianUtil.expectedDeviance(
            parameter, observations, RANDOM,NUM_SAMPLES);

        double result = BayesianUtil.deviance(predictive, observations);

        BayesianCredibleInterval vi =
            BayesianCredibleInterval.compute(expected,CONFIDENCE);
        System.out.println( "Result: " + result );
        System.out.println( "Interval: " + vi );
        assertTrue( vi.withinInterval(result) );

    }

    /**
     * Test of getMean method, of class UnivariateMonteCarloIntegrator.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        double mean = RANDOM.nextGaussian();
        double precision = RANDOM.nextDouble();
        StudentTDistribution targetDistribution =
            new StudentTDistribution( 4.0, mean, precision );

        System.out.println( "Target     = " + targetDistribution );

        int num = 100;
        ArrayList<Double> means = new ArrayList<Double>( num );
        ArrayList<Double> variances = new ArrayList<Double>( num );
        for( int n = 0; n < num; n++ )
        {
            UnivariateGaussian g = BayesianUtil.getMean(
                targetDistribution.sample(RANDOM, NUM_SAMPLES ) );
            means.add( g.getMean() );
            variances.add( g.getVariance() );
        }

        UnivariateGaussian sampleMeanDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        UnivariateGaussian sampleVarianceDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        ConfidenceInterval ci = StudentTConfidence.computeConfidenceInterval(
            sampleMeanDistribution, num, CONFIDENCE );
        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution );
        System.out.println( "Sample   = " + sampleMeanDistribution );
        System.out.println( "Interval = " + ci );
        assertTrue( ci.withinInterval( targetDistribution.getMean() ) );

        ConfidenceInterval vi = StudentTConfidence.computeConfidenceInterval(
            sampleVarianceDistribution, num, CONFIDENCE );
        double varianceTarget = targetDistribution.getVariance() / NUM_SAMPLES;
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution );
        System.out.println( "Interval = " + vi );
        assertTrue( vi.withinInterval(varianceTarget) );

    }

}
