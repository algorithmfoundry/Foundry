/*
 * File:                GradientDescendable.java
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

package gov.sandia.cognition.learning.algorithm.gradient;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;

/**
 * The GradientDescendable interface defines the functionality of an object
 * that is required in order to apply the gradient descent algorithm to it.
 * That is, GradientDescendable can differentiate its output with respect to
 * its parameters for a given input.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=false,
            comments={
                "Minor change to class javadoc.",
                "Moved previous code review as CodeReview annotation",
                "Looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-04",
            changesNeeded=false,
            comments="Interface looks fine."
        )
    }
)
public interface GradientDescendable
    extends VectorizableVectorFunction,
    ParameterGradientEvaluator<Vector,Matrix>
{
    
    /**
     * Computes the derivative of the function about the input with respect
     * to the parameters of the function
     * @param input Point about which to differentiate w.r.t. the parameters
     * @return Matrix of parameter gradients
     */
    public Matrix computeParameterGradient( Vector input );
    
    public GradientDescendable clone();
    
}
