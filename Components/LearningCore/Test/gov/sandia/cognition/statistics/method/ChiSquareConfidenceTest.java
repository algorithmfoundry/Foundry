/*
 * File:                ChiSquareConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class ChiSquareConfidenceTest
 * @author Kevin R. Dixon
 */
public class ChiSquareConfidenceTest
    extends TestCase
{

    /**
     * Random number generator.
     */
    public Random RANDOM = new Random(1);

    /**
     * Default confidence.
     */
    public double CONFIDENCE = 0.95;

    /**
     * Entry point for JUnit tests for class ChiSquareConfidenceTest
     * @param testName name of this test
     */
    public ChiSquareConfidenceTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class ChiSquareConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");
        ChiSquareConfidence instance = new ChiSquareConfidence();
        ChiSquareConfidence clone = (ChiSquareConfidence) instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
    }

    /**
     * Test of evaluateNullHypothesis method, of class ChiSquareConfidence.
     */
    public void testEvaluateNullHypothesis()
    {
        System.out.println("evaluateNullHypothesis");
        Collection<Double> data1 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Collection<Double> data2 = Arrays.asList(2.0, 3.0, 4.0, 5.0, 6.0);
        ChiSquareConfidence instance = new ChiSquareConfidence();
        ChiSquareConfidence.Statistic result = instance.evaluateNullHypothesis(data1, data2);
        System.out.println("Result:\n" + ObjectUtil.inspectFieldValues(result));
        final double EPS = 1e-5;

        assertEquals(1.45, result.getChiSquare(), EPS);
        assertEquals(4.0, result.getDegreesOfFreedom(), EPS);
        assertEquals(0.83545988, result.getNullHypothesisProbability(), EPS);

        data1 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 10.0);
        data2 = Arrays.asList(6.0, 5.0, 4.0, 3.0, 2.0, 5.0);
        result = instance.evaluateNullHypothesis(data1, data2);

        System.out.println("Result:\n" + ObjectUtil.inspectFieldValues(result));
        assertEquals(16.05, result.getChiSquare(), EPS);
        assertEquals(5.0, result.getDegreesOfFreedom(), EPS);
        assertEquals(0.00670276, result.getNullHypothesisProbability(), EPS);
    }


    /**
     * test of static evaluateNullHypothesis
     */
    public void testStaticEvaluateNullHypothesis()
    {
        System.out.println( "evaluateNullHypothesis" );

        double p1 = 0.5;
        int N = 10;
        int numSamples = 1000;
        BinomialDistribution.PMF pmf = new BinomialDistribution.PMF( N, p1 );

        Collection<Number> data = pmf.sample(RANDOM, numSamples);

        ChiSquareConfidence.Statistic stat =
            ChiSquareConfidence.evaluateNullHypothesis(data, pmf);
        System.out.println( "Chi Square (Pass?): " + stat );
        assertEquals( 1.0, stat.getNullHypothesisProbability(), CONFIDENCE );

        // Change from p=0.5 to p=0.52 and the test should now fail...
        // pretty sensitve, eh?
        double p2 = 0.52;
        pmf = new BinomialDistribution.PMF(N, p2);

        stat = ChiSquareConfidence.evaluateNullHypothesis(data, pmf);
        System.out.println( "Chi Square (Fail?): " + stat );
        assertEquals( 0.0, stat.getNullHypothesisProbability(), 1.0-CONFIDENCE );

        // This should barf, because there should be data values not in the
        // domain of the PMF
        pmf = new BinomialDistribution.PMF( 2, p1 );
        try
        {
            stat = ChiSquareConfidence.evaluateNullHypothesis(data, pmf);
            fail( "Data are outside the domain of PMF" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        // Some of the domain have expected counts of 0.0... this should be
        // OK!!
        double p3 = 1.0;
        pmf = new BinomialDistribution.PMF( N, p3 );
        data = pmf.sample(RANDOM, numSamples);
        stat = ChiSquareConfidence.evaluateNullHypothesis(data, pmf);
        System.out.println( "Chi Square (Pass?): " + stat );
        assertEquals( 1.0, stat.getNullHypothesisProbability(), CONFIDENCE );

    }

}
