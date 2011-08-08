/*
 * File:                NearestNeighborKDTreeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceSquaredMetric;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.geometry.KDTree;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Unit tests for NearestNeighborKDTreeTest.
 *
 * @author krdixon
 */
public class NearestNeighborKDTreeTest
    extends NearestNeighborTestHarness
{

    /**
     * Tests for class NearestNeighborKDTreeTest.
     * @param testName Name of the test.
     */
    public NearestNeighborKDTreeTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class NearestNeighborKDTreeTest.
     */
    public void testConstructors()
    {
        NearestNeighborKDTree<Vector, Double> nn =
            new NearestNeighborKDTree<Vector, Double>();
        assertNull( nn.getDivergenceFunction() );
        assertNull( nn.getData() );

        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        KDTree<Vector,Double,InputOutputPair<? extends Vector,Double>> tree =
            new KDTree<Vector, Double, InputOutputPair<? extends Vector, Double>>( POINTS );
        nn = new NearestNeighborKDTree<Vector, Double>( tree, metric );
        assertSame( tree, nn.getData() );
        assertSame( metric, nn.getDivergenceFunction() );

    }

    /**
     * Tests setData
     */
    public void testSetData()
    {

        NearestNeighborKDTree<Vector, Double> nn = this.createInstance(POINTS);

        Collection<InputOutputPair<? extends Vector, Double>> data = nn.getData();
        KDTree<Vector,Double,InputOutputPair<? extends Vector,Double>> tree =
            new KDTree<Vector, Double, InputOutputPair<? extends Vector, Double>>( POINTS );
        nn.setData(null);
        assertNull( nn.getData() );
        nn.setData( tree );
        assertSame( tree, nn.getData() );

    }

    /**
     * Test of setDivergenceFunction method, of class NearestNeighborKDTree.
     */
    @SuppressWarnings("unchecked")
    public void testSetDivergenceFunction_Metric()
    {
        System.out.println("setDivergenceFunction");

        NearestNeighborKDTree<Vector, Double> nn = this.createInstance(POINTS);
        DivergenceFunction<? super Vector,? super Vector> metric =
            new EuclideanDistanceMetric();
        nn.setDivergenceFunction(metric);
        assertSame( metric, nn.getDivergenceFunction() );
        nn.setDivergenceFunction( (Metric<? super Vector>) metric );
        assertSame( metric, nn.getDivergenceFunction() );


        metric = new EuclideanDistanceSquaredMetric();
        try
        {
            nn.setDivergenceFunction(metric);
            fail( "EDS is not a Metric!");
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of evaluate method, of class NearestNeighborExhaustive.
     */
    public void testEvaluateString()
    {
        LinkedList<InputOutputPair<? extends Vector, String>> data =
            new LinkedList<InputOutputPair<? extends Vector, String>>();
        data.add(new DefaultInputOutputPair<Vector, String>(
            new Vector3(1.0, 0.0, 0.0), "a"));
        data.add(new DefaultInputOutputPair<Vector, String>(
            new Vector3(0.0, 2.0, 0.0), "b"));
        data.add(new DefaultInputOutputPair<Vector, String>(
            new Vector3(0.0, 0.0, 3.0), "c"));

        NearestNeighborExhaustive<Vector, String> instance =
            new NearestNeighborExhaustive<Vector, String>(
                CounterEuclidenDistance.INSTANCE, data);
        assertEquals("a", instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
        assertEquals("c", instance.evaluate(new Vector3(0.0, 0.0, 4.0)));
        assertEquals("b", instance.evaluate(new Vector3(0.0, 3.0, 0.0)));

        data = new LinkedList<InputOutputPair<? extends Vector, String>>();
        instance.setData(data);
        assertNull(instance.evaluate(new Vector3(0.0, 0.0, 0.0)));
    }

    @Override
    public NearestNeighborKDTree<Vector, Double> createInstance(
        Collection<? extends InputOutputPair<Vector, Double>> data)
    {
        KDTree<Vector,Double,InputOutputPair<? extends Vector,Double>> kdtree =
            new KDTree<Vector, Double, InputOutputPair<? extends Vector, Double>>( data );
        return new NearestNeighborKDTree<Vector, Double>(
                kdtree, CounterEuclidenDistance.INSTANCE );
    }

    @Override
    public void testLearner()
    {

        NearestNeighborKDTree.Learner<Vector,Double> learner =
            new NearestNeighborKDTree.Learner<Vector, Double>();
        assertNull( learner.getDivergenceFunction() );

        Metric<? super Vectorizable> metric = CounterEuclidenDistance.INSTANCE;
        learner = new NearestNeighborKDTree.Learner<Vector, Double>( metric );
        assertSame( metric, learner.getDivergenceFunction() );

        NearestNeighborKDTree<Vector,Double> nn = learner.learn(POINTS);
        assertNotNull( nn.getDivergenceFunction() );
        assertNotSame( metric, nn.getDivergenceFunction() );
        assertEquals( POINTS.size(), nn.getData().size() );
        assertTrue( nn.getData().containsAll(POINTS ));
    }

}
