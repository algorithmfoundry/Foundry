    /*
 * File:                SnedecorFDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 * CDF of the Snedecor F-distribution (also known as Fisher F-distribution,
 * Fisher-Snedecor F-distribution, or just plain old F-distribution).  This is
 * a type of Beta Distribution,
 * where F(x,v1,v2) = 1-Beta(v2/(v2+v1*x),v2,v1)
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="F-distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/F-distribution"
)
public class SnedecorFDistribution
    extends AbstractClosedFormUnivariateDistribution<Double>
{

    /**
     * Default value of v1, {@value}.
     */
    public static final double DEFAULT_V1 = 3.0;

    /**
     * Default value of v2, {@value}.
     */
    public static final double DEFAULT_V2 = 5.0;

    /**
     * First degree of freedom
     */
    private double v1;

    /**
     * Second degree of freedom
     */
    private double v2;

    /**
     * Default constructor
     */
    public SnedecorFDistribution()
    {
        this( DEFAULT_V1, DEFAULT_V2 );
    }

    /**
     * Creates a new instance of CumulativeDistribution
     * @param v1 
     * First degree of freedom
     * @param v2 
     * Second degree of freedom
     */
    public SnedecorFDistribution(
        final double v1,
        final double v2 )
    {
        this.setV1( v1 );
        this.setV2( v2 );
    }

    /**
     * Copy Constructor
     * @param other CumulativeDistribution to copy
     */
    public SnedecorFDistribution(
        final SnedecorFDistribution other )
    {
        this( other.getV1(), other.getV2() );
    }

    @Override
    public SnedecorFDistribution clone()
    {
        return (SnedecorFDistribution) super.clone();
    }
    
    /**
     * Getter for v1
     * @return 
     * First degree of freedom
     */
    public double getV1()
    {
        return this.v1;
    }

    /**
     * Setter for v1
     * @param v1 
     * First degree of freedom
     */
    public void setV1(
        final double v1 )
    {
        if( v1 <= 0.0 )
        {
            throw new IllegalArgumentException( "v1 must be > 0.0" );
        }
        this.v1 = v1;
    }

    /**
     * Getter for v2
     * @return 
     * Second degree of freedom
     */
    public double getV2()
    {
        return this.v2;
    }

    /**
     * Setter for v2
     * @param v2 
     * Second degree of freedom
     */
    public void setV2(
        final double v2 )
    {
        if( v2 <= 0.0 )
        {
            throw new IllegalArgumentException( "v2 must be > 0.0" );
        }
        this.v2 = v2;
    }

    @Override
    public Double getMean()
    {
        if( this.v2 > 2.0 )
        {
            return this.v2 / (this.v2 - 2.0);
        }
        else
        {
            return this.v2;
        }
    }

    @Override
    public double getVariance()
    {

        if( this.v2 > 4.0 )
        {
            double t1 = 2.0 * this.v2*this.v2;
            double t2 = this.v1 + this.v2 - 2.0;
            double top = t1*t2;

            double b1 = this.v2 - 2.0;
            double b2 = this.v2 - 4.0;
            double bottom = this.v1 * b1*b1 * b2;
            return top / bottom;
        }
        else
        {
            return 1.0;
        }

    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getV1(), this.getV2() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        if( parameters.getDimensionality() != 2 )
        {
            throw new IllegalArgumentException(
                "Parameters must have dimensionality 2" );
        }
        this.setV1( parameters.getElement( 0 ) );
        this.setV2( parameters.getElement( 1 ) );
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        ArrayList<Double> g1 = GammaDistribution.sample(
            this.v1/2.0, 2.0/this.v1, random,numSamples);
        ArrayList<Double> g2 = GammaDistribution.sample(
            this.v2/2.0, 2.0/this.v2, random,numSamples);
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            samples.add( g1.get(n) / g2.get(n) );
        }
        return samples;
    }

    @Override
    public SnedecorFDistribution.CDF getCDF()
    {
        return new SnedecorFDistribution.CDF( this );
    }

    @Override
    public Double getMinSupport()
    {
        return 0.0;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * CDF of the F-distribution.
     */
    public static class CDF
        extends SnedecorFDistribution
        implements ClosedFormCumulativeDistributionFunction<Double>,
        UnivariateScalarFunction
    {

        /**
         * Default constructor
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of CumulativeDistribution
         * @param v1 
         * First degree of freedom
         * @param v2 
         * Second degree of freedom
         */
        public CDF(
            final double v1,
            final double v2 )
        {
            super( v1, v2 );
        }

        /**
         * Copy Constructor
         * @param other CumulativeDistribution to copy
         */
        public CDF(
            final SnedecorFDistribution other )
        {
            super( other );
        }

        @Override
        public double evaluate(
            final double input )
        {
            return evaluate( input, this.getV1(), this.getV2() );
        }

        @Override
        public Double evaluate(
            final Double input )
        {
            return this.evaluate( input.doubleValue() );
        }
        
        /**
         * Evaluates the F-distribution CDF(input,v1,v2)
         * @param input 
         * Input independent variable in the F-distribution
         * @param v1 
         * First degree of freedom
         * @param v2 
         * Second degree of freedom
         * @return 
         * Probability of the CDF(input,v1,v2)
         */
        public static double evaluate(
            final double input,
            final double v1,
            final double v2 )
        {
            double p;
            if (input < 0.0)
            {
                p = 0.0;
            }
            else
            {
                double a = BetaDistribution.CDF.evaluate(
                    v2 / (v2 + v1 * input), 0.5 * v2, 0.5 * v1 );
                p = 1.0 - a;
            }
            return p;

        }

        @Override
        public SnedecorFDistribution.CDF getCDF()
        {
            return this;
        }

    }

}
