/*
 * File:                EvaluatorBasedCognitiveModuleTest.java
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
import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelFactory;
import gov.sandia.cognition.framework.lite.ArrayBasedCognitiveModelInput;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
import gov.sandia.cognition.math.matrix.Vector;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleTest
    extends TestCase
{

    public EvaluatorBasedCognitiveModuleTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of initializeState method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testInitializeState()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");
        DefaultSemanticLabel out2 = new DefaultSemanticLabel("out2");

        LinearVectorFunction evaluator =
            new LinearVectorFunction(2.0);
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1, out2});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);
        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> moduleFactory =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>(
            settings, "Module Name");

        CognitiveModelLiteFactory modelFactory =
            new CognitiveModelLiteFactory();
        modelFactory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        modelFactory.addModuleFactory(moduleFactory);


        CognitiveModel model = modelFactory.createModel();
        EvaluatorBasedCognitiveModule module =
            (EvaluatorBasedCognitiveModule) model.getModules().get(1);
        assertNull(module.initializeState(model.getCurrentState()));
    }

    /**
     * Test of update method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testUpdate()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");
        DefaultSemanticLabel out2 = new DefaultSemanticLabel("out2");

        LinearVectorFunction evaluator =
            new LinearVectorFunction(2.0);
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1, out2});

        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);
        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> moduleFactory =
            new EvaluatorBasedCognitiveModuleFactory<Vector, Vector>(
            settings, "Module Name");

        CognitiveModelLiteFactory modelFactory =
            new CognitiveModelLiteFactory();
        modelFactory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        modelFactory.addModuleFactory(moduleFactory);

        CognitiveModel model = modelFactory.createModel();
        SemanticIdentifierMap map = model.getSemanticIdentifierMap();

        SemanticIdentifier in1ID = map.addLabel(in1);
        SemanticIdentifier in2ID = map.addLabel(in2);
        SemanticIdentifier out1ID = map.addLabel(out1);
        SemanticIdentifier out2ID = map.addLabel(out2);

        SemanticIdentifier[] inIDs = new SemanticIdentifier[]{in1ID, in2ID};
        double[] inData = new double[]{1.0, -2.0};
        ArrayBasedCognitiveModelInput input = new ArrayBasedCognitiveModelInput(
            inIDs, inData, false);
        model.update(input);

        CogxelState cogxels = model.getCurrentState().getCogxels();
        assertEquals(2.0, cogxels.getCogxelActivation(out1ID));
        assertEquals(-4.0, cogxels.getCogxelActivation(out2ID));

        inData[0] = -4.0;
        inData[1] = 7.0;

        model.update(input);
        cogxels = model.getCurrentState().getCogxels();
        assertEquals(-8.0, cogxels.getCogxelActivation(out1ID));
        assertEquals(14.0, cogxels.getCogxelActivation(out2ID));
    }

    /**
     * Test of getName method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testGetName()
    {
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            new LinearVectorFunction(2.0),
            new CogxelVectorConverter(), new CogxelVectorConverter());
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        String name = "Module Name";
        EvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, name);

        assertEquals(name, instance.getName());
    }

    /**
     * Test of getSettings method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testGetSettings()
    {
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            new LinearVectorFunction(2.0),
            new CogxelVectorConverter(), new CogxelVectorConverter());
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        EvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, "Module Name");

        assertNotNull(instance.getSettings());
        assertNotSame(instance.getSettings(), settings);
    }

    /**
     * Test of getEvaluator method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testGetEvaluator()
    {
        LinearVectorFunction evaluator = new LinearVectorFunction(2.0);
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator,
            new CogxelVectorConverter(), new CogxelVectorConverter());
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        EvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, "Module Name");

        assertSame(instance.getEvaluator(), evaluator);
    }

    /**
     * Test of getInputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testGetInputConverter()
    {
        LinearVectorFunction evaluator = new LinearVectorFunction(2.0);
        CogxelVectorConverter inputConverter = new CogxelVectorConverter();
        inputConverter.addLabel(new DefaultSemanticLabel("a"));
        CogxelVectorConverter outputConverter = new CogxelVectorConverter();
        outputConverter.addLabel(new DefaultSemanticLabel("b"));
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        EvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, "Module Name");

        assertNotSame(instance.getInputConverter(), inputConverter);
        assertEquals(inputConverter, instance.getInputConverter());
    }

    /**
     * Test of getOutputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModule.
     */
    public void testGetOutputConverter()
    {
        LinearVectorFunction evaluator = new LinearVectorFunction(2.0);
        CogxelVectorConverter inputConverter = new CogxelVectorConverter();
        inputConverter.addLabel(new DefaultSemanticLabel("a"));
        CogxelVectorConverter outputConverter = new CogxelVectorConverter();
        outputConverter.addLabel(new DefaultSemanticLabel("b"));
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator, inputConverter, outputConverter);
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        EvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new EvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, "Module Name");

        assertNotSame(instance.getOutputConverter(), outputConverter);
        assertEquals(outputConverter, instance.getOutputConverter());
    }

}
