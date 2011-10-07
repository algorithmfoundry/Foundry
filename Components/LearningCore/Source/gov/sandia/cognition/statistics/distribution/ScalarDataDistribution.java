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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.UnivariateDistribution;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Data Distribution that uses Doubles as its keys, making it a univariate
 * distribution
 * @author Kevin R. Dixon
 * @since 3.1
 */
public class ScalarDataDistribution
    extends DefaultDataDistribution<Double>
    implements UnivariateDistribution<Double>
{

    /** 
     * Creates a new instance of ScalarDataDistribution
     */
    public ScalarDataDistribution()
    {
        super();
    }

    /**
     * Copy constructor
     * @param other
     * ScalarDataDistribution to copy
     */
    public ScalarDataDistribution(
        ScalarDataDistribution other)
    {
        super(other);
    }

    /**
     * Creates a new instance of ScalarDataDistribution
     * @param data
     * Data to create the distribution
     */
    public ScalarDataDistribution(
        Iterable<? extends Number> data )
    {
        this();
        // Can't use incrementAll, because that would require "? extends Double"
        for( Number value : data )
        {
            this.increment(value.doubleValue());
        }
    }

    /**
     * Creates a new instance of ScalarDataDistribution
     * @param map
     * @param total
     */
    protected ScalarDataDistribution(
        final Map<Double, MutableDouble> map,
        final double total)
    {
        super(map, total);
    }

    @Override
    public ScalarDataDistribution clone()
    {
        return (ScalarDataDistribution) super.clone();
    }

    @Override
    public ScalarDataDistribution.PMF getProbabilityFunction()
    {
        return new ScalarDataDistribution.PMF(this);
    }

    @Override
    public Double getMean()
    {
        double sum = 0.0;
        for (Entry<Double> entry : this.entrySet())
        {
            final double weight = entry.getValue();
            final double value = entry.getKey().doubleValue();
            sum += weight * value;
        }

        final double totalWeight = this.getTotal();
        return (totalWeight > 0.0) ? sum / totalWeight : 0.0;
    }

    @Override
    public ScalarDataDistribution.Estimator getEstimator()
    {
        return new ScalarDataDistribution.Estimator();
    }

    @Override
    public Double getMinSupport()
    {
        // Find the minimum key (not the key corresponding to the minimum value)
        double minkey = Double.POSITIVE_INFINITY;
        for (double key : this.keySet())
        {
            if (minkey > key)
            {
                minkey = key;
            }
        }
        return minkey;
    }

    @Override
    public Double getMaxSupport()
    {
        // Find the max key (not the key corresponding to the max value)
        double maxkey = Double.NEGATIVE_INFINITY;
        for (double key : this.keySet())
        {
            if (maxkey < key)
            {
                maxkey = key;
            }
        }
        return maxkey;
    }

    @Override
    public ScalarDataDistribution.CDF getCDF()
    {
        return new ScalarDataDistribution.CDF(this);
    }

    @Override
    @PublicationReference(
        title = "Algorithms for calculating variance",
        type = PublicationType.WebPage,
        year = 2010,
        author = "Wikipedia",
        url = "http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance")
    public double getVariance()
    {

        // Largely copied from:
        // UnivariateStatisticsUtil.computeWeightedMeanAndVariance();
        // Note: This is more compilcated than a straight-forward algorithm
        // that just computes the sum and sum-of-squares to get around
        // numerical precision issues.
        double mean = 0.0;
        double weightSum = 0.0;
        double m2 = 0.0;

        for (Entry<Double> entry : this.entrySet())
        {
            final double x = entry.getKey();
            double weight = entry.getValue();

            if (weight != 0.0)
            {
                if (weight < 0.0)
                {
                    // Use the absolute value of weights.
                    weight = -weight;
                }

                final double newWeightSum = weightSum + weight;
                final double delta = x - mean;

                final double update = delta * weight / newWeightSum;
                m2 += weightSum * delta * update;
                mean += update;
                weightSum = newWeightSum;
            }
        }

        double variance;
        if (weightSum > 0.0)
        {
            variance = m2 / weightSum;
        }
        else
        {
            variance = 0.0;
        }

        return variance;

    }

    /**
     * PMF of the ScalarDataDistribution
     */
    public static class PMF
        extends ScalarDataDistribution
        implements DataDistribution.PMF<Double>
    {

        /**
         * Default constructor
         */
        public PMF()
        {
            super();
        }

        /**
         * Copy constructor
         * @param other
         * ScalarDataDistribution to copy
         */
        public PMF(
            final ScalarDataDistribution other)
        {
            super(other);
        }

        /**
         * Creates a new instance of PMF
         * @param data
         * Data used to create the PMF
         */
        public PMF(
            final Iterable<? extends Number> data )
        {
            super( data );
        }

        @Override
        public double logEvaluate(
            final Double input)
        {
            return this.getLogFraction(input);
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.getFraction(input);
        }

        @Override
        public ScalarDataDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the ScalarDataDistribution, maintains the keys/domain in
     * sorted order (TreeMap), so it's slower than it's peers.
     */
    public static class CDF
        extends ScalarDataDistribution
        implements CumulativeDistributionFunction<Double>
    {

        /**
         * Default constructor
         */
        public CDF()
        {
            super(new TreeMap<Double, MutableDouble>(), 0.0);
        }

        /**
         * Copy constructor
         * @param other
         * ScalarDataDistribution to copy
         */
        public CDF(
            ScalarDataDistribution other)
        {
            this();
            this.incrementAll(other);
        }

        /**
         * Creates a new instance of PMF
         * @param data
         * Data used to create the PMF
         */
        public CDF(
            final Iterable<? extends Number> data )
        {
            this();
            for( Number value : data )
            {
                this.increment(value.doubleValue());
            }
        }

        @Override
        public ScalarDataDistribution.CDF clone()
        {
            // The copy constructor is more appropriate because
            // super.clone will attempt to create a new LinkedHashMap
            // instead of a TreeMap
            ScalarDataDistribution.CDF clone =
                new ScalarDataDistribution.CDF( this );
            return clone;
        }

        @Override
        public Double getMinSupport()
        {
            return ((TreeMap<Double, MutableDouble>) this.map).firstKey();
        }

        @Override
        public Double getMaxSupport()
        {
            return ((TreeMap<Double, MutableDouble>) this.map).lastKey();
        }

        @Override
        public ScalarDataDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public Double evaluate(
            Double input)
        {
            final double x0 = input;
            double sum = 0.0;
            for (Entry<Double> entry : this.entrySet())
            {
                final double x = entry.getKey();
                if (x <= x0)
                {
                    sum += entry.getValue();
                }
            }
            final double t = this.getTotal();
            return (t > 0.0) ? (sum / t) : 0.0;
        }

    }

    /**
     * Estimator for a ScalarDataDistribution
     */
    public static class Estimator
        extends AbstractBatchAndIncrementalLearner<Double, ScalarDataDistribution>
        implements DistributionEstimator<Double, ScalarDataDistribution>
    {

        /**
         * Default constructor
         */
        public Estimator()
        {
            super();
        }

        @Override
        public ScalarDataDistribution createInitialLearnedObject()
        {
            return new ScalarDataDistribution();
        }

        @Override
        public void update(
            ScalarDataDistribution target,
            Double data)
        {
            target.increment(data, 1.0);
        }

    }

}
