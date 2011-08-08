/*
 * File:                DocumentSingleFieldConverterTest.java
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

import gov.sandia.cognition.text.document.DefaultDocument;
import gov.sandia.cognition.text.document.DefaultTextField;
import gov.sandia.cognition.text.document.AbstractDocument;
import gov.sandia.cognition.text.document.Field;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DocumentSingleFieldConverter.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DocumentSingleFieldConverterTest
{
    /**
     * Creates a new test.
     */
    public DocumentSingleFieldConverterTest()
    {
    }

    /**
     * Test of evaluate method, of class DocumentSingleFieldConverter.
     */
    @Test
    public void testConstructors()
    {
        String fieldName = AbstractDocument.BODY_FIELD_NAME;
        DocumentSingleFieldConverter instance =
            new DocumentSingleFieldConverter();
        assertSame(fieldName, instance.getFieldName());


        fieldName = "a field";
        instance = new DocumentSingleFieldConverter(fieldName);
        assertSame(fieldName, instance.getFieldName());
    }

    /**
     * Test of evaluate method, of class DocumentSingleFieldConverter.
     */
    @Test
    public void testEvaluate()
    {
        DefaultDocument document = new DefaultDocument();
        document.setBody("example body");
        document.setTitle("example title");
        document.setAuthor("example author");
        document.addField(new DefaultTextField("other", "example other"));

        DocumentSingleFieldConverter instance =
            new DocumentSingleFieldConverter();
        Field result = instance.evaluate(document);
        assertEquals(document.getBodyField(), result);
        assertNull(instance.evaluate(null));

        instance.setFieldName(AbstractDocument.TITLE_FIELD_NAME);
        result = instance.evaluate(document);
        assertEquals(document.getTitleField(), result);

        instance.setFieldName("none");
        assertNull(instance.evaluate(document));
    }

    /**
     * Test of getFieldName method, of class DocumentSingleFieldConverter.
     */
    @Test
    public void testGetFieldName()
    {
        this.testSetFieldName();
    }

    /**
     * Test of setFieldName method, of class DocumentSingleFieldConverter.
     */
    @Test
    public void testSetFieldName()
    {
        String fieldName = AbstractDocument.BODY_FIELD_NAME;
        DocumentSingleFieldConverter instance =
            new DocumentSingleFieldConverter();
        assertSame(fieldName, instance.getFieldName());

        fieldName = "a field";
        instance.setFieldName(fieldName);
        assertSame(fieldName, instance.getFieldName());

        fieldName = "another field";
        instance.setFieldName(fieldName);
        assertSame(fieldName, instance.getFieldName());

        fieldName = "";
        instance.setFieldName(fieldName);
        assertSame(fieldName, instance.getFieldName());

        fieldName = null;
        instance.setFieldName(fieldName);
        assertSame(fieldName, instance.getFieldName());
        
        fieldName = "title";
        instance.setFieldName(fieldName);
        assertSame(fieldName, instance.getFieldName());
    }

}