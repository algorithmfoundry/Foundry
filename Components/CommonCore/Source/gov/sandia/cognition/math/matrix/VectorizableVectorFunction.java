/*
 * File:                VectorizableVectorFunction.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 28, 2006, Sandia Corporation.  Under the terms of Contract
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
 * The VectorizableVectorFunction interface defines a useful interface for
 * doing machine learning, which is a function that takes and returns vectors
 * and also is parameterizable as a vector.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments="Interface looks fine."
)
public interface VectorizableVectorFunction
    extends Vectorizable, VectorFunction
{
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public VectorizableVectorFunction clone();
    
}
