/*
 * File:                ExponentialDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 29, 2009, Sandia Corporation.
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
 * Unit tests for ExponentialDistributionTest.
 *
 * @author krdixon
 */
public class ExponentialDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Tests for class ExponentialDistributionTest.
     * @param testName Name of the test.
     */
    public ExponentialDistributionTest(
        String testName)
    {
        super(testName);
        MONTE_CARLO_FACTOR = 10;
    }


    /**
     * Tests the constructors of class ExponentialDistributionTest.
     */
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        ExponentialDistribution instance = new ExponentialDistribution();
        assertEquals( ExponentialDistribution.DEFAULT_RATE, instance.getRate() );

        double rate = RANDOM.nextDouble();
        instance = new ExponentialDistribution( rate );
        assertEquals( rate, instance.getRate() );

        ExponentialDistribution d2 = new ExponentialDistribution( instance );
        assertNotSame( d2, instance );
        assertEquals( instance.getRate(), d2.getRate() );
    }

    /**
     * Tests the constructors of class ExponentialDistributionTest.
     */
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );

        ExponentialDistribution.PDF instance = new ExponentialDistribution.PDF();
        assertEquals( ExponentialDistribution.DEFAULT_RATE, instance.getRate() );

        double rate = RANDOM.nextDouble();
        instance = new ExponentialDistribution.PDF( rate );
        assertEquals( rate, instance.getRate() );

        ExponentialDistribution.PDF d2 = new ExponentialDistribution.PDF( instance );
        assertNotSame( d2, instance );
        assertEquals( instance.getRate(), d2.getRate() );
    }

    /**
     * Tests the constructors of class ExponentialDistributionTest.
     */
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        ExponentialDistribution.CDF instance = new ExponentialDistribution.CDF();
        assertEquals( ExponentialDistribution.DEFAULT_RATE, instance.getRate() );

        double rate = RANDOM.nextDouble();
        instance = new ExponentialDistribution.CDF( rate );
        assertEquals( rate, instance.getRate() );

        ExponentialDistribution.CDF d2 = new ExponentialDistribution.CDF( instance );
        assertNotSame( d2, instance );
        assertEquals( instance.getRate(), d2.getRate() );
    }

    /**
     * Test of getRate method, of class ExponentialDistribution.
     */
    public void testGetRate()
    {
        System.out.println("getRate");
        ExponentialDistribution instance = this.createInstance();
        assertTrue( instance.getRate() > 0.0 );
    }

    /**
     * Test of setRate method, of class ExponentialDistribution.
     */
    public void testSetRate()
    {
        System.out.println("setRate");
        ExponentialDistribution instance = this.createInstance();
        double rate = instance.getRate();
        rate *= 2.0;
        instance.setRate(rate);
        assertEquals( rate, instance.getRate() );

        try
        {
            instance.setRate(0.0);
            fail( "Rate must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }


    @Override
    public ExponentialDistribution createInstance()
    {
        double rate = Math.abs( RANDOM.nextGaussian() ) + RANDOM.nextDouble();
        return new ExponentialDistribution( rate );
    }

    @Override
    public void testPDFKnownValues()
    {
        ExponentialDistribution.PDF pdf = new ExponentialDistribution.PDF( 1.0/4.0 );
        assertEquals( 0.071626199, pdf.evaluate(5.0), TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        double rate = RANDOM.nextDouble();
        ExponentialDistribution instance = new ExponentialDistribution( rate );
        Vector p = instance.convertToVector();
        assertEquals( 1, p.getDimensionality() );
        assertEquals( rate, p.getElement(0) );
    }

    @Override
    public void testCDFKnownValues()
    {
        ExponentialDistribution.CDF cdf = new ExponentialDistribution.CDF( 1.0/4.0 );
        assertEquals( 1.0-Math.exp(-5.0/4.0), cdf.evaluate(5.0), TOLERANCE );
    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        ExponentialDistribution.MaximumLikelihoodEstimator learner =
            new ExponentialDistribution.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);
    }

    /**
     * Weighted learner
     */
    public void testWeightedLearner()
    {
        System.out.println( "Weighted Learner" );

        ExponentialDistribution.WeightedMaximumLikelihoodEstimator learner =
            new ExponentialDistribution.WeightedMaximumLikelihoodEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }

}
