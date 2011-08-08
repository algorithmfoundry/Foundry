/*
 * File:                UniformDistributionBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 10, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.distribution.ParetoDistribution;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for UniformDistributionBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class UniformDistributionBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Double,Double,ParetoDistribution>
{

    /**
     * Tests for class UniformDistributionBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public UniformDistributionBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class UniformDistributionBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        UniformDistributionBayesianEstimator instance =
            new UniformDistributionBayesianEstimator();
        assertNotNull( instance.getInitialBelief() );

        UniformDistributionBayesianEstimator i2 =
            new UniformDistributionBayesianEstimator( instance.getInitialBelief() );
        assertSame( instance.getInitialBelief(), i2.getInitialBelief() );
    }

    @Override
    public UniformDistributionBayesianEstimator createInstance()
    {
        return new UniformDistributionBayesianEstimator();
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        List<Double> values = Arrays.asList( 1.0, 2.0, 3.0, 6.0, 4.0, 5.0 );
        ParetoDistribution prior = new ParetoDistribution( 1.0, 2.0, 0.0 );
        UniformDistributionBayesianEstimator instance =
            new UniformDistributionBayesianEstimator( prior );
        assertEquals( 1.0, instance.computeEquivalentSampleSize(prior) );
        ParetoDistribution posterior = instance.learn(values);

        assertEquals( 7.0, posterior.getShape() );
        assertEquals( 6.0, posterior.getScale() );
    }

    @Override
    public UniformDistribution createConditionalDistribution()
    {
        double theta = Math.abs( RANDOM.nextGaussian() );
        return new UniformDistribution( 0.0, theta );
    }

}
