/*
 * File:                DiscreteTimeFilter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.StatefulEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * A discrete-time filter.
 * @param <StateType> Type of state to use to store the previous history.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Digital filter",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Digital_filter"
)
public interface DiscreteTimeFilter<StateType extends CloneableSerializable>
    extends StatefulEvaluator<Double,Double,StateType>,
    Vectorizable
{
}
