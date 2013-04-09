/*
 * File:            EuclideanRing.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2013, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * Defines something similar to a Euclidean ring from abstract algebra. It is 
 * also known as a Euclidean Domain. Extends a ring and adds division.
 *
 * @author  Justin Basilico
 * @version 3.3.3
 */
@PublicationReference(
    author="Wikipedia",
    title="Euclidean Domain",
    type=PublicationType.WebPage,
    year=2013,
    url="http://en.wikipedia.org/wiki/Euclidean_domain"
)
public interface EuclideanRing<RingType extends EuclideanRing<RingType>>
    extends Ring<RingType>
{

    /**
     * Multiplies {@code this} value by the {@code other} value, returning the
     * result of the multiplication as a new value.
     *
     * @param   other
     *      The other value.
     * @return
     *      The result of the multiplication.
     */
    public RingType times(
        final RingType other);

    /**
     * Inline multiplies {@code this} value by the {@code other} value,
     * storing the result inside {@code this}.
     *
     * @param   other
     *      The other value.
     */
    public void timesEquals(
        final RingType other);

    /**
     * Divides {@code this} value by the {@code other} value, returning the
     * result of the division as a new value.
     *
     * @param   other
     *      The other value.
     * @return
     *      The result of the division.
     */
    public RingType divide(
        final RingType other);

    /**
     * Inline divises {@code this} value by the {@code other} value, storing
     * the result inside {@code this}.
     *
     * @param   other
     *      The other value.
     */
    public void divideEquals(
        final RingType other);

}
