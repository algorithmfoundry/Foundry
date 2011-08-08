/*
 * File:                MultivariateGaussianMeanCovarianceBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.MultivariateStudentTDistribution;
import gov.sandia.cognition.statistics.distribution.NormalInverseWishartDistribution;
import gov.sandia.cognition.util.Pair;
import java.util.Arrays;

/**
 * Performs robust estimation of both the mean and covariance of a
 * MultivariateGaussian conditional distribution using the conjugate prior
 * Normal-Inverse-Wishart distribution.  The resulting predictive distribution
 * for future data is a multivariate Student's t-distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "Andrew Gelman",
                "John B. Carlin",
                "Hal S. Stern",
                "Donald B. Rubin"
            },
            title="Bayesian Data Analysis, Second Edition",
            type=PublicationType.Book,
            year=2004,
            pages={87,88}
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Conjugate Prior",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Conjugate_prior"
        )
    }
)
public class MultivariateGaussianMeanCovarianceBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Vector,Matrix,MultivariateGaussian,NormalInverseWishartDistribution>
    implements ConjugatePriorBayesianEstimatorPredictor<Vector,Matrix,MultivariateGaussian,NormalInverseWishartDistribution>
{

    /** 
     * Creates a new instance of MultivariateGaussianMeanCovarianceBayesianEstimator 
     */
    public MultivariateGaussianMeanCovarianceBayesianEstimator()
    {
        this( new NormalInverseWishartDistribution() );
    }

    /**
     * Creates a new instance of MultivariateGaussianMeanCovarianceBayesianEstimator 
     * @param dimensionality
     * Dimensionality of the observations to consider.
     */
    public MultivariateGaussianMeanCovarianceBayesianEstimator(
        int dimensionality )
    {
        this( new NormalInverseWishartDistribution( dimensionality ) );
    }

    /** 
     * Creates a new instance of MultivariateGaussianMeanCovarianceBayesianEstimator 
     * @param belief 
     * Initial belief of the conditional parameters
     */
    public MultivariateGaussianMeanCovarianceBayesianEstimator(
        NormalInverseWishartDistribution belief )
    {
        this( new MultivariateGaussian( belief.getInputDimensionality() ), belief );
    }

    /**
     * Creates a new instance
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public MultivariateGaussianMeanCovarianceBayesianEstimator(
        MultivariateGaussian conditional,
        NormalInverseWishartDistribution prior )
    {
        this( new MultivariateGaussianMeanCovarianceBayesianEstimator.Parameter(
            conditional, prior) );
    }
    
    /**
     * Creates a new instance
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected MultivariateGaussianMeanCovarianceBayesianEstimator(
        BayesianParameter<Matrix,MultivariateGaussian,NormalInverseWishartDistribution> parameter )
    {
        super( parameter );
    }

    public MultivariateGaussianMeanCovarianceBayesianEstimator.Parameter createParameter(
        MultivariateGaussian conditional,
        NormalInverseWishartDistribution prior)
    {
        return new MultivariateGaussianMeanCovarianceBayesianEstimator.Parameter( conditional, prior );
    }

    public void update(
        NormalInverseWishartDistribution target,
        Vector data)
    {
        this.update(target, Arrays.asList(data) );
    }

    @Override
    public void update(
        NormalInverseWishartDistribution prior,
        Iterable<? extends Vector> data)
    {
        final int n = CollectionUtil.size(data);

        Pair<Vector,Matrix> pair =
            MultivariateStatisticsUtil.computeMeanAndCovariance(data);
        Vector sampleMean = pair.getFirst();
        Matrix sampleCovariance = pair.getSecond();

        Vector lambda = prior.getGaussian().getMean();
        double nu = prior.getCovarianceDivisor();
        int alpha = prior.getInverseWishart().getDegreesOfFreedom();
        Matrix beta = prior.getInverseWishart().getInverseScale();

        int alphahat = alpha + n;
        double nuhat = nu+n;

        Vector lambdahat = lambda.scale(nu/n);
        lambdahat.plusEquals( sampleMean );
        lambdahat.scaleEquals( n/nuhat );

        Vector delta = sampleMean;
        delta.minusEquals(lambda);
        Matrix betahat = sampleCovariance;
        if( n > 1 )
        {
            betahat.scaleEquals(n);
        }
        betahat.plusEquals(beta);
        betahat.plusEquals( delta.outerProduct(delta.scale((n*nu)/nuhat)) );

        prior.getGaussian().setMean(lambdahat);
        prior.setCovarianceDivisor(nuhat);
        prior.getInverseWishart().setDegreesOfFreedom(alphahat);
        prior.getInverseWishart().setInverseScale(betahat);

    }

    public double computeEquivalentSampleSize(
        NormalInverseWishartDistribution belief)
    {
        return belief.getCovarianceDivisor();
    }

    public MultivariateStudentTDistribution createPredictiveDistribution(
        NormalInverseWishartDistribution posterior)
    {
        Vector mean = posterior.getGaussian().getMean();
        double dofs = posterior.getInverseWishart().getDegreesOfFreedom()
            - posterior.getInverseWishart().getInputDimensionality() + 1.0;

        Matrix covariance = posterior.getInverseWishart().getInverseScale().scale(
            (posterior.getCovarianceDivisor()+1.0) / (posterior.getCovarianceDivisor()*dofs) );
        Matrix precision = covariance.inverse();
        return new MultivariateStudentTDistribution( dofs, mean, precision );
    }

    /**
     * Parameter for this conjugate prior estimator.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Matrix,MultivariateGaussian,NormalInverseWishartDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "meanAndCovariance";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            MultivariateGaussian conditional,
            NormalInverseWishartDistribution prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Matrix value)
        {
            final int dim = this.conditionalDistribution.getInputDimensionality();
            if( (value.getNumRows() != dim) ||
                (value.getNumColumns() != (dim+1) ) )
            {
                throw new IllegalArgumentException( "Expected (dim x dim+1) Matrix" );
            }
            Vector mean = value.getColumn(0);
            Matrix covariance = value.getSubMatrix(0, dim-1, 1,dim);
            this.conditionalDistribution.setMean(mean);
            this.conditionalDistribution.setCovariance(covariance);
        }

        public Matrix getValue()
        {
            final int dim = this.conditionalDistribution.getInputDimensionality();
            Matrix parameter = MatrixFactory.getDefault().createMatrix(dim, dim+1);
            parameter.setColumn(0, this.conditionalDistribution.getMean() );
            parameter.setSubMatrix(0,1, this.conditionalDistribution.getCovariance() );
            return parameter;
        }        

    }

}
