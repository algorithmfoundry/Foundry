/*
 * File:                CauchyDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 25, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 * A Cauchy Distribution is the ratio of two Gaussian Distributions, sometimes
 * known as the Lorentz distribution.  The mean is undefined and it has
 * infinite variance.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Cauchy distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Cauchy_distribution"
)
public class CauchyDistribution 
    extends AbstractClosedFormSmoothUnivariateDistribution
{

    /**
     * Default location, {@value}.
     */
    public static final double DEFAULT_LOCATION = 0.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Central location (also the median and mode) of the distribution.
     */
    protected double location;

    /**
     * Scale of the distribution, must be greater than zero.
     */
    protected double scale;
    
    /** 
     * Creates a new instance of CauchyDistribution 
     */
    public CauchyDistribution()
    {
        this( DEFAULT_LOCATION, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of CauchyDistribution
     * @param location
     * Central location (also the median and mode) of the distribution.
     * @param scale
     * Scale of the distribution, must be greater than zero.
     */
    public CauchyDistribution(
        final double location,
        final double scale)
    {
        this.location = location;
        this.scale = scale;
    }

    /**
     * Copy constructor
     * @param other
     * CauchyDistribution to copy.
     */
    public CauchyDistribution(
        final CauchyDistribution other )
    {
        this( other.getLocation(), other.getScale() );
    }

    @Override
    public CauchyDistribution clone()
    {
        CauchyDistribution clone = (CauchyDistribution) super.clone();
        return clone;
    }

    /**
     * Getter for location.
     * @return
     * Central location (also the median and mode) of the distribution.
     */
    public double getLocation()
    {
        return this.location;
    }

    /**
     * Setter for location
     * @param location
     * Central location (also the median and mode) of the distribution.
     */
    public void setLocation(
        final double location)
    {
        this.location = location;
    }

    /**
     * Getter for scale.
     * @return
     * Scale of the distribution, must be greater than zero.
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale of the distribution, must be greater than zero.
     */
    public void setScale(
        final double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException( "Scale must be > 0.0" );
        }

        this.scale = scale;
    }

    @Override
    public Double getMean()
    {
        // The mean of a Cauchy is undefined due to its heavy tails.
        // However, a Cauchy distribution is symmetric about the "location"
        // parameter.  So, one common-sense interpretation of the mean is that
        // it's equal to the median of a symmetric distribution.
        // So that's what I'm going with.
        return this.getLocation();
    }

    @Override
    public ArrayList<? extends Double> sample(
        final Random random,
        final int numSamples)
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            double g1 = random.nextGaussian();
            double g2 = random.nextGaussian();
            double ratio = g1/g2;
            double scaled = ratio * this.scale;
            samples.add( scaled + this.location );
        }
        return samples;
    }

    @Override
    public CauchyDistribution.CDF getCDF()
    {
        return new CauchyDistribution.CDF( this );
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getLocation(), this.getScale() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setLocation( parameters.getElement(0) );
        this.setScale( parameters.getElement(1) );
    }

    @Override
    public double getVariance()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public CauchyDistribution.PDF getProbabilityFunction()
    {
        return new CauchyDistribution.PDF( this );
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
     * PDF of the CauchyDistribution.
     */
    public static class PDF
        extends CauchyDistribution
        implements UnivariateProbabilityDensityFunction
    {

        /**
         * Creates a new instance of CauchyDistribution
         */
        public PDF()
        {
            super( DEFAULT_LOCATION, DEFAULT_SCALE );
        }

        /**
         * Creates a new instance of CauchyDistribution
         * @param location
         * Central location (also the median and mode) of the distribution.
         * @param scale
         * Scale of the distribution, must be greater than zero.
         */
        public PDF(
            final double location,
            final double scale)
        {
            super( location, scale );
        }

        /**
         * Copy constructor
         * @param other
         * CauchyDistribution to copy.
         */
        public PDF(
            final CauchyDistribution other )
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
            return Math.log(this.evaluate(input));
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
            final double leading = Math.PI * this.scale;
            final double d1 = (input - this.location) / this.scale;
            final double dx = 1.0 + d1*d1;
            final double denominator = leading * dx;
            return 1.0/denominator;
        }

        @Override
        public CauchyDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the CauchyDistribution.
     */
    public static class CDF
        extends CauchyDistribution
        implements SmoothCumulativeDistributionFunction
    {

        /**
         * Creates a new instance of CauchyDistribution
         */
        public CDF()
        {
            super( DEFAULT_LOCATION, DEFAULT_SCALE );
        }

        /**
         * Creates a new instance of CauchyDistribution
         * @param location
         * Central location (also the median and mode) of the distribution.
         * @param scale
         * Scale of the distribution, must be greater than zero.
         */
        public CDF(
            final double location,
            final double scale)
        {
            super( location, scale );
        }

        /**
         * Copy constructor
         * @param other
         * CauchyDistribution to copy.
         */
        public CDF(
            final CauchyDistribution other )
        {
            super( other );
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
            double d1 = Math.atan( (input - this.location)/this.scale );
            return d1/Math.PI + 0.5;
        }

        @Override
        public CauchyDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public CauchyDistribution.PDF getDerivative()
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
