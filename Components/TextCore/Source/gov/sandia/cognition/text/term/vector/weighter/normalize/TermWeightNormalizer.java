/*
 * File:                TermWeightNormalizer.java
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

package gov.sandia.cognition.text.term.vector.weighter.normalize;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Interface for a tem weight normalization scheme.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermWeightNormalizer
    extends CloneableSerializable
{

    /**
     * Normalizes the given weight vector. Can make use of the supporting 
     * information such as the term counts for the document, and the global
     * weights used to compute the weights (if any). However, it does not have
     * to use this information if it does not need to.
     * 
     * @param   weights
     *      The weights to normalize. Contains an input and should be modified
     *      to create an output. Cannot be null.
     * @param   counts
     *      The term counts for the document to normalize. Cannot be null.
     * @param   globalWeights
     *      The global weights to use in to create the weights, if any. Can be
     *      null.
     */
    public void normalizeWeights(
        final Vector weights,
        final Vector counts,
        final Vector globalWeights);

}
