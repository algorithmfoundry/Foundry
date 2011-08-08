/*
 * File:                CommonDocumentTextualConverterFactoryTest.java
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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class CommonDocumentTextualConverterFactory.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class CommonDocumentTextualConverterFactoryTest
{
    /**
     * Creates a new test.
     */
    public CommonDocumentTextualConverterFactoryTest()
    {
    }

    /**
     * Creates an example document to convert.
     *
     * @return
     *      An example document.
     */
    public DefaultDocument createExampleDocument()
    {
        DefaultDocument result = new DefaultDocument();
        result.setBody("example body");
        result.setTitle("example title");
        result.setAuthor("example author");
        result.addField(new DefaultTextField("other", "example other"));
        return result;
    }

    /**
     * Test of createBodyConverter method, of class CommonDocumentTextualConverterFactory.
     */
    @Test
    public void testCreateBodyConverter()
    {
        DocumentSingleFieldConverter result =
            CommonDocumentTextualConverterFactory.createBodyConverter();
        assertEquals("example body",
            result.evaluate(this.createExampleDocument()).getText());
    }

    /**
     * Test of createTitleBodyConverter method, of class CommonDocumentTextualConverterFactory.
     */
    @Test
    public void testCreateTitleBodyConverter()
    {
        DocumentFieldConcatenator result =
            CommonDocumentTextualConverterFactory.createTitleBodyConverter();
        assertEquals("example title\nexample body",
            result.evaluate(this.createExampleDocument()).getText());
    }

    /**
     * Test of createTitleAuthorBodyConverter method, of class CommonDocumentTextualConverterFactory.
     */
    @Test
    public void testCreateTitleAuthorBodyConverter()
    {
        DocumentFieldConcatenator result =
            CommonDocumentTextualConverterFactory.createTitleAuthorBodyConverter();
        assertEquals("example title\nexample author\nexample body",
            result.evaluate(this.createExampleDocument()).getText());
    }

}