/*
 * File:                Constants.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.optimized;

/**
 * Package-private constants for optimized matrices and vectors.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
class Constants
{

    /**
     * Sparse matrices and vectors appear to be less effective after passing
     * this threshold
     */
    final static double SPARSE_TO_DENSE_THRESHOLD = 0.25;

}
