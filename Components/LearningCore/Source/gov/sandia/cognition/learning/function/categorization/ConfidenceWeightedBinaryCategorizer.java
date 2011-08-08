/*
 * File:                ConfidenceWeightedBinaryCategorizer.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;

/**
 * Interface for a confidence-weighted binary categorizer, which defines a
 * distribution over linear binary categorizers. It extends the
 * vector input evaluator and threshold binary categorizer like a
 * {@code LinearBinaryCategorizer} so that it can behave as a binary
 * categorizer, it but also has methods for accessing the distribution of
 * binary categorizers that it represents. It is typically represented using a
 * mean vector and a covariance matrix.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public interface ConfidenceWeightedBinaryCategorizer
    extends VectorInputEvaluator<Vectorizable, Boolean>,
        ThresholdBinaryCategorizer<Vectorizable>
{

    /**
     * Returns the univariate Gaussian distribution over the output of
     * the distribution of weight vectors times the input, with the
     * confidence that the categorizer was trained using.
     *
     * @param   input
     *      The input to evaluate.
     * @return
     *      The distribution of outputs as a Gaussian.
     */
    public UnivariateGaussian evaluateAsGaussian(
        final Vectorizable input);
    
    /**
     * Returns a Bernoulli distribution over the output of
     * the distribution of weight vectors times the input, with the
     * confidence that the categorizer was trained using.
     *
     * @param   input
     *      The input to evaluate.
     * @return
     *      The distribution over outputs as a Bernoulli.
     */
    public BernoulliDistribution evaluateAsBernoulli(
        final Vectorizable input);

    /**
     * Creates a multivariate Gaussian distribution that represents the
     * distribution of weight vectors that the algorithm has learned.
     *
     * @return
     *      The distribution of weight vectors.
     */
    public MultivariateGaussian createWeightDistribution();

    /**
     * Determines if this category has been initialized with a mean and
     * covariance.
     *
     * @return
     *      True if this categorizer has been initialized. Otherwise, false.
     */
    public boolean isInitialized();

    /**
     * Gets the mean of the categorizer, which is the weight vector.
     *
     * @return
     *      The mean of the categorizer.
     */
    public Vector getMean();

    /**
     * Gets the covariance matrix of the categorizer.
     *
     * @return
     *      The covariance matrix.
     */
    public Matrix getCovariance();

}
