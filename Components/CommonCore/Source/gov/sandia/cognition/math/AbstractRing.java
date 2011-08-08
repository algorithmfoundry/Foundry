/*
 * File:                AbstractRing.java
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
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Implements the non-inline versions of the various Ring functions. It 
 * implements these by making use of the clone method to get a copy of the
 * object it is operating on and then returning that clone.
 *
 * @param <RingType> Type of Ring that this can operate upon (usually itself)
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Looks fine."
        ),
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-04-25",
            changesNeeded=false,
            comments={
                "Only a few minor modifications were made to the documentation.",
                "Everything checks out."
            }
        )
    }
)
public abstract class AbstractRing<RingType extends Ring<RingType>>
    extends AbstractCloneableSerializable
    implements Ring<RingType>
{

    @Override
    @SuppressWarnings("unchecked")
    public RingType clone()
    {
        return (RingType) super.clone();
    }

    public RingType dotTimes(
        final RingType other )
    {
        RingType copy = this.clone();
        copy.dotTimesEquals( other );
        return copy;
    }

    public RingType minus(
        final RingType other )
    {
        RingType copy = this.clone();
        copy.minusEquals( other );
        return copy;
    }

    public RingType plus(
        final RingType other )
    {
        RingType copy = this.clone();
        copy.plusEquals( other );
        return copy;
    }

    public RingType scale(
        double scaleFactor )
    {
        RingType copy = this.clone();
        copy.scaleEquals( scaleFactor );
        return copy;
    }

    public RingType negative()
    {
        RingType copy = this.clone();
        copy.negativeEquals();
        return copy;
    }

    public void negativeEquals()
    {
        this.scaleEquals( -1.0 );
    }

    public void zero()
    {
        this.scaleEquals( 0.0 );
    }

    public boolean isZero()
    {
        return this.isZero(0.0);
    }
}
