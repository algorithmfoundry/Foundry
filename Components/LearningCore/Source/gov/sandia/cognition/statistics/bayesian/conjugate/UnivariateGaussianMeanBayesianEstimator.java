/*
 * File:                UnivariateGaussianMeanBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;

/**
 * Bayesian estimator for the mean of a UnivariateGaussian using its conjugate
 * prior, which is also a UnivariateGaussian.  This estimation method assumes
 * that the variance of the estimated mean is known.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="William M. Bolstad",
    title="Introduction to Bayesian Statistics: Second Edition",
    type=PublicationType.Book,
    year=2007,
    pages={208}
)
public class UnivariateGaussianMeanBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Double,Double,UnivariateGaussian,UnivariateGaussian>
    implements ConjugatePriorBayesianEstimatorPredictor<Double,Double,UnivariateGaussian,UnivariateGaussian>
{

    /**
     * Default known variance of the estimated distribution, {@value}.
     */
    public static final double DEFAULT_KNOWN_VARIANCE = 1.0;

    /** 
     * Creates a new instance of UnivariateGaussianMeanBayesianEstimator 
     */
    public UnivariateGaussianMeanBayesianEstimator()
    {
        this( DEFAULT_KNOWN_VARIANCE );
    }

    /**
     * Creates a new instance of UnivariateGaussianMeanBayesianEstimator
     * @param knownVariance
     * Known variance of the distribution.
     */
    public UnivariateGaussianMeanBayesianEstimator(
        double knownVariance )
    {
        this( knownVariance, new UnivariateGaussian( 0.0, 1.0 ) );
    }


    /**
     * Creates a new instance of UnivariateGaussianMeanBayesianEstimator
     * @param belief
     * Conjugate prior of the posterior belief.
     * @param knownVariance
     * Known variance of the distribution.
     */
    public UnivariateGaussianMeanBayesianEstimator(
        double knownVariance,
        UnivariateGaussian belief )
    {
        this( new UnivariateGaussian( 0.0, knownVariance ), belief );
    }

    /**
     * Creates a new instance
     * @param conditional
     * Distribution from which observations are generated
     * @param prior
     * Conjugate prior to the conditional distribution
     */
    public UnivariateGaussianMeanBayesianEstimator(
        UnivariateGaussian conditional,
        UnivariateGaussian prior )
    {
        this( new UnivariateGaussianMeanBayesianEstimator.Parameter(conditional,prior) );
    }
    
    /**
     * Creates a new instance
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected UnivariateGaussianMeanBayesianEstimator(
        BayesianParameter<Double,UnivariateGaussian,UnivariateGaussian> parameter )
    {
        super( parameter );
    }

    public UnivariateGaussianMeanBayesianEstimator.Parameter createParameter(
        UnivariateGaussian conditional,
        UnivariateGaussian prior)
    {
        return new UnivariateGaussianMeanBayesianEstimator.Parameter( conditional, prior );
    }

    /**
     * Getter for knownVariance.
     * @return
     * Known variance of the distribution.
     */
    public double getKnownVariance()
    {
        return this.parameter.getConditionalDistribution().getVariance();
    }

    /**
     * Setter for knownVariance.
     * @param knownVariance
     * Known variance of the distribution.
     */
    public void setKnownVariance(
        double knownVariance)
    {
        this.parameter.getConditionalDistribution().setVariance(knownVariance);
    }

    public void update(
        UnivariateGaussian updater,
        Double data)
    {
        final double s2 = this.getKnownVariance();
        final double s2hat = updater.getVariance();
        final double m = updater.getMean();
        final double y = data;

        double mhat = (s2*m + s2hat*y) / (s2 + s2hat);
        double v2hat = (s2*s2hat) / (s2 + s2hat);
        updater.setMean(mhat);
        updater.setVariance(v2hat);
    }

    public double computeEquivalentSampleSize(
        UnivariateGaussian belief)
    {
        return this.getKnownVariance() / belief.getVariance();
    }

    /**
     * Creates the predictive distribution from the given posterior.
     * @param posterior
     * Posterior from which to create the predictive distribution
     * @return
     * Predictive distribution from the posterior.
     */
    public UnivariateGaussian createPredictiveDistribution(
        UnivariateGaussian posterior )
    {
        UnivariateGaussian conditional =
            new UnivariateGaussian( 0.0, this.getKnownVariance() );
        return posterior.convolve( conditional );
    }
 
    /**
     * Parameter of this conjugate prior relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,UnivariateGaussian,UnivariateGaussian>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "mean";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            UnivariateGaussian conditional,
            UnivariateGaussian prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Double value)
        {
            this.conditionalDistribution.setMean(value);
        }

        public Double getValue()
        {
            return this.conditionalDistribution.getMean();
        }

    }

}
