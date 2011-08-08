/*
 * File:                Ring.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 15, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Defines something similar to a mathematical ring.  Specifies: equality,
 * element-wise multiplication, addition, subtraction, scaling, negation,
 * deep copy, as well as inline versions of each of these.
 *
 * @param <RingType> Type of Ring that this class can operate upon, usually itself
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments="Looks good."
        ),
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-04-25",
            changesNeeded=false,
            comments="Interface definition looks good. Edited the spacing of some of the elements."
        )
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Ring (mathematics)",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Ring_(mathematics)"
)
public interface Ring<RingType extends Ring<RingType>>
    extends CloneableSerializable
{

    /**
     * Determines if two RingType objects are equal 
     *
     * @param other 
     *          RingType to compare against <code>this</code>
     * @return True if the two objects are equal, false otherwise
     */
    public boolean equals(
        Object other );

    /**
     * Determines if two RingType objects are effectively equal 
     *
     * @param other 
     *          RingType to compare against <code>this</code>
     * @param effectiveZero
     *          tolerance threshold for element-wise equality
     * @return True if the two objects are equal, false otherwise
     */
    public boolean equals(
        RingType other,
        double effectiveZero );

    /**
     * Returns a smart copy of <code>this</code>, such that changing the values
     * of the return class will not effect <code>this</code> 
     *
     * @return smart copy of <code>this</code>
     */
    public RingType clone();

    /**
     * Arithmetic addition of <code>this</code> and <code>other</code>
     *
     * @param other
     *          object to add to <code>this</code>
     * @return sum of <code>this</code> and <code>other</code>
     */
    public RingType plus(
        final RingType other );

    /**
     * Inline arithmetic addition of <code>this</code> and <code>other</code> 
     *
     * @param other
     *      object to add to <code>this</code>
     */
    public void plusEquals(
        final RingType other );

    /**
     * Arithmetic subtraction of <code>other</code> from <code>this</code>
     *
     * @param other
     *          object to subtract from <code>this</code>
     * @return difference of <code>this</code> and <code>other</code>
     */
    public RingType minus(
        final RingType other );

    /**
     * Inline arithmetic subtraction of <code>other</code> from
     * <code>this</code> 
     *
     * @param other
     *      object to subtract from <code>this</code>
     */
    public void minusEquals(
        final RingType other );

    /**
     * Element-wise multiplication of <code>this</code> and <code>other</code>
     *
     * @param other
     *          elements of other will be multiplied to the corresponding
     *          elements of <code>this</code>
     * @return element-wise multiplication of <code>this</code> and
     * <code>other</code>
     */
    public RingType dotTimes(
        final RingType other );

    /**
     * Inline element-wise multiplication of <code>this</code> and
     * <code>other</code> 
     *
     * @param other
     *          elements of other will be multiplied to the corresponding
     *          elements of <code>this</code>
     */
    public void dotTimesEquals(
        final RingType other );

    /**
     * Element-wise scaling of <code>this</code> by <code>scaleFactor</code>
     *
     * @param scaleFactor
     *          amount to scale the elements of <code>this</code>
     * @return scaling of <code>this</code>
     */
    public RingType scale(
        double scaleFactor );

    /**
     * Inline element-wise scaling of <code>this</code> by
     * <code>scaleFactor</code> 
     *
     * @param scaleFactor
     *          amount to scale the elements of <code>this</code>
     */
    public void scaleEquals(
        double scaleFactor );

    /**
     * Returns the element-wise negation of <code>this</code>, such that
     * <code>this.plus( this.negative() )</code> has only zero elements.
     *
     * @return element-wise negation of <code>this</code>
     */
    public RingType negative();

    /**
     * Inline element-wise negation of <code>this</code>
     */
    public void negativeEquals();

    /**
     * Zeros out all elements of <code>this</code>, so that the following are
     * equivalent
     * r1.scaleEquals( 0.0 );
     * and
     * r1.zero();
     * Furthermore, 
     * r1.zero(); anything.dotTimes( r1 ).equals( r1 );
     */
    public void zero();

    /**
     * Determines if this ring is equal to zero.
     *
     * @return
     *      True if all of the elements of this ring are zero.
     */
    public boolean isZero();

    /**
     * Determines if this ring is equal to zero using the element-wise effective
     * zero value.
     *
     * @param effectiveZero
     *          Tolerance threshold for element-wise equality
     * @return
     *      True if all of the elements of this ring are effectively zero.
     */
    public boolean isZero(
        final double effectiveZero);

}
