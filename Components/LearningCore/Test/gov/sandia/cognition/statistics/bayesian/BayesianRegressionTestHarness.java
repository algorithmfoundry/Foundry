/*
 * File:                BayesianRegressionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 1, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.IncrementalLearner;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.UnivariateDistribution;
import gov.sandia.cognition.statistics.SufficientStatistic;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for BayesianRegressionTestHarness.
 * @param <PosteriorType> Posterior type
 * @author krdixon
 */
public abstract class BayesianRegressionTestHarness<PosteriorType extends ClosedFormDistribution<Vector>>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Default number of samples, {@value}.
     */
    public static int NUM_SAMPLES = 10;

    /**
     * Default confidence, {@value}.
     */
    public double CONFIDENCE = 0.95;

    /**
     * Tests for class BayesianRegressionTestHarness.
     * @param testName Name of the test.
     */
    public BayesianRegressionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * Instance
     */
    abstract public BayesianRegression<Double,Double,PosteriorType> createInstance();

    /**
     * Model
     */
    public static class Model
        extends AbstractCloneableSerializable
        implements Evaluator<Double,UnivariateGaussian>
    {

        /**
         * Variance
         */
        double variance;

        /**
         * Model
         * @param variance
         * Variance
         */
        public Model(
            double variance)
        {
            this.variance = variance;
        }

        @Override
        public UnivariateGaussian evaluate(
            Double input)
        {
            double mean = Math.sin( 2.0*Math.PI * input );
            return new UnivariateGaussian( mean, variance );
        }

    }

    /**
     * Creates the model of the data.
     * @return
     * Model
     */
    public Model createModel()
    {
        return new Model( RANDOM.nextDouble() );
    }

    /**
     * Creates inputs for the model.
     * @param random Random
     * @return
     * Inputs for the model
     */
    public static ArrayList<Double> createInputs(
        Random random )
    {
        ArrayList<Double> samples = new ArrayList<Double>( NUM_SAMPLES );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            samples.add( random.nextDouble() );
        }
        return samples;
    }

    /**
     * RBF
     */
    public static class RadialBasisVectorFunction
        extends AbstractCloneableSerializable
        implements Evaluator<Number,Vector>
    {

        /**
         * Number of RBFs
         */
        int num;

        /**
         * RBF
         * @param num
         * Number of RBFs
         */
        public RadialBasisVectorFunction(
            int num)
        {
            this.num = num;
        }

        @Override
        public Vector evaluate(
            Number input)
        {
            Vector x = VectorFactory.getDefault().createVector(num+1);
            x.setElement(num-1, 1.0);
            for( int i = 0; i < num; i++ )
            {
                double mean = i*(2.0/num)-1.0;
                double variance = 0.01;
                x.setElement(i, evaluate(input.doubleValue(), mean, variance));
            }
            return x;
        }

        /**
         * Evaluates
         * @param input Input
         * @param mean mean
         * @param variance Variance
         * @return
         * Output
         */
        public static double evaluate(
            double input,
            double mean,
            double variance )
        {
            double delta = input - mean;
            return Math.exp( delta*delta / (-2.0*variance) );
        }


    }


    /**
     * Creates data
     * @param inputs Inputs
     * @param model Model
     * @param random Random
     * @return
     * Data
     */
    public static ArrayList<InputOutputPair<Double,Double>> createData(
        ArrayList<Double> inputs,
        Evaluator<? super Double,? extends UnivariateDistribution<Double>> model,
        Random random )
    {

        ArrayList<InputOutputPair<Double,Double>> samples =
            new ArrayList<InputOutputPair<Double, Double>>( inputs.size() );
        for( int n = 0; n < inputs.size(); n++ )
        {
            Double input = inputs.get(n);
            UnivariateDistribution<Double> outputDistribution = model.evaluate( input );
            samples.add( new DefaultInputOutputPair<Double, Double>(
                input, outputDistribution.sample(random) ) );
        }
        return samples;

    }


    /**
     * Tests the constructors of class BayesianRegressionTestHarness.
     */
    abstract public void testConstructors();

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        @SuppressWarnings("unchecked")
        BayesianRegression<Double,Double,? extends Distribution<Vector>> clone =
            (BayesianRegression<Double,Double,? extends Distribution<Vector>>) instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getFeatureMap(), clone.getFeatureMap() );
        assertNotNull( clone.getFeatureMap() );

    }

    /**
     * Test of getFeatureMap method, of class BayesianRegression.
     */
    public void testGetFeatureMap()
    {
        System.out.println("getFeatureMap");
        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        assertNotNull( instance.getFeatureMap() );
    }

    /**
     * Test of setFeatureMap method, of class BayesianRegression.
     */
    public void testSetFeatureMap()
    {
        System.out.println("setFeatureMap");
        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        Evaluator<? super Double, Vector> featureMap = instance.getFeatureMap();
        assertNotNull( featureMap );
        instance.setFeatureMap(null);
        assertNull( instance.getFeatureMap() );
        instance.setFeatureMap(featureMap);
        assertSame( featureMap, instance.getFeatureMap() );
    }

    /**
     * Learn
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        System.out.println("createConditionalDistribution");
        BayesianRegression<Double,Double,? extends ClosedFormDistribution<Vector>> instance =
            this.createInstance();
        ArrayList<Double> inputs = createInputs(RANDOM);
        Evaluator<? super Double,? extends UnivariateDistribution<Double>> target = this.createModel();
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target, RANDOM);
        ClosedFormDistribution<Vector> posterior = instance.learn(data);

        Vector mean = posterior.getMean();


        LinearRegression<Double> linearRegression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> result = linearRegression.learn(data);
        System.out.println( "Mean: " + mean );
        System.out.println( "Result: " + result.getWeightVector() );

//        System.out.println( "=====================" );
//        System.out.println( "Estimates!" );
//        for( double x = 0.0; x <= 1.0; x += 0.1 )
//        {
//            UnivariateGaussian y = f.evaluate(x);
//            System.out.println( "x = " + x + ", y = " + y );
//        }

    }

    /**
     * Test of createConditionalDistribution method, of class BayesianRegression.
     */
    public void testCreateConditionalDistribution()
    {
        System.out.println("createConditionalDistribution");
        // This is similar to Bishop's example on p. 157

        ArrayList<Double> inputs = createInputs(RANDOM);
        Evaluator<? super Double,? extends UnivariateDistribution<Double>> target = this.createModel();
        ArrayList<InputOutputPair<Double,Double>> samples =
            createData(inputs, target, RANDOM);

//        System.out.println( "Targets:" );
//        for( InputOutputPair<Double,Double> sample : samples )
//        {
//            System.out.println( "x = " + sample.getInput() + ", y = " + sample.getOutput() );
//        }

        BayesianRegression<Double,Double,PosteriorType> instance = this.createInstance();
        PosteriorType posterior = instance.learn(samples);

        Vector weights = posterior.getMean();
        UnivariateDistribution<Double> conditional = (UnivariateDistribution<Double>)
            instance.createConditionalDistribution(samples.get(1).getFirst(), weights );
        System.out.println( "Result: " + conditional );
        System.out.println( "Target: " + samples.get(1).getSecond() );

        ConfidenceInterval interval = GaussianConfidence.computeConfidenceInterval(
            conditional, 1, 0.95);
        System.out.println( "Interval: " + interval );
        assertTrue( interval.withinInterval(samples.get(1).getSecond()) );
    }

    public static void compareMethods(
        Evaluator<? super Double, ? extends ClosedFormDistribution<Double>> predictive,
        VectorFunctionLinearDiscriminant<Double> mle,
        Model target )
    {

        System.out.println( "=====================" );
        double logMLE = 0.0;
        double logBayesian = 0.0;
        double logTarget = 0.0;
        for( double x = 0.0; x <= 1.0; x += 0.1 )
        {
            ClosedFormComputableDistribution<Double> y = target.evaluate(x).getProbabilityFunction();
            ClosedFormDistribution<Double> ybayes = predictive.evaluate(x);
            Double ymle = mle.evaluate(x);
            System.out.printf( "x = %.1f", x );
            System.out.println( ", target = " + y + ", Estimate: " + ybayes + ", MLE: " + ymle);
            logTarget = y.getProbabilityFunction().logEvaluate( y.getMean() );
            logBayesian += y.getProbabilityFunction().logEvaluate( ybayes.getMean() );
            logMLE += y.getProbabilityFunction().logEvaluate(ymle);
        }

        System.out.println( "Log-Likelihood Results: " );
        System.out.println( "Target: " + logTarget );
        System.out.println( "Bayes: " + logBayesian );
        System.out.println( "MLE: " + logMLE );
        assertTrue( logTarget > logBayesian );
        assertTrue( logBayesian > logMLE );
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution10()
    {
        System.out.println("createPredictiveDistribution(10)");

        NUM_SAMPLES = 10;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(0.25);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }


    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution100()
    {
        System.out.println("createPredictiveDistribution(100)");
        NUM_SAMPLES = 100;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(0.25);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution5()
    {
        System.out.println("createPredictiveDistribution(5)");
        NUM_SAMPLES = 5;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(0.25);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution1000()
    {
        System.out.println("createPredictiveDistribution(100)");
        NUM_SAMPLES = 100;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(1.0);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends ClosedFormDistribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }


    public <SufficientStatisticType extends SufficientStatistic<InputOutputPair<? extends Double, Double>,PosteriorType>> void testIncrementalAndBatch(
        IncrementalLearner<InputOutputPair<? extends Double,Double>,SufficientStatisticType> incremental )
//        IncrementalLearner<InputOutputPair<? extends Double,Double>,SufficientStatistic<InputOutputPair<? extends Double, Double>, PosteriorType>> incremental )
    {
        System.out.println( "Incremental And Batch" );
        ArrayList<Double> inputs = createInputs(RANDOM);
        NUM_SAMPLES = 100;
        Model target = new Model(1.0);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();

        Evaluator<? super Double, ? extends Distribution<Double>> batch =
            instance.createPredictiveDistribution( instance.learn(data) );

        SufficientStatisticType posterior = incremental.createInitialLearnedObject();
        for( InputOutputPair<Double,Double> pair : data )
        {
            incremental.update(posterior, pair);
        }

        Evaluator<? super Double, ? extends Distribution<Double>> incrementalPredictive =
            instance.createPredictiveDistribution( posterior.create() );


        // Now run some K-S tests to see if they're almost the same
        for( InputOutputPair<Double,Double> pair : data )
        {
            Distribution<Double> b = batch.evaluate(pair.getInput());
            ArrayList<? extends Double> sb = b.sample(RANDOM,NUM_SAMPLES);
            Distribution<Double> i = incrementalPredictive.evaluate(pair.getInput());
            System.out.println( "Batch:  " + b );
            System.out.println( "Incre:  " + i );
            System.out.println( "Target: " + target.evaluate(pair.getInput()) );
            ArrayList<? extends Double> si = i.sample(RANDOM, NUM_SAMPLES);
            KolmogorovSmirnovConfidence.Statistic kstest =
                KolmogorovSmirnovConfidence.INSTANCE.evaluateNullHypothesis(sb,si);
            System.out.println( "K-S test: " + kstest );
            assertEquals( 1.0, kstest.getNullHypothesisProbability(), 0.99 );

        }


    }

}
