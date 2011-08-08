/*
 * File:                DefaultTextualTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Text Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.text;

import java.io.Reader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultTextual.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultTextualTest
{
    /**
     * Creates a new test.
     */
    public DefaultTextualTest()
    {
    }

    /**
     * Test of constructors of class DefaultTextual.
     */
    @Test
    public void testConstructors()
    {
        String text = "";
        DefaultTextual instance = new DefaultTextual();
        assertSame(text, instance.getText());

        text = "something awesome";
        instance = new DefaultTextual(text);
        assertSame(text, instance.getText());

        instance = new DefaultTextual(instance);
        assertSame(text, instance.getText());
    }

    /**
     * Test of clone method, of class DefaultTextual.
     */
    @Test
    public void testClone()
    {
        DefaultTextual instance = new DefaultTextual("cloning");
        DefaultTextual clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance, clone);
        assertNotSame(clone, instance.clone());

        instance.setText(null);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance, clone);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of equals method, of class DefaultTextual.
     */
    @Test
    public void testEquals()
    {
        DefaultTextual instance = new DefaultTextual();
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(instance.clone()));
        assertFalse(instance.equals(null));
        assertTrue(instance.equals(new DefaultTextual()));
        assertFalse(instance.equals(new DefaultTextual("some text")));
        assertFalse(instance.equals(new DefaultTextual((String) null)));
        assertFalse(instance.equals(new DefaultTextual("another")));

        instance.setText("some text");
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(instance.clone()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new DefaultTextual()));
        assertTrue(instance.equals(new DefaultTextual("some text")));
        assertFalse(instance.equals(new DefaultTextual((String) null)));
        assertFalse(instance.equals(new DefaultTextual("another")));
        
        instance.setText(null);
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(instance.clone()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new DefaultTextual()));
        assertFalse(instance.equals(new DefaultTextual("some text")));
        assertTrue(instance.equals(new DefaultTextual((String) null)));
        assertFalse(instance.equals(new DefaultTextual("another")));

        instance.setText("another");
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(instance.clone()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(new DefaultTextual()));
        assertFalse(instance.equals(new DefaultTextual("some text")));
        assertFalse(instance.equals(new DefaultTextual((String) null)));
        assertTrue(instance.equals(new DefaultTextual("another")));
    }

    /**
     * Test of hashCode method, of class DefaultTextual.
     */
    @Test
    public void testHashCode()
    {
        DefaultTextual instance = new DefaultTextual();
        assertEquals(instance.hashCode(), new DefaultTextual().hashCode());

        instance.setText("some text");
        assertEquals(instance.hashCode(), new DefaultTextual("some text").hashCode());

        instance.setText(null);
        assertEquals(instance.hashCode(), (new DefaultTextual((String) null).hashCode()));

        instance.setText("another");
        assertEquals(instance.hashCode(), new DefaultTextual("another").hashCode());
    }



    /**
     * Test of readText method, of class DefaultTextual.
     */
    @Test
    public void testReadText()
        throws Exception
    {
        DefaultTextual instance = new DefaultTextual();
        Reader reader = instance.readText();
        assertTrue(reader.read() == -1);

        String text = "some text";
        instance.setText(text);
        char[] chars = new char[text.length()];
        reader = instance.readText();
        assertTrue(reader.read(chars) == text.length());
        assertEquals(text, new String(chars));
        assertTrue(reader.read() == -1);
    }

    /**
     * Test of getText method, of class DefaultTextual.
     */
    @Test
    public void testGetText()
    {
        this.testSetText();
    }

    /**
     * Test of setText method, of class DefaultTextual.
     */
    @Test
    public void testSetText()
    {
        String text = "";
        DefaultTextual instance = new DefaultTextual();
        assertSame(text, instance.getText());

        text = "something awesome";
        instance.setText(text);
        assertSame(text, instance.getText());

        text = null;
        instance.setText(text);
        assertSame(text, instance.getText());

        text = "something else";
        instance.setText(text);
        assertSame(text, instance.getText());

        text = "";
        instance.setText(text);
        assertSame(text, instance.getText());
    }

}