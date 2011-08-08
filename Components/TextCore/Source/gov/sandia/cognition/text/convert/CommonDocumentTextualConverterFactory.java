/*
 * File:                CommonDocumentTextualConverterFactory.java
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
import java.util.Arrays;

/**
 * A utility class for creating common document-text converters.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class CommonDocumentTextualConverterFactory
{

    /**
     * Creates a document text converter that extracts the body field.
     *
     * @return
     *      A new document text converter for the body field.
     */
    public static DocumentSingleFieldConverter createBodyConverter()
    {
        return new DocumentSingleFieldConverter(
            AbstractDocument.BODY_FIELD_NAME);
    }

    /**
     * Creates a document text converter that puts the title and body fields
     * together.
     *
     * @return
     *      A new document text converter for the title and body fields.
     */
    public static DocumentFieldConcatenator createTitleBodyConverter()
    {
        return new DocumentFieldConcatenator(
            Arrays.asList(AbstractDocument.TITLE_FIELD_NAME,
                DefaultDocument.BODY_FIELD_NAME),
            "\n");
    }

    /**
     * Creates a document text converter that puts the title, author, and body
     * fields together.
     *
     * @return
     *      A new document text converter for the title, author, and body
     *      fields.
     */
    public static DocumentFieldConcatenator createTitleAuthorBodyConverter()
    {
        return new DocumentFieldConcatenator(
            Arrays.asList(AbstractDocument.TITLE_FIELD_NAME,
                AbstractDocument.AUTHOR_FIELD_NAME,
                AbstractDocument.BODY_FIELD_NAME),
            "\n");
    }
    
}
