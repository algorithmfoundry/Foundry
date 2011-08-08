/*
 * File:                DataToVectorEncoder.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 30, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert.vector;

import gov.sandia.cognition.data.convert.DataConverter;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;

/**
 * Defines a converter that can be used to encode data into a {@code Vector}.
 * 
 * @param   <InputType> 
 *      The type of input data to be encoded into a {@code Vector}.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface DataToVectorEncoder<InputType>
    extends DataConverter<InputType, Vector>, VectorOutputEvaluator<InputType, Vector>
{

    /**
     * Encodes the given object into the given {@code Vector}. It starts at
     * index zero in the {@code Vector}.
     * 
     * @param   object
     *      The object to encode.
     * @param   vector
     *      The vector to encode the object into.
     */
    public void encode(
        final InputType object,
        final Vector vector);

    /**
     * Encodes the given object into the given {@code Vector}, starting at the
     * given index.
     * 
     * @param   object
     *      The object to encode.
     * @param   vector
     *      The vector to encode the object into.
     * @param   startIndex
     *      The index to start the encoding at.
     */
    public void encode(
        final InputType object,
        final Vector vector,
        final int startIndex);

    /**
     * Gets size of the the {@code Vector} created by the encoder.
     * 
     * @return  The dimensionality of the {@code Vector} created by the 
     *      encoder.
     */
    public int getOutputDimensionality();

}
