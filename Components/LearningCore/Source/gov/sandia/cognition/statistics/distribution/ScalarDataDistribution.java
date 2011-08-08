/*
 * File:                ScalarDataDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 27, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.NumberComparator;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.statistics.ScalarDistribution;
import java.util.Collection;

/**
 * A data histogram that is based on scalar values (that is, Number).  This
 * class allows for the efficient sampling from both its PMF and CDF, but
 * the general use case is that the data are loaded into the data structure
 * once, and then repeatedly queried.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class ScalarDataDistribution
    extends MapBasedPointMassDistribution<Number>
    implements ScalarDistribution<Number>
{

    /**
     * Creates a new instance of ScalarDataDistribution2
     */
    public ScalarDataDistribution()
    {
        super();
    }

    /**
     * Creates a new instance of ScalarDataDistribution
     * @param values
     * Values to add to the distribution.
     */
    public ScalarDataDistribution(
        Collection<? extends Number> values )
    {
        super( values );
    }

    /**
     * Copy constructor
     * @param other
     * MapBasedPointMassDistribution to copy
     */
    public ScalarDataDistribution(
        MapBasedPointMassDistribution<Number> other )
    {
        super( other );
    }

    @Override
    public ScalarDataDistribution clone()
    {
        ScalarDataDistribution clone = (ScalarDataDistribution) super.clone();
        return clone;
    }

    public CumulativeDistributionFunction<Number> getCDF()
    {
        return new ScalarDataDistribution.CDF( this );
    }

    /**
     * Gets the mean (first central moment) of a MapBasedPointMassDistribution
     * over the set of Numbers.
     * @param distribution
     * Distribution to compute the mean of.
     * @return
     * Arithmetic mean of the given distribution.
     */
    public static double getMean(
        MapBasedPointMassDistribution<Number> distribution )
    {
        double mean = 0.0;
        for( Number value : distribution.getDomain() )
        {
            mean += value.doubleValue() * distribution.getMass(value);
        }

        double tm = distribution.getTotalMass();
        if( tm <= 0.0 )
        {
            tm = 1.0;
        }

        mean /= tm;
        return mean;
    }

    @Override
    public Double getMean()
    {
        return ScalarDataDistribution.getMean(this);
    }

    /**
     * Gets the variance (second central moment) of a
     * MapBasedPointMassDistribution over the set of Numbers.
     * @param distribution
     * Distribution to compute the variance of.
     * @return
     * Unbiased variance of the given distribution.
     */
    public static double getVariance(
        MapBasedPointMassDistribution<Number> distribution )
    {

        final double mean = distribution.getMean().doubleValue();
        double variance = 0.0;
        for( Number value : distribution.getDomain() )
        {
            double delta = value.doubleValue() - mean;
            double p = distribution.getMass(value);
            variance += p * delta*delta;
        }

        double tm = distribution.getTotalMass();
        if( tm <= 0.0 )
        {
            tm = 1.0;
        }

        variance /= tm;
        return variance;

    }

    public double getVariance()
    {
        return ScalarDataDistribution.getVariance(this);
    }

    public Double getMinSupport()
    {
        return Double.NEGATIVE_INFINITY;
    }

    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * CDF of the ScalarDataDistribution
     */
    public static class CDF
        extends ScalarDataDistribution
        implements CumulativeDistributionFunction<Number>
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
         * @param values
         * Values to add to the distribution.
         */
        public CDF(
            Collection<? extends Number> values )
        {
            super( values );
        }

        /**
         * Copy constructor
         * @param other
         * MapBasedPointMassDistribution to copy
         */
        public CDF(
            MapBasedPointMassDistribution<Number> other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            Number input)
        {

            double sum = 0.0;
            for( Number value : this.getDomain() )
            {
                if( NumberComparator.INSTANCE.compare(input,value) >= 0 )
                {
                    double p = this.getMass(value);
                    sum += p;
                }
            }

            double tm = this.getTotalMass();
            if( tm <= 0.0 )
            {
                tm = 1.0;
            }

            return sum/tm;
        }

        @Override
        public CumulativeDistributionFunction<Number> getCDF()
        {
            return this;
        }

    }

    /**
     * PMF of the ScalarDataDistribution2
     */
    public static class PMF
        extends ScalarDataDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Default constructor
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of PMF
         * @param values
         * Values to add to the distribution.
         */
        public PMF(
            Collection<? extends Number> values )
        {
            super( values );
        }

        /**
         * Copy constructor
         * @param other
         * MapBasedPointMassDistribution to copy
         */
        public PMF(
            MapBasedPointMassDistribution<Number> other )
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
            double tm = this.getTotalMass();
            if( tm <= 0.0 )
            {
                tm = 1.0;
            }
            return this.getMass(input) / tm;
        }

        public double logEvaluate(
            Number input)
        {
            return Math.log( this.evaluate(input) );
        }

    }

}
