/*
 * File:                BernoulliBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 16, 2009, Sandia Corporation.
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
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;

/**
 * A Bayesian estimator for the parameter of a BernoulliDistribution using
 * the conjugate prior BetaDistribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="William M. Bolstad",
    title="Introduction to Bayesian Statistics: Second Edition",
    type=PublicationType.Book,
    year=2007,
    pages={143}
)
public class BernoulliBayesianEstimator
    extends AbstractConjugatePriorBayesianEstimator<Number,Double,BernoulliDistribution,BetaDistribution>
{

    /** 
     * Creates a new instance of BernoulliBayesianEstimator
     */
    public BernoulliBayesianEstimator()
    {
        // This is the uniform distribution.
        this( new BetaDistribution.PDF( 1.0, 1.0 ) );
    }

    /**
     * Creates a new instance of BernoulliBayesianEstimator
     * @param prior
     * Default conjugate prior.
     */
    public BernoulliBayesianEstimator(
        BetaDistribution prior )
    {
        this( new BernoulliDistribution(), prior );
    }

    /**
     * Creates a new instance of BernoulliBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public BernoulliBayesianEstimator(
        BernoulliDistribution conditional,
        BetaDistribution prior )
    {
        this( new BernoulliBayesianEstimator.Parameter( conditional, prior ) );
    }

    /**
     * Creates a new instance of BernoulliBayesianEstimator
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected BernoulliBayesianEstimator(
        BayesianParameter<Double,BernoulliDistribution,BetaDistribution> parameter )
    {
        super( parameter );
    }

    public BernoulliBayesianEstimator.Parameter createParameter(
        BernoulliDistribution conditional,
        BetaDistribution prior)
    {
        return new BernoulliBayesianEstimator.Parameter( conditional, prior );
    }

    public void update(
        BetaDistribution updater,
        Number data)
    {
        if( data.intValue() != 0 )
        {
            double alpha = updater.getAlpha();
            alpha += 1.0;
            updater.setAlpha(alpha);
        }
        else
        {
            double beta = updater.getBeta();
            beta += 1.0;
            updater.setBeta(beta);
        }
    }

    @Override
    public double computeEquivalentSampleSize(
        BetaDistribution belief)
    {
        double alpha = belief.getAlpha();
        double beta = belief.getBeta();
        return alpha + beta + 1.0;
    }

    /**
     * Parameter of this conjugate prior relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,BernoulliDistribution,BetaDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "p";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            BernoulliDistribution conditional,
            BetaDistribution prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Double value)
        {
            this.conditionalDistribution.setP(value);
        }

        public Double getValue()
        {
            return this.conditionalDistribution.getP();
        }


    }

}
