/*
 * File:                BayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Nov 16, 2009, Sandia Corporation.
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
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.statistics.Distribution;
import java.util.Collection;

/**
 * A type of estimation procedure based on Bayes's rule, which allows us
 * to estimate the uncertainty of parameters given a set of observations
 * that we are given.
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
@PublicationReferences(
    references={
        @PublicationReference(
            author="William M. Bolstad",
            title="Introduction to Bayesian Statistics: Second Edition",
            type=PublicationType.Book,
            year=2007,
            notes="Good introductory text."
        )
        ,
        @PublicationReference(
            author="Christian P. Robert",
            title="The Bayesian Choice: From Decision-Theoretic Foundations to Computational Implementation, Second Edition",
            type=PublicationType.Book,
            year=2007,
            notes="Good advanced text."
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Bayes estimator",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Bayes_estimator"
        )
    }
)
public interface BayesianEstimator<ObservationType, ParameterType,
        PosteriorType extends Distribution<? extends ParameterType>>
    extends BatchLearner<Collection<? extends ObservationType>,PosteriorType>
{
}
