/*
 * File:                BinomialBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.BetaBinomialDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.BinomialDistribution;

/**
 * A Bayesian estimator for the parameter of a Bernoulli parameter, p,
 * of a BinomialDistribution using the conjugate prior BetaDistribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class BinomialBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Number,Double,BinomialDistribution,BetaDistribution>
    implements ConjugatePriorBayesianEstimatorPredictor<Number,Double,BinomialDistribution,BetaDistribution>
{
    
    /**
     * Default n, {@value}.
     */
    public static final int DEFAULT_N = 1;

    /** 
     * Creates a new instance of BinomialBayesianEstimator 
     */
    public BinomialBayesianEstimator()
    {
        this( DEFAULT_N );
    }

    /**
     * Creates a new instance of BinomialBayesianEstimator
     * @param n
     * Samples in the experiment, must be greater than zero
     */
    public BinomialBayesianEstimator(
        int n )
    {
        this( n, new BetaDistribution(1.0,1.0) );
    }

    /**
     * Creates a new instance of BinomialBayesianEstimator
     * @param n
     * Samples in the experiment, must be greater than zero
     * @param prior
     * Conjugate prior of the conditional for the parameter
     */
    public BinomialBayesianEstimator(
        int n,
        BetaDistribution prior )
    {
        this( new BinomialDistribution(n,0.5), prior );
    }

    /**
     * Creates a new instance of BinomialBayesianEstimator
     * @param conditional
     * Distribution that generates the observations
     * @param prior
     * Conjugate prior of the conditional for the parameter
     */
    public BinomialBayesianEstimator(
        BinomialDistribution conditional,
        BetaDistribution prior )
    {
        this( new BinomialBayesianEstimator.Parameter(conditional, prior) );
    }

    /**
     * Creates a new instance of BinomialBayesianEstimator
     * @param parameter
     * Parameter that describes the relationship between the conditional and
     * the conjugate prior
     */
    protected BinomialBayesianEstimator(
        BayesianParameter<Double,BinomialDistribution,BetaDistribution> parameter )
    {
        super( parameter );
    }

    public BinomialBayesianEstimator.Parameter createParameter(
        BinomialDistribution conditional,
        BetaDistribution prior)
    {
        return new BinomialBayesianEstimator.Parameter( conditional, prior );
    }

    @Override
    public BinomialBayesianEstimator clone()
    {
        return (BinomialBayesianEstimator) super.clone();
    }

    public void update(
        BetaDistribution target,
        Number data)
    {
        final int n = this.getN();
        final int success = data.intValue();
        final int failure = n-success;
        double alpha = target.getAlpha();
        double beta = target.getBeta();
        alpha += success;
        beta += failure;

        target.setAlpha(alpha);
        target.setBeta(beta);
    }

    @Override
    public double computeEquivalentSampleSize(
        BetaDistribution belief)
    {
        double alpha = belief.getAlpha();
        double beta = belief.getBeta();
        return (alpha + beta)/this.getN() + 1.0;
    }

    public BetaBinomialDistribution createPredictiveDistribution(
        BetaDistribution posterior)
    {
        return new BetaBinomialDistribution(
            this.getN(), posterior.getAlpha(), posterior.getBeta() );
    }

    /**
     * Gets the number of samples in the experiment
     * @return
     * Samples in the experiment, must be greater than zero
     */
    public int getN()
    {
        return this.parameter.getConditionalDistribution().getN();
    }

    /**
     * Sets the number of samples in the experiment
     * @param n
     * Samples in the experiment, must be greater than zero
     */
    public void setN(
        int n )
    {
        if( n <= 0 )
        {
            throw new IllegalArgumentException( "n must be > 0" );
        }
        this.parameter.getConditionalDistribution().setN(n);
    }

    /**
     * Parameter of this relationship
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,BinomialDistribution,BetaDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "p";

        /**
         * Creates a new instance of Parameter
         * @param conditional
         * Distribution that generates the observations
         * @param prior
         * Prior distribution of the parameter
         */
        public Parameter(
            BinomialDistribution conditional,
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
