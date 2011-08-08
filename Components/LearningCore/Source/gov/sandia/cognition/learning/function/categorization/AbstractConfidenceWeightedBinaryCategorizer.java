/*
 * File:                AbstractConfidenceWeightedBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 26, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;

/**
 * Unit tests for class AbstractConfidenceWeightedBinaryCategorizer.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public abstract class AbstractConfidenceWeightedBinaryCategorizer
    extends LinearBinaryCategorizer
    implements ConfidenceWeightedBinaryCategorizer
{

    /**
     * Creates a new, uninitialized {@code AbstractConfidenceWeightedBinaryCategorizer}.
     */
    public AbstractConfidenceWeightedBinaryCategorizer()
    {
        super();
    }

    /**
     * Creates a new {@code AbstractConfidenceWeightedBinaryCategorizer} with
     * the given mean vector.
     *
     * @param   mean
     *      The mean vector.
     */
    public AbstractConfidenceWeightedBinaryCategorizer(
        final Vector mean)
    {
        super(mean, 0.0);
    }

    @Override
    public BernoulliDistribution evaluateAsBernoulli(
        final Vectorizable input)
    {
        // Figure out the probability of zero using the Gaussian CDF to get
        // the amount of the distribution below the threshold of 0.0.
        final double pZero =
            this.evaluateAsGaussian(input).getCDF().evaluate(0.0);

        // Now get the probability of 1.
        final double pOne = 1.0 - pZero;
        return new BernoulliDistribution(pOne);
    }

    @Override
    public boolean isInitialized()
    {
        return this.getMean() != null && this.getCovariance() != null;
    }
    
    @Override
    public MultivariateGaussian createWeightDistribution()
    {
        return new MultivariateGaussian(
            this.getMean(), this.getCovariance());
    }

    /**
     * Gets the mean of the categorizer, which is the weight vector.
     *
     * @return
     *      The mean of the categorizer.
     */
    @Override
    public Vector getMean()
    {
        return this.getWeights();
    }

    /**
     * Sets the mean of the categorizer, which is the weight vector.
     *
     * @param   mean
     *      The mean of the categorizer.
     */
    public void setMean(
        final Vector mean)
    {
        this.setWeights(mean);
    }
    
}
