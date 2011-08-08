/*
 * File:                ManhattanDistanceMetric.java
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

import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The {@code ManhattanDistanceMetric} class implements a distance metric
 * between two vectors that is implemented as the sum of the absolute value of 
 * the difference between the elements in the vectors. This is also known as 
 * the city-block distance, taxicab distance, rectilinear distance, L1  
 * distance, and absolute value distance.
 * 
 * d(x, y) = sum_{i=1 to d} |x_i - y_i|
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public class ManhattanDistanceMetric
    extends AbstractCloneableSerializable
    implements Metric<Vectorizable>
{
    /** An instance of the {@code ManhattanDistanceMetric} to use since this
     *  class has no internal data. */
    public static final ManhattanDistanceMetric INSTANCE = 
        new ManhattanDistanceMetric();
    
    /**
     * Creates a new instance of {@code ManhattanDistanceMetric}.
     */
    public ManhattanDistanceMetric()
    {
        super();
    }
    
    /**
     * Evaluates the Manhattan distance between the two given vectors.
     * 
     * @param  first The first Vector.
     * @param  second The second Vector.
     * @return The Manhattan distance between the two given vectors.
     */
    public double evaluate(
        final Vectorizable first,
        final Vectorizable second)
    {
        return first.convertToVector().minus(second.convertToVector()).norm1();
    }
}
