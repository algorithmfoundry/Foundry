/*
 * File:                MatrixVectorMultiplierDiagonalPreconditioner.java
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
import gov.sandia.cognition.math.matrix.custom.DiagonalMatrix;

/**
 * Implements a diagonal preconditioner for a matrix-vector multiplier.
 * 
 * @author Jeremy D. Wendt
 * @since 4.0.0
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
public class MatrixVectorMultiplierDiagonalPreconditioner
    extends MatrixVectorMultiplierWithPreconditioner
{

    DiagonalMatrix Minv;

    private MatrixVectorMultiplierDiagonalPreconditioner()
    {
        super(null);
        throw new UnsupportedOperationException("Can't call the null "
            + "constructor!");
    }

    /**
     * Creates a new {@link MatrixVectorMultiplierDiagonalPreconditioner} for
     * the given matrix.
     * 
     * @param m 
     *      The matrix.
     */
    public MatrixVectorMultiplierDiagonalPreconditioner(Matrix m)
    {
        super(m);
        if (!m.isSquare())
        {
            throw new IllegalArgumentException("This preconditioner only works "
                + "on square matrices");
        }
        int n = m.getNumRows();
        Minv = new DiagonalMatrix(n);
        for (int i = 0; i < n; ++i)
        {
            double ij = m.getElement(i, i);
            if (ij == 0)
            {
                throw new IllegalArgumentException("Diagonal preconditioner "
                    + "only serves for matrices with non-zero diagonal elements");
            }
            Minv.setElement(i, i, 1.0 / ij);
        }
    }

    @Override
    Vector precondition(Vector v)
    {
        return Minv.times(v);
    }

}
