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
import gov.sandia.cognition.statistics.AbstractClosedFormScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteScalarDistribution;
import gov.sandia.cognition.statistics.ClosedFormScalarCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

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
    extends AbstractClosedFormScalarDistribution<Double>
    implements ClosedFormDiscreteScalarDistribution<Double>
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
        double point )
    {
        this.setPoint( point );
    }
    
    /**
     * Copy Constructor
     * @param other DeterministicDistribution to copy
     */
    public DeterministicDistribution(
        DeterministicDistribution other )
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
        double point )
    {
        this.point = point;
    }

    public Double getMean()
    {
        return this.getPoint();
    }

    public ArrayList<Double> sample(
        Random random,
        int numSamples )
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            samples.add( this.getPoint() );
        }
        return samples;
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getPoint() );
    }

    public void convertFromVector(
        Vector parameters )
    {
        if( parameters.getDimensionality() != 1 )
        {
            throw new IllegalArgumentException( "Parameter dimension must be 1" );
        }
        this.setPoint( parameters.getElement( 0 ) );
    }

    public double getVariance()
    {
        return 0.0;
    }

    public DeterministicDistribution.CDF getCDF()
    {
        return new DeterministicDistribution.CDF( this );
    }

    public Double getMinSupport()
    {
        return this.getPoint();
    }

    public Double getMaxSupport()
    {
        return this.getPoint();
    }

    public Collection<Double> getDomain()
    {
        return Arrays.asList( this.getPoint() );
    }

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
            double point )
        {
            super( point );
        }

        /**
         * Copy Constructor
         * @param other DeterministicDistribution to copy
         */
        public PMF(
            DeterministicDistribution other )
        {
            super( other );
        }

        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        public double logEvaluate(
            Double input)
        {
            return Math.log(this.evaluate(input));
        }

        public Double evaluate(
            Double input)
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
        implements ClosedFormScalarCumulativeDistributionFunction<Double>,
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
            double point )
        {
            super( point );
        }

        /**
         * Copy Constructor
         * @param other DeterministicDistribution to copy
         */
        public CDF(
            DeterministicDistribution other )
        {
            super( other );
        }
        
        public Double evaluate(
            Double input )
        {
            return this.evaluate( input.doubleValue() );
        }

        public double evaluate(
            double input )
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
