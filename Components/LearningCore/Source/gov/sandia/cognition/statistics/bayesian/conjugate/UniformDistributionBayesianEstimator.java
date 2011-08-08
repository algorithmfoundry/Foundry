/*
 * File:                UniformDistributionBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 10, 2010, Sandia Corporation.
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
import gov.sandia.cognition.statistics.distribution.ParetoDistribution;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;

/**
 * A Bayesian estimator for a conditional Uniform(0,theta) distribution using
 * its conjugate prior Pareto distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Conjugate prior",
    year=2010,
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Conjugate_prior"
)
public class UniformDistributionBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Double,Double,UniformDistribution,ParetoDistribution>
{

    /** 
     * Creates a new instance of UniformDistributionBayesianEstimator 
     */
    public UniformDistributionBayesianEstimator()
    {
        this( new ParetoDistribution() );
    }

    /**
     * Creates a new instance of UniformDistributionBayesianEstimator
     * @param belief
     * Conjugate prior to use.
     */
    public UniformDistributionBayesianEstimator(
        ParetoDistribution belief )
    {
        this( new UniformDistribution(0.0,1.0), belief );
    }

    /**
     * Creates a new instance of PoissonBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public UniformDistributionBayesianEstimator(
        UniformDistribution conditional,
        ParetoDistribution prior )
    {
        this( new UniformDistributionBayesianEstimator.Parameter(conditional,prior) );
    }

    /**
     * Creates a new instance
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    public UniformDistributionBayesianEstimator(
        BayesianParameter<Double,UniformDistribution,ParetoDistribution> parameter )
    {
        super( parameter );
    }

    public UniformDistributionBayesianEstimator.Parameter createParameter(
        UniformDistribution conditional,
        ParetoDistribution prior)
    {
        return new UniformDistributionBayesianEstimator.Parameter( conditional, prior );
    }

    public void update(
        ParetoDistribution target,
        Double data)
    {
        double scale = Math.max( data, target.getScale() );
        double shape = target.getShape() + 1.0;
        target.setShape( shape );
        target.setScale( scale );
    }

    public double computeEquivalentSampleSize(
        ParetoDistribution belief)
    {
        return belief.getShape();
    }

    /**
     * Parameter of this conjugate prior relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,UniformDistribution,ParetoDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "maxSupport";
        
        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            UniformDistribution conditional,
            ParetoDistribution prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Double value)
        {
            this.conditionalDistribution.setMaxSupport(value);
        }

        public Double getValue()
        {
            return this.conditionalDistribution.getMaxSupport();
        }

    }

}
