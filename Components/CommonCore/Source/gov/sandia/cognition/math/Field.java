/*
 * File:            Field.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2013, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * Defines something similar to a mathematical field. It extends a ring and
 * allows division to be defined for all non-zero values.
 *
 * @param   <FieldType>
 *      The type of field.
 * @author  Justin Basilico
 * @version 3.3.3
 */
@PublicationReference(
    author="Wikipedia",
    title="Field (mathematics)",
    type=PublicationType.WebPage,
    year=2013,
    url="http://en.wikipedia.org/wiki/Field_(mathematics))"
)
public interface Field<FieldType extends Field<FieldType>>
    extends EuclideanRing<FieldType>
{
    
    /**
     * Returns the inverse of {@code this}.
     *
     * @return
     *      The inverse of this field.
     */
    public FieldType inverse();

    /**
     * Changes this value to be its inverse.
     */
    public void inverseEquals();
    
}
