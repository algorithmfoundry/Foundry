/*
 * File:                OverconstrainedMatrixVectorMultiplier.java
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
 * Implements an overconstrainted matrix-vector multiplication.
 * 
 * @author Jeremy D. Wendt
 * @since 4.0.0
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
public class OverconstrainedMatrixVectorMultiplier
    extends MatrixVectorMultiplier
{

    private OverconstrainedMatrixVectorMultiplier()
    {
        super(null);
        throw new UnsupportedOperationException("Null constructor not supported");
    }

    /**
     * Clones the input matrix to prevent any later edits to the input from
     * changing the results of iterative multiplications.
     *
     * @param m The matrix to multiply with
     */
    OverconstrainedMatrixVectorMultiplier(Matrix m)
    {
        super(m);
        if (m.getNumRows() < m.getNumColumns())
        {
            throw new IllegalArgumentException("Unable to minimize an "
                + "underconstrained problem");
        }
    }

    /**
     * Returns m times input.
     *
     * @param input The vector to multiply by m.
     * @return m times input.
     */
    @Override
    public Vector evaluate(Vector input)
    {
        return transposeMult(super.evaluate(input));
    }

    /**
     * Return A^(T) * input.
     *
     * @param input The vector to multiply by the transpose of A
     * @return A^(T) * input
     */
    public Vector transposeMult(Vector input)
    {
        // NOTE: This computes A^(T)x by the following:
        // return (x^(T)A)^(T)
        // But as we don't have to transpose vectors in this code, it requires
        // no real transposes at all.
        return input.times(m);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof OverconstrainedMatrixVectorMultiplier))
        {
            return false;
        }
        // Other than the type, I don't add anything here
        return super.equals(o);
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        return hash * 17 + super.hashCode();
    }

}
