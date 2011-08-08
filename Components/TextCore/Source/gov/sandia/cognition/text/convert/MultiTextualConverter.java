/*
 * File:                MultiTextualConverter.java
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

import gov.sandia.cognition.data.convert.DataConverter;
import gov.sandia.cognition.text.Textual;

/**
 * Interface for an {@code  TextConverter} that converts an input into possibly
 * multiple output textual objects. This is usually done to encapsulate a
 * strategy for transforming an object so that it can be used in text analysis,
 * such as being fed into tokenization for a term extraction pipeline.
 * 
 * @param   <InputType>
 *      The input type to convert.
 * @param   <OutputType>
 *      The output textual representation.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface MultiTextualConverter<InputType, OutputType extends Textual>
    extends DataConverter<InputType, Iterable<OutputType>>, TextualConverter<InputType, OutputType>
{
    
    /**
     * Convert the input object into zero or more textual objects.
     *
     * @param   input
     *      The input to convert.
     * @return
     *      Zero or more textual objects.
     */
    public Iterable<OutputType> convert(
        final InputType input);

}
