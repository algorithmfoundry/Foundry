/*
 * File:                DocumentSingleFieldConverter.java
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
import gov.sandia.cognition.text.document.Document;
import gov.sandia.cognition.text.document.Field;

/**
 * Extracts a single field from a document. Typically used between the document
 * extraction stage and tokenization stage to choose what text field to use
 * in the text analysis.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class DocumentSingleFieldConverter
    extends AbstractSingleTextualConverter<Document, Field>
{

    /** The name of the field to extract. */
    protected String fieldName;

    /**
     * Creates a new {@code DocumentSingleFieldConverter} with the body field
     * as the field to extract.
     */
    public DocumentSingleFieldConverter()
    {
        this(AbstractDocument.BODY_FIELD_NAME);
    }

    /**
     * Creates a new {@code DocumentSingleFieldConverter} with the given field
     * to extract.
     *
     * @param   fieldName
     *      The name of the field to extract.
     */
    public DocumentSingleFieldConverter(
        final String fieldName)
    {
        this.fieldName = fieldName;
    }

    @Override
    public Field evaluate(
        final Document input)
    {
        if (input == null)
        {
            return null;
        }
        else
        {
            return input.getField(this.getFieldName());
        }
    }

    /**
     * Gets the name of the field to extract.
     *
     * @return
     *      The name of the field to extract.
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * Sets the name of the field to extract.
     *
     * @param   fieldName
     *      The name of the field to extract.
     */
    public void setFieldName(
        final String fieldName)
    {
        this.fieldName = fieldName;
    }

}
