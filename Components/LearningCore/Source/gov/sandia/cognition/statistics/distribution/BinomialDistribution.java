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
import gov.sandia.cognition.collection.IntegerCollection;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormScalarCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

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
    extends AbstractClosedFormScalarDistribution<Number>
    implements ClosedFormDiscreteScalarDistribution<Number>
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
        int N,
        double p )
    {
        this.setN( N );
        this.setP( p );
    }

    /**
     * Copy constructor
     * @param other BinomialDistribution to copy
     */
    public BinomialDistribution(
        BinomialDistribution other  )
    {
        this( other.getN(), other.getP() );
    }

    @Override
    public BinomialDistribution clone()
    {
        return (BinomialDistribution) super.clone();
    }
    
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
    public double getVariance()
    {
        return this.N*this.p*(1.0-this.p);
    }
    
    public ArrayList<Number> sample(
        Random random,
        int numSamples )
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getN(), this.getP() );
    }

    public void convertFromVector(
        Vector parameters )
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
        int N )
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
        double p )
    {
        ProbabilityUtil.assertIsProbability(p);
        this.p = p;
    }

    public Collection<Integer> getDomain()
    {
        return new IntegerCollection( 0, this.getN() );
    }

    public BinomialDistribution.CDF getCDF()
    {
        return new BinomialDistribution.CDF( this );
    }

    public BinomialDistribution.PMF getProbabilityFunction()
    {
        return new BinomialDistribution.PMF( this );
    }

    public Integer getMinSupport()
    {
        return 0;
    }

    public Integer getMaxSupport()
    {
        return this.getN();
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
            int N,
            double p )
        {
            super( N, p );
        }

        /**
         * Copy constructor
         * @param other BinomialDistribution to copy
         */
        public PMF(
            BinomialDistribution other )
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
        public Double evaluate(
            Number input )
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
            int N,
            int k,
            double p )
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

        public double logEvaluate(
            Number input)
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
            int N,
            int k,
            double p )
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
         * Creates a new instance of BinomialDistribution
         * @param N
         * Total number of experiments, must be greater than zero
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public CDF(
            int N,
            double p )
        {
            super( N, p );
        }
        
        /**
         * Creates a new instance of CDF
         * @param other Underlying Binomial PMF to use
         */
        public CDF(
            BinomialDistribution other  )
        {
            super( other );
        }

        public Double evaluate(
            Number input )
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
            int N,
            int k,
            double p )
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

}
