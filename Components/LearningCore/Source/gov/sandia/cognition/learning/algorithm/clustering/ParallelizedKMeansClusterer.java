/*
 * File:                ParallelizedKMeansClusterer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 6, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;
import gov.sandia.cognition.learning.data.SequentialDataMultiPartitioner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a parallel implementation of the k-means clustering algorithm.  The
 * default is to use n-1 available cores/hyperthreads on a machine and spread
 * the data-point-to-cluster assignment (E-step) and the cluster re-estimation
 * (M-step) across these computational units.  The output of this algorithm is
 * exact, and should return the same results as the serial version of k-means
 * for an identical dataset and random seed.
 *
 * @param   <DataType> The type of the data to cluster. This is typically 
 *          defined by the divergence function used.
 * @param   <ClusterType> The type of {@code Cluster} created by the algorithm.
 *          This is typically defined by the cluster creator function used.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Halil Bisgin",
    title="Parallel Clustering Algorithms with Application to Climatology",
    type=PublicationType.Thesis,
    year=2007,
    url="http://www.halilbisgin.com/thesis/thesis.pdf"
)
public class ParallelizedKMeansClusterer<DataType, ClusterType extends Cluster<DataType>>
    extends KMeansClusterer<DataType,ClusterType>
    implements ParallelAlgorithm
{

    /**
     * Parallel tasks that assign the data points to clusters
     */
    private ArrayList<Callable<int[]>> assignmentTasks;
    
    /**
     * Parallel tasks that creates clusters from the assigned data points
     */
    private ArrayList<Callable<ClusterType>> clusterCreatorTask;
    
    /**
     * Thread pool used to parallelize the computation
     */
    private transient ThreadPoolExecutor threadPool;    
    
    /**
     * ArrayList of assignments from the subtasks
     */
    private Collection<int[]> assignmentList;    
    
    /**
     * Array of new assignments
     */
    private int[] newAssignments;
    
    /**
     * Default constructor
     */
    public ParallelizedKMeansClusterer()
    {
        this( DEFAULT_NUM_REQUESTED_CLUSTERS, DEFAULT_MAX_ITERATIONS,
            null, null, null, null );
    }
    
    /** 
     * Creates a new instance of ParallelizedKMeansClusterer2 
     * @param numRequestedClusters The number of clusters requested (k).
     * @param maxIterations Maximum number of iterations before stopping
     * @param threadPool Thread pool to use for parallelization
     * @param initializer The initializer for the clusters.
     * @param divergenceFunction The divergence function.
     * @param creator The cluster creator.
     */
    public ParallelizedKMeansClusterer(
        int numRequestedClusters,
        int maxIterations,
        ThreadPoolExecutor threadPool,
        FixedClusterInitializer<ClusterType, DataType> initializer,
        ClusterDivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction,
        ClusterCreator<ClusterType, DataType> creator )
    {
        super( numRequestedClusters, maxIterations, initializer, divergenceFunction, creator );
        this.setThreadPool( threadPool );
    }

    @Override
    public ParallelizedKMeansClusterer<DataType, ClusterType> clone()
    {
        return (ParallelizedKMeansClusterer<DataType, ClusterType>) super.clone();
    }
    
    public ThreadPoolExecutor getThreadPool()
    {
        if( this.threadPool == null )
        {
            this.setThreadPool( ParallelUtil.createThreadPool() );
        }
        
        return this.threadPool;
    }

    public void setThreadPool(
        final ThreadPoolExecutor threadPool )
    {
        this.threadPool = threadPool;
    }
    
    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads( this );
    }
    
    /**
     * Creates the assignment tasks given the number of threads requested
     */
    protected void createAssignmentTasks()
    {
        
        int numThreads = this.getNumThreads();
        
        // if the number of requested components is less than 1, then
        ArrayList<ArrayList<DataType>> partitions =
            SequentialDataMultiPartitioner.create( this.getData(), numThreads );
        
        this.assignmentTasks = new ArrayList<Callable<int[]>>( numThreads );
        for( int i = 0; i < numThreads; i++ )
        {
            this.assignmentTasks.add( new AssignDataToCluster( partitions.get( i ) ) );
        }
        
        // Create the tasks that will assign data point onto clusters to
        // re-estimate the cluster locations
        int numClusters = this.getNumClusters();
        this.clusterCreatorTask = new ArrayList<Callable<ClusterType>>( numClusters );
        for( int i = 0; i < numClusters; i++ )
        {
            this.clusterCreatorTask.add( new CreateClustersFromAssignments() );
        }
        
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        boolean superRetval = super.initializeAlgorithm();
        this.createAssignmentTasks();
        this.newAssignments = new int[ this.data.size() ];
        return superRetval;
    }

    @Override
    protected int[] assignDataToClusters(
        Collection<? extends DataType> data )
    {
        
        try
        {
            // Execute the assignments in parallel.  The k-means algorithm
            // typically spends the vast majority of its time executing
            // this loop.
            this.assignmentList = ParallelUtil.executeInParallel(
                this.assignmentTasks, this.getThreadPool() );
        }
        catch (Exception ex)
        {
            Logger.getLogger( ParallelizedKMeansClusterer.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        // Put the assignment array back together.
        // We're just going to re-use the "newAssignment" member as the super
        // method does a element-wise copy, not a pointer reassignment to
        // minimize memory thrashing.
        int index = 0;
        for( int[] subAssignment : this.assignmentList )
        {
            for( int i = 0; i < subAssignment.length; i++ )
            {
                int assignment = subAssignment[i];
                this.newAssignments[index] = assignment;
                index++;
            }
        }
        
        return this.newAssignments;
        
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void createClustersFromAssignments()
    {
        // Loop through the clusters and initialize their membership lists
        // based on who is in them.
        // The k-means algoirhtm typically spends much less time in this loop
        // than the assignment step, but that also depends on the type of
        // cluster being estimated.
        int numClusters = this.getNumClusters();
        ArrayList<ArrayList<DataType>> clustersMembers = this.assignDataFromIndices();
        for( int i = 0; i < numClusters; i++ )
        {
            ((CreateClustersFromAssignments) this.clusterCreatorTask.get(i)).data = 
                clustersMembers.get( i );
        }

        Collection<ClusterType> results = null;
        try
        {
            // Execute the assignments in parallel
            results = ParallelUtil.executeInParallel(
                this.clusterCreatorTask, this.getThreadPool() );
        }
        catch (Exception ex)
        {
            Logger.getLogger( ParallelizedKMeansClusterer.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        int index = 0;
        for( ClusterType cluster : results )
        {
            this.getClusters().set( index, cluster );
            index++;
        }
        
    }
    
    
    /**
     * Callable task for that creates clusters from assigned data
     */
    protected class CreateClustersFromAssignments
        implements Callable<ClusterType>
    {
     
        /**
         * Data set to use for the task
         */
        public ArrayList<DataType> data;

        /**
         * Creates a new instance of CreateClustersFromAssignments
         */
        public CreateClustersFromAssignments()
        {
            this.data = null;
        }
        
        public ClusterType call()
        {
            return ParallelizedKMeansClusterer.this.getCreator().createCluster( this.data );
        }
        
    }
    
    /**
     * Callable task for the evaluate() method.
     */
    protected class AssignDataToCluster
        implements Callable<int[]>
    {
        
        /**
         * local data
         */
        private Collection<DataType> localData;
        
        /**
         * Creates a new instance of AssignDataToCluster
         * @param localData
         * Local data
         */
        public AssignDataToCluster(
            Collection<DataType> localData )
        {
            this.localData = localData;
        }

        public int[] call()
        {
            return ParallelizedKMeansClusterer.super.assignDataToClusters( 
                this.localData );
        }
        
    }    
    
}
