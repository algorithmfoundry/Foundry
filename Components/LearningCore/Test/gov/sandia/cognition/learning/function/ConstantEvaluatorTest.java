/*
 * File:                ConstantEvaluatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 21, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function;

import junit.framework.TestCase;

/**
 * Tests of ConstantEvaluator
 * @author  Justin Basilico
 * @since   2.1
 */
public class ConstantEvaluatorTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ConstantEvaluatorTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the constructors of class ConstantEvaluator.
     */
    public void testConstructors()
    {
        String value = null;
        ConstantEvaluator<String> instance = new ConstantEvaluator<String>();
        assertSame(value, instance.getValue());
        
        value = "this is a test";
        instance = new ConstantEvaluator<String>(value);
        assertSame(value, instance.getValue());
    }

    /**
     * Test of evaluate method, of class ConstantEvaluator.
     */
    public void testEvaluate()
    {
        String value = "output";
        ConstantEvaluator<String> instance = new ConstantEvaluator<String>(
            value);
        
        assertSame(value, instance.evaluate("input"));
        assertSame(value, instance.evaluate(1));
        assertSame(value, instance.evaluate(new Object()));
        assertSame(value, instance.evaluate(value));
        
    }

    /**
     * Test of getValue method, of class ConstantEvaluator.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class ConstantEvaluator.
     */
    public void testSetValue()
    {
        String value = null;
        ConstantEvaluator<String> instance = new ConstantEvaluator<String>();
        assertSame(value, instance.getValue());
        
        value = "test";
        instance.setValue(value);
        assertSame(value, instance.getValue());
        
        value = "test2";
        instance.setValue(value);
        assertSame(value, instance.getValue());
        
        
        value = null;
        instance.setValue(value);
        assertSame(value, instance.getValue());
        
    }

    /**
     * Test of create method, of class ConstantEvaluator.
     */
    public void testCreate()
    {
        String value = "test";
        ConstantEvaluator<String> instance = ConstantEvaluator.create(value);
        assertSame(value, instance.getValue());
        assertNotSame(instance, ConstantEvaluator.create(value));

        value = null;
        instance = ConstantEvaluator.create(value);
        assertSame(value, instance.getValue());
        assertNotSame(instance, ConstantEvaluator.create(value));
    }
}
