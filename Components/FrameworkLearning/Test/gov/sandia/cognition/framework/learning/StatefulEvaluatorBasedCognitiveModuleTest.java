/*
 * File:                StatefulEvaluatorBasedCognitiveModuleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 27, 2007, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.ArrayBasedCognitiveModelInput;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.framework.lite.CognitiveModuleStateWrapper;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.math.signals.LinearDynamicalSystem;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    StatefulEvaluatorBasedCognitiveModule
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class StatefulEvaluatorBasedCognitiveModuleTest
    extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random();
    
    public StatefulEvaluatorBasedCognitiveModuleTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of initializeState method, of class gov.sandia.cognition.framework.learning.StatefulEvaluatorBasedCognitiveModule.
     */
    @SuppressWarnings("unchecked")
    public void testInitializeState()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel in3 = new DefaultSemanticLabel("in3");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");
        DefaultSemanticLabel out2 = new DefaultSemanticLabel("out2");
        DefaultSemanticLabel out3 = new DefaultSemanticLabel("out3");

        LinearDynamicalSystem evaluator = new LinearDynamicalSystem(
            MatrixFactory.getDefault().copyColumnVectors(
                new Vector3(2.0, 0.0, 0.0),
                new Vector3(0.0, 2.0, 0.0),
                new Vector3(0.0, 0.0, 2.0)),
            MatrixFactory.getDefault().copyColumnVectors(
            new Vector3(1.0, 0.0, 0.0),
            new Vector3(0.0, 1.0, 0.0),
            new Vector3(0.0, 0.0, 1.0)));
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2, in3});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1, out2, out3});

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
        StatefulEvaluatorBasedCognitiveModule<Vector, Vector> module =
            (StatefulEvaluatorBasedCognitiveModule<Vector, Vector>) model.getModules().get(1);

        CognitiveModuleState state1 = module.initializeState(model.getCurrentState());
        CognitiveModuleState state2 = module.initializeState(model.getCurrentState());

        assertNotNull(state1);
        assertNotNull(state2);
        assertNotSame(state1, state2);
        assertTrue(state1 instanceof CognitiveModuleStateWrapper);
        Object internalState = ((CognitiveModuleStateWrapper) state1).getInternalState();
        assertNotNull(internalState);
        assertEquals(evaluator.createDefaultState(), internalState);

    }

    /**
     * Test of update method, of class gov.sandia.cognition.framework.learning.StatefulEvaluatorBasedCognitiveModule.
     */
    public void testUpdate()
    {
        DefaultSemanticLabel in1 = new DefaultSemanticLabel("in1");
        DefaultSemanticLabel in2 = new DefaultSemanticLabel("in2");
        DefaultSemanticLabel in3 = new DefaultSemanticLabel("in3");
        DefaultSemanticLabel out1 = new DefaultSemanticLabel("out1");
        DefaultSemanticLabel out2 = new DefaultSemanticLabel("out2");
        DefaultSemanticLabel out3 = new DefaultSemanticLabel("out3");

        LinearDynamicalSystem evaluator = new LinearDynamicalSystem(
            MatrixFactory.getDefault().copyColumnVectors(
                new Vector3(2.0, 0.0, 0.0),
                new Vector3(0.0, 2.0, 0.0),
                new Vector3(0.0, 0.0, 2.0)),
            MatrixFactory.getDefault().copyColumnVectors(
                new Vector3(1.0, 0.0, 0.0),
                new Vector3(0.0, 1.0, 0.0),
                new Vector3(0.0, 0.0, 1.0)));
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{in1, in2, in3});
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(
            new SemanticLabel[]{out1, out2, out3});

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
        SemanticIdentifier in3ID = map.addLabel(in3);
        SemanticIdentifier out1ID = map.addLabel(out1);
        SemanticIdentifier out2ID = map.addLabel(out2);
        SemanticIdentifier out3ID = map.addLabel(out3);

        SemanticIdentifier[] inIDs =
            new SemanticIdentifier[]{in1ID, in2ID, in3ID};
        double[] inData = new double[]{1.0, -2.0, 0.0};
        ArrayBasedCognitiveModelInput input = new ArrayBasedCognitiveModelInput(
            inIDs, inData, false);
        model.update(input);

        CogxelState cogxels = model.getCurrentState().getCogxels();
        assertEquals(1.0, cogxels.getCogxelActivation(out1ID));
        assertEquals(-2.0, cogxels.getCogxelActivation(out2ID));
        assertEquals(0.0, cogxels.getCogxelActivation(out3ID));


        model.update(input);
        cogxels = model.getCurrentState().getCogxels();
        assertEquals(3.0, cogxels.getCogxelActivation(out1ID));
        assertEquals(-6.0, cogxels.getCogxelActivation(out2ID));
        assertEquals(0.0, cogxels.getCogxelActivation(out3ID));

        model.resetCognitiveState();

        inData[0] = -4.0;
        inData[1] = 7.0;
        inData[2] = 0.5;

        model.update(input);


        cogxels = model.getCurrentState().getCogxels();
        assertEquals(-4.0, cogxels.getCogxelActivation(out1ID));
        assertEquals(7.0, cogxels.getCogxelActivation(out2ID));
        assertEquals(0.5, cogxels.getCogxelActivation(out3ID));

        model.update(input);
        cogxels = model.getCurrentState().getCogxels();
        assertEquals(-12.0, cogxels.getCogxelActivation(out1ID));
        assertEquals(21.0, cogxels.getCogxelActivation(out2ID));
        assertEquals(1.5, cogxels.getCogxelActivation(out3ID));
    }

    /**
     * Test of getName method, of class gov.sandia.cognition.framework.learning.StatefulEvaluatorBasedCognitiveModule.
     */
    public void testGetName()
    {
        LinearDynamicalSystem evaluator = new LinearDynamicalSystem(
            MatrixFactory.getDefault().createUniformRandom(2, 2, 0.0, 1.0, random),
            MatrixFactory.getDefault().createUniformRandom(2, 2, 0.0, 1.0, random));
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator,
            new CogxelVectorConverter(), new CogxelVectorConverter());
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        String name = "Module Name";
        StatefulEvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new StatefulEvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, name);

        assertEquals(name, instance.getName());
    }

    /**
     * Test of getStatefulEvaluator method, of class gov.sandia.cognition.framework.learning.StatefulEvaluatorBasedCognitiveModule.
     */
    public void testGetStatefulEvaluator()
    {
        LinearDynamicalSystem evaluator = new LinearDynamicalSystem(
            MatrixFactory.getDefault().createUniformRandom(2, 2, 0.0, 1.0, random),
            MatrixFactory.getDefault().createUniformRandom(2, 2, 0.0, 1.0, random));
        EvaluatorBasedCognitiveModuleSettings<Vector, Vector> settings =
            new EvaluatorBasedCognitiveModuleSettings<Vector, Vector>(
            evaluator,
            new CogxelVectorConverter(), new CogxelVectorConverter());
        CognitiveModelFactory modelFactory = new CognitiveModelLiteFactory();
        StatefulEvaluatorBasedCognitiveModule<Vector, Vector> instance =
            new StatefulEvaluatorBasedCognitiveModule<Vector, Vector>(
            modelFactory.createModel(), settings, "Module Name");

        assertSame(evaluator, instance.getStatefulEvaluator());
    }

}
