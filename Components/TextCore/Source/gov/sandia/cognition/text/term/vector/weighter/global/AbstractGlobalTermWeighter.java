/*
 * File:                AbstractGlobalTermWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.global;

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.text.term.vector.AbstractVectorSpaceModel;

/**
 * An abstract implementation of the {@code GlobalTermWeighter} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractGlobalTermWeighter
    extends AbstractVectorSpaceModel
    implements GlobalTermWeighter,
        VectorFactoryContainer
{

    /** The vector factory. */
    protected VectorFactory<? extends Vector> vectorFactory;

    /**
     * Creates a new {@code AbstractGlobalTermWeighter}.
     */
    public AbstractGlobalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractGlobalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractGlobalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super();

        this.setVectorFactory(vectorFactory);
    }

    /**
     * Gets the vector factory.
     * 
     * @return
     *      The vector factory.
     */
    public VectorFactory<? extends Vector> getVectorFactory()
    {
        return vectorFactory;
    }

    /**
     * Sets the vector factory.
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
