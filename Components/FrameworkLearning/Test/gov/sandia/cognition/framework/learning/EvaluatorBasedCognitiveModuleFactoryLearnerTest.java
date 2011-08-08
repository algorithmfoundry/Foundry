/*
 * File:                EvaluatorBasedCognitiveModuleFactoryLearnerTest.java
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
import gov.sandia.cognition.framework.learning.converter.CogxelInputOutputPairConverter;
import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.ArrayBasedCognitiveModelInput;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleFactoryLearnerTest
    extends TestCase
{

    public EvaluatorBasedCognitiveModuleFactoryLearnerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testClone()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        MatrixMultiplyVectorFunction.ClosedFormSolver learner =
            new MatrixMultiplyVectorFunction.ClosedFormSolver();
        CogxelVectorConverter inputConverter = new CogxelVectorConverter(a);
        CogxelVectorConverter outputConverter = new CogxelVectorConverter(b);
        CogxelInputOutputPairConverter<Vector, Vector> learningDataConverter =
            new CogxelInputOutputPairConverter<Vector, Vector>(
            inputConverter, outputConverter);

        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>> instance = new EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>>();

        instance.setLearner(learner);
        instance.setInputConverter(inputConverter);
        instance.setOutputConverter(outputConverter);
        instance.setLearningDataConverter(learningDataConverter);

        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>> clone = instance.clone();

        assertNotSame(learner, clone.getLearner());
        assertEquals(inputConverter, clone.getInputConverter());
        assertNotSame(instance.getInputConverter(), clone.getInputConverter());
        assertEquals(outputConverter, clone.getOutputConverter());
        assertNotSame(instance.getOutputConverter(), clone.getOutputConverter());
        assertEquals(learningDataConverter, clone.getLearningDataConverter());
        assertNotSame(instance.getLearningDataConverter(), clone.getLearningDataConverter());
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testLearn()
    {
        double[][] positiveExamples = new double[][]{
            {0.0, 0.0, 1.0, 1.0, 1.0},
            {1.0, 0.0, 1.0, 1.0, 0.0},
            {1.0, 1.0, 0.0, 0.0, 1.0},
            {1.0, 1.0, 0.0, 0.0, 0.0},
            {0.0, 1.0, 0.0, 0.0, 1.0},
            {0.0, 0.0, 0.0, 1.0, 0.0}};

        double[][] negativeExamples = new double[][]{
            {1.0, 0.0, 0.0, 1.0, 1.0},
            {1.0, 1.0, 0.0, 0.0, 1.0},
            {1.0, 1.0, 1.0, 1.0, 0.0},
            {1.0, 1.0, 0.0, 1.0, 0.0},
            {1.0, 1.0, 0.0, 1.0, 1.0},
            {1.0, 0.0, 1.0, 1.0, 0.0},
            {1.0, 0.0, 1.0, 0.0, 0.0}};

        SemanticLabel[] inputLabels = new SemanticLabel[]{
            new DefaultSemanticLabel("in0"),
            new DefaultSemanticLabel("in1"),
            new DefaultSemanticLabel("in2"),
            new DefaultSemanticLabel("in3"),
            new DefaultSemanticLabel("in4")
        ,
                  };
        
        SemanticLabel[] outputLabels = new SemanticLabel[]
        {
            new DefaultSemanticLabel("out")};


        SemanticLabel[] trainingLabels = new SemanticLabel[]{
            new DefaultSemanticLabel("label")
        };

        Collection<InputOutputPair<Vector, Vector>> training =
            new LinkedList<InputOutputPair<Vector, Vector>>();

        Vector positiveLabel = VectorFactory.getDefault().createVector(1);
        positiveLabel.setElement(0, 1.0);

        Vector negativeLabel = VectorFactory.getDefault().createVector(1);
        negativeLabel.setElement(0, -1.0);

        for (double[] positiveExample : positiveExamples)
        {
            training.add(new DefaultInputOutputPair<Vector, Vector>(
                VectorFactory.getDefault().copyArray(positiveExample),
                positiveLabel));
        }

        for (double[] negativeExample : negativeExamples)
        {
            training.add(new DefaultInputOutputPair<Vector, Vector>(
                VectorFactory.getDefault().copyArray(negativeExample),
                negativeLabel));
        }

        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>> instance = new EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>>();

        MatrixMultiplyVectorFunction.ClosedFormSolver learner =
            new MatrixMultiplyVectorFunction.ClosedFormSolver();
        instance.setLearner(learner);

        CogxelVectorConverter inputConverter = new CogxelVectorConverter(inputLabels);
        instance.setInputConverter(inputConverter);

        CogxelVectorConverter outputConverter = new CogxelVectorConverter(outputLabels);
        instance.setOutputConverter(outputConverter);

        CogxelVectorConverter trainingLabelConverter = new CogxelVectorConverter(trainingLabels);

        CogxelInputOutputPairConverter<Vector, Vector> learningDataConverter =
            new CogxelInputOutputPairConverter<Vector, Vector>(
            inputConverter,
            trainingLabelConverter);
        instance.setLearningDataConverter(learningDataConverter);


        CognitiveModelLiteFactory modelFactory =
            new CognitiveModelLiteFactory();
        modelFactory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());

        CognitiveModel model = modelFactory.createModel();
        SemanticIdentifierMap map = model.getSemanticIdentifierMap();

        SemanticIdentifier[] identifiers = new SemanticIdentifier[inputLabels.length + outputLabels.length];

        int index = 0;
        for (SemanticLabel label : inputLabels)
        {
            identifiers[index] = map.addLabel(label);
            index++;
        }
        for (SemanticLabel label : trainingLabels)
        {
            identifiers[index] = map.addLabel(label);
            index++;
        }

        ArrayList<CognitiveModelInput> inputs = new ArrayList<CognitiveModelInput>();
        for (InputOutputPair<Vector, Vector> pair : training)
        {
            double[] values = new double[identifiers.length];
            index = 0;
            for (VectorEntry entry : pair.getInput())
            {
                values[index] = entry.getValue();
                index++;
            }
            for (VectorEntry entry : pair.getOutput())
            {
                values[index] = entry.getValue();
                index++;
            }

            inputs.add(new ArrayBasedCognitiveModelInput(identifiers, values, false));
        }

        ArrayList<ArrayList<CognitiveModelInput>> inputSets =
            new ArrayList<ArrayList<CognitiveModelInput>>();
        inputSets.add(inputs);

        EvaluatorBasedCognitiveModuleFactory<Vector, Vector> moduleFactory =
            instance.learn(model, inputSets);

        assertNotNull(moduleFactory);
        assertNotNull(moduleFactory.getSettings());
        assertNotNull(moduleFactory.getSettings().getEvaluator());

        modelFactory.addModuleFactory(moduleFactory);
        model = modelFactory.createModel();

        map = model.getSemanticIdentifierMap();
        trainingLabelConverter.setSemanticIdentifierMap(map);
        outputConverter.setSemanticIdentifierMap(map);

        // Here we exercise the model to make sure it returns nonzero values.
        for (CognitiveModelInput input : inputs)
        {
            model.update(input);
            CogxelState cogxels = model.getCurrentState().getCogxels();

            Vector actual = trainingLabelConverter.fromCogxels(cogxels);
            Vector predicted = outputConverter.fromCogxels(cogxels);
            assertTrue(predicted.norm2() > 0.0);
        }
    }

    /**
     * Test of getLearner method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testGetLearner()
    {
        this.testSetLearner();
    }

    /**
     * Test of setLearner method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testSetLearner()
    {
        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>> instance = new EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, InputOutputPair<Vector, Vector>>();
        assertNull(instance.getLearner());

        MatrixMultiplyVectorFunction.ClosedFormSolver learner =
            new MatrixMultiplyVectorFunction.ClosedFormSolver();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());

        instance.setLearner(null);
        assertNull(instance.getLearner());
    }

    /**
     * Test of getInputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testGetInputConverter()
    {
        this.testSetInputConverter();
    }

    /**
     * Test of setInputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testSetInputConverter()
    {
        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, Vector> instance = new EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, Vector>();
        assertNull(instance.getInputConverter());

        CogxelVectorConverter converter = new CogxelVectorConverter();
        instance.setInputConverter(converter);
        assertSame(instance.getInputConverter(), converter);

        instance.setInputConverter(null);
        assertNull(instance.getInputConverter());
    }

    /**
     * Test of getOutputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testGetOutputConverter()
    {
        this.testSetOutputConverter();
    }

    /**
     * Test of setOutputConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testSetOutputConverter()
    {
        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, Vector> instance = new EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, Vector>();
        assertNull(instance.getOutputConverter());

        CogxelVectorConverter converter = new CogxelVectorConverter();
        instance.setOutputConverter(converter);
        assertSame(instance.getOutputConverter(), converter);

        instance.setOutputConverter(null);
        assertNull(instance.getOutputConverter());
    }

    /**
     * Test of getLearningDataConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testGetLearningDataConverter()
    {
        this.testSetLearningDataConverter();
    }

    /**
     * Test of setLearningDataConverter method, of class gov.sandia.cognition.framework.learning.EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    public void testSetLearningDataConverter()
    {
        EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, Vector> instance = new EvaluatorBasedCognitiveModuleFactoryLearner<Vector, Vector, Vector>();
        assertNull(instance.getLearningDataConverter());

        CogxelVectorConverter converter = new CogxelVectorConverter();
        instance.setLearningDataConverter(converter);
        assertSame(instance.getLearningDataConverter(), converter);

        instance.setLearningDataConverter(null);
        assertNull(instance.getLearningDataConverter());
    }

}
