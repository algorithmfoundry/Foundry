/*
 * File:            ReversibleEvaluator.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2012 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.evaluator;

/**
 * Represents a {@link Evaluator} whose evaluation can be reversed. The
 * reverse is just another {@code Evaluator}. It is required that the
 * converter's range must be part of the domain of the reverse converter.
 * 
 * @param   <InputType>
 *      The input type to evaluate from.
 * @param   <OutputType>
 *      The output type to evaluate to.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public interface ReversibleEvaluator<InputType, OutputType, ReverseType extends Evaluator<? super OutputType, ? extends InputType>>
    extends Evaluator<InputType, OutputType>
{
    
    /**
     * Gets the data converter that performs the reverse conversion.
     *
     * @return The reverse converter.
     */
    public ReverseType reverse();

}
