/*
 * File:                MatrixVectorMultiplierWithPreconditioner.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * As various preconditioner operations could be created, this class defines the
 * interface that must be followed by them.
 * 
 * @author Jeremy D. Wendt
 * @since 4.0.0
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
abstract public class MatrixVectorMultiplierWithPreconditioner
    extends MatrixVectorMultiplier
{

    /**
     * Never call this.
     *
     * @throws UnsupportedOperationException Because it's not supported.
     */
    private MatrixVectorMultiplierWithPreconditioner()
    {
        super(null);
        throw new UnsupportedOperationException("Can't call the null "
            + "constructor!");
    }

    /**
     * Clones the input matrix to prevent any later edits to the input from
     * changing the results of iterative multiplications.
     *
     * @param m The matrix to multiply with
     */
    MatrixVectorMultiplierWithPreconditioner(Matrix m)
    {
        super(m);
    }

    /**
     * Preconditions the residual vector (applies M^(-1))
     *
     * @param v The vector to precondition
     * @return The vector having been preconditioned
     */
    abstract Vector precondition(Vector v);

}
