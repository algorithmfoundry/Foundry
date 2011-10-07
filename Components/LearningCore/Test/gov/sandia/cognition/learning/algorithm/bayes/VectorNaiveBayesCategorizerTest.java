/*
 * File:                VectorNaiveBayesCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 *
 * Copyright November 24, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 */

package gov.sandia.cognition.learning.algorithm.bayes;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 * Unit tests for class VectorNaiveBayesCategorizer.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class VectorNaiveBayesCategorizerTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public VectorNaiveBayesCategorizerTest(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors for class VectorNaiveBayesCategorizer.
     */
    public void testConstructors()
    {
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        assertEquals( 0.0, instance.getPriors().getTotal(), 0.0 );
        assertTrue(instance.getConditionals().isEmpty());

        DataDistribution<String> priors = new DefaultDataDistribution<String>();
        Map<String, List<UnivariateGaussian.PDF>> conditionals =
            new LinkedHashMap<String, List<UnivariateGaussian.PDF>>();
        instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>(priors, conditionals);
        assertSame(priors, instance.getPriors());
        assertSame(conditionals, instance.getConditionals());
    }

    /**
     * Test of clone method, of class VectorNaiveBayesCategorizer.
     */
    public void testClone()
    {
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> clone = instance.clone();
        assertNotSame(instance.getPriors(), clone.getPriors());
        assertNotSame(instance.getConditionals(), clone.getConditionals());
    }

    /**
     * Test of evaluate method, of class VectorNaiveBayesCategorizer.
     */
    public void testEvaluate()
    {
        Vector2 input = new Vector2(1, 2);
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        assertNull(instance.evaluate(input));

        instance.getPriors().increment("a", 3);
        instance.getConditionals().put("a", new ArrayList<UnivariateGaussian.PDF>());
        instance.getConditionals().get("a").add(new UnivariateGaussian.PDF(3.0, 10.0));
        instance.getConditionals().get("a").add(new UnivariateGaussian.PDF(5.0, 1.0));
        assertEquals("a", instance.evaluate(input));

        instance.getPriors().increment("b", 2);
        instance.getConditionals().put("b", new ArrayList<UnivariateGaussian.PDF>());
        instance.getConditionals().get("b").add(new UnivariateGaussian.PDF(0.0, 1.0));
        instance.getConditionals().get("b").add(new UnivariateGaussian.PDF(1.0, 1.0));
        assertEquals("b", instance.evaluate(input));

    }

    /**
     * Test of getCategories method, of class VectorNaiveBayesCategorizer.
     */
    public void testGetCategories()
    {
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        assertTrue(instance.getCategories().isEmpty());

        instance.getPriors().increment("a", 1);
        instance.getConditionals().put("a", new ArrayList<UnivariateGaussian.PDF>());
        assertEquals(1, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));

        instance.getPriors().increment("b", 1);
        instance.getConditionals().put("b", new ArrayList<UnivariateGaussian.PDF>());
        assertEquals(2, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));
        assertTrue(instance.getCategories().contains("b"));
    }

    /**
     * Test of getInputDimensionality method, of class VectorNaiveBayesCategorizer.
     */
    public void testGetInputDimensionality()
    {
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        assertEquals(0, instance.getInputDimensionality());

        instance.getPriors().increment("a", 1);
        instance.getConditionals().put("a", new ArrayList<UnivariateGaussian.PDF>());
        assertEquals(0, instance.getInputDimensionality());

        instance.getConditionals().get("a").add(new UnivariateGaussian.PDF());
        assertEquals(1, instance.getInputDimensionality());

        instance.getConditionals().get("a").add(new UnivariateGaussian.PDF());
        assertEquals(2, instance.getInputDimensionality());
    }

    /**
     * Test of getPriors method, of class VectorNaiveBayesCategorizer.
     */
    public void testGetPriors()
    {
        this.testSetPriors();
    }

    /**
     * Test of setPriors method, of class VectorNaiveBayesCategorizer.
     */
    public void testSetPriors()
    {
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        assertEquals( 0.0, instance.getPriors().getTotal(), 0.0 );

        DataDistribution<String> priors = new DefaultDataDistribution<String>();
        instance.setPriors(priors);
        assertSame(priors, instance.getPriors());

        priors = null;
        instance.setPriors(priors);
        assertSame(priors, instance.getPriors());

        priors = new DefaultDataDistribution<String>();
        instance.setPriors(priors);
        assertSame(priors, instance.getPriors());
    }

    /**
     * Test of getConditionals method, of class VectorNaiveBayesCategorizer.
     */
    public void testGetConditionals()
    {
        this.testSetConditionals();
    }

    /**
     * Test of setConditionals method, of class VectorNaiveBayesCategorizer.
     */
    public void testSetConditionals()
    {
        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = new VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF>();
        assertTrue(instance.getConditionals().isEmpty());

        Map<String, List<UnivariateGaussian.PDF>> conditionals =
            new LinkedHashMap<String, List<UnivariateGaussian.PDF>>();
        instance.setConditionals(conditionals);
        assertSame(conditionals, instance.getConditionals());

        conditionals = null;
        instance.setConditionals(conditionals);
        assertSame(conditionals, instance.getConditionals());

        conditionals = new LinkedHashMap<String, List<UnivariateGaussian.PDF>>();
        instance.setConditionals(conditionals);
        assertSame(conditionals, instance.getConditionals());
    }

    /**
     * Test of the VectorNaiveBayesCategorizer.Learner class.
     */
    public void testLearner()
    {
        VectorNaiveBayesCategorizer.Learner<String, UnivariateGaussian.PDF> learner =
            new VectorNaiveBayesCategorizer.Learner<String, UnivariateGaussian.PDF>(
                new UnivariateGaussian.MaximumLikelihoodEstimator());

        ArrayList<InputOutputPair<Vector3, String>> data =
            new ArrayList<InputOutputPair<Vector3, String>>();

        data.add(DefaultInputOutputPair.create(new Vector3(-1.0, 0.0, 3.0), "a"));
        data.add(DefaultInputOutputPair.create(new Vector3(2.0, 1.0, 9.0), "b"));
        data.add(DefaultInputOutputPair.create(new Vector3(4.0, 0.0, 9.2), "b"));
        data.add(DefaultInputOutputPair.create(new Vector3(-5.0, 1.0, 2.0), "a"));
        data.add(DefaultInputOutputPair.create(new Vector3(-7.0, 1.0, 3.0), "a"));

        VectorNaiveBayesCategorizer<String, UnivariateGaussian.PDF> instance = learner.learn(data);
        assertEquals(2, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));
        assertTrue(instance.getCategories().contains("b"));
        assertEquals(3.0, instance.getPriors().get("a"));
        assertEquals(2.0, instance.getPriors().get("b"));
        assertEquals(5.0, instance.getPriors().getTotal());
        assertEquals(2, instance.getConditionals().size());
        assertEquals(3, instance.getConditionals().get("a").size());
        assertEquals(3, instance.getConditionals().get("b").size());
        assertEquals(3, instance.getInputDimensionality());
        
        for (InputOutputPair<Vector3, String> example : data)
        {
            assertEquals(example.getOutput(), instance.evaluate(example.getInput()));
        }
    }

}
