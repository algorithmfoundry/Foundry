/*
 * File:                SamplingImportanceResamplingParticleFilter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Dec 15, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;

/**
 * An implementation of the standard Sampling Importance Resampling
 * particle filter.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType>
 * Type of observations handled by the algorithm.
 * @param <ParameterType>
 * Type of parameters to infer.
 */
@PublicationReference(
    author={
        "M. Sanjeev Arulampalam",
        "Simon Maskell",
        "Neil Gordon",
        "Tim Clapp"
    },
    title="A Tutorial on Particle Filters for Online Nonlinear/Non-Gaussian Bayesian Tracking",
    type=PublicationType.Journal,
    publication="IEEE Transactions on Signal Processing, Vol. 50, No. 2",
    year=2002,
    pages={174,188},
    url="http://people.cs.ubc.ca/~murphyk/Software/Kalman/ParticleFilterTutorial.pdf"
)
public class SamplingImportanceResamplingParticleFilter<ObservationType,ParameterType>
    extends AbstractParticleFilter<ObservationType,ParameterType>
{
    /**
     * Percentage of effective particles, below which we resample.
     */
    protected double particlePctThreadhold;

    /** 
     * Creates a new instance of SamplingImportanceResamplingParticleFilter
     */
    public SamplingImportanceResamplingParticleFilter()
    {
        super();
    }

    /**
     * Getter for particlePctThreadhold
     * @return
     * Number of effective particles, below which we resample.
     */
    public double getParticlePctThreadhold()
    {
        return this.particlePctThreadhold;
    }

    /**
     * Setter for particlePctThreadhold
     * @param particlePctThreadhold
     * Number of effective particles, below which we resample.
     */
    public void setParticlePctThreadhold(
        double particlePctThreadhold)
    {
        ProbabilityUtil.assertIsProbability(particlePctThreadhold);
        this.particlePctThreadhold = particlePctThreadhold;
    }

    @Override
    public void update(
        DataDistribution<ParameterType> particles,
        ObservationType value)
    {

        // Sample from the existing weighted particles.
        ArrayList<? extends ParameterType> sampledParticles =
            particles.sample( this.random, this.numParticles );

        // Weight the samples by the observation likelihood.
        ArrayList<DefaultWeightedValue<ParameterType>> updatedParticles =
            new ArrayList<DefaultWeightedValue<ParameterType>>( this.numParticles );
        double weightSum = 0.0;
        for( ParameterType sampledParticle : sampledParticles )
        {
            ParameterType updatedParticle =
                this.getUpdater().update( sampledParticle );
            double previousWeight = particles.get(sampledParticle);
            double weight = previousWeight * Math.exp(
                this.getUpdater().computeLogLikelihood( updatedParticle, value) );

            weightSum += weight;
            updatedParticles.add( new DefaultWeightedValue<ParameterType>(
                updatedParticle, weight ) );
        }

        // Normalize the weights and add them back into the PMF.
        particles.clear();
        for( DefaultWeightedValue<ParameterType> updatedParticle : updatedParticles )
        {
            final double weight = updatedParticle.getWeight();
            particles.set( updatedParticle.getValue(), weight/weightSum );
        }

        // Now make sure we've got enough effective particles.
        double particlePct =
            this.computeEffectiveParticles(particles) / this.getNumParticles();
        if( particlePct < this.getParticlePctThreadhold() )
        {
            // Sample from the current belief (this will be close to degenerate)
            sampledParticles = particles.sample(this.random, this.numParticles);
            final double uniformWeight = 1.0/this.numParticles;
            particles.clear();

            // Resample new particles from the existing ones but assign
            // uniform weight.
            for( ParameterType sampledParticle : sampledParticles )
            {
                ParameterType resampledParticle =
                    this.getUpdater().update( sampledParticle );
                particles.set( resampledParticle, uniformWeight );
            }
        }
    }

}
