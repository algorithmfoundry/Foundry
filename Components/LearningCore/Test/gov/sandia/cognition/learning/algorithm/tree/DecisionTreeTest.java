/*
 * File:                DecisionTreeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DecisionTree
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class DecisionTreeTest
    extends TestCase
{
    public DecisionTreeTest(
        String testName)
    {
        super(testName);
    }
    
    
    public void testConstructors()
    {
        DecisionTree<Vectorizable, Boolean> instance = 
            new DecisionTree<Vectorizable, Boolean>();
        assertNull(instance.getRootNode());
        
        CategorizationTreeNode<Vectorizable, Boolean, Object> rootNode =
            new CategorizationTreeNode<Vectorizable, Boolean, Object>();
        instance = new DecisionTree<Vectorizable, Boolean>(rootNode);
        assertSame(rootNode, instance.getRootNode());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.algorithm.tree.DecisionTree.
     */
    public void testEvaluate()
    {
        DecisionTree<Vectorizable, String> instance = 
            new DecisionTree<Vectorizable, String>();
        Vector3 input = new Vector3(Math.random(), Math.random(), Math.random());
        assertNull(instance.evaluate(input));
        
        CategorizationTreeNode<Vectorizable, String, Boolean> rootNode =
            new CategorizationTreeNode<Vectorizable, String, Boolean>(null, "root");
        instance.setRootNode(rootNode);
        assertEquals("root", instance.evaluate(input));
        
        rootNode.setDecider(new VectorElementThresholdCategorizer(1, 10.0));
        assertEquals("root", instance.evaluate(input));
        
        CategorizationTreeNode<Vectorizable, String, Boolean> child1 =
            new CategorizationTreeNode<Vectorizable, String, Boolean>(rootNode,
                "child1");
        
        rootNode.addChild(false, child1);
        assertEquals("child1", instance.evaluate(new Vector3(0.0, 9.0, 0.0)));
        assertEquals("root", instance.evaluate(new Vector3(0.0, 11.0, 0.0)));
        
        CategorizationTreeNode<Vectorizable, String, Boolean> child2 =
            new CategorizationTreeNode<Vectorizable, String, Boolean>(rootNode,
            "child2");
        rootNode.addChild(true, child2);
        assertEquals("child1", instance.evaluate(new Vector3(0.0, 9.0, 0.0)));
        assertEquals("child2", instance.evaluate(new Vector3(0.0, 11.0, 0.0)));
    }

    /**
     * Test of evaluateNode method, of class gov.sandia.cognition.learning.algorithm.tree.DecisionTree.
     */
    public void testEvaluateNode()
    {
        DecisionTree<Vectorizable, String> instance = 
            new DecisionTree<Vectorizable, String>();
        
        Vector3 random = new Vector3(Math.random(), Math.random(), Math.random());
        assertNull(instance.evaluateNode(random, null));
        
        CategorizationTreeNode<Vectorizable, String, Boolean> rootNode =
            new CategorizationTreeNode<Vectorizable, String, Boolean>(
                null, "root");
        instance.setRootNode(rootNode);
        assertEquals("root", instance.evaluateNode(random, rootNode));
       
        rootNode.setDecider(new VectorElementThresholdCategorizer(1, 10.0));
        assertEquals("root", instance.evaluateNode(random, rootNode));
        
        CategorizationTreeNode<Vectorizable, String, Boolean> child1 =
            new CategorizationTreeNode<Vectorizable, String, Boolean>(rootNode,
                "child1");
        
        rootNode.addChild(false, child1);
        assertEquals("child1", instance.evaluateNode(new Vector3(0.0, 9.0, 0.0), rootNode));
        assertEquals("root", instance.evaluateNode(new Vector3(0.0, 11.0, 0.0), rootNode));
        
        CategorizationTreeNode<Vectorizable, String, Boolean> child2 =
            new CategorizationTreeNode<Vectorizable, String, Boolean>(rootNode,
                "child2");
        rootNode.addChild(true, child2);
        assertEquals("child1", instance.evaluateNode(new Vector3(0.0, 9.0, 0.0), rootNode));
        assertEquals("child2", instance.evaluateNode(new Vector3(0.0, 11.0, 0.0), rootNode));
    }

    /**
     * Test of getRootNode method, of class gov.sandia.cognition.learning.algorithm.tree.DecisionTree.
     */
    public void testGetRootNode()
    {
        this.testSetRootNode();
    }

    /**
     * Test of setRootNode method, of class gov.sandia.cognition.learning.algorithm.tree.DecisionTree.
     */
    public void testSetRootNode()
    {
        DecisionTree<Vectorizable, Boolean> instance = 
            new DecisionTree<Vectorizable, Boolean>();
        assertNull(instance.getRootNode());
        
        CategorizationTreeNode<Vectorizable, Boolean, Object> rootNode =
            new CategorizationTreeNode<Vectorizable, Boolean, Object>();
        instance.setRootNode(rootNode);
        assertSame(rootNode, instance.getRootNode());
        
        instance.setRootNode(null);
        assertNull(instance.getRootNode());
    }
}
