/*
 * File:            IdentityEvaluator.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An identity function that returns its input as its output. It is a basic
 * function of f(x) = x, which is defined for any generic type.
 *
 * @param   <DataType>
 *      The data type of the input and output of the evaluator.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class IdentityEvaluator<DataType>
    extends AbstractCloneableSerializable
    implements ReversibleEvaluator<DataType, DataType, IdentityEvaluator<DataType>>
{

    /**
     * Creates a new {@code IdentityEvaluator}, which has no parameters.
     */
    public IdentityEvaluator()
    {
        super();
    }

    @Override
    public IdentityEvaluator<DataType> clone()
    {
        @SuppressWarnings("unchecked")
        final IdentityEvaluator<DataType> clone = (IdentityEvaluator<DataType>)
            super.clone();
        return clone;
    }

    /**
     * Returns the given input.
     *
     * @param   input
     *      The input value.
     * @return
     *      The input value.
     */
    @Override
    public DataType evaluate(
        final DataType input)
    {
        return input;
    }

    @Override
    public IdentityEvaluator<DataType> reverse()
    {
        return this;
    }

    /**
     * Convenience method for creating an identity evaluator.
     *
     * @param   <DataType>
     *      The type of the input and output of the evaluator.
     * @return
     *      A new evaluator.
     */
    public static <DataType> IdentityEvaluator<DataType> create()
    {
        return new IdentityEvaluator<DataType>();
    }

}
