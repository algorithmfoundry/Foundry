/*
 * File:                ParallelBaumWelchAlgorithm.java
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

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A Parallelized implementation of some of the methods of the
 * Baum-Welch Algorithm.
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
public class ParallelBaumWelchAlgorithm<ObservationType>
    extends BaumWelchAlgorithm<ObservationType>
    implements ParallelAlgorithm
{

    /**
     * Thread pool used for parallelization.
     */
    transient private ThreadPoolExecutor threadPool;

    /**
     * Tasks for re-estimating the PDFs.
     */
    transient protected ArrayList<DistributionEstimatorTask<ObservationType>> distributionEstimatorTasks;

    /**
     * Default constructor
     */
    public ParallelBaumWelchAlgorithm()
    {
        super();
    }

    /**
     * Creates a new instance of ParallelBaumWelchAlgorithm
     * @param initialGuess
     * Initial guess for the iterations.
     * @param distributionLearner
     * Learner for the Distribution Functions of the HMM.
     * @param reestimateInitialProbabilities
     * Flag to re-estimate the initial probability Vector.
     */
    public ParallelBaumWelchAlgorithm(
        HiddenMarkovModel<ObservationType> initialGuess,
        BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> distributionLearner,
        boolean reestimateInitialProbabilities )
    {
        super( initialGuess, distributionLearner, reestimateInitialProbabilities );
    }

    public ThreadPoolExecutor getThreadPool()
    {
        if( this.threadPool == null )
        {
            this.setThreadPool( ParallelUtil.createThreadPool() );
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

    @Override
    protected boolean initializeAlgorithm()
    {
        this.distributionEstimatorTasks = this.createDistributionEstimatorTasks();
        return super.initializeAlgorithm();
    }

    @Override
    protected ArrayList<ProbabilityFunction<ObservationType>> updateProbabilityFunctions(
        ArrayList<Vector> sequenceGammas)
    {
        final int N = this.getResult().getNumStates();
        for( int i = 0; i < N; i++ )
        {
            this.distributionEstimatorTasks.get(i).setGammas( sequenceGammas );
        }

        ArrayList<ProbabilityFunction<ObservationType>> fs = null;
        try
        {
            fs = ParallelUtil.executeInParallel(
                this.distributionEstimatorTasks, this.getThreadPool() );
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }

        return fs;
    }

    /**
     * Creates the DistributionEstimatorTask
     * @return
     * DistributionEstimatorTask.
     */
    protected ArrayList<DistributionEstimatorTask<ObservationType>> createDistributionEstimatorTasks()
    {
        final int N = this.initialGuess.getNumStates();
        ArrayList<DistributionEstimatorTask<ObservationType>> tasks =
            new ArrayList<DistributionEstimatorTask<ObservationType>>( N );
        for( int i = 0; i < N; i++ )
        {
            tasks.add( new DistributionEstimatorTask<ObservationType>(
                this.data, this.distributionLearner, i ) );
        }
        return tasks;
    }

    /**
     * Re-estimates the PDF from the gammas.
     * @param <ObservationType> Type of Observations.
     */
    protected static class DistributionEstimatorTask<ObservationType>
        extends AbstractCloneableSerializable
        implements Callable<ProbabilityFunction<ObservationType>>
    {

        /**
         * Weighted values for the PDF estimator.
         */
        protected ArrayList<DefaultWeightedValue<ObservationType>> weightedValues;

        /**
         * My copy of the PDF estimator.
         */
        protected BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> distributionLearner;

        /**
         * Gammas used to weight the learner samples.
         */
        private ArrayList<Vector> gammas;

        /**
         * Index into the gammas to pull the weights.
         */
        protected int index;

        /**
         * Creates an instance of DistributionEstimatorTask
         * @param data
         * Data to stuff into the weightedValues
         * @param distributionLearner
         * Distribution Learner
         * @param index
         * Index into the gammas
         */
        public DistributionEstimatorTask(
            Collection<? extends ObservationType> data,
            BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> distributionLearner,
            int index )
        {
            this.index = index;
            this.distributionLearner = distributionLearner;
            this.weightedValues =
                new ArrayList<DefaultWeightedValue<ObservationType>>( data.size() );
            for( ObservationType v : data )
            {
                this.weightedValues.add( new DefaultWeightedValue<ObservationType>( v ) );
            }
        }

        /**
         * Sets the gamma samples pointer.
         * @param gammas
         * Gammas
         */
        public void setGammas(
            ArrayList<Vector> gammas )
        {
            this.gammas = gammas;
        }

        public ProbabilityFunction<ObservationType> call()
        {
            final int N = this.gammas.size();
            for( int n = 0; n < N; n++ )
            {
                this.weightedValues.get(n).setWeight(
                    this.gammas.get(n).getElement( this.index ) );
            }
            return this.distributionLearner.learn(this.weightedValues).getProbabilityFunction();
        }

    }



}
