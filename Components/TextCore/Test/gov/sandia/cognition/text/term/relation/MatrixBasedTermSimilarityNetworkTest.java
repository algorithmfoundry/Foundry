/*
 * File:                MatrixBasedTermSimilarityNetworkTest.java
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

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.text.term.DefaultIndexedTerm;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermIndex;
import gov.sandia.cognition.text.term.IndexedTerm;
import gov.sandia.cognition.text.term.Term;
import java.util.LinkedList;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class MatrixBasedTermSimilarityNetwork.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class MatrixBasedTermSimilarityNetworkTest
{
    protected DefaultTerm termA = new DefaultTerm("a");
    protected DefaultTerm termB = new DefaultTerm("b");
    protected DefaultTerm termC = new DefaultTerm("c");
    protected DefaultTerm termD = new DefaultTerm("d");

    protected IndexedTerm indexedTermA;
    protected IndexedTerm indexedTermB;
    protected IndexedTerm indexedTermC;
    protected IndexedTerm indexedTermD;

    protected DefaultTermIndex defaultTermIndex;
    protected Term[] exampleTerms;
    protected IndexedTerm[] exampleIndexedTerms;
    protected double[][] exampleSimilarityValues;
    protected Matrix exampleSimilarities;

    /**
     * Creates a new test.
     */
    public MatrixBasedTermSimilarityNetworkTest()
    {
        this.defaultTermIndex = new DefaultTermIndex();
        this.indexedTermA = this.defaultTermIndex.add(termA);
        this.indexedTermB = this.defaultTermIndex.add(termB);
        this.indexedTermC = this.defaultTermIndex.add(termC);
        this.indexedTermD = this.defaultTermIndex.add(termD);

        this.exampleTerms = new Term[] { termA, termB, termC, termD };
        this.exampleIndexedTerms = new IndexedTerm[] { indexedTermA, indexedTermB, indexedTermC, indexedTermD };

        this.exampleSimilarityValues = new double[][] {
            { 1.0, 0.5, 0.0, 0.0 },
            { 0.5, 1.0, 0.1, 0.0 },
            { 1.0, 0.1, 1.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0 }
        };

        this.exampleSimilarities = MatrixFactory.getDefault().copyArray(
            this.exampleSimilarityValues);
    }

    public MatrixBasedTermSimilarityNetwork createExampleNetwork()
    {
        DefaultTermIndex termIndex = this.defaultTermIndex;

        return new MatrixBasedTermSimilarityNetwork(termIndex, exampleSimilarities);
    }

    /**
     * Test of constructors of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testConstructors()
    {
        DefaultTermIndex termIndex = new DefaultTermIndex();
        Matrix similarities = MatrixFactory.getDefault().createMatrix(0, 0);
        MatrixBasedTermSimilarityNetwork instance =
            new MatrixBasedTermSimilarityNetwork(termIndex, similarities);
        assertSame(termIndex, instance.getTermIndex());
        assertSame(similarities, instance.getSimilarities());

        boolean exceptionThrown = false;
        try
        {
            instance = new MatrixBasedTermSimilarityNetwork(termIndex, MatrixFactory.getDefault().createMatrix(0, 2));
        }
        catch (DimensionalityMismatchException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            instance = new MatrixBasedTermSimilarityNetwork(termIndex, MatrixFactory.getDefault().createMatrix(2, 0));
        }
        catch (DimensionalityMismatchException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            instance = new MatrixBasedTermSimilarityNetwork(termIndex, MatrixFactory.getDefault().createMatrix(2, 2));
        }
        catch (DimensionalityMismatchException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of getSimilarity method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetSimilarity_Term_Term()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();

        for (int i = 0; i < exampleTerms.length; i++)
        {
            for (int j = 0; j < exampleTerms.length; j++)
            {
                assertEquals(this.exampleSimilarityValues[i][j],
                    instance.getSimilarity(exampleTerms[i], exampleTerms[j]),
                    0.0);
            }
        }

        assertEquals(0.0, instance.getSimilarity(new DefaultTerm("no"),
            new DefaultTerm("no")), 0.0);
        assertEquals(0.0, instance.getSimilarity((Term) null, null), 0.0);
        assertEquals(0.0, instance.getSimilarity(null, new DefaultTerm("no")), 0.0);
        assertEquals(0.0, instance.getSimilarity(new DefaultTerm("no"), null), 0.0);
    }

    /**
     * Test of getSimilarity method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetSimilarity_IndexedTerm_IndexedTerm()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        for (int i = 0; i < exampleTerms.length; i++)
        {
            for (int j = 0; j < exampleTerms.length; j++)
            {
                assertEquals(this.exampleSimilarityValues[i][j],
                    instance.getSimilarity(exampleIndexedTerms[i], exampleIndexedTerms[j]),
                    0.0);
            }
        }


        assertEquals(0.0, instance.getSimilarity((IndexedTerm) null, null), 0.0);
        assertEquals(0.0, instance.getSimilarity(null, new DefaultIndexedTerm(-1, new DefaultTerm("no"))), 0.0);
        assertEquals(0.0, instance.getSimilarity(new DefaultIndexedTerm(-1, new DefaultTerm("no")), null), 0.0);
    }

    /**
     * Test of getSimilarity method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetSimilarity_int_int()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        for (int i = 0; i < exampleTerms.length; i++)
        {
            for (int j = 0; j < exampleTerms.length; j++)
            {
                assertEquals(this.exampleSimilarityValues[i][j],
                    instance.getSimilarity(i, j),
                    0.0);
            }
        }
    }

    /**
     * Test of getObjectCount method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetObjectCount()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        assertEquals(this.exampleTerms.length, instance.getObjectCount());
    }

    /**
     * Test of getObjects method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetObjects()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        assertArrayEquals(this.exampleIndexedTerms, instance.getObjects().toArray());
    }

    /**
     * Test of isObject method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testIsObject()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        for (IndexedTerm term : this.exampleIndexedTerms)
        {
            assertTrue(instance.isObject(term));
        }
        assertFalse(instance.isObject(new DefaultIndexedTerm(-1, new DefaultTerm("no"))));

        assertFalse(instance.isObject(null));

        assertFalse(instance.isObject(new Object()));
    }

    /**
     * Test of hasRelation method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testHasRelation()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        assertTrue(instance.hasRelation(indexedTermA, indexedTermB));
        assertTrue(instance.hasRelation(indexedTermB, indexedTermA));
        assertFalse(instance.hasRelation(indexedTermA, indexedTermC));
        assertTrue(instance.hasRelation(indexedTermC, indexedTermA));
        assertFalse(instance.hasRelation(indexedTermA, null));
        assertFalse(instance.hasRelation(null, indexedTermA));
    }

    /**
     * Test of getRelation method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetRelation()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();

        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm source = this.exampleIndexedTerms[i];
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm target = this.exampleIndexedTerms[j];
                double similarity = this.exampleSimilarityValues[i][j];

                IndexedTermSimilarityRelation relation = instance.getRelation(
                    source, target);

                if (similarity == 0.0)
                {
                    assertNull(relation);
                }
                else
                {
                    assertNotNull(relation);
                    assertSame(source, relation.getSource());
                    assertSame(target, relation.getTarget());
                    assertEquals(similarity, relation.getSimilarity(), 0.0);
                }

            }
        }

        assertNull(instance.getRelation(indexedTermA, null));
        assertNull(instance.getRelation(null, indexedTermA));
    }

    /**
     * Test of getAllRelations method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetAllRelations()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();

        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm source = this.exampleIndexedTerms[i];
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm target = this.exampleIndexedTerms[j];
                double similarity = this.exampleSimilarityValues[i][j];

                Set<IndexedTermSimilarityRelation> relations =
                    instance.getAllRelations(source, target);

                if (similarity == 0.0)
                {
                    assertTrue(relations.isEmpty());
                }
                else
                {
                    assertEquals(1, relations.size());
                    IndexedTermSimilarityRelation relation = CollectionUtil.getFirst(relations);
                    assertNotNull(relation);
                    assertSame(source, relation.getSource());
                    assertSame(target, relation.getTarget());
                    assertEquals(similarity, relation.getSimilarity(), 0.0);
                }
            }
        }
        
        assertTrue(instance.getAllRelations(indexedTermA, null).isEmpty());
        assertTrue(instance.getAllRelations(null, indexedTermA).isEmpty());
    }

    /**
     * Test of getRelationSource method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetRelationSource()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();

        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm source = this.exampleIndexedTerms[i];
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm target = this.exampleIndexedTerms[j];

                IndexedTermSimilarityRelation relation = instance.getRelation(
                    source, target);

                if (relation != null)
                {
                    assertSame(source, instance.getRelationSource(relation));
                }

            }
        }

        assertNull(instance.getRelationSource(null));
    }

    /**
     * Test of getRelationTarget method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetRelationTarget()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();

        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm source = this.exampleIndexedTerms[i];
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm target = this.exampleIndexedTerms[j];

                IndexedTermSimilarityRelation relation = instance.getRelation(
                    source, target);

                if (relation != null)
                {
                    assertSame(target, instance.getRelationTarget(relation));
                }

            }
        }

        assertNull(instance.getRelationTarget(null));
    }

    /**
     * Test of relationsOf method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testRelationsOf()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm term = this.exampleIndexedTerms[i];
            LinkedList<IndexedTermSimilarityRelation> expected =
                new LinkedList<IndexedTermSimilarityRelation>();
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm other = this.exampleIndexedTerms[j];

                IndexedTermSimilarityRelation relation = instance.getRelation(
                    term, other);

                if (relation != null)
                {
                    expected.add(relation);
                }

                if (i != j)
                {
                    relation = instance.getRelation(
                        other, term);

                    if (relation != null)
                    {
                        expected.add(relation);
                    }
                }
            }

            Set<IndexedTermSimilarityRelation> result =
                instance.relationsOf(term);
            assertEquals(expected.size(), result.size());
            assertTrue(expected.containsAll(result));
            assertTrue(result.containsAll(expected));
        }
    }

    /**
     * Test of relationsFrom method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testRelationsFrom()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm source = this.exampleIndexedTerms[i];
            LinkedList<IndexedTermSimilarityRelation> expected =
                new LinkedList<IndexedTermSimilarityRelation>();
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm target = this.exampleIndexedTerms[j];

                IndexedTermSimilarityRelation relation = instance.getRelation(
                    source, target);

                if (relation != null)
                {
                    expected.add(relation);
                }
            }

            assertArrayEquals(expected.toArray(), instance.relationsFrom(source).toArray());
        }
    }

    /**
     * Test of relationsTo method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testRelationsTo()
    {
        MatrixBasedTermSimilarityNetwork instance = this.createExampleNetwork();
        for (int i = 0; i < exampleTerms.length; i++)
        {
            IndexedTerm target = this.exampleIndexedTerms[i];
            LinkedList<IndexedTermSimilarityRelation> expected =
                new LinkedList<IndexedTermSimilarityRelation>();
            for (int j = 0; j < exampleTerms.length; j++)
            {
                IndexedTerm source = this.exampleIndexedTerms[j];

                IndexedTermSimilarityRelation relation = instance.getRelation(
                    source, target);

                if (relation != null)
                {
                    expected.add(relation);
                }
            }

            assertArrayEquals(expected.toArray(), instance.relationsTo(target).toArray());
        }
    }

    /**
     * Test of getTermIndex method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetTermIndex()
    {
        this.testSetTermIndex();
    }


    /**
     * Test of setTermIndex method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testSetTermIndex()
    {
        DefaultTermIndex termIndex = new DefaultTermIndex();
        Matrix similarities = MatrixFactory.getDefault().createMatrix(0, 0);
        MatrixBasedTermSimilarityNetwork instance =
            new MatrixBasedTermSimilarityNetwork(termIndex, similarities);
        assertSame(termIndex, instance.getTermIndex());

        termIndex = new DefaultTermIndex();
        instance.setTermIndex(termIndex);
        assertSame(termIndex, instance.getTermIndex());

        termIndex = null;
        instance.setTermIndex(termIndex);
        assertSame(termIndex, instance.getTermIndex());
    }

    /**
     * Test of getSimilarities method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testGetSimilarities()
    {
        this.testSetSimilarities();
    }

    /**
     * Test of setSimilarities method, of class MatrixBasedTermSimilarityNetwork.
     */
    @Test
    public void testSetSimilarities()
    {
        Matrix similarities = MatrixFactory.getDefault().createMatrix(0, 0);
        MatrixBasedTermSimilarityNetwork instance =
            new MatrixBasedTermSimilarityNetwork(
            new DefaultTermIndex(), similarities);
        assertSame(similarities, instance.getSimilarities());

        similarities = MatrixFactory.getDefault().createMatrix(10, 10);
        instance.setSimilarities(similarities);
        assertSame(similarities, instance.getSimilarities());

        similarities = null;
        instance.setSimilarities(similarities);
        assertSame(similarities, instance.getSimilarities());
    }

}