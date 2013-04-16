/*
 * File:            WeightedAdditiveEnsembleTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.util.WeightedValue;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class WeightedAdditiveEnsemble.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class WeightedAdditiveEnsembleTest
{

    /**
     * Creates a new test.
     */
    public WeightedAdditiveEnsembleTest()
    {
        super();
    }

    /**
     * Test of constructors of class WeightedAdditiveEnsemble.
     */
    @Test
    public void testConstructors()
    {
        double bias = 0.0;
        WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());
        assertEquals(bias, instance.getBias(), 0.0);

        List<WeightedValue<Evaluator<Double, Double>>> members =
            new ArrayList<WeightedValue<Evaluator<Double, Double>>>();
        instance = new WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>>(
            members);
        assertSame(members, instance.getMembers());
        assertEquals(bias, instance.getBias(), 0.0);

        bias = 3.3;
        instance = new WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>>(
            members, bias);
        assertSame(members, instance.getMembers());
        assertEquals(bias, instance.getBias(), 0.0);
    }

    /**
     * Test of evaluate method, of class WeightedAdditiveEnsemble.
     */
    @Test
    public void testEvaluate()
    {
        WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluate(1.0), 0.0);
        assertEquals(0.0, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluate(1.0), 0.0);
        assertEquals(3.5, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0), 2.0);
        instance.add(new LinearFunction(0.0, -1.0), 0.5);

        assertEquals(1.5, instance.evaluate(0.0), 0.0);
        assertEquals(7.0, instance.evaluate(1.0), 0.0);
        assertEquals(12.5, instance.evaluate(2.0), 0.0);
        assertEquals(18.0, instance.evaluate(3.0), 0.0);
        assertEquals(4.25, instance.evaluate(0.5), 0.0);
        assertEquals(-1.25, instance.evaluate(-0.5), 0.0);
        assertEquals(-15.0, instance.evaluate(-3.0), 0.0);

        // Make sure the bias is used.
        instance.setBias(0.5);
        assertEquals(2.0, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(7.5, instance.evaluateAsDouble(1.0), 0.0);
    }

    /**
     * Test of evaluateAsDouble method, of class WeightedAdditiveEnsemble.
     */
    @Test
    public void testEvaluateAsDouble()
    {
        WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(0.0, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(3.5, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0), 2.0);
        instance.add(new LinearFunction(0.0, -1.0), 0.5);

        assertEquals(1.5, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(7.0, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(12.5, instance.evaluateAsDouble(2.0), 0.0);
        assertEquals(18.0, instance.evaluateAsDouble(3.0), 0.0);
        assertEquals(4.25, instance.evaluateAsDouble(0.5), 0.0);
        assertEquals(-1.25, instance.evaluateAsDouble(-0.5), 0.0);
        assertEquals(-15.0, instance.evaluateAsDouble(-3.0), 0.0);

        // Make sure the bias is used.
        instance.setBias(0.5);
        assertEquals(2.0, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(7.5, instance.evaluateAsDouble(1.0), 0.0);
    }

    @Test
    public void testGetBias()
    {
        this.testSetBias();
    }

    @Test
    public void testSetBias()
    {
        double bias = 0.0;
        WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAdditiveEnsemble<Double, Evaluator<Double, Double>>();
        assertEquals(bias, instance.getBias(), 0.0);

        bias = 1.3;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias(), 0.0);

        bias = -3.1;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias(), 0.0);
    }

}
