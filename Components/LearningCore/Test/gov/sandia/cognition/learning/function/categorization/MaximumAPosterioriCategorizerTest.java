/*
 * File:                MaximumAPosterioriCategorizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MaximumAPosterioriCategorizerTest.
 *
 * @author krdixon
 */
public class MaximumAPosterioriCategorizerTest
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
     * Number of samples, {@value}.
     */
    public final int NUM_SAMPLES = 1000;

    /**
     * Tests for class MaximumAPosterioriCategorizerTest.
     * @param testName Name of the test.
     */
    public MaximumAPosterioriCategorizerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * Instance
     */
    public MaximumAPosterioriCategorizer<Double,String> createInstance()
    {
        MaximumAPosterioriCategorizer<Double,String> instance =
            new MaximumAPosterioriCategorizer<Double, String>();
        instance.addCategory( "A", RANDOM.nextDouble(), new UnivariateGaussian.PDF(
            RANDOM.nextGaussian(), RANDOM.nextDouble() * 10 ) );
        instance.addCategory( "B", RANDOM.nextDouble(), new UnivariateGaussian.PDF(
            RANDOM.nextGaussian(), RANDOM.nextDouble() * 10 ) );
        instance.addCategory( "C", RANDOM.nextDouble(), new UnivariateGaussian.PDF(
            RANDOM.nextGaussian(), RANDOM.nextDouble() * 10 ) );
        return instance;
    }


    /**
     * Tests the constructors of class MaximumAPosterioriCategorizerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MaximumAPosterioriCategorizer<Double,String> instance =
            new MaximumAPosterioriCategorizer<Double, String>();
        assertNotNull( instance.getCategories() );
        assertEquals( 0, instance.getCategories().size() );
    }

    /**
     * Test of clone method, of class MaximumAPosterioriCategorizer.
     */
    public void testClone()
    {
        System.out.println("clone");
        MaximumAPosterioriCategorizer<Double,String> instance =
            this.createInstance();
        MaximumAPosterioriCategorizer<Double,String> clone = instance.clone();
        assertEquals( instance.getCategories().size(), clone.getCategories().size() );

    }

    /**
     * Test of addCategory method, of class MaximumAPosterioriCategorizer.
     */
    public void testAddCategory()
    {
        System.out.println("addCategory");
        MaximumAPosterioriCategorizer<Double,String> instance =
            this.createInstance();
        String newCategory = "Nothing there";
        WeightedValue<ProbabilityFunction<Double>> categoryValue =
            instance.getCategory(newCategory);
        assertEquals( 0.0, categoryValue.getWeight() );
        assertNull( categoryValue.getValue() );

        UnivariateGaussian.PDF pdf = new UnivariateGaussian.PDF();
        double prior = RANDOM.nextDouble();
        instance.addCategory(newCategory, prior, pdf);
        categoryValue = instance.getCategory(newCategory);
        assertTrue( categoryValue.getWeight() > 0.0 );
        assertTrue( prior != categoryValue.getWeight() );
        assertSame( pdf, categoryValue.getValue() );

    }

    /**
     * Test of getCategory method, of class MaximumAPosterioriCategorizer.
     */
    public void testGetCategory()
    {
        System.out.println("getCategory");
        MaximumAPosterioriCategorizer<Double,String> instance = this.createInstance();
        for( String category : instance.getCategories() )
        {
            WeightedValue<ProbabilityFunction<Double>> categoryValue =
                instance.getCategory(category);
            assertTrue( categoryValue.getWeight() > 0.0 );
        }

        WeightedValue<ProbabilityFunction<Double>> categoryValue =
            instance.getCategory("Nothing there");
        assertEquals( 0.0, categoryValue.getWeight() );
        assertNull( categoryValue.getValue() );


    }

    /**
     * Test of getCategories method, of class MaximumAPosterioriCategorizer.
     */
    public void testGetCategories()
    {
        System.out.println("getCategories");
        MaximumAPosterioriCategorizer<Double,String> instance = this.createInstance();
        assertEquals( 3, instance.getCategories().size() );
    }

    /**
     * Test of evaluate method, of class MaximumAPosterioriCategorizer.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        MaximumAPosterioriCategorizer<Double,String> instance = this.createInstance();
        ArrayList<? extends Double> observations = instance.sample(RANDOM,10);
        for( Double observation : observations )
        {
            String categoryMAP = instance.evaluate(observation);
            double maxPosterior = Double.NEGATIVE_INFINITY;
            String maxCategory = null;
            for( String category : instance.getCategories() )
            {
                WeightedValue<ProbabilityFunction<Double>> categoryValue =
                    instance.getCategory(category);
                double prior = categoryValue.getWeight();
                double conditional = categoryValue.getValue().evaluate(observation);
                double posterior = prior*conditional;
                if( maxPosterior < posterior )
                {
                    maxPosterior = posterior;
                    maxCategory = category;
                }
            }

            assertEquals( maxCategory, categoryMAP );
        }

    }

    public void testLearner()
    {
        System.out.println( "Learner" );

        MaximumAPosterioriCategorizer<? extends Number,String> instance = this.createInstance();

        BatchLearner<Collection<? extends Double>, ? extends ComputableDistribution<Double>> conditionalLearner
            = new UnivariateGaussian.MaximumLikelihoodEstimator();

        MaximumAPosterioriCategorizer.Learner<Double,String> learner =
            new MaximumAPosterioriCategorizer.Learner<Double, String>( conditionalLearner );


    }

}
