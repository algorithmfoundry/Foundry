/*
 * File:                ImportanceSamplingTest.java
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
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.PointMassDistribution;
import gov.sandia.cognition.statistics.distribution.BernoulliDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.LogNormalDistribution;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for ImportanceSamplingTest.
 *
 * @author krdixon
 */
public class ImportanceSamplingTest
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
     * Tests for class ImportanceSamplingTest.
     * @param testName Name of the test.
     */
    public ImportanceSamplingTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class ImportanceSamplingTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        ImportanceSampling<Integer,Double> instance =
            new ImportanceSampling<Integer,Double>();
        assertEquals( ImportanceSampling.DEFAULT_NUM_SAMPLES, instance.getNumSamples() );
        assertNull( instance.getUpdater() );
        assertNull( instance.getRandom() );
    }

    /**
     * Test of clone method, of class ImportanceSampling.
     */
    public void testClone()
    {
        System.out.println("clone");
        ImportanceSampling<Integer,Double> instance =
            new ImportanceSampling<Integer,Double>();
        instance.setRandom(RANDOM);
        instance.setNumSamples(1000);
        instance.setUpdater( new ImportanceSampling.DefaultUpdater<Integer,Double>() );
        ImportanceSampling<Integer,Double> clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getUpdater(), clone.getUpdater() );
        assertNotNull( clone.getUpdater() );
    }

    /**
     * Bernoulli Inference
     */
    public void testBernoulliInference()
    {

        System.out.println( "Bernoulli Inference" );

        double p = 0.75;
        BernoulliDistribution.PMF target = new BernoulliDistribution.PMF(p);
        final int numSamples = 1000;
        ArrayList<? extends Number> samples = target.sample(RANDOM, numSamples);

        ImportanceSampling<Number,Double> instance =
            new ImportanceSampling<Number, Double>();
        instance.setNumSamples(1000);
        instance.setRandom(RANDOM);

        BernoulliDistribution.PMF pmf = new BernoulliDistribution.PMF();
        ProbabilityFunction<Double> prior = new UniformDistribution.PDF( 0.0, 1.0 );
        ImportanceSampling.DefaultUpdater<Number,Double> updater = new ImportanceSampling.DefaultUpdater<Number,Double>(
            new DefaultBayesianParameter<Double,BernoulliDistribution.PMF,ProbabilityFunction<Double>>( pmf, "p", prior ) );
        assertNotNull( updater.getConjuctive() );
        instance.setUpdater( updater );

        PointMassDistribution<Double> result = instance.learn(samples);
        ArrayList<WeightedValue<Double>> ps =
            new ArrayList<WeightedValue<Double>>( result.getDomain().size() );
        for( Double pvalue : result.getDomain() )
        {
            ps.add( new DefaultWeightedValue<Double>( pvalue, result.getMass(pvalue) ) );
        }

        System.out.println( "Nonzero = " + result.getDomain().size() );
        UnivariateGaussian presult =
            UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(ps, 0.0);
        System.out.println( "Presult: " + presult );

        BernoulliBayesianEstimator bbe = new BernoulliBayesianEstimator();
        BetaDistribution posterior = bbe.learn(samples);
        System.out.println( "Beta: Mean = " + posterior.getMean() + ", Variance = " + posterior.getVariance() );

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

        ImportanceSampling<Double,GammaDistribution.PDF> instance =
            new ImportanceSampling<Double, GammaDistribution.PDF>();
        instance.setRandom(RANDOM);
        instance.setNumSamples(1000);
        instance.setUpdater(new GammaUpdater() );

        PointMassDistribution<GammaDistribution.PDF> result =
            instance.learn(samples);

        ArrayList<WeightedValue<Double>> shapes =
            new ArrayList<WeightedValue<Double>>( result.getDomain().size() );
        ArrayList<WeightedValue<Double>> scales =
            new ArrayList<WeightedValue<Double>>( result.getDomain().size() );
        for( GammaDistribution.PDF gamma : result.getDomain() )
        {
            shapes.add( new DefaultWeightedValue<Double>( gamma.getShape(), result.getMass(gamma) ) );
            scales.add( new DefaultWeightedValue<Double>( gamma.getScale(), result.getMass(gamma) ) );
        }

        System.out.println( "Nonzero = " + result.getDomain().size() );
        UnivariateGaussian shapeResult = UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(shapes, 0.0);
        UnivariateGaussian scaleResult = UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(scales, 0.0);
        System.out.println( "Shape: " + shapeResult );
        System.out.println( "Scale: " + scaleResult );
        System.out.println( "Target: " + target );
    }


    public class GammaUpdater
        extends AbstractCloneableSerializable
        implements ImportanceSampling.Updater<Double,GammaDistribution.PDF>
    {

        private ProbabilityFunction<Double> tweaker;

        public GammaUpdater()
        {
            this.tweaker = new LogNormalDistribution.PDF(1.0, 1.0);
        }

        public GammaDistribution.PDF update(
            GammaDistribution.PDF previousParameter)
        {
            double sf1 = this.tweaker.sample(RANDOM);
            double sf2 = this.tweaker.sample(RANDOM);

            return new GammaDistribution.PDF(
                sf1 * previousParameter.getShape(), sf2 * previousParameter.getScale() );
        }

        public double computeLogLikelihood(
            GammaDistribution.PDF parameter,
            Iterable<? extends Double> data)
        {
            return BayesianUtil.logLikelihood(parameter, data);
        }

        public double computeLogImportanceValue(
            GammaDistribution.PDF parameter)
        {
            return this.tweaker.logEvaluate(parameter.getShape()) +
                this.tweaker.logEvaluate( parameter.getScale() );
        }

        public GammaDistribution.PDF makeProposal(
            Random random)
        {
            return new GammaDistribution.PDF(
                this.tweaker.sample(random),
                this.tweaker.sample(random) );
        }

    }

}
