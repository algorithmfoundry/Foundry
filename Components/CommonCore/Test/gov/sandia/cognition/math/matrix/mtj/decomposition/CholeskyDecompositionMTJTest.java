/*
 * File:                CholeskyDecompositionMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 5, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.math.matrix.mtj.decomposition;

import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import junit.framework.TestCase;

/**
 * JUnit tests for class CholeskyDecompositionMTJTest
 * @author Kevin R. Dixon
 */
public class CholeskyDecompositionMTJTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class CholeskyDecompositionMTJTest
     * @param testName name of this test
     */
    public CholeskyDecompositionMTJTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of create method, of class CholeskyDecompositionMTJ.
     */
    public void testCreate()
    {
        System.out.println( "create" );
        
        // I validated these values in octave's chol() method
        double[][] v1 = { { 1, 2, 3 }, { 2, 20, 26 }, { 3, 26, 70 } };
        
        DenseMatrix A = DenseMatrixFactoryMTJ.INSTANCE.copyArray( v1 );
        
        double[][] v2 = { { 1, 2, 3 }, { 0, 4, 5 }, { 0, 0, 6 } };
        DenseMatrix expected = DenseMatrixFactoryMTJ.INSTANCE.copyArray( v2 );
        CholeskyDecompositionMTJ result = CholeskyDecompositionMTJ.create( A );
        
        System.out.println( "A =\n" + A );
        System.out.println( "R = \n" + result.getR() );
        
        DenseMatrix R = result.getR();
        
        assertEquals( expected, R );
        
        DenseMatrix Ahat = R.transpose().times( R );
        assertEquals( A, Ahat );
        
        try
        {
            CholeskyDecompositionMTJ.create( R );
            fail( "Should only accept symmetric matrices" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        DenseMatrix An = (DenseMatrix) A.negative();

        try
        {
            CholeskyDecompositionMTJ.create( An );
            fail( "Can only factor symmetric positive definite matrices!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

}
