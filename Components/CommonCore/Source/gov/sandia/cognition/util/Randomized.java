/*
 * File:                Randomized.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.util;

import java.util.Random;

/**
 * The {@code Randomized} interface defines the functionality of an object
 * whose computations are based in part on an underlying random number
 * generator (a {@code Random} object). If an object implements this interface
 * it should not make use of {@code Math.random()}.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public interface Randomized
{

    /**
     * Gets the random number generator used by this object.
     *
     * @return The random number generator used by this object.
     */
    Random getRandom();

    /**
     * Sets the random number generator used by this object.
     *
     * @param  random The random number generator for this object to use.
     */
    void setRandom(
        Random random);

}
