
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.AbstractMatrix;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.Vector;
import java.text.NumberFormat;
import java.util.Iterator;
import javax.xml.bind.TypeConstraintException;

/**
 * This package-private class implements the basic math methods, ensures size
 * constraints are met for the operation, and then calls the
 * matrix-type-specific (diagonal, dense, sparse) version of the basic matrix
 * method.
 *
 * @author Jeremy D. Wendt
 */
abstract class BaseMatrix
    extends AbstractMatrix
{

    /**
     * @see AbstractMatrix#plus(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix plus(Matrix m)
    {
        if ((m.getNumRows() != getNumRows()) || (m.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + m.getNumRows() + ", " + m.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        Matrix ret;
        if (this instanceof DenseMatrix)
        {
            ret = this.clone();
            ret.plusEquals(m);
        }
        else if (m instanceof DenseMatrix)
        {
            ret = m.clone();
            ret.plusEquals(this);
        }
        else if (this instanceof SparseMatrix)
        {
            ret = this.clone();
            ret.plusEquals(m);
        }
        else if (m instanceof SparseMatrix)
        {
            ret = m.clone();
            ret.plusEquals(this);
        }
        else if (this instanceof DiagonalMatrix && m instanceof DiagonalMatrix)
        {
            ret = this.clone();
            ret.plusEquals(m);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + m.getClass() + " not supported");
        }
        return ret;
    }

    /**
     * @see AbstractMatrix#minus(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix minus(Matrix m)
    {
        if ((m.getNumRows() != getNumRows()) || (m.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + m.getNumRows() + ", " + m.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        Matrix ret;
        if (this instanceof DenseMatrix)
        {
            ret = this.clone();
            ret.minusEquals(m);
        }
        else if (m instanceof DenseMatrix)
        {
            ret = m.negative();
            ret.plusEquals(this);
        }
        else if (this instanceof SparseMatrix)
        {
            ret = this.clone();
            ret.minusEquals(m);
        }
        else if (m instanceof SparseMatrix)
        {
            ret = m.negative();
            ret.plusEquals(this);
        }
        else if (this instanceof DiagonalMatrix && m instanceof DiagonalMatrix)
        {
            ret = this.clone();
            ret.minusEquals(m);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + m.getClass() + " not supported");
        }
        return ret;
    }

    /**
     * @see AbstractMatrix#dotTimes(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix dotTimes(Matrix m)
    {
        if ((m.getNumRows() != getNumRows()) || (m.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + m.getNumRows() + ", " + m.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        Matrix ret;
        if (this instanceof DiagonalMatrix)
        {
            ret = this.clone();
            ret.dotTimesEquals(m);
        }
        else if (m instanceof DiagonalMatrix)
        {
            ret = m.clone();
            ret.dotTimesEquals(this);
        }
        else if (this instanceof SparseMatrix)
        {
            ret = this.clone();
            ret.dotTimesEquals(m);
        }
        else if (m instanceof SparseMatrix)
        {
            ret = m.clone();
            ret.dotTimesEquals(this);
        }
        else if (this instanceof DenseMatrix && m instanceof DenseMatrix)
        {
            ret = this.clone();
            ret.dotTimesEquals(m);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + m.getClass() + " not supported");
        }
        return ret;
    }

    /**
     * @see AbstractMatrix#plusEquals(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void plusEquals(Matrix other)
    {
        if ((other.getNumRows() != getNumRows()) || (other.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + other.getNumRows() + ", " + other.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        if (other instanceof DiagonalMatrix)
        {
            _plusEquals((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            _plusEquals((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            _plusEquals((SparseMatrix) other);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + other.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input sparse matrix.
     *
     * @param other A sparse matrix to add to this
     */
    abstract void _plusEquals(SparseMatrix other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input dense matrix.
     *
     * @param other A dense matrix to add to this
     */
    abstract void _plusEquals(DenseMatrix other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input diagonal matrix.
     *
     * @param other A diagonal matrix to add to this
     */
    abstract void _plusEquals(DiagonalMatrix other);

    /**
     * @see Matrix#scaledPlusEquals(double, gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void scaledPlusEquals(double scaleFactor,
        Matrix other)
    {
        if ((other.getNumRows() != getNumRows()) || (other.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + other.getNumRows() + ", " + other.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        if (other instanceof DiagonalMatrix)
        {
            _scaledPlusEquals((DiagonalMatrix) other, scaleFactor);
        }
        else if (other instanceof DenseMatrix)
        {
            _scaledPlusEquals((DenseMatrix) other, scaleFactor);
        }
        else if (other instanceof SparseMatrix)
        {
            _scaledPlusEquals((SparseMatrix) other, scaleFactor);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + other.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input sparse matrix.
     *
     * @param other A sparse matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    abstract void _scaledPlusEquals(SparseMatrix other,
        double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input dense matrix.
     *
     * @param other A dense matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    abstract void _scaledPlusEquals(DenseMatrix other,
        double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input diagonal matrix.
     *
     * @param other A diagonal matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    abstract void _scaledPlusEquals(DiagonalMatrix other,
        double scaleFactor);

    /**
     * @see AbstractMatrix#minusEquals(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void minusEquals(Matrix other)
    {
        if ((other.getNumRows() != getNumRows()) || (other.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + other.getNumRows() + ", " + other.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        if (other instanceof DiagonalMatrix)
        {
            _minusEquals((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            _minusEquals((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            _minusEquals((SparseMatrix) other);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + other.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input sparse matrix.
     *
     * @param other A sparse matrix to subtract from this
     */
    abstract void _minusEquals(SparseMatrix other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input dense matrix.
     *
     * @param other A dense matrix to subtract from this
     */
    abstract void _minusEquals(DenseMatrix other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input diagonal matrix.
     *
     * @param other A diagonal matrix to subtract from this
     */
    abstract void _minusEquals(DiagonalMatrix other);

    /**
     * @see AbstractMatrix#dotTimesEquals(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void dotTimesEquals(Matrix other)
    {
        if ((other.getNumRows() != getNumRows()) || (other.getNumColumns()
            != getNumColumns()))
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + other.getNumRows() + ", " + other.getNumColumns()
                + ") doesn't match self (" + getNumRows() + ", "
                + getNumColumns() + ")");
        }

        if (other instanceof DiagonalMatrix)
        {
            _dotTimesEquals((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            _dotTimesEquals((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            _dotTimesEquals((SparseMatrix) other);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + other.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input sparse matrix.
     *
     * @param other A sparse matrix to dot with this
     */
    abstract void _dotTimesEquals(SparseMatrix other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input dense matrix.
     *
     * @param other A dense matrix to dot with this
     */
    abstract void _dotTimesEquals(DenseMatrix other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input diagonal matrix.
     *
     * @param other A diagonal matrix to dot with this
     */
    abstract void _dotTimesEquals(DiagonalMatrix other);

    /**
     * @see AbstractMatrix#times(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input matrix's numRows doesn't
     * match this's numColumns.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix times(Matrix matrix)
    {
        if (matrix.getNumRows() != getNumColumns())
        {
            throw new IllegalArgumentException("Input matrix's size ("
                + matrix.getNumRows() + ", " + matrix.getNumColumns()
                + ") doesn't match self for multiplication (" + getNumRows()
                + ", " + getNumColumns() + ")");
        }
        if (matrix instanceof DiagonalMatrix)
        {
            return _times((DiagonalMatrix) matrix);
        }
        else if (matrix instanceof DenseMatrix)
        {
            return _times((DenseMatrix) matrix);
        }
        else if (matrix instanceof SparseMatrix)
        {
            return _times((SparseMatrix) matrix);
        }
        else
        {
            throw new TypeConstraintException("Input Matrix class "
                + matrix.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input sparse matrix.
     *
     * @param other A sparse matrix to multiply with this
     */
    abstract Matrix _times(SparseMatrix other);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input dense matrix.
     *
     * @param other A dense matrix to multiply with this
     */
    abstract Matrix _times(DenseMatrix other);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input diagonal matrix.
     *
     * @param other A diagonal matrix to multiply with this
     */
    abstract Matrix _times(DiagonalMatrix other);

    /**
     * @see AbstractMatrix#times(gov.sandia.cognition.math.Vector)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws IllegalArgumentException if the input vectors's dimensions
     * doesn't match this's numCols.
     * @throws TypeConstraintException if the input vector's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Vector times(Vector vector)
    {
        if (vector.getDimensionality() != getNumColumns())
        {
            throw new IllegalArgumentException("Input vector's size ("
                + vector.getDimensionality() + ") doesn't match mine for mult ("
                + getNumRows() + ", " + getNumColumns() + ")");
        }

        if (vector instanceof DenseVector)
        {
            return _times((DenseVector) vector);
        }
        else if (vector instanceof SparseVector)
        {
            return _times((SparseVector) vector);
        }
        else
        {
            throw new TypeConstraintException("Input Vector class "
                + vector.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input sparse vector.
     *
     * @param vector A sparse vector to multiply with this
     */
    abstract Vector _times(SparseVector vector);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input dense vector.
     *
     * @param vector A dense vector to multiply with this
     */
    abstract Vector _times(DenseVector vector);

    /**
     * Package-private method that puts the vector * matrix code in the matrix
     * class instead of in the vector class (why should vectors know the
     * internals of matrices?).
     *
     * @param vector The vector to pre-multiply this by
     * @return The resulting vector from input * this
     * @throws IllegalArgumentException if the input vectors's dimensions
     * doesn't match this's numRows.
     * @throws TypeConstraintException if the input vector's type doesn't match
     * an implementation within this package.
     */
    final Vector preTimes(Vector vector)
    {
        if (vector.getDimensionality() != getNumRows())
        {
            throw new IllegalArgumentException("Input vector's size ("
                + vector.getDimensionality() + ") doesn't match mine for "
                + "pre-mult (" + getNumRows() + ", " + getNumColumns() + ")");
        }

        if (vector instanceof DenseVector)
        {
            return _preTimes((DenseVector) vector);
        }
        else if (vector instanceof SparseVector)
        {
            return _preTimes((SparseVector) vector);
        }
        else
        {
            throw new TypeConstraintException("Input Vector class "
                + vector.getClass() + " not supported");
        }
    }

    /**
     * Type-specific version of pre-times for combining whatever type this is
     * with the input sparse vector.
     *
     * @param vector A sparse vector to multiply with this
     */
    abstract Vector _preTimes(SparseVector vector);

    /**
     * Type-specific version of pre-times for combining whatever type this is
     * with the input dense vector.
     *
     * @param vector A dense vector to multiply with this
     */
    abstract Vector _preTimes(DenseVector vector);

    /**
     * Helper method checks that the input submatrix range is acceptable for
     * this matrix, including that the max is greater than or equal to the min.
     * Note that the input mins and maxs are inclusive.
     *
     * @param minrow The minimum row to return (inclusive)
     * @param maxrow The maximum row to return (inclusive)
     * @param mincol The minimum column to return (inclusive)
     * @param maxcol The maximum column to return (inclusive)
     * @throws ArrayIndexOutOfBoundsException if the input range is illegal or
     * outside the bounds of this.
     */
    final protected void checkSubmatrixRange(int minrow,
        int maxrow,
        int mincol,
        int maxcol)
    {
        if (maxrow < minrow || maxcol < mincol || minrow < 0 || mincol < 0
            || maxrow
            > getNumRows() || maxcol > getNumColumns())
        {
            throw new ArrayIndexOutOfBoundsException("Input range is invalid: ["
                + minrow + ", " + maxrow + "], [" + mincol + ", " + maxcol + "]");
        }
    }

    /**
     * Checks that the input vector has the same dimensionality as this's
     * numRows (otherwise, there can be no solution for Ax = b).
     *
     * @param b The RHS vector
     * @throws IllegalArgumentException if the input's size doesn't match this's
     * numRows
     */
    final protected void checkSolveDimensions(Vector b)
    {
        if (b.getDimensionality() != getNumRows())
        {
            throw new IllegalArgumentException("Input vector (length = "
                + b.getDimensionality()
                + ") can't be \"solved\" with by a matrix with dimensions "
                + getNumRows() + "x" + getNumColumns());
        }
    }

    /**
     * Checks that the input matrix has the same numRows as this's numRows
     * (otherwise, there can be no soluation for Ax = b).
     *
     * @param b The RHS matrix
     * @throws IllegalArgumentException if the input's size doesn't match this's
     * numRows
     */
    final protected void checkSolveDimensions(Matrix b)
    {
        if (b.getNumRows() != getNumRows())
        {
            throw new IllegalArgumentException("Input matrix (numRows = "
                + b.getNumRows()
                + ") can't be \"solved\" with by a matrix with dimensions "
                + getNumRows() + "x" + getNumColumns());
        }
    }

    /**
     * @see AbstractMatrix#toString(java.text.NumberFormat)
     */
    @Override
    final public String toString(NumberFormat format)
    {
        StringBuilder sb = new StringBuilder();
        int m = getNumRows();
        int n = getNumColumns();
        for (int i = 0; i < m; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                sb.append(format.format(getElement(i, j))).append(" ");
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * Makes sure the input effectiveZero is non-negative. Because a negative
     * effective zero is such a bummer.
     *
     * @param effectiveZero The effectiveZero parameter (for pseudoInverse,
     * rank, etc.)
     * @throws IllegalArgumentException if effectiveZero is negative
     */
    final protected void testEffZero(double effectiveZero)
    {
        if (effectiveZero < 0)
        {
            throw new IllegalArgumentException("Effective zero must not be "
                + "negative.");
        }
    }

    /**
     * @see AbstractMatrix#iterator()
     */
    @Override
    final public Iterator<MatrixEntry> iterator()
    {
        return new MatrixIterator(this);
    }

}
