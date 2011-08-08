/*
 * File:                ParallelHiddenMarkovModelTest.java
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
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Unit tests for ParallelHiddenMarkovModelTest.
 *
 * @author krdixon
 */
public class ParallelHiddenMarkovModelTest
    extends HiddenMarkovModelTest
{

    /**
     * Tests for class ParallelHiddenMarkovModelTest.
     * @param testName Name of the test.
     */
    public ParallelHiddenMarkovModelTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a ParallelContinuousDensityHiddenMarkovModel
     * @return
     * ParallelContinuousDensityHiddenMarkovModel
     */
    public static ParallelHiddenMarkovModel<Vector> staticCreateInstance()
    {
        HiddenMarkovModel<Vector> hmm =
            HiddenMarkovModelTest.staticCreateInstance();
        return new ParallelHiddenMarkovModel<Vector>(
                hmm.getInitialProbability(),
                hmm.getTransitionProbability(),
                hmm.getEmissionFunctions() );
    }

    @Override
    public ParallelHiddenMarkovModel<Vector> createInstance()
    {
        return staticCreateInstance();
    }

    /**
     * Tests the constructors of class ParallelHiddenMarkovModelTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ParallelHiddenMarkovModel<Vector> hmm =
            new ParallelHiddenMarkovModel<Vector>();
        assertEquals( ParallelHiddenMarkovModel.DEFAULT_NUM_STATES, hmm.getNumStates() );
        assertNull( hmm.getEmissionFunctions() );

        int k = RANDOM.nextInt(10) + 10;
        hmm = new ParallelHiddenMarkovModel<Vector>( k );
        assertEquals( k, hmm.getNumStates() );
        assertNull( hmm.getEmissionFunctions() );

        ParallelHiddenMarkovModel<Vector> hmm2 = this.createInstance();

        hmm = new ParallelHiddenMarkovModel<Vector>(
            hmm2.getInitialProbability(),
            hmm2.getTransitionProbability(),
            hmm2.getEmissionFunctions() );

        assertSame( hmm2.getInitialProbability(), hmm.getInitialProbability() );
        assertSame( hmm2.getTransitionProbability(), hmm.getTransitionProbability() );
        assertSame( hmm2.getEmissionFunctions(), hmm.getEmissionFunctions() );

    }

    /**
     * Test of getThreadPool method, of class ParallelBaumWelchAlgorithm.
     */
    public void testGetThreadPool()
    {
        System.out.println("getThreadPool");
        ParallelHiddenMarkovModel<Vector> instance = this.createInstance();
        assertNotNull( instance.getThreadPool() );
    }

    /**
     * Test of setThreadPool method, of class ParallelBaumWelchAlgorithm.
     */
    public void testSetThreadPool()
    {
        System.out.println("setThreadPool");
        ThreadPoolExecutor threadPool = ParallelUtil.createThreadPool();
        ParallelHiddenMarkovModel<Vector> instance = this.createInstance();
        instance.setThreadPool(threadPool);
        assertSame( threadPool, instance.getThreadPool() );
    }

    /**
     * Test of getNumThreads method, of class ParallelBaumWelchAlgorithm.
     */
    public void testGetNumThreads()
    {
        System.out.println("getNumThreads");
        ParallelHiddenMarkovModel<Vector> instance = this.createInstance();
        assertTrue( instance.getNumThreads() >= 1 );
    }

    /**
     * Ensures that the parallel and serial HMMs are the same.
     */
    public void testEquivalentToSerialHMM()
    {

        HiddenMarkovModel<Vector> hmm = super.createInstance();
        ParallelHiddenMarkovModel<Vector> phmm = this.createInstance();

        Random r1 = new Random(1);
        Random r2 = new Random(1);

        ArrayList<Vector> s1 = hmm.sample( r1, NUM_SAMPLES );
        ArrayList<Vector> s2 = phmm.sample( r2, NUM_SAMPLES );
        assertEquals( s1.size(), s2.size() );
        for( int i = 0; i < s1.size(); i++ )
        {
            assertNotSame( s1.get(i), s2.get(i) );
            assertEquals( s1.get(i), s2.get(i) );
        }

        double l1 = hmm.computeObservationLogLikelihood(s1);
        double l2 = phmm.computeObservationLogLikelihood(s1);
        assertEquals( l1, l2, TOLERANCE );

    }
 
}
