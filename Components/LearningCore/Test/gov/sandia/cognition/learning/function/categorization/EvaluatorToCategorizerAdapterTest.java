/*
 * File:                EvaluatorToCategorizerAdapterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 30, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.nearest.KNearestNeighborExhaustive;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.ThresholdFunction;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.NumberAverager;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Unit tests for EvaluatorToCategorizerAdapterTest.
 *
 * @author krdixon
 */
public class EvaluatorToCategorizerAdapterTest
    extends CategorizerTestHarness<Double,Double>
{

    /**
     * Tests for class EvaluatorToCategorizerAdapterTest.
     * @param testName Name of the test.
     */
    public EvaluatorToCategorizerAdapterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class EvaluatorToCategorizerAdapterTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        EvaluatorToCategorizerAdapter a = new EvaluatorToCategorizerAdapter();
        assertNotNull( a );
        assertNull( a.getEvaluator() );
        assertNotNull( a.getCategories() );
        assertEquals( 0, a.getCategories().size() );

        a = this.createInstance();
        assertNotNull( a );
        assertNotNull( a.getEvaluator() );
        assertNotNull( a.getCategories() );
    }

    /**
     * Test of getEvaluator method, of class EvaluatorToCategorizerAdapter.
     */
    public void testGetEvaluator()
    {
        System.out.println("getEvaluator");
        EvaluatorToCategorizerAdapter instance = this.createInstance();
        assertNotNull( instance.getEvaluator() );
    }

    /**
     * Test of setEvaluator method, of class EvaluatorToCategorizerAdapter.
     */
    public void testSetEvaluator()
    {
        System.out.println("setEvaluator");
        EvaluatorToCategorizerAdapter<Double,Double> instance = this.createInstance();
        Evaluator<? super Double,? extends Double> f = instance.getEvaluator();
        instance.setEvaluator(null);
        assertNull( instance.getEvaluator() );
        instance.setEvaluator(f);
        assertSame( f, instance.getEvaluator() );
    }

    /**
     * Test of setCategories method, of class EvaluatorToCategorizerAdapter.
     */
    public void testSetCategories()
    {
        System.out.println("setCategories");
        EvaluatorToCategorizerAdapter<Double,Double> instance = this.createInstance();
        Set<Double> categories = instance.getCategories();
        instance.setCategories(null);
        assertNull( instance.getCategories() );
        instance.setCategories(categories);
        assertSame( categories, instance.getCategories() );
    }

    @Override
    public EvaluatorToCategorizerAdapter<Double, Double> createInstance()
    {
        ThresholdFunction f = new ThresholdFunction();
        return new EvaluatorToCategorizerAdapter<Double, Double>(
            f, new TreeSet<Double>( Arrays.asList( f.getLowValue(), f.getHighValue() ) ) );
    }

    @Override
    public Double createRandomInput()
    {
        return RANDOM.nextGaussian();
    }

    @Override
    public void testKnownValues()
    {
        EvaluatorToCategorizerAdapter<Double,Double> instance = this.createInstance();
        ThresholdFunction f = (ThresholdFunction) instance.getEvaluator();
        Double inputLow = f.getThreshold() - 1.0;
        Double inputHigh = f.getThreshold() + 1.0;

        assertEquals( f.getLowValue(), instance.evaluate(inputLow) );
        assertEquals( f.getHighValue(), instance.evaluate(inputHigh) );
    }

    public static class EuclideanScalarMetric
        extends AbstractCloneableSerializable
        implements Metric<Double>
    {

        public double evaluate(Double first,
            Double second)
        {
            double delta = first - second;
            return Math.abs(delta);
        }

    }

    public EvaluatorToCategorizerAdapter.Learner<Double,Double> createLearnerInstance()
    {
        SupervisedBatchLearner<Double,Double,KNearestNeighborExhaustive<Double,Double>> knn =
            new KNearestNeighborExhaustive.Learner<Double,Double>(1, new EuclideanScalarMetric(), new NumberAverager() );
        return new EvaluatorToCategorizerAdapter.Learner<Double,Double>( knn );
    }

    public void testLearnerConstructors()
    {
        System.out.println( "Learner.constructors" );

        EvaluatorToCategorizerAdapter.Learner<Double,Double> learner =
            new EvaluatorToCategorizerAdapter.Learner<Double,Double>();
        assertNotNull( learner );
        assertNull( learner.getLearner() );

        learner = this.createLearnerInstance();
        assertNotNull( learner );
        assertNotNull( learner.getLearner() );
    }

    public void testLearnerLearn()
    {
        System.out.println( "Learner.learn" );

        Collection<InputOutputPair<Double,Double>>data = new ArrayList<InputOutputPair<Double,Double>>( 2 );
        data.add( new DefaultInputOutputPair<Double,Double>( RANDOM.nextGaussian(), RANDOM.nextGaussian() ) );
        data.add( new DefaultInputOutputPair<Double,Double>( RANDOM.nextGaussian(), RANDOM.nextGaussian() ) );

        EvaluatorToCategorizerAdapter.Learner<Double,Double> learner = this.createLearnerInstance();
        EvaluatorToCategorizerAdapter<Double,Double> f = learner.learn(data);
        assertNotNull( f );
        assertNotNull( f.getEvaluator() );
        assertEquals( 2, f.getCategories().size() );

    }

}
