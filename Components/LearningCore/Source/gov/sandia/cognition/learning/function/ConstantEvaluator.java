/*
 * File:                ConstantEvaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The {@code ConstantEvaluator} class implements an {@code Evaluator} that
 * always returns the same output value.
 * 
 * @param   <OutputType> The output type of the evaluator.
 * @author  Justin Basilico
 * @since   2.1
 */
public class ConstantEvaluator<OutputType>
    extends AbstractCloneableSerializable
    implements Evaluator<Object, OutputType>
{
    // TODO: This class should be moved to the evaluator package in Common Core.
    // -- jdbasil (2010-10-21)
    
    /** The output value. */
    protected OutputType value;
    
    /**
     * Creates a new {@code ConstantEvaluator}.
     */
    public ConstantEvaluator()
    {
        this(null);
    }
    
    /**
     * Creates a new {@code ConstantEvaluator}.
     * 
     * @param   value The constant output value.
     */
    public ConstantEvaluator(
        final OutputType value)
    {
        super();
        
        this.setValue(value);
    }
    
    /**
     * Evaluating this object just returns the constant output value.
     * 
     * @param   input The input (ignored).
     * @return  The constant output value.
     */
    public OutputType evaluate(
        final Object input)
    {
        return this.value;
    }
    
    /**
     * Gets the constant output value for the evaluator.
     * 
     * @return  The constant output value.
     */
    public OutputType getValue()
    {
        return this.value;
    }
    
    /**
     * Sets the constant output value for the evaluator.
     * 
     * @param   value The constant output value.
     */
    public void setValue(
        final OutputType value)
    {
        this.value = value;
    }

    /**
     * Creates a new {@code ConstantEvaluator} for the given value.
     *
     * @param   <OutputType>
     *      The output type for the evaluator
     * @param   value
     *      The constant to output.
     * @return
     *      A new {@code ConstantEvaluator} that always returns the given value.
     */
    public static <OutputType> ConstantEvaluator<OutputType> create(
        final OutputType value)
    {
        return new ConstantEvaluator<OutputType>(value);
    }
}
