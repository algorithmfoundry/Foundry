/*
 * File:                CompositeCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorTestHarness.CosineFunction;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 * Unit tests for class CompositeCategorizer.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class CompositeCategorizerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public CompositeCategorizerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class CompositeCategorizer.
     */
    public void testConstructors()
    {
        Evaluator<Double, Double> preprocessor = null;
        ThresholdBinaryCategorizer categorizer = null;
        CompositeCategorizer<Double, Double, Boolean> instance =
            new CompositeCategorizer<Double, Double, Boolean>();
        assertSame(preprocessor, instance.getPreprocessor());
        assertSame(categorizer, instance.getCategorizer());

        preprocessor = new CosineFunction();
        categorizer = new ThresholdBinaryCategorizer(0.5);
        instance = new CompositeCategorizer<Double, Double, Boolean>(
            preprocessor, categorizer);
        assertSame(preprocessor, instance.getPreprocessor());
        assertSame(categorizer, instance.getCategorizer());
    }

    /**
     * Test of clone method, of class CompositeCategorizer.
     */
    public void testClone()
    {
        Evaluator<Double, Double> preprocessor = new CosineFunction();
        ThresholdBinaryCategorizer categorizer = new ThresholdBinaryCategorizer(0.5);
        CompositeCategorizer<Double, Double, Boolean> instance =
            new CompositeCategorizer<Double, Double, Boolean>(
                preprocessor, categorizer);

        CompositeCategorizer<Double, Double, Boolean> clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());

        assertSame(preprocessor, instance.getPreprocessor());
        assertSame(categorizer, instance.getCategorizer());
        assertNotSame(preprocessor, clone.getPreprocessor());
        assertNotSame(categorizer, clone.getCategorizer());
        assertNotNull(clone.getPreprocessor());
        assertNotNull(clone.getCategorizer());
    }

    /**
     * Test of evaluate method, of class CompositeCategorizer.
     */
    public void testEvaluate()
    {
        CompositeCategorizer<Double, Double, Boolean> instance =
            new CompositeCategorizer<Double, Double, Boolean>(
                new CosineFunction(), new ThresholdBinaryCategorizer(0.5));

        assertTrue(instance.evaluate(0.0));
        assertFalse(instance.evaluate(Math.PI));
    }

    /**
     * Test of getCategories method, of class CompositeCategorizer.
     */
    public void testGetCategories()
    {
        Set<String> categories = new HashSet<String>();
        categories.add("a");
        categories.add("b");
        EvaluatorToCategorizerAdapter<Object, String> categorizer =
            new EvaluatorToCategorizerAdapter<Object, String>(null, categories);
        CompositeCategorizer<?, ?, String> instance = new CompositeCategorizer<Object, Object, String>(
            null, categorizer);
        assertSame(categories, instance.getCategories());

        categories = new HashSet<String>();
        categorizer.setCategories(categories);
        assertSame(categories, instance.getCategories());
    }

    /**
     * Test of getPreprocessor method, of class CompositeCategorizer.
     */
    public void testGetPreprocessor()
    {
        this.testSetPreprocessor();
    }

    /**
     * Test of setPreprocessor method, of class CompositeCategorizer.
     */
    public void testSetPreprocessor()
    {
        Evaluator<Double, Double> preprocessor = null;
        CompositeCategorizer<Double, Double, Boolean> instance =
            new CompositeCategorizer<Double, Double, Boolean>();
        assertSame(preprocessor, instance.getPreprocessor());

        preprocessor = new CosineFunction();
        instance.setPreprocessor(preprocessor);
        assertSame(preprocessor, instance.getPreprocessor());

        preprocessor = new AtanFunction();
        instance.setPreprocessor(preprocessor);
        assertSame(preprocessor, instance.getPreprocessor());

        preprocessor = null;
        instance.setPreprocessor(preprocessor);
        assertSame(preprocessor, instance.getPreprocessor());
    }

    /**
     * Test of getCategorizer method, of class CompositeCategorizer.
     */
    public void testGetCategorizer()
    {
        this.testSetCategorizer();
    }

    /**
     * Test of setCategorizer method, of class CompositeCategorizer.
     */
    public void testSetCategorizer()
    {
        ThresholdBinaryCategorizer categorizer = null;
        CompositeCategorizer<Double, Double, Boolean> instance =
            new CompositeCategorizer<Double, Double, Boolean>();
        assertSame(categorizer, instance.getCategorizer());

        categorizer = new ThresholdBinaryCategorizer(0.5);
        instance.setCategorizer(categorizer);
        assertSame(categorizer, instance.getCategorizer());

        categorizer = new ThresholdBinaryCategorizer(-1.0);
        instance.setCategorizer(categorizer);
        assertSame(categorizer, instance.getCategorizer());

        categorizer = null;
        instance.setCategorizer(categorizer);
        assertSame(categorizer, instance.getCategorizer());
    }

}
