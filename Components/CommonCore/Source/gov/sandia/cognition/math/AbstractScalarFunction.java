/*
 * File:            AbstractScalarFunction.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code ScalarFunction} interface. The
 * {@code evaluate} method calls {@code evaluateAsDouble}.
 *
 * @param   <InputType>
 *      The type of the input to the scalar function.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public abstract class AbstractScalarFunction<InputType>
    extends AbstractCloneableSerializable
    implements ScalarFunction<InputType>
{
    /**
     * Creates a new {@code AbstractScalarFunction}.
     */
    public AbstractScalarFunction()
    {
        super();
    }

    /**
     * Returns the result of calling {@code evaluateAsDouble}.
     *
     * @param   input
     *      The input value.
     * @return
     *      The result evaluated as a double.
     */
    @Override
    public Double evaluate(
        final InputType input)
    {
        return this.evaluateAsDouble(input);
    }

}
