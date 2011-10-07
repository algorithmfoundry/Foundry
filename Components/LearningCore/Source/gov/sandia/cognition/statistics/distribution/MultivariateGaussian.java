/*
 * File:                MultivariateGaussian.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.CholeskyDecompositionMTJ;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.AbstractIncrementalEstimator;
import gov.sandia.cognition.statistics.AbstractSufficientStatistic;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.EstimableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The MultivariateGaussian class implements a multidimensional Gaussian
 * distribution that contains a mean vector and a covariance matrix.
 * If your underlying distribution is univariate (scalar),
 * then use the UnivariateGaussian class, as its operations
 * are MUCH less computationally intensive.
 * 
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-15",
    changesNeeded=true,
    comments={
        "A few minor changes needed.",
        "Comments indicated with a / / / comment in the first column."
    },
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-15",
        moreChangesNeeded=false,
        comments="Fixed."
    )
)
@PublicationReference(
    author="Wikipedia",
    title="Multivariate normal distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Multivariate_normal_distribution"
)
public class MultivariateGaussian
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDistribution<Vector>,
    EstimableDistribution<Vector,MultivariateGaussian>
{

    /**
     * Tolerance check for symmetry covariance tolerance, {@value}.
     */
    public static final double DEFAULT_COVARIANCE_SYMMETRY_TOLERANCE = 1e-5;

    /**
     * Default dimensionality of the Gaussian, {@value}
     */
    public static final int DEFAULT_DIMENSIONALITY = 2;

    /**
     * Natural logarithm of 2pi.
     */
    public static final double LOG_TWO_PI = Math.log(2*Math.PI);

    /**
     * Mean of the MultivariateGaussian.
     */
    private Vector mean;
    
    /**
     * Covariance matrix of the MultivariateGaussian.
     */
    private Matrix covariance;
    
    /**
     * Natural logarithm of the covariance matrix, automatically computed.
     */
    private Double logCovarianceDeterminant;

    /**
     * Inverse of the covariance matrix, automatically computed.
     */
    private Matrix covarianceInverse;
    
    /**
     * Natural logarithm of the leading likelihood coefficient,
     * automatically computed.
     */
    private Double logLeadingCoefficient;

    /**
     * Default constructor.
     */
    public MultivariateGaussian()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a new instance of MultivariateGaussian.
     * @param dimensionality
     * Dimensionality of the Gaussian to create.
     */
    public MultivariateGaussian(
        int dimensionality )
    {
        this( VectorFactory.getDefault().createVector(dimensionality),
            MatrixFactory.getDefault().createIdentity(dimensionality,dimensionality) );
    }

    /**
     * Creates a new instance of MultivariateGaussian.
     * 
     * @param mean The mean of the Gaussian distribution.
     * @param covariance The covariance matrix, which should be a symmetric 
     *        matrix.
     */
    public MultivariateGaussian(
        Vector mean,
        Matrix covariance)
    {
        this.setMean(mean);
        this.setCovariance(covariance);
    }
    
    
    /**
     * Creates a new instance of MultivariateGaussian.
     * 
     * @param other The other MultivariateGaussian to copy.
     */
    public MultivariateGaussian(
        MultivariateGaussian other)
    {
        this( ObjectUtil.cloneSafe( other.getMean() ),
            ObjectUtil.cloneSafe( other.getCovariance() ) );
    }
        
    @Override
    public MultivariateGaussian clone()
    {
        MultivariateGaussian clone = (MultivariateGaussian) super.clone();
        clone.setMean( ObjectUtil.cloneSafe( this.getMean() ) );
        clone.setCovariance( ObjectUtil.cloneSafe( this.getCovariance() ) );
        return clone;
    }

    @Override
    public MultivariateGaussian.PDF getProbabilityFunction()
    {
        return new MultivariateGaussian.PDF( this );
    }

    /**
     * Computes the z value squared, such that p(x) = coefficient * exp{-0.5*z^2}
     * @param input 
     * input about which to compute the z-value squared
     * @return 
     * z-value squared
     */
    public double computeZSquared(
        Vector input )
    {
        // Subtract the mean
        Vector delta = input.minus(this.mean);
        
        // Compute the weighted inner product.
        double zsquared = delta.times(this.getCovarianceInverse()).dotProduct(delta);
        return zsquared;
    }

    /**
     * Multiplies this Gaussian with the other Gaussian.  This is also
     * equivalent to computing the posterior belief using one of the Gaussians
     * as the prior and one as the conditional likelihood.
     * @param other
     * Other Gaussian to multiply with this.
     * @return
     * Multiplied Gaussians.
     */
    public MultivariateGaussian times(
        MultivariateGaussian other )
    {

        Vector m1 = this.mean;
        Matrix c1inv = this.getCovarianceInverse();

        Vector m2 = other.getMean();
        Matrix c2inv = other.getCovarianceInverse();

        Matrix Cinv = c1inv.plus( c2inv );
        Matrix C = Cinv.inverse();

        Vector m = C.times(
            c1inv.times( m1 ).plus( c2inv.times( m2 ) ) );

        return new MultivariateGaussian( m, C );

    }

    /**
     * Convolves this Gaussian with the other Gaussian.
     * @param other
     * Other Gaussian to convolve with this.
     * @return
     * Convolved Gaussians.
     */
    public MultivariateGaussian convolve(
        MultivariateGaussian other )
    {
        Vector meanHat = this.mean.plus( other.getMean() );
        Matrix covarianceHat = this.getCovariance().plus( other.getCovariance() );
        return new MultivariateGaussian( meanHat, covarianceHat );
    }

    /**
     * Gets the dimensionality of the Gaussian. This is the expected 
     * dimensionality of the vectors that can be used with the Gaussian.
     *
     * @return The dimensionality of the multivariate Gaussian.
     */
    public int getInputDimensionality()
    {
        return (this.mean != null) ? this.mean.getDimensionality() : 0;
    }
    
    @Override
    public Vector getMean()
    {
        return this.mean;
    }
    
    /**
     * Sets the mean vector.
     *
     * @param  mean The new mean, throws NullPointerException if null
     */
    public void setMean(
        Vector mean)
    {
        if ( mean == null )
        {
            throw new NullPointerException("Mean cannot be null.");
        }
        this.mean = mean;
    }

    /**
     * Gets the covariance matrix of the Gaussian.
     *
     * @return The covariance matrix of the Gaussian.
     */
    public Matrix getCovariance()
    {
        if( this.covariance == null )
        {
            this.covariance = this.covarianceInverse.inverse();
        }

        return this.covariance;
    }

    /**
     * Sets the covariance matrix.
     * @param  covariance The new covariance matrix,
     * the method forces the matrix to be symmetric by averaging the
     * off-diagonal components into a clone of this parameter.
     */
    public void setCovariance(
        Matrix covariance)
    {
        this.setCovariance(covariance,DEFAULT_COVARIANCE_SYMMETRY_TOLERANCE);
    }


    /**
     * Sets the covariance matrix. 
     *
     * @param  covariance The new covariance matrix,
     * the method forces the matrix to be symmetric by averaging the
     * off-diagonal components into a clone of this parameter.
     * @param symmetryTolerance Tolerance for symmetry check
     */
    public void setCovariance(
        Matrix covariance,
        double symmetryTolerance )
    {
        if( !covariance.isSymmetric(symmetryTolerance) )
        {
            covariance = covariance.clone();
            int N = covariance.getNumRows();
            for( int i = 1; i < N; i++ )
            {
                for( int j = 0; j < i; j++ )
                {
                    double vij = covariance.getElement(i, j);
                    double vji = covariance.getElement(j, i);
                    if( vij != vji )
                    {
                        double v = (vij + vji) / 2.0;
                        covariance.setElement(i, j, v);
                        covariance.setElement(j, i, v);
                    }
                }
            }
        }

        this.covariance = covariance;

        // Flag the other values for recomputation...
        this.covarianceInverse = null;
        this.logCovarianceDeterminant = null;
        this.logLeadingCoefficient = null;
    }

    /**
     * Gets the inverse of the covariance matrix.
     *
     * @return The inverse of the covariance matrix.
     */
    public Matrix getCovarianceInverse()
    {
        // Need to recompute the covariance inverse.
        if( this.covarianceInverse == null )
        {
            this.covarianceInverse = this.covariance.inverse();
        }
        return this.covarianceInverse;
    }

    /**
     * Sets the covariance inverse
     * @param covarianceInverse
     * Inverse of the covariance matrix
     */
    public void setCovarianceInverse(
        Matrix covarianceInverse )
    {
        this.setCovarianceInverse(covarianceInverse,
            DEFAULT_COVARIANCE_SYMMETRY_TOLERANCE);
    }

    /**
     * Sets the covariance inverse
     * @param covarianceInverse
     * Inverse of the covariance matrix
     * @param symmetryTolerance
     * Tolerance to enforce symmetry
     */
    public void setCovarianceInverse(
        Matrix covarianceInverse,
        double symmetryTolerance )
    {

        if( !covarianceInverse.isSymmetric(symmetryTolerance) )
        {
            covarianceInverse = covarianceInverse.clone();
            int N = covarianceInverse.getNumRows();
            for( int i = 1; i < N; i++ )
            {
                for( int j = 0; j < i; j++ )
                {
                    final double vij = covarianceInverse.getElement(i, j);
                    final double vji = covarianceInverse.getElement(j, i);
                    if( vij != vji )
                    {
                        final double v = (vij + vji) / 2.0;
                        covarianceInverse.setElement(i, j, v);
                        covarianceInverse.setElement(j, i, v);
                    }
                }
            }
        }

        this.covarianceInverse = covarianceInverse;

        // Flag the other values for recomputation...
        this.covariance = null;
        this.logCovarianceDeterminant = null;
        this.logLeadingCoefficient = null;

    }


    /**
     * Getter for logCovarianceDeterminant
     * @return
     * Natural logarithm of the covariance matrix, automatically computed.
     */
    public double getLogCovarianceDeterminant()
    {

        if( this.logCovarianceDeterminant == null )
        {
            // Compute the determinant of the matrix.
            ComplexNumber logDeterminant = this.covariance.logDeterminant();

            // There should be no imaginary part, as the the determinant is
            // a positive value
            this.logCovarianceDeterminant = logDeterminant.getRealPart();
        }

        return this.logCovarianceDeterminant;

    }

    /**
     * Getter for logLeadingCoefficient
     * @return
     * Natural logarithm of the leading likelihood coefficient,
     * automatically computed.
     */
    public double getLogLeadingCoefficient()
    {
        if( this.logLeadingCoefficient == null )
        {
            final int k = this.getInputDimensionality();
            this.logLeadingCoefficient =
                (-0.5*k*LOG_TWO_PI) + (-0.5*this.getLogCovarianceDeterminant());
        }

        return this.logLeadingCoefficient;

    }

    @Override
    public boolean equals(
        Object randomVariable)
    {
        boolean retval = false;
        if( randomVariable instanceof MultivariateGaussian )
        {
            MultivariateGaussian other = (MultivariateGaussian) randomVariable;
            retval = (this.getMean().equals( other.getMean() )) &&
                (this.getCovariance().equals( other.getCovariance() ));
        }
        return retval;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + ObjectUtil.hashCodeSafe(this.mean);
        hash = 53 * hash + ObjectUtil.hashCodeSafe(this.getCovariance());
        return hash;
    }
    
    @Override
    public ArrayList<Vector> sample(
        Random random,
        int numSamples )
    {
        Matrix covSqrt = CholeskyDecompositionMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( this.getCovariance() ) ).getR();
        
        return sample( this.mean, covSqrt, random, numSamples );
    }

    /**
     * Returns a collection of draws this Gaussian with the given mean
     * and covariance. If you need random draws from a Gaussian many times, I
     * would recommend that you cache the square-root decomposition of the
     * covariance and pass that to the method randomCovarianceSquareRoot().
     * 
     * @param mean mean of the Gaussian
     * @param covarianceSquareRoot square-root decomposition of the 
     * covariance of the Gaussian
     * @param numDraws number of times to draw from this random variable
     * @param random Random-number generator
     * @return ArrayList of Vectors drawn from this Gaussian distribution
     */
    public static ArrayList<Vector> sample(
        Vector mean,
        Matrix covarianceSquareRoot,
        Random random,
        int numDraws )
    {
        ArrayList<Vector> retval = new ArrayList<Vector>( numDraws );
        for( int n = 0; n < numDraws; n++ )
        {
            retval.add( sample( mean, covarianceSquareRoot, random ) );
        }
        return retval;        
    }

    /**
     * Returns a single draw from the Gaussian with the given mean
     * and covariance. 
     * @param mean mean of the Gaussian
     * @param covarianceSquareRoot square-root decomposition of the 
     * covariance of the Gaussian
     * @param random Random-number generator
     * @return Vector drawn from this Gaussian distribution
     */
    public static Vector sample(
        Vector mean,
        Matrix covarianceSquareRoot,
        Random random )
    {
        int M = covarianceSquareRoot.getNumRows();
        Vector x = VectorFactory.getDefault().createVector( M );
        for( int i = 0; i < M; i++ )
        {
            x.setElement( i, random.nextGaussian() );
        }
        Vector sample = covarianceSquareRoot.times( x );
        sample.plusEquals(mean);
        return sample;
    }    
    
    /**
     * Scales the MultivariateGaussian by premultiplying by the given Matrix
     * @param premultiplyMatrix
     * Matrix against which to premultiply this
     * @return
     * Scaled MultivariateGaussian
     */
    public MultivariateGaussian scale(
        Matrix premultiplyMatrix)
    {
        Vector m = premultiplyMatrix.times( this.mean );
        Matrix C = premultiplyMatrix.times( this.getCovariance() ).times(
            premultiplyMatrix.transpose() );
        return new MultivariateGaussian( m, C );
    }
    
    /**
     * Adds two MultivariateGaussian random variables together
     * and returns the resulting MultivariateGaussian
     * @param other
     * MultivariateGaussian to add to this MultivariateGaussian
     * @return 
     * Effective addition of the two MultivariateGaussian random variables
     */
    public MultivariateGaussian plus(
        MultivariateGaussian other )
    {
        Vector m = this.mean.plus( other.getMean() );
        Matrix C = this.getCovariance().plus( other.getCovariance() );
        return new MultivariateGaussian( m, C );
    }

    @Override
    public String toString()
    {
        String retval = "Mean: " + this.getMean() + "\n"
            + "Covariance:\n" + this.getCovariance();
        return retval;
    }    
    
    @Override
    public Vector convertToVector()
    {
        return this.mean.stack( this.getCovariance().convertToVector() );
    }

    @Override
    public void convertFromVector(
        Vector parameters )
    {
        int N = this.getInputDimensionality();
        this.setMean( parameters.subVector( 0, N-1 ) );
        
        Matrix m = this.getCovariance();
        m.convertFromVector(
            parameters.subVector( N, parameters.getDimensionality()-1 ) );
        this.setCovariance( m );
    }

    @Override
    public MultivariateGaussian.MaximumLikelihoodEstimator getEstimator()
    {
        return new MultivariateGaussian.MaximumLikelihoodEstimator();
    }
    
    /**
     * PDF of a multivariate Gaussian
     */
    public static class PDF
        extends MultivariateGaussian
        implements ProbabilityDensityFunction<Vector>,
        VectorInputEvaluator<Vector,Double>
    {

        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of MultivariateGaussian.
         * @param dimensionality
         * Dimensionality of the Gaussian to create.
         */
        public PDF(
            int dimensionality )
        {
            super( dimensionality );
        }

        /**
         * Creates a new instance of MultivariateGaussian.
         * 
         * @param mean The mean of the Gaussian distribution.
         * @param covariance The covariance matrix, which should be a symmetric 
         *        matrix.
         */
        public PDF(
            Vector mean,
            Matrix covariance)
        {
            super( mean, covariance );
        }

        /**
         * Creates a new instance of MultivariateGaussian.
         * 
         * @param other The other MultivariateGaussian to copy.
         */
        public PDF(
            MultivariateGaussian other)
        {
            super( other );
        }
        
        @Override
        public Double evaluate(
            Vector input )
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public double logEvaluate(
            Vector input)
        {
            double zsquared = this.computeZSquared(input);
            return this.getLogLeadingCoefficient() - 0.5*zsquared;
        }

        @Override
        public MultivariateGaussian.PDF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * Computes the Maximum Likelihood Estimate of the MultivariateGaussian
     * given a set of Vectors
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Vector,MultivariateGaussian.PDF>
    {
        
        /**
         * Default covariance used in estimation, {@value}.
         */
        public static final double DEFAULT_COVARIANCE =
            UnivariateGaussian.MaximumLikelihoodEstimator.DEFAULT_VARIANCE;

        /**
         * Amount to add to the diagonal of the covariance matrix
         */
        private double defaultCovariance;
        
        /**
         * Default constructor;
         */
        public MaximumLikelihoodEstimator()
        {
            this( DEFAULT_COVARIANCE );
        }

        /**
         * Creates a new instance of MaximumLikelihoodEstimator
         * @param defaultCovariance 
         * Amount to add to the diagonal of the covariance matrix
         */
        public MaximumLikelihoodEstimator(
            double defaultCovariance )
        {
            this.defaultCovariance = defaultCovariance;
        }
        
        /**
         * Computes the Gaussian that estimates the maximum likelihood of
         * generating the given set of samples.
         *
         * @return The Gaussian that estimates the maximum likelihood of generating
         *         the given data.
         * @param defaultCovariance amount to add to the diagonals of the covariance
         * matrix, typically on the order of 1e-4, can be 0.0.
         * @param data The samples to calculate the Gaussian from
         * throws IllegalArgumentException if samples has 1 or fewer samples.
         */
        public static MultivariateGaussian.PDF learn(
            Collection<? extends Vector> data,
            final double defaultCovariance )
        {

            final int N = data.size();
            if( N <= 1 )
            {
                throw new IllegalArgumentException(
                    "Need at least 2 data points to compute covariance" );
            }
            Pair<Vector,Matrix> mle =
                MultivariateStatisticsUtil.computeMeanAndCovariance(data);
            Vector mean = mle.getFirst();
            Matrix covariance = mle.getSecond();

            // Add "defaultCovariance" to the diagonal entries
            if( defaultCovariance != 0.0 )
            {
                final int M = mean.getDimensionality();
                for( int i = 0; i < M; i++ )
                {
                    double v = covariance.getElement(i, i);
                    covariance.setElement(i, i, v + defaultCovariance );
                }
            }     
            
            return new MultivariateGaussian.PDF( mean, covariance );
            
        }
        
        /**
         * Computes the Gaussian that estimates the maximum likelihood of
         * generating the given set of samples.
         *
         * @return The Gaussian that estimates the maximum likelihood of generating
         *         the given data.
         * @param data The samples to calculate the Gaussian from
         * throws IllegalArgumentException if samples has 1 or fewer samples.
         */
        @Override
        public MultivariateGaussian.PDF learn(
            Collection<? extends Vector> data)
        {
            return MaximumLikelihoodEstimator.learn(
                data, this.defaultCovariance );
        }
    }
    
    /**
     * Computes the Weighted Maximum Likelihood Estimate of the
     * MultivariateGaussian given a weighted set of Vectors
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Vector,MultivariateGaussian.PDF>
    {
        
        /**
         * Default covariance used in estimation, {@value}.
         */
        public static final double DEFAULT_COVARIANCE =
            MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;

        /**
         * Amount to add to the diagonal of the covariance matrix
         */
        private double defaultCovariance;
        
        /**
         * Default constructor.
         */
        public WeightedMaximumLikelihoodEstimator()
        {
            this( DEFAULT_COVARIANCE );
        }

        /**
         * Creates a new instance of WeightedMaximumLikelihoodEstimator
         * @param defaultCovariance 
         * Amount to add to the diagonal of the covariance matrix
         */
        public WeightedMaximumLikelihoodEstimator(
            double defaultCovariance )
        {
            this.defaultCovariance = defaultCovariance;
        }
        
        /**
         * Computes the Gaussian that estimates the maximum likelihood of
         * generating the given set of weighted samples.
         *
         * @return The Gaussian that estimates the maximum likelihood of
         * generating the given weighted data.
         * @param data The weighted samples to calculate the Gaussian from
         * throws IllegalArgumentException if samples has 1 or fewer samples.
         */
        @Override
        public MultivariateGaussian.PDF learn(
            Collection<? extends WeightedValue<? extends Vector>> data )
        {
            return WeightedMaximumLikelihoodEstimator.learn( 
                data, this.defaultCovariance );
        }
        
        /**
         * Computes the Gaussian that estimates the maximum likelihood of
         * generating the given set of weighted samples.
         * 
         * 
         * @return The Gaussian that estimates the maximum likelihood of
         * generating the given weighted data.
         * @param defaultCovariance 
         * Amount to add to the diagonal of the covariance matrix
         * @param data The weighted samples to calculate the Gaussian from
         * throws IllegalArgumentException if samples has 1 or fewer samples.
         */
        public static MultivariateGaussian.PDF learn(
            Collection<? extends WeightedValue<? extends Vector>> data,
            final double defaultCovariance )
        {
            
            // Make sure there are enough samples.
            final int N = data.size();
            if( N <= 1 )
            {
                // Error: Not enough samples.
                throw new IllegalArgumentException(
                    "The number of samples must be greater than 1.");
            }

            Pair<Vector,Matrix> mle =
                MultivariateStatisticsUtil.computeWeightedMeanAndCovariance(data);
            Vector mean = mle.getFirst();
            Matrix covariance = mle.getSecond();
            
            if( defaultCovariance != 0.0 )
            {
                final int M = covariance.getNumRows();
                for( int i = 0; i < M; i++ )
                {
                    final double v = covariance.getElement(i, i);
                    covariance.setElement(i, i, v+defaultCovariance );
                }
            }            
            
            return new MultivariateGaussian.PDF( mean, covariance );
            
        }
    }

    /**
     * Implements the sufficient statistics of the MultivariateGaussian.
     */
    public static class SufficientStatistic
        extends AbstractSufficientStatistic<Vector, MultivariateGaussian>
    {

        /**
         * Default covariance of the statistics, {@value}.
         */
        public static final double DEFAULT_COVARIANCE =
            MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;

        /**
         * The mean of the Gaussian
         */
        private Vector mean;

        /**
         * This is the sum-squared differences
         */
        private Matrix sumSquaredDifferences;

        /**
         * Default covariance of the distribution
         */
        protected double defaultCovariance;

        /**
         * Default constructor
         */
        public SufficientStatistic()
        {
            this( DEFAULT_COVARIANCE );
        }

        /**
         * Creates a new instance of SufficientStatistic
         * @param defaultCovariance
         * Default covariance of the distribution
         */
        public SufficientStatistic(
            double defaultCovariance)
        {
            super();
            this.clear();
            this.defaultCovariance = defaultCovariance;
        }

        @Override
        public MultivariateGaussian.SufficientStatistic clone()
        {
            return (MultivariateGaussian.SufficientStatistic) super.clone();
        }

        /**
         * Resets this set of sufficient statistics to its empty state.
         */
        public void clear()
        {
            this.count = 0;
            this.mean = null;
            this.sumSquaredDifferences = null;
        }

        @Override
        public void update(
            Vector value)
        {
            // We've added another value.
            this.count++;

            // Compute the difference between the value and the current mean.
            final int dim = value.getDimensionality();
            if( this.mean == null )
            {
                this.mean = VectorFactory.getDefault().createVector(dim);
            }
            Vector delta = value.minus( this.mean );

            // Update the mean based on the difference between the value
            // and the mean along with the new count.
            this.mean.plusEquals( delta.scale(1.0/this.count) );

            // Update the squared differences from the mean, using the new
            // mean in the process.
            if( this.sumSquaredDifferences == null )
            {
                this.sumSquaredDifferences =
                    MatrixFactory.getDefault().createIdentity(dim,dim);
                this.sumSquaredDifferences.scaleEquals(this.getDefaultCovariance());
            }
            Vector delta2 = value.minus( this.mean );
            this.sumSquaredDifferences.plusEquals( delta.outerProduct(delta2) );
            
        }

        @Override
        public MultivariateGaussian.PDF create()
        {
            return new MultivariateGaussian.PDF(
                this.getMean(), this.getCovariance() );
        }

        @Override
        public void create(
            MultivariateGaussian distribution)
        {
            distribution.setMean( this.getMean() );
            distribution.setCovariance( this.getCovariance() );
        }

        /**
         * Getter for defaultCovariance
         * @return
         * Default covariance of the distribution
         */
        public double getDefaultCovariance()
        {
            return this.defaultCovariance;
        }

        /**
         * Setter for defaultCovariance
         * @param defaultCovariance
         * Default covariance of the distribution
         */
        public void setDefaultCovariance(
            double defaultCovariance)
        {
            this.defaultCovariance = defaultCovariance;
        }

        /**
         * Getter for mean
         * @return 
         * The mean of the Gaussian
         */
        public Vector getMean()
        {
            return this.mean;
        }

        /**
         * Getter for sumSquaredDifferences
         * @return
         * This is the sum-squared differences
         */
        public Matrix getSumSquaredDifferences()
        {
            return this.sumSquaredDifferences;
        }

        /**
         * Gets the covariance of the Gaussian.
         *
         * @return
         *      The covariance.
         */
        public Matrix getCovariance()
        {
            if( this.count <= 0 )
            {
                return null;
            }
            else if( this.count == 1 )
            {
                // This allows the default variance to be used.
                return this.sumSquaredDifferences.clone();
            }
            else
            {
                return this.sumSquaredDifferences.scale( 1.0/(this.count - 1.0) );
            }
        }

    }


    /**
     * The estimator that creates a MultivariateGaussian from a stream of
     * values.
     */
    public static class IncrementalEstimator
        extends AbstractIncrementalEstimator<Vector, MultivariateGaussian, MultivariateGaussian.SufficientStatistic>
    {

        /**
         * Default covariance, {@value}.
         */
        public static final double DEFAULT_COVARIANCE = MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;

        /**
         * Default covariance of the distribution
         */
        private double defaultCovariance;

        /**
         * Default constructor
         */
        public IncrementalEstimator()
        {
            this( DEFAULT_COVARIANCE );
        }

        /**
         * Creates a new instance of IncrementalEstimator
         * @param defaultCovariance
         * Default covariance of the distribution
         */
        public IncrementalEstimator(
            double defaultCovariance)
        {
            super();
            this.setDefaultCovariance(defaultCovariance);
        }

        /**
         * Getter for defaultCovariance
         * @return
         * Default covariance of the distribution
         */
        public double getDefaultCovariance()
        {
            return this.defaultCovariance;
        }

        /**
         * Setter for defaultCovariance
         * @param defaultCovariance
         * Default covariance of the distribution
         */
        public void setDefaultCovariance(
            double defaultCovariance)
        {
            this.defaultCovariance = defaultCovariance;
        }

        @Override
        public MultivariateGaussian.SufficientStatistic createInitialLearnedObject()
        {
            return new MultivariateGaussian.SufficientStatistic(
                this.getDefaultCovariance() );
        }        

    }

    /**
     * Implements the sufficient statistics of the MultivariateGaussian
     * while estimating the inverse of the covariance matrix.  This is only
     * slightly more computationally intensive than estimating the covariance
     * directly, but does not require a single matrix inversion.  This is
     * useful when it's the covariance inverse ("precision") that you're
     * interested in.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Sherman–Morrison formula",
        type=PublicationType.WebPage,
        year=2011,
        url="http://en.wikipedia.org/wiki/Sherman%E2%80%93Morrison_formula"
    )
    public static class SufficientStatisticCovarianceInverse
        extends AbstractSufficientStatistic<Vector, MultivariateGaussian>
    {

        /**
         * Default covariance of the statistics, {@value}.
         */
        public static final double DEFAULT_COVARIANCE_INVERSE =
            1.0/MultivariateGaussian.MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;

        /**
         * The mean of the Gaussian
         */
        private Vector mean;

        /**
         * This is the sum-squared differences
         */
        private Matrix sumSquaredDifferencesInverse;

        /**
         * Default covariance inverse of the distribution
         */
        protected double defaultCovarianceInverse;

        /**
         * Default constructor
         */
        public SufficientStatisticCovarianceInverse()
        {
            this( DEFAULT_COVARIANCE_INVERSE );
        }

        /**
         * Creates a new instance of SufficientStatisticCovarianceInverse
         * @param defaultCovarianceInverse
         * Default covariance inverse of the distribution
         */
        public SufficientStatisticCovarianceInverse(
            double defaultCovarianceInverse)
        {
            super();
            this.clear();
            this.defaultCovarianceInverse = defaultCovarianceInverse;
        }

        @Override
        public SufficientStatisticCovarianceInverse clone()
        {
            return (SufficientStatisticCovarianceInverse) super.clone();
        }

        /**
         * Resets this set of sufficient statistics to its empty state.
         */
        public void clear()
        {
            this.count = 0;
            this.mean = null;
            this.sumSquaredDifferencesInverse = null;
        }

        @Override
        public void update(
            Vector value)
        {
            // We've added another value.
            this.count++;

            // Compute the difference between the value and the current mean.
            final int dim = value.getDimensionality();
            if( this.mean == null )
            {
                this.mean = VectorFactory.getDefault().createVector(dim);
            }
            Vector delta = value.minus( this.mean );

            // Update the mean based on the difference between the value
            // and the mean along with the new count.
            this.mean.plusEquals( delta.scale(1.0/this.count) );

            // Update the squared differences from the mean, using the new
            // mean in the process.
            if( this.sumSquaredDifferencesInverse == null )
            {
                this.sumSquaredDifferencesInverse =
                    MatrixFactory.getDefault().createIdentity(dim,dim);
                this.sumSquaredDifferencesInverse.scaleEquals(this.getDefaultCovarianceInverse());
            }
            Vector delta2 = value.minus( this.mean );

            // This is the Sherman–Morrison formula:
            // inv(A+uv') = inv(A)-(inv(A)uv'inv(A))/(1+v'inv(A)*u)
            Vector Aiu = this.sumSquaredDifferencesInverse.times( delta );
            Vector vtAi = delta2.times(this.sumSquaredDifferencesInverse);
            double denom = 1.0 + delta2.dotProduct(Aiu);
            vtAi.scaleEquals(1.0/denom);
            Matrix update = Aiu.outerProduct(vtAi);
            this.sumSquaredDifferencesInverse.minusEquals(update);
        }

        @Override
        public MultivariateGaussian.PDF create()
        {
            final Vector m = this.getMean();
            MultivariateGaussian.PDF retval =
                new MultivariateGaussian.PDF( m.getDimensionality() );
            retval.setMean(m);
            retval.setCovarianceInverse(this.getCovarianceInverse());
            return retval;
        }

        @Override
        public void create(
            MultivariateGaussian distribution)
        {
            distribution.setMean( this.getMean() );
            distribution.setCovarianceInverse( this.getCovarianceInverse() );
        }

        /**
         * Getter for defaultCovarianceInverse
         * @return
         * Default covariance Inverse of the distribution
         */
        public double getDefaultCovarianceInverse()
        {
            return this.defaultCovarianceInverse;
        }

        /**
         * Setter for defaultCovarianceInverse
         * @param defaultCovarianceInverse
         * Default covariance Inverse of the distribution
         */
        public void setDefaultCovariance(
            double defaultCovarianceInverse)
        {
            this.defaultCovarianceInverse = defaultCovarianceInverse;
        }

        /**
         * Getter for mean
         * @return
         * The mean of the Gaussian
         */
        public Vector getMean()
        {
            return this.mean;
        }

        /**
         * Getter for sumSquaredDifferences
         * @return
         * This is the sum-squared differences
         */
        public Matrix getSumSquaredDifferencesInverse()
        {
            return this.sumSquaredDifferencesInverse;
        }

        /**
         * Gets the covariance Inverse of the Gaussian.
         *
         * @return
         *      The covariance.
         */
        public Matrix getCovarianceInverse()
        {
            if( this.count <= 0 )
            {
                return null;
            }
            else if( this.count == 1 )
            {
                // This allows the default variance to be used.
                return this.sumSquaredDifferencesInverse.clone();
            }
            else
            {
                return this.sumSquaredDifferencesInverse.scale( this.count - 1.0 );
            }
        }

    }

    /**
     * The estimator that creates a MultivariateGaussian from a stream of
     * values by estimating the mean and covariance inverse (as opposed to
     * the covariance directly), without ever performing a matrix inversion.
     * This is useful when you're interested in the covariance inverse
     * (precision) instead of the covariance itself.
     */
    public static class IncrementalEstimatorCovarianceInverse
        extends AbstractIncrementalEstimator<Vector, MultivariateGaussian, MultivariateGaussian.SufficientStatisticCovarianceInverse>
    {

        /**
         * Default covariance Inverse, {@value}.
         */
        public static final double DEFAULT_COVARIANCE_INVERSE = 1.0/MaximumLikelihoodEstimator.DEFAULT_COVARIANCE;

        /**
         * Default covariance Inverse of the distribution
         */
        private double defaultCovarianceInverse;

        /**
         * Default constructor
         */
        public IncrementalEstimatorCovarianceInverse()
        {
            this( DEFAULT_COVARIANCE_INVERSE );
        }

        /**
         * Creates a new instance of IncrementalEstimatorCovarianceInverse
         * @param defaultCovarianceInverse
         * Default covariance Inverse of the distribution
         */
        public IncrementalEstimatorCovarianceInverse(
            double defaultCovarianceInverse )
        {
            super();
            this.setDefaultCovarianceInverse(defaultCovarianceInverse);
        }

        /**
         * Getter for defaultCovarianceInverse
         * @return
         * Default covariance Inverse of the distribution
         */
        public double getDefaultCovarianceInverse()
        {
            return this.defaultCovarianceInverse;
        }

        /**
         * Setter for defaultCovarianceInverse
         * @param defaultCovarianceInverse
         * Default covariance Inverse of the distribution
         */
        public void setDefaultCovarianceInverse(
            double defaultCovarianceInverse)
        {
            this.defaultCovarianceInverse = defaultCovarianceInverse;
        }

        @Override
        public MultivariateGaussian.SufficientStatisticCovarianceInverse createInitialLearnedObject()
        {
            return new MultivariateGaussian.SufficientStatisticCovarianceInverse(
                this.getDefaultCovarianceInverse() );
        }

    }


}
