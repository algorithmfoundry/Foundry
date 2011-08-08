/*
 * File:                Vector2.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector2D;

/**
 * Implements a two-dimensional MTJ {@code DenseVector}.
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-26",
    changesNeeded=false,
    comments="Looks good."
)
public class Vector2
    extends DenseVector
    implements Vector2D
{

    /**
     * Creates a new instance of Vector2 that is all zeros.
     */
    public Vector2()
    {
        this(0.0, 0.0);
    }
    
    /**
     * Creates a new instance of Vector2 with the given coordinates.
     *
     * @param  x
     *      The x-coordinate.
     * @param  y 
     *      The y-coordinate.
     */
    public Vector2(
        final double x,
        final double y)
    {
        super(2);
        
        this.setX(x);
        this.setY(y);
    }
    
    /**
     * Creates a new instance of Vector2 with values copied from the given
     * vector.
     * 
     * @param   other
     *      The other vector to copy.
     */
    public Vector2(
        final Vector other)
    {
        this(other.getElement(0), other.getElement(1));
        this.assertSameDimensionality(other);
    }

    /**
     * Creates a new instance of Vector2 wi th values copied from the given
     * vector.
     *
     * @param   other
     *      The other vector to copy.
     */
    public Vector2(
        final Vector2D other)
    {
        this(other.getX(), other.getY());
    }
    
    @Override
    public Vector2 clone()
    {
        return (Vector2) super.clone();
    }

    @Override
    public int getDimensionality()
    {
        return 2;
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
        final double y)
    {
        this.setElement(1, y);
    }

    public void setXY(
        final double x,
        final double y)
    {
        this.setX(x);
        this.setY(y);
    }

    /**
     * The String representation of the Vector2, which is "&lt;x, y&gt;".
     *
     * @return The string "&lt;x, y&gt;".
     */
    @Override
    public String toString()
    {
        return "<" + this.getX() + ", " + this.getY() + ">";
    }

    public Double getFirst()
    {
        return this.getX();
    }

    public Double getSecond()
    {
        return this.getY();
    }
    
}
