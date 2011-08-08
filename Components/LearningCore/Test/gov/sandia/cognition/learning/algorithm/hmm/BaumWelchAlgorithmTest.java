/*
 * File:                BaumWelchAlgorithmTest.java
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

import gov.sandia.cognition.collection.DefaultMultiCollection;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for BaumWelchAlgorithmTest.
 *
 * @author krdixon
 */
public class BaumWelchAlgorithmTest
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
     * Tests for class BaumWelchAlgorithmTest.
     * @param testName Name of the test.
     */
    public BaumWelchAlgorithmTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * instance
     */
    public BaumWelchAlgorithm<Vector> createInstance()
    {
        HiddenMarkovModel<Vector> hmm = new HiddenMarkovModel<Vector>(
                HiddenMarkovModelTest.DEFAULT_NUM_STATES );
        final int dim = HiddenMarkovModelTest.DEFAULT_OBSERVATION_DIM;
        ArrayList<MultivariateGaussian.PDF> pdfs =
            new ArrayList<MultivariateGaussian.PDF>( hmm.getNumStates() );
        for( int i = 0; i < hmm.getNumStates(); i++ )
        {
            Vector mean = VectorFactory.getDefault().createVector( dim, i );
            Matrix cov = MatrixFactory.getDefault().createIdentity( dim, dim );
            pdfs.add( new MultivariateGaussian.PDF( mean, cov ) );
        }
        hmm.setEmissionFunctions(pdfs);
        
        return new BaumWelchAlgorithm<Vector>( hmm,
            new MultivariateGaussian.WeightedMaximumLikelihoodEstimator(),
            true );
    }

    /**
     * Creates a ContinuousDensityHiddenMarkovModel
     * @return
     * ContinuousDensityHiddenMarkovModel
     */
    public HiddenMarkovModel<Vector> createHMMInstance()
    {
        return HiddenMarkovModelTest.staticCreateInstance();
    }

    /**
     * Tests the constructors of class BaumWelchAlgorithmTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        BaumWelchAlgorithm<Vector> instance = new BaumWelchAlgorithm<Vector>();
        assertEquals( BaumWelchAlgorithm.DEFAULT_REESTIMATE_INITIAL_PROBABILITY, instance.getReestimateInitialProbabilities() );
        assertNull( instance.getInitialGuess() );
        assertNull( instance.getDistributionLearner() );
        assertNull( instance.getResult() );

        MultivariateGaussian.WeightedMaximumLikelihoodEstimator learner =
            new MultivariateGaussian.WeightedMaximumLikelihoodEstimator();
        HiddenMarkovModel<Vector> hmm = this.createHMMInstance();
        boolean reestimate = !instance.getReestimateInitialProbabilities();
        instance = new BaumWelchAlgorithm<Vector>( hmm, learner, reestimate );
        assertSame( hmm, instance.getInitialGuess() );
        assertSame( learner, instance.getDistributionLearner() );
        assertEquals( reestimate, instance.getReestimateInitialProbabilities() );
        assertNull( instance.getResult() );

    }

    /**
     * Test of clone method, of class BaumWelchAlgorithm.
     */
    public void testClone()
    {
        System.out.println("clone");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        BaumWelchAlgorithm<Vector> clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getDistributionLearner(), clone.getDistributionLearner() );
        assertNotSame( instance.getInitialGuess(), clone.getInitialGuess() );
        assertEquals( instance.getReestimateInitialProbabilities(), clone.getReestimateInitialProbabilities() );
    }

    /**
     * Learn
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        HiddenMarkovModel<Vector> target = this.createHMMInstance();
        ArrayList<Vector> observations = target.sample(RANDOM, 1000);

        System.out.println( "TARGET: " + target );

        double l1 = target.computeObservationLogLikelihood(observations);
        System.out.println( "TARGET Log Likelihood: " + l1 );

        BaumWelchAlgorithm<Vector> learner = this.createInstance();
        double l0 = learner.getInitialGuess().computeObservationLogLikelihood(
            observations);
        System.out.println( "INITIAL Log Likelihood: " + l0 );
        learner.setMaxIterations(1000);

        HiddenMarkovModel<Vector> result = learner.learn(observations);
        System.out.println( "Result: " + learner.getIteration() + ": " + result );
        double l2 = result.computeObservationLogLikelihood(observations);
        System.out.println( "Result Log Likelihood: " +  l2 );
        assertTrue( l2 > l0 );

    }


    /**
     * Test of getPerformance method, of class BaumWelchAlgorithm.
     */
    public void testGetPerformance()
    {
        System.out.println("getPerformance");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        NamedValue<Double> result = instance.getPerformance();
        assertEquals( BaumWelchAlgorithm.PERFORMANCE_NAME, result.getName() );
        assertEquals( Double.NEGATIVE_INFINITY, result.getValue() );
    }

    /**
     * Test of getResult method, of class BaumWelchAlgorithm.
     */
    public void testGetResult()
    {
        System.out.println("getResult");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        assertNull( instance.getResult() );

        HiddenMarkovModel<Vector> hmm =
            HiddenMarkovModelTest.staticCreateInstance();
        ArrayList<Vector> y = hmm.sample( RANDOM, 100 );

        HiddenMarkovModel<Vector> result = instance.learn(y);
        assertSame( result, instance.getResult() );
        assertNotNull( result );
    }

    /**
     * Test of getInitialGuess method, of class BaumWelchAlgorithm.
     */
    public void testGetInitialGuess()
    {
        System.out.println("getInitialGuess");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        assertNotNull( instance.getInitialGuess() );
    }

    /**
     * Test of setInitialGuess method, of class BaumWelchAlgorithm.
     */
    public void testSetInitialGuess()
    {
        System.out.println("setInitialGuess");
        HiddenMarkovModel<Vector> target =
            HiddenMarkovModelTest.staticCreateInstance();
        ArrayList<Vector> observations = target.sample(RANDOM, 1000);

        System.out.println( "TARGET: " + target );

        double l1 = target.computeObservationLogLikelihood(observations);
        System.out.println( "TARGET Log Likelihood: " + l1 );

        BaumWelchAlgorithm<Vector> learner = this.createInstance();
        HiddenMarkovModel<Vector> initial = learner.getInitialGuess().clone();
        learner.setMaxIterations(10);

        HiddenMarkovModel<Vector> result = learner.learn(observations);
        assertNotSame( result, initial );
    }

    /**
     * Test of getReestimateInitialProbabilities method, of class BaumWelchAlgorithm.
     */
    public void testGetReestimateInitialProbabilities()
    {
        System.out.println("getReestimateInitialProbabilities");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        boolean flag = instance.getReestimateInitialProbabilities();
        flag = !flag;
        instance.setReestimateInitialProbabilities(flag);
        assertEquals( flag, instance.getReestimateInitialProbabilities() );
    }

    /**
     * Test of setReestimateInitialProbabilities method, of class BaumWelchAlgorithm.
     */
    public void testSetReestimateInitialProbabilities()
    {
        System.out.println("setReestimateInitialProbabilities");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        boolean flag = instance.getReestimateInitialProbabilities();
        flag = !flag;
        instance.setReestimateInitialProbabilities(flag);
        assertEquals( flag, instance.getReestimateInitialProbabilities() );
    }

    /**
     * Test of getDistributionLearner method, of class BaumWelchAlgorithm.
     */
    public void testGetDistributionLearner()
    {
        System.out.println("getDistributionLearner");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        BatchLearner<Collection<? extends WeightedValue<? extends Vector>>, ? extends ComputableDistribution<Vector>> learner =
            instance.getDistributionLearner();
        assertNotNull( learner );
    }

    /**
     * Test of setDistributionLearner method, of class BaumWelchAlgorithm.
     */
    public void testSetDistributionLearner()
    {
        System.out.println("setDistributionLearner");
        BaumWelchAlgorithm<Vector> instance = this.createInstance();
        BatchLearner<Collection<? extends WeightedValue<? extends Vector>>, ? extends ComputableDistribution<Vector>> learner =
            instance.getDistributionLearner();
        assertNotNull( learner );

        instance.setDistributionLearner(null);
        assertNull( instance.getDistributionLearner() );
        instance.setDistributionLearner(learner);
        assertSame( learner, instance.getDistributionLearner() );
    }

    /**
     * Learn
     */
    public void testMultiSequenceLearn()
    {
        System.out.println( "Multi-sequence learn" );

        HiddenMarkovModel<Vector> target = this.createHMMInstance();
        final int numSequences = 100;
        ArrayList<ArrayList<Vector>> sequences =
            new ArrayList<ArrayList<Vector>>( numSequences );
        for( int k = 0; k < numSequences; k++ )
        {
            sequences.add( target.sample(RANDOM, 10) );
        }

        DefaultMultiCollection<Vector> data =
            new DefaultMultiCollection<Vector>( sequences );
        sequences = null;

        System.out.println( "TARGET: " + target );
        double l1 = target.computeMultipleObservationLogLikelihood(data.subCollections());
        System.out.println( "TARGET Log Likelihood: " + l1 );

        // FALSE: Result Log Likelihood: -114550.38265183996
        BaumWelchAlgorithm<Vector> learner = this.createInstance();
        learner.setReestimateInitialProbabilities(true);
        double l0 = learner.getInitialGuess().computeMultipleObservationLogLikelihood( data.subCollections() );
        System.out.println( "INITIAL Log Likelihood: " + l0 );
        learner.setMaxIterations(1000);

        HiddenMarkovModel<Vector> result = learner.learn(data);
        System.out.println( "Result: " + learner.getIteration() + ": " + result );
        double l2 = result.computeMultipleObservationLogLikelihood(data.subCollections());
        System.out.println( "Result Log Likelihood: " +  l2 );
        assertTrue( l2 > l0 );

    }


}
