/*
 * File:                AbstractSingleTermFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 09, 2009, Sandia Corporation.
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractSingleTermFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractSingleTermFilterTest
{
    /**
     * Creates a new test.
     */
    public AbstractSingleTermFilterTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
        throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass()
        throws Exception
    {
    }

    /**
     * Test of filterTerms method, of class AbstractSingleTermFilter.
     */
    @Test
    public void testFilterTerms()
    {
        DummySingleTermFilter instance = new DummySingleTermFilter();

        TermOccurrence accept = new DefaultTermOccurrence(new DefaultTerm("accept"), 1, 1);
        TermOccurrence replace = new DefaultTermOccurrence(new DefaultTerm("replace"), 2, 2);
        TermOccurrence reject = new DefaultTermOccurrence(new DefaultTerm("reject"), 3, 3);
        LinkedList<TermOccurrence> terms = new LinkedList<TermOccurrence>();
        terms.add(accept);
        terms.add(replace);
        terms.add(reject);

        Iterable<TermOccurrence> result = instance.filterTerms(terms);

        Iterator<TermOccurrence> it = result.iterator();
        assertTrue(it.hasNext());
        TermOccurrence result1 = it.next();
        assertSame(accept, result1);
        assertEquals("accept", result1.getTerm().getName());
        assertTrue(it.hasNext());
        TermOccurrence result2 = it.next();
        assertNotSame(replace, result2);
        assertEquals("replaced", result2.getTerm().getName());
        assertFalse(it.hasNext());
    }

    public class DummySingleTermFilter
        extends AbstractSingleTermFilter
    {

        public TermOccurrence filterTerm(
            final TermOccurrence occurrence)
        {
            final String text = occurrence.asTerm().getName();

            if (text.equals("reject"))
            {
                return null;
            }
            else if (text.equals("replace"))
            {
                return new DefaultTermOccurrence(new DefaultTerm("replaced"),
                    occurrence.getStart() + 1,
                    occurrence.getLength() + 1);
            }
            else
            {
                return occurrence;
            }
        }

    }

}