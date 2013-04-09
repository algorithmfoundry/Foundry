/*
 * File:            AbstractField.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2013, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

/**
 * An abstract implementation of the {@link Field} interface. It implements
 * some of the basic functionality.
 *
 * @param   <FieldType>
 *      The type of field.
 * @author  Justin Basilico
 * @version 3.3.3
 */
public abstract class AbstractField<FieldType extends Field<FieldType>>
    extends AbstractEuclideanRing<FieldType>
    implements Field<FieldType>
{

    /**
     * Creates a new {@link AbstractField}.
     */
    public AbstractField()
    {
        super();
    }

    @Override
    public FieldType inverse()
    {
        final FieldType result = this.clone();
        result.inverseEquals();
        return result;
    }

}
