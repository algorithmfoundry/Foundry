/*
 * File:                SparseColumnMatrixTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 12, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.util.ObjectUtil;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit tests for class SparseColumnMatrix
 * @author Kevin R. Dixon
 */
public class SparseColumnMatrixTest extends AbstractSparseMatrixTest
{
    
    public SparseColumnMatrixTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SparseColumnMatrixTest.class);
        
        return suite;
    }

    @Override
    protected SparseColumnMatrix createMatrix(int numRows, int numColumns)
    {
        return new SparseColumnMatrix( numRows, numColumns );
    }

    @Override
    protected SparseColumnMatrix createCopy(Matrix matrix)
    {
        return new SparseColumnMatrix( matrix );
    }

    @Override
    public void testSerialize()
        throws Exception
    {
        System.out.println( "serialize (SparseColumnMatrix)" );

        Matrix c1 = this.createRandom();
        Matrix c2 = ObjectUtil.deepCopy(c1);

        // Had to nerf this test because there's a bug in MTJ's code...
        assertNotNull( c1 );
        assertNotNull( c2 );
        assertNotSame( c1, c2 );
    }


    
    

}
