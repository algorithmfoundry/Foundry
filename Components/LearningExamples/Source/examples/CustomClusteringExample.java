/*
 * File:                CustomClusteringExample.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 11, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package examples;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.clustering.AgglomerativeClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterMeanLinkDivergenceFunction;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

/**
 * This is an example of using a custom distance metric and cluster creator with
 * agglomerative clustering. 
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public class CustomClusteringExample
{
    /**
     * Main method
     * @param args
     * We don't take any command-line arguments
     */
    public static void main(
        final String... args)
    {
        // Step 1: Get the data.
        // First we generate some data to cluster. Typically, one already has
        // the data loaded. The important thing is that it gets turned into a
        // Collection.
        final int numClusters = 4;
        final Random random = new Random();
        final Collection<ExampleData> data = generateData(numClusters, random);
        
        
        // Step 2: Create our metric.
        // We are going to use a custom metric, which is defined below.
        final Metric<ExampleData> metric = new CustomMetric();
        
        
        // Step 3: Choose the cluster creator.
        // We also need to provide a cluster creator for our algorithm. Here
        // we make use of our custom cluster creator. Note that we could have
        // used the DefaultClusterCreator with the divergence function we are
        // using, but by using our own custom creator, we can pull out 
        // information regarding the centroid after clustering is complete.
        final ClusterCreator<CentroidCluster<ExampleData>, ExampleData> 
            clusterCreator = new CustomClusterCreator();
        
        
        // Step 4: Choose the type of agglomerative clustering.
        // The key to using the custom metric is that it needs to be given to
        // the method of cluster-to-cluster divergence that we will provide to
        // the agglomerative clustering algorithm. Here we pick "mean link"
        // as our type of clustering, but we could alternatively have chosen 
        // one of the others such as "complete link", or "single link".
        final ClusterMeanLinkDivergenceFunction<CentroidCluster<ExampleData>, ExampleData> 
            clusterDivergence = 
                new ClusterMeanLinkDivergenceFunction<CentroidCluster<ExampleData>, ExampleData>(
                    metric);
        
        // Step 5: Create and initialize the clustering algorithm.
        // Next we populate the agglomerative clustering algorithm with our
        // the cluster divergence and cluster creator that we just created.
        final AgglomerativeClusterer<ExampleData, CentroidCluster<ExampleData>> 
            clusterer = new AgglomerativeClusterer<ExampleData, CentroidCluster<ExampleData>>(
                clusterDivergence, clusterCreator);

        // Now we the minimum number of clusters to create. Alternatively, we
        // could set the "maximum minimum distance", which is a threshold of the 
        // maximum allowed minimum distance between clusters, which stops the
        // clustering when the clusters are too far away.
        clusterer.setMinNumClusters(numClusters);
        
        
        // Step 6: Run the clustering.
        // Now we run the clustering by passing our data to the clustering 
        // algorithm.
        final Collection<CentroidCluster<ExampleData>> clusters =
            clusterer.learn(data);
        
        
        // Step 7: Use te clusters.
        // Now we can use the clusters that we've created.
        printClusters(clusters);
    }
    

    /**
     * The example data holds a three-dimensional position value, plus an
     * integer score associated with that position.
     */
    public static class ExampleData
    {
        /** The three-dimensional position of the example. */
        private Vector3 position;

        /** The score of the example. */
        private int score;

        /**
         * Creates a new example data instance.
         * 
         * @param position The position.
         * @param score The score
         */
        public ExampleData(
            final Vector3 position,
            final int score)
        {
            super();

            this.setPosition(position);
            this.setScore(score);
        }

        /**
         * Gets the position.
         * 
         * @return The position.
         */
        public Vector3 getPosition()
        {
            return position;
        }

        /**
         * Sets the position.
         * 
         * @param position The position.
         */
        public void setPosition(
            final Vector3 position)
        {
            this.position = position;
        }

        /**
         * Gets the score.
         * 
         * @return The score.
         */
        public int getScore()
        {
            return score;
        }

        /**
         * Sets the score.
         * 
         * @param score The score.
         */
        public void setScore(
            final int score)
        {
            this.score = score;
        }

        @Override
        public String toString()
        {
            return "position = " + this.getPosition() + ", score = " 
                + this.getScore();
        }

    }

    /**
     * This implements our custom metric. The Metric interface specifies that
     * the value returned must fulfill the requirements of being a mathematical
     * metric, which are:
     * 
     *     g(x, y) + g(y, z) >= g(x, z)
     *               g(x, y) == g(y, x)
     *               g(x, x) == 0
     */
    public static class CustomMetric
        extends AbstractCloneableSerializable
        implements Metric<ExampleData>
    {

        public double evaluate(
            final ExampleData first,
            final ExampleData second)
        {
            // To compute the metric, we use the euclidean distance in position
            // and the difference in the score.
            
            // First get he euclidean distance between the position. We
            // already know that euclidean distance fulfills the contract for
            // being a Metric.
            final double positionDifference =
                first.getPosition().euclideanDistance(
                second.getPosition());

            // Now compute the difference in score. Note that we need to use
            // the absolute value here in order to fulfill the contract for
            // a Metric.
            final int scoreDifference =
                Math.abs(first.getScore() - second.getScore());

            // We weight the difference in position higher than a difference
            // in score for some extra customization.
            return 2.0 * positionDifference + 0.5 * scoreDifference;
        }

    }
    
    /**
     * This implements a custom cluster creator. We make use of the
     * ClusterCreator interface to create our own CentroidCluster, which is
     * the type of cluster that has an object representing its centroid. In
     * our case, the centroid is the mean element of the cluster. To implement
     * this, we just have to compute the mean over the two fields in our
     * data type: the position and the score.
     */
    public static class CustomClusterCreator
        extends AbstractCloneableSerializable
        implements ClusterCreator<CentroidCluster<ExampleData>, ExampleData>
    {
        @Override
        public CentroidCluster<ExampleData> createCluster(
            final Collection<? extends ExampleData> members)
        {        
            if ( members == null )
            {
                // Error: Members is null.
                throw new NullPointerException("The members cannot be null.");
            }
            else if ( members.size() <= 0 )
            {
                // No members to create the cluster from.
                return null;
            }
            
            // We need to compute the mean position and mean score for our the
            // centroid example for our cluster.
            Vector3 meanPosition = new Vector3();
            double meanScore = 0.0;
            
            // Go through al lthe examples and add up te sum of positions and
            // scores.
            for (ExampleData example : members)
            {
                meanPosition.plusEquals(example.getPosition());
                meanScore += example.getScore();
            }
            
            // Now convert the sums into means by dividing by the number of
            // members.
            final int numMembers = members.size();
            meanPosition.scaleEquals(1.0 / (double) numMembers);
            meanScore = Math.round(meanScore / (double) numMembers);
            
            // Create the centroid object.
            final ExampleData centroid = 
                new ExampleData(meanPosition, (int) meanScore);
            
            // Return the centroid cluster.
            return new CentroidCluster<ExampleData>(centroid, members);
        }
    }


    /**
     * Generates the data we use for clustering.
     * 
     * @param numClusters The number of clusters.
     * @param random The random number generator.
     * @return The generated data.
     */
    public static LinkedList<ExampleData> generateData(
        final int numClusters,
        final Random random)
    {
        final LinkedList<ExampleData> result = new LinkedList<ExampleData>();

        // We're going to generate some clusters from which we will sample
        // the example data. Note that there is more noise in the score than
        // in the position.\
        final int numToGenerate = 100;
        final UnivariateGaussian.CDF positionNoiseCDF =
            new UnivariateGaussian.CDF(0.0, 1.0);
        final UnivariateGaussian.CDF scoreNoiseCDF =
            new UnivariateGaussian.CDF(0.0, 5.0);
        
        // Create the clusters to sample from.
        final ExampleData[] clusters = new ExampleData[numClusters];
        for (int i = 0; i < numClusters; i++)
        {
            final Vector3 position = new Vector3(
                25.0 * random.nextDouble(),
                25.0 * random.nextDouble(),
                25.0 * random.nextDouble());
            final int score = random.nextInt(10);
            clusters[i] = new ExampleData(position, score);
        }

        // Generate the examples.
        for (int i = 0; i < numToGenerate; i++)
        {
            // Get the cluster information.
            final ExampleData cluster = clusters[random.nextInt(numClusters)];
            Vector3 position = cluster.getPosition().clone();
            int score = cluster.getScore();
            
            // Add the noise.
            position.plusEquals(new Vector3(
                positionNoiseCDF.sample(random),
                positionNoiseCDF.sample(random),
                positionNoiseCDF.sample(random)));
            score += (int) Math.round(scoreNoiseCDF.sample(random));
            
            // Create the example and add it to the result.
            final ExampleData example = new ExampleData(position, score);
            result.add(example);
        }
        
        return result;
    }
    
    /**
     * Prints the given clusters to standard out.
     * 
     * @param clusters The clusters to print.
     */
    public static void printClusters(
        final Collection<CentroidCluster<ExampleData>> clusters)
    {
        for (int i = 0; i < clusters.size(); i++)
        {
            final CentroidCluster<ExampleData> cluster = CollectionUtil.getElement(clusters, i);
            System.out.println("Cluster " + i + ":" + cluster.getCentroid());
            
            for (ExampleData example : cluster.getMembers())
            {
                System.out.println("    " + example);
            }
        }
    }

}
