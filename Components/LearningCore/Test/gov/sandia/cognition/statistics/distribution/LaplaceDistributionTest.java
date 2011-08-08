/*
 * File:                LaplaceDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;

/**
 * Unit tests for LaplaceDistributionTest.
 *
 * @author krdixon
 */
public class LaplaceDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Test name
     * @param testName name
     */
    public LaplaceDistributionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getMean method, of class LaplaceDistribution.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        LaplaceDistribution instance = new LaplaceDistribution.PDF();
        assertEquals( LaplaceDistribution.DEFAULT_MEAN, instance.getMean() );
        instance = new LaplaceDistribution.CDF();
        assertEquals( LaplaceDistribution.DEFAULT_MEAN, instance.getMean() );

        double mean = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new LaplaceDistribution.PDF( mean, scale );
        assertEquals( mean, instance.getMean() );
    }

    /**
     * Test of setMean method, of class LaplaceDistribution.
     */
    public void testSetMean()
    {
        System.out.println("setMean");
        double mean = RANDOM.nextGaussian();
        LaplaceDistribution instance =
            new LaplaceDistribution.CDF( this.createInstance() );
        instance.setMean(mean);
        assertEquals( mean, instance.getMean() );
    }

    /**
     * Test of getScale method, of class LaplaceDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        LaplaceDistribution instance = new LaplaceDistribution.CDF();
        assertEquals( LaplaceDistribution.DEFAULT_SCALE, instance.getScale() );
        instance = new LaplaceDistribution.PDF();
        assertEquals( LaplaceDistribution.DEFAULT_SCALE, instance.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new LaplaceDistribution.PDF( mean, scale );
        assertEquals( scale, instance.getScale() );

    }

    /**
     * Test of setScale method, of class LaplaceDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        LaplaceDistribution instance = this.createInstance();

        double scale = RANDOM.nextDouble() * 10.0;
        instance.setScale(scale);
        assertEquals( scale, instance.getScale() );

        try
        {
            instance.setScale(0.0);
            fail( "scale must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.knownValues" );

        LaplaceDistribution.PDF pdf = new LaplaceDistribution.PDF( 2.0, 4.0 );

        assertEquals( 0.035813, pdf.evaluate(-3.0), TOLERANCE );
        assertEquals( 0.097350, pdf.evaluate( 3.0), TOLERANCE );
    }

    @Override
    public LaplaceDistribution createInstance()
    {
        return new LaplaceDistribution(
            RANDOM.nextGaussian(), RANDOM.nextDouble()*5.0 );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );

        LaplaceDistribution.CDF cdf = this.createInstance().getCDF();
        Vector parameters = cdf.convertToVector();
        assertEquals( 2, parameters.getDimensionality() );
        assertEquals( cdf.getMean(), parameters.getElement(0) );
        assertEquals( cdf.getScale(), parameters.getElement(1) );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.knownValues" );

        LaplaceDistribution.CDF cdf = this.createInstance().getCDF();
        assertEquals( 0.5, cdf.evaluate(cdf.getMean()), TOLERANCE );
        cdf = new LaplaceDistribution.CDF( 2.0, 4.0 );
        assertEquals( 0.610600, cdf.evaluate( 3.0), TOLERANCE );
        assertEquals( 0.143252, cdf.evaluate(-3.0), TOLERANCE );
    }

    /**
     * Test of toString
     */
    public void testToString()
    {
        System.out.println( "toString" );

        LaplaceDistribution laplace = this.createInstance();
        String s = laplace.toString();
        System.out.println( "Laplace: " + s );
        assertNotNull( s );
        assertTrue( s.length() > 0 );
    }

    /**
     * Test of learner
     */
    public void testLearner()
    {
        System.out.println( "LaplaceDistribution.Learner" );

        LaplaceDistribution.MaximumLikelihoodEstimator learner =
            new LaplaceDistribution.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);
        
    }

    /**
     * Test of learner
     */
    public void testWeightedLearner()
    {
        System.out.println( "LaplaceDistribution.Learner" );

        LaplaceDistribution.WeightedMaximumLikelihoodEstimator learner =
            new LaplaceDistribution.WeightedMaximumLikelihoodEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );

        LaplaceDistribution.PDF d = new LaplaceDistribution.PDF();
        assertEquals( LaplaceDistribution.DEFAULT_MEAN, d.getMean() );
        assertEquals( LaplaceDistribution.DEFAULT_SCALE, d.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = Math.abs( RANDOM.nextGaussian() );
        d = new LaplaceDistribution.PDF( mean, scale );
        assertEquals( mean, d.getMean() );
        assertEquals( scale, d.getScale() );

        LaplaceDistribution.PDF d2 = new LaplaceDistribution.PDF( d );
        assertEquals( d.getMean(), d2.getMean() );
        assertEquals( d.getScale(), d2.getScale() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        LaplaceDistribution d = new LaplaceDistribution();
        assertEquals( LaplaceDistribution.DEFAULT_MEAN, d.getMean() );
        assertEquals( LaplaceDistribution.DEFAULT_SCALE, d.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = Math.abs( RANDOM.nextGaussian() );
        d = new LaplaceDistribution( mean, scale );
        assertEquals( mean, d.getMean() );
        assertEquals( scale, d.getScale() );

        LaplaceDistribution d2 = new LaplaceDistribution( d );
        assertEquals( d.getMean(), d2.getMean() );
        assertEquals( d.getScale(), d2.getScale() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        LaplaceDistribution.CDF d = new LaplaceDistribution.CDF();
        assertEquals( LaplaceDistribution.DEFAULT_MEAN, d.getMean() );
        assertEquals( LaplaceDistribution.DEFAULT_SCALE, d.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = Math.abs( RANDOM.nextGaussian() );
        d = new LaplaceDistribution.CDF( mean, scale );
        assertEquals( mean, d.getMean() );
        assertEquals( scale, d.getScale() );

        LaplaceDistribution.CDF d2 = new LaplaceDistribution.CDF( d );
        assertEquals( d.getMean(), d2.getMean() );
        assertEquals( d.getScale(), d2.getScale() );
    }

}
