/*
 * File:                OperationNotConvergedException.java
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

/**
 * The <code>OperationNotConvergedException</code> class is an exception that
 * is thrown when some mathematical operation does not converge, when it is
 * expected to converge.
 *
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
            comments="Updated missing documentation."
        )
    }
)
public class OperationNotConvergedException
    extends RuntimeException
{
    /**
     * Creates a new instance of OperationNotConvergedException
     *
     * @param message text of error message
     */
    public OperationNotConvergedException(
        String message)
    {
        super(message);
    }
}
