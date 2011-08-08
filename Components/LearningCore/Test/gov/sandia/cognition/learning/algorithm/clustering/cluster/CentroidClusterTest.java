/*
 * File:                CentroidClusterTest.java
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
 *     CentroidCluster
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CentroidClusterTest
    extends TestCase
{
    /**
     * Creates a new instance of CentroidClusterTest.
     *
     * @param  testName The test name.
     */
    public CentroidClusterTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the CentroidCluster class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCentroidCluster()
    {
        // Create a cluster to start with no centroid.
        CentroidCluster<Object> cluster1 = new CentroidCluster<Object>();
        assertEquals(-1, cluster1.getIndex());
        assertNull(cluster1.getCentroid());
        
        // Try setting the centroid.
        Object c1 = new Object();
        Object c2 = new Object();
        
        // Create a cluster with just a centroid.
        CentroidCluster<Object> cluster2 = new CentroidCluster<Object>(c1);
        assertEquals(-1, cluster2.getIndex());
        assertSame(c1, cluster2.getCentroid());
        cluster2.setCentroid(c2);
        assertSame(c2, cluster2.getCentroid());
        
        // Create a cluster with an index and a centroid.
        CentroidCluster<Object> cluster3 = new CentroidCluster<Object>(3, c1);
        assertEquals(3, cluster3.getIndex());
        assertSame(c1, cluster3.getCentroid());
        
        // Create a list of cluster members to use.
        Object o1 = new Object();
        ArrayList<Object> members = new ArrayList<Object>();
        members.add(o1);
        
        // Create a cluster with a centroid and members.
        CentroidCluster<Object> cluster4 = 
            new CentroidCluster<Object>(c1, members);
        assertEquals(-1, cluster4.getIndex());
        assertSame(c1, cluster4.getCentroid());
        assertNotNull(cluster4.getMembers());
        assertNotSame(members, cluster4.getMembers());
        assertEquals(1, cluster4.getMembers().size());
        assertTrue(cluster4.getMembers().contains(o1));
        
        // Create a cluster with an index, centroid, and members.
        CentroidCluster<Object> cluster5 = 
            new CentroidCluster<Object>(5, c1, members);
        assertEquals(5, cluster5.getIndex());
        assertSame(c1, cluster5.getCentroid());
        assertNotNull(cluster5.getMembers());
        assertNotSame(members, cluster5.getMembers());
        assertEquals(1, cluster5.getMembers().size());
        assertTrue(cluster5.getMembers().contains(o1));
    }
}
