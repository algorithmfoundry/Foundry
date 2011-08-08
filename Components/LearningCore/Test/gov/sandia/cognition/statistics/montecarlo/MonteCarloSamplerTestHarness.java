/*
 * File:                MonteCarloSamplerTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MonteCarloSamplerTestHarness.
 *
 * @param <DataType> Data type
 * @param <SampleType> Sample type
 * @param <FunctionType> Function type
 * @author krdixon
 */
public abstract class MonteCarloSamplerTestHarness<DataType extends Number,SampleType,FunctionType extends Evaluator<DataType,Double>>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Number of samples.
     */
    public static int NUM_SAMPLES = 1000;

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class MonteCarloSamplerTestHarness.
     * @param testName Name of the test.
     */
    public MonteCarloSamplerTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a sampler instance
     * @return
     * Sampler instance
     */
    public abstract MonteCarloSampler<DataType,SampleType,? super FunctionType> createInstance();

    /**
     * Creates a target function
     * @return
     * Target function
     */
    public abstract ProbabilityFunction<Double> createTargetFunctionInstance();

    /**
     * Tests constructors.
     */
    public abstract void testConstructors();

    /**
     * Tests against known values.
     */
    public abstract void testKnownValues();

    /**
     * Clone.
     */
    @SuppressWarnings("unchecked")
    public void testClone()
    {
        System.out.println( "clone" );

        ProbabilityFunction<Double> f =this.createTargetFunctionInstance();

        MonteCarloSampler<DataType,SampleType,? super FunctionType> instance =
            this.createInstance();

        MonteCarloSampler<DataType,SampleType,? super FunctionType> clone =
            (MonteCarloSampler<DataType, SampleType,? super FunctionType>) instance.clone();

        Random r1 = new Random(1);
        Random r2 = new Random(1);

        ArrayList<? extends SampleType> s1 = instance.sample( (FunctionType) f, r1, NUM_SAMPLES);
        ArrayList<? extends SampleType> s2 = clone.sample( (FunctionType) f, r2, NUM_SAMPLES);

        double m1;
        double m2;
        if( CollectionUtil.getFirst(s1) instanceof Number )
        {
            m1 = UnivariateMonteCarloIntegrator.INSTANCE.getMean(
                (ArrayList<? extends Double>) s1 ).getMean();
            m2 = UnivariateMonteCarloIntegrator.INSTANCE.getMean(
                (ArrayList<? extends Double>) s2 ).getMean();
        }
        else
        {
            m1 = UnivariateMonteCarloIntegrator.INSTANCE.getMean(
                (ArrayList<? extends WeightedValue<? extends Double>>) s1 ).getMean();
            m2 = UnivariateMonteCarloIntegrator.INSTANCE.getMean(
                (ArrayList<? extends WeightedValue<? extends Double>>) s2 ).getMean();
        }

        assertEquals( m1, m2 );

    }

    /**
     * Test of sample method, of class MonteCarloSampler.
     */
    @SuppressWarnings("unchecked")
    public void testSample()
    {
        System.out.println("sample");

        ProbabilityFunction<Double> f =this.createTargetFunctionInstance();

        MonteCarloSampler<DataType,SampleType,? super FunctionType> instance =
            this.createInstance();

        ArrayList<? extends SampleType> samples =
            instance.sample( (FunctionType) f, RANDOM, NUM_SAMPLES );
        assertEquals( NUM_SAMPLES, samples.size() );

        UnivariateGaussian.PDF pdf;
        if( CollectionUtil.getFirst(samples) instanceof Number )
        {
            pdf = UnivariateMonteCarloIntegrator.INSTANCE.getMean(
                (ArrayList<? extends Double>) samples );
         }
        else
        {
            pdf = UnivariateMonteCarloIntegrator.INSTANCE.getMean(
                (ArrayList<? extends WeightedValue<? extends Double>>) samples );
        }

        double mean = f.getMean().doubleValue();

        System.out.println( "Mean: " + mean );
        System.out.println( "Monte Carlo: " + pdf );
        ConfidenceInterval ci = StudentTConfidence.computeConfidenceInterval(
            pdf,samples.size(), 0.95 );
        System.out.println( "Interval: " + ci );
        assertTrue( ci.withinInterval(mean) );

    }

}
