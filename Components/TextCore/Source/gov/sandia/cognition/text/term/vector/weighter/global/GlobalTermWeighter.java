/*
 * File:                GlobalWeighter.java
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

package gov.sandia.cognition.text.term.vector.weighter.global;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.text.term.vector.VectorSpaceModel;

/**
 * Implements a global term weighting scheme. It contains weights for each
 * term. Typically, these are combined with a local weighting scheme by a
 * {@code CompositeLocalGlobalTermWeighter}.
 * 
 * @author  Justin Basilico
 * @since   3.0
 * @see     gov.sandia.cognition.text.term.vector.weighter.CompositeLocalGlobalTermWeighter
 */
public interface GlobalTermWeighter
    extends VectorSpaceModel
{
    /**
     * Gets the dimensionality of the global weights.
     * 
     * @return
     *      The dimensionality of the global weights. -1 if unknown.
     */
    public int getDimensionality();

    /**
     * Gets the current vector of global weights.
     *
     * @return
     *      The global weights.
     */
    public Vector getGlobalWeights();

}
