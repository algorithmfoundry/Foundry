/*
 * File:                VectorMeanCentroidClusterCreatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     VectorMeanCentroidClusterCreator
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class VectorMeanCentroidClusterCreatorTest
    extends TestCase
{
    /**
     * Creates a new instance of VectorMeanCentroidClusterCreatorTest.
     *
     * @param  testName The test name.
     */
    public VectorMeanCentroidClusterCreatorTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the VectorMeanCentroidClusterCreator class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testVectorMeanCentroidClusterCreator()
    {
        // Get the cluster creator's instance.
        VectorMeanCentroidClusterCreator creator = 
            VectorMeanCentroidClusterCreator.INSTANCE;
        
        // Create the 
        ArrayList<Vector> members = new ArrayList<Vector>();
        
        CentroidCluster<Vector> cluster = null;
        
        cluster = creator.createCluster(members);
        
        assertNull(cluster);
        
        Vector2 v1 = new Vector2(1.0, 1.0);
        
        members.add(v1);
        
        cluster = creator.createCluster(members);
        assertNotNull(cluster);
        assertNotNull(cluster.getCentroid());
        assertEquals(v1, cluster.getCentroid());
        assertNotNull(cluster.getMembers());
        assertEquals(1, cluster.getMembers().size());
        assertTrue(cluster.getMembers().contains(v1));
        
        Vector2 v2 = new Vector2(3.0, 3.0);
        
        members.add(v2);
        
        cluster = creator.createCluster(members);
        assertNotNull(cluster);
        assertNotNull(cluster.getCentroid());
        assertEquals(new Vector2(2.0, 2.0), cluster.getCentroid());
        
        
        // Test to make sure exceptions are thrown properly.
        boolean exceptionThrown = false;
        try
        {
            creator.createCluster(null);
        }
        catch ( NullPointerException npe )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}
