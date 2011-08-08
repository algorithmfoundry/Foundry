/*
 * File:                CodeReviewResponse.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 11, 2008, Sandia Corporation.  Under the terms of Contract
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
 * The {@code CodeReviewResponse} annotation contains information regarding a
 * response to a {@code CodeReview} annotation.
 * 
 * @author  Kevin R. Dixon
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=true,
    comments={
        "Not sure about the Documented or Runtime Retention annotations for the class. Review this please.",
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
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CodeReviewResponse 
{
    
    /**
     * The full name of the respondent of the code review.
     * For example, {@code "Justin Basilico"}.
     * 
     * @return
     *      The full name of the respondent.
     */
    String respondent();

    /**
     * The date of the response. Use {@code "YYYY-mm-dd"} format.
     * For example, {@code "2008-01-14"}.
     * 
     * @return
     *      The date of the code review response.
     */
    String date();

    /**
     * True if more changes are needed; otherwise, false.
     * 
     * @return
     *      True if more changes are needed; otherwise, false.
     */
    boolean moreChangesNeeded();

    /**
     * The comments of the response.
     * 
     * @return
     *      The comments of the response.
     */
    String[] comments();

}
