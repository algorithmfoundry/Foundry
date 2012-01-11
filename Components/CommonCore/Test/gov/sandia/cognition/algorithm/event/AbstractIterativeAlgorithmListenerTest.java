/*
 * File:            AbstractIterativeAlgorithmListenerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.algorithm.event;

import org.junit.Test;

/**
 *
 * Unit tests for class AbstractIterativeAlgorithmListener.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class AbstractIterativeAlgorithmListenerTest
{

    /**
     * Creates a new test.
     */
    public AbstractIterativeAlgorithmListenerTest()
    {
        super();
    }

    /**
     * Test of constructors of class AbstractIterativeAlgorithmListener.
     */
    @Test
    public void testConstructors()
    {
        // This is a simple test that just makes sure that an exception is not
        // thrown.
        AbstractIterativeAlgorithmListener instance =
            new DummyIterativeAlgorithmListener();
    }

    /**
     * Test of algorithmStarted method, of class AbstractIterativeAlgorithmListener.
     */
    @Test
    public void testAlgorithmStarted()
    {
        // This is a simple test that just makes sure that an exception is not
        // thrown.
        AbstractIterativeAlgorithmListener instance =
            new DummyIterativeAlgorithmListener();
        instance.algorithmStarted(null);
    }

    /**
     * Test of algorithmEnded method, of class AbstractIterativeAlgorithmListener.
     */
    @Test
    public void testAlgorithmEnded()
    {
        // This is a simple test that just makes sure that an exception is not
        // thrown.
        AbstractIterativeAlgorithmListener instance =
            new DummyIterativeAlgorithmListener();
        instance.algorithmEnded(null);
    }

    /**
     * Test of stepStarted method, of class AbstractIterativeAlgorithmListener.
     */
    @Test
    public void testStepStarted()
    {
        // This is a simple test that just makes sure that an exception is not
        // thrown.
        AbstractIterativeAlgorithmListener instance =
            new DummyIterativeAlgorithmListener();
        instance.stepStarted(null);
    }

    /**
     * Test of stepEnded method, of class AbstractIterativeAlgorithmListener.
     */
    @Test
    public void testStepEnded()
    {
        // This is a simple test that just makes sure that an exception is not
        // thrown.
        AbstractIterativeAlgorithmListener instance =
            new DummyIterativeAlgorithmListener();
        instance.stepEnded(null);
    }

    /**
     * A testing only class that just ensures that no additional methods need
     * to be implemented for the abstract class.
     */
    public class DummyIterativeAlgorithmListener
        extends AbstractIterativeAlgorithmListener
    {

        /**
         * Creates a new {@code DummyIterativeAlgorithmListener}.
         */
        public DummyIterativeAlgorithmListener()
        {
            super();
        }

        // No additional methods should be added here.
    }

}
