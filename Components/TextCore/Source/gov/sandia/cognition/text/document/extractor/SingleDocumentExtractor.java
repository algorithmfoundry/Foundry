/*
 * File:                SingleDocumentExtractor.java
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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

/**
 * Interface for a {@code DocumentExtractor} that only extracts a single
 * document from a file.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface SingleDocumentExtractor
    extends DocumentExtractor
{
    /**
     * Attempts to extract a document from the given file.
     *
     * @param   file
     *      The file to extract.
     * @return
     *      The document extracted from the given file.
     * @throws DocumentExtractionException
     *      If there is an error extracting data from the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public Document extractDocument(
        final File file)
        throws DocumentExtractionException, IOException;

    /**
     * Attempts to extract a document from the given file.
     *
     * @param   uri
     *      The URI of the file to extract.
     * @return
     *      The document extracted from the given file.
     * @throws DocumentExtractionException
     *      If there is an error extracting data from the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public Document extractDocument(
        final URI uri)
        throws DocumentExtractionException, IOException;

    /**
     * Attempts to extract a document from the given file.
     *
     * @param   connection
     *      The connection to the file to extract.
     * @return
     *      The document extracted from the given file.
     * @throws DocumentExtractionException
     *      If there is an error extracting data from the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public Document extractDocument(
        final URLConnection connection)
        throws DocumentExtractionException, IOException;
}
