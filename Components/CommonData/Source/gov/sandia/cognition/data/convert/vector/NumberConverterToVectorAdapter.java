/*
 * File:                NumberConverterToVectorAdapter.java
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

import gov.sandia.cognition.data.convert.DataConverter;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Adapts a {@code DataConverter} that outputs a number to be a 
 * {@code VectorEncoder}.
 * 
 * @param   <InputType> 
 *      The input type to convert encode into a number, which is then adapted
 *      to be used in a {@code Vector}.
 * @author  Justin Basilico
 * @since   3.0
 */
public class NumberConverterToVectorAdapter<InputType>
    extends AbstractToVectorEncoder<InputType>
{

    /** The converter to adapt for use with {@code Vector}s. */
    protected DataConverter<? super InputType, ? extends Number> converter;

    /**
     * Creates a new {@code NumberConverterToVectorAdapter}.
     */
    public NumberConverterToVectorAdapter()
    {
        this(null);
    }

    /**
     * Creates a new {@code NumberConverterToVectorAdapter} for the given
     * converter.
     * 
     * @param   converter 
     *      The converter to adapt.
     */
    public NumberConverterToVectorAdapter(
        final DataConverter<? super InputType, ? extends Number> converter)
    {
        super();

        this.setConverter(converter);
    }

    /**
     * Encodes the given object into the vector at the given index by using the
     * number converter that this object is adapting.
     * 
     * @param   object
     *      The input object to encode.
     * @param   vector
     *      The vector to encode into.
     * @param   index
     *      The index to encode at.
     */
    public void encode(
        final InputType object,
        final Vector vector,
        final int index)
    {
        final Number number = this.getConverter().evaluate(object);
        vector.setElement(index, number == null ? 0.0 : number.doubleValue());
    }

    /**
     * Gets the dimensionality of this encoder, which is 1.
     * 
     * @return  The dimensionality of this encoder.
     */
    public int getOutputDimensionality()
    {
        return 1;
    }

    /**
     * Gets the number converter being adapted to work with vectors.
     * 
     * @return  The adapted converter.
     */
    public DataConverter<? super InputType, ? extends Number> getConverter()
    {
        return this.converter;
    }

    /**
     * Sets the number converter being adapted to work with vectors.
     * 
     * @param   converter The adapted converter.
     */
    public void setConverter(
        final DataConverter<? super InputType, ? extends Number> converter)
    {
        this.converter = converter;
    }

}
