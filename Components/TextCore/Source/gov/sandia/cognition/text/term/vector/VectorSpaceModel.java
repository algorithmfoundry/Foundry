/*
 * File:                VectorSpaceModel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * An interface for a model based on vector representations of documents.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface VectorSpaceModel
{

    /**
     * Adds a document to the model.
     *
     * @param   document
     *      Adds a document to the model.
     */
    public void add(
        final Vectorizable document);

    /**
     * Adds a document to the model.
     *
     * @param   document
     *      Adds a document to the model.
     */
    public void add(
        final Vector document);

    /**
     * Adds all of the given documents to the model.
     *
     * @param   documents
     *      The documents to add.
     */
    public void addAll(
        final Iterable<? extends Vectorizable> documents);

    /**
     * Removes the document from the model.
     *
     * @param   document
     *      The document to remove.
     * @return
     *      True if this object changed as a result of the removal.
     */
    public boolean remove(
        final Vectorizable document);

    /**
     * Removes the document from the model.
     *
     * @param   document
     *      The document to remove.
     * @return
     *      True if this object changed as a result of the removal.
     */
    public boolean remove(
        final Vector document);

    /**
     * Removes all of the given documents from the model.
     *
     * @param   documents
     *      The documents to remove.
     * @return
     *      True if this object changed as a result of the removal.
     */
    public boolean removeAll(
        final Iterable<? extends Vectorizable> documents);

    /**
     * Gets the number of documents that this object is using for its model
     *
     * @return
     *      The number of documents used for the model.
     */
    public int getDocumentCount();

}
