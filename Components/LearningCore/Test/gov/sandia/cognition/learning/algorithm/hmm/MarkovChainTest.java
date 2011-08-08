/*
 * File:                MarkovChainTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 3, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.hmm;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MarkovChainTest.
 *
 * @author krdixon
 */
public class MarkovChainTest
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
     * Tests for class MarkovChainTest.
     * @param testName Name of the test.
     */
    public MarkovChainTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance.
     * @return
     * Markov chain
     */
    public MarkovChain createInstance()
    {
        int numStates = RANDOM.nextInt( 10 ) + 3;
        Matrix A = MatrixFactory.getDefault().createUniformRandom(
            numStates, numStates, 0.0, 1.0, RANDOM);
        Vector pi = VectorFactory.getDefault().createUniformRandom(
            numStates, 0.0, 1.0, RANDOM);
        pi.scaleEquals(1.0/pi.norm1());
        MarkovChain retval = new MarkovChain( numStates );
        retval.normalizeTransitionMatrix(A);
        retval.setTransitionProbability(A);
        retval.setInitialProbability(pi);
        return retval;
    }

    /**
     * Tests the constructors of class MarkovChainTest.
     */
    public void testMarkovChainConstructors()
    {
        System.out.println( "Constructors" );

        MarkovChain f = new MarkovChain();
        assertEquals( MarkovChain.DEFAULT_NUM_STATES, f.getNumStates() );
        assertEquals( 1.0, f.getInitialProbability().norm1(), TOLERANCE );
        for( int j = 0; j < f.getNumStates(); j++ )
        {
            assertEquals( 1.0, f.getTransitionProbability().getColumn(j).norm1(), TOLERANCE );
        }

        int k = RANDOM.nextInt(10) + 10;
        f = new MarkovChain( k );
        assertEquals( k, f.getNumStates() );
        assertEquals( 1.0, f.getInitialProbability().norm1(), TOLERANCE );
        for( int j = 0; j < f.getNumStates(); j++ )
        {
            assertEquals( 1.0, f.getTransitionProbability().getColumn(j).norm1(), TOLERANCE );
        }

        MarkovChain f2 = new MarkovChain(
            f.getInitialProbability(), f.getTransitionProbability() );
        assertSame( f.getInitialProbability(), f2.getInitialProbability() );
        assertSame( f.getTransitionProbability(), f2.getTransitionProbability() );

        Matrix A = MatrixFactory.getDefault().createUniformRandom(
            f.getNumStates(), 1, 0.0, 1.0, RANDOM);
        try
        {
            MarkovChain f3 = new MarkovChain( f2.getInitialProbability(), A );
            fail( "Transition matrix must be square" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clone method, of class MarkovChain.
     */
    public void testClone()
    {
        System.out.println("clone");
        MarkovChain instance = this.createInstance();
        MarkovChain clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getInitialProbability(), clone.getInitialProbability() );
        assertEquals( instance.getInitialProbability(), clone.getInitialProbability() );
        assertNotSame( instance.getTransitionProbability(), clone.getTransitionProbability() );
        if( !instance.getTransitionProbability().equals( clone.getTransitionProbability(), TOLERANCE ) )
        {
            assertEquals( instance.getTransitionProbability(), clone.getTransitionProbability() );
        }
    }

    /**
     * Test of createUniformInitialProbability method, of class MarkovChain.
     */
    public void testCreateUniformInitialProbability()
    {
        System.out.println("createUniformInitialProbability");
        int numStates = RANDOM.nextInt(10) + 2;
        Vector result = MarkovChain.createUniformInitialProbability(numStates);
        assertEquals( 1.0, result.norm1(), TOLERANCE );
        for( int i = 0; i < numStates; i++ )
        {
            assertEquals( 1.0/numStates, result.getElement(i) );
        }
    }

    /**
     * Test of createUniformTransitionProbability method, of class MarkovChain.
     */
    public void testCreateUniformTransitionProbability()
    {
        System.out.println("createUniformTransitionProbability");
        int numStates = RANDOM.nextInt(10) + 2;
        Matrix result = MarkovChain.createUniformTransitionProbability(numStates);
        assertEquals( numStates, result.getNumRows() );
        assertEquals( numStates, result.getNumColumns() );
        for( int j = 0; j < numStates; j++ )
        {
            assertEquals( 1.0, result.getColumn(j).norm1(), TOLERANCE );
            for( int i = 0; i < numStates; i++ )
            {
                assertEquals( 1.0/numStates, result.getElement(i, j), TOLERANCE );
            }
        }

    }

    /**
     * Test of getInitialProbability method, of class MarkovChain.
     */
    public void testGetInitialProbability()
    {
        System.out.println("getInitialProbability");
        MarkovChain instance = this.createInstance();
        Vector pi = instance.getInitialProbability();
        assertEquals( 1.0, pi.norm1(), TOLERANCE );
    }

    /**
     * Test of setInitialProbability method, of class MarkovChain.
     */
    public void testSetInitialProbability()
    {
        System.out.println("setInitialProbability");
        MarkovChain instance = this.createInstance();
        Vector pi = instance.getInitialProbability();
        assertEquals( 1.0, pi.norm1(), TOLERANCE );

        Vector p2 = pi.scale(RANDOM.nextDouble());
        instance.setInitialProbability(p2);
        assertSame( p2, instance.getInitialProbability() );
        assertEquals( 1.0, p2.norm1(), TOLERANCE );
    }

    /**
     * Test of getTransitionProbability method, of class MarkovChain.
     */
    public void testGetTransitionProbability()
    {
        System.out.println("getTransitionProbability");
        MarkovChain instance = this.createInstance();
        Matrix A = instance.getTransitionProbability();
        assertTrue( A.isSquare() );
        for( int j = 0; j < A.getNumColumns(); j++ )
        {
            assertEquals( 1.0, A.getColumn(j).norm1(), TOLERANCE );
        }
    }

    /**
     * Test of setTransitionProbability method, of class MarkovChain.
     */
    public void testSetTransitionProbability()
    {
        System.out.println("setTransitionProbability");
        MarkovChain instance = this.createInstance();
        Matrix A = instance.getTransitionProbability();
        assertTrue( A.isSquare() );
        for( int j = 0; j < A.getNumColumns(); j++ )
        {
            assertEquals( 1.0, A.getColumn(j).norm1(), TOLERANCE );
        }

        Matrix A2 = A.scale( RANDOM.nextDouble() );
        instance.setTransitionProbability(A2);
        assertSame( A2, instance.getTransitionProbability() );
        for( int j = 0; j < A.getNumColumns(); j++ )
        {
            assertEquals( 1.0, A.getColumn(j).norm1(), TOLERANCE );
        }

        Matrix A3 = MatrixFactory.getDefault().createUniformRandom(
            4, 3, 0.0, 1.0, RANDOM);
        try
        {
            instance.setTransitionProbability(A3);
            fail( "Must be square" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getNumStates method, of class MarkovChain.
     */
    public void testGetNumStates()
    {
        System.out.println("getNumStates");
        MarkovChain instance = this.createInstance();
        assertEquals( instance.getInitialProbability().getDimensionality(), instance.getNumStates() );
    }

    /**
     * Test of toString method, of class MarkovChain.
     */
    public void testToString()
    {
        System.out.println("toString");
        MarkovChain instance = this.createInstance();
        String result = instance.toString();
        System.out.println( "Result =\n" + result );
        assertTrue( result.length() > 0 );
    }

    /**
     * Test of getSteadyStateDistribution method, of class MarkovChain.
     */
    public void testGetSteadyStateDistribution()
    {
        System.out.println("getSteadyStateDistribution");
        MarkovChain instance = this.createInstance();

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

    /**
     * Test of getFutureStateDistribution method, of class MarkovChain.
     */
    public void testGetFutureStateDistribution()
    {
        System.out.println("getFutureStateDistribution");
        MarkovChain instance = this.createInstance();
        Vector expected = instance.transitionProbability.times(
            instance.getInitialProbability() );
        Vector result = instance.getFutureStateDistribution(
            instance.getInitialProbability(), 1);
        if( !expected.equals( result ) )
        {
            assertEquals( expected, result );
        }

        expected = instance.getInitialProbability();
        assertEquals( expected, instance.getFutureStateDistribution( instance.getInitialProbability(), -1 ) );
        assertEquals( expected, instance.getFutureStateDistribution( instance.getInitialProbability(),  0 ) );
        for( int i = 1; i < 100; i++ )
        {
            expected = instance.getTransitionProbability().times( expected );
            expected = expected.scale( 1.0/result.norm1() );
            result = instance.getFutureStateDistribution(
                instance.getInitialProbability(), i );
            if( !expected.equals( result, TOLERANCE ) )
            {
                assertEquals( expected, result );
            }
        }

    }

}
