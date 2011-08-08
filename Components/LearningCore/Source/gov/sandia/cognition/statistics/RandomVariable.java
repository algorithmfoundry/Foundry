/*
 * File:                RandomVariable.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.Ring;

/**
 * Describes the functionality of a random variable.  That is, a distribution
 * that can be mathematically manipulated by other random variables using
 * the "Ring" operations.
 * @param <DataType> Type of data that can be sampled from the distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Random variable",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Random_variable"
)
public interface RandomVariable<DataType>
    extends Distribution<DataType>,
    Ring<RandomVariable<DataType>>
{
}
