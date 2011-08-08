/*
 * File:                Vector3D.java
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

import gov.sandia.cognition.util.Triple;


/**
 * An interface for a 3-dimensional vector. Adds convenience methods for
 * accessing the three values (x, y, z).
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Vector3D
    extends Vector, Triple<Double, Double, Double>
{
// TODO: Add cross-product implementation. --jdbasil (2010-04-30)
//    public Vector3D crossProduct(
//        final Vector3D other);

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
     * Gets the value of the third dimension (z).
     *
     * @return
     *      The value of the third dimension (z).
     */
    public double getZ();

    /**
     * Sets the value of the third dimension (z).
     *
     * @param   z
     *      The value of the third dimension (z).
     */
    public void setZ(
        final double z);

    /**
     * Sets the value of all three dimensions of this vector.
     *
     * @param   x
     *      The value of the first dimension (x).
     * @param y
     *      The value of the second dimension (y).
     * @param z
     *      The value of the third dimension (z).
     */
    public void setXYZ(
        final double x,
        final double y,
        final double z);
    
}
