/*
 * File:                KMeansClusterer.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;
import gov.sandia.cognition.learning.function.distance.DivergenceFunctionContainer;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The {@code KMeansClusterer} class implements the standard k-means 
 * (k-centroids) clustering algorithm. 
 *
 * @param   <DataType> The type of the data to cluster. This is typically 
 *          defined by the divergence function used.
 * @param   <ClusterType> The type of {@code Cluster} created by the algorithm.
 *          This is typically defined by the cluster creator function used.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-06",
            changesNeeded=true,
            comments={
                "The constructors for this class are not user friendly.",
                "I've been trying to write a test GUI for k-means for over an hour and STILL can't figure out the combination of classes to configure the constructor.",
                "Please make a constructor that configures the class with meaningful, user-friendly default arguments."
            }
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-22",
            changesNeeded=false,
            comments={
                "Changed the condition to be 'members.size() > 0' instead of 1 in createClustersFromAssignments()",
                "Cleaned up javadoc.",
                "Code generally looks fine."
            }
        )
    }
)
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="K-means algorithm",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/K-means_algorithm"
        )
        ,
        @PublicationReference(
            author="Matteo Matteucci",
            title="A Tutorial on Clustering Algorithms: k-means Demo",
            type=PublicationType.WebPage,
            year=2008,
            url="http://home.dei.polimi.it/matteucc/Clustering/tutorial_html/AppletKM.html"
        )
    }
)
public class KMeansClusterer<DataType, ClusterType extends Cluster<DataType>>
    extends AbstractAnytimeBatchLearner<Collection<? extends DataType>, Collection<ClusterType>>
    implements BatchClusterer<DataType, ClusterType>, MeasurablePerformanceAlgorithm,
        DivergenceFunctionContainer<ClusterType, DataType>
{
    /** The default number of requested clusters is {@value}. */
    public static final int DEFAULT_NUM_REQUESTED_CLUSTERS = 10;
    
    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;
    
    /** The number of clusters requested. */
    protected int numRequestedClusters;

    /** The initializer for the algorithm. */
    protected FixedClusterInitializer<ClusterType, DataType> initializer;

    /** The divergence function between cluster being used. */
    protected ClusterDivergenceFunction<? super ClusterType, ? super DataType>
        divergenceFunction;

    /** The cluster creator for creating clusters. */
    protected ClusterCreator<ClusterType, DataType> creator;

    /** The current set of clusters. */
    protected ArrayList<ClusterType> clusters;

    /** The current assignments of elements to clusters. */
    protected int[] assignments;

    /** The current number of elements assigned to each cluster. */
    protected int[] clusterCounts;

    /**
     * Returns the number of samples that changed assignment between iterations
     */
    private int numChanged;
    
    /**
     * Creates a new instance of {@code KMeansClusterer} with default 
     * parameters.
     */
    public KMeansClusterer()
    {
        this(DEFAULT_NUM_REQUESTED_CLUSTERS, DEFAULT_MAX_ITERATIONS,
            null, null, null);
    }

    /**
     * Creates a new instance of KMeansClusterer using the given parameters.
     *
     * @param numRequestedClusters The number of clusters requested (k).
     * @param maxIterations Maximum number of iterations before stopping
     * @param initializer The initializer for the clusters.
     * @param divergenceFunction The divergence function.
     * @param creator The cluster creator.
     */
    public KMeansClusterer(
        int numRequestedClusters,
        int maxIterations,
        FixedClusterInitializer<ClusterType, DataType> initializer,
        ClusterDivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction,
        ClusterCreator<ClusterType, DataType> creator)
    {
        super(maxIterations);

        this.setNumRequestedClusters(numRequestedClusters);
        this.setInitializer(initializer);
        this.setDivergenceFunction(divergenceFunction);
        this.setCreator(creator);
    }
    
    @Override
    public KMeansClusterer<DataType, ClusterType> clone()
    {
        @SuppressWarnings("unchecked")
        final KMeansClusterer<DataType, ClusterType> result =
            (KMeansClusterer<DataType, ClusterType>) super.clone();
        result.initializer = ObjectUtil.cloneSmart(this.initializer);
        result.divergenceFunction = ObjectUtil.cloneSmart(this.divergenceFunction);
        result.creator = ObjectUtil.cloneSmart(this.creator);
        
        result.clusters = null;
        result.assignments = null;
        result.clusterCounts = null;
        
        return result;
        
    }

    protected boolean initializeAlgorithm()
    {
        // Set the cluster state variables.
        this.setClusters(this.initializer.initializeClusters(
            this.numRequestedClusters, this.data));
        this.setClusterCounts(new int[this.getNumClusters()]);

        this.setAssignments(new int[this.getNumElements()]);
        Arrays.fill(this.assignments, -1);
        Arrays.fill(this.clusterCounts, 0);

        this.setNumChanged(0);

        // we can only run k-means if we have at least as many datapoints as
        // clusters we are requested to find.
        return this.getNumClusters() <= this.getNumElements();
    }

    /**
     * Do a step of the clustering algorithm. Return the number of
     * elements the changed their cluster membership. If this is zero then
     * the clustering is complete.
     * @return true means keep going, false means stop clustering.
     */
    protected boolean step()
    {
        // First, assign each data point to a cluster, given the current
        // location of the clusters
        int[] newAssignements = this.assignDataToClusters( this.getData() );
//        this.clusterCounts = new int[this.getNumClusters()];
        int nc = 0;
        for( int i = 0; i < newAssignements.length; i++ )
        {
            final int newAssignment = newAssignements[i];
            if (this.setAssignment(i, newAssignment))
            {
                nc++;
            }
            /*
                        int assignment = newAssignements[i];
            if( this.assignments[i] != assignment )
            {
                nc++;
                this.assignments[i] = assignment;
            }
             */
        }
        
        this.setNumChanged( nc );
        
        // There was a change so create the clusters and keep going.
        if (this.getNumChanged() > 0)
        {
            // Now, re-estimate the cluster locations, given the current
            // assignments of the data points
            this.createClustersFromAssignments();
            return true;
        }
        // If the cluster assignments didn't change, then we're done
        else
        {
            return false;
        }

    }

    protected void cleanupAlgorithm()
    {
    }

    /**
     * Creates the cluster assignments given the current locations of clusters
     * @param data Data to assign
     * @return Assignments of the data to each of the k-clusters
     */
    protected int[] assignDataToClusters(
        Collection<? extends DataType> data )
    {
        // Loop through the elements and find the closest cluster for each.
        int i = 0;
        int[] localAssignments = new int[ data.size() ];
        for (DataType element : data)
        {
            // Get the i-th element and find the index of the closest cluster
            // to it.
            localAssignments[i] = this.getClosestClusterIndex(element);
            i++;
        }
        
        return localAssignments;
        
    }

    @Override
    public void setData(
        Collection<? extends DataType> data )
    {
        super.setData( data );
    }
    
    /**
     * Puts the data into a list of lists for each cluster to then estimate
     * @return
     * The list of lists for each cluster to then estimate
     */
    protected ArrayList<ArrayList<DataType>> assignDataFromIndices()
    {
        // Loop through the clusters and initialize their membership lists
        // based on who is in them.
        int numClusters = this.getNumClusters();
        ArrayList<ArrayList<DataType>> clustersMembers =
            new ArrayList<ArrayList<DataType>>( numClusters );
        for (int i = 0; i < numClusters; i++)
        {
            int clusterSize = this.clusterCounts[i];
            clustersMembers.add(new ArrayList<DataType>(clusterSize));
        }

        // Go through and add each element to its proper cluster based on
        // the current assignments.
        int index = 0;
        for (DataType element : this.data)
        {
            int assignment = this.assignments[index];
            clustersMembers.get(assignment).add(element);
            index++;
        }
        
        return clustersMembers;
        
    }
    
    /**
     * Creates the set of clusters using the current cluster assignments.
     */
    protected void createClustersFromAssignments()
    {
        // Loop through the clusters and initialize their membership lists
        // based on who is in them.
        int numClusters = this.getNumClusters();
        ArrayList<ArrayList<DataType>> clustersMembers = this.assignDataFromIndices();

        // Create the clusters from their memberships.
        for (int i = 0; i < numClusters; i++)
        {
            ArrayList<DataType> members = clustersMembers.get(i);
            ClusterType cluster;
            if (members.size() > 0)
            {
                cluster = this.creator.createCluster(members);
            }
            else
            {
                cluster = null;
            }
            
            this.clusters.set( i, cluster );
            
        }
        
    }

    /**
     * Gets the index of the closest cluster for the given element.
     *
     * @param  element The element to get the closet cluster for.
     * @return The index of the closest cluster.
     */
    protected int getClosestClusterIndex(
        DataType element)
    {
        // Find the closest cluster.
        double minDistance = Double.MAX_VALUE;
        int closestClusterIndex = -1;

        // Loop over all the clusters.
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            // Get the i-th cluster.
            ClusterType cluster = this.clusters.get(i);

            // Compute the distance to the i-th cluster.
            double distance =
                this.divergenceFunction.evaluate(cluster, element);

            if (closestClusterIndex < 0 || distance < minDistance)
            {
                // This is the closest so far.
                minDistance = distance;
                closestClusterIndex = i;
            }
        // else - There is already a closer cluster.
        }

        // Return the index of the closest cluster.
        return closestClusterIndex;
    }

    /**
     * Sets the assignment of the given element to the new cluster index,
     * updating the cluster counts as well.
     *
     * @param elementIndex The index of the element.
     * @param newClusterIndex The new cluster the element is assigned to.
     * @return
     *      True if the assignment changed. Otherwise, false.
     */
    protected boolean setAssignment(
        int elementIndex,
        int newClusterIndex)
    {
        // Save the old assignment.
        int oldClusterIndex = this.assignments[elementIndex];

        // Set the new assignment.
        this.assignments[elementIndex] = newClusterIndex;

        if (oldClusterIndex >= 0)
        {
            // Decrement the counter for the old assignment since the element
            // is no longer in that cluster.
            this.clusterCounts[oldClusterIndex]--;
        }

        if (newClusterIndex >= 0)
        {
            // Increment the counter for the new assignment since the element
            // is now in that cluster.
            this.clusterCounts[newClusterIndex]++;
        }

        return newClusterIndex != oldClusterIndex;
    }

    /**
     * Gets the cluster for the given index.
     *
     * @param index The index of the cluster.
     * @return The cluster for the given index.
     */
    protected ClusterType getCluster(
        int index)
    {
        return this.clusters.get(index);
    }

    /**
     * Gets the actual number of clusters that were created.
     *
     * @return The actual number of clusters.
     */
    protected int getNumClusters()
    {
        return (this.getClusters() == null) ? 0 : this.getClusters().size();
    }

    /**
     * Gets the number of clusters that were requested.
     *
     * @return The number of clusters that were requested.
     */
    public int getNumRequestedClusters()
    {
        return this.numRequestedClusters;
    }

    /**
     * Gets the cluster initializer.
     *
     * @return The cluster initializer.
     */
    public FixedClusterInitializer<ClusterType, DataType> getInitializer()
    {
        return this.initializer;
    }

    /**
     * Gets the divergence function used in clustering.
     *
     * @return The divergence function.
     */
    public ClusterDivergenceFunction<? super ClusterType, ? super DataType>
        getDivergenceFunction()
    {
        return this.divergenceFunction;
    }

    /**
     * Gets the cluster creator.
     *
     * @return The cluster creator.
     */
    public ClusterCreator<ClusterType, DataType> getCreator()
    {
        return this.creator;
    }

    /**
     * Sets the number of requested clusters.
     *
     * @param numRequestedClusters The number of requested clusters.
     */
    public void setNumRequestedClusters(
        int numRequestedClusters)
    {
        if (numRequestedClusters < 0)
        {
            // Error: Bad number of clusters requested.
            throw new IllegalArgumentException(
                "The number of clusters cannot be less than zero.");
        }

        this.numRequestedClusters = numRequestedClusters;
    }

    /**
     * Sets the cluster initializer.
     *
     * @param initializer The cluster initializer.
     */
    public void setInitializer(
        FixedClusterInitializer<ClusterType, DataType> initializer)
    {
        this.initializer = initializer;
    }

    /**
     * Sets the divergence function.
     *
     * @param divergenceFunction The divergence function.
     */
    public void setDivergenceFunction(
        ClusterDivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction)
    {
        this.divergenceFunction = divergenceFunction;
    }

    /**
     * Sets the cluster creator.
     *
     * @param creator The creator for clusters.
     */
    public void setCreator(
        ClusterCreator<ClusterType, DataType> creator)
    {
        this.creator = creator;
    }

    /**
     * Returns the number of elements
     * @return number of elements being clustered
     */
    public int getNumElements()
    {
        if( this.data != null )
        {
            return this.data.size();
        }
        else
        {
            return 0;
        }
    }

    /**
     * Sets the clusters.
     *
     * @param clusters The clusters.
     */
    protected void setClusters(
        ArrayList<ClusterType> clusters)
    {
        this.clusters = clusters;
    }

    /**
     * Getter for clusters
     *
     * @return list of clusters in the algorithm
     */
    public ArrayList<ClusterType> getClusters()
    {
        return this.clusters;
    }

    public ArrayList<ClusterType> getResult()
    {
        return this.getClusters();
    }

    /**
     * Sets the assignment of elements to clusters.
     *
     * @param assignments The new assignments.
     */
    private void setAssignments(
        int[] assignments)
    {
        this.assignments = assignments;
    }

    /**
     * Getter for assignments
     *
     * @return The assignment of elements to clusters
     */
    protected int[] getAssignments()
    {
        return this.assignments;
    }

    /**
     * Sets the counts for how many elements are in each cluster.
     *
     * @param clusterCounts The new cluster counts.
     */
    private void setClusterCounts(
        int[] clusterCounts)
    {
        this.clusterCounts = clusterCounts;
    }

    /**
     * Getter for clusterCounts
     * @return counts for how many elements are assigned to each cluster
     */
    protected int[] getClusterCounts()
    {
        return this.clusterCounts;
    }

    /**
     * Getter for numChanged
     * @return 
     * Returns the number of samples that changed assignment between iterations
     */
    public int getNumChanged()
    {
        return this.numChanged;
    }

    /**
     * Setter for numChanged
     * @param numChanged 
     * Returns the number of samples that changed assignment between iterations
     */
    protected void setNumChanged(
        int numChanged)
    {
        this.numChanged = numChanged;
    }
    
    /**
     * Gets the performance, which is the number changed on the last iteration.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Integer> getPerformance()
    {
        return new DefaultNamedValue<Integer>(
            "Assignments changed", this.getNumChanged());
    }

}
