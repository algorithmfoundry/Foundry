/*
 * File:                MedoidClusterCreatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: MedoidClusterCreator
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class MedoidClusterCreatorTest
    extends TestCase
{
    public MedoidClusterCreatorTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        MedoidClusterCreator<Vector> instance = 
            new MedoidClusterCreator<Vector>();
        assertNull(instance.getDivergenceFunction());
        
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        instance = new MedoidClusterCreator<Vector>(metric);
        assertSame(metric, instance.getDivergenceFunction());
    }

    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        MedoidClusterCreator<?> instance = new MedoidClusterCreator<Vector>();

        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }


    /**
     * Test of createCluster method, of class gov.sandia.cognition.learning.clustering.cluster.creator.MedoidClusterCreator.
     */
    public void testCreateCluster()
    {
        MedoidClusterCreator<Vector> instance = 
            new MedoidClusterCreator<Vector>(EuclideanDistanceMetric.INSTANCE);
        
        Vector[] data1 = new Vector[]
        {
            new Vector2(-2.341500, 3.696800),
            new Vector2(-1.109200, 3.111700),
            new Vector2(-1.566900, 1.835100),
            new Vector2(-2.658500, 0.664900),
            new Vector2(-4.031700, 2.845700),
            new Vector2(-3.081000, 2.101100),
            new Vector2(-1.144400, 0.505300),
        };
        
        Vector medoid1 = data1[2];
        
        Collection<Vector> members = Arrays.asList(data1);
        CentroidCluster<Vector> result = instance.createCluster(members);
        
        assertNotNull(result);
        assertEquals(medoid1, result.getCentroid());
        assertEquals(members.size(), result.getMembers().size());
        
        members = new ArrayList<Vector>();
        result = instance.createCluster(members);
        assertNotNull(result);
        assertNull(result.getCentroid());
        assertSame(members.size(), result.getMembers().size());
        
        members.add(medoid1);
        result = instance.createCluster(members);
        
        assertNotNull(result);
        assertEquals(medoid1, result.getCentroid());
        assertEquals(members.size(), result.getMembers().size());
        
        boolean exceptionThrown = false;
        try
        {
            result = instance.createCluster(null);
        }
        catch ( NullPointerException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of getDivergenceFunction method, of class gov.sandia.cognition.learning.clustering.cluster.creator.MedoidClusterCreator.
     */
    public void testGetDivergenceFunction()
    {
        this.testSetDivergenceFunction();
    }

    /**
     * Test of setDivergenceFunction method, of class gov.sandia.cognition.learning.clustering.cluster.creator.MedoidClusterCreator.
     */
    public void testSetDivergenceFunction()
    {
        MedoidClusterCreator<Vector> instance = 
            new MedoidClusterCreator<Vector>();
        assertNull(instance.getDivergenceFunction());
        
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        instance.setDivergenceFunction(metric);
        assertSame(metric, instance.getDivergenceFunction());
        
        instance.setDivergenceFunction(null);
        assertNull(instance.getDivergenceFunction());
    }
}
