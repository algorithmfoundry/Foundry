/*
 * File:                RecursiveBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 13, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.IncrementalLearner;
import gov.sandia.cognition.statistics.Distribution;

/**
 * A recursive Bayesian estimator is an estimation method that uses the
 * previous belief of the system parameter and a single observation to refine
 * the estimate of the system parameter.
 * @param <ObservationType>
 * Type of observations incorporated by the model
 * @param <ParameterType>
 * Type of parameter that we are estimating
 * @param <BeliefType>
 * Belief distribution of the parameter
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Recursive Bayesian estimation",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Recursive_Bayesian_estimation"
)
public interface RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType extends Distribution<ParameterType>>
    extends BayesianEstimator<ObservationType,ParameterType,BeliefType>,
    IncrementalLearner<ObservationType,BeliefType>
{
}
