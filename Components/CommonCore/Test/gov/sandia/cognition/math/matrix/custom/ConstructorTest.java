/*
 * File:                ConstructorTest.java
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

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.math.matrix.custom.SparseVector;
import gov.sandia.cognition.math.matrix.custom.CustomDenseVectorFactory;
import gov.sandia.cognition.math.matrix.custom.CustomSparseVectorFactory;
import gov.sandia.cognition.math.matrix.custom.CustomDenseMatrixFactory;
import gov.sandia.cognition.math.matrix.custom.CustomSparseMatrixFactory;
import gov.sandia.cognition.math.matrix.custom.DenseVector;
import gov.sandia.cognition.math.matrix.custom.SparseMatrix;
import gov.sandia.cognition.math.matrix.custom.CustomDiagonalMatrixFactory;
import gov.sandia.cognition.math.matrix.custom.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.custom.DenseMatrix;
import gov.sandia.cognition.io.XStreamSerializationHandler;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector1D;
import gov.sandia.cognition.math.matrix.Vector2D;
import gov.sandia.cognition.math.matrix.Vector3D;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import gov.sandia.cognition.testutil.MatrixUtil;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests all constructors for the various types. The only real thing to test is
 * that the correct values are loaded after the constructor is called.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class ConstructorTest
{

    /**
     * Fills a matrix with a set of values
     *
     * @param m The matrix to load
     * @param vals The values to load it with (must be the same dims as m)
     */
    private static void fillMatrix(Matrix m,
        double[][] vals)
    {
        for (int i = 0; i < vals.length; ++i)
        {
            for (int j = 0; j < vals[i].length; ++j)
            {
                m.setElement(i, j, vals[i][j]);
            }
        }
    }

    /**
     * Tests that the matrices are correctly created by their constructors and
     * factories.
     */
    @Test
    public void createMatrices()
    {
        double[][] data =
        {
            {
                0, 0, 1, 0, 1
            },
            {
                1, 0, 1, 1, 1
            },
            {
                1, 0, 1, 1, 1
            }
        };
        List<List<Double>> dataAsList = new ArrayList<List<Double>>(data.length);
        for (double[] row : data)
        {
            List<Double> rowAsList = new ArrayList<Double>(row.length);
            for (double datum : row)
            {
                rowAsList.add(datum);
            }
            dataAsList.add(rowAsList);
        }
        SparseMatrix sm1 = new SparseMatrix(data.length, data[0].length);
        MatrixUtil.testMatrixEquals(sm1, SparseMatrix.class, data.length,
            data[0].length, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        fillMatrix(sm1, data);
        MatrixUtil.testMatrixEquals(sm1, SparseMatrix.class, data.length,
            data[0].length, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1);
        DenseMatrix dm1 = new DenseMatrix(data.length, data[0].length);
        MatrixUtil.testMatrixEquals(dm1, DenseMatrix.class, data.length,
            data[0].length, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        fillMatrix(dm1, data);
        assertTrue(sm1.equals(dm1));
        DenseMatrix dm2 = new DenseMatrix(data);
        assertTrue(dm2.equals(dm1));
        DenseMatrix dm3 = new DenseMatrix(dataAsList);
        assertTrue(dm3.equals(dm1));
        DenseMatrix dm4 = new DenseMatrix(dm3);
        assertTrue(dm4.equals(dm1));
        DenseMatrix dm5 = new DenseMatrix(sm1);
        assertTrue(dm5.equals(dm1));
        DenseMatrix dm6 = new DenseMatrix(2, 2, 1);
        MatrixUtil.testMatrixEquals(dm6, DenseMatrix.class, 2, 2, 1, 1, 1, 1);
        SparseMatrix sm2 = new SparseMatrix(dm3);
        assertTrue(sm2.equals(dm1));
        SparseMatrix sm3 = new SparseMatrix(sm2);
        assertTrue(sm3.equals(dm1));
        DiagonalMatrix di1 = new DiagonalMatrix(data[1]);
        MatrixUtil.testMatrixEquals(di1, DiagonalMatrix.class, 5, 5, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1);
        SparseMatrix sm4 = new SparseMatrix(di1);
        assertTrue(sm4.equals(di1));
        DiagonalMatrix di2 = new DiagonalMatrix(di1);
        assertTrue(di2.equals(di1));
        DiagonalMatrix di3 = new DiagonalMatrix(sm4);
        assertTrue(di3.equals(di1));
        DiagonalMatrix di4 = new DiagonalMatrix(5);
        MatrixUtil.testMatrixEquals(di4, DiagonalMatrix.class, 5, 5, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        di4.setElement(0, 0, 1);
        di4.setElement(2, 2, 1);
        di4.setElement(3, 3, 1);
        di4.setElement(4, 4, 1);
        assertTrue(di4.equals(di1));
        try
        {
            // Creating a non-square diagonal matrix fails
            DiagonalMatrix di5 = new DiagonalMatrix(sm1);
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            // Creating a square, non-diagonal diagonal matrix fails
            DiagonalMatrix di10 =
                new DiagonalMatrix(sm1.getSubMatrix(0, 2, 0, 2));
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        CustomDenseMatrixFactory df = new CustomDenseMatrixFactory();
        DenseMatrix dm7 = df.createMatrix(data.length, data[0].length);
        MatrixUtil.testMatrixEquals(dm7, DenseMatrix.class, data.length,
            data[0].length, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        fillMatrix(dm7, data);
        assertTrue(dm7.equals(dm1));
        DenseMatrix dm8 = df.copyMatrix(dm7);
        assertTrue(dm8.equals(dm1));
        DenseMatrix dm9 = df.copyMatrix(sm1);
        assertTrue(dm9.equals(dm1));
        DenseMatrix dm10 = df.copyMatrix(di4);
        assertTrue(dm10.equals(di1));
        DenseMatrix dm11 = df.createMatrix(0, 0);
        MatrixUtil.testMatrixEquals(dm11, DenseMatrix.class, 0, 0);
        double[][] nulld =
        {
        };
        DenseMatrix dm12 = new DenseMatrix(nulld);
        MatrixUtil.testMatrixEquals(dm12, DenseMatrix.class, 0, 0);
        List<List<Double>> nulll = new ArrayList<List<Double>>();
        DenseMatrix dm13 = new DenseMatrix(nulll);
        MatrixUtil.testMatrixEquals(dm13, DenseMatrix.class, 0, 0);
        DenseMatrix dm14 = new DenseMatrix(0, 0, true);
        MatrixUtil.testMatrixEquals(dm14, DenseMatrix.class, 0, 0);

        CustomSparseMatrixFactory sf = new CustomSparseMatrixFactory();
        SparseMatrix sm5 = sf.createMatrix(data.length, data[0].length);
        MatrixUtil.testMatrixEquals(sm5, SparseMatrix.class, data.length,
            data[0].length, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        fillMatrix(sm5, data);
        assertTrue(sm5.equals(dm1));
        SparseMatrix sm6 = sf.copyMatrix(dm10);
        assertTrue(sm6.equals(di1));
        SparseMatrix sm7 = sf.copyMatrix(sm4);
        assertTrue(sm7.equals(di1));
        SparseMatrix sm8 = sf.copyMatrix(di4);
        assertTrue(sm8.equals(di1));
        SparseMatrixFactoryMTJ sfmtj = new SparseMatrixFactoryMTJ();
        Matrix mtjm = sfmtj.createMatrix(2, 4);
        mtjm.setElement(0, 1, 4);
        mtjm.setElement(1, 3, 2);
        SparseMatrix sm9 = sf.copyMatrix(mtjm);
        MatrixUtil.testMatrixEquals(sm9, SparseMatrix.class, 2, 4, 0, 4, 0, 0, 0,
            0, 0, 2);

        CustomDiagonalMatrixFactory dif =
            new CustomDiagonalMatrixFactory();
        DiagonalMatrix di6 = dif.createMatrix(5, 5);
        MatrixUtil.testMatrixEquals(di6, DiagonalMatrix.class, 5, 5, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        di6.convertFromVector(di4.convertToVector());
        assertTrue(di6.equals(di1));
        try
        {
            dif.createMatrix(2, 3);
            assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            // this is the correct path
        }
        DiagonalMatrix di7 = dif.copyMatrix(dm10);
        assertTrue(di7.equals(di1));
        DiagonalMatrix di8 = dif.copyMatrix(sm8);
        assertTrue(di8.equals(di1));
        DiagonalMatrix di9 = dif.copyMatrix(di8);
        assertTrue(di9.equals(di1));
    }

    /**
     * Tests that the vectors are correctly created by their constructors and
     * factories.
     */
    @Test
    public void createVectors()
    {
        // Get the 1D, 2D, and 3D vectors out of the way
        CustomSparseVectorFactory sf = new CustomSparseVectorFactory();
        Vector1D v1 = sf.createVector1D(0);
        MatrixUtil.testVectorEquals(v1,
            CustomDenseVectorFactory.DenseVector1D.class, 1, 0);
        Vector2D v2 = sf.createVector2D(0, 1);
        MatrixUtil.testVectorEquals(v2,
            CustomDenseVectorFactory.DenseVector2D.class, 2, 0, 1);
        Vector3D v3 = sf.createVector3D(0, 1, 2);
        MatrixUtil.testVectorEquals(v3,
            CustomDenseVectorFactory.DenseVector3D.class, 3, 0, 1, 2);
        CustomDenseVectorFactory df = new CustomDenseVectorFactory();
        v1 = df.createVector1D(0);
        MatrixUtil.testVectorEquals(v1,
            CustomDenseVectorFactory.DenseVector1D.class, 1, 0);
        assertEquals(0, v1.getX(), 1e-12);
        v1.setX(1);
        assertEquals(1, v1.getX(), 1e-12);
        v2 = df.createVector2D(0, 1);
        MatrixUtil.testVectorEquals(v2,
            CustomDenseVectorFactory.DenseVector2D.class, 2, 0, 1);
        assertEquals(0, v2.getX(), 1e-12);
        v2.setX(1);
        assertEquals(1, v2.getX(), 1e-12);
        assertEquals(1, v2.getY(), 1e-12);
        v2.setY(2);
        assertEquals(2, v2.getY(), 1e-12);
        v2.setXY(2, 3);
        assertEquals(2, v2.getFirst(), 1e-12);
        assertEquals(3, v2.getSecond(), 1e-12);
        v3 = df.createVector3D(0, 1, 2);
        MatrixUtil.testVectorEquals(v3,
            CustomDenseVectorFactory.DenseVector3D.class, 3, 0, 1, 2);
        assertEquals(0, v3.getX(), 1e-12);
        v3.setX(1);
        assertEquals(1, v3.getX(), 1e-12);
        assertEquals(1, v3.getY(), 1e-12);
        v3.setY(2);
        assertEquals(2, v3.getY(), 1e-12);
        assertEquals(2, v3.getZ(), 1e-12);
        v3.setZ(3);
        assertEquals(3, v3.getZ(), 1e-12);
        v3.setXYZ(2, 3, 4);
        assertEquals(2, v3.getFirst(), 1e-12);
        assertEquals(3, v3.getSecond(), 1e-12);
        assertEquals(4, v3.getThird(), 1e-12);

        double[] vals =
        {
            0, 3, 0, 1
        };
        List<Double> valsAsList = new ArrayList<Double>();
        for (double val : vals)
        {
            valsAsList.add(val);
        }
        SparseVector s1 = new SparseVector(4);
        MatrixUtil.testVectorEquals(s1, SparseVector.class, 4, 0, 0, 0, 0);
        s1.setElement(1, 3);
        s1.setElement(3, 1);
        MatrixUtil.testVectorEquals(s1, SparseVector.class, 4, 0, 3, 0, 1);
        SparseVector s2 = new SparseVector(s1);
        assertTrue(s2.equals(s1));
        DenseVector d1 = new DenseVector(4);
        MatrixUtil.testVectorEquals(d1, DenseVector.class, 4, 0, 0, 0, 0);
        d1.setElement(1, 3);
        d1.setElement(3, 1);
        assertTrue(d1.equals(s1));
        SparseVector s3 = new SparseVector(d1);
        assertTrue(s3.equals(s1));
        DenseVector d2 = new DenseVector(d1);
        assertTrue(d2.equals(s1));
        DenseVector d3 = new DenseVector(valsAsList);
        assertTrue(d3.equals(s1));
        DenseVector d4 = new DenseVector(vals);
        assertTrue(d4.equals(s1));
        DenseVector d5 = new DenseVector(4, 1);
        MatrixUtil.testVectorEquals(d5, DenseVector.class, 4, 1, 1, 1, 1);

        SparseVector s4 = sf.createVector(4);
        MatrixUtil.testVectorEquals(s4, SparseVector.class, 4, 0, 0, 0, 0);
        DenseVector d6 = df.createVector(4);
        MatrixUtil.testVectorEquals(d6, DenseVector.class, 4, 0, 0, 0, 0);
        SparseVector s5 = sf.copyVector(s2);
        assertTrue(s5.equals(s1));
        SparseVector s6 = sf.copyVector(d1);
        assertTrue(s6.equals(s1));
        DenseVectorFactoryMTJ dfm = new DenseVectorFactoryMTJ();
        Vector mtjv = dfm.copyArray(vals);
        SparseVector s7 = sf.copyVector(mtjv);
        assertTrue(s7.equals(s1));
        DenseVector d7 = df.copyVector(s2);
        assertTrue(d7.equals(s1));
        DenseVector d8 = df.copyVector(d1);
        assertTrue(d8.equals(s1));
    }

    /**
     * Converts s into its serialized version, de-serializes it, and makes sure
     * the result equals s.
     *
     * @param <Type> The type that will be serialized, then deserialized
     * @param s The object to serialize and deserialize to ensure correctness
     * @throws IOException On serialization/deserialization issues
     */
    private static <Type extends Serializable> void toAndFromStream(Type s)
        throws IOException
    {
        final StringWriter w = new StringWriter();
        XStreamSerializationHandler.write(w, s);
        w.close();
        final StringReader r = new StringReader(w.toString());
        Object o = XStreamSerializationHandler.read(r);
        if (!(s.getClass().isInstance(o)))
        {
            assertFalse("The deserialized object doesn't match the pre-"
                + "serialized object", true);
        }
        @SuppressWarnings("unchecked")
        final Type rebuilt = (Type) s.getClass().cast(o);
        r.close();
        assertTrue(s.equals(rebuilt));
        assertTrue(rebuilt.equals(s));
    }

    /**
     * Creates instances of each of the matrix and vector types, then serializes
     * and deserializes them to make sure they are still the same after
     * deserialization.
     *
     * @throws IOException on errors during serialization/deserialization.
     */
    @Test
    public void serializationTest()
        throws IOException
    {
        double[] vals =
        {
            0, 3, 0, 1
        };
        DenseVector dv = new DenseVector(vals);
        SparseVector sv = new SparseVector(dv);
        double[][] mat =
        {
            {
                0, 1, 2
            },
            {
                3, 4, 5
            }
        };
        DenseMatrix dm = new DenseMatrix(mat);
        SparseMatrix sm = new SparseMatrix(dm);
        DiagonalMatrix dia = new DiagonalMatrix(vals);

        toAndFromStream(dv);
        toAndFromStream(sv);
        toAndFromStream(dm);
        toAndFromStream(sm);
        toAndFromStream(dia);
    }

}
