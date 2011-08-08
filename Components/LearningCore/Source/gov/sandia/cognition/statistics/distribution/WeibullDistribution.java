/*
 * File:                WeibullDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 30, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 * Describes a Weibull distribution, which is often used to describe the
 * mortality, lifespan, or size distribution of objects.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Weibull Distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Weibull_distribution"
)
public class WeibullDistribution 
    extends AbstractClosedFormSmoothUnivariateDistribution
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 1.0;

    /**
     * Default scale, {@value}
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Shape parameter, must be greater than 0.0
     */
    protected double shape;

    /**
     * Scale parameter, must be greater than 0.0
     */
    protected double scale;

    /** 
     * Creates a new instance of WeibullDistribution 
     */
    public WeibullDistribution()
    {
        this( DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of WeibullDistribution
     * @param shape
     * Shape parameter, must be greater than 0.0
     * @param scale
     * Scale parameter, must be greater than 0.0
     */
    public WeibullDistribution(
        final double shape,
        final double scale)
    {
        this.shape = shape;
        this.scale = scale;
    }

    /**
     * Copy constructor
     * @param other
     * WeibullDistribution to copy
     */
    public WeibullDistribution(
        final WeibullDistribution other )
    {
        this( other.getShape(), other.getScale() );
    }

    @Override
    public WeibullDistribution clone()
    {
        return (WeibullDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape parameter, must be greater than 0.0
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape parameter, must be greater than 0.0
     */
    public void setShape(
        final double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale parameter, must be greater than 0.0
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale parameter, must be greater than 0.0
     */
    public void setScale(
        final double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Scale must be > 0.0" );
        }
        this.scale = scale;
    }

    @Override
    public Double getMean()
    {
        return this.scale * Math.exp( MathUtil.logGammaFunction(
            1.0 + 1.0/this.shape ) );
    }

    @Override
    public double getVariance()
    {
        final double mean = this.getMean();
        return this.scale*this.scale * Math.exp( MathUtil.logGammaFunction(
            1.0 + 2.0/this.shape ) ) - mean*mean;
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples)
    {        
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        final double exp = 1.0/this.shape;
        for( int n = 0; n < numSamples; n++ )
        {
            double u = random.nextDouble();
            samples.add( this.scale * Math.pow(-Math.log(u), exp) );
        }
        return samples;
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getShape(), this.getScale() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setShape( parameters.getElement(0) );
        this.setScale( parameters.getElement(1) );
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
    public WeibullDistribution.PDF getProbabilityFunction()
    {
        return new WeibullDistribution.PDF( this );
    }

    @Override
    public WeibullDistribution.CDF getCDF()
    {
        return new WeibullDistribution.CDF( this );
    }

    /**
     * PDF of the Weibull distribution
     */
    public static class PDF
        extends WeibullDistribution
        implements UnivariateProbabilityDensityFunction
    {

        /** 
         * Creates a new instance of WeibullDistribution 
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of WeibullDistribution
         * @param shape
         * Shape parameter, must be greater than 0.0
         * @param scale
         * Scale parameter, must be greater than 0.0
         */
        public PDF(
            final double shape,
            final double scale)
        {
            super( shape, scale );
        }        

        /**
         * Copy constructor
         * @param other
         * WeibullDistribution to copy
         */
        public PDF(
            final WeibullDistribution other )
        {
            super( other );
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
            if( input < 0.0 )
            {
                return Math.log(0.0);
            }

            double logSum = 0.0;
            logSum += Math.log(this.shape/this.scale);
            logSum += (this.shape-1.0) * Math.log(input/this.scale);
            logSum -= Math.pow( input/this.scale, this.shape );
            return logSum;
        }

        @Override
        public Double evaluate(
            Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluate(
            double input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public WeibullDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the Weibull distribution
     */
    public static class CDF
        extends WeibullDistribution
        implements SmoothCumulativeDistributionFunction
    {

        /**
         * Creates a new instance of WeibullDistribution
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of WeibullDistribution
         * @param shape
         * Shape parameter, must be greater than 0.0
         * @param scale
         * Scale parameter, must be greater than 0.0
         */
        public CDF(
            final double shape,
            final double scale)
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * WeibullDistribution to copy
         */
        public CDF(
            final WeibullDistribution other )
        {
            super( other );
        }

        @Override
        public WeibullDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
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
            if( input < 0.0 )
            {
                return 0.0;
            }
            else
            {
                return 1.0 - Math.exp( -Math.pow( input/this.scale, this.shape ) );
            }
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getDerivative().evaluate( input );
        }

        @Override
        public WeibullDistribution.CDF getCDF()
        {
            return this;
        }

    }

}
