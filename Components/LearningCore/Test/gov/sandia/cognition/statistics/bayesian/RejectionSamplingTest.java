/*
 * File:                RejectionSamplingTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 3, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.bayesian.conjugate.BernoulliBayesianEstimator;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for RejectionSamplingTest.
 *
 * @author krdixon
 */
public class RejectionSamplingTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 2 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class RejectionSamplingTest.
     * @param testName Name of the test.
     */
    public RejectionSamplingTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class RejectionSamplingTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        RejectionSampling<Integer,Double> instance =
            new RejectionSampling<Integer,Double>();
        assertEquals( RejectionSampling.DEFAULT_NUM_SAMPLES, instance.getNumSamples() );
        assertNull( instance.getRandom() );
        assertNull( instance.getUpdater() );
    }

    /**
     * Test of clone method, of class RejectionSampling.
     */
    public void testClone()
    {
        System.out.println("clone");
        RejectionSampling<Number,Double> instance =
            new RejectionSampling<Number, Double>();
        instance.setNumSamples(11);
        UniformDistribution.PDF prior = new UniformDistribution.PDF( 0.0, 1.0 );
        BernoulliDistribution.PMF conditional = new BernoulliDistribution.PMF();
        instance.setUpdater( new RejectionSampling.DefaultUpdater<Number, Double>(
            new DefaultBayesianParameter<Double,BernoulliDistribution.PMF,UniformDistribution.PDF>( conditional, "p", prior ) ) );
        instance.setRandom(RANDOM);

        RejectionSampling<Number,Double> clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getRandom(), clone.getRandom() );
        assertNotNull( clone.getUpdater() );
        assertNotSame( instance.getUpdater(), clone.getUpdater() );
        assertEquals( instance.getNumSamples(), clone.getNumSamples() );

    }


    public void testBernoulliInference()
    {
        System.out.println( "Bernoulli Inference" );

        double p = 0.75;
        BernoulliDistribution.PMF target = new BernoulliDistribution.PMF(p);
        final int numSamples = 100;
        ArrayList<Integer> samples = target.sample(RANDOM, numSamples);

        RejectionSampling<Number,Double> instance =
            new RejectionSampling<Number, Double>();
        instance.setNumSamples(numSamples);
        UniformDistribution.PDF prior = new UniformDistribution.PDF( 0.0, 1.0 );
        BernoulliDistribution.PMF conditional = new BernoulliDistribution.PMF();
        instance.setUpdater( new RejectionSampling.DefaultUpdater<Number, Double>(
            new DefaultBayesianParameter<Double,BernoulliDistribution.PMF,UniformDistribution.PDF>( conditional, "p", prior ) ) );
        instance.setRandom(RANDOM);

        DataDistribution<Double> berns = instance.learn(samples);
        ArrayList<Double> ps = new ArrayList<Double>( berns.getDomain().size() );
        for( Double b : berns.getDomain() )
        {
            ps.add( b );
        }

        UnivariateGaussian presult =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(ps, 0.0);
        System.out.println( "Proposals: " + ((RejectionSampling.DefaultUpdater) instance.getUpdater()).getProposals() );
        System.out.println( "P: " + presult );

        // Run this through a Conjugate Prior Bayesian Estimator to see
        // what the answer "should" (with uniform prior)
        BernoulliBayesianEstimator bbe = new BernoulliBayesianEstimator();
        BetaDistribution posterior = bbe.learn(samples);
        System.out.println( "Beta: Mean = " + posterior.getMean() + ", Variance = " + posterior.getVariance() );

    }

    /**
     * computeGaussianSampler
     */
    public void testComputeGaussianSampler()
    {
        System.out.println( "computeGaussianSampler" );

        double p = 0.75;
        BernoulliDistribution.PMF target = new BernoulliDistribution.PMF(p);
        final int numSamples = 100;
        ArrayList<Integer> samples = target.sample(RANDOM, numSamples);

        RejectionSampling<Number,Double> instance =
            new RejectionSampling<Number, Double>();
        instance.setNumSamples(numSamples);
        UniformDistribution.PDF prior = new UniformDistribution.PDF( 0.0, 1.0 );
        BernoulliDistribution.PMF conditional = new BernoulliDistribution.PMF();
        BayesianParameter<Double,BernoulliDistribution.PMF,UniformDistribution.PDF> conjunctive =
            new DefaultBayesianParameter<Double,BernoulliDistribution.PMF,UniformDistribution.PDF>( conditional, "p", prior );
        RejectionSampling.DefaultUpdater<Number,Double> updater =
            new RejectionSampling.DefaultUpdater<Number, Double>( conjunctive );
        UnivariateGaussian.PDF sampler =
            updater.computeGaussianSampler(samples, RANDOM, 100);
        System.out.println( "Sampler = " + sampler );
        updater.setSampler(sampler);
        instance.setUpdater(updater);
        instance.setRandom(RANDOM);

        DataDistribution<Double> berns = instance.learn(samples);
        ArrayList<Double> ps = new ArrayList<Double>( berns.getDomain().size() );
        for( Double b : berns.getDomain() )
        {
            ps.add( b );
        }

        UnivariateGaussian presult =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(ps, 0.0);
        System.out.println( "Proposals: " + ((RejectionSampling.DefaultUpdater) instance.getUpdater()).getProposals() );
        System.out.println( "P: " + presult );

        // Run this through a Conjugate Prior Bayesian Estimator to see
        // what the answer "should" (with uniform prior)
        BernoulliBayesianEstimator bbe = new BernoulliBayesianEstimator();
        BetaDistribution posterior = bbe.learn(samples);
        System.out.println( "Beta: Mean = " + posterior.getMean() + ", Variance = " + posterior.getVariance() );        

    }

}
