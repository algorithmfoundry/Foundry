/*
 * File:                ConfidenceWeightedDiagonalDeviationProject.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 13, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.confidence;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DiagonalConfidenceWeightedBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * An implementation of the Standard Deviation (Stdev) algorithm for learning
 * a confidence-weighted categorizer. It updates only the diagonal of the
 * covariance matrix, thus computing the variance for each dimension. This
 * corresponds to the "Stdev-project" version.
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
public class ConfidenceWeightedDiagonalDeviationProject
    extends ConfidenceWeightedDiagonalDeviation
{

    /**
     * Creates a new {@code ConfidenceWeightedDiagonalDeviationProject} with
     * default parameters.
     */
    public ConfidenceWeightedDiagonalDeviationProject()
    {
        this(DEFAULT_CONFIDENCE, DEFAULT_DEFAULT_VARIANCE);
    }

    /**
     * Creates a new {@code ConfidenceWeightedDiagonalDeviationProject} with the given
     * parameters.
     *
     * @param   confidence
     *      The confidence to use. Must be in [0, 1].
     * @param   defaultVariance
     *      The default value to initialize the covariance matrix to.
     */
    public ConfidenceWeightedDiagonalDeviationProject(
        final double confidence,
        final double defaultVariance)
    {
        super(confidence, defaultVariance);
    }

    @Override
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

        final double m = margin;
        final double v = marginVariance;

        // Only update if there is a margin error (and the variance is valid).
        final boolean update = v > 0.0 && m <= this.phi * Math.sqrt(v);
        if (!update)
        {
            return;
        }

        final double alpha = (-m * psi + Math.sqrt(m * m * Math.pow(phi, 4)
            / 4.0 + v * phi * phi * epsilon)) / (v * epsilon);

        final double u = 0.25 * Math.pow(-alpha * v * phi
                + Math.sqrt(alpha * alpha * v * v * phi * phi + 4.0 * v), 2);
        // Compute the update factor.
        final double sqrtU = Math.sqrt(u);
        // double beta = alpha * phi / (sqrtU + v * alpha * phi);
        final double factor = alpha * phi / sqrtU;

        // Update only if alpha is valid.
        if (alpha > 0.0)
        {
            // Compute the new mean.
            final Vector meanUpdate = varianceTimesInput.scale(actual * alpha);
            mean.plusEquals(meanUpdate);

            // Update the variance only if u and sqrtU are valid. This helps
            // avoid division-by-zero which causes NaNs.
            if (u > 0.0 && sqrtU > 0.0)
            {

                // Update the variance.
                // We loop over the input entries to handle sparse vectors since
                // a zero on the input will mean no change to the variance.
                for (VectorEntry entry : input)
                {
                    final int index = entry.getIndex();
                    final double value = entry.getValue();
                    final double sigma = variance.getElement(index);
                    double newSigma = (1.0 / sigma) + factor * value * value;
                    newSigma = 1.0 / newSigma;

                    variance.setElement(index, newSigma);
                }
            }
        }

        // Set the mean and variance.
        target.setMean(mean);
        target.setVariance(variance);
    }

}
