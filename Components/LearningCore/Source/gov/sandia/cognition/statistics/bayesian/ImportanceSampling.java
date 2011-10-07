/*
 * File:                ImportanceSampling.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Oct 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.AbstractRandomized;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Importance sampling is a Monte Carlo inference technique where we sample
 * from an easy distribution over the hidden variables (parameters) and then
 * weight the result by the ratio of the likelihood of the parameters given
 * the evidence and the likelihood of generating the parameters.  This is a
 * simple alternative to MCMC that is computationally simple, but does not
 * scale well to many data points or many dimensions.
 * @param <ObservationType> Type of observation
 * @param <ParameterType> Type of parameters to infer
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Importance Sampling",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Importance_sampling"
)
public class ImportanceSampling<ObservationType,ParameterType>
    extends AbstractRandomized
    implements BayesianEstimator<ObservationType, ParameterType, DataDistribution<ParameterType>>
{

    /**
     * Default maximum number of samples, {@value}.
     */
    public static final int DEFAULT_NUM_SAMPLES = 1000;

    /**
     * Updater for the ImportanceSampling algorithm.
     */
    protected ImportanceSampling.Updater<ObservationType,ParameterType> updater;

    /**
     * Number of samples.
     */
    private int numSamples;

    /** 
     * Creates a new instance of ImportanceSampling 
     */
    public ImportanceSampling()
    {
        super( null );
        this.setNumSamples(numSamples);
        this.numSamples = DEFAULT_NUM_SAMPLES;
    }

    @Override
    public ImportanceSampling<ObservationType,ParameterType> clone()
    {
        @SuppressWarnings("unchecked")
        ImportanceSampling<ObservationType,ParameterType> clone =
            (ImportanceSampling<ObservationType,ParameterType>) super.clone();
        clone.setUpdater( ObjectUtil.cloneSafe( this.getUpdater() ) );
        return clone;
    }

    @Override
    public DataDistribution<ParameterType> learn(
        final Collection<? extends ObservationType> data)
    {

        ArrayList<DefaultWeightedValue<ParameterType>> weightedSamples =
            new ArrayList<DefaultWeightedValue<ParameterType>>( this.getNumSamples());

        double maxWeight = Double.NEGATIVE_INFINITY;
        for( int n = 0; n < this.getNumSamples(); n++ )
        {
            ParameterType parameter = this.getUpdater().makeProposal(random);
            double ll = this.getUpdater().computeLogLikelihood(parameter, data);
            double lq = this.getUpdater().computeLogImportanceValue(parameter);
            double weight = ll - lq;
            if( maxWeight < weight )
            {
                maxWeight = weight;
            }
            weightedSamples.add( new DefaultWeightedValue<ParameterType>( parameter, weight ) );
        }

        maxWeight -= Math.log(Double.MAX_VALUE/ this.getNumSamples() / 2.0 );

        DataDistribution<ParameterType> retval =
            new DefaultDataDistribution<ParameterType>( this.getNumSamples());
        for( DefaultWeightedValue<ParameterType> weightedSample : weightedSamples )
        {
            double mass = Math.exp(weightedSample.getWeight() - maxWeight);
            retval.increment( weightedSample.getValue(), mass );
        }

        return retval;

    }

    /**
     * Getter for updater
     * @return
     * Updater for the ImportanceSampling algorithm.
     */
    public ImportanceSampling.Updater<ObservationType, ParameterType> getUpdater()
    {
        return this.updater;
    }

    /**
     * Setter for updater
     * @param updater
     * Updater for the ImportanceSampling algorithm.
     */
    public void setUpdater(
        final ImportanceSampling.Updater<ObservationType, ParameterType> updater)
    {
        this.updater = updater;
    }

    /**
     * Getter for numSamples
     * @return
     * Number of samples.
     */
    public int getNumSamples()
    {
        return this.numSamples;
    }

    /**
     * Setter for numSamples
     * @param numSamples
     * Number of samples.
     */
    public void setNumSamples(
        final int numSamples)
    {
        this.numSamples = numSamples;
    }


    /**
     * Updater for ImportanceSampling
     * @param <ObservationType> Type of observation
     * @param <ParameterType> Type of parameters to infer
     */
    public static interface Updater<ObservationType,ParameterType>
        extends CloneableSerializable
    {

        /**
         * Computes the log likelihood of the data given the parameter
         * @param parameter
         * Parameter to consider
         * @param data
         * Data to consider
         * @return
         * log likelihood of the data given the parameter
         */
        public double computeLogLikelihood(
            final ParameterType parameter,
            final Iterable<? extends ObservationType> data );

        /**
         * Computes the parameter's importance weight.
         * @param parameter
         * Parameter to consider
         * @return
         * Importance value
         */
        public double computeLogImportanceValue(
            final ParameterType parameter );

        /**
         * Samples from the parameter prior
         * @param random
         * Random number generator.
         * @return
         * Location of the proposed sample
         */
        public ParameterType makeProposal(
            final Random random );

    }

    /**
     * Default ImportanceSampling Updater that uses a BayesianParameter
     * to compute the quantities of interest.
     * @param <ObservationType> Type of observation
     * @param <ParameterType> Type of parameters to infer
     */
    public static class DefaultUpdater<ObservationType,ParameterType>
        extends AbstractCloneableSerializable
        implements Updater<ObservationType,ParameterType>
    {

        /**
         * Defines the parameter that connects the conditional and prior
         * distributions.
         */
        protected BayesianParameter<ParameterType,? extends ProbabilityFunction<ObservationType>,? extends ProbabilityFunction<ParameterType>> conjuctive;

        /**
         * Default constructor.
         */
        public DefaultUpdater()
        {
            this( null );
        }

        /**
         * Creates a new instance of DefaultUpdater
         * @param conjuctive
         * Defines the parameter that connects the conditional and prior
         * distributions.
         */
        public DefaultUpdater(
            final BayesianParameter<ParameterType,? extends ProbabilityFunction<ObservationType>,? extends ProbabilityFunction<ParameterType>> conjuctive)
        {
            this.setConjuctive(conjuctive);
        }

        @Override
        public double computeLogLikelihood(
            final ParameterType parameter,
            final Iterable<? extends ObservationType> data)
        {
            this.conjuctive.setValue(parameter);
            return BayesianUtil.logLikelihood(
                this.conjuctive.getConditionalDistribution(), data);
        }

        @Override
        public double computeLogImportanceValue(
            final ParameterType parameter)
        {
            return this.conjuctive.getParameterPrior().logEvaluate(parameter);
        }

        @Override
        public ParameterType makeProposal(
            final Random random)
        {
            return this.conjuctive.getParameterPrior().sample(random);
        }

        /**
         * Getter for conjunctive
         * @return
         * Defines the parameter that connects the conditional and prior
         * distributions.
         */
        public BayesianParameter<ParameterType,? extends ProbabilityFunction<ObservationType>,? extends ProbabilityFunction<ParameterType>> getConjuctive()
        {
            return this.conjuctive;
        }

        /**
         * Setter for conjunctive
         * @param conjuctive
         * Defines the parameter that connects the conditional and prior
         * distributions.
         */
        public void setConjuctive(
            final BayesianParameter<ParameterType,? extends ProbabilityFunction<ObservationType>,? extends ProbabilityFunction<ParameterType>> conjuctive)
        {
            this.conjuctive = conjuctive;
        }

    }

}
