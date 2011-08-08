/*
 * File:                ChiSquareDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 * Describes a Chi-Square Distribution.  The Chi-Square distribution occurs
 * when Y = X1^2 + X2^2 + ... + Xn^2.  In this case, Y will be a Chi-Square
 * Random Variable with "n" degrees of freedom, iff the Xi are independent
 * Gaussian Random Variables. 
 * The chi-square distribution is a member of the Gamma-distribution family.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Chi-square distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Chi-square_distribution"
)
public class ChiSquareDistribution
    extends AbstractClosedFormSmoothUnivariateDistribution
{

    /**
     * Default degrees of freedom, {@value}.
     */
    public static final double DEFAULT_DEGREES_OF_FREEDOM = 2.0;

    /**
     * Number of degrees of freedom in the distribution,
     * must be greater than 0.0
     */
    private double degreesOfFreedom;

    /**
     * Default constructor.
     */
    public ChiSquareDistribution()
    {
        this( DEFAULT_DEGREES_OF_FREEDOM );
    }

    /**
     * Creates a new instance of ChiSquareDistribution
     * @param degreesOfFreedom 
     * Number of degrees of freedom in the distribution,
     * must be greater than 0.0
     */
    public ChiSquareDistribution(
        final double degreesOfFreedom )
    {
        this.setDegreesOfFreedom( degreesOfFreedom );
    }

    /**
     * Copy constructor
     * @param other
     * ChiSquareDistribution to copy
     */
    public ChiSquareDistribution(
        final ChiSquareDistribution other )
    {
        this( other.getDegreesOfFreedom() );
    }
    
    @Override
    public ChiSquareDistribution clone()
    {
        return (ChiSquareDistribution) super.clone();
    }

    /**
     * Getter for degrees of freedom
     * @return 
     * Number of degrees of freedom in the distribution,
     * must be greater than 0.0
     */
    public double getDegreesOfFreedom()
    {
        return this.degreesOfFreedom;
    }

    /**
     * Setter for degrees of freedom
     * @param degreesOfFreedom 
     * Number of degrees of freedom in the distribution,
     * must be greater than 0.0
     */
    public void setDegreesOfFreedom(
        final double degreesOfFreedom )
    {
        if (degreesOfFreedom <= 0.0)
        {
            throw new IllegalArgumentException(
                "Degrees of Freedom must be > 0.0" );
        }
        this.degreesOfFreedom = degreesOfFreedom;
    }

    @Override
    public Double getMean()
    {
        return this.getDegreesOfFreedom();
    }

    @Override
    public double getVariance()
    {
        return 2.0 * this.getDegreesOfFreedom();
    }

    /**
     * Returns the parameter of the chi-square PDF
     * @return 
     * 1-dimensional Vector containing the degrees of freedom
     */
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getDegreesOfFreedom() );
    }

    /**
     * Sets the parameter of the chi-square PDF
     * @param parameters 
     * 1-dimensional Vector containing the degrees of freedom
     */
    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if (parameters.getDimensionality() != 1)
        {
            throw new IllegalArgumentException(
                "Expected 1-dimensional Vector of parameters!" );
        }
        this.setDegreesOfFreedom( parameters.getElement( 0 ) );
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        return sample( this.degreesOfFreedom, random, numSamples);
    }

    /**
     * Samples from a Chi-Square distribution with the given degrees of freedom
     * @param degreesOfFreedom
     * Degrees of freedom of the Chi-Square distribution
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to generate
     * @return
     * Samples from the GammaDistribution using the Chi-Square DOFs.
     */
    public static ArrayList<Double> sample(
        final double degreesOfFreedom,
        final Random random,
        final int numSamples )
    {
        return GammaDistribution.sample(
            degreesOfFreedom/2.0, 2.0, random, numSamples);
    }

    @Override
    public ChiSquareDistribution.CDF getCDF()
    {
        return new ChiSquareDistribution.CDF( this );
    }

    @Override
    public ChiSquareDistribution.PDF getProbabilityFunction()
    {
        return new ChiSquareDistribution.PDF( this );
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
    
    /**
     * PDF of the Chi-Square distribution
     */
    public static class PDF
        extends ChiSquareDistribution
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
         * Creates a new instance of ChiSquareDistribution
         * @param degreesOfFreedom 
         * Number of degrees of freedom in the distribution,
         * must be greater than 0.0
         */
        public PDF(
            final double degreesOfFreedom )
        {
            super( degreesOfFreedom );
        }

        /**
         * Copy constructor
         * @param other
         * ChiSquareDistribution to copy
         */
        public PDF(
            final ChiSquareDistribution other )
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
            return evaluate( input, this.getDegreesOfFreedom() );
        }

        /**
         * Evaluates the chi-square PDF for the given input and DOFs
         * @param x 
         * Input to the PDF
         * @param degreesOfFreedom 
         * DOFs of the PDF
         * @return 
         * p(x|dof)
         */
        public static double evaluate(
            final double x,
            final double degreesOfFreedom )
        {
            return Math.exp(logEvaluate(x, degreesOfFreedom));
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
            return logEvaluate(input, this.getDegreesOfFreedom());
        }

        /**
         * Computes the natural logarithm of the PDF.
         * @param x
         * Input to the PDF
         * @param degreesOfFreedom
         * DOFs of the PDF
         * @return
         * Natural logarithm of the PDF.
         */
        public static double logEvaluate(
            final double x,
            final double degreesOfFreedom )
        {
            return GammaDistribution.PDF.logEvaluate( x, degreesOfFreedom/2.0, 2.0 );
        }

        @Override
        public ChiSquareDistribution.PDF getProbabilityFunction()
        {
            return this;
        }
        
    }
    
    /**
     * Cumulative Distribution Function (CDF) of a Chi-Square Distribution
     */
    public static class CDF
        extends ChiSquareDistribution
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
         * Creates a new instance of ChiSquareDistribution
         * @param degreesOfFreedom 
         * Number of degrees of freedom in the distribution,
         * must be greater than 0.0
         */
        public CDF(
            final double degreesOfFreedom )
        {
            super( degreesOfFreedom );
        }

        /**
         * Copy constructor
         * @param other
         * ChiSquareDistribution to copy
         */
        public CDF(
            final ChiSquareDistribution other )
        {
            super( other );
        }
    
        @Override
        public double evaluate(
            final double input )
        {
            return evaluate( input, this.getDegreesOfFreedom() );
        }
        
        @Override
        public Double evaluate(
            final Double input )
        {
            return this.evaluate( input.doubleValue() );
        }        

        /**
         * Computes the values of the Chi-Square CDF for the given input and
         * degrees of freedom
         * @param input
         * Input about which to evaluate the CDF
         * @param degreesOfFreedom
         * Number of degrees of freedom in the distribution
         * @return
         * Pr{ y <= input | degreesOfFreedom }
         */
        public static double evaluate(
            final double input,
            final double degreesOfFreedom )
        {
            if( degreesOfFreedom <= 0.0 )
            {
                throw new IllegalArgumentException(
                    "Degrees of Freedom must be > 0.0" );
            }
            return GammaDistribution.CDF.evaluate(
                input, degreesOfFreedom / 2.0, 2.0 );
        }

        @Override
        public ChiSquareDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public ChiSquareDistribution.PDF getDerivative()
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
