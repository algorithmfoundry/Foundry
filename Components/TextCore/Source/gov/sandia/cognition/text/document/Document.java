/*
 * File:                Document.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document;

import gov.sandia.cognition.util.Named;
import java.net.URI;
import java.util.Collection;

/**
 * Defines the interface for a document. A document is composed of several
 * data fields that contain text. It also has a reference describing where it
 * can be located.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Document
    extends Named
{
    /**
     * Gets a reference to the location of the document so that it can be
     * retrieved again later. It should be unique and reproducible since it is
     * used to identify this document.
     *
     * @return
     *      The reference to the document.
     */
    public DocumentReference getReference();

    /**
     * Gets the reference to the location of the document as a {@code URI}.
     *
     * @return
     *      The document reference as a URI.
     */
    public URI getReferenceURI();

    /**
     * Gets the collection of the fields that make up the document.
     *
     * @return
     *      The collection of fields that make up the document.
     */
    public Collection<Field> getFields();

    /**
     * Determines if this document has a field of the given name.
     *
     * @param   fieldName
     *      The name of the field.
     * @return
     *      True if the document has a field of the given name;
     *      otherwise, false.
     */
    public boolean hasField(
        final String fieldName);

    /**
     * Gets the field from the document with the given field name, if it exists.
     * If not, null is returned.
     *
     * @param   fieldName
     *      The name of the field.
     * @return
     *      The field with the given name if it exists; otherwise, null.
     */
    public Field getField(
        final String fieldName);

    /**
     * Gets the commonly-used title field.
     *
     * @return
     *      The title field, if it exists; otherwise, null.
     */
    public Field getTitleField();

    /**
     * Gets the commonly-used last modified date field.
     *
     * @return
     *      The last modified date field, if it exists; otherwise, null.
     */
    public Field getLastModifiedDateField();

    /**
     * Gets the commonly-used accessed date field.
     *
     * @return
     *      The accessed date field, if it exists; otherwise, null.
     */
    public Field getAccessedDateField();

    /**
     * Gets the commonly-used author field.
     *
     * @return
     *      The author field, if it exists; otherwise, null.
     */
    public Field getAuthorField();

    /**
     * Gets the commonly-used body field. Implementations of the
     * {@code Document} interface are highly encouraged to use this field to
     * represent the primary content of the document.
     *
     * @return
     *      The body field, if it exists; otherwise, null.
     */
    public Field getBodyField();
}
