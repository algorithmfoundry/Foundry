/*
 * File:                AbstractCognitiveModelTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.framework;

import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.framework.lite.SharedSemanticMemoryLiteFactory;
import gov.sandia.cognition.framework.lite.SimplePatternRecognizer;
import junit.framework.*;
import java.util.LinkedList;

/**
 * Unit tests for AbstractCognitiveModelTest
 *
 * @author Kevin R. Dixon
 */
public class AbstractCognitiveModelTest extends TestCase
{
    
    public AbstractCognitiveModelTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractCognitiveModelTest.class);
        
        return suite;
    }
    
    public AbstractCognitiveModel createTestInstance()
    {

        SimplePatternRecognizer recognizer =
            new SimplePatternRecognizer();
        recognizer.addNode( new DefaultSemanticLabel("i1") );
        recognizer.addNode( new DefaultSemanticLabel("i2") );
        recognizer.addNode( new DefaultSemanticLabel("i3") );

        recognizer.addNode( new DefaultSemanticLabel("o1") );
        recognizer.addNode( new DefaultSemanticLabel("o2") );

        recognizer.setAssociation( new DefaultSemanticLabel("i1"), new DefaultSemanticLabel("o1"), Math.random() );
        recognizer.setAssociation( new DefaultSemanticLabel("i3"), new DefaultSemanticLabel("o1"), Math.random() );
        recognizer.setAssociation( new DefaultSemanticLabel("i3"), new DefaultSemanticLabel("o2"), Math.random() );

        recognizer.setAssociation( new DefaultSemanticLabel("o1"), new DefaultSemanticLabel("o1"), Math.random() );
        recognizer.setAssociation( new DefaultSemanticLabel("o1"), new DefaultSemanticLabel("o2"), Math.random() );
        
        CognitiveModelLiteFactory factory = new CognitiveModelLiteFactory();
        factory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        factory.addModuleFactory(
            new SharedSemanticMemoryLiteFactory(recognizer));
        
        return factory.createModel();
    }
    

    /**
     * Test of addCognitiveModelListener method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModel.
     */
    public void testAddCognitiveModelListener()
    {
        System.out.println("addCognitiveModelListener");
        
        CognitiveModelListener listener = null;
        AbstractCognitiveModel instance = this.createTestInstance();

        // For now, just make sure we can handle null pointers
        instance.addCognitiveModelListener(listener);

    }

    /**
     * Test of removeCognitiveModelListener method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModel.
     */
    public void testRemoveCognitiveModelListener()
    {
        System.out.println("removeCognitiveModelListener");
        
        AbstractCognitiveModel instance = this.createTestInstance();
        
        // For now, just make sure we can handle null pointers
        CognitiveModelListener listener = null;
        instance.removeCognitiveModelListener(listener);
    }

    /**
     * Test of fireModelStateChangedEvent method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModel.
     */
    public void testFireModelStateChangedEvent()
    {
        System.out.println("fireModelStateChangedEvent");
        
        AbstractCognitiveModel instance = this.createTestInstance();
        
        instance.fireModelStateChangedEvent();

    }

    /**
     * Test of getModelListeners method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModel.
     */
    public void testGetModelListeners()
    {
        System.out.println("getModelListeners");
        
        AbstractCognitiveModel instance = this.createTestInstance();
        
        LinkedList<CognitiveModelListener> expResult =
            new LinkedList<CognitiveModelListener>();
  
        assertNull( instance.getModelListeners() );
        assertNotSame( instance.getModelListeners(), expResult );
                
    }
    
}
