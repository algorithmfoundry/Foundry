/*
 * File:                GaussianProcessRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 18, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;


import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.kernel.RadialBasisKernel;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminantWithBias;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for GaussianProcessRegressionTest.
 *
 * @author krdixon
 */
public class GaussianProcessRegressionTest
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

//    /**
//     * RadialBasisKernel
//     */
//    public static class RadialBasisKernel
//        extends AbstractCloneableSerializable
//        implements Kernel<Double>
//    {
//
//        /**
//         * Sigma
//         */
//        double sigma;
//
//        /**
//         * Default constructor
//         */
//        public RadialBasisKernel()
//        {
//            this( 1.0 );
//        }
//
//        /**
//         * Constructor
//         * @param sigma
//         * sigma
//         */
//        public RadialBasisKernel(
//            double sigma)
//        {
//            this.sigma = sigma;
//        }
//
//        @Override
//        public double evaluate(
//            Double x,
//            Double y)
//        {
//            final double delta = (x + y);
//            final double distance = delta*delta;
//            return Math.exp( -distance / (-this.sigma*this.sigma) );
//        }
//
//    }

    /**
     * Tests for class GaussianProcessRegressionTest.
     * @param testName Name of the test.
     */
    public GaussianProcessRegressionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates instance
     * @return
     * instance
     */
    public GaussianProcessRegression<Vectorizable> createInstance()
    {
        GaussianProcessRegression<Vectorizable> instance =
            new GaussianProcessRegression<Vectorizable>();
        instance.setOutputVariance(0.1);

        instance.setKernel( new RadialBasisKernel() );
        return instance;
    }

    /**
     * Tests the constructors of class GaussianProcessRegressionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        GaussianProcessRegression<Double> instance =
            new GaussianProcessRegression<Double>();
        assertNull( instance.getKernel() );
        assertEquals( GaussianProcessRegression.DEFAULT_MEASUREMENT_VARIANCE, instance.getOutputVariance() );
        

    }

    /**
     * Test of clone method, of class GaussianProcessRegression.
     */
    public void testClone()
    {
        System.out.println("clone");
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        GaussianProcessRegression<Vectorizable> clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getKernel(), clone.getKernel() );
        assertNotNull( clone.getKernel() );
        assertEquals( instance.getOutputVariance(), clone.getOutputVariance() );
    }

    /**
     * Test of getOutputVariance method, of class GaussianProcessRegression.
     */
    public void testGetOutputVariance()
    {
        System.out.println("getOutputVariance");
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        assertTrue( instance.getOutputVariance() >= 0.0 );
    }

    /**
     * Test of setOutputVariance method, of class GaussianProcessRegression.
     */
    public void testSetOutputVariance()
    {
        System.out.println("setOutputVariance");
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        double outputVariance = RANDOM.nextDouble();
        instance.setOutputVariance(outputVariance);
        assertEquals( outputVariance, instance.getOutputVariance() );
        instance.setOutputVariance(0.0);
        assertEquals( 0.0, instance.getOutputVariance() );
        try
        {
            instance.setOutputVariance(-1.0);
            fail( "outputVariance must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution10()
    {
        System.out.println("createPredictiveDistribution(10)");

        BayesianRegressionTestHarness.NUM_SAMPLES = 10;
        ArrayList<Double> inputs = BayesianRegressionTestHarness.createInputs(RANDOM);
        BayesianRegressionTestHarness.Model target = new BayesianRegressionTestHarness.Model(0.25);
        ArrayList<InputOutputPair<Vector,Double>> data =
            BayesianRegressionTestHarness.createData(inputs, target, RANDOM);
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        ArrayList<Vectorizable> vs = new ArrayList<Vectorizable>( data.size() );
        for( InputOutputPair<Vector,Double> v : data )
        {
            vs.add( v.getInput() );
        }

        Evaluator<? super Vectorizable, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data), vs );
        LinearRegression regression = new LinearRegression();
        LinearDiscriminantWithBias mle = regression.learn(data);

        BayesianRegressionTestHarness.compareMethods(predictive, mle, target);
    }


    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution30()
    {
        System.out.println("createPredictiveDistribution(30)");
        BayesianRegressionTestHarness.NUM_SAMPLES = 30;
        ArrayList<Double> inputs = BayesianRegressionTestHarness.createInputs(RANDOM);
        BayesianRegressionTestHarness.Model target = new BayesianRegressionTestHarness.Model(0.25);
        ArrayList<InputOutputPair<Vector,Double>> data =
            BayesianRegressionTestHarness.createData(inputs, target, RANDOM);
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        ArrayList<Vectorizable> vs = new ArrayList<Vectorizable>( data.size() );
        for( InputOutputPair<Vector,Double> v : data )
        {
            vs.add( v.getInput() );
        }

        Evaluator<? super Vectorizable, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data), vs );
        LinearRegression regression = new LinearRegression();
        LinearDiscriminantWithBias mle = regression.learn(data);

        BayesianRegressionTestHarness.compareMethods(predictive, mle, target);
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution5()
    {
        System.out.println("createPredictiveDistribution(5)");
        BayesianRegressionTestHarness.NUM_SAMPLES = 5;
        ArrayList<Double> inputs = BayesianRegressionTestHarness.createInputs(RANDOM);
        BayesianRegressionTestHarness.Model target = new BayesianRegressionTestHarness.Model(0.25);
        ArrayList<InputOutputPair<Vector,Double>> data =
            BayesianRegressionTestHarness.createData(inputs, target, RANDOM);
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        ArrayList<Vectorizable> vs = new ArrayList<Vectorizable>( data.size() );
        for( InputOutputPair<Vector,Double> v : data )
        {
            vs.add( v.getInput() );
        }

        Evaluator<? super Vectorizable, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data), vs );
        LinearRegression regression = new LinearRegression();
        LinearDiscriminantWithBias mle = regression.learn(data);
        BayesianRegressionTestHarness.compareMethods(predictive, mle, target);
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution20()
    {
        System.out.println("createPredictiveDistribution(20)");
        BayesianRegressionTestHarness.NUM_SAMPLES = 20;
        ArrayList<Double> inputs = BayesianRegressionTestHarness.createInputs(RANDOM);
        BayesianRegressionTestHarness.Model target = new BayesianRegressionTestHarness.Model(1.0);
        ArrayList<InputOutputPair<Vector,Double>> data =
            BayesianRegressionTestHarness.createData(inputs, target, RANDOM);
        GaussianProcessRegression<Vectorizable> instance = this.createInstance();
        ArrayList<Vectorizable> vs = new ArrayList<Vectorizable>( data.size() );
        for( InputOutputPair<Vector,Double> v : data )
        {
            vs.add( v.getInput() );
        }

        Evaluator<? super Vectorizable, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data), vs );
        LinearRegression regression = new LinearRegression();
        LinearDiscriminantWithBias mle = regression.learn(data);

        BayesianRegressionTestHarness.compareMethods(predictive, mle, target);
    }


}
