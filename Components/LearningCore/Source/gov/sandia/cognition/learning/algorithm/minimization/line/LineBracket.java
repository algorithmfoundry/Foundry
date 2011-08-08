/*
 * File:                LineBracket.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 13, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Class that defines a bracket for a scalar function.  This consists of a
 * lower bound, upper bound, and an optional third point.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2009-07-06",
            changesNeeded=false,
            comments={
                "Made clone() call super.clone().",
                "Fixed the brittleness in the copy constructor.",
                "Looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-05",
            changesNeeded=false,
            comments="Class looks fine. Compared against the code it is based on from Numerical Recipies in C and it looks fine."
        )
    }
)
public class LineBracket
    extends AbstractCloneableSerializable
{

    /**
     * Lower bound of the bracket.
     */
    private InputOutputSlopeTriplet lowerBound;
    
    /**
     * Upper bound of the bracket.
     */
    private InputOutputSlopeTriplet upperBound;
    
    /**
     * Another (optional) point associated with the bracket.
     */
    private InputOutputSlopeTriplet otherPoint;
    
    /** 
     * Creates a new instance of LineBracket 
     */
    public LineBracket()
    {
        this( null, null, null );
    }

    /**
     * Creates a new instance of LineBracket 
     * 
     * @param lowerBound
     * Lower bound of the bracket.
     * @param upperBound
     * Upper bound of the bracket.
     * @param otherPoint
     * Another (optional) point associated with the bracket.
     */
    public LineBracket(
        InputOutputSlopeTriplet lowerBound,
        InputOutputSlopeTriplet upperBound,
        InputOutputSlopeTriplet otherPoint )
    {
        this.setLowerBound( lowerBound );
        this.setUpperBound( upperBound );
        this.setOtherPoint( otherPoint );
    }        
    
    /**
     * Copy Constructor
     * @param other LineBracket to copy
     */
    public LineBracket(
        LineBracket other )
    {
        this( ObjectUtil.cloneSafe(other.getLowerBound()),
            ObjectUtil.cloneSafe(other.getUpperBound()),
            ObjectUtil.cloneSafe(other.getOtherPoint()) );
    }
    
    @Override
    public LineBracket clone()
    {
        LineBracket clone = (LineBracket) super.clone();
        clone.setLowerBound( ObjectUtil.cloneSafe( this.getLowerBound() ) );
        clone.setUpperBound( ObjectUtil.cloneSafe( this.getUpperBound() ) );
        clone.setOtherPoint( ObjectUtil.cloneSafe( this.getOtherPoint() ) );
        return clone;
    }

    @Override
    public String toString()
    {
        return "Lower: " + this.getLowerBound() + 
            " Upper: " + this.getUpperBound() +
            " Other: " + this.getOtherPoint();
    }
    
    /**
     * Getter for lowerBound
     * @return
     * Lower bound of the bracket.
     */
    public InputOutputSlopeTriplet getLowerBound()
    {
        return this.lowerBound;
    }
    
    /**
     * Setter for lowerBound
     * @param lowerBound
     * Lower bound of the bracket.
     */
    public void setLowerBound(
        InputOutputSlopeTriplet lowerBound )
    {
        this.lowerBound = lowerBound;
    }

    /**
     * Getter for upperBound
     * @return
     * Upper bound of the bracket.
     */
    public InputOutputSlopeTriplet getUpperBound()
    {
        return this.upperBound;
    }

    /**
     * Setter for upperBound
     * @param upperBound
     * Upper bound of the bracket.
     */
    public void setUpperBound(
        InputOutputSlopeTriplet upperBound )
    {
        this.upperBound = upperBound;
    }

    /**
     * Getter for otherPoint
     * @return
     * Another (optional) point associated with the bracket.
     */
    public InputOutputSlopeTriplet getOtherPoint()
    {
        return this.otherPoint;
    }

    /**
     * Setter for otherPoint
     * @param otherPoint
     * Another (optional) point associated with the bracket.
     */
    public void setOtherPoint(
        InputOutputSlopeTriplet otherPoint )
    {
        this.otherPoint = otherPoint;
    }

}
