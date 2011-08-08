/*
 * File:                TextualConverter.java
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

/**
 * Interface for a class that can convert some type of object into one or more
 * textual objects. This is usually done to encapsulate a strategy for
 * transforming an object so that it can be used in text analysis, such as
 * being fed into tokenization for a term extraction pipeline.
 *
 * @param   <InputType>
 *      The type of input that can be converted to a textual form.
 * @param   <OutputType>
 *      The type of textual output of the converter. Must implement the
 *      {@code Textual} interface.
 * @author  Justin Basilico
 * @since   3.1
 * @see     SingleTextualConverter
 * @see     MultiTextualConverter
 */
public interface TextualConverter<InputType, OutputType extends Textual>
{

    /**
     * Convert the given input objects into zero or more textual objects.
     * Typically the result is the concatenation of calls to the single
     * {@code convert} methods.
     *
     * @param   inputs
     *      The inputs to convert.
     * @return
     *      Zero or more textual objects.
     */
    public Iterable<OutputType> convertAll(
        final Iterable<? extends InputType> inputs);

}
