/*
 * File:                DefaultIncrementalClusterCreatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 09, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultIncrementalClusterCreator.
 *
 * @author  Justin Basilico
 * @since   3.1.1
 */
public class DefaultIncrementalClusterCreatorTest
{
    /**
     * Creates a new test.
     */
    public DefaultIncrementalClusterCreatorTest()
    {
    }

    /**
     * Test of constructors of class DefaultIncrementalClusterCreator.
     */
    @Test
    public void testConstructors()
    {
        DefaultIncrementalClusterCreator<Object> instance =
            new DefaultIncrementalClusterCreator<Object>();
        assertNotNull(instance);
    }

    /**
     * Test of createCluster method, of class DefaultIncrementalClusterCreator.
     */
    @Test
    public void testCreateCluster()
    {
        DefaultIncrementalClusterCreator<String> instance =
            new DefaultIncrementalClusterCreator<String>();
        DefaultCluster<String> result = instance.createCluster();
        assertNotNull(result);
        assertTrue(result.getMembers().isEmpty());
        assertNotSame(result, instance.createCluster());
    }

    /**
     * Test of addClusterMember method, of class DefaultIncrementalClusterCreator.
     */
    @Test
    public void testAddClusterMember()
    {
        DefaultIncrementalClusterCreator<String> instance =
            new DefaultIncrementalClusterCreator<String>();
        DefaultCluster<String> cluster = instance.createCluster();
        assertTrue(cluster.getMembers().isEmpty());

        instance.addClusterMember(cluster, "a");
        assertEquals(1, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains("a"));

        instance.addClusterMember(cluster, "b");
        assertEquals(2, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains("a"));
        assertTrue(cluster.getMembers().contains("b"));
    }

    /**
     * Test of removeClusterMember method, of class DefaultIncrementalClusterCreator.
     */
    @Test
    public void testRemoveClusterMember()
    {
        DefaultIncrementalClusterCreator<String> instance =
            new DefaultIncrementalClusterCreator<String>();
        DefaultCluster<String> cluster = instance.createCluster();
        assertTrue(cluster.getMembers().isEmpty());
        
        assertFalse(instance.removeClusterMember(cluster, "a"));

        instance.addClusterMember(cluster, "a");
        assertEquals(1, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains("a"));

        instance.addClusterMember(cluster, "b");
        assertEquals(2, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains("a"));
        assertTrue(cluster.getMembers().contains("b"));

        assertFalse(instance.removeClusterMember(cluster, "c"));
        assertEquals(2, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains("a"));
        assertTrue(cluster.getMembers().contains("b"));

        assertTrue(instance.removeClusterMember(cluster, "a"));
        assertEquals(1, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains("b"));

        assertTrue(instance.removeClusterMember(cluster, "b"));
        assertTrue(cluster.getMembers().isEmpty());
    }

}