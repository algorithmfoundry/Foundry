/*
 * File:                NormalInverseWishartDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 25, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * The normal inverse Wishart distribution
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Stanley Sawyer",
    title="Wishart Distributions and Inverse-Wishart Sampling",
    type=PublicationType.Misc,
    year=2007,
    url="http://www.math.wustl.edu/~sawyer/hmhandouts/Wishart.pdf"
)
public class NormalInverseWishartDistribution 
    extends AbstractDistribution<Matrix>
    implements ClosedFormComputableDistribution<Matrix>
{

    /**
     * Default dimensionality of the precision matrix, {@value}.
     */
    public static final int DEFAULT_DIMENSIONALITY = 2;

    /**
     * Default covariance divisor, {@value}.
     */
    public static final double DEFAULT_COVARIANCE_DIVISOR = 1.0;

    /**
     * Term that divides the covariance sampled from the inverseWishart,
     * must be greater than zero.
     */
    protected double covarianceDivisor;

    /**
     * Generates the mean, given the covariance from the inverseWishart.
     */
    protected MultivariateGaussian gaussian;

    /**
     * Generates the covariance for the Gaussian.
     */
    protected InverseWishartDistribution inverseWishart;

    /**
     * Default constructor
     */
    public NormalInverseWishartDistribution()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a new instance of NormalInverseWishartDistribution
     * @param dimensionality
     * Dimensionality of the distributions
     */
    public NormalInverseWishartDistribution(
        int dimensionality )
    {
        this( dimensionality, DEFAULT_COVARIANCE_DIVISOR );
    }

    /**
     * Creates a new instance of NormalInverseWishartDistribution
     * @param dimensionality
     * Dimensionality of the distributions
     * @param covarianceDivisor
     * Term that divides the covariance sampled from the inverseWishart,
     * must be greater than zero.
     */
    public NormalInverseWishartDistribution(
        int dimensionality,
        double covarianceDivisor )
    {
        this( new MultivariateGaussian( dimensionality ),
            new InverseWishartDistribution( dimensionality ),
            covarianceDivisor );
    }

    /** 
     * Creates a new instance of NormalInverseWishartDistribution 
     * @param gaussian 
     * Generates the mean, given the covariance from the inverseWishart.
     * @param inverseWishart
     * Generates the covariance for the Gaussian.
     * @param covarianceDivisor
     * Term that divides the covariance sampled from the inverseWishart,
     * must be greater than zero.
     */
    public NormalInverseWishartDistribution(
        MultivariateGaussian gaussian,
        InverseWishartDistribution inverseWishart,
        double covarianceDivisor )
    {
        this.setGaussian(gaussian);
        this.setInverseWishart(inverseWishart);
        this.setCovarianceDivisor(covarianceDivisor);
    }

    /**
     * Copy constructor
     * @param other
     * NormalInverseWishartDistribution to copy
     */
    public NormalInverseWishartDistribution(
        NormalInverseWishartDistribution other )
    {
        this( ObjectUtil.cloneSafe( other.getGaussian() ),
            ObjectUtil.cloneSafe( other.getInverseWishart() ),
            other.getCovarianceDivisor() );
    }

    @Override
    public NormalInverseWishartDistribution clone()
    {
        NormalInverseWishartDistribution clone =
            (NormalInverseWishartDistribution) super.clone();
        clone.setGaussian( ObjectUtil.cloneSafe( this.getGaussian() ) );
        clone.setInverseWishart( ObjectUtil.cloneSafe( this.getInverseWishart() ) );
        return clone;
    }

    /**
     * Getter for gaussian.
     * @return
     * Generates the mean, given the covariance from the inverseWishart.
     */
    public MultivariateGaussian getGaussian()
    {
        return this.gaussian;
    }

    /**
     * Setter for gaussian
     * @param gaussian
     * Generates the mean, given the covariance from the inverseWishart.
     */
    public void setGaussian(
        MultivariateGaussian gaussian)
    {
        this.gaussian = gaussian;
    }

    /**
     * Getter for inverseWishart
     * @return
     * Generates the covariance for the Gaussian.
     */
    public InverseWishartDistribution getInverseWishart()
    {
        return this.inverseWishart;
    }

    /**
     * Setter for inverseWishart
     * @param inverseWishart
     * Generates the covariance for the Gaussian.
     */
    public void setInverseWishart(
        InverseWishartDistribution inverseWishart)
    {
        this.inverseWishart = inverseWishart;
    }
    
    /**
     * Getter for covarianceDivisor
     * @return
     * Term that divides the covariance sampled from the inverseWishart,
     * must be greater than zero.
     */
    public double getCovarianceDivisor()
    {
        return this.covarianceDivisor;
    }

    /**
     * Setter for covarianceDivisor
     * @param covarianceDivisor
     * Term that divides the covariance sampled from the inverseWishart,
     * must be greater than zero.
     */
    public void setCovarianceDivisor(
        double covarianceDivisor)
    {
        if( covarianceDivisor <= 0.0 )
        {
            throw new IllegalArgumentException(
                "covarianceDivisor must be > 0.0" );
        }
        this.covarianceDivisor = covarianceDivisor;
    }

    public Matrix getMean()
    {
        Matrix C = this.inverseWishart.getMean();
        Vector mean = this.gaussian.getMean();
        final int d = this.getInputDimensionality();
        Matrix R = MatrixFactory.getDefault().createMatrix(d, d+1);
        R.setColumn(0, mean);
        R.setSubMatrix(0, 1, C);
        return R;
    }

    public ArrayList<Matrix> sample(
        Random random,
        int numSamples)
    {
        final int d = this.gaussian.getInputDimensionality();

        ArrayList<Matrix> samples = new ArrayList<Matrix>( numSamples );
        ArrayList<Matrix> covariances =
            this.inverseWishart.sample(random,numSamples);
        for( Matrix covariance : covariances )
        {
            Matrix meanAndCovariance =
                MatrixFactory.getDefault().createMatrix(d,d+1);
            meanAndCovariance.setSubMatrix(0, 1, covariance);
            covariance.scaleEquals(1.0/this.covarianceDivisor);
            this.gaussian.setCovariance(covariance);
            Vector mean = this.gaussian.sample(random);
            meanAndCovariance.setColumn(0, mean);
            samples.add( meanAndCovariance );
        }

        return samples;
    }

    public Vector convertToVector()
    {
        Vector c = VectorFactory.getDefault().copyValues( this.covarianceDivisor );
        c = c.stack( this.gaussian.getMean() );
        return c.stack( this.inverseWishart.convertToVector() );
    }

    public void convertFromVector(
        Vector parameters)
    {
        final int d = this.getInputDimensionality();
        parameters.assertDimensionalityEquals( 1+d + 1+d*d );
        this.setCovarianceDivisor( parameters.getElement(0) );
        Vector mean = parameters.subVector(1, d);
        this.gaussian.setMean(mean);
        Vector iwp = parameters.subVector(d+1, parameters.getDimensionality()-1);
        this.inverseWishart.convertFromVector(iwp);
    }

    /**
     * Gets the input dimensionality of the Gaussian and the inverse-Wishart
     * distributions
     * @return
     * Input dimensionality of the Gaussian and the inverse-Wishart
     * distributions
     */
    public int getInputDimensionality()
    {
        return (this.gaussian != null) ? this.gaussian.getInputDimensionality() : 0;
    }

    public NormalInverseWishartDistribution.PDF getProbabilityFunction()
    {
        return new NormalInverseWishartDistribution.PDF( this );
    }

    /**
     * PDF of the normal inverse-Wishart distribution.
     */
    public static class PDF
        extends NormalInverseWishartDistribution
        implements ProbabilityDensityFunction<Matrix>
    {

        /**
         * Default constructor
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of NormalInverseWishartDistribution
         * @param dimensionality
         * Dimensionality of the distributions
         * @param covarianceDivisor
         * Term that divides the covariance sampled from the inverseWishart,
         * must be greater than zero.
         */
        public PDF(
            int dimensionality,
            double covarianceDivisor )
        {
            super( dimensionality, covarianceDivisor );
        }

        /**
         * Creates a new instance of NormalInverseWishartDistribution
         * @param gaussian
         * Generates the mean, given the covariance from the inverseWishart.
         * @param inverseWishart
         * Generates the covariance for the Gaussian.
         * @param covarianceDivisor
         * Term that divides the covariance sampled from the inverseWishart,
         * must be greater than zero.
         */
        public PDF(
            MultivariateGaussian gaussian,
            InverseWishartDistribution inverseWishart,
            double covarianceDivisor )
        {
            super( gaussian, inverseWishart, covarianceDivisor );
        }

        /**
         * Copy constructor
         * @param other
         * NormalInverseWishartDistribution to copy
         */
        public PDF(
            NormalInverseWishartDistribution other )
        {
            super( other );
        }

        @Override
        public NormalInverseWishartDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

        public double logEvaluate(
            Matrix input)
        {
            final int d = input.getNumRows();
            Vector mean = input.getColumn(0);
            Matrix C = input.getSubMatrix(0,d-1,1,d);
            C.scaleEquals(1.0/this.covarianceDivisor);
            double lpg = this.gaussian.getProbabilityFunction().logEvaluate(mean);
            double lpiw = this.inverseWishart.getProbabilityFunction().logEvaluate(C);
            return lpg + lpiw;
        }

        public Double evaluate(
            Matrix input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

    }

}
