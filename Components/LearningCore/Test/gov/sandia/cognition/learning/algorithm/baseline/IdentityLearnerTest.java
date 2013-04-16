/*
 * File:            IdentityLearnerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@code IdentityLearner}.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class IdentityLearnerTest
{

    /**
     * Creates a new test.
     */
    public IdentityLearnerTest()
    {
    }

    /**
     * Test of constructors of class IdentityLearner.
     */
    @Test
    public void testConstructors()
    {
        IdentityLearner<String> instance = new IdentityLearner<String>();
        assertNotNull(instance);
    }

    /**
     * Test of clone method, of class IdentityLearner.
     */
    @Test
    public void testClone()
    {
        IdentityLearner<String> instance = new IdentityLearner<String>();
        IdentityLearner<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of learn method, of class IdentityLearner.
     */
    @Test
    public void testLearn()
    {
        IdentityLearner<String> instance = new IdentityLearner<String>();
        
        String input = "a";
        assertSame(input, instance.learn(input));

        input = new String("another");
        assertSame(input, instance.learn(input));

        input = null;
        assertSame(input, instance.learn(input));
    }

    /**
     * Test of create method, of class IdentityLearner.
     */
    @Test
    public void testCreate()
    {
        IdentityLearner<String> instance = IdentityLearner.create();
        assertNotNull(instance);
        assertNotSame(instance, IdentityLearner.create());
    }

}
