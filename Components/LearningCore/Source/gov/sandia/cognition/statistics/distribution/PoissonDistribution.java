/*
 * File:                PoissonDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 14, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.IntegerCollection;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormScalarCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * A Poisson distribution is the limits of what happens when a Bernoulli trial
 * with "rare" events are sampled on a continuous basis and then binned into
 * discrete time intervals.  For example, if two people buy a house every day,
 * then what is the probability that ten people buy a house on some day?
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Poisson distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Poisson_distribution"
)
public class PoissonDistribution
    extends AbstractClosedFormScalarDistribution<Number>
    implements ClosedFormDiscreteScalarDistribution<Number>,
    EstimableDistribution<Number,PoissonDistribution>
{

    /**
     * Default rate parameter, {@value}.
     */
    public static final double DEFAULT_RATE = 1.0;

    /**
     * Expected number of occurrences during the integer interval, must be
     * greater than zero.
     */
    protected double rate;

    /** 
     * Creates a new instance of PoissonDistribution 
     */
    public PoissonDistribution()
    {
        this( DEFAULT_RATE );
    }

    /**
     * Creates a new instance of PoissonDistribution
     * @param rate
     * Expected number of occurrences during the integer interval, must be
     * greater than zero.
     */
    public PoissonDistribution(
        double rate )
    {
        this.setRate(rate);
    }

    /**
     * Creates a new instance of PoissonDistribution
     * @param other
     * PoissonDistribution to copy.
     */
    public PoissonDistribution(
        PoissonDistribution other )
    {
        this( other.getRate() );
    }


    @Override
    public PoissonDistribution clone()
    {
        PoissonDistribution clone = (PoissonDistribution) super.clone();
        return clone;
    }

    public Double getMean()
    {
        return this.getRate();
    }

    public ArrayList<Number> sample(
        Random random,
        int numSamples)
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    public PoissonDistribution.CDF getCDF()
    {
        return new CDF( this );
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
        return this.getRate();
    }

    public IntegerCollection getDomain()
    {
        // The actual support is the entire set of nonnegative integers, but
        // that would crush us... So let's just go out far enough where
        // the PMF values are almost zero (and the CDF is almost 1.0).
        return new IntegerCollection(0, (int) Math.round(this.getRate()*10)+5 );
    }

    public PoissonDistribution.PMF getProbabilityFunction()
    {
        return new PoissonDistribution.PMF( this );
    }

    /**
     * Getter for rate
     * @return
     * Expected number of occurrences during the integer interval, must be
     * greater than zero.
     */
    public double getRate()
    {
        return this.rate;
    }

    /**
     * Setter for rate
     * @param rate
     * Expected number of occurrences during the integer interval, must be
     * greater than zero.
     */
    public void setRate(
        double rate)
    {
        if( rate <= 0.0 )
        {
            throw new IllegalArgumentException( "Rate must be >0.0" );
        }
        this.rate = rate;
    }

    public Integer getMinSupport()
    {
        return 0;
    }

    public Integer getMaxSupport()
    {
        return Integer.MAX_VALUE;
    }

    public PoissonDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new PoissonDistribution.MaximumLikelihoodEstimator();
    }

    /**
     * PMF of the PoissonDistribution.
     */
    public static class PMF
        extends PoissonDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Default constructor.
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new PMF
         * @param rate
         * Expected number of occurrences during the integer interval, must be
         * greater than zero.
         */
        public PMF(
            double rate )
        {
            super( rate );
        }

        /**
         * Copy constructor
         * @param other
         * PoissonDistribution to copy
         */
        public PMF(
            PoissonDistribution other )
        {
            super( other );
        }

        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        public Double evaluate(
            Number input)
        {
            int k = input.intValue();
            if( k < 0 )
            {
                return 0.0;
            }
            else
            {
                return Math.exp( this.logEvaluate(input) );
            }
        }

        public double logEvaluate(
            Number input)
        {
            int k = input.intValue();
            if( k < 0 )
            {
                return Math.log(0.0);
            }
            else if( k == 0 )
            {
                return -this.rate;
            }
            else
            {
                final double lambda = this.rate;
                double logSum = k*Math.log(lambda) - lambda - MathUtil.logFactorial(k);
                return logSum;
            }
        }

        @Override
        public PoissonDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the PoissonDistribution
     */
    public static class CDF
        extends PoissonDistribution
        implements ClosedFormScalarCumulativeDistributionFunction<Number>
    {

        /**
         * Default constructor.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new CDF
         * @param rate
         * Expected number of occurrences during the integer interval, must be
         * greater than zero.
         */
        public CDF(
            double rate )
        {
            super( rate );
        }

        /**
         * Copy constructor
         * @param other
         * PoissonDistribution to copy
         */
        public CDF(
            PoissonDistribution other )
        {
            super( other );
        }

        public Double evaluate(
            Number input)
        {
            int k = (int) Math.floor(input.doubleValue());
            if( k < 0 )
            {
                return 0.0;
            }
            else if( k == 0 )
            {
                return Math.exp( -this.rate );
            }
            else
            {
                return 1.0 - MathUtil.lowerIncompleteGammaFunction( k+1, this.rate );
            }
        }

        @Override
        public PoissonDistribution.CDF getCDF()
        {
            return this;
        }
        
    }

    /**
     * Creates a PoissonDistribution from data
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Number,PoissonDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
        }

        public PoissonDistribution.PMF learn(
            Collection<? extends Number> data )
        {
            double mean = UnivariateStatisticsUtil.computeMean(data);
            return new PoissonDistribution.PMF(mean);
        }

    }

}

