/*
 * File:                SamplingImportanceResamplingParticleFilterTest.java
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
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.PointMassDistribution;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution.PMF;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.LogNormalDistribution;
import gov.sandia.cognition.statistics.distribution.MapBasedPointMassDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;

/**
 * Unit tests for SamplingImportanceResamplingParticleFilterTest.
 *
 * @author krdixon
 */
public class SamplingImportanceResamplingParticleFilterTest
    extends RecursiveBayesianEstimatorTestHarness<Double, Double, PointMassDistribution<Double>>
{

    /**
     * Tests for class SamplingImportanceResamplingParticleFilterTest.
     * @param testName Name of the test.
     */
    public SamplingImportanceResamplingParticleFilterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class SamplingImportanceResamplingParticleFilterTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        SamplingImportanceResamplingParticleFilter<Double,GammaDistribution.PDF> particleFilter =
            new SamplingImportanceResamplingParticleFilter<Double, GammaDistribution.PDF>();
        assertNotNull( particleFilter );
    }

    /**
     * clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        SamplingImportanceResamplingParticleFilter<Double,GammaDistribution.PDF> particleFilter =
            new SamplingImportanceResamplingParticleFilter<Double, GammaDistribution.PDF>();
        particleFilter.setRandom(RANDOM);
        particleFilter.setNumParticles(200);
        particleFilter.setParticlePctThreadhold(0.5);
        particleFilter.setUpdater(new GammaUpdater() );

        SamplingImportanceResamplingParticleFilter<Double,GammaDistribution.PDF> clone =
            (SamplingImportanceResamplingParticleFilter<Double, GammaDistribution.PDF>) particleFilter.clone();
        assertNotNull( clone );
        assertNotSame( clone, particleFilter );
        assertSame( particleFilter.getRandom(), clone.getRandom() );
        assertNotSame( particleFilter.getUpdater(), clone.getUpdater() );
        assertEquals( particleFilter.getParticlePctThreadhold(), clone.getParticlePctThreadhold() );
        assertEquals( particleFilter.getNumParticles(), clone.getNumParticles() );

    }

    /**
     * Test Gamma Distribution
     */
    public void testGammaInference()
    {
        System.out.println( "Gamma Distribution Inference" );

        double shape = 5.0;
        double scale = 2.0;
        GammaDistribution.PDF target = new GammaDistribution.PDF( shape, scale );

        final int numSamples = 1000;
        ArrayList<Double> samples = target.sample(RANDOM, numSamples);

        SamplingImportanceResamplingParticleFilter<Double,GammaDistribution.PDF> particleFilter =
            new SamplingImportanceResamplingParticleFilter<Double, GammaDistribution.PDF>();
        particleFilter.setRandom(RANDOM);
        particleFilter.setNumParticles(200);
        particleFilter.setParticlePctThreadhold(0.5);
        particleFilter.setUpdater(new GammaUpdater() );

        PointMassDistribution<GammaDistribution.PDF> particles =
            particleFilter.learn(samples);

        ArrayList<WeightedValue<Double>> shapes =
            new ArrayList<WeightedValue<Double>>( particles.getDomain().size() );
        ArrayList<WeightedValue<Double>> scales =
            new ArrayList<WeightedValue<Double>>( particles.getDomain().size() );
        for( GammaDistribution.PDF gamma : particles.getDomain() )
        {
            shapes.add( new DefaultWeightedValue<Double>( gamma.getShape(), particles.getMass(gamma) ) );
            scales.add( new DefaultWeightedValue<Double>( gamma.getScale(), particles.getMass(gamma) ) );
        }

        UnivariateGaussian shapeResult = UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(shapes, 0.0);
        UnivariateGaussian scaleResult = UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(scales, 0.0);
        System.out.println( "Shape: " + shapeResult );
        System.out.println( "Scale: " + scaleResult );
        System.out.println( "Target: " + target );
    }

    @Override
    public SamplingImportanceResamplingParticleFilter<Double,Double> createInstance()
    {
        SamplingImportanceResamplingParticleFilter<Double,Double> particleFilter =
            new SamplingImportanceResamplingParticleFilter<Double,Double>();
        particleFilter.setRandom(RANDOM);
        particleFilter.setNumParticles(100);
        particleFilter.setParticlePctThreadhold(0.5);
        particleFilter.setUpdater( new GaussianUpdater() );
        return particleFilter;
    }

    @Override
    public Distribution<Double> createConditionalDistribution()
    {
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble() * 2.0 + 1.0;
        return new UnivariateGaussian( mean, variance );
    }

    public class GammaUpdater
        extends AbstractCloneableSerializable
        implements ParticleFilter.Updater<Double,GammaDistribution.PDF>
    {

        private GammaDistribution.PDF initialDistribution;

        private Distribution<Double> tweaker;

        public GammaUpdater()
        {
            this.initialDistribution = new GammaDistribution.PDF( 2.0, 1.0 );
            this.tweaker = new LogNormalDistribution(0.0, 1e-4);
        }

        public GammaDistribution.PDF update(
            GammaDistribution.PDF previousParameter)
        {
            double sf1 = this.tweaker.sample(RANDOM);
            double sf2 = this.tweaker.sample(RANDOM);

            return new GammaDistribution.PDF(
                sf1 * previousParameter.getShape(), sf2 * previousParameter.getScale() );
        }

        public PointMassDistribution<GammaDistribution.PDF> createInitialParticles(
            int numParticles)
        {
            PointMassDistribution<GammaDistribution.PDF> distribution =
                new MapBasedPointMassDistribution<GammaDistribution.PDF>();
            final double uniformWeight = 1.0/numParticles;
            for( int i = 0; i < numParticles; i++ )
            {
                distribution.add( this.update(this.initialDistribution), uniformWeight );
            }

            return distribution;
        }

        public double computeLogLikelihood(
            GammaDistribution.PDF particle,
            Double observation)
        {
            return particle.logEvaluate(observation);
        }

    }

    public void testKnownValues()
    {
        System.out.println( "Bernoulli Inference" );

        double p = 0.75;
        BernoulliDistribution.PMF target = new BernoulliDistribution.PMF(p);
        final int numSamples = 1000;
        ArrayList<Integer> samples = target.sample(RANDOM, numSamples);

        SamplingImportanceResamplingParticleFilter<Integer,BernoulliDistribution.PMF> particleFilter =
            new SamplingImportanceResamplingParticleFilter<Integer,BernoulliDistribution.PMF>();
        particleFilter.setRandom(RANDOM);
        particleFilter.setNumParticles(200);
        particleFilter.setParticlePctThreadhold(0.5);
        particleFilter.setUpdater(new BernoulliUpdater() );

        PointMassDistribution<BernoulliDistribution.PMF> particles =
            particleFilter.learn(samples);

        ArrayList<WeightedValue<Double>> ps =
            new ArrayList<WeightedValue<Double>>( particles.getDomain().size() );
        for( BernoulliDistribution.PMF b : particles.getDomain() )
        {
            ps.add( new DefaultWeightedValue<Double>( b.getP(), particles.getMass(b) ) );
        }

        UnivariateGaussian presult =
            UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(ps, 0.0);
        System.out.println( "Presult: " + presult );

        BernoulliBayesianEstimator bbe = new BernoulliBayesianEstimator();
        BetaDistribution posterior = bbe.learn(samples);
        System.out.println( "Beta: Mean = " + posterior.getMean() + ", Variance = " + posterior.getVariance() );

    }

    public void testBernoulliInference2()
    {
        System.out.println( "Bernoulli Inference2" );

        double p = 0.75;
        BernoulliDistribution.PMF target = new BernoulliDistribution.PMF(p);
        final int numSamples = 100;
        ArrayList<Integer> samples = target.sample(RANDOM, numSamples);

        SamplingImportanceResamplingParticleFilter<Integer,BernoulliDistribution.PMF> particleFilter =
            new SamplingImportanceResamplingParticleFilter<Integer,BernoulliDistribution.PMF>();
        particleFilter.setRandom(RANDOM);
        particleFilter.setNumParticles(100);
        particleFilter.setParticlePctThreadhold(1.0);
        particleFilter.setUpdater(new BernoulliUpdater() );

        PointMassDistribution<BernoulliDistribution.PMF> particles =
            particleFilter.learn(samples);

        ArrayList<WeightedValue<Double>> ps =
            new ArrayList<WeightedValue<Double>>( particles.getDomain().size() );
        for( BernoulliDistribution.PMF b : particles.getDomain() )
        {
            ps.add( new DefaultWeightedValue<Double>( b.getP(), particles.getMass(b) ) );
        }

        UnivariateGaussian presult =
            UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(ps, 0.0);
        System.out.println( "Presult: " + presult );

        BernoulliBayesianEstimator bbe = new BernoulliBayesianEstimator();
        BetaDistribution posterior = bbe.learn(samples);
        System.out.println( "Beta: Mean = " + posterior.getMean() + ", Variance = " + posterior.getVariance() );

    }

    public class GaussianUpdater
        extends AbstractCloneableSerializable
        implements ParticleFilter.Updater<Double,Double>
    {

        UnivariateGaussian tweaker;
        double variance;

        public GaussianUpdater()
        {
            this.tweaker = new UnivariateGaussian();
            this.variance = 2.0;
        }

        public Double update(
            Double previousParameter)
        {
            return tweaker.sample(RANDOM) + previousParameter;
        }

        public PointMassDistribution<Double> createInitialParticles(
            int numParticles)
        {
            return new MapBasedPointMassDistribution<Double>(
                this.tweaker.sample(RANDOM, numParticles) );
        }

        public double computeLogLikelihood(
            Double particle,
            Double observation)
        {
            return UnivariateGaussian.PDF.logEvaluate(
                observation, particle, this.variance );
        }

    }

    public class BernoulliUpdater
        extends AbstractCloneableSerializable
        implements ParticleFilter.Updater<Integer,BernoulliDistribution.PMF>
    {

        private Distribution<Double> tweaker;

        public BernoulliUpdater()
        {
            this.tweaker = new LogNormalDistribution(0.0, 1e-4);
        }

        public BernoulliDistribution.PMF update(
            BernoulliDistribution.PMF previousParameter)
        {
            double sf1 = this.tweaker.sample(RANDOM);
            double pinv = 1.0 / previousParameter.getP() - 1.0;
            pinv *= sf1;
            return new BernoulliDistribution.PMF( 1.0/(pinv+1.0) );
        }

        public PointMassDistribution<BernoulliDistribution.PMF> createInitialParticles(
            int numParticles)
        {
            PointMassDistribution<BernoulliDistribution.PMF> particles =
                new MapBasedPointMassDistribution<PMF>();
            final double uniformMass = 1.0/numParticles;
            for( int i = 0; i < numParticles; i++ )
            {
                double p = RANDOM.nextDouble();
                particles.add( new BernoulliDistribution.PMF( p ), uniformMass );
            }
            return particles;
        }

        public double computeLogLikelihood(
            BernoulliDistribution.PMF particle,
            Integer observation)
        {
            return particle.logEvaluate(observation);
        }
        
    }

}
