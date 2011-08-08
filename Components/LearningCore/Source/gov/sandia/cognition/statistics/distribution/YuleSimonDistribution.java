/*
 * File:                YuleSimonDistribution.java
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
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormScalarCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Yule-Simon distribution is a model of preferential attachment, such as
 * a model of the number of groups follows a power-law distribution
 * (Zipf's Law).  That is, the PMF of a Yule-Simon distribution is a straight
 * line on a semilogy plot, with the negative slope of the line determined
 * by the "shape" of the distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Yule-Simon Distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Yule%E2%80%93Simon_distribution"
)
public class YuleSimonDistribution 
    extends AbstractClosedFormScalarDistribution<Number>
    implements ClosedFormDiscreteScalarDistribution<Number>
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 3.0;

    /**
     * Shape parameter, must be greater than zero
     */
    protected double shape;

    /** 
     * Creates a new instance of YuleSimonDistribution 
     */
    public YuleSimonDistribution()
    {
        this( DEFAULT_SHAPE );
    }

    /**
     * Creates a new instance of YuleSimonDistribution
     * @param shape
     * Shape parameter, must be greater than zero
     */
    public YuleSimonDistribution(
        double shape)
    {
        this.setShape(shape);
    }

    /**
     * Copy constructor
     * @param other
     * YuleSimonDistribution to copy
     */
    public YuleSimonDistribution(
        YuleSimonDistribution other )
    {
        this( other.getShape() );
    }

    @Override
    public YuleSimonDistribution clone()
    {
        return (YuleSimonDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape parameter, must be greater than zero
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape parameter, must be greater than zero
     */
    public void setShape(
        double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException( "Shape must be > 0.0" );
        }
        this.shape = shape;
    }

    public Double getMean()
    {
        return this.shape / (this.shape-1.0);
    }

    public double getVariance()
    {
        if( this.shape > 2.0 )
        {
            final double mean = this.getMean();
            return mean*mean / (this.shape-2.0);
        }
        else
        {
            return 0.0;
        }
    }

    public ArrayList<Integer> sample(
        Random random,
        int numSamples)
    {
        ArrayList<Integer> samples = new ArrayList<Integer>( numSamples );
        final double negativeInverseScale = -1.0/this.shape;
        for( int n = 0; n < numSamples; n++ )
        {
            // First, generate exponential( shape )
            final double u = random.nextDouble();
            final double e = Math.log(u) * negativeInverseScale;

            // Next, generate geometric( Math.exp(-e) )
            final double geo = Math.exp( -e );
            final double denom = Math.log( 1.0 - geo );
            final double lnu = Math.log( random.nextDouble() );
            samples.add( (int) Math.floor( lnu / denom ) + 1 );
        }
        return samples;
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getShape() );
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(1);
        this.setShape( parameters.getElement(0) );
    }

    public Integer getMinSupport()
    {
        return 1;
    }

    public Number getMaxSupport()
    {
        return Integer.MAX_VALUE;
    }

    public IntegerCollection getDomain()
    {
        double std = Math.sqrt( this.getVariance() );
        final int max = (int) Math.ceil(10.0*std + 100.0);
        return new IntegerCollection( this.getMinSupport(), max );
    }

    public YuleSimonDistribution.PMF getProbabilityFunction()
    {
        return new YuleSimonDistribution.PMF( this );
    }
    
    public YuleSimonDistribution.CDF getCDF()
    {
        return new YuleSimonDistribution.CDF( this );
    }

    /**
     * PMF of the Yule-Simon Distribution
     */
    public static class PMF
        extends YuleSimonDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Creates a new instance of YuleSimonDistribution
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of YuleSimonDistribution
         * @param shape
         * Shape parameter, must be greater than zero
         */
        public PMF(
            double shape)
        {
            super( shape );
        }

        /**
         * Copy constructor
         * @param other
         * YuleSimonDistribution to copy
         */
        public PMF(
            YuleSimonDistribution other )
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
            if( k < 1 )
            {
                return Math.log(0.0);
            }
            
            double logSum = 0.0;
            logSum += Math.log( this.shape );
            logSum += MathUtil.logBetaFunction(k, this.shape + 1.0 );
            return logSum;
        }

        public Double evaluate(
            Number input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public YuleSimonDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the Yule-Simon Distribution
     */
    public static class CDF
        extends YuleSimonDistribution
        implements ClosedFormScalarCumulativeDistributionFunction<Number>
    {

        /**
         * Creates a new instance of YuleSimonDistribution
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of YuleSimonDistribution
         * @param shape
         * Shape parameter, must be greater than zero
         */
        public CDF(
            double shape)
        {
            super( shape );
        }

        /**
         * Copy constructor
         * @param other
         * YuleSimonDistribution to copy
         */
        public CDF(
            YuleSimonDistribution other )
        {
            super( other );
        }

        public Double evaluate(
            Number input)
        {
            final int k = input.intValue();
            if( k < 1 )
            {
                return 0.0;
            }
            final double bc = Math.exp( MathUtil.logBetaFunction(k, this.shape+1.0) );
            return 1.0 - k*bc;
        }

        @Override
        public YuleSimonDistribution.CDF getCDF()
        {
            return this;
        }

    }


}
