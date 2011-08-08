/*
 * File:                NearestNeighborTestHarness.java
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
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for NearestNeighborTestHarness.
 *
 * @author krdixon
 */
public abstract class NearestNeighborTestHarness
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
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
        DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(2,3), 0.0 ),
        DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(5,4), 1.0 ),
        DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(9,6), 2.0 ),
        DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(4,7), 3.0 ),
        DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(8,1), 4.0 ),
        DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(7,2), 5.0 )
    );


    /**
     * Tests for class NearestNeighborTestHarness.
     * @param testName Name of the test.
     */
    public NearestNeighborTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance.
     * @param data Pairs.
     * @return KNN
     */
    public abstract AbstractNearestNeighbor<Vector, Double> createInstance(
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

        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );
        assertEquals( POINTS.size(), nn.getData().size() );

        for( InputOutputPair<? extends Vector,Double> d : nn.getData() )
        {
            boolean found = false;
            for( InputOutputPair<Vector,Double> point : POINTS )
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

        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );

        @SuppressWarnings("unchecked")
        AbstractNearestNeighbor<Vector, Double> clone =
            (AbstractNearestNeighbor<Vector, Double>) nn.clone();
        assertNotNull( clone );
        assertNotSame( nn, clone );
        assertNotSame( nn.getDivergenceFunction(), clone.getDivergenceFunction() );
        assertEquals( nn.getData().size(), clone.getData().size() );
        assertNotSame( nn.getData(), clone.getData() );

    }

    /**
     * Test of getDivergenceFunction method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testGetDivergenceFunction()
    {
        System.out.println( "getDivergenceFunction" );
        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );

        assertNotNull( nn.getDivergenceFunction() );
    }

    /**
     * Test of setDivergenceFunction method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testSetDivergenceFunction()
    {
        System.out.println( "setDivergenceFunction" );
        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );

        assertNotNull( nn.getDivergenceFunction() );

        DivergenceFunction<? super Vector, ? super Vector> foo =
            nn.getDivergenceFunction();
        nn.setDivergenceFunction( null );
        assertNull( nn.getDivergenceFunction() );

        nn.setDivergenceFunction( foo );
        assertSame( foo, nn.getDivergenceFunction() );
    }

    /**
     * Test of getData method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testGetData()
    {
        System.out.println( "getData" );

        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );

        Collection<InputOutputPair<? extends Vector,Double>> data = nn.getData();
        assertNotNull( data );
        assertEquals( POINTS.size(), data.size() );

        for( InputOutputPair<? extends Vector,Double> value : data )
        {
            boolean found = false;
            for( InputOutputPair<Vector,Double> point : POINTS )
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
     * Tests add.
     */
    public void testAdd()
    {
        System.out.println( "add" );

        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );
        InputOutputPair<Vector,Double> pair =
            DefaultInputOutputPair.create( VectorFactory.getDefault().copyValues(0,0), 6.0 );

        int preSize = nn.getData().size();
        assertFalse( nn.getData().contains(pair) );
        nn.add( pair );
        assertEquals( preSize+1, nn.getData().size() );
        boolean found = false;
        for( InputOutputPair<? extends Vector,Double> d : nn.getData() )
        {
            if( pair.getFirst().equals( d.getFirst() ) )
            {
                found = true;
                assertEquals( pair.getSecond(), d.getSecond() );
            }
        }
        assertTrue( found );

        // Add it again.
        nn.add( pair );
        assertEquals( preSize+2, nn.getData().size() );
        found = false;
        for( InputOutputPair<? extends Vector,Double> d : nn.getData() )
        {
            if( pair.getFirst().equals( d.getFirst() ) )
            {
                found = true;
                assertEquals( pair.getSecond(), d.getSecond() );
            }
        }
        assertTrue( found );

    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.KNearestNeighbor.
     */
    public void testEvaluateSimple()
    {
        System.out.println( "evaluate 1NN" );

        AbstractNearestNeighbor<Vector, Double> nn = this.createInstance( POINTS );
        int M = -1;
        for (InputOutputPair<? extends Vector, ? extends Double> pair : nn.getData())
        {
            M = pair.getFirst().getDimensionality();
            assertEquals( pair.getSecond(), nn.evaluate( pair.getFirst() ) );
        }

        try
        {
            nn.evaluate( null );
            fail( "Should have thrown null-pointer exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good!  Properly thrown exception: " + e );
        }

        final double r = TOLERANCE;
        for (InputOutputPair<? extends Vector, ? extends Double> pair : nn.getData())
        {
            Vector small = VectorFactory.getDefault().createUniformRandom( M, -r, r, RANDOM );
            Vector perturb = pair.getFirst().plus( small );
            Double estimate = nn.evaluate( perturb );
            double error = pair.getSecond() - estimate;
            double distance = Math.abs( error );
            assertEquals( 0.0, distance, TOLERANCE );
        }

    }

}
