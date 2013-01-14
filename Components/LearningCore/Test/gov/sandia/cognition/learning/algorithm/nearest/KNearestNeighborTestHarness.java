/*
 * File:                KNearestNeighborTestHarness.java
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

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.Summarizer;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for KNearestNeighborTestHarness
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public abstract class KNearestNeighborTestHarness
    extends TestCase
{

    /** The RANDOM number generator for the tests. */
    public final Random RANDOM = new Random(1);

    /**
     * Tolerance for tests
     */
    public final double TOLERANCE = 1e-5;

    public static class CounterEuclidenDistance
        extends EuclideanDistanceMetric
    {

        public int evaluations = 0;

        @Override
        public double evaluate(Vectorizable first,
            Vectorizable second)
        {
            this.evaluations++;
            return super.evaluate(first, second);
        }

        

    }

    /**
     * Example from http://en.wikipedia.org/wiki/Kd-tree#Construction
     */
    @SuppressWarnings("unchecked")
    public static List<DefaultInputOutputPair<Vector,Double>> POINTS = Arrays.asList(
        new DefaultInputOutputPair<Vector, Double>(VectorFactory.getDefault().copyValues(2,3), 0.0),
        new DefaultInputOutputPair<Vector, Double>(VectorFactory.getDefault().copyValues(5,4), 1.0),
        new DefaultInputOutputPair<Vector, Double>(VectorFactory.getDefault().copyValues(9,6), 2.0),
        new DefaultInputOutputPair<Vector, Double>(VectorFactory.getDefault().copyValues(4,7), 3.0),
        new DefaultInputOutputPair<Vector, Double>(VectorFactory.getDefault().copyValues(8,1), 4.0),
        new DefaultInputOutputPair<Vector, Double>(VectorFactory.getDefault().copyValues(7,2), 5.0)
    );

    /**
     * 
     * @param testName
     */
    public KNearestNeighborTestHarness(
        String testName )
    {
        super( testName );
    }

    /**
     * Creates an instance.
     * @param k K
     * @param data Pairs.
     * @return KNN
     */
    public abstract AbstractKNearestNeighbor<Vector, Double> createInstance(
        int k,
        Collection<? extends InputOutputPair<Vector,Double>> data );

    /**
     * Test constructors
     */
    public abstract void testConstructors();

    /**
     * Test learner
     */
    public abstract void testLearner();

    /**
     * Tests to make sure you implemented the createInstance method properly.
     */
    public void testCreateInstance()
    {
        System.out.println( "createInstance" );

        int k = RANDOM.nextInt(10) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );
        assertEquals( k, knn.getK() );
        assertEquals( POINTS.size(), knn.getData().size() );

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
     * Test clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( 1, POINTS );

        AbstractKNearestNeighbor<Vector, Double> clone = knn.clone();
        assertNotNull( clone );
        assertNotSame( knn, clone );
        assertEquals( knn.getK(), clone.getK() );
        assertNotSame( knn.getAverager(), clone.getAverager() );
        assertNotSame( knn.getDivergenceFunction(), clone.getDivergenceFunction() );
        assertEquals( knn.getData().size(), clone.getData().size() );
        assertNotSame( knn.getData(), clone.getData() );

    }


    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testEvaluateSimple()
    {
        System.out.println( "evaluate 1NN" );

        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( 1, POINTS );
        int M = -1;
        for (Pair<? extends Vector, ? extends Double> pair : knn.getData())
        {
            M = pair.getFirst().getDimensionality();
            assertEquals( pair.getSecond(), knn.evaluate( pair.getFirst() ) );
        }

        try
        {
            knn.evaluate( null );
            fail( "Should have thrown null-pointer exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good!  Properly thrown exception: " + e );
        }

        final double r = TOLERANCE;
        for (Pair<? extends Vector, ? extends Double> pair : knn.getData())
        {
            Vector small = VectorFactory.getDefault().createUniformRandom( M, -r, r, RANDOM );
            Vector perturb = pair.getFirst().plus( small );
            Double estimate = knn.evaluate( perturb );
            double error = pair.getSecond() - estimate;
            double distance = Math.abs( error );
            assertEquals( 0.0, distance, TOLERANCE );
        }

    }

    /**
     * Evaluate many
     */
    public void testEvaluateMany()
    {
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( 1, POINTS );
        final int M = CollectionUtil.getFirst(knn.getData()).getFirst().getDimensionality();

        // A "k" larger than there are datapoint, should just return the
        // average value
        knn.setK( knn.getData().size() + 1 );
        Collection<? extends Pair<? extends Vector, Double>> data = knn.getData();
        ArrayList<Double> outputs = new ArrayList<Double>( data.size() );
        for( Pair<? extends Vector,Double> pair : data )
        {
            outputs.add(pair.getSecond());
        }

        double expected = UnivariateStatisticsUtil.computeMean(outputs);
        Vector input = VectorFactory.getDefault().createUniformRandom( M, -10.0, 10.0, RANDOM );
        assertEquals( expected, knn.evaluate( input ), TOLERANCE );

    }


    /**
     * Evaluates 2NN against known values.
     */
    public void testEvaluate2NN()
    {

        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( 2, POINTS );

        double value = knn.evaluate( POINTS.get(5).getFirst() );
        assertEquals( 4.5, value );

        value = knn.evaluate( POINTS.get(0).getFirst() );
        assertEquals( 0.5, value );

    }

    /**
     * Test of getDivergenceFunction method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testGetDivergenceFunction()
    {
        System.out.println( "getDivergenceFunction" );
        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );

        assertNotNull( knn.getDivergenceFunction() );
    }

    /**
     * Test of setDivergenceFunction method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testSetDivergenceFunction()
    {
        System.out.println( "setDivergenceFunction" );
        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );

        assertNotNull( knn.getDivergenceFunction() );

        DivergenceFunction<? super Vector, ? super Vector> foo =
            knn.getDivergenceFunction();
        knn.setDivergenceFunction( null );
        assertNull( knn.getDivergenceFunction() );

        knn.setDivergenceFunction( foo );
        assertSame( foo, knn.getDivergenceFunction() );
    }

    /**
     * Test of getAverager method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testGetAverager()
    {
        System.out.println( "getAverager" );

        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );

        assertNotNull( knn.getAverager() );

    }

    /**
     * Test of setAverager method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testSetAverager()
    {
        System.out.println( "setAverager" );

        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );

        assertNotNull( knn.getAverager() );

        Summarizer<? super Double, ? extends Double> avg = knn.getAverager();

        knn.setAverager( null );
        assertNull( knn.getAverager() );

        knn.setAverager( avg );
        assertSame( avg, knn.getAverager() );

    }

    /**
     * Test of getData method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testGetData()
    {
        System.out.println( "getData" );

        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );

        Collection<? extends Pair<? extends Vector,Double>> data = knn.getData();
        assertNotNull( data );
        assertEquals( POINTS.size(), data.size() );

        for( Pair<? extends Vector,Double> value : data )
        {
            boolean found = false;
            for( Pair<Vector,Double> point : POINTS )
            {
                if( point.getFirst().equals( value.getFirst() ) )
                {
                    found = true;
                    assertEquals( point.getSecond(), value.getSecond() );
                }
            }

            assertTrue( found );

        }

    }

    /**
     * Test of getK method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testGetK()
    {
        System.out.println( "getK" );

        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );
        assertEquals( k, knn.getK() );
    }

    /**
     * Test of setK method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testSetK()
    {
        System.out.println( "setK" );

        int k = RANDOM.nextInt( 10 ) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );

        assertEquals( k, knn.getK() );

        k++;
        assertEquals( k, knn.getK() + 1 );

        knn.setK( k );
        assertEquals( k, knn.getK() );

        try
        {
            knn.setK( 0 );
            fail( "Number of neighbors must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Tests add.
     */
    public void testAdd()
    {
        System.out.println( "add" );

        int k = RANDOM.nextInt(10) + 1;
        AbstractKNearestNeighbor<Vector, Double> knn = this.createInstance( k, POINTS );
        InputOutputPair<Vector, Double> pair =
            new DefaultInputOutputPair<Vector,Double>(VectorFactory.getDefault().copyValues(0,0), 6.0);

        int preSize = knn.getData().size();
        assertFalse( knn.getData().contains(pair) );
        knn.add( pair );
        assertEquals( preSize+1, knn.getData().size() );
        boolean found = false;
        for( Pair<? extends Vector,Double> d : knn.getData() )
        {
            if( pair.getFirst().equals( d.getFirst() ) )
            {
                found = true;
                assertEquals( pair.getSecond(), d.getSecond() );
            }
        }
        assertTrue( found );

        // Add it again.
        knn.add( pair );
        assertEquals( preSize+2, knn.getData().size() );
        found = false;
        for( Pair<? extends Vector,Double> d : knn.getData() )
        {
            if( pair.getFirst().equals( d.getFirst() ) )
            {
                found = true;
                assertEquals( pair.getSecond(), d.getSecond() );
            }
        }
        assertTrue( found );

    }


}
