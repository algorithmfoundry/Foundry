
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Arrays;

/**
 * Diagonal matrices are a special case, but a rather common one with very quick
 * and simple solutions to multiplications, inverses, etc. This can't magically
 * change type, however. So if you will ever add an off-diagonal element, don't
 * use this!
 *
 * NOTE: This only supports square diagonal matrices.
 *
 * @author Jeremy D. Wendt
 */
public class DiagonalMatrix
    extends BaseMatrix
{

    /**
     * The elements down the diagonal
     */
    private double[] diag;

    /**
     * Creates a square (n x n) diagonal matrix initialized to zero.
     *
     * @param n The dimensions (num rows and num columns) of the matrix
     */
    public DiagonalMatrix(int n)
    {
        diag = new double[n];
        Arrays.fill(diag, 0);
    }

    /**
     * Copy constructor. Creates a deep copy of d.
     *
     * @param d The diagonal matrix to copy
     */
    public DiagonalMatrix(DiagonalMatrix d)
    {
        diag = Arrays.copyOf(d.diag, d.diag.length);
    }

    public DiagonalMatrix(Matrix m)
    {
        if (m.getNumRows() != m.getNumColumns())
        {
            throw new IllegalArgumentException("Unable to copy a non-square "
                + "matrix into a diagonal matrix.");
        }

        diag = new double[m.getNumRows()];
        for (int i = 0; i < m.getNumRows(); ++i)
        {
            for (int j = 0; j < m.getNumColumns(); ++j)
            {
                if (i == j)
                {
                    diag[i] = m.getElement(i, i);
                }
                else if (m.getElement(i, j) != 0)
                {
                    throw new IllegalArgumentException("Unable to copy the "
                        + "input to a diagonal matrix as the element at " + i
                        + ", " + j + " is non-zero (" + m.getElement(i, j) + ")");
                }
            }
        }
    }

    /**
     * Package-private helper that saves a bit of time by not initializing the
     * values down the diagonal. It is assumed that any place that calls this
     * will immediately initialize the values along the diagonal to their
     * correct values.
     *
     * @param n The dimensions (num rows and num columns) of the matrix
     * @param unused Only here to differentiate the signature from the other
     * constructor
     */
    DiagonalMatrix(int n,
        boolean unused)
    {
        diag = new double[n];
        // Don't initialize
    }

    /**
     * Creates a square (diagonal.length x diagonal.length) diagonal matrix,
     * initialized to the input values (makes a deep copy).
     *
     * @param diagonal The initial values for the diagonal (implicitly defines
     * the size of the matrix, too!).
     */
    DiagonalMatrix(double[] diagonal)
    {
        diag = Arrays.copyOf(diagonal, diagonal.length);
    }

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected DiagonalMatrix()
    {
        // NOTE: This doesn't initialize anything
    }

    /**
     * @see BaseMatrix#clone()
     */
    @Override
    final public Matrix clone()
    {
        return new DiagonalMatrix(this);
    }

    /**
     * @see
     * BaseMatrix#scaledPlusEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    void scaledPlusEquals(SparseMatrix other,
        double scaleFactor)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal elements are 0 (as well as subtracting along the diagonal)
        if (!other.isCompressed())
        {
            other.compress();
        }
        int rowNum = 0;
        for (int i = 0; i < other.getVals().length; ++i)
        {
            while (i >= other.getFirstInRows()[rowNum + 1])
            {
                ++rowNum;
            }
            if (other.getVals()[i] == 0)
            {
                continue;
            }
            if (other.getColIdxs()[i] != rowNum)
            {
                throw new IllegalArgumentException("Unable to store the "
                    + " difference of a non-diagonal sparse matrix with a "
                    + "diagonal matrix in the diagonal matrix");
            }
            diag[rowNum] += other.getVals()[i] * scaleFactor;
        }
    }

    /**
     * @see
     * BaseMatrix#scaledPlusEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    void scaledPlusEquals(DenseMatrix other,
        double scaleFactor)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal are 0 (as well as summing along the diagonal)
        for (int i = 0; i < diag.length; ++i)
        {
            for (int j = 0; j < diag.length; ++j)
            {
                if (i == j)
                {
                    diag[i] += other.row(i).elements()[i] * scaleFactor;
                }
                else if (other.row(i).elements()[j] != 0)
                {
                    throw new IllegalArgumentException("Unable to store the "
                        + "sum of a non-diagonal dense matrix with a "
                        + "diagonal matrix in the diagonal matrix.");
                }
            }
        }
    }

    /**
     * @see
     * BaseMatrix#plusEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     */
    @Override
    void scaledPlusEquals(DiagonalMatrix other,
        double scaleFactor)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] += other.diag[i] * scaleFactor;
        }
    }

    /**
     * @see
     * BaseMatrix#plusEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    final void plusEquals(SparseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal elements are 0 (as well as subtracting along the diagonal)
        if (!other.isCompressed())
        {
            other.compress();
        }
        int rowNum = 0;
        for (int i = 0; i < other.getVals().length; ++i)
        {
            while (i >= other.getFirstInRows()[rowNum + 1])
            {
                ++rowNum;
            }
            if (other.getVals()[i] == 0)
            {
                continue;
            }
            if (other.getColIdxs()[i] != rowNum)
            {
                throw new IllegalArgumentException("Unable to store the "
                    + " difference of a non-diagonal sparse matrix with a "
                    + "diagonal matrix in the diagonal matrix");
            }
            diag[rowNum] += other.getVals()[i];
        }
    }

    /**
     * @see
     * BaseMatrix#plusEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    final void plusEquals(DenseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal are 0 (as well as summing along the diagonal)
        for (int i = 0; i < diag.length; ++i)
        {
            for (int j = 0; j < diag.length; ++j)
            {
                if (i == j)
                {
                    diag[i] += other.row(i).elements()[i];
                }
                else if (other.row(i).elements()[j] != 0)
                {
                    throw new IllegalArgumentException("Unable to store the "
                        + "sum of a non-diagonal dense matrix with a "
                        + "diagonal matrix in the diagonal matrix.");
                }
            }
        }
    }

    /**
     * @see
     * BaseMatrix#plusEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     */
    @Override
    final void plusEquals(DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] += other.diag[i];
        }
    }

    /**
     * @see
     * BaseMatrix#minusEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    final void minusEquals(SparseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal elements are 0 (as well as subtracting along the diagonal)
        if (!other.isCompressed())
        {
            other.compress();
        }
        int rowNum = 0;
        for (int i = 0; i < other.getVals().length; ++i)
        {
            while (i >= other.getFirstInRows()[rowNum + 1])
            {
                ++rowNum;
            }
            if (other.getVals()[i] == 0)
            {
                continue;
            }
            if (other.getColIdxs()[i] != rowNum)
            {
                throw new IllegalArgumentException("Unable to store the "
                    + " difference of a non-diagonal sparse matrix with a "
                    + "diagonal matrix in the diagonal matrix");
            }
            diag[rowNum] -= other.getVals()[i];
        }
    }

    /**
     * @see
     * BaseMatrix#minusEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     * @throws IllegalArgumentException if the input has any non-zero off-axis
     * elements as that would make this a non-diagonal matrix
     */
    @Override
    final void minusEquals(DenseMatrix other)
    {
        this.assertSameDimensions(other);
        
        // I have to run through all values in the input to make sure all off-
        // diagonal are 0 (as well as subtracting along the diagonal)
        for (int i = 0; i < diag.length; ++i)
        {
            for (int j = 0; j < diag.length; ++j)
            {
                if (i == j)
                {
                    diag[i] -= other.row(i).elements()[i];
                }
                else if (other.row(i).elements()[j] != 0)
                {
                    throw new IllegalArgumentException("Unable to store the "
                        + "difference of a non-diagonal dense matrix with a "
                        + "diagonal matrix in the diagonal matrix.");
                }
            }
        }
    }

    /**
     * @see
     * BaseMatrix#minusEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     */
    @Override
    final void minusEquals(DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] -= other.diag[i];
        }
    }

    /**
     * @see
     * BaseMatrix#dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     */
    @Override
    final void dotTimesEquals(SparseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] *= other.getElement(i, i);
        }
    }

    /**
     * @see
     * BaseMatrix#dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     */
    @Override
    final void dotTimesEquals(DenseMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] *= other.row(i).elements()[i];
        }
    }

    /**
     * @see
     * BaseMatrix#dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     */
    @Override
    final void dotTimesEquals(DiagonalMatrix other)
    {
        this.assertSameDimensions(other);
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] *= other.diag[i];
        }
    }

    /**
     * @see
     * BaseMatrix#times(gov.sandia.cognition.math.matrix.optimized.SparseMatrix)
     */
    @Override
    final Matrix times(SparseMatrix other)
    {
        return other.preTimes(this);
    }

    /**
     * @see
     * BaseMatrix#times(gov.sandia.cognition.math.matrix.optimized.DenseMatrix)
     */
    @Override
    final Matrix times(DenseMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        DenseMatrix ret = new DenseMatrix(diag.length, other.getNumColumns(),
            true);
        for (int i = 0; i < diag.length; ++i)
        {
            DenseVector v = other.row(i);
            ret.setRow(i, (DenseVector) v.scale(diag[i]));
        }
        return ret;
    }

    /**
     * @see
     * BaseMatrix#times(gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix)
     */
    @Override
    final Matrix times(DiagonalMatrix other)
    {
        this.assertMultiplicationDimensions(other);
        DiagonalMatrix ret = new DiagonalMatrix(this);
        for (int i = 0; i < diag.length; ++i)
        {
            ret.diag[i] *= other.diag[i];
        }

        return ret;
    }

    /**
     * @see
     * BaseMatrix#times(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final Vector times(SparseVector vector)
    {
        vector.assertDimensionalityEquals(this.getNumColumns());
        SparseVector ret = new SparseVector(diag.length);
        vector.compress();
        int[] locs = vector.getLocs();
        for (int i = 0; i < locs.length; ++i)
        {
            ret.setElement(locs[i], vector.getVals()[i] * diag[locs[i]]);
        }

        return ret;
    }

    /**
     * @see
     * BaseMatrix#times(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final Vector times(DenseVector vector)
    {
        vector.assertDimensionalityEquals(this.getNumColumns());
        DenseVector ret = new DenseVector(diag.length, true);
        for (int i = 0; i < diag.length; ++i)
        {
            ret.setElement(i, vector.getElement(i) * diag[i]);
        }

        return ret;
    }

    /**
     * @see BaseMatrix#scaleEquals(double)
     */
    @Override
    final public void scaleEquals(double scaleFactor)
    {
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] *= scaleFactor;
        }
    }

    /**
     * @see BaseMatrix#getNumRows()
     */
    @Override
    final public int getNumRows()
    {
        return diag.length;
    }

    /**
     * @see BaseMatrix#getNumColumns()
     */
    @Override
    final public int getNumColumns()
    {
        return diag.length;
    }

    /**
     * Helper that makes sure the input row index and column index are within
     * the bounds of matrix. This does not ensure the inputs are on the diagonal
     * as there are valid reasons to get elements off the main diagonal.
     *
     * @param rowIndex The row index
     * @param columnIndex The column index
     * @throws ArrayIndexOutOfBoundsException if the input values are outside
     * the matrix
     */
    private void checkBounds(int rowIndex,
        int columnIndex)
    {
        if (rowIndex < 0 || columnIndex < 0 || rowIndex >= diag.length
            || columnIndex
            >= diag.length)
        {
            throw new ArrayIndexOutOfBoundsException("Input index (" + rowIndex
                + ", " + columnIndex + ") is not within this " + diag.length
                + "x" + diag.length + " matrix");
        }
    }

    /**
     * @see Matrix#get(int, int)
     */
    @Override
    public double get(int rowIndex,
        int columnIndex)
    {
        return getElement(rowIndex, columnIndex);
    }

    /**
     * Returns the value stored at the input locations
     *
     * @param rowIndex The row index
     * @param columnIndex The column index
     * @return the value stored at the input locations
     * @throws ArrayIndexOutOfBoundsException if the input values are outside
     * the matrix
     */
    @Override
    final public double getElement(int rowIndex,
        int columnIndex)
    {
        checkBounds(rowIndex, columnIndex);
        if (rowIndex == columnIndex)
        {
            return diag[rowIndex];
        }
        return 0;
    }

    /**
     * @see Matrix#set(int, int, double)
     */
    @Override
    public void set(int rowIndex,
        int columnIndex,
        double value)
    {
        setElement(rowIndex, columnIndex, value);
    }

    /**
     * Set the value stored at the input locations to the input value
     *
     * @param rowIndex The row index
     * @param columnIndex The column index
     * @param value The new value for the input location
     * @throws ArrayIndexOutOfBoundsException if the input indices are outside
     * the matrix
     * @throws IllegalArgumentException if the input indices attempt to set a
     * non-zero value off the main axis
     */
    @Override
    final public void setElement(int rowIndex,
        int columnIndex,
        double value)
    {
        checkBounds(rowIndex, columnIndex);
        if (rowIndex == columnIndex)
        {
            diag[rowIndex] = value;
        }
        else if (value != 0)
        {
            throw new IllegalArgumentException(
                "Unable to set an off-axis value in "
                + "a diagonal matrix");
        }
    }

    /**
     * @see BaseMatrix#getSubMatrix(int, int, int, int)
     * @throws ArrayIndexOutOfBoundsException if the input indices are outside
     * the matrix
     */
    @Override
    final public Matrix getSubMatrix(int minRow,
        int maxRow,
        int minColumn,
        int maxColumn)
    {
        checkSubmatrixRange(minRow, maxRow, minColumn, maxColumn);
        SparseMatrix ret = new SparseMatrix(maxRow - minRow + 1, maxColumn
            - minColumn + 1);
        // You only need worry about the diagonal, so one of the extents will do
        for (int i = minRow; i <= maxRow; ++i)
        {
            // Check to make sure that this element of the diagonal is also in
            // the other extents
            if (i >= minColumn && i <= maxColumn)
            {
                // If it is, add it at the right place in the output
                ret.setElement(i - minRow, i - minColumn, getElement(i, i));
            }
        }
        return ret;
    }

    /**
     * @see BaseMatrix#isSymmetric(double)
     */
    @Override
    final public boolean isSymmetric(double effectiveZero)
    {
        testEffZero(effectiveZero);
        return true;
    }

    /**
     * @see BaseMatrix#transpose()
     */
    @Override
    final public Matrix transpose()
    {
        return new DiagonalMatrix(this);
    }

    /**
     * @see BaseMatrix#inverse()
     * @throws UnsupportedOperationException if this doesn't span the space, so
     * can't be inverted.
     */
    @Override
    final public Matrix inverse()
    {
        DiagonalMatrix ret = new DiagonalMatrix(diag.length, true);
        for (int i = 0; i < diag.length; ++i)
        {
            if (diag[i] == 0)
            {
                throw new UnsupportedOperationException("Can't invert matrix "
                    + "because it does not span the columns");
            }
            ret.diag[i] = 1.0 / diag[i];
        }

        return ret;
    }

    /**
     * @see BaseMatrix#pseudoInverse(double)
     */
    @Override
    final public Matrix pseudoInverse(double effectiveZero)
    {
        testEffZero(effectiveZero);
        DiagonalMatrix ret = new DiagonalMatrix(diag.length, true);
        for (int i = 0; i < diag.length; ++i)
        {
            ret.diag[i] = (Math.abs(diag[i]) > effectiveZero) ? 1.0 / diag[i]
                : 0;
        }

        return ret;
    }

    /**
     * @see BaseMatrix#logDeterminant()
     */
    @Override
    final public ComplexNumber logDeterminant()
    {
        // This can never fail in a diagonal matrix
        // if (!isSquare())
        // {
        // throw new IllegalArgumentException("Matrix must be square");
        // }

        // This is a diagonal matrix (which can be considered upper triangular).
        // NOTE: The determinant of a triangular matrix is the product of the
        // diagonal entries (see 
        // http://en.wikipedia.org/wiki/Triangular_matrix#Special_properties)
        // NOTE: The logarithm of the product of two numbers is the sum of the
        // logarightm of each of the two numbers (see
        // http://en.wikipedia.org/wiki/Logarithm)
        // 
        // The diagonal elements will be REAL, but they may be negative.
        // The logarithm of a negative number is the logarithm of the absolute
        // value of the number, with an imaginary part of PI.  The exponential
        // is all that matters, so the log-determinant is equivalent, MODULO
        // PI (3.14...), so we just toggle this sign bit.
        int sign = 1;
        double logsum = 0.0;
        for (int i = 0; i < diag.length; i++)
        {
            double eigenvalue = diag[i];
            if (eigenvalue < 0.0)
            {
                sign = -sign;
                logsum += Math.log(-eigenvalue);
            }
            else
            {
                logsum += Math.log(eigenvalue);
            }

        }

        return new ComplexNumber(logsum, (sign < 0) ? Math.PI : 0.0);
    }

    /**
     * @see BaseMatrix#rank(double)
     */
    @Override
    final public int rank(double effectiveZero)
    {
        testEffZero(effectiveZero);
        int rank = 0;
        for (int i = 0; i < diag.length; ++i)
        {
            if (Math.abs(diag[i]) > effectiveZero)
            {
                ++rank;
            }
        }

        return rank;
    }

    /**
     * @see Matrix#normFrobeniusSquared() 
     * @return 
     */
    @Override
    public double normFrobeniusSquared()
    {
        double ret = 0;
        for (int i = 0; i < diag.length; ++i)
        {
            ret += diag[i] * diag[i];
        }
        return ret;
    }

    /**
     * @see BaseMatrix#normFrobenius()
     */
    @Override
    final public double normFrobenius()
    {
        return Math.sqrt(normFrobeniusSquared());
    }

    /**
     * @see BaseMatrix#isSquare()
     */
    @Override
    final public boolean isSquare()
    {
        return true;
    }

    /**
     * @see BaseMatrix#solve(gov.sandia.cognition.math.matrix.Matrix)
     * @throws IllegalArgumentException if the input's numRows doesn't match
     * this's numRows
     * @throws UnsupportedOperationException if this doesn't span the space
     */
    @Override
    final public Matrix solve(Matrix B)
    {
        checkSolveDimensions(B);
        Matrix ret = B.clone();
        for (int i = 0; i < diag.length; ++i)
        {
            for (int j = 0; j < B.getNumColumns(); ++j)
            {
                if (diag[i] == 0)
                {
                    throw new UnsupportedOperationException("Can't invert "
                        + "matrix because it does not span the columns");
                }
                else
                {
                    ret.setElement(i, j, ret.getElement(i, j) / diag[i]);
                }
            }
        }

        return ret;
    }

    /**
     * @see BaseMatrix#solve(gov.sandia.cognition.math.matrix.Vector)
     * @throws IllegalArgumentException if the input's numRows doesn't match
     * this's numRows
     * @throws UnsupportedOperationException if this doesn't span the space
     */
    @Override
    final public Vector solve(Vector b)
    {
        checkSolveDimensions(b);
        Vector ret = b.clone();
        for (int i = 0; i < diag.length; ++i)
        {
            if (diag[i] == 0)
            {
                if (ret.getElement(i) != 0)
                {
                    throw new UnsupportedOperationException("Unable to solve "
                        + "Ax=b because b spans different space than A");
                }
            }
            else
            {
                ret.setElement(i, ret.getElement(i) / diag[i]);
            }
        }

        return ret;
    }

    /**
     * @see BaseMatrix#identity()
     */
    @Override
    final public void identity()
    {
        for (int i = 0; i < diag.length; ++i)
        {
            diag[i] = 1;
        }
    }

    /**
     * @see BaseMatrix#getColumn(int)
     * @throws ArrayIndexOutOfBoundsException if the input column index is
     * outside the matrix
     */
    @Override
    final public Vector getColumn(int columnIndex)
    {
        if (columnIndex < 0 || columnIndex >= diag.length)
        {
            throw new ArrayIndexOutOfBoundsException("Input column index ("
                + columnIndex + ") is not within this " + diag.length + "x"
                + diag.length + " matrix");
        }
        SparseVector ret = new SparseVector(diag.length);
        ret.setElement(columnIndex, diag[columnIndex]);

        return ret;
    }

    /**
     * @see BaseMatrix#getRow(int)
     * @throws ArrayIndexOutOfBoundsException if the input row index is outside
     * the matrix
     */
    @Override
    final public Vector getRow(int rowIndex)
    {
        if (rowIndex < 0 || rowIndex >= diag.length)
        {
            throw new ArrayIndexOutOfBoundsException("Input row index ("
                + rowIndex + ") is not within this " + diag.length + "x"
                + diag.length + " matrix");
        }
        SparseVector ret = new SparseVector(diag.length);
        ret.setElement(rowIndex, diag[rowIndex]);

        return ret;
    }

    /**
     * @see
     * BaseMatrix#convertFromVector(gov.sandia.cognition.math.matrix.Vector)
     * @throws IllegalArgumentException if input vector doesn't have enough
     * elements to cover all elements of this or if the input vector specifies
     * non-zero values in off-diagonal elements.
     */
    @Override
    final public void convertFromVector(Vector parameters)
    {
        parameters.assertDimensionalityEquals(this.getNumRows() * getNumColumns());

        for (int i = 0; i < getNumRows(); ++i)
        {
            for (int j = 0; j < getNumColumns(); ++j)
            {
                if (i == j)
                {
                    diag[i] = parameters.getElement(i * getNumColumns() + j);
                }
                // this checks that all off-diagonal elements are zero
                else if (parameters.getElement(i * getNumColumns() + j) != 0)
                {
                    throw new IllegalArgumentException("Cannot convert "
                        + "diagonal matrix from vector with non-zero element "
                        + "that maps to off-diagonal location at " + i
                        + ", " + j);
                }
            }
        }
    }

    /**
     * @see BaseMatrix#convertToVector()
     */
    @Override
    final public Vector convertToVector()
    {
        SparseVector ret = new SparseVector(getNumRows() * getNumColumns());
        for (int i = 0; i < diag.length; ++i)
        {
            ret.setElement(i * getNumColumns() + i, diag[i]);
        }
        return ret;
    }

    /**
     * @see
     * BaseMatrix#preTimes(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final Vector preTimes(SparseVector vector)
    {
        // Only true for diagonal (and symmetric) matrices: pre-mult vector is the same as post-mult
        // vector
        return times(vector);
    }

    /**
     * @see
     * BaseMatrix#preTimes(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final Vector preTimes(DenseVector vector)
    {
        // Only true for diagonal (and symmetric) matrices: pre-mult vector is the same as post-mult
        // vector
        return times(vector);
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

    @Override
    public MatrixFactory<?> getMatrixFactory()
    {
        return DiagonalMatrixFactoryOptimized.INSTANCE;
    }
    
}
