/*
 * File:                KNearestNeighborExhaustiveTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.NumberAverager;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

/**
 * Unit tests for KNearestNeighborExhaustiveTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class KNearestNeighborExhaustiveTest
    extends KNearestNeighborTestHarness
{

    /**
     * 
     * @param testName
     */
    public KNearestNeighborExhaustiveTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public KNearestNeighborExhaustive<Vector, Double> createInstance(
        int k,
        Collection<? extends InputOutputPair<Vector, Double>> data)
    {
        return new KNearestNeighborExhaustive<Vector, Double>(
            k, data, CounterEuclidenDistance.INSTANCE, NumberAverager.INSTANCE );
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        KNearestNeighborExhaustive<Vector, Double> knn = new KNearestNeighborExhaustive<Vector, Double>();
        assertEquals( KNearestNeighborExhaustive.DEFAULT_K, knn.getK() );
        assertNull( knn.getAverager() );
        assertNull( knn.getDivergenceFunction() );
        assertEquals( 0, knn.getData().size() );

        int k = RANDOM.nextInt(10) + 1;
        EuclideanDistanceMetric distance = EuclideanDistanceMetric.INSTANCE;
        NumberAverager averager = NumberAverager.INSTANCE;
        knn = new KNearestNeighborExhaustive<Vector, Double>( k, POINTS, distance, averager );
        assertEquals( k, knn.getK() );
        assertSame( distance, knn.getDivergenceFunction() );
        assertSame( averager, knn.getAverager() );

        for( Pair<? extends Vector,Double> d : knn.getData() )
        {
            boolean found = false;
            for( Pair<Vector,Double> point : POINTS )
            {
                if( point.getFirst().equals( d.getFirst() ) )
                {
                    found = true;
                    assertEquals( point.getSecond(), d.getSecond() );
                }
            }

            assertTrue( found );

        }

    }

    /**
     * Test of setData method, of class gov.sandia.isrc.learning.util.function.KNearestNeighborExhaustive.
     */
    public void testSetData()
    {
        System.out.println( "setData" );

        int k = RANDOM.nextInt( 10 ) + 1;
        KNearestNeighborExhaustive<Vector, Double> knn = this.createInstance( k, POINTS );

        assertNotNull( knn.getData() );

        Collection<InputOutputPair<? extends Vector,Double>> data = knn.getData();

        knn.setData( null );
        assertNull( knn.getData() );

        knn.setData( data );
        assertSame( data, knn.getData() );
    }


    /**
     * Tests the learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        KNearestNeighborExhaustive.Learner<Vector,Double> learner =
            new KNearestNeighborExhaustive.Learner<Vector, Double>();

        assertEquals( KNearestNeighborExhaustive.Learner.DEFAULT_K, learner.getK() );
        assertEquals( 0, learner.getData().size() );
        assertNull( learner.getAverager() );
        assertNull( learner.getDivergenceFunction() );
        learner.setK(3);

        KNearestNeighborExhaustive<Vector, Double> knn = this.createInstance( learner.getK(), POINTS );

        learner.setAverager(knn.getAverager());
        learner.setDivergenceFunction(knn.getDivergenceFunction());

        KNearestNeighborExhaustive<Vector,Double> result = learner.learn(POINTS);

        for( Pair<? extends Vector,Double> d : result.getData() )
        {
            boolean found = false;
            for( Pair<Vector,Double> point : POINTS )
            {
                if( point.getFirst().equals( d.getFirst() ) )
                {
                    found = true;
                    assertEquals( point.getSecond(), d.getSecond() );
                }
            }

            assertTrue( found );

        }

        assertEquals( knn.getData().size(), result.getData().size() );

    }

}
