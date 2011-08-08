/*
 * File:                State.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.CodeReview;
import java.io.Serializable;

/**
 * An object that is both cloneable and serializable, because Java's
 * Cloneable interface mistakenly doesn't have a clone() method
 * (search on the Web, it's funny "lost in the mists of time..." )
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-02",
            changesNeeded=false,
            comments="Looks fine."
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2007-11-25",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public interface CloneableSerializable
    extends Cloneable, Serializable
{

    /**
     * Creates a new clone (shallow copy) of this object.
     *
     * @return A new clone (shallow copy) of this object.
     */
    public CloneableSerializable clone();

}
