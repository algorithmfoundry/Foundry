/*
 * File:                OnlineVotedPerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright October 20, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.ensemble.WeightedBinaryEnsemble;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import junit.framework.TestCase;

/**
 * Unit tests for class OnlineVotedPerceptron.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class OnlineVotedPerceptronTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public OnlineVotedPerceptronTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class OnlineVotedPerceptron.
     */
    public void testConstructors()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlineVotedPerceptron instance = new OnlineVotedPerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance = new OnlineVotedPerceptron(factory);
        assertSame(factory, instance.getVectorFactory());
    }

    /**
     * Test of createInitialLearnedObject method, of class OnlineVotedPerceptron.
     */
    public void testCreateInitialLearnedObject()
    {
        OnlineVotedPerceptron instance = new OnlineVotedPerceptron();
        WeightedBinaryEnsemble<Vectorizable, ?> result =
            instance.createInitialLearnedObject();
        assertEquals(0, result.getMembers().size());
        assertNotSame(result, instance.createInitialLearnedObject());
    }

    /**
     * Test of update method, of class OnlineVotedPerceptron.
     */
    public void testUpdate()
    {
        System.out.println("update");
        WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer> target =
            null;
        InputOutputPair<? extends Vectorizable, Boolean> example = null;
        OnlineVotedPerceptron instance = new OnlineVotedPerceptron();
        WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer> result =
            instance.createInitialLearnedObject();
        assertEquals(0, result.getMembers().size());


        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(1, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(0).getWeight());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(4.0, 4.0), true));
        assertEquals(1, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(0).getWeight());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(2, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(0).getWeight());
        assertEquals(new Vector2(1.0, 2.0), result.getMembers().get(1).getValue().getWeights());
        assertEquals(0.0, result.getMembers().get(1).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(1).getWeight());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(3, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(0).getWeight());
        assertEquals(new Vector2(1.0, 2.0), result.getMembers().get(1).getValue().getWeights());
        assertEquals(0.0, result.getMembers().get(1).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(1).getWeight());
        assertEquals(new Vector2(0.0, 1.0), result.getMembers().get(2).getValue().getWeights());
        assertEquals(-1.0, result.getMembers().get(2).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(2).getWeight());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(3, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(0).getWeight());
        assertEquals(new Vector2(1.0, 2.0), result.getMembers().get(1).getValue().getWeights());
        assertEquals(0.0, result.getMembers().get(1).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(1).getWeight());
        assertEquals(new Vector2(0.0, 1.0), result.getMembers().get(2).getValue().getWeights());
        assertEquals(-1.0, result.getMembers().get(2).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(2).getWeight());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(3, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(0).getWeight());
        assertEquals(new Vector2(1.0, 2.0), result.getMembers().get(1).getValue().getWeights());
        assertEquals(0.0, result.getMembers().get(1).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(1).getWeight());
        assertEquals(new Vector2(0.0, 1.0), result.getMembers().get(2).getValue().getWeights());
        assertEquals(-1.0, result.getMembers().get(2).getValue().getBias());
        assertEquals(3.0, result.getMembers().get(2).getWeight());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(3, result.getMembers().size());
        assertEquals(new Vector2(2.0, 3.0), result.getMembers().get(0).getValue().getWeights());
        assertEquals(1.0, result.getMembers().get(0).getValue().getBias());
        assertEquals(2.0, result.getMembers().get(0).getWeight());
        assertEquals(new Vector2(1.0, 2.0), result.getMembers().get(1).getValue().getWeights());
        assertEquals(0.0, result.getMembers().get(1).getValue().getBias());
        assertEquals(1.0, result.getMembers().get(1).getWeight());
        assertEquals(new Vector2(0.0, 1.0), result.getMembers().get(2).getValue().getWeights());
        assertEquals(-1.0, result.getMembers().get(2).getValue().getBias());
        assertEquals(4.0, result.getMembers().get(2).getWeight());
    }

    /**
     * Test of getLastMember method, of class OnlineVotedPerceptron.
     */
    public void testGetLastMember()
    {
        System.out.println("getLastMember");
        WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer> ensemble =
            new WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer>();

        assertNull(OnlineVotedPerceptron.getLastMember(ensemble));
        LinearBinaryCategorizer last = new LinearBinaryCategorizer();
        ensemble.add(last);
        assertSame(last, OnlineVotedPerceptron.getLastMember(ensemble).getValue());
        
        last = new LinearBinaryCategorizer();
        ensemble.add(last);
        assertSame(last, OnlineVotedPerceptron.getLastMember(ensemble).getValue());
    }

    /**
     * Test of getVectorFactory method, of class OnlineVotedPerceptron.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class OnlineVotedPerceptron.
     */
    public void testSetVectorFactory()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlineVotedPerceptron instance = new OnlineVotedPerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());

        factory = null;
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());

        factory = VectorFactory.getDenseDefault();
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());
    }

}
