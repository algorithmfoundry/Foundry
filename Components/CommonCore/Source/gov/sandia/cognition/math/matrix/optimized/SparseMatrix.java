
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.Vector;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A sparse matrix implementation. This stores the data in two formats: The
 * first is as an array of sparse vectors for each row. This format is easy to
 * modify (see the set* methods), but slow to operate on due to each element
 * being dispersed to different locations in memory. The second is the
 * compressed Yale format (see
 * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This format densely
 * packs the values into arrays of double -- permitting fast computation, but
 * difficult and costly modification. This switches between the formats as
 * necessary (to Yale when multiplying, to sparse vector when altering), so it
 * is recommended that computation calls not be interleaved with modification
 * calls unnecessarily.
 *
 * @author Jeremy D. Wendt
 */
@PublicationReference(author = "Wikipedia",
    title = "Sparse Matrix / Yale format",
    type = PublicationType.WebPage,
    year = 2013,
    url = "http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format")
public class SparseMatrix
    extends BaseMatrix
{

    /**
     * The number of rows in the matrix. This is stored explicitly here (vs.
     * using the implicit storage in the sparse rows or compressed Yale format)
     * because of the fact that this switches between compressed Yale and sparse
     * row formats at runtime.
     */
    private int numRows;

    /**
     * The number of columns in the matrix. This is stored explicitly here (vs.
     * using the implicit storage in the sparse rows) because of the fact that
     * this switches between compressed Yale (which doesn't store the number of
     * columns anywhere) and sparse row formats at runtime.
     */
    private int numCols;

    /**
     * The sparse vector representation of the matrix. Can be out-of-sync with
     * latest values (or null) depending on if the compressed Yale format is the
     * current representation. See the isCompresed method to tell which format
     * is current.
     *
     */
    private SparseVector[] rows;

    /**
     * Part of the compressed Yale format. Generally only stores the non-zero
     * elements of the matrix (to optimize some of the methods herein, some zero
     * elements may be in vals). Will be null when the matrix is not compressed.
     */
    protected double[] vals;

    /**
     * Part of the compressed Yale format. Stores the first index into vals for
     * each row in the matrix. Has length of numRows + 1. Will be null when the
     * matrix is not compressed.
     */
    protected int[] firstIdxsForRows;

    /**
     * Part of the compressed Yale format. Has the same length of vals. This
     * specifies which column each element of vals is in. Will be null when the
     * matrix is not compressed.
     */
    protected int[] colIdxs;

    /**
     * This method is provided so that the calling programmer can explicitly
     * declare when a matrix should be compressed to the compressed Yale format.
     * Note that the method will be automatically compressed and decompressed
     * when various methods are called (e.g., decompressed for set* methods,
     * compressed for times, dotTimes, etc.).
     */
    final public void compress()
    {
        if (isCompressed())
        {
            return;
        }

        int numNonZero = 0;
        for (int i = 0; i < getNumRows(); ++i)
        {
            rows[i].compress();
            numNonZero += rows[i].numNonZero();
        }
        vals = new double[numNonZero];
        firstIdxsForRows = new int[getNumRows() + 1];
        colIdxs = new int[numNonZero];
        int curIdx = 0;
        for (int i = 0; i < getNumRows(); ++i)
        {
            firstIdxsForRows[i] = curIdx;
            for (int j = 0; j < rows[i].getLocs().length; ++j)
            {
                vals[curIdx] = rows[i].getVals()[j];
                colIdxs[curIdx] = rows[i].getLocs()[j];
                ++curIdx;
            }
            rows[i].clear();
        }
        firstIdxsForRows[getNumRows()] = curIdx;
    }

    /**
     * This method is provided so that the calling programmer can explicitly
     * declare when a matrix should be decompressed from the compressed Yale
     * format. Note that the method will be automatically compressed and
     * decompressed when various methods are called (e.g., decompressed for set*
     * methods, compressed for times, dotTimes, etc.).
     */
    final public void decompress()
    {
        if (!isCompressed())
        {
            return;
        }

        for (int i = 0; i < getNumRows(); ++i)
        {
            rows[i].clear();
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                rows[i].setElement(colIdxs[j], vals[j]);
            }
        }
        vals = null;
        firstIdxsForRows = colIdxs = null;
    }

    /**
     * This method tests if the matrix is currently compressed to the compressed
     * Yale format. Please note that the matrix gets compressed and decompressed
     * as a side effect of various operations (including decompressed by set*
     * calls and compressed by times, etc.). Therefore, don't be surprised if
     * you call decompress, call some other methods and then find that it's
     * compressed.
     *
     * @return true if this is compressed to the compressed Yale format, else
     * false.
     */
    final public boolean isCompressed()
    {
        return (vals != null) && (firstIdxsForRows != null) && (colIdxs != null);
    }

    /**
     * Package-private method that returns the compressed Yale-format values.
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @return the compressed Yale-format values
     */
    final double[] getVals()
    {
        if (!isCompressed())
        {
            compress();
        }
        return vals;
    }

    /**
     * Package-private method that returns the compressed Yale-format column
     * indices.
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @return the compressed Yale-format column indices
     */
    final int[] getColIdxs()
    {
        if (!isCompressed())
        {
            compress();
        }
        return colIdxs;
    }

    /**
     * Package-private method that returns the compressed Yale-format first
     * indices for each row.
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @return the compressed Yale-format first indices for each row
     */
    final int[] getFirstInRows()
    {
        if (!isCompressed())
        {
            compress();
        }
        return firstIdxsForRows;
    }

    /**
     * Creates a new sparse matrix with the specified number of rows and
     * columns. All values are implicitly set to zero.
     *
     * NOTE: Upon completion this is in the sparse vector.
     *
     * @param numRows The number of rows in the matrix
     * @param numCols The number of columns in the matrix
     */
    public SparseMatrix(int numRows,
        int numCols)
    {
        this.numCols = numCols;
        this.numRows = numRows;
        rows = new SparseVector[numRows];
        for (int i = 0; i < numRows; ++i)
        {
            rows[i] = new SparseVector(numCols);
        }
        vals = null;
        firstIdxsForRows = colIdxs = null;
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as m
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the same format as m.
     *
     * @param m The sparse matrix to copy
     */
    public SparseMatrix(SparseMatrix m)
    {
        this.numCols = m.getNumColumns();
        this.numRows = m.getNumRows();
        rows = new SparseVector[m.rows.length];
        if (m.isCompressed())
        {
            for (int i = 0; i < getNumRows(); ++i)
            {
                rows[i] = new SparseVector(m.numCols);
            }
            vals = Arrays.copyOf(m.vals, m.vals.length);
            firstIdxsForRows = Arrays.copyOf(m.firstIdxsForRows,
                m.firstIdxsForRows.length);
            colIdxs = Arrays.copyOf(m.colIdxs, m.colIdxs.length);
        }
        else
        {
            for (int i = 0; i < getNumRows(); ++i)
            {
                rows[i] = new SparseVector(m.rows[i]);
            }
            vals = null;
            firstIdxsForRows = colIdxs = null;
        }
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as d
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @param d The dense matrix to copy
     */
    public SparseMatrix(DenseMatrix d)
    {
        int nnz = d.numNonZero();
        this.numCols = d.getNumColumns();
        this.numRows = d.getNumRows();
        rows = new SparseVector[numRows];
        vals = new double[nnz];
        firstIdxsForRows = new int[numRows + 1];
        colIdxs = new int[nnz];
        int idx = 0;
        for (int i = 0; i < numRows; ++i)
        {
            rows[i] = new SparseVector(numCols);
            firstIdxsForRows[i] = idx;
            for (int j = 0; j < numCols; ++j)
            {
                double val = d.row(i).elements()[j];
                if (val != 0)
                {
                    vals[idx] = val;
                    colIdxs[idx] = j;
                    ++idx;
                }
            }
        }
        firstIdxsForRows[numRows] = idx;
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as d
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @param d The diagonal matrix to copy
     */
    public SparseMatrix(DiagonalMatrix d)
    {
        int nnz = 0;
        for (int i = 0; i < d.getNumRows(); ++i)
        {
            if (d.getElement(i, i) != 0)
            {
                ++nnz;
            }
        }
        this.numCols = d.getNumColumns();
        this.numRows = d.getNumRows();
        rows = new SparseVector[numRows];
        vals = new double[nnz];
        firstIdxsForRows = new int[numRows + 1];
        colIdxs = new int[nnz];
        int idx = 0;
        for (int i = 0; i < numRows; ++i)
        {
            rows[i] = new SparseVector(numCols);
            firstIdxsForRows[i] = idx;
            double val = d.getElement(i, i);
            if (val != 0)
            {
                vals[idx] = val;
                colIdxs[idx] = i;
                ++idx;
            }
        }

        firstIdxsForRows[numRows] = idx;
    }

    /**
     * Package-private helper that creates a completely empty matrix (neither
     * format initialized). It is assumed that the calling function will
     * immediately fill the appropriate values.
     *
     * @param numRows The number of rows in the matrix
     * @param numCols The number of columns in the matrix
     * @param unused Only present to differentiate from the other full
     * constructor
     */
    SparseMatrix(int numRows,
        int numCols,
        boolean unused)
    {
        this.numCols = numCols;
        this.numRows = numRows;
        rows = new SparseVector[numRows];
        vals = null;
        firstIdxsForRows = colIdxs = null;
        // Don't initialize the rows' values
    }

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected SparseMatrix()
    {
        // NOTE: This initialize to bad values or nothing
        numCols = numRows = 0;
    }

    /**
     * @see BaseMatrix#clone()
     *
     * NOTE: This does not affect this's format. The cloned matrix is in the
     * same format as this.
     */
    @Override
    public Matrix clone()
    {
        return new SparseMatrix(this);
    }

    /**
     * @see Matrix#isSparse()
     *
     * NOTE: This does not affect this's format.
     */
    @Override
    public boolean isSparse()
    {
        return true;
    }

    /**
     * This enum allows the getNumNonZeroWhenCombinedWith method to count the
     * number of non-zeros depending on if an add/subtract (OR) or multiply
     * (AND) operation is being performed.
     */
    static enum Combiner
    {

        /**
         * For operations that are 0 unless both operands are non-zero (mult)
         */
        AND,
        /**
         * For operations that are 0 only if both operands are zero (add,
         * subtract)
         */
        OR;

    };

    /**
     * Helper method that calculates the number of non-zero entries in the
     * result of a computation between two sparse matrices. Notice that the math
     * is not actually performed, so zeros caused by the operation (say A - A)
     * are not counted.
     *
     * NOTE: This method assumes this is in the compressed Yale format on start
     * and does not change the format.
     *
     * @param otherColIds The other sparse matrix's compressed Yale format
     * column ids
     * @param otherFirstInRows The other matrix's compressed Yale format first
     * in rows
     * @param combiner How zeros are formed by the type of operation to be
     * performed (AND = times, OR = plus/minus)
     * @return The number of non-zero elements expected by the operation and the
     * location of the elements.
     */
    private int getNumNonZeroWhenCombinedWith(int[] otherColIds,
        int[] otherFirstInRows,
        Combiner combiner)
    {
        int nnz = 0;
        int myidx, otheridx;
        // This assumes none of the entries combine together to 0
        for (int i = 0; i < getNumRows(); ++i)
        {
            // Counters for me and other on this row
            myidx = firstIdxsForRows[i];
            otheridx = otherFirstInRows[i];
            // While we're both on this row
            while (myidx < firstIdxsForRows[i + 1] && otheridx
                < otherFirstInRows[i + 1])
            {
                // If we share the spot, count it as one and advance both
                if (colIdxs[myidx] == otherColIds[otheridx])
                {
                    ++nnz;
                    ++myidx;
                    ++otheridx;
                }
                // Otherwise if I'm before other on this row, count me and
                // advance me
                else if (colIdxs[myidx] < otherColIds[otheridx])
                {
                    if (combiner == Combiner.OR)
                    {
                        ++nnz;
                    }
                    ++myidx;
                }
                // Otherwise he's first, count him and advance
                else
                {
                    if (combiner == Combiner.OR)
                    {
                        ++nnz;
                    }
                    ++otheridx;
                }
            }
            if (combiner == Combiner.OR)
            {
                // If I've made it here, one of us could still be on the row while
                // the other isn't.  Count each non-zero and advance
                while (myidx < firstIdxsForRows[i + 1])
                {
                    ++nnz;
                    ++myidx;
                }
                while (otheridx < otherFirstInRows[i + 1])
                {
                    ++nnz;
                    ++otheridx;
                }
            }
        }

        return nnz;
    }

    /**
     * This helper combines the logic for all addition-style operations (so
     * minus becomes plusEqualsScaled with a scaleOtherBy of -1).
     *
     * NOTE: This method assumes this and other are in the compressed Yale
     * format, and they remain in that state after completion.
     *
     * @param colIdxsOut [OUT] The resulting column indices
     * @param firstInRowsOut [OUT] The resulting first idx in rows values
     * @param valsOut [OUT] The resulting values
     * @param other The other matrix
     * @param scaleOtherBy The scalar to multiply the other's values by before
     * summing.
     */
    private void plusEqualsScaled(int[] colIdxsOut,
        int[] firstInRowsOut,
        double[] valsOut,
        SparseMatrix other,
        double scaleOtherBy)
    {
        int newidx = 0;
        int myctr, otherctr;
        // For all rows
        for (int i = 0; i < getNumRows(); ++i)
        {
            // The first index for the row is the current idx
            firstInRowsOut[i] = newidx;

            // Now set up both of us for this row
            myctr = firstIdxsForRows[i];
            otherctr = other.firstIdxsForRows[i];
            // As long as we're both still on the row...
            while (myctr < firstIdxsForRows[i + 1] && otherctr
                < other.firstIdxsForRows[i + 1])
            {
                // Three cases:
                // 1) We're both at the same point, store our sum
                if (colIdxs[myctr] == other.colIdxs[otherctr])
                {
                    valsOut[newidx] = vals[myctr] + (other.vals[otherctr]
                        * scaleOtherBy);
                    colIdxsOut[newidx] = colIdxs[myctr];
                    ++newidx;
                    ++myctr;
                    ++otherctr;
                }
                // 2) Me first, store me
                else if (colIdxs[myctr] < other.colIdxs[otherctr])
                {
                    valsOut[newidx] = vals[myctr];
                    colIdxsOut[newidx] = colIdxs[myctr];
                    ++newidx;
                    ++myctr;
                }
                // 3) Him first, store him
                else
                {
                    valsOut[newidx] = (other.vals[otherctr] * scaleOtherBy);
                    colIdxsOut[newidx] = other.colIdxs[otherctr];
                    ++newidx;
                    ++otherctr;
                }
            }
            // One of these while loops could still run
            while (myctr < firstIdxsForRows[i + 1])
            {
                valsOut[newidx] = vals[myctr];
                colIdxsOut[newidx] = colIdxs[myctr];
                ++newidx;
                ++myctr;
            }
            while (otherctr < other.firstIdxsForRows[i + 1])
            {
                valsOut[newidx] = (other.vals[otherctr] * scaleOtherBy);
                colIdxsOut[newidx] = other.colIdxs[otherctr];
                ++newidx;
                ++otherctr;
            }
        }
        firstInRowsOut[numRows] = newidx;
    }

    /**
     * @see
     * BaseMatrix#_scaledPlusEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    void _scaledPlusEquals(SparseMatrix other,
        double scaleFactor)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!other.isCompressed())
        {
            other.compress();
        }
        // First get the number of non-zero entries in the output
        int nnzAfter = getNumNonZeroWhenCombinedWith(other.colIdxs,
            other.firstIdxsForRows, Combiner.OR);

        // Now create the output matrix's values (init as empty but to nnz size)
        double[] newVals = new double[nnzAfter];
        int[] newColIdxs = new int[nnzAfter];
        int[] newFirstIdxsForRows = new int[getNumRows() + 1];

        // Now calculate the result of summing thes matrices
        plusEqualsScaled(newColIdxs, newFirstIdxsForRows, newVals, other,
            scaleFactor);

        // Now store the new values in me
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_scaledPlusEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    void _scaledPlusEquals(DenseMatrix other,
        double scaleFactor)
    {
        // Such a bad idea!  This will store dense values in a sparse matrix
        // Take care of the null case -- keeps from an out-of-bounds exception below
        if (getNumRows() == 0)
        {
            return;
        }
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: I'm doing something sneaky here.  I'm storing the old values
        // in the compressed version while storing the updated rows in the
        // decompressed rows.
        int rowNum = 0;
        SparseVector row = new SparseVector(other.row(rowNum));
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                rows[rowNum] = row;
                ++rowNum;
                row = new SparseVector(other.row(rowNum));
                row.scaleEquals(scaleFactor);
            }
            row.setElement(colIdxs[i], row.getElement(colIdxs[i]) + vals[i]);
        }
        while (rowNum < getNumRows())
        {
            rows[rowNum] = row;
            ++rowNum;
            if (rowNum >= getNumRows())
            {
                break;
            }
            row = new SparseVector(other.row(rowNum));
            row.scaleEquals(scaleFactor);
        }
        vals = null;
        colIdxs = null;
        firstIdxsForRows = null;
        compress();
    }

    /**
     * @see
     * BaseMatrix#_plusEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    void _scaledPlusEquals(DiagonalMatrix other,
        double scaleFactor)
    {
        if (!isCompressed())
        {
            compress();
        }
        // Count the number of new values to add.
        int nnz = getNumNonZeroWhenCombinedWith(getZeroToNMinusOneArray(
            other.getNumRows()), getZeroToNMinusOneArray(other.getNumRows() + 1),
            Combiner.OR);

        // Now start up the values for the sum matrix
        double[] newVals = new double[nnz];
        int[] newFirstIdxsForRows = new int[firstIdxsForRows.length];
        int[] newColIdxs = new int[nnz];

        // Now do the logic for adding
        plusEqualsScaled(newColIdxs, newFirstIdxsForRows, newVals, other, 1);

        // Now store the new values in me
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_plusEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _plusEquals(SparseMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!other.isCompressed())
        {
            other.compress();
        }
        // First get the number of non-zero entries in the output
        int nnzAfter = getNumNonZeroWhenCombinedWith(other.colIdxs,
            other.firstIdxsForRows, Combiner.OR);

        // Now create the output matrix's values (init as empty but to nnz size)
        double[] newVals = new double[nnzAfter];
        int[] newColIdxs = new int[nnzAfter];
        int[] newFirstIdxsForRows = new int[getNumRows() + 1];

        // Now calculate the result of summing thes matrices
        plusEqualsScaled(newColIdxs, newFirstIdxsForRows, newVals, other, 1);

        // Now store the new values in me
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_plusEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _plusEquals(DenseMatrix other)
    {
        // Such a bad idea!  This will store dense values in a sparse matrix
        // Take care of the null case -- keeps from an out-of-bounds exception below
        if (getNumRows() == 0)
        {
            return;
        }
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: I'm doing something sneaky here.  I'm storing the old values
        // in the compressed version while storing the updated rows in the
        // decompressed rows.
        int rowNum = 0;
        SparseVector row = new SparseVector(other.row(rowNum));
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                rows[rowNum] = row;
                ++rowNum;
                row = new SparseVector(other.row(rowNum));
            }
            row.setElement(colIdxs[i], row.getElement(colIdxs[i]) + vals[i]);
        }
        while (rowNum < getNumRows())
        {
            rows[rowNum] = row;
            ++rowNum;
            if (rowNum >= getNumRows())
            {
                break;
            }
            row = new SparseVector(other.row(rowNum));
        }
        vals = null;
        colIdxs = null;
        firstIdxsForRows = null;
        compress();
    }

    /**
     * Private helper that generates an array of integers from 0 to n-1
     * (inclusive).
     *
     * NOTE: Does not affect internal storage format.
     *
     * @param n The length of the array desired
     * @return The above-described array
     */
    private static int[] getZeroToNMinusOneArray(int n)
    {
        int[] ret = new int[n];
        for (int i = 0; i < n; ++i)
        {
            ret[i] = i;
        }

        return ret;
    }

    /**
     * Private helper for adding and subtracting from a diagonal matrix.
     *
     * This method assumes this is in compressed Yale format and does not alter
     * that.
     *
     * @param colIdxsOut [OUT] The compressed Yale-format column indices for the
     * output
     * @param firstInRowsOut [OUT] The compressed Yale-format first index into
     * vals for all rows
     * @param valsOut [OUT] The compressed Yale-format values
     * @param other The diagonal matrix to scale then sum with this
     * @param scaleOtherBy The amount to scale other by (usually +/- 1)
     */
    private void plusEqualsScaled(int[] colIdxsOut,
        int[] firstInRowsOut,
        double[] valsOut,
        DiagonalMatrix other,
        double scaleOtherBy)
    {
        int rowNum = 0;
        int idx = 0;
        boolean addedOnRow = false;
        firstInRowsOut[rowNum] = idx;
        for (int i = 0; i < vals.length; ++i)
        {
            // This skips all-zero rows and advances this row when necessary
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                // But we have to add an element for the diagonal element in 
                // the other matrix
                if (!addedOnRow)
                {
                    valsOut[idx] = (other.getElement(rowNum, rowNum)
                        * scaleOtherBy);
                    colIdxsOut[idx] = rowNum;
                    ++idx;
                }
                // Now advance the row information
                ++rowNum;
                addedOnRow = false;
                firstInRowsOut[rowNum] = idx;
            }
            // If we're on the diagonal
            if (rowNum == colIdxs[i])
            {
                // Add the current value with the other guy's diagoal
                valsOut[idx] = (other.getElement(rowNum, rowNum) * scaleOtherBy)
                    + vals[i];
                addedOnRow = true;
            }
            // Otherwise, if I'm past the diagonal and haven't added the other
            // guy's value
            else if (colIdxs[i] > rowNum && !addedOnRow)
            {
                // Add the diagonal
                colIdxsOut[idx] = rowNum;
                valsOut[idx] = (other.getElement(rowNum, rowNum) * scaleOtherBy);
                addedOnRow = true;
                // Then add the current value
                ++idx;
                valsOut[idx] = vals[i];
            }
            // Otherwise, I'm before the diagonal or I've already added it
            else
            {
                // So add this value
                valsOut[idx] = vals[i];
            }
            colIdxsOut[idx] = colIdxs[i];
            ++idx;
        }
        // Now add elements for diagonals past the last non-zero row in this
        while (rowNum < getNumRows())
        {
            if (!addedOnRow)
            {
                valsOut[idx] = (other.getElement(rowNum, rowNum) * scaleOtherBy);
                colIdxsOut[idx] = rowNum;
                ++idx;
            }
            ++rowNum;
            firstInRowsOut[rowNum] = idx;
        }
        firstInRowsOut[rowNum] = idx;
    }

    /**
     * @see
     * BaseMatrix#_plusEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _plusEquals(DiagonalMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        // Count the number of new values to add.
        int nnz = getNumNonZeroWhenCombinedWith(getZeroToNMinusOneArray(
            other.getNumRows()), getZeroToNMinusOneArray(other.getNumRows() + 1),
            Combiner.OR);

        // Now start up the values for the sum matrix
        double[] newVals = new double[nnz];
        int[] newFirstIdxsForRows = new int[firstIdxsForRows.length];
        int[] newColIdxs = new int[nnz];

        // Now do the logic for adding
        plusEqualsScaled(newColIdxs, newFirstIdxsForRows, newVals, other, 1);

        // Now store the new values in me
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_minusEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     *
     * NOTE: Upon completion this and other are in the compressed Yale format.
     */
    @Override
    final void _minusEquals(SparseMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!other.isCompressed())
        {
            other.compress();
        }

        // Calculate the resulting number of non-zero elements
        int nnzAfter = getNumNonZeroWhenCombinedWith(other.colIdxs,
            other.firstIdxsForRows, Combiner.OR);

        // Create the new locations for the results
        double[] newVals = new double[nnzAfter];
        int[] newColIdxs = new int[nnzAfter];
        int[] newFirstIdxsForRows = new int[getNumRows() + 1];

        // Actually perform the subtraction
        plusEqualsScaled(newColIdxs, newFirstIdxsForRows, newVals, other, -1);

        // Store the results
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_minusEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _minusEquals(DenseMatrix other)
    {
        // Such a bad idea!  This will store dense values in a sparse matrix
        // Take care of the null case -- keeps from an out-of-bounds exception below
        if (getNumRows() == 0)
        {
            return;
        }
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: I'm storing the old values in the compressed locations and the
        // result in the sparse vector rows.
        int rowNum = 0;
        SparseVector row = new SparseVector(other.row(rowNum));
        row.negativeEquals();
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                rows[rowNum] = row;
                ++rowNum;
                row = new SparseVector(other.row(rowNum));
                row.negativeEquals();
            }
            row.setElement(colIdxs[i], row.getElement(colIdxs[i]) + vals[i]);
        }
        while (rowNum < getNumRows())
        {
            rows[rowNum] = row;
            ++rowNum;
            if (rowNum >= getNumRows())
            {
                break;
            }
            row = new SparseVector(other.row(rowNum));
            row.negativeEquals();
        }
        vals = null;
        firstIdxsForRows = colIdxs = null;
        compress();
    }

    /**
     * @see
     * BaseMatrix#_minusEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _minusEquals(DiagonalMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        // Count the number of new values to add.
        int nnz = getNumNonZeroWhenCombinedWith(getZeroToNMinusOneArray(
            other.getNumRows()), getZeroToNMinusOneArray(other.getNumRows() + 1),
            Combiner.OR);

        // Initialize the new matrix storage
        double[] newVals = new double[nnz];
        int[] newFirstIdxsForRows = new int[firstIdxsForRows.length];
        int[] newColIdxs = new int[nnz];

        // Actually do the computation
        plusEqualsScaled(newColIdxs, newFirstIdxsForRows, newVals, other, -1);

        // Store the results
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     *
     * NOTE: Upon completion this and other are in the compressed Yale format.
     */
    @Override
    final void _dotTimesEquals(SparseMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!other.isCompressed())
        {
            other.compress();
        }

        // Count the number of new values to add.
        int nnz = getNumNonZeroWhenCombinedWith(other.colIdxs,
            other.firstIdxsForRows, Combiner.AND);

        // Initialize the new matrix storage
        double[] newVals = new double[nnz];
        int[] newFirstIdxsForRows = new int[firstIdxsForRows.length];
        int[] newColIdxs = new int[nnz];

        // Do the computation
        int newidx = 0;
        int myctr, otherctr;
        // For all rows
        for (int i = 0; i < getNumRows(); ++i)
        {
            // The first index for the row is the current idx
            newFirstIdxsForRows[i] = newidx;

            // Now set up both of us for this row
            myctr = firstIdxsForRows[i];
            otherctr = other.firstIdxsForRows[i];
            // As long as we're both still on the row...
            while (myctr < firstIdxsForRows[i + 1] && otherctr
                < other.firstIdxsForRows[i + 1])
            {
                // Three cases:
                // 1) We're both at the same point, store our product
                if (colIdxs[myctr] == other.colIdxs[otherctr])
                {
                    newVals[newidx] = vals[myctr] * other.vals[otherctr];
                    newColIdxs[newidx] = colIdxs[myctr];
                    ++newidx;
                    ++myctr;
                    ++otherctr;
                }
                // 2) Me first, advance me
                else if (colIdxs[myctr] < other.colIdxs[otherctr])
                {
                    ++myctr;
                }
                // 3) Him first, advance him
                else
                {
                    ++otherctr;
                }
            }
        }
        newFirstIdxsForRows[numRows] = newidx;

        // Now store the results
        vals = newVals;
        firstIdxsForRows = newFirstIdxsForRows;
        colIdxs = newColIdxs;
    }

    /**
     * @see
     * BaseMatrix#_dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _dotTimesEquals(DenseMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        // The shortcut I'll take here is that few of the dense matrix's values
        // are zero where I'm not
        int rownum = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rownum + 1])
            {
                ++rownum;
            }
            vals[i] *= other.getElement(rownum, colIdxs[i]);
        }
    }

    /**
     * @see
     * BaseMatrix#_dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final void _dotTimesEquals(DiagonalMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        // Count the number of new values to add.
        int nnz = getNumNonZeroWhenCombinedWith(getZeroToNMinusOneArray(
            other.getNumRows()), getZeroToNMinusOneArray(other.getNumRows() + 1),
            Combiner.AND);

        // Initialize the new matrix storage
        double[] newVals = new double[nnz];
        int[] newFirstIdxsForRows = new int[firstIdxsForRows.length];
        int[] newColIdxs = new int[nnz];

        // Actually do the computation
        int rowNum = 0;
        int idx = 0;
        boolean addedOnRow = false;
        newFirstIdxsForRows[rowNum] = idx;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                ++rowNum;
                newFirstIdxsForRows[rowNum] = idx;
            }
            if (rowNum == colIdxs[i])
            {
                newVals[idx] = vals[i] * other.getElement(rowNum, rowNum);
                newColIdxs[idx] = rowNum;
                ++idx;
            }
        }
        newFirstIdxsForRows[rowNum] = idx;

        // Store the results
        vals = newVals;
        colIdxs = newColIdxs;
        firstIdxsForRows = newFirstIdxsForRows;
    }

    /**
     * @see
     * BaseMatrix#_times(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     *
     * This returns either a dense or a sparse matrix depending on the
     * sparseness of the resulting multiplication
     *
     * NOTE: Upon completion this and other are in the compressed Yale format.
     */
    @Override
    final Matrix _times(SparseMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!other.isCompressed())
        {
            other.compress();
        }
        DenseMatrix ret = new DenseMatrix(getNumRows(), other.getNumColumns(),
            true);
        for (int i = 0; i < getNumRows(); ++i)
        {
            DenseVector row = new DenseVector(other.getNumColumns());
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                for (int k = other.firstIdxsForRows[colIdxs[j]]; k
                    < other.firstIdxsForRows[colIdxs[j] + 1]; ++k)
                {
                    row.elements()[other.colIdxs[k]] += other.vals[k] * vals[j];
                }
            }
            ret.setRow(i, row);
        }

        if (ret.percentNonzero() < Constants.SPARSE_TO_DENSE_THRESHOLD)
        {
            return new SparseMatrix(ret);
        }
        else
        {
            return ret;
        }
    }

    /**
     * @see
     * BaseMatrix#_times(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final Matrix _times(DenseMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        DenseMatrix ret = new DenseMatrix(getNumRows(), other.getNumColumns());
        int curRow = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[curRow + 1])
            {
                ++curRow;
            }
            for (int j = 0; j < other.getNumColumns(); ++j)
            {
                ret.row(curRow).elements()[j] += vals[i]
                    * other.row(colIdxs[i]).elements()[j];
            }
        }

        return ret;
    }

    /**
     * @see
     * BaseMatrix#_times(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final Matrix _times(DiagonalMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        SparseMatrix ret = new SparseMatrix(getNumRows(), getNumColumns());
        ret.colIdxs = Arrays.copyOf(colIdxs, colIdxs.length);
        ret.firstIdxsForRows = Arrays.copyOf(firstIdxsForRows,
            firstIdxsForRows.length);
        ret.vals = new double[vals.length];
        for (int i = 0; i < vals.length; ++i)
        {
            ret.vals[i] = vals[i] * other.getElement(colIdxs[i], colIdxs[i]);
        }

        return ret;
    }

    /**
     * Package-private helper for the diagonal matrix class. This handles
     * diagonal*sparse calculation (so that sparse logic need not be embedded
     * unnecessarily into the diagonal class).
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @param other The matrix to pre-multiply this by
     * @return The sparse matrix (compressed Yale format) resulting from
     * multiplying other * this.
     */
    final Matrix _preTimes(DiagonalMatrix other)
    {
        if (!isCompressed())
        {
            compress();
        }
        SparseMatrix ret = new SparseMatrix(getNumRows(), getNumColumns());
        ret.colIdxs = Arrays.copyOf(colIdxs, colIdxs.length);
        ret.firstIdxsForRows = Arrays.copyOf(firstIdxsForRows,
            firstIdxsForRows.length);
        ret.vals = new double[vals.length];
        int curRow = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[curRow + 1])
            {
                ++curRow;
            }
            ret.vals[i] = vals[i] * other.getElement(curRow, curRow);
        }

        return ret;
    }

    /**
     * @see
     * BaseMatrix#_times(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    // Not final because this method is overridden by the Parallel implementation
    Vector _times(SparseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!vector.isCompressed())
        {
            vector.compress();
        }

        DenseVector ret = new DenseVector(numRows, true);

        int idx;
        int[] vLocs = vector.getLocs();
        double[] vVals = vector.getVals();
        for (int i = 0; i < numRows; ++i)
        {
            ret.elements()[i] = 0;
            idx = 0;
            // For all non-zero elements on this row of the matrix
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                // First advance past non-zero elements in the vector that are
                // before this one
                while ((idx < vLocs.length) && (vLocs[idx] < colIdxs[j]))
                {
                    ++idx;
                }
                // If you've exceeded the length of the vector, you can stop
                // this row
                if (idx >= vLocs.length)
                {
                    break;
                }
                // If the vector's current location is past this point in the
                // row, then there's no non-zero element at this location
                if (vLocs[idx] > colIdxs[j])
                {
                    continue;
                }
                // You only reach here if they are at the same location
                ret.elements()[i] += vals[j] * vVals[idx];
            }
        }

        return new SparseVector(ret);
    }

    /**
     * @see
     * BaseMatrix#_times(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    // Not final because this method is overridden by the Parallel implementation
    Vector _times(DenseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }

        DenseVector ret = new DenseVector(numRows, true);

        for (int i = 0; i < numRows; ++i)
        {
            ret.elements()[i] = 0;
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                ret.elements()[i] += vals[j] * vector.elements()[colIdxs[j]];
            }
        }

        return ret;
    }

    /**
     * @see BaseMatrix#scaleEquals(double)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final public void scaleEquals(double scaleFactor)
    {
        if (!isCompressed())
        {
            compress();
        }
        for (int i = 0; i < vals.length; ++i)
        {
            vals[i] *= scaleFactor;
        }
    }

    /**
     * @see BaseMatrix#getNumRows()
     *
     * No change to compressed Yale or sparse row format
     */
    @Override
    final public int getNumRows()
    {
        return numRows;
    }

    /**
     * @see BaseMatrix#getNumColumns()
     *
     * No change to compressed Yale or sparse row format
     */
    @Override
    final public int getNumColumns()
    {
        return numCols;
    }

    /**
     * @see Matrix#get(int, int)
     *
     * No change to compressed Yale or sparse row format
     */
    @Override
    public double get(int rowIndex,
        int columnIndex)
    {
        return getElement(rowIndex, columnIndex);
    }

    /**
     * @see BaseMatrix#getElement(int, int)
     *
     * No change to compressed Yale or sparse row format
     */
    @Override
    final public double getElement(int rowIndex,
        int columnIndex)
    {
        if (!isCompressed())
        {
            return rows[rowIndex].getElement(columnIndex);
        }
        else
        {
            if (columnIndex < 0 || columnIndex >= numCols)
            {
                throw new ArrayIndexOutOfBoundsException(
                    "Unable to index column "
                    + columnIndex);
            }
            for (int k = firstIdxsForRows[rowIndex]; k
                < firstIdxsForRows[rowIndex
                + 1]; ++k)
            {
                if (colIdxs[k] == columnIndex)
                {
                    return vals[k];
                }
                else if (colIdxs[k] > columnIndex)
                {
                    return 0;
                }
            }
            return 0;
        }
    }

    /**
     * @see Matrix#set(int, int, double)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @throws ArrayIndexOutOfBoundsException if the indices are out of bounds
     */
    @Override
    public void set(int rowIndex,
        int columnIndex,
        double value)
    {
        setElement(rowIndex, columnIndex, value);
    }

    /**
     * @see BaseMatrix#setElement(int, int, double)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @throws ArrayIndexOutOfBoundsException if the indices are out of bounds
     */
    @Override
    final public void setElement(int rowIndex,
        int columnIndex,
        double value)
    {
        if (isCompressed())
        {
            decompress();
        }
        rows[rowIndex].setElement(columnIndex, value);
    }

    /**
     * @see BaseMatrix#getSubMatrix(int, int, int, int)
     *
     * NOTE: Upon completion, this is in compressed Yale format. Return value is
     * also in compressed Yale format.
     *
     * @throws ArrayIndexOutOfBoundsException if the input indices are outside
     * the acceptable bounds
     */
    @Override
    final public Matrix getSubMatrix(int minRow,
        int maxRow,
        int minColumn,
        int maxColumn)
    {
        checkSubmatrixRange(minRow, maxRow, minColumn, maxColumn);
        if (!isCompressed())
        {
            compress();
        }

        SparseMatrix ret = new SparseMatrix(maxRow - minRow + 1, maxColumn
            - minColumn + 1, true);
        // First, count the number of elements in the new output
        int nnz = 0;
        for (int i = minRow; i <= maxRow; ++i)
        {
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                if (colIdxs[j] >= minColumn && colIdxs[j] <= maxColumn)
                {
                    ++nnz;
                }
            }
        }
        // Initialize the compressed Yale format in ret
        ret.vals = new double[nnz];
        ret.colIdxs = new int[nnz];
        ret.firstIdxsForRows = new int[ret.numRows + 1];
        // Load in the values from this
        int idx = 0;
        for (int i = minRow; i <= maxRow; ++i)
        {
            ret.rows[i - minRow] = new SparseVector(ret.numCols);
            ret.firstIdxsForRows[i - minRow] = idx;
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                if (colIdxs[j] >= minColumn && colIdxs[j] <= maxColumn)
                {
                    ret.vals[idx] = vals[j];
                    ret.colIdxs[idx] = colIdxs[j] - minColumn;
                    ++idx;
                }
            }
        }
        ret.firstIdxsForRows[maxRow - minRow + 1] = idx;

        return ret;
    }

    /**
     * @see BaseMatrix#isSymmetric()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @throws IllegalArgumentException if effectiveZero less than zero.
     */
    @Override
    final public boolean isSymmetric(double effectiveZero)
    {
        testEffZero(effectiveZero);
        if (numRows != numCols)
        {
            return false;
        }

        if (!isCompressed())
        {
            compress();
        }

        int rowNum = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                ++rowNum;
            }
            if (Math.abs(vals[i] - getElement(colIdxs[i], rowNum))
                > effectiveZero)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * @see BaseMatrix#isZero()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final public boolean isZero()
    {
        if (!isCompressed())
        {
            compress();
        }

        // This is a shortcut
        if (vals.length == 0)
        {
            return true;
        }

        // There's a chance of zero values in vals after some operations
        return isZero(0);
    }

    /**
     * @see BaseMatrix#isZero(double)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @throws IllegalArgumentException if effectiveZero less than zero.
     */
    @Override
    final public boolean isZero(double effectiveZero)
    {
        testEffZero(effectiveZero);
        if (!isCompressed())
        {
            compress();
        }

        for (double d : vals)
        {
            if (Math.abs(d) > effectiveZero)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * @see BaseMatrix#transpose()
     *
     * NOTE: Upon completion, this is in compressed Yale format. Returned sparse
     * matrix is in sparse vector format.
     */
    @Override
    final public Matrix transpose()
    {
        SparseMatrix ret = new SparseMatrix(numCols, numRows);
        if (!isCompressed())
        {
            compress();
        }

        int rowNum = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[rowNum + 1])
            {
                ++rowNum;
            }
            ret.setElement(colIdxs[i], rowNum, vals[i]);
        }

        return ret;
    }

    /**
     * @see BaseMatrix#inverse()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * This is implemented by creating a new dense matrix version of this and
     * calling its inverse method -- inverting a sparse matrix is likely to
     * generate a dense matrix anyway. We would recommend using an iterative
     * solver (like Conjugate Gradient).
     */
    @Override
    final public Matrix inverse()
    {
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: Inverting a sparse matrix generally creates a dense matrix
        // anyway.  Sparse matrices make the most sense for iterative solvers
        return (new DenseMatrix(this)).inverse();
    }

    /**
     * @see BaseMatrix#pseudoInverse()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * This is implemented by creating a new dense matrix version of this and
     * calling its pseudoInverse method -- inverting a sparse matrix is likely
     * to generate a dense matrix anyway. We would recommend using an iterative
     * solver (like Conjugate Gradient).
     */
    @Override
    final public Matrix pseudoInverse(double effectiveZero)
    {
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: Pseudo-inverting a sparse matrix generally creates a dense matrix
        // anyway.  Sparse matrices make the most sense for iterative solvers
        return (new DenseMatrix(this)).pseudoInverse(effectiveZero);
    }

    /**
     * @see BaseMatrix#pseudoInverse()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * This is implemented by creating a new dense matrix version of this and
     * calling its logDeterminant method -- It requires factoring the matrix
     * which is going to be memory intense anyway.
     */
    @Override
    final public ComplexNumber logDeterminant()
    {
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: I'm using LU decomposition to do this, so ... to Dense!
        return (new DenseMatrix(this)).logDeterminant();
    }

    /**
     * @see BaseMatrix#rank(double)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * This is implemented by creating a new dense matrix version of this and
     * calling its rank method -- It requires factoring the matrix which is
     * going to memory intense anyway.
     */
    @Override
    final public int rank(double effectiveZero)
    {
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: This generally requires factoring the matrix which quickly
        // creates dense matrices anyway
        return (new DenseMatrix(this)).rank(effectiveZero);
    }

    /**
     * @see Matrix#normFrobeniusSquared()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    public double normFrobeniusSquared()
    {
        if (!isCompressed())
        {
            compress();
        }
        double ret = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            ret += vals[i] * vals[i];
        }
        return ret;
    }

    /**
     * @see BaseMatrix#normFrobenius()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final public double normFrobenius()
    {
        return Math.sqrt(normFrobeniusSquared());
    }

    /**
     * @see BaseMatrix#isSquare()
     *
     * NOTE: No change to the internal format of this.
     */
    @Override
    final public boolean isSquare()
    {
        return numRows == numCols;
    }

    /**
     * @see BaseMatrix#solve(gov.sandia.cognition.math.matrix.Matrix)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * This is implemented by creating a new dense matrix version of this and
     * calling its solve method -- It requires factoring the matrix which is
     * going to memory intense anyway. We recommend an iterative solver instead.
     */
    @Override
    final public Matrix solve(Matrix B)
    {
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: Iterative solvers are the way to go with sparse matrices.
        // Direct solution of a sparse matrix generally creates a dense matrix
        // anyway.  Sparse matrices make the most sense for iterative solvers
        return (new DenseMatrix(this)).solve(B);
    }

    /**
     * @see BaseMatrix#solve(gov.sandia.cognition.math.matrix.Vector)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * This is implemented by creating a new dense matrix version of this and
     * calling its solve method -- It requires factoring the matrix which is
     * going to memory intense anyway. We recommend an iterative solver instead.
     */
    @Override
    final public Vector solve(Vector b)
    {
        if (!isCompressed())
        {
            compress();
        }
        // NOTE: Iterative solvers are the way to go with sparse matrices.
        // Direct solution of a sparse matrix generally creates a dense matrix
        // anyway.  Sparse matrices make the most sense for iterative solvers
        return (new DenseMatrix(this)).solve(b);
    }

    /**
     * @see BaseMatrix#identity()
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final public void identity()
    {
        // First get rid of any values already in there
        for (int i = 0; i < numRows; ++i)
        {
            rows[i].clear();
        }
        // Kill compressed representation if there ... update to new values
        int diagLen = (numRows <= numCols) ? numRows : numCols;
        vals = new double[diagLen];
        Arrays.fill(vals, 1);
        firstIdxsForRows = getZeroToNMinusOneArray(numRows + 1);
        colIdxs = getZeroToNMinusOneArray(diagLen);

        // We just need to update the first indices for the rows if this is a
        // tall rectangle matrix
        for (int i = diagLen; i < numRows + 1; ++i)
        {
            firstIdxsForRows[i] = diagLen;
        }
    }

    /**
     * @see BaseMatrix#getColumn(int)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final public Vector getColumn(int columnIndex)
    {
        if (!isCompressed())
        {
            compress();
        }
        SparseVector ret = new SparseVector(numRows);
        for (int i = 0; i < numRows; ++i)
        {
            ret.setElement(i, getElement(i, columnIndex));
        }

        return ret;
    }

    /**
     * @see BaseMatrix#sumOfColumns()
     */
    @Override
    public Vector sumOfColumns()
    {
        if (!isCompressed())
        {
            compress();
        }
        DenseVector ret = new DenseVector(numRows, true);
        for (int i = 0; i < numRows; ++i)
        {
            ret.elements()[i] = 0;
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                ret.elements()[i] += vals[j];
            }
        }

        return ret;
    }

    /**
     * @see BaseMatrix#sumOfRows()
     */
    @Override
    public Vector sumOfRows()
    {
        if (!isCompressed())
        {
            compress();
        }
        DenseVector ret = new DenseVector(numCols);
        for (int i = 0; i < numRows; ++i)
        {
            for (int j = firstIdxsForRows[i]; j < firstIdxsForRows[i + 1]; ++j)
            {
                ret.elements()[colIdxs[j]] += vals[j];
            }
        }

        return ret;
    }

    /**
     * @see BaseMatrix#getRow(int)
     *
     * NOTE: Internal format is unchanged after this method
     */
    @Override
    final public Vector getRow(int rowIndex)
    {
        if (!isCompressed())
        {
            return new SparseVector(rows[rowIndex]);
        }
        else
        {
            SparseVector ret = new SparseVector(numCols);
            for (int i = firstIdxsForRows[rowIndex]; i
                < firstIdxsForRows[rowIndex + 1]; ++i)
            {
                ret.setElement(colIdxs[i], vals[i]);
            }
            return ret;
        }
    }

    /**
     * @see
     * BaseMatrix#convertFromVector(gov.sandia.cognition.math.matrix.Vector)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @throws IllegalArgumentException if parameters does not have the same
     * number of elements as this's full size (including all of the zero
     * values).
     */
    @Override
    final public void convertFromVector(Vector parameters)
    {
        if (parameters.getDimensionality() != (numRows * numCols))
        {
            throw new IllegalArgumentException("Dimensions do not match: "
                + parameters.getDimensionality() + " != " + numRows + " * "
                + numCols);
        }

        // Count how many non-zero elements there will be at the end
        int nnz = 0;
        for (int i = 0; i < parameters.getDimensionality(); ++i)
        {
            if (parameters.getElement(i) != 0)
            {
                ++nnz;
            }
        }
        // Initialize the compressed Yale format
        firstIdxsForRows = new int[numRows + 1];
        colIdxs = new int[nnz];
        vals = new double[nnz];
        int idx = 0;
        // Fill the data in
        for (int i = 0; i < numRows; ++i)
        {
            rows[i].clear();
            firstIdxsForRows[i] = idx;
            for (int j = 0; j < numCols; ++j)
            {
                double val = parameters.getElement(i + j * numRows);
                if (val != 0)
                {
                    colIdxs[idx] = j;
                    vals[idx] = val;
                    ++idx;
                }
            }
        }
        firstIdxsForRows[numRows] = idx;
    }

    /**
     * @see BaseMatrix#convertToVector()
     *
     * NOTE: This does not affect compressed Yale/sparse row representation --
     * either is handled.
     */
    @Override
    final public Vector convertToVector()
    {
        SparseVector ret = new SparseVector(numRows * numCols);
        if (isCompressed())
        {
            for (int i = 0; i < numRows; ++i)
            {
                for (int j = firstIdxsForRows[i]; j
                    < firstIdxsForRows[i + 1];
                    ++j)
                {
                    ret.setElement(i + colIdxs[j] * numRows, vals[j]);
                }
            }
        }
        else
        {
            for (int i = 0; i < numRows; ++i)
            {
                rows[i].compress();
                for (int j = 0; j < rows[i].getLocs().length; ++j)
                {
                    ret.setElement(i + rows[i].getLocs()[j] * numRows,
                        rows[i].getVals()[j]);
                }
            }
        }
        return ret;
    }

    /**
     * Private helper class that reads sparse entries from a compressed
     * SparseMatrix. Does not support the edit operations.
     */
    final private class ReadOnlySparseMatrixEntry
        implements MatrixEntry
    {

        /**
         * The row index for this value
         */
        private int rowIdx;

        /**
         * The index for the column and value in the compressed data
         */
        private int col_valIdx;

        /**
         * Initializes this instance with the necessary values
         *
         * @param rowIdx The row index for this value
         * @param col_valIdx The index for the column and value in the
         * compressed data
         */
        public ReadOnlySparseMatrixEntry(int rowIdx,
            int col_valIdx)
        {
            this.rowIdx = rowIdx;
            this.col_valIdx = col_valIdx;
        }

        /**
         * Returns the row index
         *
         * @return the row index
         */
        @Override
        public int getRowIndex()
        {
            return rowIdx;
        }

        /**
         * @throws UnsupportedOperationException
         */
        @Override
        public void setRowIndex(int rowIndex)
        {
            throw new UnsupportedOperationException(
                "This implementation immutable.");
        }

        /**
         * Returns the value stored herein
         *
         * @return the value stored herein
         */
        @Override
        public double getValue()
        {
            return vals[col_valIdx];
        }

        /**
         * @throws UnsupportedOperationException
         */
        @Override
        public void setValue(double value)
        {
            throw new UnsupportedOperationException(
                "This implementation immutable.");
        }

        /**
         * Returns the column index
         *
         * @return the column index
         */
        @Override
        public int getColumnIndex()
        {
            return colIdxs[col_valIdx];
        }

        /**
         * @throws UnsupportedOperationException
         */
        @Override
        public void setColumnIndex(int columnIndex)
        {
            throw new UnsupportedOperationException(
                "This implementation immutable.");
        }

    }

    /**
     * This private helper class returns MatrixEntries for a compressed
     * SparseMatrix.
     */
    final private class NonZeroEntryIterator
        implements Iterator<MatrixEntry>
    {

        /**
         * The row index for the next value
         */
        private int rowIdx;

        /**
         * The column and value index for the next value
         */
        private int col_valIdx;

        /**
         * Initializes a new iterator starting at the first value in the matrix
         */
        public NonZeroEntryIterator()
        {
            rowIdx = col_valIdx = 0;
            // If there are no-entry rows, we need to skip past them
            while (col_valIdx >= firstIdxsForRows[rowIdx + 1])
            {
                ++rowIdx;
                if (rowIdx >= numRows) {
                    break;
                }
            }
        }

        /**
         * Initializes a new iterator starting at the first value in the first
         * row of the matrix
         *
         * @param startRow The row to start on
         * @throws IllegalArgumentException if startRow is outside of [0 ..
         * numRows]
         */
        public NonZeroEntryIterator(int startRow)
        {
            if (startRow >= getNumRows() || startRow < 0)
            {
                throw new IllegalArgumentException("Input row index ("
                    + startRow + ") greater than number of rows: "
                    + getNumRows());
            }
            rowIdx = startRow;
            col_valIdx = firstIdxsForRows[rowIdx];
            // If there are no-entry rows, we need to skip past them
            while (col_valIdx >= firstIdxsForRows[rowIdx + 1])
            {
                ++rowIdx;
                if (rowIdx >= numRows) {
                    break;
                }
            }
        }

        /**
         * Returns true if there are more values to return
         *
         * @return true if there are more values to return (else false)
         */
        @Override
        public boolean hasNext()
        {
            return col_valIdx < vals.length;
        }

        /**
         * Returns the next value in the matrix. Updates internal state past
         * this entry.
         *
         * @return the next value in the matrix
         */
        @Override
        public MatrixEntry next()
        {
            ReadOnlySparseMatrixEntry ret
                = new ReadOnlySparseMatrixEntry(rowIdx, col_valIdx);
            col_valIdx++;
            // If there are no-entry rows, we need to skip past them
            while (col_valIdx >= firstIdxsForRows[rowIdx + 1])
            {
                ++rowIdx;
                if (rowIdx >= numRows) {
                    break;
                }
            }

            return ret;
        }

        /**
         * @throws UnsupportedOperationException
         */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException(
                "This implementation immutable.");
        }

    }

    /**
     * Returns an iterator over the non-zero entries in this matrix. The
     * returned iterator is invalidated by any subsequent changes to the
     * referenced matrix. Any follow-on calls to the iterator after any changes
     * to the referred instance will provide undefined results (up to and
     * including throwing Exceptions).
     *
     * @return an iterator over the non-zero entries in this matrix
     * (left-to-right, top-to-bottom).
     */
    final public Iterator<MatrixEntry> getNonZeroValueIterator()
    {
        if (!isCompressed())
        {
            compress();
        }
        return new NonZeroEntryIterator();
    }

    /**
     * Returns an iterator over the non-zero entries in this matrix starting
     * with startRow. The returned iterator is invalidated by any subsequent
     * changes to the referenced matrix. Any follow-on calls to the iterator
     * after any changes to the referred instance will provide undefined results
     * (up to and including throwing Exceptions).
     *
     * @param startRow The row to begin reading non-zero entries from
     * @return an iterator over the non-zero entries in this matrix
     * (left-to-right, top-to-bottom).
     */
    final public Iterator<MatrixEntry> getNonZeroValueIterator(int startRow)
    {
        if (!isCompressed())
        {
            compress();
        }
        return new NonZeroEntryIterator(startRow);
    }

    /**
     * @see
     * BaseMatrix#_preTimes(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final Vector _preTimes(SparseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }
        SparseVector ret = new SparseVector(getNumColumns());
        vector.compress();
        for (int j = 0; j < vector.getLocs().length; ++j)
        {
            for (int i = firstIdxsForRows[vector.getLocs()[j]]; i
                < firstIdxsForRows[vector.getLocs()[j] + 1]; ++i)
            {
                ret.setElement(colIdxs[i], ret.getElement(colIdxs[i])
                    + vals[i] * vector.getVals()[j]);
            }
        }

        return ret;
    }

    /**
     * @see
     * BaseMatrix#_preTimes(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     */
    @Override
    final Vector _preTimes(DenseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }
        DenseVector ret = new DenseVector(getNumColumns());
        int row = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (i >= firstIdxsForRows[row + 1])
            {
                ++row;
            }
            ret.setElement(colIdxs[i], ret.getElement(colIdxs[i])
                + vector.getElement(row) * vals[i]);
        }

        return ret;
    }

    /**
     * Package-private helper that allows other matrices direct access to
     * setting the sparse vector rows of this. Be very careful when calling this
     * -- you have direct access to the data!
     *
     * NOTE: Upon completion, this is in the sparse vector format.
     *
     * @param i The row index
     * @param v The vector that to put in the new row
     */
    final void _setRow(int i,
        SparseVector v)
    {
        if (isCompressed())
        {
            decompress();
        }

        rows[i] = v;
    }

    /**
     * Called by serialization (through the magic of Java reflection) and
     * shouldn't be called by anyone else.
     *
     * @param oos The stream to write this to
     * @throws IOException If there's a problem
     */
    private void writeObject(ObjectOutputStream oos)
        throws IOException
    {
        compress();
        oos.writeInt(numRows);
        oos.writeInt(numCols);
        oos.writeObject(colIdxs);
        oos.writeObject(firstIdxsForRows);
        oos.writeObject(vals);
    }

    /**
     * Called by de-serialization (through the magic of Java reflect) and
     * shouldn't be called by anyone else.
     *
     * @param ois The stream to read this from
     * @throws IOException If there's a problem
     * @throws ClassNotFoundException If there's a problem
     */
    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException
    {
        numRows = ois.readInt();
        numCols = ois.readInt();
        colIdxs = (int[]) ois.readObject();
        firstIdxsForRows = (int[]) ois.readObject();
        vals = (double[]) ois.readObject();
        rows = new SparseVector[numRows];
        for (int i = 0; i < getNumRows(); ++i)
        {
            rows[i] = new SparseVector(numCols);
        }
    }

}
