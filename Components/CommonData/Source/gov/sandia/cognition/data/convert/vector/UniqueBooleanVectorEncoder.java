/*
 * File:                UniqueBooleanVectorEncoder.java
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

import gov.sandia.cognition.math.matrix.Vector;
import java.util.List;

/**
 * An encoder for arbitrary objects that encodes an equality comparison between
 * a given input and a set of unique values. It does an equality comparison to 
 * each specified value and encodes those booleans using a given boolean 
 * encoding. Typically, this encoding uses a single dimension for each value,
 * but it supports boolean encodings of higher dimensionalities. This encoding
 * is sometimes refered to as a "grandmother" encoding for neural networks, 
 * after the idea that there is a single neuron for each concept, such as your
 * grandmother. The encoding is called a unique encoding because if a given
 * input is in the set of possible values, a single true value will be encoded.
 * 
 * @param   <InputType>
 *      The input type to encode as a unique values in a {@code Vector}.
 * @author  Justin Basilico
 * @since   3.0
 */
public class UniqueBooleanVectorEncoder<InputType>
    extends AbstractToVectorEncoder<InputType>
{

    /** The set of possible unique values. */
    protected List<InputType> values;

    /** The boolean encoder for the equality comparison between each of the
     *  possible values and a given input. */
    protected DataToVectorEncoder<Boolean> booleanConverter;

    /**
     * Creates a new {@code UniqueBooleanVectorEncoder}.
     * 
     * @param   values The list of possible values.
     * @param   booleanConverter The boolean converter for the values.
     */
    public UniqueBooleanVectorEncoder(
        final List<InputType> values,
        final DataToVectorEncoder<Boolean> booleanConverter)
    {
        super();

        this.setValues(values);
        this.setBooleanConverter(booleanConverter);
    }

    /**
     * Encodes the given object into the given vector at the given starting 
     * index by using a unique boolean encoding, where the given input value is
     * compared to each of the encoder's values using equality. If the input is 
     * in the set of unique values, a single unique true boolean will be 
     * encoded, otherwise, all of the encoded values will be false.
     * 
     * @param   object 
     *      The object to encode.
     * @param   vector
     *      The vector to encode into.
     * @param   startIndex
     *      The index to start the encoding at.
     */
    public void encode(
        final InputType object,
        final Vector vector,
        final int startIndex)
    {
        // Handle the case where the input object is null.
        final boolean isNull = object == null;

        // Loop through the possible values and determine equality.
        int index = startIndex;
        for (InputType value : this.values)
        {
            // Determine if the input equals this value.
            final Boolean b = isNull ? null : value.equals(object);

            // Encode the equality.
            this.booleanConverter.encode(b, vector, index);

            // Increment the index.
            index += booleanConverter.getOutputDimensionality();
        }
    }

    /**
     * Gets the dimensionality of the vector created by this converter, which
     * is the number of possible values times the size of the boolean encoding.
     * 
     * @return  The dimensionality of the vector created by this encoder.
     */
    public int getOutputDimensionality()
    {
        return this.values.size() * this.booleanConverter.getOutputDimensionality();
    }

    /**
     * Gets the list of unique values that the encoder is to use.
     * 
     * @return  The list of unique values.
     */
    public List<InputType> getValues()
    {
        return this.values;
    }

    /**
     * Sets the list of unique values that the encoder is to use.
     * 
     * @param   values The list of unique values.
     */
    protected void setValues(
        final List<InputType> values)
    {
        this.values = values;
    }

    /**
     * Gets the boolean converter used to encode the equality comparison 
     * between each of the unique values and a given input.
     * 
     * @return  The boolean converter to use.
     */
    public DataToVectorEncoder<Boolean> getBooleanConverter()
    {
        return this.booleanConverter;
    }

    /**
     * Sets the boolean converter used to encode the equality comparison 
     * between each of the unique values and a given input.
     * 
     * @param   booleanConverter The boolean converter to use.
     */
    protected void setBooleanConverter(
        final DataToVectorEncoder<Boolean> booleanConverter)
    {
        this.booleanConverter = booleanConverter;
    }

}
