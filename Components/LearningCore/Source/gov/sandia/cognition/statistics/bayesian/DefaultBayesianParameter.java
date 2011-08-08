/*
 * File:                DefaultBayesianParameter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 27, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.DefaultDistributionParameter;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * Default implementation of BayesianParameter using reflection.
 * @param <ParameterType> Type of parameters.
 * @param <ConditionalType>
 * Type of parameterized distribution that generates observations.
 * @param <PriorType>
 * Assumed underlying distribution of parameters of the conditional distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class DefaultBayesianParameter<ParameterType,ConditionalType extends ClosedFormDistribution<?>,PriorType extends Distribution<ParameterType>>
    extends DefaultDistributionParameter<ParameterType,ConditionalType>
    implements BayesianParameter<ParameterType,ConditionalType,PriorType>
{

    /**
     * Distribution of values that the parameter is assumed to take.
     */
    private PriorType parameterPrior;

    /** 
     * Creates a new instance of DefaultBayesianParameter
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param parameterName
     * Name of the parameter
     */
    public DefaultBayesianParameter(
        ConditionalType conditionalDistribution,
        String parameterName )
    {
        this( conditionalDistribution, parameterName, null );
    }

    /**
     * Creates a new instance of DefaultBayesianParameter
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param parameterName
     * Name of the parameter
     * @param parameterPrior
     * Distribution of values that the parameter is assumed to take.
     */
    public DefaultBayesianParameter(
        ConditionalType conditionalDistribution,
        String parameterName,
        PriorType parameterPrior )
    {
        super( conditionalDistribution, parameterName );
        this.setParameterPrior(parameterPrior);
    }

    @Override
    public DefaultBayesianParameter<ParameterType,ConditionalType,PriorType> clone()
    {
        @SuppressWarnings("unchecked")
        DefaultBayesianParameter<ParameterType,ConditionalType,PriorType> clone =
            (DefaultBayesianParameter<ParameterType,ConditionalType,PriorType>) super.clone();

        clone.setParameterPrior(
            ObjectUtil.cloneSafe( this.getParameterPrior() ) );
        clone.setName( this.getName() );
        return clone;
    }

    @Override
    public PriorType getParameterPrior()
    {
        return this.parameterPrior;
    }

    /**
     * Sets the Distribution of values that the parameter is assumed to take.
     * @param parameterPrior
     * Distribution of values that the parameter is assumed to take.
     */
    public void setParameterPrior(
        PriorType parameterPrior)
    {
        this.parameterPrior = parameterPrior;
    }

    @Override
    public void updateConditionalDistribution(
        Random random)
    {
        ParameterType parameter = this.parameterPrior.sample(random);
        this.setValue(parameter);
    }

    /**
     * Creates a new instance of DefaultBayesianParameter
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param parameterName
     * Name of the parameter
     * @param parameterPrior
     * Distribution of values that the parameter is assumed to take.
     * @param <ParameterType> Type of parameters.
     * @param <ConditionalType>
     * Type of parameterized distribution that generates observations.
     * @param <PriorType>
     * Assumed underlying distribution of parameters of the conditional distribution.
     * @return
     * Creates a new instance of DefaultBayesianParameter
     */
    public static <ParameterType,ConditionalType extends ClosedFormDistribution<?>,PriorType extends Distribution<ParameterType>> DefaultBayesianParameter<ParameterType,ConditionalType,PriorType> create(
        ConditionalType conditionalDistribution,
        String parameterName,
        PriorType parameterPrior )
    {
        return new DefaultBayesianParameter<ParameterType, ConditionalType, PriorType>(
            conditionalDistribution, parameterName, parameterPrior);
    }

}
