/*
 * File:                DefaultTermOccurrenceTest.java
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefualtTermOccurrence.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermOccurrenceTest
{

    public DefaultTermOccurrenceTest()
    {
    }

    /**
     * Test of constructors of class DefaultTermOccurrence.
     */
    @Test
    public void testConstructors()
    {
        int start = DefaultTermOccurrence.DEFAULT_START;
        int length = DefaultTermOccurrence.DEFAULT_LENGTH;
        DefaultTerm term = null;
        DefaultTermOccurrence instance = new DefaultTermOccurrence();
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
        assertSame(term, instance.getTerm());

        start = 4;
        length = 7;
        term = new DefaultTerm();
        instance = new DefaultTermOccurrence(term, start, length);
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
        assertSame(term, instance.getTerm());
    }

    /**
     * Test of asTerm method, of class DefaultTermOccurrence.
     */
    @Test
    public void testAsTerm()
    {
        DefaultTerm term = new DefaultTerm();
        DefaultTermOccurrence instance = new DefaultTermOccurrence();
        instance.setTerm(term);
        assertSame(term, instance.asTerm());
    }

    /**
     * Test of getData method, of class DefaultTermOccurrence.
     */
    @Test
    public void testGetData()
    {
        DefaultTerm term = new DefaultTerm();
        DefaultTermOccurrence instance = new DefaultTermOccurrence();
        instance.setTerm(term);
        assertSame(term, instance.getData());
    }

    /**
     * Test of getTerm method, of class DefaultTermOccurrence.
     */
    @Test
    public void testGetTerm()
    {
        this.testSetTerm();
    }

    /**
     * Test of setTerm method, of class DefaultTermOccurrence.
     */
    @Test
    public void testSetTerm()
    {
        DefaultTerm term = null;
        DefaultTermOccurrence instance = new DefaultTermOccurrence();
        assertSame(term, instance.getTerm());

        term = new DefaultTerm();
        instance.setTerm(term);
        assertSame(term, instance.getTerm());

        term = null;
        instance.setTerm(term);
        assertSame(term, instance.getTerm());
    }


    /**
     * Test of equals method, of class DefaultTermOccurrence.
     */
    @Test
    public void testEquals()
    {
        DefaultTermOccurrence instance = new DefaultTermOccurrence();
        assertTrue(instance.equals(instance));
        assertEquals(instance, instance);
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new Object()));

        assertTrue(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 1).equals(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 1)));

        assertFalse(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 1).equals(
            new DefaultTermOccurrence(new DefaultTerm("no"), 0, 1)));

        assertFalse(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 1).equals(
            new DefaultTermOccurrence(new DefaultTerm("test"), 1, 1)));

        assertFalse(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 1).equals(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 0)));

        assertFalse(
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 1).equals((Object)
            new DefaultTermOccurrence(new DefaultTerm("test"), 0, 0)));
    }


    /**
     * Test of hashCode method, of class DefaultTermOccurrence.
     */
    @Test
    public void testHashCode()
    {
        DefaultTermOccurrence instance = new DefaultTermOccurrence();
        assertEquals(instance.hashCode(), new DefaultTermOccurrence().hashCode());

        Term term = new DefaultTerm("test");
        instance.setTerm(term);
        assertEquals(instance.hashCode(), new DefaultTermOccurrence(term, 0, 0).hashCode());
    }

}