/*
 * File:                GeometricDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 30, 2010, Sandia Corporation.
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
import gov.sandia.cognition.math.ProbabilityUtil;
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
 * The geometric distribution models the number of successes before the first
 * failure occurs under an independent succession of Bernoulli tests.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Mathworks",
    title="Geometric Distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://www.mathworks.com/access/helpdesk/help/toolbox/stats/brn2ivz-58.html"
)
public class GeometricDistribution 
    extends AbstractClosedFormScalarDistribution<Number>
    implements ClosedFormDiscreteScalarDistribution<Number>,
    EstimableDistribution<Number,GeometricDistribution>
{

    /**
     * Default p, {@value}.
     */
    public static final double DEFAULT_P = 0.5;

    /**
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    protected double p;

    /** 
     * Creates a new instance of GeometricDistribution 
     */
    public GeometricDistribution()
    {
        this( DEFAULT_P );
    }

    /**
     * Creates a new instance of GeometricDistribution
     * @param p
     * Probability of a positive outcome (Bernoulli probability), [0,1]
     */
    public GeometricDistribution(
        double p)
    {
        this.setP(p);
    }

    /**
     * Copy constructor
     * @param other
     * GeometricDistribution to copy
     */
    public GeometricDistribution(
        GeometricDistribution other )
    {
        this( other.getP() );
    }

    @Override
    public GeometricDistribution clone()
    {
        return (GeometricDistribution) super.clone();
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

    public Double getMean()
    {
        return (1.0-this.p)/this.p;
    }

    public double getVariance()
    {
        return (1.0-this.p) / (this.p*this.p);
    }

    public ArrayList<Integer> sample(
        Random random,
        int numSamples)
    {
        final double denom = Math.log( 1.0 - this.p );
        ArrayList<Integer> samples = new ArrayList<Integer>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            final double lnu = Math.log( random.nextDouble() );
            samples.add( (int) Math.floor( lnu / denom ) );
        }
        return samples;
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(this.getP());
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(1);
        this.setP( parameters.getElement(0) );
    }

    public Integer getMinSupport()
    {
        return 0;
    }

    public Integer getMaxSupport()
    {
        return Integer.MAX_VALUE;
    }

    public IntegerCollection getDomain()
    {
        final double std = Math.sqrt( this.getVariance() );
        final int max = (int) Math.ceil( std*10.0 + 10.0 );
        return new IntegerCollection( this.getMinSupport(), max);
    }

    @Override
    public int getDomainSize()
    {
        return getDomain().size();
    }

    public GeometricDistribution.CDF getCDF()
    {
        return new GeometricDistribution.CDF( this );
    }

    public GeometricDistribution.PMF getProbabilityFunction()
    {
        return new GeometricDistribution.PMF( this );
    }

    public GeometricDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new GeometricDistribution.MaximumLikelihoodEstimator();
    }

    /**
     * PMF of the Geometric distribution
     */
    public static class PMF
        extends GeometricDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Creates a new instance of GeometricDistribution
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of GeometricDistribution
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public PMF(
            double p)
        {
            super( p );
        }

        /**
         * Copy constructor
         * @param other
         * GeometricDistribution to copy
         */
        public PMF(
            GeometricDistribution other )
        {
            super( other );
        }

        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        public double logEvaluate(
            Number input)
        {
            final int k = input.intValue();
            if( k < 0 )
            {
                return Math.log(0.0);
            }
            return k * Math.log( 1.0-this.p ) + Math.log( this.p );
        }

        public Double evaluate(
            Number input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public GeometricDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the Geometric distribution
     */
    public static class CDF
        extends GeometricDistribution
        implements ClosedFormScalarCumulativeDistributionFunction<Number>
    {

        /**
         * Creates a new instance of GeometricDistribution
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of GeometricDistribution
         * @param p
         * Probability of a positive outcome (Bernoulli probability), [0,1]
         */
        public CDF(
            double p)
        {
            super( p );
        }

        /**
         * Copy constructor
         * @param other
         * GeometricDistribution to copy
         */
        public CDF(
            GeometricDistribution other )
        {
            super( other );
        }

        public Double evaluate(
            Number input)
        {
            final int k = input.intValue();
            if( k < 0 )
            {
                return 0.0;
            }
            else
            {
                return 1.0 - Math.pow(1.0-this.p, k+1);
            }
        }

        @Override
        public GeometricDistribution.CDF getCDF()
        {
            return this;
        }

    }

    /**
     * Maximum likelihood estimator of the distribution
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Number,GeometricDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
        }

        public GeometricDistribution.PMF learn(
            Collection<? extends Number> data )
        {

            double mean = UnivariateStatisticsUtil.computeMean(data);
            double p = 1.0/(1+mean);
            return new GeometricDistribution.PMF( p );
        }

    }

}
