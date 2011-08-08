/*
 * File:                TermLengthFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 29, 2009, Sandia Corporation.
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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class TermLengthFilter.
 *
 * @author Justin Basilico
 */
public class TermLengthFilterTest
{

    /**
     * Creates a new test.
     */
    public TermLengthFilterTest()
    {
    }

    /**
     * Test of constructors of class TermLengthFilter
     */
    @Test
    public void testConstructors()
    {
        Integer minimumLength = TermLengthFilter.DEFAULT_MINIMUM_LENGTH;
        Integer maximumLength = TermLengthFilter.DEFAULT_MAXIMUM_LENGTH;
        TermLengthFilter instance = new TermLengthFilter();
        assertEquals(minimumLength, instance.getMinimumLength());
        assertEquals(maximumLength, instance.getMaximumLength());

        minimumLength++;
        maximumLength--;

        instance = new TermLengthFilter(minimumLength, maximumLength);
        assertEquals(minimumLength, instance.getMinimumLength());
        assertEquals(maximumLength, instance.getMaximumLength());
    }

    /**
     * Test of filterTerm method, of class TermLengthFilter.
     */
    @Test
    public void testFilterTerm()
    {
        TermOccurrence one = new DefaultTermOccurrence(
            new DefaultTerm("a"), 0, 0);
        TermOccurrence two = new DefaultTermOccurrence(
            new DefaultTerm("ab"), 0, 0);
        TermOccurrence three = new DefaultTermOccurrence(
            new DefaultTerm("abc"), 0, 0);
        TermOccurrence four = new DefaultTermOccurrence(
            new DefaultTerm("abcd"), 0, 0);
        TermOccurrence five = new DefaultTermOccurrence(
            new DefaultTerm("abcde"), 0, 0);
        TermOccurrence six = new DefaultTermOccurrence(
            new DefaultTerm("abcdef"), 0, 0);

        TermLengthFilter instance = new TermLengthFilter(2, 5);
        assertNull(instance.filterTerm(one));
        assertSame(two, instance.filterTerm(two));
        assertSame(three, instance.filterTerm(three));
        assertSame(four, instance.filterTerm(four));
        assertSame(five, instance.filterTerm(five));
        assertNull(instance.filterTerm(six));

        instance.setMaximumLength(null);
        assertNull(instance.filterTerm(one));
        assertSame(two, instance.filterTerm(two));
        assertSame(three, instance.filterTerm(three));
        assertSame(four, instance.filterTerm(four));
        assertSame(five, instance.filterTerm(five));
        assertSame(six, instance.filterTerm(six));

        instance.setMinimumLength(null);
        instance.setMaximumLength(4);
        assertSame(one, instance.filterTerm(one));
        assertSame(two, instance.filterTerm(two));
        assertSame(three, instance.filterTerm(three));
        assertSame(four, instance.filterTerm(four));
        assertNull(instance.filterTerm(five));
        assertNull(instance.filterTerm(six));

        instance.setMaximumLength(null);
        assertSame(one, instance.filterTerm(one));
        assertSame(two, instance.filterTerm(two));
        assertSame(three, instance.filterTerm(three));
        assertSame(four, instance.filterTerm(four));
        assertSame(five, instance.filterTerm(five));
        assertSame(six, instance.filterTerm(six));
    }

    /**
     * Test of getMinimumLength method, of class TermLengthFilter.
     */
    @Test
    public void testGetMinimumLength()
    {
        this.testSetMinimumLength();
    }

    /**
     * Test of setMinimumLength method, of class TermLengthFilter.
     */
    @Test
    public void testSetMinimumLength()
    {
        Integer minimumLength = TermLengthFilter.DEFAULT_MINIMUM_LENGTH;
        TermLengthFilter instance = new TermLengthFilter();
        assertEquals(minimumLength, instance.getMinimumLength());

        minimumLength = 4;
        instance.setMinimumLength(minimumLength);
        assertEquals(minimumLength, instance.getMinimumLength());

        minimumLength = null;
        instance.setMinimumLength(minimumLength);
        assertEquals(minimumLength, instance.getMinimumLength());

        minimumLength = 50;
        instance.setMinimumLength(minimumLength);
        assertEquals(minimumLength, instance.getMinimumLength());

        minimumLength = 0;
        instance.setMinimumLength(minimumLength);
        assertEquals(minimumLength, instance.getMinimumLength());

        minimumLength = 5;
        instance.setMinimumLength(minimumLength);
        assertEquals(minimumLength, instance.getMinimumLength());

        boolean exceptionThrown = false;
        try
        {
            instance.setMinimumLength(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(minimumLength, instance.getMinimumLength());
    }

    /**
     * Test of getMaximumLength method, of class TermLengthFilter.
     */
    @Test
    public void testGetMaximumLength()
    {
        this.testSetMaximumLength();
    }

    /**
     * Test of setMaximumLength method, of class TermLengthFilter.
     */
    @Test
    public void testSetMaximumLength()
    {
        Integer maximumLength = TermLengthFilter.DEFAULT_MAXIMUM_LENGTH;
        TermLengthFilter instance = new TermLengthFilter();
        assertEquals(maximumLength, instance.getMaximumLength());

        maximumLength = 4;
        instance.setMaximumLength(maximumLength);
        assertEquals(maximumLength, instance.getMaximumLength());

        maximumLength = null;
        instance.setMaximumLength(maximumLength);
        assertEquals(maximumLength, instance.getMaximumLength());

        maximumLength = 1;
        instance.setMaximumLength(maximumLength);
        assertEquals(maximumLength, instance.getMaximumLength());

        boolean exceptionThrown = false;
        try
        {
            instance.setMaximumLength(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(maximumLength, instance.getMaximumLength());

        exceptionThrown = false;
        try
        {
            instance.setMaximumLength(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(maximumLength, instance.getMaximumLength());
    }

}