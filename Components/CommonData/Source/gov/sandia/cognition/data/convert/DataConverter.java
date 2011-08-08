/*
 * File:                DataConverter.java
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

import gov.sandia.cognition.evaluator.Evaluator;

/**
 * Defines an object used to convert data from one type to another.
 * 
 * @param   <InputType> The input type to convert from.
 * @param   <OutputType> The output type to convert to.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface DataConverter<InputType, OutputType>
    extends Evaluator<InputType, OutputType>
{
}
