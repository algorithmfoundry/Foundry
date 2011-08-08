/*
 * File:                AbstractMTJMatrixTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 27, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.MatrixTestHarness;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit tests for AbstractMTJMatrixTest
 * @author Kevin R. Dixon
 */
public class AbstractMTJMatrixTest
    extends MatrixTestHarness
{
    
    /**
     * Constructor
     * @param testName name
     */
    public AbstractMTJMatrixTest(String testName)
    {
        super(testName);
    }

    /**
     * Suite
     * @return test
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractMTJMatrixTest.class);
        
        return suite;
    }


    protected AbstractMTJMatrix createCopy(Matrix matrix)
    {
        if( (new Random()).nextBoolean() )
        {
            return new DenseMatrix( matrix );
        }
        else
        {
            return new SparseMatrix( matrix );
        }
    }

    protected AbstractMTJMatrix createMatrix(
        int numRows,
        int numColumns)
    {
        if( (new Random()).nextBoolean() )
        {
            return new DenseMatrix( numRows, numColumns );
        }
        else
        {
            return new SparseMatrix( numRows, numColumns );
        }
    }
   
    
    /**
     * Test of getInternalMatrix method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJMatrix.
     */
    public void testGetInternalMatrix()
    {
        System.out.println("getInternalMatrix");
        
        no.uib.cipr.matrix.DenseMatrix expResult =
            new no.uib.cipr.matrix.DenseMatrix( 10, 20 );
        AbstractMTJMatrix instance = new DenseMatrix( expResult );
        
        no.uib.cipr.matrix.Matrix result = instance.getInternalMatrix();
        assertEquals(expResult, result);

    }

    /**
     * Test of setInternalMatrix method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJMatrix.
     */
    public void testSetInternalMatrix()
    {
        System.out.println("setInternalMatrix");

        AbstractMTJMatrix instance = new DenseMatrix( 10, 20 );
        no.uib.cipr.matrix.DenseMatrix expResult =
            new no.uib.cipr.matrix.DenseMatrix( 10, 20 );
        
        assertFalse( instance.getInternalMatrix() == expResult );

        instance.setInternalMatrix(expResult);
        
        assertEquals( expResult, instance.getInternalMatrix() );
    }


    /**
     * Test of transposeInto method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJMatrix.
     */
    public void testTransposeInto()
    {
        System.out.println("transposeInto");
        
        int M = 10;
        int N = 20;
        
        AbstractMTJMatrix m1 = (AbstractMTJMatrix) DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom( M, N, -10, 10, RANDOM  );
        AbstractMTJMatrix r1 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix( N, M );
            
        m1.transposeInto( r1 );
        
        for( MatrixEntry e : r1 )
        {
            assertEquals( e.getValue(),
                m1.getElement(e.getColumnIndex(), e.getRowIndex()) );
        }

        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix( M, N );
        DenseMatrix m3 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix( M, M );
        DenseMatrix m4 = DenseMatrixFactoryMTJ.INSTANCE.createMatrix( N, N );
        
        
        try
        {
            m1.transposeInto( m2 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
            
        try
        {
            m1.transposeInto( m3 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
        
        try
        {
            m1.transposeInto( m4 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
                 
    }

    /**
     * Test of getSubMatrixInto method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJMatrix.
     */
    public void testGetSubMatrixInto()
    {
        System.out.println("getSubMatrixInto");
        
        int numRows = 5;
        int numColumns = 2;
        
        int minRow = 3;
        int maxRow = minRow+numRows-1;
        int minColumn = 4;
        int maxColumn = minColumn+numColumns-1;

        AbstractMTJMatrix submatrix = (AbstractMTJMatrix) DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(
            numRows, numColumns, -10, 10, RANDOM  );
        
        int M = 50;
        int N = 100;        
        AbstractMTJMatrix matrix = (AbstractMTJMatrix) DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom( M, N, 100, 200, RANDOM  );
        
        for( MatrixEntry e : submatrix )
        {
            matrix.setElement( e.getRowIndex() + minRow,
                e.getColumnIndex() + minColumn, e.getValue() );
        }
        
        AbstractMTJMatrix r1 = new DenseMatrix( numRows, numColumns );
        matrix.getSubMatrixInto( minRow, maxRow, minColumn, maxColumn, r1 );
        
        assertEquals( submatrix, r1 );

        AbstractMTJMatrix m1 = new DenseMatrix( M, N );
        try
        {
            matrix.getSubMatrixInto( minRow, maxRow, minColumn, maxColumn, m1 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
        
        AbstractMTJMatrix m2 = new DenseMatrix( numRows, numRows );
        try
        {
            matrix.getSubMatrixInto( minRow, maxRow, minColumn, maxColumn, m2 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }

        AbstractMTJMatrix m3 = new DenseMatrix( numColumns, numColumns );
        try
        {
            matrix.getSubMatrixInto( minRow, maxRow, minColumn, maxColumn, m3 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
                
        AbstractMTJMatrix m4 = new DenseMatrix( M+1, N-1 );
        try
        {
            matrix.getSubMatrixInto( minRow, maxRow, minColumn, maxColumn, m4 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
                
    }

    /**
     * Test of timesInto method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJMatrix.
     */
    public void testTimesInto()
    {
        System.out.println("timesInto");
        
        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );
        DenseMatrix m1 = DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors( column1, column2, column3 );
        
        DenseMatrix m2 = new DenseMatrix( 3, 4 );
        m2.setElement( 0, 0, 1.0 );
        m2.setElement( 0, 1, 2.0 );
        m2.setElement( 0, 2, 3.0 );
        m2.setElement( 0, 3, 4.0 );
        
        m2.setElement( 1, 0, 5.0 );
        m2.setElement( 1, 1, 6.0 );
        m2.setElement( 1, 2, -1.0 );
        m2.setElement( 1, 3, -2.0 );
        
        m2.setElement( 2, 0, -3.0 );
        m2.setElement( 2, 1, -4.0 );
        m2.setElement( 2, 2, -5.0 );
        m2.setElement( 2, 3, -6.0 );
        
        DenseMatrix r1 = new DenseMatrix( 3, 4 );
        m1.timesInto( m2, r1 );
        
        DenseMatrix m3 = new DenseMatrix( 3, 4 );
        m3.setElement( 0, 0, 0  );
        m3.setElement( 0, 1, -2 );
        m3.setElement( 0, 2, -36 );
        m3.setElement( 0, 3, -46 );
        
        m3.setElement( 1, 0, 51 );
        m3.setElement( 1, 1, 66 );
        m3.setElement( 1, 2, 41 );
        m3.setElement( 1, 3, 46 );
        
        m3.setElement( 2, 0, 6 );
        m3.setElement( 2, 1, 6 );
        m3.setElement( 2, 2, -42 );
        m3.setElement( 2, 3, -54 );
        
        assertEquals( m3, r1 );
        
        try
        {
            m2.timesInto( m1, r1 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }

        DenseMatrix m4 = new DenseMatrix( 10, 10 );
        try
        {
            m4.timesInto( m1, r1 );
            fail( "Did not throw exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Good! Threw exception: " + e );
        }
        
    }
    
}
