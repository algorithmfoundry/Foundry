/*
 * File:                MarkovChain.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.hmm;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.math.matrix.decomposition.EigenvectorPowerIteration;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A Markov chain is a random process that has a finite number of states with
 * random transition probabilities between states at discrete time steps.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Markov chain",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Markov_chain"
)
public class MarkovChain 
    extends AbstractCloneableSerializable
{

    /**
     * Default number of states, {@value}.
     */
    public static final int DEFAULT_NUM_STATES = 3;

    /**
     * Initial probability Vector over the states.  Each entry must be
     * nonnegative and the Vector must sum to 1.
     */
    protected Vector initialProbability;

    /**
     * Transition probability matrix.  The entry (i,j) is the probability
     * of transition from state "j" to state "i".  As a corollary, all
     * entries in the Matrix must be nonnegative and the
     * columns of the Matrix must sum to 1.
     */
    protected Matrix transitionProbability;

    /**
     * Default constructor.
     */
    public MarkovChain()
    {
        this( DEFAULT_NUM_STATES );
    }

    /**
     * Creates a new instance of ContinuousDensityHiddenMarkovModel
     * with uniform initial and transition probabilities.  Also uses
     * Dirichlet PDFs as the emission functions.
     * @param numStates
     * Number of states to use.
     */
    public MarkovChain(
        int numStates )
    {
        this( createUniformInitialProbability(numStates),
            createUniformTransitionProbability(numStates) );
    }

    /**
     * Creates a new instance of ContinuousDensityHiddenMarkovModel
     * @param initialProbability
     * Initial probability Vector over the states.  Each entry must be
     * nonnegative and the Vector must sum to 1.
     * @param transitionProbability
     * Transition probability matrix.  The entry (i,j) is the probability
     * of transition from state "j" to state "i".  As a corollary, all
     * entries in the Matrix must be nonnegative and the
     * columns of the Matrix must sum to 1.
     */
    public MarkovChain(
        Vector initialProbability,
        Matrix transitionProbability )
    {

        if( !transitionProbability.isSquare() )
        {
            throw new IllegalArgumentException(
                "transitionProbability must be square!" );
        }

        final int k = transitionProbability.getNumRows();
        initialProbability.assertDimensionalityEquals( k );

        this.setTransitionProbability(transitionProbability);
        this.setInitialProbability(initialProbability);

    }

    @Override
    public MarkovChain clone()
    {
        MarkovChain clone = (MarkovChain) super.clone();

        clone.setInitialProbability(
            ObjectUtil.cloneSafe( this.getInitialProbability() ) );
        clone.setTransitionProbability(
            ObjectUtil.cloneSafe( this.getTransitionProbability() ) );
        return clone;
    }

    /**
     * Creates a uniform initial-probability Vector
     * @param numStates
     * Number of states to create the Vector for
     * @return
     * Uniform probability Vector.
     */
    protected static Vector createUniformInitialProbability(
        int numStates )
    {
        return VectorFactory.getDefault().createVector(numStates, 1.0/numStates);
    }

    /**
     * Creates a uniform transition-probability Matrix
     * @param numStates
     * Number of states to create the Matrix for
     * @return
     * Uniform probability Matrix.
     */
    protected static Matrix createUniformTransitionProbability(
        int numStates )
    {
        Matrix A = MatrixFactory.getDefault().createMatrix(numStates, numStates);
        final double p = 1.0/numStates;
        for( int i = 0; i < numStates; i++ )
        {
            for( int j = 0; j < numStates; j++ )
            {
                A.setElement(i, j, p);
            }
        }
        return A;
    }

    /**
     * Getter for initialProbability.
     * @return
     * Initial probability Vector over the states.  Each entry must be
     * nonnegative and the Vector must sum to 1.
     */
    public Vector getInitialProbability()
    {
        return this.initialProbability;
    }

    /**
     * Setter for initialProbability
     * @param initialProbability
     * Initial probability Vector over the states.  Each entry must be
     * nonnegative and the Vector must sum to 1.
     */
    public void setInitialProbability(
        Vector initialProbability)
    {
        final int k = initialProbability.getDimensionality();
        double sum = 0.0;
        for( int i = 0; i < k; i++ )
        {
            double value = initialProbability.getElement(i);
            if( value < 0.0 )
            {
                throw new IllegalArgumentException(
                    "Initial Probabilities must be >= 0.0" );
            }
            sum += value;
        }
        if( sum != 1.0 )
        {
            initialProbability.scaleEquals(1.0/sum);
        }
        this.initialProbability = initialProbability;
    }

    /**
     * Getter for transitionProbability.
     * @return
     * Transition probability matrix.  The entry (i,j) is the probability
     * of transition from state "j" to state "i".  As a corollary, all
     * entries in the Matrix must be nonnegative and the
     * columns of the Matrix must sum to 1.
     */
    public Matrix getTransitionProbability()
    {
        return this.transitionProbability;
    }

    /**
     * Setter for transitionProbability.
     * @param transitionProbability
     * Transition probability matrix.  The entry (i,j) is the probability
     * of transition from state "j" to state "i".  As a corollary, all
     * entries in the Matrix must be nonnegative and the
     * columns of the Matrix must sum to 1.
     */
    public void setTransitionProbability(
        Matrix transitionProbability)
    {
        if( !transitionProbability.isSquare() )
        {
            throw new IllegalArgumentException(
                "Transition Probability must be square" );
        }
        this.normalizeTransitionMatrix(transitionProbability);
        this.transitionProbability = transitionProbability;
    }

    /**
     * Normalizes this Markov chain.
     */
    public void normalize()
    {
        VectorUtil.divideByNorm1Equals(this.initialProbability);
        this.normalizeTransitionMatrix(this.transitionProbability);
    }

    /**
     * Normalizes a column of the transition-probability matrix
     * @param A
     * Transition probability matrix to normalize, modified by side effect
     * @param j
     * Column of the matrix to normalize
     */
    protected static void normalizeTransitionMatrix(
        Matrix A,
        final int j )
    {
        double sum = 0.0;
        final int k = A.getNumRows();
        for( int i = 0; i < k; i++ )
        {
            final double value = A.getElement(i, j);
            if( value < 0.0 )
            {
                throw new IllegalArgumentException(
                    "Transition Probabilities must be >= 0.0" );
            }
            sum += A.getElement(i,j);
        }
        if( sum <= 0.0 )
        {
            sum = 1.0;
        }
        if( sum != 1.0 )
        {
            for( int i = 0; i < k; i++ )
            {
                A.setElement( i, j, A.getElement(i,j)/sum );
            }
        }

    }

    /**
     * Normalizes the transition-probability matrix
     * @param A
     * Transition probability matrix to normalize, modified by side effect
     */
    // Note this method isn't static, because we want to override to make
    // it parallelized --krdixon
    protected void normalizeTransitionMatrix(
        Matrix A )
    {
        final int k = A.getNumColumns();
        for( int j = 0; j < k; j++ )
        {
            normalizeTransitionMatrix(A, j);
        }
    }

    /**
     * Gets the number of states in the HMM.
     * @return
     * Number of states in the HMM.
     */
    public int getNumStates()
    {
        return this.initialProbability.getDimensionality();
    }

    @Override
    public String toString()
    {
        StringBuilder retval = new StringBuilder( 100 * this.getNumStates() );
        retval.append( "Markov Chain has " + this.getNumStates() + " states:\n" );
        retval.append( "Initial: " + this.getInitialProbability() + "\n" );
        retval.append( "Transition:\n" + this.getTransitionProbability() );
        return retval.toString();
    }

    /**
     * Returns the steady-state distribution of the state distribution.
     * This is also the largest eigenvector of the transition-probability
     * matrix, which has an eigenvalue of 1.
     * @return
     * Steady-state probability distribution of state distribution.
     */
    public Vector getSteadyStateDistribution()
    {
        final double tolerance = 1e-5;
        final int maxIterations = 100;
        Vector p = EigenvectorPowerIteration.estimateEigenvector(
            this.initialProbability, this.transitionProbability,
            tolerance, maxIterations );

        // We do the manual sum (instead of norm1) in case the EVD found
        // the negative of the eigenvector.
        double sum = 0.0;
        for( int i = 0; i < p.getDimensionality(); i++ )
        {
            sum += p.getElement(i);
        }
        p.scaleEquals( 1.0/sum );
        return p;
    }
    
    /**
     * Simulates the Markov chain into the future, given the transition Matrix
     * and the given current state-probability distribution, for the
     * given number of time steps into the future.
     * @param current
     * Current distribution of probabilities of the various states.
     * @param numSteps
     * Number of steps into the future to simulate.
     * @return
     * State-probability distribution for numSteps into the future, starting
     * from the given state-probability distribution.
     */
    public Vector getFutureStateDistribution(
        Vector current,
        int numSteps )
    {

        Vector predicted = current;
        for( int n = 0; n < numSteps; n++ )
        {
            predicted = this.transitionProbability.times( predicted );
        }
        return predicted.scale( 1.0/predicted.norm1() );

    }


}
