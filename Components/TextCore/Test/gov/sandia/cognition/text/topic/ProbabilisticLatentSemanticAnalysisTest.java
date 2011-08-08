/*
 * File:                ProbabilisticLatentSemanticAnalysisTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 02, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.topic;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import gov.sandia.cognition.text.topic.ProbabilisticLatentSemanticAnalysis.Transform;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class ProbabilisticLatentSemanticAnalysisTest
{
    /**
     * Creates a new test.
     */
    public ProbabilisticLatentSemanticAnalysisTest()
    {
    }


    /**
     * Test of constructors of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testConstructors()
    {

    }
    /**
     * Test of learn method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testLearn()
    {
        final Random random = new Random(111);
        
        double[][] data = new double[][] {
            { 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0 },
            { 1, 0, 0, 0, 2, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1 }
        };
        // Note: See the comment at the end of the file for some MATLAB code
        // for this data.

        double EPSILON = 0.01;
        NumberFormat format = new DecimalFormat("0.00");
        ArrayList<Vector> documents = new ArrayList<Vector>(data.length);
        for (double[] d : data)
        {
            documents.add(VectorFactory.getDefault().copyArray(d));
        }


        ProbabilisticLatentSemanticAnalysis instance = new ProbabilisticLatentSemanticAnalysis(random);
        instance.addIterativeAlgorithmListener(new ProbabilisticLatentSemanticAnalysis.StatusPrinter());

        int rank = 2;
        instance.setRequestedRank(rank);
        ProbabilisticLatentSemanticAnalysis.Transform result =
            instance.learn(documents);
        assertSame(result, instance.getResult());

        System.out.println("Result:");
        for (ProbabilisticLatentSemanticAnalysis.LatentData latent : result.latents)
        {
            System.out.println("    Latent " + latent.index);
            System.out.println("        p(z)   = " + format.format(latent.pLatent));
            System.out.println("        p(t|z) = " + latent.pTermGivenLatent.toString(format));
            System.out.println("        p(d|z) = " + latent.pDocumentGivenLatent.toString(format));
        }
        
        System.out.println("Transforms:");
        for (Vector document : documents)
        {
            Vector transformed = result.evaluate(document);
            System.out.println("    p(z|q) = " + transformed.toString(format));
        }

        Vector zeros = documents.get(0).clone();
        zeros.zero();
        Vector transformed = result.evaluate(zeros);
        System.out.println("    p(z|0) = " + transformed.toString(format));

        Vector ones = zeros.clone();
        Vector tens = zeros.clone();
        for (int i =0; i < ones.getDimensionality(); i++)
        {
            ones.setElement(i, 1.0);
            tens.setElement(i, 10.0);
        }
        
        transformed = result.evaluate(ones);
        System.out.println("    p(z|1) = " + transformed.toString(format));

        transformed = result.evaluate(tens);
        System.out.println("    p(z|10) = " + transformed.toString(format));

        // TODO: Have this test actually try to assert something.
    }

    /**
     * Test of getResult method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testGetResult()
    {
        // Tested by testLearn.
    }

    /**
     * Test of getRandom method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testGetRandom()
    {
         this.testSetRandom();
    }


    /**
     * Test of setRandom method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testSetRandom()
    {
        ProbabilisticLatentSemanticAnalysis instance =
            new ProbabilisticLatentSemanticAnalysis();
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of getVectorFactory method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testGetVectorFactory()
    {
        VectorFactory<? extends Vector> vectorFactory = VectorFactory.getDefault();
        ProbabilisticLatentSemanticAnalysis instance =
            new ProbabilisticLatentSemanticAnalysis();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = SparseVectorFactory.getDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        
        vectorFactory = null;
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of getMatrixFactory method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testGetMatrixFactory()
    {
        MatrixFactory<? extends Matrix> matrixFactory = MatrixFactory.getDefault();
        ProbabilisticLatentSemanticAnalysis instance =
            new ProbabilisticLatentSemanticAnalysis();
        assertSame(matrixFactory, instance.getMatrixFactory());

        matrixFactory = new SparseMatrixFactoryMTJ();
        instance.setMatrixFactory(matrixFactory);
        assertSame(matrixFactory, instance.getMatrixFactory());

        matrixFactory = null;
        instance.setMatrixFactory(matrixFactory);
        assertSame(matrixFactory, instance.getMatrixFactory());
    }

    /**
     * Test of getRequestedRank method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testGetRequestedRank()
    {
        this.testSetRequestedRank();
    }

    /**
     * Test of setRequestedRank method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testSetRequestedRank()
    {
        int requestedRank = ProbabilisticLatentSemanticAnalysis.DEFAULT_REQUESTED_RANK;
        ProbabilisticLatentSemanticAnalysis instance =
            new ProbabilisticLatentSemanticAnalysis();
        assertEquals(requestedRank, instance.getRequestedRank());

        requestedRank++;
        instance.setRequestedRank(requestedRank);
        assertEquals(requestedRank, instance.getRequestedRank());

        boolean exceptionThrown = false;
        try
        {
            instance.setRequestedRank(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(requestedRank, instance.getRequestedRank());
    }


    /**
     * Test of getMinimumChange method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testGetMinimumChange()
    {
        this.testSetMinimumChange();
    }

    /**
     * Test of setMinimumChange method, of class ProbabilisticLatentSemanticAnalysis.
     */
    @Test
    public void testSetMinimumChange()
    {
        double minimumChange = ProbabilisticLatentSemanticAnalysis.DEFAULT_MINIMUM_CHANGE;
        ProbabilisticLatentSemanticAnalysis instance =
            new ProbabilisticLatentSemanticAnalysis();
        assertEquals(minimumChange, instance.getMinimumChange(), 0.0);

        minimumChange = 0.0;
        instance.setMinimumChange(minimumChange);
        assertEquals(minimumChange, instance.getMinimumChange(), 0.0);

        minimumChange = 2.4;
        instance.setMinimumChange(minimumChange);
        assertEquals(minimumChange, instance.getMinimumChange(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            instance.setMinimumChange(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(minimumChange, instance.getMinimumChange(), 0.0);
    }
}
/*
 * Here is some helpful Octave/MATLAB code used for creating the unit test data:

D = [
1 0 0 1 0 0 0 0 0;
1 0 1 0 0 0 0 0 0;
1 1 0 0 0 0 0 0 0;
0 1 1 0 1 0 0 0 0;
0 1 1 2 0 0 0 0 0;
0 1 0 0 1 0 0 0 0;
0 1 0 0 1 0 0 0 0;
0 0 1 1 0 0 0 0 0;
0 1 0 0 0 0 0 0 1;
0 0 0 0 0 1 1 1 0;
0 0 0 0 0 0 1 1 1;
0 0 0 0 0 0 0 1 1];

TODO: Put in more code for how to actually compute PLSA with the data.
 *
 */