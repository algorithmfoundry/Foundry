/*
 * File:                Matrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 12, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.Ring;
import java.text.NumberFormat;

/**
 * Defines the base functionality for all implementations of a Matrix
 *
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments={
                "Minor changes to the formatting",
                "Otherwise, looks good."
            }
        ),
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments="Interface looks ok. Made a few minor changes."
        )
    }
)
public interface Matrix
    extends java.lang.Iterable<MatrixEntry>, Ring<Matrix>, Vectorizable
{

    public Matrix clone();

    /**
     * Returns the number of rows in the Matrix 
     *
     * @return Number of rows in the Matrix
     */
    public int getNumRows();

    /**
     * Returns the number of columns in the Matrix 
     *
     * @return Number of columns in the Matrix
     */
    public int getNumColumns();

    /**
     * Gets the Matrix element at the specified zero-based indices
     * throws ArrayIndexOutOfBoundsException if either rowIndex or columnIndex
     * are less than 0, or greater than the number of rows (columns) minus one
     * (0 <= index <= num-1) 
     * 
     * @param rowIndex Zero-based index into the Matrix
     * @param columnIndex Zero-based index into the Matrix
     * @return The value at rowIndex, columnIndex
     */
    public double getElement(
        int rowIndex,
        int columnIndex );

    /**
     * Sets the Matrix element at the specified zero-based indices
     * throws ArrayIndexOutOfBoundsException if either rowIndex or columnIndex
     * are less than 0, or greater than the number of rows (columns) minus one
     * (0 <= index <= num-1)
     * 
     * @param rowIndex Zero-based index into the rows of the Matrix
     * @param columnIndex Zero-based index into the columns of the Matrix
     * @param value Value to set at the specified index
     */
    public void setElement(
        int rowIndex,
        int columnIndex,
        double value );

    /**
     * Gets the embedded submatrix inside of the Matrix, specified by the
     * inclusive, zero-based indices such that the result matrix will have size
     * (maxRow-minRow+1) x (maxColum-minCcolumn+1)
     * 
     * 
     * @param  minRow Zero-based index into the rows of the Matrix, must be less 
     *        than or equal to maxRow
     * @param  maxRow Zero-based index into the rows of the Matrix, must be 
     *         greater than or equal to minRow
     * @param  minColumn Zero-based index into the rows of the Matrix, must be 
     *         less than or equal to maxColumn
     * @param  maxColumn Zero-based index into the rows of the Matrix, must be 
     *         greater than or equal to minColumn
     * @return the Matrix of dimension (maxRow-minRow+1)x(maxColumn-minColumn+1)
     */
    public Matrix getSubMatrix(
        int minRow,
        int maxRow,
        int minColumn,
        int maxColumn );

    /**
     * Sets the submatrix inside of the Matrix, specified by the zero-based 
     * indices.
     *
     * @param  minRow Zero-based index into the rows of the Matrix, must be less 
     *         than or equal to maxRow
     * @param  minColumn Zero-based index into the rows of the Matrix, must be 
     *         less than or equal to maxColumn
     * @param  submatrix Matrix containing the values to set at the specified 
     *         indices
     */
    public void setSubMatrix(
        int minRow,
        int minColumn,
        Matrix submatrix );

    /**
     * Determines if the matrix is symmetric.
     *
     * @return true if the matrix is symmetric, false otherwise
     */
    public boolean isSymmetric();

    /**
     * Determines if the matrix is effectively symmetric 
     *
     * @param effectiveZero
     *          tolerance to determine symmetry
     * @return true if effectively symmetric, false otherwise
     */
    public boolean isSymmetric(
        double effectiveZero );

    /**
     * Checks to see if the dimensions are the same between <code>this</code> 
     * and <code>otherMatrix</code>
     *
     * @param otherMatrix 
     *          matrix against which to check
     * @return true if 
     *         <code>this.getNumRows() == otherMatrix.getNumRows()</code> and 
     *         <code>this.getNumColumns() == otherMatrix.getNumColumns()</code>
     */
    public boolean checkSameDimensions(
        Matrix otherMatrix );

    /**
     * Throws a DimensionalityMismatchException if dimensions between this
     * and otherMatrix aren't the same 
     *
     * @param otherMatrix
     *          Matrix dimensions to compare to this
     */
    public void assertSameDimensions(
        Matrix otherMatrix );

    /**
     * Checks to see if the dimensions are appropriate for:
     * <code>this.times( postMultiplicationMatrix )</code>
     *
     * @param postMultiplicationMatrix
     *          matrix by which <code>this</code> is to be multiplied
     * @return true if <code>this.getNumColumns() ==
     *          postMultlicationMatrix.getNumColumns()</code>, false otherwise
     */
    public boolean checkMultiplicationDimensions(
        Matrix postMultiplicationMatrix );

    /**
     * Matrix multiplication of <code>this</code> and <code>matrix</code>,
     * operates like the "<code>*</code>" operator in Matlab
     * 
     * @param matrix <code>this.getNumColumns()==matrix.getNumRows()</code>
     * @return Matrix multiplication of <code>this</code> and
     *         <code>matrix</code>, will <code>this.getNumRows()</code> rows and
     *         <code>matrix.getNumColumns()</code> columns
     */
    public Matrix times(
        final Matrix matrix );

    /**
     * Returns the transpose of <code>this</code> 
     *
     * @return Matrix whose elements are equivalent to:
     *         <code>this.getElement(i, j) == this.transpose().getElement(j, i)
     *         </code> for any valid <code>i, j</code>.
     */
    public Matrix transpose();

    /**
     * Computes the full-blown inverse of <code>this</code>, which must be a
     * square matrix
     * 
     * @return Inverse of <code>this</code>, such that
     *         <code>this.times(this.inverse()) == this.inverse().times(this)
     *         == </code>identity matrix
     */
    public Matrix inverse();

    /**
     * Computes the effective pseudo-inverse of <code>this</code>, using a
     * rather expensive procedure (SVD)
     *
     * @return full singular-value pseudo-inverse of <code>this</code>
     */
    public Matrix pseudoInverse();

    /**
     * Computes the effective pseudo-inverse of <code>this</code>, using a
     * rather expensive procedure (SVD)
     *
     * @param effectiveZero
     *          effective zero to pass along to the SVD
     *
     * @return effective pseudo-inverse of <code>this</code>
     */
    public Matrix pseudoInverse(
        double effectiveZero );

    /**
     * Computes the natural logarithm of the determinant of <code>this</code>.
     * Very computationally intensive.  Please THINK LONG AND HARD before
     * invoking this method on sparse matrices, as they have to be converted
     * to a DenseMatrix first.
     * 
     * @return natural logarithm of the determinant of <code>this</code>
     */
    public ComplexNumber logDeterminant();

    /**
     * Computes the trace of <code>this</code>, which is the sum of the
     * eigenvalues and if equivalent to the sum of the diagonal element (which
     * is probably the most interesting result in all of algebra!!)
     * 
     * @return trace of <code>this</code>
     */
    public double trace();

    /**
     * Computes the rank of <code>this</code>, which is the number of
     * linearly independent rows and columns in <code>this</code>.  Rank is
     * typically based on the SVD, which is a fairly computationally expensive
     * procedure and should be used carefully
     *
     * @return rank of <code>this</code>, equivalent to the number of linearly
     *         independent rows and columns in <code>this</code>
     */
    public int rank();

    /**
     * Computes the effective rank of <code>this</code>, which is the number of
     * linearly independent rows and columns in <code>this</code>.  Rank is
     * typically based on the SVD, which is a fairly computationally expensive
     * procedure and should be used carefully
     *
     * @param effectiveZero
     *          parameter to pass along to SVD to determine linear dependence
     *
     * @return rank of <code>this</code>, equivalent to the number of linearly
     *         indepenedent rows and columns in <code>this</code>
     */
    public int rank(
        double effectiveZero );

    /**
     * Compute the Frobenius norm of <code>this</code>, which is just a fancy
     * way of saying that I will square each element, add those up, and square
     * root the result.  This is probably the most intuitive of the matrix norms
     *
     * @return Frobenius norm of <code>this</code>
     */
    public double normFrobenius();

    /**
     * Determines if the matrix is square (numRows == numColumns) 
     *
     * @return true if square, false if nonsquare
     */
    public boolean isSquare();

    /**
     * Solves for "X" in the equation: this*X = B
     *
     * @param B Must satisfy this.getNumColumns() == B.getNumRows();
     * @return X Matrix with dimensions (this.getNumColumns() x B.getNumColumns())
     */
    public Matrix solve(
        final Matrix B );

    /**
     * Solves for "x" in the equation: this*x = b
     *
     * @param b must satisfy this.getNumColumns() == b.getDimensionality()
     * @return x Vector with dimensions (this.getNumColumns())
     */
    public Vector solve(
        final Vector b );

    /**
     * Formats the matrix as an identity matrix.  This does not have to be
     * square.
     */
    public void identity();

    /**
     * Returns the column vector from the equation
     * return = this * vector 
     *
     * @param vector
     *          Vector by which to post-multiply this, must have the
     *          same number of rows as this
     * @return Vector with the same dimensionality as the number of rows as
     *         this and vector
     */
    public Vector times(
        final Vector vector );

    /**
     * Gets the specified column from the zero-based index and returns a
     * vector that corresponds to that column.
     *
     * @param columnIndex
     *          zero-based index into the matrix
     * @return Vector with elements equal to the number of rows in this
     */
    public Vector getColumn(
        int columnIndex );

    /**
     * Gets the specified row from the zero-based index and returns a vector
     * that corresponds to that column 
     *
     * @param rowIndex
     *          zero-based index into the matrix
     * @return Vector with elements equal to the number of columns in this
     */
    public Vector getRow(
        int rowIndex );

    /**
     * Sets the specified column from the given columnVector 
     *
     * @param columnIndex
     *          zero-based index into the matrix
     * @param columnVector
     *          vector to replace in this, must have same number of elements
     *          as this has rows
     */
    public void setColumn(
        int columnIndex,
        Vector columnVector );

    /**
     * Sets the specified row from the given rowVector
     *
     * @param rowIndex
     *          zero-based index into the matrix
     * @param rowVector 
     *          vector to replace in this, must have the same number of elements
     *          as this has columns
     */
    public void setRow(
        int rowIndex,
        Vector rowVector );

    /**
     * Returns a new vector containing the sum across the rows.
     *
     * @return
     *      A new vector containing the sum of the rows. Its dimensionality is
     *      equal to the number of columns.
     * @since   3.0
     */
    public Vector sumOfRows();

    /**
     * Returns a new vector containing the sum across the columns.
     *
     * @return
     *      A new vector containing the sum of the columns. Its dimensionality
     *      is equal to the number of rows.
     * @since   3.0
     */
    public Vector sumOfColumns();

    /**
     * uploads a matrix from a column-stacked vector of parameters, so that
     * v(k) = A(i,j) = A( k%M, k/M )
     *
     * @param parameters column-stacked version of this
     */
    public void convertFromVector(
        Vector parameters );

    /**
     * Creates a column-stacked version of this, so that
     * v(k) = A(i,j) = v(j*M+i)
     *
     * @return column-stacked Vector representing this
     */
    public Vector convertToVector();

    /**
     * Converts this matrix to a new array of array of doubles, in the same
     * order as they are in the matrix. The returned will be safe in that no
     * references are maintained by this class.
     *
     * @return
     *      This matrix as a dense array. The length of the first layer of
     *      the array will be equal to the number of rows and the second
     *      layer will be equal to the number of columns.
     */
    public double[][] toArray();

    public String toString();

    /**
     * Converts the vector to a {@code String}, using the given formatter.
     *
     * @param   format The number format to use.
     * @return  The String representation of the Matrix.
     */
    public String toString(
        final NumberFormat format);

}
