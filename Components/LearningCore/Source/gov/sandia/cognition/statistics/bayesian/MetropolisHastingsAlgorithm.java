/*
 * File:                MetropolisHastingsAlgorithm.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Sep 30, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;

/**
 * An implementation of the Metropolis-Hastings MCMC algorithm, which is the
 * most general formulation of MCMC but can be slow.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType>
 * Type of observations handled by the MCMC algorithm.
 * @param <ParameterType>
 * Type of parameters to infer.
 */
@PublicationReference(
    author="Wikipedia",
    title="Metropolis–Hastings algorithm",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Metropolis-Hastings_algorithm"
)
public class MetropolisHastingsAlgorithm<ObservationType,ParameterType>
    extends AbstractMarkovChainMonteCarlo<ObservationType,ParameterType>
    implements MeasurablePerformanceAlgorithm
{

    /**
     * Performance statistic name, {@value}.
     */
    public static final String PERFORMANCE_NAME = "Current Log Likelihood";

    /**
     * Log Likelihood of the current parameters.
     */
    private double currentLogLikelihood;

    /**
     * The object that makes proposal samples from the current location.
     */
    protected MetropolisHastingsAlgorithm.Updater<ObservationType,ParameterType> updater;

    /**
     * Creates a new instance of MetropolisHastingsAlgorithm.
     */
    public MetropolisHastingsAlgorithm()
    {
        super();
        this.setBurnInIterations(this.maxIterations/10);
        this.setIterationsPerSample(this.maxIterations/100);
    }

    @Override
    public MetropolisHastingsAlgorithm<ObservationType,ParameterType> clone()
    {
        @SuppressWarnings("unchecked")
        MetropolisHastingsAlgorithm<ObservationType,ParameterType> clone =
            (MetropolisHastingsAlgorithm<ObservationType,ParameterType>) super.clone();
        clone.setUpdater( ObjectUtil.cloneSafe( this.getUpdater() ) );
        return clone;
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        this.currentLogLikelihood = Double.NEGATIVE_INFINITY;
        return super.initializeAlgorithm();
    }

    @Override
    protected void mcmcUpdate()
    {

        WeightedValue<ParameterType> proposal = null;
        double proposalLogLikelihood = 0.0;
        boolean acceptProposal = false;
        while( !acceptProposal )
        {
            proposal = this.updater.makeProposal(this.currentParameter);
            proposalLogLikelihood = this.getUpdater().computeLogLikelihood(
                proposal.getValue(), this.data );
            
            if( Double.isInfinite(this.currentLogLikelihood) )
            {
                acceptProposal = true;
                break;
            }

            final double pratio = Math.exp( proposalLogLikelihood - this.currentLogLikelihood );
            final double qratio = proposal.getWeight();

            final double a = qratio * pratio;

            // If the proposal has higher probability, then always accept it
            if( a >= 1.0 )
            {
                acceptProposal = true;
            }
            else
            {
                // Allow the sample with probability "a"
                double r = this.random.nextDouble();
                if( r <= a )
                {
                    acceptProposal = true;
                }
            }
        }

        this.currentParameter = proposal.getValue();
        this.currentLogLikelihood = proposalLogLikelihood;

    }

    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>( PERFORMANCE_NAME, this.currentLogLikelihood );
    }

    /**
     * Gets the object that makes proposal samples from the current location.
     * @return
     * The object that makes proposal samples from the current location.
     */
    public MetropolisHastingsAlgorithm.Updater<ObservationType,ParameterType> getUpdater()
    {
        return this.updater;
    }

    /**
     * Sets the object that makes proposal samples from the current location.
     * @param updater
     * The object that makes proposal samples from the current location.
     */
    public void setUpdater(
        MetropolisHastingsAlgorithm.Updater<ObservationType,ParameterType> updater)
    {
        this.updater = updater;
    }

    public ParameterType createInitialLearnedObject()
    {
        return this.getUpdater().createInitialParameter();
    }

    /**
     * Creates proposals for the MCMC steps.
     * @param <ObservationType>
     * Type of observations handled by the MCMC algorithm.
     * @param <ParameterType>
     * Type of parameters to infer.
     */
    public static interface Updater<ObservationType,ParameterType>
        extends CloneableSerializable
    {

        /**
         * Creates the initial parameterization
         * @return
         * Initial parameters
         */
        public ParameterType createInitialParameter();

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
            ParameterType parameter,
            Iterable<? extends ObservationType> data );

        /**
         * Makes a proposal update given the current parameter set
         * @param location Location from which to make a proposal
         * @return
         * Location of the proposed sample, weighted by the "q" ratio.
         */
        public WeightedValue<ParameterType> makeProposal(
            ParameterType location );

    }

}
