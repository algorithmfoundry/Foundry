/*
 * File:                DefaultTermIndexTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.text.term;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultTermIndex.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermIndexTest
{

    public DefaultTermIndexTest()
    {
    }

    /**
     * Test of constructors of class DefaultTermIndex.
     */
    @Test
    public void testConstructors()
    {
        DefaultTermIndex instance = new DefaultTermIndex();
    }

    /**
     * Test of clone method, of class DefaultTermIndex.
     */
    @Test
    public void testClone()
    {
        Term termA = new DefaultTerm("a");
        DefaultTermIndex instance = new DefaultTermIndex();
        instance.add(termA);
        DefaultTermIndex clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(instance.termMap, clone.termMap);
        assertNotSame(instance.termList, clone.termList);
        assertArrayEquals(instance.termList.toArray(), clone.termList.toArray());
    }

    /**
     * Test of add method, of class DefaultTermIndex.
     */
    @Test
    public void testAdd()
    {
        DefaultTerm term0 = new DefaultTerm("term0");
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTermIndex instance = new DefaultTermIndex();

        assertEquals(new DefaultIndexedTerm(0, term0), instance.add(term0));
        assertEquals(new DefaultIndexedTerm(1, term1), instance.add(term1));
        assertEquals(new DefaultIndexedTerm(1, term1), instance.add(new DefaultTerm("term1")));
        assertEquals(new DefaultIndexedTerm(2, term2), instance.add(term2));
        assertEquals(new DefaultIndexedTerm(0, term0), instance.add(new DefaultTerm("term0")));
    }

    /**
     * Test of getTermCount method, of class DefaultTermIndex.
     */
    @Test
    public void testGetTermCount()
    {
        DefaultTermIndex instance = new DefaultTermIndex();
        assertEquals(0, instance.getTermCount());

        instance.add(new DefaultTerm("term1"));
        assertEquals(1, instance.getTermCount());

        instance.add(new DefaultTerm("term2"));
        assertEquals(2, instance.getTermCount());
    }

    /**
     * Test of getTerms method, of class DefaultTermIndex.
     */
    @Test
    public void testGetTerms()
    {
        DefaultTermIndex instance = new DefaultTermIndex();
        List<? extends IndexedTerm> result = instance.getTerms();
        assertTrue(result.isEmpty());

        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        instance.add(term1);
        instance.add(term2);

        result = instance.getTerms();
        assertEquals(2, result.size());
        assertSame(term1, result.get(0).asTerm());
        assertSame(term2, result.get(1).asTerm());
    }


    /**
     * Test of getIndexedTerm method, of class DefaultTermIndex.
     */
    @Test
    public void testGetIndexedTerm()
    {
        DefaultTerm term0 = new DefaultTerm("term0");
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTermIndex instance = new DefaultTermIndex();
        assertEquals(null, instance.getIndexedTerm(term0));
        assertEquals(null, instance.getIndexedTerm(term1));
        assertEquals(null, instance.getIndexedTerm(term2));
        assertEquals(null, instance.getIndexedTerm(0));
        assertEquals(null, instance.getIndexedTerm(1));
        assertEquals(null, instance.getIndexedTerm(2));

        instance.add(term0);
        assertEquals(new DefaultIndexedTerm(0, term0), instance.getIndexedTerm(term0));
        assertEquals(null, instance.getIndexedTerm(term1));
        assertEquals(null, instance.getIndexedTerm(term2));
        assertEquals(new DefaultIndexedTerm(0, term0), instance.getIndexedTerm(0));
        assertEquals(null, instance.getIndexedTerm(1));
        assertEquals(null, instance.getIndexedTerm(2));
        assertSame(instance.getIndexedTerm(term0), instance.getIndexedTerm(0));
        assertSame(instance.getIndexedTerm(term0), instance.getIndexedTerm((Termable) term0));

        instance.add(term1);
        assertEquals(new DefaultIndexedTerm(0, term0), instance.getIndexedTerm(term0));
        assertEquals(new DefaultIndexedTerm(1, term1), instance.getIndexedTerm(term1));
        assertEquals(null, instance.getIndexedTerm(term2));
        assertEquals(new DefaultIndexedTerm(0, term0), instance.getIndexedTerm(0));
        assertEquals(new DefaultIndexedTerm(1, term1), instance.getIndexedTerm(1));
        assertEquals(null, instance.getIndexedTerm(2));
        assertSame(instance.getIndexedTerm(term0), instance.getIndexedTerm(0));
        assertSame(instance.getIndexedTerm(term1), instance.getIndexedTerm(1));
        assertSame(instance.getIndexedTerm(term0), instance.getIndexedTerm((Termable) term0));
        assertSame(instance.getIndexedTerm(term1), instance.getIndexedTerm((Termable) term1));
    }

    /**
     * Test of hasTerm method, of class DefaultTermIndex.
     */
    @Test
    public void testHasIndexedTerm()
    {

        DefaultTerm term0 = new DefaultTerm("term0");
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTermIndex instance = new DefaultTermIndex();
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term0)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term1)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(2, term2)));
        assertFalse(instance.hasIndexedTerm(null));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(-1, null)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term1)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term0)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term2)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term2)));

        instance.add(term0);
        assertTrue(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term0)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term1)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(2, term2)));
        assertFalse(instance.hasIndexedTerm(null));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(-1, null)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term1)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term0)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term2)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term2)));

        instance.add(term1);
        assertTrue(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term0)));
        assertTrue(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term1)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(2, term2)));
        assertFalse(instance.hasIndexedTerm(null));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(-1, null)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term1)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term0)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(0, term2)));
        assertFalse(instance.hasIndexedTerm(new DefaultIndexedTerm(1, term2)));
    }

    /**
     * Test of hasTerm method, of class DefaultTermIndex.
     */
    @Test
    public void testHasTerm()
    {
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTerm term3 = new DefaultTerm("term3");
        DefaultTermIndex instance = new DefaultTermIndex();
        assertFalse(instance.hasTerm(term1));
        assertFalse(instance.hasTerm(term2));
        assertFalse(instance.hasTerm(term3));

        instance.add(term1);
        assertTrue(instance.hasTerm(term1));
        assertFalse(instance.hasTerm(term2));
        assertFalse(instance.hasTerm(term3));

        instance.add(term2);
        assertTrue(instance.hasTerm(term1));
        assertTrue(instance.hasTerm(term2));
        assertFalse(instance.hasTerm(term3));

        instance.add(term2);
        assertTrue(instance.hasTerm((Termable) term1));
        assertTrue(instance.hasTerm((Termable) term2));
        assertFalse(instance.hasTerm((Termable) term3));
    }

    /**
     * Test of hasIndex method, of class DefaultTermIndex.
     */
    @Test
    public void testHasIndex()
    {
        DefaultTerm term0 = new DefaultTerm("term0");
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTermIndex instance = new DefaultTermIndex();
        assertFalse(instance.hasIndex(-1));
        assertFalse(instance.hasIndex(0));
        assertFalse(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));

        instance.add(term0);
        assertFalse(instance.hasIndex(-1));
        assertTrue(instance.hasIndex(0));
        assertFalse(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));

        instance.add(term1);
        assertFalse(instance.hasIndex(-1));
        assertTrue(instance.hasIndex(0));
        assertTrue(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));

        instance.add(term1);
        assertFalse(instance.hasIndex(-1));
        assertTrue(instance.hasIndex(0));
        assertTrue(instance.hasIndex(1));
        assertFalse(instance.hasIndex(2));
    }

    /**
     * Test of getIndex method, of class DefaultTermIndex.
     */
    @Test
    public void testGetIndex()
    {
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTerm term3 = new DefaultTerm("term3");
        DefaultTermIndex instance = new DefaultTermIndex();
        assertEquals(-1, instance.getIndex(term1));
        assertEquals(-1, instance.getIndex(term2));
        assertEquals(-1, instance.getIndex(term3));

        instance.add(term1);
        assertEquals(0, instance.getIndex(term1));
        assertEquals(-1, instance.getIndex(term2));
        assertEquals(-1, instance.getIndex(term3));

        instance.add(term2);
        assertEquals(0, instance.getIndex(term1));
        assertEquals(1, instance.getIndex(term2));
        assertEquals(-1, instance.getIndex(term3));


        assertEquals(0, instance.getIndex((Termable) term1));
        assertEquals(1, instance.getIndex((Termable) term2));
        assertEquals(-1, instance.getIndex((Termable) term3));
    }

    /**
     * Test of getTerm method, of class DefaultTermIndex.
     */
    @Test
    public void testGetTerm()
    {
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTermIndex instance = new DefaultTermIndex();
        assertEquals(null, instance.getTerm(0));
        assertEquals(null, instance.getTerm(1));
        assertEquals(null, instance.getTerm(2));
        assertEquals(null, instance.getTerm(-1));

        instance.add(term1);
        assertEquals(term1, instance.getTerm(0));
        assertEquals(null, instance.getTerm(1));
        assertEquals(null, instance.getTerm(2));
        assertEquals(null, instance.getTerm(-1));

        instance.add(term2);
        assertEquals(term1, instance.getTerm(0));
        assertEquals(term2, instance.getTerm(1));
        assertEquals(null, instance.getTerm(2));
        assertEquals(null, instance.getTerm(-1));

    }

    /**
     * Test of getTermMap method, of class DefaultTermIndex.
     */
    @Test
    public void testGetTermMap()
    {
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTermIndex instance = new DefaultTermIndex();
        instance.add(term1);
        instance.add(term2);

        Map<Term, DefaultIndexedTerm> result = instance.getTermMap();
        assertEquals(2, result.size());
        assertEquals(new DefaultIndexedTerm(0, term1), result.get(term1));
        assertEquals(new DefaultIndexedTerm(1, term2), result.get(term2));
    }

    /**
     * Test of getTermList method, of class DefaultTermIndex.
     */
    @Test
    public void testGetTermList()
    {
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTermIndex instance = new DefaultTermIndex();
        instance.add(term1);
        instance.add(term2);

        List<DefaultIndexedTerm> result = instance.getTermList();
        assertEquals(2, result.size());
        assertEquals(new DefaultIndexedTerm(0, term1), result.get(0));
        assertEquals(new DefaultIndexedTerm(1, term2), result.get(1));
    }

}