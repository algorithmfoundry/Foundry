/*
 * File:                CompositeEvaluatorListTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 30, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.evaluator.CompositeEvaluatorPairTest.Adder;
import gov.sandia.cognition.evaluator.CompositeEvaluatorPairTest.Multiplier;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * Tests of CompositeEvaluatorList
 * @author  Justin Basilico
 * @since   2.1
 */
public class CompositeEvaluatorListTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public CompositeEvaluatorListTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Test of constructors of CompositeEvaluatorList.
     */
    public void testConstructors()
    {
        CompositeEvaluatorList<Number, Number> instance = 
            new CompositeEvaluatorList<Number, Number>();
        assertTrue(instance.getEvaluators().isEmpty());
        
        Evaluator[] evaluatorsArray =
        {
            new Adder(),
            new Multiplier(),
            new Adder()
        };
        
        instance = 
            new CompositeEvaluatorList<Number, Number>(evaluatorsArray);
        assertFalse(instance.getEvaluators().isEmpty());
        assertEquals(evaluatorsArray.length, instance.getEvaluators().size());
        for (int i = 0; i < evaluatorsArray.length; i++)
        {
            assertSame(evaluatorsArray[i], instance.getEvaluators().get(i));
        }
        
        ArrayList<Evaluator<?,?>> evaluatorsList = new ArrayList<Evaluator<?,?>>();
        for (int i = 0; i < evaluatorsArray.length; i++)
        {
            evaluatorsList.add(evaluatorsArray[i]);
        }
        
        instance = new CompositeEvaluatorList<Number, Number>(evaluatorsList);
        assertNotSame(evaluatorsList, instance.getEvaluators());
        assertFalse(instance.getEvaluators().isEmpty());
        assertEquals(evaluatorsArray.length, instance.getEvaluators().size());
        for (int i = 0; i < evaluatorsArray.length; i++)
        {
            assertSame(evaluatorsArray[i], instance.getEvaluators().get(i));
        }
        
        
    }
    
    public void testClone()
    {
        System.out.println( "clone" );
        
        Evaluator[] evaluatorsArray =
        {
            new Adder(),
            new Multiplier(),
            new Adder()
        };
        
        CompositeEvaluatorList<Number, Number> instance =
            new CompositeEvaluatorList<Number, Number>(evaluatorsArray);
        CompositeEvaluatorList<Number, Number> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );

    }

    /**
     * Test of evaluate method, of class CompositeEvaluatorList.
     */
    public void testEvaluate()
    {
        Adder adder = new Adder(4.0);
        Multiplier multiplier = new Multiplier(7.0);
        CompositeEvaluatorList<Number, Number> instance = 
            new CompositeEvaluatorList<Number, Number>(adder, multiplier);
        
        double input = 10;
        assertEquals(((input + 4.0) * 7.0), instance.evaluate(input));
        
        instance.setEvaluators(adder, multiplier, adder);
        assertEquals(((input + 4.0) * 7.0) + 4.0, instance.evaluate(input));
        
        instance.setEvaluators(multiplier, adder, adder);
        assertEquals(((input * 7.0) + 4.0) + 4.0, instance.evaluate(input));
        
        instance.setEvaluators(multiplier, multiplier, multiplier);
        assertEquals(((input * 7.0) * 7.0) * 7.0, instance.evaluate(input));
        
        instance.setEvaluators(adder, adder, adder);
        assertEquals(((input + 4.0) + 4.0) + 4.0, instance.evaluate(input));
    }

    /**
     * Test of getEvaluators method, of class CompositeEvaluatorList.
     */
    public void testGetEvaluators()
    {
        this.testSetEvaluators();
    }

    /**
     * Test of setEvaluators method, of class CompositeEvaluatorList.
     */
    public void testSetEvaluators()
    {
        CompositeEvaluatorList<Number, Number> instance = 
            new CompositeEvaluatorList<Number, Number>();
        assertNotNull(instance.getEvaluators());
        assertTrue(instance.getEvaluators().isEmpty());
        assertSame(instance.getEvaluators(), instance.getEvaluators());
        
        Evaluator[] evaluatorsArray =
        {
            new Adder(),
            new Multiplier(),
            new Adder()
        };
        
        instance.setEvaluators(evaluatorsArray);
        assertFalse(instance.getEvaluators().isEmpty());
        assertEquals(evaluatorsArray.length, instance.getEvaluators().size());
        for (int i = 0; i < evaluatorsArray.length; i++)
        {
            assertSame(evaluatorsArray[i], instance.getEvaluators().get(i));
        }
        
        ArrayList<Evaluator<?,?>> evaluatorsList = new ArrayList<Evaluator<?,?>>();
        for (int i = 0; i < evaluatorsArray.length; i++)
        {
            evaluatorsList.add(evaluatorsArray[i]);
        }
        
        instance.setEvaluators(evaluatorsList);
        assertNotSame(evaluatorsList, instance.getEvaluators());
        assertFalse(instance.getEvaluators().isEmpty());
        assertEquals(evaluatorsArray.length, instance.getEvaluators().size());
        for (int i = 0; i < evaluatorsArray.length; i++)
        {
            assertSame(evaluatorsArray[i], instance.getEvaluators().get(i));
        }
    }

}
