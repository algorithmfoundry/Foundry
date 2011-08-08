/*
 * File:                AbstractOccurrenceInTextTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Justin Basilico
 */
public class AbstractOccurrenceInTextTest
{

    public AbstractOccurrenceInTextTest()
    {
    }

    /**
     * Test of constructors of class AbstractOccurrenceInText.
     */
    @Test
    public void testConstructors()
    {
        int start = 0;
        int length = 0;
        AbstractOccurrenceInText<?> instance = new DummyOccurrenceInText();
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());

        start = 1;
        length = 2;
        instance = new DummyOccurrenceInText(start, length);
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
    }

    /**
     * Test of getStart method, of class AbstractOccurrenceInText.
     */
    @Test
    public void testGetStart()
    {
        this.testSetStart();
    }

    /**
     * Test of setStart method, of class AbstractOccurrenceInText.
     */
    @Test
    public void testSetStart()
    {
        int start = 0;
        AbstractOccurrenceInText<?> instance = new DummyOccurrenceInText();
        assertEquals(start, instance.getStart());

        start = 1;
        instance.setStart(start);
        assertEquals(start, instance.getStart());

        start = 2;
        instance.setStart(start);
        assertEquals(start, instance.getStart());
        
        start = 1;
        instance.setStart(start);
        assertEquals(start, instance.getStart());

        boolean exceptionThrown = false;
        try
        {
            instance.setStart(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(start, instance.getStart());
    }

    /**
     * Test of getLength method, of class AbstractOccurrenceInText.
     */
    @Test
    public void testGetLength()
    {
        this.testSetLength();
    }

    /**
     * Test of setLength method, of class AbstractOccurrenceInText.
     */
    @Test
    public void testSetLength()
    {
        int length = 0;
        AbstractOccurrenceInText<?> instance = new DummyOccurrenceInText();
        assertEquals(length, instance.getLength());

        length = 1;
        instance.setLength(length);
        assertEquals(length, instance.getLength());

        length = 2;
        instance.setLength(length);
        assertEquals(length, instance.getLength());

        length = 1;
        instance.setLength(length);
        assertEquals(length, instance.getLength());

        boolean exceptionThrown = false;
        try
        {
            instance.setLength(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(length, instance.getLength());
    }

    class DummyOccurrenceInText
        extends AbstractOccurrenceInText<Object>
    {
        public DummyOccurrenceInText()
        {
            super();
        }

        public DummyOccurrenceInText(
            final int start,
            final int length)
        {
            super(start, length);
        }

        public Object getData()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}