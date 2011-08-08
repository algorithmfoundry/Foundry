/*
 * File:                PoissonBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 15, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.PoissonDistribution;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Unit tests for PoissonBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class PoissonBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Number,Double,GammaDistribution>
{

    /**
     * Tests for class PoissonBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public PoissonBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class PoissonBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        PoissonBayesianEstimator f = new PoissonBayesianEstimator();
        assertEquals( 1.0, f.getInitialBelief().getShape() );
        assertEquals( 1.0, f.getInitialBelief().getScale() );

        GammaDistribution g = new GammaDistribution( RANDOM.nextDouble() * 10.0, RANDOM.nextDouble() * 10.0 );
        f = new PoissonBayesianEstimator( g );
        assertSame( g, f.getInitialBelief() );

    }

    @Override
    public PoissonBayesianEstimator createInstance()
    {
        double shape = RANDOM.nextDouble() * 10.0+10.0;
        double scale = 1.0;
        return new PoissonBayesianEstimator( new GammaDistribution( shape, scale ) );
    }

    @Override
    public PoissonDistribution.PMF createConditionalDistribution()
    {
        double rate = RANDOM.nextDouble() * 10.0 + 10.0;
        return new PoissonDistribution.PMF( rate );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        PoissonBayesianEstimator instance = new PoissonBayesianEstimator(
            new GammaDistribution( 6.25, 1.0/2.5 ) );

        Collection<Number> values = new LinkedList<Number>(
            Arrays.asList( 3.0, 2.0, 0.0, 8.0, 2.0, 4.0, 6.0, 1.0 ) );
        GammaDistribution result = instance.learn(values);
        System.out.println( "Gamma Mean: " + result.getMean() );
        assertEquals( 32.25, result.getShape() );
        assertEquals( 1.0/10.5, result.getScale() );

    }


}
