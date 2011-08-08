/*
 * File:                WinnerTakeAllCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.learning.function.vector.VectorizableVectorConverter;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class WinnerTakeAllCategorizerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public WinnerTakeAllCategorizerTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        Evaluator<Vectorizable, Vector> evaluator = null;
        WinnerTakeAllCategorizer<Vector, String> instance = new WinnerTakeAllCategorizer<Vector, String>();
        assertSame(evaluator, instance.getEvaluator());
        assertTrue(instance.getCategories().isEmpty());

        evaluator = new VectorizableVectorConverter();
        Set<String> categories = new HashSet<String>();
        categories.add("a");
        instance = new WinnerTakeAllCategorizer<Vector, String>(evaluator,
            categories);
        assertSame(evaluator, instance.getEvaluator());
        assertSame(categories, instance.getCategories());
    }

    /**
     * Test of evaluate method, of class WinnerTakeAllCategorizer.
     */
    public void testEvaluate()
    {Set<String> categories = new LinkedHashSet<String>();
        categories.add("a");
        categories.add("b");
        categories.add("c");
        WinnerTakeAllCategorizer<Vector, String> instance =
            new WinnerTakeAllCategorizer<Vector, String>(
                new VectorizableVectorConverter(),
                categories);

        assertEquals("a", instance.evaluate(new Vector3(1.0, 0.0, 0.0)));
        assertEquals("b", instance.evaluate(new Vector3(0.0, 0.1, 0.0)));
        assertEquals("c", instance.evaluate(new Vector3(0.0, 0.1, 20.0)));
        assertEquals("a", instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
    }

    /**
     * Test of evaluateWithWeight method, of class WinnerTakeAllCategorizer.
     */
    public void testEvaluateWithWeight()
    {
        Set<String> categories = new LinkedHashSet<String>();
        categories.add("a");
        categories.add("b");
        categories.add("c");
        WinnerTakeAllCategorizer<Vector, String> instance =
            new WinnerTakeAllCategorizer<Vector, String>(
                new VectorizableVectorConverter(),
                categories);

        WeightedValue<String> result = instance.evaluateWithWeight(new Vector3(1.0, 0.0, 0.0));
        assertEquals("a", result.getValue());
        assertEquals(1.0, result.getWeight());


        result = instance.evaluateWithWeight(new Vector3(0.0, 0.1, 0.0));
        assertEquals("b", result.getValue());
        assertEquals(0.1, result.getWeight());

        result = instance.evaluateWithWeight(new Vector3(0.0, 0.1, 0.2));
        assertEquals("c", result.getValue());
        assertEquals(0.2, result.getWeight());


        result = instance.evaluateWithWeight(new Vector3(0.0, 0.0, 0.0));
        assertEquals("a", result.getValue());
        assertEquals(0.0, result.getWeight());

    }

    /**
     * Test of findBestCategory method, of class WinnerTakeAllCategorizer.
     */
    public void testFindBestCategory()
    {
        Set<String> categories = new LinkedHashSet<String>();
        categories.add("a");
        categories.add("b");
        categories.add("c");
        WinnerTakeAllCategorizer<Vector, String> instance =
            new WinnerTakeAllCategorizer<Vector, String>(
                null, categories);

        WeightedValue<String> result = instance.findBestCategory(new Vector3(1.0, 0.0, 0.0));
        assertEquals("a", result.getValue());
        assertEquals(1.0, result.getWeight());


        result = instance.findBestCategory(new Vector3(0.0, 0.1, 0.0));
        assertEquals("b", result.getValue());
        assertEquals(0.1, result.getWeight());

        result = instance.findBestCategory(new Vector3(0.0, 0.1, 0.2));
        assertEquals("c", result.getValue());
        assertEquals(0.2, result.getWeight());

        result = instance.findBestCategory(new Vector3(0.0, 0.0, 0.0));
        assertEquals("a", result.getValue());
        assertEquals(0.0, result.getWeight());
    }

    /**
     * Test of getEvaluator method, of class WinnerTakeAllCategorizer.
     */
    public void testGetEvaluator()
    {
        this.testSetEvaluator();
    }

    /**
     * Test of setEvaluator method, of class WinnerTakeAllCategorizer.
     */
    public void testSetEvaluator()
    {
        Evaluator<Vectorizable, Vector> evaluator = null;
        WinnerTakeAllCategorizer<Vector, String> instance = new WinnerTakeAllCategorizer<Vector, String>();
        assertSame(evaluator, instance.getEvaluator());

        evaluator = new VectorizableVectorConverter();
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());


        evaluator = new VectorizableVectorConverter();
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());

        evaluator = null;
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());

    }

    /**
     * Test of setCategories method, of class WinnerTakeAllCategorizer.
     */
    public void testSetCategories()
    {
        Set<String> categories = null;
        WinnerTakeAllCategorizer<Vector, String> instance = new WinnerTakeAllCategorizer<Vector, String>();
        assertTrue(instance.getCategories().isEmpty());

        categories = new HashSet<String>();
        instance.setCategories(categories);
        assertSame(categories, instance.getCategories());

        categories = new LinkedHashSet<String>();
        categories.add("a");
        instance.setCategories(categories);
        assertSame(categories, instance.getCategories());

        categories = null;
        instance.setCategories(categories);
        assertSame(categories, instance.getCategories());
    }

    /**
     * Test of Learner class of class WinnerTakeAllCategorizer.
     */
    public void testLearner()
    {
        WinnerTakeAllCategorizer.Learner<Vector, String> learner = 
            new WinnerTakeAllCategorizer.Learner<Vector, String>();
        learner.setLearner(new MatrixMultiplyVectorFunction.ClosedFormSolver());

        ArrayList<InputOutputPair<Vector, String>> training =
            new ArrayList<InputOutputPair<Vector, String>>();
        training.add(new DefaultInputOutputPair<Vector, String>(new Vector3(1.0, 0.0, 0.0), "a"));
        training.add(new DefaultInputOutputPair<Vector, String>(new Vector3(0.0, 2.0, 0.0), "b"));
        training.add(new DefaultInputOutputPair<Vector, String>(new Vector3(0.0, 0.0, 3.0), "c"));

        WinnerTakeAllCategorizer<Vector, String> instance = learner.learn(training);
        assertTrue(instance.getEvaluator() instanceof MatrixMultiplyVectorFunction);
        assertEquals(3, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));
        assertTrue(instance.getCategories().contains("b"));
        assertTrue(instance.getCategories().contains("c"));

        assertEquals("a", instance.evaluate(new Vector3(1.0, 0.0, 0.0)));
        assertEquals("b", instance.evaluate(new Vector3(0.0, 1.0, 0.0)));
        assertEquals("c", instance.evaluate(new Vector3(0.0, 0.0, 1.0)));

        assertEquals("a", instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
        assertEquals("a", instance.evaluate(new Vector3(0.0, -1.0, -1.0)));
        assertEquals("a", instance.evaluate(new Vector3(1.0, 1.0, 1.0)));
    }

}
