/*
 * File:                ParallelMatrixFunction.java
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

package gov.sandia.cognition.math.matrix.optimized;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This package-private class simplifies parallelizing Matrix operations. It
 * uses generics for defining the two (possibly different) input types, and the
 * output type. This should be used for parallel operations where the output can
 * be stored separately for each row (for instance in matrix/vector multiplies,
 * where the output is a vector with independent values for each row's output).
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 * @param <InputType1> The first part of the input
 * @param <InputType2> The second part of the input
 * @param <OutputType> The output type
 */
abstract class ParallelMatrixFunction<InputType1, InputType2, OutputType>
    implements Callable<Integer>
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
     * The left-part of the operation. For instance, in matrix-vector
     * multiplication, this is the matrix. Each thread should only read from
     * rows between minRow and maxRow (for caching purposes). This should not be
     * changed at all during the operations.
     */
    protected InputType1 input1;

    /**
     * The right-part of the operation. For instance, in matrix-vector
     * multiplication, this is the vector. This should not be changed at all
     * during the operations.
     */
    protected InputType2 input2;

    /**
     * The result of the operation. Each thread will only write to rows between
     * minRow and maxRow (not inclusive). The results of the operation will
     * alter this -- and the caller should maintain a copy.
     */
    protected OutputType output;

    /**
     * Private because this should never be called. Ever. No matter what.
     */
    private ParallelMatrixFunction()
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
     * @param output The output -- the callee will see the results of the
     * parallel operations in this
     * @param minRow The minimum row for this thread to operate on
     * @param maxRow The maximum row (not inclusive) for this thread to operate
     * on
     */
    public ParallelMatrixFunction(InputType1 input1,
        InputType2 input2,
        OutputType output,
        int minRow,
        int maxRow)
    {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.minRow = minRow;
        this.maxRow = maxRow;
    }

    /**
     * This needs to be extended by operation-specific classes. NOTE: The return
     * type will be ignored (it's just required by the Callable interface).
     *
     * @return Is ignored.
     * @throws Exception Part of the interface. Please don't throw exceptions
     * unless you really need to.
     */
    @Override
    abstract public Integer call()
        throws Exception;

    /**
     * This static method handles all the logic of splitting up the chunks of a
     * matrix problem and calling the chunks in parallel.
     *
     * @param <InputType1> The type for the left operand
     * @param <InputType2> The type for the right operand
     * @param <OutputType> The type for the result
     * @param input1 The left operand
     * @param input2 The right operand
     * @param output The result -- this will change as a result of operations
     * @param numPieces The number of pieces to split the problem into -- can be
     * more than the number of threads if you think the pieces may be non-equal
     * in size.
     * @param numThreads The number of threads to create for solving the problem
     * @param numRows The number of rows in the problem (usually input1's
     * numRows)
     * @param factory The factory for creating ParallelMatrixFunction instnaces
     */
    public static <InputType1, InputType2, OutputType> void solve(
        InputType1 input1,
        InputType2 input2,
        OutputType output,
        int numPieces,
        int numThreads,
        int numRows,
        IParallelFunctionFactory<InputType1, InputType2, OutputType> factory)
    {
        double numRowsPer = ((double) numRows) / ((double) numPieces);
        numRowsPer = Math.max(numRowsPer, 1.0);
        List<ParallelMatrixFunction<InputType1, InputType2, OutputType>> pieces =
            new ArrayList<ParallelMatrixFunction<InputType1, InputType2, OutputType>>(
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
            pieces.add(factory.init(input1, input2, output, minRow, maxRow));
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
            threads.invokeAll(pieces);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException("Threads stopped prematurely", e);
        }
        finally
        {
            threads.shutdown();
        }
    }

    /**
     * A factory for creating the necessary parallel-aware solvers
     *
     * @param <InputType1> The left input's type
     * @param <InputType2> The right input's type
     * @param <OutputType> The output's type
     */
    public interface IParallelFunctionFactory<InputType1, InputType2, OutputType>
    {

        /**
         * Creates an instance of the parallel-aware solver with the input
         * values stored for the call method.
         *
         * @param input1 The left input
         * @param input2 The right input
         * @param output The output -- this will be altered by the call method
         * (between minRow (inclusive) and maxRow (not inclusive)).
         * @param minRow The minimum row to affect (inclusive)
         * @param maxRow The maximum row to affect (not inclusive)
         * @return A new instance of the correct parallel-aware solver with the
         * input values stored for the call method.
         */
        ParallelMatrixFunction<InputType1, InputType2, OutputType> init(
            InputType1 input1,
            InputType2 input2,
            OutputType output,
            int minRow,
            int maxRow);

    }

}
