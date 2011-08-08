/*
 * File:                UnivariateStatisticsUtilTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;


/**
 *
 * @author Kevin R. Dixon
 */
public class UnivariateStatisticsUtilTest
    extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random(1);

    /**
     * Tolerance
     */
    final static double EPS = 1e-5;

    /**
     * 
     * @param testName
     */
    public UnivariateStatisticsUtilTest(
        String testName )
    {
        super( testName );
    }


    /**
     * Test of constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        UnivariateStatisticsUtil su = new UnivariateStatisticsUtil();
        assertNotNull( su );
    }

    /**
     * Test of computeMean method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeMean()
    {
        System.out.println( "Double.computeMean" );

        Collection<Double> data = new LinkedList<Double>();
        int num = random.nextInt(100) + 10;
        double sum = 0.0;
        for (int n = 0; n < num; n++)
        {
            double value = random.nextGaussian();
            data.add( value );
            sum += value;
        }

        assertEquals( sum / num, UnivariateStatisticsUtil.computeMean( data ) );

        assertEquals( 0.0, UnivariateStatisticsUtil.computeMean( new LinkedList<Double>() ) );
    }

    /**
     * Test of computeVariance method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeVariance()
    {
        System.out.println( "computeVariance" );

        assertEquals( 0.0, UnivariateStatisticsUtil.computeVariance( new LinkedList<Double>() ) );


        Collection<Double> data = new LinkedList<Double>();
        int num = random.nextInt(100) + 10;
        double sum = 0.0;
        for (int n = 0; n < num; n++)
        {
            double value = random.nextGaussian();
            data.add( value );
            sum += value;
        }
        double mean = sum / num;

        sum = 0.0;
        for (double value : data)
        {
            double delta = value - mean;
            sum += delta * delta;
        }

        double variance = sum / (num - 1);

        assertEquals( variance, UnivariateStatisticsUtil.computeVariance( data ), EPS );
        assertEquals( variance, UnivariateStatisticsUtil.computeVariance( data, mean ), EPS );
        assertEquals( UnivariateStatisticsUtil.computeVariance( data ),
            UnivariateStatisticsUtil.computeVariance( data, mean ), EPS );

    }

    /**
     * Test of computeSumSquaredDifference method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeSumSquaredDifference()
    {
        System.out.println( "computeSumSquaredDifference" );

        Collection<Double> data = new LinkedList<Double>();
        double target = random.nextDouble();
        int num = random.nextInt(100) + 10;
        double sum = 0.0;
        for (int n = 0; n < num; n++)
        {
            double value = random.nextGaussian();
            data.add( value );
            double delta = value - target;
            sum += delta * delta;
        }

        assertEquals( sum, UnivariateStatisticsUtil.computeSumSquaredDifference( data, target ) );
    }

    /**
     * Test of computeRootMeanSquaredError method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeRootMeanSquaredError()
    {
        System.out.println( "computeRootMeanSquaredError" );

        Collection<Double> data = new LinkedList<Double>();
        assertEquals( 0.0, UnivariateStatisticsUtil.computeRootMeanSquaredError(data) );
        double target = random.nextDouble();
        int num = random.nextInt(100) + 10;
        double sum = 0.0;
        for (int n = 0; n < num; n++)
        {
            double value = random.nextGaussian();
            data.add( value );
            double delta = value - target;
            sum += delta * delta;
        }

        double mean = sum / num;
        double rms = Math.sqrt( mean );

        assertEquals( rms, UnivariateStatisticsUtil.computeRootMeanSquaredError( data, target ), EPS );

    }

    /**
     * Test of computeSum method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeSum()
    {
        System.out.println( "computeSum" );

        Collection<Double> data = new LinkedList<Double>();
        int num = random.nextInt(100) + 10;
        double sum = 0.0;
        for (int n = 0; n < num; n++)
        {
            double value = random.nextGaussian();
            data.add( value );
            sum += value;
        }

        assertEquals( sum, UnivariateStatisticsUtil.computeSum( data ) );

    }

    /**
     * Test of computeCorrelation method, of class gov.sandia.cognition.learning.util.statistics.UnivariateStatisticsUtil.
     */
    public void testComputeCorrelation()
    {
        System.out.println( "computeCorrelation" );

        Collection<Double> data1 = new LinkedList<Double>();
        Collection<Double> datan1 = new LinkedList<Double>();
        Collection<Double> data2 = new LinkedList<Double>();

        assertEquals( 1.0, UnivariateStatisticsUtil.computeCorrelation(data1, data2) );

        int num = random.nextInt(100) + 10;
        for (int n = 0; n < num; n++)
        {
            double value1 = random.nextGaussian();
            data1.add( value1 );
            datan1.add( -value1 );
            data2.add( random.nextGaussian() );
        }

        assertEquals( 1.0, UnivariateStatisticsUtil.computeCorrelation( data1, data1 ) );
        assertEquals( -1.0, UnivariateStatisticsUtil.computeCorrelation( data1, datan1 ) );

        try
        {
            UnivariateStatisticsUtil.computeCorrelation( data1, new LinkedList<Double>() );
            fail( "Datsets must be same size" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        // These were collected from the octave corrcoef() function
        Collection<Double> d3 = Arrays.asList( 1.0, 2.0, 3.0, 4.0, 5.0 );
        Collection<Double> d4 = Arrays.asList( 1.0, 2.0, 4.0, 2.0, 0.0 );

        assertEquals( -0.213201, UnivariateStatisticsUtil.computeCorrelation( d3, d4 ), EPS );

        Collection<Double> d5 = Arrays.asList( 0.0, 1.0, 2.0, 2.0, 1.0 );
        assertEquals( 0.566947, UnivariateStatisticsUtil.computeCorrelation( d5, d3 ), EPS );

    }

    /**
     * Computes median
     */
    public void testComputeMedian()
    {
        System.out.println( "computeMedian" );

        Collection<Double> data1 = Arrays.asList( 4.0, 2.0, 1.0, -1.0, 5.0 );
        assertEquals( 2.0, UnivariateStatisticsUtil.computeMedian(data1) );

        Collection<Double> data2 = Arrays.asList( 4.0, 2.0, 1.0, -1.0, 5.0, 0.0 );
        assertEquals( 1.5, UnivariateStatisticsUtil.computeMedian(data2) );

    }

    public void testComputePercentile()
    {
        System.out.println( "computePercentile" );
        Collection<Double> data1 = Arrays.asList( 4.0, 2.0, 1.0, -1.0, 5.0 );
        assertEquals( -0.6, UnivariateStatisticsUtil.computePercentile(data1,0.2), EPS );
        assertEquals( UnivariateStatisticsUtil.computePercentile(data1, 0.5),
            UnivariateStatisticsUtil.computeMedian(data1) );

        assertEquals( 5.0, UnivariateStatisticsUtil.computePercentile(data1, 1.0) );
        assertEquals( -1.0, UnivariateStatisticsUtil.computePercentile(data1, 0.0) );

        try
        {
            UnivariateStatisticsUtil.computePercentile(data1, -1.0);
            fail( "Percentile must be [0.0,1.0]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


        try
        {
            UnivariateStatisticsUtil.computePercentile(data1, 1.1);
            fail( "Percentile must be [0.0,1.0]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * computeSkewness
     */
    public void testComputeSkewness()
    {
        System.out.println( "computeSkewness" );

        assertEquals( 0.0, UnivariateStatisticsUtil.computeSkewness(new LinkedList<Double>() ) );
        assertEquals( 0.655415, UnivariateStatisticsUtil.computeSkewness(
            Arrays.asList( 1, 2, 3, 10, 20 ) ), EPS );

        // From: http://www.stat.wvu.edu/SRS/Modules/Moments/moments.html
        /*
        List<Double> data = Arrays.asList(
            12.61, 13.07, 7.36, 8.26, 10.98, 8.43, 11.53, 10.10, 8.89, 8.66,
            12.08, 11.26, 11.41, 9.78, 7.23, 13.81, 10.62, 10.49, 8.81, 10.58,
            10.60, 10.93, 12.20, 8.28, 6.97, 8.12, 9.06, 10.49, 3.84, 12.05 );

        assertEquals( -0.76, UnivariateStatisticsUtil.computeSkewness(data) );
        */
    }


    public void testComputeCentralMoment()
    {
        System.out.println( "computeCentralMoment" );

        assertEquals( 0.0, UnivariateStatisticsUtil.computeCentralMoment( new LinkedList<Double>(), 0.0, 1 ) );
        List<Integer> data = Arrays.asList( 1, 2, 3, 10, 20 );
        double mean = UnivariateStatisticsUtil.computeMean(data);
        assertEquals( 0.0, UnivariateStatisticsUtil.computeCentralMoment(data, mean, 1 ) );
        assertEquals( 50.96, UnivariateStatisticsUtil.computeCentralMoment(data, mean, 2), EPS );

        try
        {
            UnivariateStatisticsUtil.computeCentralMoment(data, mean, 0);
            fail( "Moment must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    public void testComputeKurtosis()
    {
        System.out.println( "computeKurtosis" );

        // This is from octave's kurtosis command
        List<Integer> data = Arrays.asList( 1, 2, 3, 10, 20 );
        assertEquals( -1.549669, UnivariateStatisticsUtil.computeKurtosis(data), EPS );
        assertEquals( 0, UnivariateStatisticsUtil.computeKurtosis(Arrays.asList(1)), EPS );
    }

    public void testComputeMaximum()
    {
        System.out.println( "computeMaximum" );

        assertEquals( 4.0, UnivariateStatisticsUtil.computeMaximum( Arrays.asList( 3, 1, 4, 1, 4, 2 ) ) );
    }

    public void testComputeMinimum()
    {
        System.out.println( "computeminimum" );

        assertEquals( 1.0, UnivariateStatisticsUtil.computeMinimum( Arrays.asList( 3, 1, 4, 1, 4, 2 ) ) );
    }

    public void testComputeMinAndMax()
    {
        System.out.println( "computeMinAndMax" );
        Pair<Double,Double> result = UnivariateStatisticsUtil.computeMinAndMax( Arrays.asList( 3, 1, 4, 1, 4, 2 ) );
        assertEquals( 1.0, result.getFirst() );
        assertEquals( 4.0, result.getSecond() );
    }

    public void testComputeEntropy()
    {
        System.out.println( "computeEntropy" );

        Collection<Double> data = Arrays.asList( 0.2, 0.6, 0.2 );
        assertEquals( 1.370951, UnivariateStatisticsUtil.computeEntropy(data), EPS );
        
    }

    public void testComputeMeanAndVariance()
    {
        System.out.println( "computeMeanAndVariance" );

        Collection<Integer> x = Arrays.asList( 1, 2, 3, 4, 5 );
        double mean = 3.0;
        double variance = 2.5;

        Pair<Double,Double> result = UnivariateStatisticsUtil.computeMeanAndVariance(x);
        assertEquals( mean, result.getFirst(), EPS );
        assertEquals( variance, result.getSecond(), EPS );

        x = Arrays.asList(1);
        result = UnivariateStatisticsUtil.computeMeanAndVariance(x);
        assertEquals( CollectionUtil.getFirst(x).doubleValue(), result.getFirst() );
        assertEquals( 0.0, result.getSecond() );

        x = new LinkedList<Integer>();
        result = UnivariateStatisticsUtil.computeMeanAndVariance(x);
        assertEquals( 0.0, result.getFirst() );
        assertEquals( 0.0, result.getSecond() );

        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        Collection<Double> xd = Arrays.asList(4.0, 7.0, 13.0, 16.0);
        result = UnivariateStatisticsUtil.computeMeanAndVariance(xd);
        assertEquals(10.0, result.getFirst(), EPS);
        assertEquals(30.0, result.getSecond(), EPS);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        xd = Arrays.asList(1.0e9 + 4.0, 1e9 + 7, 1e9 + 13, 1e9 + 16);
        result = UnivariateStatisticsUtil.computeMeanAndVariance(xd);
        assertEquals(1e9 + 10.0, result.getFirst(), EPS);
        assertEquals(30.0, result.getSecond(), EPS);
    }

    public void testComputeWeightedMeanAndVariance()
    {
        System.out.println( "computeWeightedMeanAndVariance" );

        final int N = 100;
        ArrayList<DefaultWeightedValue<? extends Number>> data =
            new ArrayList<DefaultWeightedValue<? extends Number>>( N );
        ArrayList<DefaultWeightedValue<? extends Number>> negatedData =
            new ArrayList<DefaultWeightedValue<? extends Number>>( N );
        double sum = 0.0;
        double weightSum = 0.0;
        for( int n = 0; n < N; n++ )
        {
            double weight = random.nextDouble();
            double value = random.nextGaussian();
            weightSum += weight;
            sum += weight*value;
            data.add( new DefaultWeightedValue<Double>( value, weight ) );
            negatedData.add(DefaultWeightedValue.create(value, -weight));
        }
        
        double mean = sum / weightSum;

        double sum2 = 0.0;
        for( int n = 0; n < N; n++ )
        {
            double weight = data.get(n).getWeight();
            double value = data.get(n).getValue().doubleValue();
            double delta = value - mean;
            sum2 += weight*delta*delta;
        }
        double variance = sum2 / weightSum;

        Pair<Double,Double> result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
        assertEquals( mean, result.getFirst(), EPS );
        assertEquals( variance, result.getSecond(), EPS );

        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(negatedData);
        assertEquals( mean, result.getFirst(), EPS );
        assertEquals( variance, result.getSecond(), EPS );

        while( data.size() > 1 )
        {
            data.remove(0);
            negatedData.remove(0);
        }

        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
        assertEquals( CollectionUtil.getFirst(data).getValue().doubleValue(), result.getFirst() );
        assertEquals( 0.0, result.getSecond() );

        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(negatedData);
        assertEquals( CollectionUtil.getFirst(data).getValue().doubleValue(), result.getFirst() );
        assertEquals( 0.0, result.getSecond() );

        data.remove(0);
        negatedData.remove(0);
        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
        assertEquals( 0.0, result.getFirst() );
        assertEquals( 0.0, result.getSecond() );
        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(negatedData);
        assertEquals( 0.0, result.getFirst() );
        assertEquals( 0.0, result.getSecond() );


        // This example is from the Wikipedia page: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        // First demonstrate that for these simple values the mean is 10 and variance is 30.
        Collection<Double> xd = Arrays.asList(4.0, 7.0, 13.0, 16.0);
        data.clear();
        negatedData.clear();
        for (Double x : xd)
        {
            data.add(DefaultWeightedValue.create(x, 1.0));
            negatedData.add(DefaultWeightedValue.create(x, 
                negatedData.size() % 2 == 0 ? 1.0 : -1.0));
        }
        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
        assertEquals(10.0, result.getFirst(), EPS);
        assertEquals(22.5, result.getSecond(), EPS);
        
        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(negatedData);
        assertEquals(10.0, result.getFirst(), EPS);
        assertEquals(22.5, result.getSecond(), EPS);

        // Now shift the mean by adding 10^9 and check that the variance remains
        // 30.
        xd = Arrays.asList(1.0e9 + 4.0, 1e9 + 7, 1e9 + 13, 1e9 + 16);
        data.clear();
        negatedData.clear();
        for (Double x : xd)
        {
            data.add(DefaultWeightedValue.create(x, 1.0));
            negatedData.add(DefaultWeightedValue.create(x,
                negatedData.size() % 2 == 0 ? 1.0 : -1.0));
        }
        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
        assertEquals(1e9 + 10.0, result.getFirst(), EPS);
        assertEquals(22.5, result.getSecond(), EPS);

        data.add(0, DefaultWeightedValue.create(1.0, 0.0));
        data.add(0, DefaultWeightedValue.create(1.0, 0.0));
        data.add(4, DefaultWeightedValue.create(1.0, 0.0));
        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
        assertEquals(1e9 + 10.0, result.getFirst(), EPS);
        assertEquals(22.5, result.getSecond(), EPS);

        result = UnivariateStatisticsUtil.computeWeightedMeanAndVariance(negatedData);
        assertEquals(1e9 + 10.0, result.getFirst(), EPS);
        assertEquals(22.5, result.getSecond(), EPS);

    }

    public void testComputeWeightedMean()
    {
        System.out.println( "computeWeightedMean" );

        final int N = 100;
        ArrayList<DefaultWeightedValue<? extends Number>> data =
            new ArrayList<DefaultWeightedValue<? extends Number>>( N );
        double sum = 0.0;
        double weightSum = 0.0;
        for( int n = 0; n < N; n++ )
        {
            double weight = random.nextDouble();
            double value = random.nextGaussian();
            weightSum += weight;
            sum += weight*value;
            data.add( new DefaultWeightedValue<Double>( value, weight ) );
        }

        double mean = sum / weightSum;

        double meanHat = UnivariateStatisticsUtil.computeWeightedMean(data);
        assertEquals( mean, meanHat, EPS );

        data.clear();
        data.add( new DefaultWeightedValue<Double>( 1.0, 0.0 ) );
        assertEquals( 0.0, UnivariateStatisticsUtil.computeWeightedMean(data) );

    }


    /**
     * computeWeightedKurtosis
     */
    public void testComputeWeightedKurtosis()
    {
        System.out.println( "computeWeightedKurtosis" );
        
        final int N = 100;
        ArrayList<Number> data =
            new ArrayList<Number>( N );
        ArrayList<DefaultWeightedValue<? extends Number>> weightedData =
            new ArrayList<DefaultWeightedValue<? extends Number>>( N );
        for( int n = 0; n < N; n++ )
        {
            double weight = 1.0;
            double value = random.nextGaussian();
            data.add( value );
            weightedData.add( new DefaultWeightedValue<Double>( value, weight ) );
        }

        // khat is biased, and k is unbiased, so we need to switch it.
        double scale = (N-1.0) / N;
        double k = UnivariateStatisticsUtil.computeKurtosis(data);
        double khat = UnivariateStatisticsUtil.computeWeightedKurtosis(weightedData);
        assertEquals( k, scale*scale*(khat+3.0)-3.0, EPS );

    }
    
}
