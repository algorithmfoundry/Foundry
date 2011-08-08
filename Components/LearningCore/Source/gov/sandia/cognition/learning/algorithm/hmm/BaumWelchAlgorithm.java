/*
 * File:                BaumWelchAlgorithm.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.MultiCollection;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Implements the Baum-Welch algorithm, also known as the "forward-backward
 * algorithm", the expectation-maximization algorithm, etc for
 * Hidden Markov Models (HMMs).  This is the
 * standard learning algorithm for HMMs.  This implementation allows for
 * multiple sequences using the MultiCollection interface.
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
public class BaumWelchAlgorithm<ObservationType>
    extends AbstractBaumWelchAlgorithm<ObservationType,Collection<? extends ObservationType>>
{

    /**
     * Creates a new instance of BaumWelchAlgorithm
     */
    public BaumWelchAlgorithm()
    {
        this( null, null, DEFAULT_REESTIMATE_INITIAL_PROBABILITY );
    }

    /**
     * Creates a new instance of BaumWelchAlgorithm
     * @param initialGuess
     * Initial guess for the iterations.
     * @param distributionLearner
     * Learner for the Probability Functions of the HMM.
     * @param reestimateInitialProbabilities
     * Flag to re-estimate the initial probability Vector.
     */
    public BaumWelchAlgorithm(
        HiddenMarkovModel<ObservationType> initialGuess,
        BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> distributionLearner,
        boolean reestimateInitialProbabilities )
    {
        super( initialGuess, distributionLearner, reestimateInitialProbabilities );
    }

    @Override
    public BaumWelchAlgorithm<ObservationType> clone()
    {
        return (BaumWelchAlgorithm<ObservationType>) super.clone();
    }

    /**
     * Weighted data for the re-estimation procedure.
     */
    private transient ArrayList<DefaultWeightedValue<ObservationType>> weightedData;

    /**
     * Log likelihoods of the various sequences, with the corresponding weights.
     */
    private transient ArrayList<DefaultWeightedValue<Double>> sequenceLogLikelihoods;

    /**
     * Total number of observations in the sequences of data.
     */
    private transient int totalNum;

    /**
     * The multi-collection of sequences
     */
    protected transient MultiCollection<? extends ObservationType> multicollection;

    /**
     * The list of all gammas from each sequence
     */
    protected transient ArrayList<Vector> sequenceGammas;


    /**
     * Allows the algorithm to learn against multiple sequences of data.
     * @param data
     * Multiple sequences of data against which to train.
     * @return
     * HMM resulting from the locally maximum likelihood estimate of the
     * Baum-Welch algorithm.
     */
    public HiddenMarkovModel<ObservationType> learn(
        MultiCollection<ObservationType> data)
    {
        return super.learn(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initializeAlgorithm()
    {
        this.multicollection = DatasetUtil.asMultiCollection(this.data);

        // Let's make sure nobody uses the original data!
        this.data = null;


        final int numSequences = this.multicollection.getSubCollectionsCount();
        this.sequenceLogLikelihoods =
            new ArrayList<DefaultWeightedValue<Double>>( numSequences );
        this.totalNum = 0;
        for( Collection<? extends ObservationType> sequence : this.multicollection.subCollections() )
        {
            this.sequenceLogLikelihoods.add( new DefaultWeightedValue<Double>() );
            this.totalNum += sequence.size();
        }
        this.weightedData = new ArrayList<DefaultWeightedValue<ObservationType>>(
            this.totalNum );
        this.sequenceGammas = new ArrayList<Vector>( this.totalNum );
        for( Collection<? extends ObservationType> sequence : this.multicollection.subCollections() )
        {
            for( ObservationType observation : sequence )
            {
                this.weightedData.add( new DefaultWeightedValue<ObservationType>( observation ) );
                this.sequenceGammas.add( null );
            }
        }

        this.result = this.getInitialGuess().clone();
        this.lastLogLikelihood = this.updateSequenceLogLikelihoods( this.result );

        return (this.result != null);

    }

    @Override
    protected boolean step()
    {
        final int numSequences = this.multicollection.getSubCollectionsCount();
        final boolean updatePi = this.getReestimateInitialProbabilities();
        Pair<ArrayList<ArrayList<Vector>>, ArrayList<Matrix>> pair =
            this.computeSequenceParameters();
        ArrayList<ArrayList<Vector>> allGammas = pair.getFirst();
        ArrayList<Matrix> sequenceTransitionMatrices = pair.getSecond();
        ArrayList<Vector> firstGammas  = (updatePi)
            ? new ArrayList<Vector>( numSequences ) : null;

        int index = 0;
        for( int i = 0; i < numSequences; i++ )
        {
            ArrayList<Vector> gammas = allGammas.get(i);
            if( updatePi )
            {
                firstGammas.add( gammas.get(0) );
            }
            final int Ni = gammas.size();
            for( int n = 0; n < Ni; n++ )
            {
                this.sequenceGammas.set(index, gammas.get(n));
                index++;
            }
        }

        Vector pi = this.result.getInitialProbability();
        if( this.getReestimateInitialProbabilities() )
        {
            pi = this.updateInitialProbabilities(firstGammas);
        }
//        System.out.println( "Pi: " + pi );
        Matrix A = this.updateTransitionMatrix(sequenceTransitionMatrices);
        ArrayList<ProbabilityFunction<ObservationType>> fs =
            this.updateProbabilityFunctions(this.sequenceGammas);

        // See how well we're doing...
        boolean gettingBetter;
        
        // If somebody asks for a single iteration, then just assume they want
        // the re-estimated parameter
        if( this.getMaxIterations() <= 1 )
        {
            this.result.emissionFunctions = fs;
            this.result.initialProbability = pi;
            this.result.transitionProbability = A;
            gettingBetter = true;
        }

        // If somebody wants multiple steps, then check and see if our
        // current candidate is actually better.  This can happen due to
        // numerical round-off or asymptotic behavior.
        else
        {
            HiddenMarkovModel<ObservationType> candidate = this.result.clone();
            candidate.emissionFunctions = fs;
            candidate.initialProbability = pi;
            candidate.transitionProbability = A;
            double logLikelihood = this.updateSequenceLogLikelihoods( candidate );

            gettingBetter = (logLikelihood > this.lastLogLikelihood) ||
                (this.getIteration() <= 1);
            if( gettingBetter )
            {
                this.result = candidate;
                this.lastLogLikelihood = logLikelihood;
            }
        }

//        System.out.println( this.getIteration() + ": " + logLikelihood  );
        return gettingBetter;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.multicollection = null;
        this.weightedData = null;
        this.sequenceLogLikelihoods = null;
        this.totalNum = 0;
    }

    /**
     * Computes the gammas and A matrices for each sequence.
     * @return
     * Gammas and A matrices for each sequence
     */
    protected Pair<ArrayList<ArrayList<Vector>>,ArrayList<Matrix>> computeSequenceParameters()
    {

        final int numSequences = this.multicollection.getSubCollectionsCount();
        ArrayList<ArrayList<Vector>> allGammas =
            new ArrayList<ArrayList<Vector>>( numSequences );
        ArrayList<Matrix> sequenceTransitionMatrices =
            new ArrayList<Matrix>( numSequences );
        
        final boolean normalize = true;
        int k = 0;
        for( Collection<? extends ObservationType> sequence : this.multicollection.subCollections() )
        {
            double sequenceWeight = this.sequenceLogLikelihoods.get(k).getWeight();
            ArrayList<Vector> b =
                this.result.computeObservationLikelihoods( sequence );
            ArrayList<WeightedValue<Vector>> alphas =
                this.result.computeForwardProbabilities( b, normalize );
            ArrayList<WeightedValue<Vector>> betas =
                this.result.computeBackwardProbabilities( b, alphas );
            ArrayList<Vector> gammas =
                this.result.computeStateObservationLikelihood(alphas, betas, sequenceWeight);
            allGammas.add( gammas );
            Matrix A = this.result.computeTransitions( alphas, betas, b );
            if( sequenceWeight != 1.0 )
            {
                A.scaleEquals(sequenceWeight);
            }
            sequenceTransitionMatrices.add( A );
            k++;
        }

        return DefaultPair.create( allGammas, sequenceTransitionMatrices );
    }

    /**
     * Updates the probability function from the concatenated gammas from
     * all sequences
     * @param sequenceGammas
     * Concatenated gammas from all sequences
     * @return
     * Maximum Likelihood probability functions
     */
    protected ArrayList<ProbabilityFunction<ObservationType>> updateProbabilityFunctions(
        ArrayList<Vector> sequenceGammas )
    {
        final int numStates = this.result.getNumStates();
        ArrayList<ProbabilityFunction<ObservationType>> fs =
            new ArrayList<ProbabilityFunction<ObservationType>>( numStates );
        for( int i = 0; i < numStates; i++ )
        {
            int index = 0;
            for( int n = 0; n < sequenceGammas.size(); n++ )
            {
                final double g = sequenceGammas.get(n).getElement(i);
                this.weightedData.get(index).setWeight(g);
                index++;
            }
            ProbabilityFunction<ObservationType> f =
                this.distributionLearner.learn( this.weightedData ).getProbabilityFunction();
            fs.add( f );
        }
        return fs;
    }

    /**
     * Computes an updated transition matrix from the scaled estimates
     * @param sequenceTransitionMatrices
     * Scaled estimates from each sequence
     * @return
     * Overall Maximum Likelihood estimate of the transition matrix
     */
    protected Matrix updateTransitionMatrix(
        ArrayList<Matrix> sequenceTransitionMatrices )
    {
        RingAccumulator<Matrix> As =
            new RingAccumulator<Matrix>( sequenceTransitionMatrices );
        Matrix A = As.getSum();
        this.result.normalizeTransitionMatrix(A);
        return A;
    }

    /**
     * Updates the initial probabilities from sequenceGammas
     * @param firstGammas
     * The first gamma of the each sequence
     * @return
     * Updated initial probability Vector for the HMM.
     */
    protected Vector updateInitialProbabilities(
        ArrayList<Vector> firstGammas )
    {
        RingAccumulator<Vector> pi = new RingAccumulator<Vector>();
        for( int k = 0; k < firstGammas.size(); k++ )
        {
            pi.accumulate( firstGammas.get(k) );
        }
        Vector pisum = pi.getSum();
        pisum.scaleEquals( 1.0 / pisum.norm1() );
        return pisum;
    }

    /**
     * Updates the internal sequence likelihoods for the given HMM
     * @param hmm
     * Hidden Markov model to consider
     * @return
     * log likelihood of the observations sequences given the HMM.
     */
    protected double updateSequenceLogLikelihoods(
        HiddenMarkovModel<ObservationType> hmm )
    {
        int k = 0;
        double maxLogLikelihood = Double.NEGATIVE_INFINITY;
        double totalLogLikelihood = 0.0;
        for( Collection<? extends ObservationType> sequence : this.multicollection.subCollections() )
        {
            double logLikelihood = hmm.computeObservationLogLikelihood(sequence);
            if( maxLogLikelihood < logLikelihood )
            {
                maxLogLikelihood = logLikelihood;
            }
            this.sequenceLogLikelihoods.get(k).setValue(logLikelihood);
            totalLogLikelihood += logLikelihood;
            k++;
        }

        // Subtract off the maximum log-likleihood to at least make sure the
        // weights go from 1.0 to almost zero.
        final int numSequences = this.multicollection.getSubCollectionsCount();
        for( k = 0; k < numSequences; k++ )
        {
            // The weight is the INVERSE of the sequence Probability!!
            DefaultWeightedValue<Double> wv = this.sequenceLogLikelihoods.get(k);
            final double logLikelihood = wv.getValue();
            final double weight = 1.0/Math.exp(logLikelihood - maxLogLikelihood);
            wv.setWeight(weight);
        }
        return totalLogLikelihood;
    }

//
//    public static final int DEFAULT_NUM_STATES = 3;
//    public static final int DEFAULT_OBSERVATION_DIM = 1;
//    public static final Random RANDOM = new Random(1);
//    /**
//     * Creates static CDHMM
//     * @return
//     * CDHMM
//     */
//    public static HiddenMarkovModel<Vector> createHMMInstance()
//    {
//        int k = DEFAULT_NUM_STATES;
//        int dim = DEFAULT_OBSERVATION_DIM;
//
//        ArrayList<MultivariateGaussian.PDF> pdfs =
//            new ArrayList<MultivariateGaussian.PDF>( k );
//
//        Matrix C = MatrixFactory.getDefault().createIdentity(dim, dim).scale(
//            0.1);
//        for( int i = 0; i < k; i++ )
//        {
//            pdfs.add( new MultivariateGaussian.PDF(
//                VectorFactory.getDefault().createVector( dim, i ), C.clone() ) );
//        }
//
//        Matrix A = MatrixFactory.getDefault().createMatrix(k, k);
//        A.setElement(0, 0, 0.5);
//        A.setElement(1, 0, 0.2 );
//        A.setElement(2, 0, 0.3 );
//
//        A.setElement(0, 1, 0.3 );
//        A.setElement(1, 1, 0.5 );
//        A.setElement(2, 1, 0.2 );
//
//        A.setElement(0, 2, 0.3 );
//        A.setElement(1, 2, 0.2 );
//        A.setElement(2, 2, 0.5 );
//
//        Vector pi = VectorFactory.getDefault().copyValues( 0.5, 0.25, 0.25 );
//
//        return new HiddenMarkovModel<Vector>( pi, A, pdfs );
//
//    }
//
//    /**
//     * Creates an instance
//     * @return
//     * instance
//     */
//    public static BaumWelchAlgorithm<Vector> createInstance()
//    {
//        HiddenMarkovModel<Vector> hmm = new HiddenMarkovModel<Vector>(
//                DEFAULT_NUM_STATES );
//        final int dim = DEFAULT_OBSERVATION_DIM;
//        ArrayList<MultivariateGaussian.PDF> pdfs =
//            new ArrayList<MultivariateGaussian.PDF>( hmm.getNumStates() );
//        for( int i = 0; i < hmm.getNumStates(); i++ )
//        {
//            Vector mean = VectorFactory.getDefault().createVector( dim, i );
//            Matrix cov = MatrixFactory.getDefault().createIdentity( dim, dim );
//            pdfs.add( new MultivariateGaussian.PDF( mean, cov ) );
//        }
//        hmm.setEmissionFunctions(pdfs);
//
//        return new BaumWelchAlgorithm<Vector>( hmm,
//            new MultivariateGaussian.WeightedMaximumLikelihoodEstimator(),
//            true );
//    }
//
//    public static void main(
//        String[] args )
//    {
//
//
//        HiddenMarkovModel<Vector> target = createHMMInstance();
//        ArrayList<Vector> observations = target.sample(RANDOM, 100);
//
//        System.out.println( "TARGET: " + target );
//
//        double l1 = target.computeObservationLogLikelihood(observations);
//        System.out.println( "TARGET Log Likelihood: " + l1 );
//
//        for( int n = 0; n < 100; n++ )
//        {
//            BaumWelchAlgorithm<Vector> learner = createInstance();
//            double l0 = learner.getInitialGuess().computeObservationLogLikelihood(
//                observations);
//            System.out.println( "INITIAL Log Likelihood: " + l0 );
//            learner.setMaxIterations(1);
//            HiddenMarkovModel<Vector> result = learner.learn(observations);
//            System.out.println( "Result: " + learner.getIteration() + ": " + result );
//            double l2 = result.computeObservationLogLikelihood(observations);
//            System.out.println( "Result Log Likelihood: " +  l2 );
//        }
//
//    }

}
