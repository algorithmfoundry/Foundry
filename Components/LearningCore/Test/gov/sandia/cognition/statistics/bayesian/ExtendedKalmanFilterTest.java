/*
 * File:                ExtendedKalmanFilterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 13, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.learning.function.vector.SquashedMatrixMultiplyVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.signals.LinearDynamicalSystem;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;

/**
 * Unit tests for ExtendedKalmanFilterTest.
 *
 * @author krdixon
 */
public class ExtendedKalmanFilterTest
    extends RecursiveBayesianEstimatorTestHarness<Vector,Vector,MultivariateGaussian>
{

    /**
     * Tests for class ExtendedKalmanFilterTest.
     * @param testName Name of the test.
     */
    public ExtendedKalmanFilterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class ExtendedKalmanFilterTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter();
        assertNull( ekf.getMotionModel() );
        assertNull( ekf.getObservationModel() );

        int stateDim = 2;
        int outputDim = 1;

        SquashedMatrixMultiplyVectorFunction observationModel =
            new SquashedMatrixMultiplyVectorFunction(
                stateDim, outputDim, new AtanFunction() );
        StateSummer motionModel = new StateSummer(stateDim);
        Vector input = VectorFactory.getDefault().createVector(stateDim);
        Matrix modelCovariance =
            MatrixFactory.getDefault().createIdentity(stateDim,stateDim);
        Matrix measurementCovariance =
            MatrixFactory.getDefault().createIdentity(outputDim, outputDim);
        ekf = new ExtendedKalmanFilter(
            motionModel, observationModel, input,
            modelCovariance, measurementCovariance);
        assertSame( motionModel, ekf.getMotionModel() );
        assertSame( observationModel, ekf.getObservationModel() );
        assertSame( modelCovariance, ekf.getModelCovariance() );
        assertSame( measurementCovariance, ekf.getMeasurementCovariance() );
        assertSame( input, ekf.getCurrentInput() );

    }

    /**
     * Test of clone method, of class ExtendedKalmanFilter.
     */
    public void testClone()
    {
        System.out.println("clone");
        ExtendedKalmanFilter instance = this.createInstance();
        ExtendedKalmanFilter clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getMotionModel() );
        assertNotSame( instance.getMotionModel(), clone.getMotionModel() );
        assertNotNull( clone.getObservationModel() );
        assertNotSame( instance.getMotionModel(), clone.getMotionModel() );
    }

    public static class StateSummer
        extends AbstractStatefulEvaluator<Vector,Vector,Vector>
    {

        int dim;

        public StateSummer(
            int dim)
        {
            this.dim = dim;
        }

        public Vector createDefaultState()
        {
            return VectorFactory.getDefault().createVector(dim,1.0);
        }

        public Vector evaluate(
            Vector input)
        {
            Vector state = this.getState();
            state.scaleEquals(0.9);
            state.plusEquals(input);
            this.setState(state);
            return state;
        }
        
    }

    @Override
    public ExtendedKalmanFilter createInstance()
    {
        int stateDim = 2;
        int outputDim = 1;

        SquashedMatrixMultiplyVectorFunction observationModel =
            new SquashedMatrixMultiplyVectorFunction(
                stateDim, outputDim, new AtanFunction() );
        StateSummer motionModel = new StateSummer(stateDim);
        Vector input = VectorFactory.getDefault().createVector(stateDim);
        Matrix modelCovariance =
            MatrixFactory.getDefault().createIdentity(stateDim,stateDim);
        Matrix measurementCovariance =
            MatrixFactory.getDefault().createIdentity(outputDim, outputDim);
        return new ExtendedKalmanFilter(
            motionModel, observationModel, input,
            modelCovariance, measurementCovariance);
    }

    @Override
    public Distribution<Vector> createConditionalDistribution()
    {
        Vector mean = VectorFactory.getDefault().copyValues( RANDOM.nextGaussian() );
        Matrix covariance = MatrixFactory.getDefault().createMatrix(1,1);
        covariance.setElement(0, 0, RANDOM.nextDouble());
        return new MultivariateGaussian(mean, covariance);
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        // EKF and KF should be approximately equal for a LDS
        final int dim = 2;
        Matrix A = MatrixFactory.getDefault().createIdentity(dim, dim);
        Matrix B = MatrixFactory.getDefault().createIdentity(dim, dim);
        Matrix C = MatrixFactory.getDefault().createIdentity(dim, dim);
        LinearDynamicalSystem model = new LinearDynamicalSystem( A, B, C );
        MatrixMultiplyVectorFunction outputModel =
            new MatrixMultiplyVectorFunction( C );

        Vector input = VectorFactory.getDefault().createVector(dim,0.1);
        Matrix modelCovariance = MatrixFactory.getDefault().createIdentity(dim,dim);
        Matrix outputCovariance = MatrixFactory.getDefault().createIdentity(dim,dim);
        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
            model.clone(), outputModel, input, modelCovariance, outputCovariance );
        KalmanFilter kalman = new KalmanFilter(
            model.clone(), modelCovariance, outputCovariance );

        MultivariateGaussian noiseMaker = new MultivariateGaussian(
            VectorFactory.getDefault().createVector(dim), outputCovariance );
        ArrayList<Vector> noise = noiseMaker.sample(RANDOM, 100);
        ArrayList<Vector> ks = new ArrayList<Vector>( noise.size() );
        for( int n = 0; n < noise.size(); n++ )
        {
            ks.add( model.evaluate(input).plus( noise.get(n) ) );
        }

        MultivariateGaussian gekf = ekf.learn(ks);
        MultivariateGaussian gk = kalman.learn(ks);

        System.out.println( "EKF:\n" + gekf );
        System.out.println( "Kalman:\n" + gk );
        final double EPS = 1e-1;
        Vector m1 = gk.getMean();
        Vector m2 = gekf.getMean();
        if( !m1.equals(m2,EPS) )
        {
            assertEquals( m1, m2 );
        }

        Matrix C1 = gk.getCovariance();
        Matrix C2 = gekf.getCovariance();
        if( !C1.equals(C2,EPS) )
        {
            assertEquals( C1, C2 );
        }

    }

}
