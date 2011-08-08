/*
 * File:                DefaultTermNGramTest.java
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

package gov.sandia.cognition.text.term;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultTermNGram.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermNGramTest
{
    /**
     * Creates a new test.
     */
    public DefaultTermNGramTest()
    {
    }

    /**
     * Test of constructors of class DefaultTermNGram.
     */
    @Test
    public void testConstructors()
    {
        DefaultTermNGram instance = new DefaultTermNGram();
        assertEquals(0, instance.getTerms().length);

        Term[] terms = new Term[4];
        terms[0] = new DefaultTerm("a");
        instance = new DefaultTermNGram(terms);
        assertSame(terms, instance.getTerms());
    }

    /**
     * Test of clone method, of class DefaultTermNGram.
     */
    @Test
    public void testClone()
    {
        DefaultTermNGram instance = new DefaultTermNGram();
        DefaultTermNGram clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotSame(instance.getTerms(), clone.getTerms());
        assertEquals(instance.getTermCount(), clone.getTermCount());

        instance.setTerms(new DefaultTerm("a"), null, new DefaultTerm("foo"));
        clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotSame(instance.getTerms(), clone.getTerms());
        assertEquals(instance.getTermCount(), clone.getTermCount());
        assertSame(instance.getTerm(0), clone.getTerm(0));
        assertSame(instance.getTerm(1), clone.getTerm(1));
        assertSame(instance.getTerm(2), clone.getTerm(2));
    }

    /**
     * Test of getName method, of class DefaultTermNGram.
     */
    @Test
    public void testGetName()
    {
        DefaultTermNGram instance = new DefaultTermNGram();
        assertEquals("[0-gram:]", instance.getName());

        instance.setTerms((Term) null);
        assertEquals("[1-gram:]", instance.getName());

        instance.setTerms(null, null);
        assertEquals("[2-gram:,]", instance.getName());

        instance.setTerms(new DefaultTerm("foo"));
        assertEquals("[1-gram: foo]", instance.getName());

        instance.setTerms(new DefaultTerm("foo"), new DefaultTerm("bar"));
        assertEquals("[2-gram: foo, bar]", instance.getName());

        instance.setTerms(new DefaultTerm("foo"), new DefaultTerm("bar"), new DefaultTerm("baz"));
        assertEquals("[3-gram: foo, bar, baz]", instance.getName());

        instance.setTerms(null, new DefaultTerm("bar"), new DefaultTerm("baz"));
        assertEquals("[3-gram:, bar, baz]", instance.getName());

        instance.setTerms(new DefaultTerm("foo"), null, new DefaultTerm("baz"));
        assertEquals("[3-gram: foo,, baz]", instance.getName());

        instance.setTerms(new DefaultTerm("foo"), new DefaultTerm("bar"), null);
        assertEquals("[3-gram: foo, bar,]", instance.getName());
    }

    /**
     * Test of getTermCount method, of class DefaultTermNGram.
     */
    @Test
    public void testGetTermCount()
    {
        DefaultTermNGram instance = new DefaultTermNGram();
        assertEquals(0, instance.getTermCount());

        instance.setTerms(new DefaultTerm("foo"));
        assertEquals(1, instance.getTermCount());

        instance.setTerms(new DefaultTerm("foo"), new DefaultTerm("bar"));
        assertEquals(2, instance.getTermCount());

        instance.setTerms(new DefaultTerm("foo"), new DefaultTerm("bar"), new DefaultTerm("baz"));
        assertEquals(3, instance.getTermCount());

        instance.setTerms((Term) null);
        assertEquals(1, instance.getTermCount());

        instance.setTerms(null, null);
        assertEquals(2, instance.getTermCount());
    }

    /**
     * Test of getTerm method, of class DefaultTermNGram.
     */
    @Test
    public void testGetTerm()
    {
        DefaultTermNGram instance = new DefaultTermNGram();

        Term a = new DefaultTerm("a");
        Term foo = new DefaultTerm("foo");
        Term[] terms = new Term[] { a, foo, null};
        instance.setTerms(terms);
        assertSame(a, instance.getTerm(0));
        assertSame(foo, instance.getTerm(1));
        assertNull(instance.getTerm(2));

        boolean exceptionThrown = false;
        try
        {
            instance.getTerm(3);
        }
        catch (Exception e)
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
            instance.getTerm(-1);
        }
        catch (Exception e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of getTermList method, of class DefaultTermNGram.
     */
    @Test
    public void testGetTermList()
    {
        DefaultTermNGram instance = new DefaultTermNGram();
        List<Term> result = instance.getTermList();
        assertTrue(result.isEmpty());

        Term a = new DefaultTerm("a");
        Term foo = new DefaultTerm("foo");
        instance.setTerms(a, null, foo);
        result = instance.getTermList();
        assertEquals(3, result.size());
        assertSame(a, result.get(0));
        assertNull(result.get(1));
        assertSame(foo, result.get(2));
        
        result = instance.getTermList();
        assertEquals(3, result.size());
        assertSame(a, result.get(0));
        assertNull(result.get(1));
        assertSame(foo, result.get(2));
    }

    /**
     * Test of getTerms method, of class DefaultTermNGram.
     */
    @Test
    public void testGetTerms()
    {
        this.testSetTerms();
    }

    /**
     * Test of setTerms method, of class DefaultTermNGram.
     */
    @Test
    public void testSetTerms()
    {
        DefaultTermNGram instance = new DefaultTermNGram();
        assertEquals(0, instance.getTerms().length);

        Term a = new DefaultTerm("a");
        Term foo = new DefaultTerm("foo");
        Term[] terms = new Term[] { a, foo, null};
        instance.setTerms(terms);
        assertSame(terms, instance.getTerms());
        assertSame(a, instance.getTerms()[0]);
        assertSame(foo, instance.getTerms()[1]);
        assertNull(instance.getTerms()[2]);
    }

}