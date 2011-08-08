/*
 * File:                MixtureOfGaussiansExample.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 11, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package examples;

import gov.sandia.cognition.learning.algorithm.clustering.KMeansClustererWithRemoval;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.GaussianClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.NeighborhoodGaussianClusterInitializer;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MixtureOfGaussians;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import java.util.ArrayList;
import java.util.Random;

/**
 * This example shows how to learn a MixtureOfGaussians from a set of data. It
 * uses a random dataset to provide the training data and then learns a
 * MixtureOfGaussians using both a soft learner and a hard learner. The hard
 * learner does hard assignment of points to gaussians when learning the
 * mixture while the soft learner does soft assignment.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class MixtureOfGaussiansExample
{

    /**
     * Runs the example.
     *
     * @param   args
     *      Command-line arguments (ignored).
     */
    public static void main(
        final String[] args)
    {
        // Part 1: Create some data.
        // To start with we need some data to learn from. To do this, we are
        // going to create a random mixture of gaussians and then generate
        // random samples from it. In a real application you would typically
        // load in some data set of Vectors instead of generating the data.

        // Here are some general parameters for the example.
        final int dimensionality = 2;
        final double range = 10.0;
        final int numSamples = 100;
        final int actualNumGaussians = 2;
        final int guessedNumGaussians = 2;

        // Create a random number generator to use to generate our data.
        final Random random = new Random(47);
        // Note: Using a Random like this means that each time the example is
        // run a same set of data will be generated.
        
        // Create a mixture of gaussians as mechanism to create some random
        // data.
        final MixtureOfGaussians actualMixture = createRandomMixtureOfGaussians(
            random, actualNumGaussians, dimensionality, range);

        // Print out our actual mixture so we can look at it to compare how
        // the learner does.
        System.out.println("Actual gaussians:");
        printMixture(actualMixture);
        System.out.println();

        // Now we sample from the mixture to create our training dataset.
        final ArrayList<Vector> data = actualMixture.sample(random, numSamples);

        // Part 2: Using a Soft Learner
        // Now that we have some data to use, we create a soft learner to try
        // and fit a mixture of gaussians to the example data that we have.

        // We construct a new learner object.
        final MixtureOfGaussians.SoftLearner softLearner =
            new MixtureOfGaussians.SoftLearner();

        // Next we configure the parameters of the soft learner.
        // For this example, the only real parameter we are concerned with
        // is telling the soft learner how many gaussians to look for. We
        // do this by calling the setNumGaussians method.
        softLearner.setNumGaussians(guessedNumGaussians);

        // Now that our learner is configured, we call the learning algorithm
        // by calling the learn method and passing in our dataset. This runs
        // the algorithm and then returns the mixture of gaussians it has
        // learned from that data.
        MixtureOfGaussians learnedMixture = softLearner.learn(data);

        System.out.println("Soft Learned Gaussians: ");
        printMixture(learnedMixture);
        System.out.println();

        // Part 3: Using a Hard Learner
        // Using the hard learner is a little more complicated than the soft
        // learner because you can pass in the specific clustering algorithm
        // that you want it to use.

        // We are going to try using the K-means algorithm with removal to
        // do hard clustering on the data.
        final int maxIterations = 1000;
        final double removalThreshold = 0.1;
        final KMeansClustererWithRemoval<Vector, GaussianCluster> kmeans =
            new KMeansClustererWithRemoval<Vector, GaussianCluster>(
            guessedNumGaussians,
            maxIterations,new NeighborhoodGaussianClusterInitializer(random),
            new GaussianClusterDivergenceFunction(),
            new GaussianClusterCreator(),
            removalThreshold);
        
        // Note that here we pass in the guessed number of gaussians to the
        // constructor for KMeans so that it knows how many clusters to start
        // with.

        // After we have created our clustering algorithm, we create a new
        // hard learner and pass the clustering algorithm to it.
        final MixtureOfGaussians.Learner hardLearner =
            new MixtureOfGaussians.Learner(kmeans);

        // The hard learner does not have any parameters to tune since it just
        // uses the parameters of the KMeans algorithm.

        // Now we cal the learn method on the hard learner and again get out
        // our learned mixture of gaussians.
        learnedMixture = hardLearner.learn(data);

        System.out.println("Hard Learned Gaussians: ");
        printMixture(learnedMixture);
        System.out.println();
    }

    /**
     * Prints a mixture of gaussians to System.out.
     *
     * @param   mixture
     *      The mixture to print.
     */
    public static void printMixture(
        final MixtureOfGaussians mixture)
    {
        // Loop through the mixture and print out the random variables that
        // make up the mixture.
        for (int i = 0; i < mixture.getNumRandomVariables(); i++)
        {
            final MultivariateGaussian gaussian =
                mixture.getRandomVariables().get(i);

            // Get some information about the gaussian.
            final double prior = mixture.getPriorProbabilities().getElement(i);
            final Vector mean = gaussian.getMean();
            final Matrix covariance = gaussian.getCovariance();

            System.out.println("Gaussian " + (i + 1));
            System.out.println("Prior: " + prior);
            System.out.println("Mean: " + mean);
            System.out.println("Covariance: ");
            System.out.println(covariance);
        }
    }

    /**
     * Creates a random mixture of gaussians containing the requested number
     * of gaussians inside it of the given dimensionality.
     *
     * @param   random
     *      The random number generator.
     * @param   numGaussians
     *      The number of gaussians to put in the mixture.
     * @param   dimensionality
     *      The dimensionality of multivariate gaussians in the mixture.
     * @param   range
     *      The range of values to allow the mixture over.
     * @return
     *      A new random mixture of gaussians.
     */
    public static MixtureOfGaussians createRandomMixtureOfGaussians(
        final Random random,
        final int numGaussians,
        final int dimensionality,
        final double range)
    {
        // Create a random set of gaussians to form the mixture.
        final ArrayList<MultivariateGaussian.PDF> gaussians =
            new ArrayList<MultivariateGaussian.PDF>(numGaussians);
        for (int i = 0; i < numGaussians; i++)
        {
            final MultivariateGaussian.PDF gaussian =
                createRandomGaussian(random, dimensionality, range);
            gaussians.add(gaussian);
        }

        return new MixtureOfGaussians(gaussians);
    }

    /**
     * Creates a random multivariate gaussian.
     *
     * @param   random
     *      The random number generator.
     * @param   dimensionality
     *      The dimensionality of the multivariate gaussian to create.
     * @param   range
     *      The range of values for the gaussian
     * @return
     *      A new random multivariate gaussian.
     */
    public static MultivariateGaussian.PDF createRandomGaussian(
        final Random random,
        final int dimensionality,
        final double range)
    {
        return new MultivariateGaussian.PDF(
            VectorFactory.getDefault().createUniformRandom(
                dimensionality, -range, range, random),
            MatrixFactory.getDefault().createIdentity(
                dimensionality, dimensionality).scale(0.5 * range));
    }
}
