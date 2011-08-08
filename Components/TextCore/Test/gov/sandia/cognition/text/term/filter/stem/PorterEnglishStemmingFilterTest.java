/*
 * File:                PorterEnglishStemmingFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 27, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter.stem;

import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.TermOccurrence;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class PorterEnglishStemmingFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class PorterEnglishStemmingFilterTest
{

    /**
     * Creates a new test.
     */
    public PorterEnglishStemmingFilterTest()
    {
    }

    /**
     * Test of constructors of class PorterEnglishStemmingFilter.
     */
    @Test
    public void testConstructors()
    {
        PorterEnglishStemmingFilter instance = new PorterEnglishStemmingFilter();
        assertNotNull(instance);
    }

    /**
     * Test of filterTerm method, of class PorterEnglishStemmingFilter.
     */
    @Test
    public void testFilterTerm()
    {
        TermOccurrence occurrence = new DefaultTermOccurrence(new DefaultTerm(
            "connected"), 0, 321);
        PorterEnglishStemmingFilter instance = new PorterEnglishStemmingFilter();
        TermOccurrence result = instance.filterTerm(occurrence);
        assertNotSame(occurrence, result);
        assertEquals("connect", result.getTerm().getName());
        assertEquals(occurrence.getStart(), result.getStart());
        assertEquals(occurrence.getLength(), result.getLength());
    }

    /**
     * Test of filterTerm method, of class PorterEnglishStemmingFilter.
     */
    @Test
    public void testStem()
    {
        assertEquals("connect", PorterEnglishStemmingFilter.stem("connects"));
        assertEquals("connect", PorterEnglishStemmingFilter.stem("connected"));
        assertEquals("connect", PorterEnglishStemmingFilter.stem("connecting"));
        assertEquals("connect", PorterEnglishStemmingFilter.stem("connection"));
        assertEquals("connect", PorterEnglishStemmingFilter.stem("connects"));

        // Checking handling of spaces:
        assertEquals("connection ", PorterEnglishStemmingFilter.stem("connection "));
        assertEquals(" connect", PorterEnglishStemmingFilter.stem(" connect"));
        assertEquals("a connect", PorterEnglishStemmingFilter.stem("a connection"));
        assertEquals("connection to a", PorterEnglishStemmingFilter.stem("connection to a"));

        assertEquals("", PorterEnglishStemmingFilter.stem(""));
        assertEquals(" ", PorterEnglishStemmingFilter.stem(" "));
        assertEquals("a", PorterEnglishStemmingFilter.stem("a"));
        assertEquals("1", PorterEnglishStemmingFilter.stem("1"));
    }


}