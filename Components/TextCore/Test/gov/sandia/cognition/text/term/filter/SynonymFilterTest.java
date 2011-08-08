/*
 * File:                SynonymFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermOccurrence;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class SynonymFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class SynonymFilterTest
{
    /**
     * Creates a new test.
     */
    public SynonymFilterTest()
    {
    }

    /**
     * Test of constructors of class SynonymFilter.
     */
    @Test
    public void testConstructors()
    {
        SynonymFilter instance = new SynonymFilter();
        assertNotNull(instance.getSynonyms());
        assertTrue(instance.getSynonyms().isEmpty());


        Map<Term, Term> synonyms = new HashMap<Term, Term>();
        instance = new SynonymFilter(synonyms);
        assertSame(synonyms, instance.getSynonyms());
    }

    /**
     * Test of filterTerm method, of class SynonymFilter.
     */
    @Test
    public void testFilterTerm()
    {
        SynonymFilter instance = new SynonymFilter();
        instance.getSynonyms().put(new DefaultTerm("replace"), new DefaultTerm("me"));
        
        DefaultTermOccurrence input = new DefaultTermOccurrence(new DefaultTerm("replace"), 4, 7);
        TermOccurrence result = instance.filterTerm(input);
        assertEquals(new DefaultTerm("me"), result.getTerm());
        assertEquals(4, result.getStart());
        assertEquals(7, result.getLength());

        input.setTerm(new DefaultTerm("other"));

        assertSame(input, instance.filterTerm(input));
    }

    /**
     * Test of getSynonyms method, of class SynonymFilter.
     */
    @Test
    public void testGetSynonyms()
    {
        this.testSetSynonyms();
    }

    /**
     * Test of setSynonyms method, of class SynonymFilter.
     */
    @Test
    public void testSetSynonyms()
    {
        SynonymFilter instance = new SynonymFilter();
        assertNotNull(instance.getSynonyms());
        assertTrue(instance.getSynonyms().isEmpty());


        Map<Term, Term> synonyms = new HashMap<Term, Term>();
        instance.setSynonyms(synonyms);
        assertSame(synonyms, instance.getSynonyms());

        synonyms = new LinkedHashMap<Term, Term>();
        instance.setSynonyms(synonyms);
        assertSame(synonyms, instance.getSynonyms());

        synonyms = null;
        instance.setSynonyms(synonyms);
        assertSame(synonyms, instance.getSynonyms());

        synonyms = new LinkedHashMap<Term, Term>();
        instance.setSynonyms(synonyms);
        assertSame(synonyms, instance.getSynonyms());
    }

}