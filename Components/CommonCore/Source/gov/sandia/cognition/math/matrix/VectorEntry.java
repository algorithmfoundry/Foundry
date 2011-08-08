/*
 * File:                VectorEntry.java
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
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Interface the specifies the functionality that a VectorEntry should have
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments={
        "Why doesn't the Vector interface have methods that allow you to use VectorEntry directly?",
        "Interface looks fine."
    }
)
public interface VectorEntry
    extends CloneableSerializable
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
        int index );
    
    /**
     * Gets the value to which this entry points 
     *
     * @return value of the Vector entry
     */
    public double getValue();
    
    /**
     * Sets the value to which this entry points 
     *
     * @param value
     *          new value for the Vector at the current index
     */
    public void setValue(
        double value );
    
}
