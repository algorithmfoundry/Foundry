/*
 * File:                MatrixUnionIteratorMTJTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import junit.framework.*;
import gov.sandia.cognition.math.matrix.MatrixUnionIterator;
import gov.sandia.cognition.math.matrix.TwoMatrixEntry;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MatrixUnionIteratorMTJ
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class MatrixUnionIteratorMTJTest
    extends TestCase
{
    protected Random random = new Random();
    
    public MatrixUnionIteratorMTJTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MatrixUnionIteratorMTJTest.class);
        
        return suite;
    }
    
    /**
     * Test of hasNext method, of class gov.sandia.isrc.math.matrix.MatrixUnionIterator.
     */
    public void testHasNext()
    {
        Matrix first  = MatrixFactory.getDefault().createUniformRandom(4, 7, 1.0, 2.0, random);
        Matrix second = MatrixFactory.getDefault().createUniformRandom(4, 7, 3.0, 4.0, random);
        
        MatrixUnionIterator instance = 
            new MatrixUnionIteratorMTJ( new DenseMatrix( first ), new DenseMatrix( second) );
        
        for (int j = 0; j < 7; j++)
        {
            for (int i = 0; i < 4; i++)
            {
                assertTrue(instance.hasNext());
                TwoMatrixEntry entry = instance.next();
                assertEquals(first.getElement(i, j), entry.getFirstValue());
                assertEquals(second.getElement(i, j), entry.getSecondValue());
            }
        }
        
        assertFalse(instance.hasNext());
    }

    /**
     * Test of next method, of class gov.sandia.isrc.math.matrix.MatrixUnionIterator.
     */
    public void testNext()
    {
        Matrix first  = MatrixFactory.getDefault().createUniformRandom(4, 7, 1.0, 2.0, random);
        Matrix second = MatrixFactory.getDefault().createUniformRandom(4, 7, 3.0, 4.0, random);
        
        MatrixUnionIterator instance = 
            new MatrixUnionIteratorMTJ( new DenseMatrix( first ), new DenseMatrix( second) );
        
        for (int j = 0; j < 7; j++)
        {
            for (int i = 0; i < 4; i++)
            {
                TwoMatrixEntry entry = instance.next();
                assertEquals(first.getElement(i, j), entry.getFirstValue());
                assertEquals(second.getElement(i, j), entry.getSecondValue());
            }
        }
        boolean exceptionThrown = false;
        try
        {
            TwoMatrixEntry bad = instance.next();
        }
        catch ( NoSuchElementException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of remove method, of class gov.sandia.isrc.math.matrix.MatrixUnionIterator.
     */
    public void testRemove()
    {
        DenseMatrix first  = new DenseMatrix( MatrixFactory.getDefault().createUniformRandom(4, 7, 1.0, 2.0, random) );
        DenseMatrix second = new DenseMatrix( MatrixFactory.getDefault().createUniformRandom(4, 7, 3.0, 4.0, random) );
        
        MatrixUnionIteratorMTJ instance = 
            new MatrixUnionIteratorMTJ( first, second );
        
        for (int j = 0; j < 7; j++)
        {
            for (int i = 0; i < 4; i++)
            {
                assertTrue(instance.hasNext());
                TwoMatrixEntry entry = instance.next();
                instance.remove();
                assertEquals(0.0, first.getElement(i, j) );
                assertEquals(0.0, second.getElement(i, j) );
            }
        }
        

    }
    
}
