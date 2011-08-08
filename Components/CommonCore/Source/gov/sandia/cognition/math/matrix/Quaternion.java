/*
 * File:                Quaternion.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 07, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Interface for a mathematical quaternion, which represents rotations using
 * four dimensions. This representation is useful for manipulating for 
 * animations and for utilizing in machine learning algorithms.
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public interface Quaternion
    extends CloneableSerializable, Vectorizable
{
    /**
     * Clones this object.
     * 
     * @return  A clone of this object.
     */
    public Quaternion clone();
    
    /**
     * Sets the elements of this quaternion equal to the elements of the given
     * quaternion.
     * 
     * @param   other The other quaternion to copy the values of.
     */
    public void setElements(
        final Quaternion other);

    /**
     * Returns the normalized version of the quaternion.
     * 
     * @return  The normalized version of the quaternion.
     */
    public Quaternion normalize();

    /**
     * Normalizes the quaternion in-place.
     */
    public void normalizeEquals();

    /**
     * Returns the inverse of this quaternion.
     * 
     * @return The inverse of this quaternion
     */
    public Quaternion inverse();

    /**
     * Changes this quaternion to be its inverse.
     */
    public void inverseEquals();
    
    /**
     * Rotates the given vector by this quaternion by performing a 
     * multiplication with this quaternion's rotation matrix: M * v.
     * 
     * @param   vector The 3-dimensional vector to rotate.
     * @return  The vector rotated by this orientation.
     */
    public Vector3 rotate(
        final Vector vector);

    /**
     * Multiplies {@code this * other} and returns the result.
     * 
     * @param   other The other quaternion to multiply with.
     * @return  The result of the multiplication.
     */
    public Quaternion compose(
        final Quaternion other);

    /**
     * Multiplies {@code this = this * other}, thus modifying this object.
     * 
     * @param   other The other quaternion to multiply with.
     */
    public void composeEquals(
        final Quaternion other);

    /**
     * Interpolates between this quaternion and the given quaternion.
     * 
     * @param   other The other quaternion.
     * @param   alpha The interpolation parameter.
     * @return  The interpolated quaternion.
     */
    public Quaternion interpolate(
        final Quaternion other,
        final double alpha);
    
    /**
     * Interpolates between this quaternion and the given quaternion and sets
     * it in this quaternion.
     * 
     * @param   other The other quaternion.
     * @param   alpha The interpolation parameter.
     */
    public void interpolateEquals(
        final Quaternion other,
        final double alpha);
    
    /**
     * Converts the quaternion to a 3-by-3 rotation matrix.
     * 
     * @return  A 3-by-3 rotation matrix.
     */
    public Matrix convertToRotationMatrix();
    
    /**
     * Sets the quaternion to be equivalent to the given a 3-by-3 rotation 
     * matrix.
     * 
     * @param   rotationMatrix A 3-by-3 rotation matrix.
     */
    public void convertFromRotationMatrix(
        final Matrix rotationMatrix);
    
    /**
     * Determines if this quaternion is equal to the given quaternion.
     * 
     * @param   other The other quaternion to determine equality with.
     * @return  True if the two are equal; otherwise, false.
     */
    public boolean equals(
        final Quaternion other);
    
    /**
     * Determines if this quaternion is equal to the given quaternion, using
     * a given threshold for determining if two numbers are equal.
     * 
     * @param   other The other quaternion.
     * @param   effectiveZero The effective zero value for performing the
     *      fuzzy equality. Typically, this is a number close to zero.
     * @return  True if the two are equal; otherwise, false.
     */
    public boolean equals(
        final Quaternion other,
        final double effectiveZero);

    /**
     * Gets the w component of the quaternion.
     * 
     * @return The w component of the quaternion.
     */
    public double getW();

    /**
     * Sets the w component of the quaternion.
     * 
     * @param   w The w component of the quaternion.
     */
    public void setW(
        final double w);

    /**
     * Gets the x component of the quaternion.
     * 
     * @return The x component of the quaternion.
     */
    public double getX();

    /**
     * Sets the x component of the quaternion.
     * 
     * @param   x The x component of the quaternion.
     */
    public void setX(
        final double x);

    /**
     * Gets the y component of the quaternion.
     * 
     * @return The y component of the quaternion.
     */
    public double getY();

    /**
     * Sets the y component of the quaternion.
     * 
     * @param   y The y component of the quaternion.
     */
    public void setY(
        final double y);

    /**
     * Gets the z component of the quaternion.
     * 
     * @return The z component of the quaternion.
     */
    public double getZ();

    /**
     * Sets the z component of the quaternion.
     * 
     * @param   z The z component of the quaternion.
     */
    public void setZ(
        final double z);
    
    /**
     * Sets all of the elements of the quaternion in w-x-y-z order.
     * 
     * @param   w The w component of the quaternion.
     * @param   x The x component of the quaternion.
     * @param   y The y component of the quaternion.
     * @param   z The z component of the quaternion.
     */
    public void setWXYZ(
        final double w,
        final double x,
        final double y,
        final double z);
    
    /**
     * Sets all of the elements of the quaternion in x-y-z-w order.
     * 
     * @param   x The x component of the quaternion.
     * @param   y The y component of the quaternion.
     * @param   z The z component of the quaternion.
     * @param   w The w component of the quaternion.
     */
    public void setXYZW(
        final double x,
        final double y,
        final double z,
        final double w);

}
