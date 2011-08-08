/*
 * File:                AbstractIterativeAlgorithmTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.algorithm;

import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AbstractIterativeLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class AbstractIterativeAlgorithmTest
    extends TestCase
{
    
    public AbstractIterativeAlgorithmTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of addIterativeLearnerListener method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testAddIterativeLearnerListener()
    {
        DummyIterativeLearnerListener listener = 
                new DummyIterativeLearnerListener();
        
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        
        instance.addIterativeAlgorithmListener(listener);
        assertTrue(instance.getListeners().contains(listener));
    }

    public void testClone()
    {
        System.out.println( "Clone" );
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        instance.addIterativeAlgorithmListener( new DummyIterativeLearnerListener() );
        instance.setIteration(10);
        AbstractIterativeAlgorithm clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( 0, clone.getIteration() );
        assertNull( clone.getListeners() );
    }


    /**
     * Test of removeIterativeLearnerListener method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testRemoveIterativeLearnerListener()
    {
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        DummyIterativeLearnerListener listener = 
                new DummyIterativeLearnerListener();
        
        assertNull(instance.getListeners());
        instance.removeIterativeAlgorithmListener(listener);
        
        instance.addIterativeAlgorithmListener(listener);
        assertTrue(instance.getListeners().contains(listener));
        
        instance.removeIterativeAlgorithmListener(listener);
        assertNull(instance.getListeners());
    }

    /**
     * Test of fireAlgorithmStarted method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testFireAlgorithmStarted()
    {
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        DummyIterativeLearnerListener listener = 
                new DummyIterativeLearnerListener();
        instance.addIterativeAlgorithmListener(listener);
        
        assertEquals(0, listener.learningStartedCount);
        instance.fireAlgorithmStarted();
        assertEquals(1, listener.learningStartedCount);
        instance.fireAlgorithmStarted();
        assertEquals(2, listener.learningStartedCount);
    }

    /**
     * Test of fireAlgorithmEnded method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testFireAlgorithmEnded()
    {
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        DummyIterativeLearnerListener listener = 
                new DummyIterativeLearnerListener();
        instance.addIterativeAlgorithmListener(listener);
        
        assertEquals(0, listener.learningEndedCount);
        instance.fireAlgorithmEnded();
        assertEquals(1, listener.learningEndedCount);
        instance.fireAlgorithmEnded();
        assertEquals(2, listener.learningEndedCount);
    }

    /**
     * Test of fireStepStarted method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testFireStepStarted()
    {
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        DummyIterativeLearnerListener listener = 
                new DummyIterativeLearnerListener();
        instance.addIterativeAlgorithmListener(listener);
        
        assertEquals(0, listener.stepStartedCount);
        instance.fireStepStarted();
        assertEquals(1, listener.stepStartedCount);
        instance.fireStepStarted();
        assertEquals(2, listener.stepStartedCount);
    }

    /**
     * Test of fireStepEnded method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testFireStepEnded()
    {
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        DummyIterativeLearnerListener listener = 
                new DummyIterativeLearnerListener();
        instance.addIterativeAlgorithmListener(listener);
        
        assertEquals(0, listener.stepEndedCount);
        instance.fireStepEnded();
        assertEquals(1, listener.stepEndedCount);
        instance.fireStepEnded();
        assertEquals(2, listener.stepEndedCount);
    }

    /**
     * Test of getListeners method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testGetListeners()
    {
        this.testSetListeners();
    }

    /**
     * Test of setListeners method, of class gov.sandia.cognition.learning.AbstractIterativeLearner.
     */
    public void testSetListeners()
    {
        DummyIterativeAlgorithm instance = new DummyIterativeAlgorithm();
        assertNull(instance.getListeners());
        
        LinkedList<IterativeAlgorithmListener> listeners = 
            new LinkedList<IterativeAlgorithmListener>();
        instance.setListeners(listeners);
        assertSame(listeners, instance.getListeners());
        
        listeners = 
            new LinkedList<IterativeAlgorithmListener>();
        instance.setListeners(listeners);
        assertSame(listeners, instance.getListeners());
        
        instance.setListeners(null);
        assertNull(instance.getListeners());
    }
    
    public static class DummyIterativeAlgorithm
        extends AbstractIterativeAlgorithm
    {
        public DummyIterativeAlgorithm()
        {
            super();
        }
    }

}

