/*
 * File:                Vectorizable.java
 * Authors:             Justin Basilico and Kevin R. Dixon
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
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The Vectorizable interface is an interface for an object that can be 
 * converted to and from a Vector.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments="Looks fine."
)
public interface Vectorizable
    extends CloneableSerializable
{

    public Vectorizable clone();    
    
    /**
     * Converts the object to a vector.
     *
     * @return The Vector form of the object.
     */
    public Vector convertToVector();
    
    /**
     * Converts the object from a Vector of parameters.
     *
     * @param parameters The parameters to incorporate.
     */
    public void convertFromVector(
        Vector parameters);
    
}
