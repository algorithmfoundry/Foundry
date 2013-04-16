/*
 * File:            WeightedAveragingEnsembleTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Justin Basilico
 */
public class WeightedAveragingEnsembleTest
    extends AbstractWeightedEnsembleTest
{

    /**
     * Creates a new test.
     */
    public WeightedAveragingEnsembleTest()
    {
        super();
    }

    /**
     * Test of constructors of class WeightedAveragingEnsemble.
     */
    @Test
    public void testConstructors()
    {
        WeightedAveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAveragingEnsemble<Double, Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<WeightedValue<Evaluator<Double, Double>>> members =
            new ArrayList<WeightedValue<Evaluator<Double, Double>>>();
        instance = new WeightedAveragingEnsemble<Double, Evaluator<Double, Double>>(
            members);
        assertSame(members, instance.getMembers());
    }


    @Test
    public void testAdd()
    {
        super.testAdd();

        WeightedAveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAveragingEnsemble<Double, Evaluator<Double, Double>>();
        
        boolean exceptionThrown = false;
        try
        {
            instance.add(new LinearFunction(), -0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(0, instance.getMembers().size());
    }

    /**
     * Test of evaluate method, of class WeightedAveragingEnsemble.
     */
    @Test
    public void testEvaluate()
    {
        WeightedAveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAveragingEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluate(1.0), 0.0);
        assertEquals(0.0, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluate(1.0), 0.0);
        assertEquals(3.5, instance.evaluate(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0), 2.0);
        instance.add(new LinearFunction(0.0, -1.0), 0.5);

        assertEquals(1.5 / 4.5, instance.evaluate(0.0), 0.0);
        assertEquals(7.0 / 4.5, instance.evaluate(1.0), 0.0);
        assertEquals(12.5 / 4.5, instance.evaluate(2.0), 0.0);
        assertEquals(18.0 / 4.5, instance.evaluate(3.0), 0.0);
        assertEquals(4.25 / 4.5, instance.evaluate(0.5), 0.0);
        assertEquals(-1.25 / 4.5, instance.evaluate(-0.5), 0.0);
        assertEquals(-15.0 / 4.5, instance.evaluate(-3.0), 0.0);
    }

    /**
     * Test of evaluateAsDouble method, of class WeightedAveragingEnsemble.
     */
    @Test
    public void testEvaluateAsDouble()
    {
        WeightedAveragingEnsemble<Double, Evaluator<Double, Double>> instance =
            new WeightedAveragingEnsemble<Double, Evaluator<Double, Double>>();

        assertEquals(0.0, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(0.0, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(0.5, 2.0));

        assertEquals(2.5, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(3.5, instance.evaluateAsDouble(3.0), 0.0);

        instance.add(new LinearFunction(1.0, 0.0));
        instance.add(new LinearFunction(2.0, 0.0), 2.0);
        instance.add(new LinearFunction(0.0, -1.0), 0.5);

        assertEquals(1.5 / 4.5, instance.evaluateAsDouble(0.0), 0.0);
        assertEquals(7.0 / 4.5, instance.evaluateAsDouble(1.0), 0.0);
        assertEquals(12.5 / 4.5, instance.evaluateAsDouble(2.0), 0.0);
        assertEquals(18.0 / 4.5, instance.evaluateAsDouble(3.0), 0.0);
        assertEquals(4.25 / 4.5, instance.evaluateAsDouble(0.5), 0.0);
        assertEquals(-1.25 / 4.5, instance.evaluateAsDouble(-0.5), 0.0);
        assertEquals(-15.0 / 4.5, instance.evaluateAsDouble(-3.0), 0.0);
    }


}
