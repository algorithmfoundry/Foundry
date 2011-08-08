/*
 * File:                MultivariateStatisticsUtil.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.Collection;

/**
 * Some static methods for computing generally useful multivariate statistics.
 * 
 * @author  Kevin Dixon
 * @since   2.0
 */
public class MultivariateStatisticsUtil
{

    /**
     * Computes the arithmetic sum of the dataset
     *
     * @param   <RingType> The type of data to compute the sum over, which must
     *      implement the {@code Ring} interface.
     * @param data
     * Dataset to consider
     * @return
     * Arithmetic sum of the given dataset
     */
    static public <RingType extends Ring<RingType>> RingType computeSum(
        Iterable<? extends RingType> data )
    {

        RingAccumulator<RingType> sum = new RingAccumulator<RingType>( data );
        return sum.getSum();

    }

    /**
     * Computes the arithmetic mean (average, expectation, first central moment)
     * of a dataset
     *
     * @param   <RingType> The type of data to compute the sum over, which must
     *      implement the {@code Ring} interface.
     * @param data
     * Collection of Vectors to consider
     * @return
     * Arithmetic mean of the given dataset
     */
    static public <RingType extends Ring<RingType>> RingType computeMean(
        Collection<? extends RingType> data )
    {

        RingType mean = computeSum( data );
        if (data.size() <= 0)
        {
            mean = null;
        }
        else
        {
            mean.scaleEquals( 1.0 / data.size() );
        }

        return mean;

    }

    /**
     * Computes the variance (second central moment, squared standard deviation)
     * of a dataset.  Computes the mean first, then computes the variance.  If
     * you already have the mean, then use the two-argument
     * computeVariance(data,mean) method to save duplication of effort
     * @param data
     * Collection of Vector to consider
     * @return
     * Variance of the given dataset
     */
    static public Matrix computeVariance(
        Collection<? extends Vector> data )
    {
        Pair<Vector,Matrix> result = computeMeanAndCovariance(data);
        return (result != null) ? result.getSecond() : null;
    }

    /**
     * Computes the variance (second central moment, squared standard deviation)
     * of a dataset
     * @param data
     * Collection of Doubles to consider
     * @param mean
     * Pre-computed mean (or central value) of the dataset
     * @return
     * Full covariance matrix of the given dataset
     */
    static public Matrix computeVariance(
        Collection<? extends Vector> data,
        Vector mean )
    {

        if( data.size() == 0 )
        {
            return null;
        }

        RingAccumulator<Matrix> scatter = new RingAccumulator<Matrix>();
        for (Vector value : data)
        {
            Vector delta = value.minus( mean );
            scatter.accumulate( delta.outerProduct( delta ) );
        }

        Matrix covariance;
        int num = data.size();
        if (num >= 2)
        {
            covariance = scatter.getSum().scale( 1.0 / (num - 1) );
        }
        else
        {
            covariance = scatter.getSum();
            covariance.zero();
        }

        return covariance;

    }

    /**
     * Computes the mean and unbiased covariance Matrix of a multivariate
     * data set.
     * @param data
     * Data set to consider
     * @return
     * Mean and unbiased Covariance
     */
    public static Pair<Vector,Matrix> computeMeanAndCovariance(
        Iterable<? extends Vectorizable> data )
    {

        RingAccumulator<Vector> sum = new RingAccumulator<Vector>();
        Matrix sum2 = null;
        int dim = 0;
        int n = 0;

        for( Vectorizable vectorizable : data )
        {
            Vector x = vectorizable.convertToVector();
            sum.accumulate( x );
            if( sum2 == null )
            {
                dim = x.getDimensionality();
                sum2 = MatrixFactory.getDefault().createMatrix(dim, dim);
            }

            for( int i = 0; i < dim; i++ )
            {
                for( int j = 0; j < dim; j++ )
                {
                    double v = sum2.getElement(i, j);
                    v += x.getElement(i) * x.getElement(j);
                    sum2.setElement(i, j, v);
                }
            }
            n++;
        }

        Vector mean;
        Matrix C;
        if( n >= 2 )
        {
            Vector s2 = sum.getSum().scale( 1.0/(n-1) );
            mean = sum.getSum();
            mean.scaleEquals(1.0/n);
            C = sum2.scale( 1.0/(n-1) ).minus( mean.outerProduct(s2) );
        }
        else if( n == 1 )
        {
            mean = sum.getSum();
            C = MatrixFactory.getDefault().createMatrix(dim, dim);
        }
        else
        {
            return null;
        }

        return new DefaultPair<Vector, Matrix>( mean, C );

    }

    /**
     * Computes the mean and biased covariance Matrix of a multivariate
     * weighted data set.
     * @param data
     * Data set to consider
     * @return
     * Mean and biased Covariance
     */
    public static Pair<Vector,Matrix> computeWeightedMeanAndCovariance(
        Iterable<? extends WeightedValue<? extends Vectorizable>> data )
    {

        RingAccumulator<Vector> s1 = new RingAccumulator<Vector>();
        int dim = 0;
        Matrix s2 = null;

        int N = 0;
        double weightSum = 0.0;
        for( WeightedValue<? extends Vectorizable> x : data )
        {
            N++;
            final Vector v2 = x.getValue().convertToVector();
            if( s2 == null )
            {
                dim = v2.getDimensionality();
                s2 = MatrixFactory.getDefault().createMatrix(dim, dim);
            }

            final double weight = x.getWeight();
            if( weight != 0.0 )
            {
                weightSum += weight;
                Vector wx = v2;
                if( weight != 1.0 )
                {
                    // Can't use scaleEquals because we may need the original data
                    wx = wx.scale( weight );
                }
                s1.accumulate( wx );
                for( int i = 0; i < dim; i++ )
                {
                    for( int j = 0; j < dim; j++ )
                    {
                       double v = s2.getElement(i, j);
                       v += wx.getElement(i) * v2.getElement(j);
                       s2.setElement(i, j, v);
                    }
                }
            }
        }

        Vector mean;
        Matrix covariance;
        if( N >= 2 )
        {
            mean = s1.getSum().scale( 1.0 / weightSum );
            covariance = s2.scale( 1.0/weightSum ).minus( mean.outerProduct(mean) );
        }
        else if( N == 1 )
        {
            mean = s1.getSum().scale( 1.0 / weightSum );
            covariance = MatrixFactory.getDefault().createMatrix(dim, dim);
        }
        else
        {
            return null;
        }

        return new DefaultPair<Vector, Matrix>( mean, covariance );

    }

}
