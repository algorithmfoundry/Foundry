/*
 * File:                AbstractMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.AbstractRing;
import gov.sandia.cognition.math.RingAccumulator;

/**
 * Abstract implementation of some low-hanging functions in the Matrix 
 * interface. It contains default implementations of these functions that make
 * extensive use of the setElement method, which it assumes to be a fast
 * lookup function.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments={
                "Minor changes to formatting and documentation for equals().",
                "Otherwise, looks good."
            }
        ),    
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=true,
            comments="A few minor changes.",
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2006-05-16",
                moreChangesNeeded=false,
                comments="Added a line to the class documentation that says that setElement is used extensively in the abstract implementation."
            )
        )
    }
)
public abstract class AbstractMatrix
    extends AbstractRing<Matrix>
    implements Matrix
{

    /** 
     * Creates a new instance of AbstractMatrix.
     */
    public AbstractMatrix()
    {
        super();
    }

    @Override
    public boolean equals(
        Object other )
    {
        if (other == null)
        {
            return false;
        }
        else if (this == other)
        {
            return true;
        }
        else if (other instanceof Matrix)
        {
            return this.equals( (Matrix) other, 0.0 );
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean equals(
        Matrix other,
        double effectiveZero)
    {
        final int M = this.getNumRows();
        final int N = this.getNumColumns();
        
        if (M != other.getNumRows() || N != other.getNumColumns())
        {
            return false;
        }
        
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                double difference = this.getElement(i,j) - other.getElement(i,j);
                if( Math.abs(difference) > effectiveZero )
                {
                    return false;
                }
            }
        }

        return true;
    }

    
    @Override
    public int hashCode()
    {
        final int numRows = this.getNumRows();
        final int numColumns = this.getNumColumns();
        int result = 7 + numRows * numColumns;
        
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numColumns; j++)
            {
                
                final double value = this.getElement(i, j);

                // We add a check for zero so that sparse matrices can create
                // the same hash-code without needing to handle the zeros.
                if (value != 0.0)
                {
                    final long bits = Double.doubleToLongBits(value);
                    
                    // This is based on how NetBeans auto-generates hash codes
                    // plus the documentation for Map.Entry.
                    result = 47 * result + i ^ j ^ (int) (bits ^ (bits >>> 32));
                }
            }
        }
        
        return result;
    }

    @Override
    public boolean isSymmetric()
    {
        return this.isSymmetric( 0.0 );
    }

    @Override
    public boolean checkSameDimensions(
        Matrix otherMatrix )
    {
        return (this.getNumRows() == otherMatrix.getNumRows()) &&
            (this.getNumColumns() == otherMatrix.getNumColumns());
    }

    @Override
    public void assertSameDimensions(
        Matrix other )
    {
        if (this.checkSameDimensions( other ) == false)
        {
            throw new DimensionalityMismatchException(
                "Matrices must have same dimensions: " +
                "(" + this.getNumRows() + "x" + this.getNumColumns() + ") !=" +
                "(" + other.getNumRows() + "x" + other.getNumColumns() + ")" );
        }
    }

    @Override
    public boolean checkMultiplicationDimensions(
        Matrix postMultiplicationMatrix )
    {
        return this.getNumColumns() == postMultiplicationMatrix.getNumRows();
    }

    @Override
    public double trace()
    {

        if (this.isSquare() == false)
        {
            throw new DimensionalityMismatchException(
                "Matrix must be square for trace()" );
        }

        // find the min dimension... should be same, but this is more robust
        int M = this.getNumRows();
        int N = this.getNumColumns();
        int D = (M < N) ? M : N;

        double diagonal_sum = 0.0;
        for (int i = 0; i < D; i++)
        {
            diagonal_sum += this.getElement( i, i );
        }

        return diagonal_sum;
    }

    @Override
    public void setSubMatrix(
        final int minRow,
        final int minColumn,
        final Matrix submatrix )
    {

        final int M = submatrix.getNumRows();
        final int N = submatrix.getNumColumns();
        for (int i = 0; i < M; i++)
        {
            final int setRow = minRow+i;
            for (int j = 0; j < N; j++)
            {
                final double v = submatrix.getElement( i, j );
                this.setElement( setRow, minColumn + j, v );
            }
        }
    }

    @Override
    public int rank()
    {
        return this.rank( 0.0 );
    }

    @Override
    public Matrix pseudoInverse()
    {
        return this.pseudoInverse( 0.0 );
    }

    @Override
    public void setColumn(
        int columnIndex,
        Vector columnVector )
    {

        int M = this.getNumRows();
        if (M != columnVector.getDimensionality())
        {
            throw new DimensionalityMismatchException(
                M, columnVector.getDimensionality() );
        }

        for (int i = 0; i < M; i++)
        {
            this.setElement( i, columnIndex, columnVector.getElement( i ) );
        }
    }

    @Override
    public void setRow(
        int rowIndex,
        Vector rowVector )
    {

        int N = this.getNumColumns();
        if (N != rowVector.getDimensionality())
        {
            throw new DimensionalityMismatchException(
                N, rowVector.getDimensionality() );
        }

        for (int j = 0; j < N; j++)
        {
            this.setElement( rowIndex, j, rowVector.getElement( j ) );
        }

    }

    /**
     * Internal function that writes the column onto the destinationVector.
     * Gets the specified column from the zero-based index and returns a
     * vector that corresponds to that column.
     *
     * @param columnIndex
     *          zero-based index into the matrix
     * @param destinationVector
     *          Vector with elements equal to the number of rows in this
     */
    protected void getColumnInto(
        int columnIndex,
        Vector destinationVector )
    {
        int M = this.getNumRows();
        if (M != destinationVector.getDimensionality())
        {
            throw new DimensionalityMismatchException(
                M, destinationVector.getDimensionality() );
        }

        for (int i = 0; i < M; i++)
        {
            destinationVector.setElement( i, this.getElement( i, columnIndex ) );
        }
    }

    /**
     * Internal function that writes the row onto the destinationVector.
     * Gets the specified row from the zero-based index and returns a vector
     * that corresponds to that column 
     * 
     * @param rowIndex zero-based index into the matrix
     * @param destinationVector 
     *          Vector with elements equal to the number of columns in this
     */
    protected void getRowInto(
        int rowIndex,
        Vector destinationVector )
    {
        int N = this.getNumColumns();
        if (N != destinationVector.getDimensionality())
        {
            throw new DimensionalityMismatchException(
                N, destinationVector.getDimensionality() );
        }

        for (int j = 0; j < N; j++)
        {
            destinationVector.setElement( j, this.getElement( rowIndex, j ) );
        }
    }

    @Override
    public Vector sumOfRows()
    {
        final RingAccumulator<Vector> accumulator = new RingAccumulator<Vector>();
        final int numRows = this.getNumRows();

        for (int i = 0; i < numRows; i++)
        {
            accumulator.accumulate(this.getRow(i));
        }

        return accumulator.getSum();
    }

    @Override
    public Vector sumOfColumns()
    {
        final RingAccumulator<Vector> accumulator = new RingAccumulator<Vector>();
        final int numColumns = this.getNumColumns();

        for (int j = 0; j < numColumns; j++)
        {
            accumulator.accumulate(this.getColumn(j));
        }
        return accumulator.getSum();
    }


    @Override
    public boolean isZero(
        final double effectiveZero)
    {
        for (MatrixEntry e : this)
        {
            if (Math.abs(e.getValue()) > effectiveZero)
            {
                return false;
            }
        }

        return true;
    }
}
