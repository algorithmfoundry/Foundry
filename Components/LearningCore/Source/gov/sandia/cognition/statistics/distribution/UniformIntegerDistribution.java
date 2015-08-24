/*
 * File:            UniformIntegerDistribution.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.LogMath;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormIntegerDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * Contains the (very simple) definition of a continuous Uniform distribution,
 * parameterized between the minimum and maximum bounds. The uniform 
 * distribution has equal mass on all of the integers between the two bounds,
 * and including them. For example, if the bounds were (10, 14) then the values
 * 10, 11, 12, 13, and 14 would all have probability 1/5 of being sampled.
 * 
 * @author  Justin Basilico
 * @since   3.4.3
 */
@PublicationReference(
    author="Wikipedia",
    title="Uniform distribution (discrete)",
    type=PublicationType.WebPage,
    year=2015,
    url="https://en.wikipedia.org/wiki/Uniform_distribution_(discrete)"
)
public class UniformIntegerDistribution
    extends AbstractClosedFormIntegerDistribution
    implements ClosedFormDiscreteUnivariateDistribution<Number>,
        EstimableDistribution<Number, UniformIntegerDistribution>
{

    /** The default minimum support is {@value}. */
    public static final int DEFAULT_MIN_SUPPORT = 0;

    /** The default maximum support is {@value}. */
    public static final int DEFAULT_MAX_SUPPORT = 0;
    
    /** The minimum bound on the distribution (inclusive). */
    private int minSupport;
    
    /** The maximum bound on the distribution (inclusive). */    
    private int maxSupport;
    
    /**
     * Creates a new {@link UniformIntegerDistribution} with default parameters.
     */
    public UniformIntegerDistribution()
    {
        this(DEFAULT_MIN_SUPPORT, DEFAULT_MAX_SUPPORT);
    }

    /**
     * Creates a new {@link UniformIntegerDistribution} with the given bounds.
     * 
     * @param minSupport
     *      The minimum bound on the distribution (inclusive).
     * @param maxSupport 
     *      The maximum bound on the distribution (exclusive).
     */
    public UniformIntegerDistribution(
        final int minSupport,
        final int maxSupport)
    {
        super();
        
        this.setMinSupport(minSupport);
        this.setMaxSupport(maxSupport);
    }
    
    /**
     * Creates a new {@link UniformIntegerDistribution} that is a copy of the
     * given other distribution.
     * 
     * @param other 
     *      The distribution to copy.
     */
    public UniformIntegerDistribution(
        final UniformIntegerDistribution other)
    {
        this(other.getMinSupport(), other.getMaxSupport());
    }

    @Override
    public UniformIntegerDistribution clone()
    {
        return (UniformIntegerDistribution) super.clone();
    }
    
    @Override
    public UniformIntegerDistribution.PMF getProbabilityFunction()
    {
        return new UniformIntegerDistribution.PMF(this);
    }

    @Override
    public UniformIntegerDistribution.CDF getCDF()
    {
        return new UniformIntegerDistribution.CDF(this);
    }

    @Override
    public Number getMean()
    {
        return this.getMeanAsDouble();
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDenseDefault().copyValues(
            this.getMinSupport(), this.getMaxSupport());
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        final int a = (int) parameters.getElement(0);
        final int b = (int) parameters.getElement(1);

        this.setMinSupport(Math.min(a, b));
        this.setMaxSupport(Math.max(a, b));
    }

    @Override
    public Integer getMinSupport()
    {
        return this.minSupport;
    }

    /**
     * Sets the minimum support. It is the smallest value in the uniform
     * distribution, and is inclusive. It should be less than (or equal to) 
     * the maximum support.
     * 
     * @param   minSupport 
     *      The minimum support.
     */
    public void setMinSupport(
        final int minSupport)
    {
        this.minSupport = minSupport;
    }

    @Override
    public Integer getMaxSupport()
    {
        return this.maxSupport;
    }

    /**
     * Sets the maximum support. It is the largest value in the uniform
     * distribution, and is inclusive. It should be greater than (or equal to)
     * the minimum support.
     * 
     * @param   maxSupport 
     *      The maximum support.
     */
    public void setMaxSupport(
        final int maxSupport)
    {
        this.maxSupport = maxSupport;
    }

    @Override
    public double getMeanAsDouble()
    {
        return (this.maxSupport + this.minSupport) / 2.0;
    }

    @Override
    public double getVariance()
    {
        final double difference = this.maxSupport - this.minSupport + 1;
        return (difference * difference - 1) / 12.0;
    }

    @Override
    public Set<? extends Number> getDomain()
    {
        return new IntegerSpan(this.minSupport, this.maxSupport);
    }

    @Override
    public int getDomainSize()
    {
        return (this.maxSupport - this.minSupport + 1);
    }

    @Override
    public int sampleAsInt(
        final Random random)
    {
        return this.minSupport 
            + random.nextInt(this.maxSupport - this.minSupport + 1);
    }
    
    @Override
    public void sampleInto(
        final Random random,
        final int sampleCount,
        final Collection<? super Number> output)
    {
        for (int i = 0; i < sampleCount; i++)
        {
            output.add(this.sampleAsInt(random));
        }
    }

    @Override
    public UniformIntegerDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new MaximumLikelihoodEstimator();
    }
    
    /**
     * Probability mass function of a discrete uniform distribution.
     */
    public static class PMF
        extends UniformIntegerDistribution
        implements ProbabilityMassFunction<Number>
    {
        
        /**
         * Creates a new {@link UniformIntegerDistribution.PMF} with min and
         * max 0.
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new {@link UniformIntegerDistribution.PMF} with the given
         * min and max.
         * 
         * @param   minSupport
         *      The minimum support. Should be less-than-or-equal to the max
         *      support.
         * @param   maxSupport
         *      The maximum support. Should be greater-than-or-equal to the min
         *      support.
         */
        public PMF(
            final int minSupport,
            final int maxSupport)
        {
            super(minSupport, maxSupport);
        }
        
        /**
         * Creates a new {@link UniformIntegerDistribution.PMF} as a copy
         * of the given other uniform distribution.
         * 
         * @param   other
         *      The other distribution.
         */
        public PMF(
            final UniformIntegerDistribution other)
        {
            super(other);
        }

        @Override
        public double getEntropy()
        {
            return MathUtil.log2(this.getDomainSize());
        }

        @Override
        public double logEvaluate(
            final Number input)
        {
            return logEvaluate(input.intValue(), 
                this.getMinSupport(), this.getMaxSupport());
        }

        @Override
        public Double evaluate(
            final Number input)
        {
            return this.evaluateAsDouble(input.intValue());
        }
        
        /**
         * Evaluates the input value for the PMF to compute its mass as a
         * double.
         * 
         * @param   input
         *      The input value.
         * @return 
         *      The probability mass for the input value.
         */
        public double evaluateAsDouble(
            final int input)
        {
            return evaluate(input, this.getMinSupport(), this.getMaxSupport());
        }
        
        /**
         * Evaluates the probability mass function of the discrete uniform
         * distribution. The mass is a uniform value if the input is between
         * the supports (inclusive) and 0 if it is not.
         * 
         * @param   input
         *      The input value.
         * @param   minSupport
         *      The minimum support. Should be less-than-or-equal to the max
         *      support.
         * @param   maxSupport
         *      The maximum support. Should be greater-than-or-equal to the min
         *      support.
         * @return 
         *      The probability mass value for the input.
         */
        public static double evaluate(
            final int input,
            final int minSupport,
            final int maxSupport)
        {
            if (input < minSupport || input > maxSupport)
            {
                // Outside the support range.
                return 0.0;
            }
            else
            {
                // Uniform within support range.
                return 1.0 / (maxSupport - minSupport + 1);
            }
        }
        
        /**
         * Evaluates the log of the probability mass function of the discrete 
         * uniform distribution. The mass is a uniform value if the input is 
         * between the supports (inclusive) and 0 if it is not.
         * 
         * @param   input
         *      The input value.
         * @param   minSupport
         *      The minimum support. Should be less-than-or-equal to the max
         *      support.
         * @param   maxSupport
         *      The maximum support. Should be greater-than-or-equal to the min
         *      support.
         * @return 
         *      The log of the probability mass value for the input.
         */
        public static double logEvaluate(
            final int input,
            final int minSupport,
            final int maxSupport)
        {
            if (input < minSupport || input > maxSupport)
            {
                return LogMath.LOG_0;
            }
            else
            {
                return -Math.log(maxSupport - minSupport + 1);
            }
        }

        @Override
        public PMF getProbabilityFunction()
        {
            return this;
        }
        
    }
    
    /**
     * Implements the cumulative distribution function for the discrete
     * uniform distribution.
     */
    public static class CDF
        extends UniformIntegerDistribution
        implements ClosedFormCumulativeDistributionFunction<Number>
    {

        /**
         * Creates a new {@link UniformIntegerDistribution.PMF} with min and
         * max 0.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new {@link UniformIntegerDistribution.CDF} with the given
         * min and max.
         * 
         * @param   minSupport
         *      The minimum support. Should be less-than-or-equal to the max
         *      support.
         * @param   maxSupport
         *      The maximum support. Should be greater-than-or-equal to the min
         *      support.
         */
        public CDF(
            final int minSupport,
            final int maxSupport)
        {
            super(minSupport, maxSupport);
        }
        
        /**
         * Creates a new {@link UniformIntegerDistribution.CDF} as a copy
         * of the given other uniform distribution.
         * 
         * @param   other
         *      The other distribution.
         */
        public CDF(
            final UniformIntegerDistribution other)
        {
            super(other);
        }
        
        @Override
        public Double evaluate(
            final Number input)
        {
            return this.evaluateAsDouble(input.intValue());
        }
        
        /**
         * Evaluates the cumulative distribution function for the input.
         * 
         * @param   input
         *      The input value.
         * @return 
         *      The cumulative distribution value at the input.
         */
        public double evaluateAsDouble(
            final int input)
        {
            return evaluate(input, this.getMinSupport(), this.getMaxSupport());
        }
        
        /**
         * Evaluates the cumulative density function of the discrete uniform
         * distribution. 
         * 
         * @param   input
         *      The input value.
         * @param   minSupport
         *      The minimum support. Should be less-than-or-equal to the max
         *      support.
         * @param   maxSupport
         *      The maximum support. Should be greater-than-or-equal to the min
         *      support.
         * @return 
         *      The cumulative density value for the input.
         */
        public static double evaluate(
            final int input,
            final int minSupport,
            final int maxSupport)
        {
            final double p;
            if (input < minSupport)
            {
                // Before the support range.
                p = 0.0;
            }
            else if (input > maxSupport)
            {
                // After the support range.
                p = 1.0;
            }
            else
            {
                // Within the support range.
                p = (input - minSupport + 1.0) / (maxSupport - minSupport + 1.0);
            }

            return p;
        }
        
        @Override
        public UniformIntegerDistribution.CDF getCDF()
        {
            return this;
        }
    }
    
    /**
     * Implements a maximum likelihood estimator for the discrete uniform
     * distribution.
     */
    @PublicationReference(
        author="Wikipedia",
        title="German tank problem",
        type=PublicationType.WebPage,
        year=2010,
        url="http://en.wikipedia.org/wiki/German_tank_problem"
    )
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Number, UniformIntegerDistribution>
    {

        /**
         * Creates a new {@link UniformIntegerDistribution.MaximumLikelihoodEstimator}.
         */
        public MaximumLikelihoodEstimator()
        {
            super();
        }

        @Override
        public UniformIntegerDistribution.PMF learn(
            final Collection<? extends Number> data)
        {
            final Pair<Double,Double> result = 
                UnivariateStatisticsUtil.computeMinAndMax(data);
            final int min = result.getFirst().intValue();
            final int max = result.getSecond().intValue();
            
            return new UniformIntegerDistribution.PMF(min, max);
        }
        
    }
    
}
