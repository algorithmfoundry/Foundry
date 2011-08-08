/*
 * File:                BayesianLinearRegressionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;

/**
 * Unit tests for BayesianLinearRegressionTest.
 *
 * @author krdixon
 */
public class BayesianLinearRegressionTest
    extends BayesianRegressionTestHarness<MultivariateGaussian>
{

    /**
     * Tests for class BayesianLinearRegressionTest.
     * @param testName Name of the test.
     */
    public BayesianLinearRegressionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class BayesianLinearRegressionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        final int dim = 2;
        BayesianLinearRegression<Double> instance =
            new BayesianLinearRegression<Double>( dim );
        assertNull( instance.getFeatureMap() );
        assertEquals( dim, instance.getWeightPrior().getMean().getDimensionality() );
        assertEquals( BayesianLinearRegression.DEFAULT_OUTPUT_VARIANCE, instance.getOutputVariance() );
    }

    @Override
    public BayesianLinearRegression<Double> createInstance()
    {
        BayesianLinearRegression<Double> instance =
            new BayesianLinearRegression<Double>( 10 );
        instance.setOutputVariance(0.1);
        instance.setFeatureMap( new RadialBasisVectorFunction(9) );
        return instance;
    }

    /**
     * Test of getWeightPrior method, of class BayesianLinearRegression.
     */
    public void testGetWeightPrior()
    {
        System.out.println("getWeightPrior");
        BayesianLinearRegression<Double> instance = this.createInstance();
        assertNotNull( instance.getWeightPrior() );
    }

    /**
     * Test of setWeightPrior method, of class BayesianLinearRegression.
     */
    public void testSetWeightPrior()
    {
        System.out.println("setWeightPrior");
        BayesianLinearRegression<Double> instance = this.createInstance();
        MultivariateGaussian prior = instance.getWeightPrior();
        instance.setWeightPrior(null);
        assertNull( instance.getWeightPrior() );
        instance.setWeightPrior(prior);
        assertSame( prior, instance.getWeightPrior() );
    }

    /**
     * Test of getOutputVariance method, of class BayesianLinearRegression.
     */
    public void testGetOutputVariance()
    {
        System.out.println("getOutputVariance");
        BayesianLinearRegression<Double> instance = this.createInstance();
        assertTrue( instance.getOutputVariance() > 0.0 );
    }

    /**
     * Test of setOutputVariance method, of class BayesianLinearRegression.
     */
    public void testSetOutputVariance()
    {
        System.out.println("setOutputVariance");
        BayesianLinearRegression<Double> instance = this.createInstance();
        double variance = RANDOM.nextDouble() * 10.0;
        instance.setOutputVariance(variance);
        assertEquals( variance, instance.getOutputVariance() );
        try
        {
            instance.setOutputVariance(0.0);
            fail( "Outputvariance must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Batch and Incremental Comparison
     */
    public void testIncrementalAndBatch()
    {
        
        BayesianLinearRegression<Double> instance = this.createInstance();

        BayesianLinearRegression.IncrementalEstimator<Double> incremental =
            new BayesianLinearRegression.IncrementalEstimator<Double>(
                instance.getFeatureMap(), instance.getOutputVariance(), instance.getWeightPrior() );

        super.testIncrementalAndBatch(incremental);
    }
    
}
