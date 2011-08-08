/*
 * File:                TermVectorSimilarityNetworkCreatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 18, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.relation;

import gov.sandia.cognition.text.term.vector.CosineSimilarityFunction;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import gov.sandia.cognition.text.relation.SimilarityFunction;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermIndex;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class TermVectorSimilarityNetworkCreator
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class TermVectorSimilarityNetworkCreatorTest
{
    /**
     * Creates a new test.
     */
    public TermVectorSimilarityNetworkCreatorTest()
    {
    }

    /**
     * Test of constructors of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testConstructors()
    {
        SimilarityFunction<? super Vector, ? super Vector>
            similarityFunction = CosineSimilarityFunction.getInstance();
        double effectiveZero = 0.0;
        MatrixFactory<?> matrixFactory = MatrixFactory.getDefault();
        TermVectorSimilarityNetworkCreator instance =
            new TermVectorSimilarityNetworkCreator();
        assertSame(similarityFunction, instance.getSimilarityFunction());
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);
        assertSame(matrixFactory, instance.getMatrixFactory());

        similarityFunction = new CosineSimilarityFunction();
        instance = new TermVectorSimilarityNetworkCreator(similarityFunction);
        assertSame(similarityFunction, instance.getSimilarityFunction());
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);
        assertSame(matrixFactory, instance.getMatrixFactory());

        effectiveZero = 0.1;
        instance = new TermVectorSimilarityNetworkCreator(similarityFunction, effectiveZero);
        assertSame(similarityFunction, instance.getSimilarityFunction());
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);
        assertSame(matrixFactory, instance.getMatrixFactory());

        matrixFactory = new SparseMatrixFactoryMTJ();
        instance = new TermVectorSimilarityNetworkCreator(similarityFunction, effectiveZero, matrixFactory);
        assertSame(similarityFunction, instance.getSimilarityFunction());
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);
        assertSame(matrixFactory, instance.getMatrixFactory());
    }

    /**
     * Test of create method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testCreate()
    {
        @PublicationReference(
            author={"Thomas K. Landauer", "Peter W. Foltz", "Darrell Laham"},
            title="An Introduction to Latent Semantic Analysis",
            year=1998,
            type=PublicationType.Journal,
            publication="Discourse Processes",
            pages={259, 284},
            url="http://lsa.colorado.edu/papers/dp1.LSAintro.pdf",
            notes="This is the paper that had the following example data."
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
        double[][] similarityValues = new double[][] {
            {1.00, 0.50, 0.50, 0.00, 0.58, 0.00, 0.00, 0.50, 0.00, 0.00, 0.00, 0.00},
            {0.50, 1.00, 0.50, 0.41, 0.29, 0.00, 0.00, 0.50, 0.00, 0.00, 0.00, 0.00},
            {0.50, 0.50, 1.00, 0.41, 0.29, 0.50, 0.50, 0.00, 0.50, 0.00, 0.00, 0.00},
            {0.00, 0.41, 0.41, 1.00, 0.47, 0.82, 0.82, 0.41, 0.41, 0.00, 0.00, 0.00},
            {0.58, 0.29, 0.29, 0.47, 1.00, 0.29, 0.29, 0.87, 0.29, 0.00, 0.00, 0.00},
            {0.00, 0.00, 0.50, 0.82, 0.29, 1.00, 1.00, 0.00, 0.50, 0.00, 0.00, 0.00},
            {0.00, 0.00, 0.50, 0.82, 0.29, 1.00, 1.00, 0.00, 0.50, 0.00, 0.00, 0.00},
            {0.50, 0.50, 0.00, 0.41, 0.87, 0.00, 0.00, 1.00, 0.00, 0.00, 0.00, 0.00},
            {0.00, 0.00, 0.50, 0.41, 0.29, 0.50, 0.50, 0.00, 1.00, 0.00, 0.41, 0.50},
            {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1.00, 0.67, 0.41},
            {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.41, 0.67, 1.00, 0.82},
            {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.50, 0.41, 0.82, 1.00}
        };
        Matrix expectedSimilarities = MatrixFactory.getDefault().copyArray(similarityValues);
        String[] termNames = { 
            "human", "interface", "computer", "user", "system", "response",
            "time", "EPS", "survey", "trees", "graph", "minors"
        };
        DefaultTermIndex termIndex = new DefaultTermIndex();
        for (String name : termNames)
        {
            termIndex.add(new DefaultTerm(name));
        }

        ArrayList<Vector> documents = new ArrayList<Vector>(data.length);
        for (double[] d : data)
        {
            documents.add(VectorFactory.getDefault().copyArray(d));
        }
        TermVectorSimilarityNetworkCreator instance =
            new TermVectorSimilarityNetworkCreator(new CosineSimilarityFunction());

        int termCount = termIndex.getTermCount();

        MatrixBasedTermSimilarityNetwork result = instance.create(documents,
            termIndex);
        assertSame(termIndex, result.getTermIndex());
        assertTrue(expectedSimilarities.equals(result.getSimilarities(), 0.01));

        double effectiveZero = 0.5;
        instance.setEffectiveZero(effectiveZero);
        for (int i = 0; i < termCount; i++)
        {
            for (int j = 0; j < termCount; j++)
            {
                if (expectedSimilarities.getElement(i, j) <= effectiveZero)
                {
                    expectedSimilarities.setElement(i, j, 0.0);
                }
            }
        };

        result = instance.create(documents,
            termIndex);

        assertSame(termIndex, result.getTermIndex());
        assertTrue(expectedSimilarities.equals(result.getSimilarities(), 0.01));
    }

    /**
     * Test of getSimilarityFunction method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testGetSimilarityFunction()
    {
        this.testSetSimilarityFunction();
    }

    /**
     * Test of setSimilarityFunction method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testSetSimilarityFunction()
    {
        SimilarityFunction<? super Vector, ? super Vector>
            similarityFunction = CosineSimilarityFunction.getInstance();
        TermVectorSimilarityNetworkCreator instance =
            new TermVectorSimilarityNetworkCreator();
        assertSame(similarityFunction, instance.getSimilarityFunction());

        similarityFunction = new CosineSimilarityFunction();
        instance.setSimilarityFunction(similarityFunction);
        assertSame(similarityFunction, instance.getSimilarityFunction());
    }

    /**
     * Test of getEffectiveZero method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testGetEffectiveZero()
    {
        this.testSetEffectiveZero();
    }

    /**
     * Test of setEffectiveZero method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testSetEffectiveZero()
    {
        double effectiveZero = 0.0;
        TermVectorSimilarityNetworkCreator instance =
            new TermVectorSimilarityNetworkCreator();
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);

        effectiveZero = 0.1;
        instance.setEffectiveZero(effectiveZero);
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);
        
        effectiveZero = 0.2;
        instance.setEffectiveZero(effectiveZero);
        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            instance.setEffectiveZero(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        assertEquals(effectiveZero, instance.getEffectiveZero(), 0.0);
    }

    /**
     * Test of getMatrixFactory method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testGetMatrixFactory()
    {
        this.testSetMatrixFactory();
    }

    /**
     * Test of setMatrixFactory method, of class TermVectorSimilarityNetworkCreator.
     */
    @Test
    public void testSetMatrixFactory()
    {
        MatrixFactory<?> matrixFactory = MatrixFactory.getDefault();
        TermVectorSimilarityNetworkCreator instance =
            new TermVectorSimilarityNetworkCreator();
        assertSame(matrixFactory, instance.getMatrixFactory());

        matrixFactory = new SparseMatrixFactoryMTJ();
        instance.setMatrixFactory(matrixFactory);
        assertSame(matrixFactory, instance.getMatrixFactory());

        matrixFactory = new DenseMatrixFactoryMTJ();
        instance.setMatrixFactory(matrixFactory);
        assertSame(matrixFactory, instance.getMatrixFactory());

        matrixFactory = null;
        instance.setMatrixFactory(matrixFactory);
        assertSame(matrixFactory, instance.getMatrixFactory());
    }

}