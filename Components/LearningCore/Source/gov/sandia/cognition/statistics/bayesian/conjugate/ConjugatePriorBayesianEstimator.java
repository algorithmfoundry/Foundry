/*
 * File:                ConjugatePriorBayesianEstimator.java
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

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.bayesian.RecursiveBayesianEstimator;

/**
 * A Bayesian Estimator that makes use of conjugate priors, which is a
 * mathematical trick when the conditional and the prior result a posterior
 * that is the same type as the prior.
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
@PublicationReferences(
    references={
        @PublicationReference(
            author="Daniel Fink",
            title="A Compendium of Conjugate Priors",
            type=PublicationType.TechnicalReport,
            year=1997,
            url="http://www.stat.columbia.edu/~cook/movabletype/mlm/CONJINTRnew%2BTEX.pdf"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Conjugate Prior",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Conjugate_prior"
        )
    }
)
public interface ConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType extends ClosedFormDistribution<ObservationType>,BeliefType extends ClosedFormDistribution<ParameterType>>
    extends RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType>
{

    /**
     * Creates a parameter linking the conditional and prior distributions
     * @param conditional
     * Distribution from which observations are generated
     * @param prior
     * Distribution that generates parameters for the conditional
     * @return
     * Parameter describing the relationship between the conditional and prior
     */
    public BayesianParameter<ParameterType,ConditionalType,BeliefType> createParameter(
        ConditionalType conditional,
        BeliefType prior );

    /**
     * Gets the Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     * @return
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    public BayesianParameter<ParameterType,ConditionalType,BeliefType> getParameter();

    /**
     * Creates an instance of the class conditional distribution,
     * parameterized by the given parameter value.  This is the distribution
     * that we implicitly draw observation samples from.
     * @param parameter 
     * Parameter used to create the class conditional distribution.
     * @return
     * Parameterized class conditional distribution.
     */
    public ConditionalType createConditionalDistribution(
        ParameterType parameter );

    /**
     * Computes the equivalent sample size of using the given prior.  This is
     * effectively how many samples of bias the prior injects into the
     * estimate.
     * @param belief
     * Prior belief to measure.
     * @return
     * Equivalent sample size of the initial belief.
     */
    public double computeEquivalentSampleSize(
        BeliefType belief );

}
