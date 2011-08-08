/*
 * File:                AbstractTermTest.java
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractTerm.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractTermTest
{

    /**
     * Creates a new test.
     */
    public AbstractTermTest()
    {
    }

    /**
     * Test of constructors of class AbstractTerm.
     */
    @Test
    public void testConstructors()
    {
        AbstractTerm instance = new DummyTerm();
    }

    /**
     * Test of asTerm method, of class AbstractTerm.
     */
    @Test
    public void testAsTerm()
    {
        AbstractTerm instance = new DummyTerm();
        assertSame(instance, instance.asTerm());
        assertSame(instance, instance.asTerm());
    }

    /**
     * Test of hashCode method, of class AbstractTerm.
     */
    @Test
    public void testHashCode()
    {
        AbstractTerm instance = new DummyTerm();
        assertEquals("dummy".hashCode(), instance.hashCode());
        assertEquals("dummy".hashCode(), instance.hashCode());
    }

    /**
     * Test of equals method, of class AbstractTerm.
     */
    @Test
    public void testEquals()
    {
        AbstractTerm instance = new DummyTerm();
        assertTrue(instance.equals(instance));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new Object()));
        assertTrue(instance.equals(new DefaultTerm("dummy")));
        assertFalse(instance.equals(new DefaultTerm("no")));
    }

    /**
     * Test of toString method, of class AbstractTerm.
     */
    @Test
    public void testToString()
    {
        AbstractTerm instance = new DummyTerm();
        assertEquals("dummy", instance.toString());
    }

    public static class DummyTerm
        extends AbstractTerm
    {

        public DummyTerm()
        {
            super();
        }

        public String getName()
        {
            return "dummy";
        }

    }

}