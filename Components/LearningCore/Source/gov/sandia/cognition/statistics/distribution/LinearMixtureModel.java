/*
 * File:                LinearMixtureModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * A linear mixture of RandomVariables, with a prior probability distribution.
 * The posterior pdf is:
 * p(x|this) = \sum_{y\in this} p(x|y,this)P(y|this),
 * where p(x|y,this) is the pdf of the underlying RandomVariable, and
 * P(y|this) is the prior probability of RandomVariable y in this.
 *
 * @param <DataType> 
 *      Type of data in this mixture model
 * @param <DistributionType>
 *      The type of the internal distributions inside the mixture.
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Mixture Model",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Mixture_model"
)
public abstract class LinearMixtureModel<DataType, DistributionType extends Distribution<DataType>>
    extends AbstractDistribution<DataType>
{
    
    /**
     * Underlying distributions from which we sample
     */
    protected ArrayList<? extends DistributionType> distributions;

    /**
     * Weights proportionate by which the distributions are sampled
     */
    protected double[] priorWeights;

    /**
     * Creates a new instance of LinearMixtureModel
     * @param distributions
     * Underlying distributions from which we sample
     */
    public LinearMixtureModel(
        Collection<? extends DistributionType> distributions )
    {
        this( distributions, null );
    }

    /**
     * Creates a new instance of LinearMixtureModel
     * @param distributions
     * Underlying distributions from which we sample
     * @param priorWeights
     * Weights proportionate by which the distributions are sampled
     */
    public LinearMixtureModel(
        Collection<? extends DistributionType> distributions,
        double[] priorWeights)
    {

        if( priorWeights == null )
        {
            priorWeights = new double[distributions.size()];
            Arrays.fill(priorWeights, 1.0);
        }

        if( distributions.size() != priorWeights.length )
        {
            throw new IllegalArgumentException(
                "Distribution count must equal number of priors" );
        }
        for( int i = 0; i < priorWeights.length; i++ )
        {
            if( priorWeights[i] < 0.0 )
            {
                throw new IllegalArgumentException( "weights must be >= 0.0!" );
            }
        }

        this.setDistributions(CollectionUtil.asArrayList(distributions));
        this.setPriorWeights(priorWeights);
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinearMixtureModel<DataType,DistributionType> clone()
    {
        LinearMixtureModel<DataType,DistributionType> clone = 
            (LinearMixtureModel<DataType, DistributionType>) super.clone();
        clone.setDistributions( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getDistributions() ) );
        clone.setPriorWeights( ObjectUtil.cloneSmart( this.getPriorWeights() ) );
        return clone;
    }

    @Override
    public String toString()
    {
        StringBuilder retval = new StringBuilder(1000);
        retval.append("LinearMixtureModel has " + this.getDistributionCount() + " distributions:\n");
        int k = 0;
        for( DistributionType distribution : this.getDistributions() )
        {
            retval.append( " " + k + ": Prior: " + this.getPriorWeights()[k] + ", Distribution: " + distribution + "\n" );
            k++;
        }
        return retval.toString();
    }

    /**
     * Getter for distributions
     * @return
     * Underlying distributions from which we sample
     */
    public ArrayList<? extends DistributionType> getDistributions()
    {
        return this.distributions;
    }

    /**
     * Setter for distributions
     * @param distributions
     * Underlying distributions from which we sample
     */
    public void setDistributions(
        ArrayList<? extends DistributionType> distributions)
    {
        this.distributions = distributions;
    }
    
    /**
     * Gets the number of distributions in the model
     * @return
     * Number of distributions in the model
     */
    public int getDistributionCount()
    {
        return this.distributions.size();
    }

    @Override
    public DataType sample(
        Random random)
    {
        DistributionType d = ProbabilityMassFunctionUtil.sampleSingle(
            this.getPriorWeights(), this.getDistributions(), random);
        return d.sample(random);
    }

    @Override
    public ArrayList<DataType> sample(
        Random random,
        int numSamples)
    {

        final int N = this.getDistributionCount();
        double[] cumulativeWeights = new double[ N ];
        double sum = 0.0;
        for( int n = 0; n < N; n++ )
        {
            sum += this.getPriorWeights()[n];
            cumulativeWeights[n] = sum;
        }

        ArrayList<DistributionType> whichDistributions =
            ProbabilityMassFunctionUtil.sampleMultiple(
                cumulativeWeights, sum, this.getDistributions(), random, numSamples);
        ArrayList<DataType> samples = new ArrayList<DataType>( numSamples );
        for( DistributionType d : whichDistributions )
        {
            samples.add( d.sample(random) );
        }
        return samples;
    }

    /**
     * Getter for priorWeights
     * @return
     * Weights proportionate by which the distributions are sampled
     */
    public double[] getPriorWeights()
    {
        return this.priorWeights;
    }

    /**
     * Getter for priorWeights
     * @param priorWeights
     * Weights proportionate by which the distributions are sampled
     */
    public void setPriorWeights(
        double[] priorWeights)
    {
        this.priorWeights = priorWeights;
    }

    /**
     * Computes the sum of the prior weights
     * @return
     * Sum of the prior weights
     */
    public double getPriorWeightSum()
    {
        double sum = 0.0;
        final int K = this.getPriorWeights().length;
        for( int k = 0; k < K; k++ )
        {
            sum += this.getPriorWeights()[k];
        }
        return (sum <= 0.0) ? 1.0 : sum;
    }

}
