/*
 * File:                LogisticDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 30, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.ArrayList;
import java.util.Random;

/**
 * A implementation of the scalar logistic distribution, which measures the
 * log-odds of a binary event.
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
@PublicationReference(
    author="Wikipedia",
    title="Logistic distribution",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/Logistic_distribution"
)
public class LogisticDistribution 
    extends AbstractClosedFormSmoothUnivariateDistribution
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
     * Mean (median and mode) of the distribution.
     */
    protected double mean;

    /**
     * Scale of the distribution, proportionate to the standard deviation,
     * must be greater than zero.
     */
    protected double scale;

    /**
     * Default constructor
     */
    public LogisticDistribution()
    {
        this( DEFAULT_MEAN, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of LogisticDistribution
     * @param mean
     * Mean (median and mode) of the distribution.
     * @param scale
     * Scale of the distribution, proportionate to the standard deviation,
     * must be greater than zero.
     */
    public LogisticDistribution(
        final double mean,
        final double scale)
    {
        this.setMean(mean);
        this.setScale(scale);
    }

    /**
     * Copy constructor
     * @param other
     * LogisticDistribution to copy
     */
    public LogisticDistribution(
        final LogisticDistribution other )
    {
        this( other.getMean(), other.getScale() );
    }

    @Override
    public LogisticDistribution clone()
    {
        return (LogisticDistribution) super.clone();
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples)
    {

        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            final double p = random.nextDouble();
            samples.add( this.mean + this.scale * Math.log( p/(1.0-p) ) );
        }
        return samples;
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getMean(), this.getScale() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setMean( parameters.getElement(0) );
        this.setScale( parameters.getElement(1) );
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

    @Override
    public double getVariance()
    {
        double s = Math.PI * this.getScale();
        return (s*s) / 3.0;
    }

    @Override
    public LogisticDistribution.PDF getProbabilityFunction()
    {
        return new LogisticDistribution.PDF( this );
    }

    @Override
    public LogisticDistribution.CDF getCDF()
    {
        return new LogisticDistribution.CDF( this );
    }

    @Override
    public Double getMean()
    {
        return this.mean;
    }

    /**
     * Setter for mean
     * @param mean
     * Mean (median and mode) of the distribution.
     */
    public void setMean(
        final double mean)
    {
        this.mean = mean;
    }

    /**
     * Getter for scale
     * @return 
     * Scale of the distribution, proportionate to the standard deviation,
     * must be greater than zero.
     */
    public double getScale()
    {
        return scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale of the distribution, proportionate to the standard deviation,
     * must be greater than zero.
     */
    public void setScale(
        final double scale)
    {
        ArgumentChecker.assertIsPositive("scale", scale);
        this.scale = scale;
    }

    /**
     * PDF of the LogisticDistribution
     */
    public static class PDF
        extends LogisticDistribution
        implements UnivariateProbabilityDensityFunction
    {

        /**
         * Default constructor
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF
         * @param mean
         * Mean (median and mode) of the distribution.
         * @param scale
         * Scale of the distribution, proportionate to the standard deviation,
         * must be greater than zero.
         */
        public PDF(
            final double mean,
            final double scale)
        {
            super( mean, scale );
        }

        /**
         * Copy constructor
         * @param other
         * LogisticDistribution to copy
         */
        public PDF(
            final LogisticDistribution other )
        {
            super( other );
        }

        @Override
        public double logEvaluate(
            final double input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public double logEvaluate(
            final Double input)
        {
            return this.logEvaluate(input.doubleValue());
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluate(
            final double input)
        {
            final double v1 = Math.exp( (this.mean-input) / this.scale );
            final double v2 = 1.0 + v1;
            return v1 / (this.scale * (v2*v2) );
        }

        @Override
        public LogisticDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the LogisticDistribution
     */
    public static class CDF
        extends LogisticDistribution
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
    {

        /**
         * Default constructor
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of CDF
         * @param mean
         * Mean (median and mode) of the distribution.
         * @param scale
         * Scale of the distribution, proportionate to the standard deviation,
         * must be greater than zero.
         */
        public CDF(
            final double mean,
            final double scale)
        {
            super( mean, scale );
        }

        /**
         * Copy constructor
         * @param other
         * LogisticDistribution to copy
         */
        public CDF(
            LogisticDistribution other )
        {
            super( other );
        }

        @Override
        public LogisticDistribution.PDF getDerivative()
        {
            return new PDF( this );
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        @Override
        public double evaluate(
            final double input)
        {
            final double v1 = Math.exp( (this.mean-input) / this.scale );
            return 1.0 / (1.0 + v1);
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getProbabilityFunction().evaluate(input);
        }

        @Override
        public LogisticDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public Double inverse(
            final double probability)
        {
            ProbabilityUtil.assertIsProbability(probability);
            return this.mean + this.scale * Math.log( probability/(1.0-probability) );
        }

    }

}
