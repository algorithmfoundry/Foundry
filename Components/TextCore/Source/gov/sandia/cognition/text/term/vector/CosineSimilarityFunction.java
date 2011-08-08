/*
 * File:                CosineSimilarityFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector;

import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.text.relation.SimilarityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * A vector cosine similarity function. The function is computed as:
 *
 * f(x, y) = (x * y) / (||x|| ||y||)
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class CosineSimilarityFunction
    extends AbstractCloneableSerializable
    implements SimilarityFunction<Vectorizable, Vectorizable>
{

    /** A singleton instance of the class. */
    private static CosineSimilarityFunction INSTANCE;

    /**
     * Gets a singleton instance of the class.
     *
     * @return
     *      A singleton instance of the class.
     */
    public static CosineSimilarityFunction getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new CosineSimilarityFunction();
        }

        return INSTANCE;
    }

    /**
     * Creates a new {@code CosineSimilarityFunction}.
     */
    public CosineSimilarityFunction()
    {
        super();
    }

    public double evaluate(
        final Vectorizable from,
        final Vectorizable to)
    {
        return from.convertToVector().cosine(
            to.convertToVector());

    }

    public CosineDistanceMetric asDivergence()
    {
        return CosineDistanceMetric.INSTANCE;
    }

}
