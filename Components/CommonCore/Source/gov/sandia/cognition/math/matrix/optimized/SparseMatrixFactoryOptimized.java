
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory for Sparse Matrices.
 * 
 * @author Jeremy D. Wendt
 */
public class SparseMatrixFactoryOptimized
    extends MatrixFactory<SparseMatrix>
{

    /**
     * @see MatrixFactory#copyMatrix(gov.sandia.cognition.math.matrix.Matrix)
     *
     * NOTE: Returned matrix is Yale format if m is Diagonal, Dense, or Sparse
     * in Yale format. Is sparse row if m is Sparse and in sparse row format.
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
     * @see MatrixFactory#createMatrix(int, int)
     *
     * NOTE: Returned matrix is sparse row format.
     */
    @Override
    final public SparseMatrix createMatrix(int numRows,
        int numColumns)
    {
        return new SparseMatrix(numRows, numColumns);
    }

}
