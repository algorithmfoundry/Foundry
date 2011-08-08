/*
 * File:                ConjugatePriorBayesianEstimatorPredictor.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 3, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.bayesian.BayesianEstimatorPredictor;

/**
 * A conjugate prior estimator that also has a closed-form predictive posterior.
 * @param <ObservationType>
 * Observations from the ConditionalType that are used to estimate the
 * parameters of the distribution.
 * @param <BeliefType>
 * Type of Distribution that represents uncertainty in the parameters.
 * @param <ParameterType>
 * Type of parameter estimated by this algorithm, which is used to
 * parameterize the conditional distribution.
 * @param <ConditionalType>
 * Type of conditional distribution that generates observations for this
 * relationship.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ConjugatePriorBayesianEstimatorPredictor<ObservationType,ParameterType,ConditionalType extends ClosedFormDistribution<ObservationType>,BeliefType extends ClosedFormDistribution<ParameterType>>
    extends ConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType,BeliefType>,
    BayesianEstimatorPredictor<ObservationType,ParameterType,BeliefType>
{
}
