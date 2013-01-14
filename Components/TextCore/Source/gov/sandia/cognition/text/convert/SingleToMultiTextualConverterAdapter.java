/*
 * File:                SingleToMultiTextualConverterAdapter.java
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
import java.util.Collections;

/**
 * Adapts a {@code SingleTextualConverter} to work within the interface of an
 * {@code MultiTextualConverter}.
 *
 * @param   <InputType>
 *      The type of input that can be converted to a textual form.
 * @param   <OutputType>
 *      The type of textual output of the converter. Must implement the
 *      {@code Textual} interface.
 * @author  Justin Basilico
 * @since   3.1
 */
public class SingleToMultiTextualConverterAdapter<InputType, OutputType extends Textual>
    extends AbstractMultiTextualConverter<InputType, OutputType>
{

    /** The single text converter being wrapped. */
    protected SingleTextualConverter<? super InputType, ? extends OutputType> converter;

    /**
     * Creates a new {@code SingleToMultiTextualConverterAdapter} with no
     * internal converter.
     */
    public SingleToMultiTextualConverterAdapter()
    {
        this(null);
    }

    /**
     * Creates a new {@code SingleToMultiTextualConverterAdapter} with the given
     * internal converter.
     *
     * @param   converter
     *      The internal converter.
     */
    public SingleToMultiTextualConverterAdapter(
        final SingleTextualConverter<? super InputType, ? extends OutputType> converter)
    {
        super();

        this.setConverter(converter);
    }

    @Override
    public Iterable<OutputType> evaluate(
        final InputType input)
    {
        // Wrap the output as a singleton list.
        return Collections.<OutputType>singletonList(this.getConverter().evaluate(input));
    }

    /**
     * Gets the internal single textual converter being wrapped.
     *
     * @return
     *      The internal single textual converter.
     */
    public SingleTextualConverter<? super InputType, ? extends OutputType> getConverter()
    {
        return this.converter;
    }

    /**
     * Sets the internal single textual converter being wrapped.
     *
     * @param   converter
     *      The internal single textual converter.
     */
    public void setConverter(
        final SingleTextualConverter<? super InputType, ? extends OutputType> converter)
    {
        this.converter = converter;
    }

}
