/*
 * File:                BernoulliBayesianEstimatorTest.java
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

import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;

/**
 * Unit tests for BernoulliBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class BernoulliBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Number,Double,BetaDistribution>
{

    /**
     * Tests for class BernoulliBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public BernoulliBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class BernoulliBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        BernoulliBayesianEstimator instance = new BernoulliBayesianEstimator();
        assertEquals( 1.0, instance.getInitialBelief().getAlpha() );
        assertEquals( 1.0, instance.getInitialBelief().getBeta() );

        BetaDistribution beta = new BetaDistribution.CDF( RANDOM.nextDouble(), RANDOM.nextDouble() );
        instance = new BernoulliBayesianEstimator( beta );
        assertSame( beta, instance.getInitialBelief() );

    }

    @Override
    public BernoulliBayesianEstimator createInstance()
    {
        return new BernoulliBayesianEstimator();
    }
    
    @Override
    public BernoulliDistribution.PMF createConditionalDistribution()
    {
        return new BernoulliDistribution.PMF( RANDOM.nextDouble() );
    }

    @Override
    public void testKnownValues()
    {

        BernoulliBayesianEstimator instance = new BernoulliBayesianEstimator(
            new BetaDistribution.CDF( 4.8, 19.2 ) );

        ArrayList<Number> data = new ArrayList<Number>( 100 );
        for( int i = 0; i < 100; i++ )
        {
            if( i < 26 )
            {
                data.add( 1 );
            }
            else
            {
                data.add( 0 );
            }
        }

        BetaDistribution result = instance.learn(data);
        System.out.println( "Result: " + ObjectUtil.toString(result) );
        assertEquals( 30.8, result.getAlpha() );
        assertEquals( 93.2, result.getBeta() );

    }

}
