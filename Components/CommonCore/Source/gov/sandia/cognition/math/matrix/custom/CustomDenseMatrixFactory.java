/*
 * File:                DenseMatrixFactoryOptimized.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory that creates DenseMatrix instances. NOTE: There are other DenseMatrix
 * constructors that are available to the outside caller.
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.4
 */
public class CustomDenseMatrixFactory
    extends MatrixFactory<DenseMatrix>
{

    /** An instance of this class. */
    public static final CustomDenseMatrixFactory INSTANCE = new CustomDenseMatrixFactory();
    
    /**
     * Creates a deep copy of m into a DenseMatrix and returns it.
     *
     * @param m The matrix to copy
     * @return The DenseMatrix deep copy of m.
     */
    @Override
    final public DenseMatrix copyMatrix(
        final Matrix m)
    {
        return new DenseMatrix(m);
    }

    /**
     * Creates a new all-zero DenseMatrix of the specified dimensions.
     *
     * @param numRows The number of rows desired in the result
     * @param numColumns The number of columns desired in the result
     * @return a new all-zero DenseMatrix of the specified dimensions
     */
    @Override
    final public DenseMatrix createMatrix(
        final int numRows,
        final int numColumns)
    {
        return new DenseMatrix(numRows, numColumns);
    }

}
