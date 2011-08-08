/*
 * File:                CumulativeDistributionFunction.java
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
import gov.sandia.cognition.evaluator.Evaluator;

/**
 * Functionality of a cumulative distribution function.  The CDF returns the
 * probability that the distribution takes a value less than a given input.
 * That is, CDF(x) = Probability(all z less than x).  Additionally, any CDF has
 * the following properties: it is nonnegative, nondecreasing,
 * CDF(-infinity)=0, CDF(infinity)=1.
 * <BR><BR>
 * Note that CumulativeDistributionFunction is NOT a DistributionFunction,
 * but merely an Evaluator.  This is because a DistributionFunction is meant
 * for PMFs/PDFs.
 * @param <NumberType> Type of Number used as the domain.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Cumulative distribution function",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Cumulative_distribution_function"
)
public interface CumulativeDistributionFunction<NumberType extends Number>
    extends UnivariateDistribution<NumberType>,
    Evaluator<NumberType,Double>
{
}
