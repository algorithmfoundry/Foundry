/*
 * File:                TwoMatrixEntry.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 12, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 * Review Fixer: Kevin "Dude" Dixon
 * Review Fix Date: May 18, 2006
 * Review Fix Comments: Okie dokie... I removed the inheritance and made the
 * function names more coherent, as requested.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;

/**
 * Interface that specifies the functionality for a class that stores entries
 * for two matrices
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=true,
    comments="Why are there only getters and setters for second value, but not first value? Other than that looks fine.",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-18",
        moreChangesNeeded=false,
        comments="Okie dokie... I removed the inheritance and made the function names more coherent, as requested."
    )
)
public interface TwoMatrixEntry
{
    /**
     * Gets the column index to which the entry points
     * 
     * @return zero-based column index
     */
    public int getColumnIndex();    


    /**
     * Sets the column index to which the entry points  
     *
     * @param columnIndex
     *          new zero-based column index for the entry
     */
    public void setColumnIndex(
        int columnIndex);
    
    
    /**
     * Gets the current row index to which this entry points 
     *
     * @return current zero-based index
     */
    int getRowIndex();
    
    /**
     * Sets the current row index to which this entry points 
     *
     * @param rowIndex
     *          zero-based row index 
     */
    public void setRowIndex(
        int rowIndex );
    
    
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
     *          new value of the first entry
     */
    public void setFirstValue(
        double value );    
    
    
    /**
     * Gets the second value to which this entry points
     * 
     * @return value of the second entry
     */
    public double getSecondValue();

    /**
     * Sets the second value to which this entry points
     *
     * @param value
     *          new value of the second entry
     */
    public void setSecondValue(
        double value );
}
