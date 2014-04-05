/*
 * File:                DifferentiableEvaluator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.evaluator.Evaluator;

/**
 * Interface that indicates that the Evaluator can be differentiated about the
 * given input.
 *
 * @param <InputType> Input to the Evaluator
 * @param <OutputType> Output of the Evaluator
 * @param <DerivativeType> Derivative of the Evaluator
 * 
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Looks fine."
)
public interface DifferentiableEvaluator<InputType, OutputType, DerivativeType>
    extends Evaluator<InputType, OutputType>
{

    /**
     * Differentiates the output with respect to the input
     * @param input 
     * Input about which to compute the derivative
     * @return 
     * Derivative of the output with respect to the given input
     */
    public DerivativeType differentiate(
        InputType input );

}
