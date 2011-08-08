/*
 * File:                UnivariateMonteCarloIntegratorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.learning.function.scalar.PolynomialFunction.Linear;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for UnivariateMonteCarloIntegratorTest.
 *
 * @author krdixon
 */
public class UnivariateMonteCarloIntegratorTest
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
     * Samples
     */
    public int NUM_SAMPLES = 1000;

    /**
     * Tests for class UnivariateMonteCarloIntegratorTest.
     * @param testName Name of the test.
     */
    public UnivariateMonteCarloIntegratorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class UnivariateMonteCarloIntegratorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        assertNotNull( UnivariateMonteCarloIntegrator.INSTANCE );
        assertNotNull( new UnivariateMonteCarloIntegrator() );

    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        UnivariateMonteCarloIntegrator instance =
            new UnivariateMonteCarloIntegrator();
        UnivariateMonteCarloIntegrator clone =
            (UnivariateMonteCarloIntegrator) instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone );
    }

    /**
     * Test of integrate method, of class UnivariateMonteCarloIntegrator.
     */
    public void testIntegrate_Collection_Evaluator()
    {
        System.out.println("integrate");

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 3.0;
        UnivariateGaussian.PDF targetDistribution =
            new UnivariateGaussian.PDF( mean, variance );
        UnivariateMonteCarloIntegrator instance =
            new UnivariateMonteCarloIntegrator();

        int num = 100;
        ArrayList<Double> means = new ArrayList<Double>( num );
        ArrayList<Double> variances = new ArrayList<Double>( num );
        Linear linear = new Linear(0.0, 1.0);
        for( int n = 0; n < num; n++ )
        {
            ArrayList<Double> samples =
                targetDistribution.sample(RANDOM, NUM_SAMPLES);
            UnivariateGaussian.PDF g =
                instance.integrate(samples, linear);
            means.add( g.getMean() );
            variances.add( g.getVariance() );
        }

        UnivariateGaussian sampleMeanDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        UnivariateGaussian sampleVarianceDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        ConfidenceInterval ci = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleMeanDistribution.getMean(), sampleMeanDistribution.getVariance(), num, 0.95 );
        double meanTarget = targetDistribution.getMean();
        System.out.println( "====== Mean ========" );
        System.out.println( "Target D = " + targetDistribution );
        System.out.println( "Target   = " + meanTarget );
        System.out.println( "Sample   = " + sampleMeanDistribution );
        System.out.println( "Interval = " + ci );
        assertTrue( ci.withinInterval( meanTarget ) );

        ConfidenceInterval vi = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleVarianceDistribution.getMean(), sampleVarianceDistribution.getVariance(), num, 0.95 );
        double varianceTarget = targetDistribution.getVariance()/NUM_SAMPLES;
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution );
        System.out.println( "Interval = " + vi );
        assertTrue( vi.withinInterval(varianceTarget) );

    }

    /**
     * Test of integrate method, of class UnivariateMonteCarloIntegrator.
     */
    public void testIntegrate_List_Evaluator()
    {
        System.out.println("integrate");

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 3.0;
        UnivariateGaussian.PDF targetDistribution =
            new UnivariateGaussian.PDF( mean, variance );

        UnivariateGaussian.PDF importanceDistribution =
            new UnivariateGaussian.PDF( mean+mean, 2.0*variance );
        ImportanceSampler<Double> sampler =
            new ImportanceSampler<Double>( importanceDistribution );

        System.out.println( "Target     = " + targetDistribution );
        System.out.println( "Importance = " + importanceDistribution );

        UnivariateMonteCarloIntegrator instance =
            new UnivariateMonteCarloIntegrator();

        int num = 100;
        ArrayList<Double> means = new ArrayList<Double>( num );
        ArrayList<Double> variances = new ArrayList<Double>( num );
        Linear linear = new Linear(0.0, 1.0);
        for( int n = 0; n < num; n++ )
        {
            UnivariateGaussian.PDF g =
                instance.integrate( sampler.sample(targetDistribution, RANDOM, NUM_SAMPLES ), linear );
            means.add( g.getMean() );
            variances.add( g.getVariance() );
        }

        UnivariateGaussian sampleMeanDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        UnivariateGaussian sampleVarianceDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);


        ConfidenceInterval ci = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleMeanDistribution.getMean(), sampleMeanDistribution.getVariance(), num, 0.95 );
        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution );
        System.out.println( "Sample   = " + sampleMeanDistribution );
        System.out.println( "Interval = " + ci );
        assertTrue( ci.withinInterval( targetDistribution.getMean() ) );

        ConfidenceInterval vi = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleVarianceDistribution.getMean(), sampleVarianceDistribution.getVariance(), num, 0.95 );
        double s2 = importanceDistribution.getVariance();
        double s2hat = targetDistribution.getVariance();
        double y = targetDistribution.getMean();
        double m = importanceDistribution.getMean();
        double mhat = (s2*m + s2hat*y) / (s2 + s2hat);
        double varianceTarget = mhat/NUM_SAMPLES;
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution );
        System.out.println( "Interval = " + vi );
        assertTrue( vi.withinInterval(varianceTarget) );

    }

    /**
     * Test of getMean method, of class UnivariateMonteCarloIntegrator.
     */
    public void testGetMean_Collection()
    {
        System.out.println("getMean");
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 3.0;
        UnivariateGaussian.PDF targetDistribution =
            new UnivariateGaussian.PDF( mean, variance );
        UnivariateMonteCarloIntegrator instance =
            new UnivariateMonteCarloIntegrator();

        int num = 100;
        ArrayList<Double> means = new ArrayList<Double>( num );
        ArrayList<Double> variances = new ArrayList<Double>( num );
        for( int n = 0; n < num; n++ )
        {
            UnivariateGaussian.PDF g =
                instance.getMean( targetDistribution.sample(RANDOM, NUM_SAMPLES) );
            means.add( g.getMean() );
            variances.add( g.getVariance() );
        }

        UnivariateGaussian sampleMeanDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        UnivariateGaussian sampleVarianceDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        ConfidenceInterval ci = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleMeanDistribution.getMean(), sampleMeanDistribution.getVariance(), num, 0.95 );
        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution );
        System.out.println( "Sample   = " + sampleMeanDistribution );
        System.out.println( "Interval = " + ci );
        assertTrue( ci.withinInterval( targetDistribution.getMean() ) );
        
        ConfidenceInterval vi = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleVarianceDistribution.getMean(), sampleVarianceDistribution.getVariance(), num, 0.95 );
        double varianceTarget = targetDistribution.getVariance()/NUM_SAMPLES;
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution );
        System.out.println( "Interval = " + vi );
        assertTrue( vi.withinInterval(varianceTarget) );

    }

    /**
     * Equivalence of Weighted/Unweighted methods
     */
    public void testGetMean_Equivalence()
    {
        System.out.println( "Equivalence of Weighted/Unweighted methods" );

        final double weight = RANDOM.nextDouble() * 10.0;
        System.out.println( "Weight = " + weight );
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 3.0;
        UnivariateGaussian.PDF targetDistribution =
            new UnivariateGaussian.PDF( mean, variance );

        ArrayList<Double> samples = targetDistribution.sample(RANDOM, NUM_SAMPLES);
        ArrayList<DefaultWeightedValue<Double>> weightedSamples =
            new ArrayList<DefaultWeightedValue<Double>>( samples.size() );
        for( Double sample : samples )
        {
            weightedSamples.add( new DefaultWeightedValue<Double>( sample, weight ) );
        }

        UnivariateMonteCarloIntegrator instance =
            new UnivariateMonteCarloIntegrator();
        UnivariateGaussian ur = instance.getMean(samples);
        UnivariateGaussian wr = instance.getMean(weightedSamples);
        System.out.println( "Unweighted = " + ur );
        System.out.println( "Weighted   = " + wr );
        System.out.println( "U/W Ratio  = " + ur.getVariance() / wr.getVariance() );
        assertEquals( ur.getVariance(), wr.getVariance(), TOLERANCE );

        // Now add a whole bunch of samples to the weighted method with zero
        // weights.  This should return the sample variance.
        weightedSamples.ensureCapacity( weightedSamples.size() + NUM_SAMPLES );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            weightedSamples.add( new DefaultWeightedValue<Double>( samples.get(n), 0.0 ) );
        }
        UnivariateGaussian wr2 = instance.getMean(weightedSamples);
        System.out.println( "Weighted2  = " + wr2 );
        assertEquals( wr.getVariance(), wr2.getVariance(), TOLERANCE );


    }

    /**
     * Test of getMean method, of class UnivariateMonteCarloIntegrator.
     */
    public void testGetMean_List()
    {
        System.out.println("getMean");
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 3.0;
        UnivariateGaussian.PDF targetDistribution =
            new UnivariateGaussian.PDF( mean, variance );

        UnivariateGaussian.PDF importanceDistribution =
            new UnivariateGaussian.PDF( mean+mean, 2.0*variance );
        ImportanceSampler<Double> sampler =
            new ImportanceSampler<Double>( importanceDistribution );

        System.out.println( "Target     = " + targetDistribution );
        System.out.println( "Importance = " + importanceDistribution );

        UnivariateMonteCarloIntegrator instance =
            new UnivariateMonteCarloIntegrator();

        int num = 100;
        ArrayList<Double> means = new ArrayList<Double>( num );
        ArrayList<Double> variances = new ArrayList<Double>( num );
        for( int n = 0; n < num; n++ )
        {
            UnivariateGaussian.PDF g =
                instance.getMean( sampler.sample(targetDistribution, RANDOM, NUM_SAMPLES ) );
            means.add( g.getMean() );
            variances.add( g.getVariance() );
        }

        UnivariateGaussian sampleMeanDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        UnivariateGaussian sampleVarianceDistribution =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);
        

        ConfidenceInterval ci = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleMeanDistribution.getMean(), sampleMeanDistribution.getVariance(), num, 0.95 );
        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution );
        System.out.println( "Sample   = " + sampleMeanDistribution );
        System.out.println( "Interval = " + ci );
        assertTrue( ci.withinInterval( targetDistribution.getMean() ) );

        ConfidenceInterval vi = StudentTConfidence.INSTANCE.computeConfidenceInterval(
            sampleVarianceDistribution.getMean(), sampleVarianceDistribution.getVariance(), num, 0.95 );
        double s2 = importanceDistribution.getVariance();
        double s2hat = targetDistribution.getVariance();
        double y = targetDistribution.getMean();
        double m = importanceDistribution.getMean();
        double mhat = (s2*m + s2hat*y) / (s2 + s2hat);
        double varianceTarget = mhat/NUM_SAMPLES;
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution );
        System.out.println( "Interval = " + vi );
        assertTrue( vi.withinInterval(varianceTarget) );
        
    }



}
