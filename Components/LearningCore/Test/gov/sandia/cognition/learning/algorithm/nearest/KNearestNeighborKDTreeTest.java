/*
 * File:                KNearestNeighborKDTreeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 5, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.geometry.KDTree;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.NumberAverager;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

/**
 * Unit tests for KNearestNeighborKDTreeTest.
 *
 * @author krdixon
 */
public class KNearestNeighborKDTreeTest
    extends KNearestNeighborTestHarness
{

    /**
     * Tests for class KNearestNeighborKDTreeTest.
     * @param testName Name of the test.
     */
    public KNearestNeighborKDTreeTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public KNearestNeighborKDTree<Vector, Double> createInstance(
        int k,
        Collection<? extends InputOutputPair<Vector, Double>> data)
    {

        KDTree<Vector,Double,InputOutputPair<? extends Vector,Double>> tree =
            new KDTree<Vector, Double, InputOutputPair<? extends Vector, Double>>( POINTS );
        return new KNearestNeighborKDTree<Vector, Double>(
            k, tree, new CounterEuclidenDistance(), NumberAverager.INSTANCE );
    }

    /**
     * Tests the constructors of class KNearestNeighborKDTreeTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        KNearestNeighborKDTree<Vector, Double> knn = new KNearestNeighborKDTree<Vector, Double>();
        assertEquals( KNearestNeighborKDTree.DEFAULT_K, knn.getK() );
        assertNull( knn.getAverager() );
        assertNull( knn.getDivergenceFunction() );
        assertNull( knn.getData() );

        int k = RANDOM.nextInt( 10 ) + 1;
        EuclideanDistanceMetric metric = EuclideanDistanceMetric.INSTANCE;
        NumberAverager averager = NumberAverager.INSTANCE;
        KDTree<Vector,Double,InputOutputPair<? extends Vector,Double>> tree =
            new KDTree<Vector, Double, InputOutputPair<? extends Vector, Double>>( POINTS );
        knn = new KNearestNeighborKDTree<Vector, Double>( k, tree, metric, averager );
        assertEquals( k, knn.getK() );
        assertSame( tree, knn.getData() );
        assertSame( metric, knn.getDivergenceFunction() );
        assertSame( averager, knn.getAverager() );

    }

    /**
     * Tests setData
     */
    public void testSetData()
    {

        KNearestNeighborKDTree<Vector, Double> knn =
            this.createInstance(1,POINTS);

        Collection<InputOutputPair<? extends Vector, Double>> data = knn.getData();
        KDTree<Vector,Double,InputOutputPair<? extends Vector,Double>> tree =
            new KDTree<Vector, Double, InputOutputPair<? extends Vector, Double>>( POINTS );
        knn.setData(null);
        assertNull( knn.getData() );
        knn.setData( tree );
        assertSame( tree, knn.getData() );

    }

    /**
     * setMetric
     */
    public void testSetMetric()
    {
        System.out.println( "setMetric" );

        KNearestNeighborKDTree<Vector, Double> knn =
            this.createInstance(1,POINTS);

        Metric<? super Vector> metric = knn.getDivergenceFunction();
        knn.setDivergenceFunction(null);
        assertNull( knn.getDivergenceFunction() );
        knn.setDivergenceFunction(metric);
        assertSame( metric, knn.getDivergenceFunction() );

    }

    @Override
    public void testLearner()
    {
        System.out.println( "Learner" );

        KNearestNeighborKDTree.Learner<Vector,Double> learner =
            new KNearestNeighborKDTree.Learner<Vector, Double>();
        assertEquals( KNearestNeighborKDTree.DEFAULT_K, learner.getK() );
        assertNull( learner.getAverager() );
        assertSame( EuclideanDistanceMetric.INSTANCE, learner.getDivergenceFunction() );

        int k = RANDOM.nextInt(10) + 1;
        learner.setK(k);
        learner.setAverager( NumberAverager.INSTANCE );

        KNearestNeighborKDTree<Vector,Double> knn = learner.learn(POINTS);
        assertEquals( k, knn.getK() );
        assertNotSame( learner.getAverager(), knn.getAverager() );
        assertNotNull( knn.getAverager() );
        assertNotSame( learner.getDivergenceFunction(), knn.getDivergenceFunction() );
        assertNotNull( knn.getDivergenceFunction() );
        assertEquals( POINTS.size(), knn.getData().size() );
        assertTrue( knn.getData().containsAll(POINTS) );

    }

}
