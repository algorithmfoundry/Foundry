/*
 * File:                UnivariateGaussian.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.AbstractIncrementalEstimator;
import gov.sandia.cognition.statistics.AbstractSufficientStatistic;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * This class contains internal classes that implement useful functions based
 * on the Gaussian distribution.  Use this class if the underlying distribution
 * has a univariate (scalar) Random Variable.  If your distribution is Vector
 * based, then use MultivariateGaussian.  However, MultivariateGaussian is
 * a MUCH more computationally intensive class.
 *
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Normal distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Normal_distribution"
)
public class UnivariateGaussian 
    extends AbstractClosedFormSmoothUnivariateDistribution
    implements EstimableDistribution<Double,UnivariateGaussian>
{

    /**
     * Default mean, {@value}.
     */
    public static final double DEFAULT_MEAN = 0.0;

    /**
     * Default variance, {@value}.
     */
    public static final double DEFAULT_VARIANCE = 1.0;

    /**
     * First central moment (expectation) of the distribution
     */
    protected double mean;

    /**
     * Second central moment (square of standard deviation) of the distribution
     */
    protected double variance;
    
    /**
     * A big value to input into the Gaussian CDF that will get 1.0
     * probability, {@value}.
     */
    public final static double BIG_Z = 100.0;

    /**
     * Square root of 2.0, 0.707...
     */
    public static final double SQRT2 = Math.sqrt( 2.0 );
    
    /**
     * PI times 2.0, {@value}
     */
    public static final double PI2 = Math.PI * 2.0;
    
    /** 
     * Creates a new instance of UnivariateGaussian
     * with zero mean and unit variance
     */
    public UnivariateGaussian()
    {
        this( DEFAULT_MEAN, DEFAULT_VARIANCE );
    }

    /**
     * Creates a new instance of UnivariateGaussian
     *
     * @param mean
     * First central moment (expectation) of the distribution
     * @param variance
     * Second central moment (square of standard deviation) of the distribution
     */
    public UnivariateGaussian(
        final double mean,
        final double variance )
    {
        this.setMean( mean );
        this.setVariance( variance );
    }

    /**
     * Copy constructor
     * @param other UnivariateGaussian to copy
     */
    public UnivariateGaussian(
        final UnivariateGaussian other )
    {
        this( other.getMean(), other.getVariance() );
    }

    @Override
    public UnivariateGaussian clone()
    {
        return (UnivariateGaussian) super.clone();
    }
    
    /**
     * Getter for mean
     * @return
     * First central moment (expectation) of the distribution
     */
    @Override
    public Double getMean()
    {
        return this.mean;
    }

    /**
     * Setter for mean
     * @param mean
     * First central moment (expectation) of the distribution
     */
    public void setMean(
        final double mean )
    {
        this.mean = mean;
    }

    @Override
    public double getVariance()
    {
        return this.variance;
    }

    /**
     * Setter for variance
     * @param variance
     * Second central moment (square of standard deviation) of the distribution
     */
    public void setVariance(
        final double variance )
    {
        ArgumentChecker.assertIsPositive("variance", variance);
        this.variance = variance;
    }

    /**
     * Returns the string representation of the object.
     * @return
     * String representation of the object.
     */
    @Override
    public String toString()
    {
        return "Mean: " + this.getMean() + " Variance: " + this.getVariance();
    }    
    
    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        double stddev = Math.sqrt( this.getVariance() );
        for( int n = 0; n < numSamples; n++ )
        {
            // NOTE: It is about twice as fast to use random.nextGaussian()
            // than to compute a compute the method properly, using:
            // UnivariateGaussian.CDF.Inverse.evaluate( random.nextDouble(), stddev )
            // on my test battery.
            //      -- krdixon, 2009-01-23
            double x = random.nextGaussian();
            samples.add( x*stddev + this.mean );
        }
        
        return samples;
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getMean(), this.getVariance() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if( parameters.getDimensionality() != 2 )
        {
            throw new IllegalArgumentException(
                "Must have parameters of dimension 2" );
        }
        this.setMean( parameters.getElement( 0 ) );
        this.setVariance( parameters.getElement( 1 ) );
    }

    @Override
    public UnivariateGaussian.CDF getCDF()
    {
        return new UnivariateGaussian.CDF( this );
    }

    @Override
    public UnivariateGaussian.PDF getProbabilityFunction()
    {
        return new UnivariateGaussian.PDF( this );
    }

    @Override
    public Double getMinSupport()
    {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Multiplies this Gaussian with the other Gaussian.  This is also
     * equivalent to computing the posterior belief using one of the Gaussians
     * as the prior and one as the conditional likelihood.
     * @param other
     * Other Gaussian to multiply with this.
     * @return
     * Multiplied Gaussians.
     */
    public UnivariateGaussian times(
        final UnivariateGaussian other )
    {

        double m1 = this.getMean();
        double v1 = this.getVariance();

        double m2 = other.getMean();
        double v2 = other.getVariance();

        double vhat = 1.0 / (1.0/v1 + 1.0/v2);
        double meanHat = (vhat/v1)*m1 + (vhat/v2)*m2;

        return new UnivariateGaussian( meanHat, vhat );

    }

    /**
     * Convolves this Gaussian with the other Gaussian.
     * @param other
     * Other Gaussian to convolve with this.
     * @return
     * Convolved Gaussians.
     */
    public UnivariateGaussian convolve(
        final UnivariateGaussian other )
    {
        double meanHat = this.getMean() + other.getMean();
        double varianceHat = this.getVariance() + other.getVariance();
        return new UnivariateGaussian( meanHat, varianceHat );
    }

    @Override
    public UnivariateGaussian.MaximumLikelihoodEstimator getEstimator()
    {
        return new UnivariateGaussian.MaximumLikelihoodEstimator();
    }

    /**
     * Gaussian Error Function, useful for computing the cumulative distribution
     * function for a Gaussian.  This implementation uses Horner's method of
     * approximation.
     */
    @PublicationReference(
        title="ErrorFunction.java",
        author={"Robert Sedgewick", "Kevin Wayne"},
        type=PublicationType.WebPage,
        year=2007,
        url="http://www.cs.princeton.edu/introcs/21function/ErrorFunction.java.html"
    )
    public static class ErrorFunction
        extends AbstractUnivariateScalarFunction
    {

        /**
         * Default instance.
         */
        public static final ErrorFunction INSTANCE = new ErrorFunction();

        /**
         * Default constructor.
         */
        public ErrorFunction()
        {
            super();
        }

        /**
         * Computes the Gaussian Error Function using Horner's Method.
         * Guaranteed to be accurate within 1.2E-7, but can suffer from
         * catastrophic cancellation when z is close to 0.0
         * @param z value on the interval (-infinity,infinity)
         * @return Normalized Gaussian Error Function value of z
         */
        @Override
        public double evaluate(
            final double z )
        {

            double t = 1.0 / (1.0 + 0.5 * Math.abs( z ));

            // use Horner's method
            double ans = 1 - t * Math.exp( -z * z - 1.26551223 +
                t * (1.00002368 +
                t * (0.37409196 +
                t * (0.09678418 +
                t * (-0.18628806 +
                t * (0.27886807 +
                t * (-1.13520398 +
                t * (1.48851587 +
                t * (-0.82215223 +
                t * (0.17087277))))))))) );

            if (z < 0)
            {
                ans = -ans;
            }

            return ans;
        }

        /**
         * Inverse of the ErrorFunction
         */
        public static class Inverse
            extends AbstractUnivariateScalarFunction
        {

            /**
             * Default instance.
             */
            public static final Inverse INSTANCE = new Inverse();
            
            /**
             * Default constructor
             */
            public Inverse()
            {
                super();
            }

            /**
             * Inverse of the error function.
             * x = erfinv(y) satisfies y = erf(x), y is [-1,1) and x is
             * any double.
             *
             * @param y
             * Computes the value of the error function inverse such that
             * y = erf(x)
             * @return
             * Returns the value "x" such that y = erf(x)
             */
            @Override
            public double evaluate(
                final double y )
            {

                // -1 < y < 1
                double x;
                if (Math.abs( y ) < 1)
                {
                    // -0.7 <= y <= 0.7
                    final double y0 = 0.7;
                    if (Math.abs( y ) <= y0)
                    {
                        final double[] a = {0.886226899, -1.645349621, 0.914624893, -0.140543331};
                        final double[] b = {-2.118377725, 1.442710462, -0.329097515, 0.012229801};
                        double z = y * y;
                        x = y * (((a[3] * z + a[2]) * z + a[1]) * z + a[0]) /
                            ((((b[3] * z + b[2]) * z + b[1]) * z + b[0]) * z + 1);
                    }
                    else
                    {
                        final double[] c = {-1.970840454, -1.624906493, 3.429567803, 1.641345311};
                        final double[] d = {3.543889200, 1.637067800};

                        // 0.7 < y < 1
                        if (y0 < y)
                        {
                            double z = Math.sqrt( -Math.log( (1 - y) / 2 ) );
                            x = (((c[3] * z + c[2]) * z + c[1]) * z + c[0]) /
                                ((d[1] * z + d[0]) * z + 1);
                        }
                        // -1 < y < -0.7
                        else
                        {
                            double z = Math.sqrt( -Math.log( (1 + y) / 2 ) );
                            x = -(((c[3] * z + c[2]) * z + c[1]) * z + c[0]) /
                                ((d[1] * z + d[0]) * z + 1);
                        }
                    }

                    // Two steps of Newton-Raphson correction to full accuracy.
                    // Without these steps, erfinv(y) would be about 3 times
                    // faster to compute, but accurate to only about 6 digits.
                    final double COEF = 2.0 / Math.sqrt( Math.PI );
                    for (int i = 0; i < 2; i++)
                    {
                        double erfx = UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( x );
                        x = x - (erfx - y) / (COEF * Math.exp( -x * x ));
                    }
                }
                // y <= -1 || y >= 1
                else
                {
                    x = (y >= 1) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                }

                return x;

            }

        }

    }
    
    /**
     * CDF of the underlying Gaussian.
     */
    public static class CDF
        extends UnivariateGaussian
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
    {
        
        /** 
         * Creates a new instance of UnivariateGaussian
         * with zero mean and unit variance
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of UnivariateGaussian
         *
         * @param mean
         * First central moment (expectation) of the distribution
         * @param variance
         * Second central moment (square of standard deviation) of the distribution
         */
        public CDF(
            final double mean,
            final double variance )
        {
            super( mean, variance );
        }

        /**
         * Copy constructor
         * @param other UnivariateGaussian to copy
         */
        public CDF(
            final UnivariateGaussian other )
        {
            super( other.getMean(), other.getVariance() );
        }
    
        @Override
        public Double evaluate(
            final Double input )
        {
            return evaluate( input.doubleValue() );
        }
        
        @Override
        public double evaluate(
            final double z )
        {
            return evaluate( z, this.mean, this.variance );
        }
                
        /**
         * Computes the cumulative distribution of a Normalized Gaussian
         * distribution using the errorFunction method.
         * cdf(0) = 0.5, cdf(-infinity) = 0, cdf(infinity) = 1
         *
         * @return integral( -infinity, z, gaussian(0,1) ) will be [0,1]
         * @param mean
         * Mean of the PDF
         * @param variance
         * Variance of the PDF
         * @param z value to compute the Gaussian cdf at
         */
        public static double evaluate(
            final double z,
            final double mean,
            final double variance )
        {

            double retval;
            double zstar = (z - mean) / Math.sqrt( variance );

            if (zstar == 0.0)
            {
                retval = 0.5;
            }
            else
            {
                retval = 0.5 * (1.0 + UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( zstar / SQRT2 ));
            }
            
            // Sometimes we run into numerical round-off problems,
            // so let's just fix them.
            if( retval < 0.0 )
            {
                retval = 0.0;
            }
            else if( retval > 1.0 )
            {
                retval = 1.0;
            }
            
            return retval;
            
        }

        @Override
        public UnivariateGaussian.CDF getCDF()
        {
            return this;
        }

        @Override
        public UnivariateGaussian.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getDerivative().evaluate(input);
        }

        @Override
        public Double inverse(
            final double probability)
        {
            return Inverse.evaluate(probability, this.mean, this.variance);
        }

        /**
         * Inverts the CumulativeDistribution function.  That is, we find the
         * value of z such that
         * p = Pr( x lessThan z ),
         * where x is drawn from a normalized Gaussian.  Uses a closed-form
         * computation.
         */
        public static class Inverse
            extends UnivariateGaussian
            implements UnivariateScalarFunction
        {

            /** 
             * Creates a new instance of UnivariateGaussian
             * with zero mean and unit variance
             */
            public Inverse()
            {
                super();
            }

            /**
             * Creates a new instance of UnivariateGaussian
             *
             * @param mean
             * First central moment (expectation) of the distribution
             * @param variance
             * Second central moment (square of standard deviation) of the distribution
             */
            public Inverse(
                final double mean,
                final double variance )
            {
                super( mean, variance );
            }

            /**
             * Copy constructor
             * @param other UnivariateGaussian to copy
             */
            public Inverse(
                final UnivariateGaussian other )
            {
                super( other.getMean(), other.getVariance() );
            }
            
            @Override
            public Double evaluate(
                final Double input )
            {
                return this.evaluate( input.doubleValue() );
            }
            
            /**
             * Evaluates the Inverse UnivariateGaussian CDF for the given
             * probability.  If you are using this method many times in a
             * row, then use the two-argument method by caching the
             * standard deviation.
             *
             * @param p Value at which to solve for x such that x=CDF(p)
             * @return Value of x such that x=CDF(p)
             */
            @Override
            public double evaluate(
                final double p )
            {
                return evaluate( p, this.mean, this.variance );
            }
            
            /**
             * Evaluates the Inverse UnivariateGaussian CDF for the given
             * probability.  This is faster than computing the single-argument
             * evaluate() method.
             *
             * @param p Value at which to solve for x such that x=CDF(p)
             * @param mean Mean of the distribution
             * @param variance Variance of the distribution.
             * @return Value of x such that x=CDF(p)
             */
            public static double evaluate(
                final double p,
                final double mean,
                final double variance )
            {
                double stddev = Math.sqrt( variance );
                if( p <= 0.0 )
                {
                    return Double.NEGATIVE_INFINITY;
                }
                else if( p >= 1.0 )
                {
                    return Double.POSITIVE_INFINITY;
                }
                else
                {
                    double stdinv = SQRT2 * UnivariateGaussian.ErrorFunction.Inverse.INSTANCE.evaluate( 2 * p - 1 );
                    return mean + stddev * stdinv;
                }
                
            }

        }

    }
    
    /**
     * PDF of the underlying Gaussian.
     */
    public static class PDF
        extends UnivariateGaussian
        implements UnivariateProbabilityDensityFunction
    {

        /** 
         * Creates a new instance of UnivariateGaussian
         * with zero mean and unit variance
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of UnivariateGaussian
         *
         * @param mean
         * First central moment (expectation) of the distribution
         * @param variance
         * Second central moment (square of standard deviation) of the distribution
         */
        public PDF(
            final double mean,
            final double variance )
        {
            super( mean, variance );
        }

        /**
         * Copy constructor
         * @param other UnivariateGaussian to copy
         */
        public PDF(
            final UnivariateGaussian other )
        {
            super( other.getMean(), other.getVariance() );
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
            return evaluate( input, this.getMean(), this.getVariance() );
        }
        
        /**
         * Computes the value of the Probability Density Function at the input
         * @param input
         * Input value to compute the PDF at, that is, p(input|mean,variance)
         * @param mean 
         * Mean of the distribution
         * @param variance 
         * Variance of the distribution
         * @return Value of the PDF at the input, p(input|mean,variance)
         */
        public static double evaluate(
            final double input,
            final double mean,
            final double variance )
        {
            return Math.exp( logEvaluate(input, mean, variance) );
        }

        @Override
        public double logEvaluate(
            final Double input)
        {
            return logEvaluate( input, this.mean, this.variance );
        }

        @Override
        public double logEvaluate(
            final double input)
        {
            return logEvaluate(input, this.mean, this.variance);
        }

        /**
         * Computes the natural logarithm of the pdf.
         * @param input
         * Input to consider.
         * @param mean
         * Mean of the Gaussian.
         * @param variance
         * Variance of the Gaussian.
         * @return
         * Natural logarithm of the pdf.
         */
        public static double logEvaluate(
            final double input,
            final double mean,
            final double variance )
        {
            final double delta = input - mean;
            final double exponent = delta*delta / (-2.0*variance);
            final double coefficient = -0.5 * Math.log(PI2 * variance);
            return exponent + coefficient;
        }

        @Override
        public UnivariateGaussian.PDF getProbabilityFunction()
        {
            return this;
        }
        
    }
    
    /**
     * Creates a UnivariateGaussian from data
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,UnivariateGaussian.PDF>
    {

        /**
         * Amount to add to the variance to keep it from being 0.0
         */
        private double defaultVariance;

        /**
         * Typical value of a defaultVariance, {@value}
         */
        public static final double DEFAULT_VARIANCE = 1e-5;

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
            this( DEFAULT_VARIANCE );
        }

        /**
         * Creates a new instance of MaximumLikelihoodEstimator
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public MaximumLikelihoodEstimator(
            final double defaultVariance )
        {
            super();

            this.setDefaultVariance(defaultVariance);
        }

        /**
         * Creates a new instance of UnivariateGaussian from the given data
         * @param data
         * Data to fit a UnivariateGaussian against
         * @return
         * Maximum likelihood estimate of the UnivariateGaussian that generated
         * the data
         */
        @Override
        public UnivariateGaussian.PDF learn(
            final Collection<? extends Double> data )
        {
            return MaximumLikelihoodEstimator.learn( data, this.defaultVariance );
        }

        /**
         * Creates a new instance of UnivariateGaussian from the given data
         * @param data
         * Data to fit a UnivariateGaussian against
         * @return
         * Maximum likelihood estimate of the UnivariateGaussian that generated
         * the data
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public static UnivariateGaussian.PDF learn(
            final Collection<? extends Number> data,
            final double defaultVariance )
        {
            Pair<Double,Double> mle =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            double mean = mle.getFirst();
            double variance = mle.getSecond();
            variance += defaultVariance;
            return new UnivariateGaussian.PDF( mean, variance );
        }

        /**
         * Gets the default variance, which is the amount added to the variance.
         * If this is greater than zero, it avoids creating zero variance.
         *
         * @return
         *      The default variance. Cannot be negative.
         */
        public double getDefaultVariance()
        {
            return this.defaultVariance;
        }

        /**
         * Sets the default variance, which is the amount added to the variance.
         * If this is greater than zero, it avoids creating zero variance.
         *
         * @param   defaultVariance
         *      The default variance. Cannot be negative.
         */
        public void setDefaultVariance(
            final double defaultVariance)
        {
            ArgumentChecker.assertIsNonNegative(
                "defaultVariance", defaultVariance);
            this.defaultVariance = defaultVariance;
        }

    }

    /**
     * Creates a UnivariateGaussian from weighted data
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,UnivariateGaussian.PDF>
    {

        /**
         * Amount to add to the variance to keep it from being 0.0
         */
        private double defaultVariance;

        /**
         * Default constructor
         */
        public WeightedMaximumLikelihoodEstimator()
        {
            this( MaximumLikelihoodEstimator.DEFAULT_VARIANCE );
        }

        /**
         * Creates a new instance of WeightedMaximumLikelihoodEstimator
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public WeightedMaximumLikelihoodEstimator(
            final double defaultVariance )
        {
            this.defaultVariance = defaultVariance;
        }

        /**
         * Creates a new instance of UnivariateGaussian using a weighted
         * Maximum Likelihood estimate based on the given data
         * @param data
         * Weighed pairs of data (first is data, second is weight) that was
         * generated by some unknown UnivariateGaussian distribution
         * @return
         * Maximum Likelihood UnivariateGaussian that generated the data
         */
        @Override
        public UnivariateGaussian.PDF learn(
            final Collection<? extends WeightedValue<? extends Double>> data )
        {
            return WeightedMaximumLikelihoodEstimator.learn(
                data, this.defaultVariance );
        }

        /**
         * Creates a new instance of UnivariateGaussian using a weighted
         * Maximum Likelihood estimate based on the given data
         * @param data
         * Weighed pairs of data (first is data, second is weight) that was
         * generated by some unknown UnivariateGaussian distribution
         * @return
         * Maximum Likelihood UnivariateGaussian that generated the data
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public static UnivariateGaussian.PDF learn(
            final Collection<? extends WeightedValue<? extends Number>> data,
            final double defaultVariance )
        {
            Pair<Double,Double> mle =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
            double mean = mle.getFirst();
            double variance = mle.getSecond();
            variance += defaultVariance;
            return new UnivariateGaussian.PDF( mean, variance );
        }

    }    


    /**
     * Captures the sufficient statistics of a UnivariateGaussian, which are
     * the values to estimate the mean and variance.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Algorithms for calculating variance",
        year=2011,
        type=PublicationType.WebPage,
        url="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance"
    )
    public static class SufficientStatistic
        extends AbstractSufficientStatistic<Double, UnivariateGaussian>
    {

        /** The mean of the Gaussian. */
        protected double mean;

        /** This is the sum-squared differences */
        protected double sumSquaredDifferences;

        /**
         * Creates a new, empty {@code SufficientStatistic}.
         */
        public SufficientStatistic()
        {
            this( 0.0 );
        }

        /**
         * Creates a new {@code SufficientStatistic} with the given value
         * to initialize the variance. This is the initial value for the
         * sum of squared differences. As the number of elements becomes
         * larger, the impact of the default variance will decrease.
         *
         * @param   defaultVariance
         *      The default variance to use. Must be greater than or equal
         *      to zero.
         */
        public SufficientStatistic(
            final double defaultVariance)
        {
            super();

            ArgumentChecker.assertIsNonNegative(
                "defaultVariance", defaultVariance);

            this.clear();
            this.sumSquaredDifferences = defaultVariance;
        }

        @Override
        public SufficientStatistic clone()
        {
            return (SufficientStatistic) super.clone();
        }

        /**
         * Resets this set of sufficient statistics to its empty state.
         */
        public void clear()
        {
            this.count = 0;
            this.mean = 0.0;
            this.sumSquaredDifferences = 0.0;
        }

        @Override
        public UnivariateGaussian.PDF create()
        {
            final UnivariateGaussian.PDF result = new UnivariateGaussian.PDF();
            this.create(result);
            return result;
        }
        
        @Override
        public void create(
            final UnivariateGaussian distribution)
        {
            distribution.setMean(this.getMean());
            distribution.setVariance(this.getVariance());
        }

        @Override
        public void update(
            final Double value)
        {
            this.update(value.doubleValue());
        }
        
        /**
         * Adds a value to the sufficient statistics for the Gaussian.
         *
         * @param   value
         *      The value to add.
         */
        public void update(
            final double value)
        {
            // We've added another value.
            this.count++;

            // Compute the difference between the value and the current mean.
            final double delta = value - this.mean;

            // Update the mean based on the difference between the value
            // and the mean along with the new count.
            this.mean += delta / this.count;

            // Update the squared differences from the mean, using the new
            // mean in the process.
            this.sumSquaredDifferences += delta * (value - this.mean);
        }

        
//
// TODO: This is not an unreasonable API, but I REALLY do not like the use
//        of "plus" and "plusEquals"... -- krdixon, 2011-03-15
//
        /**
         * Adds this set of sufficient statistics to another and returns the
         * combined sufficient statistics.
         *
         * @param   other
         *      The other set of sufficient statistics.
         * @return
         *      A combined set of sufficient statistics. The result is the
         *      same as if all of the elements added to this and other were
         *      added to one sufficient statistic.
         */
        public SufficientStatistic plus(
            final SufficientStatistic other)
        {
            final SufficientStatistic copy = this.clone();
            copy.plusEquals(other);
            return copy;
        }

        /**
         * Adds another sufficient statistic to this one. Makes this one
         * as if all the items added to the other sufficient statistics were
         * added to this one.
         *
         * @param   other
         *      The other set of sufficient statistics.
         */
        public void plusEquals(
            final SufficientStatistic other)
        {
            final double delta = other.mean - this.mean;
            final long newCount = this.count + other.count;
            final double newMean = this.mean + delta * other.count / newCount;
            final double newSumSquaredDifferences =
                this.sumSquaredDifferences + other.sumSquaredDifferences
                + delta * delta * this.count * other.count / newCount;

            this.count = newCount;
            this.sumSquaredDifferences = newSumSquaredDifferences;
            this.mean = newMean;
        }

        /**
         * Gets the mean of the Gaussian.
         *
         * @return
         *      The mean.
         */
        public double getMean()
        {
            return this.mean;
        }

        /**
         * Gets the variance of the Gaussian.
         *
         * @return
         *      The variance.
         */
        public double getVariance()
        {
            if (this.count <= 1)
            {
                // This allows the default variance to be used.
                return this.sumSquaredDifferences;
            }
            else
            {
                return this.sumSquaredDifferences / (this.count - 1);
            }
        }

        /**
         * Gets the sum of squared differences from the mean. Used to compute
         * the variance.
         *
         * @return
         *      The sum of squared differences from the mean.
         */
        public double getSumSquaredDifferences()
        {
            return this.sumSquaredDifferences;
        }

    }

    /**
     * Implements an incremental estimator for the sufficient statistics for
     * a UnivariateGaussian.
     */
    public static class IncrementalEstimator
        extends AbstractIncrementalEstimator<Double, UnivariateGaussian, UnivariateGaussian.SufficientStatistic>
    {

        /** The default value for the default variance is {@value}. */
        public static final double DEFAULT_DEFAULT_VARIANCE = MaximumLikelihoodEstimator.DEFAULT_VARIANCE;

        /** Amount to add to the variance to keep it from being 0.0. */
        protected double defaultVariance;

        /**
         * Creates a new {@code IncrementalEstimator}.
         */
        public IncrementalEstimator()
        {
            this(DEFAULT_DEFAULT_VARIANCE);
        }

        /**
         * Creates a new {@code IncrementalEstimator} with the given default
         * variance.
         *
         * @param   defaultVariance
         *      The default variance. Cannot be negative.
         */
        public IncrementalEstimator(
            final double defaultVariance)
        {
            super();

            this.setDefaultVariance(defaultVariance);
        }
        
        @Override
        public UnivariateGaussian.SufficientStatistic createInitialLearnedObject()
        {
            return new UnivariateGaussian.SufficientStatistic(
                this.getDefaultVariance());
        }

        /**
         * Gets the default variance, which is the amount added to the variance.
         * If this is greater than zero, it avoids creating zero variance.
         *
         * @return
         *      The default variance. Cannot be negative.
         */
        public double getDefaultVariance()
        {
            return this.defaultVariance;
        }

        /**
         * Sets the default variance, which is the amount added to the variance.
         * If this is greater than zero, it avoids creating zero variance.
         *
         * @param   defaultVariance
         *      The default variance. Cannot be negative.
         */
        public void setDefaultVariance(
            final double defaultVariance)
        {
            ArgumentChecker.assertIsNonNegative(
                "defaultVariance", defaultVariance);
            this.defaultVariance = defaultVariance;
        }
    }

}
