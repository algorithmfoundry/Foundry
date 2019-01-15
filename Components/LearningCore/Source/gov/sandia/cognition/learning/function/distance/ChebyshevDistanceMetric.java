/*
 * File:            ChebyshevDistance.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An implementation of the Chebyshev distance, which is the absolute value of
 * the largest difference between two vectors in a single dimension. As such, it
 * is the infinity-norm of the difference between the two vectors.
 *
 * @author  Justin Basilico
 * @since   3.3.3
 */
@PublicationReference(
    title="Chebyshev Distance",
    author="Wikipedia",
    year=2011,
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Chebyshev_distance")
public class ChebyshevDistanceMetric
    extends AbstractCloneableSerializable
    implements Metric<Vectorizable>
{
     /** An instance of the {@code ChebyshevDistanceMetric} to use since this
     *  class has no internal data. */
    public static final ChebyshevDistanceMetric INSTANCE = 
        new ChebyshevDistanceMetric();

    /**
     * Creates a new {@code ChebyshevDistanceMetric}.
     */
    public ChebyshevDistanceMetric()
    {
        super();
    }
    
    @Override
    public double evaluate(
        final Vectorizable first,
        final Vectorizable second)
    {
        // The Chebyshev distance is the infinity-norm of difference, which is
        // the size of the largest difference in a single dimension between
        // the two vectors.
        return first.convertToVector().minus(
            second.convertToVector()).normInfinity();
    }
    
}
