/*
 * File:                EuclideanDistanceSquaredMetric.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>EuclideanDistanceSquaredMetric</code> implements a distance metric 
 * that computes the squared Euclidean distance between two points.
 *
 * @author Justin Basilico
 * @since 2.0
 */
public class EuclideanDistanceSquaredMetric
    extends AbstractCloneableSerializable
    implements Semimetric<Vectorizable>
{
    /** An instance of EuclideanDistanceSquaredMetric to use since the class 
     * has no internal data. */
    public static final EuclideanDistanceSquaredMetric INSTANCE = 
        new EuclideanDistanceSquaredMetric();
    
    /**
     * Creates a new instance of EuclideanDistanceSquaredMetric.
     */
    public EuclideanDistanceSquaredMetric()
    {
        super();
    }
    
    /**
     * The evaluates the squared Euclidean distance between the two given 
     * vectors.
     *
     * @param  first The first Vector.
     * @param  second The second Vector.
     * @return The squared Euclidean distance between the two given vectors.
     */
    public double evaluate(
        Vectorizable first, 
        Vectorizable second)
    {
        return first.convertToVector().euclideanDistanceSquared(
            second.convertToVector());
    }
}
