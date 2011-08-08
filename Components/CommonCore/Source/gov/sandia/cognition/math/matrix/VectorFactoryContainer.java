/*
 * File:                VectorFactoryContainer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

/**
 * Interface for a container for a vector factory.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public interface VectorFactoryContainer
{

    /**
     * Gets the vector factory the object to use to create new vectors.
     *
     * @return
     *      The vector factory.
     */
    public VectorFactory<? extends Vector> getVectorFactory();

}
