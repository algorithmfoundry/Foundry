/*
 * File:            InputOutputTransformedBatchLearnerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2012 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.evaluator.IdentityEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.data.convert.ReversibleDataConverter;
import gov.sandia.cognition.data.convert.number.DefaultBooleanToNumberConverter;
import gov.sandia.cognition.data.convert.vector.NumberConverterToVectorAdapter;
import gov.sandia.cognition.data.convert.vector.UniqueBooleanVectorEncoder;
import gov.sandia.cognition.evaluator.CompositeEvaluatorTriple;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.baseline.ConstantLearner;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminantWithBias;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorReader;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class InputOutputTransformedBatchLearner_1.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class InputOutputTransformedBatchLearnerTest
{

    /**
     * Creates a new test.
     */
    public InputOutputTransformedBatchLearnerTest()
    {
        super();
    }

    /**
     * Test of constructors of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testConstructors()
    {
        BatchLearner<? super Collection<? extends String>, ? extends Evaluator<? super String, ? extends Vector>> inputLearner = null;
        BatchLearner<? super Collection<? extends InputOutputPair<? extends Vector, Double>>, ? extends Evaluator<? super Vector, Double>> learner = null;
        BatchLearner<? super Collection<? extends Integer>, ? extends ReversibleDataConverter<Integer, Double>> outputLearner = null;
        InputOutputTransformedBatchLearner<String, Vector, Double, Integer> instance =
            new InputOutputTransformedBatchLearner<String, Vector, Double, Integer>();

        assertSame(inputLearner, instance.getInputLearner());
        assertSame(learner, instance.getLearner());
        assertSame(outputLearner, instance.getOutputLearner());

        inputLearner = new UniqueStringEncoderLearner();
        learner = new LinearRegression();
        outputLearner = new NormalizerLearner();
        instance = new InputOutputTransformedBatchLearner<String, Vector, Double, Integer>(
            inputLearner, learner, outputLearner);

        assertSame(inputLearner, instance.getInputLearner());
        assertSame(learner, instance.getLearner());
        assertSame(outputLearner, instance.getOutputLearner());
    }

    /**
     * Test of clone method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testClone()
    {
        UniqueStringEncoderLearner inputLearner = new UniqueStringEncoderLearner();
        LinearRegression learner = new LinearRegression();
        NormalizerLearner outputLearner = new NormalizerLearner();
        InputOutputTransformedBatchLearner<String, Vector, Double, Integer> instance =
            InputOutputTransformedBatchLearner.create(inputLearner, learner, outputLearner);

        InputOutputTransformedBatchLearner<String, Vector, Double, Integer> clone =
            instance.clone();

        assertNotSame(clone, instance);
        assertNotSame(clone, instance.clone());
        
        assertNotSame(instance.getInputLearner(), clone.getInputLearner());
        assertNotNull(clone.getInputLearner());
        assertNotSame(instance.getLearner(), clone.getLearner());
        assertNotNull(clone.getLearner());
        assertNotSame(instance.getOutputLearner(), clone.getOutputLearner());
        assertNotNull(clone.getOutputLearner());
    }

    /**
     * Test of learn method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testLearn()
    {
        UniqueStringEncoderLearner inputLearner = new UniqueStringEncoderLearner();
        LinearRegression learner = new LinearRegression();
        NormalizerLearner outputLearner = new NormalizerLearner();
        InputOutputTransformedBatchLearner<String, Vector, Double, Integer> instance =
            InputOutputTransformedBatchLearner.create(inputLearner, learner, outputLearner);

        LinkedList<InputOutputPair<String, Integer>> data =
            new LinkedList<InputOutputPair<String, Integer>>();
        data.add(DefaultInputOutputPair.create("a", 1));
        data.add(DefaultInputOutputPair.create("b", 2));
        data.add(DefaultInputOutputPair.create("c", 3));

        CompositeEvaluatorTriple<String, Vector, Double, Integer> result =
            instance.learn(data);
        assertTrue(result.getFirst() instanceof UniqueBooleanVectorEncoder);
        assertTrue(result.getSecond() instanceof LinearDiscriminantWithBias);
        assertTrue(result.getThird() instanceof Normalizer.Reverse);
        System.out.println(((LinearDiscriminantWithBias) result.getSecond()).getWeightVector());

        assertEquals(1, (int) result.evaluate("a"));
        assertEquals(2, (int) result.evaluate("b"));
        assertEquals(3, (int) result.evaluate("c"));

        assertNotSame(result, instance.learn(data));
        assertNotNull(instance.learn(data));
    }

    /**
     * Test of getInputLearner method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testGetInputLearner()
    {
        this.testSetInputLearner();
    }

    /**
     * Test of setInputLearner method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testSetInputLearner()
    {
        BatchLearner<? super Collection<? extends String>, ? extends Evaluator<? super String, ? extends Vector>> inputLearner = null;
        InputOutputTransformedBatchLearner<String, Vector, ?, ?> instance =
            new InputOutputTransformedBatchLearner<String, Vector, Double, Number>();
        assertSame(inputLearner, instance.getInputLearner());

        inputLearner = new UniqueStringEncoderLearner();
        instance.setInputLearner(inputLearner);
        assertSame(inputLearner, instance.getInputLearner());

        inputLearner = null;
        instance.setInputLearner(inputLearner);
        assertSame(inputLearner, instance.getInputLearner());

        inputLearner = new UniqueStringEncoderLearner();
        instance.setInputLearner(inputLearner);
        assertSame(inputLearner, instance.getInputLearner());
    }

    /**
     * Test of getOutputLearner method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testGetOutputLearner()
    {
        this.testSetInputLearner();
    }

    /**
     * Test of setOutputLearner method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testSetOutputLearner()
    {
        BatchLearner<? super Collection<? extends Integer>, ? extends ReversibleDataConverter<Integer, Double>> outputLearner = null;
        InputOutputTransformedBatchLearner<?, ?, Double, Integer> instance =
            new InputOutputTransformedBatchLearner<String, Vector, Double, Integer>();
        assertSame(outputLearner, instance.getOutputLearner());

        outputLearner = new NormalizerLearner();
        instance.setOutputLearner(outputLearner);
        assertSame(outputLearner, instance.getOutputLearner());

        outputLearner = null;
        instance.setOutputLearner(outputLearner);
        assertSame(outputLearner, instance.getOutputLearner());
        
        outputLearner = new NormalizerLearner();
        instance.setOutputLearner(outputLearner);
        assertSame(outputLearner, instance.getOutputLearner());
    }

    /**
     * Test of create method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testCreate()
    {
        UniqueStringEncoderLearner inputLearner = new UniqueStringEncoderLearner();
        LinearRegression learner = new LinearRegression();
        NormalizerLearner outputLearner = new NormalizerLearner();
        InputOutputTransformedBatchLearner<String, Vector, Double, Integer> instance =
            InputOutputTransformedBatchLearner.create(inputLearner, learner, outputLearner);
        assertSame(inputLearner, instance.getInputLearner());
        assertSame(learner, instance.getLearner());
        assertSame(outputLearner, instance.getOutputLearner());

        StringToVectorConverter inputEvaluator = new StringToVectorConverter();
        Normalizer outputEvaluator = new Normalizer(0.0, 1.0);

        instance = InputOutputTransformedBatchLearner.create(inputEvaluator, learner, outputLearner);
        assertSame(inputEvaluator, ((ConstantLearner<?>) instance.getInputLearner()).getValue());
        assertSame(learner, instance.getLearner());
        assertSame(outputLearner, instance.getOutputLearner());

        instance = InputOutputTransformedBatchLearner.create(inputLearner, learner, outputEvaluator);
        assertSame(inputLearner, instance.getInputLearner());
        assertSame(learner, instance.getLearner());
        assertSame(outputEvaluator, ((ConstantLearner<?>) instance.getOutputLearner()).getValue());
        
        instance = InputOutputTransformedBatchLearner.create(inputEvaluator, learner, outputEvaluator);
        assertSame(inputEvaluator, ((ConstantLearner<?>) instance.getInputLearner()).getValue());
        assertSame(learner, instance.getLearner());
        assertSame(outputEvaluator, ((ConstantLearner<?>) instance.getOutputLearner()).getValue());
    }

    /**
     * Test of createInputTransformed method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testCreateInputTransformed()
    {

        UniqueStringEncoderLearner inputLearner = new UniqueStringEncoderLearner();
        LinearRegression learner = new LinearRegression();
        InputOutputTransformedBatchLearner<String, Vector, Double, Double> instance =
            InputOutputTransformedBatchLearner.createInputTransformed(inputLearner, learner);
        assertSame(inputLearner, instance.getInputLearner());
        assertSame(learner, instance.getLearner());
        assertTrue(((ConstantLearner<?>) instance.getOutputLearner()).getValue() instanceof IdentityEvaluator);

        StringToVectorConverter inputEvaluator = new StringToVectorConverter();
        instance = InputOutputTransformedBatchLearner.createInputTransformed(inputEvaluator, learner);
        assertSame(inputEvaluator, ((ConstantLearner<?>) instance.getInputLearner()).getValue());
        assertSame(learner, instance.getLearner());
        assertTrue(((ConstantLearner<?>) instance.getOutputLearner()).getValue() instanceof IdentityEvaluator);
    }

    /**
     * Test of createOutputTransformed method, of class InputOutputTransformedBatchLearner_1.
     */
    @Test
    public void testCreateOutputTransformed()
    {
        LinearRegression learner = new LinearRegression();
        NormalizerLearner outputLearner = new NormalizerLearner();
        InputOutputTransformedBatchLearner<Vector, Vector, Double, Integer> instance =
            InputOutputTransformedBatchLearner.createOutputTransformed(learner, outputLearner);
        assertTrue(((ConstantLearner<?>) instance.getInputLearner()).getValue() instanceof IdentityEvaluator);
        assertSame(learner, instance.getLearner());
        assertSame(outputLearner, instance.getOutputLearner());

        Normalizer outputEvaluator = new Normalizer(0.0, 1.0);
        
        instance = InputOutputTransformedBatchLearner.createOutputTransformed(learner, outputEvaluator);
        assertTrue(((ConstantLearner<?>) instance.getInputLearner()).getValue() instanceof IdentityEvaluator);
        assertSame(learner, instance.getLearner());
        assertSame(outputEvaluator, ((ConstantLearner<?>) instance.getOutputLearner()).getValue());
    }

    public static class StringToVectorConverter
        extends AbstractCloneableSerializable
        implements Evaluator<String, Vector>
    {

        public StringToVectorConverter()
        {
            super();
        }

        public Vector evaluate(
            final String input)
        {
            return VectorReader.parseVector(Arrays.asList(input.split(",")));
        }

    }

    public static class UniqueStringEncoderLearner
        extends AbstractCloneableSerializable
        implements BatchLearner<Collection<? extends String>, UniqueBooleanVectorEncoder<String>>
    {

        public UniqueStringEncoderLearner()
        {
            super();
        }

        public UniqueBooleanVectorEncoder<String> learn(
            final Collection<? extends String> data)
        {
            final ArrayList<String> uniqueValues =
                new ArrayList<String>(new LinkedHashSet<String>(data));

            return new UniqueBooleanVectorEncoder<String>(uniqueValues,
                new NumberConverterToVectorAdapter<Boolean>(
                    new DefaultBooleanToNumberConverter()));
        }


    }

    public static class NormalizerLearner
        extends AbstractCloneableSerializable
        implements BatchLearner<Collection<? extends Integer>, Normalizer>
    {

        public Normalizer learn(
            final Collection<? extends Integer> data)
        {
            final double mean = UnivariateStatisticsUtil.computeMean(data);;
            final double variance = UnivariateStatisticsUtil.computeVariance(data, mean);

            return new Normalizer(mean, variance);
        }


    }

    public static class Normalizer
        extends AbstractCloneableSerializable
        implements ReversibleDataConverter<Integer, Double>
    {
        protected double mean;
        protected double standardDeviation;

        public Normalizer(
            final double mean,
            final double standardDeviation)
        {
            super();

            this.mean = mean;
            this.standardDeviation = standardDeviation;
        }

        public Double evaluate(
            final Integer input)
        {
            return (input - this.mean) / this.standardDeviation;
        }

        public Reverse reverse()
        {
            return new Reverse();
        }

        public class Reverse
            extends AbstractCloneableSerializable
            implements ReversibleDataConverter<Double, Integer>
        {

            public Reverse()
            {
                super();
            }

            public Integer evaluate(
                final Double input)
            {
                return (int) (input.doubleValue() * standardDeviation + mean);
            }

            public Normalizer reverse()
            {
                return Normalizer.this;
            }

        }

    }

    public static class DoubleToBooleanConverter
        implements ReversibleDataConverter<Double, Boolean>
    {

        public DoubleToBooleanConverter()
        {
            super();
        }

        public Boolean evaluate(
            final Double input)
        {
            return input >= 0.0;

        }


        public Reverse reverse()
        {
            return new Reverse();
        }

        public class Reverse
            implements ReversibleDataConverter<Boolean, Double>
        {

            public Reverse()
            {
                super();
            }

            public Double evaluate(
                final Boolean input)
            {
                return (input ? 1.0 : -1.0);
            }

            public DoubleToBooleanConverter reverse()
            {
                return DoubleToBooleanConverter.this;
            }



        }
    }



}