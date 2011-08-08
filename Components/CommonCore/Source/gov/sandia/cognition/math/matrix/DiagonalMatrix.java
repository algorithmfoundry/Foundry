/*
 * File:                DiagonalMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

/**
 * Interface describing a diagonal matrix.  That is, a square Matrix with
 * arbitrary values along the diagonal, and zero on the off-diagonal entries.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public interface DiagonalMatrix 
    extends Matrix
{

    /**
     * Gets the data along the diagonal
     * @return
     * Diagonal data with length of "dimension"
     */
    public double[] getDiagonal();
    
    /**
     * Gets the zero-based index diagonal element into the diagonal matrix
     * @param index
     * Zero-based index into the diagonal to get
     * @return
     * Diagonal value at index
     */
    public double getElement(
        int index );
    
    /**
     * Sets the zero-based index diagonal element into the diagonal matrix
     * @param index
     * Zero-based index into the diagonal to set
     * @param value 
     * Value to set
     */
    public void setElement(
        int index,
        double value );
    
    /**
     * Gets the dimensionality of this square matrix, which is equal to either
     * the number of rows or the number of columns
     * @return
     * Dimensionality of the square matrix
     */
    public int getDimensionality();
    
    // A diagonal matrix dot-multiplied by another will always be diagonal,
    // since the off-diagonal elements are necessarily zero
    public DiagonalMatrix dotTimes(
        Matrix matrix );
    
    public DiagonalMatrix pseudoInverse();
    
    public DiagonalMatrix pseudoInverse(
        double effectiveZero );
    
}
