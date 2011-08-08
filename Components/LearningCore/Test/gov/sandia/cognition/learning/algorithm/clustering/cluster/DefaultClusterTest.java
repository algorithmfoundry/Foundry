/*
 * File:                DefaultClusterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import java.util.ArrayList;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DefaultCluster
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DefaultClusterTest
    extends TestCase
{
    /**
     * Creates a new instance of DefaultClusterTest.
     *
     * @param  testName The test name.
     */
    public DefaultClusterTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the DefaultCluster class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testDefaultCluster()
    {
        // Create a cluster with the default constructor.
        DefaultCluster<Object> cluster1 = new DefaultCluster<Object>();
        assertEquals(-1, cluster1.getIndex());
        assertNotNull(cluster1.getMembers());
        assertEquals(0, cluster1.getMembers().size());
        
        // Test setting the index.
        cluster1.setIndex(47);
        assertEquals(47, cluster1.getIndex());
        
        // Test modifying the members.
        cluster1.getMembers().add(new Object());
        assertEquals(1, cluster1.getMembers().size());
        
        // Create a cluster with an index.
        DefaultCluster<Object> cluster2 = new DefaultCluster<Object>(2);
        assertEquals(2, cluster2.getIndex());
        assertNotNull(cluster2.getMembers());
        assertEquals(0, cluster2.getMembers().size());
        
        // Create a list of members.
        ArrayList<Object> members = new ArrayList<Object>();
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        members.add(o1);
        members.add(o2);
        
        // Create a cluster with a given set of members.
        DefaultCluster<Object> cluster3 = new DefaultCluster<Object>(members);
        assertEquals(-1, cluster3.getIndex());
        assertNotNull(cluster3.getMembers());
        assertNotSame(members, cluster3.getMembers());
        assertEquals(2, cluster3.getMembers().size());
        assertTrue(cluster3.getMembers().contains(o1));
        assertTrue(cluster3.getMembers().contains(o2));
        
        // Add a member to the member list. It should not effect the cluster
        // membership list.
        members.add(o3);
        assertEquals(2, cluster3.getMembers().size());
        assertFalse(cluster3.getMembers().contains(o3));
        
        // Create a cluster with an index and members.
        DefaultCluster<Object> cluster4 = 
            new DefaultCluster<Object>(4, members);
        assertEquals(4, cluster4.getIndex());
        assertNotSame(members, cluster4.getMembers());
        
        // Create a cluster with a null set of members.
        DefaultCluster<Object> cluster5 = new DefaultCluster<Object>(null);
        assertEquals(-1, cluster5.getIndex());
        assertNotNull(cluster5.getMembers());
        assertEquals(0, cluster5.getMembers().size());
        
        // Create a cluster with an index and a null set of members.
        DefaultCluster<Object> cluster6 = new DefaultCluster<Object>(6, null);
        assertEquals(6, cluster6.getIndex());
        assertNotNull(cluster6.getMembers());
        assertEquals(0, cluster6.getMembers().size());
    }
}
