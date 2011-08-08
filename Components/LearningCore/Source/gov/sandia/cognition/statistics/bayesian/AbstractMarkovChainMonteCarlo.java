/*
 * File:                AbstractMarkovChainMonteCarlo.java
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

import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * Partial abstract implementation of MarkovChainMonteCarlo.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType>
 * Type of observations handled by the MCMC algorithm.
 * @param <ParameterType>
 * Type of parameters to infer.
 */
public abstract class AbstractMarkovChainMonteCarlo<ObservationType,ParameterType>
    extends AbstractAnytimeBatchLearner<Iterable<? extends ObservationType>,DataHistogram<ParameterType>>
    implements MarkovChainMonteCarlo<ObservationType,ParameterType>
{

    /**
     * Default number of sample/iterations, {@value}.
     */
    public static final int DEFAULT_NUM_SAMPLES = 1000;

    /**
     * Random number generator.
     */
    protected Random random;

    /**
     * The number of iterations that must transpire before the algorithm
     * begins collection the samples.
     */
    private int burnInIterations;

    /**
     * The number of iterations that must transpire between capturing
     * samples from the distribution.
     */
    private int iterationsPerSample;

    /**
     * The current parameters in the random walk.
     */
    protected ParameterType currentParameter;

    /**
     * The previous parameter in the random walk.
     */
    protected ParameterType previousParameter;

    /**
     * Resulting parameters to return.
     */
    private transient MapBasedDataHistogram<ParameterType> result;

    /**
     * Creates a new instance of AbstractMarkovChainMonteCarlo
     */
    public AbstractMarkovChainMonteCarlo()
    {
        super( DEFAULT_NUM_SAMPLES );
        this.setIterationsPerSample(1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMarkovChainMonteCarlo clone()
    {
        AbstractMarkovChainMonteCarlo<ObservationType,ParameterType> clone =
            (AbstractMarkovChainMonteCarlo<ObservationType,ParameterType>) super.clone();
        clone.setRandom( ObjectUtil.cloneSmart( this.getRandom() ) );
        clone.setCurrentParameter(
            ObjectUtil.cloneSmart( this.getCurrentParameter() ) );
        return clone;
    }

    public int getBurnInIterations()
    {
        return this.burnInIterations;
    }

    public void setBurnInIterations(
        int burnInIterations)
    {
        if( burnInIterations < 0 )
        {
            throw new IllegalArgumentException( "burnInIterations must be >= 0" );
        }
        this.burnInIterations = burnInIterations;
    }

    public int getIterationsPerSample()
    {
        return this.iterationsPerSample;
    }

    public void setIterationsPerSample(
        int iterationsPerSample)
    {
        if( iterationsPerSample < 1 )
        {
            throw new IllegalArgumentException( "iterationsPerSample must be >= 1" );
        }

        this.iterationsPerSample = iterationsPerSample;
    }

    public MapBasedDataHistogram<ParameterType> getResult()
    {
        return this.result;
    }

    /**
     * Setter for result
     * @param result
     * Results to return.
     */
    protected void setResult(
        MapBasedDataHistogram<ParameterType> result)
    {
        this.result = result;
    }

    public ParameterType getCurrentParameter()
    {
        return this.currentParameter;
    }

    /**
     * Setter for currentParameter.
     * @param currentParameter
     * The current location in the random walk.
     */
    protected void setCurrentParameter(
        ParameterType currentParameter )
    {
        this.currentParameter = currentParameter;
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

    /**
     * Performs a valid MCMC update step.  That is, the function is expected to
     * modify the currentParameter member.
     */
    abstract protected void mcmcUpdate();

    /**
     * Creates the initial parameters from which to start the Markov chain.
     * @return
     * initial parameters from which to start the Markov chain.
     */
    abstract public ParameterType createInitialLearnedObject();

    @Override
    protected boolean initializeAlgorithm()
    {
        this.previousParameter =
            ObjectUtil.cloneSmart(this.createInitialLearnedObject());
        this.setCurrentParameter( this.previousParameter );

        for( int i = 0; i < this.getBurnInIterations(); i++ )
        {
            this.mcmcUpdate();
        }

        this.setResult( new MapBasedDataHistogram<ParameterType>(
            this.getMaxIterations() ) );

        return true;

    }

    @Override
    protected boolean step()
    {

        for( int i = 0; i < this.iterationsPerSample; i++ )
        {
            this.mcmcUpdate();
        }

        // Put a clone of the current parameter into the array list.
        this.previousParameter = ObjectUtil.cloneSmart(this.currentParameter);
        this.result.add( this.previousParameter );
        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
    }

    /**
     * Getter for previousParameter
     * @return
     * The previous parameter in the random walk.
     */
    public ParameterType getPreviousParameter()
    {
        return this.previousParameter;
    }

}
