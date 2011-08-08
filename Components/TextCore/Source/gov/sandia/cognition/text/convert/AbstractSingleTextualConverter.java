/*
 * File:                AbstractSingleTextConverter.java
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

import gov.sandia.cognition.data.convert.AbstractDataConverter;
import gov.sandia.cognition.text.Textual;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract implementation of the {@code SingleTextualConverter} interface.
 * It implements the {@code convert} and {@code convertAll} methods to call the
 * {@code evaluate} method.
 *
 * @param   <InputType>
 *      The type of input object to convert to text.
 * @param   <OutputType>
 *      The type of output textual representation.
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractSingleTextualConverter<InputType, OutputType extends Textual>
    extends AbstractDataConverter<InputType, OutputType>
    implements SingleTextualConverter<InputType, OutputType>
{

    /**
     * Creates a new {@code AbstractSingleTextualConverter}.
     */
    public AbstractSingleTextualConverter()
    {
        super();
    }

    @Override
    public OutputType convert(
        final InputType input)
    {
        return this.evaluate(input);
    }

    @Override
    public List<OutputType> convertAll(
        final Iterable<? extends InputType> inputs)
    {
        // Convert each input and add it to the result.
        final LinkedList<OutputType> result = new LinkedList<OutputType>();
        for (InputType input : inputs)
        {
            result.add(this.convert(input));
        }
        return result;
    }

}
