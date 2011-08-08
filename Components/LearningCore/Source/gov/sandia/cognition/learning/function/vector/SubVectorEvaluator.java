/*
 * File:                SubVectorEvaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 06, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.DefaultVectorFactoryContainer;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Extracts the given set of indices from an input vector to create a new
 * vector containing the input vector's elements at those indices.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class SubVectorEvaluator
    extends DefaultVectorFactoryContainer
    implements VectorInputEvaluator<Vectorizable, Vector>,
        VectorOutputEvaluator<Vectorizable, Vector>
{

    /** The expected dimensionality of the input. All the subIndices must be
     *  less than this value. */
    protected int inputDimensionality;

    /** The indices to pull out of an input vector to create a new vector from.
     */
    protected int[] subIndices;

    /**
     * Creates a new {@code SubVectorEvaluator}. The dimensionality and indices
     * are initialized to invalid values and must be set later before use.
     */
    public SubVectorEvaluator()
    {
        this(-1, null);
    }

    /**
     * Creates a new {@code SubVectorEvaluator} with the given parameters.
     *
     * @param   inputDimensionality
     *      The dimensionality of the expected input vectors. This is checked
     *      against all the incoming vectors.
     * @param   subIndices
     *      The indices used to create the sub-vector. Must be between 0
     *      (inclusive) and the input dimensionality (exclusive).
     */
    public SubVectorEvaluator(
        final int inputDimensionality,
        final int[] subIndices)
    {
        this(inputDimensionality, subIndices, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code SubVectorEvaluator} with the given parameters.
     *
     * @param   inputDimensionality
     *      The dimensionality of the expected input vectors. This is checked
     *      against all the incoming vectors.
     * @param   subIndices
     *      The indices used to create the sub-vector. Must be between 0
     *      (inclusive) and the input dimensionality (exclusive).
     * @param   vectorFactory
     *      The vector factory used to create the new sub-vector.
     */
    public SubVectorEvaluator(
        final int inputDimensionality,
        final int[] subIndices,
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);

        this.setInputDimensionality(inputDimensionality);
        this.setSubIndices(subIndices);
    }

    public Vector evaluate(
        final Vectorizable input)
    {
        if (input == null)
        {
            // Pass through null.
            return null;
        }

        // Convert the input to a vector.
        final Vector vector = input.convertToVector();
        if (vector == null)
        {
            // Pass through null.
            return null;
        }

        // The input vector has to be of the proper dimensionality.
        vector.assertDimensionalityEquals(this.inputDimensionality);

        // Create the sub-vector to fill in.
        final int subDimensionality = this.subIndices.length;
        final Vector subVector = this.getVectorFactory().createVector(
            subDimensionality);

        // Fill in the sub-vector.
        for (int i = 0; i < subDimensionality; i++)
        {
            subVector.setElement(i, vector.getElement(this.subIndices[i]));
        }

        return subVector;
    }

    public int getInputDimensionality()
    {
        return this.inputDimensionality;
    }

    /**
     * Sets the expected input dimensionality.
     *
     * @param   inputDimensionality
     *      The expected input dimensionality.
     */
    public void setInputDimensionality(
        final int inputDimensionality)
    {
        this.inputDimensionality = inputDimensionality;
    }

    public int getOutputDimensionality()
    {
        return this.subIndices.length;
    }

    /**
     * Gets the array of indices used to create the sub-vector.
     *
     * @return
     *      The array of indices used to create the sub-vector.
     */
    public int[] getSubIndices()
    {
        return this.subIndices;
    }

    /**
     * Sets the array of sub-indices. They should all be between 0 (inclusive)
     * and the expected input dimensionality (exclusive). The size and ordering
     * of the array itself is unconstrained as long as the indices are valid.
     *
     * @param   subIndices
     *      The array of indices to use to create a sub-vector from an input
     *      vector.
     */
    public void setSubIndices(
        final int[] subIndices)
    {
        this.subIndices = subIndices;
    }

}
