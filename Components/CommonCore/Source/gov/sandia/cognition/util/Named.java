/*
 * File:                Named.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 11, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.CodeReview;

/**
 * The <code>Named</code> interface defines an Object that has a useful name 
 * attached to it, which is common for many types of Objects.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-02",
            changesNeeded=false,
            comments={
                "Moved previous code review to CodeReview annotation.",
                "Otherwise, still looks fine."
            }
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
public interface Named
{

    /**
     * Gets the name of the Object that it is called on.
     *
     * @return The name of the Object.
     */
    String getName();

}
