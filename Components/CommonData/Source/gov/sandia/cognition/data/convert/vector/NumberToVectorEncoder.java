/*
 * File:                NumberToVectorEncoder.java
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

import gov.sandia.cognition.math.matrix.Vector;

/**
 * An encoder that encodes a number as an element of a {@code Vector}.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class NumberToVectorEncoder
    extends AbstractToVectorEncoder<Number>
{

    /**
     * Creates a new {@code NumberToVectorEncoder}.
     */
    public NumberToVectorEncoder()
    {
        super();
    }

    /**
     * Encodes the given number into the given vector at the given index.
     * 
     * @param   number
     *      The number to encode.
     * @param   vector
     *      The vector to encode into.
     * @param   index
     *      The index to encode at.
     */
    public void encode(
        final Number number,
        final Vector vector,
        final int index)
    {
        final double value = number == null ? 0.0 : number.doubleValue();
        vector.setElement(index, value);
    }

    /**
     * Gets the dimensionality of the converter, which is 1.
     * 
     * @return  The dimensionality of the converter.
     */
    public int getOutputDimensionality()
    {
        return 1;
    }

}
