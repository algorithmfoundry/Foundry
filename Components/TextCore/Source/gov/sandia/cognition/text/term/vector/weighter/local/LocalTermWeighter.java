/*
 * File:                LocalWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.local;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Defines the functionality of a local term weighting scheme. Takes in a
 * vector representation of a document (usually containing counts) and returns
 * a new, reweighted vector based on that vector based only on the information
 * in the document.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface LocalTermWeighter
    extends CloneableSerializable
{

    /**
     * Computes the new local weights for a given document.
     *
     * @param   document
     *      The document to compute local weights for.
     * @return
     *      The local weight vector for the documents.
     */
    public Vector computeLocalWeights(
        final Vectorizable document);

    /**
     * Computes the new local weights for a given document.
     *
     * @param   document
     *      The document to compute local weights for.
     * @return
     *      The local weight vector for the documents.
     */
    public Vector computeLocalWeights(
        final Vector document);
    
}
