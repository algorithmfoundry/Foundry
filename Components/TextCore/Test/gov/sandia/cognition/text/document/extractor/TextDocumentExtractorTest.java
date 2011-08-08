/*
 * File:                TextDocumentExtractorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 02, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document.extractor;

import gov.sandia.cognition.text.document.Document;
import java.net.URI;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@code TextDocumentExtractor}.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class TextDocumentExtractorTest
{
    /** Base directory for resources. */
    public static final String BASE_DIR = "gov/sandia/cognition/text/document/extractor/resources/";

    private URI testTextURI;
    private URI testHTMLURI;

    /**
     * Creates a new test.
     * @throws Exception
     */
    public TextDocumentExtractorTest()
        throws Exception
    {
        this.testTextURI = ClassLoader.getSystemResource(BASE_DIR + "testText.txt").toURI();
        this.testHTMLURI = ClassLoader.getSystemResource(BASE_DIR + "testHTML.html").toURI();
    }

    /**
     * Test of constructors of class TextDocumentExtractor.
     */
    @Test
    public void testConstructors()
    {
        TextDocumentExtractor instance = new TextDocumentExtractor();
    }

    /**
     * Test of canExtract method, of class TextDocumentExtractor.
     * @throws Exception
     */
    @Test
    public void testCanExtract()
        throws Exception
    {
        TextDocumentExtractor instance = new TextDocumentExtractor();
        assertTrue(instance.canExtract(this.testTextURI));
        assertFalse(instance.canExtract(this.testHTMLURI));
        assertFalse(instance.canExtract(new URI("file://doesNotExist")));
    }

    /**
     * Test of extractDocument method, of class TextDocumentExtractor.
     * @throws Exception
     */
    @Test
    public void testExtractDocument()
        throws Exception
    {
        TextDocumentExtractor instance = new TextDocumentExtractor();
        Document document = instance.extractDocument(this.testTextURI);
        assertEquals("testText.txt", document.getName());
        assertEquals("testText", document.getTitleField().getText());
        assertEquals("This is a test text document.\n", document.getBodyField().getText());
        // TODO: Test extraction of the other fields.
    }

}