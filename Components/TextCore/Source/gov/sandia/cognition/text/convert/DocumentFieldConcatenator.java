/*
 * File:                DocumentFieldConcatenator.java
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

import gov.sandia.cognition.text.DefaultTextual;
import gov.sandia.cognition.text.Textual;
import gov.sandia.cognition.text.document.Document;
import gov.sandia.cognition.text.document.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * A document-text converter that concatenates multiple text fields from a
 * document together for further processing.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class DocumentFieldConcatenator
    extends AbstractSingleTextualConverter<Document, Textual>
{
    /** The default field separator is a newline. */
    public static final String DEFAULT_FIELD_SEPARATOR = "\n";

    /** The list of fields to concatenate together from a document. */
    protected List<String> fieldNames;

    /** The field separator. */
    protected String fieldSeparator;

    /**
     * Creates a new {@code DocumentFieldConcatenator} with an empty list of
     * fields and a newline separator.
     */
    public DocumentFieldConcatenator()
    {
        this(new LinkedList<String>(), DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * Creates a new {@code DocumentFieldConcatenator} with the given field
     * names and field separator.
     *
     * @param   fieldNames
     *      The names of the fields to include.
     * @param   fieldSeparator
     *      The field separator.
     */
    public DocumentFieldConcatenator(
        final List<String> fieldNames,
        final String fieldSeparator)
    {
        super();

        this.setFieldNames(fieldNames);
        this.setFieldSeparator(fieldSeparator);
    }

    @Override
    public Textual evaluate(
        final Document document)
    {
        if (document == null)
        {
            return null;
        }
        
        // Concatenate together all the fields.
        final StringBuilder result = new StringBuilder();
        int i = 0;
        for (String fieldName : this.getFieldNames())
        {
            if (i > 0)
            {
                // Add a separator between fields.
                result.append(this.getFieldSeparator());
            }

            // Get the field.
            final Field field = document.getField(fieldName);
            if (field != null)
            {
                // Not null, so append the text.
                result.append(field.getText());
            }
            
            i++;
        }

        // Convert the string builder to a textual.
        return new DefaultTextual(result.toString());
    }

    /**
     * Gets the list of field names whose text are to be concatenated together.
     *
     * @return
     *      The list of field names.
     */
    public List<String> getFieldNames()
    {
        return this.fieldNames;
    }

    /**
     * Sets the list of field names whose text are to be concatenated together.
     *
     * @param   fieldNames
     *      The list of field names.
     */
    public void setFieldNames(
        final List<String> fieldNames)
    {
        this.fieldNames = fieldNames;
    }

    /**
     * Gets the string used as a separator between field text values.
     *
     * @return
     *      The separator between fields used in the result.
     */
    public String getFieldSeparator()
    {
        return fieldSeparator;
    }

    /**
     * Sets the string used as a separator between field text values.
     *
     * @param   fieldSeparator
     *      The separator between fields used in the result.
     */
    public void setFieldSeparator(
        final String fieldSeparator)
    {
        this.fieldSeparator = fieldSeparator;
    }
    
}
