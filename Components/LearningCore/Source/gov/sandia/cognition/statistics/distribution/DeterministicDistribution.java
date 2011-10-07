/*
 * File:                DeterministicDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

/**
 * A deterministic distribution that returns samples at a single point.  This
 * is also known as a degenerate distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Degenerate distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Degenerate_distribution"
)
public class DeterministicDistribution
    extends AbstractClosedFormUnivariateDistribution<Double>
    implements ClosedFormDiscreteUnivariateDistribution<Double>
{

    /**
     * Default point, {@value}
     */
    public static final double DEFAULT_POINT = 0.0;
    
    /**
     * Location of the distribution
     */
    private double point;
    
    /** 
     * Creates a new instance of DeterministicDistribution 
     */
    public DeterministicDistribution()
    {
        this( DEFAULT_POINT );
    }

    /**
     * Creates a new instance of DeterministicDistribution
     * @param point
     * Location of the distribution
     */
    public DeterministicDistribution(
        final double point )
    {
        this.setPoint( point );
    }
    
    /**
     * Copy Constructor
     * @param other DeterministicDistribution to copy
     */
    public DeterministicDistribution(
        final DeterministicDistribution other )
    {
        this( other.getPoint() );
    }

    /**
     * Getter for point
     * @return
     * Location of the distribution
     */
    public double getPoint()
    {
        return this.point;
    }

    /**
     * Setter for point
     * @param point
     * Location of the distribution
     */
    public void setPoint(
        final double point )
    {
        this.point = point;
    }

    @Override
    public Double getMean()
    {
        return this.getPoint();
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            samples.add( this.getPoint() );
        }
        return samples;
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getPoint() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if( parameters.getDimensionality() != 1 )
        {
            throw new IllegalArgumentException( "Parameter dimension must be 1" );
        }
        this.setPoint( parameters.getElement( 0 ) );
    }

    @Override
    public double getVariance()
    {
        return 0.0;
    }

    @Override
    public DeterministicDistribution.CDF getCDF()
    {
        return new DeterministicDistribution.CDF( this );
    }

    @Override
    public Double getMinSupport()
    {
        return this.getPoint();
    }

    @Override
    public Double getMaxSupport()
    {
        return this.getPoint();
    }

    @Override
    public Set<Double> getDomain()
    {
        return Collections.singleton(this.getPoint());
    }

    @Override
    public int getDomainSize()
    {
        return 1;
    }

    @Override
    public DeterministicDistribution.PMF getProbabilityFunction()
    {
        return new DeterministicDistribution.PMF( this );
    }

    /**
     * PMF of the deterministic distribution.
     */
    public static class PMF
        extends DeterministicDistribution
        implements ProbabilityMassFunction<Double>
    {

        /**
         * Creates a new instance of DeterministicDistribution
         */
        public PMF()
        {
            super();
       }

        /**
         * Creates a new instance of DeterministicDistribution
         * @param point
         * Location of the distribution
         */
        public PMF(
            final double point )
        {
            super( point );
        }

        /**
         * Copy Constructor
         * @param other DeterministicDistribution to copy
         */
        public PMF(
            final DeterministicDistribution other )
        {
            super( other );
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        @Override
        public double logEvaluate(
            final Double input)
        {
            return Math.log(this.evaluate(input));
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return (input.doubleValue() == this.getPoint()) ? 1.0 : 0.0;
        }

        @Override
        public DeterministicDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * CDF of the deterministic distribution.
     */
    public static class CDF
        extends DeterministicDistribution
        implements ClosedFormCumulativeDistributionFunction<Double>,
        UnivariateScalarFunction
    {

        /** 
         * Creates a new instance of DeterministicDistribution 
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of DeterministicDistribution
         * @param point
         * Location of the distribution
         */
        public CDF(
            final double point )
        {
            super( point );
        }

        /**
         * Copy Constructor
         * @param other DeterministicDistribution to copy
         */
        public CDF(
            final DeterministicDistribution other )
        {
            super( other );
        }
        
        @Override
        public Double evaluate(
            final Double input )
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluate(
            final double input )
        {
            return (input < this.getPoint()) ? 0.0 : 1.0;
        }

        @Override
        public DeterministicDistribution.CDF getCDF()
        {
            return this;
        }

    }
    
}
