/*
 * File:                SparseColumnMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 11, 2006, Sandia Corporation.  Under the terms of Contract
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;

/**
 * A sparse matrix, represented as a collection of sparse column vectors. 
 * Generally, this is the slowest sparse matrix for premultiplying against
 * a vector.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-27",
    changesNeeded=false,
    comments="Looks good."
)
public class SparseColumnMatrix
    extends AbstractSparseMatrix
{

    /**
     * Creates a new empty instance of SparseColumnMatrix.
     * 
     * @param numRows Number of rows in the matrix.
     * @param numColumns Number of columns in the matrix.
     */
    protected SparseColumnMatrix(
        int numRows,
        int numColumns )
    {
        this( new no.uib.cipr.matrix.sparse.FlexCompColMatrix(
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
     * Copy constructor for SparseColumnMatrix matrices.
     * 
     * @param matrix Matrix from which to copy the internal MTJ matrix.
     */
    protected SparseColumnMatrix(
        SparseColumnMatrix matrix )
    {
        this( matrix.getInternalMatrix().copy() );
    }

    /**
     * Copy constructor for general matrices, copies over nonzero values.
     * 
     * @param matrix Matrix from which to copy the nonzero elements into this.
     */
    protected SparseColumnMatrix(
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
     * Creates a SparseColumnMatrix based on the appropriate MTJ matrix,
     * does NOT create a copy of internalMatrix.
     * 
     * @param internalMatrix New internal matrix for this, no copy made.
     */
    protected SparseColumnMatrix(
        FlexCompColMatrix internalMatrix )
    {
        super( internalMatrix );
    }

    @Override
    public FlexCompColMatrix getInternalMatrix()
    {
        return (FlexCompColMatrix) super.getInternalMatrix();
    }

    public SparseColumnMatrix times(
        final AbstractMTJMatrix matrix )
    {
        int returnRows = this.getNumRows();
        int returnColumns = matrix.getNumColumns();

        SparseColumnMatrix result =
            new SparseColumnMatrix( returnRows, returnColumns );

        this.getInternalMatrix().mult( matrix.getInternalMatrix(),
            result.getInternalMatrix() );

        return result;
    }

    public SparseColumnMatrix getSubMatrix(
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
        SparseColumnMatrix submatrix =
            new SparseColumnMatrix( numRows, numColumns );

        this.getSubMatrixInto(
            minRow, maxRow, minColumn, maxColumn, submatrix );
        return submatrix;
    }

    /**
     * Gets the specified column of the matrix, using MTJ's internal routine
     * to speed things up 
     *
     * @param columnIndex
     *          zero-based column index 
     * @return SparseVector with the same dimensions as this has rows
     */
    @Override
    public SparseVector getColumn(
        int columnIndex )
    {
        return new SparseVector(
            this.getInternalMatrix().getColumn( columnIndex ) );
    }

    /**
     * Sets the column of the matrix using the given SparseVector, using MTJ's
     * internal routine to speed things up 
     *
     * @param columnIndex Zero-based column index.
     * @param columnVector SparseVector containing the elements to replace in 
     *        this.
     */
    public void setColumn(
        int columnIndex,
        SparseVector columnVector )
    {
        this.getInternalMatrix().setColumn(
            columnIndex, columnVector.getInternalVector() );
    }

    public SparseRowMatrix transpose()
    {
        SparseRowMatrix result = new SparseRowMatrix(
            this.getNumColumns(), this.getNumRows() );
        this.transposeInto( result );
        return result;
    }

    public void compact()
    {
        this.getInternalMatrix().compact();
    }

    @Override
    public int getEntryCount()
    {
        final FlexCompColMatrix m = this.getInternalMatrix();
        final int columnCount = this.getNumColumns();
        int result = 0;
        for (int i = 0; i < columnCount; i++)
        {
            result += m.getColumn(i).getUsed();
        }
        return result;
    }
    
    /**
     * Custom deserialization is needed.
     *
     * @param in The ObjectInputStream to deseralize from.
     * @throws java.io.IOException If there is an error with the stream.
     * @throws java.lang.ClassNotFoundException If a class used by this one 
     *         cannot be found.
     */
    private void readObject(
        ObjectInputStream in )
        throws IOException, ClassNotFoundException
    {
        // Do the default stuff.
        in.defaultReadObject();

        // Read the meta-data.
        int numRows = in.readInt();
        int numCols = in.readInt();

        // Set the internal matrix.
        this.setInternalMatrix( new FlexCompColMatrix( numRows, numCols ) );

        // Read the rows.
        for (int i = 0; i < numCols; i++)
        {
            SparseVector col = (SparseVector) in.readObject();
            this.setColumn( i, col );
        }

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
        out.defaultWriteObject();

        int numRows = this.getNumRows();
        int numCols = this.getNumColumns();
        out.writeInt( numRows );
        out.writeInt( numCols );
        for (int i = 0; i < numCols; i++)
        {
            out.writeObject( this.getColumn( i ) );
        }
    }

}
