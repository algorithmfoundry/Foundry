/*
 * File:                WeightedInputOutputPair.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.WeightedPair;

/**
 * The {@code WeightedInputOutputPair} class implements an additional
 * weighting term on an {@code InputOutputPair}, typically used to inform
 * learning algorithms of the relative weight between examples.
 *
 * @param  <InputType>
 *      The type for the input object in the pair.
 * @param  <OutputType>
 *      The type for the output object in the pair.
 * @author Kevin R. Dixon
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-04",
    changesNeeded=false,
    comments="Simple container class looks fine."
)
public interface WeightedInputOutputPair<InputType, OutputType>
    extends InputOutputPair<InputType, OutputType>,
        WeightedPair<InputType, OutputType>
{
}
