/*
 * File:                UnitTermWeightNormalizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.normalize;

import gov.sandia.cognition.math.matrix.Vector;


/**
 * Normalizes term weights to be a unit vector. It is the most common form of
 * normalization.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class UnitTermWeightNormalizer
    extends AbstractTermWeightNormalizer
{

    /**
     * Creates a new {@code UnitTermWeightNormalizer}.
     */
    public UnitTermWeightNormalizer()
    {
        super();
    }

    public void normalizeWeights(
        final Vector weights,
        final Vector counts,
        final Vector globalWeights)
    {
        weights.unitVectorEquals();
    }
    
}
