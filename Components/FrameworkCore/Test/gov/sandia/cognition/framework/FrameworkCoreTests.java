/*
 * File:                FrameworkCoreTests.java
 * Authors:             Benjamin Currier
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite Core
 *
 * Copyright September 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.framework;

import gov.sandia.cognition.framework.io.CSVDefaultCognitiveModelLiteHandlerTest;
import gov.sandia.cognition.framework.io.SerializedModelHandlerTest;
import gov.sandia.cognition.framework.lite.AbstractSemanticMemoryLiteTest;
import gov.sandia.cognition.framework.lite.ArrayBasedCognitiveModelInputTest;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactoryTest;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleTest;
import gov.sandia.cognition.framework.lite.BooleanActivatableCogxelFactoryTest;
import gov.sandia.cognition.framework.lite.BooleanActivatableCogxelTest;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactoryTest;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteStateTest;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteTest;
import gov.sandia.cognition.framework.lite.CognitiveModuleStateWrapperTest;
import gov.sandia.cognition.framework.lite.CogxelStateLiteTest;
import gov.sandia.cognition.framework.lite.MutableSemanticMemoryLiteFactoryTest;
import gov.sandia.cognition.framework.lite.MutableSemanticMemoryLiteTest;
import gov.sandia.cognition.framework.lite.SharedSemanticMemoryLiteFactoryTest;
import gov.sandia.cognition.framework.lite.SharedSemanticMemoryLiteSettingsTest;
import gov.sandia.cognition.framework.lite.SharedSemanticMemoryLiteTest;
import gov.sandia.cognition.framework.lite.SimplePatternRecognizerStateTest;
import gov.sandia.cognition.framework.lite.SimplePatternRecognizerTest;
import gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInputTest;
import gov.sandia.cognition.framework.lite.VectorBasedPerceptionModuleFactoryTest;
import gov.sandia.cognition.framework.lite.VectorBasedPerceptionModuleTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test Suite for all tests in Framework Core module.
 */
public class FrameworkCoreTests
{

  public static Test suite()
  {
    final TestSuite suite = new TestSuite("Tests for Framework Core...");

    // gov.sandia.cognition.framework
    suite.addTest(AbstractCognitiveModelTest.suite());
    suite.addTestSuite(DefaultSemanticLabelTest.class);
    suite.addTestSuite(DefaultSemanticIdentifierTest.class);
    suite.addTestSuite(DefaultSemanticNetworkTest.class);
    suite.addTestSuite(DefaultCogxelFactoryTest.class);
    suite.addTestSuite(DefaultSemanticIdentifierMapTest.class);
    suite.addTestSuite(DefaultCogxelTest.class);
    suite.addTest(CognitiveModelStateChangeEventTest.suite());
    suite.addTest(AbstractCognitiveModelFactoryTest.suite());

    // gov.sandia.cognition.framework.io
    suite.addTestSuite(CSVDefaultCognitiveModelLiteHandlerTest.class);
    suite.addTestSuite(SerializedModelHandlerTest.class);

    // gov.sandia.cognition.framework.lite
    suite.addTest(SharedSemanticMemoryLiteSettingsTest.suite());
    suite.addTest(SharedSemanticMemoryLiteFactoryTest.suite());
    suite.addTest(BooleanActivatableCogxelFactoryTest.suite());
    suite.addTestSuite(CognitiveModuleStateWrapperTest.class);
    suite.addTest(CognitiveModelLiteTest.suite());
    suite.addTest(CognitiveModelLiteStateTest.suite());
    suite.addTestSuite(CognitiveModelLiteFactoryTest.class);
    suite.addTest(SimplePatternRecognizerStateTest.suite());
    suite.addTestSuite(VectorBasedPerceptionModuleTest.class);
    suite.addTest(ArrayBasedCognitiveModelInputTest.suite());
    suite.addTest(ArrayBasedPerceptionModuleTest.suite());
    suite.addTest(BooleanActivatableCogxelTest.suite());
    suite.addTest(ArrayBasedPerceptionModuleFactoryTest.suite());
    suite.addTest(SimplePatternRecognizerTest.suite());
    suite.addTest(CogxelStateLiteTest.suite());
    suite.addTest(MutableSemanticMemoryLiteTest.suite());
    suite.addTestSuite(VectorBasedPerceptionModuleFactoryTest.class);
    suite.addTest(AbstractSemanticMemoryLiteTest.suite());
    suite.addTest(SharedSemanticMemoryLiteTest.suite());
    suite.addTestSuite(VectorBasedCognitiveModelInputTest.class);
    suite.addTest(MutableSemanticMemoryLiteFactoryTest.suite());

    return suite;
  }

}
