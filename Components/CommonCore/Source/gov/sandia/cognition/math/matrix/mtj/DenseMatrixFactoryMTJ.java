/*
 * File:                DenseMatrixFactoryMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 18, 2007, Sandia Corporation.  Under the terms of Contract
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
 * MatrixFactory for creating MTJ's DenseMatrix-based Matrix
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class DenseMatrixFactoryMTJ
    extends MatrixFactory<DenseMatrix>
{
    
    /**
     * Default instance of this
     */
    public static final DenseMatrixFactoryMTJ INSTANCE = 
        new DenseMatrixFactoryMTJ();
    
    /**
     * Creates a new instance of DenseMatrixFactoryMTJ
     */
    public DenseMatrixFactoryMTJ()
    {
        super();
    }

    public DenseMatrix copyMatrix(
        Matrix m)
    {
        return new DenseMatrix( m );
    }

    public DenseMatrix createMatrix(
        int numRows,
        int numColumns)
    {
        return new DenseMatrix( numRows, numColumns );
    }
    
    /**
     * Creates a new wrapper for a dense MTJ matrix.
     * 
     * @param internalMatrix The MTJ matrix to wrap.
     * @return A wrapper of the given MTJ matrix.
     */
    public DenseMatrix createWrapper(
        final no.uib.cipr.matrix.DenseMatrix internalMatrix)
    {
        return new DenseMatrix(internalMatrix);
    }
}
