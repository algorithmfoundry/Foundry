/*
 * File:                AbstractDocumentExtractor.java
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

package gov.sandia.cognition.text.document.extractor;

import gov.sandia.cognition.text.document.Document;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * An abstract implementation of the {@code DocumentExtractor} interface. It
 * chains together the extraction calls so that subclasses only have to handle
 * the {@code URLConnection} calls.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractDocumentExtractor
    extends AbstractCloneableSerializable
    implements DocumentExtractor
{
    /**
     * Creates a new {@code AbstractDocumentExtractor}.
     */
    public AbstractDocumentExtractor()
    {
        super();
    }

    public boolean canExtract(
        final File file)
        throws IOException
    {
        // Convert to URI.
        return this.canExtract(file.toURI());
    }

    public Iterable<? extends Document> extractAll(
        final File file)
        throws DocumentExtractionException, IOException
    {
        // Convert to URI.
        return this.extractAll(file.toURI());
    }

    public Iterable<? extends Document> extractAll(
        final URI uri)
        throws DocumentExtractionException, IOException
    {
        // Convert to URL connection.
        return this.extractAll(uri.toURL().openConnection());
    }

}
