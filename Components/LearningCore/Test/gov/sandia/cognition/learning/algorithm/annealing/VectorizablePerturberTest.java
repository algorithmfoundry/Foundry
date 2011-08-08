/*
 * File:                VectorizablePerturberTest.java
 * Authors:             Jonathan McClain and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.annealing;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import java.util.ArrayList;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * The VectorizablePerturberTest implements a series of tests for the 
 * VectorizablePerturber class.
 *
 * @author Jonathan McClain
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-04",
    changesNeeded=false,
    comments="Tests look fine."
)
public class VectorizablePerturberTest 
    extends TestCase 
{
  
    /** The perturber to use in the tests. */
    private VectorizablePerturber perturber = null;
    
    /** The covariance matrix to use in the tests. */
    private Matrix covariance = null;
    
    /** The random number generator for the tests. */
    private Random random = new Random(1);
    
    /**
     * The test constructor.
     *
     * @param testName The name of the test.
     */
    public VectorizablePerturberTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Called before each test is run. Prepares the covariance matrix and the 
     * VectorizablePerturber to use in the tests.
     */
    @Override
    protected void setUp() 
        throws Exception 
    {
        double diagonal = 2.0;
        this.covariance = MatrixFactory.getDefault().createIdentity(2, 2).scale(diagonal);
        double offDiagonal = 1.0;
        this.covariance.setElement(1, 0, offDiagonal);
        this.covariance.setElement(0, 1, offDiagonal);
                
        this.perturber = new VectorizablePerturber(
            new Random(2),
            this.covariance);
    }

    public void testClone()
    {
        System.out.println( "Clone" );
        VectorizablePerturber clone = this.perturber.clone();
        assertNotNull( clone );
        assertNotSame( this.perturber, clone );
        assertNotNull( clone.getCovarianceSqrt() );
        assertNotSame( this.perturber.getCovarianceSqrt(), clone.getCovarianceSqrt() );
    }

    /**
     * Test of perturb method, of class 
     * gov.sandia.isrc.learning.vector.VectorizablePerturber.
     */
    public void testPerturb() 
    {    
        System.out.println("perturb");
        
        Vector initialParameters = VectorFactory.getDefault().createUniformRandom( 2, -10, 10, random );
        
        int N = 100;
        ArrayList<Vector> results = new ArrayList<Vector>();
        for( int i = 0; i < N; i++ )
        {
            Vector r = VectorFactory.getDefault().copyValues(
                random.nextGaussian(), random.nextGaussian() );
            Vectorizable s = this.perturber.perturb( r );
            results.add( s.convertToVector() );
        }
        
        MultivariateGaussian estimate =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn( results, 0.0 );

        Matrix diffCov = this.covariance.minus( estimate.getCovariance() );
        Vector diffMean = initialParameters.minus( estimate.getMean() );
        double normMean = diffMean.norm2();
        double normCov = diffCov.normFrobenius();
        double testMax = 1.0 / Math.log( N );
        
        System.out.println("NormMean = " + normMean + ", testMax = " + testMax);
//        assertTrue( normMean <= testMax );
        
        System.out.println("NormCov = " + normCov + ", testMax = " + testMax);
//        assertTrue( normCov <= testMax );
    }

    /**
     * Test of perturbVector method, of class 
     * gov.sandia.isrc.learning.vector.VectorizablePerturber.
     */
    public void testPerturbVector() 
    {
        System.out.println("perturbVector");
        
        Vector initialParameters = VectorFactory.getDefault().createUniformRandom( 2, -10, 10, random );
        
        int N = 100;
        ArrayList<Vector> results = new ArrayList<Vector>();
        for( int i = 0; i < N; i++ )
        {
            Vector r = initialParameters.clone();
            Vector s = this.perturber.perturbVector( r );
            results.add( s );
        }
        
        MultivariateGaussian estimate =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn( results, 0.0 );

        Matrix diffCov = this.covariance.minus( estimate.getCovariance() );
        Vector diffMean = initialParameters.minus( estimate.getMean() );
        double normMean = diffMean.norm2();
        double normCov = diffCov.normFrobenius();
        double testMax = 1.0 / Math.log( N );
        
        System.out.println("NormMean = " + normMean + ", testMax = " + testMax);
        assertTrue( normMean <= testMax );
        
        System.out.println("NormCov = " + normCov + ", testMax = " + testMax);
//        assertTrue( normCov <= testMax );
    }

    /**
     * Test of getRandom method, of class 
     * gov.sandia.isrc.learning.vector.VectorizablePerturber.
     */
    public void testGetRandom() 
    {
        System.out.println("getRandom");
        
        Random expected = new Random();
        this.perturber.setRandom(expected);
        Random actual = this.perturber.getRandom();
        assertEquals(
                "getRandom did not get the expected Random", 
                expected, 
                actual);
    }

    /**
     * Test of setRandom method, of class 
     * gov.sandia.isrc.learning.vector.VectorizablePerturber.
     */
    public void testSetRandom() 
    {
        System.out.println("setRandom");
        
        Random expected = new Random();
        this.perturber.setRandom(expected);
        Random actual = this.perturber.getRandom();
        assertEquals(
                "getRandom did not get the expected Random", 
                expected, 
                actual);
    }

    /**
     * Test of getCovarianceSqrt method, of class 
     * gov.sandia.isrc.learning.vector.VectorizablePerturber.
     */
    public void testGetCovarianceSqrt() 
    {
        System.out.println("getCovarianceSqrt");
        
        Matrix expected = MatrixFactory.getDefault().createUniformRandom(1,1,-1,1, random);
        this.perturber.setCovarianceSqrt(expected);
        DenseMatrix actual = (DenseMatrix) this.perturber.getCovarianceSqrt();
        assertEquals(
                "getCovarianceSqrt did not get the expected Matrix",
                expected,
                actual);
    }

    /**
     * Test of setCovarianceSqrt method, of class 
     * gov.sandia.isrc.learning.vector.VectorizablePerturber.
     */
    public void testSetCovarianceSqrt() 
    {
        System.out.println("setCovarianceSqrt");
        
        Matrix expected = MatrixFactory.getDefault().createUniformRandom(1,1,-1,1, random);
        this.perturber.setCovarianceSqrt(expected);
        DenseMatrix actual = (DenseMatrix)this.perturber.getCovarianceSqrt();
        assertEquals(
                "getCovarianceSqrt did not get the expected Matrix",
                expected,
                actual);
    }

}
