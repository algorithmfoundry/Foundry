/*
 * File:                DefaultWeightedInputOutputPair.java
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
 * A default implementation of the {@code WeightedInputOutputPair} interface.
 * Stores pointers to the input and output plus the weight as a double.
 * 
 * @param  <InputType>
 *      The type for the input object in the pair.
 * @param  <OutputType>
 *      The type for the output object in the pair.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultWeightedInputOutputPair<InputType, OutputType>
    extends DefaultInputOutputPair<InputType, OutputType>
    implements WeightedInputOutputPair<InputType, OutputType>
{
    /** The default weight is {@value}. */
    public static final double DEFAULT_WEIGHT = 1.0;

    /** Weighting term for the InputOutputPair. */
    private double weight;

    /**
     * Creates a new {@code DefaultWeightedInputOutputPair} with null as the
     * input and output and a default weight of 1.0.
     */
    public DefaultWeightedInputOutputPair()
    {
        this(null, null, 1.0);
    }

    /**
     * Creates a new {@code DefaultWeightedInputOutputPair} with the given
     * input, output, and weight.
     *
     * @param   input
     *      The input.
     * @param   output
     *      The output.
     * @param   weight
     *      The weight.
     */
    public DefaultWeightedInputOutputPair(
        final InputType input,
        final OutputType output,
        final double weight)
    {
        super(input, output);

        this.setWeight(weight);
    }

    /**
     * Creates a new {@code DefaultWeightedInputOutputPair} with the given
     * input and output from the given pair plus a weight.
     *
     * @param   pair
     *      The pair to provide the input and output values.
     * @param   weight
     *      The weight.
     */
    public DefaultWeightedInputOutputPair(
        final Pair<? extends InputType, ? extends OutputType> pair,
        final double weight)
    {
        this(pair.getFirst(), pair.getSecond(), weight);
    }

    /**
     * Creates a new {@code DefaultWeightedInputOutputPair} that is a shallow
     * copy of the given {@code WeightedInputOuptutPair}.
     *
     * @param   other
     *      The other weighted input-output pair to copy the values of.
     */
    public DefaultWeightedInputOutputPair(
        final WeightedInputOutputPair<? extends InputType, ? extends OutputType> other)
    {
        this(other.getInput(), other.getOutput(), other.getWeight());
    }

    public double getWeight()
    {
        return this.weight;
    }

    /**
     * Sets the weight for the pair.
     *
     * @param weight Weighting term for the InputOutputPair.
     */
    public void setWeight(
        final double weight)
    {
        this.weight = weight;
    }

    /**
     * Convenience method to create a new, empty
     * {@code DefaultWeightedInputOutputPair}.
     *
     * @param  <InputType>
     *      The type for the input object in the pair.
     * @param  <OutputType>
     *      The type for the output object in the pair.
     * @return
     *      A new, empty {@code DefaultWeightedInputOutputPair}.
     */
    public static <InputType, OutputType> DefaultWeightedInputOutputPair<InputType, OutputType>
        create()
    {
        return new DefaultWeightedInputOutputPair<InputType, OutputType>();
    }

    /**
     * Convenience method to create a new 
     * {@code DefaultWeightedInputOutputPair}.
     *
     * @param  <InputType>
     *      The type for the input object in the pair.
     * @param  <OutputType>
     *      The type for the output object in the pair.
     * @param input
     *      The input.
     * @param output
     *      The output.
     * @param weight
     *      The weight.
     * @return
     *      A new default weighted input-output pair with the given input,
     *      output, and weight.
     */
    public static <InputType, OutputType> DefaultWeightedInputOutputPair<InputType, OutputType>
        create(
        final InputType input,
        final OutputType output,
        final double weight)
    {
        return new DefaultWeightedInputOutputPair<InputType, OutputType>(input, output, weight);
    }


    /**
     * Takes two Collections of data and creates a single
     * ArrayList<WeightedInputOutputPair> out of them.
     *
     * @param   <InputType> The type of the input.
     * @param   <OutputType> The type of the output.
     * @param inputs
     * Collection of the data to transform into the input of the
     * WeightedInputOutputPair, must have the same size as targets
     * @param outputs
     * Collection of the data to transform into the output of the
     * WeightedInputOutputPair, must have the same size as inputs
     * @param weights
     * Collection of weights, must have the same size as inputs/targets
     * @return
     * ArrayList<WeightedInputOutputPair> of the same type as the input/output
     * Collections
     */
    public static <InputType, OutputType> ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>>
        mergeCollections(
        final Collection<InputType> inputs,
        final Collection<OutputType> outputs,
        final Collection<? extends Number> weights)
    {
        final int count = inputs.size();
        if ((count != outputs.size()) &&
            (count != weights.size()))
        {
            throw new IllegalArgumentException(
                "The inputs, outputs, and weights collections "
                + "must be the same size ("
                + inputs.size() + ", " + outputs.size() + ", " + weights.size()
                + ")");
        }

        final Iterator<InputType> ii = inputs.iterator();
        final Iterator<OutputType> io = outputs.iterator();
        final Iterator<? extends Number> iw = weights.iterator();
        final ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>> result =
            new ArrayList<DefaultWeightedInputOutputPair<InputType, OutputType>>(count);

        for (int n = 0; n < count; n++)
        {
            result.add(create(ii.next(), io.next(), iw.next().doubleValue()));
        }
        
        return result;
    }

}
