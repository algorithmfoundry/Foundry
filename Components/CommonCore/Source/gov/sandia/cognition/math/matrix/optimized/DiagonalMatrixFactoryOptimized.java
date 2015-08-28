
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory for diagonal matrices.
 * 
 * @author Jeremy D. Wendt
 */
public class DiagonalMatrixFactoryOptimized
    extends MatrixFactory<DiagonalMatrix>
{

    /**
     * @see MatrixFactory#copyMatrix(gov.sandia.cognition.math.matrix.Matrix)
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
     * @see MatrixFactory#createMatrix(int, int)
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
