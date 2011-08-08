/*
 * File:                VectorizablePerturber.java
 * Authors:             Jonathan McClain, Justin Basilico, and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.annealing;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.CholeskyDecompositionMTJ;
import gov.sandia.cognition.util.AbstractRandomized;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * The VectorizablePerturber implements a Perturber for Vectorizable objects.
 * It uses an underlying random number generator to perturb each element of the
 * vector.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @author Jonathan McClain
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer = "Kevin R. Dixon",
            date = "2008-07-22",
            changesNeeded = false,
            comments = {
                "Moved previous code review to annotation.",
                "Fixed a few typos in javadoc.",
                "Code looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer = "Justin Basilico",
            date = "2006-10-02",
            changesNeeded = false,
            comments = "Minor documentation updates."
        )
    }
)
public class VectorizablePerturber
    extends AbstractRandomized
    implements Perturber<Vectorizable>
{

    /** The covariance square root matrix to use while perturbing. */
    private Matrix covarianceSqrt = null;

    /** 
     * Creates a new instance of VectorizablePerturber. This constructor can
     * take a non-negligible amount of time since it needs to do an 
     * eigendecomposition on the covariance matrix in order to calculate its
     * square root.
     *
     * @param random The random number generator to use.
     * @param covariance The covariance matrix to use in perturbing. It must
     *        be a symmetric matrix.
     */
    public VectorizablePerturber(
        final Random random,
        final Matrix covariance)
    {
        super(random);

        this.setCovariance(covariance);
    }

    @Override
    public VectorizablePerturber clone()
    {
        final VectorizablePerturber result = (VectorizablePerturber) 
            super.clone();
        result.covarianceSqrt = ObjectUtil.cloneSafe(this.covarianceSqrt);
        return result;
    }

    /**
     * Perturbs the given Vectorizable by cloning it and then operating on
     * the clone by side-effect.
     *
     * @param input The Vectorizable to perturb.
     * @return The perturbed version of the given vector, which is a new object.
     */
    public Vectorizable perturb(
        final Vectorizable input)
    {
        // Clone the object so that we do not modify it.
        final Vectorizable clone = input.clone();

        // Convert the object to a vector.
        Vector parameters = clone.convertToVector();

        // Purturb the vector.
        parameters = this.perturbVector(parameters);

        // Reset the parameters of the vectorizable.
        clone.convertFromVector(parameters);

        // Return the purturbed clone.
        return clone;
    }

    /**
     * Perturbs the given vector using the underlying random number generator.
     *
     * @param  input The vector to perturb.
     * @return The perturbed vector using the underlying random number 
     *         generator.
     */
    public Vector perturbVector(
        final Vector input)
    {
        Vector perturbed = MultivariateGaussian.sample(
            input, this.getCovarianceSqrt(), this.getRandom());
        return perturbed;
    }
    
    /**
     * Sets the covariance of the perturber. This calls setCoverianceSqrt
     * on the square root decomposition of the given matrix.
     * 
     * @param   covariance The covariance matrix.
     */
    public void setCovariance(
        final Matrix covariance)
    {
        final DenseMatrix denseCovariance =
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix(covariance);

        this.setCovarianceSqrt(
            CholeskyDecompositionMTJ.create( denseCovariance ).getR() );
    }

    /**
     * Gets the covariance square root matrix.
     *
     * @return The covariance square root matrix.
     */
    public Matrix getCovarianceSqrt()
    {
        return this.covarianceSqrt;
    }

    /**
     * Sets the covariance square root matrix.
     *
     * @param covarianceSqrt The new covariance square root matrix.
     */
    protected void setCovarianceSqrt(
        final Matrix covarianceSqrt)
    {
        this.covarianceSqrt = covarianceSqrt;
    }

}
