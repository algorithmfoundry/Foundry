/*
 * File:                ExponentialBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 14, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.bayesian.BayesianCredibleInterval;
import gov.sandia.cognition.statistics.distribution.ExponentialDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import java.util.ArrayList;

/**
 * Unit tests for ExponentialBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class ExponentialBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Double,Double,GammaDistribution>
{

    /**
     * Tests for class ExponentialBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public ExponentialBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class ExponentialBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ExponentialBayesianEstimator instance =
            new ExponentialBayesianEstimator();
        assertNotNull( instance.getParameter() );

        GammaDistribution belief = new GammaDistribution();
        instance = new ExponentialBayesianEstimator( belief );
        assertSame( instance.getInitialBelief(), belief );

        ExponentialDistribution conditional = new ExponentialDistribution();
        instance = new ExponentialBayesianEstimator( conditional, belief );
        assertSame( instance.getInitialBelief(), belief );
        assertSame( instance.getParameter().getConditionalDistribution(), conditional );
    }

    @Override
    public ExponentialBayesianEstimator createInstance()
    {
        GammaDistribution belief = new GammaDistribution( 2.0, 2.0 );
        return new ExponentialBayesianEstimator( belief );
    }

    @Override
    public ExponentialDistribution createConditionalDistribution()
    {
        double rate = RANDOM.nextDouble() * 10.0 + 1.0;
        return new ExponentialDistribution( rate );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known values" );
        GammaDistribution belief = new GammaDistribution( 1.0, 1.0 );
        ExponentialDistribution target = new ExponentialDistribution(
            Math.abs(RANDOM.nextGaussian()) );
        ExponentialBayesianEstimator instance =
            new ExponentialBayesianEstimator( belief );

        ArrayList<Double> samples = target.sample(RANDOM, NUM_SAMPLES);
        GammaDistribution posterior = instance.learn(samples);
        BayesianCredibleInterval interval =
            BayesianCredibleInterval.compute(posterior, 0.95);
        System.out.println( "Target: " + target.getRate() );
        System.out.println( "Interval: " + interval );
        assertTrue( interval.withinInterval(target.getRate()) );
    }
    
}
