/*
 * File:                AbstractVectorSpaceModel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector;

import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code VectorSpaceModel} class. It handles
 * the conversion of {@code Vectorizable} objects to {@code Vector} objects.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractVectorSpaceModel
    extends AbstractCloneableSerializable
    implements VectorSpaceModel
{

    /**
     * Creates a new {@code AbstractVectorSpaceModel}.
     */
    public AbstractVectorSpaceModel()
    {
        super();
    }
    
    public void add(
        final Vectorizable document)
    {
        this.add(document.convertToVector());
    }

    public void addAll(
        final Iterable<? extends Vectorizable> documents)
    {
        for (Vectorizable document : documents)
        {
            this.add(document);
        }
    }

    public boolean remove(
        final Vectorizable document)
    {
        return this.remove(document.convertToVector());
    }

    public boolean removeAll(
        final Iterable<? extends Vectorizable> documents)
    {
        boolean result = false;
        for (Vectorizable document : documents)
        {
            result = result | this.remove(document);
        }
        return result;
    }
}
