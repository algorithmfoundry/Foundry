/*
 * File:                BetaBinomialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 11, 2010, Sandia Corporation.
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
import java.util.Collection;
import java.util.Random;

/**
 * A Binomial distribution where the binomial parameter, p, is set according
 * to a Beta distribution instead of a single value.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Eric Weisstein",
    title="Beta Binomial Distribution",
    type=PublicationType.WebPage,
    year=2010,
    url="http://mathworld.wolfram.com/BetaBinomialDistribution.html"
)
public class BetaBinomialDistribution 
    extends AbstractClosedFormScalarDistribution<Number>
    implements ClosedFormDiscreteScalarDistribution<Number>
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 1.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Default n, {@value}.
     */
    public static final int DEFAULT_N = 1;

    /**
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    protected int n;

    /**
     * Shape, similar to the beta parameter shape, must be greater than zero
     */
    protected double shape;

    /**
     * Scale, similar to the beta parameter scale, must be greater than zero
     */
    protected double scale;

    /** 
     * Creates a new instance of BetaBinomialDistribution 
     */
    public BetaBinomialDistribution()
    {
        this( DEFAULT_N, DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of BetaBinomialDistribution 
     * @param shape
     * Shape, similar to the beta parameter shape, must be greater than zero
     * @param scale
     * Scale, similar to the beta parameter scale, must be greater than zero
     * @param n
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    public BetaBinomialDistribution(
        int n,
        double shape,
        double scale )
    {
        this.shape = shape;
        this.scale = scale;
        this.n = n;
    }

    /**
     * Copy constructor
     * @param other
     * BetaBinomialDistribution to copy
     */
    public BetaBinomialDistribution(
        BetaBinomialDistribution other )
    {
        this( other.getN(), other.getShape(), other.getScale() );
    }

    @Override
    public BetaBinomialDistribution clone()
    {
        return (BetaBinomialDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape, similar to the beta parameter shape, must be greater than zero
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape, similar to the beta parameter shape, must be greater than zero
     */
    public void setShape(
        double shape)
    {
        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException( "shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale, similar to the beta parameter scale, must be greater than zero
     */
    public double getScale()
    {
        return scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale, similar to the beta parameter scale, must be greater than zero
     */
    public void setScale(
        double scale)
    {
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException( "scale must be > 0.0" );
        }
        this.scale = scale;
    }

    /**
     * Getter for n
     * @return
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    public int getN()
    {
        return this.n;
    }

    /**
     * Setter for n
     * @param n
     * Number of observations, similar to the Binomial N,
     * must be greater than zero
     */
    public void setN(
        int n)
    {
        if( n < 1 )
        {
            throw new IllegalArgumentException( "n must be > 0" );
        }
        this.n = n;
    }

    public Number getMean()
    {
        return this.n * this.shape / (this.shape+this.scale);
    }

    public ArrayList<? extends Number> sample(
        Random random,
        int numSamples)
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    public BetaBinomialDistribution.CDF getCDF()
    {
        return new BetaBinomialDistribution.CDF( this );
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.n, this.shape, this.scale );
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(3);
        this.setN( (int) parameters.getElement(0) );
        this.setShape(parameters.getElement(1) );
        this.setScale(parameters.getElement(2) );
    }

    public Number getMinSupport()
    {
        return 0;
    }

    public Number getMaxSupport()
    {
        return this.n;
    }

    public double getVariance()
    {
        final double ss = this.shape + this.scale;
        final double numer = this.n*this.shape*this.scale*(ss+this.n);
        final double denom = ss*ss*(ss+1);
        return numer / denom;
    }

    public Collection<? extends Number> getDomain()
    {
        return new IntegerCollection(0, (int) Math.ceil(this.n) );
    }

    public BetaBinomialDistribution.PMF getProbabilityFunction()
    {
        return new BetaBinomialDistribution.PMF( this );
    }

    /**
     * PMF of the BetaBinomialDistribution
     */
    public static class PMF
        extends BetaBinomialDistribution
        implements ProbabilityMassFunction<Number>
    {

        /**
         * Creates a new instance of BetaBinomialDistribution
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of BetaBinomialDistribution
         * @param shape
         * Shape, similar to the beta parameter shape, must be greater than zero
         * @param scale
         * Scale, similar to the beta parameter scale, must be greater than zero
         * @param n
         * Number of observations, similar to the Binomial N,
         * must be greater than zero
         */
        public PMF(
            int n,
            double shape,
            double scale )
        {
            super( n, shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * BetaBinomialDistribution to copy
         */
        public PMF(
            BetaBinomialDistribution other )
        {
            super( other );
        }

        @Override
        public BetaBinomialDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

        public Double evaluate(
            Number input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        public double logEvaluate(
            Number input)
        {
            if( input.doubleValue() < 0.0 )
            {
                return Math.log(0.0);
            }
            else if( input.doubleValue() > this.n )
            {
                return Math.log(0.0);
            }

            final int x = input.intValue();
            double logSum = 0.0;
            logSum += MathUtil.logBinomialCoefficient(this.n, x);
            logSum += MathUtil.logBetaFunction( this.shape + x, this.n+this.scale - x);
            logSum -= MathUtil.logBetaFunction( this.shape, this.scale );
            return logSum;
        }

        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

    }

    /**
     * CDF of BetaBinomialDistribution
     */
    public static class CDF
        extends BetaBinomialDistribution
        implements ClosedFormScalarCumulativeDistributionFunction<Number>
    {

        /**
         * Creates a new instance of BetaBinomialDistribution
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of BetaBinomialDistribution
         * @param shape
         * Shape, similar to the beta parameter shape, must be greater than zero
         * @param scale
         * Scale, similar to the beta parameter scale, must be greater than zero
         * @param n
         * Number of observations, similar to the Binomial N,
         * must be greater than zero
         */
        public CDF(
            int n,
            double shape,
            double scale )
        {
            super( n, shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * BetaBinomialDistribution to copy
         */
        public CDF(
            BetaBinomialDistribution other )
        {
            super( other );
        }

        public Double evaluate(
            Number input)
        {
            return ProbabilityMassFunctionUtil.computeCumulativeValue(
                input.intValue(),this);
        }

        @Override
        public BetaBinomialDistribution.CDF getCDF()
        {
            return this;
        }

    }

}
