/*
 * File:                AbstractLocalTermWeighter.java
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

package gov.sandia.cognition.text.term.vector.weighter.local;

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.DefaultVectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Abstract implementation of the {@code LocalTermWeighter} interface. Defaults
 * to a sparse vector factory.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractLocalTermWeighter
    extends DefaultVectorFactoryContainer
    implements LocalTermWeighter
{

    /**
     * Creates a new {@code AbstractLocalTermWeighter}.
     */
    public AbstractLocalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractLocalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractLocalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);
    }

    public Vector computeLocalWeights(
        final Vectorizable document)
    {
        return this.computeLocalWeights(document.convertToVector());
    }

}
