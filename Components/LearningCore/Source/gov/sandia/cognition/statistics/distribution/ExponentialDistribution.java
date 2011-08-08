/*
 * File:                ExponentialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothScalarDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * An Exponential distribution describes the time between events in a poisson
 * process, resulting in a memoryless distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Exponential distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Exponential_distribution"
)
public class ExponentialDistribution 
    extends AbstractClosedFormSmoothScalarDistribution
{

    /**
     * Default rate, {@value}.
     */
    public static final double DEFAULT_RATE = 1.0;

    /**
     * Rate, or inverse scale, of the distribution, must be greater than zero.
     */
    protected double rate;

    /** 
     * Creates a new instance of ExponentialDistribution 
     */
    public ExponentialDistribution()
    {
        this( DEFAULT_RATE );
    }

    /**
     * Creates a new instance of ExponentialDistribution
     * @param rate
     * Rate, or inverse scale, of the distribution, must be greater than zero.
     */
    public ExponentialDistribution(
        double rate )
    {
        this.setRate(rate);
    }

    /**
     * Creates a new instance of ExponentialDistribution 
     * @param other
     * ExponentialDistribution to copy.
     */
    public ExponentialDistribution(
        ExponentialDistribution other )
    {
        this( other.getRate() );
    }

    @Override
    public ExponentialDistribution clone()
    {
        ExponentialDistribution clone = (ExponentialDistribution) super.clone();
        return clone;
    }

    /**
     * Getter for rate.
     * @return 
     * Rate, or inverse scale, of the distribution, must be greater than zero.
     */
    public double getRate()
    {
        return this.rate;
    }

    /**
     * Setter for rate.
     * @param rate
     * Rate, or inverse scale, of the distribution, must be greater than zero.
     */
    public void setRate(
        double rate)
    {
        if( rate <= 0.0 )
        {
            throw new IllegalArgumentException( "Rate must be > 0.0" );
        }
        this.rate = rate;
    }

    public Double getMean()
    {
        return 1.0/this.getRate();
    }

    @PublicationReference(
        author={
            "Christian P. Robert",
            "George Casella"
        },
        title="Monte Carlo Statistical Methods, Second Edition",
        type=PublicationType.Book,
        year=2004,
        pages=39,
        notes="Example 2.5"
    )
    public ArrayList<Double> sample(
        Random random,
        int numSamples)
    {

        final double negativeInverseScale = -1.0/this.rate;
        ArrayList<Double> retval = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            final double u = random.nextDouble();
            retval.add( Math.log(u) * negativeInverseScale );
        }

        return retval;
    }

    public ExponentialDistribution.CDF getCDF()
    {
        return new ExponentialDistribution.CDF( this );
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getRate() );
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(1);
        this.setRate( parameters.getElement(0) );
    }

    public double getVariance()
    {
        double d2 = this.rate * this.rate;
        return 1.0/d2;
    }

    public ExponentialDistribution.PDF getProbabilityFunction()
    {
        return new ExponentialDistribution.PDF( this );
    }

    public Double getMinSupport()
    {
        return 0.0;
    }

    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * PDF of the ExponentialDistribution.
     */
    public static class PDF
        extends ExponentialDistribution
        implements ScalarProbabilityDensityFunction
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
         * @param rate
         * Rate, or inverse scale, of the distribution, must be greater than zero.
         */
        public PDF(
            double rate )
        {
            super( rate );
        }

        /**
         * Copy constructor
         * @param other
         * ExponentialDistribution to copy.
         */
        public PDF(
            ExponentialDistribution other )
        {
            super( other );
        }

        public Double evaluate(
            Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        public double evaluate(
            double input)
        {
            if( input < 0.0 )
            {
                return 0.0;
            }
            else
            {
                return this.rate * Math.exp( -input * this.rate );
            }
        }

        public double logEvaluate(
            Double input)
        {
            if( input < 0.0 )
            {
                return Math.log(0.0);
            }
            else
            {
                final double n1 = Math.log( this.rate );
                final double n2 = -input * this.rate;
                return n1 + n2;
            }
        }

        @Override
        public ExponentialDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the ExponentialDistribution.
     */
    public static class CDF
        extends ExponentialDistribution
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
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
         * @param rate
         * Rate, or inverse scale, of the distribution, must be greater than zero.
         */
        public CDF(
            double rate )
        {
            super( rate );
        }

        /**
         * Copy constructor
         * @param other
         * ExponentialDistribution to copy.
         */
        public CDF(
            ExponentialDistribution other )
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
            if( input <= 0.0 )
            {
                return 0.0;
            }
            else
            {
                return 1.0 - Math.exp( -input * this.rate );
            }
        }

        @Override
        public ExponentialDistribution.CDF getCDF()
        {
            return this;
        }

        public ExponentialDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        public Double differentiate(
            Double input)
        {
            return this.getDerivative().evaluate(input);
        }

        public Double inverse(
            double probability)
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
                return -Math.log(1-probability) / this.rate;
            }
        }

    }


    /**
     * Creates a ExponentialDistribution from data
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,ExponentialDistribution>
    {

        /**
         * Default estimator.
         */
        public MaximumLikelihoodEstimator()
        {
        }

        public ExponentialDistribution learn(
            Collection<? extends Double> data)
        {
            double mean = UnivariateStatisticsUtil.computeMean(data);
            return new ExponentialDistribution( 1.0/mean );
        }

    }

    /**
     * Creates a ExponentialDistribution from weighted data
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,ExponentialDistribution>
    {

        /**
         * Default constructor.
         */
        public WeightedMaximumLikelihoodEstimator()
        {
        }

        public ExponentialDistribution learn(
            Collection<? extends WeightedValue<? extends Double>> data)
        {
            double mean = UnivariateStatisticsUtil.computeWeightedMean(data);
            return new ExponentialDistribution(1.0/mean);
        }

    }

}
