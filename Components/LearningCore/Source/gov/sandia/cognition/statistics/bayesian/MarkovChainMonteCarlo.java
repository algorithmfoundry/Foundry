/*
 * File:                MarkovChainMonteCarlo.java
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

import gov.sandia.cognition.algorithm.AnytimeAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.util.Randomized;

/**
 * Defines the functionality of a Markov chain Monte Carlo algorithm.
 * Technically, this algorithm allows for the sampling of a function where it's
 * difficult to sample directly from the distribution.  In machine learning
 * it's primarily used as to estimate the distribution of parameters of data.
 * As opposed to asking, "What is the most likely parameter that generated
 * the data?"  MCMC techniques can answer the question of "What does the
 * distribution of parameters look like that generated the data?"  The
 * algorithm works a lot like simulated annealing as follows.
 * The algorithm starts taking probability-directed steps in a target function
 * from some user-defined initial condition.  The first several steps
 * (1% of the total typically) are thrown out until the random steps have time
 * to "burn in" to the true probability space.  Then, the algorithm starts
 * recording the Collection of steps that it takes until it hits some
 * pre-defined number of samples.  It has been shown by Metropolis in the 1950s
 * that this Collection of samples necessarily follows the same distribution
 * as the target distribution, if the steps are taken in a clever manner.
 *
 * @param <ObservationType>
 * Type of observations handled by the MCMC algorithm.
 * @param <ParameterType>
 * Type of parameters to infer.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "Christian P. Robert",
                "George Casella"
            },
            title="Monte Carlo Statistical Methods, Second Edition",
            type=PublicationType.Book,
            year=2004,
            pages={267,320}
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Markov chain Monte Carlo",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Markov_chain_Monte_Carlo"
        )
    }

)
public interface MarkovChainMonteCarlo<ObservationType,ParameterType>
    extends BayesianEstimator<ObservationType,ParameterType,DataHistogram<ParameterType>>,
    AnytimeAlgorithm<DataHistogram<ParameterType>>,
    Randomized
{

    /**
     * Gets the number of iterations that must transpire before the algorithm
     * begins collection the samples.
     * @return
     * The number of iterations that must transpire before the algorithm
     * begins collection the samples.
     */
    public int getBurnInIterations();

    /**
     * Sets the number of iterations that must transpire before the algorithm
     * begins collection the samples.
     * @param burnInIterations
     * The number of iterations that must transpire before the algorithm
     * begins collection the samples.
     */
    public void setBurnInIterations(
        int burnInIterations );

    /**
     * Gets the number of iterations that must transpire between capturing
     * samples from the distribution.
     * @return
     * The number of iterations that must transpire between capturing
     * samples from the distribution.
     */
    public int getIterationsPerSample();

    /**
     * Sets the number of iterations that must transpire between capturing
     * samples from the distribution.
     * @param iterationsPerSample
     * The number of iterations that must transpire between capturing
     * samples from the distribution.
     */
    public void setIterationsPerSample(
        int iterationsPerSample );

    /**
     * Gets the current parameters in the random walk.
     * @return
     * The current parameters in the random walk.
     */
    public ParameterType getCurrentParameter();
    
}
