/*
 * File:                CodeReviews.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 6, 2008, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code CodeReviews} annotation defines a container for one or more
 * {@code CodeReview} annotations. This container annotation is needed because 
 * Java allows only one instance of an annotations per element.
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=true,
    comments={
        "Not sure about the Documented or Runtime Retention annotations for the class. Please review.",
        "Otherwise, class looks fine."
    },
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2008-02-18",
        moreChangesNeeded=false,
        comments={
            "Runtime retention means that we could look via reflection to " +
                "see what has been code reviewed.",
            "Documented makes the review show up in the documentation."
        }
    )
)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeReviews
{

    /**
     * One or more {@code CodeReview} annotations.
     * 
     * @return
     *      One or more {@code CodeReview} annotations.
     */
    CodeReview[] reviews();

}
