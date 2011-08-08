/*
 * File:                GammaInverseScaleBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 15, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;

/**
 * A Bayesian estimator for the scale parameter of a Gamma distribution
 * using the conjugate prior Gamma distribution for the inverse-scale (rate)
 * of the Gamma.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class GammaInverseScaleBayesianEstimator
    extends AbstractConjugatePriorBayesianEstimator<Double,Double,GammaDistribution,GammaDistribution>
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 1.0;

    /** 
     * Creates a new instance of GammaInverseScaleBayesianEstimator
     */
    public GammaInverseScaleBayesianEstimator()
    {
        this( DEFAULT_SHAPE, new GammaDistribution() );
    }

    /**
     * Creates a new instance of GammaInverseScaleBayesianEstimator
     * @param shape
     * Shape of the conditional distribution
     * @param prior
     * Default conjugate prior.
     */
    public GammaInverseScaleBayesianEstimator(
        double shape,
        GammaDistribution prior )
    {
        this( new GammaDistribution( shape, 1.0 ), prior );
    }

    /**
     * Creates a new instance of GammaInverseScaleBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public GammaInverseScaleBayesianEstimator(
        GammaDistribution conditional,
        GammaDistribution prior )
    {
        this( new Parameter(conditional, prior) );
    }

    /**
     * Creates a new instance of GammaInverseScaleBayesianEstimator
     * @param parameter
     * Bayesian parameter describing this conjugate relationship.
     */
    protected GammaInverseScaleBayesianEstimator(
        BayesianParameter<Double,GammaDistribution,GammaDistribution> parameter )
    {
        super( parameter );
    }

    public GammaInverseScaleBayesianEstimator.Parameter createParameter(
        GammaDistribution conditional,
        GammaDistribution prior)
    {
        return new GammaInverseScaleBayesianEstimator.Parameter( conditional, prior );
    }

    /**
     * Gets the shape of the conditional distribution
     * @return
     * Shape of the conditional distribution
     */
    public double getShape()
    {
        return this.parameter.getConditionalDistribution().getShape();
    }

    /**
     * Sets the shape of the conditional distribution
     * @param shape
     * Shape of the conditional distribution
     */
    public void setShape(
        double shape )
    {
        this.parameter.getConditionalDistribution().setShape(shape);
    }

    public void update(
        GammaDistribution belief,
        Double data)
    {
        double alpha = belief.getShape();
        double beta = 1.0/belief.getScale();
        alpha += this.getShape();
        beta += data.doubleValue();
        double theta = 1.0/beta;
        belief.setShape(alpha);
        belief.setScale(theta);
    }

    public double computeEquivalentSampleSize(
        GammaDistribution belief)
    {
        double alpha = belief.getShape();
        return alpha / this.getShape();
    }

    /**
     * Bayesian parameter describing this conjugate relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Double,GammaDistribution,GammaDistribution>
    {

        /**
         * Default name, {@value}.
         */
        public static final String NAME = "inverse-scale";

        /**
         * Creates a new instance of Parameter
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            GammaDistribution conditional,
            GammaDistribution prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Double value)
        {
            this.conditionalDistribution.setScale(1.0/value);
        }

        public Double getValue()
        {
            return 1.0/this.conditionalDistribution.getScale();
        }

    }

}
