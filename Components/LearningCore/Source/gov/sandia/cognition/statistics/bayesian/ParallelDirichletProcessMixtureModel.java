/*
 * File:                ParallelDirichletProcessMixtureModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 3, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A Parallelized version of vanilla Dirichlet Process Mixture Model learning.
 * In particular, this class parallelizes the assignment of observations to
 * clusters and the Gibbs sampling updating of clusters from their constituent
 * observations.
 * @param <ObservationType>
 * Type of observations handled by the algorithm
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class ParallelDirichletProcessMixtureModel<ObservationType>
    extends DirichletProcessMixtureModel<ObservationType>
    implements ParallelAlgorithm
{

    /**
     * Thread pool used for parallelization.
     */
    private transient ThreadPoolExecutor threadPool;
    
    /** 
     * Creates a new instance of ParallelDirichletProcessMixtureModel 
     */
    public ParallelDirichletProcessMixtureModel()
    {
        super();
    }

    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads(this);
    }

    public ThreadPoolExecutor getThreadPool()
    {
        if (this.threadPool == null)
        {
            this.setThreadPool(ParallelUtil.createThreadPool());
        }

        return this.threadPool;
    }

    public void setThreadPool(
        final ThreadPoolExecutor threadPool)
    {
        this.threadPool = threadPool;
    }

    /**
     * Tasks that assign observations to clusters
     */
    transient protected ArrayList<ObservationAssignmentTask> assignmentTasks;

    @Override
    protected ArrayList<Collection<ObservationType>> assignObservationsToClusters(
        int K)
    {
        if( this.assignmentTasks == null )
        {
            ArrayList<? extends ObservationType> dataArray =
                CollectionUtil.asArrayList(this.data );
            final int N = dataArray.size();
            final int numThreads = this.getNumThreads();
            this.assignmentTasks = new ArrayList<ObservationAssignmentTask>( numThreads );

            int numPerTask = N/numThreads;

            int endIndex = 0;
            for( int n = 0; n < numThreads-1; n++ )
            {
                int startIndex = endIndex;
                endIndex += numPerTask;
                this.assignmentTasks.add( new ObservationAssignmentTask(
                    dataArray.subList(startIndex, endIndex) ) );
            }
            this.assignmentTasks.add( new ObservationAssignmentTask(
                dataArray.subList(endIndex,N) ) );
        }

        ArrayList<ArrayList<Integer>> results;
        try
        {
            results = ParallelUtil.executeInParallel(
                this.assignmentTasks, this.getThreadPool() );
        }
        catch( Exception ex )
        {
            throw new RuntimeException( ex );
        }

        // This assigns observations to each of the K clusters, plus the
        // as-yet-uncreated new cluster
        ArrayList<Collection<ObservationType>> clusterAssignments =
            new ArrayList<Collection<ObservationType>>( K+1 );
        for( int k = 0; k < K+1; k++ )
        {
            clusterAssignments.add( new LinkedList<ObservationType>() );
        }

        for( int n = 0; n < results.size(); n++ )
        {
            ArrayList<Integer> assignments = results.get(n);
            int index = 0;
            for( ObservationType observation : this.assignmentTasks.get(n).observations )
            {
                int assignment = assignments.get(index);
                clusterAssignments.get(assignment).add( observation );
                index++;
            }
        }

        return clusterAssignments;
        
    }

    /**
     * Task that assign observations to cluster indices
     */
    protected class ObservationAssignmentTask
        extends AbstractCloneableSerializable
        implements Callable<ArrayList<Integer>>
    {

        /**
         * Observations to assign
         */
        private Collection<? extends ObservationType> observations;

        /**
         * Weights that are re-used
         */
        private double[] weights;

        /**
         * Resulting assignments
         */
        private ArrayList<Integer> assignments;

        /**
         * Creates a new instance of ObservationAssignmentTask
         * @param observations
         * Observations to assign
         */
        public ObservationAssignmentTask(
            Collection<? extends ObservationType> observations )
        {
            this.weights = null;
            this.observations = observations;
        }

        public ArrayList<Integer> call()
            throws Exception
        {

            final int K = currentParameter.getNumClusters();
            if( (this.weights == null) ||
                (this.weights.length != K+1) )
            {
                this.weights = new double[ K+1 ];
            }

            if( this.assignments == null )
            {
                this.assignments = new ArrayList<Integer>(
                    this.observations.size() );
                for( int n = 0; n < this.observations.size(); n++ )
                {
                    this.assignments.add( null );
                }
            }

            int index = 0;
            for( ObservationType observation : this.observations )
            {
                int clusterAssignment = assignObservationToCluster(
                    observation, this.weights );
                this.assignments.set( index, clusterAssignment );
                index++;
            }

            return this.assignments;
        }

    }

    /**
     * Tasks that update the values of the clusters for Gibbs sampling
     */
    transient protected ArrayList<ClusterUpdaterTask> clusterUpdaterTasks;

    @Override
    protected ArrayList<WeightedValue<ProbabilityFunction<ObservationType>>> updateClusters(
        ArrayList<Collection<ObservationType>> clusterAssignments)
    {

        final int Kp1 = clusterAssignments.size();

        if( (this.clusterUpdaterTasks == null) ||
            (this.clusterUpdaterTasks.size() != Kp1) )
        {
            this.clusterUpdaterTasks = new ArrayList<ClusterUpdaterTask>( Kp1 );
        }

        while( this.clusterUpdaterTasks.size() > Kp1 )
        {
            this.clusterUpdaterTasks.remove( this.clusterUpdaterTasks.size()-1 );
        }

        this.clusterUpdaterTasks.ensureCapacity(Kp1);
        while( this.clusterUpdaterTasks.size() < Kp1 )
        {
            this.clusterUpdaterTasks.add( new ClusterUpdaterTask() );
        }
        
        for( int k = 0; k < Kp1; k++ )
        {
            Collection<ObservationType> observations = clusterAssignments.get(k);
            if( observations.size() <= 1 )
            {
                observations = null;
            }
            this.clusterUpdaterTasks.get(k).observations = observations;
        }

        ArrayList<WeightedValue<ProbabilityFunction<ObservationType>>> clusters = null;

        try
        {
            clusters = ParallelUtil.executeInParallel(
                this.clusterUpdaterTasks, this.getThreadPool() );
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        ArrayList<WeightedValue<ProbabilityFunction<ObservationType>>> results =
            new ArrayList<WeightedValue<ProbabilityFunction<ObservationType>>>( Kp1 );
        for( int k = 0; k < Kp1; k++ )
        {
            WeightedValue<ProbabilityFunction<ObservationType>> cluster = clusters.get(k);
            if( cluster != null )
            {
                results.add( cluster );
            }
        }

        return results;
    }

    /**
     * Tasks that update the values of the clusters for Gibbs sampling
     */
    protected class ClusterUpdaterTask
        extends AbstractCloneableSerializable
        implements Callable<WeightedValue<ProbabilityFunction<ObservationType>>>
    {

        /**
         * Observations that comprise the cluster
         */
        Collection<ObservationType> observations;

        /**
         * Local clone of the updater, needed to ensure thread safety
         */
        Updater<ObservationType> localUpdater;

        /**
         * Creates a new instance of ClusterUpdaterTask
         */
        public ClusterUpdaterTask()
        {
            this.localUpdater = ObjectUtil.cloneSafe( updater );
        }

        public WeightedValue<ProbabilityFunction<ObservationType>> call()
        {
            return createCluster(this.observations, this.localUpdater );
        }

    }

}
