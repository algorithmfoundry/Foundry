/*
 * File:                DefaultDateFieldTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document;

import java.text.DateFormat;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultDateField.
 *
 * @author  Justin Basilico
 */
public class DefaultDateFieldTest
{

    /**
     * Creates a new test.
     */
    public DefaultDateFieldTest() 
    {
        super();
    }

    /**
     * Test of constructors of class DefaultDateField.
     */
    @Test
    public void testConstructors()
    {
        String name = null;
        Date date = null;

        DefaultDateField instance = new DefaultDateField();
        assertSame(name, instance.getName());
        assertSame(date, instance.getDate());

        name = "something awesome";
        date = new Date(1234567);
        instance = new DefaultDateField(name, date);
        assertSame(name, instance.getName());
        assertSame(date, instance.getDate());
    }

    /**
     * Test of getText method, of class DefaultDateField.
     */
    @Test
    public void testGetText()
    {
        String name = "something";
        Date date = new Date();
        DefaultDateField instance = new DefaultDateField(name, date);
        assertEquals(DateFormat.getDateTimeInstance().format(date),
            instance.getText());
        
        instance.setDate(null);
        assertEquals(null, instance.getText());
    }

    /**
     * Test of getDate method, of class DefaultDateField.
     */
    @Test
    public void testGetDate()
    {
        this.testSetDate();
    }

    /**
     * Test of setDate method, of class DefaultDateField.
     */
    @Test
    public void testSetDate()
    {
        Date date = null;
        DefaultDateField instance = new DefaultDateField();
        assertSame(date, instance.getDate());

        date = new Date();
        instance.setDate(date);
        assertSame(date, instance.getDate());

        date = new Date(987654321);
        instance.setDate(date);
        assertSame(date, instance.getDate());

        date = null;
        instance.setDate(date);
        assertSame(date, instance.getDate());

        date = new Date(0);
        instance.setDate(date);
        assertSame(date, instance.getDate());

        date = new Date();
        instance.setDate(date);
        assertSame(date, instance.getDate());
    }

}