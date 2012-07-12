/*
 * File:                ParallelNegativeLogLikelihoodTest.java
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

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for ParallelNegativeLogLikelihoodTest.
 *
 * @author krdixon
 */
public class ParallelNegativeLogLikelihoodTest
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
     * Tests for class ParallelNegativeLogLikelihoodTest.
     * @param testName Name of the test.
     */
    public ParallelNegativeLogLikelihoodTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class ParallelNegativeLogLikelihoodTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ParallelNegativeLogLikelihood<Double> instance =
            new ParallelNegativeLogLikelihood<Double>();
        assertNull( instance.getCostParameters() );
        assertNotNull( instance.getThreadPool() );

        UnivariateGaussian g = new UnivariateGaussian();
        ArrayList<Double> data = g.sample(RANDOM, 1000);
        instance = new ParallelNegativeLogLikelihood<Double>(data);
        assertSame( data, instance.getCostParameters() );
        assertNotNull( instance.getThreadPool() );
    }

    /**
     * Test of evaluate method, of class ParallelNegativeLogLikelihood.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        UnivariateGaussian g = new UnivariateGaussian();
        ArrayList<Double> data = g.sample(RANDOM, 1000);
        ParallelNegativeLogLikelihood<Double> instance =
            new ParallelNegativeLogLikelihood<Double>(data);
        double pr = instance.evaluate(g);

        NegativeLogLikelihood<Double> serial =
            new NegativeLogLikelihood<Double>( data );
        double sr = serial.evaluate(g);

        assertEquals( sr, pr, TOLERANCE );

    }

    /**
     * Test of getThreadPool method, of class ParallelNegativeLogLikelihood.
     */
    public void testGetThreadPool()
    {
        System.out.println("getThreadPool");
        ParallelNegativeLogLikelihood<Double> instance =
            new ParallelNegativeLogLikelihood<Double>();
        assertNotNull( instance.getThreadPool() );

        instance.setThreadPool(null);
        assertNotNull( instance.getThreadPool() );

    }

    /**
     * Test of setThreadPool method, of class ParallelNegativeLogLikelihood.
     */
    public void testSetThreadPool()
    {
        System.out.println("setThreadPool");
        ThreadPoolExecutor threadPool = ParallelUtil.createThreadPool();
        ParallelNegativeLogLikelihood<Double> instance =
            new ParallelNegativeLogLikelihood<Double>();
        instance.setThreadPool(threadPool);
        assertSame( threadPool, instance.getThreadPool() );
    }

    /**
     * Test of getNumThreads method, of class ParallelNegativeLogLikelihood.
     */
    public void testGetNumThreads()
    {
        System.out.println("getNumThreads");
        ParallelNegativeLogLikelihood<Double> instance =
            new ParallelNegativeLogLikelihood<Double>();
        int result = instance.getNumThreads();
        assertTrue( result > 0 );
    }

}
