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
 * @since   3.4.3
 */
class BaseMatrixEntry
    implements MatrixEntry
{

    /**
     * The matrix to represent
     */
    private BaseMatrix m;

    /**
     * The row idx in the matrix
     */
    private int i;

    /**
     * The column idx in the matrix
     */
    private int j;

    /**
     * Unsupported null constructor
     */
    private BaseMatrixEntry()
    {
        throw new UnsupportedOperationException("The null constructor is not "
            + "valid");
    }

    /**
     * Initializes the values for this entry
     *
     * @param m the matrix
     * @param i the row index
     * @param j the column index
     */
    BaseMatrixEntry(BaseMatrix m,
        int i,
        int j)
    {
        this.m = m;
        this.i = i;
        this.j = j;
    }

    /**
     * Returns the current row index
     *
     * @return the current row index
     */
    @Override
    final public int getRowIndex()
    {
        return i;
    }

    /**
     * Updates this to point to a new location in the matrix (if input is within
     * bounds).
     *
     * @param rowIndex The new row index
     * @throws IllegalArgumentException if the input rowIndex is out of bounds
     */
    @Override
    final public void setRowIndex(int rowIndex)
    {
        if ((rowIndex < 0) || (rowIndex >= m.getNumRows()))
        {
            throw new IllegalArgumentException("Unable to set row index "
                + "beyond bounds: " + rowIndex + " not within [0, "
                + m.getNumRows() + ")");
        }

        i = rowIndex;
    }

    /**
     * Returns the current column index
     *
     * @return the current column index
     */
    @Override
    final public int getColumnIndex()
    {
        return j;
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
    final public void setColumnIndex(int columnIndex)
    {
        if ((columnIndex < 0) || (columnIndex >= m.getNumColumns()))
        {
            throw new IllegalArgumentException("Unable to set row index "
                + "beyond bounds: " + columnIndex + " not within [0, "
                + m.getNumColumns() + ")");
        }

        j = columnIndex;
    }

    /**
     * Returns the value stored at the current location in the matrix
     *
     * @return the value stored at the current location in the matrix
     */
    @Override
    final public double getValue()
    {
        return m.getElement(i, j);
    }

    /**
     * Sets the value stored at the current location in the matrix
     *
     * @param value the value to set
     */
    @Override
    final public void setValue(double value)
    {
        m.setElement(i, j, value);
    }

}
