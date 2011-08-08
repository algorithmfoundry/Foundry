/*
 * File:                Vector2D.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 30, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.util.Pair;

/**
 * An interface for a 2-dimensional vector. Adds convenience methods for
 * accessing the two values (x, y).
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Vector2D
    extends Vector, Pair<Double, Double>
{
    /**
     * Gets the value of the first dimension (x).
     *
     * @return
     *      The value of the first dimension (x).
     */
    public double getX();

    /**
     * Sets the value of the first dimension (x).
     *
     * @param   x
     *      The value for the first dimension (x).
     */
    public void setX(
        final double x);

    /**
     * Gets the value of the second dimension (y).
     *
     * @return
     *      The value of the second dimension (y).
     */
    public double getY();

    /**
     * Sets the value of the second dimension (y).
     *
     * @param   y
     *      The value of the second dimension (y).
     */
    public void setY(
        final double y);

    /**
     * Sets the value of both dimensions of the vector.
     *
     * @param   x
     *      The value of the first dimension (x).
     * @param   y
     *      The value of the second dimension (y).
     */
    public void setXY(
        final double x,
        final double y);
    
}
