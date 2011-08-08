/*
 * File:                UnivariateGaussianMeanVarianceBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 17, 2010, Sandia Corporation.
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
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.NormalInverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Computes the mean and variance of a univariate Gaussian using the
 * conjugate prior NormalInverseGammaDistribution
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Jeff Grynaviski",
            title="Bayesian Analysis of the Normal Distribution, Part II",
            type=PublicationType.Misc,
            year=2009,
            url="http://home.uchicago.edu/~grynav/bayes/ABSLec8.ppt"
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
public class UnivariateGaussianMeanVarianceBayesianEstimator
    extends AbstractConjugatePriorBayesianEstimator<Double,Vector,UnivariateGaussian,NormalInverseGammaDistribution>
    implements ConjugatePriorBayesianEstimatorPredictor<Double,Vector,UnivariateGaussian,NormalInverseGammaDistribution>
{

    /** 
     * Creates a new instance of UnivariateGaussianMeanVarianceBayesianEstimator 
     */
    public UnivariateGaussianMeanVarianceBayesianEstimator()
    {
        this( new NormalInverseGammaDistribution() );
    }

    /**
     * Creates a new instance of UnivariateGaussianMeanVarianceBayesianEstimator
     * @param prior
     * Conjugate prior
     */
    public UnivariateGaussianMeanVarianceBayesianEstimator(
        NormalInverseGammaDistribution prior )
    {
        this( new UnivariateGaussian(), prior );
    }

    /**
     * Creates a new instance of UnivariateGaussianMeanVarianceBayesianEstimator
     * @param conditional
     * Conditional distribution
     * @param prior
     * Conjugate prior
     */
    public UnivariateGaussianMeanVarianceBayesianEstimator(
        UnivariateGaussian conditional,
        NormalInverseGammaDistribution prior )
    {
        this( new UnivariateGaussianMeanVarianceBayesianEstimator.Parameter(
            conditional, prior) );
    }

    /**
     * Creates a new instance of UnivariateGaussianMeanVarianceBayesianEstimator
     * @param parameter
     * Parameter that describes the relationship between the conditional and
     * conjugate prior
     */
    protected UnivariateGaussianMeanVarianceBayesianEstimator(
        BayesianParameter<Vector,UnivariateGaussian,NormalInverseGammaDistribution> parameter )
    {
        super( parameter );
    }

    public UnivariateGaussianMeanVarianceBayesianEstimator.Parameter createParameter(
        UnivariateGaussian conditional,
        NormalInverseGammaDistribution prior)
    {
        return new UnivariateGaussianMeanVarianceBayesianEstimator.Parameter( conditional, prior );
    }

    public void update(
        NormalInverseGammaDistribution target,
        Double data)
    {
        this.update(target, Arrays.asList(data) );
    }

    @Override
    public void update(
        NormalInverseGammaDistribution prior,
        Iterable<? extends Double> data )
    {
        ArrayList<? extends Double> dataArray = CollectionUtil.asArrayList(data);
        final int n = dataArray.size();

        double sum = 0.0;
        double sum2 = 0.0;
        for( Number x : data )
        {
            double v = x.doubleValue();
            sum += v;
            sum2 += v*v;
        }

        final double sampleMean = sum / n;
        final double sampleVariance = (n*sum2 - sum*sum) / (n*n);

        double lambda = prior.getLocation();
        double nu = prior.getPrecision();
        double alpha = prior.getShape();
        double beta = prior.getScale();

        double alphahat = alpha + n/2.0;
        double nuhat = nu+n;
        double lambdahat = (nu*lambda + sum) / nuhat;

        double delta = sampleMean-lambda;
        double betahat = beta + 0.5*n*sampleVariance + 0.5*((n*nu)/nuhat) * (delta*delta);

        prior.setLocation(lambdahat);
        prior.setPrecision(nuhat);
        prior.setShape(alphahat);
        prior.setScale(betahat);
    }

    public double computeEquivalentSampleSize(
        NormalInverseGammaDistribution belief)
    {
        return belief.getPrecision();
    }

    public StudentTDistribution createPredictiveDistribution(
        NormalInverseGammaDistribution posterior )
    {
        double mean = posterior.getLocation();
//        double dofs = 2.0 * posterior.getShape();
        double dofs = posterior.getPrecision();
        double precision = posterior.getShape() / posterior.getScale();
        return new StudentTDistribution( dofs, mean, precision );
    }

    /**
     * Parameter for this conjugate prior estimator.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Vector,UnivariateGaussian,NormalInverseGammaDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "meanAndVariance";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            UnivariateGaussian conditional,
            NormalInverseGammaDistribution prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Vector value)
        {
            value.assertDimensionalityEquals(2);
            double mean = value.getElement(0);
            double variance = value.getElement(1);
            this.conditionalDistribution.setMean(mean);
            this.conditionalDistribution.setVariance(variance);
        }

        public Vector getValue()
        {
            return VectorFactory.getDefault().copyValues(
                this.conditionalDistribution.getMean(),
                this.conditionalDistribution.getVariance() );
        }

    }

}
