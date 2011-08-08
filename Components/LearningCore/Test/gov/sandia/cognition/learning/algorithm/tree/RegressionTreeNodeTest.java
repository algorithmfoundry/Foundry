/*
 * File:                RegressionTreeNodeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import junit.framework.*;
import gov.sandia.cognition.learning.function.scalar.KernelScalarFunction;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.Collections;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     RegressionTreeNode
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class RegressionTreeNodeTest
    extends TestCase
{

    /**
     * Creates a new RegressionTreeNodeTest.
     *
     * @param testName The test name.
     */
    public RegressionTreeNodeTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeNode.
     */
    public void testConstructors()
    {
        DecisionTreeNode<Vectorizable, Double> parent = null;
        RegressionTreeNode<Vectorizable, Object> instance =
            new RegressionTreeNode<Vectorizable, Object>();
        assertSame(parent, instance.getParent());
        assertNull(instance.getScalarFunction());
        assertNull(instance.getDecider());
        assertEquals(RegressionTreeNode.DEFAULT_VALUE, instance.getValue());
        assertNull(instance.getIncomingValue());

        parent = new RegressionTreeNode<Vectorizable, Object>();
        double value = Math.random();
        instance = new RegressionTreeNode<Vectorizable, Object>(parent, value);
        assertSame(parent, instance.getParent());
        assertNull(instance.getScalarFunction());
        assertNull(instance.getDecider());
        assertEquals(value, instance.getValue());
        assertNull(instance.getIncomingValue());

        VectorElementThresholdCategorizer decider =
            new VectorElementThresholdCategorizer(4,
            Math.random());
        instance = new RegressionTreeNode<Vectorizable, Object>(parent, decider,
            value);
        assertSame(parent, instance.getParent());
        assertNull(instance.getScalarFunction());
        assertSame(decider, instance.getDecider());
        assertEquals(value, instance.getValue());
        assertNull(instance.getIncomingValue());

        KernelScalarFunction<Vectorizable> scalarFunction =
            new KernelScalarFunction<Vectorizable>();
        instance = new RegressionTreeNode<Vectorizable, Object>(parent,
            scalarFunction, value);
        assertSame(parent, instance.getParent());
        assertSame(scalarFunction, instance.getScalarFunction());
        assertNull(instance.getDecider());
        assertEquals(value, instance.getValue());
        assertNull(instance.getIncomingValue());

        Object incomingValue = "something";
        instance = new RegressionTreeNode<Vectorizable, Object>(parent,
            scalarFunction, value, incomingValue);
        assertSame(parent, instance.getParent());
        assertSame(scalarFunction, instance.getScalarFunction());
        assertNull(instance.getDecider());
        assertEquals(value, instance.getValue());
        assertEquals(incomingValue, instance.getIncomingValue());


        instance = new RegressionTreeNode<Vectorizable, Object>(parent,
            decider, scalarFunction, value, incomingValue);
        assertSame(parent, instance.getParent());
        assertSame(scalarFunction, instance.getScalarFunction());
        assertSame(decider, instance.getDecider());
        assertEquals(value, instance.getValue());
        assertEquals(incomingValue, instance.getIncomingValue());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeNode.
     */
    public void testClone()
    {

        VectorElementThresholdCategorizer decider =
            new VectorElementThresholdCategorizer(4, Math.random());
        KernelScalarFunction<Vectorizable> scalarFunction =
            new KernelScalarFunction<Vectorizable>();
        Object incomingValue = "something";

        double value = Math.random();
        RegressionTreeNode<Vectorizable, Object> instance =
            new RegressionTreeNode<Vectorizable, Object>(null,
            decider, scalarFunction, value, incomingValue);

        RegressionTreeNode<Vectorizable, Object> clone = instance.clone();
        assertSame(scalarFunction, instance.getScalarFunction());
        assertSame(decider, instance.getDecider());
        assertEquals(incomingValue, instance.getIncomingValue());
    }

    /**
     * Test of getOutput method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeNode.
     */
    public void testGetOutput()
    {
        double value = Math.random();
        RegressionTreeNode<Vectorizable, Object> instance =
            new RegressionTreeNode<Vectorizable, Object>(null, value);
        assertEquals(value, instance.getOutput(new Vector3()));


        KernelScalarFunction<Vectorizable> scalarFunction =
            new KernelScalarFunction<Vectorizable>();
        scalarFunction.setKernel(LinearKernel.getInstance());
        scalarFunction.setBias(Math.random());
        scalarFunction.setExamples(Collections.singleton(
            new DefaultWeightedValue<Vector3>(
            new Vector3(Math.random(), Math.random(), Math.random()),
            Math.random())));
        instance.setScalarFunction(scalarFunction);

        for (int i = 0; i < 5; i++)
        {
            Vector3 input = new Vector3(
                Math.random(), Math.random(), Math.random());
            double expected = scalarFunction.evaluate(input);
            assertEquals(expected, (double) instance.getOutput(input));
        }
    }

    /**
     * Test of getScalarFunction method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeNode.
     */
    public void testGetScalarFunction()
    {
        this.testSetScalarFunction();
    }

    /**
     * Test of setScalarFunction method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeNode.
     */
    public void testSetScalarFunction()
    {
        RegressionTreeNode<Vectorizable, Object> instance =
            new RegressionTreeNode<Vectorizable, Object>();
        assertNull(instance.getScalarFunction());

        KernelScalarFunction<Vectorizable> scalarFunction =
            new KernelScalarFunction<Vectorizable>();
        instance.setScalarFunction(scalarFunction);
        assertSame(scalarFunction, instance.getScalarFunction());

        instance.setScalarFunction(null);
        assertNull(instance.getScalarFunction());

    }

    /**
     * Test of getValue method, of class RegressionTreeNode.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class RegressionTreeNode.
     */
    public void testSetValue()
    {
        RegressionTreeNode<Vectorizable, Object> instance =
            new RegressionTreeNode<Vectorizable, Object>();
        assertEquals(RegressionTreeNode.DEFAULT_VALUE, instance.getValue());

        double value = Math.random();
        instance.setValue(value);
        assertEquals(value, instance.getValue());

        value = -Math.random();
        instance.setValue(value);
        assertEquals(value, instance.getValue());
    }

}
