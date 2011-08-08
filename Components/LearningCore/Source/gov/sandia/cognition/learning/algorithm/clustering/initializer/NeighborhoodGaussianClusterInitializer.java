/*
 * File:                NeighborhoodGaussianClusterInitializer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.initializer;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Creates GaussianClusters near existing, but not on top of, data points.  Also
 * {@code GaussianClusters} are not created near each other.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Now extends AbstractRandomized.",
        "Got rid of C-style comments from inside methods.",
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
public class NeighborhoodGaussianClusterInitializer
    extends AbstractRandomized
    implements FixedClusterInitializer<GaussianCluster, Vector>
{

    /**
     * Default range of the "neighborhood", {@value}.
     */
    public static final double DEFAULT_RANGE = 1.0;

    /**
     * Default covariance to put on the diagonal entries, {@value}.
     */
    public static final double DEFAULT_COVARIANCE = 1.0;

    /** 
     * default diagonal covariance scaling factor 
     */
    private double defaultCovariance;

    /** 
     * range of the neighborhood from which to place the cluster 
     */
    private double randomRange;

    /**
     * Default constructor.
     */
    public NeighborhoodGaussianClusterInitializer()
    {
        this( new Random() );
    }

    /**
     * Creates a new instance of NeighborhoodGaussianClusterInitializer
     * @param random
     * random-number generator for the system
     */
    public NeighborhoodGaussianClusterInitializer(
        Random random )
    {
        this( DEFAULT_COVARIANCE, DEFAULT_RANGE, random );
    }

    /**
     * Creates a new instance of NeighborhoodGaussianClusterInitializer
     * @param defaultCovariance
     * default diagonal covariance scaling factor 
     * @param randomRange
     * range of the neighborhood from which to place the cluster 
     * @param random 
     * random-number generator for the system 
     */
    public NeighborhoodGaussianClusterInitializer(
        double defaultCovariance,
        double randomRange,
        Random random)
    {
        super( random );
        this.setDefaultCovariance(defaultCovariance);
        this.setRandomRange(randomRange);
    }

    /**
     * Getter for randomRange
     * @return range of the neighborhood from which to place the cluster
     */
    public double getRandomRange()
    {
        return this.randomRange;
    }

    /**
     * Setter for randomRange
     * @param randomRange
     *      range of the neighborhood from which to place the cluster
     */
    public void setRandomRange(
        double randomRange)
    {
        this.randomRange = randomRange;
    }

    /**
     * Getter for defaultCovariance
     * @return default diagonal covariance scaling factor
     */
    public double getDefaultCovariance()
    {
        return this.defaultCovariance;
    }

    /**
     * Setter for defaultCovariance
     * @param defaultCovariance default diagonal covariance scaling factor
     */
    public void setDefaultCovariance(
        double defaultCovariance)
    {
        this.defaultCovariance = defaultCovariance;
    }

    public ArrayList<GaussianCluster> initializeClusters(
        int numClusters,
        Collection<? extends Vector> elements)
    {

        if (numClusters < 0)
        {
            // Error: Bad number of clusters.
            throw new IllegalArgumentException(
                "The number of clusters cannot be negative.");
        }
        else if (elements == null)
        {
            // Error: Bad elements.
            throw new NullPointerException("The elements cannot be null.");
        }
        else if (numClusters == 0 || elements.size() == 0)
        {
            // No clusters to create.
            return new ArrayList<GaussianCluster>();
        }

        ArrayList<Vector> elementsArray =
            new ArrayList<Vector>(elements);

        // Initialize the cluster objects.
        ArrayList<GaussianCluster> clusterList =
            new ArrayList<GaussianCluster>();

        for (int k = 0; k < numClusters; k++)
        {
            // keep selecting random point until and ensure that the
            // means aren't on top of eachother
            Vector mean = null;
            double minDiff = 0.0;
            while (minDiff <= 0.0)
            {
                // select a new data point at random and place the cluster
                // on top of it
                int index = this.random.nextInt( elements.size() );

                // add some noise to the point
                Vector data = elementsArray.get(index);
                Vector randomNoise = VectorFactory.getDefault().createUniformRandom(
                    data.getDimensionality(),
                    -this.getRandomRange(), this.getRandomRange(), this.random );
                mean = data.plus(randomNoise);

                minDiff = Double.POSITIVE_INFINITY;
                for (int i = 0; i < k; i++)
                {
                    Vector otherMean =
                        clusterList.get(i).getGaussian().getMean();
                    double diff = mean.euclideanDistance(otherMean);
                    if (minDiff > diff)
                    {
                        minDiff = diff;
                    }
                }

            }
            
            // create a diagonal covariance matrix with "defaultCovariance"
            // on the diagonal, and zeros elsewhere
            int M = mean.getDimensionality();
            Matrix covariance = MatrixFactory.getDefault().createIdentity(M, M).scale(this.getDefaultCovariance());

            MultivariateGaussian.PDF gaussian =
                new MultivariateGaussian.PDF(mean, covariance);

            GaussianCluster cluster = new GaussianCluster(null, gaussian);
            clusterList.add(cluster);

        }

        return clusterList;

    }

}
