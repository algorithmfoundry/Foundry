/*
 * File:                MatrixIterator.java
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

import gov.sandia.cognition.math.matrix.MatrixEntry;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Matrix iterator for all optimized matrix types.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
class MatrixIterator
    implements Iterator<MatrixEntry>
{

    /**
     * The matrix to iterate over.
     */
    private BaseMatrix matrix;
    
    /**
     * Current row index.
     */
    private int rowIndex;

    /**
     * Current column index.
     */
    private int columnIndex;

    /**
     * Unsupported null constructor
     */
    private MatrixIterator()
    {
        throw new UnsupportedOperationException("Unable to construct null "
            + "MatrixIterator");
    }

    /**
     * Initializes an iterator at the start position of the matrix (0, 0).
     *
     * @param m The matrix to iterate over
     */
    public MatrixIterator(BaseMatrix m)
    {
        // Start in the upper left corner
        this.rowIndex = this.columnIndex = 0;
        this.matrix = m;
    }

    @Override
    final public boolean hasNext()
    {
        return (rowIndex < matrix.getNumRows());
    }

    @Override
    final public MatrixEntry next()
    {
        if (!hasNext())
        {
            throw new NoSuchElementException("Iterator has exceeded the bounds "
                + "of the matrix");
        }

        // Get the current value
        BaseMatrixEntry result = new BaseMatrixEntry(matrix, rowIndex, columnIndex);
        // Go to the next value
        ++columnIndex;
        // Roll to the next row
        if (columnIndex >= matrix.getNumColumns())
        {
            columnIndex = 0;
            ++rowIndex;
        }

        return result;
    }

    /**
     * Unsupported operation: You can't remove elements from matrices
     */
    @Override
    final public void remove()
    {
        throw new UnsupportedOperationException(
            "Elements can't be removed from matrices.");
    }

}
