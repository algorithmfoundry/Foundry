/*
 * File:                NegativeBinomialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 23, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Negative binomial distribution, also known as the Polya distribution,
 * gives the number of successes of a series of Bernoulli trials before
 * recording a given number of failures.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Negative binomial distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Negative_binomial_distribution"
)
public class NegativeBinomialDistribution
    extends AbstractClosedFormUnivariateDistribution<Number>
    implements ClosedFormDiscreteUnivariateDistribution<Number>,
    EstimableDistribution<Number,NegativeBinomialDistribution>
{

    /**
     * Default p, {@value}.
     */
    public static final double DEFAULT_P = 0.5;

    /**
     * Default r, {@value}.
     */
    public static final double DEFAULT_R = 1.0;

    /**
     * Number of trials before the experiment is stopped,
     * must be greater than zero.
     */
    protected double r;

    /**
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    protected double p;

    /** 
     * Creates a new instance of NegativeBinomialDistribution
     */
    public NegativeBinomialDistribution()
    {
        this( DEFAULT_R, DEFAULT_P );
    }

    /**
     * Creates a new instance of NegativeBinomialDistribution
     * @param r
     * Number of trials before the experiment is stopped,
     * must be greater than zero.
     * @param p
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    public NegativeBinomialDistribution(
        final double r,
        final double p)
    {
        this.setR(r);
        this.setP(p);
    }

    /**
     * Copy constructor
     * @param other
     * NegativeBinomialDistribution to copy
     */
    public NegativeBinomialDistribution(
        final NegativeBinomialDistribution other )
    {
        this( other.getR(), other.getP() );
    }

    @Override
    public NegativeBinomialDistribution clone()
    {
        return (NegativeBinomialDistribution) super.clone();
    }

    /**
     * Getter for p
     * @return
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    public double getP()
    {
        return this.p;
    }

    /**
     * Setter for p
     * @param p
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    public void setP(
        final double p )
    {
        ProbabilityUtil.assertIsProbability(p);
        this.p = p;
    }

    /**
     * Getter for r.
     * @return
     * Number of trials before the experiment is stopped,
     * must be greater than zero.
     */
    public double getR()
    {
        return this.r;
    }

    /**
     * Setter for r.
     * @param r
     * Number of trials before the experiment is stopped,
     * must be greater than zero.
     */
    public void setR(
        final double r)
    {
        if( r <= 0.0 )
        {
            throw new IllegalArgumentException( "R must be > 0" );
        }
        this.r = r;
    }

    @Override
    public Double getMean()
    {
        return this.r * this.p / (1.0-this.p);
    }

    @Override
    public ArrayList<? extends Number> sample(
        final Random random,
        final int numSamples)
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    @Override
    public NegativeBinomialDistribution.CDF getCDF()
    {
        return new NegativeBinomialDistribution.CDF( this );
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.r, this.p );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setR( parameters.getElement(0) );
        this.setP( parameters.getElement(1) );
    }

    @Override
    public Integer getMinSupport()
    {
        return 0;
    }

    @Override
    public Integer getMaxSupport()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public double getVariance()
    {
        final double np = 1.0-this.p;
        return this.r * this.p / (np*np);
    }

    @Override
    public IntegerSpan getDomain()
    {
        final int max = (int) Math.ceil( 10 * this.getMean() + 10 );
        return new IntegerSpan(0, max);
    }

    @Override
    public int getDomainSize()
    {
        return this.getDomain().size();
    }

    @Override
    public NegativeBinomialDistribution.PMF getProbabilityFunction()
    {
        return new NegativeBinomialDistribution.PMF( this );
    }

    @Override
    public NegativeBinomialDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new NegativeBinomialDistribution.MaximumLikelihoodEstimator();
    }

    @Override
    public String toString()
    {
        System.out.println( "r = " + this.getR() + ", p = " + this.getP() );
        return super.toString();
    }

    /**
     * PMF of the NegativeBinomialDistribution.
     */
    public static class PMF
        extends NegativeBinomialDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Creates a new instance of PMF
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of PMF
         * @param r
         * Number of trials before the experiment is stopped,
         * must be greater than zero.
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public PMF(
            final double r,
            final double p)
        {
            super( r, p );
        }

        /**
         * Copy constructor
         * @param other
         * NegativeBinomialDistribution to copy
         */
        public PMF(
            final NegativeBinomialDistribution other )
        {
            super( other );
        }

        @Override
        public NegativeBinomialDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        @Override
        public double logEvaluate(
            final Number input)
        {
            final int k = input.intValue();
            if( k < 0 )
            {
                return Math.log(0.0);
            }
            else
            {
                double logSum = 0.0;
                logSum += MathUtil.logGammaFunction(k+this.r);
                logSum -= MathUtil.logFactorial(k);
                logSum -= MathUtil.logGammaFunction(this.r);
                logSum += this.r * Math.log(1.0-this.p);
                logSum += k * Math.log(this.p);
                return logSum;
            }
        }

        @Override
        public Double evaluate(
            final Number input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

    }

    /**
     * CDF of the NegativeBinomialDistribution
     */
    public static class CDF
        extends NegativeBinomialDistribution
        implements ClosedFormCumulativeDistributionFunction<Number>
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
         * @param r
         * Number of trials before the experiment is stopped,
         * must be greater than zero.
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public CDF(
            final double r,
            final double p)
        {
            super( r, p );
        }

        /**
         * Copy constructor
         * @param other
         * NegativeBinomialDistribution to copy
         */
        public CDF(
            final NegativeBinomialDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Number input)
        {
            final int k = input.intValue();
            if( k < 0 )
            {
                return 0.0;
            }
            else
            {
                return MathUtil.regularizedIncompleteBetaFunction(
                    this.r, k+1, 1.0-p );
            }
        }

        @Override
        public NegativeBinomialDistribution.CDF getCDF()
        {
            return this;
        }

    }

    /**
     * Maximum likelihood estimator of the distribution
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Number,NegativeBinomialDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
        }

        @Override
        public NegativeBinomialDistribution.PMF learn(
            final Collection<? extends Number> data )
        {

            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            double mean = pair.getFirst();
            double variance = pair.getSecond();
            double ratio = mean/variance;
            double r = Math.abs(mean * ratio / (ratio-1.0));
            double p = mean / (mean + r);
            return new NegativeBinomialDistribution.PMF(r, p);
        }

    }

    /**
     * Weighted maximum likelihood estimator of the distribution
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Number,NegativeBinomialDistribution>
    {

        /**
         * Default constructor
         */
        public WeightedMaximumLikelihoodEstimator()
        {
        }

        @Override
        public NegativeBinomialDistribution learn(
            final Collection<? extends WeightedValue<? extends Number>> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
            double mean = pair.getFirst();
            double variance = pair.getSecond();
            double ratio = mean/variance;
            double r = Math.abs(mean * ratio / (ratio-1.0));
            double p = mean / (mean + r);
            return new NegativeBinomialDistribution.PMF(r, p);
        }
    }


}
