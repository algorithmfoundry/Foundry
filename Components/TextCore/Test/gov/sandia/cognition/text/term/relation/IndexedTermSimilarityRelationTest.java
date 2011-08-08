/*
 * File:                IndexedTermSimilarityRelationTest.java
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

import gov.sandia.cognition.text.term.DefaultIndexedTerm;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.IndexedTerm;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class IndexedTermSimilarityRelation.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class IndexedTermSimilarityRelationTest
{
    /**
     * Creates a new test.
     */
    public IndexedTermSimilarityRelationTest()
    {
    }

    /**
     * Test of constructors of class IndexedTermSimilarityRelation.
     */
    @Test
    public void testConstructors()
    {
        IndexedTerm source = new DefaultIndexedTerm(8, new DefaultTerm("source"));
        IndexedTerm target = new DefaultIndexedTerm(7, new DefaultTerm("target"));
        double similarity = 3.4;
        IndexedTermSimilarityRelation instance =
            new IndexedTermSimilarityRelation(source, target, similarity);
        assertSame(source, instance.getSource());
        assertSame(target, instance.getTarget());
        assertEquals(similarity, instance.getSimilarity(), 0.0);
    }

    /**
     * Test of equals method, of class IndexedTermSimilarityRelation.
     */
    @Test
    public void testEquals()
    {
        IndexedTermSimilarityRelation instance =
            new IndexedTermSimilarityRelation(null, null, 0.0);
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals((Object) instance));
        assertTrue(instance.equals(new IndexedTermSimilarityRelation(null, null, 0.0)));
        assertTrue(instance.equals((Object) new IndexedTermSimilarityRelation(null, null, 0.0)));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new Object()));

        IndexedTerm source = new DefaultIndexedTerm(8, new DefaultTerm("source"));
        IndexedTerm target = new DefaultIndexedTerm(7, new DefaultTerm("target"));
        double similarity = 3.4;
        assertTrue(
            new IndexedTermSimilarityRelation(source, target, similarity).equals(
            new IndexedTermSimilarityRelation(source, target, similarity)));
        assertFalse(
            new IndexedTermSimilarityRelation(source, target, similarity).equals(
            new IndexedTermSimilarityRelation(source, target, 4.3)));
        assertFalse(
            new IndexedTermSimilarityRelation(source, target, similarity).equals(
            new IndexedTermSimilarityRelation(target, source, similarity)));
        assertFalse(
            new IndexedTermSimilarityRelation(source, target, similarity).equals(
            new IndexedTermSimilarityRelation(source, source, similarity)));
    }

    /**
     * Test of hashCode method, of class IndexedTermSimilarityRelation.
     */
    @Test
    public void testHashCode()
    {
        IndexedTermSimilarityRelation instance =
            new IndexedTermSimilarityRelation(null, null, 0.0);
        assertEquals(instance.hashCode(), new IndexedTermSimilarityRelation(null, null, 0.0).hashCode());


        IndexedTerm source = new DefaultIndexedTerm(8, new DefaultTerm("source"));
        IndexedTerm target = new DefaultIndexedTerm(7, new DefaultTerm("target"));
        double similarity = 3.4;
        instance = new IndexedTermSimilarityRelation(source, target, similarity);
        assertEquals(instance.hashCode(), new IndexedTermSimilarityRelation(source, target, similarity).hashCode());
    }

    /**
     * Test of getSimilarity method, of class IndexedTermSimilarityRelation.
     */
    @Test
    public void testGetSimilarity()
    {
        this.testSetSimilarity();
    }

    /**
     * Test of setSimilarity method, of class IndexedTermSimilarityRelation.
     */
    @Test
    public void testSetSimilarity()
    {
        double similarity = 0.0;
        IndexedTermSimilarityRelation instance =
            new IndexedTermSimilarityRelation(null, null, similarity);
        assertEquals(similarity, instance.getSimilarity(), 0.0);

        similarity = 3.4;
        instance.setSimilarity(similarity);
        assertEquals(similarity, instance.getSimilarity(), 0.0);

        similarity = -4.3;
        instance.setSimilarity(similarity);
        assertEquals(similarity, instance.getSimilarity(), 0.0);
    }
}