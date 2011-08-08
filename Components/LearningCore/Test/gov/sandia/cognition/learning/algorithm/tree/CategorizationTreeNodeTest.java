/*
 * File:                CategorizationTreeNodeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import junit.framework.*;
import gov.sandia.cognition.math.matrix.mtj.Vector3;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CategorizationTreeNode
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CategorizationTreeNodeTest
    extends TestCase
{
    public CategorizationTreeNodeTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        DecisionTreeNode<Vector3, String> parent = null;
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertSame(parent, instance.getParent());
        assertNull(instance.getOutputCategory());
        assertNull(instance.getDecider());
        assertNull(instance.getIncomingValue());
        
        String outputCategory = "a";
        instance = new CategorizationTreeNode<Vector3, String, Boolean>(parent,
            outputCategory);
        assertSame(parent, instance.getParent());
        assertSame(outputCategory, instance.getOutputCategory());
        assertNull(instance.getDecider());
        assertNull(instance.getIncomingValue());
        
        Object incomingValue = Math.random();
        instance = new CategorizationTreeNode<Vector3, String, Boolean>(parent,
            outputCategory, incomingValue);
        assertSame(parent, instance.getParent());
        assertSame(outputCategory, instance.getOutputCategory());
        assertNull(instance.getDecider());
        assertSame(incomingValue, instance.getIncomingValue());
        
        VectorElementThresholdCategorizer decider = 
            new VectorElementThresholdCategorizer(4, Math.random());
        instance = new CategorizationTreeNode<Vector3, String, Boolean>(parent,
            decider, outputCategory, incomingValue);
        assertSame(parent, instance.getParent());
        assertSame(outputCategory, instance.getOutputCategory());
        assertSame(decider, instance.getDecider());
        assertSame(incomingValue, instance.getIncomingValue());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testClone()
    {
        String outputCategory = "a";
        Object incomingValue = Math.random();
        VectorElementThresholdCategorizer decider = 
            new VectorElementThresholdCategorizer(4, Math.random());
        
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>(null,
                decider, outputCategory, incomingValue);
        
        CategorizationTreeNode<Vector3, String, Boolean> clone = instance.clone();
        assertSame(outputCategory, clone.getOutputCategory());
        assertSame(decider, clone.getDecider());
        assertSame(incomingValue, clone.getIncomingValue());
    }

    /**
     * Test of addChild method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testAddChild()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        
        CategorizationTreeNode<Vector3, String, Boolean> child = 
            new CategorizationTreeNode<Vector3, String, Boolean>(instance,
                "child");
        instance.addChild(false, child);
        assertEquals(1, instance.getChildMap().size());
        assertEquals(1, instance.getChildren().size());
        assertTrue(instance.getChildren().contains(child));
    }

    /**
     * Test of getChildren method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetChildren()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertTrue(instance.getChildren().isEmpty());
    }

    /**
     * Test of isLeaf method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testIsLeaf()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertTrue(instance.isLeaf());
        
        
        CategorizationTreeNode<Vector3, String, Boolean> child = 
            new CategorizationTreeNode<Vector3, String, Boolean>(instance,
                "child");
        instance.addChild(false, child);
        assertFalse(instance.isLeaf());
    }

    /**
     * Test of chooseChild method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testChooseChild()
    {
        VectorElementThresholdCategorizer decider = 
            new VectorElementThresholdCategorizer(1, 7.0);
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>(
                null, decider, "a", null);
        
        assertNull(instance.chooseChild(new Vector3()));
        
        CategorizationTreeNode<Vector3, String, Boolean> child1 = 
            new CategorizationTreeNode<Vector3, String, Boolean>(
                instance, "child1");
        CategorizationTreeNode<Vector3, String, Boolean> child2 = 
            new CategorizationTreeNode<Vector3, String, Boolean>(
                instance, "child2");
        instance.addChild(false, child1);
        
        assertSame(child1, instance.chooseChild(new Vector3(0.0, 4.0, 0.0)));
        assertNull(instance.chooseChild(new Vector3(0.0, 7.0, 0.0)));
        
        instance.addChild(true, child2);
        assertSame(child1, instance.chooseChild(new Vector3(0.0, 4.0, 0.0)));
        assertSame(child2, instance.chooseChild(new Vector3(0.0, 7.0, 0.0)));
    }

    /**
     * Test of getOutput method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetOutput()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>(null, "a");
        assertEquals("a", 
            instance.getOutput(new Vector3(Math.random(), Math.random(), Math.random())));
        
        instance.setOutputCategory("b");
        assertEquals("b", instance.getOutput(new Vector3(Math.random(), Math.random(), Math.random())));
        
        instance.setDecider(new VectorElementThresholdCategorizer(1, 0.5));
        CategorizationTreeNode<Vector3, String, Boolean> child1 = 
            new CategorizationTreeNode<Vector3, String, Boolean>(
                instance, "child1");
        CategorizationTreeNode<Vector3, String, Boolean> child2 = 
            new CategorizationTreeNode<Vector3, String, Boolean>(
                instance, "child2");
        instance.addChild(false, child1);
        instance.addChild(true, child2);
        
        instance.setOutputCategory("b");
        assertEquals("b", instance.getOutput(new Vector3(Math.random(), Math.random(), Math.random())));
    }

    /**
     * Test of getDepth method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetDepth()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance =
            new CategorizationTreeNode<Vector3, String, Boolean>(null, "a");
        assertEquals(1, instance.getDepth());

        CategorizationTreeNode<Vector3, String, Boolean> child1 =
            new CategorizationTreeNode<Vector3, String, Boolean>(
                instance, "child1");
        CategorizationTreeNode<Vector3, String, Boolean> child2 =
            new CategorizationTreeNode<Vector3, String, Boolean>(
                instance, "child2");
        instance.addChild(false, child1);
        instance.addChild(true, child2);

        assertEquals(2, child1.getDepth());
        assertEquals(2, child2.getDepth());

        CategorizationTreeNode<Vector3, String, Boolean> child21 =
            new CategorizationTreeNode<Vector3, String, Boolean>(
                child2, "child21");
        child2.addChild(true, child21);
        assertEquals(3, child21.getDepth());
    }

    /**
     * Test of getDecider method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetDecider()
    {
        this.testSetDecider();
    }

    /**
     * Test of setDecider method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testSetDecider()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertNull(instance.getDecider());
        
        VectorElementThresholdCategorizer decider = 
            new VectorElementThresholdCategorizer(4, Math.random());
        instance.setDecider(decider);
        assertSame(decider, instance.getDecider());
        
        instance.setDecider(null);
        assertNull(instance.getDecider());
    }

    /**
     * Test of getChildMap method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetChildMap()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertNull(instance.getChildMap());
    }

    /**
     * Test of getOutputCategory method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetOutputCategory()
    {
        this.testSetOutputCategory();
    }

    /**
     * Test of setOutputCategory method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testSetOutputCategory()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertNull(instance.getOutputCategory());
        
        String outputCategory = "a";
        instance.setOutputCategory(outputCategory);
        assertSame(outputCategory, instance.getOutputCategory());
        
        instance.setOutputCategory(null);
        assertNull(instance.getOutputCategory());
    }

    /**
     * Test of getIncomingValue method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testGetIncomingValue()
    {
        this.testSetIncomingValue();
    }

    /**
     * Test of setIncomingValue method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeNode.
     */
    public void testSetIncomingValue()
    {
        CategorizationTreeNode<Vector3, String, Boolean> instance = 
            new CategorizationTreeNode<Vector3, String, Boolean>();
        assertNull(instance.getIncomingValue());
        
        Object incomingValue = Math.random();
        instance.setIncomingValue(incomingValue);
        assertSame(incomingValue, instance.getIncomingValue());
        
        instance.setIncomingValue(null);
        assertNull(instance.getIncomingValue());
    }
}
