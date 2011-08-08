/*
 * File:                ParallelAlgorithm.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 10, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.util.CloneableSerializable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Interface for algorithms that are parallelized using multithreading.
 * Using multithreading can provide a significant speedup by letting the
 * threads execute in parallel on multi-core or hyperthreaded processors as
 * well as multi-processor, shared-memory systems.
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
public interface ParallelAlgorithm
    extends CloneableSerializable
{

    /**
     * Gets the thread pool for the algorithm to use.
     *
     * @return
     *      Thread pool used for parallelization.
     */
    public ThreadPoolExecutor getThreadPool();

    /**
     * Sets the thread pool for the algorithm to use.
     *
     * @param   threadPool
     *      Thread pool used for parallelization.
     */
    public void setThreadPool(
        final ThreadPoolExecutor threadPool);

    /**
     * Gets the number of threads in the thread pool.
     *
     * @return
     *      Number of threads in the thread pool
     */
    public int getNumThreads();

}
