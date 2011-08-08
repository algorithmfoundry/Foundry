/*
 * File:                SharedSemanticMemoryLiteSettingsTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 27, 2006, Sandia Corporation.  Under the terms of Contract
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
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     SharedSemanticMemoryLiteSettings
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SharedSemanticMemoryLiteSettingsTest
    extends TestCase
{
    public SharedSemanticMemoryLiteSettingsTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SharedSemanticMemoryLiteSettingsTest.class);
        
        return suite;
    }

    /**
     * Test of getRecognizer method, of class gov.sandia.isrc.cognition.framework.lite.SharedSemanticMemoryLiteSettings.
     */
    public void testGetRecognizer()
    {
        DefaultSemanticNetwork network = new DefaultSemanticNetwork();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        network.addNode(a);
        network.addNode(b);
        network.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(network);
        SharedSemanticMemoryLiteSettings instance = 
            new SharedSemanticMemoryLiteSettings(recognizer);
        
        PatternRecognizerLite result = instance.getRecognizer();
        assertNotNull(result);
        assertNotSame(recognizer, result);
    }

}
