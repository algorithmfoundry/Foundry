/*
 * File:                DefaultTermTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 19, 2009, Sandia Corporation.
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
 * Unit tests for class DefaultTerm.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermTest
{

    /**
     * Creates a new unit test.
     */
    public DefaultTermTest()
    {
    }

    /**
     * Test of constructors of class DefaultTerm.
     */
    @Test
    public void testConstructors()
    {
        String name = "";
        DefaultTerm instance = new DefaultTerm();
        assertEquals(name, instance.getName());

        name = "test name";
        instance = new DefaultTerm(name);
        assertEquals(name, instance.getName());
    }

    /**
     * Test of clone methods, of class DefaultTerm.
     */
    @Test
    public void testClone()
    {
        String name = null;
        DefaultTerm instance = new DefaultTerm(name);
        DefaultTerm clone = instance.clone();
        assertNotSame(instance, clone);
        assertSame(instance.getName(), clone.getName());

        name = "test";
        instance.setText(name);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertSame(instance.getName(), clone.getName());
    }

    /**
     * Test of asTerm method, of class DefaultTerm.
     */
    @Test
    public void testAsTerm()
    {
        DefaultTerm instance = new DefaultTerm();
        assertSame(instance, instance.asTerm());
    }

    /**
     * Test of hashCode method, of class DefaultTerm.
     */
    @Test
    public void testHashCode()
    {
        String text = "abc";
        DefaultTerm instance = new DefaultTerm(text);
        assertEquals(text.hashCode(), instance.hashCode());
    }

    /**
     * Test of equals method, of class DefaultTerm.
     */
    @Test
    public void testEquals()
    {
        DefaultTerm instance = new DefaultTerm("yay");
        assertTrue(instance.equals(instance));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new Object()));
        assertTrue(instance.equals(new DefaultTerm("yay")));
        assertFalse(instance.equals(new DefaultTerm("no")));
    }

}