/*
 * File:                BetaDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothUnivariateDistributionTestHarness;

/**
 *
 * @author Kevin R. Dixon
 */
public class BetaDistributionTest
    extends SmoothUnivariateDistributionTestHarness
{

    /**
     * Test
     * @param testName name
     */
    public BetaDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public BetaDistribution createInstance()
    {
        double alpha = RANDOM.nextDouble() * 5;
        double beta = RANDOM.nextDouble() * 5;
        return new BetaDistribution( alpha, beta );
    }    

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Default constructor" );

        BetaDistribution instance = new BetaDistribution();
        assertEquals( BetaDistribution.DEFAULT_ALPHA, instance.getAlpha() );
        assertEquals( BetaDistribution.DEFAULT_BETA, instance.getBeta() );

        double a = RANDOM.nextDouble();
        double b = RANDOM.nextDouble();
        instance = new BetaDistribution( a, b );
        assertEquals( a, instance.getAlpha() );
        assertEquals( b, instance.getBeta() );

        BetaDistribution d2 = new BetaDistribution( instance );
        assertEquals( instance.getAlpha(), d2.getAlpha() );
        assertEquals( instance.getBeta(), d2.getBeta() );
    }


    /**
     * Test of getAlpha method, of class gov.sandia.cognition.learning.util.statistics.distribution.BetaDistribution.
     */
    public void testGetAlpha()
    {
        System.out.println( "getAlpha" );

        double a = RANDOM.nextDouble();
        double b = RANDOM.nextDouble();
        BetaDistribution instance = new BetaDistribution( a, b );
        assertEquals( a, instance.getAlpha() );
    }

    /**
     * Test of setAlpha method, of class gov.sandia.cognition.learning.util.statistics.distribution.BetaDistribution.
     */
    public void testSetAlpha()
    {
        System.out.println( "setAlpha" );

        BetaDistribution.PDF instance = this.createInstance().getProbabilityFunction();
        assertTrue( instance.getAlpha() > 0.0 );

        double v = instance.getAlpha() + 1.0;
        instance.setAlpha( v );
        assertEquals( v, instance.getAlpha() );

        try
        {
            instance.setAlpha( 0.0 );
            fail( "Alpha must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getBeta method, of class gov.sandia.cognition.learning.util.statistics.distribution.BetaDistribution.
     */
    public void testGetBeta()
    {
        System.out.println( "getBeta" );

        double a = RANDOM.nextDouble();
        double b = RANDOM.nextDouble();
        BetaDistribution instance = new BetaDistribution( a, b );
        assertEquals( b, instance.getBeta() );

    }

    /**
     * Test of setBeta method, of class gov.sandia.cognition.learning.util.statistics.distribution.BetaDistribution.
     */
    public void testSetBeta()
    {
        System.out.println( "setBeta" );

        BetaDistribution.PDF instance = this.createInstance().getProbabilityFunction();
        assertTrue( instance.getBeta() > 0.0 );

        double v = instance.getBeta() + 1.0;
        instance.setBeta( v );
        assertEquals( v, instance.getBeta() );

        try
        {
            instance.setBeta( 0.0 );
            fail( "Beta must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.evaluate" );

        // I got these values from beta_pdf() in octave
        assertEquals( 0.6366197724, BetaDistribution.PDF.evaluate( 0.5, 0.5, 0.5 ), TOLERANCE );
        assertEquals( 1.0610329539, BetaDistribution.PDF.evaluate( 0.1, 0.5, 0.5 ), TOLERANCE );
        assertEquals( 1.1599612085, BetaDistribution.PDF.evaluate( 0.1, 0.3, 0.5 ), TOLERANCE );
        assertEquals( 1.5360000000, BetaDistribution.PDF.evaluate( 0.2, 2.0, 3.0 ), TOLERANCE );

        assertEquals( 0.0, BetaDistribution.PDF.evaluate( -1.0, 1.0, 2.0 ) );
        assertEquals( 0.0, BetaDistribution.PDF.evaluate(  2.0, 1.0, 2.0 ) );

    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.evaluate" );

        // I got these values from octave's beta_cdf() function
        assertEquals( 0.5000000000, BetaDistribution.CDF.evaluate( 0.5, 0.5, 0.5 ), TOLERANCE );
        assertEquals( 0.7048327647, BetaDistribution.CDF.evaluate( 0.8, 0.5, 0.5 ), TOLERANCE );
        assertEquals( 0.3739009663, BetaDistribution.CDF.evaluate( 0.8, 2.0, 0.5 ), TOLERANCE );
        assertEquals( 0.8032260180, BetaDistribution.CDF.evaluate( 0.8, 2.0, 1.5 ), TOLERANCE );
        assertEquals( 0.0181127865, BetaDistribution.CDF.evaluate( 0.1, 2.0, 1.5 ), TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );
        
        BetaDistribution instance = this.createInstance();
        Vector x = instance.convertToVector();
        assertEquals( 2, x.getDimensionality() );
        assertEquals( instance.getAlpha(), x.getElement( 0 ) );
        assertEquals( instance.getBeta(), x.getElement( 1 ) );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );
        BetaDistribution.PDF instance = new BetaDistribution.PDF();
        assertEquals( BetaDistribution.DEFAULT_ALPHA, instance.getAlpha() );
        assertEquals( BetaDistribution.DEFAULT_BETA, instance.getBeta() );

        double a = RANDOM.nextDouble();
        double b = RANDOM.nextDouble();
        instance = new BetaDistribution.PDF( a, b );
        assertEquals( a, instance.getAlpha() );
        assertEquals( b, instance.getBeta() );

        BetaDistribution.PDF d2 = new BetaDistribution.PDF( instance );
        assertEquals( instance.getAlpha(), d2.getAlpha() );
        assertEquals( instance.getBeta(), d2.getBeta() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );
        BetaDistribution.CDF instance = new BetaDistribution.CDF();
        assertEquals( BetaDistribution.DEFAULT_ALPHA, instance.getAlpha() );
        assertEquals( BetaDistribution.DEFAULT_BETA, instance.getBeta() );

        double a = RANDOM.nextDouble();
        double b = RANDOM.nextDouble();
        instance = new BetaDistribution.CDF( a, b );
        assertEquals( a, instance.getAlpha() );
        assertEquals( b, instance.getBeta() );

        BetaDistribution.CDF d2 = new BetaDistribution.CDF( instance );
        assertEquals( instance.getAlpha(), d2.getAlpha() );
        assertEquals( instance.getBeta(), d2.getBeta() );
    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        BetaDistribution.MomentMatchingEstimator learner =
            new BetaDistribution.MomentMatchingEstimator();
        this.distributionEstimatorTest(learner);
    }

    /**
     * Weighted learner
     */
    public void testWeightedLearner()
    {
        System.out.println( "Weighted Learner" );

        BetaDistribution.WeightedMomentMatchingEstimator learner =
            new BetaDistribution.WeightedMomentMatchingEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }
}
