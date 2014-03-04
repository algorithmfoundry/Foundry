/*
 * File:                SupervisedPerformanceEvaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
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
import java.util.Collection;

/**
 * The {@code SupervisedPerformanceEvaluator} interface extends the
 * {@code PerformanceEvaluator} interface for performance evaluations of 
 * supervised machine learning algorithms where the target type is evaluated
 * against the estimated type produced by the evaluator.
 *
 * @param   <InputType> The input type to evaluate.
 * @param   <TargetType> The type of the ground-truth targets (the labels).
 * @param   <EstimateType> The type of estimate to evaluate.
 * @param   <ResultType> The output type of the performance evalautor.
 * @author  Justin Basilico
 * @since   2.0
 */
public interface SupervisedPerformanceEvaluator
    <InputType, TargetType, EstimateType, ResultType>
    extends PerformanceEvaluator
        <Evaluator<? super InputType, ? extends EstimateType>,
         Collection<? extends InputOutputPair<? extends InputType, TargetType>>,
         ResultType>
{

    /**
     * Evaluates the performance accuracy of the given estimates against the
     * given targets.
     *
     * @param  data The target-estimate pairs to use to evaluate performance.
     * @return The performance evaluation result.
     */
    ResultType evaluatePerformance(
        Collection<? extends TargetEstimatePair<? extends TargetType, ? extends EstimateType>> data);

}
