/*
 * File:                BernoulliDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 5, 2009, Sandia Corporation.
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
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * A Bernoulli distribution, which takes a value of "1" with probability "p"
 * and value of "0" with probability "1-p".  This is also known as flipping
 * a weighted coin.  A sum of Bernoulli random variables is a Binomial
 * distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Bernoulli distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Bernoulli_distribution"
)
public class BernoulliDistribution 
    extends AbstractClosedFormUnivariateDistribution<Number>
    implements ClosedFormDiscreteUnivariateDistribution<Number>
{

    /**
     * Default Bernoulli parameter, {@value}
     */
    public static final double DEFAULT_P = 0.5;
    
    /**
     * Bernoulli parameter, where the distribution takes value "1" with
     * probability "p" and value "0" with probability 1-p.
     */
    private double p;
    
    /** 
     * Creates a new instance of BernoulliDistribution 
     */
    public BernoulliDistribution()
    {
        this( DEFAULT_P );
    }

    /**
     * Creates a new instance of BernoulliDistribution
     * @param p
     * Bernoulli parameter, where the distribution takes value "1" with
     * probability "p" and value "0" with probability 1-p.
     */
    public BernoulliDistribution(
        final double p )
    {
        this.setP( p );
    }
    
    /**
     * Copy Constructor
     * @param other BernoulliDistribution to copy
     */
    public BernoulliDistribution(
        final BernoulliDistribution other )
    {
        this( other.getP() );
    }

    @Override
    public Double getMean()
    {
        return this.getP();
    }

    @Override
    public Integer getMinSupport()
    {
        return 0;
    }

    @Override
    public Integer getMaxSupport()
    {
        return 1;
    }

    @Override
    public ArrayList<Integer> sample(
        final Random random,
        final int numSamples )
    {
        
        ArrayList<Integer> samples = new ArrayList<Integer>( numSamples );
        for( int i = 0; i < numSamples; i++ )
        {
            double x = random.nextDouble();
            if( x < this.p )
            {
                samples.add( 1 );
            }
            else
            {
                samples.add( 0 );
            }
        }
        
        return samples;
        
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getP() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if( parameters.getDimensionality() != 1 )
        {
            throw new IllegalArgumentException(
                "parameter dimension must be 1" );
        }
        this.setP( parameters.getElement(0) );
    }

    @Override
    public double getVariance()
    {
        return this.getP() * (1.0-this.getP());
    }

    @Override
    public IntegerSpan getDomain()
    {
        return new IntegerSpan(0, 1);
    }

    @Override
    public int getDomainSize()
    {
        return 2;
    }

    /**
     * Getter for p
     * @return
     * Bernoulli parameter, where the distribution takes value "1" with
     * probability "p" and value "0" with probability 1-p.
     */
    public double getP()
    {
        return this.p;
    }

    /**
     * Setter for p
     * @param p
     * Bernoulli parameter, where the distribution takes value "1" with
     * probability "p" and value "0" with probability 1-p.
     */
    public void setP(
        final double p )
    {
        ProbabilityUtil.assertIsProbability(p);
        this.p = p;
    }

    @Override
    public BernoulliDistribution.CDF getCDF()
    {
        return new CDF( this );
    }

    @Override
    public BernoulliDistribution.PMF getProbabilityFunction()
    {
        return new PMF( this );
    }

    @Override
    public String toString()
    {
        return "p = " + this.getP();
    }

    /**
     * PMF of the Bernoulli distribution.
     */
    public static class PMF
        extends BernoulliDistribution
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
         * @param p
         * Bernoulli parameter, where the distribution takes value "1" with
         * probability "p" and value "0" with probability 1-p.
         */
        public PMF(
            final double p )
        {
            super( p );
        }
        
        /**
         * Copy constructor
         * @param other
         * BernoulliDistribution to copy
         */
        public PMF(
            final BernoulliDistribution other )
        {
            super( other );
        }
        
        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy( this );
        }

        @Override
        public Double evaluate(
            final Number input )
        {
            if( input.intValue() == 0 )
            {
                return 1.0-this.getP();
            }
            else if( input.intValue() == 1 )
            {
                return this.getP();
            }
            else
            {
                return 0.0;
            }
        }

        @Override
        public double logEvaluate(
            final Number input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public BernoulliDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * CDF of a Bernoulli distribution.
     */
    public static class CDF
        extends BernoulliDistribution
        implements ClosedFormCumulativeDistributionFunction<Number>
    {

        /**
         * Default constructor
         */
        public CDF()
        {
            super();
        }
        
        /**
         * Creates a new instance of PMF
         * @param p
         * Bernoulli parameter, where the distribution takes value "1" with
         * probability "p" and value "0" with probability 1-p.
         */
        public CDF(
            final double p )
        {
            super( p );
        }
        
        /**
         * Copy constructor
         * @param other
         * BernoulliDistribution to copy
         */
        public CDF(
            final BernoulliDistribution other )
        {
            super( other );
        }        
        
        @Override
        public Double evaluate(
            final Number input )
        {
            if( input.intValue() < 0 )
            {
                return 0.0;
            }
            else if( input.intValue() <= 0 )
            {
                return 1.0-this.getP();
            }
            else
            {
                return 1.0;
            }
            
        }

        @Override
        public BernoulliDistribution.CDF getCDF()
        {
            return this;
        }

    }
    
}
