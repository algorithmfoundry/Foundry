/*
 * File:                DefaultInputOutputPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A default implementation of the {@code InputOutputPair} interface. It stores
 * a pointer to the input and output objects that make up the pair.
 *
 * @param  <InputType>
 *      The type for the input object in the pair.
 * @param  <OutputType>
 *      The type for the output object in the pair.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since   3.0
 */
public class DefaultInputOutputPair<InputType, OutputType>
    extends AbstractInputOutputPair<InputType, OutputType>
{

    /** The input. */
    private InputType input;

    /** The output associated with the input. */
    private OutputType output;

    /**
     * Creates a new {@code DefaultInputOutputPair} with both the input and
     * output as null.
     */
    public DefaultInputOutputPair()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code DefaultInputOutputPair} with the given input and
     * output.
     *
     * @param   input
     *      The input to store.
     * @param   output
     *      The output to store.
     */
    public DefaultInputOutputPair(
        final InputType input,
        final OutputType output)
    {
        super();

        this.setInput(input);
        this.setOutput(output);
    }

    /**
     * Creates a new {@code DefaultInputOutputPair} using the first element of
     * the given pair as the input and the second element of the given pair as
     * the output.
     *
     * @param   pair
     *      The pair to get the input and output from.
     */
    public DefaultInputOutputPair(
        final Pair<? extends InputType, ? extends OutputType> pair)
    {
        this(pair.getFirst(), pair.getSecond());
    }

    public InputType getInput()
    {
        return this.input;
    }

    /**
     * Sets the input.
     *
     * @param input The new input.
     */
    public void setInput(
        final InputType input)
    {
        this.input = input;
    }

    public OutputType getOutput()
    {
        return this.output;
    }

    /**
     * Sets the output.
     *
     * @param output The new output.
     */
    public void setOutput(
        final OutputType output)
    {
        this.output = output;
    }

    /**
     * Convenience method to create a new {@code DefaultInputOutputPair}.
     *
     * @param  <InputType>
     *      The type for the input object in the pair.
     * @param  <OutputType>
     *      The type for the output object in the pair.
     * @return
     *      A new default input-output pair.
     */
    public static <InputType, OutputType> DefaultInputOutputPair<InputType, OutputType>
        create()
    {
        return new DefaultInputOutputPair<InputType, OutputType>();
    }

    /**
     * Convenience method to create a new {@code DefaultInputOutputPair}.
     *
     * @param  <InputType>
     *      The type for the input object in the pair.
     * @param  <OutputType>
     *      The type for the output object in the pair.
     * @param input
     *      The input.
     * @param output
     *      The output.
     * @return
     *      A new default input-output pair with the given input and output.
     */
    public static <InputType, OutputType> DefaultInputOutputPair<InputType, OutputType>
        create(
        final InputType input,
        final OutputType output)
    {
        return new DefaultInputOutputPair<InputType, OutputType>(input, output);
    }

    /**
     * Takes two collections of data of equal size and creates a single
     * ArrayList of InputOutputPairs out of them.
     *
     * @param   <InputType>
     *      The type of the input.
     * @param   <OutputType>
     *      The type of the output.
     * @param   inputs
     *      A collection of the data to transform into the input element of the
     *      pair. Must have the same number of elements as outputs.
     * @param   outputs
     *      A collection of data to transform into the output element of the
     *      pair. Must have the same number of elements as inputs.
     * @return
     *      A new {@code ArrayList<InputOutputPair>} of the same size as the
     *      two given collections where the input is from the first collection
     *      and output is from the second.
     */
    public static <InputType, OutputType> ArrayList<DefaultInputOutputPair<InputType, OutputType>>
        mergeCollections(
        final Collection<InputType> inputs,
        final Collection<OutputType> outputs)
    {
        final int count = inputs.size();
        if (count != outputs.size())
        {
            throw new IllegalArgumentException(
                "The inputs and outputs collections must be the same size ("
                    + inputs.size() + " != " + outputs.size() + ")");
        }

        final ArrayList<DefaultInputOutputPair<InputType, OutputType>> result =
            new ArrayList<DefaultInputOutputPair<InputType, OutputType>>(count);
        final Iterator<OutputType> outputsIterator = outputs.iterator();
        for (InputType input : inputs)
        {
            result.add(create(input, outputsIterator.next()));
        }

        return result;

    }

    /**
     * Takes a collection of input values and a single output value and creates
     * a new collection of default input output pairs with each of the given
     * inputs and the given output.
     *
     * @param   <InputType>
     *      The type of the input.
     * @param   <OutputType>
     *      The type of the output.
     * @param   inputs
     *      A collection of the data to transform into the input element of the
     *      pair.
     * @param   output
     *      The value to use as the output element of the pair. (The label)
     * @return
     *      A new {@code ArrayList<InputOutputPair>} of the same size as the
     *      input collection where the inputs are from the given collection and
     *      outputs are all the given value.
     */
    public static <InputType, OutputType> ArrayList<DefaultInputOutputPair<InputType, OutputType>>
        labelCollection(
        final Collection<InputType> inputs,
        final OutputType output)
    {
        final int count = inputs.size();

        final ArrayList<DefaultInputOutputPair<InputType, OutputType>> result =
            new ArrayList<DefaultInputOutputPair<InputType, OutputType>>(count);
        for (InputType input : inputs)
        {
            result.add(create(input, output));
        }

        return result;
    }

}
