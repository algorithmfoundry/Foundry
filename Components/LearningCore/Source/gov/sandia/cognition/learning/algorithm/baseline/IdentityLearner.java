/*
 * File:            IdentityLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * A batch learner implementation that just returns its inputs, creating an
 * identity function.
 *
 * @param   <ValueType>
 *      The type of value passed into and out of the learner.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class IdentityLearner<ValueType>
    extends AbstractCloneableSerializable
    implements BatchLearner<ValueType, ValueType>
{

    /**
     * Creates a new {@code IdentityLearner}, which has no parameters.
     */
    public IdentityLearner()
    {
        super();
    }

    @Override
    public IdentityLearner<ValueType> clone()
    {
        @SuppressWarnings("unchecked")
        final IdentityLearner<ValueType> clone = (IdentityLearner<ValueType>) 
            super.clone();
        return clone;
    }
    
    @Override
    public ValueType learn(
        final ValueType data)
    {
        return data;
    }

    /**
     * A convenience method for creating a {@code IdentityLearner}.
     *
     * @param   <ValueType>
     *      The type of value passed into and out of the learner.
     * @return
     *      A new learner.
     */
    public static <ValueType> IdentityLearner<ValueType> create()
    {
        return new IdentityLearner<ValueType>();
    }

}
