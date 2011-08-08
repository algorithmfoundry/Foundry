/*
 * File:                AbstractSingleDocumentExtractor.java
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
import java.util.Collections;
import java.util.List;

/**
 * An abstract implementation of the {@code SingleDocumentExtractor} interface.
 * It turns the {@code extractAll} calls into the appropriate
 * {@code extractDocument} calls. It also chains the different
 * {@code extractDocument} so that subclasses only need to handle the
 * {@code URLConnection} version.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractSingleDocumentExtractor
    extends AbstractDocumentExtractor
    implements SingleDocumentExtractor
{

    /**
     * Creates a new {@code AbstractSingleDocumentExtractor}.
     */
    public AbstractSingleDocumentExtractor()
    {
        super();
    }

    @Override
    public List<? extends Document> extractAll(
        final File file)
        throws DocumentExtractionException, IOException
    {
        // Call the single extraction version.
        return Collections.singletonList(this.extractDocument(file));
    }

    @Override
    public List<? extends Document> extractAll(
        final URI uri)
        throws DocumentExtractionException, IOException
    {
        return Collections.singletonList(this.extractDocument(uri));
    }

    public List<? extends Document> extractAll(
        final URLConnection connection)
        throws DocumentExtractionException, IOException
    {
        return Collections.singletonList(this.extractDocument(connection));
    }


    public Document extractDocument(
        final File file)
        throws DocumentExtractionException, IOException
    {
        // Convert to URI.
        return this.extractDocument(file.toURI());
    }

    public Document extractDocument(
        final URI uri)
        throws DocumentExtractionException, IOException
    {
        // Convert to URL connection.
        return this.extractDocument(uri.toURL().openConnection());
    }

}
