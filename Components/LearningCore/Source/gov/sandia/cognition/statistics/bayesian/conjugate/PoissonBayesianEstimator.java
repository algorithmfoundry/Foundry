/*
 * File:                PoissonBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 15, 2009, Sandia Corporation.
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
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.NegativeBinomialDistribution;
import gov.sandia.cognition.statistics.distribution.PoissonDistribution;

/**
 * A Bayesian estimator for the parameter of a PoissonDistribution using
 * the conjugate prior GammaDistribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="William M. Bolstad",
    title="Introduction to Bayesian Statistics: Second Edition",
    type=PublicationType.Book,
    year=2007,
    pages={185},
    notes={
        "Bolstad primarily uses INVERSE shape parameter on gamma!",
        "So we must invert his calculations for shape!"
    }
)
public class PoissonBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Number,Double,PoissonDistribution,GammaDistribution>
    implements ConjugatePriorBayesianEstimatorPredictor<Number,Double,PoissonDistribution,GammaDistribution>
{

    /** 
     * Creates a new instance of PoissonBayesianEstimator 
     */
    public PoissonBayesianEstimator()
    {
        this( new GammaDistribution.PDF( 1.0, 1.0 ) );
    }

    /**
     * Creates a new instance of PoissonBayesianEstimator
     * @param belief
     * Conjugate prior belief.
     */
    public PoissonBayesianEstimator(
        GammaDistribution belief )
    {
        this( new PoissonDistribution(), belief );
    }

    /**
     * Creates a new instance of PoissonBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public PoissonBayesianEstimator(
        PoissonDistribution conditional,
        GammaDistribution prior )
    {
        this( new PoissonBayesianEstimator.Parameter(conditional, prior) );
    }

    /**
     * Creates a new instance
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected PoissonBayesianEstimator(
        BayesianParameter<Double,PoissonDistribution,GammaDistribution> parameter )
    {
        super( parameter );
    }

    public PoissonBayesianEstimator.Parameter createParameter(
        PoissonDistribution conditional,
        GammaDistribution prior)
    {
        return new PoissonBayesianEstimator.Parameter( conditional, prior );
    }

    public double computeEquivalentSampleSize(
        GammaDistribution belief)
    {
        return 1.0/belief.getScale();
    }

    public void update(
        GammaDistribution belief,
        Number value)
    {
        belief.setShape( belief.getShape() + value.doubleValue() );
        belief.setScale( 1.0/((1.0/belief.getScale()) + 1.0) );
    }

    public NegativeBinomialDistribution createPredictiveDistribution(
        GammaDistribution posterior)
    {
        double p = posterior.getScale() / (posterior.getScale() + 1.0);
        double r = posterior.getShape();
        return new NegativeBinomialDistribution( r, p );
    }

    /**
     * Parameter of this conjugate prior relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,PoissonDistribution,GammaDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "rate";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            PoissonDistribution conditional,
            GammaDistribution prior )
        {
            super( conditional, NAME, prior );
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
