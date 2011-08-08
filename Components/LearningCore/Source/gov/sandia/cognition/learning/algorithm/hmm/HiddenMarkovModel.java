/*
 * File:                HiddenMarkovModel.java
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
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.DirichletDistribution;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 * A discrete-state Hidden Markov Model (HMM) with either continuous
 * or discrete observations.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType> Type of Observations handled by the HMM.
 */
@PublicationReference(
    author="Lawrence R. Rabiner",
    title="A tutorial on hidden Markov models and selected applications in speech recognition",
    type=PublicationType.Journal,
    year=1989,
    publication="Proceedings of the IEEE",
    pages={257,286},
    url="http://www.cs.ubc.ca/~murphyk/Bayes/rabiner.pdf",
    notes="Rabiner's transition matrix is transposed from mine."
)
public class HiddenMarkovModel<ObservationType>
    extends MarkovChain
    implements Distribution<ObservationType>
{

    /**
     * The PDFs that emit symbols from each state.
     */
    protected Collection<? extends ProbabilityFunction<ObservationType>> emissionFunctions;

    /**
     * Default constructor.
     */
    public HiddenMarkovModel()
    {
        super();
    }

    /**
     * Creates a new instance of ContinuousDensityHiddenMarkovModel
     * @param numStates
     * Number of states in the HMM.
     */
    public HiddenMarkovModel(
        int numStates )
    {
        super( numStates );
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
     * @param emissionFunctions
     * The PDFs that emit symbols from each state.
     */
    public HiddenMarkovModel(
        Vector initialProbability,
        Matrix transitionProbability,
        Collection<? extends ProbabilityFunction<ObservationType>> emissionFunctions )
    {
        super( initialProbability, transitionProbability );
        final int k = this.getNumStates();
        if( emissionFunctions.size() != k )
        {
            throw new IllegalArgumentException(
                "Number of PDFs must be equal to number of states!" );
        }

        this.setEmissionFunctions(emissionFunctions);

    }

    /**
     * Creates a Hidden Markov Model with the same PMF/PDF for each state,
     * but sampling the columns of the transition matrix and the initial
     * probability distributions from a diffuse Dirichlet.
     * @param <ObservationType>
     * Type of observations to generate.
     * @param numStates
     * Number of states to create
     * @param learner
     * Learner to create the distributions for each state
     * @param data
     * Data from which to make the distribution
     * @param random
     * Random number generator
     * @return
     * HMM with the specified states.
     */
    public static <ObservationType> HiddenMarkovModel<ObservationType> createRandom(
        int numStates,
        BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> learner,
        Collection<? extends ObservationType> data,
        Random random )
    {
        ArrayList<DefaultWeightedValue<ObservationType>> weightedData =
            new ArrayList<DefaultWeightedValue<ObservationType>>( data.size() );
        for( ObservationType observation : data )
        {
            weightedData.add( new DefaultWeightedValue<ObservationType>( observation, 1.0 ) );
        }
        ComputableDistribution<ObservationType> distribution =
            learner.learn(weightedData);
        return createRandom( numStates, distribution, random );
        
    }

    /**
     * Creates a Hidden Markov Model with the same PMF/PDF for each state,
     * but sampling the columns of the transition matrix and the initial
     * probability distributions from a diffuse Dirichlet.
     * @param <ObservationType>
     * Type of observations to generate.
     * @param numStates
     * Number of states to create
     * @param distribution
     * Distribution from which we will assign to each state.
     * @param random
     * Random number generator
     * @return
     * HMM with the specified states.
     */
    public static <ObservationType> HiddenMarkovModel<ObservationType> createRandom(
        int numStates,
        ComputableDistribution<ObservationType> distribution,
        Random random )
    {
        Collection<ProbabilityFunction<ObservationType>> distributions =
            Collections.nCopies(numStates, distribution.getProbabilityFunction());
        return createRandom(distributions, random);
    }

    /**
     * Creates a Hidden Markov Model with the given probability function for
     * each state, but sampling the columns of the transition matrix and the
     * initial probability distributions from a diffuse Dirichlet.
     * @param <ObservationType>
     *      Type of observations to generate.
     * @param distributions
     *      The distribution for each state. The size of the collection is the
     *      number of states to create.
     * @param random
     * Random number generator
     * @return
     * HMM with the specified states.
     */
    public static <ObservationType> HiddenMarkovModel<ObservationType> createRandom(
        Collection<? extends ProbabilityFunction<ObservationType>> distributions,
        Random random)
    {
        int numStates = distributions.size();

        // We'll sample the multinomial probabilities from a uniform Dirichlet
        DirichletDistribution dirichlet =
            new DirichletDistribution( numStates );

        Vector initialProbability = dirichlet.sample(random);
        Matrix transitionMatrix =
            MatrixFactory.getDefault().createMatrix(numStates, numStates);
        ArrayList<Vector> outbounds = dirichlet.sample(random, numStates);
        for( int i = 0; i < numStates; i++ )
        {
            transitionMatrix.setColumn( i, outbounds.get(i) );
        }

        return new HiddenMarkovModel<ObservationType>(
            initialProbability,transitionMatrix, distributions);
    }

    @Override
    public HiddenMarkovModel<ObservationType> clone()
    {
        @SuppressWarnings("unchecked")
        HiddenMarkovModel<ObservationType>  clone =
            (HiddenMarkovModel<ObservationType>) super.clone();
        clone.setEmissionFunctions( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getEmissionFunctions() ) );
        return clone;
    }

    /**
     * Computes the log-likelihood of the observation sequence, given the
     * current HMM's parameterization.  This is the answer to Rabiner's
     * "Three Basic Problems for HMMs, Problem 1: Probability Evaluation".
     * @param observations
     * Observations to consider.
     * @return
     * Log-likelihood of the given observation sequence.
     */
    public double computeObservationLogLikelihood(
        Collection<? extends ObservationType> observations )
    {

        ArrayList<Vector> b = this.computeObservationLikelihoods(observations);
        final boolean normalize = true;
        ArrayList<WeightedValue<Vector>> alphas =
            this.computeForwardProbabilities(b, normalize);

        double logLikelihood = 0.0;
        for( WeightedValue<Vector> alpha : alphas )
        {
            logLikelihood -= Math.log( alpha.getWeight() );
        }

        return logLikelihood;

    }

    /**
     * Computes the log-likelihood of the observation sequences, given the
     * current HMM's parameterization.  This is the answer to Rabiner's
     * "Three Basic Problems for HMMs, Problem 1: Probability Evaluation".
     * @param sequences
     * Observations sequences to consider
     * @return
     * Log-likelihood of the given observation sequence.
     */
    public double computeMultipleObservationLogLikelihood(
        Collection<? extends Collection<? extends ObservationType>> sequences )
    {
        double logLikelihood = 0.0;
        for( Collection<? extends ObservationType> observations : sequences )
        {
            logLikelihood += this.computeObservationLogLikelihood(observations);
        }
        return logLikelihood;
    }

    /**
     * Computes the log-likelihood that the given observation sequence
     * was generated by the given sequence of state indices.
     * @param observations
     * Observations to consider.
     * @param states
     * Indices of states hypothesized to have generated the observation
     * sequence.
     * @return
     * Log-likelihood of the given observation sequence.
     */
    public double computeObservationLogLikelihood(
        Collection<? extends ObservationType> observations,
        Collection<Integer> states )
    {

        final int N = observations.size();
        if( N != states.size() )
        {
            throw new IllegalArgumentException(
                "Observations and states must be the same size" );
        }

        Iterator<Integer> stateIterator = states.iterator();
        double logLikelihood = 0.0;
        ArrayList<ProbabilityFunction<ObservationType>> fs =
            new ArrayList<ProbabilityFunction<ObservationType>>( this.emissionFunctions );
        int lastState = -1;
        for( ObservationType observation : observations )
        {
            final int state = stateIterator.next();
            double blog = fs.get(state).logEvaluate(observation);
            double ll;
            if( lastState < 0 )
            {
                ll = Math.log(this.initialProbability.getElement(state));
            }
            else
            {
                ll = Math.log(this.transitionProbability.getElement(state, lastState) );
            }

            lastState = state;

            logLikelihood += blog + ll;

        }

        return logLikelihood;

    }

    public ObservationType sample(
        Random random)
    {
        return CollectionUtil.getFirst( this.sample(random, 1) );
    }

    public ArrayList<ObservationType> sample(
        Random random,
        int numSamples )
    {

        ArrayList<ObservationType> samples =
            new ArrayList<ObservationType>( numSamples );
        Vector p = this.getInitialProbability();
        int state = -1;
        for( int n = 0; n < numSamples; n++ )
        {
            double value = random.nextDouble();
            state = -1;
            while( value > 0.0 )
            {
                state++;
                value -= p.getElement(state);
            }

            ObservationType sample = CollectionUtil.getElement(
                this.getEmissionFunctions(), state ).sample(random);
            samples.add( sample );
            p = this.getTransitionProbability().getColumn(state);
        }

        return samples;

    }

    /**
     * Getter for emissionFunctions
     * @return
     * The PDFs that emit symbols from each state.
     */
    public Collection<? extends ProbabilityFunction<ObservationType>> getEmissionFunctions()
    {
        return this.emissionFunctions;
    }

    /**
     * Setter for emissionFunctions.
     * @param emissionFunctions
     * The PDFs that emit symbols from each state.
     */
    public void setEmissionFunctions(
        Collection<? extends ProbabilityFunction<ObservationType>> emissionFunctions)
    {
        this.emissionFunctions = emissionFunctions;
    }

    @SuppressWarnings("unchecked")
    public ObservationType getMean()
    {

        Vector p = this.getSteadyStateDistribution();

        ObservationType observation =
            CollectionUtil.getFirst(this.emissionFunctions).getMean();
        if( observation instanceof Ring )
        {
            RingAccumulator weightedAverage = new RingAccumulator();
            int i = 0;
            for( ProbabilityFunction<ObservationType> f : this.emissionFunctions )
            {
                Ring mean = (Ring) f.getMean();
                weightedAverage.accumulate( mean.scale( p.getElement(i) ) );
                i++;
            }
            return (ObservationType) weightedAverage.getSum();
        }
        else if( observation instanceof Number )
        {
            double weightedAverage = 0.0;
            int i = 0;
            for( ProbabilityFunction<ObservationType> f : this.emissionFunctions )
            {
                Number mean = (Number) f.getMean();
                weightedAverage += mean.doubleValue() * p.getElement(i);
                i++;
            }
            return (ObservationType) (new Double( weightedAverage ));
        }
        else
        {
            throw new UnsupportedOperationException(
                "Mean not supported for type: " + observation.getClass() );
        }
    }

    /**
     * Computes the recursive solution to the forward probabilities of the
     * HMM.
     * @param alpha
     * Previous alpha value.
     * @param b
     * Current observation-emission likelihood.
     * @param normalize
     * True to normalize the alphas, false to leave them unnormalized.
     * @return
     * Alpha with the associated weighting (will be 1 if unnormalized).
     */
    protected WeightedValue<Vector> computeForwardProbabilities(
        Vector alpha,
        Vector b,
        boolean normalize )
    {
        Vector alphaNext =
            b.dotTimes( this.getTransitionProbability().times( alpha ) );

        double weight;
        if( normalize )
        {
            weight = 1.0/alphaNext.norm1();
            alphaNext.scaleEquals(weight);
        }
        else
        {
            weight = 1.0;
        }

        return new DefaultWeightedValue<Vector>( alphaNext, weight );

    }


    /**
     * Computes the forward probabilities for the given observation likelihood
     * sequence.
     * @param b
     * Observation likelihood sequence.
     * @param normalize
     * True to normalize the alphas, false to leave them unnormalized.
     * @return
     * Forward probability alphas, with their associated weights.
     */
    protected ArrayList<WeightedValue<Vector>> computeForwardProbabilities(
        ArrayList<Vector> b,
        boolean normalize )
    {
        final int N = b.size();
        ArrayList<WeightedValue<Vector>> weightedAlphas =
            new ArrayList<WeightedValue<Vector>>( N );
        Vector alpha = b.get(0).dotTimes( this.getInitialProbability() );
        double weight;
        if( normalize )
        {
            weight = 1.0/alpha.norm1();
            alpha.scaleEquals(weight);
        }
        else
        {
            weight = 1.0;
        }
        WeightedValue<Vector> weightedAlpha =
            new DefaultWeightedValue<Vector>(alpha,weight);
        weightedAlphas.add( weightedAlpha );
        for( int n = 1; n < N; n++ )
        {
            weightedAlpha = this.computeForwardProbabilities(
                weightedAlpha.getValue(), b.get(n), normalize );
            weightedAlphas.add( weightedAlpha );
        }

        return weightedAlphas;
    }

    /**
     * Computes the conditionally independent likelihoods
     * for each state given the observation.
     * @param observation
     * Observation to consider
     * @return
     * Likelihood of each state generating the given observation.
     */
    protected Vector computeObservationLikelihoods(
        ObservationType observation )
    {

        final int k = this.getEmissionFunctions().size();
        double[] b = new double[k];
        int i = 0;
        for( ProbabilityFunction<ObservationType> f : this.getEmissionFunctions() )
        {
            b[i] = f.evaluate(observation);
            i++;
        }

        return VectorFactory.getDefault().copyArray(b);

    }

    /**
     * Computes the conditionally independent likelihoods
     * for each state given the observation sequence.
     * @param observations
     * Observation sequence to consider
     * @return
     * Likelihood of each state generating the given observation sequence.
     */
    protected ArrayList<Vector> computeObservationLikelihoods(
        Collection<? extends ObservationType> observations )
    {
        final int N = observations.size();
        ArrayList<Vector> bs = new ArrayList<Vector>( N );
        for( ObservationType observation : observations )
        {
            bs.add( this.computeObservationLikelihoods(observation) );
        }

        return bs;
    }

    /**
     * Computes the backward probability recursion.
     * @param beta
     * Beta from the "next" time step.
     * @param b
     * Observation likelihood from the "next" time step.
     * @param weight
     * Weight to use for the current time step.
     * @return
     * Beta for the previous time step, weighted by "weight".
     */
    public WeightedValue<Vector> computeBackwardProbabilities(
        Vector beta,
        Vector b,
        double weight )
    {
        Vector betaPrevious = b.dotTimes(beta);
        betaPrevious = betaPrevious.times( this.getTransitionProbability() );
        if( weight != 1.0 )
        {
            betaPrevious.scaleEquals(weight);
        }
        return new DefaultWeightedValue<Vector>( betaPrevious, weight );
    }

    /**
     * Computes the backward-probabilities for the given observation likelihoods
     * and the weights from the alphas.
     * @param b
     * Observation likelihoods.
     * @param alphas
     * Forward probabilities from which we will use the weights.
     * @return
     * Backward probabilities.
     */
    public ArrayList<WeightedValue<Vector>> computeBackwardProbabilities(
        ArrayList<Vector> b,
        ArrayList<WeightedValue<Vector>> alphas )
    {
        final int N = b.size();
        final int k = this.getInitialProbability().getDimensionality();
        ArrayList<WeightedValue<Vector>> weightedBetas =
            new ArrayList<WeightedValue<Vector>>( N );
        for( int n = 0; n < N; n++ )
        {
            weightedBetas.add( null );
        }
        Vector beta = VectorFactory.getDefault().createVector(k, 1.0);
        double weight = alphas.get(N-1).getWeight();
        if( weight != 1.0 )
        {
            beta.scaleEquals(weight);
        }
        WeightedValue<Vector> weightedBeta =
            new DefaultWeightedValue<Vector>( beta, weight );
        weightedBetas.set( N-1, weightedBeta );
        for( int n = N-2; n >= 0; n-- )
        {
            weight = alphas.get(n).getWeight();
            weightedBeta = this.computeBackwardProbabilities(
                weightedBeta.getValue(), b.get(n+1), weight );
            weightedBetas.set( n, weightedBeta );
        }

        return weightedBetas;

    }

    /**
     * Computes the probability of the various states at a time instance given
     * the observation sequence.  Rabiner calls this the "gamma".
     * @param alpha
     * Forward probability at time n.
     * @param beta
     * Backward probability at time n.
     * @param scaleFactor
     * Amount to scale the gamma by
     * @return
     * Gamma at time n.
     */
    protected static Vector computeStateObservationLikelihood(
        Vector alpha,
        Vector beta,
        double scaleFactor )
    {
        Vector gamma = alpha.dotTimes(beta);
        gamma.scaleEquals(scaleFactor/gamma.norm1());
        return gamma;
    }

    /**
     * Computes the probabilities of the various states over time given the
     * observation sequence.  Rabiner calls these the "gammas".
     * @param alphas
     * Forward probabilities.
     * @param betas
     * Backward probabilities.
     * @param scaleFactor
     * Amount to scale the gamma by
     * @return
     * Gammas.
     */
    protected ArrayList<Vector> computeStateObservationLikelihood(
        ArrayList<WeightedValue<Vector>> alphas,
        ArrayList<WeightedValue<Vector>> betas,
        double scaleFactor )
    {
        final int N = alphas.size();
        ArrayList<Vector> gammas = new ArrayList<Vector>( N );
        for( int n = 0; n < N; n++ )
        {
            Vector alphan = alphas.get(n).getValue();
            Vector betan = betas.get(n).getValue();
            gammas.add( computeStateObservationLikelihood(
                alphan, betan, scaleFactor ) );

        }

        return gammas;
    }

    /**
     * Computes the stochastic transition-probability matrix from the
     * given probabilities.
     * @param alphan
     * Result of the forward pass through the HMM at time n
     * @param betanp1
     * Result of the backward pass through the HMM at time n+1
     * @param bnp1
     * Conditionally independent likelihoods of each observation at time n+1
     * @return
     * Transition probabilities at time n
     */
    protected static Matrix computeTransitions(
        Vector alphan,
        Vector betanp1,
        Vector bnp1 )
    {
        Vector bnext = bnp1.dotTimes(betanp1);
        return bnext.outerProduct(alphan);
    }

    /**
     * Computes the stochastic transition-probability matrix from the
     * given probabilities.
     * @param alphas
     * Result of the forward pass through the HMM.
     * @param betas
     * Result of the backward pass through the HMM.
     * @param b
     * Conditionally independent likelihoods of each observation.
     * @return
     * ML estimate of the transition probability Matrix over all time steps.
     */
    protected Matrix computeTransitions(
        ArrayList<WeightedValue<Vector>> alphas,
        ArrayList<WeightedValue<Vector>> betas,
        ArrayList<Vector> b )
    {
        final int N = b.size();
        RingAccumulator<Matrix> counts = new RingAccumulator<Matrix>();
        for( int n = 0; n < N-1; n++ )
        {
            Vector alpha = alphas.get(n).getValue();
            Vector beta = betas.get(n+1).getValue();
            counts.accumulate(
                computeTransitions( alpha, beta, b.get(n+1) ) );
        }

        Matrix A = counts.getSum();
        A.dotTimesEquals(this.getTransitionProbability());
        this.normalizeTransitionMatrix(A);

        return A;

    }

    @Override
    public String toString()
    {

        StringBuilder retval = new StringBuilder( super.toString() );
        for( ProbabilityFunction<ObservationType> f : this.getEmissionFunctions() )
        {
            retval.append( "F: " + f );
        }

        return retval.toString();
    }

    /**
     * Finds the most-likely next state given the previous "delta" in the
     * Viterbi algorithm.
     * @param destinationState
     * Destination state index to consider.
     * @param delta
     * Previous value of the "delta".
     * @return
     * Most-likely previous state, weighted by its likelihood.
     */
    protected WeightedValue<Integer> findMostLikelyState(
        int destinationState, Vector delta )
    {
        double best = Double.NEGATIVE_INFINITY;
        int index = -1;
        double dj;
        final int k = delta.getDimensionality();
        for( int j = 0; j < k; j++ )
        {
            dj = this.transitionProbability.getElement(destinationState,j) * delta.getElement(j);
            if( best < dj )
            {
                best = dj;
                index = j;
            }
        }

        return new DefaultWeightedValue<Integer>( index, best );

    }

    /**
     * Computes the Viterbi recursion for a given "delta" and "b"
     * @param delta
     * Previous value of the Viterbi recursion.
     * @param bn
     * Current observation likelihood.
     * @return
     * Updated "delta" and state backpointers.
     */
    protected Pair<Vector,int[]> computeViterbiRecursion(
        Vector delta,
        Vector bn )
    {

        final int k = delta.getDimensionality();
        final Vector dn = VectorFactory.getDefault().createVector(k);
        final int[] psi = new int[ k ];
        for( int i = 0; i < k; i++ )
        {
            WeightedValue<Integer> transition =
                this.findMostLikelyState(i, delta);
            psi[i] = transition.getValue();
            dn.setElement(i, transition.getWeight());
        }
        dn.dotTimesEquals( bn );
        delta = dn;
        delta.scaleEquals( 1.0/delta.norm1() );

        return new DefaultPair<Vector,int[]>( delta, psi );

    }

    /**
     * Viterbi algorithm for decoding the most-likely sequence of states
     * from the HMMs underlying Markov chain for a given observation sequence.
     * @param observations
     * Observation sequence to consider
     * @return
     * Indices of the most-likely state sequence that generated the given
     * observations.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Viterbi algorithm",
        year=2010,
        type=PublicationType.WebPage,
        url="http://en.wikipedia.org/wiki/Viterbi_algorithm"
    )
    public ArrayList<Integer> viterbi(
        Collection<? extends ObservationType> observations )
    {
        
        final int N = observations.size();
        final int k = this.getNumStates();
        ArrayList<Vector> bs = this.computeObservationLikelihoods(observations);
        Vector delta = this.getInitialProbability().dotTimes( bs.get(0) );
        ArrayList<int[]> psis = new ArrayList<int[]>( N );
        int[] psi = new int[ k ];
        for( int i = 0; i < k; i++ )
        {
            psi[i] = 0;
        }
        psis.add( psi );
        ArrayList<Integer> states = new ArrayList<Integer>( N );
        states.add( null );
        for( int n = 1; n < N; n++ )
        {
            states.add( null );
            Pair<Vector,int[]> pair =
                this.computeViterbiRecursion( delta, bs.get(n) );
            delta = pair.getFirst();
            psis.add( pair.getSecond() );
        }

        // Backchaining
        int finalState = -1;
        double best = Double.NEGATIVE_INFINITY;
        for( int i = 0; i < k; i++ )
        {
            final double v = delta.getElement(i);
            if( best < v )
            {
                best = v;
                finalState = i;
            }
        }
        int state = finalState;
        states.set(N-1, state);
        for( int n = N-2; n >= 0; n-- )
        {
            state = psis.get(n+1)[state];
            states.set(n, state);
        }

        return states;

    }

}
