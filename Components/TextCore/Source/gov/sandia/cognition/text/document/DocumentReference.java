/*
 * File:                DocumentReference.java
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

import java.net.URI;

/**
 * Interface for a reference to a document. The references are used to load
 * documents. They must be able to be converted to a URI in order to be loaded.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface DocumentReference
{
    /**
     * Converts the document reference to a URI that can be used to locate the
     * document.
     *
     * @return
     *      The document reference as a URI.
     */
    public URI toURI();

    /**
     * Gets the reference to the file containing the document.
     * If this is already a file, then this reference should be returned. It
     * may be different if, for example, this reference is to a document inside
     * a compressed file like a zip.
     *
     * @return
     *      A reference to the file containing this document.
     */
    public FileReference getFileReference();
}
