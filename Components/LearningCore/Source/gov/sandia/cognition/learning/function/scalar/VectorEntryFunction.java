/*
 * File:                VectorEntryFunction.java
 * Authors:             Dan Morrow and Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * An evaluator that returns the value of an input vector at a specified index.
 *
 * @author  Dan Morrow
 * @author  Justin Basilico
 * @since   3.0
 */
public class VectorEntryFunction
    extends AbstractScalarFunction<Vectorizable>
    implements Vectorizable
{
    /** The default index is {@value}. */
    public static final int DEFAULT_INDEX = 0;

    /** The index of the vector to get. */
    protected int index;

    /**
     * Creates a new {@code VectorEntryFunction} with an initial index of
     * 0.
     */
    public VectorEntryFunction()
    {
        this(DEFAULT_INDEX);
    }

    /**
     * Creates a new {@code VectorEntryFunction} with the given index.
     *
     * @param   index
     *      The index into a vector to get the output for.
     */
    public VectorEntryFunction(
        final int index)
    {
        super();

        this.setIndex(index);
    }

    @Override
    public VectorEntryFunction clone()
    {
        return (VectorEntryFunction) super.clone();
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof VectorEntryFunction
            && ((VectorEntryFunction) other).index == this.index;
    }

    @Override
    public int hashCode()
    {
        return this.index;
    }
    
    /**
     * Returns the vector value at the specified index.
     *
     * @param   input
     *      The input vector.
     * @return
     *      The value of the input vector at the given index.
     */
    @Override
    public double evaluateAsDouble(
        final Vectorizable input)
    {
        return input.convertToVector().getElement(this.index);
    }

    /**
     * Converts the index to select into a vector (of length 1).
     *
     * @return
     *      A 1-dimensional vector containing the index.
     */
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().createVector(1, this.getIndex());
    }

    /**
     * Converts a vector into the index to select from the vector.
     *
     * @param   parameters
     *      The parameter vector. Must be of dimensionality one.
     */
    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        // The parameters must be of dimensionality 1.
        parameters.assertDimensionalityEquals(1);

        // Set the index.
        this.setIndex((int) parameters.getElement(0));
    }

    /**
     * Gets the index into the vector that the function gets the value for.
     *
     * @return
     *      The index.
     */
    public int getIndex()
    {
        return this.index;
    }

    /**
     * Sets the index into the vector that the function gets the value for.
     *
     * @param   index
     *      The index.
     */
    public void setIndex(
        final int index)
    {
        if (index < 0)
        {
            throw new IllegalArgumentException("index must be non-negative");
        }
        
        this.index = index;
    }

}
