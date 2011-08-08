/*
 * File:                MatrixUnionIteratorMTJ.java
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

import gov.sandia.cognition.math.matrix.MatrixUnionIterator;

/**
 * Implementation of MatrixUnionIterator for MTJ-based matrices.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class MatrixUnionIteratorMTJ
    extends MatrixUnionIterator
{
    /**
     * Creates a new instance of AbstractMTJMatrixIntersectionIterator,
     * iterating at rowIndex = 0, columnIndex = 0
     *
     * @param firstMatrix Must have the same dimensions as secondMatrix.
     * @param secondMatrix Must have the same dimensions as firstMatrix.
     */
    public MatrixUnionIteratorMTJ( 
        final AbstractMTJMatrix firstMatrix,
        final AbstractMTJMatrix secondMatrix )
    {
        super( firstMatrix.iterator(), secondMatrix.iterator(),
            new TwoMatrixEntryMTJ(firstMatrix, secondMatrix),
            MatrixEntryIndexComparatorMTJ.INSTANCE);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove()
    {
        this.getInternalEntry().setFirstValue( 0.0 );
        this.getInternalEntry().setSecondValue( 0.0 );
    }
}
