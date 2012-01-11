/*
 * File:                RejectionSampling.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 3, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeFree;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.AbstractRandomized;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Rejection sampling is a method of inferring hidden parameters by using
 * an easy-to-sample-from distribution (times a scale factor) that envelopes
 * another distribution that is difficult to sample from.  Typically, we sample
 * from the parameter prior to infer the likelihood of the parameters given
 * an observation sequence.  In my limited experience, vanilla rejection
 * sampling, implemented here, is inferior to ImportanceSamping.
 * @param <ObservationType> Type of observation
 * @param <ParameterType> Type of parameters to infer
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Rejection Sampling",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Rejection_sampling"
)
public class RejectionSampling<ObservationType,ParameterType>
    extends AbstractRandomized
    implements BayesianEstimator<ObservationType,ParameterType,DataDistribution<ParameterType>>
{

    /**
     * Default number of samples, {@value}.
     */
    public static final int DEFAULT_NUM_SAMPLES = 1000;

    /**
     * Number of samples.
     */
    private int numSamples;

    /**
     * Updater for the ImportanceSampling algorithm.
     */
    protected RejectionSampling.Updater<ObservationType,ParameterType> updater;

    /** 
     * Creates a new instance of RejectionSampling 
     */
    public RejectionSampling()
    {
        super( null );
        this.setNumSamples(DEFAULT_NUM_SAMPLES);
    }

    @Override
    public RejectionSampling<ObservationType,ParameterType> clone()
    {
        @SuppressWarnings("unchecked")
        RejectionSampling<ObservationType,ParameterType> clone =
            (RejectionSampling<ObservationType,ParameterType>) super.clone();
        clone.setUpdater( ObjectUtil.cloneSafe( this.getUpdater() ) );
        return clone;
    }

    @Override
    public DataDistribution<ParameterType> learn(
        final Collection<? extends ObservationType> data)
    {
        DataDistribution<ParameterType> retval =
            new DefaultDataDistribution<ParameterType>( this.getNumSamples() );

        for( int n = 0; n < numSamples; n++ )
        {
            ParameterType parameter = null;
            boolean accepted = false;
            while( !accepted )
            {
                parameter = this.getUpdater().makeProposal( this.getRandom() );
                double acceptProbability =
                    this.getUpdater().computeAcceptanceProbability(parameter,data);
                double p = this.getRandom().nextDouble();
                if( p <= acceptProbability )
                {
                    accepted = true;
                    break;
                }
            }
            retval.increment(parameter);
        }

        return retval;
        
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
     * Getter for updater
     * @return
     * Updater for the ImportanceSampling algorithm.
     */
    public RejectionSampling.Updater<ObservationType, ParameterType> getUpdater()
    {
        return this.updater;
    }

    /**
     * Setter for updater
     * @param updater
     * Updater for the ImportanceSampling algorithm.
     */
    public void setUpdater(
        final RejectionSampling.Updater<ObservationType, ParameterType> updater)
    {
        this.updater = updater;
    }

    /**
     * Routine for estimating the minimum scalar needed to envelop the
     * conjunctive distribution.
     * @param <ObservationType>
     * Type of observations to use.
     */
    public static class ScalarEstimator<ObservationType>
    {

        /**
         * Defines the parameter that connects the conditional and prior
         * distributions.
         */
        BayesianParameter<Double,? extends ProbabilityFunction<ObservationType>,? extends UnivariateProbabilityDensityFunction> conjunctive;

        /**
         * Data to consider
         */
        Iterable<? extends ObservationType> data;

        /**
         * Creates a new instance of ScalarEstimator
         * @param conjunctive
         * Defines the parameter that connects the conditional and prior
         * distributions.
         * @param data
         * Data to consider
         */
        public ScalarEstimator(
            final BayesianParameter<Double,? extends ProbabilityFunction<ObservationType>,? extends UnivariateProbabilityDensityFunction> conjunctive,
            final Iterable<? extends ObservationType> data )
        {
            this.conjunctive = conjunctive;
            this.data = data;
        }

        /**
         * Computes the logarithm of the conjunctive likelihood for the given
         * parameter
         * @param parameter
         * Parameter to update.
         * @return
         * Logarithm of the conjunctive likelihood for the given parameter
         */
        public double logConjunctive(
            final Double parameter )
        {
            double logSum = this.conjunctive.getParameterPrior().logEvaluate(parameter);
            if( !Double.isInfinite(logSum) )
            {
                this.conjunctive.setValue(parameter);
                logSum += BayesianUtil.logLikelihood(
                    this.conjunctive.getConditionalDistribution(), this.data);
            }
            return logSum;
        }

        /**
         * Estimates the minimum scalar needed for the sampler distribution to
         * envelope the conjunctive distribution
         * @param sampler
         * Distribution from which we sample and envelop the conjunctive
         * distribution.
         * @return
         * Minimum scalar needed for the sampler distribution to envelope the
         * conjunctive distribution
         */
        public double estimateScalarFactor(
            final UnivariateProbabilityDensityFunction sampler )
        {
            MinimizerFunction f = new MinimizerFunction( sampler );
            LineMinimizerDerivativeFree minimizer =
                new LineMinimizerDerivativeFree();
            minimizer.setInitialGuess( sampler.getMean() );
            InputOutputPair<Double,Double> mode = minimizer.learn(f);
            return Math.exp(-mode.getOutput());
        }

        /**
         * Minimization function that measures the difference between the
         * logarithm of the sampler function minus the logarithm of the
         * conjunctive distribution.
         */
        public class MinimizerFunction
            implements Evaluator<Double,Double>
        {

            /**
             * Sampler function
             */
            protected ProbabilityFunction<Double> sampler;

            /**
             * Creates a new instance of MinimizerFunction
             * @param sampler
             * Sampler function
             */
            public MinimizerFunction(
                final ProbabilityFunction<Double> sampler)
            {
                this.sampler = sampler;
            }
            
            @Override
            public Double evaluate(
                final Double parameter)
            {
                // Find the point where the conjuctive is the largest compared
                // to the sampler: min(logSampler - logConjuctive)
                final double logSampler = this.sampler.logEvaluate(parameter);
                final double logCon = logConjunctive(parameter);
                if( Double.isInfinite(logSampler) ||
                    Double.isInfinite(logCon) )
                {
                    return Double.POSITIVE_INFINITY;
                }
                else
                {
                    return logSampler-logCon;
                }
            }
        }
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
         * Computes the probability of accepting the parameter for the given
         * data.
         * @param parameter
         * Parameter to consider
         * @param data
         * Data to consider.
         * @return
         * Probability of accepting the parameter
         */
        public double computeAcceptanceProbability(
            final ParameterType parameter,
            final Iterable<? extends ObservationType> data );

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
         * Scale factor to multiply the sampler function by to envelop the
         * conjunctive distribution.
         */
        protected Double scale;

        /**
         * Distribution from which we sample and envelop the conjunctive
         * distribution.
         */
        private ProbabilityFunction<ParameterType> sampler;

        /**
         * Number of proposals suggested
         */
        protected int proposals;

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
            this( conjuctive, (conjuctive != null) ? conjuctive.getParameterPrior() : null );
        }

        /**
         * Creates a new instance of DefaultUpdater
         * @param conjuctive
         * Defines the parameter that connects the conditional and prior
         * distributions.
         * @param sampler
         * Distribution from which we sample and envelop the conjunctive
         * distribution.
         */
        public DefaultUpdater(
            final BayesianParameter<ParameterType,? extends ProbabilityFunction<ObservationType>,? extends ProbabilityFunction<ParameterType>> conjuctive,
            final ProbabilityFunction<ParameterType> sampler)
        {
            this( conjuctive, null, sampler );
        }

        /**
         * Creates a new instance of DefaultUpdater
         * @param conjuctive
         * Defines the parameter that connects the conditional and prior
         * distributions.
         * @param scale
         * Scale factor to multiply the sampler function by to envelop the
         * conjunctive distribution.
         * @param sampler
         * Distribution from which we sample and envelop the conjunctive
         * distribution.
         */
        public DefaultUpdater(
            final BayesianParameter<ParameterType,? extends ProbabilityFunction<ObservationType>,? extends ProbabilityFunction<ParameterType>> conjuctive,
            final Double scale,
            final ProbabilityFunction<ParameterType> sampler)
        {
            this.setConjuctive(conjuctive);
            this.setScale(scale);
            this.setSampler(sampler);
            this.setProposals(0);
        }

        @Override
        public double computeAcceptanceProbability(
            final ParameterType parameter,
            final Iterable<? extends ObservationType> data)
        {
            if( this.scale == null )
            {
                this.scale = this.computeOptimalScale(data);
            }
            this.conjuctive.setValue(parameter);
            double logSum = BayesianUtil.logLikelihood(
                this.conjuctive.getConditionalDistribution(), data);
            logSum += this.conjuctive.getParameterPrior().logEvaluate(parameter);
            logSum -= this.sampler.logEvaluate(parameter);
            logSum -= Math.log( this.getScale());
            return Math.exp( logSum );
        }

        @Override
        public ParameterType makeProposal(
            final Random random)
        {
            this.proposals++;
            ParameterType parameter = null;
            boolean keepGoing = true;
            while( keepGoing )
            {
                parameter = this.sampler.sample(random);
                if( this.conjuctive.getParameterPrior().evaluate(parameter) > 0.0 )
                {
                    keepGoing = false;
                }
            }
            return parameter;

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

        /**
         * Computes the optimal scale factor for enveloping the conjunctive
         * distribution with the sampler function given the data
         * @param data
         * Data to consider
         * @return
         * optimal scale factor for enveloping the conjunctive
         * distribution with the sampler function given the data
         */
        @SuppressWarnings("unchecked")
        public double computeOptimalScale(
            final Iterable<? extends ObservationType> data )
        {
            ScalarEstimator<ObservationType> estimator =
                new ScalarEstimator<ObservationType>(
                    (BayesianParameter<Double,? extends ProbabilityFunction<ObservationType>,? extends UnivariateProbabilityDensityFunction>) this.getConjuctive(), data );
            return estimator.estimateScalarFactor(
                (UnivariateProbabilityDensityFunction) this.getSampler() );
        }

        /**
         * Computes a Gaussian sample for the parameter, assuming it has is
         * a Double, using importance sampling.
         * @param data
         * (Sub)set of the data to use to estimate the Gaussian
         * @param random
         * Random number generator
         * @param numSamples
         * Number of samples to create the Gaussian... doesn't need to be
         * very large.
         * @return
         * Gaussian that has the appropriate mean and variance to generate
         * parameters.
         */
        @SuppressWarnings("unchecked")
        public UnivariateGaussian.PDF computeGaussianSampler(
            final Iterable<? extends ObservationType> data,
            final Random random,
            final int numSamples )
        {
            ArrayList<? extends ParameterType> parameters =
                this.conjuctive.getParameterPrior().sample(random, numSamples);

            UnivariateGaussian.WeightedMaximumLikelihoodEstimator mle =
                new UnivariateGaussian.WeightedMaximumLikelihoodEstimator();
            ArrayList<DefaultWeightedValue<Double>> values =
                new ArrayList<DefaultWeightedValue<Double>>( parameters.size() );
            for( int n = 0; n < parameters.size(); n++ )
            {
                ParameterType parameter = parameters.get(n);
                if( this.conjuctive.getParameterPrior().evaluate(parameter) > 0.0 )
                {
                    this.conjuctive.setValue(parameter);
                    double weight = BayesianUtil.logLikelihood(
                        this.conjuctive.getConditionalDistribution(), data);
                    values.add( new DefaultWeightedValue<Double>(
                        (Double) parameter, weight ) );
                }
            }
            return mle.learn(values);
        }

        /**
         * Getter for scale
         * @return
         * Scale factor to multiply the sampler function by to envelop the
         * conjunctive distribution.
         */
        public Double getScale()
        {
            return this.scale;
        }

        /**
         * Setter for scale
         * @param scale
         * Scale factor to multiply the sampler function by to envelop the
         * conjunctive distribution.
         */
        public void setScale(
            final Double scale)
        {
            this.scale = scale;
        }

        /**
         * Getter for proposals
         * @return
         * Number of proposals suggested
         */
        public int getProposals()
        {
            return this.proposals;
        }

        /**
         * Setter for proposals
         * @param proposals
         * Number of proposals suggested
         */
        protected void setProposals(
            final int proposals)
        {
            this.proposals = proposals;
        }

        /**
         * Getter for sampler
         * @return 
         * Distribution from which we sample and envelop the conjunctive
         * distribution.
         */
        public ProbabilityFunction<ParameterType> getSampler()
        {
            return this.sampler;
        }

        /**
         * Setter for sampler
         * @param sampler
         * Distribution from which we sample and envelop the conjunctive
         * distribution.
         */
        public void setSampler(
            final ProbabilityFunction<ParameterType> sampler)
        {
            this.sampler = sampler;
        }

    }
}
