/*
 * File:                AbstractConjugatePriorBayesianEstimator.java
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

import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Partial implementation of ConjugatePriorBayesianEstimator that contains a initial belief
 * (prior) distribution function.
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
public abstract class AbstractConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType extends ClosedFormDistribution<ObservationType>,BeliefType extends ClosedFormDistribution<ParameterType>>
    extends AbstractBatchAndIncrementalLearner<ObservationType,BeliefType>
    implements ConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType,BeliefType>
{

    /**
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected BayesianParameter<ParameterType,ConditionalType,BeliefType> parameter;

    /** 
     * Creates a new instance of AbstractConjugatePriorBayesianEstimator
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    public AbstractConjugatePriorBayesianEstimator(
        BayesianParameter<ParameterType,ConditionalType,BeliefType> parameter )
    {
        super();
        this.setParameter(parameter);
    }

    @Override
    public AbstractConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType,BeliefType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType,BeliefType> clone =
            (AbstractConjugatePriorBayesianEstimator<ObservationType,ParameterType,ConditionalType,BeliefType>) super.clone();
        clone.setParameter( ObjectUtil.cloneSafe( this.getParameter() ) );
        return clone;
    }

    public BeliefType createInitialLearnedObject()
    {
        return ObjectUtil.cloneSmart(this.getInitialBelief());
    }

    public ConditionalType createConditionalDistribution(
        ParameterType parameter)
    {
        this.parameter.setValue(parameter);
        return ObjectUtil.cloneSafe(this.parameter.getConditionalDistribution());
    }

    /**
     * Getter for initialBelief.
     * @return
     * Initial belief distribution of the parameters.
     */
    public BeliefType getInitialBelief()
    {
        return this.parameter.getParameterPrior();
    }

    public BayesianParameter<ParameterType,ConditionalType,BeliefType> getParameter()
    {
        return this.parameter;
    }

    /**
     * Setter for parameter
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected void setParameter(
        BayesianParameter<ParameterType,ConditionalType,BeliefType> parameter)
    {
        this.parameter = parameter;
    }

}
