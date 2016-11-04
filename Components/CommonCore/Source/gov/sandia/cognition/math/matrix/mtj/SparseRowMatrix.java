/*
 * File:                SparseRowMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 * A sparse matrix, represented as a collection of sparse row vectors. 
 * Generally, this is the fastest sparse matrix for premultiplying against
 * a vector.
 * 
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-27",
    changesNeeded=false,
    comments="Looks good."
)
public class SparseRowMatrix
    extends AbstractSparseMatrix
{

    /**
     * Creates a new empty instance of SparseRowMatrix.
     * 
     * @param numRows Number of rows in the matrix.
     * @param numColumns Number of columns in the matrix.
     */
    protected SparseRowMatrix(
        int numRows,
        int numColumns )
    {
        this( new no.uib.cipr.matrix.sparse.FlexCompRowMatrix(
            numRows, numColumns ) );

        if (numRows < 0)
        {
            throw new IllegalArgumentException( "Num rows must be >= 0" );
        }
        if (numColumns < 0)
        {
            throw new IllegalArgumentException( "Num columns must be >= 0" );
        }

    }

    /**
     * Copy constructor for SparseRowMatrix matrices
     * 
     * @param matrix Matrix from which to copy the internal MTJ matrix.
     */
    protected SparseRowMatrix(
        SparseRowMatrix matrix )
    {
        this( (FlexCompRowMatrix) matrix.getInternalMatrix().copy() );
    }

    /**
     * Copy constructor for general matrices, copies over nonzero values.
     * 
     * @param matrix Matrix from which to copy the nonzero elements into this.
     */
    protected SparseRowMatrix(
        Matrix matrix )
    {
        this( matrix.getNumRows(), matrix.getNumColumns() );

        for (MatrixEntry e : matrix)
        {
            double value = e.getValue();

            if (value != 0.0)
            {
                this.setElement( e.getRowIndex(), e.getColumnIndex(), value );
            }
        }
    }

    /**
     * Creates a SparseRowMatrix based on the appropriate MTJ matrix,
     * does NOT create a copy of internalMatrix.
     * 
     * @param internalMatrix New internal matrix for this, no copy made.
     */
    protected SparseRowMatrix(
        FlexCompRowMatrix internalMatrix )
    {
        super( internalMatrix );
    }

    @Override
    public FlexCompRowMatrix getInternalMatrix()
    {
        return (FlexCompRowMatrix) super.getInternalMatrix();
    }

    public SparseColumnMatrix transpose()
    {
        SparseColumnMatrix result = new SparseColumnMatrix(
            this.getNumColumns(), this.getNumRows() );
        this.transposeInto( result );
        return result;
    }

    public SparseRowMatrix times(
        final AbstractMTJMatrix matrix )
    {
        int returnRows = this.getNumRows();
        int returnColumns = matrix.getNumColumns();

        SparseRowMatrix result = new SparseRowMatrix(
            returnRows, returnColumns );

        this.timesInto( matrix, result );
        return result;

    }

    public SparseRowMatrix getSubMatrix(
        int minRow,
        int maxRow,
        int minColumn,
        int maxColumn )
    {

        if (minRow > maxRow)
        {
            throw new IllegalArgumentException( "minRow > maxRow" );
        }
        if (minColumn > maxColumn)
        {
            throw new IllegalArgumentException( "minColumn > maxColumn" );
        }
        int numRows = maxRow - minRow + 1;
        int numColumns = maxColumn - minColumn + 1;
        SparseRowMatrix submatrix = new SparseRowMatrix( numRows, numColumns );
        this.getSubMatrixInto(
            minRow, maxRow, minColumn, maxColumn, submatrix );
        return submatrix;
    }

    /**
     * Gets the specified row of the matrix, using MTJ's internal routine
     * to speed things up.
     *
     * @param rowIndex Zero-based row index.
     * @return SparseVector with the same dimensions as this has columns.
     */
    @Override
    public SparseVector getRow(
        int rowIndex )
    {
        return new SparseVector( this.getInternalMatrix().getRow( rowIndex ) );
    }

    /**
     * Sets the specified row of the matrix using rowVector, using MTJ's
     * internal routine to speed things up.
     *
     * @param rowIndex Zero-based row index.
     * @param rowVector SparseVector containing the elements to replace in 
     *        this.
     */
    public void setRow(
        int rowIndex,
        SparseVector rowVector )
    {
        this.getInternalMatrix().setRow(
            rowIndex, rowVector.getInternalVector() );
    }

    public void compact()
    {
        this.getInternalMatrix().compact();
    }

    @Override
    public int getEntryCount()
    {
        final FlexCompRowMatrix m = this.getInternalMatrix();
        final int rowCount = this.getNumRows();
        int result = 0;
        for (int i = 0; i < rowCount; i++)
        {
            result += m.getRow(i).getUsed();
        }
        return result;
    }

    /**
     * Custom deserialization is needed.
     *
     * @param in The ObjectInputStream to deserialize from.
     * @throws java.io.IOException If there is an error with the stream.
     * @throws java.lang.ClassNotFoundException If a class used by this one 
     *         cannot be found.
     */
    private void readObject(
        ObjectInputStream in )
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        int numRows = in.readInt();
        int numCols = in.readInt();
        this.setInternalMatrix( new FlexCompRowMatrix( numRows, numCols ) );

        for (int i = 0; i < numRows; i++)
        {
            SparseVector row = (SparseVector) in.readObject();
            this.setRow( i, row );
        }

        this.compact();

    }


    /**
     * Custom serialization is needed.
     *
     * @param out The ObjectOutputStream to write this object to.
     * @throws java.io.IOException If there is an error writing to the stream.
     */
    private void writeObject(
        ObjectOutputStream out )
        throws IOException
    {

        this.compact();

        out.defaultWriteObject();

        int numRows = this.getNumRows();
        int numCols = this.getNumColumns();
        out.writeInt( numRows );
        out.writeInt( numCols );
        for (int i = 0; i < numRows; i++)
        {
            out.writeObject( this.getRow( i ) );
        }
    }

    @Override
    public MatrixFactory<?> getMatrixFactory()
    {
        return SparseMatrixFactoryMTJ.INSTANCE;
    }
    
}
