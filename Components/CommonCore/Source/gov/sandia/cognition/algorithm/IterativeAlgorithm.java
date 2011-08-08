/*
 * File:                IterativeAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 30, 2006, Sandia Corporation.  Under the terms of Contract
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
 * The {@code IterativeAlgorithm} interface defines the functionality of a
 * algorithm that works through multiple iteration steps in order to perform 
 * its computation. It can add listeners to the algorithm that are notified
 * at the beginning/end of the algorithm and the beginning/end of each step of 
 * the algorithm.
 *
 * @author  Justin Basilico
 * @since   2.0
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
public interface IterativeAlgorithm
{

    /**
     * Gets the current number of iterations executed by this algorithm since
     * its it was started.
     * 
     * @return
     *      Current number of iterations executed by this algorithm.
     */
    int getIteration();

    /**
     * Adds a listener for the iterations of the algorithm.
     *
     * @param   listener
     *      The listener to add.
     */
    void addIterativeAlgorithmListener(
        IterativeAlgorithmListener listener);

    /**
     * Removes a listener for the iterations of the algorithm.
     *
     * @param   listener
     *      The listener to remove.
     */
    void removeIterativeAlgorithmListener(
        IterativeAlgorithmListener listener);

}
