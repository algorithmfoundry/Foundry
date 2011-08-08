/*
 * File:                AbstractValueDiscriminantPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 02, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code ValueDiscriminantPair} interface.
 * Takes care of the mapping of the {@code Pair} interface methods.
 *
 * @param   <ValueType>
 *      The general value stored in the pair.
 * @param   <DiscriminantType>
 *      The discriminant comparable object used for ordering objects that have
 *      equal value.
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractValueDiscriminantPair<ValueType, DiscriminantType extends Comparable<? super DiscriminantType>>
    extends AbstractCloneableSerializable
    implements ValueDiscriminantPair<ValueType, DiscriminantType>
{

    /**
     * Creates a new {@code AbstractValueDiscriminantPair}.
     */
    public AbstractValueDiscriminantPair()
    {
        super();
    }

    @Override
    public ValueType getFirst()
    {
        return this.getValue();
    }

    @Override
    public DiscriminantType getSecond()
    {
        return this.getDiscriminant();
    }
    
}
