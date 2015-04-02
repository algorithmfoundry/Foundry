/*
 * File:                UnivariateRandomVariable.java
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

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.distribution.ScalarDataDistribution;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * This is an implementation of a RandomVariable for scalar distributions.  The
 * primary method by which random-variable algebra is conducted is by
 * empirically sampling the distributions, performing the algebra on the
 * samples, and then fitting an empirical distribution to the resulting
 * samples.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Algebra of random variables",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Algebra_of_random_variables"
)
public class UnivariateRandomVariable
    extends AbstractRandomVariable<Number>
    implements UnivariateDistribution<Number>
{

    /**
     * Default number of samples to draw from a distribution to perform the
     * empirical algebra approximation, {@value}.
     */
    public static final int DEFAULT_NUM_SAMPLES = 10000;
    
    /**
     * Number of samples to draw from the distribution to perform the
     * empirical algebra approximation.
     */
    private int numSamples;
    
    /**
     * Random number generator used for sampling the distribution.
     */
    private Random random;
    
    /**
     * Scalar distribution that backs the random variable, from which samples
     * will be drawn to approximate the distribution during algebra.
     */
    private UnivariateDistribution<? extends Number> distribution;
    
    /** 
     * Creates a new instance of UnivariateRandomVariable
     * @param distribution 
     * Scalar distribution that backs the random variable, from which samples
     * will be drawn to approximate the distribution during algebra.
     * @param random
     * Random number generator used to sample the distribution
     */
    public UnivariateRandomVariable(
        final UnivariateDistribution<? extends Number> distribution,
        final Random random )
    {
        this( distribution, random, DEFAULT_NUM_SAMPLES );
    }

    /** 
     * Creates a new instance of UnivariateRandomVariable
     * @param distribution 
     * Scalar distribution that backs the random variable, from which samples
     * will be drawn to approximate the distribution during algebra.
     * @param random
     * Random number generator used to sample the distribution
     * @param numSamples 
     */
    public UnivariateRandomVariable(
        final UnivariateDistribution<? extends Number> distribution,
        final Random random,
        final int numSamples )
    {
        this.setDistribution( distribution );
        this.setRandom( random );
        this.setNumSamples( numSamples );
    }

    @Override
    public UnivariateRandomVariable clone()
    {
        return (UnivariateRandomVariable) super.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(
        final Object obj )
    {
        if( obj == null )
        {
            return false;
        }
        else if( obj == this )
        {
            return true;
        }
        else if( obj instanceof RandomVariable )
        {
            return this.equals( (RandomVariable<Number>) obj, 0.95 );
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public boolean equals(
        final RandomVariable<Number> other,
        final double effectiveZero )
    {
        // effectiveZero=0.0 -> null hypothesis probability = 1.0
        // effectiveZero=1.0 -> null hypothesis probability >= 0.0
        // effectiveZero=0.95 -> null hypothesis probability >= 0.05
        double pValue = 1.0-effectiveZero;
        
        ArrayList<Number> data1 = this.sample( this.random, this.numSamples );
        ArrayList<? extends Number> data2 =
            other.sample( this.random, this.numSamples );
        
        KolmogorovSmirnovConfidence kstest = new KolmogorovSmirnovConfidence();
        KolmogorovSmirnovConfidence.Statistic stat =
            kstest.evaluateNullHypothesis( data1, data2 );
        return stat.getNullHypothesisProbability() >= pValue;
    }

    /**
     * Warning: The hashCode of this class is just the default hashCode,
     * but equality uses a statistical test to see if two random variables
     * are equal.
     * 
     * @return @inheritDoc
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public void plusEquals(
        final RandomVariable<Number> other )
    {
        ArrayList<Number> X = this.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<? extends Number> Y =
            other.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<Number> Z = new ArrayList<Number>( this.getNumSamples() );
        for( int n = 0; n < this.getNumSamples(); n++ )
        {
            Z.add( X.get(n).doubleValue() + Y.get(n).doubleValue() );
        }
        
        this.setDistribution( new ScalarDataDistribution.PMF( Z ) );
    }

    @Override
    public void minusEquals(
        final RandomVariable<Number> other )
    {
        ArrayList<Number> X = this.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<? extends Number> Y =
            other.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<Number> Z = new ArrayList<Number>( this.getNumSamples() );
        for( int n = 0; n < this.getNumSamples(); n++ )
        {
            Z.add( X.get(n).doubleValue() - Y.get(n).doubleValue() );
        }
        
        this.setDistribution( new ScalarDataDistribution.PMF( Z ) );
    }

    @Override
    public void dotTimesEquals(
        final RandomVariable<Number> other )
    {
        ArrayList<Number> X = this.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<? extends Number> Y =
            other.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<Number> Z = new ArrayList<Number>( this.getNumSamples() );
        for( int n = 0; n < this.getNumSamples(); n++ )
        {
            Z.add( X.get(n).doubleValue() * Y.get(n).doubleValue() );
        }
        
        this.setDistribution( new ScalarDataDistribution.PMF( Z ) );
    }

    @Override
    public void scaleEquals(
        final double scaleFactor )
    {
        ArrayList<Number> X = this.sample( this.getRandom(), this.getNumSamples() );
        ArrayList<Number> Z = new ArrayList<Number>( this.getNumSamples() );
        for( int n = 0; n < this.getNumSamples(); n++ )
        {
            Z.add( X.get(n).doubleValue() * scaleFactor );
        }
        
        this.setDistribution( new ScalarDataDistribution.PMF( Z ) );
    }

    @Override
    public void scaledPlusEquals(
        final double scaleFactor,
        final RandomVariable<Number> other)
    {
        this.plusEquals(other.scale(scaleFactor));
    }

    @Override
    public boolean isZero(
        final double effectiveZero)
    {
        // TODO: Optimize this method to work faster.
        final UnivariateRandomVariable zero = this.clone();
        zero.zero();
        return this.equals(zero, effectiveZero);
    }

    @Override
    public Number sample(
        final Random random)
    {
        return this.getDistribution().sample(random);
    }

    @Override
    public ArrayList<Number> sample(
        final Random random,
        final int numSamples)
    {
        return new ArrayList<Number>(this.getDistribution().sample(random, numSamples));
    }
    
    @Override
    public void sampleInto(
        final Random random,
        final int sampleCount,
        final Collection<? super Number> output)
    {
        this.getDistribution().sampleInto(random, sampleCount, output);
    }

    /**
     * Getter for numSamples
     * @return
     * Number of samples to draw from the distribution to perform the
     * empirical algebra approximation.
     */
    public int getNumSamples()
    {
        return this.numSamples;
    }

    /**
     * Setter for numSamples
     * @param numSamples
     * Number of samples to draw from the distribution to perform the
     * empirical algebra approximation.
     */
    public void setNumSamples(
        final int numSamples )
    {
        if( numSamples <= 0 )
        {
            throw new IllegalArgumentException(
                "numSamples must be >= 1" );
        }
        this.numSamples = numSamples;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
    public void setRandom(
        final Random random )
    {
        this.random = random;
    }

    /**
     * Getter for distribution
     * @return
     * Scalar distribution that backs the random variable, from which samples
     * will be drawn to approximate the distribution during algebra.
     */
    public UnivariateDistribution<? extends Number> getDistribution()
    {
        return this.distribution;
    }

    /**
     * Setter for distribution
     * @param distribution
     * Scalar distribution that backs the random variable, from which samples
     * will be drawn to approximate the distribution during algebra.
     */
    public void setDistribution(
        final UnivariateDistribution<? extends Number> distribution )
    {
        this.distribution = distribution;
    }

    @Override
    public Number getMean()
    {
        return this.getDistribution().getMean();
    }

    @Override
    public double getMeanAsDouble()
    {
        return this.getDistribution().getMeanAsDouble();
    }
    
    @Override
    public double getVariance()
    {
        return this.getDistribution().getVariance();
    }

    @Override
    public Double getMinSupport()
    {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Gets the 95% confidence interval estimated sampling error associated 
     * with this empirical random variable.
     * @param confidence 
     * Confidence required.  If you want 95% confidence, which is the standard
     * in social science, then use 0.95.
     * @return
     * Estimated sampling error associated with this empirical random variable.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Standard error (statistics)",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Standard_error_(statistics)"
    )
    public ConfidenceInterval getSamplingError(
        final double confidence )
    {
        return GaussianConfidence.computeConfidenceInterval( this, numSamples, confidence );
    }

    @SuppressWarnings("unchecked")
    @Override
    public CumulativeDistributionFunction<Number> getCDF()
    {
        return (CumulativeDistributionFunction<Number>) this.getDistribution().getCDF();
    }
    
}
