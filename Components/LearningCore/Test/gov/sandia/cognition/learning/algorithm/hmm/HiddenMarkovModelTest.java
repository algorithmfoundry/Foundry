/*
 * File:                ContinuousDensityHiddenMarkovModelTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Jan 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.hmm;

import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.MultivariateDistributionTestHarness;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.statistics.distribution.MapBasedPointMassDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Unit tests for ContinuousDensityHiddenMarkovModelTest.
 *
 * @author krdixon
 */
public class HiddenMarkovModelTest
    extends MultivariateDistributionTestHarness<Vector>
{

    /**
     * Num states
     */
    public static int DEFAULT_NUM_STATES = 3;

    /**
     * observation dim
     */
    public static int DEFAULT_OBSERVATION_DIM = 1;

    /**
     * Tests for class ContinuousDensityHiddenMarkovModelTest.
     * @param testName Name of the test.
     */
    public HiddenMarkovModelTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates static CDHMM
     * @return
     * CDHMM
     */
    public static HiddenMarkovModel<Vector> staticCreateInstance()
    {
        int k = DEFAULT_NUM_STATES;
        int dim = DEFAULT_OBSERVATION_DIM;

        ArrayList<MultivariateGaussian.PDF> pdfs =
            new ArrayList<MultivariateGaussian.PDF>( k );

        Matrix C = MatrixFactory.getDefault().createIdentity(dim, dim).scale(
            0.1);
        for( int i = 0; i < k; i++ )
        {
            pdfs.add( new MultivariateGaussian.PDF(
                VectorFactory.getDefault().createVector( dim, i ), C.clone() ) );
        }

        Matrix A = MatrixFactory.getDefault().createMatrix(k, k);
        A.setElement(0, 0, 0.5);
        A.setElement(1, 0, 0.2 );
        A.setElement(2, 0, 0.3 );

        A.setElement(0, 1, 0.3 );
        A.setElement(1, 1, 0.5 );
        A.setElement(2, 1, 0.2 );

        A.setElement(0, 2, 0.3 );
        A.setElement(1, 2, 0.2 );
        A.setElement(2, 2, 0.5 );

        Vector pi = VectorFactory.getDefault().copyValues( 0.5, 0.25, 0.25 );

        return new HiddenMarkovModel<Vector>( pi, A, pdfs );

    }

    /**
     * Creates an instance.
     * @return
     * CDHMM
     */
    public HiddenMarkovModel<Vector> createInstance()
    {
        return staticCreateInstance();
    }


    /**
     * Tests the constructors of class ContinuousDensityHiddenMarkovModelTest.
     */
    @SuppressWarnings("unchecked")
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        HiddenMarkovModel<Vector> hmm =
            new HiddenMarkovModel<Vector>();
        assertEquals( HiddenMarkovModel.DEFAULT_NUM_STATES, hmm.getNumStates() );
        assertNull( hmm.getEmissionFunctions() );

        int k = RANDOM.nextInt(10) + 10;
        hmm = new HiddenMarkovModel<Vector>( k );
        assertEquals( k, hmm.getNumStates() );
        assertNull( hmm.getEmissionFunctions() );

        HiddenMarkovModel<Vector> hmm2 = this.createInstance();

        hmm = new HiddenMarkovModel<Vector>(
            hmm2.getInitialProbability(),
            hmm2.getTransitionProbability(),
            hmm2.getEmissionFunctions() );

        assertSame( hmm2.getInitialProbability(), hmm.getInitialProbability() );
        assertSame( hmm2.getTransitionProbability(), hmm.getTransitionProbability() );
        assertSame( hmm2.getEmissionFunctions(), hmm.getEmissionFunctions() );        

    }

    /**
     * Test of computeObservationLikelihoods method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testComputeObservationLikelihoods()
    {
        System.out.println("computeObservationLikelihoods");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        Vector observation = VectorFactory.getDefault().copyValues(1.0);
        Vector result = instance.computeObservationLikelihoods(observation);
        Vector expected = VectorFactory.getDefault().copyValues(
            0.008500366602520341, 1.26156626101008, 0.008500366602520341 );

        System.out.println( "Observation: " + observation );
        System.out.println( "Result: " + result );
        System.out.println( "Expected: " + expected );

        assertTrue( expected.equals( result, TOLERANCE ) );

    }

    /**
     * Test of computeForwardProbabilities method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testKnownValues()
    {
        System.out.println("computeForwardProbabilities");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        ArrayList<Vector> observations = instance.sample(RANDOM, 10);
        ArrayList<Vector> b = instance.computeObservationLikelihoods(observations);
        boolean normalize = true;
        ArrayList<WeightedValue<Vector>> normalizedAlphas =
            instance.computeForwardProbabilities(b,normalize);
        double logLikelihood1 = 0.0;
        for( WeightedValue<Vector> a : normalizedAlphas )
        {
            logLikelihood1 -= Math.log(a.getWeight());
            System.out.println( a.getWeight() + ": " + a.getValue() );
            assertEquals( 1.0, a.getValue().norm1(), TOLERANCE );
        }

        normalize = false;
        ArrayList<WeightedValue<Vector>> unnormalizedAlphas =
            instance.computeForwardProbabilities(b,normalize);
        double logLikelihood2 = Math.log(
            unnormalizedAlphas.get(observations.size()-1).getValue().norm1() );
        assertEquals( logLikelihood1, logLikelihood2, TOLERANCE );

    }

    /**
     * Test of computeBackwardProbabilities method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testComputeBackwardProbabilities_ArrayList()
    {
        System.out.println("computeBackwardProbabilities");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        ArrayList<Vector> observations = instance.sample(RANDOM, 10);
        ArrayList<Vector> bs = instance.computeObservationLikelihoods(observations);
        boolean normalize = true;
        ArrayList<WeightedValue<Vector>> alphas =
            instance.computeForwardProbabilities(bs, normalize);
        ArrayList<WeightedValue<Vector>> normalizedBetas =
            instance.computeBackwardProbabilities(bs, alphas);
        double logLikelihood1 = 0.0;
        for( WeightedValue<Vector> b : normalizedBetas )
        {
            logLikelihood1 -= Math.log(b.getWeight());
            System.out.println( b.getWeight() + ": " + b.getValue() );
//            assertEquals( 1.0, b.getValue().norm1(), TOLERANCE );
        }

        normalize = false;
        alphas = instance.computeForwardProbabilities(bs, normalize);
        ArrayList<WeightedValue<Vector>> unnormalizedBetas =
            instance.computeBackwardProbabilities(bs, alphas);
        Vector beta0 = unnormalizedBetas.get(0).getValue();
        double logLikelihood2 = Math.log(beta0.norm1());
//        assertEquals( logLikelihood1, logLikelihood2, TOLERANCE );

    }

    /**
     * Test of createUniformInitialProbability method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testCreateUniformInitialProbability()
    {
        System.out.println("createUniformInitialProbability");
        int numStates = RANDOM.nextInt(10) + 1;
        Vector result =
            HiddenMarkovModel.createUniformInitialProbability(numStates);
        double p = 1.0/numStates;
        assertEquals( numStates, result.getDimensionality() );
        for( int i = 0; i < numStates; i++ )
        {
            assertEquals( p, result.getElement(i) );
        }
        
    }

    /**
     * Test of createUniformTransitionProbability method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testCreateUniformTransitionProbability()
    {
        System.out.println("createUniformTransitionProbability");
        int numStates = RANDOM.nextInt(10) + 1;
        double p = 1.0/numStates;
        Matrix result =
            HiddenMarkovModel.createUniformTransitionProbability(numStates);
        assertEquals( numStates, result.getNumRows() );
        assertEquals( numStates, result.getNumColumns() );

        for( int j = 0; j < numStates; j++ )
        {
            for( int i = 0; i < numStates; i++ )
            {
                assertEquals( p, result.getElement(i, j) );
            }
        }

    }
 
    /**
     * Test of getNumStates method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testGetNumStates()
    {
        System.out.println("getNumStates");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        assertEquals( DEFAULT_NUM_STATES, instance.getNumStates() );
    }

    /**
     * Test of toString method, of class ContinuousDensityHiddenMarkovModel.
     */
    @Override
    public void testToString()
    {
        System.out.println("toString");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        String s = instance.toString();
        System.out.println( "toSring: " + s );
        assertNotNull( s );
    }

    /**
     * Test of getInitialProbability method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testGetInitialProbability()
    {
        System.out.println("getInitialProbability");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        assertEquals( 1.0, instance.getInitialProbability().norm1(), TOLERANCE );
    }

    /**
     * Test of setInitialProbability method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testSetInitialProbability()
    {
        System.out.println("setInitialProbability");
        HiddenMarkovModel<Vector> instance = this.createInstance();

        Vector p = instance.getInitialProbability().clone();
        instance.setInitialProbability(p);
        assertSame( p, instance.getInitialProbability() );

        Vector p2 = p.scale(2.0);
        Vector p2clone = p2.clone();
        instance.setInitialProbability(p2);
        assertEquals( 1.0, instance.getInitialProbability().norm1() );
        assertFalse( p2.equals( p2clone ) );
        assertEquals( p2, p2clone.scale(1.0/p2clone.norm1() ) );

        p.setElement(0, -1.0);
        try
        {
            instance.setInitialProbability(p);
            fail( "initial probability must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setInitialProbability(null);
            fail( "Cannot be null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getTransitionProbability method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testGetTransitionProbability()
    {
        System.out.println("getTransitionProbability");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        for( int j = 0; j < instance.getNumStates(); j++ )
        {
            assertEquals( 1.0, instance.getTransitionProbability().getColumn(j).norm1(), TOLERANCE );
        }
    }

    /**
     * Test of setTransitionProbability method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testSetTransitionProbability()
    {
        System.out.println("setTransitionProbability");
        HiddenMarkovModel<Vector> instance = this.createInstance();

        Matrix A = instance.getTransitionProbability();
        Matrix A2 = A.scale(2.0);
        Matrix A2clone = A2.clone();
        instance.setTransitionProbability(A2);
        for( int j = 0; j < instance.getNumStates(); j++ )
        {
            Vector Aj = instance.getTransitionProbability().getColumn(j);
            Vector A2clonej = A2clone.getColumn(j);
            assertFalse( Aj.equals(A2clonej) );
            assertEquals( Aj, A2clonej.scale(1.0/A2clonej.norm1() ) );
        }

        A2.setElement(0, 0, -1.0);
        try
        {
            instance.setTransitionProbability(A2);
            fail( "Transition Probabilities must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setTransitionProbability(null);
            fail( "Cannot be null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getEmissionFunctions method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testGetEmissionFunctions()
    {
        System.out.println("getEmissionFunctions");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        assertEquals( instance.getNumStates(), instance.getEmissionFunctions().size() );
    }

    /**
     * Test of setEmissionFunctions method, of class ContinuousDensityHiddenMarkovModel.
     */
    public void testSetEmissionFunctions()
    {
        System.out.println("setEmissionFunctions");
        HiddenMarkovModel<Vector> instance = this.createInstance();
        Collection<? extends ProbabilityFunction<Vector>> emissionFunctions = instance.getEmissionFunctions();
        assertNotNull( emissionFunctions );
        instance.setEmissionFunctions(null);
        assertNull( instance.getEmissionFunctions() );
        instance.setEmissionFunctions(emissionFunctions);
        assertSame( emissionFunctions, instance.getEmissionFunctions() );
    }

    /**
     * Tests getSteadyStateDistribution
     */
    public void testGetSteadyStateDistribution()
    {
        System.out.println( "getSteadyStateDistribution" );

        HiddenMarkovModel<Vector> instance = this.createInstance();

        Vector phat = instance.getSteadyStateDistribution();

        EigenDecompositionRightMTJ evd = EigenDecompositionRightMTJ.create(
            (DenseMatrix) instance.getTransitionProbability() );

        Vector p = evd.getEigenVectorsRealPart().getColumn(0);
        // We do the manual sum (instead of norm1) in case the EVD found
        // the negative of the eigenvector.
        double sum = 0.0;
        for( int i = 0; i < p.getDimensionality(); i++ )
        {
            sum += p.getElement(i);
        }
        p.scaleEquals( 1.0/sum );

        System.out.println( "P: " + p );
        System.out.println( "Phat: " + phat );
        assertTrue( p.equals( phat, TOLERANCE ) );

    }

    @Override
    public void testGetMean()
    {
        System.out.println( "getMean" );
        
        int temp = NUM_SAMPLES;
        NUM_SAMPLES = 10000;
        double tt = TOLERANCE;
        TOLERANCE = 1e-2;
        HiddenMarkovModel<Vector> instance = this.createInstance();
        Collection<Vector> samples = instance.sample(RANDOM, NUM_SAMPLES);
        Vector sampleMean = MultivariateStatisticsUtil.computeMean(samples);
        Vector result = instance.getMean();
        if( !result.equals( sampleMean, TOLERANCE ) )
        {
            assertEquals( result, sampleMean );
        }

        HiddenMarkovModel<Double> hmm2 = new HiddenMarkovModel<Double>();
        int k = hmm2.getNumStates();
        ArrayList<UnivariateGaussian.PDF> pdfs =
            new ArrayList<UnivariateGaussian.PDF>( k );
        for( int i = 0; i < k; i++ )
        {
            UnivariateGaussian.PDF f = new UnivariateGaussian.PDF(
                RANDOM.nextGaussian(), RANDOM.nextDouble() );
            pdfs.add( f );
        }
        hmm2.setEmissionFunctions(pdfs);

        Collection<Double> samples2 = hmm2.sample(RANDOM, NUM_SAMPLES );
        Double sampleMean2 = UnivariateStatisticsUtil.computeMean(samples2);
        Double result2 = hmm2.getMean();
        assertEquals( result2, sampleMean2, TOLERANCE );

        TOLERANCE = tt;
        NUM_SAMPLES = temp;

        HiddenMarkovModel<String> hmm3 = new HiddenMarkovModel<String>();
        k = hmm3.getNumStates();
        ArrayList<MapBasedDataHistogram<String>> sf =
            new ArrayList<MapBasedDataHistogram<String>>( k );
        for( int i = 0; i < k; i++ )
        {
            MapBasedDataHistogram<String> f = new MapBasedDataHistogram<String>();
            f.add( "a" );
            sf.add( f );
        }
        hmm3.setEmissionFunctions(sf);
        try
        {
            String sm = hmm3.getMean();
            fail( "Mean not implemented for Strings" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * viterbi
     */
    public void testViterbi()
    {
        System.out.println( "viterbi" );

        HiddenMarkovModel<Vector> instance = this.createInstance();
        Collection<Vector> samples = instance.sample(RANDOM, 10);
        ArrayList<Integer> states = instance.viterbi(samples);
        assertEquals( samples.size(), states.size() );
        for( Integer state : states )
        {
            System.out.print( state + "->" );
            assertTrue( 0 <= state );
            assertTrue( state < instance.getNumStates() );
        }

        System.out.println( " Done" );

        double all = instance.computeObservationLogLikelihood(samples);
        double ml = instance.computeObservationLogLikelihood(samples, states);
        System.out.println( "All sequences: " + all );
        System.out.println( "ML  sequence:  " + ml );
        assertTrue( ml <= all );
        assertEquals( -13.051414871301235, ml, TOLERANCE );

    }

    /**
     * createRandom
     */
    public void testCreateRandom()
    {
        System.out.println( "createRandom" );

        Collection<String> data = Arrays.asList( "a", "b", "c", "d", "a" );
        MapBasedPointMassDistribution.Learner<String> learner =
            new MapBasedPointMassDistribution.Learner<String>();
        HiddenMarkovModel<String> hmm = HiddenMarkovModel.createRandom(
            5, learner, data, RANDOM );
        System.out.println( "HMM:\n" + hmm );


        BinomialDistribution distribution =
            new BinomialDistribution( 10, RANDOM.nextDouble() );
        HiddenMarkovModel<Number> hmm2 = HiddenMarkovModel.createRandom(
            4, distribution, RANDOM );
        System.out.println( "HMM2:\n" + hmm2 );

    }

}
