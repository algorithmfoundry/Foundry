/*
 * File:                DirichletProcessMixtureModelTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.PointMassDistribution;
import gov.sandia.cognition.statistics.bayesian.conjugate.MultivariateGaussianMeanBayesianEstimator;
import gov.sandia.cognition.statistics.distribution.MapBasedPointMassDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DirichletProcessMixtureModelTest.
 *
 * @author krdixon
 */
public class DirichletProcessMixtureModelTest
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
     * Number of samples
     */
    public int NUM_SAMPLES = 5;

    /**
     * Dimensionality
     */
    public int DIM = 2;

    /**
     * Tests for class DirichletProcessMixtureModelTest.
     * @param testName Name of the test.
     */
    public DirichletProcessMixtureModelTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Instance
     * @return
     * Instance
     */
    public DirichletProcessMixtureModel<Vector> createInstance()
    {
        DirichletProcessMixtureModel<Vector> instance =
            new DirichletProcessMixtureModel<Vector>();
        instance.setUpdater( new DirichletProcessMixtureModel.MultivariateMeanCovarianceUpdater(DIM) );
        instance.setMaxIterations(100);
        instance.setBurnInIterations(instance.getMaxIterations());
        instance.setIterationsPerSample(2);
        instance.setRandom(RANDOM);
        return instance;
    }


    /**
     * Tests the constructors of class DirichletProcessMixtureModelTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DirichletProcessMixtureModel<Vector> instance =
            new DirichletProcessMixtureModel<Vector>();
        assertNotNull( instance );
        assertNull( instance.getUpdater() );
        assertNull( instance.getRandom() );
        assertTrue( instance.getIterationsPerSample() >= 1 );
        assertTrue( instance.getNumInitialClusters() > 1 );
    }

    /**
     * Test of clone method, of class DirichletProcessMixtureModel.
     */
    public void testClone()
    {
        System.out.println("clone");
        DirichletProcessMixtureModel<Vector> instance = this.createInstance();
        instance.setMaxIterations(10);
        instance.setIterationsPerSample(1);
        instance.setBurnInIterations(1);
        DirichletProcessMixtureModel<Vector> clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone );
        assertNotSame( instance.getUpdater(), clone.getUpdater() );

        double m = 1.0;
        double s = 0.1;
        int N = 2;
        ArrayList<Vector> samples = new ArrayList<Vector>( NUM_SAMPLES*N );
        for( int n = 0; n < N; n++ )
        {
            Vector mean = VectorFactory.getDefault().createVector(DIM, m*n);
            Matrix C = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale(s*s);
            MultivariateGaussian g = new MultivariateGaussian(mean, C);
            samples.addAll( g.sample(RANDOM, 2 ) );
        }

        Random r1 = new Random(1);
        Random r2 = new Random(1);
        instance.setRandom(r1);
        clone.setRandom(r2);

        DataHistogram<DirichletProcessMixtureModel.Sample<Vector>> d1 =
            instance.learn(samples);

        DataHistogram<DirichletProcessMixtureModel.Sample<Vector>> d2 =
            clone.learn(samples);

        assertEquals( d1.getTotalCount(), d2.getTotalCount() );

        Iterator<DirichletProcessMixtureModel.Sample<Vector>> i1 =
            d1.getValues().iterator();
        Iterator<DirichletProcessMixtureModel.Sample<Vector>> i2 =
            d2.getValues().iterator();
        while( i1.hasNext() )
        {
            DirichletProcessMixtureModel.Sample<Vector> s1 = i1.next();
            DirichletProcessMixtureModel.Sample<Vector> s2 = i2.next();
            assertNotSame( s1, s2 );
            assertEquals( s1.getAlpha(), s2.getAlpha(), TOLERANCE );
            assertEquals( s1.getNumClusters(), s2.getNumClusters() );
        }
        

    }

    /**
     * Tests learn
     */
    public void testLearn()
    {
        System.out.println( "Learn" );

        DirichletProcessMixtureModel<Vector> instance = this.createInstance();

        // Serial / Parallel (Thread=1)
        // Best: ll = 1768.1222563043032, k = 4, alpha = 0.04826570450192344

        double m = 1.0;
        double s = 0.1;
        int N = 4;
        ArrayList<Vector> samples = new ArrayList<Vector>( NUM_SAMPLES*N );
        for( int n = 0; n < N; n++ )
        {
            Vector mean = VectorFactory.getDefault().createVector(DIM, m*n);
            Matrix C = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale(s*s);
            MultivariateGaussian g = new MultivariateGaussian(mean, C);
            samples.addAll( g.sample(RANDOM, NUM_SAMPLES ) );
        }
        long start = System.currentTimeMillis();
        DataHistogram<DirichletProcessMixtureModel.Sample<Vector>> results =
            instance.learn(samples);
        long stop = System.currentTimeMillis();
        System.out.println( "Time taken: " + (stop-start)/1000.0);

        PointMassDistribution.PMF<Double> ks = new MapBasedPointMassDistribution.PMF<Double>();
        DirichletProcessMixtureModel.Sample<Vector> bestSample = null;
        double maxLL = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        int index = 0;
        for( DirichletProcessMixtureModel.Sample<Vector> result : results.getValues() )
        {
            ks.add( (double) result.getNumClusters() );
            Double ll = result.getPosteriorLogLikelihood();
            double actualLL = result.computePosteriorLogLikelihood(samples);
            if( ll != null )
            {
                assertEquals( index + ": expected " + actualLL + ", got: " + ll, actualLL, ll, TOLERANCE );
            }
            if( (ll != null) && (maxLL < ll) )
            {
                maxIndex = index;
                maxLL = ll;
                bestSample = result;
            }
            index++;
        }

        for( Double k : ks.getDomain() )
        {
            double pk = ks.evaluate(k);
            if( pk > 0.0 )
            {
                System.out.println( "p(" + k + "):" + pk );
            }
        }

        System.out.println( "Mean k = " + ks.getMean() );

        System.out.println( "Best: " + maxIndex + ": ll = " + maxLL + ", k = " + bestSample.getNumClusters() + ", alpha = " + bestSample.getAlpha() );
        for( int i = 0; i < bestSample.getNumClusters(); i++ )
        {
            System.out.println( "Members = " + bestSample.getClusters().get(i).getMembers().size() );
            System.out.println( "PDF =\n" + bestSample.getClusters().get(i).getProbabilityFunction() );
        }

    }


    /**
     * Tests learn
     */
    public void testLearnConstantVariance()
    {
        System.out.println( "Learn Constant Variance" );
        double m = 1.0;
        double s = 0.1;
        int N = 4;

        DirichletProcessMixtureModel<Vector> instance = this.createInstance();
        Matrix Ci = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale(s*s).inverse();
        DirichletProcessMixtureModel.MultivariateMeanUpdater updater =
            new DirichletProcessMixtureModel.MultivariateMeanUpdater(
                new MultivariateGaussianMeanBayesianEstimator( Ci ) );
        instance.setUpdater( updater );

        ArrayList<Vector> samples = new ArrayList<Vector>( NUM_SAMPLES*N );
        for( int n = 0; n < N; n++ )
        {
            Vector mean = VectorFactory.getDefault().createVector(DIM, m*n);
            Matrix C = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale(s*s);
            MultivariateGaussian g = new MultivariateGaussian(mean, C);
            samples.addAll( g.sample(RANDOM, NUM_SAMPLES ) );
        }
        long start = System.currentTimeMillis();
        DataHistogram<DirichletProcessMixtureModel.Sample<Vector>> results =
            instance.learn(samples);
        long stop = System.currentTimeMillis();
        System.out.println( "Time taken: " + (stop-start)/1000.0);

        PointMassDistribution.PMF<Double> ks = new MapBasedPointMassDistribution.PMF<Double>();
        DirichletProcessMixtureModel.Sample<Vector> bestSample = null;
        double maxLL = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        int index = 0;
        for( DirichletProcessMixtureModel.Sample<Vector> result : results.getValues() )
        {
            ks.add( (double) result.getNumClusters() );
            Double ll = result.getPosteriorLogLikelihood();
            if( (ll != null) && (maxLL < ll) )
            {
                maxIndex = index;
                maxLL = ll;
                bestSample = result;
            }
            index++;
        }

        for( Double k : ks.getDomain() )
        {
            double pk = ks.evaluate(k);
            if( pk > 0.0 )
            {
                System.out.println( "p(" + k + "):" + pk );
            }
        }

        System.out.println( "Mean k = " + ks.getMean() );

        System.out.println( "Best: " + maxIndex + ", ll = " + maxLL + ", k = " + bestSample.getNumClusters() + ", alpha = " + bestSample.getAlpha() );
        for( int i = 0; i < bestSample.getNumClusters(); i++ )
        {
            System.out.println( "Members = " + bestSample.getClusters().get(i).getMembers().size() );
            System.out.println( "PDF =\n" + bestSample.getClusters().get(i).getProbabilityFunction() );
        }

    }


}
