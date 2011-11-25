/*
 * File:            ConstantLearnerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class ConstantLearner.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class ConstantLearnerTest
{

    /**
     * Creates a new test.
     */
    public ConstantLearnerTest()
    {
    }


    /**
     * Test of constructors of class ConstantLearner.
     */
    @Test
    public void testConstructors()
    {
        String value = null;
        ConstantLearner<String> instance = new ConstantLearner<String>();
        assertSame(value, instance.getValue());

        value = "abcd";
        instance = new ConstantLearner<String>(value);
        assertSame(value, instance.getValue());
    }

    /**
     * Test of learn method, of class ConstantLearner.
     */
    @Test
    public void testLearn()
    {
        String value = "some value";
        ConstantLearner<String> instance = ConstantLearner.create(value);
        assertSame(value, instance.learn(null));
        assertSame(value, instance.learn("something else"));
    }

    /**
     * Test of getValue method, of class ConstantLearner.
     */
    @Test
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class ConstantLearner.
     */
    @Test
    public void testSetValue()
    {
        String value = null;
        ConstantLearner<String> instance = new ConstantLearner<String>();
        assertSame(value, instance.getValue());

        String[] goodValues = { null, "", "blargy" };
        for (String goodValue : goodValues)
        {
            value = goodValue;
            instance.setValue(value);
            assertSame(value, instance.getValue());
        }
    }

    /**
     * Test of create method, of class ConstantLearner.
     */
    @Test
    public void testCreate()
    {
        String value = "some value";
        ConstantLearner<String> instance = ConstantLearner.create(value);
        assertSame(value, instance.getValue());
    }

}