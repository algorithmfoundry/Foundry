/*
 * File:                ParallelHiddenMarkovModel.java
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

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A Hidden Markov Model with parallelized processing.
 * @param <ObservationType> Type of Observations handled by the HMM.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="William Turin",
    title="Unidirectional and Parallel Baum–Welch Algorithms",
    type=PublicationType.Journal,
    publication="IEEE Transactions on Speech and Audio Processing",
    year=1998,
    url="http://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=00725318"
)
public class ParallelHiddenMarkovModel<ObservationType>
    extends HiddenMarkovModel<ObservationType>
    implements ParallelAlgorithm
{

    /**
     * Thread pool used for parallelization.
     */
    private transient ThreadPoolExecutor threadPool;

    /** 
     * Creates a new instance of ParallelHiddenMarkovModel 
     */
    public ParallelHiddenMarkovModel()
    {
        super();
    }

    /**
     * Creates a new instance of ParallelHiddenMarkovModel
     * @param numStates
     * Number of states in the HMM.
     */
    public ParallelHiddenMarkovModel(
        int numStates )
    {
        super( numStates );
    }

    /**
     * Creates a new instance of ParallelHiddenMarkovModel
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
    public ParallelHiddenMarkovModel(
        Vector initialProbability,
        Matrix transitionProbability,
        Collection<? extends ProbabilityFunction<ObservationType>> emissionFunctions )
    {
        super( initialProbability, transitionProbability, emissionFunctions );
    }

    public ThreadPoolExecutor getThreadPool()
    {
        if (this.threadPool == null)
        {
            this.setThreadPool(ParallelUtil.createThreadPool());
        }
        return this.threadPool;
    }

    public void setThreadPool(
        ThreadPoolExecutor threadPool)
    {
        this.threadPool = threadPool;
    }

    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads(this);
    }

    /**
     * Observation likelihood tasks
     */
    transient protected ArrayList<ObservationLikelihoodTask<ObservationType>> observationLikelihoodTasks;

    /*
    @Override
    protected ArrayList<Vector> computeObservationLikelihoods(
        Collection<? extends ObservationType> observations)
    {

        final int k = this.getNumStates();
        if( this.observationLikelihoodTasks == null )
        {
            this.observationLikelihoodTasks =
                new ArrayList<ObservationLikelihoodTask<ObservationType>>( k );
        }

        // Make sure it's the right size
        this.observationLikelihoodTasks.ensureCapacity(k);
        while( this.observationLikelihoodTasks.size() > k )
        {
            this.observationLikelihoodTasks.remove(
                this.observationLikelihoodTasks.size()-1 );
        }
        while( this.observationLikelihoodTasks.size() < k )
        {
            this.observationLikelihoodTasks.add(
                new ObservationLikelihoodTask<ObservationType>() );
        }

        int i = 0;
        for( ProbabilityFunction<ObservationType> pdf : this.getEmissionFunctions() )
        {
            ObservationLikelihoodTask<ObservationType> task =
                this.observationLikelihoodTasks.get(i);
            task.observations = observations;
            task.distributionFunction = pdf;
            i++;
        }

        ArrayList<double[]> results = null;
        try
        {
            results = ParallelUtil.executeInParallel(
                this.observationLikelihoodTasks, this.getThreadPool());
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }

        final int N = observations.size();
        ArrayList<Vector> b = new ArrayList<Vector>( N );
        for( int n = 0; n < N; n++ )
        {
            double[] bn = new double[ k ];
            for( i = 0; i < k; i++ )
            {
                bn[i] = results.get(i)[n];
            }
            b.add( VectorFactory.getDefault().copyArray(bn) );
        }
        return b;
    }
     */

    @Override
    public double computeMultipleObservationLogLikelihood(
        Collection<? extends Collection<? extends ObservationType>> sequences)
    {

        ArrayList<LogLikelihoodTask> tasks =
            new ArrayList<LogLikelihoodTask>( sequences.size() );
        for( Collection<? extends ObservationType> sequence : sequences )
        {
            tasks.add( new LogLikelihoodTask( sequence ) );
        }

        ArrayList<Double> results = null;
        try
        {
            results = ParallelUtil.executeInParallel(tasks, this.getThreadPool());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        double logSum = 0.0;
        for( int i = 0; i < results.size(); i++ )
        {
            logSum += results.get(i);
        }
        return logSum;
    }


    /**
     * ComputeTransitionsTasks.
     */
    transient protected ArrayList<ComputeTransitionsTask> computeTransitionTasks;

    @Override
    protected Matrix computeTransitions(
        ArrayList<WeightedValue<Vector>> alphas,
        ArrayList<WeightedValue<Vector>> betas,
        ArrayList<Vector> b)
    {

        final int N = alphas.size();
        if( this.computeTransitionTasks == null )
        {
            this.computeTransitionTasks =
                new ArrayList<ComputeTransitionsTask>( N-1 );
        }

        // Make sure it's N-1
        this.computeTransitionTasks.ensureCapacity(N-1);
        while( this.computeTransitionTasks.size() > N-1 )
        {
            this.computeTransitionTasks.remove(
                this.computeTransitionTasks.size()-1 );
        }
        while( this.computeTransitionTasks.size() < N-1 )
        {
            this.computeTransitionTasks.add( new ComputeTransitionsTask() );
        }

        for( int n = 0; n < N-1; n++ )
        {
            final ComputeTransitionsTask tn = this.computeTransitionTasks.get(n);
            tn.alphan = alphas.get(n).getValue();
            tn.betanp1 = betas.get(n+1).getValue();
            tn.bnp1 = b.get(n+1);
        }

        RingAccumulator<Matrix> counts = new RingAccumulator<Matrix>();
        Matrix A = null;
        try
        {
            Collection<Future<Matrix>> futures =
                this.getThreadPool().invokeAll(this.computeTransitionTasks);
            for( Future<Matrix> f : futures )
            {
                counts.accumulate( f.get() );
            }

            A = counts.getSum();
            A.dotTimesEquals(this.getTransitionProbability());
            normalizeTransitionMatrix(A);
        }
        catch (Exception ex)
        {
            throw new RuntimeException( ex );
        }

        return A;

    }

    /**
     * NormalizeTransitionTasks.
     */
    transient protected ArrayList<NormalizeTransitionTask> normalizeTransitionTasks;

    @Override
    protected void normalizeTransitionMatrix(
        Matrix A)
    {
        final int k = A.getNumColumns();
        if( this.normalizeTransitionTasks == null )
        {
            this.normalizeTransitionTasks =
                new ArrayList<NormalizeTransitionTask>( k );
        }

        this.normalizeTransitionTasks.ensureCapacity(k);
        while( this.normalizeTransitionTasks.size() > k )
        {
            this.normalizeTransitionTasks.remove(
                this.normalizeTransitionTasks.size() - 1 );
        }
        while( this.normalizeTransitionTasks.size() < k )
        {
            this.normalizeTransitionTasks.add( new NormalizeTransitionTask() );
        }

        for( int j = 0; j < k; j++ )
        {
            NormalizeTransitionTask task = this.normalizeTransitionTasks.get(j);
            task.j = j;
            task.A = A;
        }

        try
        {
            ParallelUtil.executeInParallel(
                this.normalizeTransitionTasks, this.getThreadPool());
        }
        catch (Exception ex)
        {
            throw new RuntimeException( ex );
        }

    }

    /**
     * StateObservationLikelihoodTasks
     */
    transient protected ArrayList<StateObservationLikelihoodTask> stateObservationLikelihoodTasks;

    @Override
    protected ArrayList<Vector> computeStateObservationLikelihood(
        ArrayList<WeightedValue<Vector>> alphas,
        ArrayList<WeightedValue<Vector>> betas,
        double scaleFactor )
    {

        final int N = alphas.size();
        if( this.stateObservationLikelihoodTasks == null )
        {
            this.stateObservationLikelihoodTasks =
                new ArrayList<StateObservationLikelihoodTask>( N );
        }

        this.stateObservationLikelihoodTasks.ensureCapacity(N);
        while( this.stateObservationLikelihoodTasks.size() > N )
        {
            this.stateObservationLikelihoodTasks.remove(
                this.stateObservationLikelihoodTasks.size()-1 );
        }
        while( this.stateObservationLikelihoodTasks.size() < N )
        {
            this.stateObservationLikelihoodTasks.add(
                new StateObservationLikelihoodTask() );
        }

        for( int n = 0; n < N; n++ )
        {
            StateObservationLikelihoodTask task =
                this.stateObservationLikelihoodTasks.get(n);
            task.alpha = alphas.get(n).getValue();
            task.beta = betas.get(n).getValue();
        }

        ArrayList<Vector> gammas = null;
        try
        {
            gammas = ParallelUtil.executeInParallel(
                this.stateObservationLikelihoodTasks, this.getThreadPool());
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }

        return gammas;
    }

    /**
     * Viterbi tasks.
     */
    transient protected ArrayList<ViterbiTask> viterbiTasks;

    @Override
    protected Pair<Vector, int[]> computeViterbiRecursion(
        Vector delta,
        Vector bn )
    {

        final int k = this.getNumStates();
        if( this.viterbiTasks == null )
        {
            this.viterbiTasks = new ArrayList<ViterbiTask>( k );
        }

        this.viterbiTasks.ensureCapacity(k);
        while( this.viterbiTasks.size() > k )
        {
            this.viterbiTasks.remove(
                this.viterbiTasks.size()-1 );
        }
        while( this.viterbiTasks.size() < k )
        {
            this.viterbiTasks.add( new ViterbiTask() );
        }

        for( int i = 0; i < k; i++ )
        {
            final ViterbiTask task = this.viterbiTasks.get(i);
            task.destinationState = i;
            task.delta = delta;
        }
        
        ArrayList<WeightedValue<Integer>> results;
        try
        {
            results = ParallelUtil.executeInParallel(
                this.viterbiTasks, this.getThreadPool() );
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }

        int[] psis = new int[ k ];
        Vector nextDelta = VectorFactory.getDefault().createVector(k);
        for( int i = 0; i < k; i++ )
        {
            WeightedValue<Integer> value = results.get(i);
            psis[i] = value.getValue();
            nextDelta.setElement(i, value.getWeight() );
        }

        nextDelta.dotTimesEquals(bn);
        nextDelta.scaleEquals( 1.0/nextDelta.norm1() );

        return DefaultPair.create( nextDelta, psis );

    }

    /**
     * Calls the computeObservationLikelihoods() method.
     * @param <ObservationType> Observation type
     */
    protected static class ObservationLikelihoodTask<ObservationType>
        extends AbstractCloneableSerializable
        implements Callable<double[]>
    {

        /**
         * Observations
         */
        protected Collection<? extends ObservationType> observations;

        /**
         * The PDF.
         */
        protected ProbabilityFunction<ObservationType> distributionFunction;

        /**
         * Default constructor.
         */
        public ObservationLikelihoodTask()
        {
        }

        public double[] call()
        {
            final int N = this.observations.size();
            double[] b = new double[ N ];
            int n = 0;
            for( ObservationType observation : this.observations )
            {
                b[n] = this.distributionFunction.evaluate(observation);
                n++;
            }
            return b;
        }

    }

    /**
     * Calls the computeStateObservationLikelihood() method.
     */
    protected static class StateObservationLikelihoodTask
        extends AbstractCloneableSerializable
        implements Callable<Vector>
    {

        /**
         * Alpha at time n.
         */
        protected Vector alpha;

        /**
         * Beta at time n.
         */
        protected Vector beta;

        /**
         * Default constructor.
         */
        public StateObservationLikelihoodTask()
        {
        }

        public Vector call()
            throws Exception
        {
            return ParallelHiddenMarkovModel.computeStateObservationLikelihood(
                this.alpha, this.beta, 1.0 );
        }

    }

    /**
     * Calls the normalizeTransitionMatrix method.
     */
    protected static class NormalizeTransitionTask
        extends AbstractCloneableSerializable
        implements Callable<Void>
    {

        /**
         * Matrix to normalize.
         */
        private Matrix A;

        /**
         * Column to normalize.
         */
        private int j;

        /**
         * Default constructor.
         */
        public NormalizeTransitionTask()
        {
        }

        public Void call()
        {
            ParallelHiddenMarkovModel.normalizeTransitionMatrix(
                this.A, this.j);
            return null;
        }

    }

    /**
     * Calls the computeTransitions method.
     */
    protected static class ComputeTransitionsTask
        extends AbstractCloneableSerializable
        implements Callable<Matrix>
    {

        /**
         * Alpha at time n.
         */
        Vector alphan;

        /**
         * Alpha at time n.
         */
        Vector betanp1;

        /**
         * b at time n+1.
         */
        Vector bnp1;

        /**
         * Default constructor.
         */
        public ComputeTransitionsTask()
        {
        }

        public Matrix call()
        {
            return ParallelHiddenMarkovModel.computeTransitions(
                this.alphan, this.betanp1, this.bnp1 );
        }

    }


    /**
     * Computes the most-likely "from state" for the given "destination state"
     * and the given deltas.
     */
    protected class ViterbiTask
        extends AbstractCloneableSerializable
        implements Callable<WeightedValue<Integer>>
    {

        /**
         * Destination state for the Viterbi Recursion.
         */
        int destinationState;

        /**
         * Previous value of the Viterbi Recursion.
         */
        Vector delta;

        /**
         * Default constructor
         */
        ViterbiTask()
        {
        }

        public WeightedValue<Integer> call()
            throws Exception
        {
            return ParallelHiddenMarkovModel.this.findMostLikelyState(
                this.destinationState, this.delta);
         }
        
    }

    /**
     * Computes the log-likelihood of a particular data sequence
     */
    protected class LogLikelihoodTask
        extends AbstractCloneableSerializable
        implements Callable<Double>
    {

        /**
         * Data to compute the log-likelihood of
         */
        protected Collection<? extends ObservationType> data;

        /**
         * Creates a new instance of LogLikelihoodTask
         * @param data
         * Data to compute the log-likelihood of
         */
        public LogLikelihoodTask(
            Collection<? extends ObservationType> data)
        {
            this.data = data;
        }

        public Double call()
            throws Exception
        {
            return computeObservationLogLikelihood( this.data );
        }

    }

}
