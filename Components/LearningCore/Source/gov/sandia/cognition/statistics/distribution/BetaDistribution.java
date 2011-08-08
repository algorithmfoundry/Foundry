/*
 * File:                BetaDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Computes the Beta-family of probability distributions.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Beta distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Beta_distribution"
)
public class BetaDistribution
    extends AbstractClosedFormSmoothUnivariateDistribution
    implements EstimableDistribution<Double,BetaDistribution>
{

    /**
     * Default alpha, {@value}.
     */
    public static final double DEFAULT_ALPHA = 2.0;

    /**
     * Default beta, {@value}.
     */
    public static final double DEFAULT_BETA = 2.0;

    /**
     * Alpha shape parameter, must be greater than 0 (typically greater than 1)
     */
    private double alpha;

    /**
     * Alpha shape parameter, must be greater than 0 (typically greater than 1)
     */
    private double beta;

    /**
     * Default constructor.
     */
    public BetaDistribution()
    {
        this( DEFAULT_ALPHA, DEFAULT_BETA );
    }

    /**
     * Creates a new instance of BetaDistribution
     * @param alpha
     * Alpha shape parameter, must be greater than 0 (typically greater than 1)
     * @param beta
     * Beta shape parameter, must be greater than 0 (typically greater than 1)
     */
    public BetaDistribution(
        final double alpha,
        final double beta )
    {
        this.setAlpha( alpha );
        this.setBeta( beta );
    }

    /**
     * Copy Constructor
     * @param other
     * BetaDistribution to copy
     */
    public BetaDistribution(
        final BetaDistribution other  )
    {
        this( other.getAlpha(), other.getBeta() );
    }

    @Override
    public BetaDistribution clone()
    {
        return (BetaDistribution) super.clone();
    }

    @Override
    public Double getMean()
    {
        return this.getAlpha() / (this.getAlpha() + this.getBeta());
    }

    @Override
    public double getVariance()
    {
        double numerator = this.getAlpha() * this.getBeta();
        double apb = this.getAlpha() + this.getBeta();
        double denominator = apb * apb * (apb + 1);
        return numerator / denominator;
    }
    
    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        ArrayList<Double> Xs = GammaDistribution.sample(
            this.alpha, 1.0, random,numSamples);
        ArrayList<Double> Ys = GammaDistribution.sample(
            this.beta, 1.0, random,numSamples);
        for( int n = 0; n < numSamples; n++ )
        {
            final double x = Xs.get(n);
            final double y = Ys.get(n);
            samples.add( x/(x+y) );
        }
        return samples;
    }    
    
    /**
     * Gets the parameters of the distribution
     * @return
     * 2-dimensional Vector with [alpha beta]
     */
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getAlpha(), this.getBeta() );
    }

    /**
     * Sets the parameters of the distribution
     * @param parameters
     * 2-dimensional Vector with [alpha beta]
     */
    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if (parameters.getDimensionality() != 2)
        {
            throw new IllegalArgumentException(
                "Expecting 2-dimensional parameter Vector!" );
        }

        this.setAlpha( parameters.getElement( 0 ) );
        this.setBeta( parameters.getElement( 1 ) );
    }

    /**
     * Getter for alpha
     * @return
     * Alpha shape parameter, must be greater than 0 (typically greater than 1)
     */
    public double getAlpha()
    {
        return this.alpha;
    }

    /**
     * Setter for alpha
     * @param alpha
     * Alpha shape parameter, must be greater than 0 (typically greater than 1)
     */
    public void setAlpha(
        final double alpha )
    {
        if (alpha <= 0.0)
        {
            throw new IllegalArgumentException( "Alpha must be > 0.0" );
        }
        this.alpha = alpha;
    }

    /**
     * Getter for beta
     * @return
     * Beta shape parameter, must be greater than 0 (typically greater than 1)
     */
    public double getBeta()
    {
        return this.beta;
    }

    /**
     * Setter for beta
     * @param beta
     * Beta shape parameter, must be greater than 0 (typically greater than 1)
     */
    public void setBeta(
        final double beta )
    {
        if (beta <= 0.0)
        {
            throw new IllegalArgumentException( "Beta must be > 0.0" );
        }
        this.beta = beta;
    }

    @Override
    public BetaDistribution.CDF getCDF()
    {
        return new BetaDistribution.CDF( this );
    }

    @Override
    public BetaDistribution.PDF getProbabilityFunction()
    {
        return new BetaDistribution.PDF( this );
    }

    @Override
    public Double getMinSupport()
    {
        return 0.0;
    }

    @Override
    public Double getMaxSupport()
    {
        return 1.0;
    }

    @Override
    public BetaDistribution.MomentMatchingEstimator getEstimator()
    {
        return new BetaDistribution.MomentMatchingEstimator();
    }

    /**
     * Estimates the parameters of a Beta distribution using the matching
     * of moments, not maximum likelihood.
     */
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
        pages=582,
        notes="Equation A.3"
    )
    public static class MomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,BetaDistribution>
    {

        /**
         * Default constructor
         */
        public MomentMatchingEstimator()
        {
        }

        @Override
        public BetaDistribution learn(
            final Collection<? extends Double> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            return learn( pair.getFirst(), pair.getSecond() );
        }

        /**
         * Computes the Beta distribution describes by the given moments
         * @param mean
         * Mean of the distribution
         * @param variance
         * Variance of the distribution
         * @return
         * Beta distribution that has the same mean/variance as the
         * given parameters.
         */
        public static BetaDistribution learn(
            final double mean,
            final double variance )
        {
            double apb = mean*(1.0-mean) / variance - 1.0;
            double alpha = Math.abs(apb * mean);
            double beta = Math.abs(apb * (1.0-mean));
            return new BetaDistribution( alpha, beta );
        }

    }

    /**
     * Estimates the parameters of a Beta distribution using the matching
     * of moments, not maximum likelihood.
     */
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
        pages=582,
        notes="Equation A.3"
    )
    public static class WeightedMomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,BetaDistribution>
    {

        /**
         * Default constructor
         */
        public WeightedMomentMatchingEstimator()
        {
        }

        @Override
        public BetaDistribution learn(
            final Collection<? extends WeightedValue<? extends Double>> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
            return MomentMatchingEstimator.learn(
                pair.getFirst(), pair.getSecond() );
        }

    }

    /**
     * Beta distribution probability density function
     */
    public static class PDF
        extends BetaDistribution
        implements UnivariateProbabilityDensityFunction
    {    

        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new PDF
         * @param alpha
         * Alpha shape parameter, must be greater than 0 (typically greater than 1)
         * @param beta
         * Beta shape parameter, must be greater than 0 (typically greater than 1)
         */
        public PDF(
            final double alpha,
            final double beta )
        {
            super( alpha, beta );
        }        
        
        /**
         * Copy constructor
         * @param other
         * Underlying Beta Distribution
         */
        public PDF(
            final BetaDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Double input )
        {
            return this.evaluate( input.doubleValue() );
        }
        
        @Override
        public double evaluate(
            final double input )
        {
            return evaluate( input, this.getAlpha(), this.getBeta() );
        }

        /**
         * Evaluate the Beta-distribution PDF for beta(x;alpha,beta)
         * @param x
         * Input to the beta PDF, must be on the interval [0,1]
         * @param alpha
         * Alpha shape parameter, must be greater than 0 (typically greater than 1)
         * @param beta
         * Beta shape parameter, must be greater than 0 (typically greater than 1)
         * @return
         * beta(x;alpha,beta)
         */
        public static double evaluate(
            final double x,
            final double alpha,
            final double beta )
        {
            double p;
            if (x < 0.0)
            {
                p = 0.0;
            }
            else if (x > 1.0)
            {
                p = 0.0;
            }
            else
            {
                p = Math.exp( logEvaluate(x, alpha, beta) );
            }

            return p;

        }
        
        @Override
        public double logEvaluate(
            final Double input)
        {
            return this.logEvaluate((double) input);
        }

        @Override
        public double logEvaluate(
            final double input)
        {
            return logEvaluate(input, this.getAlpha(), this.getBeta() );
        }

        /**
         * Evaluate the Beta-distribution PDF for beta(x;alpha,beta)
         * @param x
         * Input to the beta PDF, must be on the interval [0,1]
         * @param alpha
         * Alpha shape parameter, must be greater than 0 (typically greater than 1)
         * @param beta
         * Beta shape parameter, must be greater than 0 (typically greater than 1)
         * @return
         * beta(x;alpha,beta)
         */
        public static double logEvaluate(
            final double x,
            final double alpha,
            final double beta )
        {
            double plog;
            if (x < 0.0)
            {
                plog = Math.log(0.0);
            }
            else if (x > 1.0)
            {
                plog = Math.log(0.0);
            }
            else
            {
                final double n1 = (alpha-1) * Math.log(x);
                final double n2 = (beta-1) * Math.log( 1.0-x );
                final double d1 = MathUtil.logBetaFunction( alpha, beta );
                plog = n1 + n2 - d1;
            }

            return plog;

        }

        @Override
        public BetaDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * CDF of the Beta-family distribution
     */
    public static class CDF
        extends BetaDistribution
        implements SmoothCumulativeDistributionFunction
    {

        /**
         * Default constructor.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new CDF
         * @param alpha
         * Alpha shape parameter, must be greater than 0 (typically greater than 1)
         * @param beta
         * Beta shape parameter, must be greater than 0 (typically greater than 1)
         */
        public CDF(
            final double alpha,
            final double beta )
        {
            super( alpha, beta );
        }        
        
        /**
         * Copy constructor
         * @param other
         * Underlying Beta Distribution
         */
        public CDF(
            final BetaDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Double input )
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluate(
            final double input )
        {
            return evaluate(
                input, this.getAlpha(), this.getBeta() );
        }

        /**
         * Evaluate the Beta-distribution CDF for Beta(x;alpha,beta)
         * @param x
         * Input to the beta CDF, must be on the interval [0,1]
         * @param alpha
         * Alpha shape parameter, must be greater than 0 (typically greater than 1)
         * @param beta
         * Beta shape parameter, must be greater than 0 (typically greater than 1)
         * @return
         * Beta(x;alpha,beta)
         */
        public static double evaluate(
            final double x,
            final double alpha,
            final double beta )
        {
            double p;
            if (x <= 0.0)
            {
                p = 0.0;
            }
            else if (x >= 1.0)
            {
                p = 1.0;
            }
            else
            {
                p = MathUtil.regularizedIncompleteBetaFunction( alpha, beta, x );
            }

            return p;
        }

        @Override
        public BetaDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public BetaDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getDerivative().evaluate(input);
        }

    }

}
