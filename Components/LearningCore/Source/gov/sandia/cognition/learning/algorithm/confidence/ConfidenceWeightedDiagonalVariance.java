/*
 * File:                ConfidenceWeightedDiagonalVariance.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 12, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.confidence;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractSupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.function.categorization.DiagonalConfidenceWeightedBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Variance algorithm for learning a confidence-weighted
 * linear categorizer. It updates only the diagonal of the covariance matrix,
 * thus computing the variance of each dimension. It is roughly based on the
 * Passive-Aggressive algorithm PA-I, which uses a linear soft margin. This
 * corresponds to the "Variance-drop" version.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    title="Confidence-Weighted Linear Classification",
    author={"Mark Dredze", "Koby Crammer", "Fernando Pereira"},
    year=2008,
    type=PublicationType.Conference,
    publication="International Conference on Machine Learning",
    url="http://portal.acm.org/citation.cfm?id=1390190")
public class ConfidenceWeightedDiagonalVariance
    extends AbstractSupervisedBatchAndIncrementalLearner<Vectorizable, Boolean, DiagonalConfidenceWeightedBinaryCategorizer>
{

    /** The default confidence is {@value}. */
    public static final double DEFAULT_CONFIDENCE = 0.85;

    /** The default variance is {@value}. */
    public static final double DEFAULT_DEFAULT_VARIANCE = 1.0;

    /** The confidence to use for updating. Must be in [0, 1]. Called eta in the
     *  paper. */
    protected double confidence;

    /** The default variance, which the diagonal of the covariance matrix is
     *  initialized to. Must be positive. Called a in the paper. */
    protected double defaultVariance;

    /** Phi is the standard score computed from the confidence. */
    protected double phi;

    /**
     * Creates a new {@code ConfidenceWeightedDiagonalVariance} with default
     * parameters.
     */
    public ConfidenceWeightedDiagonalVariance()
    {
        this(DEFAULT_CONFIDENCE, DEFAULT_DEFAULT_VARIANCE);
    }

    /**
     * Creates a new {@code ConfidenceWeightedDiagonalVariance} with the given
     * parameters.
     *
     * @param   confidence
     *      The confidence to use. Must be in [0, 1].
     * @param   defaultVariance
     *      The default value to initialize the covariance matrix to.
     */
    public ConfidenceWeightedDiagonalVariance(
        final double confidence,
        final double defaultVariance)
    {
        super();

        this.setConfidence(confidence);
        this.setDefaultVariance(defaultVariance);
    }

    @Override
    public DiagonalConfidenceWeightedBinaryCategorizer createInitialLearnedObject()
    {
        return new DiagonalConfidenceWeightedBinaryCategorizer();
    }

    @Override
    public void update(
        final DiagonalConfidenceWeightedBinaryCategorizer target,
        final Vectorizable input,
        final Boolean output)
    {
        if (input != null && output != null)
        {
            this.update(target, input.convertToVector(), (boolean) output);
        }
    }

    /**
     * Updates the target using the given input and associated label.
     *
     * @param   target
     *      The target to update.
     * @param   input
     *      The supervised input value.
     * @param   label
     *      The output label associated with the input.
     */
    public void update(
        final DiagonalConfidenceWeightedBinaryCategorizer target,
        final Vector input,
        final boolean label)
    {
        // Get the mean and variance of the thing we will learn, which are
        // the parameters we will update.
        final Vector mean;
        final Vector variance;
        if (!target.isInitialized())
        {
            // Initialize the mean to zero and the variance to the default value
            // that we were given.
            final int dimensionality = input.getDimensionality();
            mean = VectorFactory.getDenseDefault().createVector(dimensionality);
            variance = VectorFactory.getDenseDefault().createVector(
                dimensionality, this.getDefaultVariance());
            
            target.setMean(mean);
            target.setVariance(variance);
        }
        else
        {
            mean = target.getMean();
            variance = target.getVariance();
        }

        // Figure out the predicted and actual (yi) values.
        final double predicted = input.dotProduct(mean);
        final double actual = label ? +1.0 : -1.0;

        // Now compute the margin (Mi).
        final double margin = actual * predicted;

        // Now compute the margin variance by multiplying the variance by
        // the input. In the paper this is Sigma * x. We keep track of this
        // vector since it will be useful when computing the update.
        final Vector varianceTimesInput = input.dotTimes(variance);

        // Now get the margin variance (Vi).
        final double marginVariance = input.dotProduct(varianceTimesInput);

        if (marginVariance == 0.0 || (margin > phi * marginVariance))
        {
            // No update.
            return;
        }

        // Compute alpha (also gamma) using equation 16:
        //    gamma = (-(1+2 phi Mi)
        //             + sqrt((1 + 2 phi Mi)^2 - 8 phi (Mi - phi Vi))
        //          / (4 phi Vi)
        final double meanPart = 1.0 + 2.0 * phi * margin;
        final double variancePart = margin - phi * marginVariance;
        final double numerator = -meanPart
            + Math.sqrt(meanPart * meanPart - 8.0 * phi * variancePart);
        final double denominator = (4.0 * phi * marginVariance);

        // Since alpha = max(gamma, 0), we just call it alpha and then check
        // to see if it is less than zero.
        final double alpha = numerator / denominator;
        
        if (alpha <= 0.0)
        {
            // No update.
            return;
        }

        // Compute the new mean.
        final Vector meanUpdate = varianceTimesInput.scale(actual * alpha);
        mean.plusEquals(meanUpdate);

        // Here is the code as described in Algorithm 1 and equation 17 in
        // the paper. However, we can avoid creating a whole new matrix
        // and inverting it using equation 13 instead.
        // Note that diag(x) in the paper means a matrix where the diagonal
        // contains the SQUARE of the elements of x.
        // final Matrix varianceInverseUpdate =
        //    MatrixFactory.getDiagonalDefault().createDiagonal(
        //        input.dotTimes(input));
        // varianceInverseUpdate.scaleEquals(2.0 * alpha * phi);
        // final Matrix varianceInverse = variance.inverse();
        // varianceInverse.plusEquals(varianceInverseUpdate);
        // variance = varianceInverse.inverse();

        final double twoAlphaPhi = 2.0 * alpha * phi;

        // To update the variance we set
        // sigma_i = sigma_i
        //         - (2 alpha pi / (1 + 2 alpha pi Vi)) * sigma_i^2 * x_i^2
        final Vector varianceUpdate =
            varianceTimesInput.dotTimes(varianceTimesInput);
        varianceUpdate.scaleEquals(
            -twoAlphaPhi / (1.0 + twoAlphaPhi * marginVariance));
        variance.plusEquals(varianceUpdate);

        // Set the mean and variance.
        target.setMean(mean);
        target.setVariance(variance);
    }

    /**
     * Gets the confidence to use for updating. Must be in [0, 1]. Called eta
     * in the paper.
     *
     * @return
     *      The confidence.
     */
    public double getConfidence()
    {
        return this.confidence;
    }


    /**
     * Gets the confidence to use for updating. Must be in [0, 1]. Called eta
     * in the paper.
     *
     * @param   confidence
     *      The confidence. Must be between 0 and 1, inclusive.
     */
    public void setConfidence(
        final double confidence)
    {
        ArgumentChecker.assertIsInRangeInclusive(
            "confidence", confidence, 0.0, 1.0);
        this.confidence = confidence;

        // Compute phi.
        this.phi = -UnivariateGaussian.CDF.Inverse.evaluate(
            1.0 - confidence, 0.0, 1.0 );
    }

    /**
     * Gets the default variance, which the diagonal of the covariance matrix is
     * initialized to. Must be positive. Called a in the paper.
     *
     * @return
     *      The default variance.
     */
    public double getDefaultVariance()
    {
        return this.defaultVariance;
    }

    /**
     * Sets the default variance, which the diagonal of the covariance matrix is
     * initialized to. Must be positive. Called a in the paper.
     *
     * @param   defaultVariance
     *      The default variance. Must be positive.
     */
    public void setDefaultVariance(
        final double defaultVariance)
    {
        ArgumentChecker.assertIsPositive("defaultVariance", defaultVariance);
        this.defaultVariance = defaultVariance;
    }
    
}
