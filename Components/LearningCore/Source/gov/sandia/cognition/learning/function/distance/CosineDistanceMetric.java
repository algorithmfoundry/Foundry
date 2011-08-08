/*
 * File:                CosineDistanceMetric.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 13, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The {@code CosineDistanceMetric} class implements a semimetric between
 * two vectors based on the cosine between the vectors. Since cosine is 
 * typically used as a similarity measure, to convert it to a semimetric,
 * one minus the cosine is computed.
 * 
 * d(x, y) = 1.0 - cos(x, y) = 1.0 - (x * y) / (||x|| * ||y||)
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public class CosineDistanceMetric
    extends AbstractCloneableSerializable
    implements Semimetric<Vectorizable>
{
    /** An instance of {@code CosineDistanceMetric} to use since this class has
     *  no internal data. */
    public static final CosineDistanceMetric INSTANCE =
        new CosineDistanceMetric();
    
    /**
     * Creates a new instance of {@code CosineDistanceMetric}.
     */
    public CosineDistanceMetric()
    {
        super();
    }
    
    /**
     * Evaluates the cosine distance between the two given vectors.
     * 
     * @param  first The first Vector.
     * @param  second The second Vector.
     * @return The cosine distance between the two given vectors.
     */
    public double evaluate(
        final Vectorizable first,
        final Vectorizable second)
    {
        // Ideally, we would just do:
        // return 1.0 - first.convertToVector().cosine(second.convertToVector());
        // But, we have a problem which is that zero is not similar to itself.
        // Thus, we manually decompose the cosine to make do that check.
        final Vector firstVector = first.convertToVector();
        final Vector secondVector = second.convertToVector();
        final double dotProduct = firstVector.dotProduct(secondVector);
        final double firstNormSquared = firstVector.norm2Squared();
        final double secondNormSquared = secondVector.norm2Squared();

        if (dotProduct == 0.0)
        {
            if (firstNormSquared == 0.0 && secondNormSquared == 0.0)
            {
                // Zero vectors are similar to themselves.
                return 0.0;
            }
            else
            {

                // The cosine would be 0.0, so the result is 1.0.
                return 1.0;
            }
        }
        else
        {
            // Compute the actual cosine.
            final double cosine = 
                dotProduct / Math.sqrt(firstNormSquared * secondNormSquared);

            // Change it from a similarity to a divergence.
            return 1.0 - cosine;
        }
    }

}
