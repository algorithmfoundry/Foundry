/*
 * File:                GaussianContextRecognizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 3, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.statistics.distribution.MixtureOfGaussians;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class GaussianContextRecognizerTest extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random(1);
    
    public GaussianContextRecognizerTest(String testName)
    {
        super(testName);
    }

    public GaussianContextRecognizer createInstance()
    {
        int M = 1;
        int K = 2;

        ArrayList<MultivariateGaussian.PDF> gaussians =
            new ArrayList<MultivariateGaussian.PDF>(K);
        double r = 1.0;
        Vector prior = VectorFactory.getDefault().createUniformRandom(K, 0, 1.0, random);
        for (int k = 0; k < K; k++)
        {
            Matrix covSqrt = MatrixFactory.getDefault().createUniformRandom(M, M, -r, r, random);
            Vector mean = VectorFactory.getDefault().createUniformRandom(M, -r, r, random);
            Matrix covariance = covSqrt.transpose().times(covSqrt);
            gaussians.add(new MultivariateGaussian.PDF(mean, covariance));
        }
        return new GaussianContextRecognizer(
            new MixtureOfGaussians(gaussians, prior));
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.GaussianContextRecognizer.
     */
    public void testClone()
    {
        System.out.println("clone");

        GaussianContextRecognizer instance = this.createInstance();

        GaussianContextRecognizer clone = instance.clone();
        assertEquals(instance.getGaussianMixture().getRandomVariables(), clone.getGaussianMixture().getRandomVariables());
        assertNotSame(instance.getGaussianMixture(), clone.getGaussianMixture());

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.GaussianContextRecognizer.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        GaussianContextRecognizer instance = this.createInstance();
        MixtureOfGaussians mog = instance.getGaussianMixture().clone();
        int M = mog.getDimensionality();
        double r = 1.0;

        for (int i = 0; i < 100; i++)
        {
            Vector input = VectorFactory.getDefault().createUniformRandom(M, -r, r, random);

            assertEquals(mog.computeRandomVariableProbabilities(input), instance.evaluate(input));
        }

    }

    /**
     * Test of consumeClusters method, of class gov.sandia.cognition.learning.util.function.GaussianContextRecognizer.
     */
    public void testConsumeClusters()
    {
        System.out.println("consumeClusters");

        GaussianContextRecognizer instance = this.createInstance();

        int M = instance.getGaussianMixture().getDimensionality();
        int K = random.nextInt(10) + 2;
        double r = 1.0;
        ArrayList<GaussianCluster> clusters = new ArrayList<GaussianCluster>(K);
        for (int k = 0; k < K; k++)
        {
            int N = random.nextInt( 100 ) + 2 * M;
            ArrayList<Vector> members = new ArrayList<Vector>(N);
            for (int n = 0; n < N; n++)
            {
                members.add(VectorFactory.getDefault().createUniformRandom(M, -r, r, random));
            }

            clusters.add(new GaussianCluster(members,
                MultivariateGaussian.MaximumLikelihoodEstimator.learn(members, 0.0)));

        }

        instance.consumeClusters(clusters);
        for (int k = 0; k < K; k++)
        {
            assertEquals(instance.getGaussianMixture().getRandomVariables().get(k), clusters.get(k).getGaussian());
        }

    }

    /**
     * Test of getGaussianMixture method, of class gov.sandia.cognition.learning.util.function.GaussianContextRecognizer.
     */
    public void testGetGaussianMixture()
    {
        System.out.println("getGaussianMixture");

        GaussianContextRecognizer instance = this.createInstance();
        MixtureOfGaussians mog = instance.getGaussianMixture();
        assertNotNull(mog);
    }

    /**
     * Test of setGaussianMixture method, of class gov.sandia.cognition.learning.util.function.GaussianContextRecognizer.
     */
    public void testSetGaussianMixture()
    {
        System.out.println("setGaussianMixture");

        GaussianContextRecognizer instance = this.createInstance();
        MixtureOfGaussians mog = instance.getGaussianMixture();
        assertNotNull(mog);

        instance.setGaussianMixture(null);
        assertNull(instance.getGaussianMixture());

        instance.setGaussianMixture(mog);
        assertSame(mog, instance.getGaussianMixture());
    }

    public void testDimensionality()
    {
        System.out.println( "getDimensionality" );

        GaussianContextRecognizer instance = this.createInstance();
        int M = instance.getInputDimensionality();
        double r = 2.0;
        Vector x = VectorFactory.getDefault().createUniformRandom(M, -r, r, random);
        Vector y = instance.evaluate(x);
        assertEquals( x.getDimensionality(), instance.getInputDimensionality() );
        assertEquals( y.getDimensionality(), instance.getOutputDimensionality() );

    }

}
