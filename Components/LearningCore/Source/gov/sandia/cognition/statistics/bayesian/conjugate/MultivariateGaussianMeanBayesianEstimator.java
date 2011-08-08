/*
 * File:                MultivariateGaussianMeanBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.Arrays;

/**
 * Bayesian estimator for the mean of a MultivariateGaussian using its conjugate
 * prior, which is also a MultivariateGaussian.  This estimation method assumes
 * that the covariance matrix of the estimated mean is known.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="William M. Bolstad",
    title="Introduction to Bayesian Statistics: Second Edition",
    type=PublicationType.Book,
    year=2007,
    pages={208}
)
public class MultivariateGaussianMeanBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Vector,Vector,MultivariateGaussian,MultivariateGaussian>
    implements ConjugatePriorBayesianEstimatorPredictor<Vector,Vector,MultivariateGaussian,MultivariateGaussian>
{

    /**
     * Default dimensionality, {@value}.
     */
    public static final int DEFAULT_DIMENSIONALITY = 1;

    /** 
     * Creates a new instance of MultivariateGaussianMeanBayesianEstimator 
     */
    public MultivariateGaussianMeanBayesianEstimator()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a new instance of MultivariateGaussianMeanBayesianEstimator
     * @param dimensionality
     * Dimensionality of the Vectors
     */
    public MultivariateGaussianMeanBayesianEstimator(
        int dimensionality )
    {
        this( MatrixFactory.getDefault().createIdentity(dimensionality,dimensionality) );
    }

    /**
     * Creates a new instance of MultivariateGaussianMeanBayesianEstimator
     * @param knownCovarianceInverse
     * Known covariance matrix of the estimated mean.
     */
    public MultivariateGaussianMeanBayesianEstimator(
        Matrix knownCovarianceInverse )
    {
        this( knownCovarianceInverse, new MultivariateGaussian(
            VectorFactory.getDefault().createVector(knownCovarianceInverse.getNumRows()),
            MatrixFactory.getDefault().createIdentity(knownCovarianceInverse.getNumRows(), knownCovarianceInverse.getNumColumns()) ) );
    }

    /**
     * Creates a new instance of MultivariateGaussianMeanBayesianEstimator
     * @param knownCovarianceInverse
     * Known covariance matrix inverse of the estimated mean.  Sometimes
     * called the "precision matrix".
     * @param belief
     * Belief distribution of the mean.
     */
    public MultivariateGaussianMeanBayesianEstimator(
        Matrix knownCovarianceInverse,
        MultivariateGaussian belief )
    {
        this( new MultivariateGaussian(
                VectorFactory.getDefault().createVector( knownCovarianceInverse.getNumRows() ),
                knownCovarianceInverse.inverse() ),
            belief );
    }

    /**
     * Creates a new instance of PoissonBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public MultivariateGaussianMeanBayesianEstimator(
        MultivariateGaussian conditional,
        MultivariateGaussian prior )
    {
        this( new MultivariateGaussianMeanBayesianEstimator.Parameter(
            conditional, prior) );
    }

    /**
     * Creates a new instance
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected MultivariateGaussianMeanBayesianEstimator(
        BayesianParameter<Vector,MultivariateGaussian,MultivariateGaussian> parameter )
    {
        super( parameter );
    }

    public MultivariateGaussianMeanBayesianEstimator.Parameter createParameter(
        MultivariateGaussian conditional,
        MultivariateGaussian prior)
    {
        return new MultivariateGaussianMeanBayesianEstimator.Parameter( conditional, prior );
    }

    /**
     * Getter for knownCovarianceInverse.
     * @return
     * Known covariance matrix inverse of the estimated mean.  Sometimes
     * called the "precision matrix".
     */
    public Matrix getKnownCovarianceInverse()
    {
        return this.parameter.getConditionalDistribution().getCovarianceInverse();
    }

    /**
     * Setter for knownCovarianceInverse.
     * @param knownCovarianceInverse
     * Known covariance matrix inverse of the estimated mean.  Sometimes
     * called the "precision matrix".
     */
    public void setKnownCovarianceInverse(
        Matrix knownCovarianceInverse)
    {
        if( !knownCovarianceInverse.isSymmetric() ||
            (knownCovarianceInverse.rank() != knownCovarianceInverse.getNumRows()) )
        {
            throw new IllegalArgumentException(
                "Covariance inverse must be symmetric and invertible!" );
        }
        this.parameter.getConditionalDistribution().setCovariance(
            knownCovarianceInverse.inverse() );
    }

    @Override
    public void update(
        MultivariateGaussian target,
        Iterable<? extends Vector> data)
    {
        
        int N = CollectionUtil.size(data);
        Matrix Ci0 = target.getCovarianceInverse();
        Matrix CiN = this.getKnownCovarianceInverse().clone();
        if( N > 1 )
        {
            CiN.scaleEquals(N);
        }
        Vector sampleMean = MultivariateStatisticsUtil.computeMean(data);
        Vector t0 = Ci0.times( target.getMean() );
        t0.plusEquals( CiN.times( sampleMean ) );

        // Saving another Matrix creation here... just make sure the
        // "t0" stuff gets completed first
        CiN.plusEquals(Ci0);
        Matrix updatedCovariance = CiN.inverse();
        Vector updatedMean = updatedCovariance.times( t0 );

        target.setMean(updatedMean);
        target.setCovariance(updatedCovariance);
    }

    public void update(
        MultivariateGaussian updater,
        Vector data)
    {
        this.update(updater, Arrays.asList(data) );
    }

    public double computeEquivalentSampleSize(
        MultivariateGaussian belief)
    {
        // The ratio is R = det(KnownVariance) / det(beliefVariance)
        // R = det(beliefVarianceInverse) / det(KnownVarianceInverse)
        // log(R) = log(det(beliefVarianceInverse)) - log(det(KnownVarianceInverse))
        // Effective sample size is then
        // SS = exp(log(R)/N), where "N" is the dimensionality of the data.
        ComplexNumber logR = belief.getCovarianceInverse().logDeterminant().minus(
            this.getKnownCovarianceInverse().logDeterminant() );
        return Math.exp(logR.getMagnitude()/belief.getMean().getDimensionality());
    }

    public MultivariateGaussian createPredictiveDistribution(
        MultivariateGaussian posterior)
    {
        Vector mean = posterior.getMean().clone();
        Matrix C = posterior.getCovariance().plus(
            this.parameter.getConditionalDistribution().getCovariance() );
        return new MultivariateGaussian( mean, C );
    }

    @Override
    public MultivariateGaussian createConditionalDistribution(
        Vector parameter)
    {
        parameter.assertDimensionalityEquals(
            this.parameter.getConditionalDistribution().getInputDimensionality() );
        return super.createConditionalDistribution(parameter);
    }

    /**
     * Parameter of this conjugate prior relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Vector,MultivariateGaussian,MultivariateGaussian>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "mean";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            MultivariateGaussian conditional,
            MultivariateGaussian prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Vector value)
        {
            value.assertDimensionalityEquals(
                this.conditionalDistribution.getInputDimensionality() );
            this.conditionalDistribution.setMean(value);
        }

        public Vector getValue()
        {
            return this.conditionalDistribution.getMean();
        }

    }

}
