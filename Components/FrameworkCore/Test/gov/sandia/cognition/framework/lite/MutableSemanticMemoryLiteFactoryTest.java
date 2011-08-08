/*
 * File:                MutableSemanticMemoryLiteFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import gov.sandia.cognition.framework.DummyModuleFactory;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MutableSemanticMemoryLiteFactory
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class MutableSemanticMemoryLiteFactoryTest
    extends TestCase
{
    public MutableSemanticMemoryLiteFactoryTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MutableSemanticMemoryLiteFactoryTest.class);
        
        return suite;
    }
    
    public MutableSemanticMemoryLiteFactory createTestFactory()
    {
        DefaultSemanticNetwork network = new DefaultSemanticNetwork();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        network.addNode(a);
        network.addNode(b);
        network.setAssociation(a, b, 1.0);
        
        return new MutableSemanticMemoryLiteFactory(
            new SimplePatternRecognizer(network));
    }

    /**
     * Test of createModule method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLiteFactory.
     */
    public void testCreateModule()
    {
        MutableSemanticMemoryLiteFactory instance = this.createTestFactory();
        
        CognitiveModelLite model = new CognitiveModelLite(
            new DummyModuleFactory());
        
        MutableSemanticMemoryLite module1 = instance.createModule(model);
        MutableSemanticMemoryLite module2 = instance.createModule(model);
        assertNotNull(module1);
        assertNotNull(module2);
        assertNotSame(module1, module2);
        assertNotSame(module1.getRecognizer(), module2.getRecognizer());
        
        DefaultSemanticLabel d = new DefaultSemanticLabel("d");
        
        assertFalse(module1.isNode(d));
        assertFalse(module2.isNode(d));
        instance.getRecognizer().addNode(d);
        
        assertFalse(module1.isNode(d));
        assertFalse(module2.isNode(d));
        
        module1.addNode(d);
        assertTrue(module1.isNode(d));
        assertFalse(module2.isNode(d));
    }

    /**
     * Test of getSettings method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLiteFactory.
     */
    public void testGetSettings()
    {
        MutablePatternRecognizerLite recognizer = 
            new SimplePatternRecognizer(new DefaultSemanticNetwork());
        MutableSemanticMemoryLiteFactory instance = 
            new MutableSemanticMemoryLiteFactory(recognizer);
        
        assertEquals(recognizer, instance.getSettings());
    }

    /**
     * Test of getRecognizer method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLiteFactory.
     */
    public void testGetRecognizer()
    {
        MutablePatternRecognizerLite recognizer = 
            new SimplePatternRecognizer(new DefaultSemanticNetwork());
        MutableSemanticMemoryLiteFactory instance = 
            new MutableSemanticMemoryLiteFactory(recognizer);
        
        assertEquals(recognizer, instance.getRecognizer());
    }


    /**
     * Test of setRecognizer method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLiteFactory.
     */
    public void testSetRecognizer()
    {
        System.out.println("setRecognizer");
        
        MutablePatternRecognizerLite recognizer = 
            new SimplePatternRecognizer(new DefaultSemanticNetwork());
        MutableSemanticMemoryLiteFactory instance = 
            new MutableSemanticMemoryLiteFactory(recognizer);
        assertNotNull( instance.getRecognizer() );
        recognizer = (MutablePatternRecognizerLite) recognizer.clone();
        assertTrue( recognizer != instance.getRecognizer() );
        instance.setRecognizer(recognizer);
        assertTrue( recognizer == instance.getRecognizer() );
    }
}
