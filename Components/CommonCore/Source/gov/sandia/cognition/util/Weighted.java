/*
 * File:                Weighted.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.util;

/**
 * The {@code Weighted} interface is to be used on objects that have some
 * weight component assigned to them.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public interface Weighted
{

    /**
     * Gets the weight of the object.
     *
     * @return The weight.
     */
    double getWeight();

}
