/*
 * File:                AbstractSupervisedErrorMeasure.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Summarizer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code AbstractSupervisedPerformanceEvaluator} class contains an 
 * abstract implementation of the {@code SupervisedPerformanceEvaluator} class.
 * It does the conversion of a dataset of input-target pairs to a set of
 * target-estimate pairs given the estimate returned by the evaluator whose
 * performance is being assessed.
 *
 * @param   <InputType> The input type to evaluate.
 * @param   <TargetType> The type of the ground-truth targets (the labels).
 * @param   <EstimateType> The type of estimate to evaluate.
 * @param   <ResultType> The output type of the performance evalautor.
 * @author  Justin Basilico
 * @since   2.0
 */
public abstract class AbstractSupervisedPerformanceEvaluator<InputType, TargetType, EstimateType, ResultType>
    extends AbstractCloneableSerializable
    implements SupervisedPerformanceEvaluator<InputType, TargetType, EstimateType, ResultType>,
    Summarizer<TargetEstimatePair<TargetType, EstimateType>, ResultType>
{

    /**
     * Creates a new AbstractSupervisedPerformanceEvaluator.
     */
    public AbstractSupervisedPerformanceEvaluator()
    {
        super();
    }

    /**
     * Evaluates the performance accuracy of the given estimates against the
     * given targets.
     *
     * @param evaluator Evaluator to generate estimates
     * @param  data The target-estimate pairs to use to evaluate performance.
     * @return The performance evaluation result.
     */
    public ResultType evaluatePerformance(
        final Evaluator<? super InputType, EstimateType> evaluator,
        final Collection<? extends InputOutputPair<InputType, TargetType>> data )
    {
        // Use the given evaluator to compute the Target-Estimate pairs for
        // the given data.
        final ArrayList<TargetEstimatePair<TargetType, EstimateType>> pairs =
            new ArrayList<TargetEstimatePair<TargetType, EstimateType>>( data.size() );

        for (InputOutputPair<? extends InputType, TargetType> example : data)
        {
            final InputType input = example.getInput();
            final TargetType target = example.getOutput();
            final EstimateType estimate = evaluator.evaluate( input );
            pairs.add( new TargetEstimatePair<TargetType, EstimateType>(
                target, estimate ) );
        }

        // Evaluate the performance of the pairs.
        return this.evaluatePerformance( pairs );
    }

    public ResultType summarize(
        Collection<? extends TargetEstimatePair<TargetType, EstimateType>> data )
    {
        return this.evaluatePerformance( data );
    }

    public abstract ResultType evaluatePerformance(
        Collection<? extends TargetEstimatePair<TargetType, EstimateType>> data );

}
