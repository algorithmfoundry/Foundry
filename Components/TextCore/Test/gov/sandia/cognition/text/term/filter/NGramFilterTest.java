/*
 * File:                NGramFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 01, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.DefaultTermNGram;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.TermNGram;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermOccurrence;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class NGramFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class NGramFilterTest
{
    /**
     * Creates a new test.
     */
    public NGramFilterTest()
    {
    }

    /**
     * Test of constructors of class NGramFilter.
     */
    @Test
    public void testConstructors()
    {

        int size = 2;
        NGramFilter instance = new NGramFilter();
        assertEquals(size, instance.getSize());

        size = 3;
        instance = new NGramFilter(size);
        assertEquals(size, instance.getSize());
    }

    /**
     * Test of clone method, of class NGramFilter.
     */
    @Test
    public void testClone()
    {
        NGramFilter instance = new NGramFilter();
        NGramFilter clone = instance.clone();
        assertEquals(instance.getSize(), clone.getSize());
    }

    /**
     * Test of filterTerms method, of class NGramFilter.
     */
    @Test
    public void testFilterTerms()
    {
        int size = 3;
        NGramFilter instance = new NGramFilter(size);

        Term a = new DefaultTerm("a");
        Term b = new DefaultTerm("b");
        Term c = new DefaultTerm("c");
        Term d = new DefaultTerm("d");
        LinkedList<TermOccurrence> terms = new LinkedList<TermOccurrence>();
        terms.add(new DefaultTermOccurrence(a, 2, 1));
        terms.add(new DefaultTermOccurrence(b, 4, 5));
        terms.add(new DefaultTermOccurrence(c, 10, 14));
        terms.add(new DefaultTermOccurrence(d, 55, 3));


        Collection<TermOccurrence> result = instance.filterTerms(terms);
        TermOccurrence occurrence;
        Iterator<TermOccurrence> it = result.iterator();
        assertTrue(it.hasNext());
        occurrence = it.next();
        assertEquals(2, occurrence.getStart());
        assertEquals(1, occurrence.getLength());
        assertEquals(new DefaultTermNGram(null, null, a), occurrence.getTerm());
        assertTrue(it.hasNext());
        occurrence = it.next();
        assertEquals(2, occurrence.getStart());
        assertEquals(7, occurrence.getLength());
        assertEquals(new DefaultTermNGram(null, a, b), occurrence.getTerm());
        assertTrue(it.hasNext());
        occurrence = it.next();
        assertEquals(2, occurrence.getStart());
        assertEquals(22, occurrence.getLength());
        assertEquals(new DefaultTermNGram(a, b, c), occurrence.getTerm());
        assertTrue(it.hasNext());
        occurrence = it.next();
        assertEquals(4, occurrence.getStart());
        assertEquals(54, occurrence.getLength());
        assertEquals(new DefaultTermNGram(b, c, d), occurrence.getTerm());
        assertTrue(it.hasNext());
        occurrence = it.next();
        assertEquals(10, occurrence.getStart());
        assertEquals(48, occurrence.getLength());
        assertEquals(new DefaultTermNGram(c, d, null), occurrence.getTerm());
        assertTrue(it.hasNext());
        occurrence = it.next();
        assertEquals(55, occurrence.getStart());
        assertEquals(3, occurrence.getLength());
        assertEquals(new DefaultTermNGram(d, null, null), occurrence.getTerm());
        assertFalse(it.hasNext());
    }

    /**
     * Test of getSize method, of class NGramFilter.
     */
    @Test
    public void testGetSize()
    {
        this.testSetSize();
    }

    /**
     * Test of setSize method, of class NGramFilter.
     */
    @Test
    public void testSetSize()
    {
        int size = 2;
        NGramFilter instance = new NGramFilter();
        assertEquals(size, instance.getSize());

        size = 3;
        instance.setSize(size);
        assertEquals(size, instance.getSize());

        size = 10;
        instance.setSize(size);
        assertEquals(size, instance.getSize());

        boolean exceptionThrown = false;
        try
        {
            instance.setSize(1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(size, instance.getSize());

        exceptionThrown = false;
        try
        {
            instance.setSize(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(size, instance.getSize());

        exceptionThrown = false;
        try
        {
            instance.setSize(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(size, instance.getSize());
    }

}