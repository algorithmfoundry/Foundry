/*
 * File:                MultivariateDecorrelator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.CholeskyDecompositionMTJ;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * Decorrelates a data using a mean and full or diagonal covariance matrix.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class MultivariateDecorrelator
    extends AbstractCloneableSerializable
    implements Evaluator<Vectorizable, Vector>,
        VectorInputEvaluator<Vectorizable, Vector>,
        VectorOutputEvaluator<Vectorizable, Vector>
{
    /** The underlying Gaussian. */
    protected MultivariateGaussian gaussian;

    /** The square root of the inverse of the covariance. */
    private Matrix covarianceInverseSquareRoot;

    /**
     * Creates a new instance of MultivariateDecorrelator with no underlying
     * Gaussian. It will have to be set later on using setGaussian.
     */
    public MultivariateDecorrelator()
    {
        this((MultivariateGaussian) null);
    }

    /**
     * Creates a new instance of MultivariateDecorrelator with the given
     * mean and variance.
     *
     * @param  mean The mean.
     * @param  covariance The variance.
     */
    public MultivariateDecorrelator(
        final Vector mean,
        final Matrix covariance)
    {
        this(new MultivariateGaussian(mean, covariance));
    }

    /**
     * Creates a new instance of MultivariateDecorrelator with the given
     * multivariate Gaussian.
     *
     * @param   gaussian
     *      The multivariate Gaussian to use.
     */
    public MultivariateDecorrelator(
        final MultivariateGaussian gaussian)
    {
        super();

        this.setGaussian(gaussian);
    }

    /**
     * Copy constructor.
     * @param other
     * MultivariateDecorrelator to copy
     */
    public MultivariateDecorrelator(
        final MultivariateDecorrelator other)
    {
        this(ObjectUtil.cloneSafe(other.getGaussian()));
    }

    /**
     * Creates a new copy of this MultivariateDecorrelator.
     *
     * @return A new copy of this MultivariateDecorrelator.
     */
    @Override
    public MultivariateDecorrelator clone()
    {
        final MultivariateDecorrelator clone =
            (MultivariateDecorrelator) super.clone();
        clone.gaussian = ObjectUtil.cloneSafe(this.gaussian);
        clone.covarianceInverseSquareRoot =
            ObjectUtil.cloneSafe(this.covarianceInverseSquareRoot);
        return clone;
    }

    /**
     * Normalizes the given double value by subtracting the mean and dividing
     * by the standard deviation (the square root of the variance).
     *
     * @param  value The value to normalize.
     * @return The normalized value.
     */
    public Vector evaluate(
        final Vectorizable value)
    {
        final Vector input = value.convertToVector();
        return input.minus(this.getMean()).times(
            this.getCovarianceInverseSquareRoot());
    }

    @Override
    public int getInputDimensionality()
    {
        return this.getGaussian().getInputDimensionality();
    }

    @Override
    public int getOutputDimensionality()
    {
        return this.getGaussian().getInputDimensionality();
    }
    
    /**
     * Gets the mean of the underlying Gaussian.
     *
     * @return
     *      The mean.
     */
    public Vector getMean()
    {
        return this.getGaussian().getMean();
    }

    /**
     * Gets the covariance.
     *
     * @return 
     *      The covariance
     */
    public Matrix getCovariance()
    {
        return this.getGaussian().getCovariance();
    }

    /**
     * Gets the underlying multivariate Gaussian.
     *
     * @return The underlying multivariate Gaussian.
     */
    public MultivariateGaussian getGaussian()
    {
        return this.gaussian;
    }

    /**
     * Sets the underlying multivariate Gaussian. A copy of this Gaussian is
     * kept in the object.
     *
     * @param   gaussian
     *      The Gaussian to use.
     */
    public void setGaussian(
        final MultivariateGaussian gaussian)
    {
        if (gaussian == null)
        {
            this.gaussian = null;
            this.covarianceInverseSquareRoot = null;
        }
        else
        {
            this.gaussian = gaussian.clone();
            final CholeskyDecompositionMTJ chokesky =
                CholeskyDecompositionMTJ.create(
                    DenseMatrixFactoryMTJ.INSTANCE.copyMatrix(
                        (gaussian.getCovarianceInverse())));
            this.covarianceInverseSquareRoot = chokesky.getR();
        }
    }

    /**
     * Gets the square root of the inverse of the covariance matrix. This value
     * is what is used to perform the normalization.
     *
     * @return
     *      The square root of the inverse of the covariance matrix.
     */
    public Matrix getCovarianceInverseSquareRoot()
    {
        return this.covarianceInverseSquareRoot;
    }

    /**
     * Learns a normalization based on a mean and full covariance matrix from
     * the given data.
     *
     * @param   values
     *      The values to learn the decorrelator from.
     * @param   defaultCovariance
     *      The default value for the covariance. Added to the diagonal of the
     *      covariance matrix to prevent singular values.
     * @return
     *      The MultivariateDecorrelator created from the multivariate mean and
     *      variance.
     */
    public static MultivariateDecorrelator learnFullCovariance(
        final Collection<? extends Vectorizable> values,
        final double defaultCovariance)
    {
        // Convert the values to vector form.
        final Collection<Vector> vectorValues =
            DatasetUtil.asVectorCollection(values);

        // Learn the maximum likelihood estimator of the Gaussian.
        final MultivariateGaussian.PDF pdf =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(
                vectorValues, defaultCovariance);
        return new MultivariateDecorrelator(pdf);
    }

    /**
     * Learns a normalization based on a mean and covariance where the
     * covariance matrix is diagonal. That is, each dimension is treated
     * separately.
     *
     * @param  values
     *      The values to use to build the normalizer.
     * @param   defaultCovariance
     *      The default value for the covariance. Added to the diagonal of the
     *      covariance matrix to prevent singular values.
     * @return 
     *      The MultivariateDecorrelator created from the multivariate mean and
     *         variance of the given values.
     */
    public static MultivariateDecorrelator learnDiagonalCovariance(
        final Collection<? extends Vectorizable> values,
        final double defaultCovariance)
    {
        if (values == null)
        {
            // Error: Bad values.
            throw new IllegalArgumentException("values cannot be null.");
        }

        final int count = values.size();
        if (count <= 0)
        {
            // Error: Not enough samples.
            throw new IllegalArgumentException("values cannot be empty.");
        }

        // Compute the mean.
        final RingAccumulator<Vector> meanAccumulator =
            new RingAccumulator<Vector>();
        for (Vectorizable value : values)
        {
            meanAccumulator.accumulate(value.convertToVector());
        }

        final Vector mean = meanAccumulator.getMean();

        // Compute the variance.
        final Vector variance = 
            VectorFactory.getDefault().createVector( mean.getDimensionality() );
        for (Vectorizable value : values)
        {
            final Vector difference = value.convertToVector().minus(mean);
            difference.dotTimesEquals(difference);
            variance.plusEquals(difference);
        }

        // Compute the total variance.
        variance.scaleEquals(1.0 / (double) count);

        // Add the default covariance to the variance.
        variance.plusEquals(VectorFactory.getDefault().createVector(
            mean.getDimensionality(), defaultCovariance));

        // Convert the variance into a diagonal covariance matrix.
        final Matrix covariance =
            MatrixFactory.getDefault().createDiagonal(variance);


        return new MultivariateDecorrelator(mean, covariance);
    }

    /**
     * The {@code FullCovarianceLearner} class implements a {@code BatchLearner}
     * object for a {@code MultivariateDecorrelator}.
     */
    public static class FullCovarianceLearner
        extends AbstractCloneableSerializable
        implements BatchLearner<Collection<? extends Vectorizable>, MultivariateDecorrelator>
    {
        /** The default value for default covariance is {@value}. */
        public static final double DEFAULT_DEFAULT_COVARIANCE =
            MultivariateGaussian.MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;


        /** The default covariance. Added to the diagonal to prevent it from
         *  becoming singular.
         */
        protected double defaultCovariance;

        /**
         * Creates a new MultivariateDecorrelator.FullCovarianceLearner with
         * the default value for default covariance.
         */
        public FullCovarianceLearner()
        {
            this(DEFAULT_DEFAULT_COVARIANCE);
        }

        /**
         * Creates a new MultivariateDecorrelator.FullCovarianceLearner with the
         * given value for default covariance.
         *
         * @param   defaultCovariance
         *      The default covariance value. Added to the diagonal to prevent
         *      it from becoming singular
         */
        public FullCovarianceLearner(
            final double defaultCovariance)
        {
            super();

            this.setDefaultCovariance(defaultCovariance);
        }

        /**

         * Learns a MultivariateDecorrelator from the given values by
         * computing the mean and covariance of the dimensions.
         *
         * @param  values
         *      The values to use.
         * @return
         *      The MultivariateDecorrelator computed from the given values.
         */
        public MultivariateDecorrelator learn(
            final Collection<? extends Vectorizable> values)
        {
            return MultivariateDecorrelator.learnFullCovariance(
                values, this.getDefaultCovariance());
        }

        /**
         * Gets the default covariance value. It is added to the diagonal terms
         * of the covariance matrix to attempt to prevent them from becoming
         * singular.
         *
         * @return
         *      The default covariance value.
         */
        public double getDefaultCovariance()
        {
            return this.defaultCovariance;
        }

        /**
         * Sets the default covariance value. It is added to the diagonal terms
         * of the covariance matrix to attempt to prevent them from becoming
         * singular.
         *
         * @param   defaultCovariance
         *      The default covariance value. Must be non-negative.
         */
        public void setDefaultCovariance(
            final double defaultCovariance)
        {
            if (defaultCovariance < 0.0)
            {
                throw new IllegalArgumentException(
                    "defaultCovariance cannot be negative.");
            }

            this.defaultCovariance = defaultCovariance;
        }

    }

    /**
     * The {@code DiagonalCovarianceLearner} class implements a {@code BatchLearner}
     * object for a {@code MultivariateDecorrelator}.
     */
    public static class DiagonalCovarianceLearner
        extends AbstractCloneableSerializable
        implements BatchLearner<Collection<? extends Vectorizable>, MultivariateDecorrelator>
    {

        /** The default value for default covariance is {@value}. */
        public static final double DEFAULT_DEFAULT_COVARIANCE =
            MultivariateGaussian.MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;


        /** The default covariance. Added to the diagonal to prevent it from
         *  becoming singular.
         */
        protected double defaultCovariance;

        /**
         * Creates a new MultivariateDecorrelator.DiagonalCovarianceLearner
         * with the default value for default covariance.
         */
        public DiagonalCovarianceLearner()
        {
            this(DEFAULT_DEFAULT_COVARIANCE);
        }

        /**
         * Creates a new MultivariateDecorrelator.DiagonalCovarianceLearner with
         * the given value for default covariance.
         *
         * @param   defaultCovariance
         *      The default covariance value. Added to the diagonal to prevent
         *      it from becoming singular
         */
        public DiagonalCovarianceLearner(
            final double defaultCovariance)
        {
            super();
            
            this.setDefaultCovariance(defaultCovariance);
        }

        /**
         * Learns a MultivariateDecorrelator from the given values by
         * computing the mean and variance for each dimension separately.
         *
         * @param  values
         *      The values to use.
         * @return 
         *      The MultivariateDecorrelator computed from the given values
         *      with a diagonal covariance matrix.
         */
        public MultivariateDecorrelator learn(
            final Collection<? extends Vectorizable> values)
        {
            return MultivariateDecorrelator.learnDiagonalCovariance(
                values, this.getDefaultCovariance());
        }

        /**
         * Gets the default covariance value. It is added to the diagonal terms
         * of the covariance matrix to attempt to prevent them from becoming
         * singular.
         *
         * @return
         *      The default covariance value.
         */
        public double getDefaultCovariance()
        {
            return this.defaultCovariance;
        }

        /**
         * Sets the default covariance value. It is added to the diagonal terms
         * of the covariance matrix to attempt to prevent them from becoming
         * singular.
         *
         * @param   defaultCovariance
         *      The default covariance value. Must be non-negative.
         */
        public void setDefaultCovariance(
            final double defaultCovariance)
        {
            if (defaultCovariance < 0.0)
            {
                throw new IllegalArgumentException(
                    "defaultCovariance cannot be negative.");
            }

            this.defaultCovariance = defaultCovariance;
        }

    }
}
