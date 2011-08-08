/*
 * File:                ProbabilityDensityFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * Defines a probability density function.  A PDF is a nonnegative function
 * that integrates to 1.0.  The integral between "a" and "b" is the probability
 * of the distribution between "a" and "b".  Also, we define PDFs to be
 * closed-form distributions, that is, having tunable parameters.
 * 
 * @param <DataType> Type of data on the domain.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Probability density function",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Probability_density_function"
)
public interface ProbabilityDensityFunction<DataType>
    extends ClosedFormDistribution<DataType>,
    ProbabilityFunction<DataType>
{

    @Override
    public ProbabilityDensityFunction<DataType> getProbabilityFunction();

}
