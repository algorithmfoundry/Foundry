/*
 * File:                SparseMatrixFactoryMTJTest.java
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

import gov.sandia.cognition.math.matrix.MatrixFactoryTestHarness;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit tests for SparseMatrixFactoryMTJTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class SparseMatrixFactoryMTJTest
    extends MatrixFactoryTestHarness
{
    
    public SparseMatrixFactoryMTJTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SparseMatrixFactoryMTJTest.class);
        
        return suite;
    }

    public SparseMatrixFactoryMTJ createFactory()
    {
        return SparseMatrixFactoryMTJ.INSTANCE;
    }

    @Override
    public SparseMatrix createRandomMatrix()
    {
        int M = random.nextInt(10) + 1;
        int N = random.nextInt(10) + 1;
        return this.createFactory().createUniformRandom(M, N, -RANGE, RANGE, random);
    }

}
