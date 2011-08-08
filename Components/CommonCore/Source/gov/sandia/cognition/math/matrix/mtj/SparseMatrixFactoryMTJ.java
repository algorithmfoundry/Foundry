/*
 * File:                SparseMatrixFactoryMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;

/**
 * Factory for MTJ's flexible sparse row matrix
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class SparseMatrixFactoryMTJ
    extends MatrixFactory<SparseMatrix>
{
    
    /**
     * Default instance of this
     */
    public final static SparseMatrixFactoryMTJ INSTANCE = new SparseMatrixFactoryMTJ();
    
    /**
     * Creates a new instance of SparseMatrixFactoryMTJ
     */
    public SparseMatrixFactoryMTJ()
    {
        super();
    }

    public SparseMatrix copyMatrix(
        Matrix m)
    {
        return new SparseMatrix( m );
    }

    public SparseMatrix createMatrix(
        int numRows,
        int numColumns)
    {
        return new SparseMatrix( numRows, numColumns );
    }
    
    /**
     * Creates a new wrapper for a sparse column MTJ matrix.
     *
     * @param internalMatrix The MTJ matrix to wrap.
     * @return A wrapper of the given MTJ matrix.
     */
    public SparseColumnMatrix createWrapper(
        final no.uib.cipr.matrix.sparse.FlexCompColMatrix internalMatrix)
    {
        return new SparseColumnMatrix(internalMatrix);
    }

    /**
     * Creates a new wrapper for a sparse row MTJ matrix.
     *
     * @param internalMatrix The MTJ matrix to wrap.
     * @return A wrapper of the given MTJ matrix.
     */
    public SparseRowMatrix createWrapper(
        final no.uib.cipr.matrix.sparse.FlexCompRowMatrix internalMatrix)
    {
        return new SparseRowMatrix(internalMatrix);
    }
    
}
