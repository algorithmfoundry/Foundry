/*
 * File:                ParetoDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 10, 2010, Sandia Corporation.
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
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class describes the Pareto distribution, sometimes called the Bradford
 * Distribution.  The Pareto distribution tends to be used to model social
 * phenomena, such as the distribution of wealth in a society, price returns of
 * stocks, file sizes on a computer, etc.  It encapsulates the notion that
 * there are few large entities and many smaller ones (80 percent of the wealth
 * is controlled by 20 percent of the population).
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Normal distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Pareto_distribution"
)
public class ParetoDistribution 
    extends AbstractClosedFormSmoothUnivariateDistribution
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 2.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFALUT_SCALE = 1.0;

    /**
     * Default shift, {@value}.
     */
    public static final double DEFAULT_SHIFT = 0.0;

    /**
     * Scale parameter, must be greater than zero.
     */
    protected double shape;

    /**
     * Scale parameter, must be greater than zero.
     */
    protected double scale;

    /**
     * Amount to shift the distribution to the left.
     */
    protected double shift;

    /** 
     * Creates a new instance of ParetoDistribution 
     */
    public ParetoDistribution()
    {
        this( DEFAULT_SHAPE, DEFALUT_SCALE, DEFAULT_SHIFT );
    }

    /**
     * Creates a new instance of ParetoDistribution 
     * @param shape
     * Scale parameter, must be greater than zero.
     * @param scale
     * Scale parameter, must be greater than zero.
     * @param shift
     * Amount to shift the distribution to the left.
     */
    public ParetoDistribution(
        final double shape,
        final double scale,
        final double shift )
    {
        this.setShape(shape);
        this.setScale(scale);
        this.setShift(shift);
    }

    /**
     * Copy constructor
     * @param other
     * ParetoDistribution to copy
     */
    public ParetoDistribution(
        final ParetoDistribution other )
    {
        this( other.getShape(), other.getScale(), other.getShift() );
    }

    @Override
    public ParetoDistribution clone()
    {
        return (ParetoDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape parameter, must be greater than zero.
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape parameter, must be greater than zero.
     */
    public void setShape(
        final double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Shape must be > 0.0 " );
        }

        this.shape = shape;
    }

    /**
     * Getter for scale.
     * @return 
     * Scale parameter, must be greater than zero.
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale parameter, must be greater than zero.
     */
    public void setScale(
        final double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Scale must be > 0.0 " );
        }
        this.scale = scale;
    }

    @Override
    public Double getMean()
    {
        if( this.shape > 1.0 )
        {
            return this.shape * this.scale / (this.shape-1.0) - this.shift;
        }
        else
        {
            throw new IllegalArgumentException(
                "Mean is undefined when shape is <= 1.0" );
        }
    }


    @Override
    public double getVariance()
    {
        if( this.shape > 2.0 )
        {
            final double numerator = this.scale*this.scale * this.shape;
            final double am1 = this.shape-1.0;
            final double am2 = this.shape-2.0;
            final double denominator = am1*am1*am2;
            return numerator / denominator;
        }
        else
        {
            throw new IllegalArgumentException(
                "Variance is undefined when shape is <= 2.0" );
        }
    }

    @Override
    public ArrayList<? extends Double> sample(
        final Random random,
        final int numSamples)
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        final double exp = 1.0/this.shape;
        for( int n = 0; n < numSamples; n++ )
        {
            final double u = random.nextDouble();
            samples.add( this.scale / Math.pow(u, exp) - this.shift );
        }

        return samples;
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.shape, this.scale, this.shift );
    }

    @Override
    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(3);
        this.setShape( parameters.getElement(0) );
        this.setScale( parameters.getElement(1) );
        this.setShift( parameters.getElement(2) );
    }

    @Override
    public ParetoDistribution.PDF getProbabilityFunction()
    {
        return new ParetoDistribution.PDF( this );
    }

    @Override
    public ParetoDistribution.CDF getCDF()
    {
        return new ParetoDistribution.CDF( this );
    }

    @Override
    public Double getMinSupport()
    {
        return this.scale - this.shift;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString()
    {
        return "Shape = " + this.getShape() + ", Scale = " + this.getScale() + ", Shift = " + this.getShift();
    }

    /**
     * Getter for shift.
     * @return
     * Amount to shift the distribution to the left.
     */
    double getShift()
    {
        return this.shift;
    }

    /**
     * Setter for shift.
     * @param shift
     * Amount to shift the distribution to the left.
     */
    public void setShift(
        final double shift)
    {
        this.shift = shift;
    }

    /**
     * CDF of the Pareto Distribution.
     */
    public static class CDF
        extends ParetoDistribution
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
    {

        /**
         * Creates a new instance of CDF
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of CDF
         * @param shape
         * Scale parameter, must be greater than zero.
         * @param scale
         * Scale parameter, must be greater than zero.
         * @param shift
         * Amount to shift the distribution to the left.
         */
        public CDF(
            final double shape,
            final double scale,
            final double shift )
        {
            super( shape, scale, shift );
        }

        /**
         * Copy constructor
         * @param other
         * ParetoDistribution to copy
         */
        public CDF(
            final ParetoDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        @Override
        public double evaluate(
            double input)
        {
            if( (input+this.shift) > this.scale )
            {
                return 1.0 - Math.pow( this.scale/(input+this.shift), this.shape);
            }
            else
            {
                return 0.0;
            }
        }

        @Override
        public ParetoDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public ParetoDistribution.PDF getDerivative()
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
            if( probability <= 0.0 )
            {
                return this.getMinSupport();
            }
            else if( probability >= 1.0 )
            {
                return this.getMaxSupport();
            }
            else
            {
                return this.scale / Math.pow(1.0-probability, 1.0/this.shape)-this.shift;
            }
        }

    }

    /**
     * PDF of the ParetoDistribution
     */
    public static class PDF
        extends ParetoDistribution
        implements UnivariateProbabilityDensityFunction
    {

        /**
         * Creates a new instance of PDF
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF
         * @param shape
         * Scale parameter, must be greater than zero.
         * @param scale
         * Scale parameter, must be greater than zero.
         * @param shift
         * Amount to shift the distribution to the left.
         */
        public PDF(
            final double shape,
            final double scale,
            final double shift )
        {
            super( shape, scale, shift );
        }

        /**
         * Copy constructor
         * @param other
         * ParetoDistribution to copy
         */
        public PDF(
            final ParetoDistribution other )
        {
            super( other );
        }

        @Override
        public ParetoDistribution.PDF getProbabilityFunction()
        {
            return this;
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
//            if( input > this.scale )
            if( (input+this.shift) > this.scale )
            {
                double numerator = Math.log(this.shape) + this.shape*Math.log(this.scale);
                double denominator = (this.shape+1.0)*Math.log(input+this.shift);
                return numerator - denominator;
            }
            else
            {
                return Math.log(0.0);
            }
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
            return Math.exp(this.logEvaluate(input));
        }

    }

}
