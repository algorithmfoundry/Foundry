
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.AbstractMatrix;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ArgumentChecker;
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
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix plus(Matrix m)
    {
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
            ret = this.clone();
            ret.plusEquals(m);
        }
        return ret;
    }

    /**
     * @see AbstractMatrix#minus(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix minus(Matrix m)
    {
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
            ret = this.clone();
            ret.minusEquals(m);
        }
        return ret;
    }

    /**
     * @see AbstractMatrix#dotTimes(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix dotTimes(Matrix m)
    {
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
            ret = this.clone();
            ret.dotTimesEquals(m);
        }
        return ret;
    }

    /**
     * @see AbstractMatrix#plusEquals(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void plusEquals(Matrix other)
    {
        if (other instanceof DiagonalMatrix)
        {
            plusEquals((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            this.plusEquals((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            this.plusEquals((SparseMatrix) other);
        }
        else
        {
            super.plusEquals(other);
        }
    }

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input sparse matrix.
     *
     * @param other A sparse matrix to add to this
     */
    public abstract void plusEquals(SparseMatrix other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input dense matrix.
     *
     * @param other A dense matrix to add to this
     */
    public abstract void plusEquals(DenseMatrix other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input diagonal matrix.
     *
     * @param other A diagonal matrix to add to this
     */
    public abstract void plusEquals(DiagonalMatrix other);

    /**
     * @see Matrix#scaledPlusEquals(double, gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void scaledPlusEquals(double scaleFactor,
        Matrix other)
    {
        if (other instanceof DiagonalMatrix)
        {
            scaledPlusEquals((DiagonalMatrix) other, scaleFactor);
        }
        else if (other instanceof DenseMatrix)
        {
            this.scaledPlusEquals((DenseMatrix) other, scaleFactor);
        }
        else if (other instanceof SparseMatrix)
        {
            this.scaledPlusEquals((SparseMatrix) other, scaleFactor);
        }
        else
        {
            super.scaledPlusEquals(scaleFactor, other);
        }
    }

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input sparse matrix.
     *
     * @param other A sparse matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    public abstract void scaledPlusEquals(SparseMatrix other,
        double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input dense matrix.
     *
     * @param other A dense matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    public abstract void scaledPlusEquals(DenseMatrix other,
        double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input diagonal matrix.
     *
     * @param other A diagonal matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    public abstract void scaledPlusEquals(DiagonalMatrix other,
        double scaleFactor);

    /**
     * @see AbstractMatrix#minusEquals(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void minusEquals(Matrix other)
    {
        if (other instanceof DiagonalMatrix)
        {
            minusEquals((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            this.minusEquals((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            this.minusEquals((SparseMatrix) other);
        }
        else
        {
            super.minusEquals(other);
        }
    }

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input sparse matrix.
     *
     * @param other A sparse matrix to subtract from this
     */
    public abstract void minusEquals(SparseMatrix other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input dense matrix.
     *
     * @param other A dense matrix to subtract from this
     */
    public abstract void minusEquals(DenseMatrix other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input diagonal matrix.
     *
     * @param other A diagonal matrix to subtract from this
     */
    public abstract void minusEquals(DiagonalMatrix other);

    /**
     * @see AbstractMatrix#dotTimesEquals(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's size doesn't match
     * this's size.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public void dotTimesEquals(Matrix other)
    {
        if (other instanceof DiagonalMatrix)
        {
            dotTimesEquals((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            this.dotTimesEquals((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            this.dotTimesEquals((SparseMatrix) other);
        }
        else
        {
            super.dotTimesEquals(other);
        }
    }

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input sparse matrix.
     *
     * @param other A sparse matrix to dot with this
     */
    public abstract void dotTimesEquals(SparseMatrix other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input dense matrix.
     *
     * @param other A dense matrix to dot with this
     */
    public abstract void dotTimesEquals(DenseMatrix other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input diagonal matrix.
     *
     * @param other A diagonal matrix to dot with this
     */
    public abstract void dotTimesEquals(DiagonalMatrix other);

    /**
     * @see AbstractMatrix#times(gov.sandia.cognition.math.Ring)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input matrix's numRows doesn't
     * match this's numColumns.
     * @throws TypeConstraintException if the input matrix's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Matrix times(Matrix matrix)
    {
        if (matrix instanceof DiagonalMatrix)
        {
            return this.times((DiagonalMatrix) matrix);
        }
        else if (matrix instanceof DenseMatrix)
        {
            return this.times((DenseMatrix) matrix);
        }
        else if (matrix instanceof SparseMatrix)
        {
            return this.times((SparseMatrix) matrix);
        }
        else
        {
            return super.times(matrix);
        }
    }

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input sparse matrix.
     *
     * @param other A sparse matrix to multiply with this
     */
    public abstract Matrix times(SparseMatrix other);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input dense matrix.
     *
     * @param other A dense matrix to multiply with this
     */
    public abstract Matrix times(DenseMatrix other);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input diagonal matrix.
     *
     * @param other A diagonal matrix to multiply with this
     */
    public abstract Matrix times(DiagonalMatrix other);

    /**
     * @see AbstractMatrix#times(gov.sandia.cognition.math.Vector)
     *
     * This implementation tests the input for the correct size and calls the
     * optimized-by-type method.
     * @throws DimensionalityMismatchException if the input vectors's dimensions
     * doesn't match this's numCols.
     * @throws TypeConstraintException if the input vector's type doesn't match
     * an implementation within this package.
     */
    @Override
    final public Vector times(Vector vector)
    {
        if (vector instanceof DenseVector)
        {
            return times((DenseVector) vector);
        }
        else if (vector instanceof SparseVector)
        {
            return this.times((SparseVector) vector);
        }
        else
        {
            return super.times(vector);
        }
    }

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input sparse vector.
     *
     * @param vector A sparse vector to multiply with this
     */
    public abstract Vector times(SparseVector vector);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input dense vector.
     *
     * @param vector A dense vector to multiply with this
     */
    public abstract Vector times(DenseVector vector);

    /**
     * Package-private method that puts the vector * matrix code in the matrix
     * class instead of in the vector class (why should vectors know the
     * internals of matrices?).
     *
     * @param vector The vector to pre-multiply this by
     * @return The resulting vector from input * this
     * @throws DimensionalityMismatchException if the input vectors's dimensions
     * doesn't match this's numRows.
     * @throws TypeConstraintException if the input vector's type doesn't match
     * an implementation within this package.
     */
    public final Vector preTimes(Vector vector)
    {
        if (vector instanceof DenseVector)
        {
            return preTimes((DenseVector) vector);
        }
        else if (vector instanceof SparseVector)
        {
            return this.preTimes((SparseVector) vector);
        }
        else
        {
// TODO: Remove the need for this. Maybe promote it to the abstract class.
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
    public abstract Vector preTimes(SparseVector vector);

    /**
     * Type-specific version of pre-times for combining whatever type this is
     * with the input dense vector.
     *
     * @param vector A dense vector to multiply with this
     */
    public abstract Vector preTimes(DenseVector vector);

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
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
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
