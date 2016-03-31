/*
 * File:                DiagonalMatrixFactoryMTJ.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import java.util.Random;

/**
 * An {@code MatrixFactory} that produces {@code DiagonalMatrixMTJ} matrices.
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class DiagonalMatrixFactoryMTJ 
    extends MatrixFactory<DiagonalMatrixMTJ>
{
    
    /**
     * Default instance of the class
     */
    public static final DiagonalMatrixFactoryMTJ INSTANCE =
        new DiagonalMatrixFactoryMTJ();

    /** 
     * Creates a new instance of DiagonalMatrixFactoryMTJ 
     */
    public DiagonalMatrixFactoryMTJ()
    {
    }

    @Override
    public DiagonalMatrixMTJ copyMatrix(
        Matrix m )
    {
        int M = m.getNumRows();
        int N = m.getNumColumns();
        
        DiagonalMatrixMTJ retval = this.createMatrix( M, N );
        
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                double v = m.getElement( i, j );
                if( i == j )
                {
                    retval.setElement( i, v );
                }
                
                // off diagonal elements must be zero!
                else if( v != 0.0 )
                {
                    throw new IllegalArgumentException(
                        "Off-diagonal elements must be zero!!" );
                }
            }
        }
        
        return retval;
    }

    @Override
    public DiagonalMatrixMTJ createMatrix(
        int numRows,
        int numColumns )
    {
        if( numRows != numColumns )
        {
            throw new IllegalArgumentException(
                "Matrix must be square and diagonal!" );            
        }
        
        return this.createMatrix( numRows );
        
    }

    /**
     * Creates a square diagonal matrix of dimensionality "dim" with zeros
     * along the diagonal
     * @param dim
     * Number of dimensions of the square matrix
     * @return
     * Square diagonal matrix with zeros along the diagonal
     */
    public DiagonalMatrixMTJ createMatrix(
        int dim )
    {
        return new DiagonalMatrixMTJ( dim );
    }
    
    /**
     * Creates a diagonal matrix with the array of values on its diagonal
     * @param diagonal
     * Values for the diagonal entries of the matrix
     * @return
     * Diagonal matrix with the specified diagonal entries
     */
    public DiagonalMatrixMTJ diagonalValues(
        double[] diagonal )
    {
        return new DiagonalMatrixMTJ( diagonal );
    }

    /**
     * Creates a matrix with the given initial value on all of the elements of
     * the diagonal.
     *
     * @param   numRows
     *      The number of rows. Cannot be negative.
     * @param   numColumns
     *      The number of columns. Cannot be negative.
     * @param   initialValue
     *      The initial value to set all elements to.
     * @return
     *      A Matrix of the given size initialized with the given initial value.
     */
    @Override
    public DiagonalMatrixMTJ createMatrix(
        final int numRows,
        final int numColumns,
        final double initialValue)
    {
        final DiagonalMatrixMTJ result = this.createMatrix(numRows, numColumns);
        if (initialValue != 0.0)
        {
            // The matrix is square and diagonal by default.
            for (int i = 0; i < numRows; i++)
            {
                result.setElement(i, i, initialValue);
            }
        }
        return result;
    }

    @Override
    public DiagonalMatrixMTJ createUniformRandom(
        final int numRows,
        final int numColumns,
        final Random random)
    {
        if (numRows != numColumns)
        {
            throw new IllegalArgumentException("numRows != numColumns");
        }
        
        return this.createUniformRandom(numRows, 0.0, 1.0, random);
    }
    
    @Override
    public DiagonalMatrixMTJ createUniformRandom(
        int numRows,
        int numColumns,
        double min,
        double max,
        Random random)
    {
        if( numRows != numColumns )
        {
            throw new IllegalArgumentException(
                "numRows != numColumns" );
        }

        return this.createUniformRandom(numRows, min, max, random);
    }

    /**
     * Creates a new square Matrix of the given size with random values for the
     * entries, uniformly distributed between the given minimum and maximum
     * values.
     *
     * @param   dimensionality
     *      The number of rows and columns for this square matrix.
     * @param   min
     *      The minimum range of the uniform distribution.
     * @param   max
     *      The maximum range of the uniform distribution.
     * @param   random
     *      The random number generator.
     * @return
     *      Matrix with random values for the entries on the diagonal, 
     *      uniformly distributed between the given minimum and maximum values.
     */
    public DiagonalMatrixMTJ createUniformRandom(
        int dimensionality,
        double min,
        double max,
        Random random)
    {
        DiagonalMatrixMTJ m = this.createMatrix(dimensionality);
        for( int i = 0; i < dimensionality; i++ )
        {
            m.setElement(i, random.nextDouble()*(max-min) + min );
        }
        return m;
    }

    @Override
    public DiagonalMatrixMTJ createGaussianRandom(
        final int numRows,
        final int numColumns,
        final Random random)
    {
        if (numRows != numColumns)
        {
            throw new IllegalArgumentException("numRows != numColumns");
        }

        return this.createGaussianRandom(numRows, random);
    }

    /**
     * Creates a new square Matrix of the given size with random values for the
     * entries, Gaussian distributed with mean 0 and variance 1.
     *
     * @param   dimensionality
     *      The number of rows and columns for this square matrix.
     * @param   random
     *      The random number generator.
     * @return
     *      Matrix with random values for the entries on the diagonal 
     *      distributed according to a random Gaussian.
     */
    public DiagonalMatrixMTJ createGaussianRandom(
        int dimensionality,
        Random random)
    {
        DiagonalMatrixMTJ result = this.createMatrix(dimensionality);
        for (int i = 0; i < dimensionality; i++)
        {
            result.setElement(i, random.nextGaussian());
        }
        return result;
    }
}
