/*
 * File:                AbstractCostFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 9, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Partial implementation of CostFunction.
 * @param <EvaluatedType>
 * Class type to evaluate, for example a "Vector" or "Evaluator"
 * @param <CostParametersType>
 * Class type that parameterizes the CostFunction, for example, a Collection of
 * InputOutputPairs.  Usually the dataset we're interested in.
 * @author Kevin R. Dixon
 * @since 3.1
 */
public abstract class AbstractCostFunction<EvaluatedType, CostParametersType>
    extends AbstractCloneableSerializable
    implements CostFunction<EvaluatedType, CostParametersType>
{

    /**
     * The parameters of the cost function.
     */
    protected CostParametersType costParameters;

    /** 
     * Creates a new instance of AbstractCostFunction 
     */
    public AbstractCostFunction()
    {
        this( null );
    }

    /**
     * Creates a new instance of AbstractCostFunction
     * @param costParameters The parameters of the cost function.
     */
    public AbstractCostFunction(
        CostParametersType costParameters)
    {
        this.setCostParameters(costParameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractCostFunction<EvaluatedType, CostParametersType> clone()
    {
        AbstractCostFunction<EvaluatedType, CostParametersType> clone =
            (AbstractCostFunction<EvaluatedType, CostParametersType>) super.clone();
        clone.setCostParameters(
            ObjectUtil.cloneSmart( this.getCostParameters() ) );
        return clone;
    }

    public void setCostParameters(
        CostParametersType costParameters)
    {
        this.costParameters = costParameters;
    }

    public CostParametersType getCostParameters()
    {
        return this.costParameters;
    }
    
}
