/*
 * File:                DimensionalityMismatchException.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * Gets thrown when the dimensions don't agree for a matrix/vector operation
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments="Looks fine."
)
public class DimensionalityMismatchException 
    extends java.lang.RuntimeException
{
    /**
     * Creates a new instance of <code>DimensionalityMismatchException</code> 
     * without detail message.
     */
    public DimensionalityMismatchException()
    {
        super();
    }
    
    /**
     * Constructs an instance of <code>DimensionalityMismatchException</code> 
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DimensionalityMismatchException(
        String msg)
    {
        super(msg);
    }
    
    /**
     * Constructs and instance of <code>DimensionalityMismatchException</code>
     * with the two mismatching dimensions. 
     *
     * @param first The first dimensionality
     * @param second The second dimensionality
     */
    public DimensionalityMismatchException(
        int first,
        int second)
    {
        this("The dimensionalities " + first + " and " + second + " do not "
            + "match.");
    }
}
