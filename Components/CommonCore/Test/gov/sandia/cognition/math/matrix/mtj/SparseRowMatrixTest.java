/*
 * File:                SparseRowMatrixTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 30, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Matrix;

/**
 * JUnit tests for class SparseRowMatrix
 * @author Kevin R. Dixon
 */
public class SparseRowMatrixTest extends AbstractSparseMatrixTest
{
    
    public SparseRowMatrixTest(String testName)
    {
        super(testName);
    }

    @Override
    protected SparseRowMatrix createMatrix(
        int numRows,
        int numColumns)
    {
        return new SparseRowMatrix( numRows, numColumns );
    }

    @Override
    protected SparseRowMatrix createCopy(
        Matrix matrix)
    {
        return new SparseRowMatrix( matrix );
    }
    
}
