/*
 * File:                BinomialBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.distribution.BetaBinomialDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for BinomialBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class BinomialBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Number,Double,BetaDistribution>
{

    /**
     * Tests for class BinomialBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public BinomialBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class BinomialBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        BinomialBayesianEstimator instance = new BinomialBayesianEstimator();
        assertEquals( BinomialBayesianEstimator.DEFAULT_N, instance.getN() );
        assertNotNull( instance.getParameter() );
        assertEquals( 3.0, instance.computeEquivalentSampleSize( instance.getInitialBelief() ) );

        int n = RANDOM.nextInt(10) + 10;
        instance = new BinomialBayesianEstimator(n);
        assertEquals( n, instance.getN() );

        BetaDistribution prior = new BetaDistribution(
            RANDOM.nextDouble(), RANDOM.nextDouble() );
        instance = new BinomialBayesianEstimator( n, prior );
        assertEquals( n, instance.getN() );
        assertSame( prior, instance.getInitialBelief() );

        BinomialDistribution conditional =
            new BinomialDistribution( n, RANDOM.nextDouble() );
        instance = new BinomialBayesianEstimator( conditional, prior );
        assertEquals( n, instance.getN() );
        assertSame( conditional, instance.getParameter().getConditionalDistribution() );
        assertSame( prior, instance.getParameter().getParameterPrior() );

    }

    @Override
    public BinomialBayesianEstimator createInstance()
    {
        final int n = 10;
        return new BinomialBayesianEstimator(n);
    }

    @Override
    public BinomialDistribution createConditionalDistribution()
    {
        return this.createInstance().getParameter().getConditionalDistribution();
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        // Bolstad 148-149
        BinomialBayesianEstimator instance = new BinomialBayesianEstimator(
            100, new BetaDistribution( 4.8, 19.2 ) );
        List<Integer> values = Arrays.asList(26);
        BetaDistribution posterior = instance.learn(values);
        assertEquals( 4.8+26, posterior.getAlpha(), TOLERANCE );
        assertEquals( 19.2+74, posterior.getBeta(), TOLERANCE );
    }

    /**
     * Test of getN method, of class BinomialBayesianEstimator.
     */
    public void testGetN()
    {
        System.out.println("getN");
        BinomialBayesianEstimator instance = this.createInstance();
        assertTrue( instance.getN() > 0 );
    }

    /**
     * Test of setN method, of class BinomialBayesianEstimator.
     */
    public void testSetN()
    {
        System.out.println("setN");
        BinomialBayesianEstimator instance = this.createInstance();
        int n = RANDOM.nextInt(10) + 10;
        instance.setN(n);
        assertEquals( n, instance.getN() );

        try
        {
            instance.setN(0);
            fail( "N must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
