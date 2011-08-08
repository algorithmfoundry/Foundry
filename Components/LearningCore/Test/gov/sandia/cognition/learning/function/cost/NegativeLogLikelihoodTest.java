/*
 * File:                NegativeLogLikelihoodTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.statistics.bayesian.BayesianUtil;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for NegativeLogLikelihoodTest.
 *
 * @author krdixon
 */
public class NegativeLogLikelihoodTest
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
     * Tests for class NegativeLogLikelihoodTest.
     * @param testName Name of the test.
     */
    public NegativeLogLikelihoodTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class NegativeLogLikelihoodTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        NegativeLogLikelihood<Double> instance = new NegativeLogLikelihood<Double>();
        assertNull( instance.getCostParameters() );

        UnivariateGaussian g = new UnivariateGaussian.PDF();
        Collection<Double> samples = g.sample(RANDOM, 1000);
        instance = new NegativeLogLikelihood<Double>( samples );
        assertSame( samples, instance.getCostParameters() );
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        UnivariateGaussian g = new UnivariateGaussian.PDF();
        Collection<Double> samples = g.sample(RANDOM, 1000);
        NegativeLogLikelihood<Double> instance =
            new NegativeLogLikelihood<Double>( samples );

        NegativeLogLikelihood<Double> clone =
            (NegativeLogLikelihood<Double>) instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getCostParameters(), clone.getCostParameters() );
        assertEquals( instance.getCostParameters().size(), clone.getCostParameters().size() );

        double r1 = instance.evaluate(g);
        double r2 = instance.evaluate(g);
        assertEquals( r1, r2, TOLERANCE );
    }

    /**
     * Test of evaluate method, of class NegativeLogLikelihood.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        UnivariateGaussian g = new UnivariateGaussian.PDF();
        Collection<Double> samples = g.sample(RANDOM, 1000);

        NegativeLogLikelihood<Double> instance =
            new NegativeLogLikelihood<Double>( samples );

        double result = instance.evaluate(g);
        assertEquals( -BayesianUtil.logLikelihood(g, samples)/samples.size(), result, TOLERANCE );
    }



}
