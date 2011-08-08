/*
 * File:                DocumentExtractor.java
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

package gov.sandia.cognition.text.document.extractor;

import gov.sandia.cognition.text.document.Document;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

/**
 * Interface for extracting documents from files.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface DocumentExtractor
{
    /**
     * Determines if the given file can be extracted by this extractor.
     *
     * @param   file
     *      The file to extract.
     * @return
     *      True if this extractor can extract the file and false otherwise.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public boolean canExtract(
        final File file)
        throws IOException;

    /**
     * Determines if the given file can be extracted by this extractor.
     *
     * @param   uri
     *      The URI of the file to extract.
     * @return
     *      True if this extractor can extract the file and false otherwise.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public boolean canExtract(
        final URI uri)
        throws IOException;

    /**
     * Determines if the given file can be extracted by this extractor.
     *
     * @param   connection
     *      The connection to the file to extract.
     * @return
     *      True if this extractor can extract the file and false otherwise.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public boolean canExtract(
        final URLConnection connection)
        throws IOException;

    /**
     * Attempts to extract all of the documents from the given file.
     *
     * @param file
     *      The file to extract.
     * @return
     *      The list of documents extracted from the given file.
     * @throws DocumentExtractionException
     *      If there is an error extracting data from the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public Iterable<? extends Document> extractAll(
        final File file)
        throws DocumentExtractionException, IOException;

    /**
     * Attempts to extract all of the documents from the given file.
     *
     * @param   uri
     *      The URI of the file to extract.
     * @return
     *      The list of documents extracted from the given file.
     * @throws DocumentExtractionException
     *      If there is an error extracting data from the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public Iterable<? extends Document> extractAll(
        final URI uri)
        throws DocumentExtractionException, IOException;

    /**
     * Attempts to extract all of the documents from the given file.
     *
     * @param   connection
     *      The connection to the file to extract.
     * @return
     *      The list of documents extracted from the given file.
     * @throws DocumentExtractionException
     *      If there is an error extracting data from the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public Iterable<? extends Document> extractAll(
        final URLConnection connection)
        throws DocumentExtractionException, IOException;
}
