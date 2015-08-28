
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory that creates DenseMatrix instances. NOTE: There are other DenseMatrix
 * constructors that are available to the outside caller.
 * 
 * @author Jeremy D. Wendt
 */
public class DenseMatrixFactoryOptimized
    extends MatrixFactory<DenseMatrix>
{

    /**
     * Creates a deep copy of m into a DenseMatrix and returns it
     *
     * @param m The matrix to copy
     * @return The DenseMatrix deep copy of m.
     */
    @Override
    final public DenseMatrix copyMatrix(Matrix m)
    {
        return new DenseMatrix(m);
    }

    /**
     * Creates a new all-zero DenseMatrix of the specified dimensions
     *
     * @param numRows The number of rows desired in the result
     * @param numColumns The number of columns desired in the result
     * @return a new all-zero DenseMatrix of the specified dimensions
     */
    @Override
    final public DenseMatrix createMatrix(int numRows,
        int numColumns)
    {
        return new DenseMatrix(numRows, numColumns);
    }

}
