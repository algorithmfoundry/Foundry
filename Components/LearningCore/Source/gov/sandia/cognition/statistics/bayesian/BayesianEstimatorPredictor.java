/*
 * File:                BayesianEstimatorPredictor.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 23, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.Distribution;

/**
 * A BayesianEstimator that can also compute the predictive distribution of
 * new data given the posterior.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <ObservationType>
 * Observations from the ConditionalType that are used to estimate the
 * parameters of the distribution.
 * @param <ParameterType>
 * Type of parameter estimated by this algorithm, which is used to
 * parameterize the conditional distribution.
 * @param <PosteriorType>
 * Type of posterior Distribution, which describes the uncertainty of the
 * parameters after we have incorporated the observations.
 */
public interface BayesianEstimatorPredictor<ObservationType, ParameterType,
        PosteriorType extends Distribution<? extends ParameterType>>
    extends BayesianEstimator<ObservationType,ParameterType,PosteriorType>
{

    /**
     * Creates the predictive distribution of new data given the posterior.
     * This is equivalent to evaluating the integral of:
     * p( newdata | data ) = integral( conditional( newdata | data, parameters ) * p( parameters | data ) dparameters )
     * @param posterior
     * Posterior distribution from which to compute the predictive posterior.
     * @return
     * Predictive distribution of new data given the observed data.
     */
    public ComputableDistribution<ObservationType> createPredictiveDistribution(
        PosteriorType posterior );


}
