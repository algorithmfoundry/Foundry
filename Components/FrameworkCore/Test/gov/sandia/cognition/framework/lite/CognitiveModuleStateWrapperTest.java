/*
 * File:                CognitiveModuleStateWrapperTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import junit.framework.*;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Random;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CognitiveModuleStateWrapper
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CognitiveModuleStateWrapperTest
    extends TestCase
{
    
    /** The random number generator for the tests. */
    protected Random random = new Random();
    
    public CognitiveModuleStateWrapperTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        CognitiveModuleStateWrapper instance = new CognitiveModuleStateWrapper();
        assertNull(instance.getInternalState());
        
        instance = new CognitiveModuleStateWrapper((CloneableSerializable) null);
        assertNull(instance.getInternalState());
        Vector state = 
            VectorFactory.getDefault().createUniformRandom(10, 0.0, 1.0, random);
        instance = new CognitiveModuleStateWrapper(state);
        assertSame(instance.getInternalState(), state);
        
    }
    
    /**
     * Test of clone method, of class gov.sandia.cognition.framework.lite.CognitiveModuleStateWrapper.
     */
    public void testClone()
    {
        CognitiveModuleStateWrapper instance = new CognitiveModuleStateWrapper();
        assertNull(instance.getInternalState());
        
        CognitiveModuleStateWrapper clone = instance.clone();
        assertNotSame(instance, clone);
        assertNull(clone.getInternalState());
        
        Vector state = 
            VectorFactory.getDefault().createUniformRandom(10, 0.0, 1.0, random);
        instance.setInternalState(state);
        assertSame(instance.getInternalState(), state);
        assertNull(clone.getInternalState());
        
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone.getInternalState(), state);
        assertEquals(clone.getInternalState(), state);
    }

    /**
     * Test of getInternalState method, of class gov.sandia.cognition.framework.lite.CognitiveModuleStateWrapper.
     */
    public void testGetInternalState()
    {
        CognitiveModuleStateWrapper instance = new CognitiveModuleStateWrapper();
        assertNull(instance.getInternalState());
        
        Vector state = 
            VectorFactory.getDefault().createUniformRandom(10, 0.0, 1.0, random);
        instance.setInternalState(state);
        assertSame(instance.getInternalState(), state);
    }

    /**
     * Test of setInternalState method, of class gov.sandia.cognition.framework.lite.CognitiveModuleStateWrapper.
     */
    public void testSetInternalState()
    {
        CognitiveModuleStateWrapper instance = new CognitiveModuleStateWrapper();
        assertNull(instance.getInternalState());
        
        Vector state = 
            VectorFactory.getDefault().createUniformRandom(10, 0.0, 1.0, random);
        instance.setInternalState(state);
        assertSame(instance.getInternalState(), state);
        
        instance.setInternalState(null);
        assertNull(instance.getInternalState());
    }
}
