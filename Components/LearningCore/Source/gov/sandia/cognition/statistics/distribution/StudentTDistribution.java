/*
 * File:                StudentTDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothUnivariateDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.method.InverseTransformSampling;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Defines a noncentral Student-t Distribution.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Christopher Bishop",
            title="Pattern Recognition and Machine Learning",
            type=PublicationType.Book,
            year=2006,
            pages={102,105}
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Student's t-distribution",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Student_t_distribution"
        )
    }
)
public class StudentTDistribution
    extends AbstractClosedFormSmoothUnivariateDistribution
    implements EstimableDistribution<Double,StudentTDistribution>
{

    /**
     * Default degrees of freedom, {@value}.
     */
    public static final double DEFAULT_DEGREES_OF_FREEDOM = 3.0;

    /**
     * Default mean, {@value}.
     */
    public static final double DEFAULT_MEAN = 0.0;

    /**
     * Default precision, {@value}.
     */
    public static final double DEFAULT_PRECISION = 1.0;

    /**
     * Precision, which is proportionate to the inverseRootFinder of variance, of the
     * distribution, must be greater than zero.
     * Note that we are using "precision" instead of "variance" because the
     * variance of the Student-t has another scale factor based on the dofs
     * and we want to avoid confusion.
     */
    protected double precision;

    /**
     * Mean, or noncentrality parameter, of the distribution
     */
    protected double mean;

    /**
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     */
    protected double degreesOfFreedom;

    /**
     * Default degrees of freedom.
     */
    public StudentTDistribution()
    {
        this( DEFAULT_DEGREES_OF_FREEDOM );
    }

    /**
     * Creates a new instance of StudentTDistribution
     * @param degreesOfFreedom
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     */
    public StudentTDistribution(
        final double degreesOfFreedom )
    {
        this( degreesOfFreedom, DEFAULT_MEAN, DEFAULT_PRECISION );
    }

    /**
     * Creates a new instance of StudentTDistribution
     * @param degreesOfFreedom
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     * @param mean
     * Mean, or noncentrality parameter, of the distribution
     * @param precision
     * Precision, inverseRootFinder variance, of the distribution, must be greater
     * than zero.
     */
    public StudentTDistribution(
        final double degreesOfFreedom,
        final double mean,
        final double precision )
    {
        this.setDegreesOfFreedom(degreesOfFreedom);
        this.setPrecision(precision);
        this.setMean(mean);
    }

    /**
     * Copy constructor
     * @param other
     * StudentTDistribution to copy
     */
    public StudentTDistribution(
        final StudentTDistribution other )
    {
        this( other.getDegreesOfFreedom(), other.getMean(), other.getPrecision() );
    }
    
    @Override
    public StudentTDistribution clone()
    {
        return (StudentTDistribution) super.clone();
    }

    /**
     * Getter for degreesOfFreedom
     * @return
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     */
    public double getDegreesOfFreedom()
    {
        return this.degreesOfFreedom;
    }

    /**
     * Setter for degreesOfFreedom
     * @param degreesOfFreedom
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     */
    public void setDegreesOfFreedom(
        final double degreesOfFreedom )
    {
        if( degreesOfFreedom <= 0.0 )
        {
            throw new IllegalArgumentException( "DOF must be > 0.0" );
        }
        this.degreesOfFreedom = degreesOfFreedom;
    }

    @Override
    public double getVariance()
    {
        
        final double v = this.getDegreesOfFreedom();

        if( v > 2.0 )
        {
            return v / (v - 2.0) / this.precision;
        }
        else
        {
            return Double.POSITIVE_INFINITY;
        }
    }

    /**
     * Returns the parameters of this PDF, which is a
     * 1-dimensional Vector containing the degrees of freedom
     * @return
     * 1-dimensional Vector containing the degrees of freedom
     */
    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getDegreesOfFreedom(), this.getMean(), this.getPrecision() );
    }

    /**
     * Sets the parameters of this PDF, which must be a
     * 1-dimensional Vector containing the degrees of freedom
     * @param parameters
     * 1-dimensional Vector containing the degrees of freedom
     */
    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        parameters.assertDimensionalityEquals(3);
        this.setDegreesOfFreedom( parameters.getElement( 0 ) );
        this.setMean( parameters.getElement(1) );
        this.setPrecision( parameters.getElement(2) );
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples )
    {
        // This is a Chi-square with degreesOfFreedom degrees of freedom
        ArrayList<Double> Vs = ChiSquareDistribution.sample(
            this.degreesOfFreedom, random, numSamples);
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        final double sp = Math.sqrt(this.precision);
        for( int n = 0; n < numSamples; n++ )
        {
            final double z = random.nextGaussian()/sp;
            final double v = Vs.get(n);
            samples.add( z * Math.sqrt(this.degreesOfFreedom/v)+this.mean );
        }
        return samples;
    }

    @Override
    public StudentTDistribution.CDF getCDF()
    {
        return new StudentTDistribution.CDF( this );
    }

    @Override
    public StudentTDistribution.PDF getProbabilityFunction()
    {
        return new StudentTDistribution.PDF( this );
    }

    /**
     * Getter for precision
     * @return
     * Precision, inverseRootFinder variance, of the distribution, must be greater
     * than zero.
     */
    public double getPrecision()
    {
        return this.precision;
    }

    /**
     * Setter for precision
     * @param precision
     * Precision, inverseRootFinder variance, of the distribution, must be greater
     * than zero.
     */
    public void setPrecision(
        final double precision)
    {
        if( precision <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Precision must be > 0.0" );
        }
        this.precision = precision;
    }

    /**
     * Setter for mean
     * @param mean
     * Mean, or noncentrality parameter, of the distribution
     */
    public void setMean(
        final double mean)
    {
        this.mean = mean;
    }

    @Override
    public Double getMean()
    {
        return this.mean;
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

    @Override
    public String toString()
    {
        return "Mean: " + this.getMean() + ", Variance: " + 1.0/this.getPrecision() + ", DOF: " + this.getDegreesOfFreedom();
    }

    @Override
    public StudentTDistribution.MaximumLikelihoodEstimator getEstimator()
    {
        return new StudentTDistribution.MaximumLikelihoodEstimator();
    }

    /**
     * Evaluator that computes the Probability Density Function (CDF) of
     * a Student-t distribution with a fixed number of degrees of freedom
     */
    public static class PDF
        extends StudentTDistribution
        implements UnivariateProbabilityDensityFunction
    {

        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF
         * @param degreesOfFreedom
         * Degrees of freedom in the distribution, usually the number of
         * datapoints - 1, DOFs must be greater than zero.
         */
        public PDF(
            final double degreesOfFreedom )
        {
            super( degreesOfFreedom );
        }

        /**
         * Creates a new instance of PDF
         * @param degreesOfFreedom
         * Degrees of freedom in the distribution, usually the number of
         * datapoints - 1, DOFs must be greater than zero.
         * @param mean
         * Mean, or noncentrality parameter, of the distribution
         * @param precision
         * Precision, inverseRootFinder variance, of the distribution, must be greater
         * than zero.
         */
        public PDF(
            final double degreesOfFreedom,
            final double mean,
            final double precision )
        {
            super( degreesOfFreedom, mean, precision );
        }

        /**
         * Creates a new instance of PDF
         * @param other
         * The underlying Student t-distribution
         */
        public PDF(
            final StudentTDistribution other  )
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
            return Math.exp( this.logEvaluate(input) );
        }


        @Override
        public double logEvaluate(
            final Double input)
        {
            return this.logEvaluate((double) input);
        }

        @Override
        public double logEvaluate(
            final double input)
        {
            double logSum = 0.0;
            final double v2 = this.degreesOfFreedom / 2.0;
            final double delta = input-this.mean;
            logSum += MathUtil.logGammaFunction( v2 + 0.5 );
            logSum -= MathUtil.logGammaFunction( v2 );
            logSum += 0.5 * Math.log( this.precision / (Math.PI*this.degreesOfFreedom) );
            logSum -= (v2 + 0.5) * Math.log( 1.0 + this.precision * delta*delta / this.degreesOfFreedom );
            return logSum;
        }

        @Override
        public StudentTDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * Evaluator that computes the Cumulative Distribution Function (CDF) of
     * a Student-t distribution with a fixed number of degrees of freedom
     */
    public static class CDF
        extends StudentTDistribution
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
    {

        /**
         * Default constructor.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of CDF
         * @param degreesOfFreedom
         * Degrees of freedom in the distribution, usually the number of
         * datapoints - 1, DOFs must be greater than zero.
         */
        public CDF(
            final double degreesOfFreedom )
        {
            super( degreesOfFreedom );
        }

        /**
         * Creates a new instance of PDF
         * @param degreesOfFreedom
         * Degrees of freedom in the distribution, usually the number of
         * datapoints - 1, DOFs must be greater than zero.
         * @param mean
         * Mean, or noncentrality parameter, of the distribution
         * @param precision
         * Precision, inverseRootFinder variance, of the distribution, must be greater
         * than zero.
         */
        public CDF(
            final double degreesOfFreedom,
            final double mean,
            final double precision )
        {
            super( degreesOfFreedom, mean, precision );
        }

        /**
         * Creates a new instance of CDF
         * @param other
         * The underlying Student t-distribution
         */
        public CDF(
            final StudentTDistribution other  )
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

            double delta = input-this.mean;
            double a = 0.5 * BetaDistribution.CDF.evaluate(
                degreesOfFreedom / (degreesOfFreedom + this.precision*(delta*delta)),
                0.5 * degreesOfFreedom, 0.5 );

            // Take advantage of the symmetry to compute the CDF for values
            // less than the mean
            if (delta < 0.0)
            {
                a = 1.0 - a;
            }

            double p = 1.0 - a;
            if (p < 0.0)
            {
                p = 0.0;
            }
            if (p > 1.0)
            {
                p = 1.0;
            }

            return p;
        }

        /**
         * Evaluates the Inverse Student-t CDF for the given probability
         * and degrees of freedom
         * @param p
         * Value at which to solve for x such that x=CDF(p)
         * @return
         * Value of x such that x=CDF(p)
         */
        @Override
        public Double inverse(
            final double p )
        {
            return InverseTransformSampling.inverse( this, p ).getInput();
        }

        @Override
        public StudentTDistribution.CDF getCDF()
        {
            return this;
        }

        @Override
        public StudentTDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getDerivative().evaluate(input);
        }

    }


    /**
     * Estimates the parameters of the Student-t distribution from the given
     * data, where the degrees of freedom are estimated from the Kurtosis
     * of the sample data.
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,StudentTDistribution>
    {

        /**
         * Amount to add to the variance to keep it from being 0.0
         */
        private double defaultVariance;

        /**
         * Typical value of a defaultVariance, {@value}
         */
        public static final double DEFAULT_VARIANCE = 1e-5;

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
            this( DEFAULT_VARIANCE );
        }

        /**
         * Creates a new instance of MaximumLikelihoodEstimator
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public MaximumLikelihoodEstimator(
            final double defaultVariance )
        {
            this.defaultVariance = defaultVariance;
        }

        /**
         * Creates a new instance of UnivariateGaussian from the given data
         * @param data
         * Data to fit a UnivariateGaussian against
         * @return
         * Maximum likelihood estimate of the UnivariateGaussian that generated
         * the data
         */
        @Override
        public StudentTDistribution learn(
            final Collection<? extends Double> data )
        {
            return MaximumLikelihoodEstimator.learn( data, this.defaultVariance );
        }

        /**
         * Creates a new instance of UnivariateGaussian from the given data
         * @param data
         * Data to fit a UnivariateGaussian against
         * @return
         * Maximum likelihood estimate of the UnivariateGaussian that generated
         * the data
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public static StudentTDistribution.PDF learn(
            final Collection<? extends Double> data,
            final double defaultVariance )
        {
            Pair<Double,Double> mle =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            double kurtosis = UnivariateStatisticsUtil.computeKurtosis(data);
            double mean = mle.getFirst();
            double variance = mle.getSecond();
            variance += defaultVariance;

            double dofs = (6.0/(kurtosis+defaultVariance))+4.0;
            double precision = dofs / (variance*(dofs-2.0));
            return new StudentTDistribution.PDF( dofs, mean, precision );
        }

    }

    /**
     * Creates a UnivariateGaussian from weighted data
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,StudentTDistribution>
    {

        /**
         * Amount to add to the variance to keep it from being 0.0
         */
        private double defaultVariance;

        /**
         * Default constructor
         */
        public WeightedMaximumLikelihoodEstimator()
        {
            this( MaximumLikelihoodEstimator.DEFAULT_VARIANCE );
        }

        /**
         * Creates a new instance of WeightedMaximumLikelihoodEstimator
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public WeightedMaximumLikelihoodEstimator(
            final double defaultVariance )
        {
            this.defaultVariance = defaultVariance;
        }

        /**
         * Creates a new instance of UnivariateGaussian using a weighted
         * Maximum Likelihood estimate based on the given data
         * @param data
         * Weighed pairs of data (first is data, second is weight) that was
         * generated by some unknown UnivariateGaussian distribution
         * @return
         * Maximum Likelihood UnivariateGaussian that generated the data
         */
        @Override
        public StudentTDistribution.PDF learn(
            final Collection<? extends WeightedValue<? extends Double>> data )
        {
            return WeightedMaximumLikelihoodEstimator.learn(
                data, this.defaultVariance );
        }

        /**
         * Creates a new instance of UnivariateGaussian using a weighted
         * Maximum Likelihood estimate based on the given data
         * @param data
         * Weighed pairs of data (first is data, second is weight) that was
         * generated by some unknown UnivariateGaussian distribution
         * @return
         * Maximum Likelihood UnivariateGaussian that generated the data
         * @param defaultVariance
         * Amount to add to the variance to keep it from being 0.0
         */
        public static StudentTDistribution.PDF learn(
            final Collection<? extends WeightedValue<? extends Double>> data,
            final double defaultVariance )
        {
            Pair<Double,Double> mle =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
            double kurtosis = UnivariateStatisticsUtil.computeWeightedKurtosis(data);
            double mean = mle.getFirst();
            double variance = mle.getSecond();
            variance += defaultVariance;
            
            double dofs = (6.0/(Math.abs(kurtosis)+defaultVariance))+4.0;
            double precision = dofs / (variance*(dofs-2.0));
            return new StudentTDistribution.PDF( dofs, mean, precision );
        }

    }


}
