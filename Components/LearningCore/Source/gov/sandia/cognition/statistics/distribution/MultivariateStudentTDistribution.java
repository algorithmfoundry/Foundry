/*
 * File:                MultivariateStudentTDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 29, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * Multivariate generalization of the noncentral Student's t-distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Christopher M. Bishop",
            title="Pattern Recognition and Machine Learning",
            type=PublicationType.Book,
            year=2006,
            pages={104,105}
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Multivariate Student distribution",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Multivariate_Student_distribution"
        )
    }
)
public class MultivariateStudentTDistribution 
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDistribution<Vector>
{

    /**
     * Default dimensionality, {@value}.
     */
    public static final int DEFAULT_DIMENSIONALITY = 2;

    /**
     * Default degrees of freedom, {@value}.
     */
    public static final double DEFAULT_DEGREES_OF_FREEDOM = 3.0;

    /**
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     */
    protected double degreesOfFreedom;

    /**
     * Mean, or noncentrality parameter, of the distribution
     */
    protected Vector mean;

    /**
     * Precision, which is proportionate to the inverse of variance, of the
     * distribution, must be symmetric and positive definite.
     */
    private Matrix precision;

    /** 
     * Creates a new instance of MultivariateStudentTDistribution 
     */
    public MultivariateStudentTDistribution()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a distribution with the given dimensionality.
     * @param dimensionality
     * Dimensionality of the distribution.
     */
    public MultivariateStudentTDistribution(
        int dimensionality )
    {
        this( DEFAULT_DEGREES_OF_FREEDOM,
            VectorFactory.getDefault().createVector(dimensionality),
            MatrixFactory.getDefault().createIdentity(dimensionality,dimensionality) );
    }

    /**
     * Creates a distribution with the given dimensionality.
     * @param degreesOfFreedom
     * Degrees of freedom in the distribution, usually the number of
     * datapoints - 1, DOFs must be greater than zero.
     * @param mean
     * Mean, or noncentrality parameter, of the distribution
     * @param precision
     * Precision, which is proportionate to the inverse of variance, of the
     * distribution, must be symmetric and positive definite.
     */
    public MultivariateStudentTDistribution(
        double degreesOfFreedom,
        Vector mean,
        Matrix precision)
    {
        this.degreesOfFreedom = degreesOfFreedom;
        this.mean = mean;
        this.precision = precision;
    }

    /**
     * Copy constructor
     * @param other
     * MultivariateStudentTDistribution to copy
     */
    public MultivariateStudentTDistribution(
        MultivariateStudentTDistribution other )
    {
        this( other.getDegreesOfFreedom(),
            ObjectUtil.cloneSafe( other.getMean() ),
            ObjectUtil.cloneSafe( other.getPrecision() ) );
    }

    @Override
    public MultivariateStudentTDistribution clone()
    {
        MultivariateStudentTDistribution clone =
            (MultivariateStudentTDistribution) super.clone();
        clone.setMean( ObjectUtil.cloneSafe(this.getMean() ) );
        clone.setPrecision( ObjectUtil.cloneSafe( this.getPrecision() ) );
        return clone;
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
        double degreesOfFreedom)
    {
        if( degreesOfFreedom <= 0.0 )
        {
            throw new IllegalArgumentException(
                "DOFs must be > 0.0" );
        }
        this.degreesOfFreedom = degreesOfFreedom;
    }

    public Vector getMean()
    {
        return this.mean;
    }

    /**
     * Setter for mean
     * @param mean
     * Mean, or noncentrality parameter, of the distribution
     */
    public void setMean(
        Vector mean)
    {
        this.mean = mean;
    }

    /**
     * Getter for precision
     * @return
     * Precision, which is proportionate to the inverse of variance, of the
     * distribution, must be symmetric and positive definite.
     */
    public Matrix getPrecision()
    {
        return this.precision;
    }

    /**
     * Setter for precision
     * @param precision
     * Precision, which is proportionate to the inverse of variance, of the
     * distribution, must be symmetric and positive definite.
     */
    public void setPrecision(
        Matrix precision)
    {
        if( !precision.isSymmetric() )
        {
            throw new IllegalArgumentException(
                "Precision must be symmetric and positive definite!" );
        }
        this.precision = precision;
    }

    /**
     * Computes the covariance of the distribution, which involves inverting
     * the precision matrix.
     * @return
     * covariance of the distribution, which involves inverting the
     * precision matrix.
     */
    public Matrix getCovariance()
    {
        final double scale = this.degreesOfFreedom / (this.degreesOfFreedom-2.0);
        Matrix C = this.getPrecision().inverse();
        C.scaleEquals(scale);
        return C;
    }

    public ArrayList<Vector> sample(
        Random random,
        int numSamples)
    {

        final int dim = this.getInputDimensionality();
        Vector zeroMean = VectorFactory.getDefault().createVector(dim);

        MultivariateGaussian mvg = new MultivariateGaussian(
            zeroMean, this.precision.inverse() );
        ArrayList<Double> Vs = ChiSquareDistribution.sample(
            this.degreesOfFreedom, random, numSamples);
        ArrayList<Vector> Zs = mvg.sample(random, numSamples);
        ArrayList<Vector> samples = new ArrayList<Vector>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            Vector z = Zs.get(n);
            double v = Vs.get(n);
            z.scaleEquals( Math.sqrt(this.degreesOfFreedom/v) );
            z.plusEquals(this.mean);
            samples.add( z );
        }
        
        return samples;
    }

    public Vector convertToVector()
    {
        Vector parameters = VectorFactory.getDefault().copyValues(
            this.getDegreesOfFreedom() );
        parameters = parameters.stack( this.getMean() );
        parameters = parameters.stack( this.getPrecision().convertToVector() );
        return parameters;
    }

    public void convertFromVector(
        Vector parameters)
    {
        final int dim = this.getInputDimensionality();
        parameters.assertDimensionalityEquals(1+dim+dim*dim);
        this.setDegreesOfFreedom( parameters.getElement(0) );
        this.setMean( parameters.subVector(1, dim) );
        Matrix p = this.getPrecision();
        p.convertFromVector( parameters.subVector(
            dim+1, parameters.getDimensionality()-1) );
        this.setPrecision(p);
    }

    /**
     * Gets the dimensionality of the distribution.
     * @return
     * Dimensionality of the distribution.
     */
    public int getInputDimensionality()
    {
        return this.getMean().getDimensionality();
    }

    public MultivariateStudentTDistribution.PDF getProbabilityFunction()
    {
        return new MultivariateStudentTDistribution.PDF( this );
    }

    /**
     * PDF of the MultivariateStudentTDistribution
     */
    public static class PDF
        extends MultivariateStudentTDistribution
        implements ProbabilityDensityFunction<Vector>,
        VectorInputEvaluator<Vector,Double>
    {

        /**
         * Log determinant of the precision matrix.
         */
        private Double logDeterminantPrecision;

        /**
         * Creates a new instance of MultivariateStudentTDistribution
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a distribution with the given dimensionality.
         * @param dimensionality
         * Dimensionality of the distribution.
         */
        public PDF(
            int dimensionality )
        {
            super( dimensionality );
        }

        /**
         * Creates a distribution with the given dimensionality.
         * @param degreesOfFreedom
         * Degrees of freedom in the distribution, usually the number of
         * datapoints - 1, DOFs must be greater than zero.
         * @param mean
         * Mean, or noncentrality parameter, of the distribution
         * @param precision
         * Precision, which is proportionate to the inverse of variance, of the
         * distribution, must be symmetric and positive definite.
         */
        public PDF(
            double degreesOfFreedom,
            Vector mean,
            Matrix precision)
        {
            super( degreesOfFreedom, mean, precision );
        }

        /**
         * Copy constructor
         * @param other
         * MultivariateStudentTDistribution to copy
         */
        public PDF(
            MultivariateStudentTDistribution other )
        {
            super( other );
        }

        @Override
        public MultivariateStudentTDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

        public double logEvaluate(
            Vector input)
        {
            final int dim = this.getInputDimensionality();
            final double logDet = this.getLogDeterminantPrecision();
            final Vector delta = input.minus(this.mean);
            final double z2 = delta.times(this.getPrecision()).dotProduct(delta);
            final double d2pv2 = dim/2.0+this.degreesOfFreedom/2.0;
            double logSum = 0.0;
            logSum += MathUtil.logGammaFunction( d2pv2 );
            logSum -= MathUtil.logGammaFunction( this.degreesOfFreedom/2.0 );
            logSum += 0.5 * logDet;
            logSum -= (dim/2.0)*Math.log(Math.PI*this.degreesOfFreedom);
            logSum -= d2pv2*Math.log( 1.0 + z2/this.degreesOfFreedom );
            return logSum;
        }

        public Double evaluate(
            Vector input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        /**
         * Getter for logDeterminantPrecision
         * @return
         * Log determinant of the precision matrix.
         */
        public Double getLogDeterminantPrecision()
        {
            if( this.logDeterminantPrecision == null )
            {
                this.logDeterminantPrecision =
                    this.getPrecision().logDeterminant().getRealPart();
            }
            return this.logDeterminantPrecision;
        }

        @Override
        public void setPrecision(
            Matrix precision)
        {
            this.logDeterminantPrecision = null;
            super.setPrecision(precision);
        }

    }

}
