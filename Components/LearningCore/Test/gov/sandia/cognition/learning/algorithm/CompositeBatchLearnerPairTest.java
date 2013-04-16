/*
 * File:            CompositeBatchLearnerPairTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm;

import java.util.Arrays;
import gov.sandia.cognition.learning.algorithm.baseline.ConstantLearner;
import gov.sandia.cognition.data.convert.vector.UniqueBooleanVectorEncoder;
import gov.sandia.cognition.evaluator.CompositeEvaluatorPair;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.learning.algorithm.InputOutputTransformedBatchLearnerTest.UniqueStringEncoderLearner;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@code CompositeBatchLearnerPair}.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class CompositeBatchLearnerPairTest
    extends Object
{

    /**
     * Creates a new test.
     */
    public CompositeBatchLearnerPairTest()
    {
        super();
    }

    /**
     * Test of constructors of class CompositeBatchLearnerPair.
     */
    @Test
    public void testConstructors()
    {
        BatchLearner<? super Collection<? extends String>, ? extends Evaluator<? super String, ? extends Vector>> firstLearner = null;
        BatchLearner<? super Collection<? extends Vector>, ? extends Evaluator<? super Vector, ? extends Double>> secondLearner = null;

        CompositeBatchLearnerPair<String, Vector, Double> instance =
            new CompositeBatchLearnerPair<String, Vector, Double>();
        assertSame(firstLearner, instance.getFirstLearner());
        assertSame(secondLearner, instance.getSecondLearner());

        firstLearner = new UniqueStringEncoderLearner();
        secondLearner = new MostFrequentFeatureLearner();
        instance = new CompositeBatchLearnerPair<String, Vector, Double>(
            firstLearner, secondLearner);
        assertSame(firstLearner, instance.getFirstLearner());
        assertSame(secondLearner, instance.getSecondLearner());
    }
    
    /**
     * Test of learn method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testLearn()
    {
        UniqueStringEncoderLearner firstLearner = new UniqueStringEncoderLearner();
        MostFrequentFeatureLearner secondLearner = new MostFrequentFeatureLearner();

        CompositeBatchLearnerPair<String, Vector, Double> instance =
            CompositeBatchLearnerPair.create(firstLearner, secondLearner);

        Collection<String> data = Arrays.asList("a", "b", "c", "b", "b", "b", "a");
        CompositeEvaluatorPair<String, Vector, Double> result =
            instance.learn(data);
        assertTrue(result.getFirst() instanceof UniqueBooleanVectorEncoder);
        assertTrue(result.getSecond() instanceof VectorIndexEvaluator);

        assertEquals(-1.0, result.evaluate("a"), 0.0);
        assertEquals(1.0, result.evaluate("b"), 0.0);
        assertEquals(-1.0, result.evaluate("c"), 0.0);

        assertNotSame(result, instance.learn(data));
        assertNotNull(instance.learn(data));
    }

    /**
     * Test of getFirst method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testGetFirst()
    {
        BatchLearner<? super Collection<? extends String>, ? extends Evaluator<? super String, ? extends Vector>> firstLearner = null;

        CompositeBatchLearnerPair<String, Vector, Double> instance = new CompositeBatchLearnerPair<String, Vector, Double>();
        assertSame(firstLearner, instance.getFirst());

        firstLearner = new UniqueStringEncoderLearner();
        instance.setFirstLearner(firstLearner);
        assertSame(firstLearner, instance.getFirst());
    }

    /**
     * Test of getSecond method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testGetSecond()
    {
        BatchLearner<? super Collection<? extends Vector>, ? extends Evaluator<? super Vector, ? extends Double>> secondLearner = null;
        CompositeBatchLearnerPair<String, Vector, Double> instance = new CompositeBatchLearnerPair<String, Vector, Double>();
        assertSame(secondLearner, instance.getSecond());

        secondLearner = new MostFrequentFeatureLearner();
        instance.setSecondLearner(secondLearner);
        assertSame(secondLearner, instance.getSecond());
    }

    /**
     * Test of getFirstLearner method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testGetFirstLearner()
    {
        this.testSetFirstLearner();;
    }

    /**
     * Test of setFirstLearner method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testSetFirstLearner()
    {
        BatchLearner<? super Collection<? extends String>, ? extends Evaluator<? super String, ? extends Vector>> firstLearner = null;

        CompositeBatchLearnerPair<String, Vector, Double> instance = new CompositeBatchLearnerPair<String, Vector, Double>();
        assertSame(firstLearner, instance.getFirstLearner());

        firstLearner = new UniqueStringEncoderLearner();
        instance.setFirstLearner(firstLearner);
        assertSame(firstLearner, instance.getFirstLearner());
    }

    /**
     * Test of getSecondLearner method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testGetSecondLearner()
    {
        this.testSetSecondLearner();
    }

    /**
     * Test of setSecondLearner method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testSetSecondLearner()
    {
        BatchLearner<? super Collection<? extends Vector>, ? extends Evaluator<? super Vector, ? extends Double>> secondLearner = null;
        CompositeBatchLearnerPair<String, Vector, Double> instance = new CompositeBatchLearnerPair<String, Vector, Double>();
        assertSame(secondLearner, instance.getSecondLearner());

        secondLearner = new MostFrequentFeatureLearner();
        instance.setSecondLearner(secondLearner);
        assertSame(secondLearner, instance.getSecondLearner());
    }

    /**
     * Test of create method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testCreate()
    {
        UniqueStringEncoderLearner firstLearner = new UniqueStringEncoderLearner();
        MostFrequentFeatureLearner secondLearner = new MostFrequentFeatureLearner();

        CompositeBatchLearnerPair<String, Vector, Double> instance =
            CompositeBatchLearnerPair.create(firstLearner, secondLearner);
        assertSame(firstLearner, instance.getFirstLearner());
        assertSame(secondLearner, instance.getSecondLearner());
    }

    /**
     * Test of createInputTransformed method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testCreateInputTransformed()
    {
        UniqueBooleanVectorEncoder<String> inputTransform = new UniqueBooleanVectorEncoder<String>(null, null);
        MostFrequentFeatureLearner learner = new MostFrequentFeatureLearner();

        CompositeBatchLearnerPair<String, Vector, Double> instance =
            CompositeBatchLearnerPair.createInputTransformed(inputTransform, learner);
        assertSame(inputTransform, ((ConstantLearner<?>) instance.getFirstLearner()).getValue());
        assertSame(learner, instance.getSecondLearner());
    }

    /**
     * Test of testCreateOutputTransformed method, of class CompositeBatchLearnerPair.
     */
    @Test
    public void testCreateOutputTransformed()
    {
        UniqueStringEncoderLearner learner = new UniqueStringEncoderLearner();
        VectorIndexEvaluator outputTransform = new VectorIndexEvaluator(3);

        CompositeBatchLearnerPair<String, Vector, Double> instance =
            CompositeBatchLearnerPair.createOutputTransformed(learner, outputTransform);
        assertSame(learner, instance.getFirstLearner());
        assertSame(outputTransform, ((ConstantLearner<?>) instance.getSecondLearner()).getValue());
    }

    public static class VectorIndexEvaluator
        extends AbstractCloneableSerializable
        implements Evaluator<Vector, Double>
    {
        protected int index;
        public VectorIndexEvaluator(
            final int index)
        {
            super();

            this.index = index;
        }

        @Override
        public Double evaluate(
            final Vector input)
        {
            return input.getElement(this.index);
        }
        
    }

    public static class MostFrequentFeatureLearner
        extends AbstractCloneableSerializable
        implements BatchLearner<Collection<? extends Vector>, VectorIndexEvaluator>
    {

        public MostFrequentFeatureLearner()
        {
            super();
        }

        @Override
        public VectorIndexEvaluator learn(
            final Collection<? extends Vector> data)
        {
            final DataDistribution<Integer> featureCounts =
                new DefaultDataDistribution<Integer>();

            for (Vector input : data)
            {
                for (VectorEntry entry : input)
                {
                    if (entry.getValue() > 0.0)
                    {
                        featureCounts.increment(entry.getIndex());
                    }
                }
            }

            return new VectorIndexEvaluator(featureCounts.getMaxValueKey());
        }

    }
}