/*
 * File:                VectorFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 12, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 * Post Review Changes By: Justin Basilico
 * Changes Date: May 16, 2006
 * Change Comments: While this interface defines no "functionality" in terms of
 * adding methods, it does define a certain type of object, which is the
 * purpose of an interface. Here it is defining a specific type of function
 * that maps from and to Vectors. This type of function is used extensively in
 * the learning packages, which requires the need for the additional interface.
 * It also makes it easy for us to add functions to this type later on if it is
 * required.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.evaluator.Evaluator;

/**
 * A vector function is a type of Evaluator that takes a Vector for its input
 * and output.
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments="Again, interfaces that add no functionality to the underlying interface kindof bug me...",
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2006-05-16",
        moreChangesNeeded=false,
        comments={
            "While this interface defines no \"functionality\" in terms of adding methods, it does define a certain type of object, which is the purpose of an interface.",
            "Here it is defining a specific type of function that maps from and to Vectors.",
            "This type of function is used extensively in the learning packages, which requires the need for the additional interface.",
            "It also makes it easy for us to add functions to this type later on if it is required."
        }
    )
)
public interface VectorFunction
    extends Evaluator<Vector, Vector>
{
}
