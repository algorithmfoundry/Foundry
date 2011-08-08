/*
 * File:                DefaultVectorFactoryContainer.java
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

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * A default implementation of the {@code VectorFactoryContainer} interface.
 * A useful base class for implementing classes that contain vector factories.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultVectorFactoryContainer
    extends AbstractCloneableSerializable
    implements VectorFactoryContainer
{

    /** The vector factory used to create new vectors. */
    protected VectorFactory<? extends Vector> vectorFactory;

    /**
     * Creates a new {@code DefaultVectorFactoryContainer}.
     */
    public DefaultVectorFactoryContainer()
    {
        this(VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code DefaultVectorFactoryContainer} with the given factory.
     *
     * @param   vectorFactory
     *      The vector factory to use to create vectors.
     */
    public DefaultVectorFactoryContainer(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super();

        this.setVectorFactory(vectorFactory);
    }

    /**
     * Gets the vector factory the object to use to create new vectors.
     *
     * @return
     *      The vector factory.
     */
    public VectorFactory<? extends Vector> getVectorFactory()
    {
        // The only way this will return null is if the container has been
        // explicitly set to null.
        return this.vectorFactory;
    }

    /**
     * Sets the vector factory for the object to use to create new vectors.
     *
     * @param   vectorFactory
     *      The vector factory.
     */
    public void setVectorFactory(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

}
