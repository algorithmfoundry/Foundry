/*
 * File:                MixtureOfGaussiansTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.learning.algorithm.clustering.KMeansClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.GaussianClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.NeighborhoodGaussianClusterInitializer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.util.NamedValue;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test for class MixtureOfGaussians
 * @author krdixon
 */
public class MixtureOfGaussiansTest
    extends MultivariateMixtureDensityModelTest
{

    public MixtureOfGaussiansTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public MixtureOfGaussians.PDF createInstance()
    {
        return createMixture(3, 2, RANDOM);
    }    

    /**
     * Tests the Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        int N = 2;
        double r = 2.0;
        double rC = 0.5;
        Matrix s1 = MatrixFactory.getDefault().createUniformRandom( N, N, -rC, rC, RANDOM );
        Matrix C1 = s1.transpose().times( s1 );
        Vector m1 = VectorFactory.getDefault().createUniformRandom(N, -r, r, RANDOM );

        Matrix s2 = MatrixFactory.getDefault().createUniformRandom( N, N, -rC, rC, RANDOM );
        Matrix C2 = s2.transpose().times( s2 );
        Vector m2 = VectorFactory.getDefault().createUniformRandom(N, -r, r, RANDOM );

        double p1 = RANDOM.nextDouble();
        double[] p = new double[]{ p1, 1.0-p1 };
        ArrayList<MultivariateGaussian.PDF> gs = new ArrayList<MultivariateGaussian.PDF>(
            Arrays.asList( new MultivariateGaussian.PDF( m1, C1 ),
            new MultivariateGaussian.PDF( m2, C2 ) ) );

        MixtureOfGaussians.PDF mog = new MixtureOfGaussians.PDF( gs, p );

        ArrayList<Vector> samples = mog.sample(RANDOM, 10*NUM_SAMPLES);

        System.out.println( "MOG: " + mog );


        int k = 2;
        KMeansClusterer<Vector,GaussianCluster> kmeans =
            new KMeansClusterer<Vector,GaussianCluster>( k, 100,
            new NeighborhoodGaussianClusterInitializer(RANDOM),
            GaussianClusterDivergenceFunction.INSTANCE,
            new GaussianClusterCreator() );
        
        MixtureOfGaussians.Learner learner =
            new MixtureOfGaussians.Learner( kmeans );

        learner.learn(samples);

        MixtureOfGaussians.PDF moghat = learner.getResult();
        System.out.println( "moghat: " + moghat );

        assertEquals( 2, moghat.getDistributionCount() );

        // This is kludgey, but I can't think of a better way to get the
        // automated test that I'm looking for...
        //   -- krdixon, 2009-03-04
//        Vector phat = moghat.getPriorProbabilities();
//        System.out.println( "Delta: " + p.minus(phat) );
//        assertTrue( p.equals( phat, 1e-2 ) );
//        Vector m1hat = moghat.getRandomVariables().get(0).getMean();
//        assertTrue( m1.equals( m1hat, 1e-2 ) );
//
//        Vector m2hat = moghat.getRandomVariables().get(1).getMean();
//        assertTrue( m2.equals( m2hat, 1e-2 ) );

        NamedValue<? extends Number> value = learner.getPerformance();
        assertNotNull( value );
        System.out.println( "Value: " + value.getName() + " = " + value.getValue() );

        MixtureOfGaussians.Learner l2 = new MixtureOfGaussians.Learner(null);
        try
        {
            l2.getResult();
            fail( "Null pointer exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    public void testComputeWeightedZ()
    {
        System.out.println( "computeWeightedZ" );

        int numGaussians = 2;
        int numDimensions = 2;
        MixtureOfGaussians.PDF mog = createMixture(numGaussians, numDimensions, RANDOM);

        ArrayList<Vector> samples = mog.sample(RANDOM, NUM_SAMPLES);
        ArrayList<Double> zs = new ArrayList<Double>( samples.size() );
        for( Vector input : samples )
        {
            double z = mog.computeWeightedZSquared(input);
            zs.add( z );
        }

        ChiSquareDistribution.CDF chi = new ChiSquareDistribution.CDF(numDimensions);

        KolmogorovSmirnovConfidence.Statistic kstest = 
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(zs, chi);
        System.out.println( "K-S test: " + kstest );
        final double confidence = 0.95;
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), confidence );

    }

    /**
     * Test of fitSingleGaussian method, of class gov.sandia.isrc.math.MultivariateGaussian.
     */
    public void testFitSingleGaussian()
    {
        System.out.println( "fitSingleGaussian" );

        int num = (int) (this.RANDOM.nextDouble() * 5) + 2;
        int dim = 2;

        MixtureOfGaussians.PDF mixture = MixtureOfGaussiansTest.createMixture( num, dim, RANDOM );

        ArrayList<Vector> draws = mixture.sample( RANDOM, NUM_SAMPLES );


        MultivariateGaussian sample =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn( draws, 0.0 );
        MultivariateGaussian result = mixture.fitSingleGaussian();


        double tolerance = 10 * dim * dim / (2 * Math.log( NUM_SAMPLES ));
        double EPSILON = 1e-5;
        System.out.println( "Tolerance = " + tolerance );
        System.out.println( "Mean: Mixture: " + mixture.getMean() + " Result: " + result.getMean() + " Sample: " + sample.getMean() );
        assertTrue( mixture.getMean().equals( result.getMean(), EPSILON ) );
        assertTrue( sample.getMean().equals( result.getMean(), tolerance ) );

        System.out.println( "Covariance:\nResult:\n" + result.getCovariance() + "\nSample:\n" + sample.getCovariance() );
        System.out.println( "Tolerance: " + tolerance + " Residual:\n" + result.getCovariance().minus( sample.getCovariance() ) );
        assertTrue( sample.getCovariance().equals( result.getCovariance(), tolerance ) );

    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateGaussian g1 = new MultivariateGaussian();
        MixtureOfGaussians.PDF instance =
            new MixtureOfGaussians.PDF ( g1 );
        assertEquals( 1, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertEquals( 1.0, instance.getPriorWeights()[0] );

        MultivariateGaussian g2 = new MultivariateGaussian();
        MixtureOfGaussians.PDF  i2 =
            new MixtureOfGaussians.PDF( g1, g2 );

        instance = new MixtureOfGaussians.PDF( i2 );
        assertEquals( i2.getDistributionCount(), instance.getDistributionCount() );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertEquals( i2.getPriorWeightSum(), instance.getPriorWeightSum() );

    }

    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF Constructors" );

        MultivariateGaussian g1 = new MultivariateGaussian();
        MultivariateMixtureDensityModel.PDF<MultivariateGaussian> instance =
            new MultivariateMixtureDensityModel.PDF<MultivariateGaussian>( g1 );
        assertEquals( 1, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertEquals( 1.0, instance.getPriorWeights()[0] );

        MultivariateGaussian g2 = new MultivariateGaussian();
        MultivariateMixtureDensityModel.PDF<MultivariateGaussian> i2 =
            new MultivariateMixtureDensityModel.PDF<MultivariateGaussian>( g1, g2 );

        instance = new MultivariateMixtureDensityModel.PDF<MultivariateGaussian>( i2 );
        assertEquals( i2.getDistributionCount(), instance.getDistributionCount() );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertEquals( i2.getPriorWeightSum(), instance.getPriorWeightSum() );
    }



    /**
     * EMLearner 2 Gaussians
     */
    public void testEMLearner2Gaussians()
    {
        System.out.println( "EMLearner 2 Gaussians" );

        ArrayList<MultivariateGaussian.PDF> gs = new ArrayList<MultivariateGaussian.PDF>( 2 );
        int dim = 2;
        gs.add( new MultivariateGaussian.PDF( VectorFactory.getDefault().createVector(dim, -1.0),
            MatrixFactory.getDefault().createIdentity(dim, dim) ) );
        gs.add( new MultivariateGaussian.PDF( VectorFactory.getDefault().createVector(dim, 1.0),
            MatrixFactory.getDefault().createIdentity(dim, dim) ) );

        MixtureOfGaussians.PDF target = new MixtureOfGaussians.PDF( gs );
        ArrayList<Vector> samples = target.sample(RANDOM, NUM_SAMPLES);

        MixtureOfGaussians.EMLearner learner =
            new MixtureOfGaussians.EMLearner( 2, RANDOM );
        MixtureOfGaussians.PDF estimate = learner.learn(samples);
        

    }


}
