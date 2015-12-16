/*
 * File:                DenseMatrix.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.List;
import com.github.fommil.netlib.BLAS;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.util.ArgumentChecker;
import org.netlib.util.intW;

/**
 * A dense matrix implementation. Wherever possible, computation is pushed into
 * BLAS or LAPACK expecting that those will be considerably faster than code
 * written by me.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class DenseMatrix
    extends BaseMatrix
{

    /**
     * Optimal block size returned from QR decomposition. This is assumed to be
     * hardware-specific, so static across different instances of dense matrix.
     * By saving a static member, this means that only the first QR
     * decomposition of any instance uses the less-than-optimal block size.
     */
    private static int qrOptimalBlockSize = 1024;

    /**
     * The rows of the matrix stored as dense vectors
     */
    private DenseVector[] rows;

    /**
     * This handler calls out to native BLAS if native BLAS is installed
     * properly on the computer at program start. If not, this calls jBLAS.
     */
    private static NativeBlasHandler setBlas = null;

    /**
     * Creates a zero matrix of the specified dimensions
     *
     * @param numRows The number of rows in the matrix
     * @param numCols The number of columns in the matrix
     */
    public DenseMatrix(int numRows,
        int numCols)
    {
        rows = new DenseVector[numRows];
        initToDefaultVal(numRows, numCols, 0);
        initBlas();
    }

    /**
     * Creates a matrix of default val in all cells of the specified dimensions
     *
     * @param numRows The number of rows in the matrix
     * @param numCols The number of columns in the matrix
     * @param defaultVal The value to set all elements to
     */
    public DenseMatrix(int numRows,
        int numCols,
        double defaultVal)
    {
        rows = new DenseVector[numRows];
        initToDefaultVal(numRows, numCols, defaultVal);
        initBlas();
    }

    /**
     * Copy constructor that creates a deep copy of the input matrix. Optimized
     * for copying DenseMatrices.
     *
     * @param m The matrix to copy
     */
    public DenseMatrix(DenseMatrix m)
    {
        rows = new DenseVector[m.getNumRows()];
        for (int i = 0; i < m.getNumRows(); ++i)
        {
            rows[i] = new DenseVector(m.rows[i]);
        }
        initBlas();
    }

    /**
     * Copy constructor that copies any input Matrix -- creating a deep copy of
     * the matrix.
     *
     * @param m The matrix to copy.
     */
    public DenseMatrix(Matrix m)
    {
        rows = new DenseVector[m.getNumRows()];
        for (int i = 0; i < m.getNumRows(); ++i)
        {
            rows[i] = new DenseVector(m.getNumColumns());
            for (int j = 0; j < m.getNumColumns(); ++j)
            {
                rows[i].setElement(j, m.getElement(i, j));
            }
        }
        initBlas();
    }

    /**
     * Package private optimized constructor that does not set any values for
     * the rows. If the package private setRowInternal (or equivalent) is not called
     * for all rows, this sets up for weird null pointer exceptions later. Also,
     * please note that the numCols parameter isn't used here and you can pass
     * in rows with a different number of columns, but that would be -such- bad
     * form.
     *
     * This is an optimized constructor for within package only. Don't call this
     * unless you are -super- careful.
     *
     * @param numRows The number of rows for this
     * @param numCols The number of columns for this
     * @param unused An unused boolean passed just to change the interface from
     * the standard constructor
     */
    DenseMatrix(int numRows,
        int numCols,
        boolean unused)
    {
        rows = new DenseVector[numRows];
        // Don't initialize the row's values
        initBlas();
    }

    /**
     * Constructor for creating a dense matrix from a 2-d double array. Copies
     * the values from arr. Input is treated as row-major. While it would be
     * possible to pass in a different length list for each row, that would be
     * bad form. The length of the first row is assumed as the number of columns
     * desired.
     *
     * @param arr The 2-d double array that specifies the dimensions and values
     * to be stored in the new matrix.
     */
    public DenseMatrix(double[][] arr)
    {
        int numRows = arr.length;
        int numCols = (numRows == 0) ? 0 : arr[0].length;
        rows = new DenseVector[numRows];
        for (int i = 0; i < numRows; ++i)
        {
            DenseVector v = new DenseVector(numCols, true);
            for (int j = 0; j < numCols; ++j)
            {
                v.elements()[j] = arr[i][j];
            }
            setRow(i, v);
        }
    }

    /**
     * Constructor for creating a dense matrix from a 2-d double List. Copies
     * the values from arr. Input is treated as row-major. While it would be
     * possible to pass in a different length list for each row, that would be
     * bad form. The length of the first row is assumed as the number of columns
     * desired.
     *
     * @param arr The 2-d double array that specifies the dimensions and values
     * to be stored in the matrix.
     */
    public DenseMatrix(List<List<Double>> arr)
    {
        int numRows = arr.size();
        int numCols = (numRows == 0) ? 0 : arr.get(0).size();
        rows = new DenseVector[numRows];
        for (int i = 0; i < numRows; ++i)
        {
            DenseVector v = new DenseVector(numCols, true);
            for (int j = 0; j < numCols; ++j)
            {
                v.elements()[j] = arr.get(i).get(j);
            }
            setRow(i, v);
        }
    }

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected DenseMatrix()
    {
        // NOTE: This doesn't initialize anything
    }

    /**
     * Helper that sets all values in the matrix to the specified default value.
     * Initializes each row herein as well.
     *
     * @param numRows The number of rows to set the value in (should be all of
     * the rows in the matrix).
     * @param numCols The number of columns in the matrix -- all rows are
     * initialized to that length.
     * @param defaultVal The value to specify in each element of the matrix.
     */
    private void initToDefaultVal(int numRows,
        int numCols,
        double defaultVal)
    {
        for (int i = 0; i < numRows; ++i)
        {
            rows[i] = new DenseVector(numCols, defaultVal);
        }
    }

    /**
     * Creates the NativeBlasHandler and tests to see if native BLAS is
     * available.
     */
    private static void initBlas()
    {
        if (setBlas != null)
        {
            return;
        }
        setBlas = new NativeBlasHandler();
        if (NativeBlasHandler.nativeBlasAvailable())
        {
            setBlas.setToNativeBlas();
        }
        else
        {
            setBlas.setToJBlas();
        }
    }

    /**
     * Reads the input double array (dense, column-major as output by BLAS) and
     * creates a new dense matrix with those values.
     *
     * @param d The BLAS representation of the matrix (must be length
     * numRows*numCols)
     * @param numRows The number of rows in d
     * @param numCols The number of columns in d
     * @return A new DenseMatrix with the specified size and elements stored in
     * d (column major order)
     */
    static DenseMatrix createFromBlas(double d[],
        int numRows,
        int numCols)
    {
        DenseMatrix ret = new DenseMatrix(numRows, numCols);
        ret.fromBlas(d, numRows, numCols);

        return ret;
    }

    /**
     * Loads the input values from the BLAS-ordered (dense, column-major) vector
     * d into this.
     *
     * @param d The values to load into this
     * @param numRows The number of rows in d
     * @param numCols The number of columns in d
     * @throws IllegalArgumentException if the input dimensions don't match
     * this's dimensions (that's why this is a private method -- it should only
     * be called by people who know what they're doing).
     */
    private void fromBlas(double d[],
        int numRows,
        int numCols)
    {
        if ((getNumRows() != numRows) || (getNumColumns() != numCols))
        {
            throw new IllegalArgumentException("Unable to convert from BLAS of "
                + "different size: (" + getNumRows() + " != " + numRows
                + ") || (" + getNumColumns() + " != " + numCols + ")");
        }

        for (int i = 0; i < numRows; ++i)
        {
            for (int j = 0; j < numCols; ++j)
            {
                rows[i].elements()[j] = blasElement(i, j, d, numRows, numCols);
            }
        }
    }

    /**
     * Converts this into a 1-d, dense, column-major vector (the layout required
     * by BLAS and LAPACK).
     *
     * @return a 1-d, dense, column-major vector representation of this
     */
    final double[] toBlas()
    {
        double[] ret = new double[getNumRows() * getNumColumns()];

        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                ret[i + (j * getNumRows())] = rows[i].getElement(j);
            }
        }

        return ret;
    }

    /**
     * Tests if BLAS can be called and if the multiplication will be within the
     * scale allowed by 1-d arrays that are indexed by integers (as Java
     * requires). If any of those are false, this is multiplied in the O(n^3)
     * method.
     *
     * NOTE: This is a problem whether you have native BLAS or jBLAS on your
     * system. The problem arises because I must first create the 1-d array
     * version of the matrices in Java itself.
     *
     * @param numRows1 The number of rows in the left matrix
     * @param numCols1 The number of columns in the left matrix
     * @param numRows2 The number of rows in the right matrix
     * @param numCols2 The number of columns in the right matrix
     * @return True if BLAS can be used, else false.
     */
    private static boolean canUseBlasForMult(int numRows1,
        int numCols1,
        int numRows2,
        int numCols2)
    {
        long mat1Size = ((long) numRows1) * ((long) numCols1);
        long mat2Size = ((long) numRows2) * ((long) numCols2);
        long matOutSize = ((long) numRows1) * ((long) numCols2);

        return (NativeBlasHandler.blasAvailable()) && (mat1Size
            == (int) mat1Size) && (mat2Size == (int) mat2Size) && (matOutSize
            == (int) matOutSize);
    }

    /**
     * The O(n^3) multiplication algorithm that is here for if BLAS isn't
     * available, or if the dimensions wouldn't allow 1-d array indexing.
     *
     * @param m The right matrix to multiply by this
     * @return The result of multiplying this times m
     */
    private Matrix slowMult(DenseMatrix m)
    {
        DenseMatrix ret = new DenseMatrix(getNumRows(), m.getNumColumns());
        for (int i = 0; i < getNumRows(); i++)
        {
            for (int j = 0; j < m.getNumColumns(); j++)
            {
                double val = 0;
                for (int k = 0; k < getNumColumns(); k++)
                {
                    val += getElement(i, k) * m.getElement(k, j);
                }
                ret.setElement(i, j, val);
            }
        }

        return ret;
    }

    /**
     * Returns a deep copy of this.
     *
     * @return a deep copy of this
     */
    @Override
    final public Matrix clone()
    {
        final int rowCount = this.getNumRows();
        DenseMatrix result = new DenseMatrix(rowCount,
            this.getNumColumns(), true);
        result.rows = new DenseVector[rowCount];
        for (int i = 0; i < rowCount; i++)
        {
            result.rows[i] = this.rows[i].clone();
        }
        return result;
    }

    @Override
    public void scaledPlusEquals(SparseMatrix other, double scaleFactor)
    {
        this.assertSameDimensions(other);
        if (!other.isCompressed())
        {
            other.compress();
        }

        double[] ovals = other.getVals();
        int[] ocolIdxs = other.getColIdxs();
        int[] ofirstRows = other.getFirstInRows();
        int rownum = 0;
        for (int i = 0; i < ovals.length; ++i)
        {
            while (ofirstRows[rownum + 1] <= i)
            {
                ++rownum;
            }
            rows[rownum].elements()[ocolIdxs[i]] += ovals[i] * scaleFactor;
        }
    }

    @Override
    public void scaledPlusEquals(DenseMatrix other, double scaleFactor)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                rows[i].elements()[j] += other.rows[i].elements()[j] * scaleFactor;
            }
        }
    }

    @Override
    public void scaledPlusEquals(DiagonalMatrix other, double scaleFactor)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            rows[i].elements()[i] += other.getElement(i, i) * scaleFactor;
        }
    }

    @Override
    public final void plusEquals(SparseMatrix other)
    {
        this.assertSameDimensions(other);
        if (!other.isCompressed())
        {
            other.compress();
        }

        double[] ovals = other.getVals();
        int[] ocolIdxs = other.getColIdxs();
        int[] ofirstRows = other.getFirstInRows();
        int rownum = 0;
        for (int i = 0; i < ovals.length; ++i)
        {
            while (ofirstRows[rownum + 1] <= i)
            {
                ++rownum;
            }
            rows[rownum].elements()[ocolIdxs[i]] += ovals[i];
        }
    }

    @Override
    public final void plusEquals(DenseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                rows[i].elements()[j] += other.rows[i].elements()[j];
            }
        }
    }

    @Override
    public final void plusEquals(DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            rows[i].elements()[i] += other.getElement(i, i);
        }
    }

    @Override
    public final void minusEquals(SparseMatrix other)
    {
        this.assertSameDimensions(other);
        if (!other.isCompressed())
        {
            other.compress();
        }

        double[] ovals = other.getVals();
        int[] ocolIdxs = other.getColIdxs();
        int[] ofirstRows = other.getFirstInRows();
        int rownum = 0;
        for (int i = 0; i < ovals.length; ++i)
        {
            while (ofirstRows[rownum + 1] <= i)
            {
                ++rownum;
            }
            rows[rownum].elements()[ocolIdxs[i]] -= ovals[i];
        }
    }

    @Override
    public final void minusEquals(DenseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                rows[i].elements()[j] -= other.rows[i].elements()[j];
            }
        }
    }

    @Override
    public final void minusEquals(DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            rows[i].elements()[i] -= other.getElement(i, i);
        }
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Calling this method is a bad idea because you end up storing a
     * sparse matrix in a dense representation.
     */
    @Override
    public final void dotTimesEquals(SparseMatrix other)
    {
        this.assertSameDimensions(other);
        if (!other.isCompressed())
        {
            other.compress();
        }
        int idx = 0;
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                // If j matches the current column and we're still within the
                // correct row in other
                if ((idx < other.getVals().length) && (j
                    == other.getColIdxs()[idx]) && (idx
                    < other.getFirstInRows()[i + 1]))
                {
                    // Multiply the values and advance to the next value in other
                    rows[i].elements()[j] *= other.getVals()[idx];
                    ++idx;
                }
                else
                {
                    // Otherwise, there is no value in the other matrix here
                    rows[i].elements()[j] = 0;
                }
            }
        }
    }

    @Override
    public final void dotTimesEquals(DenseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                rows[i].elements()[j] *= other.rows[i].elements()[j];
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Calling this method is a really bad idea because you end up storing
     * a diagonal matrix in a dense representation.
     */
    @Override
    public final void dotTimesEquals(DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                if (i == j)
                {
                    rows[i].elements()[i] *= other.getElement(i, i);
                }
                else
                {
                    rows[i].elements()[j] = 0;
                }
            }
        }
    }

    @Override
    public final Matrix times(SparseMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        if (!other.isCompressed())
        {
            other.compress();
        }
        DenseMatrix ret = new DenseMatrix(getNumRows(), other.getNumColumns(),
            true);
        for (int i = 0; i < getNumRows(); ++i)
        {
            ret.setRow(i, (DenseVector) other.preTimes(rows[i]));
        }
        return ret;
    }

    @Override
    public final Matrix times(DenseMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        // TODO: Make sure this BLAS is truly faster than slow version
        if (canUseBlasForMult(getNumRows(), getNumColumns(), other.getNumRows(),
            other.getNumColumns()))
        {
            double[] output = new double[getNumRows() * other.getNumColumns()];
            BLAS.getInstance().dgemm("N", "N", getNumRows(),
                other.getNumColumns(), getNumColumns(), 1.0, this.toBlas(),
                getNumRows(), other.toBlas(), other.getNumRows(), 0.0, output,
                getNumRows());
            return createFromBlas(output, getNumRows(), other.getNumColumns());
        }
        else
        {
            return slowMult(other);
        }
    }

    @Override
    public final Matrix times(DiagonalMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        DenseMatrix ret = new DenseMatrix(getNumRows(), getNumColumns(), true);
        for (int i = 0; i < getNumRows(); ++i)
        {
            ret.setRow(i, (DenseVector) other.preTimes(rows[i]));
        }

        return ret;
    }

    /**
     * Helper method that handles all vector-on-the-right multiplies because we
     * depend on the vector dotProduct optimization here.
     *
     * @param vector The vector to multiply
     * @return The vector resulting from multiplying this * vector
     */
    private Vector timesInternal(Vector vector)
    {
        vector.assertDimensionalityEquals(this.getNumColumns());
        DenseVector ret = new DenseVector(getNumRows(), true);
        for (int i = 0; i < getNumRows(); ++i)
        {
            ret.setElement(i, vector.dotProduct(rows[i]));
        }

        return ret;
    }

    @Override
    public final Vector times(SparseVector vector)
    {
        // It's the same for sparse and dense vectors (the difference is handled
        // in the vector's dotProduct code
        return timesInternal(vector);
    }

    @Override
    public final Vector times(DenseVector vector)
    {
        // It's the same for sparse and dense vectors (the difference is handled
        // in the vector's dotProduct code
        return timesInternal(vector);
    }

    @Override
    public final void scaleEquals(double scaleFactor)
    {
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                rows[i].elements()[j] = rows[i].elements()[j] * scaleFactor;
            }
        }
    }

    @Override
    final public int getNumRows()
    {
        return rows.length;
    }

    @Override
    final public int getNumColumns()
    {
        return (rows == null) ? 0 : (rows.length == 0) ? 0
            : rows[0].getDimensionality();
    }

    @Override
    public double get(int rowIndex,
        int columnIndex)
    {
        return getElement(rowIndex, columnIndex);
    }

    @Override
    final public double getElement(int rowIndex,
        int columnIndex)
    {
        return rows[rowIndex].getElement(columnIndex);
    }

    @Override
    public void set(int rowIndex,
        int columnIndex,
        double value)
    {
        setElement(rowIndex, columnIndex, value);
    }

    @Override
    final public void setElement(int rowIndex,
        int columnIndex,
        double value)
    {
        rows[rowIndex].setElement(columnIndex, value);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This is inclusive on both end points.
     * @param minRow {@inheritDoc}
     * @param maxRow {@inheritDoc}
     * @param minColumn {@inheritDoc}
     * @param maxColumn {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    final public Matrix getSubMatrix(int minRow,
        int maxRow,
        int minColumn,
        int maxColumn)
    {
        checkSubmatrixRange(minRow, maxRow, minColumn, maxColumn);
        DenseMatrix ret = new DenseMatrix(maxRow - minRow + 1, maxColumn
            - minColumn + 1, true);
        for (int i = minRow; i <= maxRow; ++i)
        {
            DenseVector row = new DenseVector(maxColumn - minColumn + 1, true);
            for (int j = minColumn; j <= maxColumn; ++j)
            {
                row.setElement(j - minColumn, rows[i].elements()[j]);
            }
            ret.setRow(i - minRow, row);
        }

        return ret;
    }

    @Override
    final public boolean isSymmetric(double effectiveZero)
    {
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
        // If it's not square, it's not symmetric
        if (getNumRows() != getNumColumns())
        {
            return false;
        }
        // Now check that all upper triangular values equal their corresponding
        // lower triangular vals (within effectiveZero range)
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = i + 1; j < getNumColumns(); ++j)
            {
                if (Math.abs(rows[i].elements()[j] - rows[j].elements()[i])
                    > effectiveZero)
                {
                    return false;
                }
            }
        }

        // If you make it here, they're equal.
        return true;
    }

    @Override
    final public Matrix transpose()
    {
        // It's the transpose of me
        int m = getNumColumns();
        int n = getNumRows();
        DenseMatrix ret = new DenseMatrix(m, n, true);

        for (int i = 0; i < m; ++i)
        {
            DenseVector row = new DenseVector(n);
            for (int j = 0; j < n; ++j)
            {
                row.setElement(j, getElement(j, i));
            }
            ret.setRow(i, row);
        }

        return ret;
    }

    @Override
    final public Matrix inverse()
    {
        if (!isSquare())
        {
            throw new IllegalStateException("Unable to compute inverse of non-"
                + "square matrix.");
        }
        Matrix I = new DiagonalMatrix(getNumRows());
        I.identity();

        return solve(I);
    }

    @Override
    final public Matrix pseudoInverse(double effectiveZero)
    {
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
        SVD svd = svdDecompose();
        int min = Math.min(getNumRows(), getNumColumns());
        for (int i = 0; i < min; ++i)
        {
            if (Math.abs(svd.Sigma.getElement(i, i)) <= effectiveZero)
            {
                svd.Sigma.setElement(i, i, 0);
            }
            else
            {
                svd.Sigma.setElement(i, i, 1.0 / svd.Sigma.getElement(i, i));
            }
        }

        return svd.V.times(svd.Sigma.transpose()).times(svd.U.transpose());
    }

    @PublicationReferences(references =
    {
        @PublicationReference(author = "Wikipedia",
            title = "Triagular Matrix / Special Properties",
            type = PublicationType.WebPage,
            year = 2013,
            url
            = "http://en.wikipedia.org/wiki/Triangular_matrix#Special_properties"),
        @PublicationReference(
            author = "Wikipedia",
            title = "Determinant / Relation to Eigenvalues and Trace",
            type = PublicationType.WebPage,
            year = 2013,
            url
            = "http://en.wikipedia.org/wiki/Determinant#Relation_to_eigenvalues_and_trace"),
        @PublicationReference(author = "Wikipedia",
            title = "Logarithm",
            type = PublicationType.WebPage,
            year = 2013,
            url = "http://en.wikipedia.org/wiki/Logarithm")
    })
    @Override
    final public ComplexNumber logDeterminant()
    {
        if (!isSquare())
        {
            throw new IllegalStateException("Matrix must be square");
        }

        LU lu = luDecompose();
        // The "L" matrix has a determinant of one, so the real action is
        // in the "U" matrix, which is upper (right) triangular and any row
        // swaps (each one flips the sign of the determinant).
        // NOTE: The determinant of a triangular matrix is the product of the
        // diagonal entries (see 
        // http://en.wikipedia.org/wiki/Triangular_matrix#Special_properties)
        // NOTE: Product of eigenvalues = determinant (see
        // http://en.wikipedia.org/wiki/Determinant#Relation_to_eigenvalues_and_trace)
        // NOTE: The logarithm of the product of two numbers is the sum of the
        // logarightm of each of the two numbers (see
        // http://en.wikipedia.org/wiki/Logarithm)
        // 
        // The diagonal elements of U will be REAL, but they may be negative.
        // The logarithm of a negative number is the logarithm of the absolute
        // value of the number, with an imaginary part of PI.  The exponential
        // is all that matters, so the log-determinant is equivalent, MODULO
        // PI (3.14...), so we just toggle this sign bit.
        int sign = 1;
        int M = lu.U.getNumRows();
        double logsum = 0.0;
        for (int i = 0; i < M; i++)
        {
            double eigenvalue = lu.U.getElement(i, i);
            if (eigenvalue < 0.0)
            {
                sign = -sign;
                logsum += Math.log(-eigenvalue);
            }
            else
            {
                logsum += Math.log(eigenvalue);
            }
            if (lu.P.get(i) != i)
            {
                sign = -sign;
            }
        }

        return new ComplexNumber(logsum, (sign < 0) ? Math.PI : 0.0);
    }

    @Override
    final public int rank(double effectiveZero)
    {
        ArgumentChecker.assertIsNonNegative("effectiveZero", effectiveZero);
        QR qr = qrDecompose();
        int min = Math.min(getNumRows(), getNumColumns());
        int ret = 0;
        for (int i = 0; i < min; ++i)
        {
            if (Math.abs(qr.R.getElement(i, i)) > effectiveZero)
            {
                ++ret;
            }
        }

        return ret;
    }

    @Override
    public double normFrobeniusSquared()
    {
        double ret = 0;
        for (int i = 0; i < rows.length; ++i)
        {
            for (int j = 0; j < rows[i].elements().length; ++j)
            {
                ret += rows[i].elements()[j] * rows[i].elements()[j];
            }
        }
        return ret;
    }

    @Override
    final public double normFrobenius()
    {
        return Math.sqrt(normFrobeniusSquared());
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

    /**
     * Simple container class for Singular Value Decomposition (SVD) results.
     * Stores three matrices U, Sigma, and V as public members. NOTE: This
     * includes V not V^T.
     */
    final public static class SVD
    {

        /**
         * The left basis matrix. Due to an issue within LAPACK, this may be a
         * left or right (proper) basis.
         */
        public Matrix U;

        /**
         * * The singular value diagonal matrix.
         */
        public Matrix Sigma;

        /**
         * The right basis matrix. Due to an issue within LAPACK, this may be a
         * left or right (proper) basis.
         */
        public Matrix V;

        /**
         * Creates the U, Sigma, and V matrices to their correct sizes, but
         * leaves them as zero matrices.
         *
         * @param A The matrix that will be decomposed and so it specifies the
         * sizes for U, Sigma, and V.
         */
        private SVD(DenseMatrix A)
        {
            U = new DenseMatrix(A.getNumRows(), A.getNumRows());
            Sigma = new SparseMatrix(A.getNumRows(), A.getNumColumns());
            V = new DenseMatrix(A.getNumColumns(), A.getNumColumns());
        }

    }

    /**
     * Leverages LAPACK to compute the Singular Value Decomposition (SVD) of
     * this. NOTE: Due to LAPACK issues, U and V may be left or right (proper)
     * basis matrices. It they don't count and report the number of row-swaps
     * used when computing the SVD so there's no way to know from the outside
     * without computing the determinant of these matrices (not a cheap
     * operation). Also, note that V is returned, not V^t.
     *
     * @return The three matrices U, Sigma, and V from the SVD of this
     * @throw IllegalStateException if LAPACK fails to decompose this for an
     * unknown reason.
     */
    final public SVD svdDecompose()
    {
        SVD ret = new SVD(this);

        // Initialize the results;
        ret.U.identity();
        ret.Sigma.zero();
        ret.V.identity();

        // Prepare for calling LAPACK
        String jobz = "A";
        int m = getNumRows();
        int n = getNumColumns();
        if ((m == 0) || (n == 0))
        {
            return ret;
        }
        double[] A = this.toBlas();
        int lda = m;
        int mindim = Math.min(m, n);
        double[] s = new double[mindim];
        int ldu = m;
        int ucol = m;
        double[] u = new double[ldu * ucol];
        int ldvt = n;
        double[] vt = new double[ldvt * n];
        int lwork = 3 * mindim * mindim + Math.max(Math.max(m, n), 4 * mindim
            * mindim + 4 * mindim) * 2;
        double[] work = new double[lwork];
        int[] iwork = new int[8 * mindim];
        intW info = new intW(1);

        // Call LAPACK
        com.github.fommil.netlib.LAPACK.getInstance().dgesdd(jobz, m, n, A, 0, lda, s, 0, u, 0, ldu,
            vt, 0, ldvt, work, 0, lwork, iwork, 0, info);
        if (info.val < 0)
        {
            throw new IllegalStateException(
                "LAPACK failed on SVD-decomposition "
                + "reporting an error at the " + (-1 * info.val) + "th input");
        }
        else if (info.val > 0)
        {
            throw new IllegalStateException("LAPACK failed to converge for "
                + "SVD-decomposition.");
        }

        // Pull out the values
        ((DenseMatrix) ret.U).fromBlas(u, ldu, ucol);
        ((DenseMatrix) ret.V).fromBlas(vt, ldvt, n);
        ret.V = ret.V.transpose();
        for (int i = 0; i < mindim; ++i)
        {
            ret.Sigma.setElement(i, i, s[i]);
        }

        return ret;
    }

    /**
     * Simple container class for LU decompositions. LU decomposition results in
     * a lower triangular matrix (L) and an upper triangular matrix (U). This
     * decomposition can be used for matrix inversion, determinant computation,
     * etc.
     */
    public static class LU
    {

        /**
         * The 0-based list of row swaps used in the factorization. NOTE: If row
         * 0 is swapped with row 1, P will contain [1, 1, 2, ...] (because if it
         * said [1, 0, 2, ...] that would mean that it swapped row 0 with 1 and
         * then row 1 with 0 (swapping back the original rows)).
         *
         * If this seems too confusing, just use the getPivotMatrix helper
         * method.
         */
        public List<Integer> P;

        /**
         * The lower triangular matrix resulting from the factorization.
         */
        public Matrix L;

        /**
         * The upper triangular matrix resulting from the factorization.
         */
        public Matrix U;

        /**
         * Creates the P, L, and U data structures based on the size of the A
         * matrix that will be factored and stored into this.
         *
         * @param A The matrix to be factored -- specifies the dimensions of the
         * parts of this.
         */
        private LU(DenseMatrix A)
        {
            int m = A.getNumRows();
            int n = A.getNumColumns();
            if (m <= n)
            {
                P = new ArrayList<Integer>(m);
                L = new DenseMatrix(m, m);
                U = new DenseMatrix(m, n);
            }
            else
            {
                P = new ArrayList<Integer>(n);
                L = new DenseMatrix(m, n);
                U = new DenseMatrix(n, n);
            }
        }

        /**
         * Helper method converts P into a pivot matrix that can pre-multiply
         * L*U.
         *
         * @return an identity matrix with the appropriate row swaps to
         * re-generate the original matrix A when P * L * U is calculated.
         */
        public Matrix getPivotMatrix()
        {
            SparseMatrix pivot
                = new SparseMatrix(L.getNumRows(), L.getNumRows());
            pivot.identity();
            for (int i = 0; i < P.size(); ++i)
            {
                if (P.get(i) != i)
                {
                    Vector tmp = pivot.getRow(i);
                    pivot.setRow(i, pivot.getRow(P.get(i)));
                    pivot.setRow(P.get(i), tmp);
                }
            }

            return pivot;
        }

    }

    /**
     * Leverages LAPACK to create the LU decomposition of this. Note: In this
     * case, the row swaps performed when computing the LU factorization are
     * preserved in LU.P so that any -1 scalings of the determinant can be
     * determined.
     *
     * @return the LU decomposition of this
     */
    final public LU luDecompose()
    {
        LU ret = new LU(this);

        // Initialize the results
        ret.L.zero();
        ret.U.zero();

        // Prepare for calling LAPACK
        int m = getNumRows();
        int n = getNumColumns();
        if ((m == 0) || (n == 0))
        {
            return ret;
        }
        int lda = m;
        double[] A = this.toBlas();
        int ipivdim = Math.min(m, n);
        int[] ipiv = new int[ipivdim];
        intW info = new intW(1);

        // Call LAPACK
        com.github.fommil.netlib.LAPACK.getInstance().dgetrf(m, n, A, 0, lda, ipiv, 0, info);
        if (info.val < 0)
        {
            throw new IllegalStateException("LAPACK failed on LU-decomposition "
                + "reporting an error at the " + (-1 * info.val) + "th input");
        }

        //Copy out L, U, and P
        for (int i = 0; i < m; ++i)
        {
            for (int j = i; j < n; ++j)
            {
                ret.U.setElement(i, j, blasElement(i, j, A, m, n));
            }
            for (int j = 0; j < Math.min(i, n); ++j)
            {
                ret.L.setElement(i, j, blasElement(i, j, A, m, n));
            }
            // Only as many row swaps and diagonals as there are rows and
            // columns (whichever is smaller)
            if (i < n)
            {
                ret.L.setElement(i, i, 1);
                // Fortran is 1-based, Java is 0-based
                ret.P.add(ipiv[i] - 1);
            }
        }

        return ret;
    }

    /**
     * Container class that stores the Q and R matrices formed by the QR
     * decomposition of this. Q is an orthonormal basis matrix and R is an upper
     * triangular matrix. Due to the fact that LAPACK does not return the row
     * swaps for QR factorization, Q may be a left or right (proper) basis.
     */
    public static class QR
    {

        /**
         * The orthonormal basis matrix
         */
        public Matrix Q;

        /**
         * The upper-triangular matrix
         */
        public Matrix R;

        /**
         * Initializes Q and R to the correct size (based on A), but leaves them
         * as zeroes.
         *
         * @param A The matrix to be factored into the Q and R stored herein.
         */
        private QR(DenseMatrix A)
        {
            Q = new DenseMatrix(A.getNumRows(), A.getNumRows());
            R = new DenseMatrix(A.getNumRows(), A.getNumColumns());
        }

    }

    /**
     * Leverage LAPACK to compute the QR Decomposition of this. Since LAPACK
     * doesn't return any record of row swaps used to factor this, Q may be a
     * left or right (proper) basis matrix.
     *
     * @return the QR decomposition (Q is orthonormal basis, R is upper
     * triangular)
     */
    @PublicationReference(author = "NetLib",
        title = "DGEQRF",
        type = PublicationType.WebPage,
        year = 2013,
        url
        = "http://icl.cs.utk.edu/projectsfiles/f2j/javadoc/org/netlib/lapack/Dgeqrf.html")
    final public QR qrDecompose()
    {
        QR ret = new QR(this);

        // Initialize the results
        ret.Q.identity();
        ret.R.zero();

        // Prepare for calling LAPACK
        int m = getNumRows();
        if (m == 0)
        {
            return ret;
        }
        int n = getNumColumns();
        if (n == 0)
        {
            return ret;
        }
        double[] A = this.toBlas();
        int lda = m;
        double[] tau = new double[Math.min(m, n)];
        int lwork = qrOptimalBlockSize * m;
        double[] work = new double[lwork];
        intW info = new intW(100);

        // Call LAPACK
        com.github.fommil.netlib.LAPACK.getInstance().dgeqrf(m, n, A, 0, lda, tau, 0, work, 0,
            lwork, info);
        if (info.val != 0)
        {
            throw new IllegalStateException("LAPACK failed on QR-decomposition "
                + "reporting an error at the " + (-1 * info.val) + "th input");
        }
        // After calling the method, the optimal block size is in work[0] (see
        // LAPACK documentation).  This is for if QR is ever called again
        qrOptimalBlockSize = (int) Math.round(work[0]);

        // Copy out R
        for (int i = 0; i < m; ++i)
        {
            for (int j = i; j < n; ++j)
            {
                ret.R.setElement(i, j, blasElement(i, j, A, m, n));
            }
        }

        // Calculate Q
        // See http://icl.cs.utk.edu/projectsfiles/f2j/javadoc/org/netlib/lapack/Dgeqrf.html
        // specifically their crazy way of storing Q in tau and the lower 
        // portion of A
        DiagonalMatrix I = new DiagonalMatrix(m);
        I.identity();
        DenseVector v = new DenseVector(m);
        int imax = Math.min(m, n);
        for (int i = 0; i < imax; ++i)
        {
            for (int j = 0; j < i; ++j)
            {
                v.elements()[j] = 0;
            }
            v.elements()[i] = 1;
            for (int j = i + 1; j < m; ++j)
            {
                v.elements()[j] = blasElement(j, i, A, m, n);
            }
            // Mult to Q
            ret.Q = (DenseMatrix) ret.Q.times(I.minus(
                v.outerProduct(v).scale(tau[i])));
        }

        return ret;
    }

    /**
     * Simple helper that computes the offset in a 1-d, dense, column-major
     * (BLAS-order) matrix representation for the input i, j, m, and n matrix A.
     *
     * @param i The row index (0-based)
     * @param j The column index (0-based)
     * @param A The 1-d, dense, column-major (BLAS-order) matrix representation
     * @param m The number of rows in A
     * @param n The number of columns in A
     * @return The value stored in A(i, j)
     */
    private double blasElement(int i,
        int j,
        double[] A,
        int m,
        int n)
    {
        return A[i + m * j];
    }

    /**
     * Solves Rx = QtransB requiring that R be upper triangular and square.
     * Solves by back-subsititution from the bottom-most corner of the
     * upper-triangular matrix.
     *
     * @param R The upper triangular right-side matrix
     * @param QtransB The Q-transformed right-side vector
     * @return The x from Rx = Qtrans * b.
     * @throws IllegalArgumentException if R is not square or if R is not
     * upper-triangular (has values below the diagonal).
     * @throws UnsupportedOperationException if any diagonal element of R is
     * zero (as it does not span the space).
     */
    private static Vector upperTriangularSolve(Matrix R,
        Vector QtransB)
    {
        // This should be tested in the solve methods, this is here just to
        // make sure no one else calls this w/o checking
        if (!R.isSquare())
        {
            throw new IllegalArgumentException("Called with a non-square "
                + "matrix.");
        }

        // Test bottom triangle is zero
        // And test that all diagonals are non-zero (else you can't solve)
        for (int i = 0; i < R.getNumRows(); ++i)
        {
            // diagonals
            if (R.getElement(i, i) == 0)
            {
                throw new UnsupportedOperationException("Can't invert matrix "
                    + "because it does not span the columns");
            }
            // lower triangle
            for (int j = 0; j < i; ++j)
            {
                if (R.getElement(i, j) != 0)
                {
                    throw new IllegalArgumentException("upperTriangleSolve "
                        + "passed a non-upper-triangle matrix");
                }
            }
        }

        DenseVector ret = new DenseVector(R.getNumColumns(), true);
        // Start from the bottom of the triangle
        for (int i = R.getNumColumns() - 1; i >= 0; --i)
        {
            // Start w/ the value in B
            double v = QtransB.getElement(i);

            // Back substitute all solved parts in
            for (int j = i + 1; j < R.getNumColumns(); ++j)
            {
                v -= R.getElement(i, j) * ret.elements()[j];
            }

            // Solve with the diagonal element
            ret.elements()[i] = (v / R.getElement(i, i));
        }

        return ret;
    }

    @Override
    final public Matrix solve(Matrix B)
    {
        checkSolveDimensions(B);
        if (!isSquare())
        {
            throw new IllegalStateException("Solve only works on square "
                + "matrices (this is " + getNumRows() + " x " + getNumColumns());
        }

        QR qr = qrDecompose();
        // I'll only use it as the transpose
        qr.Q = qr.Q.transpose();

        DenseMatrix X = new DenseMatrix(getNumColumns(), B.getNumColumns());
        for (int i = 0; i < B.getNumColumns(); ++i)
        {
            X.setColumn(i,
                upperTriangularSolve(qr.R, qr.Q.times(B.getColumn(i))));
        }

        return X;
    }

    @Override
    final public Vector solve(Vector b)
    {
        checkSolveDimensions(b);
        if (!isSquare())
        {
            throw new IllegalStateException("Solve only works on square "
                + "matrices (this is " + getNumRows() + " x " + getNumColumns());
        }

        QR qr = qrDecompose();

        return upperTriangularSolve(qr.R, qr.Q.transpose().times(b));
    }

    @Override
    final public void identity()
    {
        // NOTE: This is a bad idea as you're storing a diagonal matrix in a
        // dense data structure
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                if (i == j)
                {
                    rows[i].elements()[j] = 1;
                }
                else
                {
                    rows[i].elements()[j] = 0;
                }
            }
        }
    }

    @Override
    final public Vector getColumn(int columnIndex)
    {
        if (columnIndex < 0 || columnIndex >= getNumColumns())
        {
            throw new ArrayIndexOutOfBoundsException("Input column index ("
                + columnIndex + ") is not within this " + getNumRows() + "x"
                + getNumColumns() + " matrix");
        }
        DenseVector ret = new DenseVector(getNumRows(), true);
        for (int i = 0; i < getNumRows(); ++i)
        {
            ret.elements()[i] = rows[i].getElement(columnIndex);
        }

        return ret;
    }

    @Override
    final public Vector getRow(int rowIndex)
    {
        if (rowIndex < 0 || rowIndex >= getNumRows())
        {
            throw new ArrayIndexOutOfBoundsException("Input row index ("
                + rowIndex + ") is not within this " + getNumRows() + "x"
                + getNumColumns() + " matrix");
        }

        return new DenseVector(rows[rowIndex]);
    }

    @Override
    final public void convertFromVector(Vector v)
    {
        v.assertDimensionalityEquals(this.getNumRows() * this.getNumColumns());

        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                rows[i].elements()[j] = v.getElement(i + j
                    * getNumRows());
            }
        }
    }
    
    @Override
    final public Vector convertToVector()
    {
        DenseVector ret = new DenseVector(getNumRows() * getNumColumns(), true);

        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                ret.elements()[i + j * getNumRows()] = rows[i].elements()[j];
            }
        }

        return ret;
    }

    @Override
    public final Vector preTimes(SparseVector vector)
    {
        vector.assertDimensionalityEquals(this.getNumRows());
        DenseVector ret = new DenseVector(getNumColumns(), true);
        vector.compress();
        int[] locs = vector.getLocs();
        double[] vals = vector.getVals();
        for (int i = 0; i < getNumColumns(); ++i)
        {
            double entry = 0;
            for (int j = 0; j < locs.length; ++j)
            {
                entry += vals[j] * rows[locs[j]].getElement(i);
            }
            ret.setElement(i, entry);
        }

        return ret;
    }

    @Override
    public final Vector preTimes(DenseVector vector)
    {
        vector.assertDimensionalityEquals(this.getNumRows());
        DenseVector ret = new DenseVector(getNumColumns(), true);
        for (int i = 0; i < getNumColumns(); ++i)
        {
            double entry = 0;
            for (int j = 0; j < getNumRows(); ++j)
            {
                entry += vector.getElement(j) * rows[j].getElement(i);
            }
            ret.setElement(i, entry);
        }

        return ret;
    }

    /**
     * Package-private helper that returns the dense vector that stores row i.
     * This is intended for speeding up various computations by SparseMatrix and
     * DiagonalMatrix.
     *
     * @param i The row index -- only checked by Java's array indexing.
     * @return The DenseVector stored at i
     */
    final DenseVector row(int i)
    {
        return rows[i];
    }

    /**
     * Package-private helper that sets the input vector to the specified vector
     * without any checks. This is to speed up matrix operations in DenseMatrix
     * and SparseMatrix.
     *
     * @param i The row index -- only checked by Java's array indexing.
     * @param v The DenseVector to store at i -- not checked at all
     */
    final void setRow(int i,
        DenseVector v)
    {
        rows[i] = v;
    }

    /**
     * Simple method that computes the number of non-zero elements stored in
     * this.
     *
     * @return the number of elements in this that are non-zero.
     */
    final public int numNonZero()
    {
        int nnz = 0;
        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                nnz += (rows[i].getElement(j) == 0) ? 0 : 1;
            }
        }

        return nnz;
    }

    /**
     * Returns the percentage of this that is non-zero elements
     *
     * @return the percentage of this that is non-zero elements
     */
    final public double percentNonzero()
    {
        return ((double) numNonZero()) / ((double) (getNumRows()
            * getNumColumns()));
    }

    @Override
    public MatrixFactory<?> getMatrixFactory()
    {
        return DenseMatrixFactoryOptimized.INSTANCE;
    }

}
