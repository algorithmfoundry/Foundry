/*
 * File:                EuclideanDistanceMetric.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>EuclideanDistanceMetric</code> implements a distance metric that
 * computes the Euclidean distance between two points.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class EuclideanDistanceMetric
    extends AbstractCloneableSerializable
    implements Metric<Vectorizable>
{
    /** An instance of EuclideanDistanceMetric to use since the class has no
     * internal data. */
    public static final EuclideanDistanceMetric INSTANCE = 
        new EuclideanDistanceMetric();
    
    /**
     * Creates a new instance of EuclideanDistanceMetric.
     */
    public EuclideanDistanceMetric()
    {
        super();
    }
    
    /**
     * Evaluates the Euclidean distance between the two given vectors.
     *
     * @param  first The first Vector.
     * @param  second The second Vector.
     * @return The Euclidean distance between the two given vectors.
     */
    public double evaluate(
        Vectorizable first, 
        Vectorizable second)
    {
        return first.convertToVector().euclideanDistance(
            second.convertToVector());
    }
}

