/*
 * File:                ImportanceSampler.java
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

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
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
 * @param <DataType> Type of Data sampled.
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
public class ImportanceSampler<DataType>
    extends AbstractCloneableSerializable
    implements MonteCarloSampler<DataType,WeightedValue<DataType>,Evaluator<? super DataType,Double>>
{

    /**
     * Importance distribution from which we sample and weight by the
     * target distribution.
     */
    private ProbabilityFunction<DataType> importanceDistribution;

    /** 
     * Creates a new instance of ImportanceSampler 
     */
    public ImportanceSampler()
    {
        this( null );
    }

    /**
     * Creates a new instance of ImportanceSampler.
     * @param importanceDistribution
     * Importance distribution from which we sample and weight by the
     * target distribution.
     */
    public ImportanceSampler(
        ProbabilityDensityFunction<DataType> importanceDistribution )
    {
        this.setImportanceDistribution(importanceDistribution);
    }

    @Override
    public ImportanceSampler<DataType> clone()
    {
        @SuppressWarnings("unchecked")
        ImportanceSampler<DataType> clone =
            (ImportanceSampler<DataType>) super.clone();
        clone.setImportanceDistribution(
            ObjectUtil.cloneSafe( this.getImportanceDistribution() ) );
        return clone;
    }

    public ArrayList<DefaultWeightedValue<DataType>> sample(
        Evaluator<? super DataType,Double> targetFunction,
        Random random,
        int numSamples)
    {
        ArrayList<? extends DataType> importanceSamples =
            this.importanceDistribution.sample(random, numSamples);
        ArrayList<DefaultWeightedValue<DataType>> weightedSamples =
            new ArrayList<DefaultWeightedValue<DataType>>( numSamples );
        for( DataType importanceSample : importanceSamples )
        {
            double weight = targetFunction.evaluate(importanceSample) /
                this.importanceDistribution.evaluate(importanceSample);
            weightedSamples.add(
                new DefaultWeightedValue<DataType>( importanceSample, weight ) );
        }

        return weightedSamples;
    }

    /**
     * Getter for importanceDistribution.
     * @return
     * Importance distribution from which we sample and weight by the
     * target distribution.
     */
    public ProbabilityFunction<DataType> getImportanceDistribution()
    {
        return this.importanceDistribution;
    }

    /**
     * Setter for importanceDistribution.
     * @param importanceDistribution
     * Importance distribution from which we sample and weight by the
     * target distribution.
     */
    public void setImportanceDistribution(
        ProbabilityFunction<DataType> importanceDistribution)
    {
        this.importanceDistribution = importanceDistribution;
    }

}
