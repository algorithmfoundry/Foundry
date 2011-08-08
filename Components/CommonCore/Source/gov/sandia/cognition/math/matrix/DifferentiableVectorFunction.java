/*
 * File:                DifferentiableVectorFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.DifferentiableEvaluator;

/**
 * A VectorFunction that can is also differentiable
 * 
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments="Added proper file header. Interface looks good."
)
public interface DifferentiableVectorFunction
    extends VectorFunction,
    DifferentiableEvaluator<Vector,Vector,Matrix>
{
    /**
     * Differentiate the VectorFunction at <code>input</code> and return
     * the Jacobian 
     *
     * @param input
     *          Vector input to the VectorFunction, about which to evaluate
     *          the VectorFunction 
     * @return Jacobian at <code>input</code>
     */
    public Matrix differentiate(
        Vector input);
}
