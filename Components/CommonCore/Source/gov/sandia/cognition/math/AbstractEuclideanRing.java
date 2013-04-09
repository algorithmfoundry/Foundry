/*
 * File:            AbstractEuclideanRing.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2013, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

/**
 * An abstract implementation of the {@link EuclideanRing} interface. It
 * defines common methods like times and divide methods so that implementations
 * only need to define the inline versions of them.
 *
 * @param   <RingType>
 *      The type of Euclidean ring that this is defined over.
 * @author  Justin Basilico
 * @version 3.3.3
 */
public abstract class AbstractEuclideanRing<RingType extends EuclideanRing<RingType>>
    extends AbstractRing<RingType>
    implements EuclideanRing<RingType>
{

    /**
     * Creates a new {@link AbstractEuclideanRing}.
     */
    public AbstractEuclideanRing()
    {
        super();
    }

    @Override
    public RingType times(
        final RingType other)
    {
        final RingType result = this.clone();
        result.timesEquals(other);
        return result;
    }

    @Override
    public RingType divide(
        final RingType other)
    {
        final RingType result = this.clone();
        result.divideEquals(other);
        return result;
    }

}
