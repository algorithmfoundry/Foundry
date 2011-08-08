/*
 * File:                CompositeEvaluatorTripleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 29, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.evaluator.CompositeEvaluatorPairTest.Adder;
import gov.sandia.cognition.evaluator.CompositeEvaluatorPairTest.Multiplier;
import junit.framework.TestCase;

/**
 * Tests of CompositeEvaluatorTriple
 * @author  Justin Basilico
 * @since   2.1
 */
public class CompositeEvaluatorTripleTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public CompositeEvaluatorTripleTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of CompositeEvaluatorTriple.
     */
    public void testConstructors()
    {
        Evaluator<? super Number, ? extends Number> first = null;
        Evaluator<? super Number, ? extends Number> second = null;
        Evaluator<? super Number, ? extends Number> third = null;
        CompositeEvaluatorTriple<Number, Number, Number, Number> instance = 
            new CompositeEvaluatorTriple<Number, Number, Number, Number>();
        assertSame(first, instance.getFirst());
        assertSame(second, instance.getSecond());
        
        first = new Adder();
        second = new Multiplier();
        third = new Adder();
        
        instance = 
            new CompositeEvaluatorTriple<Number, Number, Number, Number>(first, second, third);
        assertSame(first, instance.getFirst());
        assertSame(second, instance.getSecond());
        assertSame(third, instance.getThird());
    }
    
    /**
     * Test of evaluate method, of class CompositeEvaluatorTriple.
     */
    public void testEvaluate()
    {
        Adder adder = new Adder(4.0);
        Multiplier multiplier = new Multiplier(7.0);
        CompositeEvaluatorTriple<Number, Number, Number, Number> instance = 
            new CompositeEvaluatorTriple<Number, Number, Number, Number>(adder, multiplier, adder);
        
        double input = 10;
        assertEquals(((input + 4.0) * 7.0) + 4.0, instance.evaluate(input));
        
        instance.setFirst(multiplier);
        instance.setSecond(adder);
        instance.setThird(adder);
        assertEquals(((input * 7.0) + 4.0) + 4.0, instance.evaluate(input));
        
        instance.setFirst(multiplier);
        instance.setSecond(multiplier);
        instance.setThird(multiplier);
        assertEquals(((input * 7.0) * 7.0) * 7.0, instance.evaluate(input));
        
        instance.setFirst(adder);
        instance.setSecond(adder);
        instance.setThird(adder);
        assertEquals(((input + 4.0) + 4.0) + 4.0, instance.evaluate(input));
    }

    /**
     * Test of create method, of class CompositeEvaluatorTriple.
     */
    public void testCreate()
    {
        Adder first = new Adder();
        Multiplier second = new Multiplier();
        Adder third = new Adder();
        
        CompositeEvaluatorTriple<Number, Double, Double, Double> result = 
            CompositeEvaluatorTriple.create(first, second, third);
        assertSame(first, result.getFirst());
        assertSame(second, result.getSecond());
        assertSame(third, result.getThird());
    }

}
