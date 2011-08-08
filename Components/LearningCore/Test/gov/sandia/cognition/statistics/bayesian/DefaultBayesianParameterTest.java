/*
 * File:                DefaultBayesianParameterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 27, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DefaultBayesianParameterTest.
 *
 * @author krdixon
 */
public class DefaultBayesianParameterTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class DefaultBayesianParameterTest.
     * @param testName Name of the test.
     */
    public DefaultBayesianParameterTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class DefaultBayesianParameterTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        DefaultBayesianParameter<Double,UnivariateGaussian,?> instance =
            new DefaultBayesianParameter<Double,UnivariateGaussian,Distribution<Double>>(
                new UnivariateGaussian.PDF(), "mean" );
        assertNull( instance.getParameterPrior() );
    }

    /**
     * Test of clone method, of class DefaultBayesianParameter.
     */
    public void testClone()
    {
        System.out.println("clone");
        DefaultBayesianParameter<Double,UnivariateGaussian,Distribution<Double>> instance =
            new DefaultBayesianParameter<Double,UnivariateGaussian,Distribution<Double>>(
            new UnivariateGaussian.PDF(), "mean", new UnivariateGaussian.PDF() );
        DefaultBayesianParameter<Double,UnivariateGaussian,Distribution<Double>> clone = instance.clone();
        assertNotSame( instance.getParameterPrior(), clone.getParameterPrior() );
        assertNotSame( instance.getConditionalDistribution(), clone.getConditionalDistribution() );
        assertEquals( instance.getName(), clone.getName() );
        assertEquals( instance.getValue(), clone.getValue() );
    }

}
