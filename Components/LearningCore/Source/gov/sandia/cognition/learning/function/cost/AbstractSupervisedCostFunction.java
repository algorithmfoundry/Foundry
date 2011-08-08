/*
 * File:                AbstractSupervisedCostFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.data.WeightedTargetEstimatePair;
import gov.sandia.cognition.learning.performance.AbstractSupervisedPerformanceEvaluator;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Partial implementation of SupervisedCostFunction
 * @param <InputType> Input type of the dataset and Evaluator
 * @param <TargetType> Output type (labels) of the dataset and Evaluator
 * @author Kevin R. Dixon
 * @since 2.0
 */
public abstract class AbstractSupervisedCostFunction<InputType, TargetType>
    extends AbstractSupervisedPerformanceEvaluator<InputType, TargetType, TargetType, Double>
    implements SupervisedCostFunction<InputType, TargetType>
{

    /**
     * Labeled dataset to use to evaluate the cost against
     */
    private Collection<? extends InputOutputPair<? extends InputType, TargetType>> costParameters;

    /** 
     * Creates a new instance of AbstractSupervisedCostFunction 
     */
    public AbstractSupervisedCostFunction()
    {
        this.setCostParameters( null );
    }

    /**
     * Creates a new instance of AbstractSupervisedCostFunction 
     * @param costParameters
     * Labeled dataset to use to evaluate the cost against
     */
    public AbstractSupervisedCostFunction(
        Collection<? extends InputOutputPair<? extends InputType, TargetType>> costParameters )
    {
        this.setCostParameters( costParameters );
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractSupervisedCostFunction<InputType, TargetType> clone()
    {
        AbstractSupervisedCostFunction<InputType, TargetType> clone =
            (AbstractSupervisedCostFunction<InputType, TargetType>) super.clone();
        clone.setCostParameters(
            ObjectUtil.cloneSmartElementsAsArrayList(this.getCostParameters()) );
        return clone;
    }

    @Override
    public abstract Double evaluatePerformance(
        Collection<? extends TargetEstimatePair<TargetType, TargetType>> data );

    public Double evaluate(
        Evaluator<? super InputType, ? extends TargetType> evaluator )
    {
        ArrayList<WeightedTargetEstimatePair<TargetType, TargetType>> targetEstimatePairs =
            new ArrayList<WeightedTargetEstimatePair<TargetType, TargetType>>( this.getCostParameters().size() );

        for (InputOutputPair<? extends InputType, ? extends TargetType> io : this.getCostParameters())
        {
            TargetType estimate = evaluator.evaluate( io.getInput() );
            targetEstimatePairs.add( new WeightedTargetEstimatePair<TargetType, TargetType>(
                io.getOutput(), estimate, DatasetUtil.getWeight(io) ) );
        }

        return this.evaluatePerformance( targetEstimatePairs );
    }

    public Collection<? extends InputOutputPair<? extends InputType, TargetType>> getCostParameters()
    {
        return this.costParameters;
    }

    public void setCostParameters(
        Collection<? extends InputOutputPair<? extends InputType, TargetType>> costParameters )
    {
        this.costParameters = costParameters;
    }

    @Override
    public Double summarize(
        Collection<? extends TargetEstimatePair<TargetType, TargetType>> data )
    {
        return this.evaluatePerformance( data );
    }

}
