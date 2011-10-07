/*
 * File:                AdaptiveRegularizationOfWeights.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 26, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.confidence;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractSupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.function.categorization.DefaultConfidenceWeightedBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Adaptive Regularization of Weights (AROW) algorithm
 * for online learning of a linear binary categorizer. It is a
 * confidence-weighted algorithm that keeps track of the full covariance matrix
 * when updating the learner.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Koby Crammer", "Alex Kulesza", "Mark Dredze"},
    title="Adpative Regularization of Weight Vectors",
    year=2009,
    type=PublicationType.Conference,
    publication="Advances in Neural Information Processing Systems",
    url="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.169.4127&rep=rep1&type=pdf")
public class AdaptiveRegularizationOfWeights
    extends AbstractSupervisedBatchAndIncrementalLearner<Vectorizable, Boolean, DefaultConfidenceWeightedBinaryCategorizer>
{
    
    /** The default value of r is {@value}. */
    public static final double DEFAULT_R = 0.001;

    /** The r parameter that controls regularization weight. Must be positive.
     */
    protected double r;

    /**
     * Creates a new {@code AdaptiveRegularizationOfWeights} with default
     * parameters.
     */
    public AdaptiveRegularizationOfWeights()
    {
        this(DEFAULT_R);
    }

    /**
     * Creates a new {@code AdaptiveRegularizationOfWeights} with the given
     * parameters
     *
     * @param r
     *      The regularization parameter. Must be positive.
     */
    public AdaptiveRegularizationOfWeights(
        final double r)
    {
        super();

        this.setR(r);
    }

    @Override
    public DefaultConfidenceWeightedBinaryCategorizer createInitialLearnedObject()
    {
        return new DefaultConfidenceWeightedBinaryCategorizer();
    }

    @Override
    public void update(
        final DefaultConfidenceWeightedBinaryCategorizer target,
        final Vectorizable input,
        final Boolean output)
    {
        if (input != null && output != null)
        {
            this.update(target, input.convertToVector(), (boolean) output);
        }
    }

    /**
     * Perform an update for the target using the given input and associated
     * label.
     *
     * @param   target
     *      The target to update.
     * @param   input
     *      The input value.
     * @param   label
     *      The label associated with the input.
     */
    public void update(
        final DefaultConfidenceWeightedBinaryCategorizer target,
        final Vector input,
        final boolean label)
    {
        // Get the mean and variance of the thing we will learn, which are
        // the parameters we will update.
        final Vector mean;
        final Matrix covariance;
        if (!target.isInitialized())
        {
            // Initialize the mean to zero and the variance to the default value
            // that we were given.
            final int dimensionality = input.getDimensionality();
            mean = VectorFactory.getDenseDefault().createVector(dimensionality);
            covariance = MatrixFactory.getDenseDefault().createIdentity(
                dimensionality, dimensionality);

            target.setMean(mean);
            target.setCovariance(covariance);
        }
        else
        {
            mean = target.getMean();
            covariance = target.getCovariance();
        }

        // Compute the predicted and actual values.
        final double predicted = input.dotProduct(mean);
        final double actual = label ? +1.0 : -1.0;

        // Now compute the margin (m_t) and variance (v_t).
        final double margin = actual * predicted;
        
        final boolean error = margin < 1.0;
        if (error)
        {
            final Vector covarianceTimesInput = input.times(covariance);
            final double marginVariance = covarianceTimesInput.dotProduct(input);
            
            final double beta = 1.0 / (marginVariance + this.r);
            final double alpha = Math.max(0.0, 1.0 - margin) * beta;
            
            final Vector meanUpdate = input.times(covariance);
            meanUpdate.scaleEquals(alpha * actual);
            mean.plusEquals(meanUpdate);
            
            final Matrix covarianceUpdate = covarianceTimesInput.outerProduct(
                covarianceTimesInput);
            covarianceUpdate.scaleEquals(-beta);
            covariance.plusEquals(covarianceUpdate);
        }

    }

    /**
     * Gets the regularization parameter.
     *
     * @return
     *      The regularization parameter. Must be positive.
     */
    public double getR()
    {
        return this.r;
    }

    /**
     * Sets the regularization parameter.
     *
     * @param   r
     *      The regularization parameter. Must be positive.
     */
    public void setR(
        final double r)
    {
        ArgumentChecker.assertIsPositive("r", r);
        this.r = r;
    }

}
