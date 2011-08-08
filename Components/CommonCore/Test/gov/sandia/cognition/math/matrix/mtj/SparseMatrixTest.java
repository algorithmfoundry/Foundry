/*
 * File:                SparseMatrixTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 11, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.MatrixTestHarness;
import gov.sandia.cognition.math.matrix.Matrix;

/**
 *
 * @author Kevin R. Dixon
 */
public class SparseMatrixTest
    extends MatrixTestHarness
{
    
    public SparseMatrixTest(String testName)
    {
        super(testName);
    }

    protected Matrix createMatrix(
        int numRows,
        int numColumns)
    {
        return new SparseMatrix( numRows, numColumns );
    }

    protected Matrix createCopy(
        Matrix matrix)
    {
        return new SparseMatrix( matrix );
    }

}
