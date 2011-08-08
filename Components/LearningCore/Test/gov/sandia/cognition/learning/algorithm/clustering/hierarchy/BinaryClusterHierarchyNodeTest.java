/*
 * File:                BinaryClusterHierarchyNodeTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 02, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering.hierarchy;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultCluster;
import java.util.List;
import junit.framework.TestCase;

/**
 * Tests of BinaryClusterHierarchyNode
 * @author  Justin Basilico
 * @since   2.1
 */
public class BinaryClusterHierarchyNodeTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public BinaryClusterHierarchyNodeTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        DefaultCluster<String> cluster = null;
        ClusterHierarchyNode<String, DefaultCluster<String>> firstChild = null;
        ClusterHierarchyNode<String, DefaultCluster<String>> secondChild = null;
        
        BinaryClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        assertSame(cluster, instance.getCluster());
        assertSame(firstChild, instance.getFirstChild());
        assertSame(secondChild, instance.getSecondChild());
        
        cluster = new DefaultCluster<String>();
        instance = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>(
            cluster);
        assertSame(cluster, instance.getCluster());
        assertSame(firstChild, instance.getFirstChild());
        assertSame(secondChild, instance.getSecondChild());
        
        firstChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        secondChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>(
            cluster, firstChild, secondChild);
        assertSame(cluster, instance.getCluster());
        assertSame(firstChild, instance.getFirstChild());
        assertSame(secondChild, instance.getSecondChild());
    }

    /**
     * Test of hasChildren method, of class BinaryClusterHierarchyNode.
     */
    public void testHasChildren()
    {
        BinaryClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        assertFalse(instance.hasChildren());
        
        instance.setFirstChild(new BinaryClusterHierarchyNode<String, DefaultCluster<String>>());
        assertTrue(instance.hasChildren());
        
        instance.setSecondChild(new BinaryClusterHierarchyNode<String, DefaultCluster<String>>());
        assertTrue(instance.hasChildren());
        
        instance.setFirstChild(null);
        assertTrue(instance.hasChildren());
        
        instance.setSecondChild(null);
        assertFalse(instance.hasChildren());
    }

    /**
     * Test of getChildren method, of class BinaryClusterHierarchyNode.
     */
    public void testGetChildren()
    {
        
        ClusterHierarchyNode<String, DefaultCluster<String>> firstChild = null;
        ClusterHierarchyNode<String, DefaultCluster<String>> secondChild = null;
        
        BinaryClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        List<ClusterHierarchyNode<String, DefaultCluster<String>>> result = 
            instance.getChildren();
        assertNull(result);
        
        
        firstChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance.setFirstChild(firstChild);
        result = instance.getChildren();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(firstChild, result.get(0));
        
        secondChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance.setSecondChild(secondChild);
        result = instance.getChildren();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(firstChild, result.get(0));
        assertSame(secondChild, result.get(1));
        
        firstChild = null;
        instance.setFirstChild(firstChild);
        result = instance.getChildren();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(secondChild, result.get(0));
        
        secondChild = null;
        instance.setSecondChild(secondChild);
        result = instance.getChildren();
        assertNull(result);
    }

    /**
     * Test of getFirstChild method, of class BinaryClusterHierarchyNode.
     */
    public void testGetFirstChild()
    {
        this.testSetFirstChild();
    }

    /**
     * Test of setFirstChild method, of class BinaryClusterHierarchyNode.
     */
    public void testSetFirstChild()
    {
        ClusterHierarchyNode<String, DefaultCluster<String>> firstChild = null;
        
        BinaryClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        assertSame(firstChild, instance.getFirstChild());
        
        firstChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance.setFirstChild(firstChild);
        assertSame(firstChild, instance.getFirstChild());
        
        firstChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance.setFirstChild(firstChild);
        assertSame(firstChild, instance.getFirstChild());
        
        firstChild = null;
        instance.setFirstChild(firstChild);
        assertSame(firstChild, instance.getFirstChild());
    }

    /**
     * Test of getSecondChild method, of class BinaryClusterHierarchyNode.
     */
    public void testGetSecondChild()
    {
        this.testSetSecondChild();
    }

    /**
     * Test of setSecondChild method, of class BinaryClusterHierarchyNode.
     */
    public void testSetSecondChild()
    {
        ClusterHierarchyNode<String, DefaultCluster<String>> secondChild = null;
        
        BinaryClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        assertSame(secondChild, instance.getSecondChild());
        
        secondChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance.setSecondChild(secondChild);
        assertSame(secondChild, instance.getSecondChild());
        
        secondChild = new BinaryClusterHierarchyNode<String, DefaultCluster<String>>();
        instance.setSecondChild(secondChild);
        assertSame(secondChild, instance.getSecondChild());
        
        secondChild = null;
        instance.setSecondChild(secondChild);
        assertSame(secondChild, instance.getSecondChild());
        
    }

}
