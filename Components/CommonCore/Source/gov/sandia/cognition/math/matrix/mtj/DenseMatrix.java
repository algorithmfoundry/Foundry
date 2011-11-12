/*
 * File:                DenseMatrix.java
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
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.MatrixReader;
import gov.sandia.cognition.math.matrix.Vector;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Matrix that represents all its entries using a fixed-size storage scheme,
 * based on MTJ's DenseMatrix storage class.  Fast iteration, access, and
 * arithmetic, but storage size scale with the size of the number of rows and
 * columns.
 * <BR><BR>
 * To create a new DenseMatrix, please use the DenseMatrixFactoryMTJ static
 * calls, or the DefaultRingFactory class.  This is there to provide a layer
 * of abstraction and make it easier for people to drop in new matrix packages
 * 
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-19",
    changesNeeded=true,
    comments="Comments indicated with / / / ",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-22",
        moreChangesNeeded=false,
        comments="Fixed comments for writeObject and readObject"
    )
)
@PublicationReference(
    author="Bjorn-Ove Heimsund",
    title="Matrix Toolkits for Java DenseMatrix",
    type=PublicationType.WebPage,
    year=2006,
    url="http://ressim.berlios.de/doc/no/uib/cipr/matrix/DenseMatrix.html",
    notes="This class wraps the DenseMatrix class from Heimsund's MTJ package"
)
public class DenseMatrix
    extends AbstractMTJMatrix
    implements Serializable
{
    /**
     * Creates a new instance of DenseMatrix
     * @param numRows number of rows in the matrix
     * @param numColumns number of columns in the matrix
     */
    protected DenseMatrix(
        int numRows,
        int numColumns )
    {        
        super( 
            new Object() {
                no.uib.cipr.matrix.DenseMatrix checkedCreateMatrix(
                    final int numRows,
                    final int numColumns)
                {
                    // Need to check for integer overflow because 
                    // no.uib.cipr.matrix.DenseMatrix creates a matrix as an 
                    // array of doubles with length numRows * numColumns and 
                    // Java overflows silently.  This method will throw an 
                    // exception if an overflow will occur
                    MathUtil.checkedMultiply(numRows, numColumns);
                    return new no.uib.cipr.matrix.DenseMatrix(numRows,
                        numColumns);
                }
            }.checkedCreateMatrix(numRows, numColumns)
        );
    }

    /**
     * Creates a new instance of DenseMatrix
     * @param internalMatrix Internal MTJ-based matrix
     */
    protected DenseMatrix(
        no.uib.cipr.matrix.DenseMatrix internalMatrix )
    {
        super( internalMatrix );
    }

    /**
     * Creates a new instance of DenseMatrix
     * @param matrix Matrix from which to pull data, will not be modified
     */
    protected DenseMatrix(
        Matrix matrix )
    {
        this( matrix.getNumRows(), matrix.getNumColumns() );

        for (MatrixEntry e : matrix)
        {
            this.setElement( e.getRowIndex(), e.getColumnIndex(),
                e.getValue() );
        }
    }

    /**
     * Creates a new instance of DenseMatrix
     * @param matrix DenseMatrix from which to pull data, will not be modified
     */
    protected DenseMatrix(
        DenseMatrix matrix )
    {
        this( new no.uib.cipr.matrix.DenseMatrix(
            (no.uib.cipr.matrix.DenseMatrix) matrix.getInternalMatrix().copy() ) );
    }

    /**
     * Creates a new instance of DenseMatrix
     * @param reader takes in information from a java stream
     * @throws java.io.IOException if the stream is invalid
     */
    protected DenseMatrix(
        MatrixReader reader ) throws IOException
    {
        this( reader.read() );
    }

    @Override
    public DenseMatrix clone()
    {
        return (DenseMatrix) super.clone();
    }
    
    public DenseMatrix times(
        final AbstractMTJMatrix matrix )
    {

        int returnRows = this.getNumRows();
        int returnColumns = matrix.getNumColumns();

        DenseMatrix retval = new DenseMatrix( returnRows, returnColumns );
        this.timesInto( matrix, retval );
        return retval;

    }

    public DenseVector times(
        final AbstractMTJVector vector )
    {
        DenseVector answer = new DenseVector( this.getNumRows() );
        this.timesInto( vector, answer );
        return answer;
    }

    /**
     * Solve for "X" in the equation: this*X = B
     * @param B must satisfy this.getNumRows() == B.numRows()
     * @param X must satisfy this.getNumColumns() == X.getNumRows()
     */
    protected void solveInto(
        final DenseMatrix B,
        DenseMatrix X )
    {
        this.getInternalMatrix().solve(
            B.getInternalMatrix(), X.getInternalMatrix() );
    }

    /**
     * Solve for "x" in the equation: this*x = b
     * @param b must satisfy this.getNumRows() == b.getDimensionality()
     * @param x must satisfy this.getNumColumns() == x.getDimensionality()
     */
    protected void solveInto(
        final DenseVector b,
        DenseVector x )
    {
        this.getInternalMatrix().solve(
            b.getInternalVector(), x.getInternalVector() );
    }

    public DenseMatrix pseudoInverse(
        double effectiveZero )
    {
        SingularValueDecompositionMTJ svd =
            SingularValueDecompositionMTJ.create( this );

        return (DenseMatrix) svd.pseudoInverse( effectiveZero );
    }

    public DenseMatrix getSubMatrix(
        int minRow,
        int maxRow,
        int minColumn,
        int maxColumn )
    {

        int numRows = maxRow - minRow + 1;
        if (numRows <= 0)
        {
            throw new IllegalArgumentException( "minRow " + minRow +
                " >= maxRow " + maxRow );
        }
        int numColumns = maxColumn - minColumn + 1;
        if (numColumns <= 0)
        {
            throw new IllegalArgumentException( "minCol " + minColumn +
                " >= maxCol " + maxColumn );
        }
        DenseMatrix submatrix = new DenseMatrix( numRows, numColumns );
        this.getSubMatrixInto(
            minRow, maxRow, minColumn, maxColumn, submatrix );

        return submatrix;
    }

    public DenseMatrix transpose()
    {
        DenseMatrix retval = new DenseMatrix(
            this.getNumColumns(), this.getNumRows() );
        this.transposeInto( retval );
        return retval;
    }

    /**
     * Writes the DenseMatrix out as a matrix of values (no indices are printed)
     * @return String representing the DenseMatrix
     */
    @Override
    public String toString()
    {
        final StringBuilder result =
            new StringBuilder(MathUtil.checkedMultiply(10, 
                MathUtil.checkedMultiply(this.getNumRows(),
                this.getNumColumns())));

        for (int i = 0; i < this.getNumRows(); i++)
        {
            for (int j = 0; j < this.getNumColumns(); j++)
            {
                result.append(" ");
                result.append(this.getElement(i, j));
            }
            result.append("\n");
        }

        return result.toString();
    }

    public String toString(
        final NumberFormat format)
    {
        final StringBuilder result =
            new StringBuilder(MathUtil.checkedMultiply(5, 
                MathUtil.checkedMultiply(this.getNumRows(),
                this.getNumColumns())));

        for (int i = 0; i < this.getNumRows(); i++)
        {
            for (int j = 0; j < this.getNumColumns(); j++)
            {
                result.append(" ");
                result.append(format.format(this.getElement(i, j)));
            }
            result.append("\n");
        }

        return result.toString();
    }

    public DenseVector getColumn(
        int columnIndex )
    {

        int M = this.getNumRows();

        DenseVector columnVector = new DenseVector( M );
        this.getColumnInto( columnIndex, columnVector );
        return columnVector;

    }

    public DenseVector getRow(
        int rowIndex )
    {

        int N = this.getNumColumns();

        DenseVector rowVector = new DenseVector( N );
        this.getRowInto( rowIndex, rowVector );
        return rowVector;
    }

    /**
     * Writes a DenseMatrix out to a serialized file
     * @param out output stream to which the DenseMatrix will be written
     * @throws java.io.IOException On bad write
     */
    private void writeObject(
        ObjectOutputStream out )
        throws IOException
    {
        out.defaultWriteObject();

        //manually serialize superclass
        int numRows = this.getNumRows();
        int numColumns = this.getNumColumns();
        double[][] data = new double[numRows][numColumns];
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numColumns; j++)
            {
                data[i][j] = this.getElement( i, j );
            }
        }

        out.writeObject( data );
    }

    /**
     * Reads in a serialized class from the specified stream
     * @param in stream from which to read the DenseMatrix
     * @throws java.io.IOException On bad read
     * @throws java.lang.ClassNotFoundException if next object isn't DenseMatrix
     */
    private void readObject(
        ObjectInputStream in )
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();

        double[][] data = (double[][]) in.readObject();
        this.setInternalMatrix(
            new no.uib.cipr.matrix.DenseMatrix( data ) );
    }

    /**
     * Gets the underlying double array from the MTJ matrix
     * @return double array from the underlying MTJ matrix
     */
    protected double[] getArray()
    {
        return ((no.uib.cipr.matrix.DenseMatrix) this.getInternalMatrix()).getData();
    }

    @Override
    public DenseVector convertToVector()
    {
        return new DenseVector( this.getArray() );
    }

    @Override
    public void convertFromVector(
        Vector parameters )
    {
        if (parameters instanceof DenseVector)
        {
            this.convertFromVector( (DenseVector) parameters );
        }
        else
        {
            super.convertFromVector( parameters );
        }
    }

    /**
     * Incorporates the parameters in the given vector back into the object.
     *
     * @param parameters The parameters to incorporate.
     */
    public void convertFromVector(
        DenseVector parameters )
    {

        double[] myParameters = this.getArray();
        double[] vectorParameters = parameters.getArray();

        if (myParameters.length != vectorParameters.length)
        {
            throw new IllegalArgumentException( "Dimensions do not match" );
        }

        for (int i = 0; i < myParameters.length; i++)
        {
            myParameters[i] = vectorParameters[i];
        }

    }

}
