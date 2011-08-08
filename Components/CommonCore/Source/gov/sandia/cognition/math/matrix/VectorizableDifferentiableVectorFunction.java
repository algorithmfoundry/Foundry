/*
 * File:                VectorizableDifferentiableVectorFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 26, 2006, Sandia Corporation.  Under the terms of Contract
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
 * A VectorizableVectorFunction that also define a derivative
 * (this is needed for GradientDescendable).  I would recommend using
 * MultidimensionalLineMinimizationLearning instead of gradients, though
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments="Looks good."
)
public interface VectorizableDifferentiableVectorFunction
    extends VectorizableVectorFunction, DifferentiableVectorFunction
{  
}
