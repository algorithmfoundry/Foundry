/*
 * File:                UnivariateGaussianMeanVarianceBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.distribution.NormalInverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Unit tests for UnivariateGaussianMeanVarianceBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class UnivariateGaussianMeanVarianceBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Double,Vector,NormalInverseGammaDistribution>
{

    /**
     * Tests for class UnivariateGaussianMeanVarianceBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public UnivariateGaussianMeanVarianceBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class UnivariateGaussianMeanVarianceBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        UnivariateGaussianMeanVarianceBayesianEstimator instance =
            new UnivariateGaussianMeanVarianceBayesianEstimator();
        assertNotNull( instance.getInitialBelief() );

    }

    @Override
    public UnivariateGaussianMeanVarianceBayesianEstimator createInstance()
    {
        return new UnivariateGaussianMeanVarianceBayesianEstimator();
    }

    @Override
    public UnivariateGaussian createConditionalDistribution()
    {
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 3.0;
        return new UnivariateGaussian( mean, variance );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        UnivariateGaussianMeanVarianceBayesianEstimator instance =
            this.createInstance();

        UnivariateGaussian conditional = this.createConditionalDistribution();
        Collection<? extends Double> data = this.createData(conditional);
        NormalInverseGammaDistribution posterior = instance.learn(data);
        assertEquals( conditional.getMean(), posterior.getMean().getElement(0), 1e-1 );
        assertEquals( conditional.getVariance(), posterior.getMean().getElement(1), 1e-2 );

    }

    /**
     * createPredictiveDistribution
     */
    public void testCreatePredictiveDistribution2()
    {
        System.out.println( "createPredictiveDistribution" );

        UnivariateGaussian target = this.createConditionalDistribution();
        ArrayList<? extends Double> samples = target.sample(RANDOM,NUM_SAMPLES);
        UnivariateGaussianMeanVarianceBayesianEstimator instance =
            this.createInstance();
        NormalInverseGammaDistribution posterior = instance.learn(samples);

        StudentTDistribution.PDF predictiveDistribution =
            instance.createPredictiveDistribution(posterior).getProbabilityFunction();

        ArrayList<? extends Vector> parameters =
            posterior.sample(RANDOM,NUM_SAMPLES);

        ArrayList<Double> results = new ArrayList<Double>( samples.size() );
        ArrayList<Double> empiricals = new ArrayList<Double>( samples.size() );
        for( Double sample : samples )
        {
            // This is computing the integral of the predictive distribution
            // p(sample | data) = integral{ p(sample|data,parameter) * p(parameter|data) dparameter }
            // We're using Monte Carlo integration by sampling from the
            // posterior and multiplying by the conditional distribution.
            double sum = 0.0;
            for( Vector parameter : parameters )
            {
                UnivariateGaussian prediction =
                    instance.createConditionalDistribution(parameter);
                sum += prediction.getProbabilityFunction().evaluate( sample );
            }
            double empirical = sum/parameters.size();
            empiricals.add( empirical );
            double estimate = predictiveDistribution.evaluate(sample);
            results.add( estimate );
            results.add( sum / parameters.size() );
        }

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.INSTANCE.evaluateNullHypothesis(results,empiricals);
        System.out.println( "K-S Test: " + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), 0.95 );

    }

}
