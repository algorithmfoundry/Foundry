/*
 * File:                AbstractToVectorEncoder.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 02, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert.vector;

import gov.sandia.cognition.data.convert.AbstractDataConverter;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * An abstract implementation of the {@code DataToVectorEncoder} interface. It
 * chains together the typical calls to create a vector and allows a vector
 * factory to be stored on the encoder.
 * 
 * @param   <InputType> 
 *      The type of input data to be encoded into a {@code Vector}.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractToVectorEncoder<InputType>
    extends AbstractDataConverter<InputType, Vector>
    implements DataToVectorEncoder<InputType>
{

    /** The vector factory to use to create new vectors. */
    protected VectorFactory<?> vectorFactory;

    /**
     * Creates a new {@code AbstractToVectorEncoder}.
     */
    public AbstractToVectorEncoder()
    {
        this(VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractToVectorEncoder} with the given vector
     * factory.
     * 
     * @param   vectorFactory
     *      The {@code VectorFactory} to use.
     */
    public AbstractToVectorEncoder(
        final VectorFactory<?> vectorFactory)
    {
        super();

        this.vectorFactory = vectorFactory;
    }

    /**
     * Converts the given object to a {@code Vector}.
     * 
     * @param   input
     *      The object to convert.
     * @return
     *      The {@code Vector} representing the object.
     */
    public Vector evaluate(
        final InputType input)
    {
        // Create a new vector.
        final Vector result = this.vectorFactory.createVector(
            this.getOutputDimensionality());
        
        // Encode the input into the vector.
        this.encode(input, result);
        
        // Return the vector we've encoded.
        return result;
    }

    public void encode(
        final InputType object,
        final Vector vector)
    {
        this.encode(object, vector, 0);
    }

}
