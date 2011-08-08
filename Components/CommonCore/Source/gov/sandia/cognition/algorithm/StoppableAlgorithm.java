/*
 * File:                StoppableAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 25, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;

/**
 * Defines methods for an algorithm that can be stopped early during its
 * execution. This is typically done so that a user can stop the algorithm
 * early through a user interface. Thus, it is recommended that all potentially
 * long-running algorithms implement the interface in order to be
 * user-friendly.
 *
 * Typically a {@code StoppableAlgorithm} will also be a 
 * {@code IterativeAlgorithm}, but this is not enforced.
 *
 * @author  Justin Basilico
 * @author  Kevin Dixon
 * @since   2.0
 * @see     IterativeAlgorithm
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Class looks fine."
        ),        
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-02",
            changesNeeded=false,
            comments="Interface is fine."
        )
    }
)
public interface StoppableAlgorithm
{

    /**
     * Requests that the algorithm stop at the next appropriate point. The
     * stopping point is determined by the algorithm. The algorithm should try 
     * to balance between stopping as soon as possible and leaving the results 
     * in some consistent state.
     */
    void stop();

    /**
     * Indicates whether or not the algorithm results are in a consistent state
     * or not. A {@code StoppableAlgorithm} may not always be able to stop in a 
     * manner that makes its results valid. This method is used to check whether 
     * the results are in a consistent state or not.
     *
     * @return  True if the results of the algorithm are in valid state and 
     *          false if they are not valid.
     */
    boolean isResultValid();

}
