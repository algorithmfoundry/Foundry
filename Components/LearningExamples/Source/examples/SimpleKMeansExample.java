/*
 * File:                SimpleKMeansExample.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 28, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package examples;

import gov.sandia.cognition.learning.algorithm.clustering.KMeansClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.KMeansFactory;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.GreedyClusterInitializer;
import gov.sandia.cognition.learning.function.distance.CosineDistanceMetric;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceSquaredMetric;
import gov.sandia.cognition.learning.function.distance.ManhattanDistanceMetric;
import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * This example demonstrates how to use the k-means clustering from the
 * learning package in the Cognitive Foundry. It demonstrates several different
 * ways to initialize k-means, depending on how much customization of the
 * algorithm you wish to perform.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class SimpleKMeansExample
{

    /**
     * The main method where execution begins.
     *
     * @param   arguments
     *      The command-line arguments (ignored).
     */
    public static void main(
        final String... arguments)
    {
        // We're going to need a random number generator both to create some
        // random data and because k-means does random initialization of the
        // clusters.
        final Random random = new Random();
        
        // Lets make up some random 2-dimensional data with values in [-1, +1].
        Collection<Vector> data = new ArrayList<Vector>();
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        for (int i = 0; i < 100; i++)
        {
            data.add(vectorFactory.createUniformRandom(2, -1.0, +1.0, random));
        }


        // Version 1:
        // Lets use the "easy" way to create a clusterer by just giving it
        // the number of clusters we want and use the default divergence metric
        // Euclidean distance. Note that the default k-means implementation uses
        // multi-threaded parallelism.
        int numRequestedClusters = 2; // The "k" in k-means.
        KMeansClusterer<Vector, CentroidCluster<Vector>> kMeans =
            KMeansFactory.create(numRequestedClusters, random);

        // Now run the clustering to create the clusters.
        Collection<CentroidCluster<Vector>> clusters = kMeans.learn(data);
        printClusters("Version 1: ", clusters);

        kMeans.setNumRequestedClusters(10);
        clusters = kMeans.learn(data);
        printClusters("Version 1 (k=10): ", clusters);

        // Version 2:
        // Now lets use a different metric, manhattan distance.
        kMeans = KMeansFactory.create(numRequestedClusters,
            ManhattanDistanceMetric.INSTANCE, random);

        // Now run the clustering to create the clusters.
        clusters = kMeans.learn(data);
        printClusters("Version 2: ", clusters);


        // Version 3:
        // Lets create a non-parallel k-means implementation directly instead
        // of using the factory class. It shows all of the different things you
        // can change on the clusterer, but also shows why we provided the
        // factory class for common use-cases. We will also use the cosine
        // distance metric instead.
        Semimetric<Vectorizable> metric = CosineDistanceMetric.INSTANCE;
        int maxIterations = 200;
        ClusterCreator<CentroidCluster<Vector>, Vector> creator =
            VectorMeanCentroidClusterCreator.INSTANCE;
        GreedyClusterInitializer<CentroidCluster<Vector>, Vector>
            initializer =
                new GreedyClusterInitializer<CentroidCluster<Vector>, Vector>(
                    metric, creator, random);
        ClusterDivergenceFunction<CentroidCluster<Vector>, Vector>
            clusterDivergence =
                new CentroidClusterDivergenceFunction<Vector>(metric);
        kMeans = new KMeansClusterer<Vector, CentroidCluster<Vector>>(
            numRequestedClusters, maxIterations,
            initializer, clusterDivergence, creator);

        // Now run the clustering to create the clusters.
        clusters = kMeans.learn(data);
        printClusters("Version 3: ", clusters);


        // Version 4:
        // You can also change the parameters of the clustering after you
        // created it. This would work with all three versions. We're changing
        // the distance to the euclidean distance squared, which saves us
        // computing a square-root when doing the distance computation. We're
        // also changing the number of requested clusters (k) and the maximum
        // number of iterations to allow it to run for.
        kMeans.setNumRequestedClusters(10);
        kMeans.setMaxIterations(20);
        kMeans.setDivergenceFunction(
            new CentroidClusterDivergenceFunction<Vector>(
                EuclideanDistanceSquaredMetric.INSTANCE));
        
        clusters = kMeans.learn(data);
        printClusters("Version 4: ", clusters);
    }

    /**
     * Prints out the cluster centers.
     *
     * @param   title
     *      The title to print.
     * @param   clusters
     *      The cluster centers.
     */
    public static void printClusters(
        final String title,
        final Collection<CentroidCluster<Vector>> clusters)
    {
        System.out.print(title);
        System.out.println("There are " + clusters.size() + " clusters.");
        int index = 0;
        for (CentroidCluster<Vector> cluster : clusters)
        {
            System.out.println("    " + index + ": " + cluster.getCentroid());
            index++;
            // Another useful method on a cluster is: cluster.getMembers()
        }
    }

}
