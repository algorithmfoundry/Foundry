/*
 * File:            AdditiveEnsembleTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import java.util.List;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AdditiveEnsemble.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class AdditiveEnsembleTest
{

    /**
     * Creates a new test.
     */
    public AdditiveEnsembleTest()
    {
        super();
    }

    /**
     * Test of constructors of class AdditiveEnsemble.
     */
    @Test
    public void testConstructors()
    {
        double bias = 0.0;
        AdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new AdditiveEnsemble<Double, Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());
        assertEquals(bias, instance.getBias(), 0.0);

        List<Evaluator<Double, Double>> members =
            new ArrayList<Evaluator<Double, Double>>();
        instance = new AdditiveEnsemble<Double, Evaluator<Double, Double>>(
            members);
        assertSame(members, instance.getMembers());
        assertEquals(bias, instance.getBias(), 0.0);

        bias = 3.3;
        instance = new AdditiveEnsemble<Double, Evaluator<Double, Double>>(
            members, bias);
        assertSame(members, instance.getMembers());
        assertEquals(bias, instance.getBias(), 0.0);
    }

    /**
     * Test of evaluate method, of class AdditiveEnsemble.
     */
    @Test
    public void testEvaluate()
    {
        AdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new AdditiveEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluate(1.0), 0.0);
        assertEquals(0.0, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluate(1.0), 0.0);
        assertEquals(3.5, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0));
        instance.add(new LinearFunction(0.0, -1.0));

        assertEquals(1.0, instance.evaluate(0.0), 0.0);
        assertEquals(4.5, instance.evaluate(1.0), 0.0);
        assertEquals(8.0, instance.evaluate(2.0), 0.0);
        assertEquals(11.5, instance.evaluate(3.0), 0.0);
        assertEquals(2.75, instance.evaluate(0.5), 0.0);
        assertEquals(-0.75, instance.evaluate(-0.5), 0.0);
        assertEquals(-9.5, instance.evaluate(-3.0), 0.0);

        // Make sure the bias is used.
        instance.setBias(0.5);
        assertEquals(1.5, instance.evaluate(0.0), 0.0);
        assertEquals(5.0, instance.evaluate(1.0), 0.0);
    }

    /**
     * Test of evaluateAsDouble method, of class AdditiveEnsemble.
     */
    @Test
    public void testEvaluateAsDouble()
    {
        AdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new AdditiveEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(0.0, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(3.5, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0));
        instance.add(new LinearFunction(0.0, -1.0));

        assertEquals(1.0, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(4.5, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(8.0, instance.evaluateAsDouble(2.0), 0.0);
        assertEquals(11.5, instance.evaluateAsDouble(3.0), 0.0);
        assertEquals(2.75, instance.evaluateAsDouble(0.5), 0.0);
        assertEquals(-0.75, instance.evaluateAsDouble(-0.5), 0.0);
        assertEquals(-9.5, instance.evaluateAsDouble(-3.0), 0.0);

        // Make sure the bias is used.
        instance.setBias(0.5);
        assertEquals(1.5, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(5.0, instance.evaluateAsDouble(1.0), 0.0);
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
        AdditiveEnsemble<Double, Evaluator<Double, Double>> instance =
            new AdditiveEnsemble<Double, Evaluator<Double, Double>>();
        assertEquals(bias, instance.getBias(), 0.0);

        bias = 1.3;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias(), 0.0);

        bias = -3.1;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias(), 0.0);
    }

}
