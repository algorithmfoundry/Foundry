/*
 * File:                MultivariateGaussianMeanCovarianceBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 29, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.NormalInverseWishartDistribution;
import java.util.ArrayList;

/**
 * Unit tests for MultivariateGaussianMeanCovarianceBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class MultivariateGaussianMeanCovarianceBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Vector,Matrix,NormalInverseWishartDistribution>
{

    /**
     * Dimensionality
     */
    public int DIM = 3;

    /**
     * Tests for class MultivariateGaussianMeanCovarianceBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public MultivariateGaussianMeanCovarianceBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultivariateGaussianMeanCovarianceBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateGaussianMeanCovarianceBayesianEstimator instance =
            new MultivariateGaussianMeanCovarianceBayesianEstimator();
        NormalInverseWishartDistribution belief = instance.getInitialBelief();
        assertNotNull( belief );

        instance = new MultivariateGaussianMeanCovarianceBayesianEstimator( belief );
        assertSame( belief, instance.getInitialBelief() );
    }

    @Override
    public MultivariateGaussianMeanCovarianceBayesianEstimator createInstance()
    {
        return new MultivariateGaussianMeanCovarianceBayesianEstimator(3);
    }

    @Override
    public MultivariateGaussian createConditionalDistribution()
    {
        Vector mean = VectorFactory.getDefault().createUniformRandom(
            DIM,-3.0, 3.0, RANDOM);
        Matrix R = MatrixFactory.getDefault().createUniformRandom(
            DIM, DIM, -1.0, 1.0, RANDOM);
        Matrix C = R.times( R.transpose() );
        return new MultivariateGaussian( mean, C );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        MultivariateGaussian g = this.createConditionalDistribution();
        ArrayList<? extends Vector> samples = g.sample(RANDOM,NUM_SAMPLES);
        MultivariateGaussianMeanCovarianceBayesianEstimator instance =
            this.createInstance();

        NormalInverseWishartDistribution result = instance.learn(samples);

        ArrayList<? extends Matrix> parameters = result.sample(RANDOM,NUM_SAMPLES);
        Matrix averageParameter = MultivariateStatisticsUtil.computeMean(parameters);
        MultivariateGaussian ghat =
            instance.createConditionalDistribution(averageParameter);

        Matrix parameterMean = result.getMean();
        MultivariateGaussian ghat2 =
            instance.createConditionalDistribution(parameterMean);
        System.out.println( "G:\n" + g );
        System.out.println( "Ghat:\n" + ghat );
        System.out.println( "Ghat2:\n" + ghat2 );


    }

}
