/*
 * File:                DefaultTwoVectorEntry.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * Stores an entry for two vectors. Typically used by iterators that do
 * union and intersection operations
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-26",
    changesNeeded=false,
    comments="Looks good."
)
public class DefaultTwoVectorEntry
    implements TwoVectorEntry
{
    /**
     * First underlying vector.
     */
    private Vector firstVector;

    /**
     * Second underlying vector.
     */
    private Vector secondVector;
    
    /**
     * Current index into the underlying vectors.
     */
    private int index;
    
    /**
     * Creates a new instance of DefaultTwoVectorEntry.
     * 
     * 
     * @param firstVector First underlying vector
     * @param secondVector Second underlying vector
     */
    public DefaultTwoVectorEntry(
        Vector firstVector,
        Vector secondVector)
    {
        this(firstVector, secondVector, 0);
    }
    
    /**
     * Creates a new instance of DefaultTwoVectorEntry.
     * 
     * 
     * @param firstVector First underlying vector.
     * @param secondVector Second underlying vector.
     * @param index Current index into the vectors.
     */
    public DefaultTwoVectorEntry(
        Vector firstVector,
        Vector secondVector,
        int index)
    {
        super();
        
        this.setFirstVector(firstVector);
        this.setSecondVector(secondVector);
        this.setIndex(index);
    }
    
    /**
     * Getter for firstVector.
     *
     * @return First underlying vector
     */
    public Vector getFirstVector()
    {
        return this.firstVector;
    }
    
    /**
     * Setter for firstVector.
     *
     * @param firstVector First underlying vector.
     */
    public void setFirstVector(
        Vector firstVector)
    {
        this.firstVector = firstVector;
    }
    
    /**
     * Getter for secondVector.
     *
     * @return Second underlying vector.
     */
    public Vector getSecondVector()
    {
        return this.secondVector;
    }
    
    /**
     * Setter for secondVector.
     *
     * @param secondVector Second underlying vector.
     */
    public void setSecondVector(
        Vector secondVector)
    {
        this.secondVector = secondVector;
    }
    
    /**
     * Gets the entry value from the first underlying vector.
     *
     * @return Entry value from the first underlying vector.
     */
    public double getFirstValue()
    {
        return this.getFirstVector().getElement(this.getIndex());
    }
    
    /**
     * Sets the entry value to the first underlying vector.
     *
     * @param value Entry value to the first underlying vector.
     */
    public void setFirstValue(
        double value)
    {
        this.getFirstVector().setElement(this.getIndex(), value);
    }
    
    /**
     * Gets the current index into the underlying vectors.
     *
     * @return Current index into the underlying vectors.
     */
    public int getIndex()
    {
        return this.index;
    }
    
    /**
     * Sets the current index into the underlying vectors.
     *
     * @param index Current index into the underlying vectors.
     */
    public void setIndex(
        int index)
    {
        this.index = index;
    }
    
    /**
     * Gets the entry value for the second underlying vector
     *
     * @return Entry value for the second underlying vector
     */
    public double getSecondValue()
    {
        return this.getSecondVector().getElement(this.getIndex());
    }
    
    /**
     * Sets the entry value for the second underlying vector.
     *
     * @param value Entry value for the second underlying vector.
     */
    public void setSecondValue(
        double value)
    {
        this.getSecondVector().setElement(this.getIndex(), value);
    }
}
