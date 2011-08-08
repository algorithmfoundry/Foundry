/*
 * File:                DefaultConfidenceWeightedBinaryCategorizer.java
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
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;

/**
 * A default implementation of the {@code ConfidenceWeightedBinaryCategorizer}
 * that stores a full mean and covariance matrix.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class DefaultConfidenceWeightedBinaryCategorizer
    extends AbstractConfidenceWeightedBinaryCategorizer
{
    
    /** The covariance matrix. */
    protected Matrix covariance;

    /**
     * Creates a new, uninitialized {@code DefaultConfidenceWeightedBinaryCategorizer}.
     */
    public DefaultConfidenceWeightedBinaryCategorizer()
    {
        super();

        this.setCovariance(null);
    }

    /**
     * Creates a new {@code DefaultConfidenceWeightedBinaryCategorizer} with
     * the given mean and covariance. The covariance matrix must be an d by d
     * matrix where d is the dimensionality of the mean.
     *
     * @param   mean
     *      The mean vector.
     * @param   covariance
     *      The covariance matrix.
     */
    public DefaultConfidenceWeightedBinaryCategorizer(
        final Vector mean,
        final Matrix covariance)
    {
        super(mean);

        this.setCovariance(covariance);
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
                x.times(this.getCovariance()).dotProduct(x));
        }
    }

    @Override
    public Matrix getCovariance()
    {
        return this.covariance;
    }

    /**
     * Sets the covariance matrix. Must be a square matrix the same size as
     * the dimensionality of the mean.
     *
     * @param   covariance
     *      The covariance matrix.
     */
    public void setCovariance(
        final Matrix covariance)
    {
        this.covariance = covariance;
    }
    
}
