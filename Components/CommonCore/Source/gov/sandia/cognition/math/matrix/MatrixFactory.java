/*
 * File:                MatrixFactory.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.DiagonalMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * Abstract factory for creating {@code Matrix} objects.
 *
 * @param <MatrixType> Type of Matrix created by the factory.
 * @author Kevin R. Dixon
 * @since  1.0
 */
public abstract class MatrixFactory<MatrixType extends Matrix>
    extends Object
    implements Serializable
{
    /** The default dense implementation of a {@code MatrixFactory}. */
    protected static final MatrixFactory<? extends Matrix> DEFAULT_DENSE_INSTANCE =
        new DenseMatrixFactoryMTJ();

    /** The default sparse implementation of a {@code MatrixFactory}. */
    protected static final MatrixFactory<? extends Matrix> DEFAULT_SPARSE_INSTANCE =
        new SparseMatrixFactoryMTJ();
    
    /**
     * The default implementation of a factory that creates a DiagonalMatrix
     */
    protected static final DiagonalMatrixFactoryMTJ DEFAULT_DIAGONAL_INSTANCE =
        new DiagonalMatrixFactoryMTJ();
    
    /**
     * Creates a new {@code MatrixFactory}.
     */
    protected MatrixFactory()
    {
        super();
    }
    
    /**
     * Gets the default implementation of {@code MatrixFactory}.
     * 
     * @return The default {@code MatrixFactory}.
     */
    public static MatrixFactory<? extends Matrix> getDefault()
    {
        return DEFAULT_DENSE_INSTANCE;
    }

    /**
     * Gets the default implementation of {@code MatrixFactory} for dense
     * matrices.
     *
     * @return The default dense {@code MatrixFactory}.
     */
    public static MatrixFactory<? extends Matrix> getDenseDefault()
    {
        return DEFAULT_DENSE_INSTANCE;
    }

    /**
     * Gets the default implementation of {@code MatrixFactory} for sparse
     * matrices.
     *
     * @return The default sparse {@code MatrixFactory}.
     */
    public static MatrixFactory<? extends Matrix> getSparseDefault()
    {
        return DEFAULT_SPARSE_INSTANCE;
    }
    
    /**
     * Gets the default implementation of {@code MatrixFactory} for diagonal
     * matrices.
     * 
     * @return The default {@code DiagonalMatrixFactory}.
     */
    public static MatrixFactory<? extends DiagonalMatrix> getDiagonalDefault()
    {
        return DEFAULT_DIAGONAL_INSTANCE;
    }
    
    /**
     * Creates a deep copy new Matrix given another, argument is unchanged
     * @param m Matrix to copy
     * @return Deep copy of the given Matrix
     */
    public abstract MatrixType copyMatrix(
        Matrix m );
    
    /**
     * Copies the values from the array into the Matrix
     * @param values Values to copy
     * @return Matrix with same dimension and values as "values"
     */
    public MatrixType copyArray(
        double[][] values )
    {
        int M = values.length;
        int N = 0;
        if( M > 0 )
        {
            N = values[0].length;
        }
        
        MatrixType m = this.createMatrix( M, N );
        
        for( int i = 0; i < M; i++ )
        {
            if( values[i].length != N )
            {
                throw new IllegalArgumentException(
                    "Array columns are not same size!" );
            }
            for( int j = 0; j < N; j++ )
            {
                m.setElement( i,j, values[i][j] );
            }
        }
        
        return m;
    }
    
    /**
     * Creates an empty Matrix of the specified dimensions, all elements
     * must be all zeros!
     * @param numRows number of rows in the Matrix
     * @param numColumns number of columns in the Matrix
     * @return All-zero empty Matrix of the specified dimensions
     */
    public abstract MatrixType createMatrix(
        int numRows,
        int numColumns );
    
    /**
     * Creates a Matrix with ones (1) on the diagonal, and zeros (0) elsewhere
     * @param numRows number of rows in the Matrix
     * @param numColumns number of columns in the Matrix
     * @return Identity Matrix with ones on the diagonal and zeros elsewhere
     */
    public MatrixType createIdentity(
        int numRows,
        int numColumns )
    {
        MatrixType m = this.createMatrix( numRows, numColumns );
        m.identity();
        return m;
    }
    
    /**
     * Creates a new Matrix of the given size with random values for the
     * entries, uniformly distributed between the given minimum and maximum
     * values.
     *
     * @param   numRows
     *      The number of rows in the Matrix.
     * @param   numColumns
     *      The number of columns in the Matrix.
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
    public MatrixType createUniformRandom(
        int numRows,
        int numColumns,
        double min,
        double max,
        Random random)
    {
        MatrixType m = this.createMatrix( numRows, numColumns );
        for( int i = 0; i < m.getNumRows(); i++ )
        {
            for( int j = 0; j < m.getNumColumns(); j++ )
            {
                double uniform = random.nextDouble();
                double value = ((max-min) * uniform) + min;
                m.setElement( i,j, value );
            }
        }
        
        return m;
    }

    /**
     * Creates a new matrix by copying the given set of row vectors.
     *
     * @param   rows
     *      The row vectors to create a matrix from. Must all be the same
     *      dimensionality.
     * @return
     *      A new matrix whose rows are equal to the given set of rows.
     */
    public MatrixType copyRowVectors(
        final Collection<? extends Vectorizable> rows)
    {
        // Create the matrix.
        final int numRows = rows.size();
        final int numColumns = VectorUtil.safeGetDimensionality(
            CollectionUtil.getFirst(rows));
        final MatrixType result = this.createMatrix(numRows, numColumns);

        // Fill in the matrix with the rows.
        int rowIndex = 0;
        for (Vectorizable row : rows)
        {
            result.setRow(rowIndex, row.convertToVector());
            rowIndex++;
        }

        return result;
    }

    /**
     * Creates a new matrix by copying the given set of row vectors.
     *
     * @param   rows
     *      The row vectors to create a matrix from. Must all be the same
     *      dimensionality.
     * @return
     *      A new matrix whose rows are equal to the given set of rows.
     */
    public MatrixType copyRowVectors(
        Vectorizable ... rows )
    {
        return this.copyRowVectors(Arrays.asList(rows));
    }

    /**
     * Creates a new matrix by copying the given set of column vectors.
     *
     * @param   columns
     *      The column vectors to create a matrix from. Must all be the same
     *      dimensionality.
     * @return
     *      A new matrix whose columns are equal to the given set of columns.
     */
    public MatrixType copyColumnVectors(
        final Collection<? extends Vectorizable> columns)
    {
        // Create the matrix.
        final int numRows = VectorUtil.safeGetDimensionality(
            CollectionUtil.getFirst(columns));
        final int numColumns =
            columns.size();
        final MatrixType result = this.createMatrix(numRows, numColumns);

        // Fill in the matrix with the columns.
        int columnIndex = 0;
        for (Vectorizable column : columns)
        {
            result.setColumn(columnIndex, column.convertToVector());
            columnIndex++;
        }


        return result;
    }

    /**
     * Creates a new matrix by copying the given set of column vectors.
     *
     * @param   columns
     *      The column vectors to create a matrix from. Must all be the same
     *      dimensionality.
     * @return
     *      A new matrix whose columns are equal to the given set of columns.
     */
    public MatrixType copyColumnVectors(
        Vectorizable ... columns )
    {
        return this.copyColumnVectors(Arrays.asList(columns));
    }

    /**
     * Creates a new square matrix whose number of rows and columns match the
     * dimensionality of the given vector. It also places the values of the
     * vector on the diagonal of the matrix.
     *
     * @param   diagonal
     *      The vector of diagonal values.
     * @return
     *      A new, square matrix with the diagonal elements equal to the
     *      elements of the given vector.
     */
    public MatrixType createDiagonal(
        final Vectorizable diagonal)
    {
        final Vector vector = diagonal.convertToVector();
        final int dimensionality = vector.getDimensionality();

        // Create the matrix.
        final MatrixType result = this.createMatrix(dimensionality, dimensionality);
        
        // Set the diagonal values.
        for (VectorEntry entry : vector)
        {
            final int i = entry.getIndex();
            result.setElement(i, i, entry.getValue());
        }
        return result;
    }
    
}
