/*
 * File:                UnivariateGaussianMeanBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;

/**
 * Unit tests for UnivariateGaussianMeanBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class UnivariateGaussianMeanBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Double,Double,UnivariateGaussian>
{

    /**
     * Tests for class UnivariateGaussianMeanBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public UnivariateGaussianMeanBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class UnivariateGaussianMeanBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        UnivariateGaussianMeanBayesianEstimator instance =
            new UnivariateGaussianMeanBayesianEstimator();
        assertEquals( UnivariateGaussianMeanBayesianEstimator.DEFAULT_KNOWN_VARIANCE, instance.getKnownVariance() );
        assertEquals( 0.0, instance.getInitialBelief().getMean() );
        assertEquals( 1.0, instance.getInitialBelief().getVariance() );

        double v = RANDOM.nextDouble();
        instance = new UnivariateGaussianMeanBayesianEstimator( v );
        assertEquals( v, instance.getKnownVariance() );
        assertEquals( 0.0, instance.getInitialBelief().getMean() );
        assertEquals( 1.0, instance.getInitialBelief().getVariance() );

        UnivariateGaussian u =
            new UnivariateGaussian( RANDOM.nextGaussian(), RANDOM.nextDouble() );
        instance = new UnivariateGaussianMeanBayesianEstimator( v, u );
        assertEquals( v, instance.getKnownVariance() );
        assertSame( u, instance.getInitialBelief() );
        
    }

    /**
     * Test of getKnownVariance method, of class UnivariateGaussianMeanBayesianEstimator.
     */
    public void testGetKnownVariance()
    {
        System.out.println("getKnownVariance");
        double variance = RANDOM.nextDouble();
        UnivariateGaussianMeanBayesianEstimator instance =
            new UnivariateGaussianMeanBayesianEstimator( variance );
        assertEquals( variance, instance.getKnownVariance() );
    }

    /**
     * Test of setKnownVariance method, of class UnivariateGaussianMeanBayesianEstimator.
     */
    public void testSetKnownVariance()
    {
        System.out.println("setKnownVariance");
        double knownVariance = RANDOM.nextDouble();
        UnivariateGaussianMeanBayesianEstimator instance =
            new UnivariateGaussianMeanBayesianEstimator( knownVariance );
        assertEquals( knownVariance, instance.getKnownVariance() );
        double v2 = knownVariance + 1.0;
        instance.setKnownVariance(v2);
        assertEquals( v2, instance.getKnownVariance() );

        try
        {
            instance.setKnownVariance(0.0);
            fail( "Known variance must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }


    @Override
    public UnivariateGaussianMeanBayesianEstimator createInstance()
    {
        return new UnivariateGaussianMeanBayesianEstimator();
    }

    @Override
    public UnivariateGaussian createConditionalDistribution()
    {
        return new UnivariateGaussian( RANDOM.nextGaussian(), RANDOM.nextDouble() );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known values" );

        UnivariateGaussianMeanBayesianEstimator instance =
            new UnivariateGaussianMeanBayesianEstimator(
                4.0, new UnivariateGaussian( 30, 16 ) );

        ArrayList<Double> data = new ArrayList<Double>( 12 );
        for( int i = 0; i < 12; i++ )
        {
            data.add( 32.0 );
        }

        UnivariateGaussian result = instance.learn(data);
        assertEquals( 0.3265306, result.getVariance(), TOLERANCE );
        assertEquals( 31.959184, result.getMean(), TOLERANCE );

    }

}
