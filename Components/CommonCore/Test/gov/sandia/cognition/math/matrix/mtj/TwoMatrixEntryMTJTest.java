/*
 * File:                TwoMatrixEntryMTJTest.java
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

import java.util.Random;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     TwoMatrixEntryMTJ
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class TwoMatrixEntryMTJTest
    extends TestCase
{
    protected Random random = new Random();
    
    public TwoMatrixEntryMTJTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(TwoMatrixEntryMTJTest.class);
        
        return suite;
    }

    /**
     * Test of getColumnIndex method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testGetColumnIndex()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second, 1, 2 );
        
        assertEquals(2, instance.getColumnIndex());
        
        first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        instance = new TwoMatrixEntryMTJ(first, second);
        
        assertEquals(0, instance.getColumnIndex());
    }

    /**
     * Test of setColumnIndex method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testSetColumnIndex()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second );
        
        assertEquals(0, instance.getColumnIndex());
        instance.setColumnIndex(1);
        assertEquals(1, instance.getColumnIndex());
    }

    /**
     * Test of getRowIndex method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testGetRowIndex()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second, 1, 2 );
        
        assertEquals(1, instance.getRowIndex());
        
        first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        instance = new TwoMatrixEntryMTJ(first, second);
        
        assertEquals(0, instance.getRowIndex());
    }

    /**
     * Test of setRowIndex method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testSetRowIndex()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second);
        
        assertEquals(0, instance.getRowIndex());
        instance.setRowIndex(1);
        assertEquals(1, instance.getRowIndex());
    }

    /**
     * Test of getFirstMatrix method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testGetFirstMatrix()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second);
        assertSame(first, instance.getFirstMatrix());
    }

    /**
     * Test of setFirstMatrix method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testSetFirstMatrix()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second);
        assertSame(first, instance.getFirstMatrix());
        
        DenseMatrix newFirst = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        instance.setFirstMatrix(newFirst);
        assertSame(newFirst, instance.getFirstMatrix());
    }

    /**
     * Test of getSecondMatrix method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testGetSecondMatrix()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second);
        assertSame(second, instance.getSecondMatrix());;
    }

    /**
     * Test of setSecondMatrix method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testSetSecondMatrix()
    {
        DenseMatrix first = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second);
        assertSame(second, instance.getSecondMatrix());

        DenseMatrix newSecondMatrix = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(4, 7);
        instance.setSecondMatrix(newSecondMatrix);
        assertSame(newSecondMatrix, instance.getSecondMatrix());
    }

    /**
     * Test of getFirstValue method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testGetFirstValue()
    {
        DenseMatrix first  = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second, 1, 2);

        assertEquals(first.getElement(1, 2), instance.getFirstValue());
    }

    /**
     * Test of setFirstValue method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testSetFirstValue()
    {
        DenseMatrix first  = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second, 1, 2);
        double value = Math.random();
        instance.setFirstValue(value);
        assertEquals(value, instance.getFirstValue());
        assertEquals(value, first.getElement(1, 2));
    }

    /**
     * Test of getSecondValue method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testGetSecondValue()
    {
        DenseMatrix first  = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second, 1, 2);
        assertEquals(second.getElement(1, 2), instance.getSecondValue());
    }

    /**
     * Test of setSecondValue method, of class gov.sandia.isrc.math.matrix.mtj.TwoMatrixEntryMTJ.
     */
    public void testSetSecondValue()
    {
        DenseMatrix first  = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        DenseMatrix second = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(4, 7, -1.0, 1.0, random);
        TwoMatrixEntryMTJ instance = new TwoMatrixEntryMTJ(first, second, 1, 2);
        double value = Math.random();
        instance.setSecondValue(value);
        assertEquals(value, instance.getSecondValue());
        assertEquals(value, second.getElement(1, 2));
    }
}
