/*
 * File:                ExponentialBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 14, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.ExponentialDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.ParetoDistribution;

/**
 * Conjugate prior Bayesian estimator of the "rate" parameter of an
 * Exponential distribution using the conjugate prior Gamma distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Conjugate Prior",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Conjugate_prior"
        )
        ,
        @PublicationReference(
            author={
                "Byron J. Gajewski",
                "Stephen D. Simon",
                "Susan E. Carlson"
            },
            title="Predicting accrual in clinical trials with Bayesian posterior predictive distributions",
            type=PublicationType.Journal,
            year=2008,
            publication="Statistics in Medicine",
            notes="They derive the predictive posterior for an inverse gamma, but we're using a gamma, so we have to invert the scale parameter."
        )
    }
)
public class ExponentialBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Double,Double,ExponentialDistribution,GammaDistribution>
    implements ConjugatePriorBayesianEstimatorPredictor<Double,Double,ExponentialDistribution,GammaDistribution>
{

    /**
     * Default constructor.
     */
    public ExponentialBayesianEstimator()
    {
        this( new GammaDistribution( 1.0, 1.0 ) );
    }

    /**
     * Creates a new instance of ExponentialBayesianEstimator
     * @param prior
     * Default conjugate prior.
     */
    public ExponentialBayesianEstimator(
        GammaDistribution prior )
    {
        this( new ExponentialDistribution(), prior );
    }

    /**
     * Creates a new instance of ExponentialBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public ExponentialBayesianEstimator(
        ExponentialDistribution conditional,
        GammaDistribution prior )
    {
        this( new ExponentialBayesianEstimator.Parameter(conditional, prior) );
    }

    /**
     * Creates a new instance of ExponentialBayesianEstimator
     * @param parameter
     * Bayesian parameter describing this conjugate relationship.
     */
    protected ExponentialBayesianEstimator(
        BayesianParameter<Double,ExponentialDistribution,GammaDistribution> parameter )
    {
        super( parameter );
    }

    public ExponentialBayesianEstimator.Parameter createParameter(
        ExponentialDistribution conditional,
        GammaDistribution prior)
    {
        return new ExponentialBayesianEstimator.Parameter( conditional, prior );
    }

    public void update(
        GammaDistribution belief,
        Double data)
    {
        double alpha = belief.getShape();
        double beta = 1.0/belief.getScale();
        alpha++;
        beta += data.doubleValue();
        double theta = 1.0/beta;
        belief.setShape(alpha);
        belief.setScale(theta);
    }

    public double computeEquivalentSampleSize(
        GammaDistribution belief)
    {
        return belief.getShape();
    }

    public ParetoDistribution createPredictiveDistribution(
        GammaDistribution posterior)
    {
        return new ParetoDistribution( posterior.getShape(), 1.0/posterior.getScale(), 1.0/posterior.getScale() );
    }

    /**
     * Bayesian parameter describing this conjugate relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,ExponentialDistribution,GammaDistribution>
    {

        /**
         * Default name of the parameter, {@value}.
         */
        public static final String NAME = "rate";

        /**
         * Creates a new instance of Parameter
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            ExponentialDistribution conditional,
            GammaDistribution prior )
        {
            super( conditional, NAME, prior);
        }

        public void setValue(
            Double value)
        {
            this.conditionalDistribution.setRate(value);
        }

        public Double getValue()
        {
            return this.conditionalDistribution.getRate();
        }

    }

}
