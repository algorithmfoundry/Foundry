/*
 * File:                DiagonalMatrixFactoryOptimized.java
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

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory for diagonal matrices.
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class DiagonalMatrixFactoryOptimized
    extends MatrixFactory<DiagonalMatrix>
{

    /** An instance of this class. */
    public static DiagonalMatrixFactoryOptimized INSTANCE = new DiagonalMatrixFactoryOptimized();
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the input matrix isn't square
     * (because diagonal matrices must be) or if the input matrix has non-zero
     * entries on the diagonal.
     */
    @Override
    final public DiagonalMatrix copyMatrix(Matrix m)
    {
        return new DiagonalMatrix(m);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the input dimensions are not square
     * (because diagonal matrices must be)
     */
    @Override
    final public DiagonalMatrix createMatrix(int numRows,
        int numColumns)
    {
        if (numRows != numColumns)
        {
            throw new IllegalArgumentException("Diagonal matrices must be "
                + "sqaure. Non-square (" + numRows + " x " + numColumns
                + ") diagonal matrix requested.");
        }

        return new DiagonalMatrix(numRows);
    }

}
