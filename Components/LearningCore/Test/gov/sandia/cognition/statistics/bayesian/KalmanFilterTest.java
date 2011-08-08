/*
 * File:                KalmanFilterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.signals.LinearDynamicalSystem;
import gov.sandia.cognition.statistics.bayesian.conjugate.MultivariateGaussianMeanBayesianEstimator;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;

/**
 * Unit tests for KalmanFilterTest.
 *
 * @author krdixon
 */
public class KalmanFilterTest
    extends RecursiveBayesianEstimatorTestHarness<Vector,Vector,MultivariateGaussian>
{

    /**
     * Tests for class KalmanFilterTest.
     * @param testName Name of the test.
     */
    public KalmanFilterTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Creates instance
     * @return
     * instance
     */
    @Override
    public KalmanFilter createInstance()
    {
        final int stateDim = 2;
        final int inputDim = 2;
        final int outputDim = 1;
        Matrix A = MatrixFactory.getDefault().createIdentity(stateDim,stateDim);
        Matrix B = MatrixFactory.getDefault().createMatrix(stateDim, inputDim);
        Matrix C = MatrixFactory.getDefault().createMatrix(outputDim, stateDim);

        for( int i = 0; i < B.getNumRows(); i++ )
        {
            for( int j = 0; j < B.getNumColumns(); j++ )
            {
                B.setElement(i, j, 1.0);
            }
        }
        for( int i = 0; i < C.getNumRows(); i++ )
        {
            for( int j = 0; j < C.getNumColumns(); j++ )
            {
                C.setElement(i, j, 1.0);
            }
        }

        LinearDynamicalSystem lds = new LinearDynamicalSystem( A, B, C );
        Matrix modelCovariance =
            MatrixFactory.getDefault().createIdentity(stateDim,stateDim);
        Matrix measurementCovariance =
            MatrixFactory.getDefault().createIdentity(outputDim, outputDim);

        return new KalmanFilter( lds, modelCovariance, measurementCovariance );
        
    }

    @Override
    public MultivariateGaussian createConditionalDistribution()
    {
        Vector mean = VectorFactory.getDefault().copyValues( RANDOM.nextGaussian() );
        Matrix covariance = MatrixFactory.getDefault().createMatrix(1,1);
        covariance.setElement(0, 0, RANDOM.nextDouble());
        return new MultivariateGaussian(mean, covariance);
    }

    /**
     * Tests the constructors of class KalmanFilterTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        KalmanFilter instance = new KalmanFilter();
        assertEquals( 1, instance.getModel().getInputDimensionality() );
        assertEquals( 1, instance.getModel().getOutputDimensionality() );
        assertEquals( 1, instance.getModel().getStateDimensionality() );
        assertEquals( 1, instance.getModelCovariance().getNumRows() );
        assertEquals( 1, instance.getModelCovariance().getNumColumns() );
    }

    /**
     * Test of clone method, of class KalmanFilter.
     */
    public void testClone()
    {
        System.out.println("clone");
        KalmanFilter instance = new KalmanFilter();
        KalmanFilter clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone );
        
        assertNotSame( instance.getCurrentInput(), clone.getCurrentInput() );
        assertEquals( instance.getCurrentInput(), clone.getCurrentInput() );

        assertNotSame( instance.getMeasurementCovariance(), clone.getMeasurementCovariance() );
        assertEquals( instance.getMeasurementCovariance(), clone.getMeasurementCovariance() );

        assertNotSame( instance.getModelCovariance(), clone.getModelCovariance() );
        assertEquals( instance.getMeasurementCovariance(), clone.getMeasurementCovariance() );

        assertNotSame( instance.getModel(), clone.getModel() );
        assertNotNull( clone.getModel() );

    }

    /**
     * Tests if the output of the Kalman filter can be equivalent to the
     * conjugate prior estimator.
     */
    @Override
    public void testKnownValues()
    {
        System.out.println( "MVGCOnjugateEquivalent" );

        // An autonomous LDS with identity system matrix...
        // and no model covariance.
        // Without an input, the system won't move.
        // The measurement covariance will be the same as the MVG estimator
        final int dim = 2;
        final Matrix A = MatrixFactory.getDefault().createIdentity(dim, dim);
        final Matrix B = MatrixFactory.getDefault().createMatrix(dim, dim);
        final Matrix C = MatrixFactory.getDefault().createIdentity(dim, dim);
        LinearDynamicalSystem lds = new LinearDynamicalSystem(A, B, C);
        final Matrix modelCovariance = MatrixFactory.getDefault().createMatrix(dim, dim);
        Matrix R = MatrixFactory.getDefault().createUniformRandom(
            dim, dim,-1.0, 1.0, RANDOM);
        final Matrix outputCovariance = R.times(R.transpose());
        KalmanFilter kalman = new KalmanFilter( lds, modelCovariance, outputCovariance);
        kalman.setCurrentInput( VectorFactory.getDefault().createVector(dim) );

        Vector mean = VectorFactory.getDefault().createUniformRandom(
            dim,-2.0, 2.0, RANDOM);
        R = MatrixFactory.getDefault().createUniformRandom(
            dim, dim,-1.0, 1.0, RANDOM);
        Matrix cov = R.times(R.transpose());
        MultivariateGaussian targetDistribution =
            new MultivariateGaussian( mean, cov );
        ArrayList<Vector> samples = targetDistribution.sample(RANDOM, 1000);

        MultivariateGaussianMeanBayesianEstimator conjugateEstimator =
            new MultivariateGaussianMeanBayesianEstimator( outputCovariance.inverse() );

        MultivariateGaussian initialBelief =
            conjugateEstimator.createInitialLearnedObject().clone();

        MultivariateGaussian mvgResult = conjugateEstimator.learn(samples);
        MultivariateGaussian kalmanBelief = initialBelief.clone();
        kalman.update(kalmanBelief, samples);

        System.out.println( "MVG: " + mvgResult );
        System.out.println( "Kalman: " + kalmanBelief );

        if( !mvgResult.getMean().equals( kalmanBelief.getMean(), TOLERANCE ) )
        {
            assertEquals( mvgResult.getMean(), kalmanBelief.getMean() );
        }
        if( !mvgResult.getCovariance().equals( kalmanBelief.getCovariance(), TOLERANCE ) )
        {
            assertEquals( mvgResult.getCovariance(), kalmanBelief.getCovariance() );
        }

    }

    /**
     * Test of predict method, of class KalmanFilter.
     */
    public void testPredict()
    {
        System.out.println("predict");

        KalmanFilter instance = this.createInstance();
        Vector input = VectorFactory.getDefault().createVector(
            instance.getModel().getInputDimensionality(), 1.0 );
        instance.setCurrentInput(input);

        MultivariateGaussian belief = instance.createInitialLearnedObject();
        double lastDet, det = Double.NEGATIVE_INFINITY;
        for( int n = 0; n < 100; n++ )
        {
            lastDet = det;
            MultivariateGaussian beliefBefore = belief.clone();
            instance.predict(belief);

            LinearDynamicalSystem lds = instance.getModel();
            Vector x0 = beliefBefore.getMean();

            Vector estimate = lds.getA().times( x0 ).plus( lds.getB().times( input ) );
            assertEquals( estimate, belief.getMean() );
            
            // Covariance of estimate should be getting bigger each timestep!
            det = belief.getCovariance().logDeterminant().getMagnitude();
            assertTrue( lastDet < det );
        }

    }

    /**
     * Test of measure method, of class KalmanFilter.
     */
    public void testMeasure()
    {
        System.out.println("measure");
        KalmanFilter instance = this.createInstance();
        final int N = instance.getModel().getOutputDimensionality();
        final double t = 10.0;
        Vector measurement = VectorFactory.getDefault().createVector(N,t);

        Vector target = VectorFactory.getDefault().copyValues(t/2,t/2);
        MultivariateGaussian belief = instance.createInitialLearnedObject();
        double lastDelta, delta = Double.POSITIVE_INFINITY;
        for( int i = 0; i < 100; i++ )
        {
            lastDelta = delta;
            instance.measure(belief, measurement);
            Vector err = belief.getMean().minus( target );
            delta = err.norm2();
            assertTrue( delta < lastDelta );
        }

    }

    /**
     * Test of getModel method, of class KalmanFilter.
     */
    public void testGetModel()
    {
        System.out.println("getModel");
        KalmanFilter instance = this.createInstance();
        LinearDynamicalSystem model = instance.getModel();
        assertNotNull( model );
    }

    /**
     * Test of setModel method, of class KalmanFilter.
     */
    public void testSetModel()
    {
        System.out.println("setModel");
        KalmanFilter instance = this.createInstance();
        LinearDynamicalSystem model = instance.getModel();
        assertNotNull( model );
        instance.setModel(null);
        assertNull( instance.getModel() );
        instance.setModel(model);
        assertSame( model, instance.getModel() );

    }

    /**
     * Test of getModelCovariance method, of class KalmanFilter.
     */
    public void testGetModelCovariance()
    {
        System.out.println("getModelCovariance");
        KalmanFilter instance = this.createInstance();
        Matrix modelCovariance = instance.getModelCovariance();
        assertNotNull( modelCovariance );
    }

    /**
     * Test of setModelCovariance method, of class KalmanFilter.
     */
    public void testSetModelCovariance()
    {
        System.out.println("setModelCovariance");
        KalmanFilter instance = this.createInstance();
        Matrix modelCovariance = instance.getModelCovariance();
        assertNotNull( modelCovariance );
        instance.setModelCovariance(null);
        assertNull( instance.getModelCovariance() );
        instance.setModelCovariance(modelCovariance);
        assertSame( modelCovariance, instance.getModelCovariance() );
    }

}
