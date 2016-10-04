/*
 * File:                ParallelMatrixMergeFunction.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This package-private class simplifies parallelizing Matrix operations. It
 * uses generics for defining the two (possibly different) input types, and the
 * result of merging the parallel-solved pieces. This should be used for
 * parallel operations where the result is solved in-place on a single value.
 * For instance in dot products, the result is a single double. Thus, each
 * parallel piece solves on a different double and then merge combines those
 * doubles into a single double.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 * @param <InputType1> The type of the left operand
 * @param <InputType2> The type of the right operand
 * @param <MergeType> The type of the final result
 */
abstract class ParallelMatrixMergeFunction<InputType1, InputType2, MergeType>
    implements Callable<MergeType>
{

    /**
     * The minimum index for the row that should be used in the operation (row
     * on input1, likely column on input2).
     */
    protected int minRow;

    /**
     * The maximum index for the row that should be used in the operation
     * (not-inclusive (as in the for loop goes i = minRow; i &lt; maxRow); row
     * on input1, likely column on input2).
     */
    protected int maxRow;

    /**
     * The left-part of the operation. For instance, in vector-vector dot
     * product, this is the vector type of the left side. Each thread should
     * only read from rows between minRow and maxRow (for caching purposes).
     * This should not be changed at all during the operations.
     */
    protected InputType1 input1;

    /**
     * The right-part of the operation. For instance, in vector-vector dot
     * product, this is the vector type of the right side. This should not be
     * changed at all during the operations.
     */
    protected InputType2 input2;

    /**
     * Private because this should never be called. Ever. No matter what.
     */
    private ParallelMatrixMergeFunction()
    {
        throw new UnsupportedOperationException(
            "Null constructor not supported.");
    }

    /**
     * Passes in the necessary arguments to initialize an instance. Shallow
     * copies of all inputs are made.
     *
     * @param input1 The first input
     * @param input2 The second input
     * @param minRow The minimum row for this thread to operate on
     * @param maxRow The maximum row (not inclusive) for this thread to operate
     * on
     */
    public ParallelMatrixMergeFunction(
        final InputType1 input1,
        final InputType2 input2,
        final int minRow,
        final int maxRow)
    {
        this.input1 = input1;
        this.input2 = input2;
        this.minRow = minRow;
        this.maxRow = maxRow;
    }

    /**
     * This needs to be extended by operation-specific classes.
     *
     * @return The solution for solving this piece of the operation -- the
     * results will be merged in merge (not in parallel).
     * @throws Exception Part of the interface. Please don't throw exceptions
     * unless you really need to.
     */
    @Override
    abstract public MergeType call()
        throws Exception;

    /**
     * This method will only be called on one instance and won't use internal
     * state to merge the results of all of the piece's call methods.
     *
     * @param pieces The results from all of the pieces
     * @return The merged, final result
     */
    abstract protected MergeType merge(
        final List<Future<MergeType>> pieces);

    /**
     * This static method handles all the logic of splitting up the chunks of a
     * matrix problem, calling the chunks in parallel, and merging the results.
     *
     * @param <InputType1> The type for the left operand
     * @param <InputType2> The type for the right operand
     * @param <MergeType> The type for the merged result
     * @param input1 The left operand
     * @param input2 The right operand
     * @param numPieces The number of pieces to split the problem into -- can be
     * more than the number of threads if you think the pieces may be non-equal
     * in size.
     * @param numThreads The number of threads to create for solving the problem
     * @param numRows The number of rows in the problem (usually input1's
     * numRows)
     * @param factory The factory for creating ParallelMatrixFunction instnaces
     */
    public static <InputType1, InputType2, MergeType> MergeType solve(
        final InputType1 input1,
        final InputType2 input2,
        final int numPieces,
        final int numThreads,
        final int numRows,
        final ParallelMatrixMergeFunction.Factory<InputType1, InputType2, MergeType> factory)
    {
        double numRowsPer = numRows / ((double) numPieces);
        numRowsPer = Math.max(numRowsPer, 1.0);
        List<ParallelMatrixMergeFunction<InputType1, InputType2, MergeType>> pieces =
            new ArrayList<>(
            numPieces);
        int minRow, maxRow;
        minRow = 0;
        for (int i = 0; i < numPieces; ++i)
        {
            if (i == (numPieces - 1))
            {
                maxRow = numRows;
            }
            else
            {
                maxRow = (int) Math.round((i + 1) * numRowsPer);
            }
            maxRow = Math.min(maxRow, numRows);
            pieces.add(factory.init(input1, input2, minRow, maxRow));
            minRow = maxRow;

            // Break out early if there were more pieces than rows
            if (minRow >= numRows)
            {
                break;
            }
        }
        ExecutorService threads = Executors.newFixedThreadPool(numThreads);
        try
        {
            List<Future<MergeType>> results = threads.<MergeType>invokeAll(
                pieces);
            threads.shutdown();

            return pieces.get(0).merge(results);
        }
        catch (InterruptedException e)
        {
            threads.shutdown();
            throw new RuntimeException("Threads stopped prematurely", e);
        }
    }

    /**
     * A factory for creating the necessary parallel-aware solvers
     *
     * @param <InputType1> The left input's type
     * @param <InputType2> The right input's type
     * @param <MergeType> The output type
     */
    public static interface Factory<InputType1, InputType2, MergeType>
    {

        /**
         * Creates an instance of the parallel-aware solver with the input
         * values stored for the call method.
         *
         * @param input1 The left input
         * @param input2 The right input
         * @param minRow The minimum row to affect (inclusive)
         * @param maxRow The maximum row to affect (not inclusive)
         * @return A new instance of the correct parallel-aware solver with the
         * input values stored for the call method.
         */
        ParallelMatrixMergeFunction<InputType1, InputType2, MergeType> init(
            InputType1 input1,
            InputType2 input2,
            int minRow,
            int maxRow);

    }

}
