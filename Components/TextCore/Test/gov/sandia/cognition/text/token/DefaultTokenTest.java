/*
 * File:                DefaultTokenTest.java
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

package gov.sandia.cognition.text.token;

import gov.sandia.cognition.text.term.DefaultTerm;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultToken.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTokenTest
{

    /**
     * Creates a new test.
     */
    public DefaultTokenTest()
    {
    }

    /**
     * Test of constructors of class DefaultToken.
     */
    @Test
    public void testConstructors()
    {
        int start = DefaultToken.DEFAULT_START;
        int length = DefaultToken.DEFAULT_LENGTH;
        String text = null;
        DefaultToken instance = new DefaultToken();
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
        assertSame(text, instance.getText());

        start = 3;
        text = "test";
        length = text.length();
        instance = new DefaultToken(text, start);
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
        assertSame(text, instance.getText());

        start = 3;
        text = null;
        length = 0;
        instance = new DefaultToken(text, start);
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
        assertSame(text, instance.getText());

        start = 4;
        length = 7;
        text = "test";
        instance = new DefaultToken(text, start, length);
        assertEquals(start, instance.getStart());
        assertEquals(length, instance.getLength());
        assertSame(text, instance.getText());
    }

    /**
     * Test of getData method, of class DefaultToken.
     */
    @Test
    public void testGetData()
    {
        String text = "test";
        DefaultToken instance = new DefaultToken();
        instance.setText(text);

        DefaultTerm result = instance.getData();
        assertEquals(text, result.getName());
    }

    /**
     * Test of asTerm method, of class DefaultToken.
     */
    @Test
    public void testAsTerm()
    {
        String text = "test";
        DefaultToken instance = new DefaultToken();
        instance.setText(text);

        DefaultTerm result = instance.asTerm();
        assertEquals(text, result.getName());
    }

    /**
     * Test of getTerm method, of class DefaultToken.
     */
    @Test
    public void testGetTerm()
    {
        String text = "test";
        DefaultToken instance = new DefaultToken();
        instance.setText(text);

        DefaultTerm result = instance.getTerm();
        assertEquals(text, result.getName());

        instance.setText(null);
        assertNull(instance.getTerm());
    }

    /**
     * Test of getText method, of class DefaultToken.
     */
    @Test
    public void testGetText()
    {
        this.testSetText();
    }

    /**
     * Test of setText method, of class DefaultToken.
     */
    @Test
    public void testSetText()
    {
        String text = null;
        DefaultToken instance = new DefaultToken();
        assertSame(text, instance.getText());

        text = "test";
        instance.setText(text);
        assertSame(text, instance.getText());

        text = null;
        instance.setText(text);
        assertSame(text, instance.getText());
    }

    /**
     * Test of toString method, of class DefaultToken.
     */
    @Test
    public void testToString()
    {
        String text = null;
        DefaultToken instance = new DefaultToken();
        assertSame(text, instance.toString());

        text = "test";
        instance.setText(text);
        assertSame(text, instance.toString());

        text = null;
        instance.setText(text);
        assertSame(text, instance.toString());
    }
}