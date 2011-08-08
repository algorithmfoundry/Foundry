/*
 * File:                VectorizableVectorConverterWithBias.java
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

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * The {@code VectorizableVectorConverterWithBias} class extends the
 * {@code VectorizableVectorConverter} class to append a constant bias value of
 * 1.0 to the vector returned by the converter.
 * 
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorizableVectorConverterWithBias
    extends VectorizableVectorConverter
{

    /** The factory used to create the vector. */
    protected VectorFactory vectorFactory;

    /**
     * Creates a new instance of {@code VectorizableVectorConverterWithBias}.
     * It uses the default vector factory.
     */
    public VectorizableVectorConverterWithBias()
    {
        this(VectorFactory.getDefault());
    }
    
    /**
     * Creates a new instance of {@code VectorizableVectorConverterWithBias}.
     * 
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public VectorizableVectorConverterWithBias(
        final VectorFactory vectorFactory)
    {
        super();

        this.setVectorFactory(vectorFactory);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    @Override
    public VectorizableVectorConverterWithBias clone()
    {
        return (VectorizableVectorConverterWithBias) super.clone();
    }

    /**
     * Evaluates the given vectorizable input by converting it to a vector
     * and then creating a new vector of one extra dimensionality and adding a 
     * single element with bias 1.0 to the end.
     * 
     * @param   input 
     *      The input to evaluate.
     * @return
     *      The vector version of the input with a constant bias term added.
     */
    @Override
    public Vector evaluate(
        final Vectorizable input)
    {
        final Vector baseVector = input.convertToVector();
        final int dimensionality = baseVector.getDimensionality();
        final Vector withBias = this.vectorFactory.createVector(
            dimensionality + 1);

        // Copy the original values.
        for (int i = 0; i < dimensionality; i++)
        {
            withBias.setElement(i, baseVector.getElement(i));
        }

        // Set the bias element as the last element.
        withBias.setElement(dimensionality, 1.0);

        return withBias;
    }

    /**
     * Gets the vector factory used to create the vector with the bias.
     * 
     * @return The vector factory used to create the vector with the bias.
     */
    public VectorFactory getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the vector factory used to create the vector with the bias.
     * @param   vectorFactory
     *          The vector factory used to create the vector with the bias.
     */
    public void setVectorFactory(
        final VectorFactory vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

}
