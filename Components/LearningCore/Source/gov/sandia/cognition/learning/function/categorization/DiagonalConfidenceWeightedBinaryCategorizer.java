/*
 * File:                DiagonalConfidenceWeightedBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 13, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;

/**
 * A confidence-weighted linear predictor with a diagonal covariance,
 * which is stored as a vector.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class DiagonalConfidenceWeightedBinaryCategorizer
    extends AbstractConfidenceWeightedBinaryCategorizer
{
    
    /** The variance values, which is the diagonal of the covariance
     *  matrix. It is stored as a vector to avoid needing to instantiate
     *  and use matrix operations with it. */
    protected Vector variance;

    /**
     * Creates a new {@code DiagonalConfidenceWeightedBinaryCategorizer}.
     */
    public DiagonalConfidenceWeightedBinaryCategorizer()
    {
        super();

        this.setVariance(null);
    }

    @Override
    public UnivariateGaussian evaluateAsGaussian(
        final Vectorizable input)
    {
        if (!this.isInitialized())
        {
            // Variance is not yet initialized.
            return new UnivariateGaussian();
        }
        else
        {
            final Vector x = input.convertToVector();
            return new UnivariateGaussian(
                this.evaluateAsDouble(x),
                x.dotProduct(x.dotTimes(this.getVariance())));
        }
    }

    @Override
    public boolean isInitialized()
    {
        return this.getMean() != null && this.getVariance() != null;
    }

    @Override
    public DiagonalMatrix getCovariance()
    {
        return MatrixFactory.getDiagonalDefault().createDiagonal(
            this.getVariance());
    }

    /**
     * Gets the variance vector. Used as the diagonal of the covariance
     * matrix.
     *
     * @return
     *      The variance vector.
     */
    public Vector getVariance()
    {
        return this.variance;
    }

    /**
     * Sets the variance vector. Used as the diagonal of the covariance
     * matrix.
     *
     * @param   variance
     *      The variance vector.
     */
    public void setVariance(
        final Vector variance)
    {
        this.variance = variance;
    }

}