/*
 * File:                Vector1.java
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

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector1D;

/**
 * Implements a one-dimensional MTJ {@code DenseVector}.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class Vector1
    extends DenseVector
    implements Vector1D
{

    /**
     * Creates a new instance of Vector1 that is all zeros.
     */
    public Vector1()
    {
        this(0.0);
    }

    /**
     * Creates a new instance of Vector1 with the given value.
     *
     * @param   x
     *      The x-coordinate.
     */
    public Vector1(
        final double x)
    {
        super(1);

        this.setX(x);
    }

    /**
     * Creates a new instance of Vector1 with values copied from the given
     * vector.
     *
     * @param other
     *      The other vector to copy.
     */
    public Vector1(
        final Vector other)
    {
        this(other.getElement(0));
        this.assertSameDimensionality(other);
    }
    
    /**
     * Creates a new instance of Vector1 with values copied from the given
     * vector.
     *
     * @param other
     *      The other vector to copy.
     */
    public Vector1(
        final Vector1D other)
    {
        this(other.getX());
    }

    @Override
    public Vector1 clone()
    {
        return (Vector1) super.clone();
    }

    @Override
    public int getDimensionality()
    {
        return 1;
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

    /**
     * The String representation of the Vector2, which is "&lt;x&gt;".
     *
     * @return The string "&lt;x&gt;".
     */
    @Override
    public String toString()
    {
        return "<" + this.getX() + ">";
    }
    
}
