/*
 * File:                MultivariateGaussianTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.math.matrix.mtj.decomposition.CholeskyDecompositionMTJ;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;
import java.util.ArrayList;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MultivariateGaussian
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments={
        "Fixed missing documentation.",
        "Replace calls to MathAssert.assertFuzzyEquals with assertEquals(double, double, delta)."
    }
)
public class MultivariateGaussianTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Vector>
{

    /**
     * Creates a new instance of MultivariateGaussianTest.
     *
     * @param testName The name of the test.
     */
    public MultivariateGaussianTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        Vector3 input = new Vector3( 0.0, 0.0, 0.0 );
        MultivariateGaussian.PDF instance = new MultivariateGaussian.PDF(
            new Vector3( 0.0, 0.0, 0.0 ),
            MatrixFactory.getDefault().createIdentity( 3, 3 ) );

        double expResult = 0.063494;
        double result = instance.evaluate( input );
        assertEquals( expResult, result, TOLERANCE );
    }

    /**
     * Test of getCovarianceInverse method, of class 
     * gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testGetCovarianceInverse()
    {
        Vector3 mean = new Vector3( 1.0, -2.0, 3.0 );
        Matrix covariance = MatrixFactory.getDefault().createIdentity( 3, 3 );
        MultivariateGaussian instance =
            new MultivariateGaussian( mean, covariance );

        assertNotNull( instance.getCovarianceInverse() );
        assertTrue( covariance.equals( instance.getCovarianceInverse(), TOLERANCE ) );
    }

    /**
     * Test of maximumLikelihoodEstimate method, of class 
     * gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testMaximumLikelihoodEstimate()
    {
        ArrayList<Vector> samples = new ArrayList<Vector>();
        samples.add( new Vector3( 0.0, 0.0, 0.0 ) );
        samples.add( new Vector3( 0.0, 0.1, 0.2 ) );
        samples.add( new Vector3( 0.0, -0.1, 0.3 ) );
        samples.add( new Vector3( 1.0, 0.0, 0.0 ) );
        samples.add( new Vector3( -1.0, 0.0, 0.0 ) );

        MultivariateGaussian estimate =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn( samples, 0.0 );

        Vector3 expectedMean = new Vector3( 0.0, 0.0, 0.1 );

        Vector3 covarianceColumn1 = new Vector3( 0.5, 0.0, 0.0 );
        Vector3 covarianceColumn2 = new Vector3( 0.0, 0.005, -0.0025 );
        Vector3 covarianceColumn3 = new Vector3( 0.0, -0.0025, 0.02 );

        Matrix expectedCovariance = MatrixFactory.getDefault().copyColumnVectors(
            covarianceColumn1, covarianceColumn2, covarianceColumn3 );

        Matrix estimatedCovariance = estimate.getCovariance();
        System.out.println( "Estimated:\n" + estimatedCovariance );
        System.out.println( "Expected:\n" + expectedCovariance );
        assertNotNull( estimate );
        assertEquals( expectedMean, estimate.getMean() );
        assertTrue( expectedCovariance.equals( estimatedCovariance, TOLERANCE ) );
    }

    /**
     * Test of getCovariance method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testGetCovariance()
    {
        System.out.println( "getCovariance" );

        int N = 2;
        double range = 1;
        Vector mean = VectorFactory.getDefault().createUniformRandom( N, -range, range, RANDOM );
        Matrix sqrt = MatrixFactory.getDefault().createUniformRandom( N, N, -range, range, RANDOM );
        Matrix covariance = sqrt.times( sqrt.transpose() );

        MultivariateGaussian instance =
            new MultivariateGaussian( mean, covariance );

        assertEquals( instance.getCovariance(), covariance );

    }

    /**
     * Test of randomCovarianceSquareRoot method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testRandomCovarianceSquareRoot()
    {
        System.out.println( "randomCovarianceSquareRoot" );

        int N = 2;
        double range = 1;
        Vector mean = VectorFactory.getDefault().createUniformRandom( N, -range, range, RANDOM );
        Matrix sqrt = MatrixFactory.getDefault().createUniformRandom( N, N, -range, range, RANDOM );
        DenseMatrix covariance = DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( sqrt.times( sqrt.transpose() ) );
        DenseMatrix covariancesqrt = CholeskyDecompositionMTJ.create( covariance ).getR();

        int numDraws = NUM_SAMPLES;
        ArrayList<Vector> samples =
            MultivariateGaussian.sample( mean, covariancesqrt, RANDOM, numDraws );
        MultivariateGaussian g2 =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn( samples, 0.0 );

        double tolerance = N / Math.log( numDraws ) / range;
        System.out.println( "Tolerance: " + tolerance );
        System.out.println( "Expected Mean:\n" + mean );
        System.out.println( "Resulting Mean:\n" + g2.getMean() );
        assertTrue( mean.equals( g2.getMean(), tolerance ) );

        System.out.println( "Expected Covariance:\n" + covariance );
        System.out.println( "Resulting Covariance:\n" + g2.getCovariance() );
        assertTrue( covariance.equals( g2.getCovariance(), tolerance ) );
    }

    /**
     * Test of setMean method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testSetMean()
    {
        System.out.println( "setMean" );

        MultivariateGaussian g = this.createInstance();

        int N = g.getInputDimensionality();
        Vector m1 = VectorFactory.getDefault().createVector(N, RANDOM.nextGaussian());
        g.setMean(m1);
        assertEquals( m1, g.getMean() );

        g.setMean( m1.clone() );
        assertNotSame( m1, g.getMean() );

        try
        {
            g.setMean( null );
            fail( "Cannot set null mean" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of scale method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testScale()
    {
        System.out.println( "scale" );

        MultivariateGaussian g1 = this.createInstance();

        final int M = RANDOM.nextInt( 10 ) + 1;
        final int N = g1.getInputDimensionality();
        final double range = -1.0;
        Matrix test = MatrixFactory.getDefault().createUniformRandom( M, N, -range, range, RANDOM );
        MultivariateGaussian g2 = g1.scale( test );

        assertEquals( M, g2.getInputDimensionality() );
        assertEquals( test.times( g1.getMean() ), g2.getMean() );
        assertEquals( test.times( g1.getCovariance() ).times( test.transpose() ), g2.getCovariance() );
    }

    /**
     * Test of plus method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testPlus()
    {
        System.out.println( "plus" );

        MultivariateGaussian g1 = this.createInstance();
        MultivariateGaussian g2 = this.createInstance();

        MultivariateGaussian sum = g1.plus( g2 );

        assertEquals( g1.getMean().plus( g2.getMean() ), sum.getMean() );
        assertEquals( g1.getCovariance().plus( g2.getCovariance() ),
            sum.getCovariance() );

        final int N = g1.getInputDimensionality() + 1;
        double range = -1.0;
        Vector mean = VectorFactory.getDefault().createUniformRandom( N, -range, range, RANDOM );
        Matrix sqrt = MatrixFactory.getDefault().createUniformRandom( N, N, -range, range, RANDOM );
        Matrix covariance = sqrt.times( sqrt.transpose() );

        MultivariateGaussian g3 = new MultivariateGaussian( mean, covariance );

        try
        {
            g1.plus( g3 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            g1.plus( null );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of times method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testTimes()
    {
        System.out.println( "times" );

        Vector m1 = VectorFactory.getDefault().createVector( 1 );
        m1.setElement( 0, 0.0 );
        Matrix c1 = MatrixFactory.getDefault().createMatrix( 1, 1 );
        c1.setElement( 0, 0, 0.1 );
        MultivariateGaussian g1 = new MultivariateGaussian( m1, c1 );

        Vector m2 = VectorFactory.getDefault().createVector( 1 );
        m2.setElement( 0, 1.0 );
        Matrix c2 = MatrixFactory.getDefault().createMatrix( 1, 1 );
        c2.setElement( 0, 0, 0.1 );
        MultivariateGaussian g2 = new MultivariateGaussian( m2, c2 );

        MultivariateGaussian belief = g1;
        for (int i = 0; i < 10; i++)
        {
            belief = belief.times( g2 );
            System.out.println( i + ": Belief: " + belief );
        }
    }

    /**
     * Test of convolve method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testConvolve()
    {
        System.out.println( "convolve" );
        
        Vector m1 = VectorFactory.getDefault().createVector( 1 );
        m1.setElement( 0, 0.0 );
        Matrix c1 = MatrixFactory.getDefault().createMatrix( 1, 1 );
        c1.setElement( 0, 0, 0.1 );
        MultivariateGaussian g1 = new MultivariateGaussian( m1, c1 );

        Vector m2 = VectorFactory.getDefault().createVector( 1 );
        m2.setElement( 0, 1.0 );
        Matrix c2 = MatrixFactory.getDefault().createMatrix( 1, 1 );
        c2.setElement( 0, 0, 0.1 );
        MultivariateGaussian g2 = new MultivariateGaussian( m2, c2 );

        MultivariateGaussian result = g1.convolve(g2);
        assertEquals( g1.getMean().plus(g2.getMean()), result.getMean() );
        assertEquals( g1.getCovariance().plus( g2.getCovariance()), result.getCovariance() );
    }

    /**
     * Test of setCovariance method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testSetCovariance()
    {
        System.out.println( "setCovariance" );

        MultivariateGaussian instance = this.createInstance();

        try
        {
            instance.setCovariance( null );
            fail( "Should have thrown NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Properly thrown NullPointerException: " + e );
        }

        int N = instance.getInputDimensionality();
        Matrix c2 = MatrixFactory.getDefault().createIdentity( N, N );
        assertFalse( c2.equals( instance.getCovariance() ) );
        instance.setCovariance( c2 );
        assertEquals( c2, instance.getCovariance() );
        assertTrue( c2.inverse().equals( instance.getCovarianceInverse(), TOLERANCE ) );

        Matrix Cbad = MatrixFactory.getDefault().createMatrix(3,3);
        Cbad.setElement(1, 0, 1.0);
        instance.setCovariance(Cbad);
        assertNotSame( Cbad, instance.getCovariance() );
        assertTrue( instance.getCovariance().isSymmetric() );

        Cbad = MatrixFactory.getDefault().createIdentity(3, 2);
        try
        {
            instance.setCovariance(Cbad);
            fail( "Covariance must be PD" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of equals method, of class gov.sandia.cognition.math.MultivariateGaussian.
     */
    public void testEquals()
    {
        System.out.println( "equals" );

        MultivariateGaussian instance = this.createInstance();
        MultivariateGaussian instance2 = this.createInstance();

        assertNotSame( instance, instance2 );
        assertEquals( instance, instance.clone() );
        assertEquals( instance2, instance2.clone() );

        assertFalse( instance.equals( instance2 ) );

    }

    /**
     * getInputDimensionality
     */
    public void testGetInputDimensionality()
    {
        System.out.println( "getInputDimensionality" );
        MultivariateGaussian.PDF instance = this.createInstance().getProbabilityFunction();
        assertEquals( instance.getMean().getDimensionality(), instance.getInputDimensionality() );
    }

    @Override
    public MultivariateGaussian createInstance()
    {
        int N = 3;
        double range = 2.0;

        Vector mean = VectorFactory.getDefault().createUniformRandom( N, -range, range, RANDOM );
        Matrix sqrt = MatrixFactory.getDefault().createUniformRandom( N, N, -range, range, RANDOM );
        Matrix covariance = sqrt.times( sqrt.transpose() );

        return new MultivariateGaussian( mean, covariance );
    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        int ns = NUM_SAMPLES;
        NUM_SAMPLES = 10000;
        TOLERANCE = 1e-1;
        super.testGetMean();
        TOLERANCE = temp;
        NUM_SAMPLES = ns;
    }



    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF.Constructors" );
        MultivariateGaussian.PDF g = new MultivariateGaussian.PDF();
        assertEquals( MultivariateGaussian.DEFAULT_DIMENSIONALITY, g.getInputDimensionality() );

        int dim = RANDOM.nextInt(10) + 1;
        g = new MultivariateGaussian.PDF( dim );
        assertEquals( dim, g.getInputDimensionality() );

        Vector mean = g.getMean();
        Matrix covariance = g.getCovariance();

        g = new MultivariateGaussian.PDF( mean, covariance );
        assertSame( mean, g.getMean() );
        assertSame( covariance, g.getCovariance() );

        MultivariateGaussian.PDF g2 = new MultivariateGaussian.PDF( g );
        assertNotSame( g, g2 );
        assertNotSame( g.getMean(), g2.getMean() );
        assertEquals( g.getMean(), g2.getMean() );
        assertNotSame( g.getCovariance(), g2.getCovariance() );
        assertEquals( g.getCovariance(), g2.getCovariance() );
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        MultivariateGaussian g = new MultivariateGaussian();
        assertEquals( MultivariateGaussian.DEFAULT_DIMENSIONALITY, g.getInputDimensionality() );

        int dim = RANDOM.nextInt(10) + 1;
        g = new MultivariateGaussian( dim );
        assertEquals( dim, g.getInputDimensionality() );

        Vector mean = g.getMean();
        Matrix covariance = g.getCovariance();

        g = new MultivariateGaussian( mean, covariance );
        assertSame( mean, g.getMean() );
        assertSame( covariance, g.getCovariance() );

        MultivariateGaussian g2 = new MultivariateGaussian( g );
        assertNotSame( g, g2 );
        assertNotSame( g.getMean(), g2.getMean() );
        assertEquals( g.getMean(), g2.getMean() );
        assertNotSame( g.getCovariance(), g2.getCovariance() );
        assertEquals( g.getCovariance(), g2.getCovariance() );
    }

    @Override
    public void testKnownValues()
    {
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );
        MultivariateGaussian g = this.createInstance();
        Vector p = g.convertToVector();

        int d = g.getInputDimensionality();
        Vector mhat = p.subVector(0, d-1);
        assertEquals( g.getMean(), mhat );
        Vector Chat = p.subVector(d, p.getDimensionality()-1);
        Matrix C = MatrixFactory.getDefault().createMatrix(d, d);
        C.convertFromVector(Chat);
        assertEquals( g.getCovariance(), C );
    }

    /**
     * tests the incremental estimator
     */
    public void testIncrementalEstimator()
    {
        System.out.println( "Incremental Estimator" );

        MultivariateGaussian.IncrementalEstimator estimator =
            new MultivariateGaussian.IncrementalEstimator();

        MultivariateGaussian.IncrementalEstimatorCovarianceInverse ei =
            new MultivariateGaussian.IncrementalEstimatorCovarianceInverse();

        MultivariateGaussian target = new MultivariateGaussian( 3 );
        ArrayList<Vector> samples = target.sample(RANDOM,NUM_SAMPLES);


        Vector mean = MultivariateStatisticsUtil.computeMean(samples);
        MultivariateGaussian.SufficientStatistic ss = estimator.learn(samples);
        assertEquals( samples.size(), ss.getCount() );
        assertTrue( mean.equals( ss.getMean(), TOLERANCE ) );
        assertTrue( MultivariateStatisticsUtil.computeVariance(samples, mean).equals( ss.getCovariance(), TOLERANCE ) );
        MultivariateGaussian result = ss.create();

        MultivariateGaussian.SufficientStatisticCovarianceInverse ssi = ei.learn(samples);
        MultivariateGaussian ri = ssi.create();

        MultivariateGaussian batch =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(samples, 0.0);

        System.out.println( "Target:  " + target );
        System.out.println( "Result:  " + result );
        System.out.println( "Inverse: " + ri );
        System.out.println( "Batch :  " + batch );

        assertTrue( batch.getMean().equals( result.getMean(), TOLERANCE ) );
        assertTrue( batch.getCovariance().equals( result.getCovariance(), TOLERANCE ) );

        MultivariateGaussian.SufficientStatistic clone = ss.clone();
        assertEquals( ss.getCount(), clone.getCount() );
        assertEquals( ss.getMean(), clone.getMean() );
        assertEquals( ss.getCovariance(), clone.getCovariance() );

    }

}
