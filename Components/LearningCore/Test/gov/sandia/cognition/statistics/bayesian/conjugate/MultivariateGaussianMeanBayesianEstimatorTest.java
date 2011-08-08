/*
 * File:                MultivariateGaussianMeanBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;

/**
 * Unit tests for MultivariateGaussianMeanBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class MultivariateGaussianMeanBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Vector,Vector,MultivariateGaussian>
{

    /**
     * Tests for class MultivariateGaussianMeanBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public MultivariateGaussianMeanBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultivariateGaussianMeanBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        MultivariateGaussianMeanBayesianEstimator instance =
            new MultivariateGaussianMeanBayesianEstimator();

        Matrix C = MatrixFactory.getDefault().createIdentity(DEFAULT_DIM,DEFAULT_DIM);
        instance = new MultivariateGaussianMeanBayesianEstimator( C );
        assertEquals( C, instance.getKnownCovarianceInverse() );

        MultivariateGaussian g = this.createConditionalDistribution();
        instance = new MultivariateGaussianMeanBayesianEstimator( C, g );
        assertEquals( C, instance.getKnownCovarianceInverse() );
        assertSame( g, instance.getInitialBelief() );

    }

    /**
     * Test of getKnownCovarianceInverse method, of class MultivariateGaussianMeanBayesianEstimator.
     */
    public void testGetKnownCovarianceInverse()
    {
        System.out.println("getKnownCovarianceInverse");
        MultivariateGaussianMeanBayesianEstimator instance =
            new MultivariateGaussianMeanBayesianEstimator();
        assertNotNull( instance.getKnownCovarianceInverse() );
    }

    /**
     * Test of setKnownCovarianceInverse method, of class MultivariateGaussianMeanBayesianEstimator.
     */
    public void testSetKnownCovarianceInverse()
    {
        System.out.println("setKnownCovarianceInverse");
        MultivariateGaussianMeanBayesianEstimator instance =
            new MultivariateGaussianMeanBayesianEstimator();
        Matrix C = instance.getKnownCovarianceInverse();
        assertNotNull( C );

        // Definitely not symmetric now.
        Matrix D = MatrixFactory.getDefault().createUniformRandom(DEFAULT_DIM,DEFAULT_DIM, 1.0, 2.0, RANDOM );
        D.setElement(0,1,0.5);

        Matrix C2 = D.transpose().times( D );
        instance.setKnownCovarianceInverse(C2);
        if( !C2.equals(instance.getKnownCovarianceInverse(), TOLERANCE) )
        {
            assertEquals( C2, instance.getKnownCovarianceInverse() );
        }

        try
        {
            instance.setKnownCovarianceInverse(D);
            fail( "D is not symmetric" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        Matrix Z = MatrixFactory.getDefault().createMatrix(DEFAULT_DIM,DEFAULT_DIM);
        try
        {
            instance.setKnownCovarianceInverse(Z);
            fail( "Z is not invertible" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

    /**
     * Default dimensionality.
     */
    public static final int DEFAULT_DIM = 3;

    @Override
    public MultivariateGaussianMeanBayesianEstimator createInstance()
    {
        return new MultivariateGaussianMeanBayesianEstimator(
            MatrixFactory.getDefault().createIdentity(DEFAULT_DIM,DEFAULT_DIM) );
    }

    @Override
    public MultivariateGaussian createConditionalDistribution()
    {
        return new MultivariateGaussian(
            VectorFactory.getDefault().createUniformRandom(DEFAULT_DIM, -1.0, 1.0, RANDOM ),
            MatrixFactory.getDefault().createIdentity(DEFAULT_DIM,DEFAULT_DIM).scale(RANDOM.nextDouble()) );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
        
        MultivariateGaussianMeanBayesianEstimator instance =
            new MultivariateGaussianMeanBayesianEstimator(
                MatrixFactory.getDefault().createIdentity(1,1).scale(1.0/4.0),
                new MultivariateGaussian(
                    VectorFactory.getDefault().copyValues(30.0),
                    MatrixFactory.getDefault().createIdentity(1,1).scale(16.0) ) );



        ArrayList<Vector> data = new ArrayList<Vector>( 12 );
        for( int i = 0; i < 12; i++ )
        {
            data.add( VectorFactory.getDefault().copyValues(32.0) );
        }

        MultivariateGaussian result = instance.learn(data);
        assertEquals( 0.3265306, result.getCovariance().getElement(0,0), TOLERANCE );
        assertEquals( 31.959184, result.getMean().getElement(0), TOLERANCE );
    }

}
