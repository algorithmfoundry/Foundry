/*
 * File:                NormalInverseGammaDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The normal inverse-gamma distribution is the product of a univariate
 * Gaussian distribution with an inverse-gamma distribution.  It is the
 * conjugate prior of a univariate Gaussian with unknown mean and unknown
 * variance.  (As far as I know, it has no other purpose.)
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Christopher M. Bishop",
            title="Pattern Recognition and Machine Learning",
            type=PublicationType.Book,
            year=2006,
            pages={101}
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Normal-scaled inverse gamma distribution",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Normal-scaled_inverse_gamma_distribution"
        )
    }
)
public class NormalInverseGammaDistribution
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDistribution<Vector>
{

    /**
     * Default location, {@value}.
     */
    public static final double DEFAULT_LOCATION = 0.0;

    /**
     * Default precision, {@value}.
     */
    public static final double DEFAULT_PRECISION = 1.0;

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = InverseGammaDistribution.DEFAULT_SHAPE;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = InverseGammaDistribution.DEFAULT_SCALE;

    /**
     * Location of the Gaussian kernel.
     */
    private double location;

    /**
     * Precision of the Gaussian kernel, must be greater than zero.
     */
    private double precision;

    /**
     * Shape parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    private double shape;

    /**
     * Scale parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    private double scale;


    /** 
     * Creates a new instance of NormalInverseGammaDistribution
     */
    public NormalInverseGammaDistribution()
    {
        this( DEFAULT_LOCATION, DEFAULT_PRECISION, DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of NormalInverseGammaDistribution
     * @param location
     * Location of the Gaussian kernel.
     * @param precision
     * Precision of the Gaussian kernel, must be greater than zero.
     * @param shape
     * Shape parameter of the Inverse Gamma kernel, must be greater than zero.
     * @param scale
     * Scale parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    public NormalInverseGammaDistribution(
        double location,
        double precision,
        double shape,
        double scale)
    {
        this.setLocation(location);
        this.setPrecision(precision);
        this.setShape(shape);
        this.setScale(scale);
    }

    /**
     * Copy constructor
     * @param other
     * NormalInverseGammaDistribution to copy
     */
    public NormalInverseGammaDistribution(
        NormalInverseGammaDistribution other )
    {
        this( other.getLocation(), other.getPrecision(),
            other.getShape(), other.getScale() );
    }

    @Override
    public NormalInverseGammaDistribution clone()
    {
        return (NormalInverseGammaDistribution) super.clone();
    }

    public Vector getMean()
    {
        if( this.shape > 1.0 )
        {
            return VectorFactory.getDefault().copyValues(
                this.location, this.scale/(this.shape-1.0) );
        }
        else
        {
            throw new IllegalArgumentException(
                "Shape must be > 1.0 for a mean" );
        }
    }

    @Override
    public void sampleInto(
        final Random random,
        final int sampleCount,
        final Collection<? super Vector> output)
    {
        InverseGammaDistribution.CDF inverseGamma =
            new InverseGammaDistribution.CDF(this.shape, this.scale);
        UnivariateGaussian.CDF gaussian =
            new UnivariateGaussian.CDF(this.location, 1.0 / this.precision);
        final double[] variances = inverseGamma.sampleAsDoubles(random, sampleCount);
        for (double variance : variances)
        {
            gaussian.setVariance(variance / this.precision);
            double mean = gaussian.sample(random);
            output.add(VectorFactory.getDefault().copyValues(mean, variance));
        }
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getLocation(), this.getPrecision(),
            this.getShape(), this.getScale() );
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(4);
        this.setLocation( parameters.getElement(0) );
        this.setPrecision( parameters.getElement(1) );
        this.setShape( parameters.getElement(2) );
        this.setScale( parameters.getElement(3) );
    }

    public NormalInverseGammaDistribution.PDF getProbabilityFunction()
    {
        return new NormalInverseGammaDistribution.PDF( this );
    }

    /**
     * Getter for location.
     * @return
     * Location of the Gaussian kernel.
     */
    public double getLocation()
    {
        return this.location;
    }

    /**
     * Setter for location.
     * @param location
     * Location of the Gaussian kernel.
     */
    public void setLocation(
        double location)
    {
        this.location = location;
    }

    /**
     * Getter for precision
     * @return
     * Precision of the Gaussian kernel, must be greater than zero.
     */
    public double getPrecision()
    {
        return this.precision;
    }

    /**
     * Setter for precision.
     * @param precision
     * Precision of the Gaussian kernel, must be greater than zero.
     */
    public void setPrecision(
        double precision)
    {
        if( precision <= 0.0 )
        {
            throw new IllegalArgumentException( "Precision must be > 0.0" );
        }
        this.precision = precision;
    }

    /**
     * Getter for shape
     * @return 
     * Shape parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape 
     * Shape parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    public void setShape(
        double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException( "Shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale 
     * Scale parameter of the Inverse Gamma kernel, must be greater than zero.
     */
    public void setScale(
        double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException( "Scale must be > 0.0" );
        }
        this.scale = scale;
    }

    @Override
    public String toString()
    {
        return "Location: " + this.getLocation() + ", Precision: " + this.getPrecision()
            + ", Shape: " + this.getShape() + ", Scale: " + this.getScale();
    }

    /**
     * PDF of the NormalInverseGammaDistribution
     */
    public static class PDF
        extends NormalInverseGammaDistribution
        implements ProbabilityDensityFunction<Vector>,
        VectorInputEvaluator<Vector,Double>
    {

        /**
         * Creates a new instance of NormalInverseGammaDistribution
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of NormalInverseGammaDistribution
         * @param location
         * Location of the Gaussian kernel.
         * @param precision
         * Precision of the Gaussian kernel, must be greater than zero.
         * @param shape
         * Shape parameter of the Inverse Gamma kernel, must be greater than zero.
         * @param scale
         * Scale parameter of the Inverse Gamma kernel, must be greater than zero.
         */
        public PDF(
            double location,
            double precision,
            double shape,
            double scale)
        {
            super( location, precision, shape, scale);
        }

        /**
         * Copy constructor
         * @param other
         * NormalInverseGammaDistribution to copy
         */
        public PDF(
            NormalInverseGammaDistribution other )
        {
            super( other );
        }


        @Override
        public NormalInverseGammaDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

        public double logEvaluate(
            Vector input)
        {
            input.assertDimensionalityEquals(2);
            double mean = input.getElement(0);
            double variance = input.getElement(1);
            InverseGammaDistribution.PDF inverseGamma =
                new InverseGammaDistribution.PDF( this.getShape(), this.getScale() );
            UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF(
                this.getLocation(), variance / this.getPrecision() );

            double logInverseGamma = inverseGamma.logEvaluate(variance);
            double logGaussian = gaussian.logEvaluate(mean);
            return logGaussian + logInverseGamma;
        }

        public Double evaluate(
            Vector input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        public int getInputDimensionality()
        {
            return 2;
        }
        
    }

}
