/*
 * File:                MultivariateMixtureDensityModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 12, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Arrays;
import java.util.Collection;

/**
 * A LinearMixtureModel of multivariate distributions with associated PDFs.
 * @param <DistributionType>
 * Type of Distribution in the mixture
 * @author Kevin R. Dixon
 * @since 3.1
 */
@PublicationReference(
    author="Wikipedia",
    title="Mixture Model",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Mixture_model"
)
public class MultivariateMixtureDensityModel<DistributionType extends ClosedFormComputableDistribution<Vector>>
    extends LinearMixtureModel<Vector, DistributionType>
    implements ClosedFormComputableDistribution<Vector>
{

    /**
     * Creates a new instance of MultivariateMixtureDensityModel
     * @param distributions
     * Underlying distributions from which we sample
     */
    public MultivariateMixtureDensityModel(
        DistributionType ... distributions )
    {
        this( Arrays.asList( distributions ) );
    }


    /**
     * Creates a new instance of MultivariateMixtureDensityModel
     * @param distributions
     * Underlying distributions from which we sample
     */
    public MultivariateMixtureDensityModel(
        Collection<? extends DistributionType> distributions )
    {
        this( distributions, null );
    }

    /**
     * Creates a new instance of MultivariateMixtureDensityModel
     * @param distributions
     * Underlying distributions from which we sample
     * @param priorWeights
     * Weights proportionate by which the distributions are sampled
     */
    public MultivariateMixtureDensityModel(
        Collection<? extends DistributionType> distributions,
        double[] priorWeights )
    {
        super( distributions, priorWeights );
    }

    /**
     * Copy Constructor
     * @param other
     * MultivariateMixtureDensityModel to copy
     */
    public MultivariateMixtureDensityModel(
        MultivariateMixtureDensityModel<? extends DistributionType> other )
    {
        this( ObjectUtil.cloneSmartElementsAsArrayList(other.getDistributions()),
            ObjectUtil.deepCopy(other.getPriorWeights()) );
    }

    @Override
    public MultivariateMixtureDensityModel<DistributionType> clone()
    {
        return (MultivariateMixtureDensityModel<DistributionType>) super.clone();
    }

    @Override
    public Vector getMean()
    {

        RingAccumulator<Vector> mean =
            new RingAccumulator<Vector>();
        final int K = this.getDistributionCount();
        for( int k = 0; k < K; k++ )
        {
            mean.accumulate( this.getDistributions().get(k).getMean().scale(
                this.getPriorWeights()[k] ) );
        }

        return mean.getSum().scale( 1.0 / this.getPriorWeightSum() );

    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyArray(this.getPriorWeights());
    }

    @Override
    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals( this.getDistributionCount() );
        for( int k = 0; k < parameters.getDimensionality(); k++ )
        {
            this.priorWeights[k] = parameters.getElement(k);
        }
    }

    @Override
    public MultivariateMixtureDensityModel.PDF<DistributionType> getProbabilityFunction()
    {
        return new MultivariateMixtureDensityModel.PDF<DistributionType>( this );
    }
    
    /**
     * PDF of the MultivariateMixtureDensityModel
     * @param <DistributionType>
     * Type of Distribution in the mixture
     */
    public static class PDF<DistributionType extends ClosedFormComputableDistribution<Vector>>
        extends MultivariateMixtureDensityModel<DistributionType>
        implements ProbabilityDensityFunction<Vector>
    {

        /**
         * Creates a new instance of MultivariateMixtureDensityModel
         * @param distributions
         * Underlying distributions from which we sample
         */
        public PDF(
            DistributionType ... distributions )
        {
            super( distributions );
        }


        /**
         * Creates a new instance of MultivariateMixtureDensityModel
         * @param distributions
         * Underlying distributions from which we sample
         */
        public PDF(
            Collection<? extends DistributionType> distributions )
        {
            super( distributions );
        }

        /**
         * Creates a new instance of MultivariateMixtureDensityModel
         * @param distributions
         * Underlying distributions from which we sample
         * @param priorWeights
         * Weights proportionate by which the distributions are sampled
         */
        public PDF(
            Collection<? extends DistributionType> distributions,
            double[] priorWeights )
        {
            super( distributions, priorWeights );
        }

        /**
         * Copy Constructor
         * @param other
         * MultivariateMixtureDensityModel to copy
         */
        public PDF(
            MultivariateMixtureDensityModel<? extends DistributionType> other )
        {
            super( other );
        }


        @Override
        public MultivariateMixtureDensityModel.PDF<DistributionType> getProbabilityFunction()
        {
            return this;
        }

        @Override
        public double logEvaluate(
            Vector input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public Double evaluate(
            Vector input)
        {
            double sum = 0.0;
            final int K = this.getDistributionCount();
            for( int k = 0; k < K; k++ )
            {
                ProbabilityFunction<Vector> pdf =
                    this.getDistributions().get(k).getProbabilityFunction();
                sum += pdf.evaluate(input) * this.priorWeights[k];
            }

            return sum/this.getPriorWeightSum();
        }


        /**
         * Computes the probability distribution that the input was generated by
         * the underlying distributions
         * @param input Input to consider
         * @return probability distribution that the input was generated by
         * the underlying distributions
         */
        public double[] computeRandomVariableProbabilities(
            Vector input )
        {
            int K = this.getDistributionCount();
            double[] likelihoods = this.computeRandomVariableLikelihoods( input );
            double sum = 0.0;
            for( int k = 0; k < K; k++ )
            {
                sum += likelihoods[k];
            }
            if( sum <= 0.0 )
            {
                Arrays.fill( likelihoods, 1.0/K );
            }

            sum = 0.0;
            for( int k = 0; k < K; k++ )
            {
                likelihoods[k] *= this.priorWeights[k];
                sum += likelihoods[k];
            }
            if( sum <= 0.0 )
            {
                Arrays.fill( likelihoods, 1.0/K );
                sum = 1.0;
            }
            for( int k = 0; k < K; k++ )
            {
                likelihoods[k] /= sum;
            }

            return likelihoods;

        }


        /**
         * Computes the likelihoods of the underlying distributions
         * @param input Input to consider
         * @return Vector of likelihoods for the underlying distributions
         */
        public double[] computeRandomVariableLikelihoods(
            Vector input )
        {

            int K = this.getDistributionCount();
            double[] likelihoods = new double[ K ];
            for( int k = 0; k < K; k++ )
            {
                ProbabilityFunction<Vector> pdf =
                    this.getDistributions().get(k).getProbabilityFunction();
                likelihoods[k] = pdf.evaluate(input);
            }

            return likelihoods;
        }


        /**
         * Gets the index of the most-likely distribution, given the input.
         * That is, find the distribution that most likely generated the input
         * @param input input to consider
         * @return zero-based index of the most-likely distribution
         */
        public int getMostLikelyRandomVariable(
            Vector input )
        {

            double[] probabilities = this.computeRandomVariableProbabilities( input );
            int bestIndex = 0;
            double bestProbability = probabilities[0];
            for( int i = 1; i < probabilities.length; i++ )
            {
                double prob = probabilities[i];
                if( bestProbability < prob )
                {
                    bestProbability = prob;
                    bestIndex = i;
                }
            }

            return bestIndex;

        }

    }

}
