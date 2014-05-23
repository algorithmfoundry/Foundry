/*
 * File:                MetropolisHastingsAlgorithmTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 24, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.bayesian.conjugate.BernoulliBayesianEstimator;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.LogNormalDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MetropolisHastingsAlgorithmTest.
 *
 * @author krdixon
 */
public class MetropolisHastingsAlgorithmTest
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
     * Tests for class MetropolisHastingsAlgorithmTest.
     * @param testName Name of the test.
     */
    public MetropolisHastingsAlgorithmTest(
        String testName)
    {
        super(testName);
    }

    /**
     * clone
     */
    public void testClone()
    {
        System.out.println( "clone" );
        MetropolisHastingsAlgorithm<Double,GammaDistribution.PDF> mcmc =
            new MetropolisHastingsAlgorithm<Double,GammaDistribution.PDF>();
        mcmc.setMaxIterations(100);
        mcmc.setBurnInIterations(200);
        mcmc.setIterationsPerSample(10);
        mcmc.setUpdater( new GammaUpdater() );
        mcmc.setRandom(RANDOM);

        MetropolisHastingsAlgorithm<Double,GammaDistribution.PDF> clone = mcmc.clone();
        assertNotSame( mcmc, clone );
        assertNotSame( mcmc.getUpdater(), clone.getUpdater() );
        assertSame( mcmc.getRandom(), clone.getRandom() );
    }

    /**
     * Tests the constructors of class MetropolisHastingsAlgorithmTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MetropolisHastingsAlgorithm<?,?> mcmc = new MetropolisHastingsAlgorithm<Double,GammaDistribution.PDF>();
        assertTrue( mcmc.getBurnInIterations() > 0 );
        assertTrue( mcmc.getIterationsPerSample() > 0 );
    }

    public void testGammaInference()
    {
        System.out.println( "Gamma Distribution Inference" );

        double shape = 5.0;
        double scale = 2.0;
        GammaDistribution.PDF target = new GammaDistribution.PDF( shape, scale );

        final int numSamples = 1000;
        ArrayList<Double> samples = target.sample(RANDOM, numSamples);


        MetropolisHastingsAlgorithm<Double,GammaDistribution.PDF> mcmc =
            new MetropolisHastingsAlgorithm<Double,GammaDistribution.PDF>();
        mcmc.setMaxIterations(numSamples);
        mcmc.setBurnInIterations(200);
        mcmc.setIterationsPerSample(10);
        mcmc.setUpdater( new GammaUpdater() );
        mcmc.setRandom(RANDOM);

        DataDistribution<GammaDistribution.PDF> gammas = mcmc.learn(samples);

        ArrayList<Double> shapes = new ArrayList<Double>( gammas.getDomain().size() );
        ArrayList<Double> scales = new ArrayList<Double>( gammas.getDomain().size() );
        for( GammaDistribution.PDF gamma : gammas.getDomain() )
        {
            shapes.add( gamma.getShape() );
            scales.add( gamma.getScale() );
        }

        UnivariateGaussian shapeResult = UnivariateGaussian.MaximumLikelihoodEstimator.learn(shapes, 0.0);
        UnivariateGaussian scaleResult = UnivariateGaussian.MaximumLikelihoodEstimator.learn(scales, 0.0);
        System.out.println( "Proposals: " + ((GammaUpdater) mcmc.getUpdater()).proposals );
        System.out.println( "Shape: " + shapeResult );
        System.out.println( "Scale: " + scaleResult );

        NamedValue<Double> performance = mcmc.getPerformance();
        assertEquals( MetropolisHastingsAlgorithm.PERFORMANCE_NAME, performance.getName() );

    }

    public void testBernoulliInference()
    {

        System.out.println( "Bernoulli Inference" );

        double p = 0.75;
        BernoulliDistribution.PMF target = new BernoulliDistribution.PMF(p);
        final int numSamples = 1000;
        ArrayList<Number> samples = target.sample(RANDOM, numSamples);

        MetropolisHastingsAlgorithm<Number,BernoulliDistribution.PMF> mcmc =
            new MetropolisHastingsAlgorithm<Number,BernoulliDistribution.PMF>();
        mcmc.setBurnInIterations(1000);
        mcmc.setIterationsPerSample(10);
        mcmc.setUpdater( new BernoulliUpdater() );
        mcmc.setRandom(RANDOM);

        DataDistribution<BernoulliDistribution.PMF> berns = mcmc.learn(samples);
        ArrayList<Double> ps = new ArrayList<Double>( berns.getDomain().size() );
        for( BernoulliDistribution.PMF b : berns.getDomain() )
        {
            ps.add( b.getP() );
        }

        UnivariateGaussian presult = UnivariateGaussian.MaximumLikelihoodEstimator.learn(ps, 0.0);
        System.out.println( "Proposals: " + ((BernoulliUpdater) mcmc.getUpdater()).proposals );
        System.out.println( "P: " + presult );

        // Run this through a Conjugate Prior Bayesian Estimator to see
        // what the answer "should" (with uniform prior)
        BernoulliBayesianEstimator bbe = new BernoulliBayesianEstimator();
        BetaDistribution posterior = bbe.learn(samples);
        System.out.println( "Beta: Mean = " + posterior.getMean() + ", Variance = " + posterior.getVariance() );

    }

    public class GammaUpdater
        extends AbstractCloneableSerializable
        implements MetropolisHastingsAlgorithm.Updater<Double,GammaDistribution.PDF>
    {

        private Distribution<Double> tweaker;

        int proposals;

        public GammaUpdater()
        {
            this.tweaker = new LogNormalDistribution(0.0, 1e-4);
            this.proposals = 0;
        }

        public WeightedValue<GammaDistribution.PDF> makeProposal(
            GammaDistribution.PDF location)
        {
            proposals++;
            double sf1 = this.tweaker.sample(RANDOM);
            double sf2 = this.tweaker.sample(RANDOM);

            GammaDistribution.PDF proposal = new GammaDistribution.PDF(
                sf1 * location.getShape(), sf2 * location.getScale() );

            return new DefaultWeightedValue<GammaDistribution.PDF>( proposal, (sf1*sf2) );
        }

        public GammaDistribution.PDF createInitialParameter()
        {
            return new GammaDistribution.PDF( 1.0, 1.0 );
        }

        public double computeLogLikelihood(
            GammaDistribution.PDF parameter,
            Iterable<? extends Double> data)
        {
            return BayesianUtil.logLikelihood(parameter, data);
        }

    }

    public class BernoulliUpdater
        extends AbstractCloneableSerializable
        implements MetropolisHastingsAlgorithm.Updater<Number,BernoulliDistribution.PMF>
    {

        private Distribution<Double> tweaker;

        protected int proposals;

        public BernoulliUpdater()
        {
            this.tweaker = new LogNormalDistribution(0.0, 1e-2);
            this.proposals = 0;
        }

        public WeightedValue<BernoulliDistribution.PMF> makeProposal(
            BernoulliDistribution.PMF location)
        {
            proposals++;
            double sf1 = this.tweaker.sample(RANDOM);

            double pinv = 1.0 / location.getP() - 1.0;
            pinv *= sf1;
            BernoulliDistribution.PMF proposal =
                new BernoulliDistribution.PMF( 1.0/(pinv+1.0) );
            return new DefaultWeightedValue<BernoulliDistribution.PMF>( proposal, sf1 );
        }

        public BernoulliDistribution.PMF createInitialParameter()
        {
            return new BernoulliDistribution.PMF( 0.5 );
        }

        public double computeLogLikelihood(
            BernoulliDistribution.PMF parameter,
            Iterable<? extends Number> data)
        {
            return BayesianUtil.logLikelihood(parameter, data);
        }

    }

}
