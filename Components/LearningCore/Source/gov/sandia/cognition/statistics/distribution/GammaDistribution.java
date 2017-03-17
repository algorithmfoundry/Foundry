/*
 * File:                GammaDistribution.java
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
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Class representing the Gamma distribution.  This is a two-parameter family
 * of continuous distributions. The well-known exponential and chi-square
 * distributions are special cases of the Gamma distribution.  Please note that
 * we use the "shape" and "scale" parameters to describe the PDF/CDF, whereas
 * octave/MATLAB use "shape" and "1.0/scale" to parameterize a Gamma
 * distribution, so please beware when comparing results to octave/MATLAB.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Gamma distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Gamma_distribution"
)
public class GammaDistribution
    extends AbstractClosedFormSmoothUnivariateDistribution
    implements EstimableDistribution<Double,GammaDistribution>
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
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     */
    private double shape;

    /**
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    private double scale;

    /**
     * Default constructor.
     */
    public GammaDistribution()
    {
        this( DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of GammaDistribution
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    public GammaDistribution(
        final double shape,
        final double scale )
    {
        this.setShape( shape );
        this.setScale( scale );
    }

    /**
     * Copy constructor
     * @param other
     * GammaDistribution to copy
     */
    public GammaDistribution(
        final GammaDistribution other  )
    {
        this( other.getShape(), other.getScale() );
    }

    @Override
    public GammaDistribution clone()
    {
        return (GammaDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     */
    public void setShape(
        final double shape )
    {
        if (shape <= 0.0)
        {
            throw new IllegalArgumentException( "Shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    public void setScale(
        final double scale )
    {
        ArgumentChecker.assertIsPositive("scale", scale);
        this.scale = scale;
    }
    
    /**
     * Gets the rate parameter, which is just the inverse of the scale parameter.
     * It is commonly referred to as beta.
     * 
     * @return 
     *      The rate parameter (1.0 / scale). Must be greater than 0.0.
     */
    public double getRate()
    {
        return 1.0 / this.getScale();
    }
    
    /**
     * Sets the rate parameter, which is just the inverse of the scale parameter.
     * It is commonly referred to as beta.
     * 
     * @param   rate 
     *      The rate parameter (1.0 / scale). Must be greater than 0.0.
     */
    public void setRate(
        final double rate)
    {
        ArgumentChecker.assertIsPositive("rate", rate);
        this.setScale(1.0 / rate);
    }

    @Override
    public double getMeanAsDouble()
    {
        return this.getShape() * this.getScale();
    }

    @Override
    public double getVariance()
    {
        return this.getShape() * this.getScale() * this.getScale();
    }

    /**
     * Gets the parameters of the distribution
     * @return
     * 2-dimensional Vector with (shape scale)
     */
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getShape(), this.getScale() );
    }

    /**
     * Sets the parameters of the distribution
     * @param parameters
     * 2-dimensional Vector with (shape scale)
     */
    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if (parameters.getDimensionality() != 2)
        {
            throw new IllegalArgumentException(
                "Expected a 2-dimensional Vector!" );
        }

        this.setShape( parameters.getElement( 0 ) );
        this.setScale( parameters.getElement( 1 ) );

    }

    /**
     * Efficiently samples from a Gamma distribution given by the
     * shape and scale parameters.
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to generate
     * @return
     * Samples simulated from the Gamma distribution.
     */
    public static ArrayList<Double> sample(
        final double shape,
        final double scale,
        final Random random,
        final int numSamples )
    {
        final double[] values = sampleAsDoubles(shape, scale, random, numSamples);
        final ArrayList<Double> result = new ArrayList<Double>(numSamples);
        for (final double value : values)
        {
            result.add(value);
        }
        return result;
    }
    
    /**
     * Efficiently samples from a Gamma distribution given by the
     * shape and scale parameters.
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     * @param random
     * Random number generator
     * @return
     * Samples simulated from the Gamma distribution.
     */
    public static double sampleAsDouble(
        final double shape,
        final double scale,
        final Random random)
    {
        return sample(shape, scale, random);
    }
    
    /**
     * Efficiently samples from a Gamma distribution given by the
     * shape and scale parameters.
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to generate
     * @return
     * Samples simulated from the Gamma distribution.
     */
    public static double[] sampleAsDoubles(
        final double shape,
        final double scale,
        final Random random,
        final int numSamples)
    {
        final double[] result = new double[numSamples];
        sampleInto(shape, scale, random, result, 0, numSamples);
        return result;
    }

    /**
     * Efficiently samples from a Gamma distribution given by the
     * shape and scale parameters.
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     * @param random
     * Random number generator
     * @param output Array to write samples from Gamma distribution into.
     * @param start Starting point in output array to add samples.
     * @param length Number of samples to generate
     */
    public static void sampleInto(
        final double shape,
        final double scale,
        final Random random,
        final double[] output,
        final int start,
        final int length)
    {
        final int end = start + length;
        for (int i = start; i < end; i++)
        {
            output[i] = sample(shape, scale, random);
        }
    }
    
    /**
     * Provides a single sample from a Gamma distribution with the given shape
     * and scale.
     * 
     * @param shape
     *      Shape parameter of the Gamma distribution, often written as "k",
     *      must be greater than zero.
     * @param scale
     *      Scale parameters of the Gamma distribution, often written as "theta",
     *      must be greater than zero.
     * @param random
     *      Random number generator to use.
     * @return 
     *      A value sampled from a Gamma distribution.
     */
    public static double sample(
        final double shape,
        final double scale,
        final Random random)
    {
        // Shape is checked in the next function.
        ArgumentChecker.assertIsPositive("scale", scale);
        return scale * sampleStandard(shape, random);
    }
    
    /**
     * Provides a single sample from a Gamma distribution with the given shape
     * and a scale of 1.
     * 
     * @param shape
     *      Shape parameter of the Gamma distribution, often written as "k",
     *      must be greater than zero.
     * @param random
     *      Random number generator to use.
     * @return 
     *      A value sampled from a Gamma distribution.
     */
    public static double sampleStandard(
        final double shape,
        final Random random)
    {
        ArgumentChecker.assertIsPositive("shape", shape);
        
        // This is based on the gamma distribution algorithm used in numpy.
        if (shape == 1.0)
        {
            // Sample standard exponential:
            return -Math.log(random.nextDouble());
        }
        else if (shape < 1.0)
        {
            while (true)
            {
                final double u = random.nextDouble();
                final double v = -Math.log(random.nextDouble());
                if (u <= 1.0 - shape)
                {
                    final double x = Math.pow(u, 1.0 / shape);
                    if (x <= v)
                    {
                        return x;
                    }
                }
                else
                {
                    final double y = -Math.log((1.0 - u) / shape);
                    final double x = Math.pow(1.0 - shape + shape * y, 1.0 / shape);
                    if (x <= (v + y))
                    {
                        return x;
                    }
                }
            }
        }
        else
        {
            // Marsaglia's method.
            final double b = shape - 1.0 / 3.0;
            final double c = 1.0 / Math.sqrt(9.0 * b);
            while (true)
            {
                double x = 0.0;
                double v = 0.0;
                do
                {
                    x = random.nextGaussian();
                    v = 1.0 + c * x;
                }
                while (v <= 0.0);

                v = v * v * v;
                final double xx = x * x;
                final double u = random.nextDouble();
                if (u < (1.0 - 0.0331 * xx * xx)
                    || Math.log(u) < ((0.5 * xx) + (b * (1.0 - v + Math.log(v)))))
                {
                    return b * v;
                }
            }
        }
    }

    @Override
    public double sampleAsDouble(
        final Random random)
    {
        return sample(this.getShape(), this.getScale(), random);
    }
    
    @Override
    public void sampleInto(
        final Random random,
        final double[] output,
        final int start,
        final int length)
    {
        sampleInto(this.getShape(), this.getScale(), random, 
            output, start, length);
    }

    @Override
    public GammaDistribution.CDF getCDF()
    {
        return new GammaDistribution.CDF( this );
    }

    @Override
    public GammaDistribution.PDF getProbabilityFunction()
    {
        return new GammaDistribution.PDF( this );
    }

    @Override
    public String toString()
    {
        return "Shape = " + this.getShape() + ", Scale = " + this.getScale();
    }

    @Override
    public Double getMinSupport()
    {
        return 0.0;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public GammaDistribution.MomentMatchingEstimator getEstimator()
    {
        return new GammaDistribution.MomentMatchingEstimator();
    }

    /**
     * Closed-form PDF of the Gamma distribution
     */
    public static class PDF
        extends GammaDistribution
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
         * Creates a new instance of PDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         */
        public PDF(
            final double shape,
            final double scale )
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * GammaDistribution to copy
         */
        public PDF(
            final GammaDistribution other  )
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
        public double evaluateAsDouble(
            final Double input)
        {
            return this.evaluate(input.doubleValue());
        }
        
        @Override
        public double evaluate(
            final double input )
        {
            return evaluate( input, this.getShape(), this.getScale() );
        }

        /**
         * Evaluates the Gamma PDF about the input "x", using the given
         * shape and scale
         * @param x
         * Input to the PDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         * @return
         * p(x;shape,scale)
         */
        public static double evaluate(
            final double x,
            final double shape,
            final double scale )
        {
            
            double p;
            if (x > 0.0)
            {
                p =  Math.exp( logEvaluate(x, shape, scale) );
            }
            else
            {
                p = 0.0;
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
            return logEvaluate( input, this.getShape(), this.getScale() );
        }

        /**
         * Evaluates the natural logarithm of the PDF.
         * @param input
         * Input to consider.
         * @param shape
         * Shape factor.
         * @param scale
         * Scale factor.
         * @return
         * Natural logarithm of the PDF.
         */
        public static double logEvaluate(
            final double input,
            final double shape,
            final double scale )
        {

            if( input <= 0.0 )
            {
                return Math.log(0.0);
            }
            else
            {
                final double n1 = (shape-1.0) * Math.log(input);
                final double n2 = -input / scale;
                final double d1 = MathUtil.logGammaFunction(shape);
                final double d2 = shape * Math.log(scale);
                return n1 + n2 - d1 - d2;
            }

        }

        @Override
        public GammaDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * CDF of the Gamma distribution
     */
    public static class CDF
        extends GammaDistribution
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
         * Creates a new instance of CDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         */
        public CDF(
            final double shape,
            final double scale )
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * GammaDistribution to copy
         */
        public CDF(
            final GammaDistribution other  )
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
        public double evaluateAsDouble(
            final Double input)
        {
            return this.evaluate(input.doubleValue());
        }
        
        @Override
        public double evaluate(
            final double input )
        {
            return evaluate( input, this.getShape(), this.getScale() );
        }

        /**
         * Evaluates the CDF of the Gamma distribution about x, given 
         * the shape and scale parameters
         * @param x
         * Input to the CDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         * @return
         * Pr(y le x;shape,scale)
         */
        public static double evaluate(
            final double x,
            final double shape,
            final double scale )
        {
            double p;
            if (x <= 0.0)
            {
                p = 0.0;
            }
            else
            {
                p = MathUtil.lowerIncompleteGammaFunction( shape, x / scale );
            }
            return p;
        }

        @Override
        public GammaDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public GammaDistribution.PDF getDerivative()
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

    /**
     * Computes the parameters of a Gamma distribution by the
     * Method of Moments
     */
    public static class MomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,GammaDistribution>
    {

        /**
         * Default constructor
         */
        public MomentMatchingEstimator()
        {
        }

        @Override
        public GammaDistribution learn(
            final Collection<? extends Double> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            return learn( pair.getFirst(), pair.getSecond() );
        }

        /**
         * Computes the Gamma distribution describes by the given moments
         * @param mean
         * Mean of the distribution
         * @param variance
         * Variance of the distribution
         * @return
         * Gamma distribution that has the same mean/variance as the
         * given parameters.
         */
        public static GammaDistribution learn(
            final double mean,
            final double variance )
        {
            double scale = variance / mean;
            double shape = mean*mean / variance;
            return new GammaDistribution(shape, scale);
        }

    }

    /**
     * Estimates the parameters of a Gamma distribution using the matching
     * of moments, not maximum likelihood.
     */
    public static class WeightedMomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,GammaDistribution>
    {

        /**
         * Default constructor
         */
        public WeightedMomentMatchingEstimator()
        {
        }

        @Override
        public GammaDistribution learn(
            final Collection<? extends WeightedValue<? extends Double>> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
            return MomentMatchingEstimator.learn(
                pair.getFirst(), pair.getSecond());
        }

    }

}
