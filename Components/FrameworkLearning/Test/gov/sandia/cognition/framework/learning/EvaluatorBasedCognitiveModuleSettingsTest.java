/*
 * File:                EvaluatorBasedCognitiveModuleSettingsTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.learning.converter.CogxelVectorConverter;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
import gov.sandia.cognition.math.matrix.Vector;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 * @author Justin Basilico
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleSettingsTest
    extends TestCase
{

    public EvaluatorBasedCognitiveModuleSettingsTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");

        LinearVectorFunction evaluator = new LinearVectorFunction(Math.random());
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();

        assertNull(instance.getEvaluator());
        assertNull(instance.getInputConverter());
        assertNull(instance.getOutputConverter());

        instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);

        assertSame(instance.getEvaluator(), evaluator);
        assertSame(instance.getInputConverter(), inputConverter);
        assertSame(instance.getOutputConverter(), outputConverter);

        instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(instance);

        assertSame(instance.getEvaluator(), evaluator);
        assertNotSame(instance.getInputConverter(), inputConverter);
        assertEquals(inputConverter, instance.getInputConverter());
        assertNotSame(instance.getOutputConverter(), outputConverter);
        assertEquals(outputConverter, instance.getOutputConverter());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testClone()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");

        LinearVectorFunction evaluator = new LinearVectorFunction(Math.random());
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> clone =
            instance.clone();
        assertNotSame(instance, clone);

        assertSame(clone.getEvaluator(), evaluator);
        assertNotSame(clone.getInputConverter(), inputConverter);
        assertEquals(inputConverter, clone.getInputConverter());
        assertNotSame(clone.getOutputConverter(), outputConverter);
        assertEquals(outputConverter, clone.getOutputConverter());
    }

    /**
     * Test of getEvaluator method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testGetEvaluator()
    {
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();
        assertNull(instance.getEvaluator());

        LinearVectorFunction evaluator = new LinearVectorFunction(Math.random());
        instance.setEvaluator(evaluator);

        assertSame(evaluator, instance.getEvaluator());
    }

    /**
     * Test of setEvaluator method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testSetEvaluator()
    {
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();
        assertNull(instance.getEvaluator());

        LinearVectorFunction evaluator = new LinearVectorFunction(Math.random());
        instance.setEvaluator(evaluator);

        assertSame(evaluator, instance.getEvaluator());

        instance.setEvaluator(null);
        assertNull(instance.getEvaluator());
    }

    /**
     * Test of getInputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testGetInputConverter()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");

        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();

        assertNull(instance.getInputConverter());

        instance.setInputConverter(inputConverter);

        assertSame(instance.getInputConverter(), inputConverter);
    }

    /**
     * Test of setInputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testSetInputConverter()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");

        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();

        assertNull(instance.getInputConverter());

        instance.setInputConverter(inputConverter);

        assertSame(instance.getInputConverter(), inputConverter);

        instance.setInputConverter(null);
        assertNull(instance.getInputConverter());
    }

    /**
     * Test of getOutputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testGetOutputConverter()
    {
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();

        assertNull(instance.getOutputConverter());

        instance.setOutputConverter(outputConverter);

        assertSame(instance.getOutputConverter(), outputConverter);
    }

    /**
     * Test of setOutputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleSettings.
     */
    public void testSetOutputConverter()
    {
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>();

        assertNull(instance.getOutputConverter());

        instance.setOutputConverter(outputConverter);

        assertSame(instance.getOutputConverter(), outputConverter);

        instance.setOutputConverter(null);
        assertNull(instance.getOutputConverter());
    }

}
