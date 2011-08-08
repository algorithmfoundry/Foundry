/*
 * File:                GammaInverseScaleBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 15, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import java.util.Collection;

/**
 * Unit tests for GammaInverseScaleBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class GammaInverseScaleBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Double,Double,GammaDistribution>
{

    /**
     * Tests for class GammaInverseScaleBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public GammaInverseScaleBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class GammaInverseScaleBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        GammaInverseScaleBayesianEstimator instance =
            new GammaInverseScaleBayesianEstimator();
        assertEquals( GammaInverseScaleBayesianEstimator.DEFAULT_SHAPE, instance.getShape() );
        assertNotNull( instance.getParameter() );

        double shape = RANDOM.nextDouble();
        GammaDistribution prior = new GammaDistribution();
        instance = new GammaInverseScaleBayesianEstimator( shape, prior );
        assertEquals( shape, instance.getShape() );
        assertSame( prior, instance.getParameter().getParameterPrior() );

        GammaDistribution conditional = new GammaDistribution( shape, RANDOM.nextDouble() );
        instance = new GammaInverseScaleBayesianEstimator( conditional, prior );
        assertEquals( shape, instance.getShape() );
        assertSame( conditional, instance.getParameter().getConditionalDistribution() );
        assertSame( prior, instance.getParameter().getParameterPrior() );
    }

    /**
     * Test of getShape method, of class GammaInverseScaleBayesianEstimator.
     */
    public void testGetShape()
    {
        System.out.println("getShape");
        GammaInverseScaleBayesianEstimator instance =
            new GammaInverseScaleBayesianEstimator();
        assertTrue( instance.getShape() > 0 );
    }

    /**
     * Test of setShape method, of class GammaInverseScaleBayesianEstimator.
     */
    public void testSetShape()
    {
        System.out.println("setShape");
        GammaInverseScaleBayesianEstimator instance =
            new GammaInverseScaleBayesianEstimator();
        double shape = RANDOM.nextDouble();
        instance.setShape(shape);
        assertEquals( shape, instance.getShape() );
        try
        {
            instance.setShape(0.0);
            fail( "Shape must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    @Override
    public GammaInverseScaleBayesianEstimator createInstance()
    {
        double shape = 2.0;
        return new GammaInverseScaleBayesianEstimator( shape, new GammaDistribution() );
    }

    @Override
    public GammaDistribution createConditionalDistribution()
    {
        double shape = 2.0;
        return new GammaDistribution( shape, 0.1 );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        // Exp(rate) == Gamma(1,1/rate)

        GammaDistribution prior = new GammaDistribution( 4.0, 1.0 );

        GammaDistribution target =
            new GammaDistribution( 1.0, RANDOM.nextDouble() );
        Collection<? extends Double> samples = this.createData(target);

        ExponentialBayesianEstimator exp =
            new ExponentialBayesianEstimator( prior.clone() );
        GammaInverseScaleBayesianEstimator gamma =
            new GammaInverseScaleBayesianEstimator( 1.0, prior.clone() );

        GammaDistribution ehat = exp.learn(samples);
        GammaDistribution ghat = gamma.learn(samples);

        System.out.println( "Ehat: " + ehat );
        System.out.println( "Ghat: " + ghat );

        this.identical(ehat, ghat);
    }

}
