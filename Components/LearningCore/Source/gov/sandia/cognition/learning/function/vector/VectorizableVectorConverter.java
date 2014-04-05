/*
 * File:                VectorizableVectorConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright December 3, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The {@code VectorizableVectorConverter} class implements a conversion 
 * between a {@code Vectorizable} and an {@code Vector} by calling the proper
 * conversion method on the {@code Vectorizable}.
 * 
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorizableVectorConverter
    extends AbstractCloneableSerializable
    implements Evaluator<Vectorizable, Vector>
{
    /**
     * Creates a new {@code VectorizableVectorConverter}.
     */
    public VectorizableVectorConverter()
    {
        super();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    @Override
    public VectorizableVectorConverter clone()
    {
        return (VectorizableVectorConverter) super.clone();
    }
    
    /**
     * Evaluates the given input by converting it to a vector by calling the
     * proper method on the given {@code Vectorizable}.
     * 
     * @param   input
     *      The input object.
     * @return
     *      The vector form of the input.
     */
    public Vector evaluate(
        final Vectorizable input)
    {
        return input.convertToVector();
    }
}
