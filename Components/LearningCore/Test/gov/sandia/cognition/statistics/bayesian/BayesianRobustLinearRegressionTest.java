/*
 * File:                BayesianRobustLinearRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.distribution.InverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussianInverseGammaDistribution;

/**
 * Unit tests for BayesianRobustLinearRegressionTest.
 *
 * @author krdixon
 */
public class BayesianRobustLinearRegressionTest
    extends BayesianRegressionTestHarness<MultivariateGaussianInverseGammaDistribution>
{

    /**
     * Tests for class BayesianRobustLinearRegressionTest.
     * @param testName Name of the test.
     */
    public BayesianRobustLinearRegressionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class BayesianRobustLinearRegressionTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        int dim = 10;
        BayesianRobustLinearRegression instance =
            new BayesianRobustLinearRegression( dim );
        assertNotNull( instance.getOutputVariance() );
        assertEquals( dim, instance.getWeightPrior().getInputDimensionality() );

        BayesianRobustLinearRegression i2 = new BayesianRobustLinearRegression(
            instance.getOutputVariance(),
            instance.getWeightPrior() );
        assertSame( instance.getOutputVariance(), i2.getOutputVariance() );
        assertSame( instance.getWeightPrior(), i2.getWeightPrior() );

    }

    /**
     * Test of getWeightPrior method, of class BayesianRobustLinearRegression.
     */
    public void testGetWeightPrior()
    {
        System.out.println("getWeightPrior");
        BayesianRobustLinearRegression instance = this.createInstance();
        MultivariateGaussian prior = instance.getWeightPrior();
        assertNotNull( prior );
    }

    /**
     * Test of setWeightPrior method, of class BayesianRobustLinearRegression.
     */
    public void testSetWeightPrior()
    {
        System.out.println("setWeightPrior");

        BayesianRobustLinearRegression instance = this.createInstance();
        MultivariateGaussian prior = instance.getWeightPrior();
        assertNotNull( instance );
        instance.setWeightPrior(null);
        assertNull( instance.getWeightPrior() );
        instance.setWeightPrior(prior);
        assertSame( prior, instance.getWeightPrior() );
    }

    /**
     * Test of getOutputVariance method, of class BayesianRobustLinearRegression.
     */
    public void testGetOutputVariance()
    {
        System.out.println("getOutputVariance");
        BayesianRobustLinearRegression instance = this.createInstance();
        InverseGammaDistribution ov = instance.getOutputVariance();
        assertNotNull( ov );
    }

    /**
     * Test of setOutputVariance method, of class BayesianRobustLinearRegression.
     */
    public void testSetOutputVariance()
    {
        System.out.println("setOutputVariance");
        BayesianRobustLinearRegression instance = this.createInstance();
        InverseGammaDistribution ov = instance.getOutputVariance();
        assertNotNull( ov );
        instance.setOutputVariance(null);
        assertNull( instance.getOutputVariance() );
        instance.setOutputVariance(ov);
        assertSame( ov, instance.getOutputVariance() );
    }

    @Override
    public BayesianRobustLinearRegression createInstance()
    {
        BayesianRobustLinearRegression instance =
            new BayesianRobustLinearRegression(DEFAULT_DIM+1);
        return instance;
    }

    /**
     * Batch and Incremental Comparison
     */
    public void testIncrementalAndBatch()
    {

        BayesianRobustLinearRegression instance = this.createInstance();

        BayesianRobustLinearRegression.IncrementalEstimator incremental =
            new BayesianRobustLinearRegression.IncrementalEstimator(
                instance.getOutputVariance(), instance.getWeightPrior() );

        super.testIncrementalAndBatch(incremental);
    }


}
