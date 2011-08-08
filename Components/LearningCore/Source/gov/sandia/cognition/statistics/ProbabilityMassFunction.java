/*
 * File:                ProbabilityMassFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * The {@code ProbabilityMassFunction} interface defines the functionality of
 * a probability mass function. 
 * 
 * A PMF has the following properties:
 *  - The domain is a countably finite set
 *  - The dependent values sum to 1.0
 *  - PMF(x) == "probability that a random variable takes value x"
 *  - PMF(x) >= 0.0 for all x
 *  - By consequence, PMF(x) <= 1.0 for all x
 *
 * @param <DataType> Value for the domain (x-axis, independent variable), may be
 * something like an Integer, etc.
 * @author Justin Basilico
 * @since  2.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Probability mass function",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Probability_mass_function"
)
public interface ProbabilityMassFunction<DataType>
    extends ProbabilityFunction<DataType>,
    DiscreteDistribution<DataType>
{

    public ProbabilityMassFunction<DataType> getProbabilityFunction();

    /**
     * Gets the entropy of the values in the histogram.
     *
     * @return The entropy of the values in the histogram.
     */
    public double getEntropy();
    
}
