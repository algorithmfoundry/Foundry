/*
 * File:                KernelPrincipalComponentsAnalysisTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright December 21, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.decomposition.SingularValueDecomposition;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class KernelPrincipalComponentsAnalysis.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class KernelPrincipalComponentsAnalysisTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public KernelPrincipalComponentsAnalysisTest(
        String testName)
    {
        super(testName);
    }

    /** The random number generator for the tests. */
    Random random = new Random(4);

    public int INPUT_DIM = random.nextInt(3) + 3;

    public int OUTPUT_DIM = random.nextInt(2) + 1;


    /**
     * Test of constructors of class KernelPrincipalComponentsAnalysis.
     */
    public void testConstants()
    {
        assertTrue(KernelPrincipalComponentsAnalysis.DEFAULT_COMPONENT_COUNT > 0);
        assertTrue(KernelPrincipalComponentsAnalysis.DEFAULT_CENTER_DATA);
    }
    
    /**
     * Test of constructors of class KernelPrincipalComponentsAnalysis.
     */
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        int componentCount = KernelPrincipalComponentsAnalysis.DEFAULT_COMPONENT_COUNT;
        boolean centerData = KernelPrincipalComponentsAnalysis.DEFAULT_CENTER_DATA;
        KernelPrincipalComponentsAnalysis<Vector> instance =
            new KernelPrincipalComponentsAnalysis<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(componentCount, instance.getComponentCount());

        kernel = new PolynomialKernel(4, 3.0);
        componentCount = random.nextInt(1000);
        instance =
            new KernelPrincipalComponentsAnalysis<Vector>(kernel, componentCount);
        assertSame(kernel, instance.getKernel());
        assertEquals(componentCount, instance.getComponentCount());
    }

    /**
     * Test of learn method, of class KernelPrincipalComponentsAnalysis.
     */
    public void testLearn()
    {
        System.out.println("PCA.learn");

        int num = random.nextInt(100) + 10;
        ArrayList<Vector> data = new ArrayList<Vector>(num);
        final double r1 = random.nextDouble();
        final double r2 = r1 / random.nextDouble();
        for (int i = 0; i < num; i++)
        {
            data.add(VectorFactory.getDefault().createUniformRandom(INPUT_DIM,
                r1, r2, random));
        }

        System.out.println("Data:");
        for (Vector item : data)
        {
            System.out.println(item.getElement(0) +" "+item.getElement(1));
        }

        Vector mean = MultivariateStatisticsUtil.computeMean(data);

        System.out.println("Mean: " + mean);
        DenseMatrix X = DenseMatrixFactoryMTJ.INSTANCE.createMatrix(INPUT_DIM,
            num);

        for (int n = 0; n < num; n++)
        {
            X.setColumn(n, data.get(n).minus(mean));
        }
        long startsvd = System.currentTimeMillis();
        SingularValueDecomposition svd = SingularValueDecompositionMTJ.create(X);
        long stopsvd = System.currentTimeMillis();

        long startpca = System.currentTimeMillis();
        PrincipalComponentsAnalysis pca =
            new ThinSingularValueDecomposition(OUTPUT_DIM);
        PrincipalComponentsAnalysisFunction fpca = pca.learn(data);
        long stoppca = System.currentTimeMillis();

        long startkpca = System.currentTimeMillis();
        LinearKernel kernel = new LinearKernel();
        KernelPrincipalComponentsAnalysis<Vector> instance =
            new KernelPrincipalComponentsAnalysis<Vector>(kernel, OUTPUT_DIM);

        KernelPrincipalComponentsAnalysis.Function<Vector> f = instance.learn(data);
        long stopkpca = System.currentTimeMillis();

        System.out.println("Uhat:\n"
            + f.getComponents().transpose());
        System.out.println("U:\n" + svd.getU());

        System.out.println("Time taken: SVD = " + (stopsvd - startsvd)
            + ", PCA = " + (stoppca - startpca)
            + ", KPCA = " + (stopkpca - startkpca));

        assertEquals(OUTPUT_DIM, instance.getComponentCount());
        assertEquals(instance.getComponentCount(), f.getOutputDimensionality());

        // The mean should project to zero.
        Vector zeros = VectorFactory.getDefault().createVector(OUTPUT_DIM);
        if (!zeros.equals(f.evaluate(mean), 1e-5))
        {
            assertEquals(zeros, f.evaluate(mean));
        }

        for (int i = 0; i < OUTPUT_DIM; i++)
        {
            Vector alphaI = f.getComponents().getRow(i);
            for (int j = 0; j < i; j++)
            {
                Vector alphaJ = f.getComponents().getRow(j);
                assertEquals( "Dot product between " + i + " and " + j + " is too large!", 0.0,
                    alphaI.dotProduct( alphaJ ), 1e-2 );
            }
            assertTrue(alphaI.norm2() > 0.0);
        }

    }

    /**
     * Test of the learn method on a pre-specified set of data.
     */
    public void testLearnLinearExample()
    {
        ArrayList<Vector> data = new ArrayList<Vector>();
        data.add(new Vector2(2.5, 2.4));
        data.add(new Vector2(0.5, 0.7));
        data.add(new Vector2(2.2, 2.9));
        data.add(new Vector2(1.9, 2.2));
        data.add(new Vector2(3.1, 3.0));
        data.add(new Vector2(2.3, 2.7));
        data.add(new Vector2(2, 1.6));
        data.add(new Vector2(1, 1.1));
        data.add(new Vector2(1.5, 1.6));
        data.add(new Vector2(1.1, 0.9));


        System.out.println("Data:");
        for (Vector item : data)
        {
            System.out.println(item.getElement(0) +" "+item.getElement(1));
        }
        System.out.println();
        
        KernelPrincipalComponentsAnalysis<Vector> kpca =
            new KernelPrincipalComponentsAnalysis<Vector>(new LinearKernel(), 2);
        KernelPrincipalComponentsAnalysis.Function<Vector> fkpca = kpca.learn(data);


        // The mean should project to zero.
        final Vector zeros = new Vector2();
        final Vector mean = MultivariateStatisticsUtil.computeMean(data);
        if (!zeros.equals(fkpca.evaluate(mean), 1e-5))
        {
            assertEquals(zeros, fkpca.evaluate(mean));
        }

        for (int i = 0; i < 2; i++)
        {
            Vector alphaI = fkpca.getComponents().getRow(i);
            for (int j = 0; j < i; j++)
            {
                Vector alphaJ = fkpca.getComponents().getRow(j);
                assertEquals( "Dot product between " + i + " and " + j + " is too large!", 0.0,
                    alphaI.dotProduct( alphaJ ), 1e-2 );
            }
            assertTrue(alphaI.norm2() > 0.0);
        }


        System.out.println("KPCA:");
        for (Vector v : data)
        {
            System.out.println(fkpca.evaluate(v));
        }

        ThinSingularValueDecomposition pca = new ThinSingularValueDecomposition(2);
        PrincipalComponentsAnalysisFunction fpca = pca.learn(data);
        System.out.println("PCA:");
        for (Vector v : data)
        {
            System.out.println(fpca.evaluate(v));
        }
    }

    /**
     * Test of the learn method using a toy example.
     */
    public void testLearnToyExample()
    {
        final Vector2[] clusters = new Vector2[]
        {
            new Vector2(-0.5, -0.2),
            new Vector2(0.0, 0.6),
            new Vector2(0.5, 0.0)
        };

        ArrayList<Vector> data = new ArrayList<Vector>();
        int clusterSize = 30;
        double noise = 0.1;
        for (Vector2 cluster : clusters)
        {
            for (int i = 0; i < clusterSize; i++)
            {
                data.add(VectorFactory.getDenseDefault().copyValues(
                    cluster.getX() + random.nextGaussian() * noise,
                    cluster.getY() + random.nextGaussian() * noise));
            }
        }


        System.out.println("Data:");
        for (Vector item : data)
        {
            System.out.println(item);
        }
        System.out.println();

        KernelPrincipalComponentsAnalysis<Vector> kpca =
            new KernelPrincipalComponentsAnalysis<Vector>(new LinearKernel(), 2);
        KernelPrincipalComponentsAnalysis.Function<Vector> fkpca = kpca.learn(data);


        // The mean should project to zero.
        final Vector zeros = new Vector2();
        final Vector mean = MultivariateStatisticsUtil.computeMean(data);
        if (!zeros.equals(fkpca.evaluate(mean), 1e-5))
        {
            assertEquals(zeros, fkpca.evaluate(mean));
        }

        for (int i = 0; i < 2; i++)
        {
            Vector alphaI = fkpca.getComponents().getRow(i);
            for (int j = 0; j < i; j++)
            {
                Vector alphaJ = fkpca.getComponents().getRow(j);
                assertEquals( "Dot product between " + i + " and " + j + " is too large!", 0.0,
                    alphaI.dotProduct( alphaJ ), 1e-2 );
            }
            assertTrue(alphaI.norm2() > 0.0);
        }

        System.out.println("KPCA:");
        for (Vector v : data)
        {
            System.out.println(fkpca.evaluate(v));
        }

        ThinSingularValueDecomposition pca = new ThinSingularValueDecomposition(2);
        PrincipalComponentsAnalysisFunction fpca = pca.learn(data);
        System.out.println("PCA:");
        for (Vector v : data)
        {
            System.out.println(fpca.evaluate(v));
        }
    }
 
    /**
     * Test of getComponentCount method, of class KernelPrincipalComponentsAnalysis.
     */
    public void testGetComponentCount()
    {
        this.testSetComponentCount();
    }

    /**
     * Test of setComponentCount method, of class KernelPrincipalComponentsAnalysis.
     */
    public void testSetComponentCount()
    {
        int componentCount =
            KernelPrincipalComponentsAnalysis.DEFAULT_COMPONENT_COUNT;
        KernelPrincipalComponentsAnalysis<String> instance =
            new KernelPrincipalComponentsAnalysis<String>();
        assertEquals(componentCount, instance.getComponentCount());


        componentCount = 1;
        instance.setComponentCount(componentCount);
        assertEquals(componentCount, instance.getComponentCount());


        componentCount = 5;
        instance.setComponentCount(componentCount);
        assertEquals(componentCount, instance.getComponentCount());

        boolean exceptionThrown = false;
        try
        {
            instance.setComponentCount(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(componentCount, instance.getComponentCount());


        exceptionThrown = false;
        try
        {
            instance.setComponentCount(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(componentCount, instance.getComponentCount());
    }

    /**
     * Test of isCenterData method, of class KernelPrincipalComponentsAnalysis.
     */
    public void testIsCenterData()
    {
        this.testSetCenterData();
    }

    /**
     * Test of setCenterData method, of class KernelPrincipalComponentsAnalysis.
     */
    public void testSetCenterData()
    {
        boolean centerData = KernelPrincipalComponentsAnalysis.DEFAULT_CENTER_DATA;
        KernelPrincipalComponentsAnalysis<?> instance =
            new KernelPrincipalComponentsAnalysis<Vector>();
        assertEquals(centerData, instance.isCenterData());

        centerData = false;
        instance.setCenterData(centerData);
        assertEquals(centerData, instance.isCenterData());

        centerData = true;
        instance.setCenterData(centerData);
        assertEquals(centerData, instance.isCenterData());
        
        centerData = false;
        instance.setCenterData(centerData);
        assertEquals(centerData, instance.isCenterData());
    }

    

}
