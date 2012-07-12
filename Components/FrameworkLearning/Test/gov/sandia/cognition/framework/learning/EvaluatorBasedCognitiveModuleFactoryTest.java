/*
 * File:                EvaluatorBasedCognitiveModuleFactoryTest.java
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
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.signals.LinearDynamicalSystem;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleFactoryTest
    extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random(1);
    
    public EvaluatorBasedCognitiveModuleFactoryTest(
        String testName)
    {
        super(testName);
    }

    public EvaluatorBasedCognitiveModuleSettings<Vector, Vector> createSettings()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");

        LinearVectorFunction evaluator =
            new LinearVectorFunction(Math.random());
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);

        return settings;
    }

    public void testConstructors()
    {
        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>();

        assertNotNull(instance.getSettings());

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            this.createSettings();

        instance = new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>(
            settings, "Module Name");

        assertSame(instance.getSettings(), settings);

        instance = new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>(
            instance);

        assertNotNull(instance.getSettings());
        assertNotSame(instance.getSettings(), settings);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactory.
     */
    public void testClone()
    {
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            this.createSettings();

        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>(
            settings, "Module Name");

        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> clone =
            instance.clone();

        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotNull(clone.getSettings());
        assertNotSame(clone.getSettings(), instance.getSettings());
    }

    /**
     * Test of createModule method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactory.
     */
    public void testCreateModule()
    {
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            this.createSettings();


        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>(
            settings, "Module Name");
        CognitiveModelLiteFactory modelFactory = new CognitiveModelLiteFactory();
        modelFactory.addModuleFactory(instance);


        LinearVectorFunction linearEvaluator =
            new LinearVectorFunction(Math.random());

        CognitiveModel linearModel = modelFactory.createModel();

        EvaluatorBasedCognitiveModule<?, ?> linearModule =
            (EvaluatorBasedCognitiveModule) linearModel.getModules().get(0);
        assertFalse(linearModule instanceof StatefulEvaluatorBasedCognitiveModule);
        assertSame(linearModule.getEvaluator(), settings.getEvaluator());
        assertEquals(settings.getInputConverter(), linearModule.getInputConverter());
        assertEquals(settings.getOutputConverter(), linearModule.getOutputConverter());


        // Make sure that a StatefulEvaluator turns into the module that
        // holds state.
        LinearDynamicalSystem statefulEvaluator = new LinearDynamicalSystem(
            MatrixFactory.getDefault().createUniformRandom(2, 2, 0.0, 1.0, random),
            MatrixFactory.getDefault().createUniformRandom(2, 2, 0.0, 1.0, random));

        settings.setEvaluator(statefulEvaluator);

        CognitiveModel statefulModel = modelFactory.createModel();
        StatefulEvaluatorBasedCognitiveModule<?,?> statefulModule =
            (StatefulEvaluatorBasedCognitiveModule) statefulModel.getModules().get(0);

        assertTrue(statefulModule instanceof StatefulEvaluatorBasedCognitiveModule);
        assertSame(statefulModule.getEvaluator(), settings.getEvaluator());
        assertEquals(settings.getInputConverter(), statefulModule.getInputConverter());
        assertEquals(settings.getOutputConverter(), statefulModule.getOutputConverter());
    }

    /**
     * Test of getSettings method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactory.
     */
    public void testGetSettings()
    {
        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>();
        assertNotNull(instance.getSettings());

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            this.createSettings();

        instance.setSettings(settings);
        assertSame(instance.getSettings(), settings);
    }

    /**
     * Test of setSettings method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactory.
     */
    public void testSetSettings()
    {
        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>();
        assertNotNull(instance.getSettings());

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            this.createSettings();

        instance.setSettings(settings);
        assertSame(instance.getSettings(), settings);

        instance.setSettings(null);
        assertNull(instance.getSettings());
    }

}
