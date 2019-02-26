/*
 * File:                OptimizedAffinityPropagationTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceSquaredMetric;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     OptimizedAffinityPropagation
 *
 * @author Marco Pezzulla
 * @since  2.0
 */
public class OptimizedAffinityPropagationTest
    extends TestCase
{

    public OptimizedAffinityPropagationTest(
        String testName )
    {
        super( testName );
    }

    public void testConstructors()
    {
        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<>();
        assertNull( instance.getDivergence() );
        assertEquals( OptimizedAffinityPropagation.DEFAULT_SELF_DIVERGENCE,
            instance.getSelfDivergence() );
        assertEquals( OptimizedAffinityPropagation.DEFAULT_DAMPING_FACTOR,
            instance.getDampingFactor() );
        assertEquals( OptimizedAffinityPropagation.DEFAULT_MAX_ITERATIONS,
            instance.getMaxIterations() );

        EuclideanDistanceSquaredMetric metric =
            EuclideanDistanceSquaredMetric.INSTANCE;
        double selfDivergence = 4.7;
        instance = new OptimizedAffinityPropagation<>(metric, selfDivergence);
        assertSame( metric, instance.getDivergence() );
        assertEquals( selfDivergence, instance.getSelfDivergence() );
        assertEquals( OptimizedAffinityPropagation.DEFAULT_DAMPING_FACTOR,
            instance.getDampingFactor() );
        assertEquals( OptimizedAffinityPropagation.DEFAULT_MAX_ITERATIONS,
            instance.getMaxIterations() );

        double dampingFactor = 0.47;
        instance = new OptimizedAffinityPropagation<>(metric,
            selfDivergence, dampingFactor);
        assertSame( metric, instance.getDivergence() );
        assertEquals( selfDivergence, instance.getSelfDivergence() );
        assertEquals( dampingFactor, instance.getDampingFactor() );
        assertEquals( OptimizedAffinityPropagation.DEFAULT_MAX_ITERATIONS,
            instance.getMaxIterations() );

        int maxIterations = 4774;
        instance = new OptimizedAffinityPropagation<>(metric,
            selfDivergence, dampingFactor, maxIterations);
        assertSame( metric, instance.getDivergence() );
        assertEquals( selfDivergence, instance.getSelfDivergence() );
        assertEquals( dampingFactor, instance.getDampingFactor() );
        assertEquals( maxIterations, instance.getMaxIterations() );
    }

    public void testLearn()
    {
        Vector[] data = new Vector[]{
            new Vector2( -2.341500, 3.696800 ),
            new Vector2( -1.109200, 3.111700 ),
            new Vector2( -1.566900, 1.835100 ),
            new Vector2( -2.658500, 0.664900 ),
            new Vector2( -4.031700, 2.845700 ),
            new Vector2( -3.081000, 2.101100 ),
            new Vector2( 2.588000, 1.781900 ),
            new Vector2( 3.292300, 3.058500 ),
            new Vector2( 4.031700, 1.622300 ),
            new Vector2( 3.081000, -0.611700 ),
            new Vector2( 0.264100, 0.398900 ),
            new Vector2( 1.320400, 2.207400 ),
            new Vector2( 0.193700, 3.643600 ),
            new Vector2( 1.954200, -0.505300 ),
            new Vector2( 1.637300, 1.409600 ),
            new Vector2( -0.123200, -1.516000 ),
            new Vector2( -1.355600, -3.058500 ),
            new Vector2( 0.017600, -4.016000 ),
            new Vector2( 1.003500, -3.590400 ),
            new Vector2( 0.017600, -2.420200 ),
            new Vector2( -1.531700, -0.930900 ),
            new Vector2( -1.144400, 0.505300 ),
            new Vector2( 0.616200, -1.516000 ),
            new Vector2( 1.707700, -2.207400 ),
            new Vector2( 2.095100, 3.430900 )
        };

        ArrayList<Vector> exemplars = new ArrayList<Vector>();
        exemplars.add( data[2] );
        exemplars.add( data[6] );
        exemplars.add( data[19] );

        int[] membershipCounts = new int[]{9, 8, 8};
        double selfDivergence = 15.561256;

        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<Vectorizable>(
            EuclideanDistanceSquaredMetric.INSTANCE, selfDivergence,
            0.5, 100 );

        Collection<CentroidCluster<Vectorizable>> clusters =
            instance.learn( Arrays.asList( data ) );

        assertEquals( 3, clusters.size() );

        for (CentroidCluster<Vectorizable> cluster : clusters)
        {
            int index = exemplars.indexOf( cluster.getCentroid() );
            assertTrue( index >= 0 );
            assertEquals( membershipCounts[index], cluster.getMembers().size() );
        }
    }

    /**
     * Test of getLearned method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testGetResult()
    {
        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<Vectorizable>();
        assertNull( instance.getResult() );
    }

    /**
     * Test of getDivergence method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testGetDivergence()
    {
        this.testSetDivergence();
    }

    /**
     * Test of setDivergence method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testSetDivergence()
    {
        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<Vectorizable>();
        assertNull( instance.getDivergence() );

        DivergenceFunction<Vectorizable,Vectorizable> metric =
            EuclideanDistanceSquaredMetric.INSTANCE;
        instance.setDivergence( metric );
        assertSame( metric, instance.getDivergence() );

        metric = EuclideanDistanceMetric.INSTANCE;
        instance.setDivergence( metric );
        assertSame( metric, instance.getDivergence() );

        instance.setDivergence( null );
        assertNull( instance.getDivergence() );
    }

    /**
     * Test of getDampingFactor method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testGetDampingFactor()
    {
        this.testSetDampingFactor();
    }

    /**
     * Test of setDampingFactor method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testSetDampingFactor()
    {
        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<Vectorizable>();
        assertEquals( OptimizedAffinityPropagation.DEFAULT_DAMPING_FACTOR,
            instance.getDampingFactor() );

        double dampingFactor = Math.random();
        instance.setDampingFactor( dampingFactor );
        assertEquals( dampingFactor, instance.getDampingFactor() );

        dampingFactor = 0.0;
        instance.setDampingFactor( dampingFactor );
        assertEquals( dampingFactor, instance.getDampingFactor() );

        dampingFactor = 1.0;
        instance.setDampingFactor( dampingFactor );
        assertEquals( dampingFactor, instance.getDampingFactor() );

        boolean exceptionThrown = false;
        try
        {
            instance.setDampingFactor( -1.0 );
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue( exceptionThrown );
        }

        exceptionThrown = false;
        try
        {
            instance.setDampingFactor( 2.0 );
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue( exceptionThrown );
        }
    }

    /**
     * Test of getSelfDivergence method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testGetSelfDivergence()
    {
        this.testSetSelfDivergence();
    }

    /**
     * Test of setSelfDivergence method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testSetSelfDivergence()
    {
        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<Vectorizable>();
        assertEquals( OptimizedAffinityPropagation.DEFAULT_SELF_DIVERGENCE,
            instance.getSelfDivergence() );

        double selfDivergence = Math.random();
        instance.setSelfDivergence( selfDivergence );
        assertEquals( selfDivergence, instance.getSelfDivergence() );

        selfDivergence = 0.0;
        instance.setSelfDivergence( selfDivergence );
        assertEquals( selfDivergence, instance.getSelfDivergence() );

        selfDivergence = 1.0;
        instance.setSelfDivergence( selfDivergence );
        assertEquals( selfDivergence, instance.getSelfDivergence() );
    }

    /**
     * Test of getChangedCount method, of class gov.sandia.cognition.learning.clustering.AffinityPropagation.
     */
    public void testGetChangedCount()
    {
        OptimizedAffinityPropagation<Vectorizable> instance =
            new OptimizedAffinityPropagation<Vectorizable>();
        assertEquals( 0, instance.getChangedCount() );
    }

}
