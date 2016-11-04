/*
 * File:                BaseMatrix.java
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

import gov.sandia.cognition.math.matrix.AbstractMatrix;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.Vector;
import java.text.NumberFormat;
import java.util.Iterator;

/**
 * This package-private class implements the basic math methods, ensures size
 * constraints are met for the operation, and then calls the
 * matrix-type-specific (diagonal, dense, sparse) version of the basic matrix
 * method.
 *
 * @author Jeremy D. Wendt
 * @since 3.4.4
 */
abstract class BaseMatrix
    extends AbstractMatrix
{

    /**
     * Creates a new {@link BaseMatrix}.
     */
    protected BaseMatrix()
    {
        super();
    }

    @Override
    final public Matrix plus(
        final Matrix other)
    {
        final Matrix result;
        if (this instanceof DenseMatrix)
        {
            result = this.clone();
            result.plusEquals(other);
        }
        else if (other instanceof DenseMatrix)
        {
            result = other.clone();
            result.plusEquals(this);
        }
        else if (this instanceof SparseMatrix)
        {
            result = this.clone();
            result.plusEquals(other);
        }
        else if (other instanceof SparseMatrix)
        {
            result = other.clone();
            result.plusEquals(this);
        }
        else if (this instanceof DiagonalMatrix && other instanceof DiagonalMatrix)
        {
            result = this.clone();
            result.plusEquals(other);
        }
        else
        {
            result = this.clone();
            result.plusEquals(other);
        }
        return result;
    }

    @Override
    final public Matrix minus(
        final Matrix other)
    {
        final Matrix result;
        if (this instanceof DenseMatrix)
        {
            result = this.clone();
            result.minusEquals(other);
        }
        else if (other instanceof DenseMatrix)
        {
            result = other.negative();
            result.plusEquals(this);
        }
        else if (this instanceof SparseMatrix)
        {
            result = this.clone();
            result.minusEquals(other);
        }
        else if (other instanceof SparseMatrix)
        {
            result = other.negative();
            result.plusEquals(this);
        }
        else if (this instanceof DiagonalMatrix && other instanceof DiagonalMatrix)
        {
            result = this.clone();
            result.minusEquals(other);
        }
        else
        {
            result = this.clone();
            result.minusEquals(other);
        }
        return result;
    }

    @Override
    final public Matrix dotTimes(
        final Matrix other)
    {
        final Matrix result;
        if (this instanceof DiagonalMatrix)
        {
            result = this.clone();
            result.dotTimesEquals(other);
        }
        else if (other instanceof DiagonalMatrix)
        {
            result = other.clone();
            result.dotTimesEquals(this);
        }
        else if (this instanceof SparseMatrix)
        {
            result = this.clone();
            result.dotTimesEquals(other);
        }
        else if (other instanceof SparseMatrix)
        {
            result = other.clone();
            result.dotTimesEquals(this);
        }
        else if (this instanceof DenseMatrix && other instanceof DenseMatrix)
        {
            result = this.clone();
            result.dotTimesEquals(other);
        }
        else
        {
            result = this.clone();
            result.dotTimesEquals(other);
        }
        return result;
    }

    @Override
    final public void plusEquals(
        final Matrix other)
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
    public abstract void plusEquals(
        final SparseMatrix other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input dense matrix.
     *
     * @param other A dense matrix to add to this
     */
    public abstract void plusEquals(
        final DenseMatrix other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input diagonal matrix.
     *
     * @param other A diagonal matrix to add to this
     */
    public abstract void plusEquals(
        final DiagonalMatrix other);

    @Override
    final public void scaledPlusEquals(
        final double scaleFactor,
        final Matrix other)
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
    public abstract void scaledPlusEquals(
        final SparseMatrix other,
        final double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input dense matrix.
     *
     * @param other A dense matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    public abstract void scaledPlusEquals(
        final DenseMatrix other,
        final double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input diagonal matrix.
     *
     * @param other A diagonal matrix to add to this
     * @param scaleFactor The amount to scale other by
     */
    public abstract void scaledPlusEquals(
        final DiagonalMatrix other,
        final double scaleFactor);

    @Override
    final public void minusEquals(
        final Matrix other)
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
    public abstract void minusEquals(
        final SparseMatrix other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input dense matrix.
     *
     * @param other A dense matrix to subtract from this
     */
    public abstract void minusEquals(
        final DenseMatrix other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input diagonal matrix.
     *
     * @param other A diagonal matrix to subtract from this
     */
    public abstract void minusEquals(
        final DiagonalMatrix other);

    @Override
    final public void dotTimesEquals(
        final Matrix other)
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
    public abstract void dotTimesEquals(
        final SparseMatrix other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input dense matrix.
     *
     * @param other A dense matrix to dot with this
     */
    public abstract void dotTimesEquals(
        final DenseMatrix other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input diagonal matrix.
     *
     * @param other A diagonal matrix to dot with this
     */
    public abstract void dotTimesEquals(
        final DiagonalMatrix other);

    @Override
    final public Matrix times(
        final Matrix other)
    {
        if (other instanceof DiagonalMatrix)
        {
            return this.times((DiagonalMatrix) other);
        }
        else if (other instanceof DenseMatrix)
        {
            return this.times((DenseMatrix) other);
        }
        else if (other instanceof SparseMatrix)
        {
            return this.times((SparseMatrix) other);
        }
        else
        {
            return super.times(other);
        }
    }

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input sparse matrix.
     *
     * @param other A sparse matrix to multiply with this
     */
    public abstract Matrix times(
        final SparseMatrix other);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input dense matrix.
     *
     * @param other A dense matrix to multiply with this
     */
    public abstract Matrix times(
        final DenseMatrix other);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input diagonal matrix.
     *
     * @param other A diagonal matrix to multiply with this
     */
    public abstract Matrix times(
        final DiagonalMatrix other);

    @Override
    final public Vector times(
        final Vector vector)
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
    public abstract Vector times(
        final SparseVector vector);

    /**
     * Type-specific version of times for combining whatever type this is with
     * the input dense vector.
     *
     * @param vector A dense vector to multiply with this
     */
    public abstract Vector times(
        final DenseVector vector);

    /**
     * Package-private method that puts the vector * matrix code in the matrix
     * class instead of in the vector class (why should vectors know the
     * internals of matrices?).
     *
     * @param vector The vector to pre-multiply this by
     * @return The resulting vector from input * this
     * @throws DimensionalityMismatchException if the input vectors's dimensions
     * doesn't match this's numRows.
     */
    public final Vector preTimes(
        final Vector vector)
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
            // Fallback for compatability.
            vector.assertDimensionalityEquals(this.getNumRows());
            final Vector result = vector.getVectorFactory().createVector(
                this.getNumColumns());
            for (final MatrixEntry entry : this)
            {
                result.increment(entry.getColumnIndex(),
                    entry.getValue() * vector.get(entry.getRowIndex()));
            }
            return result;
        }
    }

    /**
     * Type-specific version of pre-times for combining whatever type this is
     * with the input sparse vector.
     *
     * @param vector A sparse vector to multiply with this
     */
    public abstract Vector preTimes(
        final SparseVector vector);

    /**
     * Type-specific version of pre-times for combining whatever type this is
     * with the input dense vector.
     *
     * @param vector A dense vector to multiply with this
     */
    public abstract Vector preTimes(
        final DenseVector vector);

    /**
     * Helper method checks that the input submatrix range is acceptable for
     * this matrix, including that the max is greater than or equal to the min.
     * Note that the input mins and maxs are inclusive.
     *
     * @param minRow The minimum row to return (inclusive)
     * @param maxRow The maximum row to return (inclusive)
     * @param minCol The minimum column to return (inclusive)
     * @param maxCol The maximum column to return (inclusive)
     * @throws ArrayIndexOutOfBoundsException if the input range is illegal or
     * outside the bounds of this.
     */
    final protected void checkSubmatrixRange(
        final int minRow,
        final int maxRow,
        final int minCol,
        final int maxCol)
    {
        if (maxRow < minRow || maxCol < minCol || minRow < 0 || minCol < 0
            || maxRow
            > getNumRows() || maxCol > getNumColumns())
        {
            throw new ArrayIndexOutOfBoundsException("Input range is invalid: ["
                + minRow + ", " + maxRow + "], [" + minCol + ", " + maxCol + "]");
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
    final protected void checkSolveDimensions(
        final Vector b)
    {
        if (b.getDimensionality() != this.getNumRows())
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
    final protected void checkSolveDimensions(
        final Matrix b)
    {
        if (b.getNumRows() != this.getNumRows())
        {
            throw new IllegalArgumentException("Input matrix (numRows = "
                + b.getNumRows()
                + ") can't be \"solved\" with by a matrix with dimensions "
                + getNumRows() + "x" + getNumColumns());
        }
    }

    @Override
    final public String toString(
        final NumberFormat format)
    {
        StringBuilder sb = new StringBuilder();
        int m = getNumRows();
        int n = getNumColumns();
        for (int i = 0; i < m; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                sb.append(format.format(get(i, j))).append(" ");
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    @Override
    public Iterator<MatrixEntry> iterator()
    {
        return new MatrixIterator(this);
    }

}
