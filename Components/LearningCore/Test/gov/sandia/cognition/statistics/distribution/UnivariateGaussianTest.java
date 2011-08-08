/*
 * File:                UnivariateGaussianTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Kevin R. Dixon
 */
public class UnivariateGaussianTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Constructor
     * @param testName Name
     */
    public UnivariateGaussianTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public UnivariateGaussian createInstance()
    {
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        return new UnivariateGaussian( mean, variance );
    }

    @Override
    public void testDistributionGetVariance()
    {
        int temp = NUM_SAMPLES;
        NUM_SAMPLES = 10000;
        super.testDistributionGetVariance();
        NUM_SAMPLES = temp;
    }

    /**
     * Error function
     */
    public void testErrorFunction()
    {
        System.out.println( "ErrorFunction.evaluate" );
        double tol = 1e-6;

        // These values were computed from octave's erf() function
        assertEquals( 0.0, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( new Double(0.0) ), tol );
        assertEquals( 1.128342e-2, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( 0.01 ), tol );
        assertEquals( -1.128342e-2, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( -0.01 ), tol );
        assertEquals( 1.124629e-1, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( 0.1 ), tol );
        assertEquals( 2.227026e-1, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( 0.2 ), tol );
        assertEquals( 8.427008e-1, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( 1.0 ), tol );
        assertEquals( -8.427008e-1, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( -1.0 ), tol );
        assertEquals( 9.999779e-1, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( 3.0 ), tol );
        assertEquals( 1.0, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( 3.9 ), tol );
        assertEquals( -1.0, UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( -3.9 ), tol );
    }

    /**
     * ErrorFunction.Inverse.evaluate
     */
    public void testErrorFunctionInverse()
    {
        System.out.println( "ErrorFunction.Inverse.evaluate" );

        // These values were computes from octave's erfinv() function
        assertEquals( 0.0, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( new Double(0.0) ), TOLERANCE  );
        assertEquals( 0.0888559905, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.1 ), TOLERANCE  );
        assertEquals( 0.0888559905, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.1 ), TOLERANCE  );
        assertEquals( -0.0888559905, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -0.1 ), TOLERANCE  );
        assertEquals( 0.1791434546, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.2 ), TOLERANCE  );
        assertEquals( -0.1791434546, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -0.2 ), TOLERANCE  );
        assertEquals( 0.2724627147, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.3 ), TOLERANCE  );
        assertEquals( 0.4769362762, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.5 ), TOLERANCE  );
        assertEquals( 0.7328690780, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.7 ), TOLERANCE  );
        assertEquals( -0.7328690780, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -0.7 ), TOLERANCE  );
        assertEquals( -1.1630871537, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -0.9 ), TOLERANCE  );
        assertEquals( 1.1630871537, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.9 ), TOLERANCE  );
        assertEquals( 2.3267537655, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.999 ), TOLERANCE  );
        assertEquals( -2.3267537655, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -0.999 ), TOLERANCE  );
        assertEquals( 3.1234132743, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 0.99999 ), TOLERANCE  );
        assertEquals( -3.1234132743, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -0.99999 ), TOLERANCE  );

        assertEquals( Double.POSITIVE_INFINITY, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 1.0 ), TOLERANCE  );
        assertEquals( Double.POSITIVE_INFINITY, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 10.0 ), TOLERANCE  );
        assertEquals( Double.NEGATIVE_INFINITY, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -1.0 ), TOLERANCE  );
        assertEquals( Double.NEGATIVE_INFINITY, UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( -10.0 ), TOLERANCE  );

        for (int i = 0; i < 100; i++)
        {
            double y = RANDOM.nextDouble() * 2 - 1;
            double x = UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( y );
            double yhat = UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( x );
            assertEquals( y, yhat, TOLERANCE  );
        }
    }

    /**
     * CDF.Inverse
     */
    public void testCDFInverse()
    {
        System.out.println( "CDF.Inverse" );

        UnivariateGaussian instance = this.createInstance();
        UnivariateGaussian.CDF.Inverse cdfi =
            new UnivariateGaussian.CDF.Inverse( instance );
        assertNotSame( instance, cdfi );
        assertEquals( instance.convertToVector(), cdfi.convertToVector() );
        UnivariateGaussian clone = cdfi.clone();
        assertNotNull( clone );
        assertNotSame( cdfi, clone );
        assertEquals( cdfi.convertToVector(), clone.convertToVector() );

        UnivariateGaussian.CDF cdf = new UnivariateGaussian.CDF( cdfi );
        assertNotNull( cdf );
        assertNotSame( cdfi, cdf );
        assertEquals( cdfi.convertToVector(), cdf.convertToVector() );

        double p = RANDOM.nextDouble();
        double x = cdfi.evaluate(p);
        double phat = cdf.evaluate(x);
        assertEquals( p, phat, TOLERANCE );
        
        assertEquals( Double.NEGATIVE_INFINITY, cdfi.evaluate(0.0) );
        assertEquals( Double.POSITIVE_INFINITY, cdfi.evaluate(1.0) );

        assertEquals( Double.NEGATIVE_INFINITY, cdfi.evaluate(-1.0) );
        assertEquals( Double.POSITIVE_INFINITY, cdfi.evaluate(2.0) );
    }

    /**
     * Test of getMean method, of class gov.sandia.cognition.learning.util.statistics.UnivariateGaussian.
     */
    public void testGetMean()
    {
        System.out.println( "getMean" );

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        UnivariateGaussian.CDF instance = new UnivariateGaussian.CDF( mean, variance );
        assertEquals( mean, instance.getMean() );
    }

    /**
     * Test of setMean method, of class gov.sandia.cognition.learning.util.statistics.UnivariateGaussian.
     */
    public void testSetMean()
    {
        System.out.println( "setMean" );

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        UnivariateGaussian.PDF instance = new UnivariateGaussian.PDF( mean, variance );
        assertEquals( mean, instance.getMean() );

        double m2 = mean + RANDOM.nextGaussian();
        instance.setMean( m2 );
        assertEquals( m2, instance.getMean() );
    }

    /**
     * Test of getVariance method, of class gov.sandia.cognition.learning.util.statistics.UnivariateGaussian.
     */
    public void testGetVariance()
    {
        System.out.println( "getVariance" );

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        UnivariateGaussian instance = new UnivariateGaussian( mean, variance );
        assertEquals( variance, instance.getVariance() );
    }

    /**
     * Test of setVariance method, of class gov.sandia.cognition.learning.util.statistics.UnivariateGaussian.
     */
    public void testSetVariance()
    {
        System.out.println( "setVariance" );

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        UnivariateGaussian.PDF instance = new UnivariateGaussian.PDF( mean, variance );
        assertEquals( variance, instance.getVariance() );

        double v2 = variance + RANDOM.nextDouble();
        instance.setVariance( v2 );
        assertEquals( v2, instance.getVariance() );

        try
        {
            instance.setVariance( 0.0 );
            fail( "Variance must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setVariance( -RANDOM.nextDouble() );
            fail( "Variance must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );
        
        UnivariateGaussian instance = this.createInstance();
        Vector v = instance.convertToVector();
        assertEquals( 2, v.getDimensionality() );
        assertEquals( instance.getMean(), v.getElement( 0 ) );
        assertEquals( instance.getVariance(), v.getElement( 1 ) );
        
    }
    
    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.known values" );
        
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            UnivariateGaussian.PDF pdf = this.createInstance().getProbabilityFunction();
            double mean = pdf.getMean();
            double variance = pdf.getVariance();

            double x = RANDOM.nextGaussian();
            double p = pdf.evaluate( x );

            Double P = pdf.evaluate( new Double( x ) );
            assertEquals( p, P.doubleValue() );
            
            double delta = x - mean;
            double top = Math.exp( (delta * delta) / (-2.0 * variance) );
            double bottom = Math.sqrt( 2.0 * Math.PI * variance );
            double phat = top / bottom;
            assertEquals( p, phat, TOLERANCE );

        }
    }

    @Override
    public void testCDFKnownValues()
    {
        UnivariateGaussian.CDF cdf = this.createInstance().getCDF();
        double m1 = cdf.getMean();
        assertEquals( 0.5, cdf.evaluate( m1 ), TOLERANCE );
        assertEquals( 0.0, UnivariateGaussian.CDF.evaluate( Double.NEGATIVE_INFINITY, 0, 1 ), TOLERANCE );
        assertEquals( 1.0, UnivariateGaussian.CDF.evaluate( Double.POSITIVE_INFINITY, 0, 1 ), TOLERANCE );

        for (int i = 0; i < 100; i++)
        {
            double x = RANDOM.nextDouble() * 10 - 5;
            double expected = 0.5 * (1.0 + UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( x / Math.sqrt( 2 ) ));
            assertEquals( expected, UnivariateGaussian.CDF.evaluate( x, 0, 1 ), TOLERANCE );
        }
    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        UnivariateGaussian.MaximumLikelihoodEstimator learner =
            new UnivariateGaussian.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);
    }

    /**
     * Weighted learner
     */
    public void testWeightedLearner()
    {
        System.out.println( "Weighted Learner" );

        UnivariateGaussian.WeightedMaximumLikelihoodEstimator learner =
            new UnivariateGaussian.WeightedMaximumLikelihoodEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructor" );
        UnivariateGaussian.PDF g = new UnivariateGaussian.PDF();
        assertEquals( UnivariateGaussian.DEFAULT_MEAN, g.getMean() );
        assertEquals( UnivariateGaussian.DEFAULT_VARIANCE, g.getVariance() );

        double mean = RANDOM.nextGaussian();
        double variance = Math.abs(RANDOM.nextGaussian());
        g = new UnivariateGaussian.PDF( mean, variance );

        UnivariateGaussian.PDF g2 = new UnivariateGaussian.PDF( g );
        assertEquals( g.getMean(), g2.getMean() );
        assertEquals( g.getVariance(), g2.getVariance() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructor" );
        UnivariateGaussian g = new UnivariateGaussian();
        assertEquals( UnivariateGaussian.DEFAULT_MEAN, g.getMean() );
        assertEquals( UnivariateGaussian.DEFAULT_VARIANCE, g.getVariance() );

        double mean = RANDOM.nextGaussian();
        double variance = Math.abs(RANDOM.nextGaussian());
        g = new UnivariateGaussian( mean, variance );

        UnivariateGaussian g2 = new UnivariateGaussian( g );
        assertEquals( g.getMean(), g2.getMean() );
        assertEquals( g.getVariance(), g2.getVariance() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructor" );

        UnivariateGaussian.CDF g = new UnivariateGaussian.CDF();
        assertEquals( UnivariateGaussian.DEFAULT_MEAN, g.getMean() );
        assertEquals( UnivariateGaussian.DEFAULT_VARIANCE, g.getVariance() );

        double mean = RANDOM.nextGaussian();
        double variance = Math.abs(RANDOM.nextGaussian());
        g = new UnivariateGaussian.CDF( mean, variance );

        UnivariateGaussian.CDF g2 = new UnivariateGaussian.CDF( g );
        assertEquals( g.getMean(), g2.getMean() );
        assertEquals( g.getVariance(), g2.getVariance() );
    }

    /**
     * times
     */
    public void testTimes()
    {
        System.out.println( "times" );

        UnivariateGaussian g1 = this.createInstance();
        UnivariateGaussian g2 = this.createInstance();
        UnivariateGaussian result = g1.times( g2 );

        System.out.println( "G1: " + g1 );
        System.out.println( "G2: " + g2 );
        System.out.println( "Result: " + result );
        double vhat = 1.0 / (1.0/g1.getVariance() + 1.0/g2.getVariance());
        double meanHat = (vhat/g1.getVariance())*g1.getMean() + (vhat/g2.getVariance())*g2.getMean();
        assertEquals( meanHat, result.getMean(), TOLERANCE );
        assertEquals( vhat, result.getVariance(), TOLERANCE );

    }

    /**
     * convolve
     */
    public void testConvolve()
    {
        System.out.println( "convolve" );

        UnivariateGaussian g1 = this.createInstance();
        UnivariateGaussian g2 = this.createInstance();
        UnivariateGaussian result = g1.convolve( g2 );

        System.out.println( "G1: " + g1 );
        System.out.println( "G2: " + g2 );
        System.out.println( "Result: " + result );
        double vhat = g1.getVariance() + g2.getVariance();
        double meanHat = g1.getMean() + g2.getMean();
        assertEquals( meanHat, result.getMean(), TOLERANCE );
        assertEquals( vhat, result.getVariance(), TOLERANCE );

    }

    /**
     * tests the incremental estimator
     */
    public void testIncrementalEstimator()
    {
        System.out.println( "Incremental Estimator" );

        UnivariateGaussian.IncrementalEstimator estimator =
            new UnivariateGaussian.IncrementalEstimator();

        UnivariateGaussian target = new UnivariateGaussian(
            RANDOM.nextGaussian(), RANDOM.nextDouble() );
        ArrayList<Double> samples = target.sample(RANDOM,NUM_SAMPLES);


        double mean = UnivariateStatisticsUtil.computeMean(samples);
        UnivariateGaussian.SufficientStatistic ss = estimator.learn(samples);
        assertEquals( samples.size(), ss.getCount() );
        assertEquals( mean, ss.getMean(), TOLERANCE );
        assertEquals( UnivariateStatisticsUtil.computeVariance(samples, mean), ss.getVariance(), TOLERANCE );
        UnivariateGaussian result = ss.create();

        UnivariateGaussian batch =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(samples, 0.0);

        System.out.println( "Target: " + target );
        System.out.println( "Result: " + result );
        System.out.println( "Batch : " + batch );

        assertEquals( batch.getMean(), result.getMean(), TOLERANCE );
        assertEquals( batch.getVariance(), result.getVariance(), TOLERANCE );

        UnivariateGaussian.SufficientStatistic clone = ss.clone();
        assertEquals( ss.getCount(), clone.getCount() );
        assertEquals( ss.getMean(), clone.getMean() );
        assertEquals( ss.getVariance(), clone.getVariance() );
        
        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        double epsilon = 1E-10;
        estimator.setDefaultVariance(epsilon);
        Collection<Double> xd = Arrays.asList(4.0, 7.0, 13.0, 16.0);
        ss = estimator.createInitialLearnedObject();
        ss = estimator.learn(xd);
        result = ss.create();
        assertEquals(10.0, result.getMean(), epsilon);
        assertEquals(30.0, result.getVariance(), epsilon);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        xd = Arrays.asList(1.0e9 + 4.0, 1e9 + 7, 1e9 + 13, 1e9 + 16);
        ss = estimator.createInitialLearnedObject();
        ss = estimator.learn(xd);
        result = ss.create();
        assertEquals(1e9 + 10.0, result.getMean(), epsilon);
        assertEquals(30.0, result.getVariance(), epsilon);
    }


    /**
     * Tests adding together sufficient statistics
     */
    public void testSufficientStatisticsPlus()
    {
        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        double epsilon = 1E-10;
        List<Double> xd = Arrays.asList(4.0, 7.0, 13.0, 16.0);
        UnivariateGaussian.SufficientStatistic instance =
            new UnivariateGaussian.SufficientStatistic();
        int count = 1;
        int i = 0;
        while (i < xd.size())
        {
            UnivariateGaussian.SufficientStatistic other =
                new UnivariateGaussian.SufficientStatistic();
            for (int j = 0; j < count && i < xd.size(); j++)
            {
                other.update(xd.get(i));
                i++;
            }
            instance.plusEquals(other);
            count++;
        }
        assertEquals(10.0, instance.getMean(), epsilon);
        assertEquals(30.0, instance.getVariance(), epsilon);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        xd = Arrays.asList(1.0e9 + 4.0, 1e9 + 7, 1e9 + 13, 1e9 + 16);
        instance.clear();
        count = 1;
        i = 0;
        while (i < xd.size())
        {
            UnivariateGaussian.SufficientStatistic other =
                new UnivariateGaussian.SufficientStatistic();
            for (int j = 0; j < count && i < xd.size(); j++)
            {
                other.update(xd.get(i));
                i++;
            }
            instance.plusEquals(other);
            count++;
        }
        assertEquals(1e9 + 10.0, instance.getMean(), epsilon);
        assertEquals(30.0, instance.getVariance(), epsilon);

        UnivariateGaussian target = new UnivariateGaussian(
            RANDOM.nextGaussian(), RANDOM.nextDouble() );
        ArrayList<Double> samples = target.sample(RANDOM,NUM_SAMPLES);
        instance.clear();
        count = 1;
        i = 0;
        while (i < samples.size())
        {
            UnivariateGaussian.SufficientStatistic other =
                new UnivariateGaussian.SufficientStatistic();
            for (int j = 0; j < count && i < samples.size(); j++)
            {
                other.update(samples.get(i));
                i++;
            }
            instance.plusEquals(other);
            count++;
        }

        double mean = UnivariateStatisticsUtil.computeMean(samples);
        double variance = UnivariateStatisticsUtil.computeVariance(samples, mean);
        assertEquals(mean, instance.getMean(), epsilon);
        assertEquals(variance, instance.getVariance(), epsilon);

    }

}
