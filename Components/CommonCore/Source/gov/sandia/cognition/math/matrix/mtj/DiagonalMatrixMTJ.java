/*
 * File:                DiagonalMatrixMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;

/**
 * A diagonal matrix that wraps MTJ's BandMatrix class.
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReference(
    author="Bjorn-Ove Heimsund",
    title="Matrix Toolkits for Java BandMatrix",
    type=PublicationType.WebPage,
    year=2006,
    url="http://ressim.berlios.de/doc/no/uib/cipr/matrix/BandMatrix.html",
    notes="This class wraps the BandMatrix class from Heimsund's MTJ package"
)
public class DiagonalMatrixMTJ 
    extends AbstractMTJMatrix
    implements DiagonalMatrix
{
    
    /** 
     * Creates a new instance of DiagonalMatrixMTJ 
     * @param dim 
     * Dimensionality of the square diagonal matrix
     */
    protected DiagonalMatrixMTJ(
        int dim )
    {
        super( new no.uib.cipr.matrix.BandMatrix( dim, 0, 0 ) );
    }

    /**
     * Copy Constructor
     * @param other DiagonalMatrixMTJ to copy
     */
    protected DiagonalMatrixMTJ(
        DiagonalMatrixMTJ other )
    {
        super( other.getInternalMatrix().copy() );
    }
    
    /**
     * Creates a new instance of DiagonalMatrixMTJ 
     * @param diagonal
     * Array of elements to set the diagonal to
     */
    protected DiagonalMatrixMTJ(
        double[] diagonal )
    {
        this( diagonal.length );
        double[] actual = this.getDiagonal();
        System.arraycopy(diagonal, 0, actual, 0, diagonal.length);
    }

    public int getDimensionality()
    {
        return this.getNumRows();
    }
    
    @Override
    public no.uib.cipr.matrix.BandMatrix getInternalMatrix()
    {
        return (no.uib.cipr.matrix.BandMatrix) super.getInternalMatrix();
    }

    @Override
    protected void setInternalMatrix(
        no.uib.cipr.matrix.Matrix internalMatrix )
    {
        super.setInternalMatrix( (no.uib.cipr.matrix.BandMatrix) internalMatrix );
    }
    
    @Override
    public AbstractMTJMatrix times(
        AbstractMTJMatrix matrix )
    {
        this.assertMultiplicationDimensions(matrix);
        
        final int M = this.getNumRows();
        final int N = matrix.getNumColumns();
        
        DenseMatrix retval = DenseMatrixFactoryMTJ.INSTANCE.createMatrix( M, N );        
        
        double[] diagonal = this.getDiagonal();
        
        // The diagonal elements scale each row
        for( int i = 0; i < M; i++ )
        {
            final double di = diagonal[i];
            if( di != 0.0 )
            {
                for( int j = 0; j < N; j++ )
                {
                    double vij = matrix.getElement( i, j );
                    if( vij != 0.0 )
                    {
                        retval.setElement( i, j, di*vij );
                    }
                }
            }
        }
        
        return retval;
        
    }

    @Override
    public DiagonalMatrixMTJ times(
        DiagonalMatrix matrix)
    {
        DiagonalMatrixMTJ clone = (DiagonalMatrixMTJ) this.clone();
        clone.timesEquals(matrix);
        return clone;
    }

    @Override
    public void timesEquals(
        DiagonalMatrix matrix)
    {

        if( !this.checkSameDimensions(matrix) )
        {
            throw new IllegalArgumentException( "Matrix must be the same size as this" );
        }

        final int M = this.getDimensionality();

        // The diagonal elements scale each row
        for( int i = 0; i < M; i++ )
        {
            final double d1i = this.getElement(i);
            final double d2j = matrix.getElement(i);
            final double v = d1i * d2j;

            this.setElement(i, v);
        }

    }

    @Override
    public AbstractMTJVector times(
        AbstractMTJVector vector )
    {
        final int M = this.getDimensionality();
        if( M != vector.getDimensionality() )
        {
            throw new IllegalArgumentException(
                "Number of Columns != vector.getDimensionality()" );
        }
        double[] diagonal = this.getDiagonal();
        double[] retval = new double[ diagonal.length ];
        for( int i = 0; i < M; i++ )
        {
            final double v2 = diagonal[i];
            if( v2 != 0.0 )
            {
                final double v1 = vector.getElement( i );
                if( v1 != 0.0 )
                {
                    retval[i] = v1*v2;
                }
            }
        }
  
        return DenseVectorFactoryMTJ.INSTANCE.copyArray( retval );
        
    }

    @Override
    public DiagonalMatrixMTJ dotTimes(
        Matrix matrix )
    {
        DiagonalMatrixMTJ clone = (DiagonalMatrixMTJ) this.clone();
        clone.dotTimesEquals( matrix );
        return clone;
    }    
    
    @Override
    public void dotTimesEquals(
        AbstractMTJMatrix matrix )
    {
        
        this.assertSameDimensions( matrix );
        
        int M = this.getDimensionality();
        double[] diagonal = this.getDiagonal();
        for( int i = 0; i < M; i++ )
        {
            diagonal[i] *= matrix.getElement( i, i );
        }

    }

    @Override
    public boolean isSquare()
    {
        return true;
    }

    @Override
    public boolean isSymmetric()
    {
        return true;
    }

    @Override
    public boolean isSymmetric(
        double effectiveZero )
    {
        return true;
    }

    @Override
    public ComplexNumber logDeterminant()
    {
        // The diagonal elements (for either LU or QR) will be REAL, but
        // they may be negative.  The logarithm of a negative number is
        // the logarithm of the absolute value of the number, with an
        // imaginary part of PI.  The exponential is all that matters, so
        // the log-determinant is equivalent, MODULO PI (3.14...), so
        // we just toggle this sign bit.
        double logsum = 0.0;
        int sign = 1;
        double[] diagonal = this.getDiagonal();
        for (int i = 0; i < diagonal.length; i++)
        {
            double eigenvalue = diagonal[i];
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

    @Override
    public double normFrobenius()
    {
        
        double[] diagonal = this.getDiagonal();
        double sum = 0.0;
        for( int i = 0; i < diagonal.length; i++ )
        {
            double v = diagonal[i];
            sum += v*v;
        }
        
        return Math.sqrt( sum );
        
    }

    @Override
    public int rank(
        double effectiveZero )
    {
        int rank = 0;
        double[] diagonal = this.getDiagonal();
        int M = diagonal.length;
        for( int i = 0; i < M; i++ )
        {
            if( Math.abs( diagonal[i] ) > effectiveZero )
            {
                rank++;
            }
        }
        
        return rank;
        
    }

    @Override
    public Vector solve(
        AbstractMTJVector b )
    {
        DiagonalMatrixMTJ pinvA = this.pseudoInverse();
        return pinvA.times( b );
    }

    @Override
    public Matrix solve(
        Matrix B )
    {
        DiagonalMatrixMTJ pinvA = this.pseudoInverse();
        return pinvA.times( B );
    }

    @Override
    public Vector solve(
        Vector b )
    {
        DiagonalMatrixMTJ pinvA = this.pseudoInverse();
        return pinvA.times( b );
    }
    
    public SparseMatrix getSubMatrix(
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
        
        SparseMatrix submatrix = 
            SparseMatrixFactoryMTJ.INSTANCE.createMatrix( numRows, numColumns );
        this.getSubMatrixInto(
            minRow, maxRow, minColumn, maxColumn, submatrix );

        return submatrix;
    }

    public DiagonalMatrixMTJ transpose()
    {
        return this;
    }
    
    /**
     * Gets the data along the diagonal
     * @return
     * Diagonal data
     */
    public double[] getDiagonal()
    {
        return this.getInternalMatrix().getData();
    }

    @Override
    public DiagonalMatrixMTJ pseudoInverse()
    {
        return this.pseudoInverse( 0.0 );
    }
    
    public DiagonalMatrixMTJ pseudoInverse(
        double effectiveZero )
    {
        double[] diagonal = this.getDiagonal();
        int M = diagonal.length;
        double[] retval = new double[ M ];
        for( int i = 0; i < M; i++ )
        {
            double di = diagonal[i];
            if( Math.abs( di ) <= effectiveZero )
            {
                retval[i] = 0.0;
            }
            else
            {
                retval[i] = 1.0 / di;
            }
        }
        
        return new DiagonalMatrixMTJ( retval );
        
    }

    @Override
    public DiagonalMatrixMTJ inverse()
    {
        return this.pseudoInverse();
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

    @Override
    public int getEntryCount()
    {
        return this.getInternalMatrix().getData().length;
    }
    
    public Vector getColumn(
        int columnIndex )
    {
        int M = this.getDimensionality();
        Vector column = SparseVectorFactoryMTJ.getDefault().createVector( M );
        column.setElement( columnIndex, this.getElement( columnIndex, columnIndex ) );
        return column;
    }

    public Vector getRow(
        int rowIndex )
    {
        int N = this.getDimensionality();
        Vector row = SparseVectorFactoryMTJ.getDefault().createVector( N );
        row.setElement( rowIndex, this.getElement( rowIndex, rowIndex ) );
        return row;
    }
    
    @Override
    public double getElement(
        int rowIndex,
        int columnIndex )
    {
        if( rowIndex != columnIndex )
        {
            return 0.0;
        }
        else
        {
            return this.getElement( rowIndex );
        }
    }

    @Override
    public void setElement(
        int rowIndex,
        int columnIndex,
        double value )
    {
        if( rowIndex != columnIndex )
        {
            if( value != 0.0 )
            {
                throw new IllegalArgumentException(
                    "Can only set diagonal elements in a DiagonalMatrix!" );
            }
        }
        else
        {
            this.setElement( rowIndex, value );
        }
    }

    public double getElement(
        int index )
    {
        double[] diagonal = this.getDiagonal();
        return diagonal[index];
    }

    public void setElement(
        int index,
        double value )
    {
        double[] diagonal = this.getDiagonal();
        diagonal[index] = value;
    }

    @Override
    public DenseVector convertToVector()
    {
        return DenseVectorFactoryMTJ.INSTANCE.copyArray( this.getDiagonal() );
    }

    @Override
    public void convertFromVector(
        Vector parameters )
    {
        if( this.getNumRows() != parameters.getDimensionality() )
        {
            throw new IllegalArgumentException(
                "Wrong number of parameters!" );
        }
        
        double[] diagonal = this.getDiagonal();
        for( int i = 0; i < diagonal.length; i++ )
        {
            diagonal[i] = parameters.getElement( i );
        }
        
    }

    @Override
    public String toString()
    {
        int M = this.getDimensionality();
        StringBuilder retval = new StringBuilder( M * 10 );
        retval.append( "(" + M + "x" + M + "), diagonal:" );
        for( int i = 0; i < M; i++ )
        {
            retval.append( " " + this.getElement( i ) );
        }
        
        return retval.toString();
        
    }

    public String toString(
        final NumberFormat format)
    {
        final int d = this.getDimensionality();
        final StringBuilder result = new StringBuilder(d * 5);
        result.append("(" + d + "x" + d + "), diagonal:");
        for (int i = 0; i < d; i++)
        {
            result.append(" " + format.format(this.getElement(i)));
        }

        return result.toString();
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
        int numDiagonal = this.getDimensionality();
        double[] diag = new double[numDiagonal];
        for( int i = 0; i < numDiagonal; i++ )
        {
            diag[i] = this.getElement(i);
        }

        out.writeObject( diag );
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

        double[] diag = (double[]) in.readObject();
        this.setInternalMatrix( new no.uib.cipr.matrix.BandMatrix( diag.length, 0, 0 ) );
        System.arraycopy(diag, 0, this.getDiagonal(), 0, diag.length);
    }
    
    @Override
    public MatrixFactory<?> getMatrixFactory()
    {
        return SparseMatrixFactoryMTJ.INSTANCE;
    }

}
