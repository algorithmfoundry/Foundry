/*
 * File:                Vector1D.java
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

/**
 * An interface for a 1-dimensional vector. Adds convenience methods for
 * accessing the one value (x).
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Vector1D
    extends Vector
{

    /**
     * Gets the value of the first (and only) dimension (x).
     *
     * @return
     *      The value of the first dimension (x).
     */
    public double getX();

    /**
     * Sets the value of the first (and only) dimension (x).
     *
     * @param   x
     *      The value for the first dimension (x).
     */
    public void setX(
        final double x);
    
}
