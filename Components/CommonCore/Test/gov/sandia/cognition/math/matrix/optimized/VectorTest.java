/*
 * File:                VectorTest.java
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

import gov.sandia.cognition.math.matrix.custom.SparseVector;
import gov.sandia.cognition.math.matrix.custom.SparseMatrix;
import gov.sandia.cognition.math.matrix.custom.DenseVector;
import gov.sandia.cognition.math.matrix.custom.DenseMatrix;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import org.junit.Test;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.testutil.AssertUtil;
import gov.sandia.cognition.testutil.MatrixUtil;
import java.text.NumberFormat;
import java.util.Iterator;
import static org.junit.Assert.*;

/**
 * Tests vector-vector operations for correctness (both of results and
 * exceptions thrown).
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class VectorTest
{

    /**
     * Tests all DenseVector (op) Vector operations.
     */
    @Test
    public void testDenseVectorOps()
    {
        double[] d1 =
        {
            10.0, 2.0, 0, 0.5, 5, 2, 3, 11, -3, 0
        };
        DenseVector v1 = new DenseVector(d1);
        double[] d2 =
        {
            -1.0, 3.0, 4.0, -5, 2, 0, 10, -3, 0, 1
        };
        DenseVector v2 = new DenseVector(d2);
        SparseVector v3 = new SparseVector(10);
        v3.setElement(3, 2);
        v3.setElement(6, -1);
        v3.setElement(7, 3);
        v3.setElement(9, -4);

        // v1.angle(v3);
        // Dense
        assertEquals(v1.angle(v2), 1.56843724, 1e-6);
        assertEquals(v2.angle(v1), 1.56843724, 1e-6);
        assertEquals(v1.angle(v1), 0, 1e-6);
        // Sparse
        assertEquals(v1.angle(v3), 1.2206682, 1e-6);
        assertEquals(v2.angle(v3), 2.05900159, 1e-6);

        // v1.assertDimensionalityEquals(otherDimensionality);
        v1.assertDimensionalityEquals(10);
        v2.assertDimensionalityEquals(10);

        // v1.assertSameDimensionality(v3);
        // Dense
        v1.assertSameDimensionality(v2);
        // Sparse
        v1.assertSameDimensionality(v3);

        // v1.checkSameDimensionality(v3);
        // Dense
        assertTrue(v1.checkSameDimensionality(v2));
        assertFalse(v1.checkSameDimensionality(new DenseVector(9)));
        // Sparse
        assertTrue(v1.checkSameDimensionality(v3));
        assertFalse(v1.checkSameDimensionality(new SparseVector(9)));

        // v1.clone();
        MatrixUtil.testVectorEquals(v1.clone(), DenseVector.class, 10, 10, 2, 0,
            0.5, 5, 2, 3, 11, -3, 0);

        // v1.convertFromVector(v3);
        // Dense
        DenseVector dv = new DenseVector(10);
        dv.convertFromVector(v1);
        MatrixUtil.testVectorEquals(dv, DenseVector.class, 10, 10, 2, 0, 0.5, 5,
            2, 3, 11, -3, 0);
        // Sparse ... note this moves sparse values into a dense representation
        // (bad idea)
        dv.convertFromVector(v3);
        MatrixUtil.testVectorEquals(dv, DenseVector.class, 10, 0, 0, 0, 2, 0, 0,
            -1, 3, 0, -4);

        // v1.convertToVector();
        MatrixUtil.testVectorEquals(v1.convertToVector(), DenseVector.class,
            10, 10, 2, 0, 0.5, 5, 2, 3, 11, -3, 0);

        // v1.cosine(v3);
        // Sparse
        assertEquals(v1.cosine(v2), 0.00235909, 1e-6);
        assertEquals(v2.cosine(v1), 0.00235909, 1e-6);
        assertEquals(v1.cosine(v1), 1.0, 1e-6);
        // Dense
        assertEquals(v1.cosine(v3), 0.34301817, 1e-6);
        assertEquals(v2.cosine(v3), -0.4690416, 1e-6);

        // v1.dotProduct(v3);
        // Sparse
        AssertUtil.equalToNumDigits(31, v1.dotProduct(v3), 6);
        AssertUtil.equalToNumDigits(-33, v2.dotProduct(v3), 6);
        // Dense
        AssertUtil.equalToNumDigits(0.5, v1.dotProduct(v2), 6);
        AssertUtil.equalToNumDigits(0.5, v2.dotProduct(v1), 6);

        // v1.dotTimes(v3);
        // Dense
        Vector v = v1.dotTimes(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, 6, 0, -2.5,
            10, 0, 30, -33, 0, 0);
        v = v2.dotTimes(v1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, 6, 0, -2.5,
            10, 0, 30, -33, 0, 0);
        // Sparse
        v = v1.dotTimes(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 1, 0, 0,
            -3, 33, 0, 0);
        v = v2.dotTimes(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, -10, 0,
            0, -10, -9, 0, -4);

        // v1.dotTimesEquals(v3);
        // Dense
        v = v1.clone();
        v.dotTimesEquals(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, 6, 0, -2.5,
            10, 0, 30, -33, 0, 0);
        v = v2.clone();
        v.dotTimesEquals(v1);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, 6, 0, -2.5,
            10, 0, 30, -33, 0, 0);
        // Sparse... NOTE: This results in a dense vector storing sparse data
        v = v1.clone();
        v.dotTimesEquals(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 0, 0, 0, 1, 0, 0,
            -3, 33, 0, 0);
        v = v2.clone();
        v.dotTimesEquals(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 0, 0, 0, -10, 0, 0,
            -10, -9, 0, -4);

        // v1.equals(this);
        v = v1.clone();
        assertTrue(v1.equals(v));
        assertFalse(v1.equals(v2));
        v = new SparseVector(10);
        v.setElement(0, 10);
        v.setElement(1, 2);
        v.setElement(2, 0);
        v.setElement(3, 0.5);
        v.setElement(4, 5);
        v.setElement(5, 2);
        v.setElement(6, 3);
        v.setElement(7, 11);
        v.setElement(8, -3);
        v.setElement(9, 0);
        assertTrue(v1.equals(v));
        assertFalse(v1.equals(v3));

        // v1.equals(v3, effectiveZero);
        v = v1.clone();
        v.plusEquals(v1.scale(1e-7));
        assertTrue(v1.equals(v, 1e-5));
        assertFalse(v1.equals(v2, 1e-5));
        assertFalse(v1.equals(v2, 1e-5));

        // v1.euclideanDistance(v3);
        // Sparse
        assertEquals(v1.euclideanDistance(v1), 0, 1e-10);
        assertEquals(v1.euclideanDistance(v2), 20.8865986, 1e-6);
        // Dense
        assertEquals(v1.euclideanDistance(v3), 15.5, 1e-6);
        assertEquals(v2.euclideanDistance(v3), 16.1554944, 1e-6);

        // v1.euclideanDistanceSquared(v3);
        // Sparse
        assertEquals(v1.euclideanDistanceSquared(v1), 0, 1e-10);
        assertEquals(v1.euclideanDistanceSquared(v2), 436.25, 1e-6);
        // Dense
        assertEquals(v1.euclideanDistanceSquared(v3), 240.25, 1e-6);
        assertEquals(v2.euclideanDistanceSquared(v3), 261, 1e-6);

        // v1.getDimensionality();
        assertEquals(10, v1.getDimensionality());
        assertEquals(10, v2.getDimensionality());
        assertEquals(10, v.getDimensionality());

        // v1.getElement(index);
        // Tested in nearly all the tests

        // v1.isUnitVector();
        assertFalse(v1.isUnitVector());
        assertFalse(v2.isUnitVector());
        // To make sure it doesn't have numerical errors, only keep one value
        v = new DenseVector(new double[]
        {
            0, 0, 0, 4, 0, 0, 0, 0, 0, 0
        });
        v = v.unitVector();
        assertTrue(v.isUnitVector());
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0);
        v = new DenseVector(new double[]
        {
            0, 0, 0, 4, 0, 0, 0, 0, 0, 0
        });
        v.unitVectorEquals();
        assertTrue(v.isUnitVector());
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0);

        // v1.isUnitVector(tolerance);
        assertFalse(v1.isUnitVector(1e-5));
        assertFalse(v2.isUnitVector(1e-5));
        v.plusEquals(v1.scale(1e-7));
        assertTrue(v.isUnitVector(1e-5));

        // v1.isZero();
        assertFalse(v1.isZero());
        assertFalse(v2.isZero());
        v.zero();
        assertTrue(v.isZero());
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0);

        // v1.isZero(effectiveZero);
        assertFalse(v1.isZero(1e-5));
        assertFalse(v2.isZero(1e-5));
        v.plusEquals(v1.scale(1e-7));
        assertTrue(v.isZero(1e-5));

        // v1.iterator();
        Iterator<VectorEntry> iter = v1.iterator();
        assertTrue(iter.hasNext());
        VectorEntry e = iter.next();
        assertEquals(e.getIndex(), 0);
        assertEquals(e.getValue(), 10, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 1);
        assertEquals(e.getValue(), 2, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 2);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 3);
        assertEquals(e.getValue(), 0.5, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 4);
        assertEquals(e.getValue(), 5, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 5);
        assertEquals(e.getValue(), 2, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 6);
        assertEquals(e.getValue(), 3, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 7);
        assertEquals(e.getValue(), 11, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 8);
        assertEquals(e.getValue(), -3, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 9);
        assertEquals(e.getValue(), 0, 1e-6);
        assertFalse(iter.hasNext());

        // v1.minus(v3);
        // Dense
        v = v1.minus(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 11, -1, -4, 5.5, 3,
            2, -7, 14, -3, -1);
        v = v1.minus(v1);
        assertTrue(v.isZero(1e-10));
        // Sparse
        v = v1.minus(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 10, 2, 0, -1.5, 5,
            2, 4, 8, -3, 4);
        v = v2.minus(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -1, 3, 4, -7, 2, 0,
            11, -6, 0, 5);

        // v1.minusEquals(v3);
        // Dense
        v = v1.clone();
        v.minusEquals(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 11, -1, -4, 5.5, 3,
            2, -7, 14, -3, -1);
        v = v1.clone();
        v.minusEquals(v1);
        assertTrue(v.isZero(1e-10));
        // Sparse
        v = v1.clone();
        v.minusEquals(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 10, 2, 0, -1.5, 5,
            2, 4, 8, -3, 4);
        v = v2.clone();
        v.minusEquals(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -1, 3, 4, -7, 2, 0,
            11, -6, 0, 5);

        // v1.negative();
        v = v1.negative();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, -2, 0, -0.5,
            -5, -2, -3, -11, 3, 0);
        v = v2.negative();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 1, -3, -4, 5, -2,
            0, -10, 3, 0, -1);

        // v1.negativeEquals();
        v = v1.clone();
        v.negativeEquals();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, -2, 0, -0.5,
            -5, -2, -3, -11, 3, 0);
        v = v2.clone();
        v.negativeEquals();
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 1, -3, -4, 5, -2,
            0, -10, 3, 0, -1);

        // v1.norm(power);
        // This just makes sure it's calculated the same way in this method.
        // Test for correctness is in the next tests
        assertEquals(v1.norm(1), v1.norm1(), 1e-10);
        assertEquals(v2.norm(2), v2.norm2(), 1e-10);

        // v1.norm1();
        assertEquals(36.5, v1.norm1(), 1e-10);
        assertEquals(29, v2.norm1(), 1e-10);

        // v1.norm2();
        assertEquals(Math.sqrt(272.25), v1.norm2(), 1e-10);
        assertEquals(Math.sqrt(165), v2.norm2(), 1e-10);

        // v1.norm2Squared();
        assertEquals(272.25, v1.norm2Squared(), 1e-10);
        assertEquals(165, v2.norm2Squared(), 1e-10);

        // v1.normInfinity();
        assertEquals(11, v1.normInfinity(), 1e-10);
        assertEquals(10, v2.normInfinity(), 1e-10);

        // v1.outerProduct(v3);
        Matrix m = v1.outerProduct(v2);
        MatrixUtil.testMatrixEquals(m, DenseMatrix.class, 10, 10,
            -10, 30, 40, -50, 20, 0, 100, -30, 0, 10,
            -2, 6, 8, -10, 4, 0, 20, -6, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -.5, 1.5, 2, -2.5, 1, 0, 5, -1.5, 0, .5,
            -5, 15, 20, -25, 10, 0, 50, -15, 0, 5,
            -2, 6, 8, -10, 4, 0, 20, -6, 0, 2,
            -3, 9, 12, -15, 6, 0, 30, -9, 0, 3,
            -11, 33, 44, -55, 22, 0, 110, -33, 0, 11,
            3, -9, -12, 15, -6, 0, -30, 9, 0, -3,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Matrix m2 = v2.outerProduct(v1);
        assertEquals(m.transpose(), m2);
        MatrixUtil.testMatrixEquals(m2, DenseMatrix.class, 10, 10,
            -10, -2, 0, -.5, -5, -2, -3, -11, 3, 0,
            30, 6, 0, 1.5, 15, 6, 9, 33, -9, 0,
            40, 8, 0, 2, 20, 8, 12, 44, -12, 0,
            -50, -10, 0, -2.5, -25, -10, -15, -55, 15, 0,
            20, 4, 0, 1, 10, 4, 6, 22, -6, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            100, 20, 0, 5, 50, 20, 30, 110, -30, 0,
            -30, -6, 0, -1.5, -15, -6, -9, -33, 9, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            10, 2, 0, .5, 5, 2, 3, 11, -3, 0);
        m = v1.outerProduct(v3);
        MatrixUtil.testMatrixEquals(m, SparseMatrix.class, 10, 10,
            0, 0, 0, 20, 0, 0, -10, 30, 0, -40,
            0, 0, 0, 4, 0, 0, -2, 6, 0, -8,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, -.5, 1.5, 0, -2,
            0, 0, 0, 10, 0, 0, -5, 15, 0, -20,
            0, 0, 0, 4, 0, 0, -2, 6, 0, -8,
            0, 0, 0, 6, 0, 0, -3, 9, 0, -12,
            0, 0, 0, 22, 0, 0, -11, 33, 0, -44,
            0, 0, 0, -6, 0, 0, 3, -9, 0, 12,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        // v1.plus(v3);
        // Sparse
        v = v1.plus(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 10, 2, 0, 2.5, 5,
            2, 2, 14, -3, -4);
        // Dense
        v = v1.plus(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 9, 5, 4, -4.5, 7,
            2, 13, 8, -3, 1);

        // v1.plusEquals(v3);
        // Sparse
        v = v1.clone();
        v.plusEquals(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 10, 2, 0, 2.5, 5,
            2, 2, 14, -3, -4);
        // Dense
        v = v1.clone();
        v.plusEquals(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 9, 5, 4, -4.5, 7,
            2, 13, 8, -3, 1);

        // v1.scale(scaleFactor);
        v = v1.scale(3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 30, 6, 0, 1.5, 15,
            6, 9, 33, -9, 0);
        v = v2.scale(-2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 2, -6, -8, 10, -4,
            0, -20, 6, 0, -2);

        // v1.scaleEquals(scaleFactor);
        v = v1.clone();
        v.scaleEquals(3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 30, 6, 0, 1.5, 15,
            6, 9, 33, -9, 0);
        v = v2.clone();
        v.scaleEquals(-2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 2, -6, -8, 10, -4,
            0, -20, 6, 0, -2);

        // v1.setElement(index, value);
        v = v1.clone();
        v.setElement(0, 3);
        v.setElement(4, 4);
        v.setElement(7, 2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 3, 2, 0, 0.5, 4, 2,
            3, 2, -3, 0);

        // v1.stack(v2);
        // Dense
        v = v1.stack(v2);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 20, 10, 2, 0, 0.5, 5,
            2, 3, 11, -3, 0, -1, 3, 4, -5, 2, 0, 10, -3, 0, 1);
        // Sparse
        v = v1.stack(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 20, 10, 2, 0, 0.5, 5,
            2, 3, 11, -3, 0, 0, 0, 0, 2, 0, 0, -1, 3, 0, -4);
        v = v1.subVector(2, 2).stack(v3.subVector(0, 5));
        MatrixUtil.testVectorEquals(v, SparseVector.class, 7, 0, 0, 0, 0, 2, 0,
            0);

        // v1.subVector(minIndex, maxIndex);
        v = v1.subVector(0, 8);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 9, 10, 2, 0, 0.5, 5, 2,
            3, 11, -3);
        v = v1.subVector(3, 5);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 3, 0.5, 5, 2);

        // v1.sum();
        // Sparse
        assertEquals(30.5, v1.sum(), 1e-6);
        assertEquals(11, v2.sum(), 1e-6);

        // v1.times(null);
        // Tested in the MatrixCorrectnessTests

        // I'm not going to test these for what they say as that's likely to
        // change regularly.  I'm just making sure they don't fail.
        // v1.toString();
        assertTrue(v1.toString().length() > 0);
        // v1.toString(null);
        String s1 = v1.toString(NumberFormat.getNumberInstance());
        assertTrue(s1.length() > 0);
        // v1.toString(null, null);
        String s2 = v1.toString(NumberFormat.getNumberInstance(), ":");
        assertTrue(s2.length() > 0);
        assertEquals(s1.replace(" ", ":"), s2);

        // v1.unitVector();
        // Tested with isUnitVector()

        // v1.unitVectorEquals();
        // Tested with isUnitVector()

        // v1.zero();
        // Tested with isZero()
    }

    /**
     * Tests all SparseVector (op) Vector operations.
     */
    @Test
    public void testSparseVectorOps()
    {
        SparseVector v1 = new SparseVector(10);
        v1.setElement(3, 2);
        v1.setElement(6, -1);
        v1.setElement(7, 3);
        v1.setElement(9, -4);
        SparseVector v2 = new SparseVector(10);
        v2.setElement(0, 2);
        v2.setElement(2, 3);
        v2.setElement(6, 3);
        v2.setElement(9, -4);
        double[] d3 =
        {
            10.0, 2.0, 0, 0.5, 5, 2, 3, 11, -3, 0
        };
        DenseVector v3 = new DenseVector(d3);

        // v1.angle(v3);
        // Sparse
        assertEquals(v1.angle(v2), 1.1755595, 1e-6);
        assertEquals(v2.angle(v1), 1.1755595, 1e-6);
        assertEquals(v1.angle(v1), 0, 1e-6);
        // Dense
        assertEquals(v1.angle(v3), 1.2206682, 1e-6);
        assertEquals(v2.angle(v3), 1.28166842, 1e-6);

        // v1.assertDimensionalityEquals(otherDimensionality);
        v1.assertDimensionalityEquals(10);
        v2.assertDimensionalityEquals(10);

        // v1.assertSameDimensionality(v3);
        // Sparse
        v1.assertSameDimensionality(v2);
        // Dense
        v1.assertSameDimensionality(v3);

        // v1.checkSameDimensionality(v3);
        // Sparse
        assertTrue(v1.checkSameDimensionality(v2));
        assertFalse(v1.checkSameDimensionality(new SparseVector(9)));
        // Dense
        assertTrue(v1.checkSameDimensionality(v3));
        assertFalse(v1.checkSameDimensionality(new DenseVector(9)));

        // v1.clone();
        MatrixUtil.testVectorEquals(v1.clone(), SparseVector.class, 10, 0, 0, 0,
            2,
            0, 0, -1, 3, 0, -4);

        // v1.convertFromVector(v3);
        // Sparse
        SparseVector sv = new SparseVector(10);
        sv.convertFromVector(v1);
        MatrixUtil.testVectorEquals(sv, SparseVector.class, 10, 0, 0, 0, 2, 0, 0,
            -1, 3, 0, -4);
        // Dense ... note this moves dense values into a sparse representation (bad idea)
        sv.convertFromVector(v3);
        MatrixUtil.testVectorEquals(sv, SparseVector.class, 10, 10.0, 2.0, 0,
            0.5,
            5, 2, 3, 11, -3, 0);

        // v1.convertToVector();
        MatrixUtil.testVectorEquals(v1.convertToVector(), SparseVector.class, 10,
            0, 0, 0, 2, 0, 0, -1, 3, 0, -4);

        // v1.cosine(v3);
        // Sparse
        assertEquals(v1.cosine(v2), 0.38502677, 1e-6);
        assertEquals(v2.cosine(v1), 0.38502677, 1e-6);
        assertEquals(v1.cosine(v1), 1.0, 1e-6);
        // Dense
        assertEquals(v1.cosine(v3), 0.34301817, 1e-6);
        assertEquals(v2.cosine(v3), 0.28511644, 1e-6);

        // v1.dotProduct(v3);
        // Sparse
        AssertUtil.equalToNumDigits(13, v1.dotProduct(v2), 6);
        AssertUtil.equalToNumDigits(13, v2.dotProduct(v1), 6);
        Vector v = v1.clone();
        v.setElement(9, 0);
        AssertUtil.equalToNumDigits(-3, v2.dotProduct(v), 6);
        AssertUtil.equalToNumDigits(-3, v.dotProduct(v2), 6);
        // Dense
        AssertUtil.equalToNumDigits(31, v1.dotProduct(v3), 6);
        AssertUtil.equalToNumDigits(29, v2.dotProduct(v3), 6);

        // v1.dotTimes(v3);
        // Sparse
        v = v1.dotTimes(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 0, 0, 0,
            -3, 0, 0, 16);
        // Dense
        v = v1.dotTimes(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 1, 0, 0,
            -3, 33, 0, 0);
        v = v2.dotTimes(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 20, 0, 0, 0, 0, 0,
            9, 0, 0, 0);

        // v1.dotTimesEquals(v3);
        v = v1.clone();
        v.dotTimesEquals(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 0, 0, 0,
            -3, 0, 0, 16);
        // Dense
        v = v1.clone();
        v.dotTimesEquals(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 1, 0, 0,
            -3, 33, 0, 0);
        v = v2.clone();
        v.dotTimesEquals(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 20, 0, 0, 0, 0, 0,
            9, 0, 0, 0);

        // v1.equals(this);
        v = v1.clone();
        assertTrue(v1.equals(v));
        assertFalse(v1.equals(v2));
        assertTrue(v1.equals(new DenseVector(new double[]
        {
            0, 0, 0, 2, 0, 0, -1, 3, 0, -4
        })));
        assertFalse(v1.equals(v3));

        // v1.equals(v3, effectiveZero);
        v = v1.clone();
        v.plusEquals(v1.scale(1e-6));
        assertTrue(v1.equals(v, 1e-5));
        assertFalse(v1.equals(v2, 1e-5));
        assertFalse(v1.equals(v2, 1e-5));

        // v1.euclideanDistance(v3);
        // Sparse
        assertEquals(v1.euclideanDistance(v1), 0, 1e-10);
        assertEquals(v1.euclideanDistance(v2), 6.4807407, 1e-6);
        v = v2.clone();
        v.setElement(9, 0);
        assertEquals(Math.sqrt(58), v1.euclideanDistance(v), 1e-6);
        assertEquals(Math.sqrt(58), v.euclideanDistance(v1), 1e-6);
        // Dense
        assertEquals(v1.euclideanDistance(v3), 15.5, 1e-6);
        assertEquals(v2.euclideanDistance(v3), 15.88238017, 1e-6);

        // v1.euclideanDistanceSquared(v3);
        // Sparse
        assertEquals(v1.euclideanDistanceSquared(v1), 0, 1e-10);
        assertEquals(v1.euclideanDistanceSquared(v2), 42, 1e-6);
        // Dese
        assertEquals(v1.euclideanDistanceSquared(v3), 240.25, 1e-6);
        assertEquals(v2.euclideanDistanceSquared(v3), 252.25, 1e-6);

        // v1.getDimensionality();
        assertEquals(10, v1.getDimensionality());
        assertEquals(10, v2.getDimensionality());

        // v1.getElement(index);
        // Tested in nearly all the tests

        // v1.isUnitVector();
        assertFalse(v1.isUnitVector());
        assertFalse(v2.isUnitVector());
        // To make sure it doesn't have numerical errors, only keep one value
        v = v1.clone();
        v.setElement(6, 0);
        v.setElement(7, 0);
        v.setElement(9, 0);
        v = v.unitVector();
        assertTrue(v.isUnitVector());
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0);
        v = v1.clone();
        v.setElement(6, 0);
        v.setElement(7, 0);
        v.setElement(9, 0);
        v.unitVectorEquals();
        assertTrue(v.isUnitVector());
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0);

        // v1.isUnitVector(tolerance);
        assertFalse(v1.isUnitVector(1e-5));
        assertFalse(v2.isUnitVector(1e-5));
        v.plusEquals(v1.scale(1e-6));
        assertTrue(v.isUnitVector(1e-5));

        // v1.isZero();
        assertFalse(v1.isZero());
        assertFalse(v2.isZero());
        v.zero();
        assertTrue(v.isZero());
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0);

        // v1.isZero(effectiveZero);
        assertFalse(v1.isZero(1e-5));
        assertFalse(v2.isZero(1e-5));
        v.plusEquals(v1.scale(1e-6));
        assertTrue(v.isZero(1e-5));

        // v1.iterator();
        Iterator<VectorEntry> iter = v1.iterator();
        assertTrue(iter.hasNext());
        VectorEntry e = iter.next();
        assertEquals(e.getIndex(), 0);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 1);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 2);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 3);
        assertEquals(e.getValue(), 2, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 4);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 5);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 6);
        assertEquals(e.getValue(), -1, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 7);
        assertEquals(e.getValue(), 3, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 8);
        assertEquals(e.getValue(), 0, 1e-6);
        assertTrue(iter.hasNext());
        e = iter.next();
        assertEquals(e.getIndex(), 9);
        assertEquals(e.getValue(), -4, 1e-6);
        assertFalse(iter.hasNext());

        // v1.minus(v3);
        // Sparse
        v = v1.minus(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -2, 0, -3, 2, 0,
            0, -4, 3, 0, 0);
        v = v1.minus(v1);
        assertTrue(v.isZero(1e-10));
        // Dense
        v = v1.minus(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -10, -2, 0, 1.5,
            -5, -2, -4, -8, 3, -4);
        v = v2.minus(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, -8, -2, 3, -0.5,
            -5, -2, 0, -11, 3, -4);

        // v1.minusEquals(v3);
        v = v1.clone();
        v.minusEquals(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -2, 0, -3, 2, 0,
            0, -4, 3, 0, 0);
        v = v1.clone();
        v.minusEquals(v1);
        assertTrue(v.isZero(1e-10));
        // Dense ... NOTE: This is a bad idea as you're storing dense vector
        // data in a sparse representation
        v = v1.clone();
        v.minusEquals(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -10, -2, 0, 1.5,
            -5, -2, -4, -8, 3, -4);
        v = v2.clone();
        v.minusEquals(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -8, -2, 3, -0.5,
            -5, -2, 0, -11, 3, -4);

        // v1.negative();
        v = v1.negative();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, -2, 0, 0,
            1, -3, 0, 4);
        v = v2.negative();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -2, 0, -3, 0, 0,
            0, -3, 0, 0, 4);

        // v1.negativeEquals();
        v = v1.clone();
        v.negativeEquals();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, -2, 0, 0,
            1, -3, 0, 4);
        v = v2.clone();
        v.negativeEquals();
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -2, 0, -3, 0, 0,
            0, -3, 0, 0, 4);

        // v1.norm(power);
        // This just makes sure it's calculated the same way in this method.
        // Test for correctness is in the next tests
        assertEquals(v1.norm(1), v1.norm1(), 1e-10);
        assertEquals(v2.norm(2), v2.norm2(), 1e-10);

        // v1.norm1();
        assertEquals(10, v1.norm1(), 1e-10);
        assertEquals(12, v2.norm1(), 1e-10);

        // v1.norm2();
        assertEquals(Math.sqrt(30), v1.norm2(), 1e-10);
        assertEquals(Math.sqrt(38), v2.norm2(), 1e-10);

        // v1.norm2Squared();
        assertEquals(30, v1.norm2Squared(), 1e-10);
        assertEquals(38, v2.norm2Squared(), 1e-10);

        // v1.normInfinity();
        assertEquals(4, v1.normInfinity(), 1e-10);
        assertEquals(4, v2.normInfinity(), 1e-10);

        // v1.outerProduct(v3);
        Matrix m = v1.outerProduct(v2);
        MatrixUtil.testMatrixEquals(m, SparseMatrix.class, 10, 10,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            4, 0, 6, 0, 0, 0, 6, 0, 0, -8,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -2, 0, -3, 0, 0, 0, -3, 0, 0, 4,
            6, 0, 9, 0, 0, 0, 9, 0, 0, -12,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -8, 0, -12, 0, 0, 0, -12, 0, 0, 16);
        Matrix m2 = v2.outerProduct(v1);
        assertEquals(m.transpose(), m2);
        MatrixUtil.testMatrixEquals(m2, SparseMatrix.class, 10, 10,
            0, 0, 0, 4, 0, 0, -2, 6, 0, -8,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 6, 0, 0, -3, 9, 0, -12,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 6, 0, 0, -3, 9, 0, -12,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, -8, 0, 0, 4, -12, 0, 16);
        v = v2.clone();
        v.setElement(9, 0);
        m2 = v.outerProduct(v1);
        MatrixUtil.testMatrixEquals(m2, SparseMatrix.class, 10, 10,
            0, 0, 0, 4, 0, 0, -2, 6, 0, -8,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 6, 0, 0, -3, 9, 0, -12,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 6, 0, 0, -3, 9, 0, -12,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        m = v1.outerProduct(v);
        MatrixUtil.testMatrixEquals(m, SparseMatrix.class, 10, 10,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            4, 0, 6, 0, 0, 0, 6, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -2, 0, -3, 0, 0, 0, -3, 0, 0, 0,
            6, 0, 9, 0, 0, 0, 9, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -8, 0, -12, 0, 0, 0, -12, 0, 0, 0);
        m = v1.outerProduct(v3);
        MatrixUtil.testMatrixEquals(m, SparseMatrix.class, 10, 10,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            20, 4, 0, 1, 10, 4, 6, 22, -6, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -10, -2, 0, -.5, -5, -2, -3, -11, 3, 0,
            30, 6, 0, 1.5, 15, 6, 9, 33, -9, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -40, -8, 0, -2, -20, -8, -12, -44, 12, 0);
        v = v1.clone();
        v.setElement(9, 0);
        m = v.outerProduct(v3);
        MatrixUtil.testMatrixEquals(m, SparseMatrix.class, 10, 10,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            20, 4, 0, 1, 10, 4, 6, 22, -6, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -10, -2, 0, -.5, -5, -2, -3, -11, 3, 0,
            30, 6, 0, 1.5, 15, 6, 9, 33, -9, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        // v1.plus(v3);
        // Sparse
        v = v1.plus(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 2, 0, 3, 2, 0, 0,
            2,
            3, 0, -8);
        // Dense
        v = v1.plus(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 10, 10, 2, 0, 2.5, 5,
            2,
            2, 14, -3, -4);

        // v1.plusEquals(v3);
        v = v1.clone();
        v.plusEquals(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 2, 0, 3, 2, 0, 0,
            2,
            3, 0, -8);
        // Dense... NOTE: This is a bad idea because you're storing dense data
        // in a sparse representation
        v = v1.clone();
        v.plusEquals(v3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 10, 2, 0, 2.5, 5,
            2,
            2, 14, -3, -4);

        // v1.scale(scaleFactor);
        v = v1.scale(3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 6, 0, 0,
            -3, 9, 0, -12);
        v = v2.scale(-2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -4, 0, -6, 0, 0,
            0,
            -6, 0, 0, 8);

        // v1.scaleEquals(scaleFactor);
        v = v1.clone();
        v.scaleEquals(3);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 0, 0, 0, 6, 0, 0,
            -3, 9, 0, -12);
        v = v2.clone();
        v.scaleEquals(-2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, -4, 0, -6, 0, 0,
            0,
            -6, 0, 0, 8);

        // v1.setElement(index, value);
        v = v1.clone();
        v.setElement(0, 3);
        v.setElement(4, 4);
        v.setElement(7, 2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 10, 3, 0, 0, 2, 4, 0,
            -1, 2, 0, -4);

        // v1.stack(v2);
        // Sparse
        v = v1.stack(v2);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 20, 0, 0, 0, 2, 0, 0,
            -1, 3, 0, -4, 2, 0, 3, 0, 0, 0, 3, 0, 0, -4);
        // Dense
        v = v1.stack(v3);
        MatrixUtil.testVectorEquals(v, DenseVector.class, 20, 0, 0, 0, 2, 0, 0,
            -1, 3, 0, -4, 10, 2, 0, 0.5, 5, 2, 3, 11, -3, 0);
        v = v1.subVector(0, 5).stack(v3.subVector(2, 2));
        MatrixUtil.testVectorEquals(v, SparseVector.class, 7, 0, 0, 0, 2, 0, 0,
            0);

        // v1.subVector(minIndex, maxIndex);
        v = v1.subVector(0, 8);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 9, 0, 0, 0, 2, 0, 0,
            -1, 3, 0);
        v = v1.subVector(3, 5);
        MatrixUtil.testVectorEquals(v, SparseVector.class, 3, 2, 0, 0);

        // v1.sum();
        // Sparse
        assertEquals(0, v1.sum(), 1e-6);
        assertEquals(4, v2.sum(), 1e-6);

        // v1.times(null);

        // I'm not going to test these for what they say as that's likely to
        // change regularly.  I'm just making sure they don't fail.
        // v1.toString();
        assertTrue(v1.toString().length() > 0);
        // v1.toString(null);
        String s1 = v1.toString(NumberFormat.getNumberInstance());
        assertTrue(s1.length() > 0);
        // v1.toString(null, null);
        String s2 = v1.toString(NumberFormat.getNumberInstance(), ":");
        assertTrue(s2.length() > 0);
        assertEquals(s1.replace(" ", ":"), s2);

        // v1.unitVector();
        // Tested with isUnitVector()

        // v1.unitVectorEquals();
        // Tested with isUnitVector()

        // v1.zero();
        // Tested with isZero()
    }

    /**
     * Tests that vector operations throw the correct exceptions when handed
     * improperly sized elements.
     */
    @Test
    public void testVectorExceptionsThrown()
    {
        SparseVector v1 = new SparseVector(14);
        v1.setElement(3, 2);
        v1.setElement(6, -1);
        v1.setElement(7, 3);
        v1.setElement(9, -4);
        SparseVector v2 = new SparseVector(11);
        v2.setElement(0, 2);
        v2.setElement(2, 3);
        v2.setElement(6, 3);
        v2.setElement(9, -4);
        double[] d3 =
        {
            0, 0.5, 5, 2, 3, 11, -3, 0
        };
        DenseVector v3 = new DenseVector(d3);
        double[] d4 =
        {
            10.0, 2.0, 0, 0.5, 5, 2, 3, 11, -3
        };
        DenseVector v4 = new DenseVector(d4);
        gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ mtjf =
            new gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ();
        gov.sandia.cognition.math.matrix.mtj.DenseVector mtjv =
            mtjf.createVector(4);

        // v1.angle(v3);
        try
        {
            v1.angle(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.angle(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.angle(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.angle(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.assertDimensionalityEquals(otherDimensionality);
        try
        {
            v1.assertDimensionalityEquals(v2.getDimensionality());
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.assertDimensionalityEquals(v3.getDimensionality());
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.assertDimensionalityEquals(v2.getDimensionality());
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.assertDimensionalityEquals(v4.getDimensionality());
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.assertSameDimensionality(v3);
        try
        {
            v1.assertSameDimensionality(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.assertSameDimensionality(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.assertSameDimensionality(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.assertSameDimensionality(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.checkSameDimensionality(v3);
        // No exceptions thrown

        // v1.clone();
        // No exceptions thrown

        // v1.convertFromVector(v3);
        try
        {
            v1.convertFromVector(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.convertFromVector(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.convertFromVector(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.convertFromVector(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.convertToVector();
        // No exceptions thrown

        // v1.cosine(v3);
        try
        {
            v1.cosine(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.cosine(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.cosine(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.cosine(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.dotProduct(v3);
        try
        {
            v1.dotProduct(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.dotProduct(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.dotProduct(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.dotProduct(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        // v1.dotTimes(v3);
        try
        {
            v1.dotTimes(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.dotTimes(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.dotTimes(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.dotTimes(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        // v1.dotTimesEquals(v3);
        try
        {
            v1.dotTimesEquals(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.dotTimesEquals(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.dotTimesEquals(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.dotTimesEquals(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.equals(this);
        // No exceptions thrown

        // v1.equals(v3, effectiveZero);
        // No exceptions thrown

        // v1.euclideanDistance(v3);
        try
        {
            v1.euclideanDistance(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.euclideanDistance(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.euclideanDistance(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.euclideanDistance(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.euclideanDistanceSquared(v3);
        try
        {
            v1.euclideanDistanceSquared(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.euclideanDistanceSquared(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.euclideanDistanceSquared(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.euclideanDistanceSquared(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.getDimensionality();
        // No exceptions thrown

        // v1.getElement(index);
        try
        {
            v3.getElement(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v3.getElement(9);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v1.getElement(-1);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v1.getElement(14);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // v1.isUnitVector();
        // No exceptions thrown

        // v1.isUnitVector(tolerance);
        // No exceptions thrown

        // v1.isZero();
        // No exceptions thrown

        // v1.isZero(effectiveZero);
        // No exceptions thrown

        // v1.iterator();
        try
        {
            v1.iterator().remove();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        {
            // correct path
        }
        try
        {
            v3.iterator().remove();
            assertTrue(false);
        }
        catch (UnsupportedOperationException e)
        {
            // correct path
        }

        // v1.minus(v3);
        try
        {
            v1.minus(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.minus(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.minus(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.minus(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.minusEquals(v3);
        try
        {
            v1.minusEquals(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.minusEquals(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.minusEquals(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.minusEquals(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }
        // v1.negative();
        // No exceptions thrown

        // v1.negativeEquals();
        // No exceptions thrown

        // v1.norm(power);
        // No exceptions thrown

        // v1.norm1();
        // No exceptions thrown

        // v1.norm2();
        // No exceptions thrown

        // v1.norm2Squared();
        // No exceptions thrown

        // v1.normInfinity();
        // No exceptions thrown

        // v1.outerProduct(v3);

        // v1.plus(v3);
        try
        {
            v1.plus(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.plus(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.plus(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.plus(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.plusEquals(v3);
        try
        {
            v1.plusEquals(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v1.plusEquals(v3);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.plusEquals(v2);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        try
        {
            v3.plusEquals(v4);
            assertFalse(true);
        }
        catch (DimensionalityMismatchException e)
        {
        }

        // v1.scale(scaleFactor);
        // No exceptions thrown

        // v1.scaleEquals(scaleFactor);
        // No exceptions thrown

        // v1.setElement(index, value);
        try
        {
            v3.setElement(-1, 3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v3.setElement(9, 3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v1.setElement(-1, 3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v1.setElement(14, 3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // v1.subVector(minIndex, maxIndex);
        try
        {
            v3.subVector(-1, 3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v3.subVector(9, 3);
            assertFalse(true);
        }
        catch (NegativeArraySizeException e)
        {
        }

        try
        {
            v3.subVector(3, 12);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v1.subVector(-1, 3);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        try
        {
            v1.subVector(14, 3);
            assertFalse(true);
        }
        catch (NegativeArraySizeException e)
        {
        }

        try
        {
            v1.subVector(3, 20);
            assertFalse(true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }

        // v1.sum();
        // No exceptions thrown

        // v1.times(null);
        // Tested in MatrixCorrectnessTests

        // v1.toString();
        // No exceptions thrown

        // v1.toString(null);
        // No exceptions thrown

        // v1.toString(null, null);
        // No exceptions thrown

        // v1.unitVector();
        // No exceptions thrown

        // v1.unitVectorEquals();
        // No exceptions thrown

        // v1.zero();
        // No exceptions thrown
    }

}
