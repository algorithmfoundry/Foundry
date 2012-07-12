/*
 * File:                ParallelBaumWelchAlgorithmTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.hmm;

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Unit tests for ParallelBaumWelchAlgorithmTest.
 *
 * @author krdixon
 */
public class ParallelBaumWelchAlgorithmTest
    extends BaumWelchAlgorithmTest
{

    /**
     * Tests for class ParallelBaumWelchAlgorithmTest.
     * @param testName Name of the test.
     */
    public ParallelBaumWelchAlgorithmTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public ParallelBaumWelchAlgorithm<Vector> createInstance()
    {
        BaumWelchAlgorithm<Vector> si = super.createInstance();
        ParallelHiddenMarkovModel<Vector> hmm =
            new ParallelHiddenMarkovModel<Vector>(
                si.getInitialGuess().getInitialProbability(),
                si.getInitialGuess().getTransitionProbability(),
                si.getInitialGuess().getEmissionFunctions() );

        ParallelBaumWelchAlgorithm<Vector> instance = new ParallelBaumWelchAlgorithm<Vector>(
            hmm, si.getDistributionLearner(), si.getReestimateInitialProbabilities() );
        return instance;
    }

    /**
     * Tests the constructors of class ParallelBaumWelchAlgorithmTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ParallelBaumWelchAlgorithm<Vector> instance =
            new ParallelBaumWelchAlgorithm<Vector>();
        assertEquals( BaumWelchAlgorithm.DEFAULT_REESTIMATE_INITIAL_PROBABILITY, instance.getReestimateInitialProbabilities() );
        assertNull( instance.getInitialGuess() );
        assertNull( instance.getDistributionLearner() );
        assertNull( instance.getResult() );

        MultivariateGaussian.WeightedMaximumLikelihoodEstimator learner =
            new MultivariateGaussian.WeightedMaximumLikelihoodEstimator();
        HiddenMarkovModel<Vector> hmm = this.createHMMInstance();
        boolean reestimate = !instance.getReestimateInitialProbabilities();
        instance = new ParallelBaumWelchAlgorithm<Vector>(
            hmm, learner, reestimate );
        assertSame( hmm, instance.getInitialGuess() );
        assertSame( learner, instance.getDistributionLearner() );
        assertEquals( reestimate, instance.getReestimateInitialProbabilities() );
        assertNull( instance.getResult() );
    }

    /**
     * Test of getThreadPool method, of class ParallelBaumWelchAlgorithm.
     */
    public void testGetThreadPool()
    {
        System.out.println("getThreadPool");
        ParallelBaumWelchAlgorithm<?> instance = this.createInstance();
        assertNotNull( instance.getThreadPool() );
    }

    /**
     * Test of setThreadPool method, of class ParallelBaumWelchAlgorithm.
     */
    public void testSetThreadPool()
    {
        System.out.println("setThreadPool");
        ThreadPoolExecutor threadPool = ParallelUtil.createThreadPool();
        ParallelBaumWelchAlgorithm<?> instance = this.createInstance();
        instance.setThreadPool(threadPool);
        assertSame( threadPool, instance.getThreadPool() );
    }

    /**
     * Test of getNumThreads method, of class ParallelBaumWelchAlgorithm.
     */
    public void testGetNumThreads()
    {
        System.out.println("getNumThreads");
        ParallelBaumWelchAlgorithm<Vector> instance = this.createInstance();
        assertTrue( instance.getNumThreads() >= 1 );
    }

    /**
     * Tests equivalence between parallel and serial versions.
     */
    public void testEquivalenttoSerialBW()
    {
        System.out.println( "Tests equivalance" );

        BaumWelchAlgorithm<Vector> serial = super.createInstance();
        ParallelBaumWelchAlgorithm<Vector> parallel = this.createInstance();

        HiddenMarkovModel<Vector> hmm = createHMMInstance();
        final int NUM_SAMPLES = 100;
        Collection<Vector> samples = hmm.sample(RANDOM, NUM_SAMPLES );

        serial.learn(samples);
        parallel.learn(samples);
        assertEquals( serial.getIteration(), parallel.getIteration() );
        assertEquals( serial.getResult().computeObservationLogLikelihood(samples),
            parallel.getResult().computeObservationLogLikelihood(samples) );
    }

}
