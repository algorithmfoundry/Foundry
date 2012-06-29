/*
 * File:            AveragingEnsembleTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AveragingEnsemble.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class AveragingEnsembleTest
{

    /**
     * Creates a new test.
     */
    public AveragingEnsembleTest()
    {
    }

    /**
     * Test of constructors of class AveragingEnsemble.
     */
    @Test
    public void testConstructors()
    {
        AveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new AveragingEnsemble<Double, Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<Evaluator<Double, Double>> members =
            new ArrayList<Evaluator<Double, Double>>();
        instance = new AveragingEnsemble<Double, Evaluator<Double, Double>>(
            members);
        assertSame(members, instance.getMembers());
    }

    /**
     * Test of evaluate method, of class AveragingEnsemble.
     */
    @Test
    public void testEvaluate()
    {
        AveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new AveragingEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluate(1.0), 0.0);
        assertEquals(0.0, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluate(1.0), 0.0);
        assertEquals(3.5, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0));
        instance.add(new LinearFunction(0.0, -1.0));

        assertEquals(1.0 / 4, instance.evaluate(0.0), 0.0);
        assertEquals(4.5 / 4, instance.evaluate(1.0), 0.0);
        assertEquals(8.0 / 4, instance.evaluate(2.0), 0.0);
        assertEquals(11.5 / 4, instance.evaluate(3.0), 0.0);
        assertEquals(2.75 / 4, instance.evaluate(0.5), 0.0);
        assertEquals(-0.75 / 4, instance.evaluate(-0.5), 0.0);
        assertEquals(-9.5 / 4, instance.evaluate(-3.0), 0.0);
    }

    /**
     * Test of evaluateAsDouble method, of class AveragingEnsemble.
     */
    @Test
    public void testEvaluateAsDouble()
    {
        AveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new AveragingEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluateAsDouble(1.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(3.5, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0));
        instance.add(new LinearFunction(0.0, -1.0));

        assertEquals(1.0 / 4, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(4.5 / 4, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(8.0 / 4, instance.evaluateAsDouble(2.0), 0.0);
        assertEquals(11.5 / 4, instance.evaluateAsDouble(3.0), 0.0);
        assertEquals(2.75 / 4, instance.evaluateAsDouble(0.5), 0.0);
        assertEquals(-0.75 / 4, instance.evaluateAsDouble(-0.5), 0.0);
        assertEquals(-9.5 / 4, instance.evaluateAsDouble(-3.0), 0.0);
    }
    
}
