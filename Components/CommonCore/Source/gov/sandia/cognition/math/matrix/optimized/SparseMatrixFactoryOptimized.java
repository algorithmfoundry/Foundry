/*
 * File:                SparseMatrixFactoryOptimized.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory for Sparse Matrices.
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class SparseMatrixFactoryOptimized
    extends MatrixFactory<SparseMatrix>
{
    
    /** An instance of this class. */
    public static SparseMatrixFactoryOptimized INSTANCE = new SparseMatrixFactoryOptimized();

    /**
     * {@inheritDoc}
     *
     * NOTE: Returned matrix is Yale format if m is Diagonal, Dense, or Sparse
     * in Yale format. Is sparse row if m is Sparse and in sparse row format.
     * 
     * @return {@inheritDoc}
     */
    @Override
    final public SparseMatrix copyMatrix(Matrix m)
    {
        if (m instanceof SparseMatrix)
        {
            return new SparseMatrix((SparseMatrix) m);
        }
        else if (m instanceof DenseMatrix)
        {
            return new SparseMatrix((DenseMatrix) m);
        }
        else if (m instanceof DiagonalMatrix)
        {
            return new SparseMatrix((DiagonalMatrix) m);
        }

        // I have to handle other matrix types
        SparseMatrix ret = new SparseMatrix(m.getNumRows(), m.getNumColumns());
        ret.convertFromVector(m.convertToVector());
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Returned matrix is sparse row format.
     * 
     * @return {@inheritDoc}
     */
    @Override
    final public SparseMatrix createMatrix(int numRows,
        int numColumns)
    {
        return new SparseMatrix(numRows, numColumns);
    }

}
