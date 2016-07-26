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
    public ParallelSparseMatrix(int numRows,
        int numCols,
        int numThreads)
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
    public ParallelSparseMatrix(ParallelSparseMatrix m)
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
    public ParallelSparseMatrix(SparseMatrix m,
        int numThreads)
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
    public ParallelSparseMatrix(DenseMatrix d,
        int numThreads)
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
    public ParallelSparseMatrix(DiagonalMatrix d,
        int numThreads)
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
    ParallelSparseMatrix(int numRows,
        int numCols,
        int numThreads,
        boolean unused)
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
// TODO: Fix this clone.
        return new ParallelSparseMatrix(this);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     * @return {@inheritDoc}
     */
    @Override
    public Vector times(SparseVector vector)
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
        DenseVector ret = new DenseVector(m, true);

        // Create the factory
        ParallelMatrixFunction.IParallelFunctionFactory<ParallelSparseMatrix, SparseVector, DenseVector> factory =
            new ParallelMatrixFunction.IParallelFunctionFactory<ParallelSparseMatrix, SparseVector, DenseVector>()
        {
            @Override
            public ParallelMatrixFunction<ParallelSparseMatrix, SparseVector, DenseVector> init(
                ParallelSparseMatrix input1,
                SparseVector input2,
                DenseVector output,
                int minRow,
                int maxRow)
            {
                // The factory returns a new instance of the function
                return new ParallelMatrixFunction<ParallelSparseMatrix, SparseVector, DenseVector>(
                    input1, input2, output, minRow, maxRow)
                {
                    @Override
                    public Integer call()
                        throws Exception
                    {
                        // Here's the actual logic for multiplying
                        int idx;
                        int[] vLocs = input2.getLocs();
                        double[] vVals = input2.getVals();
                        for (int i = minRow; i < maxRow; ++i)
                        {
                            output.elements()[i] = 0;
                            idx = 0;
                            // For all non-zero elements on this row of the matrix
                            for (int j = firstIdxsForRows[i]; j
                                < firstIdxsForRows[i + 1]; ++j)
                            {
                                // First advance past non-zero elements in the
                                // vector that are before this one
                                while ((idx < vLocs.length) && (vLocs[idx]
                                    < colIdxs[j]))
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
                                if (vLocs[idx] > colIdxs[j])
                                {
                                    continue;
                                }
                                // You only reach here if they are at the same
                                // location
                                output.elements()[i] += vals[j] * vVals[idx];
                            }
                        }

                        return 0;
                    }

                };
            }

        };

        // Now that the factory is created, just call "solve" handing it in
        ParallelMatrixFunction.< ParallelSparseMatrix, SparseVector, DenseVector>solve(
            this, vector, ret, numThreads * 2, numThreads, m, factory);

        return new SparseVector(ret);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: Upon completion this is in the compressed Yale format.
     * @return {@inheritDoc}
     */
    @Override
    public Vector times(DenseVector vector)
    {
        if (!isCompressed())
        {
            compress();
        }

        int m = getNumRows();
        DenseVector ret = new DenseVector(m, true);

        // Create the factory
        ParallelMatrixFunction.IParallelFunctionFactory<ParallelSparseMatrix, DenseVector, DenseVector> factory =
            new ParallelMatrixFunction.IParallelFunctionFactory<ParallelSparseMatrix, DenseVector, DenseVector>()
        {
            @Override
            public ParallelMatrixFunction<ParallelSparseMatrix, DenseVector, DenseVector> init(
                ParallelSparseMatrix input1,
                DenseVector input2,
                DenseVector output,
                int minRow,
                int maxRow)
            {
                // The factory returns a new instance of the function
                return new ParallelMatrixFunction<ParallelSparseMatrix, DenseVector, DenseVector>(
                    input1, input2, output, minRow, maxRow)
                {
                    @Override
                    public Integer call()
                        throws Exception
                    {
                        // Here's the actual logic for multiplying
                        for (int i = minRow; i < maxRow; ++i)
                        {
                            output.elements()[i] = 0;
                            for (int j = firstIdxsForRows[i]; j
                                < firstIdxsForRows[i + 1]; ++j)
                            {
                                output.elements()[i] += vals[j]
                                    * input2.elements()[colIdxs[j]];
                            }
                        }

                        return 0;
                    }

                };
            }

        };

        // Now that the factory is created, just call "solve" handing it in
        ParallelMatrixFunction.<ParallelSparseMatrix, DenseVector, DenseVector>solve(
            this, vector, ret, numThreads * 2, numThreads, m, factory);

        return ret;
    }

}
