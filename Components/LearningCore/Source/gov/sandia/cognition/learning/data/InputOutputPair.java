/*
 * File:                InputOutputPair.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.Pair;

/**
 * The InputOutputPair interface is just a container for an input and its
 * associated output used in supervised learning.
 *
 * @param  <InputType>
 *      The type for the input object in the pair.
 * @param  <OutputType>
 *      The type for the output object in the pair.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-04",
    changesNeeded=false,
    comments="Simple container class looks fine."
)
public interface InputOutputPair<InputType, OutputType>
    extends Pair<InputType,OutputType>
{

    /**
     * Gets the input.
     *
     * @return The input.
     */
    public InputType getInput();

    /**
     * Gets the output.
     *
     * @return The output.
     */
    public OutputType getOutput();
    
}
