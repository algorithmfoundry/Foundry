/*
 * File:                LowerCaseTermFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 03, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.TermOccurrence;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LowerCaseTermFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class LowerCaseTermFilterTest
{
    /**
     * Creates a new test.
     */
    public LowerCaseTermFilterTest()
    {
    }

    @Test
    public void testConstructors()
    {
        LowerCaseTermFilter instance = new LowerCaseTermFilter();
    }

    /**
     * Test of filterTerm method, of class LowerCaseTermFilter.
     */
    @Test
    public void testFilterTerm()
    {
        LowerCaseTermFilter instance = new LowerCaseTermFilter();

        TermOccurrence term1 = new DefaultTermOccurrence(new DefaultTerm("TesT"), 1, 9);
        TermOccurrence term2 = new DefaultTermOccurrence(new DefaultTerm(""), 44, 4);
        TermOccurrence term3 = new DefaultTermOccurrence(new DefaultTerm("aBc 123"), 3, 3);

        TermOccurrence result1 = instance.filterTerm(term1);
        assertEquals(1, result1.getStart());
        assertEquals(9, result1.getLength());
        assertEquals("test", result1.getTerm().getName());

        TermOccurrence result2 = instance.filterTerm(term2);
        assertEquals(44, result2.getStart());
        assertEquals(4, result2.getLength());
        assertEquals("", result2.getTerm().getName());

        TermOccurrence result3 = instance.filterTerm(term3);
        assertEquals(3, result3.getStart());
        assertEquals(3, result3.getLength());
        assertEquals("abc 123", result3.getTerm().getName());

    }

}