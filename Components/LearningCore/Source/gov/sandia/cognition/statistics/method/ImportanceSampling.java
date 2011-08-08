/*
 * File:                ImportanceSampling.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import java.util.Random;

/**
 * Importance sampling is a technique for estimating properties of
 * a target distribution, while only having samples generated from an
 * "importance" distribution rather than the target distribution.
 * Typically, the importance distribution is easy to sample from, while the
 * target distribution is difficult to sample from, and the importance
 * distribution has support everywhere that the target distribution has
 * support.  Then, this results in an weighted set of samples
 * that are an unbiased sampling of the target distribution.
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Importance Sampling",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Importance_sampling"
)
public class ImportanceSampling
{

    /**
     * Importance sampling is a technique for estimating properties of
     * a target distribution, while only having samples generated from an
     * "importance" distribution rather than the target distribution.
     * Typically, the importance distribution is easy to sample from, while the
     * target distribution is difficult to sample from, and the importance
     * distribution has support everywhere that the target distribution has
     * support.  Then, this results in an weighted set of samples
     * that are an unbiased sampling of the target distribution.
     * @param <ValueType> Domain type of the distributions.
     * @param importanceDistribution
     * Easy-to-sample-from distribution that will generate the samples.
     * @param targetDistribution
     * The hard-to-sample-from distribution that is desired.
     * @param random
     * Random number generator.
     * @param numSamples
     * Number of samples to create.
     * @return
     * Weighted samples that are an unbiased estimate of the target
     * distribution.
     */
    public static <ValueType> ArrayList<DefaultWeightedValue<ValueType>> sample(
        ProbabilityDensityFunction<ValueType> importanceDistribution,
        Evaluator<ValueType,Double> targetDistribution,
        Random random,
        int numSamples )
    {

        ArrayList<? extends ValueType> importanceSamples =
            importanceDistribution.sample(random, numSamples);

        ArrayList<DefaultWeightedValue<ValueType>> weightedSamples =
            new ArrayList<DefaultWeightedValue<ValueType>>( numSamples );
        for( ValueType importanceSample : importanceSamples )
        {
            final double numerator = targetDistribution.evaluate(importanceSample);
            final double denominator = importanceDistribution.evaluate(importanceSample);
            double weight;
            if( denominator != 0.0 )
            {
                weight = numerator / denominator;
            }
            else
            {
                weight = 0.0;
            }
            weightedSamples.add( new DefaultWeightedValue<ValueType>( importanceSample, weight ) );
        }

        return weightedSamples;

    }
    
}
