/*
 * File:                SmoothUnivariateDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;
import gov.sandia.cognition.statistics.method.ImportanceSampling;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Unit tests for SmoothUnivariateDistribution.
 *
 * @author krdixon
 */
public abstract class SmoothUnivariateDistributionTestHarness
    extends ClosedFormUnivariateDistributionTestHarness<Double>
{

    /**
     * Tests for class SmoothScalarDistributionTestHarness2.
     * @param testName Name of the test.
     */
    public SmoothUnivariateDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    public abstract SmoothUnivariateDistribution createInstance();

    /**
     * Tests the PDF constructors
     */
    public abstract void testPDFConstructors();

    /**
     * Tests the PDF against known values
     */
    public abstract void testPDFKnownValues();

    /**
     * Test of getProbabilityFunction method, of class SmoothUnivariateDistribution.
     */
    public void testDistributionGetDistributionFunction()
    {
        System.out.println("Distribution.getDistributionFunction");
        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        assertNotNull( pdf );
        assertNotSame( instance, pdf );

        assertEquals( instance.getMean().doubleValue(), pdf.getMean().doubleValue() );
        assertEquals( instance.getVariance(), pdf.getVariance() );

        Random r11 = new Random( 1 );
        Random r12 = new Random( 1 );
        ArrayList<? extends Number> s1 = instance.sample(r11, NUM_SAMPLES);
        ArrayList<? extends Number> s2 = pdf.sample(r12, NUM_SAMPLES);
        assertTrue( this.distributionSamplesEqual(s1, s2) );
    }

    /**
     * PDF.getProbabilityFunction
     */
    public void testPDFGetDistributionFunction()
    {
        System.out.println( "PDF.getDistributionFunction" );
        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        UnivariateProbabilityDensityFunction pdf2 = pdf.getProbabilityFunction();
        assertNotNull( pdf2 );
        assertNotSame( instance, pdf );
        assertSame( pdf, pdf2 );
    }

    /**
     * PDF.sample
     */
    public void testPDFSample()
    {
        System.out.println( "PDF.sample" );

        // Make sure that when we re-feed an identical RANDOM seed, then
        // we get an equal sample from the distribution.  But different seeds
        // should give us different results... maybe.
        Random random1a = new Random( 1 );
        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        Double r11 = pdf.sample( random1a );
        Double rx2 = pdf.sample( random1a );
        assertNotNull( r11 );
        assertNotNull( rx2 );
        assertNotSame( r11, rx2 );
        assertFalse( r11.equals( rx2 ) );

        Random random1b = new Random( 1 );
        Double r13 = pdf.sample( random1b );
        assertNotNull( r13 );
        assertNotSame( r11, r13 );
        assertEquals( r11.doubleValue(), r13.doubleValue(), TOLERANCE );

        Random random2 = new Random( 2 );
        Double r21 = pdf.sample( random2 );
        assertNotNull( r21 );
        assertNotSame( r13, r21 );
        assertNotSame( r11, r21 );

        Random random1c = new Random( 1 );
        Double r14 = pdf.sample( random1c );
        assertNotNull( r14 );
        assertNotSame( r11, r14 );
        assertEquals( r11.doubleValue(), r14.doubleValue(), TOLERANCE );
        assertNotSame( r13, r14 );
        assertEquals( r13.doubleValue(), r14.doubleValue(), TOLERANCE );

    }

    /**
     * PDF.evaluate(Double)
     */
    public void testPDFEvaluate_Double()
    {
        System.out.println( "PDF.evaluate(Double)" );
        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        ArrayList<? extends Double> samples = pdf.sample( RANDOM, NUM_SAMPLES );
        for( Double sample : samples )
        {
            assertEquals( pdf.evaluate( sample ).doubleValue(), pdf.evaluate( sample.doubleValue() ) );
        }
    }

    /**
     * Tests PDF.nonnegative
     */
    public void testPDFNonNegative()
    {
        System.out.println( "PDF.nonnegative" );

        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        ArrayList<? extends Double> samples = instance.sample( RANDOM, NUM_SAMPLES );
        for( Double sample : samples )
        {
            assertTrue( pdf.evaluate( sample ) >= 0.0 );
        }
    }

    /**
     * logEvaluate
     */
    public void testPDFLogEvaluate()
    {
        System.out.println( "PDF.logEvaluate" );

        ProbabilityDensityFunction<Double> pdf = this.createInstance().getProbabilityFunction();
        ArrayList<? extends Double> samples = pdf.sample(RANDOM, NUM_SAMPLES);

        for( Double sample : samples )
        {
            double plog = pdf.logEvaluate(sample);
            double p = pdf.evaluate(sample);
            double phat = Math.exp(plog);
            assertEquals( p, phat, TOLERANCE );
        }

    }


    /**
     * PDF Monte Carlo
     */
    public void testPDFMonteCarlo()
    {
        System.out.println( "PDF Monte Carlo" );
        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        ArrayList<DefaultWeightedValue<Double>> samples =
            ImportanceSampling.sample( pdf, pdf, RANDOM, NUM_SAMPLES );
        
        double sum = 0.0;
        double weightSum = 0.0;
        for( WeightedValue<Double> value : samples )
        {
            double weight = value.getWeight();
            sum += weight * value.getValue();
            weightSum += weight;
        }

        double meanHat = sum / weightSum;
        double meanEst = pdf.getMean();
        double max = Math.max( Math.abs(meanEst), Math.abs(meanHat) );
        assertEquals( pdf.getMean(), meanHat, MONTE_CARLO_FACTOR * max / Math.sqrt(NUM_SAMPLES) );
    }

    /**
     * Computes an estimate of the probability distribution using the
     * PDF as the importance sampling weighting function.
     * @param learner
     * Weighted learner.
     */
    public void weightedDistributionEstimatorTest(
        BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends Distribution<Double>> learner )
    {
        System.out.println( "Test weighted learner" );

        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        System.out.println( "Target Distribution:\n" + pdf.toString() );

        // Sample according to some unrelated distribution.
        double std = Math.sqrt(pdf.getVariance());
        double r = 6.0;
        UniformDistribution.PDF background =
            new UniformDistribution.PDF( instance.getMean()-r*std, instance.getMean()+r*std );

        ArrayList<DefaultWeightedValue<Double>> weightedSamples =
            ImportanceSampling.sample(background, pdf, RANDOM, NUM_SAMPLES);

        Random r1 = new Random(1);
        Collection<? extends Double> samples = pdf.sample(r1, NUM_SAMPLES);

        Distribution<Double> estimate = learner.learn(weightedSamples);
        System.out.println( "Estimated Distribution:\n" + estimate.toString() );

        r1 = new Random(1);
        Collection<? extends Double> estimatedSamples =
            estimate.sample(r1, NUM_SAMPLES);

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.INSTANCE.evaluateNullHypothesis(samples,estimatedSamples);
        System.out.println( "K-S Test Results:\n" + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );
    }

    /**
     * CDF.evaluates
     */
    public void testCDFEvaluates()
    {
        System.out.println( "CDF.evaluate(Double) == CDF.evaluate(double)" );
        SmoothUnivariateDistribution instance = this.createInstance();
        SmoothCumulativeDistributionFunction cdf = instance.getCDF();
        ArrayList<? extends Double> samples = cdf.sample( RANDOM, NUM_SAMPLES );
        for( Double sample : samples )
        {
            assertEquals( cdf.evaluate( sample ).doubleValue(), cdf.evaluate( sample.doubleValue() ) );
        }
    }

    /**
     * Tests differentiate
     */
    public void testCDFDifferentiate()
    {
        System.out.println( "CDF.differentiate" );
        SmoothUnivariateDistribution instance = this.createInstance();
        SmoothCumulativeDistributionFunction cdf = instance.getCDF();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        UnivariateProbabilityDensityFunction dfdx = cdf.getDerivative();
        NumericalDifferentiator.DoubleJacobian differ =
            new NumericalDifferentiator.DoubleJacobian( cdf, TOLERANCE );
        ArrayList<? extends Double> samples = cdf.sample( RANDOM, NUM_SAMPLES );
        for( Double sample : samples )
        {
            double result = cdf.differentiate(sample);
            double actual = pdf.evaluate(sample);
            double approx = differ.differentiate(sample);
            assertEquals( actual, result );
            assertEquals( approx, result, Math.sqrt(TOLERANCE) );
        }


    }

    /**
     * CDF.getDerivative
     */
    public void testCDFGetDerivative()
    {
        System.out.println( "CDF.getDerivative" );
        SmoothUnivariateDistribution instance = this.createInstance();
        SmoothCumulativeDistributionFunction cdf = instance.getCDF();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        UnivariateProbabilityDensityFunction dfdx = cdf.getDerivative();
        assertTrue( pdf.getClass().isInstance(dfdx) );
        assertEquals( pdf.convertToVector(), dfdx.convertToVector() );
    }

    /**
     * Tests the support bound
     */
    public void testPDFSupport()
    {
        System.out.println( "PDF.Support" );
        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();
        assertEquals( instance.getMinSupport(), pdf.getMinSupport() );
        assertEquals( instance.getMaxSupport(), pdf.getMaxSupport() );
    }

    /**
     * PDF Boundary Conditions
     */
    public void testPDFBoundaryConditions()
    {
        System.out.println( "PDF Boundary Conditions" );

        SmoothUnivariateDistribution instance = this.createInstance();
        UnivariateProbabilityDensityFunction pdf = instance.getProbabilityFunction();

        Double min = pdf.getMinSupport();
        if( !Double.isInfinite(min) )
        {
            double mm1 = min-TOLERANCE;
            assertEquals( 0.0, pdf.evaluate(mm1) );
        }
        Double max = pdf.getMaxSupport();
        if( !Double.isInfinite(max) )
        {
            double mp1 = max+TOLERANCE;
            assertEquals( 0.0, pdf.evaluate(mp1) );
        }
        
    }

    /**
     * CDF.inverse
     */
    public void testCDFInverse()
    {
        System.out.println( "CDF.inverse" );

        SmoothCumulativeDistributionFunction cdf = this.createInstance().getCDF();
        if( cdf instanceof InvertibleCumulativeDistributionFunction )
        {
            System.out.println( "Found InvertibleCumulativeDistributionFunction... testing." );
            ArrayList<? extends Double> samples = cdf.sample(RANDOM, NUM_SAMPLES);
            InvertibleCumulativeDistributionFunction<Double> icdf =
                (InvertibleCumulativeDistributionFunction<Double>) cdf;
            for( int n = 0; n < samples.size(); n++ )
            {
                double p = icdf.evaluate(samples.get(n));
                double xhat = icdf.inverse(p);
                double phat = icdf.evaluate(xhat);
                assertEquals( p, phat, TOLERANCE );
            }
        }

    }

}
