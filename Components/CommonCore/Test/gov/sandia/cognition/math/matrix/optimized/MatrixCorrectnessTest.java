/*
 * File:                MatrixCorrectnessTest.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.testutil.AssertUtil;
import gov.sandia.cognition.testutil.MatrixUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the gov.sandia.cognition.math.matrix.optimized matrix instances for
 * accuracy. These are not large-matrix tests, they are intentionally small so
 * that the tests run fast and are easy to check for correctness.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class MatrixCorrectnessTest
{

    /**
     * Tests sparse matrix serialization code
     */
    @Test
    public void testSerialization()
        throws IOException, ClassNotFoundException
    {
        // Load up a matrix
        SparseMatrix m = new SparseMatrix(4, 4);
        m.setElement(0, 0, 2);
        m.setElement(0, 1, -1);
        m.setElement(1, 0, -1);
        m.setElement(1, 1, 2);
        m.setElement(1, 2, -1);
        m.setElement(2, 1, -1);
        m.setElement(2, 2, 2);
        m.setElement(2, 3, -1);
        m.setElement(3, 2, -1);
        m.setElement(3, 3, 2);
        // Stream it out
        File f = new File("tmp.oos");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
        oos.writeObject(m);
        oos.close();

        // Read it back into another matrix
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
        SparseMatrix input = (SparseMatrix) ois.readObject();
        // Make sure the two are the same
        assertEquals(input, m);
        // Make sure the read-in version is as expected
        assertTrue(input.isCompressed());
        f.delete();

        // Now let's make sure it can do basic ops w/o problem
        Vector v = new DenseVector(4);
        v.setElement(0, 1);
        v.setElement(1, 2);
        v.setElement(3, 3);
        MatrixUtil.testVectorEquals(input.times(v), DenseVector.class, 4, 0, 3,
            -5, 6);
        MatrixUtil.testVectorEquals(input.preTimes(v), DenseVector.class, 4, 0,
            3,
            -5, 6);
        MatrixUtil.testMatrixEquals(m.times(input), Matrix.class, 4, 4, 5,
            -4, 1, 0, -4, 6, -4, 1, 1, -4, 6, -4, 0, 1, -4, 5);
        MatrixUtil.testMatrixEquals(input.times(m), Matrix.class, 4, 4, 5,
            -4, 1, 0, -4, 6, -4, 1, 1, -4, 6, -4, 0, 1, -4, 5);
    }

    /**
     * Tests all matrix operations for diagonal matrix instances on all input
     * types. Attempts to be quite thorough testing different sizes, etc. Checks
     * the results for correctness.
     */
    @Test
    public void testDiagonalMatrixOps()
    {
        double[] d1 =
        {
            4, 3, 2, 1
        };
        DiagonalMatrix m1 = new DiagonalMatrix(d1);
        double[] d2 =
        {
            4, 5, 6, 0
        };
        DiagonalMatrix m2 = new DiagonalMatrix(d2);
        double[][] d3 =
        {
            {
                4, 2
            },
            {
                3, 6
            },
            {
                0, 1
            },
            {
                -2, 4
            }
        };
        DenseMatrix ds1 = new DenseMatrix(d3);
        double[][] d4 =
        {
            {
                1, 0, 1, 0
            },
            {
                1, 1, 1, 0
            },
            {
                0, 1, 0, 1
            },
            {
                0, 0, 0, 1
            }
        };
        DenseMatrix ds2 = new DenseMatrix(d4);
        SparseMatrix s = new SparseMatrix(4, 4);
        s.setElement(0, 0, 4);
        s.setElement(0, 3, 6);
        s.setElement(1, 2, 3);
        s.setElement(1, 3, 2);
        s.setElement(2, 1, 1);
        s.setElement(3, 1, 1);
        s.setElement(3, 3, 5);
        double[] d5 =
        {
            4, 3, 2, 1
        };
        DenseVector v1 = new DenseVector(d5);
        SparseVector v2 = new SparseVector(4);
        v2.setElement(3, 3);
        v2.setElement(2, 3);

        Matrix ans;
        Vector v;

        // m1.assertSameDimensions(ans)
        m1.assertSameDimensions(m1);
        m1.assertSameDimensions(m2);
        m1.assertSameDimensions(ds2);
        m1.assertSameDimensions(s);
        try
        {
            m1.assertSameDimensions(new DiagonalMatrix(2));
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path, that assert should throw something
        }
        try
        {
            m1.assertSameDimensions(new SparseMatrix(2, 4));
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path, that assert should throw something
        }
        try
        {
            m1.assertSameDimensions(new DenseMatrix(4, 3));
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path, that assert should throw something
        }

        // m1.checkMultiplicationDimensions(ans)
        assertTrue(m1.checkMultiplicationDimensions(m1));
        assertTrue(m1.checkMultiplicationDimensions(m2));
        assertTrue(m1.checkMultiplicationDimensions(ds2));
        assertTrue(m1.checkMultiplicationDimensions(ds1));
        assertTrue(m1.checkMultiplicationDimensions(s));
        assertFalse(m1.checkMultiplicationDimensions(new DiagonalMatrix(3)));
        assertTrue(m1.checkMultiplicationDimensions(new DenseMatrix(4, 3)));
        assertFalse(m1.checkMultiplicationDimensions(new DenseMatrix(2, 3)));
        assertFalse(m1.checkMultiplicationDimensions(new SparseMatrix(3, 4)));

        // m1.checkSameDimensions(ans)
        assertTrue(m1.checkSameDimensions(m1));
        assertTrue(m1.checkSameDimensions(m2));
        assertTrue(m1.checkSameDimensions(ds2));
        assertFalse(m1.checkSameDimensions(ds1));
        assertTrue(m1.checkSameDimensions(s));

        // m1.clone()
        ans = m1.clone();
        assertTrue(ans instanceof DiagonalMatrix);
        assertTrue(ans.equals(m1));
        ans = m2.clone();
        assertTrue(ans instanceof DiagonalMatrix);
        assertTrue(ans.equals(m2));

        // m1.convertFromVector(v);
        SparseVector tmp = new SparseVector(16);
        tmp.setElement(0, 1);
        tmp.setElement(5, 2);
        tmp.setElement(10, 3);
        tmp.setElement(15, 4);
        ans = m1.clone();
        ans.convertFromVector(tmp);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 1, 0, 0, 0,
            0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 4);

        // m1.convertToVector()
        v = m1.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 16, 4, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 2, 0, 0, 0, 0, 1);

        // m1.dotTimes(ans)
        ans = m1.dotTimes(m1);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 16, 0, 0, 0,
            0, 9, 0, 0, 0, 0, 4, 0, 0, 0, 0, 1);
        ans = m2.dotTimes(m1);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 16, 0, 0, 0,
            0, 15, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0);
        ans = m1.dotTimes(ds2);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 4, 0, 0, 0,
            0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
        ans = m1.dotTimes(s);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 16, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5);

        // m1.dotTimesEquals(ans);
        ans = m1.clone();
        ans.dotTimesEquals(m1);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 16, 0, 0, 0,
            0, 9, 0, 0, 0, 0, 4, 0, 0, 0, 0, 1);
        ans = m2.clone();
        ans.dotTimesEquals(m1);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 16, 0, 0, 0,
            0, 15, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0);
        ans = m1.clone();
        ans.dotTimesEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 4, 0, 0, 0,
            0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
        ans = m1.clone();
        ans.dotTimesEquals(s);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 16, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5);

        // m1.equals(this)
        // Tested above with clone
        // m1.equals(ans, effectiveZero)
        ans = m1.clone();
        ans.setElement(0, 0, 3.99);
        assertTrue(m1.equals(ans, 0.01));
        assertFalse(m1.equals(ans, 0.00999999));
        ans.setElement(1, 1, 3.1);
        // Turns out there's a rounding error in the math
        assertTrue(m1.equals(ans, 0.1 + 1e-10));
        assertFalse(m1.equals(ans, 0.0999999));
        assertTrue(m1.equals(m2, 4));
        assertFalse(m1.equals(m2, 3.9999));
        assertTrue(m1.equals(ds2, 3));

        // m1.getColumn(columnIndex)
        v = m1.getColumn(0);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 4, 0, 0, 0);
        v = m2.getColumn(3);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 0, 0, 0, 0);

        // m1.getElement(rowIndex, columnIndex)
        // Tested above in most of the assertEquals
        // m1.getNumColumns();
        assertEquals(m1.getNumColumns(), 4);
        assertEquals(m2.getNumColumns(), 4);

        // m1.getNumRows();
        assertEquals(m1.getNumRows(), 4);
        assertEquals(m2.getNumRows(), 4);

        // m1.getRow(rowIndex)
        v = m1.getRow(0);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 4, 0, 0, 0);
        v = m2.getRow(3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 0, 0, 0);

        // m1.getSubMatrix(minRow, maxRow, minColumn, maxColumn)
        ans = m1.getSubMatrix(1, 2, 2, 2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 1, 0, 2);
        ans = m2.getSubMatrix(0, 2, 1, 2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 2, 0, 0, 5, 0, 0, 6);

        // m1.identity()
        ans = m1.clone();
        ans.identity();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 1, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 1);
        ans = m2.clone();
        ans.identity();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 1, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 1);

        // m1.inverse()
        ans = m1.inverse();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, .25, 0, 0, 0, 0,
            .33333333333, 0, 0, 0, 0, .5, 0, 0, 0, 0, 1);

        // m1.isSquare()
        assertTrue(m1.isSquare());
        assertTrue(m2.isSquare());

        // m1.isSymmetric()
        assertTrue(m1.isSymmetric());
        assertTrue(m2.isSymmetric());

        // m1.isSymmetric(effectiveZero);
        // There's no good way to test this one because there's no way to get false
        assertTrue(m1.isSymmetric(0.1));
        assertTrue(m2.isSymmetric(0.1));

        // m1.isZero()
        ans = m1.clone();
        assertFalse(ans.isZero());
        ans.setElement(0, 0, 0);
        assertFalse(ans.isZero());
        ans.setElement(1, 1, 0);
        assertFalse(ans.isZero());
        ans.setElement(2, 2, 0);
        assertFalse(ans.isZero());
        ans.setElement(3, 3, 0);
        assertTrue(ans.isZero());

        // m1.isZero(effectiveZero);
        ans = m1.clone();
        assertFalse(ans.isZero(3.9));
        assertTrue(ans.isZero(4.0));
        ans.setElement(0, 0, 0.1);
        assertFalse(ans.isZero(2.9));
        assertTrue(ans.isZero(3));
        ans.setElement(1, 1, 0);
        ans.setElement(2, 2, 0);
        ans.setElement(3, 3, 0);
        assertFalse(ans.isZero(0.09));
        assertTrue(ans.isZero(0.1));

        // m1.iterator()
        // Tested by testMatrixEquals and the Dense code
        // m1.logDeterminant()
        ComplexNumber c = m1.logDeterminant();
        assertEquals(Math.log(24), c.getRealPart(), 1e-10);
        assertEquals(0, c.getImaginaryPart(), 1e-10);
        c = m2.logDeterminant();
        assertEquals(Double.NEGATIVE_INFINITY, c.getRealPart(), 1e-10);
        assertEquals(0, c.getImaginaryPart(), 1e-10);
        ans = m1.clone();
        ans.setElement(0, 0, -4);
        ans.setElement(1, 1, -3);
        ans.setElement(3, 3, -1);
        c = ans.logDeterminant();
        assertEquals(Math.log(24), c.getRealPart(), 1e-10);
        assertEquals(Math.PI, c.getImaginaryPart(), 1e-10);

        // m1.minus(ans)
        // dense
        ans = m1.minus(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 3, 0, -1, 0, -1, 2, -1, 0,
            0, -1, 2, -1, 0, 0, 0, 0);
        ans = m2.minus(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 3, 0, -1, 0, -1, 4, -1, 0,
            0, -1, 6, -1, 0, 0, 0, -1);
        // diagonal
        ans = m1.minus(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0);
        ans = m1.minus(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, -2, 0,
            0, 0, 0, -4, 0, 0, 0, 0, 1);
        ans = m2.minus(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 2, 0, 0,
            0, 0, 4, 0, 0, 0, 0, -1);
        ans = m2.minus(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0);
        // sparse
        ans = m1.minus(s);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 4, 0, 0, 0, -6, 0, 3, -3,
            -2, 0, -1, 2, 0, 0, -1, 0, -4);
        ans = m2.minus(s);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 4, 0, 0, 0, -6, 0, 5, -3,
            -2, 0, -1, 6, 0, 0, -1, 0, -5);

        // m1.minusEquals(ans);
        // dense
        try
        {
            ans = m1.clone();
            ans.minusEquals(ds2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        ans = m2.clone();
        Matrix t = ds2.clone();
        t.identity();
        ans.minusEquals(t);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 3, 0, 0, 0,
            0, 4, 0, 0, 0, 0, 5, 0, 0, 0, 0, -1);
        // diagonal
        ans = m1.clone();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        ans = m1.clone();
        ans.minusEquals(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, -2,
            0, 0, 0, 0, -4, 0, 0, 0, 0, 1);
        ans = m2.clone();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 4, 0, 0, 0, 0, -1);
        ans = m2.clone();
        ans.minusEquals(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        // sparse
        try
        {
            ans = m1.clone();
            ans.minusEquals(s);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        ans = m2.clone();
        t = new SparseMatrix(m1);
        t.minusEquals(m2);
        ans.minusEquals(t);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 4, 0, 0, 0,
            0, 7, 0, 0, 0, 0, 10, 0, 0, 0, 0, -1);

        // m1.negative()
        ans = m1.negative();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, -4, 0, 0, 0, 0, -3, 0,
            0, 0, 0, -2, 0, 0, 0, 0, -1);
        ans = m2.negative();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, -4, 0, 0, 0, 0, -5, 0,
            0, 0, 0, -6, 0, 0, 0, 0, 0);

        // m1.negativeEquals();
        ans = m1.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, -4, 0, 0, 0, 0, -3, 0,
            0, 0, 0, -2, 0, 0, 0, 0, -1);
        ans = m2.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, -4, 0, 0, 0, 0, -5, 0,
            0, 0, 0, -6, 0, 0, 0, 0, 0);

        // m1.normFrobenius()
        AssertUtil.equalToNumDigits(Math.sqrt(30), m1.normFrobenius(), 6);
        AssertUtil.equalToNumDigits(Math.sqrt(77), m2.normFrobenius(), 6);

        // m1.plus(ans)
        // dense
        ans = m1.plus(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 5, 0, 1, 0, 1, 4, 1,
            0, 0, 1, 2, 1, 0, 0, 0, 2);
        ans = m2.plus(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 5, 0, 1, 0, 1, 6, 1,
            0, 0, 1, 6, 1, 0, 0, 0, 1);
        // diagonal
        ans = m1.plus(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 8, 0, 0, 0, 0, 6,
            0, 0, 0, 0, 4, 0, 0, 0, 0, 2);
        ans = m1.plus(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 8, 0, 0, 0, 0, 8,
            0, 0, 0, 0, 8, 0, 0, 0, 0, 1);
        ans = m2.plus(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 8, 0, 0, 0, 0, 8,
            0, 0, 0, 0, 8, 0, 0, 0, 0, 1);
        ans = m2.plus(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 8, 0, 0, 0, 0, 10,
            0, 0, 0, 0, 12, 0, 0, 0, 0, 0);
        // sparse
        ans = m1.plus(s);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 4, 8, 0, 0, 6, 0, 3, 3,
            2, 0, 1, 2, 0, 0, 1, 0, 6);
        ans = m2.plus(s);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 4, 8, 0, 0, 6, 0, 5, 3,
            2, 0, 1, 6, 0, 0, 1, 0, 5);

        // m1.plusEquals(ans)
        // dense
        // Test a diagonal dense matrix, it should work
        Matrix m = ds2.clone();
        m.identity();
        ans = m1.clone();
        ans.plusEquals(m);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 5, 0, 0, 0, 0, 4, 0, 0,
            0, 0, 3, 0, 0, 0, 0, 2);
        // Regular dense matrices won't
        try
        {
            ans = m1.clone();
            ans.plusEquals(ds2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            ans = m2.clone();
            ans.plusEquals(ds2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        // diagonal
        ans = m1.plus(m1);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 8, 0, 0, 0,
            0, 6, 0, 0, 0, 0, 4, 0, 0, 0, 0, 2);
        ans = m1.plus(m2);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 8, 0, 0, 0,
            0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 1);
        ans = m2.plus(m1);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 8, 0, 0, 0,
            0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 1);
        ans = m2.plus(m2);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 8, 0, 0, 0,
            0, 10, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0);
        // sparse
        // Test a diagonal sparse matrix, it should work
        m = new SparseMatrix(m1);
        m.minusEquals(m2);
        ans = m1.clone();
        ans.plusEquals(m);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 4, 4, 4, 0, 0, 0,
            0, 1, 0, 0, 0, 0, -2, 0, 0, 0, 0, 2);
        // Regular sparse matrices won't
        try
        {
            ans = m1.clone();
            ans.plusEquals(s);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            ans = m2.clone();
            ans.plusEquals(s);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }

        // m1.pseudoInverse()
        ans = m1.pseudoInverse();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, .25, 0, 0, 0, 0,
            .33333333333, 0, 0, 0, 0, .5, 0, 0, 0, 0, 1);
        ans = m2.pseudoInverse();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, .25, 0, 0, 0, 0, .2, 0,
            0, 0, 0, 0.166666666667, 0, 0, 0, 0, 0);

        // m1.pseudoInverse(effectiveZero)
        ans = m1.pseudoInverse(1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, .25, 0, 0, 0, 0,
            .33333333333, 0, 0, 0, 0, .5, 0, 0, 0, 0, 0);
        ans = m2.pseudoInverse(4);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, .2, 0,
            0, 0, 0, 0.166666666667, 0, 0, 0, 0, 0);

        // m1.rank()
        assertEquals(4, m1.rank());
        assertEquals(3, m2.rank());

        // m1.rank(effectiveZero);
        assertEquals(4, m1.rank(0.9));
        assertEquals(3, m1.rank(1.0));
        assertEquals(3, m2.rank(3.9));
        assertEquals(2, m2.rank(4.0));

        // m1.scale(scaleFactor)
        ans = m1.scale(2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 8, 0, 0, 0, 0, 6, 0, 0,
            0, 0, 4, 0, 0, 0, 0, 2);
        ans = m2.scale(-2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, -8, 0, 0, 0, 0, -10, 0,
            0, 0, 0, -12, 0, 0, 0, 0, 0);

        // m1.scaleEquals(scaleFactor);
        ans = m1.clone();
        ans.scaleEquals(2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 8, 0, 0, 0, 0, 6, 0, 0,
            0, 0, 4, 0, 0, 0, 0, 2);
        ans = m2.clone();
        ans.scaleEquals(-2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, -8, 0, 0, 0, 0, -10, 0,
            0, 0, 0, -12, 0, 0, 0, 0, 0);

        // m1.setColumn(columnIndex, v)
        v = ds2.getRow(3);
        v.setElement(3, 3);
        ans = m1.clone();
        ans.setColumn(3, v);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 4, 0, 0, 0, 0, 3, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 3);
        v = s.getRow(2);
        ans = m1.clone();
        ans.setColumn(1, v);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 4, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 1);
        try
        {
            ans.setColumn(0, v1);
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            ans.setColumn(0, v2);
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }

        // m1.setElement(rowIndex, columnIndex, value);
        ans = m1.clone();
        ans.setElement(2, 2, 3);
        ans.setElement(3, 3, 3);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 4, 0, 0, 0, 0, 3,
            0, 0,
            0, 0, 3, 0, 0, 0, 0, 3);
        try
        {
            m1.setElement(0, 1, 2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            m1.setElement(2, 1, 2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }

        // m1.setRow(rowIndex, v);
        v = ds2.getRow(3);
        v.setElement(3, 3);
        ans = m1.clone();
        ans.setRow(3, v);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 4, 0, 0, 0, 0, 3, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 3);
        v = s.getRow(2);
        ans = m1.clone();
        ans.setRow(1, v);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 4, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 1);
        try
        {
            ans.setRow(0, v1);
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            ans.setRow(0, v2);
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }

        // m1.setSubMatrix(minRow, minColumn, ans);
        ans = m1.clone();
        ans.setSubMatrix(0, 2, new SparseMatrix(2, 2));
        assertTrue(ans.equals(m1));
        ans.setSubMatrix(2, 0, new DenseMatrix(new SparseMatrix(2, 2)));
        assertTrue(ans.equals(m1));
        ans.setSubMatrix(1, 1, m2.getSubMatrix(0, 2, 0, 2));
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 4, 0, 0, 0, 0, 4, 0, 0,
            0, 0, 5, 0, 0, 0, 0, 6);

        // m1.solve(matrix)
        ans = m1.solve(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, .25, 0, .25, 0,
            .333333333, .333333333, .33333333, 0, 0, .5, 0, .5, 0, 0, 0, 1);
        try
        {
            // m2 doesn't span all 4 columns
            m2.solve(s);
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        {
            // correct path
        }

        // m1.solve(vector)
        v = m1.solve(v1);
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 1, 1, 1, 1);
        v = m1.solve(v2);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 0, 0, 1.5, 3);
        try
        {
            // m2 doesn't span all 4 columns
            m2.solve(v2);
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        {
            // correct path
        }

        // m1.sumOfColumns()
        v = m1.sumOfColumns();
        MatrixUtil.testVectorEquals(v,
            Vector.class, 4, 4, 3, 2, 1);
        v = m2.sumOfColumns();
        MatrixUtil.testVectorEquals(v,
            Vector.class, 4, 4, 5, 6, 0);

        // m1.sumOfRows()
        v = m1.sumOfRows();
        MatrixUtil.testVectorEquals(v,
            Vector.class, 4, 4, 3, 2, 1);
        v = m2.sumOfRows();
        MatrixUtil.testVectorEquals(v,
            Vector.class, 4, 4, 5, 6, 0);

        // m1.times(matrix)
        // dense
        ans = m1.times(ds1);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 16, 8, 9, 18, 0, 2, -2, 4);
        ans = m2.times(ds1);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 16, 8, 15, 30, 0, 6, 0, 0);
        ans = m1.times(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 4, 0, 4, 0, 3, 3, 3, 0, 0,
            2, 0, 2, 0, 0, 0, 1);
        ans = m2.times(ds2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 4, 0, 4, 0, 5, 5, 5, 0, 0,
            6, 0, 6, 0, 0, 0, 0);
        // diagonal
        ans = m1.times(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 16, 0, 0, 0, 0, 9, 0,
            0, 0, 0, 4, 0, 0, 0, 0, 1);
        ans = m1.times(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 16, 0, 0, 0, 0, 15, 0,
            0, 0, 0, 12, 0, 0, 0, 0, 0);
        ans = m2.times(m1);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 16, 0, 0, 0, 0, 15, 0,
            0, 0, 0, 12, 0, 0, 0, 0, 0);
        ans = m2.times(m2);
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 16, 0, 0, 0, 0, 25, 0,
            0, 0, 0, 36, 0, 0, 0, 0, 0);
        // sparse
        ans = m1.times(s);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 4, 16, 0, 0, 24, 0, 0, 9, 6,
            0, 2, 0, 0, 0, 1, 0, 5);
        s.decompress();
        ans = m2.times(s);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 4, 16, 0, 0, 24, 0, 0, 15,
            10, 0, 6, 0, 0, 0, 0, 0, 0);

        // m1.times(vector)
        // dense
        v = m1.times(v1);
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 16, 9, 4, 1);
        v = m2.times(v1);
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 16, 15, 12, 0);
        // dense pretimes
        v = v1.times(m1);
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 16, 9, 4, 1);
        v = v1.times(m2);
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 16, 15, 12, 0);
        // sparse
        v = m1.times(v2);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 0, 0, 6, 3);
        v = m2.times(v2);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 0, 0, 18, 0);
        // sparse pretimes
        v = v2.times(m1);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 0, 0, 6, 3);
        v = v2.times(m2);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 4, 0, 0, 18, 0);

        // m1.toString()
        assertEquals("4 0 0 0 \n0 3 0 0 \n0 0 2 0 \n0 0 0 1 \n", m1.toString(
            DecimalFormat.getInstance()));

        // m1.trace()
        AssertUtil.equalToNumDigits(10, m1.trace(), 6);
        AssertUtil.equalToNumDigits(15, m2.trace(), 6);

        // m1.transpose()
        ans = m1.transpose();
        assertTrue(ans instanceof DiagonalMatrix);
        MatrixUtil.testIsTranspose(m1, ans);
        ans = m2.transpose();
        assertTrue(ans instanceof DiagonalMatrix);
        MatrixUtil.testIsTranspose(m2, ans);

        // m1.zero()
        ans = m1.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            DiagonalMatrix.class, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Tests all matrix operations for sparse matrix instances on all input
     * types. Attempts to be quite thorough testing different sizes, etc. Checks
     * the results for correctness.
     */
    @Test
    public void testSparseMatrixOps()
    {
        SparseMatrix m1 = new SparseMatrix(3, 4);
        m1.setElement(0, 0, 4);
        m1.setElement(0, 3, 6);
        m1.setElement(1, 2, 3);
        m1.setElement(1, 3, 2);
        m1.setElement(2, 1, 1);
        SparseMatrix m2 = new SparseMatrix(4, 2);
        m2.setElement(0, 0, 6);
        m2.setElement(0, 1, 2);
        m2.setElement(1, 0, 4);
        m2.setElement(2, 1, 3);
        m2.setElement(3, 0, 2);
        SparseMatrix m3 = new SparseMatrix(2, 2);
        m3.setElement(0, 1, 1);
        m3.setElement(1, 0, 5);
        double[] d1 =
        {
            4, 6
        };
        DiagonalMatrix diag = new DiagonalMatrix(d1);
        double[][] d2 =
        {
            {
                4, 7, 2, 6
            },
            {
                0, 1, 3, 2
            }
        };
        DenseMatrix ds1 = new DenseMatrix(d2);
        double[][] d3 =
        {
            {
                6, 2
            },
            {
                1, 5
            }
        };
        DenseMatrix ds2 = new DenseMatrix(d3);
        double[] d5 =
        {
            4, 3, 2, 1
        };
        DenseVector v1 = new DenseVector(d5);
        SparseVector v2 = new SparseVector(4);
        v2.setElement(3, 3);
        v2.setElement(2, 3);

        Matrix ans;
        Vector v;

        // m1.assertSameDimensions(ans)
        m1.assertSameDimensions(new SparseMatrix(3, 4));
        m1.assertSameDimensions(new DenseMatrix(3, 4));
        m3.assertSameDimensions(diag);
        try
        {
            m1.assertSameDimensions(m2);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path
        }
        try
        {
            m1.assertSameDimensions(diag);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path
        }
        try
        {
            m1.assertSameDimensions(ds1);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path
        }

        // m1.checkMultiplicationDimensions(ans)
        assertTrue(m1.checkMultiplicationDimensions(m2));
        assertFalse(m1.checkMultiplicationDimensions(m3));
        assertFalse(m1.checkMultiplicationDimensions(diag));
        assertFalse(m1.checkMultiplicationDimensions(ds1));
        assertFalse(m1.checkMultiplicationDimensions(ds2));
        assertFalse(m2.checkMultiplicationDimensions(m1));
        assertTrue(m2.checkMultiplicationDimensions(m3));
        assertTrue(m2.checkMultiplicationDimensions(diag));
        assertTrue(m2.checkMultiplicationDimensions(ds1));
        assertTrue(m2.checkMultiplicationDimensions(ds2));
        assertFalse(m3.checkMultiplicationDimensions(m1));
        assertFalse(m3.checkMultiplicationDimensions(m2));
        assertTrue(m3.checkMultiplicationDimensions(diag));
        assertTrue(m3.checkMultiplicationDimensions(ds1));
        assertTrue(m3.checkMultiplicationDimensions(ds2));

        // m1.checkSameDimensions(ans)
        assertFalse(m1.checkSameDimensions(m2));
        assertFalse(m1.checkSameDimensions(m3));
        assertFalse(m1.checkSameDimensions(diag));
        assertFalse(m1.checkSameDimensions(ds1));
        assertFalse(m1.checkSameDimensions(ds2));
        assertFalse(m2.checkSameDimensions(m1));
        assertFalse(m2.checkSameDimensions(m3));
        assertFalse(m2.checkSameDimensions(diag));
        assertFalse(m2.checkSameDimensions(ds1));
        assertFalse(m2.checkSameDimensions(ds2));
        assertFalse(m3.checkSameDimensions(m1));
        assertFalse(m3.checkSameDimensions(m2));
        assertTrue(m3.checkSameDimensions(diag));
        assertFalse(m3.checkSameDimensions(ds1));
        assertTrue(m3.checkSameDimensions(ds2));

        // m1.clone()
        ans = m1.clone();
        assertTrue(ans instanceof SparseMatrix);
        assertTrue(ans.equals(m1));
        ans = m2.clone();
        assertTrue(ans instanceof SparseMatrix);
        assertTrue(ans.equals(m2));

        // m1.convertFromVector(v);
        ans = m1.clone();
        v = new SparseVector(12);
        v.setElement(1, 2);
        v.setElement(4, 3);
        v.setElement(5, 4);
        v.setElement(9, 5);
        v.setElement(11, 6);
        ans.convertFromVector(v);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 5, 2,
            3, 0, 0, 0, 4, 0, 6);
        ans = m2.clone();
        double[] d =
        {
            1, 2, 3, 4, 5, 6, 7, 8
        };
        v = new DenseVector(d);
        ans.convertFromVector(v);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 1, 5, 2, 6, 3,
            7, 4, 8);

        // m1.convertToVector()
        m1.compress();
        v = m1.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 12, 4, 0, 0, 0, 0, 1,
            0, 3, 0, 6, 2, 0);
        m2.decompress();
        v = m2.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 8, 6, 4, 0, 2, 2, 0,
            3, 0);
        v = m3.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 5, 1, 0);

        // m1.dotTimes(ans)
        // dense
        // Necessary to get a sparse matrix to the right size
        ans = m1.getSubMatrix(0, 1, 0, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 4, 0, 0, 6, 0,
            0, 3, 2);
        ans = ans.dotTimes(ds1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 16, 0, 0, 36,
            0, 0, 9, 4);
        // diagonal
        // Necessary to get a sparse matrix the right size
        ans = m2.getSubMatrix(0, 1, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 2, 4, 0);
        ans = ans.dotTimes(diag);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 2, 2, 24, 0, 0, 0);
        // sparse
        ans = m1.dotTimes(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 16, 0, 0, 36,
            0, 0, 9, 4, 0, 1, 0, 0);
        ans = m2.dotTimes(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 36, 4, 16, 0,
            0, 9, 4, 0);
        ans = m3.dotTimes(m3);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, 1, 25, 0);

        // m1.dotTimesEquals(ans);
        // dense
        // Necessary to get a sparse matrix to the right size
        ans = m1.getSubMatrix(0, 1, 0, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 4, 0, 0, 6, 0,
            0, 3, 2);
        ((SparseMatrix) ans).decompress();
        ans.dotTimesEquals(ds1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 16, 0, 0, 36,
            0, 0, 9, 4);
        ans = m1.getSubMatrix(0, 1, 0, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 4, 0, 0, 6, 0,
            0, 3, 2);
        ((SparseMatrix) ans).compress();
        ans.dotTimesEquals(ds1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 16, 0, 0, 36,
            0, 0, 9, 4);
        // diagonal
        // Necessary to get a sparse matrix the right size
        ans = m2.getSubMatrix(0, 1, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 2, 4, 0);
        ((SparseMatrix) ans).decompress();
        ans.dotTimesEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 24, 0, 0, 0);
        ans = m2.getSubMatrix(0, 1, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 2, 4, 0);
        ((SparseMatrix) ans).compress();
        ans.dotTimesEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 24, 0, 0, 0);
        // sparse
        ans = m1.clone();
        ans.dotTimesEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 16, 0, 0, 36,
            0, 0, 9, 4, 0, 1, 0, 0);
        ans = m1.getSubMatrix(0, 2, 0, 1);
        ans.dotTimesEquals(m2.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 24, 0, 0, 0,
            0, 3);
        ans = m2.getSubMatrix(0, 2, 0, 1);
        ans.dotTimesEquals(m1.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 24, 0, 0, 0,
            0, 3);
        ans = m1.clone();
        ans.setElement(0, 3, 0);
        ans.setElement(0, 2, 1);
        ans.setElement(2, 3, 1);
        Matrix m = m1.clone();
        m.setElement(1, 0, 1);
        m.setElement(1, 1, 1);
        ans.dotTimesEquals(m);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 16, 0, 0, 0,
            0, 0, 9, 4, 0, 1, 0, 0);
        ans = m2.clone();
        ans.dotTimesEquals(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 36, 4, 16, 0,
            0, 9, 4, 0);
        ans = m3.clone();
        ans.dotTimesEquals(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 1, 25, 0);

        // m1.equals(this)
        // Tested with clone above
        // m1.equals(ans, effectiveZero)
        ans = m1.clone();
        ans.setElement(0, 0, 4.0001);
        ans.setElement(1, 0, -0.0001);
        ans.setElement(1, 3, 1.999999);
        assertFalse(ans.equals(m1));
        assertFalse(m1.equals(ans));
        assertTrue(ans.equals(m1, 0.01));
        assertTrue(ans.equals(m1, 0.00011));
        assertTrue(m1.equals(ans, 0.01));
        assertTrue(m1.equals(ans, 0.00011));
        assertFalse(ans.equals(m1, 0.00001));
        assertFalse(m1.equals(ans, 0.00001));

        // m1.getColumn(columnIndex)
        v = m1.getColumn(0);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 4, 0, 0);
        v = m1.getColumn(1);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 0, 0, 1);
        v = m1.getColumn(2);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 0, 3, 0);
        v = m1.getColumn(3);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 6, 2, 0);

        // The following are just to increase my code coverage ... sigh.
        int[] oldcols = m1.getColIdxs();
        m1.decompress();
        int[] newcols = m1.getColIdxs();
        assertEquals(oldcols.length, newcols.length);
        for (int i = 0; i < oldcols.length; ++i)
        {
            assertEquals(oldcols[i], newcols[i]);
        }
        int[] oldfirs = m1.getFirstInRows();
        m1.decompress();
        int[] newfirs = m1.getFirstInRows();
        assertEquals(oldfirs.length, newfirs.length);
        for (int i = 0; i < oldfirs.length; ++i)
        {
            assertEquals(oldfirs[i], newfirs[i]);
        }
        double[] oldvals = m1.getVals();
        m1.decompress();
        double[] newvals = m1.getVals();
        assertEquals(oldvals.length, newvals.length);
        for (int i = 0; i < oldvals.length; ++i)
        {
            AssertUtil.equalToNumDigits(oldvals[i], newvals[i], 12);
        }

        // m1.getElement(rowIndex, columnIndex)
        // Tested in most of the assertEquals above
        // m1.getNumColumns();
        assertEquals(4, m1.getNumColumns());
        assertEquals(2, m2.getNumColumns());
        assertEquals(2, m3.getNumColumns());

        // m1.getNumRows();
        assertEquals(3, m1.getNumRows());
        assertEquals(4, m2.getNumRows());
        assertEquals(2, m3.getNumRows());

        // m1.getRow(rowIndex)
        m1.compress();
        v = m1.getRow(0);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 4, 0, 0, 6);
        v = m1.getRow(1);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 0, 3, 2);
        m1.decompress();
        v = m1.getRow(2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 1, 0, 0);

        // m1.getSubMatrix(minRow, maxRow, minColumn, maxColumn)
        ans = m1.getSubMatrix(1, 2, 1, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 3, 0, 3, 2, 1, 0,
            0);
        ans = m1.getSubMatrix(1, 2, 2, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 3, 2, 0, 0);
        ans = m2.getSubMatrix(1, 3, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 4, 0, 0, 3, 2,
            0);

        // m1.identity()
        ans = m1.clone();
        ans.identity();
        assertFalse(ans.equals(m1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 1, 0, 0, 0, 0,
            1, 0,
            0, 0, 0, 1, 0);
        ans = m2.clone();
        ans.identity();
        assertFalse(ans.equals(m2));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 1, 0, 0, 1, 0,
            0, 0,
            0);
        ans = m3.clone();
        ans.identity();
        assertFalse(ans.equals(m3));
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 1, 0, 0, 1);

        // m1.inverse()
        ans = m1.getSubMatrix(0, 2, 0, 2).inverse();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 3, .25, 0, 0, 0, 0, 1, 0,
            .33333333, 0);
        MatrixUtil.testMatrixEquals(ans.times(
            m1.getSubMatrix(0, 2, 0, 2)), Matrix.class, 3,
            3, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        ans = m2.getSubMatrix(0, 1, 0, 1);
        ((SparseMatrix) ans).decompress();
        ans = ans.inverse();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 2, 2, 0, .25, .5, -.75);
        MatrixUtil.testMatrixEquals(ans.times(
            m2.getSubMatrix(0, 1, 0, 1)), Matrix.class, 2,
            2, 1, 0, 0, 1);

        // m1.isSquare()
        assertFalse(m1.isSquare());
        assertFalse(m2.isSquare());
        assertTrue(m3.isSquare());

        // m1.isSymmetric()
        assertFalse(m1.isSymmetric());
        assertFalse(m2.isSymmetric());
        assertFalse(m3.isSymmetric());
        ans = m3.clone();
        ans.identity();
        assertTrue(ans.isSymmetric());
        ans.setElement(0, 0, 500);
        assertTrue(ans.isSymmetric());
        ans.setElement(0, 1, 1);
        assertFalse(ans.isSymmetric());
        ans.setElement(1, 0, 1);
        assertTrue(ans.isSymmetric());
        ans.setElement(0, 1, 1.00001);
        assertFalse(ans.isSymmetric());

        // m1.isSymmetric(effectiveZero);
        assertFalse(m1.isSymmetric(0.01));
        assertFalse(m2.isSymmetric(0.01));
        assertFalse(m3.isSymmetric(0.01));
        assertFalse(m3.isSymmetric(3.999));
        assertTrue(m3.isSymmetric(4.001));
        assertTrue(m3.isSymmetric(4));
        ans = m3.clone();
        ans.identity();
        assertTrue(ans.isSymmetric(0.01));
        ans.setElement(0, 0, 500);
        assertTrue(ans.isSymmetric(0.01));
        ans.setElement(0, 1, 1);
        assertFalse(ans.isSymmetric(0.01));
        assertFalse(ans.isSymmetric(0.99));
        assertTrue(ans.isSymmetric(1.01));
        ans.setElement(1, 0, 1);
        assertTrue(ans.isSymmetric(0.0000001));
        ans.setElement(0, 1, 1.00001);
        assertFalse(ans.isSymmetric(0.0000001));
        assertTrue(ans.isSymmetric(0.0001));

        // m1.isZero()
        assertFalse(m1.isZero());
        assertFalse(m2.isZero());
        assertFalse(m3.isZero());
        ans = new SparseMatrix(10, 10);
        assertTrue(ans.isZero());

        // m1.isZero(effectiveZero);
        assertFalse(m1.isZero(0.01));
        assertFalse(m1.isZero(5.99));
        assertTrue(m1.isZero(6.0));
        assertTrue(m1.isZero(6.01));
        assertFalse(m2.isZero(0.01));
        assertFalse(m2.isZero(5.99));
        assertTrue(m2.isZero(6.0));
        assertTrue(m2.isZero(6.01));
        assertFalse(m3.isZero(0.01));
        assertFalse(m3.isZero(4.999999));
        assertTrue(m3.isZero(5.0));
        assertTrue(m3.isZero(5.01));
        ans = new SparseMatrix(10, 10);
        assertTrue(ans.isZero(0.01));
        ans.setElement(1, 1, 0.011);
        assertFalse(ans.isZero(0.01));
        assertTrue(ans.isZero(0.011));

        // m1.iterator()
        // Tested with testMatrixEquals
        // m1.logDeterminant()
        ComplexNumber c = m1.getSubMatrix(0, 2, 0, 2).logDeterminant();
        assertEquals(Math.log(12), c.getRealPart(), 1e-10);
        assertEquals(Math.PI, c.getImaginaryPart(), 1e-10);
        c = m2.getSubMatrix(1, 2, 0, 1).logDeterminant();
        assertEquals(Math.log(12), c.getRealPart(), 1e-10);
        assertEquals(0, c.getImaginaryPart(), 1e-10);
        c = m2.getSubMatrix(0, 1, 0, 1).logDeterminant();
        assertEquals(Math.log(8), c.getRealPart(), 1e-10);
        assertEquals(Math.PI, c.getImaginaryPart(), 1e-10);

        // m1.minus(ans)
        // dense
        ans = m3.minus(ds2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, -6, -1, 4, -5);
        // diagonal
        ans = m3.minus(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -4, 1, 5, -6);
        // sparse
        ans = m1.minus(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        assertTrue(ans.isZero());
        ans = m2.minus(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 0, 0, 0, 0, 0,
            0, 0, 0);
        assertTrue(ans.isZero());
        ans = m3.minus(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 0, 0, 0);
        assertTrue(ans.isZero());

        // m1.minusEquals(ans);
        // dense
        (new SparseMatrix(0, 0)).minusEquals(new DenseMatrix(0, 0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.minusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -6, -1, 4, -5);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.minusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -6, -1, 4, -5);
        // diagonal
        (new SparseMatrix(0, 0)).minusEquals(new DiagonalMatrix(0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.minusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -4, 1, 5, -6);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.minusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -4, 1, 5, -6);
        // sparse
        (new SparseMatrix(0, 0)).minusEquals(new SparseMatrix(0, 0));
        ans = m1.clone();
        ((SparseMatrix) ans).decompress();
        m1.decompress();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        assertTrue(ans.isZero());
        ans = m1.clone();
        ((SparseMatrix) ans).compress();
        m1.compress();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        assertTrue(ans.isZero());
        ans = m2.clone();
        ans.minusEquals(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 0, 0, 0, 0, 0,
            0, 0, 0);
        assertTrue(ans.isZero());
        ans = m3.clone();
        ans.minusEquals(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 0, 0, 0);
        assertTrue(ans.isZero());

        // m1.negative()
        ans = m1.negative();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, -4, 0, 0, -6,
            0, 0, -3, -2, 0, -1, 0, 0);
        ans = m2.negative();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, -6, -2, -4, 0,
            0, -3, -2, 0);
        ans = m3.negative();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, -1, -5, 0);

        // m1.negativeEquals();
        ans = m1.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, -4, 0, 0, -6,
            0, 0, -3, -2, 0, -1, 0, 0);
        ans = m2.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, -6, -2, -4, 0,
            0, -3, -2, 0);
        ans = m3.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, -1, -5, 0);

        // m1.normFrobenius()
        assertEquals(Math.sqrt(66), m1.normFrobenius(), 1e-6);
        assertEquals(Math.sqrt(69), m2.normFrobenius(), 1e-6);
        m3.decompress();
        assertEquals(Math.sqrt(26), m3.normFrobenius(), 1e-6);

        // m1.plus(ans)
        // dense
        ans = m3.plus(ds2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 6, 3, 6, 5);
        // diagonal
        ans = m3.plus(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 4, 1, 5, 6);
        // sparse
        ans = m1.plus(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 8, 0, 0, 12,
            0, 0, 6,
            4, 0, 2, 0, 0);
        ans = m2.plus(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 12, 4, 8, 0,
            0, 6, 4,
            0);
        ans = m3.plus(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 2, 10, 0);

        // m1.plusEquals(ans)
        // dense
        (new SparseMatrix(0, 0)).plusEquals(new DenseMatrix(0, 0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.plusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 3, 6, 5);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.plusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 3, 6, 5);
        // diagonal
        (new SparseMatrix(0, 0)).plusEquals(new DiagonalMatrix(0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.plusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 4, 1, 5, 6);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.plusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 4, 1, 5, 6);
        // sparse
        (new SparseMatrix(0, 0)).plusEquals(new SparseMatrix(0, 0));
        ans = m1.clone();
        ((SparseMatrix) ans).compress();
        m1.compress();
        ans.plusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 8, 0, 0, 12,
            0, 0, 6, 4, 0, 2, 0, 0);
        ans = m1.clone();
        ((SparseMatrix) ans).decompress();
        m1.decompress();
        ans.plusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 8, 0, 0, 12,
            0, 0, 6, 4, 0, 2, 0, 0);
        ans = m1.getSubMatrix(0, 2, 0, 1);
        ans.plusEquals(m2.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 10, 2, 4, 0,
            0, 4);
        ans = m1.getSubMatrix(0, 2, 1, 2);
        ans.plusEquals(m2.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 6, 2, 4, 3, 1,
            3);
        ans = m2.clone();
        ans.plusEquals(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 12, 4, 8, 0,
            0, 6, 4, 0);
        ans = m3.clone();
        ans.plusEquals(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 2, 10, 0);
        // m1.pseudoInverse()
        // m1.pseudoInverse(effectiveZero)
        {
            // Test from the Internet
            SparseMatrix M = new SparseMatrix(4, 5);
            M.setElement(0, 0, 1);
            M.setElement(0, 4, 2);
            M.setElement(1, 2, 3);
            M.setElement(3, 1, 4);
            ans = M.pseudoInverse();
            MatrixUtil.testMatrixEquals(ans, Matrix.class, 5, 4, .2, 0, 0, 0, 0,
                0, 0, 0.25, 0, .33333333, 0, 0, 0, 0, 0, 0, 0.4, 0, 0, 0);
            ans = M.pseudoInverse(Math.sqrt(5) - 1e-10);
            MatrixUtil.testMatrixEquals(ans, Matrix.class, 5, 4, .2, 0, 0, 0, 0,
                0, 0, 0.25, 0, .33333333, 0, 0, 0, 0, 0, 0, 0.4, 0, 0, 0);
            ans = M.pseudoInverse(Math.sqrt(5) + 1e-10);
            MatrixUtil.testMatrixEquals(ans, Matrix.class, 5, 4, 0, 0, 0, 0, 0,
                0, 0, 0.25, 0, .33333333, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            // Now, for a sparse matrix that spans its space, make sure the 
            // pseudoinverse inverts it
            ans = m2.pseudoInverse();
            MatrixUtil.testMatrixEquals(ans.times(m2), Matrix.class, 2, 2, 1, 0,
                0, 1);
        }
        // m1.rank()
        assertEquals(3, m1.rank());
        assertEquals(2, m2.rank());
        assertEquals(2, m3.rank());
        ans = m3.clone();
        ans.setElement(0, 1, 0);
        assertEquals(1, ans.rank());

        // m1.rank(effectiveZero);
        assertEquals(3, m1.rank(0.1));
        assertEquals(2, m2.rank(0.1));
        assertEquals(2, m3.rank(0.1));
        ans = m3.clone();
        ans.setElement(0, 1, 0.1);
        assertEquals(1, ans.rank(0.1));
        assertEquals(2, ans.rank(0.1 - 1e-10));

        // m1.scale(scaleFactor)
        ans = m1.scale(2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 8, 0, 0, 12, 0, 0, 6,
            4, 0, 2, 0, 0);
        ans = m2.scale(2);
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, 12, 4, 8, 0, 0, 6, 4,
            0);
        ans = m3.scale(-2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, -2, -10, 0);

        // m1.scaleEquals(scaleFactor);
        ans = m1.clone();
        ans.scaleEquals(2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 8, 0, 0, 12, 0, 0, 6,
            4, 0, 2, 0, 0);
        ans = m2.clone();
        ans.scaleEquals(2);
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, 12, 4, 8, 0, 0, 6, 4,
            0);
        ans = m3.clone();
        ans.scaleEquals(-2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, -2, -10, 0);

        // m1.setColumn(columnIndex, v)
        // dense
        ans = m2.clone();
        ans.setColumn(0, v1);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, v1.getElement(0), 2,
            v1.getElement(1), 0, v1.getElement(2), 3, v1.getElement(3), 0);
        // sparse
        ans.setColumn(1, v2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, v1.getElement(0),
            v2.getElement(0), v1.getElement(1), v2.getElement(1),
            v1.getElement(2), v2.getElement(2), v1.getElement(3), v2.getElement(
                3));

        // m1.setElement(rowIndex, columnIndex, value);
        // Tested in initialization of the SparseMatrix instances
        // m1.setRow(rowIndex, v);
        // dense
        ans = m1.clone();
        ans.setRow(0, v1);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, v1.getElement(0),
            v1.getElement(1), v1.getElement(2), v1.getElement(3), 0, 0, 3, 2,
            0, 1, 0, 0);
        // sparse
        ans.setRow(2, v2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, v1.getElement(0),
            v1.getElement(1), v1.getElement(2), v1.getElement(3), 0, 0, 3, 2,
            v2.getElement(0), v2.getElement(1), v2.getElement(2),
            v2.getElement(3));

        // m1.setSubMatrix(minRow, minColumn, ans);
        ans = m1.clone();
        ans.setSubMatrix(0, 0, m2.getSubMatrix(1, 3, 0, 1));
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 4, 0, 0, 6, 0, 3, 3,
            2, 2, 0, 0, 0);
        ans.setSubMatrix(1, 2, m3);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 4, 0, 0, 6, 0, 3, 0,
            1, 2, 0, 5, 0);

        // m1.solve(matrix)
        m3.compress();
        ans = m3.solve(m2.getSubMatrix(0, 1, 0, 1));
        MatrixUtil.testMatrixEquals(ans, Matrix.class, 2, 2, .8, 0, 6, 2);
        m3.decompress();
        ans = m3.solve(m2.getSubMatrix(0, 1, 0, 1));
        MatrixUtil.testMatrixEquals(ans, Matrix.class, 2, 2, .8, 0, 6, 2);

        // m1.solve(vector)
        ans = m1.getSubMatrix(0, 2, 0, 2);
        ((SparseMatrix) ans).compress();
        v = ans.solve(v1.subVector(0, 2));
        MatrixUtil.testVectorEquals(v, Vector.class, 3, 1, 2, 1);
        ans = m1.getSubMatrix(0, 2, 0, 2);
        ((SparseMatrix) ans).decompress();
        v = ans.solve(v2.subVector(1, 3));
        MatrixUtil.testVectorEquals(v, Vector.class, 3, 0, 3, 1);

        // m1.sumOfColumns()
        v = m1.sumOfColumns();
        MatrixUtil.testVectorEquals(v, Vector.class, 3, 10, 5, 1);
        v = m2.sumOfColumns();
        MatrixUtil.testVectorEquals(v, Vector.class, 4, 8, 4, 3, 2);
        v = m3.sumOfColumns();
        MatrixUtil.testVectorEquals(v, Vector.class, 2, 1, 5);

        // m1.sumOfRows()
        v = m1.sumOfRows();
        MatrixUtil.testVectorEquals(v, Vector.class, 4, 4, 1, 3, 8);
        v = m2.sumOfRows();
        MatrixUtil.testVectorEquals(v, Vector.class, 2, 12, 5);
        v = m3.sumOfRows();
        MatrixUtil.testVectorEquals(v, Vector.class, 2, 5, 1);

        // m1.times(matrix)
        // dense
        ans = m2.times(ds1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 4, 24, 44, 18, 40,
            16, 28, 8, 24, 0, 3, 9, 6, 8, 14, 4, 12);
        m3.decompress();
        ans = m3.times(ds2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 1, 5, 30, 10);
        // diagonal
        ans = m2.times(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 24, 12, 16, 0,
            0, 18, 8, 0);
        m3.decompress();
        ans = m3.times(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 6, 20, 0);
        // sparse
        ans = m1.times(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 2, 36, 8, 4, 9, 4,
            0);
        m2.decompress();
        m3.decompress();
        ans = m2.times(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 10, 6, 0, 4,
            15, 0, 0, 2);
        ans = m3.clone();
        ans.zero();
        ans = ans.times(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 0, 0, 0);

        // m1.times(vector)
        // dense
        m1.compress();
        v = m1.times(v1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 22, 8, 3);
        m1.decompress();
        v = m1.times(v1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 22, 8, 3);
        // premult dense
        v = v1.times(m2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 38, 14);
        m2.decompress();
        v = v1.times(m2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 38, 14);
        // sparse
        m1.compress();
        v = m1.times(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 3, 18, 15, 0);
        m1.decompress();
        v = m1.times(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 3, 18, 15, 0);
        // premult sparse
        v = v2.times(m2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 2, 6, 9);
        m2.decompress();
        v = v2.times(m2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 2, 6, 9);

        // m1.trace()
        try
        {
            // Trace only defined for square matrices
            m1.trace();
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path
        }
        try
        {
            // Trace only defined for square matrices
            m2.trace();
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path
        }
        assertEquals(0, m3.trace(), 1e-6);

        // m1.transpose()
        ans = m1.transpose();
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testIsTranspose(m1, ans);
        ans = m2.transpose();
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testIsTranspose(m2, ans);
        ans = m3.transpose();
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testIsTranspose(m3, ans);

        // m1.zero()
        ans = m1.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0);
        ans = m2.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, 0, 0, 0, 0, 0, 0, 0,
            0);
        ans = m3.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, 0, 0, 0);

        // m1.getNonZeroValueIterator()
        testIter(m1.getNonZeroValueIterator(), new double[]
        {
            4, 6, 3, 2, 1
        }, new int[]
        {
            0, 0, 1, 1, 2
        }, new int[]
        {
            0, 3, 2, 3, 1
        });
        testIter(m2.getNonZeroValueIterator(), new double[]
        {
            6, 2, 4, 3, 2
        }, new int[]
        {
            0, 0, 1, 2, 3
        }, new int[]
        {
            0, 1, 0, 1, 0
        });
        testIter(m3.getNonZeroValueIterator(), new double[]
        {
            1, 5
        }, new int[]
        {
            0, 1
        }, new int[]
        {
            1, 0
        });
        testIter(m1.getNonZeroValueIterator(2), new double[]
        {
            1
        }, new int[]
        {
            2
        }, new int[]
        {
            1
        });
        testIter(m2.getNonZeroValueIterator(2), new double[]
        {
            3, 2
        }, new int[]
        {
            2, 3
        }, new int[]
        {
            1, 0
        });
        testIter(m3.getNonZeroValueIterator(0), new double[]
        {
            1, 5
        }, new int[]
        {
            0, 1
        }, new int[]
        {
            1, 0
        });
        // This catches a bug for when there are empty rows that need skipping
        SparseMatrix emptyRows = new SparseMatrix(4, 4);
        emptyRows.setElement(1, 0, 2);
        emptyRows.setElement(3, 1, 2);
        testIter(emptyRows.getNonZeroValueIterator(), new double[]
        {
            2, 2
        }, new int[]
        {
            1, 3
        }, new int[]
        {
            0, 1
        });
        testIter(emptyRows.getNonZeroValueIterator(2), new double[]
        {
            2
        }, new int[]
        {
            3
        }, new int[]
        {
            1
        });
    }

    /**
     * Simple helper that tests the values for the input iterator
     *
     * @param iter The iterator to test
     * @param vals The correct values for the iterator
     * @param rows The correct rows for the iterator
     * @param cols The correct columns for the iterator
     */
    private static void testIter(Iterator<MatrixEntry> iter,
        double[] vals,
        int[] rows,
        int[] cols)
    {
        for (int i = 0; i < vals.length; ++i)
        {
            assertTrue(iter.hasNext());
            MatrixEntry me = iter.next();
            assertEquals(vals[i], me.getValue(), 1e-6);
            assertEquals(rows[i], me.getRowIndex());
            assertEquals(cols[i], me.getColumnIndex());
        }
        assertFalse(iter.hasNext());
    }

    /**
     * Tests all matrix operations for dense matrix instances on all input
     * types. Attempts to be quite thorough testing different sizes, etc. Checks
     * the results for correctness.
     */
    @Test
    public void testDenseMatrixOps()
    {
        double[][] d1 =
        {
            {
                4, 7, 2, 6
            },
            {
                0, 1, 3, 2
            },
            {
                1, 1, 4, 2
            }
        };
        DenseMatrix m1 = new DenseMatrix(d1);
        double[][] d2 =
        {
            {
                6, 2
            },
            {
                1, 5
            },
            {
                7, 5
            },
            {
                8, 0
            }
        };
        DenseMatrix m2 = new DenseMatrix(d2);
        double[][] d3 =
        {
            {
                2, 1
            },
            {
                5, 0
            }
        };
        DenseMatrix m3 = new DenseMatrix(d3);
        double[] d4 =
        {
            4, 6
        };
        DiagonalMatrix diag = new DiagonalMatrix(d4);
        SparseMatrix s1 = new SparseMatrix(2, 4);
        s1.setElement(1, 0, 4);
        s1.setElement(1, 1, 3);
        s1.setElement(0, 2, 2);
        s1.setElement(1, 3, 1);
        SparseMatrix s2 = new SparseMatrix(2, 2);
        s2.setElement(0, 1, 4);
        double[] d5 =
        {
            4, 3, 2, 1
        };
        DenseVector v1 = new DenseVector(d5);
        SparseVector v2 = new SparseVector(4);
        v2.setElement(3, 3);
        v2.setElement(2, 3);

        Matrix ans;
        Vector v;

        // m1.assertSameDimensions(ans)
        ans = m1.clone();
        m1.assertSameDimensions(ans);
        try
        {
            m1.assertSameDimensions(m2);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path -- the above should throw some kind of exception
        }
        try
        {
            m1.assertSameDimensions(m3);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path -- the above should throw some kind of exception
        }
        try
        {
            m1.assertSameDimensions(diag);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path -- the above should throw some kind of exception
        }
        try
        {
            m1.assertSameDimensions(s1);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path -- the above should throw some kind of exception
        }
        try
        {
            m1.assertSameDimensions(s2);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path -- the above should throw some kind of exception
        }
        ans = m2.clone();
        m2.assertSameDimensions(ans);
        m3.assertSameDimensions(diag);
        m3.assertSameDimensions(s2);

        // m1.checkMultiplicationDimensions(ans)
        assertTrue(m1.checkMultiplicationDimensions(m2));
        assertFalse(m1.checkMultiplicationDimensions(m3));
        assertFalse(m1.checkMultiplicationDimensions(diag));
        assertFalse(m1.checkMultiplicationDimensions(s1));
        assertFalse(m1.checkMultiplicationDimensions(s2));
        assertFalse(m2.checkMultiplicationDimensions(m1));
        assertTrue(m2.checkMultiplicationDimensions(m3));
        assertTrue(m2.checkMultiplicationDimensions(diag));
        assertTrue(m2.checkMultiplicationDimensions(s1));
        assertTrue(m2.checkMultiplicationDimensions(s2));
        assertFalse(m3.checkMultiplicationDimensions(m1));
        assertFalse(m3.checkMultiplicationDimensions(m2));
        assertTrue(m3.checkMultiplicationDimensions(diag));
        assertTrue(m3.checkMultiplicationDimensions(s1));
        assertTrue(m3.checkMultiplicationDimensions(s2));

        // m1.checkSameDimensions(ans)
        ans = m1.clone();
        assertTrue(m1.checkSameDimensions(ans));
        assertFalse(m1.checkSameDimensions(m2));
        assertFalse(m1.checkSameDimensions(m3));
        assertFalse(m1.checkSameDimensions(diag));
        assertFalse(m1.checkSameDimensions(s1));
        assertFalse(m1.checkSameDimensions(s2));
        ans = m2.clone();
        assertTrue(m2.checkSameDimensions(ans));
        assertTrue(m3.checkSameDimensions(diag));
        assertTrue(m3.checkSameDimensions(s2));

        // m1.clone()
        ans = m1.clone();
        assertTrue(ans instanceof DenseMatrix);
        assertTrue(ans.equals(m1));
        ans = m2.clone();
        assertTrue(ans instanceof DenseMatrix);
        assertTrue(ans.equals(m2));

        // m1.convertFromVector(v);
        double[] d =
        {
            0, 1, 2, 3, 7, 6, 5, 4
        };
        v = new DenseVector(d);
        ans = m2.clone();
        ans.convertFromVector(v);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 0, 7, 1, 6, 2,
            5, 3, 4);
        v = new SparseVector(4);
        v.setElement(2, 1);
        ans = m3.clone();
        ans.convertFromVector(v);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 0, 1, 0, 0);

        // m1.convertToVector()
        v = m1.convertToVector();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 12, 4, 0, 1, 7, 1, 1,
            2, 3, 4, 6, 2, 2);
        v = m2.convertToVector();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 8, 6, 1, 7, 8, 2, 5, 5,
            0);
        v = m3.convertToVector();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 4, 2, 5, 1, 0);

        // m1.dotTimes(ans)
        ans = m1.dotTimes(m1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 16, 49, 4, 36,
            0, 1, 9, 4, 1, 1, 16, 4);
        ans = m2.dotTimes(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 36, 4, 1, 25,
            49, 25, 64, 0);
        ans = m3.dotTimes(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 4, 1, 25, 0);
        ans = m3.dotTimes(diag);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 2, 2, 8, 0, 0, 0);
        ans = m3.dotTimes(s2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 4, 0, 0);

        // m1.dotTimesEquals(ans);
        ans = m1.clone();
        ans.dotTimesEquals(m1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 16, 49, 4, 36,
            0, 1, 9, 4, 1, 1, 16, 4);
        ans = m2.clone();
        ans.dotTimesEquals(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 36, 4, 1, 25,
            49, 25, 64, 0);
        ans = m3.clone();
        ans.dotTimesEquals(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 4, 1, 25, 0);
        ans = m3.clone();
        ans.dotTimesEquals(diag);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 8, 0, 0, 0);
        ans = m3.clone();
        ans.dotTimesEquals(s2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 0, 4, 0, 0);
        ans = m3.clone();
        Matrix m = s2.clone();
        m.setElement(0, 1, 0);
        ans.dotTimesEquals(m);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 0, 0, 0, 0);

        // m1.equals(this)
        // Tested with clone above
        // m1.equals(ans, effectiveZero)
        ans = m1.clone();
        ans.setElement(1, 0, 0.001);
        ans.setElement(2, 2, 3.999);
        assertFalse(m1.equals(ans, 0));
        assertTrue(m1.equals(ans, 0.01));
        assertTrue(m1.equals(ans, 0.001));
        assertFalse(m1.equals(ans, 0.0001));

        // m1.getColumn(columnIndex)
        v = m1.getColumn(0);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 4, 0, 1);
        v = m1.getColumn(3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 6, 2, 2);
        v = m2.getColumn(1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 4, 2, 5, 5, 0);
        v = m3.getColumn(0);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 2, 5);

        // m1.getElement(rowIndex, columnIndex)
        // Tested in most of the above assertEquals
        // m1.getNumColumns();
        assertEquals(m1.getNumColumns(), 4);
        assertEquals(m2.getNumColumns(), 2);

        // m1.getNumRows();
        assertEquals(m1.getNumRows(), 3);
        assertEquals(m2.getNumRows(), 4);

        // m1.getRow(rowIndex)
        v = m1.getRow(0);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 4, 4, 7, 2, 6);
        v = m1.getRow(2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 4, 1, 1, 4, 2);
        v = m2.getRow(1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 1, 5);
        v = m3.getRow(0);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 2, 1);

        // m1.getSubMatrix(minRow, maxRow, minColumn, maxColumn)
        ans = m1.getSubMatrix(1, 2, 2, 3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 3, 2, 4, 2);
        ans = m2.getSubMatrix(0, 2, 0, 1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 2, 6, 2, 1, 5, 7,
            5);

        // m1.identity()
        ans = m1.clone();
        assertTrue(m1.equals(ans));
        ans.identity();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 1, 0, 0, 0, 0,
            1, 0, 0, 0,
            0, 1, 0);
        assertFalse(m1.equals(ans));
        ans = m2.clone();
        assertTrue(m2.equals(ans));
        ans.identity();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 1, 0, 0, 1, 0,
            0, 0, 0);

        // m1.inverse()
        ans = m3.inverse();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 2, 2, 0, .2, 1, -.4);

        // m1.isSquare()
        assertFalse(m1.isSquare());
        assertFalse(m2.isSquare());
        assertTrue(m3.isSquare());

        // m1.isSymmetric()
        assertFalse(m1.isSymmetric());
        assertFalse(m2.isSymmetric());
        assertFalse(m3.isSymmetric());
        ans = m3.clone();
        ans.identity();
        assertTrue(ans.isSymmetric());
        ans.zero();
        assertTrue(ans.isSymmetric());
        ans = m2.clone();
        ans.identity();
        assertFalse(ans.isSymmetric());

        // m1.isSymmetric(effectiveZero);
        assertFalse(m1.isSymmetric(10));
        assertFalse(m2.isSymmetric(10));
        assertTrue(m3.isSymmetric(10));
        assertTrue(m3.isSymmetric(4));
        assertFalse(m3.isSymmetric(3.9));

        // m1.isZero()
        assertFalse(m1.isZero());
        assertFalse(m2.isZero());
        assertFalse(m3.isZero());
        ans = m2.clone();
        ans.zero();
        assertTrue(ans.isZero());

        // m1.isZero(effectiveZero);
        assertFalse(m1.isZero(1));
        assertTrue(m1.isZero(7));
        assertFalse(m2.isZero(1));
        assertTrue(m2.isZero(8));
        assertFalse(m3.isZero(1));
        assertTrue(m3.isZero(5));

        // m1.iterator()
        // Most of the iterator is tested with testMatrixEquals, but need to 
        // test the non-standard methods.
        ans = m1.clone();
        Iterator<MatrixEntry> iter = ans.iterator();
        try
        {
            iter.remove();
            assertFalse(true);
        }
        catch (UnsupportedOperationException e)
        {
            // This is the correct path
        }
        MatrixEntry me = iter.next();
        me.setValue(2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, 2, 7, 2, 6, 0, 1, 3,
            2, 1, 1, 4, 2);
        me.setRowIndex(1);
        me.setColumnIndex(2);
        AssertUtil.equalToNumDigits(3, me.getValue(), 6);

        // m1.logDeterminant()
        ComplexNumber c = m1.getSubMatrix(0, 2, 0, 2).logDeterminant();
        assertEquals(Math.log(16 + 21 - 2 - 12), c.getRealPart(), 1e-10);
        assertEquals(0, c.getImaginaryPart(), 1e-10);
        c = m2.getSubMatrix(0, 1, 0, 1).logDeterminant();
        assertEquals(Math.log(28), c.getRealPart(), 1e-10);
        assertEquals(0, c.getImaginaryPart(), 1e-10);
        c = m3.logDeterminant();
        assertEquals(Math.log(5), c.getRealPart(), 1e-10);
        assertEquals(Math.PI, c.getImaginaryPart(), 1e-10);

        // m1.luDecompose()
        DenseMatrix.LU lu = m1.luDecompose();
        MatrixUtil.testMatrixEquals(lu.L, DenseMatrix.class, 3, 3, 1, 0, 0, 0, 1,
            0, .25, -.75, 1);
        MatrixUtil.testMatrixEquals(lu.U, DenseMatrix.class, 3, 4, 4, 7, 2, 6, 0,
            1, 3, 2, 0, 0, 5.75, 2);
        assertEquals(lu.P.size(), 3);
        assertEquals(lu.P.get(0), 0, 1e-10);
        assertEquals(lu.P.get(1), 1, 1e-10);
        assertEquals(lu.P.get(2), 2, 1e-10);
        MatrixUtil.testMatrixEquals(lu.getPivotMatrix(), SparseMatrix.class, 3,
            3, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        lu = m2.luDecompose();
        MatrixUtil.testMatrixEquals(lu.L, DenseMatrix.class, 4, 2, 1, 0, .125, 1,
            .875, 1, .75, .4);
        MatrixUtil.testMatrixEquals(lu.U, DenseMatrix.class, 2, 2, 8, 0, 0, 5);
        assertEquals(lu.P.size(), 2);
        assertEquals(lu.P.get(0), 3, 1e-10);
        assertEquals(lu.P.get(1), 1, 1e-10);
        MatrixUtil.testMatrixEquals(lu.getPivotMatrix(), SparseMatrix.class, 4,
            4, 0, 0, 0,
            1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0);
        lu = m3.luDecompose();
        MatrixUtil.testMatrixEquals(lu.L, DenseMatrix.class, 2, 2, 1, 0, .4, 1);
        MatrixUtil.testMatrixEquals(lu.U, DenseMatrix.class, 2, 2, 5, 0, 0, 1);
        assertEquals(lu.P.size(), 2);
        assertEquals(lu.P.get(0), 1, 1e-10);
        assertEquals(lu.P.get(1), 1, 1e-10);
        MatrixUtil.testMatrixEquals(lu.getPivotMatrix(), SparseMatrix.class, 2,
            2, 0, 1, 1, 0);

        // m1.minus(ans)
        // dense
        ans = m3.minus(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 0, 0, 0, 0);
        ans = m1.minus(m1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        ans = m2.minus(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 0, 0, 0, 0, 0,
            0, 0, 0);
        // diagonal
        ans = m3.minus(diag);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, -2, 1, 5, -6);
        // sparse
        ans = m3.minus(s2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, -3, 5, 0);

        // m1.minusEquals(ans);
        // dense
        ans = m3.clone();
        ans.minusEquals(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 0, 0, 0, 0);
        ans = m1.clone();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        ans = m2.clone();
        ans.minusEquals(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 0, 0, 0, 0, 0,
            0, 0, 0);
        // diagonal
        ans = m3.clone();
        ans.minusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, -2, 1, 5, -6);
        // sparse
        ans = m3.clone();
        m = s2.clone();
        m.setElement(1, 0, 1);
        ((SparseMatrix) m).decompress();
        ans.minusEquals(m);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, -3, 4, 0);
        ans = m3.clone();
        ans.minusEquals(m);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, -3, 4, 0);

        // m1.negative()
        ans = m1.negative();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, -4, -7, -2, -6,
            0, -1, -3, -2, -1, -1, -4, -2);
        ans = m2.negative();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, -6, -2, -1, -5,
            -7, -5, -8, 0);
        ans = m3.negative();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, -2, -1, -5, 0);

        // m1.negativeEquals();
        ans = m1.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, -4, -7, -2, -6,
            0, -1, -3, -2, -1, -1, -4, -2);
        ans = m2.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, -6, -2, -1, -5,
            -7, -5, -8, 0);
        ans = m3.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, -2, -1, -5, 0);

        // m1.normFrobenius()
        AssertUtil.equalToNumDigits(Math.sqrt(141), m1.normFrobenius(), 6);
        AssertUtil.equalToNumDigits(Math.sqrt(204), m2.normFrobenius(), 6);
        AssertUtil.equalToNumDigits(Math.sqrt(30), m3.normFrobenius(), 6);

        // m1.plus(ans)
        // dense
        ans = m3.plus(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 4, 2, 10, 0);
        ans = m1.plus(m1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 8, 14, 4, 12,
            0, 2, 6, 4, 2, 2, 8, 4);
        ans = m2.plus(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 12, 4, 2, 10,
            14, 10, 16, 0);
        // diagonal
        ans = m3.plus(diag);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 6, 1, 5, 6);
        // sparse
        ans = m3.plus(s2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, 5, 5, 0);

        // m1.plusEquals(ans)
        // dense
        ans = m3.clone();
        ans.plusEquals(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 4, 2, 10, 0);
        ans = m1.clone();
        ans.plusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 4, 8, 14, 4, 12,
            0, 2, 6, 4, 2, 2, 8, 4);
        ans = m2.clone();
        ans.plusEquals(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 12, 4, 2, 10,
            14, 10, 16, 0);
        // diagonal
        ans = m3.clone();
        ans.plusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 6, 1, 5, 6);
        // sparse
        ans = m3.clone();
        ans.plusEquals(s2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, 5, 5, 0);
        ans = m3.clone();
        m = s2.clone();
        m.setElement(1, 0, 1);
        ((SparseMatrix) m).decompress();
        ans.plusEquals(m);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, 5, 6, 0);
        ans = m3.clone();
        ans.plusEquals(m);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 2, 5, 6, 0);
        // m1.pseudoInverse()
        // m1.pseudoInverse(effectiveZero)
        {
            // Test from the Internet
            DenseMatrix M = new DenseMatrix(4, 5);
            M.setElement(0, 0, 1);
            M.setElement(0, 4, 2);
            M.setElement(1, 2, 3);
            M.setElement(3, 1, 4);
            ans = M.pseudoInverse();
            MatrixUtil.testMatrixEquals(ans,
                Matrix.class, 5, 4, .2, 0, 0, 0, 0, 0, 0, 0.25,
                0, .33333333, 0, 0, 0, 0, 0, 0, 0.4, 0, 0, 0);
            ans = M.pseudoInverse(Math.sqrt(5) - 1e-10);
            MatrixUtil.testMatrixEquals(ans,
                Matrix.class, 5, 4, .2, 0, 0, 0, 0, 0, 0, 0.25,
                0, .33333333, 0, 0, 0, 0, 0, 0, 0.4, 0, 0, 0);
            ans = M.pseudoInverse(Math.sqrt(5) + 1e-10);
            MatrixUtil.testMatrixEquals(ans,
                Matrix.class, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0.25,
                0, .33333333, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            // Now, for a dense matrix that spans its space, make sure the 
            // pseudoinverse inverts it
            ans = m2.pseudoInverse();
            MatrixUtil.testMatrixEquals(ans.times(
                m2), DenseMatrix.class, 2, 2, 1, 0, 0, 1);
        }
        // m1.qrDecompose()
        // First test a known case from the internet
        {
            double[][] arr =
            {
                {
                    1, -1, 4
                },
                {
                    1, 4, -2
                },
                {
                    1, 4, 2
                },
                {
                    1, -1, 0
                }
            };
            DenseMatrix A = new DenseMatrix(arr);
            DenseMatrix.QR qr = A.qrDecompose();
            MatrixUtil.testMatrixEquals(qr.Q,
                DenseMatrix.class, 4, 4, -.5, .5, -.5, -.5,
                -.5, -.5, .5, -.5, -.5, -.5, -.5, .5, -.5, .5, .5, .5);
            MatrixUtil.testMatrixEquals(qr.R,
                DenseMatrix.class, 4, 3, -2, -3, -2, 0, -5, 2,
                0, 0, -4, 0, 0, 0);
            // Make sure they actually do factor A
            assertTrue(A.equals(qr.Q.times(qr.R), 1e-10));
        }
        // Now just make sure all the decompositions multiply back to their original
        DenseMatrix.QR qr = m1.qrDecompose();
        assertTrue(m1.equals(qr.Q.times(qr.R), 1e-10));
        qr = m2.qrDecompose();
        assertTrue(m2.equals(qr.Q.times(qr.R), 1e-10));
        qr = m3.qrDecompose();
        assertTrue(m3.equals(qr.Q.times(qr.R), 1e-10));

        // m1.rank()
        assertEquals(3, m1.rank());
        assertEquals(2, m2.rank());
        assertEquals(2, m3.rank());

        // m1.rank(effectiveZero);
        ans = m3.clone();
        ans.setElement(1, 1, 2.5);
        assertEquals(1, ans.rank(1e-13));
        assertEquals(2, ans.rank(1e-16));
        ans.setElement(1, 1, 2.5001);
        assertEquals(1, ans.rank(1e-4));
        assertEquals(2, ans.rank(1e-5));

        // m1.scale(scaleFactor)
        ans = m1.scale(-2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, -8, -14, -4, -12, 0,
            -2, -6, -4, -2, -2, -8, -4);

        // m1.scaleEquals(scaleFactor);
        ans = m1.clone();
        ans.scaleEquals(-2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, -8, -14, -4, -12, 0,
            -2, -6, -4, -2, -2, -8, -4);

        // m1.setColumn(columnIndex, v)
        ans = m2.clone();
        ans.setColumn(0, v1);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, v1.getElement(0), 2,
            v1.getElement(1), 5, v1.getElement(2), 5, v1.getElement(3), 0);
        ans.setColumn(1, v2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, v1.getElement(0),
            v2.getElement(0), v1.getElement(1), v2.getElement(1),
            v1.getElement(
                2), v2.getElement(2), v1.getElement(3), v2.getElement(3));

        // m1.setElement(rowIndex, columnIndex, value);
        ans = m1.clone();
        ans.setElement(0, 0, 9);
        ans.setElement(1, 1, 9);
        ans.setElement(2, 2, 9);
        ans.setElement(0, 1, 9);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, 9, 9, 2, 6, 0, 9, 3,
            2, 1, 1, 9, 2);

        // m1.setRow(rowIndex, v);
        ans = m1.clone();
        ans.setRow(0, v1);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, v1.getElement(0),
            v1.getElement(1), v1.getElement(2), v1.getElement(3), 0, 1, 3, 2,
            1, 1, 4, 2);
        ans.setRow(2, v2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, v1.getElement(0),
            v1.getElement(1), v1.getElement(2), v1.getElement(3), 0, 1, 3, 2,
            v2.getElement(0), v2.getElement(1), v2.getElement(2),
            v2.getElement(
                3));

        // m1.setSubMatrix(minRow, minColumn, ans);
        ans = m1.clone();
        ans.setSubMatrix(0, 0, m3);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, 2, 1, 2, 6, 5, 0, 3,
            2, 1, 1, 4, 2);
        ans.setSubMatrix(1, 2, m3);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, 2, 1, 2, 6, 5, 0, 2,
            1, 1, 1, 5, 0);
        ans.setSubMatrix(1, 0, s1);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, 2, 1, 2, 6, 0, 0, 2,
            0, 4, 3, 0, 1);
        ans = m2.clone();
        ans.setSubMatrix(1, 0, m3);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 6, 2, 2, 1, 5, 0, 8,
            0);
        ans.setSubMatrix(2, 0, m3);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 6, 2, 2, 1, 2, 1, 5,
            0);
        ans.setSubMatrix(0, 0, diag);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 4, 0, 0, 6, 2, 1, 5,
            0);

        // m1.solve(matrix)
        // Just make sure that the identity is returned when you solve yourself
        ans = m3.solve(m3);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 2, 2, 1, 0, 0, 1);
        ans = m1.getSubMatrix(0, 2, 0, 2).solve(m1.getSubMatrix(0, 2, 0, 2));
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 3, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        // And make sure that your inverse is returned when you solve to the identity
        Matrix I = new DiagonalMatrix(3);
        I.identity();
        ans = m3.solve(I.getSubMatrix(0, 1, 0, 1));
        assertTrue(m3.times(ans).equals(I.getSubMatrix(0, 1, 0, 1), 1e-10));
        ans = m1.getSubMatrix(0, 2, 0, 2).solve(I);
        assertTrue(m1.getSubMatrix(0, 2, 0, 2).times(ans).equals(I, 1e-10));

        // m1.solve(vector)
        v = m3.solve(v1.subVector(1, 2));
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 2, .4, 2.2);
        v = m3.solve(v2.subVector(2, 3));
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 2, .6, 1.8);

        // m1.sumOfColumns()
        v = m1.sumOfColumns();
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 3, 19, 6, 8);
        v = m2.sumOfColumns();
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 8, 6, 12, 8);
        v = m3.sumOfColumns();
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 2, 3, 5);

        // m1.sumOfRows()
        v = m1.sumOfRows();
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 4, 5, 9, 9, 10);
        v = m2.sumOfRows();
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 2, 22, 12);
        v = m3.sumOfRows();
        MatrixUtil.testVectorEquals(v,
            DenseVector.class, 2, 7, 1);
        // m1.svd();
        // A test from wikipedia
        {
            DenseMatrix M = new DenseMatrix(4, 5);
            M.setElement(0, 0, 1);
            M.setElement(0, 4, 2);
            M.setElement(1, 2, 3);
            M.setElement(3, 1, 4);
            DenseMatrix.SVD svd = M.svdDecompose();
            MatrixUtil.testMatrixEquals(svd.Sigma,
                SparseMatrix.class, 4, 5, 4, 0, 0, 0, 0,
                0, 3, 0, 0, 0, 0, 0, Math.sqrt(5), 0, 0, 0, 0, 0, 0, 0);
            MatrixUtil.testMatrixEquals(svd.U,
                DenseMatrix.class, 4, 4, 0, 0, 1, 0, 0, 1, 0,
                0, 0, 0, 0, -1, 1, 0, 0, 0);
            MatrixUtil.testMatrixEquals(svd.V,
                DenseMatrix.class, 5, 5, 0, 0,
                Math.sqrt(0.2), 0, -Math.sqrt(0.8), 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
                0, 0, 0, 1, 0, 0, 0, Math.sqrt(0.8), 0, Math.sqrt(0.2));
        }
        DenseMatrix.SVD svd = m1.svdDecompose();
        // Make sure that U and V are orthonormal
        MatrixUtil.testMatrixEquals(svd.U.times(
            svd.U.transpose()), DenseMatrix.class, 3, 3,
            1, 0, 0, 0, 1, 0, 0, 0, 1);
        MatrixUtil.testMatrixEquals(svd.V.times(
            svd.V.transpose()), DenseMatrix.class, 4, 4,
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        // Make sure sigma is diagonal
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 4; ++j)
            {
                if (i == j)
                {
                    assertTrue(svd.Sigma.getElement(i, j) != 0);
                }
                else
                {
                    AssertUtil.equalToNumDigits("At " + i + ", " + j + ": ", 0,
                        svd.Sigma.getElement(i, j), 6);
                }
            }
        }
        // Test that they multiply back to the original (AKA it's a decomposition)
        assertTrue((svd.U.times(svd.Sigma)).times(svd.V.transpose()).equals(m1,
            1e-10));
        svd = m2.svdDecompose();
        // Make sure that U and V are orthonormal
        MatrixUtil.testMatrixEquals(svd.U.times(
            svd.U.transpose()), DenseMatrix.class, 4, 4,
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        MatrixUtil.testMatrixEquals(svd.V.times(
            svd.V.transpose()), DenseMatrix.class, 2, 2,
            1, 0, 0, 1, 0, 0, 1);
        // Make sure sigma is diagonal
        for (int i = 0; i < 4; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                if (i == j)
                {
                    assertTrue(svd.Sigma.getElement(i, j) != 0);
                }
                else
                {
                    AssertUtil.equalToNumDigits("At " + i + ", " + j + ": ", 0,
                        svd.Sigma.getElement(i, j), 6);
                }
            }
        }
        // Test that they multiply back to the original (AKA it's a decomposition)
        assertTrue((svd.U.times(svd.Sigma)).times(svd.V.transpose()).equals(m2,
            1e-10));
        svd = m3.svdDecompose();
        // Make sure that U and V are orthonormal
        MatrixUtil.testMatrixEquals(svd.U.times(
            svd.U.transpose()), DenseMatrix.class, 2, 2,
            1, 0, 0, 1, 0, 0, 1);
        MatrixUtil.testMatrixEquals(svd.V.times(
            svd.V.transpose()), DenseMatrix.class, 2, 2,
            1, 0, 0, 1, 0, 0, 1);
        // Make sure sigma is diagonal
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                if (i == j)
                {
                    assertTrue(svd.Sigma.getElement(i, j) != 0);
                }
                else
                {
                    AssertUtil.equalToNumDigits("At " + i + ", " + j + ": ", 0,
                        svd.Sigma.getElement(i, j), 6);
                }
            }
        }
        // Test that they multiply back to the original (AKA it's a decomposition)
        assertTrue((svd.U.times(svd.Sigma)).times(svd.V.transpose()).equals(m3,
            1e-10));

        // m1.times(matrix)
        // dense
        ans = m1.times(m2);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 2, 93, 53, 38, 20, 51,
            27);
        // diagonal
        ans = m2.times(diag);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 24, 12, 4, 30, 28, 30,
            32, 0);
        ans = m3.times(diag);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 2, 2, 8, 6, 20, 0);
        // sparse
        ans = m2.times(s1);
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 4, 8, 6, 12, 2, 20, 15,
            2, 5, 20, 15, 14, 5, 0, 0, 16, 0);
        ans = m3.times(s2);
        assertTrue(ans instanceof DenseMatrix);
        assertEquals(ans.getNumRows(), 2);
        assertEquals(ans.getNumColumns(), 2);
        AssertUtil.equalToNumDigits(ans.getElement(0, 0), 0, 6);
        AssertUtil.equalToNumDigits(ans.getElement(0, 1), 8, 6);
        AssertUtil.equalToNumDigits(ans.getElement(1, 0), 0, 6);
        AssertUtil.equalToNumDigits(ans.getElement(1, 1), 20, 6);

        // m1.times(vector)
        // dense
        v = m1.times(v1);
        assertTrue(v instanceof DenseVector);
        assertEquals(3, v.getDimensionality());
        AssertUtil.equalToNumDigits(v.getElement(0), 47, 6);
        AssertUtil.equalToNumDigits(v.getElement(1), 11, 6);
        AssertUtil.equalToNumDigits(v.getElement(2), 17, 6);
        // dense premult
        v = v1.times(m2);
        assertTrue(v instanceof DenseVector);
        assertEquals(2, v.getDimensionality());
        AssertUtil.equalToNumDigits(v.getElement(0), 49, 6);
        AssertUtil.equalToNumDigits(v.getElement(1), 33, 6);
        // sparse
        v = m1.times(v2);
        assertTrue(v instanceof DenseVector);
        assertEquals(3, v.getDimensionality());
        AssertUtil.equalToNumDigits(v.getElement(0), 24, 6);
        AssertUtil.equalToNumDigits(v.getElement(1), 15, 6);
        AssertUtil.equalToNumDigits(v.getElement(2), 18, 6);
        // sparse premult
        v = v2.times(m2);
        assertTrue(v instanceof DenseVector);
        assertEquals(2, v.getDimensionality());
        AssertUtil.equalToNumDigits(v.getElement(0), 45, 6);
        AssertUtil.equalToNumDigits(v.getElement(1), 15, 6);

        // m1.trace()
        try
        {
            m1.trace();
            assertFalse(true);
        }
        catch (Throwable t)
        {
            // Correct path
        }
        try
        {
            m2.trace();
            assertFalse(true);
        }
        catch (Throwable t)
        {
            // Correct path
        }
        assertEquals(2, m3.trace(), 1e-6);

        // m1.transpose()
        ans = m1.transpose();
        assertTrue(ans instanceof DenseMatrix);
        MatrixUtil.testIsTranspose(m1, ans);
        ans = m2.transpose();
        assertTrue(ans instanceof DenseMatrix);
        MatrixUtil.testIsTranspose(m2, ans);
        ans = m3.transpose();
        assertTrue(ans instanceof DenseMatrix);
        MatrixUtil.testIsTranspose(m3, ans);

        // m1.zero()
        ans = m1.clone();
        assertEquals(ans, m1);
        assertFalse(ans.isZero());
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 4, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0);
        ans = m2.clone();
        assertEquals(ans, m2);
        assertFalse(ans.isZero());
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 4, 2, 0, 0, 0, 0, 0, 0, 0,
            0);
        ans = m3.clone();
        assertEquals(ans, m3);
        assertFalse(ans.isZero());
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 2, 2, 0, 0, 0, 0);
    }

    /**
     * Tests all matrix operations for parallel sparse matrix instances on all
     * input types. Attempts to be quite thorough testing different sizes, etc.
     * Checks the results for correctness.
     */
    @Test
    public void testParallelSparseMatrixOps()
    {
        ParallelSparseMatrix m1 = new ParallelSparseMatrix(3, 4, 2);
        m1.setElement(0, 0, 4);
        m1.setElement(0, 3, 6);
        m1.setElement(1, 2, 3);
        m1.setElement(1, 3, 2);
        m1.setElement(2, 1, 1);
        ParallelSparseMatrix m2 = new ParallelSparseMatrix(4, 2, 2);
        m2.setElement(0, 0, 6);
        m2.setElement(0, 1, 2);
        m2.setElement(1, 0, 4);
        m2.setElement(2, 1, 3);
        m2.setElement(3, 0, 2);
        ParallelSparseMatrix m3 = new ParallelSparseMatrix(2, 2, 2);
        m3.setElement(0, 1, 1);
        m3.setElement(1, 0, 5);
        double[] d1 =
        {
            4, 6
        };
        DiagonalMatrix diag = new DiagonalMatrix(d1);
        double[][] d2 =
        {
            {
                4, 7, 2, 6
            },
            {
                0, 1, 3, 2
            }
        };
        DenseMatrix ds1 = new DenseMatrix(d2);
        double[][] d3 =
        {
            {
                6, 2
            },
            {
                1, 5
            }
        };
        DenseMatrix ds2 = new DenseMatrix(d3);
        double[] d5 =
        {
            4, 3, 2, 1
        };
        DenseVector v1 = new DenseVector(d5);
        SparseVector v2 = new SparseVector(4);
        v2.setElement(3, 3);
        v2.setElement(2, 3);

        Matrix ans;
        Vector v;

        // m1.assertSameDimensions(ans)
        m1.assertSameDimensions(new SparseMatrix(3, 4));
        m1.assertSameDimensions(new DenseMatrix(3, 4));
        m3.assertSameDimensions(diag);
        try
        {
            m1.assertSameDimensions(m2);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path
        }
        try
        {
            m1.assertSameDimensions(diag);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path
        }
        try
        {
            m1.assertSameDimensions(ds1);
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // Correct path
        }

        // m1.checkMultiplicationDimensions(ans)
        assertTrue(m1.checkMultiplicationDimensions(m2));
        assertFalse(m1.checkMultiplicationDimensions(m3));
        assertFalse(m1.checkMultiplicationDimensions(diag));
        assertFalse(m1.checkMultiplicationDimensions(ds1));
        assertFalse(m1.checkMultiplicationDimensions(ds2));
        assertFalse(m2.checkMultiplicationDimensions(m1));
        assertTrue(m2.checkMultiplicationDimensions(m3));
        assertTrue(m2.checkMultiplicationDimensions(diag));
        assertTrue(m2.checkMultiplicationDimensions(ds1));
        assertTrue(m2.checkMultiplicationDimensions(ds2));
        assertFalse(m3.checkMultiplicationDimensions(m1));
        assertFalse(m3.checkMultiplicationDimensions(m2));
        assertTrue(m3.checkMultiplicationDimensions(diag));
        assertTrue(m3.checkMultiplicationDimensions(ds1));
        assertTrue(m3.checkMultiplicationDimensions(ds2));

        // m1.checkSameDimensions(ans)
        assertFalse(m1.checkSameDimensions(m2));
        assertFalse(m1.checkSameDimensions(m3));
        assertFalse(m1.checkSameDimensions(diag));
        assertFalse(m1.checkSameDimensions(ds1));
        assertFalse(m1.checkSameDimensions(ds2));
        assertFalse(m2.checkSameDimensions(m1));
        assertFalse(m2.checkSameDimensions(m3));
        assertFalse(m2.checkSameDimensions(diag));
        assertFalse(m2.checkSameDimensions(ds1));
        assertFalse(m2.checkSameDimensions(ds2));
        assertFalse(m3.checkSameDimensions(m1));
        assertFalse(m3.checkSameDimensions(m2));
        assertTrue(m3.checkSameDimensions(diag));
        assertFalse(m3.checkSameDimensions(ds1));
        assertTrue(m3.checkSameDimensions(ds2));

        // m1.clone()
        ans = m1.clone();
        assertTrue(ans instanceof SparseMatrix);
        assertTrue(ans.equals(m1));
        ans = m2.clone();
        assertTrue(ans instanceof SparseMatrix);
        assertTrue(ans.equals(m2));

        // m1.convertFromVector(v);
        ans = m1.clone();
        v = new SparseVector(12);
        v.setElement(1, 2);
        v.setElement(4, 3);
        v.setElement(5, 4);
        v.setElement(9, 5);
        v.setElement(11, 6);
        ans.convertFromVector(v);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 5, 2,
            3, 0, 0, 0, 4, 0, 6);
        ans = m2.clone();
        double[] d =
        {
            1, 2, 3, 4, 5, 6, 7, 8
        };
        v = new DenseVector(d);
        ans.convertFromVector(v);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 1, 5, 2, 6, 3,
            7, 4, 8);

        // m1.convertToVector()
        m1.compress();
        v = m1.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 12, 4, 0, 0, 0, 0, 1,
            0, 3, 0, 6, 2, 0);
        m2.decompress();
        v = m2.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 8, 6, 4, 0, 2, 2, 0,
            3, 0);
        v = m3.convertToVector();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 5, 1, 0);

        // m1.dotTimes(ans)
        // dense
        // Necessary to get a sparse matrix to the right size
        ans = m1.getSubMatrix(0, 1, 0, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 4, 0, 0, 6, 0,
            0, 3, 2);
        ans = ans.dotTimes(ds1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 16, 0, 0, 36,
            0, 0, 9, 4);
        // diagonal
        // Necessary to get a sparse matrix the right size
        ans = m2.getSubMatrix(0, 1, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 2, 4, 0);
        ans = ans.dotTimes(diag);
        MatrixUtil.testMatrixEquals(ans, DiagonalMatrix.class, 2, 2, 24, 0, 0, 0);
        // sparse
        ans = m1.dotTimes(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 16, 0, 0, 36,
            0, 0, 9, 4, 0, 1, 0, 0);
        ans = m2.dotTimes(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 36, 4, 16, 0,
            0, 9, 4, 0);
        ans = m3.dotTimes(m3);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, 1, 25, 0);

        // m1.dotTimesEquals(ans);
        // dense
        // Necessary to get a sparse matrix to the right size
        ans = m1.getSubMatrix(0, 1, 0, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 4, 0, 0, 6, 0,
            0, 3, 2);
        ((SparseMatrix) ans).decompress();
        ans.dotTimesEquals(ds1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 16, 0, 0, 36,
            0, 0, 9, 4);
        ans = m1.getSubMatrix(0, 1, 0, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 4, 0, 0, 6, 0,
            0, 3, 2);
        ((SparseMatrix) ans).compress();
        ans.dotTimesEquals(ds1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 4, 16, 0, 0, 36,
            0, 0, 9, 4);
        // diagonal
        // Necessary to get a sparse matrix the right size
        ans = m2.getSubMatrix(0, 1, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 2, 4, 0);
        ((SparseMatrix) ans).decompress();
        ans.dotTimesEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 24, 0, 0, 0);
        ans = m2.getSubMatrix(0, 1, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 2, 4, 0);
        ((SparseMatrix) ans).compress();
        ans.dotTimesEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 24, 0, 0, 0);
        // sparse
        ans = m1.clone();
        ans.dotTimesEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 16, 0, 0, 36,
            0, 0, 9, 4, 0, 1, 0, 0);
        ans = m1.getSubMatrix(0, 2, 0, 1);
        ans.dotTimesEquals(m2.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 24, 0, 0, 0,
            0, 3);
        ans = m2.getSubMatrix(0, 2, 0, 1);
        ans.dotTimesEquals(m1.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 24, 0, 0, 0,
            0, 3);
        ans = m1.clone();
        ans.setElement(0, 3, 0);
        ans.setElement(0, 2, 1);
        ans.setElement(2, 3, 1);
        Matrix m = m1.clone();
        m.setElement(1, 0, 1);
        m.setElement(1, 1, 1);
        ans.dotTimesEquals(m);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 16, 0, 0, 0,
            0, 0, 9, 4, 0, 1, 0, 0);
        ans = m2.clone();
        ans.dotTimesEquals(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 36, 4, 16, 0,
            0, 9, 4, 0);
        ans = m3.clone();
        ans.dotTimesEquals(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 1, 25, 0);

        // m1.equals(this)
        // Tested with clone above
        // m1.equals(ans, effectiveZero)
        ans = m1.clone();
        ans.setElement(0, 0, 4.0001);
        ans.setElement(1, 0, -0.0001);
        ans.setElement(1, 3, 1.999999);
        assertFalse(ans.equals(m1));
        assertFalse(m1.equals(ans));
        assertTrue(ans.equals(m1, 0.01));
        assertTrue(ans.equals(m1, 0.00011));
        assertTrue(m1.equals(ans, 0.01));
        assertTrue(m1.equals(ans, 0.00011));
        assertFalse(ans.equals(m1, 0.00001));
        assertFalse(m1.equals(ans, 0.00001));

        // m1.getColumn(columnIndex)
        v = m1.getColumn(0);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 4, 0, 0);
        v = m1.getColumn(1);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 0, 0, 1);
        v = m1.getColumn(2);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 0, 3, 0);
        v = m1.getColumn(3);
        MatrixUtil.testVectorEquals(v,
            SparseVector.class, 3, 6, 2, 0);

        // The following are just to increase my code coverage ... sigh.
        int[] oldcols = m1.getColIdxs();
        m1.decompress();
        int[] newcols = m1.getColIdxs();
        assertEquals(oldcols.length, newcols.length);
        for (int i = 0; i < oldcols.length; ++i)
        {
            assertEquals(oldcols[i], newcols[i]);
        }
        int[] oldfirs = m1.getFirstInRows();
        m1.decompress();
        int[] newfirs = m1.getFirstInRows();
        assertEquals(oldfirs.length, newfirs.length);
        for (int i = 0; i < oldfirs.length; ++i)
        {
            assertEquals(oldfirs[i], newfirs[i]);
        }
        double[] oldvals = m1.getVals();
        m1.decompress();
        double[] newvals = m1.getVals();
        assertEquals(oldvals.length, newvals.length);
        for (int i = 0; i < oldvals.length; ++i)
        {
            AssertUtil.equalToNumDigits(oldvals[i], newvals[i], 12);
        }

        // m1.getElement(rowIndex, columnIndex)
        // Tested in most of the assertEquals above
        // m1.getNumColumns();
        assertEquals(4, m1.getNumColumns());
        assertEquals(2, m2.getNumColumns());
        assertEquals(2, m3.getNumColumns());

        // m1.getNumRows();
        assertEquals(3, m1.getNumRows());
        assertEquals(4, m2.getNumRows());
        assertEquals(2, m3.getNumRows());

        // m1.getRow(rowIndex)
        m1.compress();
        v = m1.getRow(0);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 4, 0, 0, 6);
        v = m1.getRow(1);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 0, 3, 2);
        m1.decompress();
        v = m1.getRow(2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 4, 0, 1, 0, 0);

        // m1.getSubMatrix(minRow, maxRow, minColumn, maxColumn)
        ans = m1.getSubMatrix(1, 2, 1, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 3, 0, 3, 2, 1, 0,
            0);
        ans = m1.getSubMatrix(1, 2, 2, 3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 3, 2, 0, 0);
        ans = m2.getSubMatrix(1, 3, 0, 1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 4, 0, 0, 3, 2,
            0);

        // m1.identity()
        ans = m1.clone();
        ans.identity();
        assertFalse(ans.equals(m1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 1, 0, 0, 0, 0,
            1, 0,
            0, 0, 0, 1, 0);
        ans = m2.clone();
        ans.identity();
        assertFalse(ans.equals(m2));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 1, 0, 0, 1, 0,
            0, 0,
            0);
        ans = m3.clone();
        ans.identity();
        assertFalse(ans.equals(m3));
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 1, 0, 0, 1);

        // m1.inverse()
        ans = m1.getSubMatrix(0, 2, 0, 2).inverse();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 3, 3, .25, 0, 0, 0, 0, 1, 0,
            .33333333, 0);
        MatrixUtil.testMatrixEquals(ans.times(
            m1.getSubMatrix(0, 2, 0, 2)), Matrix.class, 3,
            3, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        ans = m2.getSubMatrix(0, 1, 0, 1);
        ((SparseMatrix) ans).decompress();
        ans = ans.inverse();
        MatrixUtil.testMatrixEquals(ans,
            DenseMatrix.class, 2, 2, 0, .25, .5, -.75);
        MatrixUtil.testMatrixEquals(ans.times(
            m2.getSubMatrix(0, 1, 0, 1)), Matrix.class, 2,
            2, 1, 0, 0, 1);

        // m1.isSquare()
        assertFalse(m1.isSquare());
        assertFalse(m2.isSquare());
        assertTrue(m3.isSquare());

        // m1.isSymmetric()
        assertFalse(m1.isSymmetric());
        assertFalse(m2.isSymmetric());
        assertFalse(m3.isSymmetric());
        ans = m3.clone();
        ans.identity();
        assertTrue(ans.isSymmetric());
        ans.setElement(0, 0, 500);
        assertTrue(ans.isSymmetric());
        ans.setElement(0, 1, 1);
        assertFalse(ans.isSymmetric());
        ans.setElement(1, 0, 1);
        assertTrue(ans.isSymmetric());
        ans.setElement(0, 1, 1.00001);
        assertFalse(ans.isSymmetric());

        // m1.isSymmetric(effectiveZero);
        assertFalse(m1.isSymmetric(0.01));
        assertFalse(m2.isSymmetric(0.01));
        assertFalse(m3.isSymmetric(0.01));
        assertFalse(m3.isSymmetric(3.999));
        assertTrue(m3.isSymmetric(4.001));
        assertTrue(m3.isSymmetric(4));
        ans = m3.clone();
        ans.identity();
        assertTrue(ans.isSymmetric(0.01));
        ans.setElement(0, 0, 500);
        assertTrue(ans.isSymmetric(0.01));
        ans.setElement(0, 1, 1);
        assertFalse(ans.isSymmetric(0.01));
        assertFalse(ans.isSymmetric(0.99));
        assertTrue(ans.isSymmetric(1.01));
        ans.setElement(1, 0, 1);
        assertTrue(ans.isSymmetric(0.0000001));
        ans.setElement(0, 1, 1.00001);
        assertFalse(ans.isSymmetric(0.0000001));
        assertTrue(ans.isSymmetric(0.0001));

        // m1.isZero()
        assertFalse(m1.isZero());
        assertFalse(m2.isZero());
        assertFalse(m3.isZero());
        ans = new SparseMatrix(10, 10);
        assertTrue(ans.isZero());

        // m1.isZero(effectiveZero);
        assertFalse(m1.isZero(0.01));
        assertFalse(m1.isZero(5.99));
        assertTrue(m1.isZero(6.0));
        assertTrue(m1.isZero(6.01));
        assertFalse(m2.isZero(0.01));
        assertFalse(m2.isZero(5.99));
        assertTrue(m2.isZero(6.0));
        assertTrue(m2.isZero(6.01));
        assertFalse(m3.isZero(0.01));
        assertFalse(m3.isZero(4.999999));
        assertTrue(m3.isZero(5.0));
        assertTrue(m3.isZero(5.01));
        ans = new SparseMatrix(10, 10);
        assertTrue(ans.isZero(0.01));
        ans.setElement(1, 1, 0.011);
        assertFalse(ans.isZero(0.01));
        assertTrue(ans.isZero(0.011));

        // m1.iterator()
        // Tested with testMatrixEquals
        // m1.logDeterminant()
        ComplexNumber c = m1.getSubMatrix(0, 2, 0, 2).logDeterminant();
        assertEquals(Math.log(12), c.getRealPart(), 1e-10);
        assertEquals(Math.PI, c.getImaginaryPart(), 1e-10);
        c = m2.getSubMatrix(1, 2, 0, 1).logDeterminant();
        assertEquals(Math.log(12), c.getRealPart(), 1e-10);
        assertEquals(0, c.getImaginaryPart(), 1e-10);
        c = m2.getSubMatrix(0, 1, 0, 1).logDeterminant();
        assertEquals(Math.log(8), c.getRealPart(), 1e-10);
        assertEquals(Math.PI, c.getImaginaryPart(), 1e-10);

        // m1.minus(ans)
        // dense
        ans = m3.minus(ds2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, -6, -1, 4, -5);
        // diagonal
        ans = m3.minus(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -4, 1, 5, -6);
        // sparse
        ans = m1.minus(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        assertTrue(ans.isZero());
        ans = m2.minus(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 0, 0, 0, 0, 0,
            0, 0, 0);
        assertTrue(ans.isZero());
        ans = m3.minus(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 0, 0, 0);
        assertTrue(ans.isZero());

        // m1.minusEquals(ans);
        // dense
        (new SparseMatrix(0, 0)).minusEquals(new DenseMatrix(0, 0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.minusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -6, -1, 4, -5);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.minusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -6, -1, 4, -5);
        // diagonal
        (new SparseMatrix(0, 0)).minusEquals(new DiagonalMatrix(0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.minusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -4, 1, 5, -6);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.minusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, -4, 1, 5, -6);
        // sparse
        (new SparseMatrix(0, 0)).minusEquals(new SparseMatrix(0, 0));
        ans = m1.clone();
        ((SparseMatrix) ans).decompress();
        m1.decompress();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        assertTrue(ans.isZero());
        ans = m1.clone();
        ((SparseMatrix) ans).compress();
        m1.compress();
        ans.minusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0);
        assertTrue(ans.isZero());
        ans = m2.clone();
        ans.minusEquals(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 0, 0, 0, 0, 0,
            0, 0, 0);
        assertTrue(ans.isZero());
        ans = m3.clone();
        ans.minusEquals(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 0, 0, 0);
        assertTrue(ans.isZero());

        // m1.negative()
        ans = m1.negative();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, -4, 0, 0, -6,
            0, 0, -3, -2, 0, -1, 0, 0);
        ans = m2.negative();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, -6, -2, -4, 0,
            0, -3, -2, 0);
        ans = m3.negative();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, -1, -5, 0);

        // m1.negativeEquals();
        ans = m1.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, -4, 0, 0, -6,
            0, 0, -3, -2, 0, -1, 0, 0);
        ans = m2.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, -6, -2, -4, 0,
            0, -3, -2, 0);
        ans = m3.clone();
        ans.negativeEquals();
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, -1, -5, 0);

        // m1.normFrobenius()
        assertEquals(Math.sqrt(66), m1.normFrobenius(), 1e-6);
        assertEquals(Math.sqrt(69), m2.normFrobenius(), 1e-6);
        m3.decompress();
        assertEquals(Math.sqrt(26), m3.normFrobenius(), 1e-6);

        // m1.plus(ans)
        // dense
        ans = m3.plus(ds2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 6, 3, 6, 5);
        // diagonal
        ans = m3.plus(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 4, 1, 5, 6);
        // sparse
        ans = m1.plus(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 8, 0, 0, 12,
            0, 0, 6,
            4, 0, 2, 0, 0);
        ans = m2.plus(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 12, 4, 8, 0,
            0, 6, 4,
            0);
        ans = m3.plus(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 2, 10, 0);

        // m1.plusEquals(ans)
        // dense
        (new SparseMatrix(0, 0)).plusEquals(new DenseMatrix(0, 0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.plusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 3, 6, 5);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.plusEquals(ds2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 6, 3, 6, 5);
        // diagonal
        (new SparseMatrix(0, 0)).plusEquals(new DiagonalMatrix(0));
        ans = m3.clone();
        ((SparseMatrix) ans).decompress();
        ans.plusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 4, 1, 5, 6);
        ans = m3.clone();
        ((SparseMatrix) ans).compress();
        ans.plusEquals(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 4, 1, 5, 6);
        // sparse
        (new SparseMatrix(0, 0)).plusEquals(new SparseMatrix(0, 0));
        ans = m1.clone();
        ((SparseMatrix) ans).compress();
        m1.compress();
        ans.plusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 8, 0, 0, 12,
            0, 0, 6, 4, 0, 2, 0, 0);
        ans = m1.clone();
        ((SparseMatrix) ans).decompress();
        m1.decompress();
        ans.plusEquals(m1);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 4, 8, 0, 0, 12,
            0, 0, 6, 4, 0, 2, 0, 0);
        ans = m1.getSubMatrix(0, 2, 0, 1);
        ans.plusEquals(m2.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 10, 2, 4, 0,
            0, 4);
        ans = m1.getSubMatrix(0, 2, 1, 2);
        ans.plusEquals(m2.getSubMatrix(0, 2, 0, 1));
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 3, 2, 6, 2, 4, 3, 1,
            3);
        ans = m2.clone();
        ans.plusEquals(m2);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 12, 4, 8, 0,
            0, 6, 4, 0);
        ans = m3.clone();
        ans.plusEquals(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 2, 10, 0);
        // m1.pseudoInverse()
        // m1.pseudoInverse(effectiveZero)
        {
            // Test from the Internet
            SparseMatrix M = new SparseMatrix(4, 5);
            M.setElement(0, 0, 1);
            M.setElement(0, 4, 2);
            M.setElement(1, 2, 3);
            M.setElement(3, 1, 4);
            ans = M.pseudoInverse();
            MatrixUtil.testMatrixEquals(ans, Matrix.class, 5, 4, .2, 0, 0, 0, 0,
                0, 0, 0.25, 0, .33333333, 0, 0, 0, 0, 0, 0, 0.4, 0, 0, 0);
            ans = M.pseudoInverse(Math.sqrt(5) - 1e-10);
            MatrixUtil.testMatrixEquals(ans, Matrix.class, 5, 4, .2, 0, 0, 0, 0,
                0, 0, 0.25, 0, .33333333, 0, 0, 0, 0, 0, 0, 0.4, 0, 0, 0);
            ans = M.pseudoInverse(Math.sqrt(5) + 1e-10);
            MatrixUtil.testMatrixEquals(ans, Matrix.class, 5, 4, 0, 0, 0, 0, 0,
                0, 0, 0.25, 0, .33333333, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            // Now, for a sparse matrix that spans its space, make sure the 
            // pseudoinverse inverts it
            ans = m2.pseudoInverse();
            MatrixUtil.testMatrixEquals(ans.times(m2), Matrix.class, 2, 2, 1, 0,
                0, 1);
        }
        // m1.rank()
        assertEquals(3, m1.rank());
        assertEquals(2, m2.rank());
        assertEquals(2, m3.rank());
        ans = m3.clone();
        ans.setElement(0, 1, 0);
        assertEquals(1, ans.rank());

        // m1.rank(effectiveZero);
        assertEquals(3, m1.rank(0.1));
        assertEquals(2, m2.rank(0.1));
        assertEquals(2, m3.rank(0.1));
        ans = m3.clone();
        ans.setElement(0, 1, 0.1);
        assertEquals(1, ans.rank(0.1));
        assertEquals(2, ans.rank(0.1 - 1e-10));

        // m1.scale(scaleFactor)
        ans = m1.scale(2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 8, 0, 0, 12, 0, 0, 6,
            4, 0, 2, 0, 0);
        ans = m2.scale(2);
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, 12, 4, 8, 0, 0, 6, 4,
            0);
        ans = m3.scale(-2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, -2, -10, 0);

        // m1.scaleEquals(scaleFactor);
        ans = m1.clone();
        ans.scaleEquals(2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 8, 0, 0, 12, 0, 0, 6,
            4, 0, 2, 0, 0);
        ans = m2.clone();
        ans.scaleEquals(2);
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, 12, 4, 8, 0, 0, 6, 4,
            0);
        ans = m3.clone();
        ans.scaleEquals(-2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, -2, -10, 0);

        // m1.setColumn(columnIndex, v)
        // dense
        ans = m2.clone();
        ans.setColumn(0, v1);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, v1.getElement(0), 2,
            v1.getElement(1), 0, v1.getElement(2), 3, v1.getElement(3), 0);
        // sparse
        ans.setColumn(1, v2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, v1.getElement(0),
            v2.getElement(0), v1.getElement(1), v2.getElement(1),
            v1.getElement(2), v2.getElement(2), v1.getElement(3), v2.getElement(
                3));

        // m1.setElement(rowIndex, columnIndex, value);
        // Tested in initialization of the SparseMatrix instances
        // m1.setRow(rowIndex, v);
        // dense
        ans = m1.clone();
        ans.setRow(0, v1);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, v1.getElement(0),
            v1.getElement(1), v1.getElement(2), v1.getElement(3), 0, 0, 3, 2,
            0, 1, 0, 0);
        // sparse
        ans.setRow(2, v2);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, v1.getElement(0),
            v1.getElement(1), v1.getElement(2), v1.getElement(3), 0, 0, 3, 2,
            v2.getElement(0), v2.getElement(1), v2.getElement(2),
            v2.getElement(3));

        // m1.setSubMatrix(minRow, minColumn, ans);
        ans = m1.clone();
        ans.setSubMatrix(0, 0, m2.getSubMatrix(1, 3, 0, 1));
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 4, 0, 0, 6, 0, 3, 3,
            2, 2, 0, 0, 0);
        ans.setSubMatrix(1, 2, m3);
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 4, 0, 0, 6, 0, 3, 0,
            1, 2, 0, 5, 0);

        // m1.solve(matrix)
        m3.compress();
        ans = m3.solve(m2.getSubMatrix(0, 1, 0, 1));
        MatrixUtil.testMatrixEquals(ans, Matrix.class, 2, 2, .8, 0, 6, 2);
        m3.decompress();
        ans = m3.solve(m2.getSubMatrix(0, 1, 0, 1));
        MatrixUtil.testMatrixEquals(ans, Matrix.class, 2, 2, .8, 0, 6, 2);

        // m1.solve(vector)
        ans = m1.getSubMatrix(0, 2, 0, 2);
        ((SparseMatrix) ans).compress();
        v = ans.solve(v1.subVector(0, 2));
        MatrixUtil.testVectorEquals(v, Vector.class, 3, 1, 2, 1);
        ans = m1.getSubMatrix(0, 2, 0, 2);
        ((SparseMatrix) ans).decompress();
        v = ans.solve(v2.subVector(1, 3));
        MatrixUtil.testVectorEquals(v, Vector.class, 3, 0, 3, 1);

        // m1.sumOfColumns()
        v = m1.sumOfColumns();
        MatrixUtil.testVectorEquals(v, Vector.class, 3, 10, 5, 1);
        v = m2.sumOfColumns();
        MatrixUtil.testVectorEquals(v, Vector.class, 4, 8, 4, 3, 2);
        v = m3.sumOfColumns();
        MatrixUtil.testVectorEquals(v, Vector.class, 2, 1, 5);

        // m1.sumOfRows()
        v = m1.sumOfRows();
        MatrixUtil.testVectorEquals(v, Vector.class, 4, 4, 1, 3, 8);
        v = m2.sumOfRows();
        MatrixUtil.testVectorEquals(v, Vector.class, 2, 12, 5);
        v = m3.sumOfRows();
        MatrixUtil.testVectorEquals(v, Vector.class, 2, 5, 1);

        // m1.times(matrix)
        // dense
        ans = m2.times(ds1);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 4, 24, 44, 18, 40,
            16, 28, 8, 24, 0, 3, 9, 6, 8, 14, 4, 12);
        m3.decompress();
        ans = m3.times(ds2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 2, 2, 1, 5, 30, 10);
        // diagonal
        ans = m2.times(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 4, 2, 24, 12, 16, 0,
            0, 18, 8, 0);
        m3.decompress();
        ans = m3.times(diag);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 6, 20, 0);
        // sparse
        ans = m1.times(m2);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 3, 2, 36, 8, 4, 9, 4,
            0);
        m2.decompress();
        m3.decompress();
        ans = m2.times(m3);
        MatrixUtil.testMatrixEquals(ans, DenseMatrix.class, 4, 2, 10, 6, 0, 4,
            15, 0, 0, 2);
        ans = m3.clone();
        ans.zero();
        ans = ans.times(m3);
        MatrixUtil.testMatrixEquals(ans, SparseMatrix.class, 2, 2, 0, 0, 0, 0);

        // m1.times(vector)
        // dense
        m1.compress();
        v = m1.times(v1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 22, 8, 3);
        m1.decompress();
        v = m1.times(v1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 22, 8, 3);
        // premult dense
        v = v1.times(m2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 38, 14);
        m2.decompress();
        v = v1.times(m2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 2, 38, 14);
        // sparse
        m1.compress();
        v = m1.times(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 3, 18, 15, 0);
        m1.decompress();
        v = m1.times(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 3, 18, 15, 0);
        // premult sparse
        v = v2.times(m2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 2, 6, 9);
        m2.decompress();
        v = v2.times(m2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 2, 6, 9);

        // m1.trace()
        try
        {
            // Trace only defined for square matrices
            m1.trace();
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path
        }
        try
        {
            // Trace only defined for square matrices
            m2.trace();
            assertTrue(false);
        }
        catch (Throwable t)
        {
            // This is the correct path
        }
        assertEquals(0, m3.trace(), 1e-6);

        // m1.transpose()
        ans = m1.transpose();
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testIsTranspose(m1, ans);
        ans = m2.transpose();
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testIsTranspose(m2, ans);
        ans = m3.transpose();
        assertTrue(ans instanceof SparseMatrix);
        MatrixUtil.testIsTranspose(m3, ans);

        // m1.zero()
        ans = m1.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 3, 4, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0);
        ans = m2.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 4, 2, 0, 0, 0, 0, 0, 0, 0,
            0);
        ans = m3.clone();
        ans.zero();
        MatrixUtil.testMatrixEquals(ans,
            SparseMatrix.class, 2, 2, 0, 0, 0, 0);
    }

    /**
     * Tests all matrix methods that should throw exceptions that they throw
     * those exceptions.
     */
    @Test
    public void testMatrixExceptionsThrown()
    {
        double[][] d1 =
        {
            {
                4, 7, 2, 6, 2
            },
            {
                0, 1, 3, 2, 0
            },
            {
                1, 1, 4, 2, 0
            }
        };
        DenseMatrix m1 = new DenseMatrix(d1);
        double[][] d2 =
        {
            {
                6, 2
            },
            {
                1, 5
            },
            {
                7, 5
            }
        };
        DenseMatrix m2 = new DenseMatrix(d2);
        double[] d3 =
        {
            2, 1, 5, 0
        };
        DiagonalMatrix diag1 = new DiagonalMatrix(d3);
        double[] d4 =
        {
            4, 6
        };
        DiagonalMatrix diag2 = new DiagonalMatrix(d4);
        SparseMatrix s1 = new SparseMatrix(2, 4);
        s1.setElement(1, 0, 4);
        s1.setElement(1, 1, 3);
        s1.setElement(0, 2, 2);
        s1.setElement(1, 3, 1);
        SparseMatrix s2 = new SparseMatrix(6, 7);
        s2.setElement(0, 1, 4);
        double[] d5 =
        {
            4, 3, 2, 1, -1, 6, 8
        };
        DenseVector v1 = new DenseVector(d5);
        SparseVector v2 = new SparseVector(12);
        v2.setElement(3, 3);
        v2.setElement(2, 3);
        gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ mtjf
            = new gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ();
        gov.sandia.cognition.math.matrix.mtj.DenseMatrix mtjm
            = mtjf.createMatrix(2, 2);
        mtjm.setElement(0, 0, 2);
        gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ mtjvf
            = new gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ();
        gov.sandia.cognition.math.matrix.mtj.DenseVector mtjv
            = mtjvf.createVector(2);
        mtjv.setElement(0, 2);

        // m1.assertSameDimensions(ans)
        // Tested with correctness tests
        // m1.checkMultiplicationDimensions(ans)
        // No way to cause an exception
        // m1.checkSameDimensions(ans)
        // No way to cause an exception
        // m1.checkSubmatrixRange(minrow, maxrow, mincol, maxcol)
        // No way to cause an exception
        // m1.clone()
        // No way to cause an exception
        // m1.convertFromVector(v);
        try
        {
            m1.convertFromVector(v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.convertFromVector(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.convertFromVector(v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.convertFromVector(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag2.convertFromVector(v1.subVector(0, 3));
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            s1.convertFromVector(v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.convertFromVector(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.convertToVector()
        // No way to cause an exception
        // m1.dotTimes(ans)
        try
        {
            m1.dotTimes(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.dotTimes(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.dotTimes(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.dotTimes(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.dotTimes(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.dotTimes(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.dotTimes(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.dotTimes(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.dotTimes(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.dotTimesEquals(ans);
        try
        {
            m1.dotTimesEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.dotTimesEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.dotTimesEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.dotTimesEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.dotTimesEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.dotTimesEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.dotTimesEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.dotTimesEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.dotTimesEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.equals(this)
        // No way to cause an exception
        // m1.equals(ans, effectiveZero)
        // No way to cause an exception
        // m1.getColumn(columnIndex)
        try
        {
            m1.getColumn(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getColumn(5);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getColumn(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getColumn(4);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getColumn(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getColumn(4);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // m1.getElement(rowIndex, columnIndex)
        try
        {
            m1.getElement(-1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getElement(2, -1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getElement(3, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getElement(2, 5);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getElement(-1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getElement(2, -1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getElement(4, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getElement(2, 4);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getElement(-1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getElement(2, -1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getElement(2, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getElement(2, 4);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // m1.getNumColumns();
        // No way to cause an exception
        // m1.getNumRows();
        // No way to cause an exception
        // m1.getRow(rowIndex)
        try
        {
            m1.getRow(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getRow(3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getRow(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getRow(4);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getRow(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.getRow(2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // m1.getSubMatrix(minRow, maxRow, minColumn, maxColumn)
        try
        {
            m1.getSubMatrix(-1, 2, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getSubMatrix(1, 2, -1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getSubMatrix(1, 4, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getSubMatrix(1, 2, 1, 6);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getSubMatrix(2, 1, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.getSubMatrix(1, 2, 2, 1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getSubMatrix(-1, 2, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getSubMatrix(1, 2, -1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getSubMatrix(1, 5, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getSubMatrix(1, 2, 1, 5);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getSubMatrix(2, 1, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.getSubMatrix(1, 2, 2, 1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s2.getSubMatrix(-1, 2, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s2.getSubMatrix(1, 2, -1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s2.getSubMatrix(1, 7, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s2.getSubMatrix(1, 2, 1, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s2.getSubMatrix(2, 1, 1, 2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s2.getSubMatrix(1, 2, 2, 1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // m1.identity()
        // No way to cause an exception
        // m1.inverse()
        // First test that rectangular matrices throw exceptions
        try
        {
            m1.inverse();
            assertTrue(false);
        }
        catch (IllegalStateException e)
        { // correct path 
        }
        try
        {
            m1.getSubMatrix(0, 2, 3, 4).inverse();
            assertTrue(false);
        }
        catch (IllegalStateException e)
        { // correct path 
        }
        try
        {
            s1.inverse();
            assertTrue(false);
        }
        catch (IllegalStateException e)
        { // correct path 
        }
        try
        {
            s1.getSubMatrix(0, 0, 0, 1).inverse();
            assertTrue(false);
        }
        catch (IllegalStateException e)
        { // correct path 
        }
        // diag can't be non-square
        // Test that non-invertable matrices don't invert
        try
        {
            m1.getSubMatrix(1, 2, 3, 4).inverse();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        { // correct path 
        }
        try
        {
            s1.getSubMatrix(0, 1, 0, 1).inverse();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        { // correct path 
        }
        try
        {
            diag1.inverse();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        { // correct path 
        }

        // m1.isSquare()
        // No way to cause an exception
        // m1.isSymmetric()
        // No way to cause an exception
        // m1.isSymmetric(effectiveZero)
        try
        {
            m1.isSymmetric(-1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        // m1.isZero()
        // No way to cause an exception
        // m1.isZero(effectiveZero);
        try
        {
            m1.isSymmetric(-1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }

        // m1.iterator()
        try
        {
            m1.iterator().remove();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        { // correct path
        }
        try
        {
            s1.iterator().remove();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        { // correct path
        }
        try
        {
            diag1.iterator().remove();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        { // correct path
        }

        // m1.logDeterminant()
        try
        {
            m1.logDeterminant();
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // correct path
        }
        try
        {
            m2.logDeterminant();
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // correct path
        }
        try
        {
            s1.logDeterminant();
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // correct path
        }
        try
        {
            s2.logDeterminant();
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // correct path
        }

        // m1.minus(ans)
        try
        {
            m1.minus(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.minus(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.minus(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.minus(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.minus(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.minus(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.minus(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.minus(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.minus(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.minusEquals(ans);
        try
        {
            m1.minusEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.minusEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.minusEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.minusEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.minusEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.minusEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.minusEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.minusEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.minusEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.negative()
        // No way to cause an exception
        // m1.negativeEquals();
        // No way to cause an exception
        // m1.normFrobenius()
        // No way to cause an exception
        // m1.plus(ans)
        try
        {
            m1.plus(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.plus(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.plus(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.plus(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.plus(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.plus(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.plus(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.plus(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.plus(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.plusEquals(ans)
        try
        {
            m1.plusEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.plusEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.plusEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.plusEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.plusEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.plusEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.plusEquals(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.plusEquals(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.plusEquals(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.pseudoInverse()
        // No way to cause an exception
        // m1.pseudoInverse(effectiveZero)
        try
        {
            m1.pseudoInverse(-1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        // m1.rank()
        // No way to cause an exception
        // m1.rank(effectiveZero);
        try
        {
            m1.rank(-1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        // m1.scale(scaleFactor)
        // No way to cause an exception
        // m1.scaleEquals(scaleFactor);
        // No way to cause an exception

        // m1.setColumn(columnIndex, v)
        try
        {
            m1.setColumn(-1, v1.subVector(0, 2));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setColumn(5, v1.subVector(0, 2));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setColumn(-1, v1.subVector(0, 3));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setColumn(4, v1.subVector(0, 3));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setColumn(-1, v1.subVector(0, 1));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setColumn(4, v1.subVector(0, 1));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        // Within bounds, wrong size
        try
        {
            m1.setColumn(0, v1.subVector(0, 0));
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.setColumn(0, v1.subVector(0, 0));
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.setColumn(0, v1.subVector(0, 0));
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.setColumn(0, v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.setColumn(0, v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.setColumn(0, v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.setElement(rowIndex, columnIndex, value);
        try
        {
            m1.setElement(-1, 2, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setElement(2, -1, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setElement(3, 2, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setElement(2, 5, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setElement(-1, 2, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setElement(2, -1, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setElement(4, 2, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setElement(2, 4, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setElement(-1, 2, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setElement(2, -1, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setElement(2, 2, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setElement(2, 4, 8);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // m1.setRow(rowIndex, v);
        try
        {
            m1.setRow(-1, v2.subVector(0, 4));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setRow(3, v2.subVector(0, 4));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setRow(-1, v2.subVector(0, 3));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setRow(4, v2.subVector(0, 3));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setRow(-1, v2.subVector(0, 3));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setRow(2, v2.subVector(0, 3));
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        // Within bounds, wrong size
        try
        {
            m1.setRow(0, v1.subVector(0, 0));
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.setRow(0, v1.subVector(0, 0));
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.setRow(0, v1.subVector(0, 0));
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.setRow(0, v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.setRow(0, v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.setRow(0, v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.setSubMatrix(minRow, minColumn, ans);
        try
        {
            m1.setSubMatrix(0, 0, diag1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setSubMatrix(2, 0, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setSubMatrix(0, 4, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setSubMatrix(-1, 0, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setSubMatrix(0, -1, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m1.setSubMatrix(0, 1, s2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            m2.setSubMatrix(0, 0, s1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setSubMatrix(0, 0, diag1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setSubMatrix(1, 0, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setSubMatrix(0, 3, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setSubMatrix(-1, 0, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setSubMatrix(0, -1, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            s1.setSubMatrix(0, 1, s2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setSubMatrix(1, 1, diag1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setSubMatrix(3, 3, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setSubMatrix(0, 3, diag2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            diag1.setSubMatrix(-1, 0, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setSubMatrix(0, -1, diag2);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        try
        {
            diag1.setSubMatrix(0, 1, s2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            diag1.setSubMatrix(0, 0, s1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }

        // m1.solve(matrix)
        // First, the non-square matrix exception
        try
        {
            m1.solve(m2);
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        try
        {
            m2.solve(m1);
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        try
        {
            s1.solve(m2.getSubMatrix(0, 1, 0, 1));
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        try
        {
            s2.getSubMatrix(0, 2, 0, 1).solve(m2);
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        // Now the RHS doesn't match the LHS dims
        try
        {
            m1.getSubMatrix(0, 1, 0, 1).solve(m2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            m1.getSubMatrix(0, 2, 0, 2).solve(m2.getSubMatrix(0, 1, 0, 1));
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            s1.getSubMatrix(0, 1, 0, 1).solve(m2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            s2.getSubMatrix(0, 5, 0, 5).solve(m2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            diag1.solve(m2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            diag2.solve(s2);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }

        // m1.solve(vector)
        // First, the non-square matrix exception
        try
        {
            m1.solve(v1.subVector(0, 2));
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        try
        {
            m2.solve(v1.subVector(0, 2));
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        try
        {
            s1.solve(v1.subVector(0, 1));
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        try
        {
            s2.solve(v1.subVector(0, 5));
            assertFalse(true);
        }
        catch (IllegalStateException e)
        {
            // Correct path
        }
        // Now the RHS doesn't match the LHS dims
        try
        {
            m1.getSubMatrix(0, 1, 0, 1).solve(v1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            m1.getSubMatrix(0, 2, 0, 2).solve(v1.subVector(0, 1));
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            s1.getSubMatrix(0, 1, 0, 1).solve(v1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            s2.getSubMatrix(0, 5, 0, 5).solve(v1.subVector(0, 4));
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            diag1.solve(v1.subVector(0, 2));
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            diag2.solve(v1.subVector(0, 2));
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }

        // m1.sumOfColumns()
        // No way to cause an exception
        // m1.sumOfRows()
        // No way to cause an exception
        // m1.times(matrix)
        try
        {
            m1.times(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.times(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.times(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.times(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.times(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.times(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.times(m2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.times(diag2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.times(s2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.times(vector)
        try
        {
            m1.times(v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            m1.times(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.times(v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            diag1.times(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.times(v1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            s1.times(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        // Pre-mult with vectors of all types but the wrong sizes
        try
        {
            v1.times(m1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            v2.times(m1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            v1.times(diag1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            v2.times(diag1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            v1.times(s1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        try
        {
            v2.times(s1);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // m1.trace()
        // No way to cause an exception
        // m1.transpose()
        // No way to cause an exception
        // m1.zero()
        // No way to cause an exception
        Iterator<MatrixEntry> iter = s1.getNonZeroValueIterator();
        for (int i = 0; i < 3; ++i)
        {
            iter.next();
        }
        try
        {
            iter.next();
            assertFalse(true);
        }
        catch (Exception e)
        {
        }
        iter = s1.getNonZeroValueIterator();
        MatrixEntry me = iter.next();
        try
        {
            me.setColumnIndex(1);
            assertFalse(true);
        }
        catch (UnsupportedOperationException e)
        {
        }
        try
        {
            me.setRowIndex(1);
            assertFalse(true);
        }
        catch (UnsupportedOperationException e)
        {
        }
        try
        {
            me.setValue(1);
            assertFalse(true);
        }
        catch (UnsupportedOperationException e)
        {
        }
        try
        {
            iter.remove();
            assertFalse(true);
        }
        catch (UnsupportedOperationException e)
        {
        }
        try
        {
            iter = s1.getNonZeroValueIterator(3);
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            iter = s1.getNonZeroValueIterator(-1);
        }
        catch (IllegalArgumentException e)
        {
        }

    }

}
