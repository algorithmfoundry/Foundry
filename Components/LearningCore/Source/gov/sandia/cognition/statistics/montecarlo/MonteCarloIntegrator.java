/*
 * File:                MonteCarloIntegrator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.WeightedValue;
import java.util.Collection;
import java.util.List;

/**
 * Monte Carlo integration is a way of compute the integral of a function using
 * samples from another.  If, as is typical, the samples come from a
 * probability distribution, then the result of Monte Carlo integration is
 * the expectation of the function under the probability distribution.  That is,
 * Expectation == integral( g(x) * p(x) dx ) ~= (1/n) sum(g(x)), where x ~ p(x).
 * @param <OutputType> Type of output from the integrator.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "Christian P. Robert",
                "George Casella"
            },
            title="Monte Carlo Statistical Methods, Second Edition",
            type=PublicationType.Book,
            year=2004,
            pages={83,106}
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Monte Carlo integration",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Monte_Carlo_integration"
        )

    }
)
public interface MonteCarloIntegrator<OutputType>
    extends CloneableSerializable
{

    /**
     * Integrates the given function given samples from another function.
     * @param <SampleType> Type of samples to consider.
     * @param samples Samples from the underlying distribution.
     * @param expectationFunction
     * Function for which to compute the expectation.
     * @return
     * Distribution of the integration.
     */
    public <SampleType> Distribution<? extends OutputType> integrate(
        Collection<? extends SampleType> samples,
        Evaluator<? super SampleType, ? extends OutputType> expectationFunction );

    /**
     * Integrates the given function given weighted samples from another
     * function.
     * @param <SampleType> Type of samples to consider.
     * @param samples Weighted samples from the underlying distribution.
     * @param expectationFunction
     * Function for which to compute the expectation.
     * @return
     * Distribution of the integration.
     */
    public <SampleType> Distribution<? extends OutputType> integrate(
        List<? extends WeightedValue<? extends SampleType>> samples,
        Evaluator<? super SampleType, ? extends OutputType> expectationFunction );


    /**
     * Computes the Monte Carlo distribution of the given samples.
     * @param samples
     * Samples to consider.
     * @return
     * Distribution describing the samples.
     */
    public Distribution<? extends OutputType> getMean(
        Collection<? extends OutputType> samples );

    /**
     * Computes the Monte Carlo distribution of the given weighted samples.
     * @param samples
     * Weighted samples to consider.
     * @return
     * Distribution describing the samples.
     */
    public Distribution<? extends OutputType> getMean(
        List<? extends WeightedValue<? extends OutputType>> samples );
}
