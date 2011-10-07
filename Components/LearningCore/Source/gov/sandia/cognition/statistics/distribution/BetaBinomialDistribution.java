/*
 * File:                BetaBinomialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * A Binomial distribution where the binomial parameter, p, is set according
 * to a Beta distribution instead of a single value.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Eric Weisstein",
    title="Beta Binomial Distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://mathworld.wolfram.com/BetaBinomialDistribution.html"
)
public class BetaBinomialDistribution 
    extends AbstractClosedFormUnivariateDistribution<Number>
    implements ClosedFormDiscreteUnivariateDistribution<Number>,
    EstimableDistribution<Number,BetaBinomialDistribution>
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 1.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Default n, {@value}.
     */
    public static final int DEFAULT_N = 1;

    /**
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    protected int n;

    /**
     * Shape, similar to the beta parameter shape, must be greater than zero
     */
    protected double shape;

    /**
     * Scale, similar to the beta parameter scale, must be greater than zero
     */
    protected double scale;

    /** 
     * Creates a new instance of BetaBinomialDistribution 
     */
    public BetaBinomialDistribution()
    {
        this( DEFAULT_N, DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of BetaBinomialDistribution 
     * @param shape
     * Shape, similar to the beta parameter shape, must be greater than zero
     * @param scale
     * Scale, similar to the beta parameter scale, must be greater than zero
     * @param n
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    public BetaBinomialDistribution(
        final int n,
        final double shape,
        final double scale )
    {
        this.setN(n);
        this.setShape(shape);
        this.setScale(scale);
    }

    /**
     * Copy constructor
     * @param other
     * BetaBinomialDistribution to copy
     */
    public BetaBinomialDistribution(
        final BetaBinomialDistribution other )
    {
        this( other.getN(), other.getShape(), other.getScale() );
    }

    @Override
    public BetaBinomialDistribution clone()
    {
        return (BetaBinomialDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape, similar to the beta parameter shape, must be greater than zero
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape, similar to the beta parameter shape, must be greater than zero
     */
    public void setShape(
        final double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException( "shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale, similar to the beta parameter scale, must be greater than zero
     */
    public double getScale()
    {
        return scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale, similar to the beta parameter scale, must be greater than zero
     */
    public void setScale(
        final double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException( "scale must be > 0.0" );
        }
        this.scale = scale;
    }

    /**
     * Getter for n
     * @return
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    public int getN()
    {
        return this.n;
    }

    /**
     * Setter for n
     * @param n
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    public void setN(
        final int n)
    {
        if( n < 1 )
        {
            throw new IllegalArgumentException( "n must be > 0" );
        }
        this.n = n;
    }

    @Override
    public Number getMean()
    {
        return this.n * this.shape / (this.shape+this.scale);
    }

    @Override
    public ArrayList<? extends Number> sample(
        final Random random,
        final int numSamples)
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    @Override
    public BetaBinomialDistribution.CDF getCDF()
    {
        return new BetaBinomialDistribution.CDF( this );
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.n, this.shape, this.scale );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(3);
        this.setN( (int) parameters.getElement(0) );
        this.setShape(parameters.getElement(1) );
        this.setScale(parameters.getElement(2) );
    }

    @Override
    public Integer getMinSupport()
    {
        return 0;
    }

    @Override
    public Integer getMaxSupport()
    {
        return this.n;
    }

    @Override
    public double getVariance()
    {
        final double ss = this.shape + this.scale;
        final double numer = this.n*this.shape*this.scale*(ss+this.n);
        final double denom = ss*ss*(ss+1);
        return numer / denom;
    }

    @Override
    public IntegerSpan getDomain()
    {
        return new IntegerSpan(0, (int) Math.ceil(this.n) );
    }

    @Override
    public int getDomainSize()
    {
        return ((int) Math.ceil(this.n)) + 1;
    }

    @Override
    public BetaBinomialDistribution.PMF getProbabilityFunction()
    {
        return new BetaBinomialDistribution.PMF( this );
    }

    @Override
    public BetaBinomialDistribution.MomentMatchingEstimator getEstimator()
    {
        return new BetaBinomialDistribution.MomentMatchingEstimator();
    }

    /**
     * PMF of the BetaBinomialDistribution
     */
    public static class PMF
        extends BetaBinomialDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Creates a new instance of BetaBinomialDistribution
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of BetaBinomialDistribution
         * @param shape
         * Shape, similar to the beta parameter shape, must be greater than zero
         * @param scale
         * Scale, similar to the beta parameter scale, must be greater than zero
         * @param n
         * Number of observations, similar to the Binomial N,
         * must be greater than zero
         */
        public PMF(
            final int n,
            final double shape,
            final double scale )
        {
            super( n, shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * BetaBinomialDistribution to copy
         */
        public PMF(
            final BetaBinomialDistribution other )
        {
            super( other );
        }

        @Override
        public BetaBinomialDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

        @Override
        public Double evaluate(
            final Number input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public double logEvaluate(
            final Number input)
        {
            if( input.doubleValue() < 0.0 )
            {
                return Math.log(0.0);
            }
            else if( input.doubleValue() > this.n )
            {
                return Math.log(0.0);
            }

            final int x = input.intValue();
            double logSum = 0.0;
            logSum += MathUtil.logBinomialCoefficient(this.n, x);
            logSum -= MathUtil.logBetaFunction( this.shape, this.scale );
            logSum += MathUtil.logBetaFunction( this.shape + x, this.n+this.scale - x);
            return logSum;
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

    }

    /**
     * CDF of BetaBinomialDistribution
     */
    public static class CDF
        extends BetaBinomialDistribution
        implements ClosedFormCumulativeDistributionFunction<Number>
    {

        /**
         * Creates a new instance of BetaBinomialDistribution
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of BetaBinomialDistribution
         * @param shape
         * Shape, similar to the beta parameter shape, must be greater than zero
         * @param scale
         * Scale, similar to the beta parameter scale, must be greater than zero
         * @param n
         * Number of observations, similar to the Binomial N,
         * must be greater than zero
         */
        public CDF(
            final int n,
            final double shape,
            final double scale )
        {
            super( n, shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * BetaBinomialDistribution to copy
         */
        public CDF(
            final BetaBinomialDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Number input)
        {
            return ProbabilityMassFunctionUtil.computeCumulativeValue(
                input.intValue(),this);
        }

        @Override
        public BetaBinomialDistribution.CDF getCDF()
        {
            return this;
        }

    }

    /**
     * Estimates the parameters of a Beta distribution using the matching
     * of moments, not maximum likelihood.
     */
    @PublicationReference(
        author={
            "Ram C. Tripathi",
            "Ramesh C. Gupta",
            "John Gurland"
        },
        title="Estimation of parameters in the beta binomial model",
        type=PublicationType.Journal,
        publication="Annals of the Institute of Statistical Mathematics",
        year=1994,
        pages={317,331},
        notes="Equation 2.11"
    )
    public static class MomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Number,BetaBinomialDistribution>
    {

        /**
         * Default constructor
         */
        public MomentMatchingEstimator()
        {
        }

        @Override
        public BetaBinomialDistribution learn(
            final Collection<? extends Number> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            double mean = pair.getFirst();
            double max = UnivariateStatisticsUtil.computeMaximum(data);
            int N = (int) Math.ceil( max );

            double eta = 0.0;
            double smooth = 1.0;
            for( Number value : data )
            {
                double numPositive = value.doubleValue() + smooth;
                double numNegative = N - value.doubleValue() + smooth;
                double e = numPositive / numNegative;
                eta += e;
            }
            eta /= N;

            double denom = N*mean - (N-mean)*eta;
            double alpha = Math.abs( ((N-1) * eta * mean) / denom );
            double beta = Math.abs( (N-1)*(N-mean)*eta / denom );

            BetaBinomialDistribution.PMF distribution =
                new BetaBinomialDistribution.PMF( N, alpha, beta );
            System.out.println( "Mean: " + distribution.getMean().doubleValue() + ", Variance: " + distribution.getVariance() );
                return distribution;
        }

        /**
         * Computes the Beta-Binomial distribution describes by the given moments
         * @param N
         * Number of trials
         * @param mean
         * Mean of the distribution
         * @param variance
         * Variance of the distribution
         * @return
         * Beta-Binomial distribution that has the same mean/variance as the
         * given parameters.
         */
        public static BetaBinomialDistribution.PMF learn(
            final int N,
            final double mean,
            final double variance )
        {

            double denom = N*((variance/mean) - mean - 1.0) + mean;
            double alpha = Math.abs((N*mean - variance) / denom);
            double beta = Math.abs( (N-mean)*(N-(variance/mean)) / denom );
            System.out.println( "N = " + N + ", alpha = " + alpha + ", beta = " + beta );
            BetaBinomialDistribution.PMF distribution =
                new BetaBinomialDistribution.PMF( N, alpha, beta );
            System.out.println( "Mean: " + distribution.getMean().doubleValue() + ", Variance: " + distribution.getVariance() );
            return new BetaBinomialDistribution.PMF( N, alpha, beta );
        }

    }

}
