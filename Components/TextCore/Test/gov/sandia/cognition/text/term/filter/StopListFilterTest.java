/*
 * File:                StopListFilterTest.java
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

import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.token.DefaultToken;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class StopListFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class StopListFilterTest
{
    /**
     * Creates a new test.
     */
    public StopListFilterTest()
    {
    }

    /**
     * Test of constructors of class StopListFilter.
     */
    @Test
    public void testConstructors()
    {
        StopList stopList = null;
        StopListFilter instance = new StopListFilter();
        assertSame(stopList, instance.getStopList());

        stopList = new DefaultStopList();
        instance = new StopListFilter(stopList);
        assertSame(stopList, instance.getStopList());
    }

    /**
     * Test of filterTerm method, of class StopListFilter.
     */
    @Test
    public void testFilterTerm()
    {
        DefaultStopList stopList = new DefaultStopList();
        stopList.add("bad");
        StopListFilter instance = new StopListFilter(stopList);

        TermOccurrence good1 = new DefaultToken("good", 1);
        TermOccurrence good2 = new DefaultToken("Good", 2);
        TermOccurrence bad1 = new DefaultToken("bad", 1);
        TermOccurrence bad2 = new DefaultToken("Bad", 2);

        assertSame(good1, instance.filterTerm(good1));
        assertSame(good2, instance.filterTerm(good2));
        assertNull(instance.filterTerm(bad1));
        assertNull(instance.filterTerm(bad2));
    }

    /**
     * Test of getStopList method, of class StopListFilter.
     */
    @Test
    public void testGetStopList()
    {
        this.testSetStopList();
    }

    /**
     * Test of setStopList method, of class StopListFilter.
     */
    @Test
    public void testSetStopList()
    {
        StopList stopList = null;
        StopListFilter instance = new StopListFilter();
        assertSame(stopList, instance.getStopList());

        stopList = new DefaultStopList();
        instance.setStopList(stopList);
        assertSame(stopList, instance.getStopList());

        stopList = new DefaultStopList();
        instance.setStopList(stopList);
        assertSame(stopList, instance.getStopList());

        stopList = null;
        instance.setStopList(stopList);
        assertSame(stopList, instance.getStopList());
    }

}