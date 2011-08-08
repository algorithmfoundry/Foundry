/*
 * File:                Vector3.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector3D;
import java.util.Random;

/**
 * Implements a three-dimensional {@code DenseVector}.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-26",
    changesNeeded=false,
    comments="Looks good."
)
public class Vector3
    extends DenseVector
    implements Vector3D
{

    /**
     * Creates a new instance of Vector3 that is all zeros.
     */
    public Vector3()
    {
        this(0.0, 0.0, 0.0);
    }

    /**
     * Creates a new instance of Vector3 with the given values.
     *
     * @param   x
     *      The x-coordinate.
     * @param   y
     *      The y-coordinate.
     * @param   z
     *      The z-coordinate.
     */
    public Vector3(
        double x,
        double y,
        double z)
    {
        super(3);

        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    /**
     * Creates a new instance of Vector3 with values copied from the given
     * vector.
     * 
     * @param   other
     *      The other vector to copy.
     */
    public Vector3(
        Vector other)
    {
        this(other.getElement(0), other.getElement(1), other.getElement(2));
        this.assertSameDimensionality(other);
    }

   /**
     * Creates a new instance of Vector3 with values copied from the given
     * vector.
     *
     * @param   other
     *      The other vector to copy.
     */
    public Vector3(
        Vector3D other)
    {
        this(other.getX(), other.getY(), other.getZ());
    }

    @Override
    public Vector3 clone()
    {
        return (Vector3) super.clone();
    }

    @Override
    public int getDimensionality()
    {
        return 3;
    }

    public double getX()
    {
        return this.getElement(0);
    }

    public void setX(
        double x)
    {
        this.setElement(0, x);
    }

    public double getY()
    {
        return this.getElement(1);
    }

    public void setY(
        double y)
    {
        this.setElement(1, y);
    }

    public double getZ()
    {
        return this.getElement(2);
    }

    public void setZ(
        double z)
    {
        this.setElement(2, z);
    }

    public void setXYZ(
        final double x,
        final double y,
        final double z)
    {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    /**
     * The String representation of the Vector3, which is "&lt;x, y, z&gt;".
     *
     * @return The string "&lt;x, y, z&gt;".
     */
    @Override
    public String toString()
    {
        return "<" + this.getX() + ", " 
                   + this.getY() + ", " 
                   + this.getZ() + ">";
    }
    
    /**
     * Creates a new random Vector3 from the given Random object. Each value
     * in the new Vector3 is a random number between 0.0 and 1.0.
     * 
     * @param   random The random number generator to use.
     * @return  A new random Vector3.
     */
    public static Vector3 createRandom(
        final Random random)
    {
        return new Vector3(
            random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public Double getFirst()
    {
        return this.getX();
    }

    public Double getSecond()
    {
        return this.getY();
    }

    public Double getThird()
    {
        return this.getZ();
    }
    
}
