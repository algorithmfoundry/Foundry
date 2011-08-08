/*
 * File:                AbstractReversibleDataConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert;

/**
 * Abstract implementation of sthe {@code ReversibleDataConverter} interface.
 * 
 * @param   <InputType>
 *      The input type to convert from.
 * @param   <OutputType>
 *      The output type to convert to.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractReversibleDataConverter<InputType, OutputType>
    extends AbstractDataConverter<InputType, OutputType>
    implements ReversibleDataConverter<InputType, OutputType>
{

    /**
     * Creates a new {@code AbstractReversibleDataConverter}.
     */
    public AbstractReversibleDataConverter()
    {
        super();
    }

}
