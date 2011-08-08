/*
 * File:                AbstractBayesianParameter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 8, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.util.AbstractNamed;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * Partial implementation of BayesianParameter
 * @param <ParameterType>
 * Type of parameter that changes the behavior of the conditional distribution.
 * @param <ConditionalType>
 * Type of parameterized distribution that generates observations.
 * @param <PriorType>
 * Assumed underlying distribution of parameters of the conditional distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractBayesianParameter<ParameterType,ConditionalType extends ClosedFormDistribution<?>,PriorType extends Distribution<ParameterType>>
    extends AbstractNamed
    implements BayesianParameter<ParameterType,ConditionalType,PriorType>
{

    /**
     * Distribution from which to pull the parameters.
     */
    protected ConditionalType conditionalDistribution;

    /**
     * Distribution of values that the parameter is assumed to take.
     */
    private PriorType parameterPrior;

    /** 
     * Creates a new instance of AbstractBayesianParameter
     */
    public AbstractBayesianParameter()
    {
        this( null, null, null );
    }

    /**
     * Creates a new instance of AbstractBayesianParameter
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param name
     * @param parameterPrior
     * Distribution of values that the parameter is assumed to take.
     */
    public AbstractBayesianParameter(
        ConditionalType conditionalDistribution,
        String name,
        PriorType parameterPrior)
    {
        super( name );
        this.setConditionalDistribution(conditionalDistribution);
        this.setParameterPrior(parameterPrior);
    }

    @Override
    public AbstractNamed clone()
    {
        @SuppressWarnings("unchecked")
        AbstractBayesianParameter<ParameterType,ConditionalType,PriorType> clone =
            (AbstractBayesianParameter<ParameterType,ConditionalType,PriorType>) super.clone();
        clone.setConditionalDistribution(
            ObjectUtil.cloneSafe( this.getConditionalDistribution() ) );
        clone.setParameterPrior( ObjectUtil.cloneSafe( this.getParameterPrior() ) );
        return clone;
    }

    /**
     * Getter for conditionalDistribution
     * @return
     * Distribution from which to pull the parameters.
     */
    public ConditionalType getConditionalDistribution()
    {
        return this.conditionalDistribution;
    }

    /**
     * Setter for conditionalDistribution
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     */
    protected void setConditionalDistribution(
        ConditionalType conditionalDistribution)
    {
        this.conditionalDistribution = conditionalDistribution;
    }

    /**
     * Getter for parameterPrior
     * @return
     * Distribution of values that the parameter is assumed to take.
     */
    public PriorType getParameterPrior()
    {
        return this.parameterPrior;
    }

    /**
     * Setter for parameterPrior
     * @param parameterPrior
     * Distribution of values that the parameter is assumed to take.
     */
    protected void setParameterPrior(
        PriorType parameterPrior)
    {
        this.parameterPrior = parameterPrior;
    }

    public void updateConditionalDistribution(
        Random random)
    {
        ParameterType parameter = this.parameterPrior.sample(random);
        this.setValue(parameter);
    }

}
