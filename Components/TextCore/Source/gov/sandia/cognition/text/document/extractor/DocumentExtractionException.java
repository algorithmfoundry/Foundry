/*
 * File:                DocumentExtractionException.java
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

/**
 * An exception that occurs during document extraction. Typically this will
 * be some sort of document parsing error.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DocumentExtractionException
    extends Exception
{

    /**
     * Creates a new instance of {@code DocumentExtractionException}
     * without detail message.
     */
    public DocumentExtractionException()
    {
        super();
    }

    /**
     * Constructs an instance of {@code DocumentExtractionException}
     * with the specified detail message.
     *
     * @param   message the detail message.
     */
    public DocumentExtractionException(
        final String message)
    {
        super(message);
    }

    /**
     * Constructs an instance of {@code DocumentExtractionException}
     * with the specified cause.
     *
     * @param   cause The cause of the exception.
     */
    public DocumentExtractionException(
        final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs an instance of {@code DocumentExtractionException}
     * with the specified detail message and cause.
     *
     * @param   message the detail message.
     * @param   cause The cause of the exception.
     */
    public DocumentExtractionException(
        final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

}
