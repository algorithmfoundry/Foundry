/*
 * File:                AbstractParticleFilter.java
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

import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * Partial abstract implementation of ParticleFilter.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType>
 * Type of observations handled by the algorithm.
 * @param <ParameterType>
 * Type of parameters to infer.
 */
public abstract class AbstractParticleFilter<ObservationType,ParameterType>
    extends AbstractBatchAndIncrementalLearner<ObservationType,DataDistribution<ParameterType>>
    implements ParticleFilter<ObservationType,ParameterType>
{

    /**
     * Updates the particle given an existing particle.
     */
    protected ParticleFilter.Updater<ObservationType,ParameterType> updater;

    /**
     * Random number generator.
     */
    protected transient Random random;

    /**
     * Number of particles in the filter.
     */
    protected int numParticles;

    /**
     * Default constructor.
     */
    public AbstractParticleFilter()
    {
    }

    @Override
    public AbstractParticleFilter<ObservationType,ParameterType> clone()
    {
        AbstractParticleFilter<ObservationType,ParameterType> clone =
            (AbstractParticleFilter<ObservationType,ParameterType>) super.clone();
        clone.setUpdater( ObjectUtil.cloneSafe( this.getUpdater() ) );

        return clone;
    }

    public ParticleFilter.Updater<ObservationType,ParameterType> getUpdater()
    {
        return this.updater;
    }

    /**
     * Setter for updater
     * @param updater
     * Updater algorithm that updates the particles.
     */
    public void setUpdater(
        ParticleFilter.Updater<ObservationType,ParameterType> updater)
    {
        this.updater = updater;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public void setRandom(
        Random random)
    {
        this.random = random;
    }

    public DataDistribution<ParameterType> createInitialLearnedObject()
    {
        return this.getUpdater().createInitialParticles( this.getNumParticles() );
    }

    public double computeEffectiveParticles(
        DataDistribution<ParameterType> particles)
    {
        // This is Equation (50) in Arulampalam's IEEE Trans paper on p. 179
        double totalMass = particles.getTotal();
        double sumSquared = 0.0;
        for( ParameterType particle : particles.getDomain() )
        {
            double w = particles.get(particle) / totalMass;
            sumSquared += w*w;
        }

        return 1.0 / sumSquared;
        
    }

    public int getNumParticles()
    {
        return this.numParticles;
    }

    public void setNumParticles(
        int numParticles)
    {
        this.numParticles = numParticles;
    }

}
