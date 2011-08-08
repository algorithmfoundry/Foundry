/*
 * File:                CodeReview.java
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
 * The {@code CodeReview} annotation describes information about the last code
 * review for a piece of code.
 * 
 * @author  Kevin R. Dixon
 * @author  Justin D. Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=true,
    comments={
        "Not sure about the Documented or Runtime Retention annotations for the class.",
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
public @interface CodeReview 
{
    
    /**
     * The full name of the reviewer of the code.
     * For example, {@code "Justin Basilico"}.
     * 
     * @return
     *      The full name of the reviewer.
     */
    String[] reviewer();
    
    /**
     * The date of the review. Use {@code "YYYY-mm-dd"} format.
     * For example, {@code "2008-01-14"}.
     * 
     * @return
     *      The date of the code review.
     */
    String date();
    
    /**
     * True if the review determined changes are needed in the code. Otherwise,
     * false.
     * 
     * @return
     *      True if the review determined are needed in the code.
     */
    boolean changesNeeded();
    
    /**
     * Comments from the code review. If {@code changesNeeded} is specified,
     * there should be at least one comment.
     * 
     * @return
     *      Comments from the code review.
     */
    String[] comments() default {};
    
    /**
     * Response(s) to a code review.
     * 
     * @return
     *      The response to the code review.
     */
    CodeReviewResponse[] response() default {};
    
}
