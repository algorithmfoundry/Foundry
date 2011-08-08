/*
 * File:                AbstractParallelAlgorithm.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 14, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Partial implementation of ParallelAlgorithm.  This class automatically
 * generates a thread pool appropriate for the number of processing units
 * available on the current machine.
 *
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractParallelAlgorithm
    extends AbstractCloneableSerializable
    implements ParallelAlgorithm
{

    /**
     * Thread pool used for parallelization.
     */
    private transient ThreadPoolExecutor threadPool;

    /** 
     * Creates a new instance of AbstractParallelAlgorithm 
     */
    public AbstractParallelAlgorithm()
    {
        this(null);
    }

    /**
     * Creates a new instance of AbstractParallelAlgorithm 
     * @param threadPool
     * Thread pool used for parallelization.
     */
    public AbstractParallelAlgorithm(
        final ThreadPoolExecutor threadPool)
    {
        super();

        this.setThreadPool(threadPool);
    }

    @Override
    public AbstractParallelAlgorithm clone()
    {
        AbstractParallelAlgorithm clone =
            (AbstractParallelAlgorithm) super.clone();
        if( this.getThreadPool() != null )
        {
            clone.setThreadPool( ParallelUtil.createThreadPool(
                this.getNumThreads() ) );
        }
        return clone;
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

}
