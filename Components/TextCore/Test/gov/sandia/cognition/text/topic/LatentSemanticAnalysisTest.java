/*
 * File:                LatentSemanticAnalysisTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.topic;

import gov.sandia.cognition.math.matrix.mtj.Vector1;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LatentSemanticAnalysis.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class LatentSemanticAnalysisTest
{
    /**
     * Creates a new test.
     */
    public LatentSemanticAnalysisTest()
    {
    }

    /**
     * Test of constructors of class LatentSemanticAnalysis.
     */
    @Test
    public void testConstructors()
    {
        int requestedRank = 10;
        LatentSemanticAnalysis instance = new LatentSemanticAnalysis();
        assertEquals(requestedRank, instance.getRequestedRank());

        requestedRank = 2 * requestedRank + 1;
        instance = new LatentSemanticAnalysis(requestedRank);
        assertEquals(requestedRank, instance.getRequestedRank());
    }

    /**
     * Test of learn method, of class LatentSemanticAnalysis.
     */
    @Test
    public void testLearn()
    {
        @PublicationReference(
            author={"Thomas K. Landauer", "Peter W. Foltz", "Darrell Laham"},
            title="An Introduction to Latent Semantic Analysis",
            year=1998,
            type=PublicationType.Journal,
            publication="Discourse Processes",
            pages={259, 284},
            url="http://lsa.colorado.edu/papers/dp1.LSAintro.pdf",
            notes="This is the paper that had the following LSA example."
        )
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
        NumberFormat format = new DecimalFormat("+0.00;-0.00");

        int dimensionality = data[0].length;

        double[] expectedSingularValues = new double[] {
            3.34, 2.54, 2.35, 1.64, 1.50, 1.31, 0.85, 0.56, 0.36
        };

        double[][] expectedTermBasisValues = new double[][] {
            {+0.22, -0.11, +0.29, -0.41, -0.11, -0.34, +0.52, -0.06, -0.41},
            {+0.20, -0.07, +0.14, -0.55, +0.28, +0.50, -0.07, -0.01, -0.11},
            {+0.24, +0.04, -0.16, -0.59, -0.11, -0.25, -0.30, +0.06, +0.49},
            {+0.40, +0.06, -0.34, +0.10, +0.33, +0.38, +0.00, +0.00, +0.01},
            {+0.64, -0.17, +0.36, +0.33, -0.16, -0.21, -0.17, +0.03, +0.27},
            {+0.27, +0.11, -0.43, +0.07, +0.08, -0.17, +0.28, -0.02, -0.05},
            {+0.27, +0.11, -0.43, +0.07, +0.08, -0.17, +0.28, -0.02, -0.05},
            {+0.30, -0.14, +0.33, +0.19, +0.11, +0.27, +0.03, -0.02, -0.17},
            {+0.21, +0.27, -0.18, -0.03, -0.54, +0.08, -0.47, -0.04, -0.58},
            {+0.01, +0.49, +0.23, +0.03, +0.59, -0.39, -0.29, +0.25, -0.23},
            {+0.04, +0.62, +0.22, +0.00, -0.07, +0.11, +0.16, -0.68, +0.23},
            {+0.03, +0.45, +0.14, -0.01, -0.30, +0.28, +0.34, +0.68, +0.18},
        };

        double[][] expectedDocumentBasisValues = new double[][] {
            {+0.20, +0.61, +0.46, +0.54, +0.28, +0.00, +0.01, +0.02, +0.08},
            {-0.06, +0.17, -0.13, -0.23, +0.11, +0.19, +0.44, +0.62, +0.53},
            {+0.11, -0.50, +0.21, +0.57, -0.51, +0.10, +0.19, +0.25, +0.08},
            {-0.95, -0.03, +0.04, +0.27, +0.15, +0.02, +0.02, +0.01, -0.03},
            {+0.05, -0.21, +0.38, -0.21, +0.33, +0.39, +0.35, +0.15, -0.60},
            {-0.08, -0.26, +0.72, -0.37, +0.03, -0.30, -0.21, +0.00, +0.36},
            {+0.18, -0.43, -0.24, +0.26, +0.67, -0.34, -0.15, +0.25, +0.04},
            {-0.01, +0.05, +0.01, -0.02, -0.06, +0.45, -0.76, +0.45, -0.07},
            {-0.06, +0.24, +0.02, -0.08, -0.26, -0.62, +0.02, +0.52, -0.45}
        };

        double[][] expectedTransformValues = new double[][] {
            {+0.7348, -0.2794, +0.6815, -0.6724, -0.1650, -0.4454, +0.4420, -0.0336, -0.14760},
            {+0.6680, -0.1778, +0.3290, -0.9020, +0.4200, +0.6550, -0.0595, -0.0056, -0.03960},
            {+0.8016, +0.1016, -0.3760, -0.9676, -0.1650, -0.3275, -0.2550, +0.0336, +0.17640},
            {+1.3360, +0.1524, -0.7990, +0.1640, +0.4950, +0.4978, +0.0000, +0.0000, +0.00360},
            {+2.1376, -0.4318, +0.8460, +0.5412, -0.2400, -0.2751, -0.1445, +0.0168, +0.09720},
            {+0.9018, +0.2794, -1.0105, +0.1148, +0.1200, -0.2227, +0.2380, -0.0112, -0.01800},
            {+0.9018, +0.2794, -1.0105, +0.1148, +0.1200, -0.2227, +0.2380, -0.0112, -0.01800},
            {+1.0020, -0.3556, +0.7755, +0.3116, +0.1650, +0.3537, +0.0255, -0.0112, -0.06120},
            {+0.7014, +0.6858, -0.4230, -0.0492, -0.8100, +0.1048, -0.3995, -0.0224, -0.20880},
            {+0.0334, +1.2446, +0.5405, +0.0492, +0.8850, -0.5109, -0.2465, +0.1400, -0.08280},
            {+0.1336, +1.5748, +0.5170, +0.0000, -0.1050, +0.1441, +0.1360, -0.3808, +0.08280},
            {+0.1002, +1.1430, +0.3290, -0.0164, -0.4500, +0.3668, +0.2890, +0.3808, +0.06480}
        };


        Matrix expectedFullTermBasis = MatrixFactory.getDefault().copyArray(
            expectedTermBasisValues);
        Matrix expectedFullTransform = MatrixFactory.getDefault().copyArray(
            expectedTransformValues);

        ArrayList<Vector> documents = new ArrayList<Vector>(data.length);
        for (double[] d : data)
        {
            documents.add(VectorFactory.getDefault().copyArray(d));
        }




        LatentSemanticAnalysis instance = new LatentSemanticAnalysis();

        // Reduced rank LSA
        int rank = 2;
        instance.setRequestedRank(rank);
        LatentSemanticAnalysis.Transform result =
            instance.learn(documents);
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());

        for (int i = 0; i < result.getRank(); i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        Matrix expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 1, 0, result.getRank() - 1);
        Matrix expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 1, 0, result.getRank() - 1);
        for (int i = 0; i < rank; i++)
        {
            Vector expected = expectedTermBasis.getColumn(i);
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }


        // Full rank LSA:
        rank = dimensionality;
        instance.setRequestedRank(rank);
        result = instance.learn(documents);
        rank = Math.min(dimensionality, documents.size());
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());

        for (int i = 0; i < expectedSingularValues.length; i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 1, 0, result.getRank() - 1);
        expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 1, 0, result.getRank() - 1);
        for (int i = 0; i < result.getRank(); i++)
        {
            Vector expected = expectedTermBasis.getColumn(i);
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }

        // Add a zero document vector and learn.
        documents.add(VectorFactory.getDefault().createVector(12));
        documents.add(VectorFactory.getDefault().createVector(12));

        rank = dimensionality;
        instance.setRequestedRank(rank);
        result = instance.learn(documents);
        rank = 9;
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());

        for (int i = 0; i < expectedSingularValues.length; i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 1, 0, result.getRank() - 1);
        expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 1, 0, result.getRank() - 1);
        for (int i = 0; i < result.getRank(); i++)
        {
            Vector expected = expectedTermBasis.getColumn(i);
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }

        // Create a second dataset with a zero column.
        ArrayList<Vector> documents2 = new ArrayList<Vector>();
        for (Vector document : documents)
        {
            documents2.add(document.stack(new Vector1()));
        }

        // Learn from the documents with zeros.
        dimensionality++;
        rank = dimensionality;
        instance.setRequestedRank(rank);
        result = instance.learn(documents2);
        rank = 9;
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());

        for (int i = 0; i < expectedSingularValues.length; i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 2, 0, result.getRank() - 1);
        expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 2, 0, result.getRank() - 1);
        for (int i = 0; i < result.getRank(); i++)
        {
            Vector expected = expectedTermBasis.getColumn(i).stack(new Vector1());
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }

        // Try a small rank and learn.
        rank = 2;
        instance.setRequestedRank(rank);
        result = instance.learn(documents2);
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());


        for (int i = 0; i < result.getRank(); i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 2, 0, result.getRank() - 1);
        expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 2, 0, result.getRank() - 1);
        for (int i = 0; i < rank; i++)
        {
            Vector expected = expectedTermBasis.getColumn(i).stack(new Vector1());
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }

        // Try a large rank and learn.
        rank = 200;
        instance.setRequestedRank(rank);
        result = instance.learn(documents2);
        rank = 9;
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());

        for (int i = 0; i < expectedSingularValues.length; i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 2, 0, result.getRank() - 1);
        expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 2, 0, result.getRank() - 1);
        for (int i = 0; i < result.getRank(); i++)
        {
            Vector expected = expectedTermBasis.getColumn(i).stack(new Vector1());
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }

        // Add a whole lot of zeros and learn.
        documents2.clear();
        for (Vector document : documents)
        {
            documents2.add(VectorFactory.getSparseDefault().copyVector(
                document).stack(VectorFactory.getSparseDefault().createVector(
                    100)));
        }
        // Try a large rank and learn.
        dimensionality += 99;
        rank = 200;
        instance.setRequestedRank(rank);
        result = instance.learn(documents2);
        rank = 9;
        assertEquals(rank, result.getRank());
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(rank, result.getOutputDimensionality());

        for (int i = 0; i < expectedSingularValues.length; i++)
        {
            assertEquals(expectedSingularValues[i],
                result.getSingularValues().getElement(i, i), EPSILON);
        }

        expectedTermBasis = expectedFullTermBasis.getSubMatrix(
            0, dimensionality - 101, 0, result.getRank() - 1);
        expectedTransform = expectedFullTransform.getSubMatrix(
            0, dimensionality - 101, 0, result.getRank() - 1);
        for (int i = 0; i < result.getRank(); i++)
        {
            Vector expected = expectedTermBasis.getColumn(i).stack(VectorFactory.getSparseDefault().createVector(
                100));
            Vector actual = result.getTermBasis().getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));

            expected = expectedTransform.getColumn(i);
            actual = expectedTransform.getColumn(i);
            assertTrue(actual.equals(expected, EPSILON)
                || actual.equals(expected.negative(), EPSILON));
        }

    }


    /**
     * Test of getRequestedRank method, of class LatentSemanticAnalysis.
     */
    @Test
    public void testGetRequestedRank()
    {
        this.testSetRequestedRank();
    }

    /**
     * Test of setRequestedRank method, of class LatentSemanticAnalysis.
     */
    @Test
    public void testSetRequestedRank()
    {
        int requestedRank = 10;
        LatentSemanticAnalysis instance = new LatentSemanticAnalysis();
        assertEquals(requestedRank, instance.getRequestedRank());

        requestedRank = 2 * requestedRank + 1;
        instance.setRequestedRank(requestedRank);
        assertEquals(requestedRank, instance.getRequestedRank());


        requestedRank = 2 * requestedRank + 1;
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

        exceptionThrown = false;
        try
        {
            instance.setRequestedRank(-1);
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

[U, S, V] = svd(D);

Z = U * S * V';

r = 2;
Ur = U(:, 1:r); Sr = S(1:r, 1:r); Vr = V(:, 1:r); Zr = Ur * Sr * Vr';
 */