/*
 * File:                ReversibleDataConverter.java
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

package gov.sandia.cognition.data.convert;

/**
 * Represents a {@code DataConverter} whose conversion can be reversed. The
 * reverse is just another {@code DataConverter}. It is required that the
 * converter's range must be part of the domain of the reverse converter.
 * 
 * @param   <InputType>
 *      The input type to convert from.
 * @param   <OutputType>
 *      The output type to convert to.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface ReversibleDataConverter<InputType, OutputType>
    extends DataConverter<InputType, OutputType>
{

    /**
     * Gets the data converter that performs the reverse conversion.
     * 
     * @return The reverse converter.
     */
    public DataConverter<? super OutputType, ? extends InputType> reverse();

}
