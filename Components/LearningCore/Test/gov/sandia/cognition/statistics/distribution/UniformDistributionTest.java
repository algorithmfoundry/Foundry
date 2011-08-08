/*
 * File:                UniformDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2007, Sandia Corporation.  Under the terms of Contract
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
public class UniformDistributionTest
    extends SmoothUnivariateDistributionTestHarness
{

    /**
     * Constructor
     * @param testName name
     */
    public UniformDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public UniformDistribution createInstance()
    {
        double a = RANDOM.nextGaussian();
        double b = a + RANDOM.nextDouble() * 2.0;
        return new UniformDistribution( a, b );
    }    

    /**
     * Tests what happens when the min == max
     */
    public void testDegenerate()
    {
        System.out.println( "CDF.degenerate" );
        
        for( int i = 0; i < 100; i++ )
        {
            double x = RANDOM.nextGaussian();
            UniformDistribution.CDF instance = new UniformDistribution.CDF( x, x );
            assertEquals( x, instance.getMinSupport() );
            assertEquals( x, instance.getMaxSupport() );
            assertEquals( x, instance.getMean() );
            assertEquals( 0.0, instance.getVariance() );
            assertEquals( x, instance.sample( RANDOM ) );
        }
        
    }
    
    /**
     * Test of getMinSupport method, of class gov.sandia.cognition.learning.util.statistics.UniformDistribution.
     */
    public void testGetMinX()
    {
        System.out.println( "getMinX" );

        double a = RANDOM.nextGaussian();
        double b = a + 1;
        UniformDistribution instance = new UniformDistribution( a, b );
        assertEquals( a, instance.getMinSupport() );

    }

    /**
     * Test of setMinSupport method, of class gov.sandia.cognition.learning.util.statistics.UniformDistribution.
     */
    public void testSetMinX()
    {
        System.out.println( "setMinX" );

        double a = RANDOM.nextGaussian();
        double b = a + 1;
        UniformDistribution instance = new UniformDistribution( a, b );
        assertEquals( a, instance.getMinSupport() );

        instance.setMinSupport( b );
        assertEquals( b, instance.getMinSupport() );
    }

    /**
     * Test of getMaxSupport method, of class gov.sandia.cognition.learning.util.statistics.UniformDistribution.
     */
    public void testGetMaxX()
    {
        System.out.println( "getMaxX" );

        double a = RANDOM.nextGaussian();
        double b = a + 1;
        UniformDistribution instance = new UniformDistribution( a, b );
        assertEquals( b, instance.getMaxSupport() );

    }

    /**
     * Test of setMaxSupport method, of class gov.sandia.cognition.learning.util.statistics.UniformDistribution.
     */
    public void testSetMaxX()
    {
        System.out.println( "setMaxX" );

        double a = RANDOM.nextGaussian();
        double b = a + 1;
        UniformDistribution instance = new UniformDistribution( a, b );
        assertEquals( b, instance.getMaxSupport() );

        instance.setMaxSupport( a );
        assertEquals( a, instance.getMaxSupport() );
    }

    /**
     * Test of getMean method, of class gov.sandia.cognition.learning.util.statistics.UniformDistribution.
     */
    public void testGetMean()
    {
        System.out.println( "PDF.getMean" );

        UniformDistribution.PDF instance = this.createInstance().getProbabilityFunction();
        assertEquals( 0.5 * (instance.getMinSupport() + instance.getMaxSupport()), instance.getMean(), TOLERANCE );

    }

    /**
     * Test of getVariance method, of class gov.sandia.cognition.learning.util.statistics.UniformDistribution.
     */
    public void testGetVariance()
    {
        System.out.println( "PDF.getVariance" );

        UniformDistribution.PDF instance = this.createInstance().getProbabilityFunction();
        double d = instance.getMaxSupport() - instance.getMinSupport();
        assertEquals( d * d / 12.0, instance.getVariance(), TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );

        UniformDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( instance.getMinSupport(), p.getElement( 0 ) );
        assertEquals( instance.getMaxSupport(), p.getElement( 1 ) );
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.knownValues" );
        
        for (int i = 0; i < 100; i++)
        {
            UniformDistribution.PDF instance = this.createInstance().getProbabilityFunction();
            double a = instance.getMinSupport();
            double b = instance.getMaxSupport();
            double x = (RANDOM.nextDouble() * (b - a + 2)) + a - 1;
            double h = 1 / (b - a);
            double y;
            if (x < a)
            {
                y = 0;
            }
            else if (x > b)
            {
                y = 0;
            }
            else
            {
                y = h;
            }


            double yhat = instance.evaluate( x );
            assertEquals( y, yhat );

            assertEquals( h, instance.evaluate( a ) );
            assertEquals( h, instance.evaluate( b ) );
        }
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.knownValues" );
         for (int i = 0; i < 1000; i++)
        {
            UniformDistribution.CDF instance = this.createInstance().getCDF();
            double x = RANDOM.nextGaussian();
            double phat = instance.evaluate( x );
            double p;
            if (x < instance.getMinSupport())
            {
                p = 0.0;
            }
            else if (x > instance.getMaxSupport())
            {
                p = 1.0;
            }
            else
            {
                p = (x - instance.getMinSupport()) / (instance.getMaxSupport() - instance.getMinSupport());
            }
            
            assertEquals( p, phat, TOLERANCE );
            
        }
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructor" );
        UniformDistribution.PDF u = new UniformDistribution.PDF();
        assertEquals( UniformDistribution.DEFAULT_MIN, u.getMinSupport() );
        assertEquals( UniformDistribution.DEFAULT_MAX, u.getMaxSupport() );

        double a = RANDOM.nextGaussian();
        double b = RANDOM.nextDouble() + a;
        u = new UniformDistribution.PDF( a, b );
        assertEquals( a, u.getMinSupport() );
        assertEquals( b, u.getMaxSupport() );

        UniformDistribution.PDF u2 = new UniformDistribution.PDF( u );
        assertEquals( u.getMinSupport(), u2.getMinSupport() );
        assertEquals( u.getMaxSupport(), u2.getMaxSupport() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructor" );
        UniformDistribution u = new UniformDistribution();
        assertEquals( UniformDistribution.DEFAULT_MIN, u.getMinSupport() );
        assertEquals( UniformDistribution.DEFAULT_MAX, u.getMaxSupport() );

        double a = RANDOM.nextGaussian();
        double b = RANDOM.nextDouble() + a;
        u = new UniformDistribution( a, b );
        assertEquals( a, u.getMinSupport() );
        assertEquals( b, u.getMaxSupport() );

        UniformDistribution u2 = new UniformDistribution( u );
        assertEquals( u.getMinSupport(), u2.getMinSupport() );
        assertEquals( u.getMaxSupport(), u2.getMaxSupport() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructor" );
        UniformDistribution.CDF u = new UniformDistribution.CDF();
        assertEquals( UniformDistribution.DEFAULT_MIN, u.getMinSupport() );
        assertEquals( UniformDistribution.DEFAULT_MAX, u.getMaxSupport() );

        double a = RANDOM.nextGaussian();
        double b = RANDOM.nextDouble() + a;
        u = new UniformDistribution.CDF( a, b );
        assertEquals( a, u.getMinSupport() );
        assertEquals( b, u.getMaxSupport() );

        UniformDistribution.CDF u2 = new UniformDistribution.CDF( u );
        assertEquals( u.getMinSupport(), u2.getMinSupport() );
        assertEquals( u.getMaxSupport(), u2.getMaxSupport() );
    }

    /**
     * MaximumLikelihoodEstimator
     */
    public void testMaximumLikelihoodEstimator()
    {
        System.out.println( "MaximumLikelihoodEstimator" );
        UniformDistribution.MaximumLikelihoodEstimator learner =
            new UniformDistribution.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);
    }

}
