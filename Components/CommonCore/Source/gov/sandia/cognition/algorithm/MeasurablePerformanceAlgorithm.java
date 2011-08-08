/*
 * File:                MeasurablePerformanceAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright August 21, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.NamedValue;

/**
 * An interface for an algorithm that has a measurable quantity of performance
 * that can be retrieved. Typically, this is used after each iteration, in
 * conjunction with the {@code IterativeAlgorithm} interface, but it can also
 * be used on its own to determine the performance of the algorithm after it
 * has completed.
 * 
 * @author  Justin Basilico
 * @since   2.1
 * @see     IterativeAlgorithm
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "I do wonder if we should return a Collection of NamedValues here.",
        "Otherwise, looks good."
    }
)
public interface MeasurablePerformanceAlgorithm
{

    /**
     * Gets the name-value pair that describes the current performance of the
     * algorithm. For most algorithms, this is the value that they are 
     * attempting to optimize.
     * 
     * @return
     *      The name-value pair that describes the current performance
     *      of the algorithm.
     */
    public NamedValue<? extends Number> getPerformance();

}
