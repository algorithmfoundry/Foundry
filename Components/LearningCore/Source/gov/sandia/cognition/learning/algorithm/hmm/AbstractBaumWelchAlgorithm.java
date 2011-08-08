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

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.Collection;

/**
 * Partial implementation of the Baum-Welch algorithm.
 * @param <ObservationType> Type of Observations handled by the HMM.
 * @param <DataType> Type of data (Collection of ObservationType, for instance)
 * sent to the learn method.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractBaumWelchAlgorithm<ObservationType,DataType>
    extends AbstractAnytimeBatchLearner<DataType,HiddenMarkovModel<ObservationType>>
    implements MeasurablePerformanceAlgorithm
{

    /**
     * Default maximum number of iterations, {@value}.
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /**
     * Default flag to re-estimate initial probabilities, {@value}.
     */
    public static final boolean DEFAULT_REESTIMATE_INITIAL_PROBABILITY = true;

    /**
     * Name of the performance statistic, {@value}.
     */
    public static final String PERFORMANCE_NAME = "Log Likelihood";

    /**
     * Learner for the Distribution Functions of the HMM.
     */
    protected BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> distributionLearner;

    /**
     * Result of the Baum-Welch Algorithm
     */
    protected HiddenMarkovModel<ObservationType> result;

    /**
     * Initial guess for the iterations.
     */
    protected HiddenMarkovModel<ObservationType> initialGuess;

     /**
     * Last Log Likelihood of the iterations
     */
    protected double lastLogLikelihood;

    /**
     * Flag to re-estimate the initial probability Vector.
     */
    protected boolean reestimateInitialProbabilities;

    /**
     * Creates a new instance of AbstractBaumWelchAlgorithm
     * @param initialGuess
     * Initial guess for the iterations.
     * @param distributionLearner
     * Learner for the Distribution Functions of the HMM.
     * @param reestimateInitialProbabilities
     * Flag to re-estimate the initial probability Vector.
     */
    public AbstractBaumWelchAlgorithm(
        HiddenMarkovModel<ObservationType> initialGuess,
        BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>,? extends ComputableDistribution<ObservationType>> distributionLearner,
        boolean reestimateInitialProbabilities )
    {
        super(DEFAULT_MAX_ITERATIONS);
        this.setInitialGuess(initialGuess);
        this.setDistributionLearner(distributionLearner);
        this.setReestimateInitialProbabilities(reestimateInitialProbabilities);
        this.result = null;
        this.lastLogLikelihood = Double.NEGATIVE_INFINITY;
    }

    @Override
    public AbstractBaumWelchAlgorithm<ObservationType,DataType> clone()
    {
        AbstractBaumWelchAlgorithm<ObservationType,DataType> clone =
            (AbstractBaumWelchAlgorithm<ObservationType,DataType>) super.clone();
        clone.setDistributionLearner(
            ObjectUtil.cloneSafe( this.getDistributionLearner() ) );
        clone.result = ObjectUtil.cloneSafe( this.getResult() );
        clone.setInitialGuess( ObjectUtil.cloneSafe( this.getInitialGuess() ) );
        return clone;
    }

    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>(
            PERFORMANCE_NAME, this.lastLogLikelihood );
    }

    public HiddenMarkovModel<ObservationType> getResult()
    {
        return this.result;
    }

    /**
     * Getter for initialGuess.
     * @return
     * Initial guess for the iterations.
     */
    public HiddenMarkovModel<ObservationType> getInitialGuess()
    {
        return this.initialGuess;
    }

    /**
     * Setter for initialGuess.
     * @param initialGuess
     * Initial guess for the iterations.
     */
    public void setInitialGuess(
        HiddenMarkovModel<ObservationType> initialGuess)
    {
        this.initialGuess = initialGuess;
    }

    /**
     * Getter for reestimateInitialProbabilities
     * @return the reestimateInitialProbabilities
     * Flag to re-estimate the initial probability Vector.
     */
    public boolean getReestimateInitialProbabilities()
    {
        return this.reestimateInitialProbabilities;
    }

    /**
     * Setter for reestimateInitialProbabilities
     * @param reestimateInitialProbabilities
     * Flag to re-estimate the initial probability Vector.
     */
    public void setReestimateInitialProbabilities(
        boolean reestimateInitialProbabilities)
    {
        this.reestimateInitialProbabilities = reestimateInitialProbabilities;
    }

    /**
     * Getter for distributionLearner
     * @return
     * Learner for the Distribution Functions of the HMM.
     */
    public BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>, ? extends ComputableDistribution<ObservationType>> getDistributionLearner()
    {
        return this.distributionLearner;
    }

    /**
     * Setter for distributionLearner
     * @param distributionLearner
     * Learner for the Distribution Functions of the HMM.
     */
    public void setDistributionLearner(
        BatchLearner<Collection<? extends WeightedValue<? extends ObservationType>>, ? extends ComputableDistribution<ObservationType>> distributionLearner)
    {
        this.distributionLearner = distributionLearner;
    }

}
