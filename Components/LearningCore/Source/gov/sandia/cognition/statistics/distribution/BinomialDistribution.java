/*
 * File:                BinomialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
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
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * Binomial distribution, which is a collection of Bernoulli trials
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Binomial distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Binomial_distribution"
)
public class BinomialDistribution
    extends AbstractClosedFormUnivariateDistribution<Number>
    implements ClosedFormDiscreteUnivariateDistribution<Number>,
    EstimableDistribution<Number,BinomialDistribution>
{

    /**
     * Default p, {@value}.
     */
    public static final double DEFAULT_P = 0.5;

    /**
     * Default N, {@value}.
     */
    public static final int DEFAULT_N = 1;

    /**
     * Total number of experiments, must be greater than zero
     */
    private int N;

    /**
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    private double p;

    /**
     * Default constructor.
     */
    public BinomialDistribution()
    {
        this( DEFAULT_N, DEFAULT_P );
    }

    /**
     * Creates a new instance of BinomialDistribution
     * @param N
     * Total number of experiments, must be greater than zero
     * @param p
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    public BinomialDistribution(
        final int N,
        final double p )
    {
        this.setN( N );
        this.setP( p );
    }

    /**
     * Copy constructor
     * @param other BinomialDistribution to copy
     */
    public BinomialDistribution(
        final BinomialDistribution other  )
    {
        this( other.getN(), other.getP() );
    }

    @Override
    public BinomialDistribution clone()
    {
        return (BinomialDistribution) super.clone();
    }
    
    @Override
    public Double getMean()
    {
        return this.N * this.p;
    }

    /**
     * Gets the variance of the distribution.  This is sometimes called
     * the second central moment by more pedantic people, which is equivalent
     * to the square of the standard deviation.
     * @return
     * Variance of the distribution.
     */
    @Override
    public double getVariance()
    {
        return this.N*this.p*(1.0-this.p);
    }
    
    @Override
    public ArrayList<Number> sample(
        final Random random,
        final int numSamples )
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getN(), this.getP() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if( parameters.getDimensionality() != 2 )
        {
            throw new IllegalArgumentException(
                "Parameters must have dimension 2" );
        }
        this.setN( (int) parameters.getElement(0) );
        this.setP( parameters.getElement(1) );
    }

    /**
     * Getter for N
     * @return 
     * Total number of experiments, must be greater than zero
     */
    public int getN()
    {
        return this.N;
    }

    /**
     * Setter for N
     * @param N 
     * Total number of experiments, must be greater than zero
     */
    public void setN(
        final int N )
    {
        if (N < 1)
        {
            throw new IllegalArgumentException( "N >= 1" );
        }
        this.N = N;
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

    @Override
    public IntegerSpan getDomain()
    {
        return new IntegerSpan( 0, this.getN() );
    }

    @Override
    public int getDomainSize()
    {
        return this.getN() + 1;
    }

    @Override
    public BinomialDistribution.CDF getCDF()
    {
        return new BinomialDistribution.CDF( this );
    }

    @Override
    public BinomialDistribution.PMF getProbabilityFunction()
    {
        return new BinomialDistribution.PMF( this );
    }

    @Override
    public Integer getMinSupport()
    {
        return 0;
    }

    @Override
    public Integer getMaxSupport()
    {
        return this.getN();
    }

    @Override
    public BinomialDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new BinomialDistribution.MaximumLikelihoodEstimator();
    }

    /**
     * The Probability Mass Function of a binomial distribution.
     */
    public static class PMF
        extends BinomialDistribution
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
         * Creates a new instance of PMF
         * @param N
         * Total number of experiments, must be greater than zero
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public PMF(
            final int N,
            final double p )
        {
            super( N, p );
        }

        /**
         * Copy constructor
         * @param other BinomialDistribution to copy
         */
        public PMF(
            final BinomialDistribution other )
        {
            super( other );
        }
    
        /**
         * Returns the binomial PMF for the parameters N, k, p, which is the
         * probability of exactly k successes in N experiments with expected
         * per-trial success probability (Bernoulli) p
         * @param input
         * Number of successes
         * @return
         * Probability of exactly input successes in N experiments with expected
         * per-trial success probability (Bernoulli) p
         */
        @Override
        public Double evaluate(
            final Number input )
        {
            return evaluate( this.getN(), input.intValue(), this.getP() );
        }

        /**
         * Returns the binomial CDF for the parameters N, k, p, which is the
         * probability of exactly k successes in N experiments with expected
         * per-trial success probability (Bernoulli) p
         * @param N
         * Total number of experiments
         * @param k
         * Total number of successes
         * @param p
         * Expected probability of success, Bernoulli parameter
         * @return
         * Probability of exactly k successes in N experiments with expected
         * per-trial success probability (Bernoulli) p
         */
        public static double evaluate(
            final int N,
            final int k,
            final double p )
        {
            if (k < 0)
            {
                return 0.0;
            }
            else if (k > N)
            {
                return 0.0;
            }
            else
            {
                return Math.exp(logEvaluate(N, k, p));
            }
        }

        @Override
        public double logEvaluate(
            final Number input)
        {
            return logEvaluate( this.getN(), input.intValue(), this.getP() );
        }

        /**
         * Computes the natural logarithm of the PMF.
         * @param N
         * Total number of experiments
         * @param k
         * Total number of successes
         * @param p
         * Expected probability of success, Bernoulli parameter
         * @return
         * Computes the natural logarithm of the PMF.
         */
        public static double logEvaluate(
            final int N,
            final int k,
            final double p )
        {
            if (k < 0)
            {
                return Math.log(0.0);
            }
            else if (k > N)
            {
                return Math.log(0.0);
            }
            else
            {
                double n1 = MathUtil.logBinomialCoefficient(N, k);
                double n2 = (k==0) ? 0.0 : (k * Math.log(p));
                double n3 = ((N-k)==0) ? 0.0 : ((N-k) * Math.log(1.0-p));
                return n1+n2+n3;
            }
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy( this );
        }

        @Override
        public BinomialDistribution.PMF getProbabilityFunction()
        {
            return this;
        }
        
    }    
    
    /**
     * CDF of the Binomial distribution, which is the probability of getting
     * up to "x" successes in "N" trials with a Bernoulli probability of "p"
     */
    public static class CDF
        extends BinomialDistribution
        implements ClosedFormCumulativeDistributionFunction<Number>
    {

        /**
         * Default constructor.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of BinomialDistribution
         * @param N
         * Total number of experiments, must be greater than zero
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public CDF(
            final int N,
            final double p )
        {
            super( N, p );
        }
        
        /**
         * Creates a new instance of CDF
         * @param other Underlying Binomial PMF to use
         */
        public CDF(
            final BinomialDistribution other  )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Number input )
        {
            int k = input.intValue();
            return evaluate( this.getN(), k, this.getP() );
        }

        /**
         * Evaluates the CDF for integer values of x, N, and double p
         * @param k Number of successful trials
         * @param N
         * Total number of possibilities in the distribution
         * @param p 
         * Bernoulli probability of a positive experiment outcome
         * @return 
         * Value of the Binomial CDF(N,n,p)
         */
        public static double evaluate(
            final int N,
            final int k,
            final double p )
        {

            double retval;

            if (k < 0)
            {
                retval = 0.0;
            }
            else if (k >= N)
            {
                retval = 1.0;
            }
            else
            {
                retval = 1.0 - MathUtil.regularizedIncompleteBetaFunction(
                    k + 1, N - k, p );
            }
            
            return retval;

        }

        @Override
        public CDF getCDF()
        {
            return this;
        }

    }

    /**
     * Maximum likelihood estimator of the distribution
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Number,BinomialDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
        }

        @Override
        public BinomialDistribution.PMF learn(
            final Collection<? extends Number> data )
        {
            double r = UnivariateStatisticsUtil.computeMaximum(data);
//            int N = (int) Math.ceil( r );
            int N = (int) Math.ceil( r + 1.0/data.size() );
            double psum = 0.0;
            for( Number value : data )
            {
                double p = value.doubleValue() / N;
                psum += p;
            }
            double phat = psum/data.size();
            return new BinomialDistribution.PMF( N, phat );
        }

    }


}
