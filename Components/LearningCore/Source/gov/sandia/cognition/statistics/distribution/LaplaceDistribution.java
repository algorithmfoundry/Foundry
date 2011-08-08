/*
 * File:                LaplaceDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.NumberAverager;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothScalarDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * A Laplace distribution, sometimes called a double exponential distribution.
 * This distribution arrises when evaluating the difference between two iid
 * exponential random variables, or when sampling Brownian motion at
 * exponentially distributed time steps.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Laplace distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Laplace_distribution"
)
public class LaplaceDistribution 
    extends AbstractClosedFormSmoothScalarDistribution
    implements EstimableDistribution<Double,LaplaceDistribution>
{

    /**
     * Default mean, {@value}.
     */
    public static final double DEFAULT_MEAN = 0.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Mean of the distribution
     */
    protected double mean;

    /**
     * Scale factor of the distribution, must be greater than zero.
     */
    protected double scale;

    /** 
     * Creates a new instance of LaplaceDistribution 
     */
    public LaplaceDistribution()
    {
        this( DEFAULT_MEAN, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of LaplaceDistribution
     * @param mean
     * Mean of the distribution
     * @param scale
     * Scale factor of the distribution, must be greater than zero.
     */
    public LaplaceDistribution(
        double mean,
        double scale )
    {
        super();
        this.setMean(mean);
        this.setScale(scale);
    }

    /**
     * Copy Constructor
     * @param other LaplaceDistribution to copy
     */
    public LaplaceDistribution(
        LaplaceDistribution other )
    {
        this( other.getMean(), other.getScale() );
    }

    @Override
    public LaplaceDistribution clone()
    {
        return (LaplaceDistribution) super.clone();
    }

    public Double getMean()
    {
        return this.mean;
    }

    /**
     * Setter for mean
     * @param mean
     * Mean of the distribution
     */
    public void setMean(
        double mean)
    {
        this.mean = mean;
    }

    /**
     * Getter for scale
     * @return
     * Scale factor of the distribution, must be greater than zero.
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale factor of the distribution, must be greater than zero.
     */
    public void setScale(
        double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException( "scale must be > 0.0" );
        }
        this.scale = scale;
    }

    public ArrayList<Double> sample(
        Random random,
        int numSamples)
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            double p = random.nextDouble();
            samples.add( LaplaceDistribution.CDF.inverse(this, p) );
        }

        return samples;
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getMean(), this.getScale() );
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setMean( parameters.getElement(0) );
        this.setScale( parameters.getElement(1) );
    }

    public double getVariance()
    {
        final double b = this.getScale();
        return 2.0*b*b;
    }

    @Override
    public String toString()
    {
        return ObjectUtil.toString(this);
    }

    public LaplaceDistribution.CDF getCDF()
    {
        return new LaplaceDistribution.CDF( this );
    }

    public LaplaceDistribution.PDF getProbabilityFunction()
    {
        return new LaplaceDistribution.PDF( this );
    }

    public Double getMinSupport()
    {
        return Double.NEGATIVE_INFINITY;
    }

    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    public LaplaceDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new LaplaceDistribution.MaximumLikelihoodEstimator();
    }

    /**
     * CDF of the Laplace distribution.
     */
    public static class CDF
        extends LaplaceDistribution
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
    {

        /**
         * Creates a new instance of LaplaceDistribution.CDF
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of LaplaceDistribution.CDF
         * @param mean
         * Mean of the distribution
         * @param scale
         * Scale factor of the distribution, must be greater than zero.
         */
        public CDF(
            double mean,
            double scale )
        {
            super(mean,scale);
        }

        /**
         * Copy Constructor
         * @param other LaplaceDistribution to copy
         */
        public CDF(
            LaplaceDistribution other )
        {
            super( other );
        }

        public Double evaluate(
            Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        public double evaluate(
            double input)
        {
            if( input == Double.NEGATIVE_INFINITY )
            {
                return 0.0;
            }
            else if( input == Double.POSITIVE_INFINITY )
            {
                return 1.0;
            }
            double delta = input - this.mean;
            return 0.5 * (1.0 + Math.signum(delta)*(1.0-Math.exp(-Math.abs(delta)/this.scale)));
        }

        /**
         * Computes the inverse of the CDF for the give probability.  That is,
         * find x=CDF.inverse(p) so that CDF(x)=p.
         * @param p
         * Probability to invert, must be [0,1].
         * @return
         * Finds the value of the CDF "x" so that CDF(x)=p.
         */
        public Double inverse(
            double p )
        {
            return inverse( this, p );
        }

        /**
         * Computes the inverse of the CDF for the give probability.  That is,
         * find x=CDF.inverse(p) so that CDF(x)=p.
         * @param laplace
         * Laplace distribution to invert.
         * @param p
         * Probability to invert, must be [0,1].
         * @return
         * Finds the value of the CDF "x" so that CDF(x)=p.
         */
        public static double inverse(
            LaplaceDistribution laplace,
            double p )
        {
            if( p <= 0.0 )
            {
                return laplace.getMinSupport();
            }
            else if( p >= 1.0 )
            {
                return laplace.getMaxSupport();
            }
            else
            {
                double delta = p - 0.5;
                return laplace.mean -
                    laplace.scale*Math.signum(delta)*Math.log(1.0-2.0*Math.abs(delta));
            }
        }

        @Override
        public LaplaceDistribution.CDF getCDF()
        {
            return this;
        }

        public LaplaceDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        public Double differentiate(
            Double input)
        {
            return this.getDerivative().evaluate(input);
        }

    }

    /**
     * The PDF of a Laplace Distribution.
     */
    public static class PDF
        extends LaplaceDistribution
        implements ScalarProbabilityDensityFunction
    {

        /**
         * Creates a new instance of LaplaceDistribution.PDF
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of LaplaceDistribution.PDF
         * @param mean
         * Mean of the distribution
         * @param scale
         * Scale factor of the distribution, must be greater than zero.
         */
        public PDF(
            double mean,
            double scale )
        {
            super(mean,scale);
        }

        /**
         * Copy Constructor
         * @param other LaplaceDistribution to copy
         */
        public PDF(
            LaplaceDistribution other )
        {
            super( other );
        }

        public double evaluate(
            double input)
        {
            double front = 0.5 / this.scale;
            double exponent = -Math.abs(input-this.mean) / this.scale;
            return front * Math.exp(exponent);
        }

        public Double evaluate(
            Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        public double logEvaluate(
            Double input)
        {
            final double n1 = Math.log( 0.5 / this.scale );
            final double n2 = -Math.abs(input-this.mean) / this.scale;
            return n1 + n2;
        }

        @Override
        public LaplaceDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * Estimates the ML parameters of a Laplace distribution from a
     * Collection of Numbers.
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,LaplaceDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
            super();
        }

        public LaplaceDistribution learn(
            Collection<? extends Double> data)
        {

            final double mean = NumberAverager.INSTANCE.summarize(data);
            double absSum = 0.0;
            for( Double value : data )
            {
                double delta = value - mean;
                absSum += Math.abs(delta);
            }
            double scale = absSum / data.size();
            return new LaplaceDistribution( mean, scale );
        }
        
    }

    /**
     * Creates a UnivariateGaussian from weighted data
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,LaplaceDistribution>
    {

        /**
         * Default constructor
         */
        public WeightedMaximumLikelihoodEstimator()
        {
            super();
        }

        /**
         * Creates a new instance of LaplaceDistribution using a weighted
         * Maximum Likelihood estimate based on the given data
         * @param data
         * Weighed pairs of data (first is data, second is weight) that was
         * generated by some unknown LaplaceDistribution distribution
         * @return
         * Maximum Likelihood UnivariateGaussian that generated the data
         */
        public LaplaceDistribution learn(
            Collection<? extends WeightedValue<? extends Double>> data )
        {

            double mean = 0.0;
            double weightSum = 0.0;
            for( WeightedValue<? extends Double> weightedValue : data )
            {
                double weight = weightedValue.getWeight();
                if( weight != 0.0 )
                {
                    double value = weightedValue.getValue().doubleValue();
                    mean += weight*value;
                    weightSum += weight;
                }
            }

            if( weightSum != 0.0 )
            {
                mean /= weightSum;
            }

            // Now compute the shape factor
            double shape = 0.0;
            for( WeightedValue<? extends Number> weightedValue : data )
            {
                double weight = weightedValue.getWeight();
                if( weight != 0.0 )
                {
                    double value = weightedValue.getValue().doubleValue();
                    double delta = value - mean;
                    shape += weight * Math.abs(delta);
                }
            }

            if( weightSum != 0.0 )
            {
                shape /= weightSum;
            }

            return new LaplaceDistribution( mean, shape );
            
        }

    }
    
}
