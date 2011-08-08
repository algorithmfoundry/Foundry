/*
 * File:                MatrixEntry.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;

/**
 * Interface that specifies the functionality for a matrix entry
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments="Looks fine. Why doesn't Matrix have additional methods that allow you to access it using MatrixEntry? Seems like a logical addition.",
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2006-05-16",
        moreChangesNeeded=false,
        comments="Can now access a Matrix using MatrixEntry"
    )
)
public interface MatrixEntry
{

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
     * Gets the value to which this entry points 
     *
     * @return value of the entry
     */
    public double getValue();
    
    /**
     * Sets the value to which this entry points 
     *
     * @param value
     *          new value at the current indices
     */
    public void setValue(
        double value );

    
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

}
