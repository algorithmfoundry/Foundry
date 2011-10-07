/*
 * File:                TwoVectorEntry.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;

/**
 * Interface that specifies the functionality for a class that stores entries
 * for two vectors
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=true,
    comments="Comments marked with triple slash",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-18",
        moreChangesNeeded=false,
        comments="Okie dokie... I removed the inheritance and made the function names more coherent, as requested."
    )
)
public interface TwoVectorEntry
{

    /**
     * Gets the current index into the Vector to which this entry points 
     *
     * @return current zero-based index
     */
    int getIndex();
    
    /**
     * Sets the current index into the Vector to which this entry points 
     *
     * @param index
     *          zero-based index into the Vector
     */
    public void setIndex(
        final int index );
    
    /**
     * Gets the first value to which this entry points
     * 
     * @return value of the first entry
     */
    public double getFirstValue();

    /**
     * Sets the first value to which this entry points
     *
     * @param value
     *          new value of the second entry
     */
    public void setFirstValue(
        final double value );

    /**
     * Gets the second value to which this entry points
     * 
     * @return value of the first entry
     */
    public double getSecondValue();

    /**
     * Sets the second value to which this entry points
     *
     * @param value
     *          new value of the second entry
     */
    public void setSecondValue(
        final double value );
}
