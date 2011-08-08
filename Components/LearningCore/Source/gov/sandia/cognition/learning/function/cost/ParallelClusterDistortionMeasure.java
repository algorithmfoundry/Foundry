/*
 * File:                ParallelClusterDistortionMeasure.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A parallel implementation of ClusterDistortionMeasure.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <DataType> Type of data to compute the distortion over
 * @param <ClusterType> Type of clusters to use
 */
public class ParallelClusterDistortionMeasure<DataType,ClusterType extends Cluster<DataType>>
    extends ClusterDistortionMeasure<DataType,ClusterType>
    implements ParallelAlgorithm
{

    /**
     * Task that perform the computation of the distortion measure for each
     * cluster
     */
    private transient ArrayList<Callable<Double>> tasks;
    
    /**
     * Thread pool to use to parallelize tasks
     */
    private transient ThreadPoolExecutor threadPool;
    
    /** 
     * Creates a new instance of ParallelClusterDistortionMeasure 
     */
    public ParallelClusterDistortionMeasure()
    {
        this( null );
    }
    
    /**
     * Creates a new instance of ClusterDistortionMeasure 
     * @param costParameters
     * Divergence function that defines the cost function
     */
    public ParallelClusterDistortionMeasure(
        ClusterDivergenceFunction<ClusterType, DataType> costParameters )
    {
        super( costParameters );
    }
        
    
    @Override
    public Double evaluate(
        Collection<? extends ClusterType> target )
    {
        
        double sum = 0.0;
        try
        {
            this.tasks = new ArrayList<Callable<Double>>( target.size() );
            for (ClusterType cluster : target)
            {
                this.tasks.add( new ClusterDistortionTask( cluster ) );
            }
            Collection<Double> results =ParallelUtil.executeInParallel(
                this.tasks, this.getThreadPool() );
            for( Double result : results )
            {
                sum += result;
            }
            
        }
        catch (Exception ex)
        {
            Logger.getLogger( ClusterDistortionMeasure.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        return sum;
        
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
        ThreadPoolExecutor threadPool )
    {
        this.threadPool = threadPool;
    }

    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads( this );
    }
    
    /**
     * Task that computes the distortion for a single cluster, used for
     * parallelization.
     */
    private class ClusterDistortionTask
        implements Callable<Double>
    {
        
        /**
         * Cluster to consider
         */
        ClusterType cluster;
        
        /**
         * Creates a new instance of ClusterDistortionTask
         * @param cluster
         * Cluster to consider
         */
        public ClusterDistortionTask(
            ClusterType cluster )
        {
            this.cluster = cluster;
        }

        public Double call()
        {
            return ParallelClusterDistortionMeasure.this.evaluate( this.cluster );
        }
        
    }
    
}
