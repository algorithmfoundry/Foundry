/*
 * File:                ParticleFilter.java
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
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.Randomized;

/**
 * A particle filter aims to estimate a sequence of hidden parameters
 * based on observed data using point-mass estimates of the posterior
 * distribution.  Particle filters are sometimes called Sequential Monte Carlo
 * estimation.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType>
 * Type of observations handled by the algorithm.
 * @param <ParameterType>
 * Type of parameters to infer.
 */
@PublicationReferences(
    references={
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
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Particle filter",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Particle_filter"
        )
    }
)
public interface ParticleFilter<ObservationType,ParameterType>
    extends RecursiveBayesianEstimator<ObservationType,ParameterType,DataDistribution<ParameterType>>,
    Randomized
{

    /**
     * Gets the updater
     * @return
     * Updater algorithm that updates the particles.
     */
    public ParticleFilter.Updater<ObservationType,ParameterType> getUpdater();

    /**
     * Gets the number of particles
     * @return
     * Number of particles.
     */
    public int getNumParticles();

    /**
     * Sets the number of particles
     * @param numParticles
     * Number of particles.
     */
    public void setNumParticles(
        int numParticles );

    /**
     * Computes the effective number of particles.
     * @param particles
     * Current state of the Particle filter.
     * @return
     * Effective number of particles.
     */
    public double computeEffectiveParticles(
        DataDistribution<ParameterType> particles );

    /**
     * Updates the particles.
     * @param <ObservationType>
     * Type of observations.
     * @param <ParameterType> Type of parameter to update.
     */
    public static interface Updater<ObservationType,ParameterType>
        extends CloneableSerializable
    {

        /**
         * Makes a proposal update given the current parameter set
         * @param previousParameter
         * Parameters from which to update
         * @return
         * Proposed parameters
         */
        public ParameterType update(
            ParameterType previousParameter );

        /**
         * Creates the initial particles.
         * @param numParticles
         * Number of particles to create.
         * @return
         * Initial particle distribution.
         */
        public DataDistribution<ParameterType> createInitialParticles(
            int numParticles );

        /**
         * Computes the log likelihood of the parameter and the observation.
         * @param particle
         * Parameter to evaluate.
         * @param observation
         * Observation to compute the likelihood of.
         * @return
         * Log likelihood of the parameter and the observation.
         */
        public double computeLogLikelihood(
            ParameterType particle,
            ObservationType observation );

    }

}
