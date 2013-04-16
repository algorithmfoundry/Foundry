/*
 * File:            ScalarFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.evaluator.Evaluator;

/**
 * Interface for a function that maps some input onto a double.
 *
 * @param   <InputType>
 *      The type of the input to the scalar function.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public interface ScalarFunction<InputType>
    extends Evaluator<InputType, Double>
{

    /**
     * Evaluates the scalar function as a double.
     *
     * @param   input
     *      The input value.
     * @return
     *      The scalar output calculated from the given input.
     */
    public double evaluateAsDouble(
        final InputType input);

}
