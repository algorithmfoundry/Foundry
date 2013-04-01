/*
 * File:                AbstractSparseMatrix.java
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
import gov.sandia.cognition.math.OperationNotConvergedException;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import java.text.NumberFormat;
import no.uib.cipr.matrix.sparse.QMR;

/**
 * Implements some generic operations that any sparse-matrix representation
 * must do.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-19",
    changesNeeded=false,
    comments="Looks fine."
)
public abstract class AbstractSparseMatrix
    extends AbstractMTJMatrix
{

    /**
     * Creates a new instance of AbstractSparseMatrix using the given MTJ matrix
     * @param internalMatrix
     *          internal MTJ matrix to base this on
     */
    protected AbstractSparseMatrix(
        no.uib.cipr.matrix.Matrix internalMatrix )
    {
        super( internalMatrix );
    }

    /**
     * Compact the memory used by the matrix, getting rid of any zero elements
     */
    public abstract void compact();

    public SparseVector times(
        final AbstractMTJVector vector )
    {
        SparseVector answer = new SparseVector( this.getNumRows() );
        this.timesInto( vector, answer );
        return answer;
    }

    @Override
    public void setElement(
        int rowIndex,
        int columnIndex,
        double value )
    {
        // Only set a value in a SparseVector if it's different than what's
        // in there already!
        // (This is to prevent us from adding zeros into the SparseVector
        // unnecessarily)
        double existing = this.getElement( rowIndex, columnIndex );
        if (existing != value)
        {
            super.setElement( rowIndex, columnIndex, value );
        }

    }

    /**
     * This sparse-vector solver performs iterative solving for "x" in the
     * equation: this*x = b, and the AbstractSparseMatrix "this" can be
     * unstructured (e.g., asymmetric, indefinite, etc.)
     * @param b {@inheritDoc}
     * @return  {@inheritDoc}
     */
    @Override
    public SparseVector solve(
        final AbstractMTJVector b )
    {

        int M = this.getNumRows();
        int N = this.getNumColumns();

        AbstractSparseMatrix Asquare;
        int S = Math.max( M, N );
        if (M != N)
        {
            Asquare = new SparseMatrix( S, S );
            Asquare.setSubMatrix( 0, 0, this );
        }
        else
        {
            Asquare = this;
        }

        AbstractMTJVector bsquare;
        if (S != M)
        {
            bsquare = new SparseVector( S );
            for (int i = 0; i < M; i++)
            {
                bsquare.setElement( i, b.getElement( i ) );
            }
        }
        else
        {
            bsquare = b;
        }

        SparseVector xsquare = new SparseVector( Asquare.getNumColumns() );

        QMR sparseSolver = new QMR( xsquare.getInternalVector() );
        try
        {
            sparseSolver.solve( Asquare.getInternalMatrix(), bsquare.getInternalVector(), xsquare.getInternalVector() );
        }
        catch (Exception ex)
        {
            throw new OperationNotConvergedException(ex);
        }

        SparseVector x;
        if (S != N)
        {
            x = new SparseVector( N );
            for (int i = 0; i < N; i++)
            {
                x.setElement( i, xsquare.getElement( i ) );
            }
        }
        else
        {
            x = xsquare;
        }

        return x;
    }

    public DenseMatrix pseudoInverse(
        double effectiveZero )
    {
        DenseMatrix Ainternal = new DenseMatrix( this );
        return Ainternal.pseudoInverse( effectiveZero );
    }

    /**
     * Creates a string in the "(Row,Column): value" for the nonzero elements
     * @return String representing the sparse matrix
     */
    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder(100);

        for (MatrixEntry e : this)
        {
            result.append("(");
            result.append(e.getRowIndex());
            result.append(",");
            result.append(e.getColumnIndex());
            result.append("): ");
            result.append(e.getValue());
            result.append("\n");
        }

        if (result.length() == 0)
        {
            result.append("No nonzero entries");
        }

        return result.toString();
    }

    public String toString(
        final NumberFormat format)
    {
        final StringBuilder result = new StringBuilder();

        for (MatrixEntry e : this)
        {
            result.append("(");
            result.append(e.getRowIndex());
            result.append(",");
            result.append(e.getColumnIndex());
            result.append("): ");
            result.append(format.format(e.getValue()));
            result.append("\n");
        }

        if (result.length() == 0)
        {
            result.append("No nonzero entries");
        }

        return result.toString();
    }

    public SparseVector getColumn(
        int columnIndex )
    {
        int M = this.getNumRows();
        SparseVector columnVector = new SparseVector( M );
        for (int i = 0; i < M; i++)
        {
            double value = this.getElement( i, columnIndex );
            if (value != 0.0)
            {
                columnVector.setElement( i, value );
            }
        }
        return columnVector;
    }

    public SparseVector getRow(
        int rowIndex )
    {
        int N = this.getNumColumns();
        SparseVector rowVector = new SparseVector( N );
        for (int j = 0; j < N; j++)
        {
            double value = this.getElement( rowIndex, j );
            if (value != 0.0)
            {
                rowVector.setElement( j, value );
            }
        }
        return rowVector;
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

}
