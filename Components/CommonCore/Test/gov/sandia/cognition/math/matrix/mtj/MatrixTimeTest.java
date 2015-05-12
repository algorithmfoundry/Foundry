/*
 * File:                MatrixTimeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 19, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import java.lang.reflect.Field;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Kevin R. Dixon
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-25",
    changesNeeded=false,
    comments="Added header information"
)
public class MatrixTimeTest
    extends TestCase
{

    protected Random random = new Random(1);
    
    public MatrixTimeTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MatrixTimeTest.class);

        return suite;
    }

    /**
     * 
     */
    public void testMatrixVectorMultiply()
    {

        int n, num = 10;
        int M = 20;
        int N = 15;

        int min = -10;
        int max = 10;
        AbstractMTJVector[] answers = new AbstractMTJVector[num];
        AbstractMTJVector[] answers2 = new AbstractMTJVector[num];
        AbstractMTJVector[] answers3 = new AbstractMTJVector[num];

        DenseVector[] vectors = new DenseVector[num];
        DenseMatrix[] matrices = new DenseMatrix[num];
        SparseRowMatrix[] srow = new SparseRowMatrix[num];
        SparseColumnMatrix[] scol = new SparseColumnMatrix[num];
        for (n = 0; n < num; n++)
        {
            vectors[n] = DenseVectorFactoryMTJ.INSTANCE.createUniformRandom(N, min, max, random);
            matrices[n] = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(M, N, min, max, random);
            srow[n] = new SparseRowMatrix(matrices[n]);
            scol[n] = new SparseColumnMatrix(matrices[n]);
        }

        for (n = 0; n < num; n++)
        {
            assertEquals(matrices[n], srow[n]);
            assertEquals(srow[n], scol[n]);
        }

        double dstart = System.currentTimeMillis() / 1000.0;
        for (n = 0; n < num; n++)
        {
            answers[n] = matrices[n].times(vectors[n]);
        }
        double dstop = System.currentTimeMillis() / 1000.0;

        double scstart = System.currentTimeMillis() / 1000.0;
        for (n = 0; n < num; n++)
        {
            answers2[n] = scol[n].times(vectors[n]);
        }
        double scstop = System.currentTimeMillis() / 1000.0;

        double srstart = System.currentTimeMillis() / 1000.0;
        for (n = 0; n < num; n++)
        {
            answers3[n] = srow[n].times(vectors[n]);
        }
        double srstop = System.currentTimeMillis() / 1000.0;

        for (n = 0; n < num; n++)
        {
            assertTrue(answers[n].equals(answers2[n], 0.00001));
            assertTrue(answers2[n].equals(answers3[n], 0.00001));
        }


        System.out.println("Dense: " + (dstop - dstart));
        System.out.println("Column: " + (scstop - scstart));
        System.out.println("Row: " + (srstop - srstart));
    }

    public void testPinvSolveTime()
    {
        int i;
        int num = 10;
        int M = 20;
        int N = 15;
        double min = -10;
        double max = 15;
        DenseMatrix[] matrices = new DenseMatrix[num];
        for (i = 0; i < matrices.length; i++)
        {
            matrices[i] = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(M, N, min, max, random);
        }

        DenseMatrix ident = DenseMatrixFactoryMTJ.INSTANCE.createIdentity(M, M);

        DenseMatrix[] pinv = new DenseMatrix[num];
        Matrix[] solve = new Matrix[num];

        double pinv_start = System.currentTimeMillis() / 1000.0;

        for (i = 0; i < matrices.length; i++)
        {
            pinv[i] = (DenseMatrix) matrices[i].pseudoInverse();
        }

        double pinv_stop = System.currentTimeMillis() / 1000.0;
        double pinv_tt = 1000.0 * (pinv_stop - pinv_start) / num;

        System.out.println("PINV Time: " + pinv_tt +
            " (" + pinv[0].getNumRows() + "," + pinv[0].getNumColumns() + ")");

        double solve_start = System.currentTimeMillis() / 1000.0;

        for (i = 0; i < matrices.length; i++)
        {
            solve[i] = matrices[i].solve(ident);
        }

        double solve_stop = System.currentTimeMillis() / 1000.0;
        double solve_tt = 1000.0 * (solve_stop - solve_start) / num;

        System.out.println("Solve Time: " + solve_tt +
            " (" + solve[0].getNumRows() + "," + solve[0].getNumColumns() + ")");

    }

    public static void testBlas() throws Exception
    {
        Class<?> c = Class.forName("com.github.fommil.netlib.BLAS");
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields)
        {
            f.setAccessible(true);
            Object o = f.get(null);
            System.out.println("Field: " + f);
            System.out.println("Object: " + o.getClass().getName());
        }


    }


    public static void testLapack() throws Exception
    {
        Class<?> c = Class.forName("com.github.fommil.netlib.LAPACK");
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields)
        {
            f.setAccessible(true);
            Object o = f.get(null);
            System.out.println("Field: " + f);
            System.out.println("Object: " + o.getClass().getName());
        }


    }
    public void testDeterminantTime()
    {
        System.out.println("DeterminantTime");

        int num = 10;
        int M = 10;
        int N = 10;
        double min = -10;
        double max = 15;
        DenseMatrix[] matrices = new DenseMatrix[num];
        for (int i = 0; i < matrices.length; i++)
        {
            matrices[i] = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom(M, N, min, max, random);
        }


        ComplexNumber[] d1 = new ComplexNumber[matrices.length];

        double d1_start = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < matrices.length; i++)
        {
            d1[i] = matrices[i].logDeterminant();
        }
        double d1_stop = System.currentTimeMillis() / 1000.0;

        ComplexNumber[] d2 = new ComplexNumber[matrices.length];

        double d2_start = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < matrices.length; i++)
        {
            d2[i] = EigenDecompositionRightMTJ.create(matrices[i]).getLogDeterminant();
        }
        double d2_stop = System.currentTimeMillis() / 1000.0;

        double d1_time = d1_stop - d1_start;
        double d2_time = d2_stop - d2_start;
        System.out.println("Matrix determinant: " + d1_time);
        System.out.println("EVD determinant: " + d2_time);

        for (int i = 0; i < matrices.length; i++)
        {

            if (Math.abs(d1[i].getRealPart() - d2[i].getRealPart()) > 1e-4)
            {
                System.out.println("Problem: d1 = " + d1[i] + ", d2 = " + d2[i]);
                System.out.println("Matrix: ");
                System.out.println(matrices[i]);
            }
            assertEquals(d1[i].getRealPart(), d2[i].getRealPart(), 1e-4);

        }

    }

}
