/*
 * File:            BatchNonLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * A learner that always returns the same value as the result. This is not
 * really a learner, but more of an adapter class when you need to stub in
 * a constant value into the learning framework.
 *
 * @param   <ValueType>
 *      The type of the constant value returned as the result of learning.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class ConstantLearner<ValueType>
    extends AbstractCloneableSerializable
    implements BatchLearner<Object, ValueType>
{
    
    /** The result of learning. */
    protected ValueType value;

    /**
     * Creates a new {@code ConstantLearner} with a null value.
     */
    public ConstantLearner()
    {
        this(null);
    }

    /**
     * Creates a new {@code ConstantLearner} with the given value;
     *
     * @param value
     *      The value that is used as the result of learning.
     */
    public ConstantLearner(
        final ValueType value)
    {
        super();

        this.setValue(value);
    }

    @Override
    public ValueType learn(
        final Object data)
    {
        return this.getValue();
    }

    /**
     * Gets the value that is the result of learning.
     *
     * @return
     *      The value that is the result of learning.
     */
    public ValueType getValue()
    {
        return this.value;
    }

    /**
     * Sets the value that is the result of learning.
     *
     * @param   value
     *      The value that is the result of learning.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    /**
     * Creates a new {@code ConstantLearner}.
     *
     * @param   <ValueType>
     *      The type of value that is the result of learning.
     * @param   value
     *      The value that is the result of learning.
     * @return
     *      A new {@code ConstantLearner} for the given value.
     */
    public static <ValueType> ConstantLearner<ValueType> create(
        final ValueType value)
    {
        return new ConstantLearner<ValueType>(value);
    }

}
