/*
 * File:                AdaptiveRejectionSamplingTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 5, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.io.IOException;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for AdaptiveRejectionSamplingTest.
 *
 * @author krdixon
 */
public class AdaptiveRejectionSamplingTest
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
     * Number of samples to draw.
     */
    public int NUM_SAMPLES = 1000;

    /**
     * Tests for class AdaptiveRejectionSamplingTest.
     * @param testName Name of the test.
     */
    public AdaptiveRejectionSamplingTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Creates an instance
     * @return
     * Instance
     */
    public AdaptiveRejectionSampling createInstance()
    {
        AdaptiveRejectionSampling instance = new AdaptiveRejectionSampling();
        BetaDistribution.PDF f = new BetaDistribution.PDF( 2, 2 );
        instance.initialize( new AdaptiveRejectionSampling.PDFLogEvaluator(f), 0.0, 1.0, 0.2, 0.5, 0.6 );
        return instance;
    }

    /**
     * Tests the constructors of class AdaptiveRejectionSamplingTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        AdaptiveRejectionSampling instance = new AdaptiveRejectionSampling();
        assertNotNull( instance );
    }

    /**
     * Test of clone method, of class AdaptiveRejectionSampling.
     */
    public void testClone()
    {
        System.out.println("clone");
        AdaptiveRejectionSampling instance = this.createInstance();
        AdaptiveRejectionSampling clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getLogFunction(), clone.getLogFunction() );
        assertNotSame( instance.upperEnvelope, clone.upperEnvelope );
        assertNotSame( instance.lowerEnvelope, clone.lowerEnvelope );
        assertEquals( instance.getMaxNumPoints(), clone.getMaxNumPoints() );
        assertEquals( instance.getMinSupport(), clone.getMinSupport() );
        assertEquals( instance.getMaxSupport(), clone.getMaxSupport() );
        assertNotSame( instance.getPoints(), clone.getPoints() );
        assertEquals( instance.getPoints().size(), clone.getPoints().size() );

        Random r1 = new Random( 10 );
        Random r2 = new Random( 10 );

        ArrayList<Double> s1 = instance.sample(r1, NUM_SAMPLES);
        ArrayList<Double> s2 = clone.sample(r2, NUM_SAMPLES);
        assertEquals( s1.size(), s2.size() );
        for( int i = 0; i < s1.size(); i++ )
        {
            assertEquals( s1.get(i), s2.get(i) );
        }
    }

    /**
     * Envelope
     */
    public void testEnvelope()
    {
        System.out.println( "Envelope" );

        AdaptiveRejectionSampling instance = new AdaptiveRejectionSampling();

        BetaDistribution.PDF f = new BetaDistribution.PDF( 2, 2 );
        instance.setMinSupport((double) f.getMinSupport());
        instance.setMaxSupport((double) f.getMaxSupport());
        double[] xs = { 0.8, 0.2, 0.55, 0.4 };
        for( int i = 0; i < xs.length; i++ )
        {
            double y = f.logEvaluate(xs[i]);
            instance.addPoint( xs[i], y );
        }

        double lastX = -1;
        AdaptiveRejectionSampling.UpperEnvelope ue = instance.new UpperEnvelope();
        AdaptiveRejectionSampling.LowerEnvelope le = instance.new LowerEnvelope();
        for( AdaptiveRejectionSampling.Point p : instance.getPoints() )
        {
            double x = p.getInput();
            double ly = ue.logEvaluate(x);
            System.out.printf( "%f < log(f(%f)) = %f < %f\n", le.logEvaluate(x), x, p.getOutput(), ly);
            assertTrue( lastX < x );
            lastX = x;
        }

        for( double x = 0.0; x <= 1.0; x += 0.01 )
        {
            double ly = ue.logEvaluate(x);
            double lb = f.logEvaluate(x);
            double lenv = le.logEvaluate(x);
            if( !Double.isInfinite(ly) )
            {
                assertTrue( lb-ly <= TOLERANCE );
            }
            if( !Double.isInfinite(lenv) )
            {
//                assertTrue( lenv-lb <= TOLERANCE );
            }
        }


    }

    /**
     * computeSegments
     */
    public void testComputeSegments()
    {
        System.out.println( "computeSegments" );

        AdaptiveRejectionSampling instance = new AdaptiveRejectionSampling();
        BetaDistribution.PDF f = new BetaDistribution.PDF( 2, 2 );
        instance.setMinSupport((double) f.getMinSupport());
        instance.setMaxSupport((double) f.getMaxSupport());
        double[] xs = { 0.8, 0.2, 0.55, 0.4, 0.3, 0.7 };
        for( int i = 0; i < xs.length; i++ )
        {
            double y = f.logEvaluate(xs[i]);
            instance.addPoint(xs[i], y);
        }

        AdaptiveRejectionSampling.UpperEnvelope ue = instance.upperEnvelope;
        AdaptiveRejectionSampling.LowerEnvelope le = instance.lowerEnvelope;
        ArrayList<AdaptiveRejectionSampling.LineSegment> segments = ue.getLines();
        for( int i = 0; i < segments.size(); i++ )
        {
            AdaptiveRejectionSampling.LineSegment segment = segments.get(i);
            double weight = ue.segmentCDF[i];
            System.out.printf( "y = %f*x+%f [%.3f,%.3f], int=%f\n",
                segment.getQ1(), segment.getQ0(), segment.left, segment.right, weight );
        }

    }

    /**
     * Sample beta
     * @throws IOException
     */
    public void testSampleBeta() throws IOException
    {
        System.out.println( "Sample Beta" );

        BetaDistribution.PDF f = new BetaDistribution.PDF( 2, 2 );


        long start = System.currentTimeMillis();
        AdaptiveRejectionSampling instance = new AdaptiveRejectionSampling();
        instance.setMaxNumPoints(10);
        instance.initialize( new AdaptiveRejectionSampling.PDFLogEvaluator(f),
            f.getMinSupport(), f.getMaxSupport(), 0.2, 0.5, 0.6);
        ArrayList<Double> samples = instance.sample(RANDOM, NUM_SAMPLES);
        long stop = System.currentTimeMillis();
        System.out.println( "ARS Time = " + (stop-start)/1000.0);
        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(samples,f.getCDF());
        System.out.println( "K-S test: " + kstest );
        samples = null;
        
        start = System.currentTimeMillis();
        samples = f.sample(RANDOM, NUM_SAMPLES);
        stop = System.currentTimeMillis();
        System.out.println( "Beta Time = " + (stop-start)/1000.0);

    }

    /**
     * Sample Gaussian
     */
    public void testSampleGaussian()
    {
        System.out.println( "sample Gaussian" );
        UnivariateGaussian.PDF f = new UnivariateGaussian.PDF( 0, 1 );


        long start = System.currentTimeMillis();
        AdaptiveRejectionSampling instance = new AdaptiveRejectionSampling();
        instance.setMaxNumPoints(10);
        instance.initialize( new AdaptiveRejectionSampling.PDFLogEvaluator(f),
            f.getMinSupport(), f.getMaxSupport(), -1.0, 0.0, 1.0);
        ArrayList<Double> samples = instance.sample(RANDOM, NUM_SAMPLES);
        long stop = System.currentTimeMillis();
        System.out.println( "ARS Time = " + (stop-start)/1000.0);
        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(samples,f.getCDF());
        System.out.println( "K-S test: " + kstest );
        samples = null;

        start = System.currentTimeMillis();
        samples = f.sample(RANDOM, NUM_SAMPLES);
        stop = System.currentTimeMillis();
        System.out.println( "Gaussian Time = " + (stop-start)/1000.0);

    }

    /**
     * Upper Envelope
     */
    public void testUpperEnvelope()
    {
        System.out.println( "Upper Envelope" );

        AdaptiveRejectionSampling instance = this.createInstance();
        AdaptiveRejectionSampling.UpperEnvelope ue = instance.upperEnvelope;
        double x = RANDOM.nextDouble();
        assertEquals( ue.evaluate(x), Math.exp(ue.logEvaluate(x) ));
        assertSame( ue, ue.getProbabilityFunction() );
        try
        {
            ue.getMean();
            fail( "Can't get mean" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

}
