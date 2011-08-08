/*
 * File:                InverseWishartDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 10, 2010, Sandia Corporation.
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
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.CholeskyDecompositionMTJ;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Inverse-Wishart distribution is the multivariate generalization of the
 * inverse-gamma distribution.  This is the conjugate prior of a multivariate
 * Gaussian with known mean and unknown covariance matrix.
 * The Inverse-Wishart distribution describes a distribution over
 * covariance matrices, where the matrices are computed by summing over
 * "degrees of freedom" number of rank-1 outer-product matrices generated from
 * a multivariate Gaussian distribution with a covariance equal to the inverse
 * of the inverseScale matrix.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Stanley Sawyer",
            title="Wishart Distributions and Inverse-Wishart Sampling",
            type=PublicationType.Misc,
            year=2007,
            url="http://www.math.wustl.edu/~sawyer/hmhandouts/Wishart.pdf"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Inverse-Wishart distribution",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Inverse-Wishart_distribution"
        )
    }
)
public class InverseWishartDistribution 
    extends AbstractDistribution<Matrix>
    implements ClosedFormComputableDistribution<Matrix>
{

    /**
     * Default inverse scale dimensionality, {@value}.
     */
    public static final int DEFAULT_DIMENSIONALITY = 2;

    /**
     * Inverse scale matrix, must be symmetric and positive definite.
     */
    protected Matrix inverseScale;

    /**
     * Degrees of freedom, must be greater than the inverse scale
     * dimensionality.
     */
    protected int degreesOfFreedom;

    /**
     * Cached value of the square root of the inverse of the inverseScale,
     * used for sampling.
     */
    transient private Matrix scaleSqrt;

    /** 
     * Creates a new instance of InverseWishartDistribution 
     */
    public InverseWishartDistribution()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a new instance of InverseWishartDistribution
     * @param dimensionality
     * Dimensionality of the inverse-Wishart distribution, which sets
     * the degrees of freedom to two plus the dimensionality.
     */
    public InverseWishartDistribution(
        final int dimensionality )
    {
        this( MatrixFactory.getDefault().createIdentity(dimensionality, dimensionality ),
            dimensionality + 2 );
    }

    /**
     * Creates a new instance of InverseWishartDistribution
     * @param inverseScale
     * Inverse scale matrix, must be symmetric and positive definite.
     * @param degreesOfFreedom
     * Degrees of freedom, must be greater than the inverse scale
     * dimensionality.
     */
    public InverseWishartDistribution(
        final Matrix inverseScale,
        final int degreesOfFreedom)
    {
        this.setInverseScale(inverseScale);
        this.setDegreesOfFreedom(degreesOfFreedom);
    }

    /**
     * Copy constructor.
     * @param other
     * InverseWishartDistribution to copy
     */
    public InverseWishartDistribution(
        InverseWishartDistribution other )
    {
        this( ObjectUtil.cloneSafe(other.getInverseScale()),
            other.getDegreesOfFreedom() );
    }

    @Override
    public InverseWishartDistribution clone()
    {
        InverseWishartDistribution clone =
            (InverseWishartDistribution) super.clone();
        clone.setInverseScale( ObjectUtil.cloneSafe( this.getInverseScale() ) );
        return clone;
    }

    /**
     * Gets the dimensionality of the inverse scale Matrix.
     * @return
     * Dimensionality of the inverse scale Matrix
     */
    public int getInputDimensionality()
    {
        return this.getInverseScale().getNumRows();
    }

    @Override
    public Matrix getMean()
    {
        final int p = this.getInputDimensionality();
        double denominator = this.getDegreesOfFreedom() - p - 1.0;
        return this.getInverseScale().scale( 1.0 / denominator );
    }

    /**
     * Creates a single sample covariance matrix inverse from the given
     * parameters.
     * @param random
     * Random number generator.
     * @param mean
     * Vector of zeros with the same dimensionality as covarianceSqrt
     * @param covarianceSqrt
     * Square root of the covariance matrix, and the covariance matrix is
     * the inverse of the inverseScale matrix.
     * @param degreesOfFreedom
     * Number of rank-1 matrices to add up for each sample.
     * @return
     * Covariance matrix inverse.
     */
    public static Matrix sample(
        final Random random,
        final Vector mean,
        final Matrix covarianceSqrt,
        final int degreesOfFreedom )
    {
        ArrayList<Vector> xs = MultivariateGaussian.sample(
            mean, covarianceSqrt, random, degreesOfFreedom );
        RingAccumulator<Matrix> sum = new RingAccumulator<Matrix>();
        for( Vector x : xs )
        {
            sum.accumulate(x.outerProduct(x));
        }
        return sum.getSum().inverse();
    }

    @Override
    public ArrayList<Matrix> sample(
        final Random random,
        final int numSamples)
    {

        // We need to sample from a Multivariate Gaussian here.
        // The inverse of the inverseScale matrix is the covariance.
        Matrix covarianceSqrt = this.getScaleSqrt();
        Vector mean = VectorFactory.getDefault().createVector(
            this.getInputDimensionality() );

        ArrayList<Matrix> samples = new ArrayList<Matrix>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            Matrix B = sample(random, mean, covarianceSqrt, this.degreesOfFreedom );
            samples.add( B );
        }

        return samples;
        
    }

    @Override
    public Vector convertToVector()
    {
        Vector dof =
            VectorFactory.getDefault().copyValues( this.getDegreesOfFreedom() );
        Vector matrix = this.getInverseScale().convertToVector();
        return dof.stack(matrix);
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        int p = this.getInputDimensionality();
        parameters.assertDimensionalityEquals( 1 + p*p );
        int dof = (int) Math.round(parameters.getElement(0));
        Vector matrix =
            parameters.subVector(1, parameters.getDimensionality()-1 );

        this.setDegreesOfFreedom(dof);
        this.getInverseScale().convertFromVector( matrix );
    }

    /**
     * Getter for inverseScale
     * @return
     * Inverse scale matrix, must be symmetric and positive definite.
     */
    public Matrix getInverseScale()
    {
        return this.inverseScale;
    }

    /**
     * Setter for inverseScale
     * @param inverseScale
     * Inverse scale matrix, must be symmetric and positive definite.
     */
    public void setInverseScale(
        final Matrix inverseScale)
    {
        this.scaleSqrt = null;
        this.inverseScale = inverseScale;
    }

    /**
     * Getter for degreesOfFreedom
     * @return
     * Degrees of freedom, must be greater than the inverse scale
     * dimensionality.
     */
    public int getDegreesOfFreedom()
    {
        return this.degreesOfFreedom;
    }

    /**
     * Setter for degreesOfFreedom
     * @param degreesOfFreedom
     * Degrees of freedom, must be greater than the inverse scale
     * dimensionality.
     */
    public void setDegreesOfFreedom(
        final int degreesOfFreedom)
    {
        if( degreesOfFreedom <= this.getInputDimensionality() )
        {
            throw new IllegalArgumentException(
                "DOFs must be > dimensionality" );
        }
        this.degreesOfFreedom = degreesOfFreedom;
    }

    @Override
    public InverseWishartDistribution.PDF getProbabilityFunction()
    {
        return new InverseWishartDistribution.PDF( this );
    }

    /**
     * Getter for scaleSqrt
     * @return
     * Cached value of the square root of the inverse of the inverseScale,
     * used for sampling.
     */
    public Matrix getScaleSqrt()
    {
        if( this.scaleSqrt == null )
        {
            this.scaleSqrt = CholeskyDecompositionMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( this.inverseScale.inverse() ) ).getR();
        }
        return this.scaleSqrt;
    }



    /**
     * PDF of the Inverse-Wishart distribution, though I have absolutely no
     * idea why anybody would evaluate the PDF of an Inverse-Wishart...
     */
    public static class PDF
        extends InverseWishartDistribution
        implements ProbabilityDensityFunction<Matrix>
    {

        /**
         * The natural logarithm of 2.0.
         */
        public static final double LOG_OF_2 = Math.log(2.0);

        /**
         * Creates a new instance of InverseWishartDistribution
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of InverseWishartDistribution
         * @param inverseScale
         * Inverse scale matrix, must be symmetric and positive definite.
         * @param degreesOfFreedom
         * Degrees of freedom, must be greater than the inverse scale
         * dimensionality.
         */
        public PDF(
            final Matrix inverseScale,
            final int degreesOfFreedom)
        {
            super( inverseScale, degreesOfFreedom );
        }

        /**
         * Copy constructor.
         * @param other
         * InverseWishartDistribution to copy
         */
        public PDF(
            final InverseWishartDistribution other )
        {
            super( other );
        }

        @Override
        public Double evaluate(
            final Matrix input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public double logEvaluate(
            final Matrix input)
        {
            final int m = this.getDegreesOfFreedom();
            final int p = this.getInputDimensionality();

            double logSum = 0.0;
            logSum += (m/2.0) * this.inverseScale.logDeterminant().getRealPart();
            logSum -= ((m+p+1.0)/2.0) * input.logDeterminant().getRealPart();
            logSum -= this.inverseScale.times( input.inverse() ).trace() / 2.0;
            logSum -= (m*p/2.0)*LOG_OF_2;
            logSum -= MultivariateGammaFunction.logEvaluate(m/2.0, p);
            return logSum;
        }

        @Override
        public InverseWishartDistribution.PDF getProbabilityFunction()
        {
            return this;
        }
        
    }

    /**
     * Multivariate generalization of the Gamma function.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Multivariate gamma function",
        type=PublicationType.WebPage,
        year=2010,
        url="http://en.wikipedia.org/wiki/Multivariate_gamma_function"
    )
    public static class MultivariateGammaFunction
    {

        /**
         * Natural logarithm of pi.
         */
        public static final double LOG_PI = Math.log(Math.PI);

        /**
         * Evaluates the logarithm of the Multivariate Gamma Function
         * @param x
         * Input to consider
         * @param p
         * Dimensionality
         * @return
         * Logarithm of the Multivariate Gamma Function
         */
        public static double logEvaluate(
            final double x,
            final int p )
        {
            double logSum = 0.0;
            logSum += p*(p-1)/4.0 * LOG_PI;
            for( int j = 1; j < p; j++ )
            {
                final double y = x + (1-j)/2.0;
                logSum += MathUtil.logGammaFunction(y);
            }
            return logSum;
        }

    }

}
