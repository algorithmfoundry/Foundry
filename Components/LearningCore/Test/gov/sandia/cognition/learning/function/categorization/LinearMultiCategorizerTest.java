/*
 * File:                LinearMultiCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.LinkedHashMap;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LinearMultiCategorizer.
 *
 * @author  Justin Basilico
 * @since   3.2.0
 */
public class LinearMultiCategorizerTest
{
    /** Random number generator. */
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public LinearMultiCategorizerTest()
    {
        super();
    }

    /**
     * Test of constructors of class LinearMultiCategorizer.
     */
    @Test
    public void testConstructors()
    {

        LinearMultiCategorizer<String> instance = new LinearMultiCategorizer<String>();
        assertNotNull(instance.getPrototypes());
        assertTrue(instance.getPrototypes().isEmpty());

        Map<String, LinearBinaryCategorizer> prototypes = new TreeMap<String, LinearBinaryCategorizer>();
        instance = new LinearMultiCategorizer<String>(prototypes);
        assertSame(prototypes, instance.getPrototypes());
    }

    /**
     * Test of clone method, of class LinearMultiCategorizer.
     */
    @Test
    public void testClone()
    {
        LinearMultiCategorizer<String> instance = new LinearMultiCategorizer<String>();
        LinearMultiCategorizer<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(instance.getPrototypes(), clone.getPrototypes());

        Vector w = new Vector2();
        double b = random.nextDouble();
        LinearBinaryCategorizer a = new LinearBinaryCategorizer(w, b);
        instance.getPrototypes().put("a", a);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(instance.getPrototypes(), clone.getPrototypes());
        assertNotNull(instance.getPrototypes().get("a"));
        assertNotSame(a, clone.getPrototypes().get("a"));
        assertEquals(w, clone.getPrototypes().get("a").getWeights());
        assertNotSame(w, clone.getPrototypes().get("a").getWeights());
        assertEquals(b, clone.getPrototypes().get("a").getBias(), 0.0);
    }

    /**
     * Test of evaluate method, of class LinearMultiCategorizer.
     */
    @Test
    public void testEvaluate()
    {
        Vector x = new Vector2(1.0, -1.0);
        LinearMultiCategorizer<String> instance =
            new LinearMultiCategorizer<String>();
        assertEquals(null, instance.evaluate(x));

        instance.getPrototypes().put("a", new LinearBinaryCategorizer(
            new Vector2(1.0, 0.0), 1.0));
        assertEquals("a", instance.evaluate(x));
        instance.getPrototypes().put("b", new LinearBinaryCategorizer(
            new Vector2(-1.0, 0.0), 0.0));
        instance.getPrototypes().put("c", new LinearBinaryCategorizer(
            new Vector2(-1.0, 4.0), -5.0));
        instance.getPrototypes().put("d", new LinearBinaryCategorizer(
            new Vector2(0.0, 0.0), 0.0));

        assertEquals("a", instance.evaluate(x));
        assertEquals("b", instance.evaluate(new Vector2(-1.0, 1.0)));
        assertEquals("c", instance.evaluate(new Vector2(-1.0, 10.0)));
    }

    /**
     * Test of evaluateWithDiscriminant method, of class LinearMultiCategorizer.
     */
    @Test
    public void testEvaluateWithDiscriminant()
    {
        Vector x = new Vector2(1.0, -1.0);
        LinearMultiCategorizer<String> instance =
            new LinearMultiCategorizer<String>();
        DefaultWeightedValueDiscriminant<String> result =
            instance.evaluateWithDiscriminant(x);

        assertEquals(null, result.getValue());
        assertEquals(0.0, result.getDiscriminant(), 0.0);

        instance.getPrototypes().put("a", new LinearBinaryCategorizer(
            new Vector2(1.0, 0.0), 1.0));
        result = instance.evaluateWithDiscriminant(x);
        assertEquals("a", result.getValue());
        assertEquals(2.0, result.getDiscriminant(), 0.0);

        instance.getPrototypes().put("b", new LinearBinaryCategorizer(
            new Vector2(-1.0, 0.0), 0.0));
        instance.getPrototypes().put("c", new LinearBinaryCategorizer(
            new Vector2(-1.0, 4.0), -5.0));
        instance.getPrototypes().put("d", new LinearBinaryCategorizer(
            new Vector2(0.0, 0.0), 0.0));

        result = instance.evaluateWithDiscriminant(x);
        assertEquals("a", result.getValue());
        assertEquals(2.0, result.getDiscriminant(), 0.0);
        result = instance.evaluateWithDiscriminant(new Vector2(-1.0, 1.0));
        assertEquals("b", result.getValue());
        assertEquals(1.0, result.getDiscriminant(), 0.0);
        result = instance.evaluateWithDiscriminant(new Vector2(-1.0, 10.0));
        assertEquals("c", result.getValue());
        assertEquals(36.0, result.getDiscriminant(), 0.0);
    }

    /**
     * Test of evaluateAsDouble method, of class LinearMultiCategorizer.
     */
    @Test
    public void testEvaluateAsDouble()
    {
        Vector x = new Vector2(1.0, -1.0);
        LinearMultiCategorizer<String> instance =
            new LinearMultiCategorizer<String>();
        assertEquals(0.0, instance.evaluateAsDouble(x, "a"), 0.0);

        instance.getPrototypes().put("a", new LinearBinaryCategorizer(
            new Vector2(1.0, 0.0), 1.0));
        assertEquals(2.0, instance.evaluateAsDouble(x, "a"), 0.0);
        instance.getPrototypes().put("b", new LinearBinaryCategorizer(
            new Vector2(-1.0, 0.0), 0.0));
        instance.getPrototypes().put("c", new LinearBinaryCategorizer(
            new Vector2(-1.0, 4.0), -5.0));
        instance.getPrototypes().put("d", new LinearBinaryCategorizer(
            new Vector2(0.0, 0.0), 0.0));

        assertEquals(2.0, instance.evaluateAsDouble(x, "a"), 0.0);
        assertEquals(-1.0, instance.evaluateAsDouble(x, "b"), 0.0);
        assertEquals(-10.0, instance.evaluateAsDouble(x, "c"), 0.0);
        assertEquals(0.0, instance.evaluateAsDouble(x, "d"), 0.0);
    }

    /**
     * Test of getCategories method, of class LinearMultiCategorizer.
     */
    @Test
    public void testGetCategories()
    {
        LinearMultiCategorizer<String> instance = new LinearMultiCategorizer<String>();
        assertTrue(instance.getCategories().isEmpty());

        instance.getPrototypes().put("a", new LinearBinaryCategorizer());
        instance.getPrototypes().put("b", new LinearBinaryCategorizer());
        instance.getPrototypes().put("c", new LinearBinaryCategorizer());
        assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c")),
            instance.getCategories());
    }

    /**
     * Test of getInputDimensionality method, of class LinearMultiCategorizer.
     */
    @Test
    public void testGetInputDimensionality()
    {
        LinearMultiCategorizer<String> instance = new LinearMultiCategorizer<String>();
        assertEquals(-1, instance.getInputDimensionality());

        instance.getPrototypes().put("a", new LinearBinaryCategorizer());
        assertEquals(-1, instance.getInputDimensionality());

        int d = 1 + random.nextInt(100);
        instance.getPrototypes().get("a").setWeights(
            VectorFactory.getDefault().createVector(d));
        assertEquals(d, instance.getInputDimensionality());
    }

    /**
     * Test of getPrototypes method, of class LinearMultiCategorizer.
     */
    @Test
    public void testGetPrototypes()
    {
        this.testSetPrototypes();
    }

    /**
     * Test of setPrototypes method, of class LinearMultiCategorizer.
     */
    @Test
    public void testSetPrototypes()
    {
        LinearMultiCategorizer<String> instance = 
            new LinearMultiCategorizer<String>();
        assertNotNull(instance.getPrototypes());
        assertTrue(instance.getPrototypes().isEmpty());

        Map<String, LinearBinaryCategorizer> prototypes =
            new LinkedHashMap<String, LinearBinaryCategorizer>();
        instance.setPrototypes(prototypes);
        assertSame(prototypes, instance.getPrototypes());

        prototypes = null;
        instance.setPrototypes(prototypes);
        assertSame(prototypes, instance.getPrototypes());

        prototypes = new TreeMap<String, LinearBinaryCategorizer>();
        instance.setPrototypes(prototypes);
        assertSame(prototypes, instance.getPrototypes());
    }

}