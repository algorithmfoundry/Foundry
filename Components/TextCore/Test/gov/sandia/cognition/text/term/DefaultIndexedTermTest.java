/*
 * File:                DefaultIndexedTermTest.java
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
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultIndexedTermTest
{

    public DefaultIndexedTermTest()
    {
    }

    /**
     * Test of constructors of class DefaultIndexedTerm.
     */
    @Test
    public void testConstructors()
    {
        int index = -1;
        DefaultTerm term = null;
        DefaultIndexedTerm instance = new DefaultIndexedTerm();
        assertEquals(index, instance.getIndex());
        assertSame(term, instance.getTerm());

        index = 4;
        term = new DefaultTerm();
        instance = new DefaultIndexedTerm(index, term);
        assertEquals(index, instance.getIndex());
        assertSame(term, instance.getTerm());
    }

    /**
     * Test of clone method, of class DefaultIndexedTerm.
     */
    @Test
    public void testClone()
    {
        int index = 3;
        Term term = null;
        DefaultIndexedTerm instance = new DefaultIndexedTerm(index, term);
        DefaultIndexedTerm clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(index, clone.getIndex());
        assertEquals(term, clone.getTerm());

        term = new DefaultTerm("test");
        instance.setTerm(term);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(index, clone.getIndex());
        assertEquals(term, clone.getTerm());
        assertNotSame(term, clone.getTerm());
    }

    /**
     * Test of hashCode method, of class DefaultIndexedTerm.
     */
    @Test
    public void testHashCode()
    {
        DefaultIndexedTerm instance = new DefaultIndexedTerm();
        assertEquals(instance.hashCode(), new DefaultIndexedTerm().hashCode());

        int index = 4;
        Term term = new DefaultTerm("test");
        instance.setIndex(index);
        instance.setTerm(term);
        assertEquals(instance.hashCode(),
            new DefaultIndexedTerm(index, term).hashCode());
    }

    /**
     * Test of equals method, of class DefaultIndexedTerm.
     */
    @Test
    public void testEquals()
    {
        DefaultIndexedTerm instance = new DefaultIndexedTerm();
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals((Object) instance));
        assertTrue(instance.equals(new DefaultIndexedTerm()));
        assertTrue(instance.equals((Object) new DefaultIndexedTerm()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new Object()));

        assertTrue(
            new DefaultIndexedTerm(2, new DefaultTerm("test")).equals(
            new DefaultIndexedTerm(2, new DefaultTerm("test"))));
        assertFalse(
            new DefaultIndexedTerm(1, new DefaultTerm("test")).equals(
            new DefaultIndexedTerm(2, new DefaultTerm("test"))));
        assertFalse(
            new DefaultIndexedTerm(2, new DefaultTerm("test")).equals(
            new DefaultIndexedTerm(2, new DefaultTerm("no"))));
    }

    /**
     * Test of asTerm method, of class DefaultIndexedTerm.
     */
    @Test
    public void testAsTerm()
    {
        DefaultTerm term = new DefaultTerm();
        DefaultIndexedTerm instance = new DefaultIndexedTerm(0, term);
        assertSame(term, instance.asTerm());
    }

    /**
     * Test of getTerm method, of class DefaultIndexedTerm.
     */
    @Test
    public void testGetTerm()
    {
        this.testSetTerm();
    }

    /**
     * Test of setTerm method, of class DefaultIndexedTerm.
     */
    @Test
    public void testSetTerm()
    {
        DefaultTerm term = null;
        DefaultIndexedTerm instance = new DefaultIndexedTerm();
        assertSame(term, instance.getTerm());

        term = new DefaultTerm();
        instance.setTerm(term);
        assertSame(term, instance.getTerm());

        term = null;
        instance.setTerm(term);
        assertSame(term, instance.getTerm());
    }

    /**
     * Test of getIndex method, of class DefaultIndexedTerm.
     */
    @Test
    public void testGetIndex()
    {
        this.testSetIndex();
    }

    /**
     * Test of setIndex method, of class DefaultIndexedTerm.
     */
    @Test
    public void testSetIndex()
    {
        int index = -1;
        DefaultIndexedTerm instance = new DefaultIndexedTerm();
        assertEquals(index, instance.getIndex());

        index = 4;
        instance.setIndex(index);
        assertEquals(index, instance.getIndex());

        index = 0;
        instance.setIndex(index);
        assertEquals(index, instance.getIndex());
    }


    /**
     * Test of toString method, of class DefaultIndexedTerm.
     */
    @Test
    public void testToString()
    {
        assertEquals("(-1, null)", new DefaultIndexedTerm().toString());
        assertEquals("(2, yes)", new DefaultIndexedTerm(2, new DefaultTerm("yes")).toString());
    }

}