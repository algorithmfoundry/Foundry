/*
 * File:                TwoMatrixEntryMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 12, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.TwoMatrixEntry;

/**
 * Stores an entry for two matrices typically used by iterators that do
 * union and intersection operations.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class TwoMatrixEntryMTJ
    implements TwoMatrixEntry
{
    /**
     * First underlying matrix.
     */
    private AbstractMTJMatrix firstMatrix = null;

    /**
     * Second underlying matrix.
     */
    private AbstractMTJMatrix secondMatrix = null;
    
    /**
     * Current row index of the entry.
     */
    private int rowIndex;

    /**
     * Current column index of the entry.
     */
    private int columnIndex;
    
    /**
     * Creates a new TwoMatrixEntryMTJ.
     *
     * @param firstMatrix First underlying matrix for the entry.
     * @param secondMatrix Second underlying matrix for the entry.
     */
    public TwoMatrixEntryMTJ(
        AbstractMTJMatrix firstMatrix,
        AbstractMTJMatrix secondMatrix)
    {
        this(firstMatrix, secondMatrix, 0, 0);
    }

    
    /**
     * Creates a new instance of TwoMatrixEntryMTJ.
     *
     * @param firstMatrix First underlying matrix for the entry.
     * @param secondMatrix Second underlying matrix for the entry.
     * @param rowIndex Current row index for the entry.
     * @param columnIndex Current column index for the entry.
     */
    public TwoMatrixEntryMTJ(
        AbstractMTJMatrix firstMatrix,
        AbstractMTJMatrix secondMatrix,
        int rowIndex,
        int columnIndex)
    {
        super();
        
        this.setFirstMatrix(firstMatrix);
        this.setSecondMatrix(secondMatrix);
        this.setRowIndex(rowIndex);
        this.setColumnIndex(columnIndex);
    }

    /**
     * Getter for columnIndex.
     *
     * @return Current column index.
     */
    public int getColumnIndex()
    {
        return this.columnIndex;
    }

    /**
     * Setter for columnIndex.
     *
     * @param columnIndex Current column index.
     */
    public void setColumnIndex(
        int columnIndex)
    {
        this.columnIndex = columnIndex;
    }

    
    /**
     * Getter for rowIndex.
     *
     * @return Current row index for the entry.
     */
    public int getRowIndex()
    {
        return this.rowIndex;
    }
    
    /**
     * Setter for rowIndex.
     *
     * @param rowIndex Current row index for the entry.
     */
    public void setRowIndex(
        int rowIndex)
    {
        this.rowIndex = rowIndex;
    }

    /**
     * Getter for firstMatrix.
     *
     * @return First underlying matrix in the entry.
     */
    public AbstractMTJMatrix getFirstMatrix()
    {
        return firstMatrix;
    }

    /**
     * Setter for firstMatrix.
     *
     * @param firstMatrix First underlying matrix in the entry.
     */
    public void setFirstMatrix(
        AbstractMTJMatrix firstMatrix)
    {
        this.firstMatrix = firstMatrix;
    }
    
    /**
     * Getter for secondMatrix.
     *
     * @return Second underlying matrix in the entry.
     */
    public AbstractMTJMatrix getSecondMatrix()
    {
        return this.secondMatrix;
    }

    /**
     * Setter for secondMatrix.
     *
     * @param secondMatrix Second underlying matrix in the entry.
     */
    public void setSecondMatrix(
        AbstractMTJMatrix secondMatrix)
    {
        this.secondMatrix = secondMatrix;
    }

    /**
     * Gets the first value from the first underlying matrix.
     *
     * @return Entry value from the first underlying matrix.
     */
    public double getFirstValue()
    {
        return this.getFirstMatrix().getElement(
            this.getRowIndex(), this.getColumnIndex() );
    }

    /**
     * Sets the first value of the entry into the first underlying matrix.
     *
     * @param value Value to update the first underlying matrix.
     */
    public void setFirstValue(
        double value)
    {
        this.getFirstMatrix().setElement(
            this.getRowIndex(), this.getColumnIndex(), value);
    }

    /**
     * Gets the value from the second underlying matrix.
     *
     * @return Entry value from the second underlying matrix.
     */
    public double getSecondValue()
    {
        return this.getSecondMatrix().getElement(
            this.getRowIndex(), this.getColumnIndex() );
    }

    /**
     * Sets the entry value from the second underlying matrix.
     *
     * @param value Value to set the entry in the second underlying matrix.
     */
    public void setSecondValue(
        double value)
    {
        this.getSecondMatrix().setElement(
            this.getRowIndex(), this.getColumnIndex(), value);
    }
}
