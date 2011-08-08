/*
 * File:                GaussianClusterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     GaussianCluster
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class GaussianClusterTest
    extends TestCase
{
    public GaussianClusterTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        GaussianCluster instance = new GaussianCluster();
        assertEquals(DefaultCluster.DEFAULT_INDEX, instance.getIndex());
        assertTrue(instance.getMembers().isEmpty());
        assertNull(instance.getGaussian());
        
        MultivariateGaussian.PDF gaussian = new MultivariateGaussian.PDF(
            new Vector3(), MatrixFactory.getDefault().createIdentity(3, 3));
        instance = new GaussianCluster(gaussian);
        assertEquals(DefaultCluster.DEFAULT_INDEX, instance.getIndex());
        assertTrue(instance.getMembers().isEmpty());
        assertSame(gaussian, instance.getGaussian());
        
        Vector3 member = new Vector3(1.0, 2.0, 3.0);
        ArrayList<Vector> members = new ArrayList<Vector>();
        members.add(member);
        
        
        instance = new GaussianCluster(members, gaussian);
        assertEquals(DefaultCluster.DEFAULT_INDEX, instance.getIndex());
        assertNotSame(members, instance.getMembers());
        assertEquals(1, instance.getMembers().size());
        assertTrue(instance.getMembers().contains(member));
        assertSame(gaussian, instance.getGaussian());
        
        int index = 47;
        instance = new GaussianCluster(index, members, gaussian);
        assertEquals(index, instance.getIndex());
        assertNotSame(members, instance.getMembers());
        assertEquals(1, instance.getMembers().size());
        assertTrue(instance.getMembers().contains(member));
        assertSame(gaussian, instance.getGaussian());
    }
    
    /**
     * Test of getGaussian method, of class gov.sandia.cognition.learning.clustering.cluster.GaussianCluster.
     */
    public void testGetGaussian()
    {
        this.testSetGaussian();
    }

    /**
     * Test of setGaussian method, of class gov.sandia.cognition.learning.clustering.cluster.GaussianCluster.
     */
    public void testSetGaussian()
    {
        GaussianCluster instance = new GaussianCluster();
        assertNull(instance.getGaussian());
        
        MultivariateGaussian.PDF gaussian = new MultivariateGaussian.PDF(
            new Vector3(), MatrixFactory.getDefault().createIdentity(3, 3));
        instance.setGaussian(gaussian);
        assertSame(gaussian, instance.getGaussian());
        
        instance.setGaussian(null);
        assertNull(instance.getGaussian());
        
    }
}
