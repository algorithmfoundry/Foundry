/*
 * File:                AbstractDocument.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document;

import gov.sandia.cognition.util.AbstractNamed;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

/**
 * An abstract implementation of the {@code Document} interface. It holds the
 * reference plus the fields as a mapping of field names to values.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractDocument
    extends AbstractNamed
    implements Document
{
    /** The name of the title field is {@value}. */
    public static final String TITLE_FIELD_NAME = "title";

    /** The name of the last modified date field is {@value}. */
    public static final String LAST_MODIFIED_DATE_FIELD_NAME = "lastModifiedDate";

    /** The name of the last accessed date field is {@value}. */
    public static final String ACCESSED_DATE_FIELD_NAME = "accessedDate";

    /** The name of the author field is {@value}. */
    public static final String AUTHOR_FIELD_NAME = "author";

    /** The name of the body field is {@value}. */
    public static final String BODY_FIELD_NAME = "body";

    /** A reference to where the document came from. */
    protected DocumentReference reference;

    /** A mapping of field names to fields. */
    protected HashMap<String, Field> fieldMap;

    /**
     * Creates a new {@code AbstractDocument}.
     */
    public AbstractDocument()
    {
        super();

        this.setFieldMap(new HashMap<String, Field>());
    }

    /**
     * Adds a field to the document. If there is already a field with the same
     * name, that field is replaced.
     *
     * @param   field
     *      The field to add.
     */
    protected void addField(
        final Field field)
    {
        this.getFieldMap().put(field.getName(), field);
    }

    /**
     * Removes a field of the given name from the document.
     *
     * @param   fieldName
     *      The name of the field to remove.
     */
    protected void removeField(
        final String fieldName)
    {
        this.getFieldMap().remove(fieldName);
    }

    public boolean hasField(
        final String fieldName)
    {
        return this.getFieldMap().containsKey(fieldName);
    }

    public Field getField(
        final String fieldName)
    {
        return this.getFieldMap().get(fieldName);
    }

    public DocumentReference getReference()
    {
        return this.reference;
    }

    /**
     * Sets the reference to where the document can be found.
     *
     * @param   reference
     *      The reference to where the document can be found.
     */
    protected void setReference(
        final DocumentReference reference)
    {
        this.reference = reference;
    }

    public URI getReferenceURI()
    {
        if (this.getReference() == null)
        {
            return null;
        }
        else
        {
            return this.getReference().toURI();
        }
    }

    public Collection<Field> getFields()
    {
        return this.getFieldMap().values();
    }

    public Field getTitleField()
    {
        return this.getField(TITLE_FIELD_NAME);
    }

    public Field getLastModifiedDateField()
    {
        return this.getField(LAST_MODIFIED_DATE_FIELD_NAME);
    }

    public Field getAccessedDateField()
    {
        return this.getField(ACCESSED_DATE_FIELD_NAME);
    }

    public Field getAuthorField()
    {
        return this.getField(AUTHOR_FIELD_NAME);
    }

    public Field getBodyField()
    {
        return this.getField(BODY_FIELD_NAME);
    }

    /**
     * Gets the mapping of field name to the field.
     *
     * @return
     *      The mapping of field name to field.
     */
    protected HashMap<String, Field> getFieldMap()
    {
        return this.fieldMap;
    }

    /**
     * Sets the mapping of field name to the field.
     *
     * @param   fieldMap
     *      The mapping of field name to field.
     */
    protected void setFieldMap(
        final HashMap<String, Field> fieldMap)
    {
        this.fieldMap = fieldMap;
    }


}
