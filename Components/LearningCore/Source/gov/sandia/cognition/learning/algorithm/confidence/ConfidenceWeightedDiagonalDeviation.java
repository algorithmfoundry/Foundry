/*
 * File:                ConfidenceWeightedDiagonalDeviation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 12, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.confidence;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractSupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.function.categorization.DiagonalConfidenceWeightedBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Standard Deviation (Stdev) algorithm for learning
 * a confidence-weighted categorizer. It updates only the diagonal of the
 * covariance matrix, thus computing the variance for each dimension. This
 * corresponds to the "Stdev-drop" version.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Koby Crammer", "Mark Dredze", "Fernando Pereira"},
    title="Exact Convex Confidence-Weighted Learning",
    year=2008,
    type=PublicationType.Conference,
    publication="Advances in Neural Information Processing Systems",
    url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.169.3364")
public class ConfidenceWeightedDiagonalDeviation
    extends AbstractSupervisedBatchAndIncrementalLearner<Vectorizable, Boolean, DiagonalConfidenceWeightedBinaryCategorizer>
{

    /** The default confidence is {@value}. */
    public static final double DEFAULT_CONFIDENCE = 0.85;

    /** The default variance is {@value}. */
    public static final double DEFAULT_DEFAULT_VARIANCE = 1.0;

    /** The confidence to use for updating. Must be in [0.5, 1]. Called eta in
     *  the paper. */
    protected double confidence;

    /** The default variance, which the diagonal of the covariance matrix is
     *  initialized to. Must be positive. Called a in the paper. */
    protected double defaultVariance;

    /** Phi is the standard score computed from the confidence. */
    protected double phi;

    /** Psi is the cached value 1 + phi^2 / 2. */
    protected double psi;

    /** Epsilon is the cached value 1 + phi^2. */
    protected double epsilon;

    /**
     * Creates a new {@code ConfidenceWeightedDiagonalVariance} with default
     * parameters.
     */
    public ConfidenceWeightedDiagonalDeviation()
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
    public ConfidenceWeightedDiagonalDeviation(
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
//System.out.println("Variance: " + variance);
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
//System.out.println("Margin: " + margin);
//System.out.println("Margin variance: " + marginVariance);
final double m = margin;
final double v = marginVariance;

if (v == 0.0 || m > phi * Math.sqrt(v))
{
    return;
}

double alpha = (-m * psi + Math.sqrt(m * m * Math.pow(phi, 4) / 4.0 + v * phi * phi * epsilon)) / (v * epsilon);
alpha = Math.max(alpha, 0.0);
if (alpha <= 0.0)
{
    return;
}
double u = 0.25 * Math.pow(-alpha * v * phi + Math.sqrt(alpha * alpha * v * v * phi * phi + 4.0 * v), 2);
double beta = alpha * phi / (Math.sqrt(u) + v * alpha * phi);

        // Compute the new mean.
        final Vector meanUpdate = varianceTimesInput.scale(actual * alpha);
        mean.plusEquals(meanUpdate);

        final Matrix varianceInverseUpdate =
           MatrixFactory.getDiagonalDefault().createDiagonal(
               input.dotTimes(input));
        varianceInverseUpdate.scaleEquals(
            beta);
            //alpha * phi * Math.pow(u, -0.5));
        final Matrix varianceInverse = target.getCovariance().inverse();
        varianceInverse.plusEquals(varianceInverseUpdate);
        final Matrix covariance = varianceInverse.inverse();
        for (int i = 0; i < variance.getDimensionality(); i++)
        {
            variance.setElement(i, covariance.getElement(i, i));
        }

        // Set the mean and variance.
        target.setMean(mean);
        target.setVariance(variance);
    }

    /**
     * Gets the confidence to use for updating. Must be in [0.5, 1]. Called eta
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
     * Gets the confidence to use for updating. Must be in [0.5, 1]. Called eta
     * in the paper.
     *
     * @param   confidence
     *      The confidence. Must be between 0.5 and 1, inclusive.
     */
    public void setConfidence(
        final double confidence)
    {
        ArgumentChecker.assertIsInRangeInclusive(
            "confidence", confidence, 0.5, 1.0);
        this.confidence = confidence;

        // Compute phi.
        this.phi = -UnivariateGaussian.CDF.Inverse.evaluate(
            1.0 - confidence, 0.0, 1.0 );
        this.psi = 1.0 + this.phi * this.phi / 2.0;
        this.epsilon = 1.0 + this.phi * this.phi;
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
