/*
 * File:                BinaryVersusCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner;
import gov.sandia.cognition.learning.algorithm.tree.VectorThresholdInformationGainLearner;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

/**
 * Unit tests for class {@code BinaryVersusCategorizer}.
 *
 * @author  Justin Basilico
 * @version 3.0
 */
public class BinaryVersusCategorizerTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public BinaryVersusCategorizerTest(
        final String testName)
    {
        super(testName);
    }
    /**
     * Test of constructors of class BinaryVersusCategorizer.
     */
    public void testConstructors()
    {
        BinaryVersusCategorizer<Vector, String> instance =
            new BinaryVersusCategorizer<Vector, String>();
        assertTrue(instance.getCategories().isEmpty());
        assertTrue(instance.getCategoryPairsToEvaluatorMap().isEmpty());

        Set<String> categories = new HashSet<String>();
        categories.add("a");
        categories.add("b");
        categories.add("c");

        instance = new BinaryVersusCategorizer<Vector, String>(categories);
        assertSame(categories, instance.getCategories());
        assertTrue(instance.getCategoryPairsToEvaluatorMap().isEmpty());

        LinkedHashMap<Pair<String, String>, Evaluator<? super Vector, Boolean>> categoryPairsToEvaluatorMap =
            new LinkedHashMap<Pair<String, String>, Evaluator<? super Vector, Boolean>>();

        instance = new BinaryVersusCategorizer<Vector, String>(
            categories, categoryPairsToEvaluatorMap);
        assertSame(categories, instance.getCategories());
        assertSame(categoryPairsToEvaluatorMap, instance.getCategoryPairsToEvaluatorMap());
    }

    /**
     * Test of clone method, of class BinaryVersusCategorizer.
     */
    public void testClone()
    {
        BinaryVersusCategorizer<Vector, String> instance =
            new BinaryVersusCategorizer<Vector, String>();

        BinaryVersusCategorizer<Vector, String> clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(clone, instance);

        instance.getCategories().add("a");
        instance.getCategories().add("b");
        instance.getCategoryPairsToEvaluatorMap().put(
            new DefaultPair<String, String>("a", "b"),
            new VectorElementThresholdCategorizer(0, 1.0));

        clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(clone, instance);
        assertNotSame(clone.categoryPairsToEvaluatorMap, instance.categoryPairsToEvaluatorMap);
        assertEquals(clone.categoryPairsToEvaluatorMap.keySet(), instance.categoryPairsToEvaluatorMap.keySet());
    }

    /**
     * Test of evaluate method, of class BinaryVersusCategorizer.
     */
    public void testEvaluate()
    {
        BinaryVersusCategorizer<Vector, String> instance =
            new BinaryVersusCategorizer<Vector, String>();
        
        assertNull(instance.evaluate(new Vector3()));

        instance.getCategories().add("a");
        instance.getCategories().add("b");
        instance.getCategoryPairsToEvaluatorMap().put(
            new DefaultPair<String, String>("a", "b"),
            new VectorElementThresholdCategorizer(0, 1.0));

        assertEquals("a", instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
        assertEquals("b", instance.evaluate(new Vector3(2.0, 0.0, 0.0)));

        instance.getCategories().add("c");
        instance.getCategoryPairsToEvaluatorMap().put(
            new DefaultPair<String, String>("a", "c"),
            new VectorElementThresholdCategorizer(1, 1.0));

        instance.getCategoryPairsToEvaluatorMap().put(
            new DefaultPair<String, String>("b", "c"),
            new VectorElementThresholdCategorizer(1, 1.0));
        assertEquals("a", instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
        assertEquals("b", instance.evaluate(new Vector3(2.0, 0.0, 0.0)));
        assertEquals("c", instance.evaluate(new Vector3(0.0, 2.0, 0.0)));
        assertEquals("c", instance.evaluate(new Vector3(2.0, 2.0, 0.0)));
    }

    /**
     * Test of getCategoryPairsToEvaluatorMap method, of class BinaryVersusCategorizer.
     */
    public void testGetCategoryPairsToEvaluatorMap()
    {
        this.testSetCategoryPairsToEvaluatorMap();
    }

    /**
     * Test of setCategoryPairsToEvaluatorMap method, of class BinaryVersusCategorizer.
     */
    public void testSetCategoryPairsToEvaluatorMap()
    {
        BinaryVersusCategorizer<Vector, String> instance =
            new BinaryVersusCategorizer<Vector, String>();
        assertTrue(instance.getCategoryPairsToEvaluatorMap().isEmpty());

        LinkedHashMap<Pair<String, String>, Evaluator<? super Vector, Boolean>> categoryPairsToEvaluatorMap =
            new LinkedHashMap<Pair<String, String>, Evaluator<? super Vector, Boolean>>();

        instance.setCategoryPairsToEvaluatorMap(categoryPairsToEvaluatorMap);
        assertSame(categoryPairsToEvaluatorMap, instance.getCategoryPairsToEvaluatorMap());

        categoryPairsToEvaluatorMap = null;
        instance.setCategoryPairsToEvaluatorMap(categoryPairsToEvaluatorMap);
        assertSame(categoryPairsToEvaluatorMap, instance.getCategoryPairsToEvaluatorMap());
    }

    /**
     * Test of Learner class of class BinaryVersusCategorizer.
     */
    public void testLearner()
    {
        BinaryVersusCategorizer.Learner<Vector3, String> instance =
            new BinaryVersusCategorizer.Learner<Vector3, String>();
        instance.setLearner(
                new CategorizationTreeLearner<Vector3, Boolean>(
                    new VectorThresholdInformationGainLearner<Boolean>()));
        BinaryVersusCategorizer<Vector3, String> result;

        List<InputOutputPair<Vector3, String>> data =
            new LinkedList<InputOutputPair<Vector3, String>>();
        
        result = instance.learn(data);
        assertNotNull(result);
        assertTrue(result.getCategories().isEmpty());

        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 4.0, 2.0), "a"));

        result = instance.learn(data);
        assertEquals("a", result.evaluate(data.get(0).getInput()));
        assertEquals(1, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));

        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 2.0), "a"));

        result = instance.learn(data);
        assertEquals("a", result.evaluate(data.get(0).getInput()));
        assertEquals("a", result.evaluate(data.get(1).getInput()));
        assertEquals(1, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));

        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 2.0, 3.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 4.0, 4.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 3.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 0.0, 2.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 5.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 7.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 8.0, 2.0), "b"));

        result = instance.learn(data);
        for ( InputOutputPair<Vector3, String> example : data )
        {
            assertEquals(example.getOutput(), result.evaluate(example.getInput()));
        }
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));

        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "b"));

        result = instance.learn(data);
        assertEquals("a", result.evaluate(new Vector3(1.0, 1.0, 1.0)));
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));


        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "b"));

        result = instance.learn(data);
        assertEquals("b", result.evaluate(new Vector3(1.0, 1.0, 1.0)));
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));
    }

}
