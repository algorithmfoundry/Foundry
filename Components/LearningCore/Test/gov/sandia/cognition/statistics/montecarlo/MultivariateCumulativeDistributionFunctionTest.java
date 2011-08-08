/*
 * File:                MultivariateCumulativeDistributionFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 31, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MultivariateCumulativeDistributionFunctionTest.
 *
 * @author krdixon
 */
public class MultivariateCumulativeDistributionFunctionTest
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

    /**
     * Confidence, {@value}.
     */
    public final double CONFIDENCE = 0.95;

    /**
     * Tests for class MultivariateCumulativeDistributionFunctionTest.
     * @param testName Name of the test.
     */
    public MultivariateCumulativeDistributionFunctionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultivariateCumulativeDistributionFunctionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateCumulativeDistributionFunction instance =
            new MultivariateCumulativeDistributionFunction();
        assertNotNull( instance );
    }

    protected void testGaussian(
        int dim,
        double tolerance )
    {
        System.out.println( "Gaussian Dimension: " + dim );
        Distribution<Vector> d1 = new MultivariateGaussian(dim);
        Vector x1 = VectorFactory.getDefault().createVector(dim);
        int num = 1000;
        ArrayList<Double> means = new ArrayList<Double>( num );
        ArrayList<Double> variances = new ArrayList<Double>( num );
        for( int n = 0; n < num; n++ )
        {
            UnivariateGaussian result = MultivariateCumulativeDistributionFunction.compute(
                x1, d1, RANDOM, tolerance );
            means.add( result.getMean() );
            variances.add( result.getVariance() );
        }
        GaussianConfidence.Statistic meanConfidence =
            GaussianConfidence.evaluateNullHypothesis(means, 1.0/Math.pow(
            2.0, dim) );
        UnivariateGaussian meanResult =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn( means, 0.0 );
        System.out.println( "Mean Result: " + meanResult );
        System.out.println( "Mean Confidence: " + meanConfidence );
        assertEquals( 1.0, meanConfidence.getNullHypothesisProbability(), 1.0-tolerance );
        double numericalStddev = Math.sqrt(meanResult.getVariance());
        System.out.println( "Numerical StdDev: " + numericalStddev );
        assertTrue( numericalStddev <= tolerance );

        UnivariateGaussian varianceResult =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);
        System.out.println( "Variance Result: " + varianceResult );



        double sp = (Math.sqrt(meanResult.getVariance()) - Math.sqrt(varianceResult.getMean())) / Math.sqrt(meanResult.getVariance());
        System.out.println( "StdDev Pct: " + sp );
        assertEquals( 0.0, sp, 1.0-CONFIDENCE);

    }

    /**
     * Test of compute method, of class MultivariateCumulativeDistributionFunction.
     */
    public void testComputeOneDim()
    {
        System.out.println("compute One Dim");

        this.testGaussian(1, 1e-2);
        this.testGaussian(3, 1e-2);

    }



}
