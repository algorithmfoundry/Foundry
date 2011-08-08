/*
 * File:                ClosedFormDifferentiableEvaluator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 14, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.evaluator.Evaluator;

/**
 * A differentiable function that has a closed-form derivative.
 * @param <InputType> Input to the Evaluator
 * @param <OutputType> Output of the Evaluator
 * @param <DerivativeType> Derivative of the Evaluator
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormDifferentiableEvaluator<InputType, OutputType, DerivativeType>
    extends DifferentiableEvaluator<InputType,OutputType,DerivativeType>
{

    /**
     * Gets the closed-form derivative of the function.
     * @return
     * Closed-form derivative of the function.
     */
    public Evaluator<InputType,DerivativeType> getDerivative();
    
}
