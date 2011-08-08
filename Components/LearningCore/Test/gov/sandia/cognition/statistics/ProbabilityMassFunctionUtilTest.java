/*
 * File:                ProbabilityMassFunctionUtilTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import gov.sandia.cognition.statistics.distribution.ScalarDataDistribution;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class ProbabilityMassFunctionUtilTest
 * @author Kevin R. Dixon
 */
public class ProbabilityMassFunctionUtilTest
    extends TestCase
{

    public Random random = new Random( 1 );
    
    /**
     * Entry point for JUnit tests for class ProbabilityMassFunctionUtilTest
     * @param testName name of this test
     */
    public ProbabilityMassFunctionUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ProbabilityMassFunctionUtil instance = new ProbabilityMassFunctionUtil();
        assertNotNull( instance );
    }

    /**
     * Test of getEntropy method, of class ProbabilityMassFunctionUtil.
     */
    public void testGetEntropy()
    {
        System.out.println( "getEntropy" );
        ProbabilityMassFunction<Number> pmf =
            new BinomialDistribution.PMF( 10, random.nextDouble() );
        
        double sum = 0.0;
        for( Number x : pmf.getDomain() )
        {
            double p = pmf.evaluate( x );
            sum -= p*MathUtil.log2( p );
        }
        assertEquals( sum, ProbabilityMassFunctionUtil.getEntropy( pmf ) );
    }

    /**
     * Test of sample method, of class ProbabilityMassFunctionUtil.
     */
    public void testSample()
    {
        System.out.println( "sample" );
        BinomialDistribution.PMF pmf =
            new BinomialDistribution.PMF( 10, random.nextDouble() );
        
        int numSamples = 1000;

        Random random1a = new Random( 1 );
        ArrayList<Number> r1a =
            ProbabilityMassFunctionUtil.sample( pmf, random1a, numSamples );
        assertEquals( numSamples, r1a.size() );
        
        Random random1b = new Random( 1 );
        ArrayList<Number> r1b =
            ProbabilityMassFunctionUtil.sample( pmf, random1b, numSamples );
        assertEquals( numSamples, r1b.size() );
        
        for( int i = 0; i < numSamples; i++ )
        {
//            assertNotSame( r1a.get(i), r1b.get(i) );
            assertEquals( r1a.get(i), r1b.get(i) );
        }
        
        ScalarDataDistribution.PMF s1a = new ScalarDataDistribution.PMF( r1a );
        for( Number x : pmf.getDomain() )
        {
            assertEquals( pmf.evaluate( x ), s1a.evaluate( x ), Math.sqrt(numSamples/pmf.getVariance()) );
        }
        
    }

    /**
     * Single sample
     */
    public void testSingleSample()
    {
        System.out.println( "Single sample" );
        BinomialDistribution.PMF pmf =
            new BinomialDistribution.PMF( 10, random.nextDouble() );

        Random r1 = new Random(1);
        Random r2 = new Random(1);

        for( int n = 0; n < 100; n++ )
        {
            assertEquals( ProbabilityMassFunctionUtil.sampleSingle(pmf, r1),
                ProbabilityMassFunctionUtil.sample(pmf, r2, 1).get(0) );
        }

        int numSamples = 1000;
        ArrayList<Number> singleSamples = new ArrayList<Number>( numSamples );
        for( int i = 0; i < numSamples; i++ )
        {
            singleSamples.add( ProbabilityMassFunctionUtil.sampleSingle(pmf,r1) );
        }
        ArrayList<Number> multiSamples = ProbabilityMassFunctionUtil.sampleMultiple(pmf, r2, numSamples);

        for( int i = 0; i < numSamples; i++ )
        {
            assertEquals( singleSamples.get(i), multiSamples.get(i) );
        }

    }


    /**
     * inverse
     */
    public void testInverse()
    {
        System.out.println( "inverse" );

        BinomialDistribution.CDF cdf =
            new BinomialDistribution.CDF( 10, random.nextDouble() );

        int numSamples = 1000;
        ArrayList<Number> samples = cdf.sample(random, numSamples);
        for( int n = 0; n < samples.size(); n++ )
        {
            double p = cdf.evaluate(samples.get(n));
            InputOutputPair<Number,Double> result = 
                ProbabilityMassFunctionUtil.inverse(cdf, p);
            Number xhat = result.getInput();
            double phat = result.getOutput();
            double pxhat = cdf.evaluate(xhat);
            assertEquals( pxhat, phat );
            assertTrue( phat <= p );
            Integer xhatp1 = xhat.intValue()+1;
            double phatp1 = cdf.evaluate(xhatp1);
            assertTrue( phatp1 >= p );
        }

    }

    /**
     * computeCumulativeValue
     */
    public void testComputeCumulativeValue()
    {
        System.out.println( "computeCumulativeValue" );

        BinomialDistribution.CDF cdf = new BinomialDistribution.CDF(
            random.nextInt(10) + 10, random.nextDouble() );
        for( Integer x : cdf.getDomain() )
        {
            double phat = ProbabilityMassFunctionUtil.computeCumulativeValue(x,cdf);
            double p = cdf.evaluate(x);
            assertEquals( p, phat, 1e-5 );
        }
        
    }
}
