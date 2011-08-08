/*
 * File:                DictionaryFilterTest.java
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DictionaryFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DictionaryFilterTest
{
    /**
     * Creates a new test.
     */
    public DictionaryFilterTest()
    {
    }

    /**
     * Test of constructors of class DictionaryFilter.
     */
    @Test
    public void testConstructors()
    {
        DictionaryFilter instance = new DictionaryFilter();
        assertNotNull(instance.getAllowedTerms());
        assertTrue(instance.getAllowedTerms().isEmpty());


        Set<Term> allowedTerms = new HashSet<Term>();
        instance = new DictionaryFilter(allowedTerms);
        assertSame(allowedTerms, instance.getAllowedTerms());
    }

    /**
     * Test of filterTerm method, of class DictionaryFilter.
     */
    @Test
    public void testFilterTerm()
    {
        DictionaryFilter instance = new DictionaryFilter();
        instance.getAllowedTerms().add(new DefaultTerm("allowed"));

        DefaultTermOccurrence input = new DefaultTermOccurrence(new DefaultTerm("nope"), 4, 7);
        assertNull(instance.filterTerm(input));

        input.setTerm(new DefaultTerm("allowed"));
        assertSame(input, instance.filterTerm(input));
    }

    /**
     * Test of getAllowedTerms method, of class DictionaryFilter.
     */
    @Test
    public void testGetAllowedTerms()
    {
        this.testSetAllowedTerms();
    }

    /**
     * Test of setAllowedTerms method, of class DictionaryFilter.
     */
    @Test
    public void testSetAllowedTerms()
    {
        DictionaryFilter instance = new DictionaryFilter();
        assertNotNull(instance.getAllowedTerms());
        assertTrue(instance.getAllowedTerms().isEmpty());


        Set<Term> allowedTerms = new HashSet<Term>();
        instance.setAllowedTerms(allowedTerms);
        assertSame(allowedTerms, instance.getAllowedTerms());

        allowedTerms = new LinkedHashSet<Term>();
        instance.setAllowedTerms(allowedTerms);
        assertSame(allowedTerms, instance.getAllowedTerms());

        allowedTerms = null;
        instance.setAllowedTerms(allowedTerms);
        assertSame(allowedTerms, instance.getAllowedTerms());

        allowedTerms = new LinkedHashSet<Term>();
        instance.setAllowedTerms(allowedTerms);
        assertSame(allowedTerms, instance.getAllowedTerms());
    }

}