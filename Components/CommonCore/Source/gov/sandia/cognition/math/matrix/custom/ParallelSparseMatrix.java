/*
 * File:                ParallelSparseMatrix.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * A sparse matrix implementation. This stores the data in two formats: The
 * first is as an array of sparse vectors for each row. This format is easy to
 * modify (see the set* methods), but slow to operate on due to each element
 * being dispersed to different locations in memory. The second is the
 * compressed Yale format (see
 * http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format). This format densely
 * packs the values into arrays of double -- permitting fast computation, but
 * difficult and costly modification. This switches between the formats as
 * necessary (to Yale when multiplying, to sparse vector when altering), so it
 * is recommended that computation calls not be interleaved with modification
 * calls unnecessarily.
 *
 * At present, this class extends SparseMatrix to leverage the serial
 * implementations for all methods not-yet parallelized. The parallelized
 * operations perform their work in parallel, but can't be called
 * asynchronously. That is, the caller can't call "times(Vector)" and then do
 * other work in parallel while waiting for the result. If you were to do so and
 * called any methods that affected this matrix or the input vector, the results
 * of the multiply could go very wrong.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
@PublicationReference(author = "Wikipedia",
    title = "Sparse Matrix / Yale format",
    type = PublicationType.WebPage,
    year = 2013,
    url = "http://en.wikipedia.org/wiki/Sparse_matrix#Yale_format")
public class ParallelSparseMatrix
    extends SparseMatrix
{

    /**
     * This stores the number of threads to use for all operations.
     */
    int numThreads;

    /**
     * Creates a new parallel sparse matrix with the specified number of rows
     * and columns. All values are implicitly set to zero.
     *
     * NOTE: Upon completion this is in the sparse vector format.
     *
     * @param numRows The number of rows in the matrix
     * @param numCols The number of columns in the matrix
     * @param numThreads The number of threads to use for parallelized
     * operations
     */
    public ParallelSparseMatrix(
        final int numRows,
        final int numCols,
        final int numThreads)
    {
        super(numRows, numCols);
        this.numThreads = numThreads;
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as m
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the same format as m.
     *
     * @param m The sparse matrix to copy
     */
    public ParallelSparseMatrix(
        final ParallelSparseMatrix m)
    {
        super(m);
        this.numThreads = m.numThreads;
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as m
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the same format as m.
     *
     * @param m The sparse matrix to copy
     * @param numThreads The number of threads to use for parallelized
     * operations
     */
    public ParallelSparseMatrix(
        final SparseMatrix m,
        final int numThreads)
    {
        super(m);
        this.numThreads = numThreads;
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as d
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @param d The dense matrix to copy
     * @param numThreads The number of threads to use for parallelized
     * operations
     */
    public ParallelSparseMatrix(
        final DenseMatrix d,
        final int numThreads)
    {
        super(d);
        this.numThreads = numThreads;
    }

    /**
     * Creates a new sparse matrix with the same dimensions and data as d
     * (performs a deep copy).
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     *
     * @param d The diagonal matrix to copy
     * @param numThreads The number of threads to use for parallelized
     * operations
     */
    public ParallelSparseMatrix(
        final DiagonalMatrix d,
        final int numThreads)
    {
        super(d);
        this.numThreads = numThreads;
    }

    /**
     * Package-private helper that creates a completely empty matrix (neither
     * format initialized). It is assumed that the calling function will
     * immediately fill the appropriate values.
     *
     * @param numRows The number of rows in the matrix
     * @param numCols The number of columns in the matrix
     * @param numThreads The number of threads to use for parallelized
     * operations
     * @param unused Only present to differentiate from the other full
     * constructor
     */
    ParallelSparseMatrix(
        final int numRows,
        final int numCols,
        final int numThreads,
        final boolean unused)
    {
        super(numRows, numCols, unused);
        this.numThreads = numThreads;
    }

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected ParallelSparseMatrix()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This does not affect this's format. The cloned matrix is in the
     * same format as this.
     * @return {@inheritDoc}
     */
    @Override
    final public Matrix clone()
    {
        return (ParallelSparseMatrix) super.clone();
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     * @return {@inheritDoc}
     */
    @Override
    public Vector times(
        final SparseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }
        if (!vector.isCompressed())
        {
            vector.compress();
        }

        int m = getNumRows();
        DenseVector result = new DenseVector(m);

        // Create the factory
        ParallelMatrixFunction.Factory<ParallelSparseMatrix, SparseVector, DenseVector> factory =
            (ParallelSparseMatrix input1, SparseVector input2, DenseVector output, int minRow, int maxRow) ->
                new ParallelMatrixFunction<ParallelSparseMatrix, SparseVector, DenseVector>(
                    input1, input2, output, minRow, maxRow)
                {
                    @Override
                    public Integer call()
                        throws Exception
                    {
                        // Here's the actual logic for multiplying
                        int idx;
                        int[] vLocs = input2.getIndices();
                        double[] vVals = input2.getValues();
                        for (int i = minRow; i < maxRow; ++i)
                        {
                            output.values[i] = 0;
                            idx = 0;
                            // For all non-zero elements on this row of the matrix
                            for (int j = firstIndicesForRows[i]; j
                                < firstIndicesForRows[i + 1]; ++j)
                            {
                                // First advance past non-zero elements in the
                                // vector that are before this one
                                while ((idx < vLocs.length) && (vLocs[idx]
                                    < columnIndices[j]))
                                {
                                    ++idx;
                                }
                                // If you've exceeded the length of the vector,
                                // you can stop this row
                                if (idx >= vLocs.length)
                                {
                                    break;
                                }
                                // If the vector's current location is past this
                                // point in the row, then there's no non-zero
                                // element at this location
                                if (vLocs[idx] > columnIndices[j])
                                {
                                    continue;
                                }
                                // You only reach here if they are at the same
                                // location
                                output.values[i] += values[j] * vVals[idx];
                            }
                        }

                        return 0;
                    }
                    
                } // The factory returns a new instance of the function
        ;

        // Now that the factory is created, just call "solve" handing it in
        ParallelMatrixFunction.< ParallelSparseMatrix, SparseVector, DenseVector>solve(
            this, vector, result, numThreads * 2, numThreads, m, factory);

        return new SparseVector(result);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     * @return {@inheritDoc}
     */
    @Override
    public Vector times(
        final DenseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }

        int m = getNumRows();
        DenseVector result = new DenseVector(m);

        // Create the factory
        ParallelMatrixFunction.Factory<ParallelSparseMatrix, DenseVector, DenseVector> factory =
            (ParallelSparseMatrix input1, DenseVector input2, DenseVector output, int minRow, int maxRow) ->
                new ParallelMatrixFunction<ParallelSparseMatrix, DenseVector, DenseVector>(
                    input1, input2, output, minRow, maxRow)
                {
                    @Override
                    public Integer call()
                        throws Exception
                    {
                        // Here's the actual logic for multiplying
                        for (int i = minRow; i < maxRow; ++i)
                        {
                            output.values[i] = 0;
                            for (int j = firstIndicesForRows[i]; j
                                < firstIndicesForRows[i + 1]; ++j)
                            {
                                output.values[i] += values[j]
                                    * input2.values[columnIndices[j]];
                            }
                        }

                        return 0;
                    }
                    
                } // The factory returns a new instance of the function
        ;

        // Now that the factory is created, just call "solve" handing it in
        ParallelMatrixFunction.<ParallelSparseMatrix, DenseVector, DenseVector>solve(
            this, vector, result, numThreads * 2, numThreads, m, factory);

        return result;
    }

}
