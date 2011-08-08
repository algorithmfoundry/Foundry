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
     *      Matrix with random values for the entries, uniformly distributed
     *      between the given minimum and maximum values.
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

}
