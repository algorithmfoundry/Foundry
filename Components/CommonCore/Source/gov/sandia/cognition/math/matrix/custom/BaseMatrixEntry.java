/*
 * File:                BaseMatrixEntry.java
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

/**
 * Implementation is the same for all matrix classes. It seemed less dangerous
 * to be a little slower in the getValue/setValue methods than to replicate the
 * logic for those operations for all matrix types herein as well as in the
 * actual matrices.
 *
 * @author Jeremy D. Wendt
 * @since 3.4.4
 */
class BaseMatrixEntry
    implements MatrixEntry
{

    /**
     * The matrix to represent.
     */
    private BaseMatrix matrix;

    /**
     * The row index in the matrix.
     */
    private int rowIndex;

    /**
     * The column index in the matrix.
     */
    private int columnIndex;

    /**
     * Unsupported empty constructor.
     */
    private BaseMatrixEntry()
    {
        throw new UnsupportedOperationException("The null constructor is not "
            + "valid");
    }

    /**
     * Initializes the values for this entry.
     *
     * @param matrix the matrix
     * @param rowIndex the row index
     * @param columnIndex the column index
     */
    BaseMatrixEntry(
        final BaseMatrix matrix,
        final int rowIndex,
        final int columnIndex)
    {
        super();
        
        this.matrix = matrix;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    @Override
    final public int getRowIndex()
    {
        return this.rowIndex;
    }

    /**
     * Updates this to point to a new location in the matrix (if input is within
     * bounds).
     *
     * @param rowIndex The new row index
     * @throws IllegalArgumentException if the input rowIndex is out of bounds
     */
    @Override
    final public void setRowIndex(
        final int rowIndex)
    {
        if ((rowIndex < 0) || (rowIndex >= this.matrix.getNumRows()))
        {
            throw new IllegalArgumentException("Unable to set row index "
                + "beyond bounds: " + rowIndex + " not within [0, "
                + matrix.getNumRows() + ")");
        }

        this.rowIndex = rowIndex;
    }

    @Override
    final public int getColumnIndex()
    {
        return this.columnIndex;
    }

    /**
     * Updates this to point to a new location in the matrix (if input is within
     * bounds).
     *
     * @param columnIndex The new column index
     * @throws IllegalArgumentException if the input columnIndex is out of
     * bounds
     */
    @Override
    final public void setColumnIndex(
        final int columnIndex)
    {
        if ((columnIndex < 0) || (columnIndex >= matrix.getNumColumns()))
        {
            throw new IllegalArgumentException("Unable to set row index "
                + "beyond bounds: " + columnIndex + " not within [0, "
                + matrix.getNumColumns() + ")");
        }

        this.columnIndex = columnIndex;
    }

    @Override
    final public double getValue()
    {
        return this.matrix.getElement(this.rowIndex, this.columnIndex);
    }

    /**
     * Sets the value stored at the current location in the matrix
     *
     * @param value the value to set
     */
    @Override
    final public void setValue(
        final double value)
    {
        this.matrix.setElement(this.rowIndex, this.columnIndex, value);
    }

}
