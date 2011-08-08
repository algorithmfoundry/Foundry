/*
 * File:                AbstractMultiTextualConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Text Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.text.convert;

import gov.sandia.cognition.text.Textual;
import java.util.LinkedList;

/**
 * An abstract implementation of the {@code MultiTextualConverter} interface.
 *
 * @param   <InputType>
 *      The type of input that can be converted to a textual form.
 * @param   <OutputType>
 *      The type of textual output of the converter. Must implement the
 *      {@code Textual} interface.
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractMultiTextualConverter<InputType, OutputType extends Textual>
    extends AbstractTextualConverter<InputType, OutputType>
    implements MultiTextualConverter<InputType, OutputType>
{

    /**
     * Creates a new {@code AbstractMultiTextualConverter}.
     */
    public AbstractMultiTextualConverter()
    {
        super();
    }

    @Override
    public Iterable<OutputType> convert(
        final InputType input)
    {
        return this.evaluate(input);
    }

    @Override
    public Iterable<OutputType> convertAll(
        final Iterable<? extends InputType> inputs)
    {
        // Convert each one and add the output to the result.
        final LinkedList<OutputType> result = new LinkedList<OutputType>();
        for (InputType input : inputs)
        {
            for (OutputType output : this.convert(input))
            {
                result.add(output);
            }
        }
        return result;
    }

}
