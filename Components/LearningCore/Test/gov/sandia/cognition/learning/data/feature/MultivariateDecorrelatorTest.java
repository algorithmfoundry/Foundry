/*
 * File:                MultivariateDecorrelatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.math.matrix.mtj.decomposition.CholeskyDecompositionMTJ;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MultivariateDecorrelatorTest.
 *
 * @author krdixon
 */
public class MultivariateDecorrelatorTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class MultivariateDecorrelatorTest.
     * @param testName Name of the test.
     */
    public MultivariateDecorrelatorTest(
        String testName)
    {
        super(testName);
    }


    public MultivariateDecorrelator createInstance()
    {
        Vector mean = this.createRandomInput();
        Matrix A = MatrixFactory.getDefault().copyColumnVectors(
            this.createRandomInput(), this.createRandomInput(), this.createRandomInput() );
        Matrix C = A.transpose().times(A);
        return new MultivariateDecorrelator( mean, C );
    }

    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }

    /**
     * Tests the constructors of class MultivariateDecorrelatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateDecorrelator f = new MultivariateDecorrelator();
        assertNotNull( f );
        assertNull( f.getGaussian() );
        assertNull( f.getCovarianceInverseSquareRoot() );
        
        f = this.createInstance();
        assertNotNull( f );
        assertNotNull( f.getGaussian() );
        assertNotNull( f.getMean() );
        assertNotNull( f.getCovariance() );
        assertNotNull( f.getCovarianceInverseSquareRoot() );
    }

    /**
     * Test of clone method, of class MultivariateDecorrelator.
     */
    public void testClone()
    {
        System.out.println("clone");
        MultivariateDecorrelator instance = this.createInstance();
        MultivariateDecorrelator clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getGaussian(), clone.getGaussian() );
        assertNotSame( instance.getCovarianceInverseSquareRoot(), clone.getCovarianceInverseSquareRoot() );
        assertEquals( instance.getMean(), clone.getMean() );
        assertEquals( instance.getCovariance(), clone.getCovariance() );

    }

    /**
     * Test of evaluate method, of class MultivariateDecorrelator.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        MultivariateDecorrelator instance = this.createInstance();
        Vector input = this.createRandomInput();
        Vector expected = input.minus(instance.getMean()).times( instance.getCovarianceInverseSquareRoot() );
        Vector result = instance.evaluate(input);
        if( !expected.equals( result, TOLERANCE ) )
        {
            assertEquals( expected, result );
        }
    }

    /**
     * Test of getMean method, of class MultivariateDecorrelator.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        MultivariateDecorrelator instance = this.createInstance();
        assertNotNull( instance.getMean() );
        
        instance = new MultivariateDecorrelator();
        try
        {
            instance.getMean();
            fail( "No gaussian, throws NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getCovariance method, of class MultivariateDecorrelator.
     */
    public void testGetCovariance()
    {
        System.out.println("getCovariance");
        MultivariateDecorrelator instance = this.createInstance();
        assertNotNull( instance.getCovariance() );

        instance = new MultivariateDecorrelator();
        try
        {
            instance.getCovariance();
            fail( "No gaussian, throws NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getGaussian method, of class MultivariateDecorrelator.
     */
    public void testGetGaussian()
    {
        System.out.println("getGaussian");
        MultivariateDecorrelator instance = this.createInstance();
        assertNotNull( instance.getGaussian() );
    }

    /**
     * Test of setGaussian method, of class MultivariateDecorrelator.
     */
    public void testSetGaussian()
    {
        System.out.println("setGaussian");
        MultivariateDecorrelator instance = this.createInstance();
        MultivariateGaussian gaussian = instance.getGaussian();
        assertNotNull( gaussian );
        instance.setGaussian(null);
        assertNull( instance.getGaussian() );
        assertNull( instance.getCovarianceInverseSquareRoot() );

        instance.setGaussian(gaussian);
        assertNotSame( gaussian, instance.getGaussian() );
        assertNotNull( instance.getCovarianceInverseSquareRoot() );
        assertEquals( gaussian.getMean(), instance.getMean() );
        assertEquals( gaussian.getCovariance(), instance.getCovariance() );

    }

    /**
     * Test of getCovarianceInverseSquareRoot method, of class MultivariateDecorrelator.
     */
    public void testGetCovarianceInverseSquareRoot()
    {
        System.out.println("getCovarianceInverseSquareRoot");
        MultivariateDecorrelator instance = this.createInstance();
        Matrix sqrt = CholeskyDecompositionMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( instance.getCovariance().inverse() ) ).getR();
        if( !sqrt.equals( instance.getCovarianceInverseSquareRoot(), TOLERANCE ) )
        {
            assertEquals( sqrt, instance.getCovarianceInverseSquareRoot() );
        }
    }

    public ArrayList<Vector> createDataset()
    {
        final int num = 100;
        ArrayList<Vector> data = new ArrayList<Vector>( num );
        for( int n = 0; n < num; n++ )
        {
            data.add( this.createRandomInput() );
        }
        return data;
    }

    /**
     * Test of learnFullCovariance method, of class MultivariateDecorrelator.
     */
    public void testLearnFullCovariance()
    {
        System.out.println("learnFullCovariance");

        MultivariateDecorrelator.FullCovarianceLearner learner =
            new MultivariateDecorrelator.FullCovarianceLearner();
        learner.setDefaultCovariance(0.0);
        ArrayList<Vector> data = this.createDataset();
        MultivariateDecorrelator instance = learner.learn( data );

        Vector mean = MultivariateStatisticsUtil.computeMean(data);
        if( !mean.equals( instance.getMean() ) )
        {
            assertEquals( mean, instance.getMean() );
        }

        Matrix covariance = MultivariateStatisticsUtil.computeVariance(data,mean);
        if( !covariance.equals(instance.getCovariance(), TOLERANCE ) )
        {
            assertEquals( covariance, instance.getCovariance() );
        }

    }

    /**
     * Test of learnDiagonalCovariance method, of class MultivariateDecorrelator.
     */
    public void testLearnDiagonalCovariance()
    {
        System.out.println("learnDiagonalCovariance");
        ArrayList<Vector> data = this.createDataset();
        MultivariateDecorrelator.DiagonalCovarianceLearner learner =
            new MultivariateDecorrelator.DiagonalCovarianceLearner();
        learner.setDefaultCovariance(0.0);

        MultivariateDecorrelator instance = learner.learn(data);

        Vector mean = MultivariateStatisticsUtil.computeMean(data);
        if( !mean.equals( instance.getMean() ) )
        {
            assertEquals( mean, instance.getMean() );
        }

        Matrix Chat = instance.getCovariance();
        final int M = mean.getDimensionality();
        assertEquals( M, Chat.getNumRows() );
        assertEquals( M, Chat.getNumColumns() );
        double biasedAdjustment = (data.size()-1.0)/data.size();
        for( int i = 0; i < M; i++ )
        {
            ArrayList<Double> di = new ArrayList<Double>( data.size() );
            for( Vector v : data )
            {
                di.add(v.getElement(i));
            }
            for( int j = 0; j < M; j++ )
            {
                if( i == j )
                {
                    double variance = biasedAdjustment * UnivariateStatisticsUtil.computeVariance(di);
                    assertEquals( variance, Chat.getElement(i,i), TOLERANCE );
                }
                else
                {
                    assertEquals( 0.0, Chat.getElement(i, j) );
                }
            }

        }

    }

}
