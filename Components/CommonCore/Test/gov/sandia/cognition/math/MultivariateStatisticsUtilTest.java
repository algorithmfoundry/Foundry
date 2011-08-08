/*
 * File:                MultivariateStatisticsUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class MultivariateStatisticsUtil.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class MultivariateStatisticsUtilTest
    extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random(1);

    /**
     * Tolerance
     */
    final static double EPS = 1e-5;

    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public MultivariateStatisticsUtilTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Test of constructors of class MultivariateStatisticsUtil.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateStatisticsUtil su = new MultivariateStatisticsUtil();
        assertNotNull( su );
    }


    /**
     * Test of computeMean method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeMeanVector()
    {

        System.out.println( "Vector.computeMean" );
        LinkedList<Vector> data = null;
        try
        {
            MultivariateStatisticsUtil.computeMean( data );
            fail( "Should have thrown null-pointer exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        data = new LinkedList<Vector>();
        Vector m1 = MultivariateStatisticsUtil.computeMean( data );
        assertNull( m1 );

        int num = 10;
        int M = 3;
        double r = 1;
        Vector v1 = VectorFactory.getDefault().createUniformRandom( M, -r, r, random );
        data.add( v1 );
        assertEquals( v1, MultivariateStatisticsUtil.computeMean( data ) );
        for (int i = 0; i < num; i++)
        {
            data.add( VectorFactory.getDefault().createUniformRandom( M, -r, r, random ) );
        }

        RingAccumulator<Vector> expected = new RingAccumulator<Vector>( data );
        if (expected.getMean().equals( MultivariateStatisticsUtil.computeMean( data ), 1e-5 ) == false)
        {
            assertEquals( expected.getMean(), MultivariateStatisticsUtil.computeMean( data ) );
        }

    }

    /**
     * Test of computeVariance method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeVarianceVector()
    {
        System.out.println( "Vector.computeVariance" );

        int num = random.nextInt(100) + 10;
        int M = 3;
        double r = 1;
        LinkedList<Vector> data = new LinkedList<Vector>();
        assertNull( MultivariateStatisticsUtil.computeVariance(data) );
        data.add( VectorFactory.getDefault().createUniformRandom( M, -r, r, random ) );
        Matrix C0 = MultivariateStatisticsUtil.computeVariance(data);
        for( int i = 0; i < C0.getNumRows(); i++ )
        {
            for( int j = 0; j < C0.getNumColumns(); j++ )
            {
                assertEquals( 0.0, C0.getElement(i,j) );
            }
        }

        for (int i = 0; i < num; i++)
        {
            data.add( VectorFactory.getDefault().createUniformRandom( M, -r, r, random ) );
        }

        Matrix estimated = MultivariateStatisticsUtil.computeVariance( data );
        assertNotNull( estimated );

    }

    public void testComputeMeanAndCovariance()
    {
        System.out.println( "computeMeanAndCovariance" );

        final int N = 10;
        final int dim = 3;
        ArrayList<Vector> data = new ArrayList<Vector>( N );
        for( int n = 0; n < N; n++ )
        {
            data.add( VectorFactory.getDefault().createUniformRandom(
                dim, -1.0, 1.0, random ) );
        }

        Vector mean = MultivariateStatisticsUtil.computeMean(data);
        Matrix C = MultivariateStatisticsUtil.computeVariance(data);

        Pair<Vector,Matrix> result =
            MultivariateStatisticsUtil.computeMeanAndCovariance(data);
        assertEquals( mean, result.getFirst() );
        assertTrue( C.equals( result.getSecond(), EPS ) );

        while( data.size() > 1 )
        {
            data.remove(0);
        }

        result = MultivariateStatisticsUtil.computeMeanAndCovariance(data);
        assertEquals( CollectionUtil.getFirst(data), result.getFirst() );
        C = result.getSecond();
        for( int i = 0; i < C.getNumRows(); i++ )
        {
            for( int j = 0; j < C.getNumColumns(); j++ )
            {
                assertEquals( 0.0, C.getElement(i, j) );
            }
        }

        data.remove(0);
        assertNull( MultivariateStatisticsUtil.computeMeanAndCovariance(data) );

    }

    public void testWeightedComputeMeanAndCovariance()
    {
        System.out.println( "computeWeightedMeanAndCovariance" );

        final int N = 10;
        final int dim = 3;
        ArrayList<DefaultWeightedValue<? extends Vector>> data =
            new ArrayList<DefaultWeightedValue<? extends Vector>>( N );
        RingAccumulator<Vector> sum = new RingAccumulator<Vector>();
        double weightSum = 0.0;
        for( int n = 0; n < N; n++ )
        {
            double weight = random.nextDouble();
            Vector x = VectorFactory.getDefault().createUniformRandom(
                dim, -1.0, 1.0, random);
            sum.accumulate( x.scale( weight ) );
            weightSum += weight;
            data.add( new DefaultWeightedValue<Vector>( x, weight ) );
        }

        Vector mean = sum.scaleSum( 1.0/weightSum );
        RingAccumulator<Matrix> Csum = new RingAccumulator<Matrix>();
        for( int n = 0; n < N; n++ )
        {
            double weight = data.get(n).getWeight();
            Vector x = data.get(n).getValue();
            Vector delta = x.minus( mean );
            Csum.accumulate( delta.scale(weight).outerProduct(delta) );
        }
        Matrix C = Csum.scaleSum(1.0/weightSum);

        Pair<Vector,Matrix> result =
            MultivariateStatisticsUtil.computeWeightedMeanAndCovariance(data);
        assertEquals( mean, result.getFirst() );
        System.out.println( "C = \n" + C );
        System.out.println( "Chat = \n" + result.getSecond() );
        assertTrue( C.equals( result.getSecond(), EPS ));

        while( data.size() > 1 )
        {
            data.remove(0);
        }

        result = MultivariateStatisticsUtil.computeWeightedMeanAndCovariance(data);
        if( !CollectionUtil.getFirst(data).getValue().equals( result.getFirst(), EPS ) )
        {
            assertEquals( CollectionUtil.getFirst(data).getValue(), result.getFirst() );
        }
        C = result.getSecond();
        for( int i = 0; i < C.getNumRows(); i++ )
        {
            for( int j = 0; j < C.getNumColumns(); j++ )
            {
                assertEquals( 0.0, C.getElement(i, j) );
            }
        }

        data.remove(0);
        assertNull( MultivariateStatisticsUtil.computeWeightedMeanAndCovariance(data) );

    }


}
