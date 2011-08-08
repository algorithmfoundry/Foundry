/*
 * File:                DocumentFieldConcatenatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Text Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.text.convert;

import gov.sandia.cognition.text.document.AbstractDocument;
import gov.sandia.cognition.text.document.DefaultDocument;
import gov.sandia.cognition.text.document.DefaultTextField;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import gov.sandia.cognition.text.Textual;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DocumentFieldConcatenator.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DocumentFieldConcatenatorTest
{
    /**
     * Creates a new test.
     */
    public DocumentFieldConcatenatorTest()
    {
    }

    /**
     * Test of constructors of class DocumentFieldConcatenator.
     */
    @Test
    public void testConstructors()
    {
        String fieldSeparator = DocumentFieldConcatenator.DEFAULT_FIELD_SEPARATOR;
        DocumentFieldConcatenator instance = new DocumentFieldConcatenator();
        assertTrue(instance.getFieldNames().isEmpty());
        assertSame(fieldSeparator, instance.getFieldSeparator());

        List<String> fieldNames = Arrays.asList("a", "b");
        fieldSeparator = "|";
        instance = new DocumentFieldConcatenator(fieldNames, fieldSeparator);
        assertSame(fieldNames, instance.getFieldNames());
        assertSame(fieldSeparator, instance.getFieldSeparator());
    }

    /**
     * Test of evaluate method, of class DocumentFieldConcatenator.
     */
    @Test
    public void testEvaluate()
    {
        DefaultDocument document = new DefaultDocument();
        document.setBody("example body");
        document.setTitle("Example Title");
        document.setAuthor("example author");
        document.addField(new DefaultTextField("other", "example other"));

        DocumentFieldConcatenator instance = new DocumentFieldConcatenator();
        instance.getFieldNames().add(AbstractDocument.TITLE_FIELD_NAME);

        Textual result = instance.evaluate(document);
        assertEquals("Example Title", result.getText());

        instance.getFieldNames().add(AbstractDocument.BODY_FIELD_NAME);
        result = instance.evaluate(document);
        assertEquals("Example Title\nexample body", result.getText());

        instance.getFieldNames().add("other");
        instance.setFieldSeparator("|");
        result = instance.evaluate(document);
        assertEquals("Example Title|example body|example other", result.getText());

        instance.getFieldNames().add("none");
        result = instance.evaluate(document);
        assertEquals("Example Title|example body|example other|", result.getText());


        result = instance.evaluate(null);
        assertEquals(null, result);
    }

    /**
     * Test of getFieldNames method, of class DocumentFieldConcatenator.
     */
    @Test
    public void testGetFieldNames()
    {
        this.testSetFieldNames();
    }

    /**
     * Test of setFieldNames method, of class DocumentFieldConcatenator.
     */
    @Test
    public void testSetFieldNames()
    {
        List<String> fieldNames = null;
        DocumentFieldConcatenator instance = new DocumentFieldConcatenator();
        assertTrue(instance.getFieldNames().isEmpty());

        fieldNames = new LinkedList<String>();
        instance.setFieldNames(fieldNames);
        assertSame(fieldNames, instance.getFieldNames());

        fieldNames = Arrays.asList("a", "b");
        instance.setFieldNames(fieldNames);
        assertSame(fieldNames, instance.getFieldNames());

        fieldNames = null;
        instance.setFieldNames(fieldNames);
        assertSame(fieldNames, instance.getFieldNames());

        fieldNames = new ArrayList<String>();
        fieldNames.add("field");
        instance.setFieldNames(fieldNames);
        assertSame(fieldNames, instance.getFieldNames());
    }

    /**
     * Test of getFieldSeparator method, of class DocumentFieldConcatenator.
     */
    @Test
    public void testGetFieldSeparator()
    {
        this.testSetFieldSeparator();
    }

    /**
     * Test of setFieldSeparator method, of class DocumentFieldConcatenator.
     */
    @Test
    public void testSetFieldSeparator()
    {
        String fieldSeparator = DocumentFieldConcatenator.DEFAULT_FIELD_SEPARATOR;
        DocumentFieldConcatenator instance = new DocumentFieldConcatenator();
        assertSame(fieldSeparator, instance.getFieldSeparator());

        fieldSeparator = " ";
        instance.setFieldSeparator(fieldSeparator);
        assertSame(fieldSeparator, instance.getFieldSeparator());

        fieldSeparator = "";
        instance.setFieldSeparator(fieldSeparator);
        assertSame(fieldSeparator, instance.getFieldSeparator());

        fieldSeparator = null;
        instance.setFieldSeparator(fieldSeparator);
        assertSame(fieldSeparator, instance.getFieldSeparator());

        fieldSeparator = "\n";
        instance.setFieldSeparator(fieldSeparator);
        assertSame(fieldSeparator, instance.getFieldSeparator());
    }

}