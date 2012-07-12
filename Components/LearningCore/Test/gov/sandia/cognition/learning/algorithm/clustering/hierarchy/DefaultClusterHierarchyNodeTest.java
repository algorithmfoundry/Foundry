/*
 * File:                DefaultClusterHierarchyNodeTest.java
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

/**
 * Tests of DefaultClusterHierarchyNode
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultClusterHierarchyNodeTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultClusterHierarchyNodeTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        DefaultCluster<String> cluster = null;
        List<ClusterHierarchyNode<String, DefaultCluster<String>>> children = null;
        
        DefaultClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new DefaultClusterHierarchyNode<String, DefaultCluster<String>>();
        assertSame(cluster, instance.getCluster());
        assertSame(children, instance.getChildren());
        
        cluster = new DefaultCluster<String>();
        instance = new DefaultClusterHierarchyNode<String, DefaultCluster<String>>(
            cluster);
        assertSame(cluster, instance.getCluster());
        assertSame(children, instance.getChildren());
        
        children = new ArrayList<ClusterHierarchyNode<String, DefaultCluster<String>>>();
        instance = new DefaultClusterHierarchyNode<String, DefaultCluster<String>>(
            cluster, children);
        assertSame(cluster, instance.getCluster());
        assertSame(children, instance.getChildren());

        instance = new DefaultClusterHierarchyNode<String, DefaultCluster<String>>(
            cluster, null);
        assertSame( cluster, instance.getCluster() );
        assertNull( instance.getChildren() );

    }

    /**
     * Test of getChildren method, of class DefaultClusterHierarchyNode.
     */
    public void testGetChildren()
    {
        this.testSetChildren();
    }

    /**
     * Test of setChildren method, of class DefaultClusterHierarchyNode.
     */
    @SuppressWarnings("unchecked")
    public void testSetChildren()
    {
        List<ClusterHierarchyNode<String, DefaultCluster<String>>> children = null;
        
        DefaultClusterHierarchyNode<String, DefaultCluster<String>> instance =
            new DefaultClusterHierarchyNode<String, DefaultCluster<String>>();
        assertSame(children, instance.getChildren());
        assertFalse(instance.hasChildren());
        
        children = new ArrayList<ClusterHierarchyNode<String, DefaultCluster<String>>>();
        instance.setChildren(children);
        assertSame(children, instance.getChildren());
        assertFalse(instance.hasChildren());
        
        children = new ArrayList<ClusterHierarchyNode<String, DefaultCluster<String>>>();
        ClusterHierarchyNode<String, DefaultCluster<String>> child =
            new DefaultClusterHierarchyNode<String, DefaultCluster<String>>();
        children.add(child);
        instance.setChildren(children);
        assertSame(children, instance.getChildren());
        assertTrue(instance.hasChildren());
        assertSame(child, instance.getChildren().get(0));
        
        children = null;
        instance.setChildren(children);
        assertSame(children, instance.getChildren());
        
        instance.setChildren(Arrays.asList(child));
        assertTrue(instance.hasChildren());
        assertSame(child, instance.getChildren().get(0));
        
        children = Collections.emptyList();
        instance.setChildren(children);
        assertFalse(instance.hasChildren());
    }

}
