/*
 * File:                LogNormalDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 */
public class LogNormalDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Constructor
     * @param testName name
     */
    public LogNormalDistributionTest(
        String testName )
    {
        super( testName );
        RANDOM = new Random(10);
    }

    @Override
    public LogNormalDistribution createInstance()
    {
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        return new LogNormalDistribution( mean, variance );
    }
    
    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.evaluate" );

        // These were found using the octave command lognormal_pdf(x,exp(mean),variance)
        // (Note that their mean has an exp() around it...
        assertEquals( 0.2419707245, LogNormalDistribution.PDF.evaluate( 1.0, 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.0539909665, LogNormalDistribution.PDF.evaluate( 1.0, 2.0, 1.0 ), TOLERANCE );
        assertEquals( 0.1182550739, LogNormalDistribution.PDF.evaluate( 1.0, 2.0, 3.0 ), TOLERANCE );
        assertEquals( 0.0540788845, LogNormalDistribution.PDF.evaluate( 4.0, 2.0, 3.0 ), TOLERANCE );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "PDF.CDF.evaluate" );

        assertEquals( 0.1586552539, LogNormalDistribution.CDF.evaluate( 1.0, 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.5392769437, LogNormalDistribution.CDF.evaluate( 3.0, 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.1836911064, LogNormalDistribution.CDF.evaluate( 3.0, 2.0, 1.0 ), TOLERANCE );
        assertEquals( 0.3261051057, LogNormalDistribution.CDF.evaluate( 3.0, 2.0, 4.0 ), TOLERANCE );

    }

    /**
     * Test of getLogNormalMean method, of class gov.sandia.cognition.statistics.distribution.LogNormalDistribution.
     */
    public void testGetLogNormalMean()
    {
        System.out.println( "getLogNormalMean" );

        double m = RANDOM.nextGaussian();
        double v = RANDOM.nextDouble();
        LogNormalDistribution instance = new LogNormalDistribution( m, v );
        assertEquals( m, instance.getLogNormalMean() );

    }

    /**
     * Test of setLogNormalMean method, of class gov.sandia.cognition.statistics.distribution.LogNormalDistribution.
     */
    public void testSetLogNormalMean()
    {
        System.out.println( "setLogNormalMean" );

        double m = RANDOM.nextGaussian();
        double v = RANDOM.nextDouble();
        LogNormalDistribution instance = new LogNormalDistribution( m, v );
        assertEquals( m, instance.getLogNormalMean() );

        double m2 = m + 1;
        instance.setLogNormalMean( m2 );
        assertEquals( m2, instance.getLogNormalMean() );
    }

    /**
     * Test of getLogNormalVariance method, of class gov.sandia.cognition.statistics.distribution.LogNormalDistribution.
     */
    public void testGetLogNormalVariance()
    {
        System.out.println( "getLogNormalVariance" );

        double m = RANDOM.nextGaussian();
        double v = RANDOM.nextDouble();
        LogNormalDistribution.PDF instance = new LogNormalDistribution.PDF( m, v );
        assertEquals( v, instance.getLogNormalVariance() );
    }

    /**
     * Test of setLogNormalVariance method, of class gov.sandia.cognition.statistics.distribution.LogNormalDistribution.
     */
    public void testSetLogNormalVariance()
    {
        System.out.println( "setLogNormalVariance" );

        double m = RANDOM.nextGaussian();
        double v = RANDOM.nextDouble();
        LogNormalDistribution.PDF instance = new LogNormalDistribution.PDF( m, v );
        assertEquals( v, instance.getLogNormalVariance() );

        double v2 = v + 1;
        instance.setLogNormalVariance( v2 );
        assertEquals( v2, instance.getLogNormalVariance() );

        try
        {
            instance.setLogNormalVariance( 0.0 );
            fail( "logNormalVariance must be > 0.0" );
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
        
        LogNormalDistribution f = this.createInstance();
        Vector x = f.convertToVector();
        assertEquals( 2, x.getDimensionality() );
        assertEquals( f.getLogNormalMean(), x.getElement( 0 ) );
        assertEquals( f.getLogNormalVariance(), x.getElement( 1 ) );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructor" );

        LogNormalDistribution.PDF d = new LogNormalDistribution.PDF();
        assertEquals( LogNormalDistribution.DEFAULT_LOG_NORMAL_MEAN, d.getLogNormalMean() );
        assertEquals( LogNormalDistribution.DEFAULT_LOG_NORMAL_VARIANCE, d.getLogNormalVariance() );

        double m = RANDOM.nextGaussian();
        double v = Math.abs( RANDOM.nextGaussian() );
        d = new LogNormalDistribution.PDF( m, v );
        assertEquals( m, d.getLogNormalMean() );
        assertEquals( v, d.getLogNormalVariance() );

        LogNormalDistribution.PDF d2 = new LogNormalDistribution.PDF( d );
        assertEquals( d.getLogNormalMean(), d2.getLogNormalMean() );
        assertEquals( d.getLogNormalVariance(), d2.getLogNormalVariance() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructor" );

        LogNormalDistribution d = new LogNormalDistribution();
        assertEquals( LogNormalDistribution.DEFAULT_LOG_NORMAL_MEAN, d.getLogNormalMean() );
        assertEquals( LogNormalDistribution.DEFAULT_LOG_NORMAL_VARIANCE, d.getLogNormalVariance() );

        double m = RANDOM.nextGaussian();
        double v = Math.abs( RANDOM.nextGaussian() );
        d = new LogNormalDistribution( m, v );
        assertEquals( m, d.getLogNormalMean() );
        assertEquals( v, d.getLogNormalVariance() );

        LogNormalDistribution d2 = new LogNormalDistribution( d );
        assertEquals( d.getLogNormalMean(), d2.getLogNormalMean() );
        assertEquals( d.getLogNormalVariance(), d2.getLogNormalVariance() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructor" );

        LogNormalDistribution.CDF d = new LogNormalDistribution.CDF();
        assertEquals( LogNormalDistribution.DEFAULT_LOG_NORMAL_MEAN, d.getLogNormalMean() );
        assertEquals( LogNormalDistribution.DEFAULT_LOG_NORMAL_VARIANCE, d.getLogNormalVariance() );

        double m = RANDOM.nextGaussian();
        double v = Math.abs( RANDOM.nextGaussian() );
        d = new LogNormalDistribution.CDF( m, v );
        assertEquals( m, d.getLogNormalMean() );
        assertEquals( v, d.getLogNormalVariance() );

        LogNormalDistribution.CDF d2 = new LogNormalDistribution.CDF( d );
        assertEquals( d.getLogNormalMean(), d2.getLogNormalMean() );
        assertEquals( d.getLogNormalVariance(), d2.getLogNormalVariance() );
    }

    /**
     * MaximumLikelihoodEstimator
     */
    public void testMaximumLikelihoodEstimator()
    {
        System.out.println( "MaximumLikelihoodEstimator" );

        LogNormalDistribution.MaximumLikelihoodEstimator learner =
            new LogNormalDistribution.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);

    }

    /**
     * Weighted learner
     */
    public void testWeightedMaximumLikelihoodEstimator()
    {
        System.out.println( "WeightedMaximumLikelihoodEstimator"  );

        LogNormalDistribution.WeightedMaximumLikelihoodEstimator learner =
            new LogNormalDistribution.WeightedMaximumLikelihoodEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }

}
